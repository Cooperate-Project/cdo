/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233273
 *    Simon McDuff - bug 233490
 *    Stefan Winkler - changed order of determining audit and revision delta support.
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.server.ContainerQueryHandlerProvider;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalCommitManager;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Repository extends Container<Object> implements InternalRepository
{
  private String name;

  private String uuid;

  private InternalStore store;

  private State state = State.ONLINE;

  private Map<String, String> properties;

  private boolean supportingAudits;

  private boolean supportingBranches;

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDOBranchManager branchManager;

  private InternalCDORevisionManager revisionManager;

  private InternalCDOCommitInfoManager commitInfoManager;

  private InternalSessionManager sessionManager;

  private InternalQueryManager queryManager;

  private InternalCommitManager commitManager;

  private InternalLockManager lockManager;

  private IQueryHandlerProvider queryHandlerProvider;

  private List<ReadAccessHandler> readAccessHandlers = new ArrayList<ReadAccessHandler>();

  private List<WriteAccessHandler> writeAccessHandlers = new ArrayList<WriteAccessHandler>();

  @ExcludeFromDump
  private transient long lastCommitTimeStamp;

  @ExcludeFromDump
  private transient Object lastCommitTimeStampLock = new Object();

  @ExcludeFromDump
  private transient Object createBranchLock = new Object();

  private CDOID rootResourceID;

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

  public InternalStore getStore()
  {
    return store;
  }

  public void setStore(InternalStore store)
  {
    this.store = store;
    store.setRepository(this);
  }

  public Type getType()
  {
    return Type.MASTER;
  }

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    checkArg(state, "state"); //$NON-NLS-1$
    if (this.state != state)
    {
      State oldState = this.state;
      this.state = state;
      fireEvent(new RepositoryStateChangedEvent(this, oldState, state));

      if (sessionManager != null)
      {
        sessionManager.sendRepositoryStateNotification(oldState, state);
      }
    }
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

  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  public boolean isSupportingBranches()
  {
    return supportingBranches;
  }

  public String getStoreType()
  {
    return store.getType();
  }

  public Set<CDOID.ObjectType> getObjectIDTypes()
  {
    return store.getObjectIDTypes();
  }

  public CDOID getRootResourceID()
  {
    return rootResourceID;
  }

  public void setRootResourceID(CDOID rootResourceID)
  {
    this.rootResourceID = rootResourceID;
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadPackageUnit((InternalCDOPackageUnit)packageUnit);
  }

  public int createBranch(BranchInfo branchInfo)
  {
    synchronized (createBranchLock)
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      return accessor.createBranch(branchInfo);
    }
  }

  public BranchInfo loadBranch(int branchID)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadBranch(branchID);
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadSubBranches(branchID);
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadBranches(startID, endID, branchHandler);
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.loadCommitInfos(branch, startTime, endTime, handler);
  }

  public CDOCommitData loadCommitData(long timeStamp)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadCommitData(timeStamp);
  }

  public List<InternalCDORevision> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint,
      int referenceChunk, int prefetchDepth)
  {
    for (RevisionInfo info : infos)
    {
      CDOID id = info.getID();
      RevisionInfo.Type type = info.getType();
      switch (type)
      {
      case AVAILABLE_NORMAL: // direct == false
      {
        RevisionInfo.Available.Normal availableInfo = (RevisionInfo.Available.Normal)info;
        checkArg(availableInfo.isDirect() == false, "Load is not needed");
        break;
      }

      case AVAILABLE_POINTER: // direct == false || target == null
      {
        RevisionInfo.Available.Pointer pointerInfo = (RevisionInfo.Available.Pointer)info;
        boolean needsTarget = !pointerInfo.hasTarget();
        checkArg(pointerInfo.isDirect() == false || needsTarget, "Load is not needed");

        if (needsTarget)
        {
          CDOBranchVersion targetBranchVersion = pointerInfo.getTargetBranchVersion();
          InternalCDORevision target = loadRevisionByVersion(id, targetBranchVersion, referenceChunk);
          PointerCDORevision pointer = new PointerCDORevision(target.getEClass(), id, pointerInfo
              .getAvailableBranchVersion().getBranch(), CDORevision.UNSPECIFIED_DATE, target);

          info.setResult(target);
          info.setSynthetic(pointer);
          continue;
        }

        break;
      }

      case AVAILABLE_DETACHED: // direct == false
      {
        RevisionInfo.Available.Detached detachedInfo = (RevisionInfo.Available.Detached)info;
        checkArg(detachedInfo.isDirect() == false, "Load is not needed");
        break;
      }

      case MISSING:
      {
        break;
      }

      default:
        throw new IllegalStateException("Invalid revision info type: " + type);
      }

      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      InternalCDORevision revision = accessor.readRevision(id, branchPoint, referenceChunk, revisionManager);
      if (revision == null)
      {
        // Case "Pointer"?
        InternalCDORevision target = loadRevisionTarget(id, branchPoint, referenceChunk, accessor);
        if (target != null)
        {
          CDOBranch branch = branchPoint.getBranch();
          long revised = loadRevisionRevised(id, branch);
          PointerCDORevision pointer = new PointerCDORevision(target.getEClass(), id, branch, revised, target);

          info.setSynthetic(pointer);
        }

        info.setResult(target);
      }
      else if (revision instanceof DetachedCDORevision)
      {
        DetachedCDORevision detached = (DetachedCDORevision)revision;
        info.setSynthetic(detached);
      }
      else
      {
        info.setResult(revision);
      }
    }

    return null;
  }

  private InternalCDORevision loadRevisionTarget(CDOID id, CDOBranchPoint branchPoint, int referenceChunk,
      IStoreAccessor accessor)
  {
    CDOBranch branch = branchPoint.getBranch();
    while (!branch.isMainBranch())
    {
      branchPoint = branch.getBase();
      branch = branchPoint.getBranch();

      InternalCDORevision revision = accessor.readRevision(id, branchPoint, referenceChunk, revisionManager);
      if (revision != null)
      {
        return revision;
      }
    }

    return null;
  }

  private long loadRevisionRevised(CDOID id, CDOBranch branch)
  {
    InternalCDORevision revision = loadRevisionByVersion(id, branch.getVersion(CDORevision.FIRST_VERSION),
        CDORevision.UNCHUNKED);
    if (revision != null)
    {
      return revision.getTimeStamp() - 1;
    }

    return CDORevision.UNSPECIFIED_DATE;
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.readRevisionByVersion(id, branchVersion, referenceChunk, revisionManager);
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
    checkInactive();
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
    checkInactive();
    this.sessionManager = sessionManager;
  }

  public InternalCDOBranchManager getBranchManager()
  {
    return branchManager;
  }

  public void setBranchManager(InternalCDOBranchManager branchManager)
  {
    checkInactive();
    this.branchManager = branchManager;
  }

  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  public void setCommitInfoManager(InternalCDOCommitInfoManager commitInfoManager)
  {
    checkInactive();
    this.commitInfoManager = commitInfoManager;
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
    checkInactive();
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
    checkInactive();
    this.queryManager = queryManager;
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
    checkInactive();
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
    checkInactive();
    this.lockManager = lockManager;
  }

  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    return new TransactionCommitContext(transaction);
  }

  public long getLastCommitTimeStamp()
  {
    synchronized (lastCommitTimeStampLock)
    {
      return lastCommitTimeStamp;
    }
  }

  protected void setLastCommitTimeStamp(long lastCommitTimeStamp)
  {
    synchronized (lastCommitTimeStampLock)
    {
      if (this.lastCommitTimeStamp < lastCommitTimeStamp)
      {
        this.lastCommitTimeStamp = lastCommitTimeStamp;
        lastCommitTimeStampLock.notifyAll();
      }
    }
  }

  public long waitForCommit(long timeout)
  {
    synchronized (lastCommitTimeStampLock)
    {
      try
      {
        lastCommitTimeStampLock.wait(timeout);
      }
      catch (Exception ignore)
      {
      }

      return lastCommitTimeStamp;
    }
  }

  public long createCommitTimeStamp(CDOBranch branch)
  {
    long now = getTimeStamp();
    if (branch == null)
    {
      // Must be unique in a new branch
      return now;
    }

    synchronized (lastCommitTimeStampLock)
    {
      if (lastCommitTimeStamp != 0)
      {
        while (lastCommitTimeStamp == now)
        {
          ConcurrencyUtil.sleep(1L);
          now = getTimeStamp();
        }
      }

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
    final Object[] elements = { packageRegistry, branchManager, revisionManager, sessionManager, queryManager,
        commitManager, commitInfoManager, lockManager, store };
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
      throw new IllegalArgumentException(
          MessageFormat
              .format(
                  "timeStamp ({0}) < repository creation time ({1})", CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(creationTimeStamp))); //$NON-NLS-1$
    }

    long currentTimeStamp = getTimeStamp();
    if (timeStamp > currentTimeStamp)
    {
      throw new IllegalArgumentException(
          MessageFormat
              .format(
                  "timeStamp ({0}) > current time ({1})", CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(currentTimeStamp))); //$NON-NLS-1$
    }
  }

  public long getTimeStamp()
  {
    return System.currentTimeMillis();
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

  public void notifyWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext,
      boolean beforeCommit, OMMonitor monitor)
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
        try
        {
          if (beforeCommit)
          {
            handler.handleTransactionBeforeCommitting(transaction, commitContext, monitor.fork());
          }
          else
          {
            handler.handleTransactionAfterCommitting(transaction, commitContext, monitor.fork());
          }
        }
        catch (RuntimeException ex)
        {
          if (!beforeCommit)
          {
            OM.LOG.error(ex);
          }
          else
          {
            // Do *not* protect against unchecked exceptions from handlers on before case!
            throw ex;
          }
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public void replicate(CDOReplicationContext context)
  {
    int startID = context.getLastReplicatedBranchID() + 1;
    branchManager.getBranches(startID, 0, context);

    long startTime = context.getLastReplicatedCommitTime();
    if (context.isSqueezeCommitInfos())
    {
      replicateSqueezed(startTime, context);
    }
    else
    {
      commitInfoManager.getCommitInfos(startTime + 1L, CDOBranchPoint.UNSPECIFIED_DATE, context);
    }
  }

  private void replicateSqueezed(long startTime, CDOCommitInfoHandler handler)
  {
    Set<CDOPackageUnit> replicatedPackageUnits = new HashSet<CDOPackageUnit>();
    InternalCDOCommitInfoManager manager = getCommitInfoManager();

    List<CDOChangeSetSegment> segments = getBaselineSegments(startTime);
    for (CDOChangeSetSegment segment : segments)
    {
      List<CDOPackageUnit> newPackages = getPackageUnitsToReplicate(segment, replicatedPackageUnits);
      CDOChangeSetData changeSet = getChangeSet(segment, segment.getEndPoint());

      if (!newPackages.isEmpty() || !changeSet.isEmpty())
      {
        List<CDOIDAndVersion> newObjects = changeSet.getNewObjects();
        List<CDORevisionKey> changedObjects = changeSet.getChangedObjects();
        List<CDOIDAndVersion> detachedObjects = changeSet.getDetachedObjects();
        CDOCommitData data = new CDOCommitDataImpl(newPackages, newObjects, changedObjects, detachedObjects);

        CDOBranch branch = segment.getBranch();
        long timeStamp = segment.getTimeStamp();
        String comment = "<replicate squeezed commits>"; //$NON-NLS-1$
        CDOCommitInfo commitInfo = manager.createCommitInfo(branch, timeStamp, SYSTEM_USER_ID, comment, data);

        handler.handleCommitInfo(commitInfo);
      }
    }
  }

  private List<CDOChangeSetSegment> getBaselineSegments(long startTime)
  {
    List<CDOChangeSetSegment> segments = new ArrayList<CDOChangeSetSegment>();
    InternalCDOBranch branch = getBranchManager().getMainBranch();
    getBaselineSegments(startTime, branch, segments);
    Collections.sort(segments);
    return segments;
  }

  private void getBaselineSegments(long startTime, InternalCDOBranch branch, List<CDOChangeSetSegment> segments)
  {
    if (startTime == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      startTime = branch.getBase().getTimeStamp();
    }

    InternalCDOBranch[] branches = branch.getBranches();
    Arrays.sort(branches, new Comparator<CDOBranch>()
    {
      public int compare(CDOBranch o1, CDOBranch o2)
      {
        return CDOCommonUtil.compareTimeStamps(o1.getBase().getTimeStamp(), o2.getBase().getTimeStamp());
      }
    });

    for (InternalCDOBranch subBranch : branches)
    {
      long baseTimeStamp = subBranch.getBase().getTimeStamp();
      if (baseTimeStamp > startTime)
      {
        segments.add(new CDOChangeSetSegment(branch, startTime, baseTimeStamp));
      }

      getBaselineSegments(baseTimeStamp, subBranch, segments);
      startTime = baseTimeStamp;
    }

    segments.add(new CDOChangeSetSegment(branch, startTime, CDOBranchPoint.UNSPECIFIED_DATE));
  }

  private List<CDOPackageUnit> getPackageUnitsToReplicate(CDOChangeSetSegment segment, Set<CDOPackageUnit> replicated)
  {
    List<CDOPackageUnit> result = new ArrayList<CDOPackageUnit>();
    InternalCDOPackageRegistry packageRegistry = getPackageRegistry(false);

    long startTime = segment.getTimeStamp();
    long endTime = segment.getEndTime();

    InternalCDOPackageUnit[] packageUnits = packageRegistry.getPackageUnits(startTime, endTime);
    for (InternalCDOPackageUnit packageUnit : packageUnits)
    {
      if (!packageUnit.isSystem() && replicated.add(packageUnit))
      {
        result.add(packageUnit);
      }
    }

    return result;
  }

  public CDOChangeSetData getChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    CDOChangeSetSegment[] segments = CDOChangeSetSegment.createFrom(startPoint, endPoint);

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    Set<CDOID> ids = accessor.readChangeSet(segments);

    return CDORevisionDeltaUtil.createChangeSetData(ids, startPoint, endPoint, revisionManager);
  }

  public Set<CDOID> getMergeData(CDORevisionAvailabilityInfo ancestorInfo, CDORevisionAvailabilityInfo targetInfo,
      CDORevisionAvailabilityInfo sourceInfo)
  {
    CDOBranchPoint ancestor = ancestorInfo.getBranchPoint();
    CDOBranchPoint target = targetInfo.getBranchPoint();
    CDOBranchPoint source = sourceInfo.getBranchPoint();

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    Set<CDOID> ids = accessor.readChangeSet(CDOChangeSetSegment.createFrom(ancestor, target));
    ids.addAll(accessor.readChangeSet(CDOChangeSetSegment.createFrom(ancestor, source)));

    loadMergeData(ids, ancestorInfo);
    loadMergeData(ids, targetInfo);
    loadMergeData(ids, sourceInfo);

    return ids;
  }

  private void loadMergeData(Set<CDOID> ids, CDORevisionAvailabilityInfo info)
  {
    CDOBranchPoint branchPoint = info.getBranchPoint();
    for (CDOID id : ids)
    {
      if (info.containsRevision(id))
      {
        info.removeRevision(id);
      }
      else
      {
        InternalCDORevision revision = getRevisionFromBranch(id, branchPoint);
        if (revision != null)
        {
          info.addRevision(revision);
        }
        else
        {
          info.removeRevision(id);
        }
      }
    }
  }

  private InternalCDORevision getRevisionFromBranch(CDOID id, CDOBranchPoint branchPoint)
  {
    InternalCDORevision revision = revisionManager.getRevision(id, branchPoint, CDORevision.UNCHUNKED,
        CDORevision.DEPTH_NONE, true);
    // if (revision == null || !ObjectUtil.equals(revision.getBranch(), branchPoint.getBranch()))
    // {
    // return null;
    // }

    return revision;
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
    checkState(branchManager, "branchManager"); //$NON-NLS-1$
    checkState(revisionManager, "revisionManager"); //$NON-NLS-1$
    checkState(queryManager, "queryManager"); //$NON-NLS-1$
    checkState(commitInfoManager, "commitInfoManager"); //$NON-NLS-1$
    checkState(commitManager, "commitManager"); //$NON-NLS-1$
    checkState(lockManager, "lockingManager"); //$NON-NLS-1$

    packageRegistry.setReplacingDescriptors(true);
    packageRegistry.setPackageLoader(this);
    branchManager.setBranchLoader(this);
    branchManager.setTimeProvider(this);
    revisionManager.setRevisionLoader(this);
    sessionManager.setRepository(this);
    queryManager.setRepository(this);
    commitInfoManager.setCommitInfoLoader(this);
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

    {
      String value = getProperties().get(Props.SUPPORTING_BRANCHES);
      if (value != null)
      {
        supportingBranches = Boolean.valueOf(value);
        store.setRevisionParallelism(supportingBranches ? IStore.RevisionParallelism.BRANCHING
            : IStore.RevisionParallelism.NONE);
      }
      else
      {
        supportingBranches = store.getRevisionParallelism() == IStore.RevisionParallelism.BRANCHING;
      }
    }

    revisionManager.setSupportingBranches(supportingBranches);
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
    LifecycleUtil.activate(commitInfoManager);
    LifecycleUtil.activate(commitManager);
    LifecycleUtil.activate(queryHandlerProvider);
    LifecycleUtil.activate(lockManager);

    lastCommitTimeStamp = Math.max(store.getCreationTime(), store.getLastCommitTime());
    branchManager.initMainBranch(lastCommitTimeStamp);
    LifecycleUtil.activate(branchManager);

    if (store.isFirstTime())
    {
      initSystemPackages();
      initRootResource();
    }
    else
    {
      readPackageUnits();
      loadRootResource();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(lockManager);
    LifecycleUtil.deactivate(queryHandlerProvider);
    LifecycleUtil.deactivate(commitManager);
    LifecycleUtil.deactivate(commitInfoManager);
    LifecycleUtil.deactivate(queryManager);
    LifecycleUtil.deactivate(revisionManager);
    LifecycleUtil.deactivate(sessionManager);
    LifecycleUtil.deactivate(store);
    LifecycleUtil.deactivate(branchManager);
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

  protected void initRootResource()
  {
    CDOBranchPoint head = branchManager.getMainBranch().getHead();
    CDOIDTemp tempID = CDOIDUtil.createTempObject(1);

    InternalCDORevision rootResource = new CDORevisionImpl(EresourcePackage.Literals.CDO_RESOURCE);
    rootResource.setBranchPoint(head);
    rootResource.setContainerID(CDOID.NULL);
    rootResource.setContainingFeatureID(0);
    rootResource.setID(tempID);
    rootResource.setResourceID(tempID);

    InternalSession session = getSessionManager().openSession(null);
    InternalTransaction transaction = session.openTransaction(1, head);
    InternalCommitContext commitContext = new TransactionCommitContext(transaction)
    {
      @Override
      protected long createTimeStamp()
      {
        return store.getCreationTime();
      }

      @Override
      public String getUserID()
      {
        return SYSTEM_USER_ID;
      }

      @Override
      public String getCommitComment()
      {
        return "<initialize root resource>"; //$NON-NLS-1$
      }
    };

    commitContext.setNewObjects(new InternalCDORevision[] { rootResource });
    commitContext.preWrite();
    boolean success = false;

    try
    {
      commitContext.write(new Monitor());
      commitContext.commit(new Monitor());
      success = true;

      rootResourceID = commitContext.getIDMappings().get(tempID);
    }
    finally
    {
      commitContext.postCommit(success);
      session.close();
    }
  }

  protected void loadRootResource()
  {
    IStoreAccessor reader = store.getReader(null);
    StoreThreadLocal.setAccessor(reader);

    try
    {
      CDOBranchPoint head = branchManager.getMainBranch().getHead();
      rootResourceID = reader.readResourceID(CDOID.NULL, null, head);
    }
    finally
    {
      LifecycleUtil.deactivate(reader); // Don't let the null-context accessor go to the pool!
      StoreThreadLocal.release();
    }
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

      if (getBranchManager() == null)
      {
        setBranchManager(createBranchManager());
      }

      if (getRevisionManager() == null)
      {
        setRevisionManager(createRevisionManager());
      }

      if (getQueryManager() == null)
      {
        setQueryManager(createQueryManager());
      }

      if (getCommitInfoManager() == null)
      {
        setCommitInfoManager(createCommitInfoManager());
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

    protected InternalCDOBranchManager createBranchManager()
    {
      return CDOBranchUtil.createBranchManager();
    }

    protected InternalCDORevisionManager createRevisionManager()
    {
      return new CDORevisionManagerImpl();
    }

    protected InternalQueryManager createQueryManager()
    {
      return new QueryManager();
    }

    protected InternalCDOCommitInfoManager createCommitInfoManager()
    {
      return CDOCommitInfoUtil.createCommitInfoManager();
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
