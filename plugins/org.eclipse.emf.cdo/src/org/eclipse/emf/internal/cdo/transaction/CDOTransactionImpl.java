/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Gonzague Reydet - bug 298334
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver2;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.LegacyModeNotEnabledException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.internal.cdo.CDOObjectMerger;
import org.eclipse.emf.internal.cdo.CDOObjectWrapper;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.revision.CDOListWithElementProxiesImpl;
import org.eclipse.emf.internal.cdo.util.CompletePackageClosure;
import org.eclipse.emf.internal.cdo.util.IPackageClosure;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.FastList;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.OptionsEvent;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.CDOTransactionStrategy;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOUserSavepoint;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl extends CDOViewImpl implements InternalCDOTransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION, CDOTransactionImpl.class);

  private FastList<CDOTransactionHandler> transactionHandlers = new FastList<CDOTransactionHandler>()
  {
    @Override
    protected CDOTransactionHandler[] newArray(int length)
    {
      return new CDOTransactionHandler[length];
    }
  };

  private InternalCDOSavepoint lastSavepoint = createSavepoint(null);

  private InternalCDOSavepoint firstSavepoint = lastSavepoint;

  private boolean dirty;

  private int conflict;

  private CDOTransactionStrategy transactionStrategy;

  private AtomicInteger lastTemporaryID = new AtomicInteger();

  private long lastCommitTime = UNSPECIFIED_DATE;

  private String commitComment;

  // Bug 283985 (Re-attachment)
  private Map<InternalCDOObject, CDORevisionKey> formerRevisionKeys = new HashMap<InternalCDOObject, CDORevisionKey>();

  // Bug 283985 (Re-attachment)
  private final ThreadLocal<Boolean> providingCDOID = new InheritableThreadLocal<Boolean>()
  {
    @Override
    protected Boolean initialValue()
    {
      return false;
    }
  };

  public CDOTransactionImpl(CDOBranch branch)
  {
    super(branch, UNSPECIFIED_DATE);
  }

  /**
   * @since 2.0
   */
  @Override
  public OptionsImpl options()
  {
    return (OptionsImpl)super.options();
  }

  /**
   * @since 2.0
   */
  @Override
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  @Override
  public boolean isReadOnly()
  {
    return false;
  }

  @Override
  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    if (branchPoint.getTimeStamp() != UNSPECIFIED_DATE)
    {
      throw new IllegalArgumentException("Changing the target time is not supported by transactions");
    }

    if (isDirty() && !getBranch().equals(branchPoint.getBranch()))
    {
      throw new IllegalStateException("Changing the target branch is impossible while transaction is dirty");
    }

    return super.setBranchPoint(branchPoint);
  }

  public void addTransactionHandler(CDOTransactionHandler handler)
  {
    transactionHandlers.add(handler);
  }

  public void removeTransactionHandler(CDOTransactionHandler handler)
  {
    transactionHandlers.remove(handler);
  }

  public CDOTransactionHandler[] getTransactionHandlers()
  {
    return transactionHandlers.get();
  }

  @Override
  public boolean isDirty()
  {
    if (isClosed())
    {
      return false;
    }

    return dirty;
  }

  @Override
  public boolean hasConflict()
  {
    checkActive();
    return conflict != 0;
  }

  public void setConflict(InternalCDOObject object)
  {
    ConflictEvent event = new ConflictEvent(object, conflict == 0);
    ++conflict;
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(event, listeners);
    }
  }

  /**
   * @since 2.0
   */
  public Set<CDOObject> getConflicts()
  {
    Set<CDOObject> conflicts = new HashSet<CDOObject>();
    for (CDOObject object : getDirtyObjects().values())
    {
      if (object.cdoConflict())
      {
        conflicts.add(object);
      }
    }

    for (CDOObject object : getDetachedObjects().values())
    {
      if (object.cdoConflict())
      {
        conflicts.add(object);
      }
    }

    return conflicts;
  }

  public CDOChangeSetData getChangeSetData()
  {
    checkActive();
    return lastSavepoint.getAllChangeSetData();
  }

  public CDOChangeSetData merge(CDOBranchPoint source, CDOMerger merger)
  {
    InternalCDOSession session = getSession();
    synchronized (session.getInvalidationLock())
    {
      if (isDirty())
      {
        throw new IllegalStateException("Merging into dirty transactions not yet supported");
      }

      long now = getLastUpdateTime();
      CDOBranchPoint target = getBranch().getPoint(now);

      CDOBranch sourceBranch = source.getBranch();
      if (source.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        source = sourceBranch.getPoint(now);
      }

      if (CDOBranchUtil.isContainedBy(source, target))
      {
        throw new IllegalArgumentException("Source is already contained in " + target);
      }

      CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(target, source);

      CDORevisionAvailabilityInfo ancestorInfo = createRevisionAvailabilityInfo(ancestor);
      CDORevisionAvailabilityInfo targetInfo = createRevisionAvailabilityInfo(target);
      CDORevisionAvailabilityInfo sourceInfo = createRevisionAvailabilityInfo(source);

      CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
      Set<CDOID> ids = sessionProtocol.loadMergeData(ancestorInfo, targetInfo, sourceInfo);

      cacheRevisions(ancestorInfo);
      cacheRevisions(targetInfo);
      cacheRevisions(sourceInfo);

      CDOChangeSet targetChanges = createChangeSet(ids, ancestorInfo, targetInfo);
      CDOChangeSet sourceChanges = createChangeSet(ids, ancestorInfo, sourceInfo);

      CDOChangeSetData result = merger.merge(targetChanges, sourceChanges);
      if (result == null)
      {
        return null;
      }

      return applyChangeSetData(result, ancestorInfo, targetInfo, sourceInfo);
    }
  }

  private CDORevisionAvailabilityInfo createRevisionAvailabilityInfo(CDOBranchPoint branchPoint)
  {
    CDORevisionAvailabilityInfo info = new CDORevisionAvailabilityInfo(branchPoint);

    InternalCDORevisionManager revisionManager = getSession().getRevisionManager();
    InternalCDORevisionCache cache = revisionManager.getCache();

    List<CDORevision> revisions = cache.getRevisions(branchPoint);
    for (CDORevision revision : revisions)
    {
      if (revision instanceof PointerCDORevision)
      {
        PointerCDORevision pointer = (PointerCDORevision)revision;
        CDOBranchVersion target = pointer.getTarget();
        if (target != null)
        {
          revision = cache.getRevisionByVersion(pointer.getID(), target);
        }
      }
      else if (revision instanceof DetachedCDORevision)
      {
        revision = null;
      }

      if (revision != null)
      {
        info.addRevision(revision);
      }
    }

    return info;
  }

  private void cacheRevisions(CDORevisionAvailabilityInfo info)
  {
    InternalCDORevisionManager revisionManager = getSession().getRevisionManager();
    CDOBranch branch = info.getBranchPoint().getBranch();
    for (CDORevisionKey key : info.getAvailableRevisions().values())
    {
      CDORevision revision = (CDORevision)key;
      revisionManager.addRevision(revision);

      if (!ObjectUtil.equals(revision.getBranch(), branch))
      {
        CDOID id = revision.getID();
        CDORevision firstRevision = revisionManager.getCache().getRevisionByVersion(id,
            branch.getVersion(CDOBranchVersion.FIRST_VERSION));
        if (firstRevision != null)
        {
          long revised = firstRevision.getTimeStamp() - 1L;
          CDOBranchVersion target = CDOBranchUtil.copyBranchVersion(revision);
          PointerCDORevision pointer = new PointerCDORevision(revision.getEClass(), id, branch, revised, target);
          revisionManager.addRevision(pointer);
        }
      }
    }
  }

  private CDOChangeSet createChangeSet(Set<CDOID> ids, CDORevisionAvailabilityInfo startInfo,
      CDORevisionAvailabilityInfo endInfo)
  {
    CDOChangeSetData data = CDORevisionDeltaUtil.createChangeSetData(ids, startInfo, endInfo);
    return new CDOChangeSetImpl(startInfo.getBranchPoint(), endInfo.getBranchPoint(), data);
  }

  public CDOChangeSetData applyChangeSetData(CDOChangeSetData changeSetData, CDORevisionAvailabilityInfo ancestorInfo,
      CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo)
  {
    CDOChangeSetData result = new CDOChangeSetDataImpl();
    if (sourceInfo.getBranchPoint().getBranch().isLocal())
    {
      mapLocalIDs(changeSetData);
    }

    for (CDOIDAndVersion key : changeSetData.getNewObjects())
    {
      InternalCDORevision revision = (InternalCDORevision)key;
      CDOID id = revision.getID();
      if (getObjectIfExists(id) == null)
      {
        InternalCDOObject object = newInstance(revision.getEClass());
        object.cdoInternalSetView(this);
        object.cdoInternalSetRevision(revision);
        object.cdoInternalSetID(id);
        object.cdoInternalSetState(CDOState.NEW);
        object.cdoInternalPostLoad();

        registerObject(object);
        registerNew(object);
        result.getNewObjects().add(revision);
        dirty = true;
      }
    }

    Map<CDOID, CDOObject> dirtyObjects = lastSavepoint.getDirtyObjects();
    ConcurrentMap<CDOID, CDORevisionDelta> revisionDeltas = lastSavepoint.getRevisionDeltas();
    Map<CDOID, InternalCDORevision> oldRevisions = new HashMap<CDOID, InternalCDORevision>();

    for (CDORevisionKey key : changeSetData.getChangedObjects())
    {
      InternalCDORevisionDelta ancestorGoalDelta = (InternalCDORevisionDelta)key;
      CDOID id = ancestorGoalDelta.getID();
      InternalCDORevision ancestorRevision = (InternalCDORevision)ancestorInfo.getRevision(id);

      InternalCDOObject object = getObject(id);
      boolean revisionChanged = false;

      InternalCDORevision targetRevision = object.cdoRevision();
      if (targetRevision == null)
      {
        targetRevision = (InternalCDORevision)targetInfo.getRevision(id);
        object.cdoInternalSetRevision(targetRevision);
        revisionChanged = true;
      }

      oldRevisions.put(id, targetRevision);

      InternalCDORevision goalRevision = ancestorRevision.copy();
      goalRevision.setBranchPoint(this);
      goalRevision.setVersion(targetRevision.getVersion());
      goalRevision.setRevised(CDOBranchPoint.UNSPECIFIED_DATE);
      ancestorGoalDelta.apply(goalRevision);

      CDORevisionDelta targetGoalDelta = goalRevision.compare(targetRevision);
      if (!targetGoalDelta.isEmpty())
      {
        revisionDeltas.put(id, targetGoalDelta);
        result.getChangedObjects().add(targetGoalDelta);

        object.cdoInternalSetState(CDOState.DIRTY);
        object.cdoInternalSetRevision(goalRevision);
        revisionChanged = true;

        dirtyObjects.put(id, object);
        dirty = true;
      }

      if (revisionChanged)
      {
        object.cdoInternalPostLoad();
      }
    }

    Set<CDOObject> detachedObjects = new HashSet<CDOObject>();
    for (CDOIDAndVersion key : changeSetData.getDetachedObjects())
    {
      CDOID id = key.getID();
      InternalCDOObject object = getObjectIfExists(id);
      if (object != null)
      {
        result.getDetachedObjects().add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
        CDOStateMachine.INSTANCE.detach(object);
        detachedObjects.add(object);
        dirty = true;
      }
    }

    List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>(revisionDeltas.values());
    if (!deltas.isEmpty() || !detachedObjects.isEmpty())
    {
      sendDeltaNotifications(deltas, detachedObjects, oldRevisions);
    }

    return result;
  }

  private void mapLocalIDs(CDOChangeSetData changeSetData)
  {
    // Collect needed ID mappings
    Map<CDOID, CDOID> idMappings = new HashMap<CDOID, CDOID>();
    for (CDOIDAndVersion key : changeSetData.getNewObjects())
    {
      InternalCDORevision revision = (InternalCDORevision)key;
      if (revision.getBranch().isLocal())
      {
        CDOID oldID = revision.getID();
        CDOIDTemp newID = getNextTemporaryID();
        idMappings.put(oldID, newID);

        revision.setID(newID);

      }
    }

    if (!idMappings.isEmpty())
    {
      // Apply collected ID mappings
      CDOIDMapper idMapper = new CDOIDMapper(idMappings);
      idMapper.setAllowUnmappedTempIDs(true);

      for (CDOIDAndVersion key : changeSetData.getNewObjects())
      {
        InternalCDORevision revision = (InternalCDORevision)key;
        revision.adjustReferences(idMapper);
      }

      for (CDORevisionKey key : changeSetData.getChangedObjects())
      {
        InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta)key;
        revisionDelta.adjustReferences(idMapper);
      }
    }
  }

  private InternalCDOObject getObjectIfExists(CDOID id)
  {
    try
    {
      return getObject(id);
    }
    catch (ObjectNotFoundException ex)
    {
      return null;
    }
  }

  @Override
  protected void handleConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts,
      List<CDORevisionDelta> deltas)
  {
    handleConflicts(conflicts, options().getConflictResolvers(), deltas);
  }

  private void handleConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts,
      CDOConflictResolver[] resolvers, List<CDORevisionDelta> deltas)
  {
    if (resolvers.length == 0)
    {
      return;
    }

    // Remember original state to be able to restore it after an exception
    List<CDOState> states = new ArrayList<CDOState>(conflicts.size());
    List<CDORevision> revisions = new ArrayList<CDORevision>(conflicts.size());
    for (CDOObject conflict : conflicts.keySet())
    {
      states.add(conflict.cdoState());
      revisions.add(conflict.cdoRevision());
    }

    int resolved = 0;

    try
    {
      Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> remaining = new HashMap<CDOObject, Pair<CDORevision, CDORevisionDelta>>(
          conflicts);
      for (CDOConflictResolver resolver : resolvers)
      {
        if (resolver instanceof CDOConflictResolver2)
        {
          ((CDOConflictResolver2)resolver).resolveConflicts(Collections.unmodifiableMap(remaining), deltas);
        }
        else
        {
          resolver.resolveConflicts(Collections.unmodifiableSet(remaining.keySet()));
        }

        for (Iterator<CDOObject> it = remaining.keySet().iterator(); it.hasNext();)
        {
          CDOObject object = it.next();
          if (!object.cdoConflict())
          {
            ++resolved;
            it.remove();
          }
        }
      }
    }
    catch (Exception ex)
    {
      // Restore original state
      Iterator<CDOState> state = states.iterator();
      Iterator<CDORevision> revision = revisions.iterator();
      for (CDOObject object : conflicts.keySet())
      {
        ((InternalCDOObject)object).cdoInternalSetState(state.next());
        ((InternalCDOObject)object).cdoInternalSetRevision(revision.next());
      }

      throw WrappedException.wrap(ex);
    }

    conflict -= resolved;
  }

  public CDOIDTemp getNextTemporaryID()
  {
    return CDOIDUtil.createTempObject(lastTemporaryID.incrementAndGet());
  }

  public CDOResource createResource(String path)
  {
    checkActive();
    URI uri = CDOURIUtil.createResourceURI(this, path);
    return (CDOResource)getResourceSet().createResource(uri);
  }

  public CDOResource getOrCreateResource(String path)
  {
    checkActive();

    try
    {
      CDOID id = getResourceNodeID(path);
      if (!CDOIDUtil.isNull(id))
      {
        return (CDOResource)getObject(id);
      }
    }
    catch (Exception ignore)
    {
      // Just create the missing resource
    }

    return createResource(path);
  }

  /**
   * @since 2.0
   */
  @Override
  public void attachResource(CDOResourceImpl resource)
  {
    if (resource.isExisting())
    {
      super.attachResource(resource);
    }
    else
    {
      // ResourceSet.createResource(uri) was called!!
      attachNewResource(resource);
    }
  }

  private void attachNewResource(CDOResourceImpl resource)
  {
    URI uri = resource.getURI();
    List<String> names = CDOURIUtil.analyzePath(uri);
    String resourceName = names.isEmpty() ? null : names.remove(names.size() - 1);

    CDOResourceFolder folder = getOrCreateResourceFolder(names);
    attachNewResourceNode(folder, resourceName, resource);
  }

  /**
   * @return never <code>null</code>;
   * @since 2.0
   */
  public CDOResourceFolder getOrCreateResourceFolder(List<String> names)
  {
    CDOResourceFolder folder = null;
    for (String name : names)
    {
      CDOResourceNode node;

      try
      {
        CDOID folderID = folder == null ? null : folder.cdoID();
        node = getResourceNode(folderID, name);
      }
      catch (CDOException ex)
      {
        node = EresourceFactory.eINSTANCE.createCDOResourceFolder();
        attachNewResourceNode(folder, name, node);
      }

      if (node instanceof CDOResourceFolder)
      {
        folder = (CDOResourceFolder)node;
      }
      else
      {
        throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.0"), node)); //$NON-NLS-1$
      }
    }

    return folder;
  }

  private void attachNewResourceNode(CDOResourceFolder folder, String name, CDOResourceNode newNode)
  {
    CDOResourceNodeImpl node = (CDOResourceNodeImpl)newNode;
    node.basicSetName(name, false);
    if (folder == null)
    {
      if (node.isRoot())
      {
        CDOStateMachine.INSTANCE.attach(node, this);
      }
      else
      {
        getRootResource().getContents().add(node);
      }
    }
    else
    {
      node.basicSetFolder(folder, false);
    }
  }

  /**
   * @since 2.0
   */
  public void detach(CDOResourceImpl cdoResource)
  {
    CDOStateMachine.INSTANCE.detach(cdoResource);
  }

  /**
   * @since 2.0
   */
  public InternalCDOSavepoint getLastSavepoint()
  {
    checkActive();
    return lastSavepoint;
  }

  /**
   * @since 2.0
   */
  public CDOTransactionStrategy getTransactionStrategy()
  {
    if (transactionStrategy == null)
    {
      transactionStrategy = CDOTransactionStrategy.DEFAULT;
      transactionStrategy.setTarget(this);
    }

    return transactionStrategy;
  }

  /**
   * @since 2.0
   */
  public void setTransactionStrategy(CDOTransactionStrategy transactionStrategy)
  {
    if (this.transactionStrategy != null)
    {
      this.transactionStrategy.unsetTarget(this);
    }

    this.transactionStrategy = transactionStrategy;
    if (this.transactionStrategy != null)
    {
      this.transactionStrategy.setTarget(this);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected CDOID getRootOrTopLevelResourceNodeID(String name)
  {
    if (dirty)
    {
      CDOResourceNode node = getRootResourceNode(name, getDirtyObjects().values());
      if (node != null)
      {
        return node.cdoID();
      }

      node = getRootResourceNode(name, getNewObjects().values());
      if (node != null)
      {
        return node.cdoID();
      }
    }

    CDOID id = super.getRootOrTopLevelResourceNodeID(name);
    if (getLastSavepoint().getAllDetachedObjects().containsKey(id) || getDirtyObjects().containsKey(id))
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.1"), name)); //$NON-NLS-1$
    }

    return id;
  }

  private CDOResourceNode getRootResourceNode(String name, Collection<? extends CDOObject> objects)
  {
    for (CDOObject object : objects)
    {
      if (object instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)object;
        if (node.getFolder() == null && ObjectUtil.equals(name, node.getName()))
        {
          return node;
        }
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    if (id.isTemporary() && isDetached(id))
    {
      throw new ObjectNotFoundException(id, this);
    }

    return super.getObject(id, loadOnDemand);
  }

  private boolean isDetached(CDOID id)
  {
    return lastSavepoint.getSharedDetachedObjects().contains(id);
  }

  /**
   * @since 2.0
   */
  public InternalCDOCommitContext createCommitContext()
  {
    return new CDOCommitContextImpl(this, analyzeNewPackages());
  }

  /**
   * @since 2.0
   */
  public CDOCommitInfo commit(IProgressMonitor progressMonitor) throws CommitException
  {
    checkActive();
    synchronized (getSession().getInvalidationLock())
    {
      getLock().lock();

      try
      {
        if (hasConflict())
        {
          throw new CommitException(Messages.getString("CDOTransactionImpl.2")); //$NON-NLS-1$
        }

        if (progressMonitor == null)
        {
          progressMonitor = new NullProgressMonitor();
        }

        CDOTransactionStrategy transactionStrategy = getTransactionStrategy();
        CDOCommitInfo info = transactionStrategy.commit(this, progressMonitor);
        if (info != null)
        {
          lastCommitTime = info.getTimeStamp();
        }

        return info;
      }
      catch (CommitException ex)
      {
        throw ex;
      }
      catch (Throwable t)
      {
        throw new CommitException(t);
      }
      finally
      {
        getLock().unlock();
      }
    }
  }

  public CDOCommitInfo commit() throws CommitException
  {
    return commit(null);
  }

  /**
   * @since 2.0
   */
  public void rollback()
  {
    checkActive();
    rollback(firstSavepoint);
    cleanUp();
  }

  private void removeObject(CDOID id, CDOObject object)
  {
    ((InternalCDOObject)object).cdoInternalSetState(CDOState.TRANSIENT);
    removeObject(id);

    if (object instanceof CDOResource)
    {
      getResourceSet().getResources().remove(object);
    }

    ((InternalCDOObject)object).cdoInternalSetID(null);
    ((InternalCDOObject)object).cdoInternalSetRevision(null);
    ((InternalCDOObject)object).cdoInternalSetView(null);
  }

  private Set<CDOID> rollbackCompletely(CDOUserSavepoint savepoint)
  {
    Set<CDOID> idsOfNewObjectWithDeltas = new HashSet<CDOID>();

    // Start from the last savepoint and come back up to the active
    for (InternalCDOSavepoint itrSavepoint = lastSavepoint; itrSavepoint != null; itrSavepoint = itrSavepoint
        .getPreviousSavepoint())
    {
      // Rollback new objects created after the save point
      Map<CDOID, CDOObject> newObjects = itrSavepoint.getNewObjects();
      for (CDOID id : newObjects.keySet())
      {
        CDOObject object = newObjects.get(id);
        removeObject(id, object);
      }

      Set<CDOID> detachedIDs = itrSavepoint.getDetachedObjects().keySet();
      for (CDOObject reattachedObject : itrSavepoint.getReattachedObjects().values())
      {
        CDOID cdoID = reattachedObject.cdoID();
        if (!detachedIDs.contains(cdoID))
        {
          removeObject(cdoID, reattachedObject);
        }
      }

      Map<CDOID, CDORevisionDelta> revisionDeltas = itrSavepoint.getRevisionDeltas();
      if (!revisionDeltas.isEmpty())
      {
        for (CDORevisionDelta dirtyObject : revisionDeltas.values())
        {
          if (dirtyObject.getID().isTemporary())
          {
            idsOfNewObjectWithDeltas.add(dirtyObject.getID());
          }
        }
      }

      // Rollback all persisted objects
      Map<CDOID, CDOObject> detachedObjectsMap = itrSavepoint.getDetachedObjects();
      if (!detachedObjectsMap.isEmpty())
      {
        for (Entry<CDOID, CDOObject> entryDirty : detachedObjectsMap.entrySet())
        {
          if (entryDirty.getKey().isTemporary())
          {
            idsOfNewObjectWithDeltas.add(entryDirty.getKey());
          }
          else
          {
            InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirty.getValue();
            cleanObject(internalDirtyObject, getRevision(entryDirty.getKey(), true));
          }
        }
      }

      for (Entry<CDOID, CDOObject> entryDirtyObject : itrSavepoint.getDirtyObjects().entrySet())
      {
        // Rollback all persisted objects
        if (!entryDirtyObject.getKey().isTemporary())
        {
          InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirtyObject.getValue();

          // Bug 283985 (Re-attachment): Skip objects that were reattached, because
          // they were already reset to TRANSIENT earlier in this method
          if (!itrSavepoint.getReattachedObjects().values().contains(internalDirtyObject))
          {
            CDOStateMachine.INSTANCE.rollback(internalDirtyObject);
          }
        }
      }

      if (savepoint == itrSavepoint)
      {
        break;
      }
    }

    return idsOfNewObjectWithDeltas;
  }

  private void loadSavepoint(CDOSavepoint savepoint, Set<CDOID> idsOfNewObjectWithDeltas)
  {
    lastSavepoint.recalculateSharedDetachedObjects();

    Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
    Map<CDOID, CDOObject> newObjMaps = getNewObjects();
    Map<CDOID, CDORevision> newBaseRevision = getBaseNewObjects();
    Map<CDOID, CDOObject> detachedObjects = getDetachedObjects();

    // Reload the objects (NEW) with their base.
    for (CDOID id : idsOfNewObjectWithDeltas)
    {
      if (detachedObjects.containsKey(id))
      {
        continue;
      }

      InternalCDOObject object = (InternalCDOObject)newObjMaps.get(id);
      CDORevision revision = newBaseRevision.get(id);
      if (revision != null)
      {
        object.cdoInternalSetRevision(revision.copy());
        object.cdoInternalSetView(this);
        object.cdoInternalSetID(revision.getID());
        object.cdoInternalSetState(CDOState.NEW);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
        if (super.getObject(object.cdoID(), false) == null)
        {
          registerObject(object);
        }
      }
    }

    // We need to register back new objects that are not removed anymore there.
    for (Entry<CDOID, CDOObject> entryNewObject : newObjMaps.entrySet())
    {
      InternalCDOObject object = (InternalCDOObject)entryNewObject.getValue();

      // Go back to the previous state
      cleanObject(object, object.cdoRevision());
      object.cdoInternalSetState(CDOState.NEW);
    }

    for (Entry<CDOID, CDOObject> entryDirtyObject : dirtyObjects.entrySet())
    {
      if (detachedObjects.containsKey(entryDirtyObject.getKey()))
      {
        continue;
      }

      // Rollback every persisted objects
      InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirtyObject.getValue();
      cleanObject(internalDirtyObject, getRevision(entryDirtyObject.getKey(), true));
    }

    for (InternalCDOSavepoint itrSavepoint = firstSavepoint; itrSavepoint != savepoint; itrSavepoint = itrSavepoint
        .getNextSavepoint())
    {
      CDOObjectMerger merger = new CDOObjectMerger();
      for (CDORevisionDelta delta : itrSavepoint.getRevisionDeltas().values())
      {
        if (delta.getID().isTemporary() && !idsOfNewObjectWithDeltas.contains(delta.getID())
            || detachedObjects.containsKey(delta.getID()))
        {
          continue;
        }

        Map<CDOID, CDOObject> map = delta.getID().isTemporary() ? newObjMaps : dirtyObjects;
        InternalCDOObject object = (InternalCDOObject)map.get(delta.getID());

        // Change state of the objects
        merger.merge(object, delta);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
      }
    }

    dirty = savepoint.wasDirty();
  }

  /**
   * @since 2.0
   */
  public void detachObject(InternalCDOObject object)
  {
    CDOTransactionHandler[] handlers = getTransactionHandlers();
    if (handlers != null)
    {
      for (int i = 0; i < handlers.length; i++)
      {
        CDOTransactionHandler handler = handlers[i];
        handler.detachingObject(this, object);
      }
    }

    // deregister object
    CDOID id = object.cdoID();
    if (object.cdoState() == CDOState.NEW)
    {
      Map<CDOID, CDOObject> map = getLastSavepoint().getNewObjects();

      // Determine if we added object
      if (map.containsKey(id))
      {
        map.remove(id);
      }
      else
      {
        getLastSavepoint().getDetachedObjects().put(id, object);
      }

      // deregister object
      deregisterObject(object);
    }
    else
    {
      getLastSavepoint().getDetachedObjects().put(id, object);
      if (!formerRevisionKeys.containsKey(object))
      {
        CDORevisionKey revKey = CDORevisionUtil.createRevisionKey(object.cdoRevision());
        formerRevisionKeys.put(object, revKey);
      }

      // Object may have been reattached previously, in which case it must
      // here be removed from the collection of reattached objects
      lastSavepoint.getReattachedObjects().remove(id);
    }

    if (!dirty)
    {
      dirty = true;
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new StartedEvent(), listeners);
      }
    }
  }

  /**
   * @since 2.0
   */
  public void rollback(CDOUserSavepoint savepoint)
  {
    checkActive();
    getTransactionStrategy().rollback(this, (InternalCDOUserSavepoint)savepoint);
  }

  /**
   * @since 2.0
   */
  public void handleRollback(InternalCDOSavepoint savepoint)
  {
    if (savepoint == null)
    {
      throw new IllegalArgumentException(Messages.getString("CDOTransactionImpl.3")); //$NON-NLS-1$
    }

    if (savepoint.getTransaction() != this)
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOTransactionImpl.4"), savepoint)); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("handleRollback()"); //$NON-NLS-1$
    }

    try
    {
      if (!savepoint.isValid())
      {
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOTransactionImpl.6"), savepoint)); //$NON-NLS-1$
      }

      // Use the state lock since rollback mechanism is playing with
      // EObject and CDORevisions. We do not want to
      // receives notifications during that process! neither the user
      // should callload any objects.
      ReentrantLock viewLock = getStateLock();
      viewLock.lock();

      try
      {
        // Rollback objects
        Set<CDOID> idsOfNewObjectWithDeltas = rollbackCompletely(savepoint);

        lastSavepoint = savepoint;
        // Make savepoint active. Erase savepoint that could have be
        // after
        lastSavepoint.setNextSavepoint(null);
        lastSavepoint.clear();

        // Load from first savepoint up to current savepoint
        loadSavepoint(lastSavepoint, idsOfNewObjectWithDeltas);

        if (lastSavepoint == firstSavepoint && options().isAutoReleaseLocksEnabled())
        {
          // Unlock all objects
          unlockObjects(null, null);
        }
      }
      finally
      {
        viewLock.unlock();
      }

      Map<CDOID, CDOID> idMappings = Collections.emptyMap();
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.ROLLED_BACK, idMappings), listeners);
      }

      CDOTransactionHandler[] handlers = getTransactionHandlers();
      if (handlers != null)
      {
        for (int i = 0; i < handlers.length; i++)
        {
          CDOTransactionHandler handler = handlers[i];

          try
          {
            handler.rolledBackTransaction(this);
          }
          catch (RuntimeException ex)
          {
            OM.LOG.error(ex);
          }
        }
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransactionException(ex);
    }
  }

  /**
   * @since 2.0
   */
  public InternalCDOSavepoint handleSetSavepoint()
  {
    addToBase(lastSavepoint.getNewObjects());
    lastSavepoint = createSavepoint(lastSavepoint);
    return lastSavepoint;
  }

  protected CDOSavepointImpl createSavepoint(InternalCDOSavepoint lastSavepoint)
  {
    return new CDOSavepointImpl(this, lastSavepoint);
  }

  /**
   * @since 2.0
   */
  public InternalCDOSavepoint setSavepoint()
  {
    checkActive();
    return (InternalCDOSavepoint)getTransactionStrategy().setSavepoint(this);
  }

  private void addToBase(Map<CDOID, CDOObject> objects)
  {
    for (CDOObject object : objects.values())
    {
      // Load instance to revision
      ((InternalCDOObject)object).cdoInternalPreCommit();
      lastSavepoint.getBaseNewObjects().put(object.cdoID(), object.cdoRevision().copy());
    }
  }

  @Override
  protected String getClassName()
  {
    return "CDOTransaction"; //$NON-NLS-1$
  }

  public void registerNew(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering new object {0}", object); //$NON-NLS-1$
    }

    registerNewPackage(object.eClass().getEPackage());

    CDOTransactionHandler[] handlers = getTransactionHandlers();
    if (handlers != null)
    {
      for (int i = 0; i < handlers.length; i++)
      {
        CDOTransactionHandler handler = handlers[i];
        handler.attachingObject(this, object);
      }
    }

    registerNew(lastSavepoint.getNewObjects(), object);
  }

  private void registerNewPackage(EPackage ePackage)
  {
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    if (!packageRegistry.containsKey(ePackage.getNsURI()))
    {
      packageRegistry.putEPackage(ePackage);
    }
  }

  /**
   * Receives notification for new and dirty objects
   */
  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    CDOID id = object.cdoID();
    boolean needToSaveFeatureDelta = true;

    if (object.cdoState() == CDOState.NEW)
    {
      // Register Delta for new objects only if objectA doesn't belong to
      // this savepoint
      if (getLastSavepoint().getPreviousSavepoint() == null || featureDelta == null)
      {
        needToSaveFeatureDelta = false;
      }
      else
      {
        Map<CDOID, CDOObject> map = getLastSavepoint().getNewObjects();
        needToSaveFeatureDelta = !map.containsKey(id);
      }
    }

    if (needToSaveFeatureDelta)
    {
      CDORevisionDelta revisionDelta = lastSavepoint.getRevisionDeltas().get(id);
      if (revisionDelta == null)
      {
        revisionDelta = CDORevisionDeltaUtil.create(object.cdoRevision());
        lastSavepoint.getRevisionDeltas().put(id, revisionDelta);
      }

      ((InternalCDORevisionDelta)revisionDelta).addFeatureDelta(featureDelta);
    }

    CDOTransactionHandler[] handlers = getTransactionHandlers();
    if (handlers != null)
    {
      for (int i = 0; i < handlers.length; i++)
      {
        CDOTransactionHandler handler = handlers[i];
        handler.modifyingObject(this, object, featureDelta);
      }
    }
  }

  public void registerRevisionDelta(CDORevisionDelta revisionDelta)
  {
    lastSavepoint.getRevisionDeltas().putIfAbsent(revisionDelta.getID(), revisionDelta);
  }

  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object); //$NON-NLS-1$
    }

    if (featureDelta != null)
    {
      registerFeatureDelta(object, featureDelta);
    }

    registerNew(lastSavepoint.getDirtyObjects(), object);
  }

  /**
   * TODO Simon: Should this method go to CDOSavePointImpl?
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void registerNew(Map map, InternalCDOObject object)
  {
    Object old = map.put(object.cdoID(), object);
    if (old != null)
    {
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOTransactionImpl.10"), object)); //$NON-NLS-1$
    }

    if (!dirty)
    {
      dirty = true;
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new StartedEvent(), listeners);
      }
    }
  }

  private List<CDOPackageUnit> analyzeNewPackages()
  {
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    Set<EPackage> usedPackages = new HashSet<EPackage>();
    Set<EPackage> usedNewPackages = new HashSet<EPackage>();
    for (CDOObject object : getNewObjects().values())
    {
      EPackage ePackage = object.eClass().getEPackage();
      if (usedPackages.add(ePackage))
      {
        EPackage topLevelPackage = EMFUtil.getTopLevelPackage(ePackage);
        if (ePackage == topLevelPackage || usedPackages.add(topLevelPackage))
        {
          if (!CDOModelUtil.isSystemPackage(topLevelPackage))
          {
            CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(topLevelPackage);
            if (packageUnit.getState() == CDOPackageUnit.State.NEW)
            {
              usedNewPackages.add(topLevelPackage);
            }
          }
        }
      }
    }

    if (usedNewPackages.size() > 0)
    {
      Set<CDOPackageUnit> result = new HashSet<CDOPackageUnit>();
      for (EPackage usedNewPackage : analyzeNewPackages(usedNewPackages, packageRegistry))
      {
        CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(usedNewPackage);
        result.add(packageUnit);
      }

      return new ArrayList<CDOPackageUnit>(result);
    }

    return Collections.emptyList();
  }

  private static List<EPackage> analyzeNewPackages(Collection<EPackage> usedTopLevelPackages,
      CDOPackageRegistry packageRegistry)
  {
    // Determine which of the corresdonding EPackages are new
    List<EPackage> newPackages = new ArrayList<EPackage>();

    IPackageClosure closure = new CompletePackageClosure();
    usedTopLevelPackages = closure.calculate(usedTopLevelPackages);

    for (EPackage usedPackage : usedTopLevelPackages)
    {
      if (!CDOModelUtil.isSystemPackage(usedPackage))
      {
        CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(usedPackage);
        if (packageUnit == null)
        {
          throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.11"), usedPackage)); //$NON-NLS-1$
        }

        if (packageUnit.getState() == CDOPackageUnit.State.NEW)
        {
          newPackages.add(usedPackage);
        }
      }
    }

    return newPackages;
  }

  private void cleanUp()
  {
    lastSavepoint = firstSavepoint;
    firstSavepoint.clear();
    firstSavepoint.setNextSavepoint(null);
    firstSavepoint.getSharedDetachedObjects().clear();

    // Bug 283985 (Re-attachment)
    formerRevisionKeys.clear();

    dirty = false;
    conflict = 0;
    lastTemporaryID.set(0);
  }

  public CDOSavepoint[] exportChanges(OutputStream stream) throws IOException
  {
    CDODataOutput out = new CDODataOutputImpl(new ExtendedDataOutputStream(stream))
    {
      @Override
      public CDOIDProvider getIDProvider()
      {
        return CDOTransactionImpl.this;
      }

      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return getSession().getPackageRegistry();
      }
    };

    List<CDOSavepoint> savepoints = new ArrayList<CDOSavepoint>();
    InternalCDOSavepoint savepoint = firstSavepoint;
    while (savepoint != null)
    {
      Collection<CDOObject> newObjects = savepoint.getNewObjects().values();
      Collection<CDORevisionDelta> revisionDeltas = savepoint.getRevisionDeltas().values();
      if (newObjects.isEmpty() && revisionDeltas.isEmpty())
      {
        savepoint = savepoint.getNextSavepoint();
        continue;
      }

      savepoints.add(savepoint);
      out.writeBoolean(true);

      out.writeInt(newObjects.size());
      for (CDOObject newObject : newObjects)
      {
        out.writeCDORevision(newObject.cdoRevision(), CDORevision.UNCHUNKED);
      }

      out.writeInt(revisionDeltas.size());
      for (CDORevisionDelta revisionDelta : revisionDeltas)
      {
        out.writeCDORevisionDelta(revisionDelta);
      }

      savepoint = savepoint.getNextSavepoint();
    }

    out.writeBoolean(false);
    return savepoints.toArray(new CDOSavepoint[savepoints.size()]);
  }

  public CDOSavepoint[] importChanges(InputStream stream, boolean reconstructSavepoints) throws IOException
  {
    List<CDOSavepoint> savepoints = new ArrayList<CDOSavepoint>();
    if (stream.available() > 0)
    {
      CDODataInput in = new CDODataInputImpl(new ExtendedDataInputStream(stream))
      {
        @Override
        protected CDOPackageRegistry getPackageRegistry()
        {
          return getSession().getPackageRegistry();
        }

        @Override
        protected CDOBranchManager getBranchManager()
        {
          return getSession().getBranchManager();
        }

        @Override
        protected CDOCommitInfoManager getCommitInfoManager()
        {
          return getSession().getCommitInfoManager();
        }

        @Override
        protected CDORevisionFactory getRevisionFactory()
        {
          return getSession().getRevisionManager().getFactory();
        }

        @Override
        protected CDOListFactory getListFactory()
        {
          return CDOListWithElementProxiesImpl.FACTORY;
        }
      };

      Map<CDOID, CDOID> idMappings = new HashMap<CDOID, CDOID>();
      while (in.readBoolean())
      {
        if (reconstructSavepoints)
        {
          InternalCDOSavepoint savepoint = setSavepoint();
          savepoints.add(savepoint);
        }

        // Import revisions and deltas
        List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
        importNewRevisions(in, revisions, idMappings);
        List<InternalCDORevisionDelta> revisionDeltas = importRevisionDeltas(in);

        // Re-map temp IDs
        CDOIDMapper idMapper = new CDOIDMapper(idMappings);
        for (InternalCDORevision revision : revisions)
        {
          revision.adjustReferences(idMapper);
        }

        for (InternalCDORevisionDelta delta : revisionDeltas)
        {
          delta.adjustReferences(idMapper);
        }

        // Create new objects
        for (InternalCDORevision revision : revisions)
        {
          InternalCDOObject object = newInstance(revision);
          registerObject(object);
          registerNew(object);
        }

        // Apply deltas
        CDOObjectMerger merger = new CDOObjectMerger();
        for (InternalCDORevisionDelta delta : revisionDeltas)
        {
          InternalCDOObject object = getObject(delta.getID());
          CDORevision revision = object.cdoRevision().copy();
          merger.merge(object, delta);
          registerRevisionDelta(delta);
          registerDirty(object, null);

          if (delta.getVersion() < revision.getVersion())
          {
            setConflict(object);
          }
        }
      }
    }

    return savepoints.toArray(new CDOSavepoint[savepoints.size()]);
  }

  private void importNewRevisions(CDODataInput in, List<InternalCDORevision> revisions, Map<CDOID, CDOID> idMappings)
      throws IOException
  {
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      InternalCDORevision revision = (InternalCDORevision)in.readCDORevision();

      CDOID oldID = revision.getID();
      CDOIDTemp newID = getNextTemporaryID();
      idMappings.put(oldID, newID);
      revision.setID(newID);

      revisions.add(revision);
    }
  }

  private List<InternalCDORevisionDelta> importRevisionDeltas(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    List<InternalCDORevisionDelta> deltas = new ArrayList<InternalCDORevisionDelta>(size);
    for (int i = 0; i < size; i++)
    {
      InternalCDORevisionDelta delta = (InternalCDORevisionDelta)in.readCDORevisionDelta();
      deltas.add(delta);
    }

    return deltas;
  }

  private InternalCDOObject newInstance(InternalCDORevision revision)
  {
    InternalCDOObject object = newInstance(revision.getEClass());
    object.cdoInternalSetID(revision.getID());
    object.cdoInternalSetRevision(revision);
    object.cdoInternalSetState(CDOState.NEW);
    object.cdoInternalSetView(this);
    return object;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    checkActive();
    return lastSavepoint.getAllDirtyObjects();
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    checkActive();
    return lastSavepoint.getAllNewObjects();
  }

  /**
   * @since 2.0
   */
  public Map<CDOID, CDORevision> getBaseNewObjects()
  {
    checkActive();
    return lastSavepoint.getAllBaseNewObjects();
  }

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    checkActive();
    return lastSavepoint.getAllRevisionDeltas();
  }

  /**
   * @since 2.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    checkActive();
    return lastSavepoint.getAllDetachedObjects();
  }

  public Map<InternalCDOObject, CDORevisionKey> getFormerRevisionKeys()
  {
    return formerRevisionKeys;
  }

  @Override
  protected CDOID getID(InternalCDOObject object, boolean onlyPersistedID)
  {
    CDOID id = super.getID(object, onlyPersistedID);

    // The super implementation will return null for a transient (unattached) object;
    // but in a tx, an transient object may previously have been attached, so we consult
    // the formerIDs -- unless this is being called indirectly through provideCDOID.
    // The latter case occurs when deltas or revisions are being written out to a stream; in
    // which case null must be returned (for transients) so that the caller will detect a
    // dangling reference
    if (!providingCDOID.get().booleanValue() && id == null)
    {
      CDORevisionKey revKey = formerRevisionKeys.get(object);
      if (revKey != null)
      {
        id = revKey.getID();
      }
    }

    return id;
  }

  @Override
  public CDOID provideCDOID(Object idOrObject)
  {
    try
    {
      providingCDOID.set(true);
      return super.provideCDOID(idOrObject);
    }
    finally
    {
      providingCDOID.set(false);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
    options().disposeConflictResolvers();
    lastSavepoint = null;
    firstSavepoint = null;
    transactionStrategy = null;
    super.doDeactivate();
  }

  /**
   * Bug 298561: This override removes references to remotely detached objects that are present in any DIRTY or NEW
   * objects.
   * 
   * @since 3.0
   */
  @Override
  protected Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> invalidate(long lastUpdateTime,
      List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects, List<CDORevisionDelta> deltas,
      Map<CDOObject, CDORevisionDelta> revisionDeltas, Set<CDOObject> detachedObjects)
  {
    if (!allDetachedObjects.isEmpty())
    {
      Set<CDOID> referencedOIDs = new HashSet<CDOID>();
      for (CDOIDAndVersion key : allDetachedObjects)
      {
        referencedOIDs.add(key.getID());
      }

      Collection<CDOObject> cachedDirtyObjects = getDirtyObjects().values();
      removeCrossReferences(cachedDirtyObjects, referencedOIDs);

      Collection<CDOObject> cachedNewObjects = getNewObjects().values();
      removeCrossReferences(cachedNewObjects, referencedOIDs);
    }

    return super.invalidate(lastUpdateTime, allChangedObjects, allDetachedObjects, deltas, revisionDeltas,
        detachedObjects);
  }

  private void removeCrossReferences(Collection<CDOObject> referencers, Set<CDOID> referencedOIDs)
  {
    for (CDOObject referencer : referencers)
    {
      EContentsEList.FeatureIterator<EObject> it = (EContentsEList.FeatureIterator<EObject>)referencer
          .eCrossReferences().iterator();
      while (it.hasNext())
      {
        EObject referencedObject = it.next();
        CDOID referencedOID = CDOUtil.getCDOObject(referencedObject).cdoID();

        if (referencedOIDs.contains(referencedOID))
        {
          EReference reference = (EReference)it.feature();

          // In the case of DIRTY, we must investigate further: Is the referencer dirty
          // because a reference to the referencedObject was added? Only in this case
          // should we remove it. If this is not the case (i.e. it is dirty in a different
          // way, we must ignore it.)
          if (referencer.cdoState() == CDOState.DIRTY)
          {
            InternalCDORevision cleanRevision = getSession().getRevisionManager().getRevisionByVersion(
                referencer.cdoID(), referencer.cdoRevision(), CDORevision.UNCHUNKED, true);
            Object value = cleanRevision.get(reference, EStore.NO_INDEX);
            if (value instanceof CDOObject && value == referencedObject || //
                value instanceof CDOID && value.equals(referencedOID) || //
                value instanceof CDOList && ((CDOList)value).contains(referencedOID))
            {
              continue;
            }
          }

          Setting setting = ((InternalEObject)referencer).eSetting(reference);
          EcoreUtil.remove(setting, referencedObject);
        }
      }
    }
  }

  public long getLastCommitTime()
  {
    return lastCommitTime;
  }

  public String getCommitComment()
  {
    return commitComment;
  }

  public void setCommitComment(String comment)
  {
    commitComment = comment;
  }

  /**
   * @author Simon McDuff
   */
  private final class CDOCommitContextImpl implements InternalCDOCommitContext
  {
    private InternalCDOTransaction transaction;

    private CDOCommitData commitData;

    public CDOCommitContextImpl(InternalCDOTransaction transaction, List<CDOPackageUnit> newPackageUnits)
    {
      this.transaction = transaction;

      List<CDOIDAndVersion> revisions = new ArrayList<CDOIDAndVersion>(getNewObjects().size());
      for (CDOObject newObject : getNewObjects().values())
      {
        revisions.add(newObject.cdoRevision());
      }

      List<CDORevisionKey> deltas = new ArrayList<CDORevisionKey>(getRevisionDeltas().size());
      for (CDORevisionDelta delta : getRevisionDeltas().values())
      {
        deltas.add(delta);
      }

      List<CDOIDAndVersion> detached = new ArrayList<CDOIDAndVersion>(getDetachedObjects().size());
      for (CDOID id : getDetachedObjects().keySet())
      {
        // Add "version-less" key.
        // CDOSessionImpl.reviseRevisions() will call reviseLatest() accordingly.
        detached.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
      }

      commitData = new CDOCommitDataImpl(newPackageUnits, revisions, deltas, detached);
    }

    public InternalCDOTransaction getTransaction()
    {
      return transaction;
    }

    public CDOCommitData getCommitData()
    {
      return commitData;
    }

    public Map<CDOID, CDOObject> getDirtyObjects()
    {
      return transaction.getDirtyObjects();
    }

    public Map<CDOID, CDOObject> getNewObjects()
    {
      return transaction.getNewObjects();
    }

    public List<CDOPackageUnit> getNewPackageUnits()
    {
      return commitData.getNewPackageUnits();
    }

    public Map<CDOID, CDOObject> getDetachedObjects()
    {
      return transaction.getDetachedObjects();
    }

    public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
    {
      return transaction.getRevisionDeltas();
    }

    public void preCommit()
    {
      if (isDirty())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("commit()"); //$NON-NLS-1$
        }

        CDOTransactionHandler[] handlers = getTransactionHandlers();
        if (handlers != null)
        {
          for (int i = 0; i < handlers.length; i++)
          {
            CDOTransactionHandler handler = handlers[i];
            handler.committingTransaction(getTransaction(), this);
          }
        }

        try
        {
          preCommit(getNewObjects());
          preCommit(getDirtyObjects());
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new TransactionException(ex);
        }
      }
    }

    public void postCommit(CommitTransactionResult result)
    {
      if (isDirty())
      {
        try
        {
          long timeStamp = result.getTimeStamp();
          CDOBranch branch = result.getBranch();
          boolean branchChanged = !ObjectUtil.equals(branch, getBranch());
          if (branchChanged)
          {
            basicSetBranchPoint(branch.getHead());
          }

          for (CDOPackageUnit newPackageUnit : getNewPackageUnits())
          {
            ((InternalCDOPackageUnit)newPackageUnit).setState(CDOPackageUnit.State.LOADED);
          }

          postCommit(getNewObjects(), result);
          postCommit(getDirtyObjects(), result);

          for (CDORevisionDelta delta : getRevisionDeltas().values())
          {
            ((InternalCDORevisionDelta)delta).adjustReferences(result.getReferenceAdjuster());
          }

          for (CDOID id : getDetachedObjects().keySet())
          {
            removeObject(id);
          }

          CDOCommitInfo commitInfo = makeCommitInfo(timeStamp);

          InternalCDOSession session = getSession();
          session.invalidate(commitInfo, transaction);

          CDOTransactionHandler[] handlers = getTransactionHandlers();
          if (handlers != null)
          {
            for (int i = 0; i < handlers.length; i++)
            {
              CDOTransactionHandler handler = handlers[i];
              handler.committedTransaction(transaction, this);
            }
          }

          getChangeSubscriptionManager().committedTransaction(transaction, this);
          getAdapterManager().committedTransaction(transaction, this);

          cleanUp();
          Map<CDOID, CDOID> idMappings = result.getIDMappings();
          IListener[] listeners = getListeners();
          if (listeners != null)
          {
            if (branchChanged)
            {
              fireViewTargetChangedEvent(listeners);
            }

            fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.COMMITTED, idMappings), listeners);
          }
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new TransactionException(ex);
        }
      }
      else
      {
        // Removes locks even if no one touch the transaction
        if (options().isAutoReleaseLocksEnabled())
        {
          unlockObjects(null, null);
        }
      }
    }

    private CDOCommitInfo makeCommitInfo(long timeStamp)
    {
      InternalCDOSession session = getSession();
      CDOBranch branch = getBranch();
      String userID = session.getUserID();
      String comment = getCommitComment();

      InternalCDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
      return commitInfoManager.createCommitInfo(branch, timeStamp, userID, comment, commitData);
    }

    private void preCommit(Map<CDOID, CDOObject> objects)
    {
      if (!objects.isEmpty())
      {
        boolean noLegacy = !isLegacyModeEnabled();
        for (CDOObject object : objects.values())
        {
          if (noLegacy && object instanceof CDOObjectWrapper)
          {
            throw new LegacyModeNotEnabledException();
          }

          ((InternalCDOObject)object).cdoInternalPreCommit();
        }
      }
    }

    private void postCommit(Map<CDOID, CDOObject> objects, CommitTransactionResult result)
    {
      if (!objects.isEmpty())
      {
        for (CDOObject object : objects.values())
        {
          CDOStateMachine.INSTANCE.commit((InternalCDOObject)object, result);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class StartedEvent extends Event implements CDOTransactionStartedEvent
  {
    private static final long serialVersionUID = 1L;

    private StartedEvent()
    {
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionStartedEvent[source={0}]", getSource()); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FinishedEvent extends Event implements CDOTransactionFinishedEvent
  {
    private static final long serialVersionUID = 1L;

    private Type type;

    private Map<CDOID, CDOID> idMappings;

    private FinishedEvent(Type type, Map<CDOID, CDOID> idMappings)
    {
      this.type = type;
      this.idMappings = idMappings;
    }

    public Type getType()
    {
      return type;
    }

    public Map<CDOID, CDOID> getIDMappings()
    {
      return idMappings;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionFinishedEvent[source={0}, type={1}, idMappings={2}]", getSource(), //$NON-NLS-1$
          getType(), idMappings == null ? 0 : idMappings.size());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ConflictEvent extends Event implements CDOTransactionConflictEvent
  {
    private static final long serialVersionUID = 1L;

    private InternalCDOObject conflictingObject;

    private boolean firstConflict;

    public ConflictEvent(InternalCDOObject conflictingObject, boolean firstConflict)
    {
      this.conflictingObject = conflictingObject;
      this.firstConflict = firstConflict;
    }

    public InternalCDOObject getConflictingObject()
    {
      return conflictingObject;
    }

    public boolean isFirstConflict()
    {
      return firstConflict;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionConflictEvent[source={0}, conflictingObject={1}, firstConflict={2}]", //$NON-NLS-1$
          getSource(), getConflictingObject(), isFirstConflict());
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected final class OptionsImpl extends CDOViewImpl.OptionsImpl implements CDOTransaction.Options
  {
    private List<CDOConflictResolver> conflictResolvers = new ArrayList<CDOConflictResolver>();

    private boolean autoReleaseLocksEnabled = true;

    public OptionsImpl()
    {
    }

    public CDOConflictResolver[] getConflictResolvers()
    {
      synchronized (conflictResolvers)
      {
        return conflictResolvers.toArray(new CDOConflictResolver[conflictResolvers.size()]);
      }
    }

    public void setConflictResolvers(CDOConflictResolver[] resolvers)
    {
      synchronized (conflictResolvers)
      {
        for (CDOConflictResolver resolver : conflictResolvers)
        {
          resolver.setTransaction(null);
        }

        conflictResolvers.clear();
        for (CDOConflictResolver resolver : resolvers)
        {
          validateResolver(resolver);
          conflictResolvers.add(resolver);
        }
      }

      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new ConflictResolversEventImpl(), listeners);
      }
    }

    public void addConflictResolver(CDOConflictResolver resolver)
    {
      boolean changed = false;
      synchronized (conflictResolvers)
      {
        if (!conflictResolvers.contains(resolver))
        {
          validateResolver(resolver);
          conflictResolvers.add(resolver);
          changed = true;
        }
      }

      if (changed)
      {
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new ConflictResolversEventImpl(), listeners);
        }
      }
    }

    public void removeConflictResolver(CDOConflictResolver resolver)
    {
      boolean changed = false;
      synchronized (conflictResolvers)
      {
        changed = conflictResolvers.remove(resolver);
      }

      if (changed)
      {
        resolver.setTransaction(null);
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new ConflictResolversEventImpl(), listeners);
        }
      }
    }

    public void disposeConflictResolvers()
    {
      try
      {
        for (CDOConflictResolver resolver : options().getConflictResolvers())
        {
          try
          {
            resolver.setTransaction(null);
          }
          catch (Exception ignore)
          {
          }
        }
      }
      catch (Exception ignore)
      {
      }
    }

    private void validateResolver(CDOConflictResolver resolver)
    {
      if (resolver.getTransaction() != null)
      {
        throw new IllegalArgumentException(Messages.getString("CDOTransactionImpl.17")); //$NON-NLS-1$
      }

      resolver.setTransaction(CDOTransactionImpl.this);
    }

    public boolean isAutoReleaseLocksEnabled()
    {
      return autoReleaseLocksEnabled;
    }

    public void setAutoReleaseLocksEnabled(boolean on)
    {
      if (autoReleaseLocksEnabled != on)
      {
        autoReleaseLocksEnabled = on;
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new AutoReleaseLocksEventImpl(), listeners);
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ConflictResolversEventImpl extends OptionsEvent implements ConflictResolversEvent
    {
      private static final long serialVersionUID = 1L;

      public ConflictResolversEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class AutoReleaseLocksEventImpl extends OptionsEvent implements AutoReleaseLocksEvent
    {
      private static final long serialVersionUID = 1L;

      public AutoReleaseLocksEventImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }
}
