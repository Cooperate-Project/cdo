/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.db4o.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OStoreAccessor extends LongIDStoreAccessor
{
  private ObjectContainer objectContainer;

  public DB4OStoreAccessor(DB4OStore store, ISession session)
  {
    super(store, session);
  }

  public DB4OStoreAccessor(DB4OStore store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Override
  public DB4OStore getStore()
  {
    return (DB4OStore)super.getStore();
  }

  public ObjectContainer getObjectContainer()
  {
    return objectContainer;
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return packageUnit.getEPackages(true);
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    monitor.begin(packageUnits.length);

    try
    {
      DB4OStore store = getStore();
      ObjectContainer objectContainer = getObjectContainer();

      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        DB4OPackageUnit primitivePackageUnit = DB4OPackageUnit.getPrimitivePackageUnit(store, packageUnit);
        objectContainer.store(primitivePackageUnit);
        monitor.worked(1);
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
    finally
    {
      monitor.done();
    }
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    List<InternalCDOPackageUnit> result = new ArrayList<InternalCDOPackageUnit>();
    InternalCDOPackageRegistry packageRegistry = getStore().getRepository().getPackageRegistry();
    Collection<DB4OPackageUnit> primitivePackageUnits = getObjectContainer().query(DB4OPackageUnit.class);

    for (DB4OPackageUnit primitivePackageUnit : primitivePackageUnits)
    {
      InternalCDOPackageUnit packageUnit = DB4OPackageUnit.getPackageUnit(packageRegistry, primitivePackageUnit);
      result.add(packageUnit);
    }

    return result;
  }

  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    int branchID = branchPoint.getBranch().getID();
    return getRevisionFromContainer(branchID, id);
  }

  public void queryResources(QueryResourcesContext context)
  {
    final long folderID = CDOIDUtil.getLong(context.getFolderID());
    final String name = context.getName();
    final boolean exactMatch = context.exactMatch();

    ObjectSet<DB4ORevision> revisionObjectSet = getObjectContainer().query(new Predicate<DB4ORevision>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(DB4ORevision primitiveRevision)
      {
        if (!primitiveRevision.isResourceNode())
        {
          return false;
        }

        // is Root resource
        if (primitiveRevision.isRootResource())
        {
          return false;
        }

        if (ObjectUtil.equals(primitiveRevision.getContainerID(), folderID))
        {
          String candidateName = (String)primitiveRevision.getValues().get(EresourcePackage.CDO_RESOURCE__NAME);
          if (exactMatch)
          {
            if (candidateName != null && candidateName.equals(name))
            {
              return true;
            }
          }
          else
          {
            // provided name is prefix of the resource name
            if (candidateName != null && candidateName.startsWith(name))
            {
              return true;
            }
          }
        }

        return false;
      }
    });

    for (DB4ORevision revision : revisionObjectSet)
    {
      if (!context.addResource(DB4ORevision.getCDOID(revision.getID())))
      {
        // No more results allowed
        break;
      }
    }

  }

  public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    objectContainer = getStore().openClient();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (objectContainer != null)
    {
      objectContainer.close();
      objectContainer = null;
    }
  }

  @Override
  protected void doPassivate() throws Exception
  {
    if (objectContainer != null)
    {
      objectContainer.rollback();
    }
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
  }

  @Override
  protected void rollback(CommitContext commitContext)
  {
    getObjectContainer().rollback();
  }

  /**
   * TODO Branching can only be supported with auditing. Where is the timeStamp parameter?
   */
  private InternalCDORevision getRevisionFromContainer(int branchID, CDOID id)
  {
    DB4ORevision lastRevision = DB4OQueryUtil.getRevision(getObjectContainer(), id, branchID);
    if (lastRevision == null)
    {
      // Revision does not exist. Return null to signal inexistent Revision
      return null;
    }

    return DB4ORevision.getCDORevision(getStore(), lastRevision);
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    int branchID = branchVersion.getBranch().getID();
    int version = branchVersion.getVersion();
    DB4ORevision revisionByVersion = DB4OQueryUtil.getRevisionByVersion(getObjectContainer(), id, branchID, version);

    // Revision does not exist. Return null to signal inexistent Revision
    if (revisionByVersion == null)
    {
      return null;
    }

    return DB4ORevision.getCDORevision(getStore(), revisionByVersion);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, CDORevisionHandler handler)
  {
    throw new UnsupportedOperationException();
  }

  public Set<CDOID> readChangeSet(CDOChangeSetSegment... segments)
  {
    throw new UnsupportedOperationException();
  }

  public void queryXRefs(final QueryXRefsContext context)
  {
    final int branchID = context.getBranch().getID();

    for (final CDOID target : context.getTargetObjects().keySet())
    {
      final long targetID = CDOIDUtil.getLong(target);

      for (final EClass eClass : context.getSourceCandidates().keySet())
      {
        final String eClassName = eClass.getName();
        final String nsURI = eClass.getEPackage().getNsURI();
        final List<EReference> eReferences = context.getSourceCandidates().get(eClass);
        getObjectContainer().query(new Predicate<DB4ORevision>()
        {
          private static final long serialVersionUID = 1L;

          private boolean moreResults = true;

          @Override
          public boolean match(DB4ORevision primitiveRevision)
          {
            if (!moreResults)
            {
              return false;
            }
            if (!primitiveRevision.getClassName().equals(eClassName))
            {
              return false;
            }

            if (!primitiveRevision.getPackageURI().equals(nsURI))
            {
              return false;
            }

            if (!(primitiveRevision.getBranchID() == branchID))
            {
              return false;
            }

            for (EReference eReference : eReferences)
            {
              Object obj = primitiveRevision.getValues().get(eReference.getFeatureID());
              if (obj instanceof List)
              {
                List<?> list = (List<?>)obj;
                if (list.contains(targetID))
                {
                  moreResults = context.addXRef(target, DB4ORevision.getCDOID(primitiveRevision.getID()), eReference, 0);
                }
              }
              else if (obj instanceof CDOID)
              {
                if (CDOIDUtil.getLong((CDOID)obj) == targetID)
                {
                  moreResults = context.addXRef(target, DB4ORevision.getCDOID(primitiveRevision.getID()), eReference, 0);
                }
              }
            }

            return false;
          }
        });

      }

    }
  }

  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public int createBranch(int branchID, BranchInfo branchInfo)
  {
    throw new UnsupportedOperationException();
  }

  public BranchInfo loadBranch(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    throw new UnsupportedOperationException();
  }

  public void loadCommitInfos(final CDOBranch branch, final long startTime, final long endTime,
      CDOCommitInfoHandler handler)
  {
    ObjectSet<DB4OCommitInfo> resultSet = getObjectContainer().query(new Predicate<DB4OCommitInfo>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(DB4OCommitInfo info)
      {
        if (startTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() < startTime)
        {
          return false;
        }

        if (endTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() > endTime)
        {
          return false;
        }

        if (branch != null && !(info.getBranchID() == branch.getID()))
        {
          return false;
        }

        return true;
      }
    });

    InternalRepository repository = getStore().getRepository();
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
    InternalCDOBranchManager branchManager = repository.getBranchManager();

    // Although not specified in the API, the test suite
    // suggests CommitInfos should be returned ordered by timeStamp
    // TODO Specify this in the API!

    List<DB4OCommitInfo> infos = new ArrayList<DB4OCommitInfo>(resultSet);
    Collections.sort(infos, new Comparator<DB4OCommitInfo>()
    {
      public int compare(DB4OCommitInfo arg0, DB4OCommitInfo arg1)
      {
        return CDOCommonUtil.compareTimeStamps(arg0.getTimeStamp(), arg1.getTimeStamp());
      }
    });

    for (DB4OCommitInfo info : infos)
    {
      info.handle(branchManager, commitInfoManager, handler);
    }
  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      getObjectContainer().commit();
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, String userID, String comment, OMMonitor monitor)
  {
    DB4OCommitInfo commitInfo = new DB4OCommitInfo(branch.getID(), timeStamp, userID, comment);
    writeObject(commitInfo, monitor);
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    monitor.begin(revisions.length);

    try
    {
      for (InternalCDORevision revision : revisions)
      {
        writeRevision(revision, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void checkDuplicateResources(CDORevision revision) throws IllegalStateException
  {
    final long folderID = CDOIDUtil.getLong((CDOID)revision.data().getContainerID());
    final long revisionID = CDOIDUtil.getLong(revision.getID());
    final String name = (String)revision.data().get(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 0);

    ObjectSet<DB4ORevision> resultSet = getObjectContainer().query(new Predicate<DB4ORevision>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(DB4ORevision revision)
      {
        if (revision.isResourceNode() && ObjectUtil.equals(revision.getContainerID(), folderID))
        {
          String candidateName = (String)revision.getValues().get(EresourcePackage.CDO_RESOURCE__NAME);
          if (StringUtil.compare(name, candidateName) == 0)
          {
            if (!ObjectUtil.equals(revision.getID(), revisionID))
            {
              return true;
            }
          }
        }

        return false;
      }
    });

    if (!resultSet.isEmpty())
    {
      throw new IllegalStateException("Duplicate resource or folder: " + name + " in folder " + folderID); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  protected void writeRevision(InternalCDORevision revision, OMMonitor monitor)
  {
    Async async = null;
    monitor.begin(10);

    try
    {
      try
      {
        async = monitor.forkAsync();
        if (revision.isResourceFolder() || revision.isResource())
        {
          checkDuplicateResources(revision);
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      // If revision is in the store, remove old, store new
      ObjectContainer objectContainer = getObjectContainer();
      int branchID = revision.getBranch().getID();
      CDOID id = revision.getID();
      DB4ORevision revisionAlreadyInStore = DB4OQueryUtil.getRevision(objectContainer, id, branchID);
      if (revisionAlreadyInStore != null)
      {
        DB4OQueryUtil.removeRevisionFromContainer(objectContainer, branchID, id);
      }

      DB4ORevision primitiveRevision = DB4ORevision.getDB4ORevision(revision);
      writeObject(primitiveRevision, monitor);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeObject(Object object, OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      getObjectContainer().store(object);
    }
    catch (Throwable t)
    {
      OM.LOG.error(t);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    monitor.begin(detachedObjects.length);

    try
    {
      int branchID = branch.getID();
      for (CDOID id : detachedObjects)
      {
        DB4OQueryUtil.removeRevisionFromContainer(getObjectContainer(), branchID, id);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }
}
