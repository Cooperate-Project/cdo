/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/233273
 *    Simon McDuff - http://bugs.eclipse.org/233490
 *    Stefan Winkler - changed order of determining audit and revision delta support.
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.InternalNotificationManager;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.ContainerQueryHandlerProvider;
import org.eclipse.emf.cdo.spi.server.InternalCommitManager;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Repository extends Container<Object> implements InternalRepository
{
  private String name;

  private IStore store;

  private String uuid;

  private Map<String, String> properties;

  private boolean supportingRevisionDeltas;

  private boolean supportingAudits;

  private boolean verifyingRevisions;

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDORevisionManager revisionManager;

  private InternalSessionManager sessionManager;

  private InternalQueryManager queryManager;

  private InternalNotificationManager notificationManager;

  private InternalCommitManager commitManager;

  private InternalLockManager lockManager;

  private IQueryHandlerProvider queryHandlerProvider;

  private List<ReadAccessHandler> readAccessHandlers = new ArrayList<ReadAccessHandler>();

  private List<WriteAccessHandler> writeAccessHandlers = new ArrayList<WriteAccessHandler>();

  @ExcludeFromDump
  private transient long lastCommitTimeStamp;

  @ExcludeFromDump
  private transient Object lastCommitTimeStampLock = new Object();

  public Repository()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public IStore getStore()
  {
    return store;
  }

  public void setStore(IStore store)
  {
    this.store = store;
    store.setRepository(this);
  }

  public String getUUID()
  {
    if (uuid == null)
    {
      uuid = getProperties().get(Props.OVERRIDE_UUID);
      if (uuid == null)
      {
        uuid = UUID.randomUUID().toString();
      }
      else if (uuid.length() == 0)
      {
        uuid = getName();
      }
    }

    return uuid;
  }

  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public boolean isSupportingRevisionDeltas()
  {
    return supportingRevisionDeltas;
  }

  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  public boolean isVerifyingRevisions()
  {
    return verifyingRevisions;
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadPackageUnit((InternalCDOPackageUnit)packageUnit);
  }

  public InternalCDORevision loadRevision(CDOID id, int referenceChunk, int prefetchDepth)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.readRevision(id, referenceChunk, revisionManager.getCache());
  }

  public InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, int prefetchDepth, long timeStamp)
  {
    if (isSupportingAudits())
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      return accessor.readRevisionByTime(id, referenceChunk, revisionManager.getCache(), timeStamp);
    }

    // TODO Simon*: Is this check necessary here?
    // TODO Eike: To have better exception message.
    // By knowing if the back-end supports it, it let know the user that it could be achieved by changing its
    // configuration file at the server.
    // if (getRepository().getStore().hasAuditingSupport())
    // {
    // throw new UnsupportedOperationException(
    // "Auditing supports isn't activated (see IRepository.Props.PROP_SUPPORTING_AUDITS).");
    // }

    throw new IllegalStateException("No support for auditing mode"); //$NON-NLS-1$
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int prefetchDepth, int version)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (isSupportingAudits())
    {
      return accessor.readRevisionByVersion(id, referenceChunk, revisionManager.getCache(), version);
    }

    InternalCDORevision revision = loadRevision(id, referenceChunk, prefetchDepth);
    if (revision.getVersion() == version)
    {
      return revision;
    }

    throw new IllegalStateException("Cannot access object with id " + id + " and version " + version); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk, int prefetchDepth)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    for (CDOID id : ids)
    {
      InternalCDORevision revision = accessor.readRevision(id, referenceChunk, revisionManager.getCache());
      revisions.add(revision);
    }

    return revisions;
  }

  public List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, int prefetchDepth,
      long timeStamp)
  {
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    for (CDOID id : ids)
    {
      InternalCDORevision revision = loadRevisionByTime(id, referenceChunk, prefetchDepth, timeStamp);
      revisions.add(revision);
    }

    return revisions;
  }

  public InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
  {
    IStoreAccessor accessor = null;
    if (isVerifyingRevisions())
    {
      accessor = StoreThreadLocal.getAccessor();
      revision = accessor.verifyRevision(revision);
    }

    ensureChunks(revision, referenceChunk, accessor);
    return revision;
  }

  protected void ensureChunks(InternalCDORevision revision, int referenceChunk, IStoreAccessor accessor)
  {
    EClass eClass = revision.getEClass();
    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(eClass);
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
      {
        MoveableList<Object> list = revision.getList(feature);
        int chunkEnd = Math.min(referenceChunk, list.size());
        accessor = ensureChunk(revision, feature, accessor, list, 0, chunkEnd);
      }
    }
  }

  public IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, int chunkStart,
      int chunkEnd)
  {
    MoveableList<Object> list = revision.getList(feature);
    chunkEnd = Math.min(chunkEnd, list.size());
    return ensureChunk(revision, feature, StoreThreadLocal.getAccessor(), list, chunkStart, chunkEnd);
  }

  protected IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature,
      IStoreAccessor accessor, MoveableList<Object> list, int chunkStart, int chunkEnd)
  {
    IStoreChunkReader chunkReader = null;
    int fromIndex = -1;
    for (int j = chunkStart; j < chunkEnd; j++)
    {
      if (list.get(j) == InternalCDOList.UNINITIALIZED)
      {
        if (fromIndex == -1)
        {
          fromIndex = j;
        }
      }
      else
      {
        if (fromIndex != -1)
        {
          if (chunkReader == null)
          {
            if (accessor == null)
            {
              accessor = StoreThreadLocal.getAccessor();
            }

            chunkReader = accessor.createChunkReader(revision, feature);
          }

          int toIndex = j;
          if (fromIndex == toIndex - 1)
          {
            chunkReader.addSimpleChunk(fromIndex);
          }
          else
          {
            chunkReader.addRangedChunk(fromIndex, toIndex);
          }

          fromIndex = -1;
        }
      }
    }

    // Add last chunk
    if (fromIndex != -1)
    {
      if (chunkReader == null)
      {
        if (accessor == null)
        {
          accessor = StoreThreadLocal.getAccessor();
        }

        chunkReader = accessor.createChunkReader(revision, feature);
      }

      int toIndex = chunkEnd;
      if (fromIndex == toIndex - 1)
      {
        chunkReader.addSimpleChunk(fromIndex);
      }
      else
      {
        chunkReader.addRangedChunk(fromIndex, toIndex);
      }
    }

    if (chunkReader != null)
    {
      List<Chunk> chunks = chunkReader.executeRead();
      for (Chunk chunk : chunks)
      {
        int startIndex = chunk.getStartIndex();
        for (int indexInChunk = 0; indexInChunk < chunk.size(); indexInChunk++)
        {
          Object id = chunk.get(indexInChunk);
          list.set(startIndex + indexInChunk, id);
        }
      }
    }

    return accessor;
  }

  public InternalCDOPackageRegistry getPackageRegistry(boolean considerCommitContext)
  {
    if (considerCommitContext)
    {
      IStoreAccessor.CommitContext commitContext = StoreThreadLocal.getCommitContext();
      if (commitContext != null)
      {
        InternalCDOPackageRegistry contextualPackageRegistry = commitContext.getPackageRegistry();
        if (contextualPackageRegistry != null)
        {
          return contextualPackageRegistry;
        }
      }
    }

    return packageRegistry;
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return getPackageRegistry(true);
  }

  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }

  public InternalSessionManager getSessionManager()
  {
    return sessionManager;
  }

  /**
   * @since 2.0
   */
  public void setSessionManager(InternalSessionManager sessionManager)
  {
    this.sessionManager = sessionManager;
  }

  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  /**
   * @since 2.0
   */
  public void setRevisionManager(InternalCDORevisionManager revisionManager)
  {
    this.revisionManager = revisionManager;
  }

  /**
   * @since 2.0
   */
  public InternalQueryManager getQueryManager()
  {
    return queryManager;
  }

  /**
   * @since 2.0
   */
  public void setQueryManager(InternalQueryManager queryManager)
  {
    this.queryManager = queryManager;
  }

  /**
   * @since 2.0
   */
  public InternalNotificationManager getNotificationManager()
  {
    return notificationManager;
  }

  /**
   * @since 2.0
   */
  public void setNotificationManager(InternalNotificationManager notificationManager)
  {
    this.notificationManager = notificationManager;
  }

  /**
   * @since 2.0
   */
  public InternalCommitManager getCommitManager()
  {
    return commitManager;
  }

  /**
   * @since 2.0
   */
  public void setCommitManager(InternalCommitManager commitManager)
  {
    this.commitManager = commitManager;
  }

  /**
   * @since 2.0
   */
  public InternalLockManager getLockManager()
  {
    return lockManager;
  }

  /**
   * @since 2.0
   */
  public void setLockManager(InternalLockManager lockManager)
  {
    this.lockManager = lockManager;
  }

  public long getLastCommitTimeStamp()
  {
    synchronized (lastCommitTimeStampLock)
    {
      return lastCommitTimeStamp;
    }
  }

  /**
   * @since 2.0
   */
  public long createCommitTimeStamp()
  {
    long now = System.currentTimeMillis();
    synchronized (lastCommitTimeStampLock)
    {
      if (lastCommitTimeStamp != 0)
      {
        while (lastCommitTimeStamp == now)
        {
          ConcurrencyUtil.sleep(1);
          now = System.currentTimeMillis();
        }
      }

      // TODO Persist lastCommitTimeStamp in store
      lastCommitTimeStamp = now;
      return now;
    }
  }

  /**
   * @since 2.0
   */
  public IQueryHandlerProvider getQueryHandlerProvider()
  {
    return queryHandlerProvider;
  }

  /**
   * @since 2.0
   */
  public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider)
  {
    this.queryHandlerProvider = queryHandlerProvider;
  }

  /**
   * @since 2.0
   */
  public synchronized IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    if (CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES.equals(info.getQueryLanguage()))
    {
      return new ResourcesQueryHandler();
    }

    IStoreAccessor storeAccessor = StoreThreadLocal.getAccessor();
    if (storeAccessor != null)
    {
      IQueryHandler handler = storeAccessor.getQueryHandler(info);
      if (handler != null)
      {
        return handler;
      }
    }

    if (queryHandlerProvider == null)
    {
      queryHandlerProvider = new ContainerQueryHandlerProvider(IPluginContainer.INSTANCE);
    }

    IQueryHandler handler = queryHandlerProvider.getQueryHandler(info);
    if (handler != null)
    {
      return handler;
    }

    return null;
  }

  public Object[] getElements()
  {
    final Object[] elements = { packageRegistry, sessionManager, revisionManager, queryManager, notificationManager,
        commitManager, lockManager, store };
    return elements;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  /**
   * @since 2.0
   */
  public long getCreationTime()
  {
    return store.getCreationTime();
  }

  /**
   * @since 2.0
   */
  public void validateTimeStamp(long timeStamp) throws IllegalArgumentException
  {
    long creationTimeStamp = getCreationTime();
    if (timeStamp < creationTimeStamp)
    {
      throw new IllegalArgumentException("timeStamp < repository creation time: " + creationTimeStamp); //$NON-NLS-1$
    }

    long currentTimeStamp = System.currentTimeMillis();
    if (timeStamp > currentTimeStamp)
    {
      throw new IllegalArgumentException("timeStamp > current time: " + currentTimeStamp); //$NON-NLS-1$
    }
  }

  /**
   * @since 2.0
   */
  public void addHandler(Handler handler)
  {
    if (handler instanceof ReadAccessHandler)
    {
      synchronized (readAccessHandlers)
      {
        if (!readAccessHandlers.contains(handler))
        {
          readAccessHandlers.add((ReadAccessHandler)handler);
        }
      }
    }
    else if (handler instanceof WriteAccessHandler)
    {
      synchronized (writeAccessHandlers)
      {
        if (!writeAccessHandlers.contains(handler))
        {
          writeAccessHandlers.add((WriteAccessHandler)handler);
        }
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid handler: " + handler); //$NON-NLS-1$
    }
  }

  /**
   * @since 2.0
   */
  public void removeHandler(Handler handler)
  {
    if (handler instanceof ReadAccessHandler)
    {
      synchronized (readAccessHandlers)
      {
        readAccessHandlers.remove(handler);
      }
    }
    else if (handler instanceof WriteAccessHandler)
    {
      synchronized (writeAccessHandlers)
      {
        writeAccessHandlers.remove(handler);
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid handler: " + handler); //$NON-NLS-1$
    }
  }

  /**
   * @since 2.0
   */
  public void notifyReadAccessHandlers(InternalSession session, CDORevision[] revisions,
      List<CDORevision> additionalRevisions)
  {
    ReadAccessHandler[] handlers;
    synchronized (readAccessHandlers)
    {
      int size = readAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = readAccessHandlers.toArray(new ReadAccessHandler[size]);
    }

    for (ReadAccessHandler handler : handlers)
    {
      // Do *not* protect against unchecked exceptions from handlers!
      handler.handleRevisionsBeforeSending(session, revisions, additionalRevisions);
    }
  }

  /**
   * @since 2.0
   */
  public void notifyWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext,
      OMMonitor monitor)
  {
    WriteAccessHandler[] handlers;
    synchronized (writeAccessHandlers)
    {
      int size = writeAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = writeAccessHandlers.toArray(new WriteAccessHandler[size]);
    }

    try
    {
      monitor.begin(handlers.length);
      for (WriteAccessHandler handler : handlers)
      {
        // Do *not* protect against unchecked exceptions from handlers!
        handler.handleTransactionBeforeCommitting(transaction, commitContext, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDORepositoryInfo[{0}]", name); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(!StringUtil.isEmpty(name), "name is empty"); //$NON-NLS-1$
    checkState(packageRegistry, "packageRegistry"); //$NON-NLS-1$
    checkState(sessionManager, "sessionManager"); //$NON-NLS-1$
    checkState(revisionManager, "revisionManager"); //$NON-NLS-1$
    checkState(queryManager, "queryManager"); //$NON-NLS-1$
    checkState(notificationManager, "notificationManager"); //$NON-NLS-1$
    checkState(commitManager, "commitManager"); //$NON-NLS-1$
    checkState(lockManager, "lockingManager"); //$NON-NLS-1$

    packageRegistry.setReplacingDescriptors(true);
    packageRegistry.setPackageLoader(this);
    revisionManager.setRevisionLoader(this);
    sessionManager.setRepository(this);
    queryManager.setRepository(this);
    notificationManager.setRepository(this);
    commitManager.setRepository(this);
    lockManager.setRepository(this);

    checkState(store, "store"); //$NON-NLS-1$

    {
      String value = getProperties().get(Props.SUPPORTING_AUDITS);
      if (value != null)
      {
        supportingAudits = Boolean.valueOf(value);
        store.setRevisionTemporality(supportingAudits ? IStore.RevisionTemporality.AUDITING
            : IStore.RevisionTemporality.NONE);
      }
      else
      {
        supportingAudits = store.getRevisionTemporality() == IStore.RevisionTemporality.AUDITING;
      }
    }

    supportingRevisionDeltas = store.getSupportedChangeFormats().contains(IStore.ChangeFormat.DELTA);

    {
      String value = getProperties().get(Props.VERIFYING_REVISIONS);
      verifyingRevisions = value == null ? false : Boolean.valueOf(value);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(packageRegistry);
    LifecycleUtil.activate(store);
    LifecycleUtil.activate(sessionManager);
    LifecycleUtil.activate(revisionManager);
    LifecycleUtil.activate(queryManager);
    LifecycleUtil.activate(notificationManager);
    LifecycleUtil.activate(commitManager);
    LifecycleUtil.activate(queryHandlerProvider);
    LifecycleUtil.activate(lockManager);

    if (store.isFirstTime())
    {
      initSystemPackages();
    }
    else
    {
      readPackageUnits();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(lockManager);
    LifecycleUtil.deactivate(queryHandlerProvider);
    LifecycleUtil.deactivate(commitManager);
    LifecycleUtil.deactivate(notificationManager);
    LifecycleUtil.deactivate(queryManager);
    LifecycleUtil.deactivate(revisionManager);
    LifecycleUtil.deactivate(sessionManager);
    LifecycleUtil.deactivate(store);
    LifecycleUtil.deactivate(packageRegistry);
    super.doDeactivate();
  }

  protected void initSystemPackages()
  {
    IStoreAccessor writer = store.getWriter(null);
    StoreThreadLocal.setAccessor(writer);

    try
    {
      InternalCDOPackageUnit ecoreUnit = initSystemPackage(EcorePackage.eINSTANCE);
      InternalCDOPackageUnit eresourceUnit = initSystemPackage(EresourcePackage.eINSTANCE);
      InternalCDOPackageUnit[] systemUnits = { ecoreUnit, eresourceUnit };

      writer.writePackageUnits(systemUnits, new Monitor());
      writer.commit(new Monitor());
    }
    finally
    {
      LifecycleUtil.deactivate(writer); // Don't let the null-context accessor go to the pool!
      StoreThreadLocal.release();
    }
  }

  protected InternalCDOPackageUnit initSystemPackage(EPackage ePackage)
  {
    EMFUtil.registerPackage(ePackage, packageRegistry);
    InternalCDOPackageInfo packageInfo = packageRegistry.getPackageInfo(ePackage);
    CDOIDMetaRange metaIDRange = store.getNextMetaIDRange(packageInfo.getMetaIDRange().size());
    packageInfo.setMetaIDRange(metaIDRange);
    packageRegistry.getMetaInstanceMapper().mapMetaInstances(ePackage, metaIDRange);

    InternalCDOPackageUnit packageUnit = packageInfo.getPackageUnit();
    packageUnit.setTimeStamp(store.getCreationTime());
    packageUnit.setState(CDOPackageUnit.State.LOADED);
    return packageUnit;
  }

  protected void readPackageUnits()
  {
    IStoreAccessor reader = store.getReader(null);
    StoreThreadLocal.setAccessor(reader);

    try
    {
      Collection<InternalCDOPackageUnit> packageUnits = reader.readPackageUnits();
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        packageRegistry.putPackageUnit(packageUnit);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(reader); // Don't let the null-context accessor go to the pool!
      StoreThreadLocal.release();
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static class Default extends Repository
  {
    public Default()
    {
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      if (getPackageRegistry(false) == null)
      {
        setPackageRegistry(createPackageRegistry());
      }

      if (getSessionManager() == null)
      {
        setSessionManager(createSessionManager());
      }

      if (getRevisionManager() == null)
      {
        setRevisionManager(createRevisionManager());
      }

      if (getQueryManager() == null)
      {
        setQueryManager(createQueryManager());
      }

      if (getNotificationManager() == null)
      {
        setNotificationManager(createNotificationManager());
      }

      if (getCommitManager() == null)
      {
        setCommitManager(createCommitManager());
      }

      if (getLockManager() == null)
      {
        setLockManager(createLockManager());
      }

      super.doBeforeActivate();
    }

    protected InternalCDOPackageRegistry createPackageRegistry()
    {
      return new CDOPackageRegistryImpl();
    }

    protected InternalSessionManager createSessionManager()
    {
      return new SessionManager();
    }

    protected InternalCDORevisionManager createRevisionManager()
    {
      return new CDORevisionManagerImpl();
    }

    protected InternalQueryManager createQueryManager()
    {
      return new QueryManager();
    }

    protected InternalNotificationManager createNotificationManager()
    {
      return new NotificationManager();
    }

    protected InternalCommitManager createCommitManager()
    {
      return new CommitManager();
    }

    protected InternalLockManager createLockManager()
    {
      return new LockManager();
    }
  }
}
