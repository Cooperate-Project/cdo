/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - specific hibernate functionality
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreAccessor;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.PersistableListHolder;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessor;
import org.eclipse.emf.cdo.spi.server.StoreChunkReader;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Implements the runtime behavior of accessing the hibernate store using queries and doing write and commit. The
 * HibernateStoreAccessor corresponds roughly to a Hibernate session. It offers methods to create and close them and
 * implements transaction handling. The main update/create/delete operations are done in the
 * {@link #write(InternalCommitContext, OMMonitor)} method.
 * 
 * @see HibernateStore
 * @see HibernatePackageHandler
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStoreAccessor extends StoreAccessor implements IHibernateStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStoreAccessor.class);

  private static final String NAME_EFEATURE_NAME = "name";//$NON-NLS-1$

  private Session hibernateSession;

  private boolean errorOccured;

  public void addToRevisionCache(Object object)
  {
    if (object instanceof CDORevision)
    {
      getStore().getRepository().getRevisionManager().addRevision((CDORevision)object);
    }
    else if (object instanceof Object[])
    {
      // handle hibernate query result
      final Object[] objects = (Object[])object;
      for (Object o : objects)
      {
        addToRevisionCache(o);
      }
    }

    // also primitive types can get here, ignore those
  }

  /**
   * Constructor
   * 
   * @param store
   *          the {@link Store} used by the accessor.
   * @param session
   *          the client session (not a Hibernate Session)
   */
  public HibernateStoreAccessor(HibernateStore store, ISession session)
  {
    super(store, session);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * Constructor for a specific transaction
   * 
   * @param store
   *          the HibernateStore backing this accessor
   * @param transaction
   *          the client transaction (not the a Hibernate transaction)
   */
  public HibernateStoreAccessor(HibernateStore store, ITransaction transaction)
  {
    super(store, transaction);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /** Clears the current hibernate session and sets a new one in the thread context */
  public void resetHibernateSession()
  {
    endHibernateSession();
    beginHibernateSession();
  }

  /**
   * @return the backing store
   */
  @Override
  public HibernateStore getStore()
  {
    return (HibernateStore)super.getStore();
  }

  /**
   * Starts a hibernate session and begins a transaction.
   * 
   * @since 2.0
   */
  public void beginHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating hibernate session and transaction"); //$NON-NLS-1$
    }

    assert hibernateSession == null;
    final SessionFactory sessionFactory = getStore().getHibernateSessionFactory();
    hibernateSession = sessionFactory.openSession();
    hibernateSession.beginTransaction();
  }

  /**
   * Calls {@link #endHibernateSession()}, commits the transaction and closes the session.
   * 
   * @since 2.0
   */
  public void commitRollbackHibernateSession()
  {
    endHibernateSession();
  }

  /**
   * Commits/rollbacks and closes the session
   * 
   * @since 2.0
   */
  public void endHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Closing hibernate session"); //$NON-NLS-1$
    }

    if (hibernateSession != null && hibernateSession.isOpen())
    {
      try
      {
        if (hibernateSession.getTransaction().isActive())
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Commiting hibernate session"); //$NON-NLS-1$
          }

          if (isErrorOccured())
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace("Rolling back hb transaction"); //$NON-NLS-1$
            }

            hibernateSession.getTransaction().rollback();
          }
          else
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace("Committing hb transaction"); //$NON-NLS-1$
            }

            hibernateSession.getTransaction().commit();
          }
        }
      }
      finally
      {
        hibernateSession.close();
      }
    }

    hibernateSession = null;
  }

  /**
   * @return the current hibernate session. If there is none then a new one is created and a transaction is started
   */
  public Session getHibernateSession()
  {
    if (hibernateSession == null)
    {
      beginHibernateSession();
    }

    return hibernateSession;
  }

  /**
   * Closes/commits the current hibernate session if there is one, and starts a new one and begins a transaction.
   * 
   * @return a newly created Hibernate Session
   */
  public Session getNewHibernateSession()
  {
    if (hibernateSession != null)
    {
      endHibernateSession();
    }

    if (hibernateSession != null)
    {
      throw new IllegalStateException("Hibernate session should be null"); //$NON-NLS-1$
    }

    beginHibernateSession();
    return hibernateSession;
  }

  /**
   * @return true if an error occured during database actions. Normally means that the transaction will be rolled back
   *         and not committed.
   * @since 2.0
   */
  public boolean isErrorOccured()
  {
    return errorOccured;
  }

  /**
   * @since 2.0
   */
  public void setErrorOccured(boolean errorOccured)
  {
    this.errorOccured = errorOccured;
  }

  /**
   * Note: the Hibernate store does not support the {@link StoreChunkReader} concept!.
   * 
   * @return a {@link HibernateStoreChunkReader} (which throws UnsupportedOperationExceptions for most methods
   */
  public HibernateStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new HibernateStoreChunkReader(this, revision, feature);
  }

  /**
   * @return the current collection of package units.
   * @see HibernateStore
   * @see HibernatePackageHandler
   */
  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return getStore().getPackageHandler().getPackageUnits();
  }

  /**
   * Loads the package units from the database and returns the EPackage instances.
   * 
   * @return the loaded EPackage instances.
   * @see HibernatePackageHandler
   */
  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return getStore().getPackageHandler().loadPackageUnit(packageUnit);
  }

  /**
   * Reads the revision from the database. using the passed id.
   * 
   * @param id
   *          identifies the CDORevision to read
   * @param branchPoint
   *          ignored until auditing is supported.
   * @param listChunk
   *          not used by Hibernate
   * @param cache
   *          the revision cache, the read revision is added to the cache
   * @return the read revision
   */
  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    if (!HibernateUtil.getInstance().isStoreCreatedID(id))
    {
      return null;
    }

    final InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(id);
    if (revision == null)
    {
      final CDOClassifierRef classifierRef = CDOIDUtil.getClassifierRef(id);
      if (classifierRef == null)
      {
        throw new IllegalArgumentException("This CDOID type of " + id + " is not supported by this store."); //$NON-NLS-1$ //$NON-NLS-2$
      }

      final EClass eClass = HibernateUtil.getInstance().getEClass(classifierRef);
      return new DetachedCDORevision(eClass, id, branchPoint.getBranch(), 0, 0);
    }

    revision.setBranchPoint(getStore().getMainBranchHead());
    return revision;
  }

  public int createBranch(int branchID, BranchInfo branchInfo)
  {
    // TODO: implement HibernateStoreAccessor.createBranch(branchID, branchInfo)
    throw new UnsupportedOperationException();
  }

  public BranchInfo loadBranch(int branchID)
  {
    // TODO: implement HibernateStoreAccessor.loadBranch(branchID)
    throw new UnsupportedOperationException();
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    // TODO: implement HibernateStoreAccessor.loadSubBranches(branchID)
    throw new UnsupportedOperationException();
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    // TODO: implement HibernateStoreAccessor.loadBranches(startID, endID, branchHandler)
    throw new UnsupportedOperationException();
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    // TODO: implement HibernateStoreAccessor.loadCommitInfos(branch, startTime, endTime, handler)
    throw new UnsupportedOperationException();
  }

  public Set<CDOID> readChangeSet(CDOChangeSetSegment... segments)
  {
    // TODO: implement HibernateStoreAccessor.readChangeSet(segments)
    throw new UnsupportedOperationException();
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, CDORevisionHandler handler)
  {
    // TODO: implement HibernateStoreAccessor.handleRevisions(eClass, branch, timeStamp, handler)
    throw new UnsupportedOperationException();
  }

  /**
   * Not supported by the Hibernate Store, auditing is not supported. Currently ignores the branchVersion and calls the
   * {@readRevision(CDOID, CDOBranchPoint, int, CDORevisionCacheAdder)} .
   */
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    return readRevision(id, branchVersion.getBranch().getPoint(System.currentTimeMillis()), listChunk, cache);
  }

  /**
   * Queries for resources in a certain folder and returns them in the context object
   * 
   * @param context
   *          the context provides input parameters (the folder) and is used to store the results of the query.
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context)
  {
    final CDOID folderID = getHibernateID(context.getFolderID());
    String name = context.getName();
    boolean exactMatch = context.exactMatch();

    final Session session = getHibernateSession();
    final Criteria criteria = session.createCriteria(EresourcePackage.eINSTANCE.getCDOResourceNode().getName());
    if (folderID == null)
    {
      criteria.add(org.hibernate.criterion.Restrictions.isNull(CDOHibernateConstants.CONTAINER_PROPERTY));
    }
    else
    {
      criteria.add(org.hibernate.criterion.Restrictions.eq(CDOHibernateConstants.CONTAINER_PROPERTY, folderID));
    }

    List<?> result = criteria.list();
    for (Object o : result)
    {
      final CDORevision revision = (CDORevision)o;
      final EStructuralFeature feature = revision.getEClass().getEStructuralFeature(NAME_EFEATURE_NAME);
      if (feature != null)
      {
        Object value = revision.data().get(feature, 0);
        if (value == CDORevisionData.NIL)
        {
          value = null;
        }

        final String revisionName = (String)value;
        final boolean match = exactMatch || revisionName == null || name == null ? ObjectUtil
            .equals(revisionName, name) : revisionName.startsWith(name);

        if (match && !context.addResource(HibernateUtil.getInstance().getCDOID(revision)))
        {
          // No more results allowed
          break;
        }
      }
    }
  }

  public void queryXRefs(QueryXRefsContext context)
  {
    // TODO: implement HibernateStoreAccessor.queryXRefs(context)
    throw new UnsupportedOperationException();
  }

  private CDOID getHibernateID(CDOID id)
  {
    if (!CDOIDUtil.isNull(id))
    {
      if (HibernateUtil.getInstance().isStoreCreatedID(id))
      {
        return id;
      }

      // TODO Can this happen? When?
      // the folder id is always a long
      final Long idValue = CDOIDUtil.getLong(id);
      return CDOIDUtil.createLongWithClassifier(new CDOClassifierRef(EresourcePackage.eINSTANCE.getCDOResourceNode()),
          idValue);
    }

    return null;
  }

  /**
   * @param info
   *          the query information, is not used actively in this method.
   * @return a new instance of {@link HibernateQueryHandler}
   */
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    String queryLanguage = info.getQueryLanguage();
    if (StringUtil.equalsUpperOrLowerCase(queryLanguage, HibernateQueryHandler.QUERY_LANGUAGE))
    {
      final HibernateQueryHandler queryHandler = new HibernateQueryHandler();
      queryHandler.setHibernateStoreAccessor(this);
      return queryHandler;
    }

    return null;
  }

  /**
   * Commits the session, see {@link #commitRollbackHibernateSession()}.
   * 
   * @param monitor
   *          not used
   */
  @Override
  protected void doCommit(OMMonitor monitor)
  {
    commitRollbackHibernateSession();
    HibernateThreadContext.setCommitContext(null);
  }

  /**
   * Performs the main write and update actions. Persists new EPackages, updates changed objects, creates new ones and
   * removes deleted objects. Updates both container as well as resource associations.
   * 
   * @param context
   *          the context contains the changed, new and to-be-removed objects
   * @param monitor
   *          not used by this method
   */
  @Override
  public void write(InternalCommitContext context, OMMonitor monitor)
  {
    HibernateThreadContext.setCommitContext(context);
    if (context.getNewPackageUnits().length > 0)
    {
      writePackageUnits(context.getNewPackageUnits(), monitor);
    }

    try
    {
      // start with fresh hibernate session to prevent side effects
      final Session session = getNewHibernateSession();
      session.setFlushMode(FlushMode.MANUAL);

      // order is 1) insert, 2) update and then delete
      // this order is the most stable! Do not change it without testing

      final List<InternalCDORevision> repairContainerIDs = new ArrayList<InternalCDORevision>();
      final List<InternalCDORevision> repairResourceIDs = new ArrayList<InternalCDORevision>();
      for (InternalCDORevision revision : context.getNewObjects())
      {
        // keep track for which cdoRevisions the container id needs to be repaired afterwards
        final CDOID containerID = (CDOID)revision.getContainerID();
        if (containerID instanceof CDOIDTemp && !containerID.isNull())
        {
          repairContainerIDs.add(revision);
        }

        final CDOID resourceID = revision.getResourceID();
        if (resourceID instanceof CDOIDTemp && !resourceID.isNull())
        {
          repairResourceIDs.add(revision);
        }

        final String entityName = getStore().getEntityName(revision.getEClass());
        session.saveOrUpdate(entityName, revision);
      }

      session.flush();

      for (CDORevision revision : context.getDirtyObjects())
      {
        final String entityName = HibernateUtil.getInstance().getEntityName(revision.getID());
        session.merge(entityName, revision);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Updated Object " + revision.getEClass().getName() + " id: " + revision.getID()); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

      session.flush();

      // delete all objects
      for (CDOID id : context.getDetachedObjects())
      {
        try
        {
          final CDORevision revision = HibernateUtil.getInstance().getCDORevision(id);
          // maybe deleted in parallell?
          if (revision != null)
          {
            session.delete(revision);
          }
        }
        catch (org.hibernate.ObjectNotFoundException ex)
        {
          // ignore these, an object can be removed through cascade deletes
        }
      }

      session.flush();

      // now do an update of the container without incrementing the version
      repairContainerIDs(repairContainerIDs, session);
      repairResourceIDs(repairResourceIDs, session);

      session.flush();
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
      throw WrappedException.wrap(e);
    }

    context.applyIDMappings(monitor);
  }

  private void repairContainerIDs(List<InternalCDORevision> repairContainerIDs, Session session)
  {
    for (InternalCDORevision revision : repairContainerIDs)
    {
      final CDORevision container = HibernateUtil.getInstance().getCDORevision((CDOID)revision.getContainerID());
      final String entityName = getStore().getEntityName(revision.getEClass());
      final CDOID id = revision.getID();
      final String hqlUpdate = "update " + entityName + " set " + CDOHibernateConstants.CONTAINER_PROPERTY //$NON-NLS-1$  //$NON-NLS-2$
          + " = :containerInfo where " + getStore().getIdentifierPropertyName(entityName) + " = :id"; //$NON-NLS-1$ //$NON-NLS-2$
      final Query qry = session.createQuery(hqlUpdate);
      qry.setParameter("containerInfo", ContainerInfoConverter.getInstance().convertContainerRelationToString(revision, //$NON-NLS-1$
          container.getID()));
      qry.setParameter("id", HibernateUtil.getInstance().getIdValue(id)); //$NON-NLS-1$
      if (qry.executeUpdate() != 1)
      {
        throw new IllegalStateException("Not able to update container columns of " + entityName + " with id " + id); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  private void repairResourceIDs(List<InternalCDORevision> repairResourceIDs, Session session)
  {
    for (InternalCDORevision revision : repairResourceIDs)
    {
      final CDORevision resource = HibernateUtil.getInstance().getCDORevision(revision.getResourceID());
      final String entityName = getStore().getEntityName(revision.getEClass());
      final CDOID id = revision.getID();
      final String hqlUpdate = "update " + entityName + " set " + CDOHibernateConstants.RESOURCE_PROPERTY //$NON-NLS-1$ //$NON-NLS-2$
          + " = :resourceInfo where " + getStore().getIdentifierPropertyName(entityName) + " = :id"; //$NON-NLS-1$ //$NON-NLS-2$
      final Query qry = session.createQuery(hqlUpdate);
      qry.setParameter("resourceInfo", resource.getID()); //$NON-NLS-1$
      qry.setParameter("id", HibernateUtil.getInstance().getIdValue(id)); //$NON-NLS-1$
      if (qry.executeUpdate() != 1)
      {
        throw new IllegalStateException("Not able to update container columns of " + entityName + " with id " + id); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    // handled by the write method
  }

  @Override
  protected void rollback(CommitContext context)
  {
    setErrorOccured(true);
    endHibernateSession();
    HibernateThreadContext.setCommitContext(null);
  }

  /**
   * Writes package units to the datbaase.
   * 
   * @param packageUnits
   *          the package units to write to the database
   * @param monitor
   *          not used by the store
   * @see HibernatePackageHandler
   */
  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    if (packageUnits != null && packageUnits.length != 0)
    {
      getStore().getPackageHandler().writePackageUnits(packageUnits);
    }
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, String userID, String comment, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    // Doesn't do anything. It is done in commit().
  }

  @Override
  protected void addIDMappings(InternalCommitContext commitContext, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void queryLobs(List<byte[]> ids)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    // TODO This method is called when this accessor is not needed anymore
    if (TRACER.isEnabled())
    {
      TRACER.trace("Committing/rollback and closing hibernate session"); //$NON-NLS-1$
    }

    try
    {
      endHibernateSession();
    }
    finally
    {
      clearThreadState();
    }
  }

  @Override
  protected void doPassivate() throws Exception
  {
    clearThreadState();
  }

  private void clearThreadState()
  {
    PersistableListHolder.getInstance().clearListMapping();
    HibernateThreadContext.setCommitContext(null);
  }

  @Override
  protected void doActivate() throws Exception
  {
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
  }
}
