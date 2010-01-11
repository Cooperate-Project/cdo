/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class TransactionCommitContextImpl implements InternalCommitContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION,
      TransactionCommitContextImpl.class);

  private TransactionPackageRegistry packageRegistry;

  private IStoreAccessor accessor;

  private long timeStamp = CDORevision.UNSPECIFIED_DATE;

  private InternalCDOPackageUnit[] newPackageUnits;

  private InternalCDORevision[] newObjects;

  private InternalCDORevision[] dirtyObjects;

  private CDOID[] detachedObjects;

  private List<CDOID> lockedObjects = new ArrayList<CDOID>();

  private List<InternalCDORevision> detachedRevisions = new ArrayList<InternalCDORevision>();;

  private InternalCDORevisionDelta[] dirtyObjectDeltas;

  private List<CDOIDMetaRange> metaIDRanges = new ArrayList<CDOIDMetaRange>();

  private ConcurrentMap<CDOIDTemp, CDOID> idMappings = new ConcurrentHashMap<CDOIDTemp, CDOID>();

  private CDOReferenceAdjuster idMapper = new CDOIDMapper(idMappings);

  private String rollbackMessage;

  private InternalTransaction transaction;

  private boolean autoReleaseLocksEnabled;

  public TransactionCommitContextImpl(InternalTransaction transaction)
  {
    this.transaction = transaction;
    InternalRepository repository = transaction.getRepository();
    packageRegistry = new TransactionPackageRegistry(repository.getPackageRegistry(false));
    packageRegistry.activate();
  }

  public int getTransactionID()
  {
    return transaction.getViewID();
  }

  public InternalTransaction getTransaction()
  {
    return transaction;
  }

  public CDOBranchPoint getBranchPoint()
  {
    return new CDOBranchPointImpl(transaction.getBranch(), timeStamp);
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public InternalCDOPackageUnit[] getNewPackageUnits()
  {
    return newPackageUnits;
  }

  public InternalCDORevision[] getNewObjects()
  {
    return newObjects;
  }

  public InternalCDORevision[] getDirtyObjects()
  {
    return dirtyObjects;
  }

  public CDOID[] getDetachedObjects()
  {
    return detachedObjects;
  }

  public InternalCDORevisionDelta[] getDirtyObjectDeltas()
  {
    return dirtyObjectDeltas;
  }

  public List<CDOIDMetaRange> getMetaIDRanges()
  {
    return Collections.unmodifiableList(metaIDRanges);
  }

  public Map<CDOIDTemp, CDOID> getIDMappings()
  {
    return Collections.unmodifiableMap(idMappings);
  }

  public void addIDMapping(CDOIDTemp oldID, CDOID newID)
  {
    if (CDOIDUtil.isNull(newID) || newID.isTemporary())
    {
      throw new IllegalStateException("newID=" + newID); //$NON-NLS-1$
    }

    CDOID previousMapping = idMappings.putIfAbsent(oldID, newID);
    if (previousMapping != null)
    {
      throw new IllegalStateException("previousMapping != null"); //$NON-NLS-1$
    }
  }

  public void applyIDMappings(OMMonitor monitor)
  {
    try
    {
      monitor.begin(newObjects.length + dirtyObjects.length + dirtyObjectDeltas.length);
      applyIDMappings(newObjects, monitor.fork(newObjects.length));
      applyIDMappings(dirtyObjects, monitor.fork(dirtyObjects.length));
      for (CDORevisionDelta dirtyObjectDelta : dirtyObjectDeltas)
      {
        ((InternalCDORevisionDelta)dirtyObjectDelta).adjustReferences(idMapper);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public String getRollbackMessage()
  {
    return rollbackMessage;
  }

  public void preCommit()
  {
    // Allocate a store writer
    accessor = transaction.getRepository().getStore().getWriter(transaction);

    // Make the store writer available in a ThreadLocal variable
    StoreThreadLocal.setAccessor(accessor);
    StoreThreadLocal.setCommitContext(this);
  }

  public void setNewPackageUnits(InternalCDOPackageUnit[] newPackageUnits)
  {
    this.newPackageUnits = newPackageUnits;
  }

  public void setNewObjects(InternalCDORevision[] newObjects)
  {
    this.newObjects = newObjects;
  }

  public void setDirtyObjectDeltas(InternalCDORevisionDelta[] dirtyObjectDeltas)
  {
    this.dirtyObjectDeltas = dirtyObjectDeltas;
  }

  public void setDetachedObjects(CDOID[] detachedObjects)
  {
    this.detachedObjects = detachedObjects;
  }

  public boolean setAutoReleaseLocksEnabled(boolean on)
  {
    try
    {
      return autoReleaseLocksEnabled;
    }
    finally
    {
      autoReleaseLocksEnabled = on;
    }
  }

  public boolean isAutoReleaseLocksEnabled()
  {
    return autoReleaseLocksEnabled;
  }

  /**
   * @since 2.0
   */
  public void write(OMMonitor monitor)
  {
    try
    {
      monitor.begin(106);

      // Could throw an exception
      timeStamp = createTimeStamp();
      dirtyObjects = new InternalCDORevision[dirtyObjectDeltas.length];

      adjustMetaRanges();
      monitor.worked();

      adjustTimeStamps();
      monitor.worked();

      lockObjects();

      InternalRepository repository = transaction.getRepository();
      computeDirtyObjects(!repository.isSupportingRevisionDeltas(), monitor.fork());

      monitor.worked();

      repository.notifyWriteAccessHandlers(transaction, this, monitor.fork());
      detachObjects(monitor.fork());

      accessor.write(this, monitor.fork(100));
    }
    catch (Throwable t)
    {
      handleException(t);
    }
    finally
    {
      monitor.done();
    }
  }

  public void commit(OMMonitor monitor)
  {
    try
    {
      monitor.begin(101);
      accessor.commit(monitor.fork(100));
      updateInfraStructure(monitor.fork());
    }
    catch (RuntimeException ex)
    {
      handleException(ex);
    }
    catch (Error ex)
    {
      handleException(ex);
    }
    finally
    {
      monitor.done();
    }
  }

  private void handleException(Throwable ex)
  {
    OM.LOG.error(ex);
    String storeClass = transaction.getRepository().getStore().getClass().getSimpleName();
    rollback("Rollback in " + storeClass + ": " + StringUtil.formatException(ex)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected long createTimeStamp()
  {
    InternalRepository repository = transaction.getSession().getManager().getRepository();
    return repository.createCommitTimeStamp();
  }

  public void postCommit(boolean success)
  {
    try
    {
      if (success)
      {
        transaction.getRepository().getNotificationManager().notifyCommit(transaction.getSession(), this);
      }
    }
    catch (Exception ex)
    {
      OM.LOG.warn("A problem occured while notifying other sessions", ex);
    }
    finally
    {
      StoreThreadLocal.release();
      accessor = null;
      timeStamp = CDORevision.UNSPECIFIED_DATE;

      packageRegistry.deactivate();
      packageRegistry = null;

      metaIDRanges.clear();
      metaIDRanges = null;

      idMappings.clear();
      idMappings = null;

      rollbackMessage = null;
      newPackageUnits = null;
      newObjects = null;
      dirtyObjectDeltas = null;
      dirtyObjects = null;
    }
  }

  private void adjustTimeStamps()
  {
    for (InternalCDOPackageUnit newPackageUnit : newPackageUnits)
    {
      newPackageUnit.setTimeStamp(timeStamp);
    }

    for (InternalCDORevision newObject : newObjects)
    {
      newObject.setCreated(timeStamp);
    }
  }

  private void adjustMetaRanges()
  {
    for (InternalCDOPackageUnit newPackageUnit : newPackageUnits)
    {
      for (InternalCDOPackageInfo packageInfo : newPackageUnit.getPackageInfos())
      {
        adjustMetaRange(packageInfo);
      }
    }
  }

  private void adjustMetaRange(InternalCDOPackageInfo packageInfo)
  {
    CDOIDMetaRange oldRange = packageInfo.getMetaIDRange();
    if (!oldRange.isTemporary())
    {
      throw new IllegalStateException("!oldRange.isTemporary()"); //$NON-NLS-1$
    }

    int count = oldRange.size();
    CDOIDMetaRange newRange = transaction.getRepository().getStore().getNextMetaIDRange(count);
    packageInfo.setMetaIDRange(newRange);
    packageRegistry.getMetaInstanceMapper().mapMetaInstances(packageInfo.getEPackage(), newRange);

    for (int i = 0; i < count; i++)
    {
      CDOIDTemp oldID = (CDOIDTemp)oldRange.get(i);
      CDOID newID = newRange.get(i);
      idMappings.put(oldID, newID);
    }

    metaIDRanges.add(newRange);
    if (TRACER.isEnabled())
    {
      TRACER.format("Mapping meta ID range: {0} --> {1}", oldRange, newRange); //$NON-NLS-1$
    }
  }

  private void lockObjects() throws InterruptedException
  {
    lockedObjects.clear();
    for (int i = 0; i < dirtyObjectDeltas.length; i++)
    {
      lockedObjects.add(dirtyObjectDeltas[i].getID());
    }

    for (int i = 0; i < detachedObjects.length; i++)
    {
      lockedObjects.add(detachedObjects[i]);
    }

    try
    {
      InternalLockManager lockManager = transaction.getRepository().getLockManager();
      lockManager.lock(LockType.WRITE, transaction, lockedObjects, 1000);
    }
    catch (TimeoutRuntimeException exception)
    {
      lockedObjects.clear();
      throw exception;
    }
  }

  private synchronized void unlockObjects()
  {
    if (!lockedObjects.isEmpty())
    {
      InternalLockManager lockManager = transaction.getRepository().getLockManager();
      lockManager.unlock(LockType.WRITE, transaction, lockedObjects);
      lockedObjects.clear();
    }
  }

  private void computeDirtyObjects(boolean failOnNull, OMMonitor monitor)
  {
    try
    {
      monitor.begin(dirtyObjectDeltas.length);
      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        dirtyObjects[i] = computeDirtyObject(dirtyObjectDeltas[i], failOnNull);
        if (dirtyObjects[i] == null && failOnNull)
        {
          throw new IllegalStateException("Can not retrieve origin revision for " + dirtyObjectDeltas[i]); //$NON-NLS-1$
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private InternalCDORevision computeDirtyObject(InternalCDORevisionDelta dirtyObjectDelta, boolean loadOnDemand)
  {
    CDOID id = dirtyObjectDelta.getID();
    int version = dirtyObjectDelta.getOriginVersion();

    InternalRepository repository = transaction.getRepository();
    InternalCDORevisionManager revisionManager = repository.getRevisionManager();

    CDORevision originObject = revisionManager.getRevisionByVersion(id, transaction.getBranch(), version,
        CDORevision.UNCHUNKED, loadOnDemand);
    if (originObject != null)
    {
      if (loadOnDemand)
      {
        // make sure all chunks are loaded
        for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(originObject.getEClass()))
        {
          if (feature.isMany())
          {
            InternalCDORevision internalOriginObject = (InternalCDORevision)originObject;
            // TODO ensureChunk should get promoted to API because the cast is not really nice here.
            repository.ensureChunk(internalOriginObject, feature, 0, internalOriginObject.getList(feature).size());
          }
        }
      }

      if (!originObject.isCurrent())
      {
        throw new ConcurrentModificationException("Trying to update object " + dirtyObjectDelta.getID() //$NON-NLS-1$
            + " that was already modified"); //$NON-NLS-1$
      }

      InternalCDORevision dirtyObject = (InternalCDORevision)originObject.copy();
      dirtyObjectDelta.apply(dirtyObject);
      dirtyObject.setCreated(timeStamp);
      return dirtyObject;
    }

    return null;
  }

  private void applyIDMappings(InternalCDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      for (InternalCDORevision revision : revisions)
      {
        if (revision != null)
        {
          CDOID newID = idMappings.get(revision.getID());
          if (newID != null)
          {
            revision.setID(newID);
          }

          revision.adjustReferences(idMapper);
          monitor.worked();
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public synchronized void rollback(String message)
  {
    // Check if we already rolledBack
    if (rollbackMessage == null)
    {
      rollbackMessage = message;
      unlockObjects();
      if (accessor != null)
      {
        try
        {
          accessor.rollback();
        }
        catch (RuntimeException ex)
        {
          OM.LOG.warn("Problem while rolling back  the transaction", ex); //$NON-NLS-1$
        }
      }
    }
  }

  protected IStoreAccessor getAccessor()
  {
    return accessor;
  }

  private void updateInfraStructure(OMMonitor monitor)
  {
    try
    {
      monitor.begin(6);
      addNewPackageUnits(monitor.fork());
      addRevisions(newObjects, monitor.fork());
      addRevisions(dirtyObjects, monitor.fork());
      revisedDetachObjects(monitor.fork());
      unlockObjects();
      monitor.worked();

      if (isAutoReleaseLocksEnabled())
      {
        transaction.getRepository().getLockManager().unlock(transaction);
      }

      monitor.worked();
    }
    catch (RuntimeException ex)
    {
      // TODO Rethink this case
      OM.LOG.error("FATAL: Memory infrastructure corrupted after successful commit operation of the store"); //$NON-NLS-1$
    }
    finally
    {
      monitor.done();
    }
  }

  private void addNewPackageUnits(OMMonitor monitor)
  {
    InternalRepository repository = transaction.getRepository();
    InternalCDOPackageRegistry repositoryPackageRegistry = repository.getPackageRegistry(false);
    synchronized (repositoryPackageRegistry)
    {
      try
      {
        monitor.begin(newPackageUnits.length);
        for (int i = 0; i < newPackageUnits.length; i++)
        {
          newPackageUnits[i].setState(CDOPackageUnit.State.LOADED);
          repositoryPackageRegistry.putPackageUnit(newPackageUnits[i]);
          monitor.worked();
        }

        repositoryPackageRegistry.getMetaInstanceMapper().mapMetaInstances(packageRegistry.getMetaInstanceMapper());
      }
      finally
      {
        monitor.done();
      }
    }
  }

  private void addRevisions(CDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      InternalCDORevisionManager revisionManager = transaction.getRepository().getRevisionManager();
      for (CDORevision revision : revisions)
      {
        if (revision != null)
        {
          revisionManager.getCache().addRevision(revision);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void revisedDetachObjects(OMMonitor monitor)
  {
    try
    {
      monitor.begin(detachedRevisions.size());
      for (InternalCDORevision revision : detachedRevisions)
      {
        revision.setRevised(getBranchPoint().getTimeStamp() - 1);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void detachObjects(OMMonitor monitor)
  {
    detachedRevisions.clear();
    InternalCDORevisionManager revisionManager = transaction.getRepository().getRevisionManager();
    CDOID[] detachedObjects = getDetachedObjects();

    try
    {
      monitor.begin(detachedObjects.length);
      for (CDOID id : detachedObjects)
      {
        InternalCDORevision revision = (InternalCDORevision)revisionManager.getRevision(id, transaction,
            CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, false);
        if (revision != null)
        {
          detachedRevisions.add(revision);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TransactionPackageRegistry extends CDOPackageRegistryImpl
  {
    private static final long serialVersionUID = 1L;

    public TransactionPackageRegistry(InternalCDOPackageRegistry repositoryPackageRegistry)
    {
      delegateRegistry = repositoryPackageRegistry;
      setPackageLoader(repositoryPackageRegistry.getPackageLoader());
    }

    @Override
    public void putPackageUnit(InternalCDOPackageUnit packageUnit)
    {
      packageUnit.setPackageRegistry(this);
      for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        EPackage ePackage = packageInfo.getEPackage();
        basicPut(ePackage.getNsURI(), ePackage);
      }

      resetInternalCaches();
    }

    @Override
    protected void disposePackageUnits()
    {
      // Do nothing
    }
  }
}
