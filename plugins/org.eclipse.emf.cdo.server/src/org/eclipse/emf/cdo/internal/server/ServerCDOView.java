/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.view.AbstractCDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSessionConfiguration;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ServerCDOView extends AbstractCDOView
{
  private InternalCDOSession session;

  private CDORevisionProvider revisionProvider;

  public ServerCDOView(InternalSession session, CDOBranchPoint branchPoint, boolean legacyModeEnabled,
      CDORevisionProvider revisionProvider)
  {
    super(branchPoint, legacyModeEnabled);
    this.session = new ServerCDOSession(session);
    this.revisionProvider = revisionProvider;
    setObjects(new ReferenceValueMap.Soft<CDOID, InternalCDOObject>());
    activate();
  }

  public ServerCDOView(InternalView view, boolean legacyModeEnabled)
  {
    super(CDOBranchUtil.copyBranchPoint(view), legacyModeEnabled);
    session = new ServerCDOSession(view.getSession());
    revisionProvider = view;
    setObjects(new ReferenceValueMap.Soft<CDOID, InternalCDOObject>());
    activate();
  }

  public ServerCDOView(IStoreAccessor.CommitContext commitContext, boolean legacyModeEnabled)
  {
    super(CDOBranchUtil.copyBranchPoint(commitContext.getTransaction()), legacyModeEnabled);
    session = new ServerCDOSession((InternalSession)commitContext.getTransaction().getSession());
    revisionProvider = commitContext;
    setObjects(new ReferenceValueMap.Soft<CDOID, InternalCDOObject>());
    activate();
  }

  public int getViewID()
  {
    return 1;
  }

  public InternalCDOSession getSession()
  {
    return session;
  }

  public long getLastUpdateTime()
  {
    return getTimeStamp();
  }

  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    return (InternalCDORevision)revisionProvider.getRevision(id);
  }

  @Override
  protected void excludeTempIDs(CDOID id)
  {
    // Do nothing
  }

  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException();
  }

  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout)
      throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    throw new UnsupportedOperationException();
  }

  public void unlockObjects()
  {
    throw new UnsupportedOperationException();
  }

  public Options options()
  {
    throw new UnsupportedOperationException();
  }

  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    throw new UnsupportedOperationException();
  }

  public void setViewID(int viewId)
  {
    throw new UnsupportedOperationException();
  }

  public void setSession(InternalCDOSession session)
  {
    throw new UnsupportedOperationException();
  }

  public CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    return CDOFeatureAnalyzer.NOOP;
  }

  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
  {
    throw new UnsupportedOperationException();
  }

  public InternalCDOTransaction toTransaction()
  {
    throw new UnsupportedOperationException();
  }

  public void invalidate(long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions)
  {
    throw new UnsupportedOperationException();
  }

  public void prefetchRevisions(CDOID id, int depth)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers)
  {
    return false;
  }

  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    // Do nothing
  }

  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    // Do nothing
  }

  public void subscribe(EObject eObject, Adapter adapter)
  {
    throw new UnsupportedOperationException();
  }

  public void unsubscribe(EObject eObject, Adapter adapter)
  {
    throw new UnsupportedOperationException();
  }

  public boolean hasSubscription(CDOID id)
  {
    return false;
  }

  /**
   * @author Eike Stepper
   */
  private final class ServerCDOSession implements InternalCDOSession, CDORepositoryInfo
  {
    private InternalSession internalSession;

    private InternalRepository repository;

    public ServerCDOSession(InternalSession internalSession)
    {
      this.internalSession = internalSession;
      repository = internalSession.getManager().getRepository();
    }

    public CDOView[] getElements()
    {
      return new ServerCDOView[] { ServerCDOView.this };
    }

    public CDOView[] getViews()
    {
      return getElements();
    }

    public CDOView getView(int viewID)
    {
      return viewID == getViewID() ? ServerCDOView.this : null;
    }

    public CDOSessionProtocol getSessionProtocol()
    {
      throw new UnsupportedOperationException();
    }

    public InternalCDORevisionManager getRevisionManager()
    {
      return repository.getRevisionManager();
    }

    public InternalCDOPackageRegistry getPackageRegistry()
    {
      if (revisionProvider instanceof IStoreAccessor.CommitContext)
      {
        IStoreAccessor.CommitContext context = (IStoreAccessor.CommitContext)revisionProvider;
        return context.getPackageRegistry();
      }

      return repository.getPackageRegistry(false);
    }

    public InternalCDOCommitInfoManager getCommitInfoManager()
    {
      return repository.getCommitInfoManager();
    }

    public InternalCDOBranchManager getBranchManager()
    {
      return repository.getBranchManager();
    }

    public boolean hasListeners()
    {
      return false;
    }

    public IListener[] getListeners()
    {
      return null;
    }

    public void addListener(IListener listener)
    {
      // Do nothing
    }

    public void removeListener(IListener listener)
    {
      // Do nothing
    }

    public void activate() throws LifecycleException
    {
      throw new UnsupportedOperationException();
    }

    public Exception deactivate()
    {
      return ServerCDOView.this.deactivate();
    }

    public LifecycleState getLifecycleState()
    {
      return LifecycleState.ACTIVE;
    }

    public boolean isActive()
    {
      return ServerCDOView.this.isActive();
    }

    public boolean isClosed()
    {
      return !isActive();
    }

    public void close()
    {
      deactivate();
    }

    public CDORepositoryInfo getRepositoryInfo()
    {
      return this;
    }

    public String getName()
    {
      return repository.getName();
    }

    public String getUUID()
    {
      return repository.getUUID();
    }

    public Type getType()
    {
      return repository.getType();
    }

    public State getState()
    {
      return repository.getState();
    }

    public long getCreationTime()
    {
      return repository.getCreationTime();
    }

    public long getTimeStamp()
    {
      return repository.getTimeStamp();
    }

    public long getTimeStamp(boolean forceRefresh)
    {
      return getTimeStamp();
    }

    public String getStoreType()
    {
      return repository.getStoreType();
    }

    public Set<ObjectType> getObjectIDTypes()
    {
      return repository.getObjectIDTypes();
    }

    public CDOID getRootResourceID()
    {
      return repository.getRootResourceID();
    }

    public boolean isSupportingAudits()
    {
      return repository.isSupportingAudits();
    }

    public boolean isSupportingBranches()
    {
      return repository.isSupportingBranches();
    }

    public boolean isEnsuringReferentialIntegrity()
    {
      return repository.isEnsuringReferentialIntegrity();
    }

    public void handleRepositoryTypeChanged(Type oldType, Type newType)
    {
    }

    public void handleRepositoryStateChanged(State oldState, State newState)
    {
    }

    public EPackage[] loadPackages(CDOPackageUnit packageUnit)
    {
      return null;
    }

    public void releaseAtomicRequestLock(Object key)
    {
      // Do nothing
    }

    public void acquireAtomicRequestLock(Object key)
    {
      // Do nothing
    }

    public Object processPackage(Object value)
    {
      return value;
    }

    public boolean isEmpty()
    {
      return false;
    }

    public boolean waitForUpdate(long updateTime, long timeoutMillis)
    {
      throw new UnsupportedOperationException();
    }

    public void waitForUpdate(long updateTime)
    {
      throw new UnsupportedOperationException();
    }

    public long getLastUpdateTime()
    {
      return getBranchPoint().getTimeStamp();
    }

    public String getUserID()
    {
      return null;
    }

    public int getSessionID()
    {
      return internalSession.getSessionID();
    }

    public long refresh()
    {
      throw new UnsupportedOperationException();
    }

    public Options options()
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView()
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(long timeStamp)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranch branch)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranch branch, long timeStamp)
    {
      throw new UnsupportedOperationException();
    }

    public CDOView openView(CDOBranch branch, long timeStamp, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction()
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(CDOBranch branch)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOTransaction openTransaction(CDOBranch branch, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    public CDOFetchRuleManager getFetchRuleManager()
    {
      return null;
    }

    public ExceptionHandler getExceptionHandler()
    {
      return null;
    }

    public void viewDetached(InternalCDOView view)
    {
      // Do nothing
    }

    public void setUserID(String userID)
    {
      throw new UnsupportedOperationException();
    }

    public void setSessionProtocol(CDOSessionProtocol sessionProtocol)
    {
      throw new UnsupportedOperationException();
    }

    public void setSessionID(int sessionID)
    {
      throw new UnsupportedOperationException();
    }

    public void setRepositoryInfo(CDORepositoryInfo repositoryInfo)
    {
      throw new UnsupportedOperationException();
    }

    public void setRemoteSessionManager(InternalCDORemoteSessionManager remoteSessionManager)
    {
      throw new UnsupportedOperationException();
    }

    public void setLastUpdateTime(long lastUpdateTime)
    {
      throw new UnsupportedOperationException();
    }

    public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
    {
      throw new UnsupportedOperationException();
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler)
    {
      throw new UnsupportedOperationException();
    }

    public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
    {
      throw new UnsupportedOperationException();
    }

    public void processRefreshSessionResult(RefreshSessionResult result, CDOBranch branch,
        List<InternalCDOView> branchViews, Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
    {
      throw new UnsupportedOperationException();
    }

    public void invalidate(CDOCommitInfo commitInfo, InternalCDOTransaction sender)
    {
      throw new UnsupportedOperationException();
    }

    public void handleCommitNotification(CDOCommitInfo commitInfo)
    {
      throw new UnsupportedOperationException();
    }

    public void handleBranchNotification(InternalCDOBranch branch)
    {
      throw new UnsupportedOperationException();
    }

    public InternalCDORemoteSessionManager getRemoteSessionManager()
    {
      throw new UnsupportedOperationException();
    }

    public Object getInvalidationLock()
    {
      throw new UnsupportedOperationException();
    }

    public InternalCDOSessionConfiguration getConfiguration()
    {
      throw new UnsupportedOperationException();
    }
  }
}
