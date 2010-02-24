/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.CDOCloningContext;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.PerfTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOClientProtocol extends SignalProtocol<CDOSession> implements CDOSessionProtocol
{
  private static final PerfTracer REVISION_LOADING = new PerfTracer(OM.PERF_REVISION_LOADING, CDOClientProtocol.class);

  private StringIO packageURICompressor = StringCompressor.BYPASS ? StringIO.DIRECT : new StringCompressor(true);

  public CDOClientProtocol()
  {
    super(CDOProtocolConstants.PROTOCOL_NAME);
  }

  public CDOSession getSession()
  {
    return getInfraStructure();
  }

  public StringIO getPackageURICompressor()
  {
    return packageURICompressor;
  }

  public OpenSessionResult openSession(String repositoryName, boolean passiveUpdateEnabled)
  {
    open();
    return send(new OpenSessionRequest(this, repositoryName, passiveUpdateEnabled));
  }

  public void disablePassiveUpdate()
  {
    send(new DisablePassiveUpdateRequest(this));
  }

  public void setPassiveUpdateMode(PassiveUpdateMode mode)
  {
    send(new SetPassiveUpdateModeRequest(this, mode));
  }

  public RepositoryTimeResult getRepositoryTime()
  {
    return send(new RepositoryTimeRequest(this));
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    return send(new LoadPackagesRequest(this, (InternalCDOPackageUnit)packageUnit));
  }

  public int createBranch(BranchInfo branchInfo)
  {
    return send(new CreateBranchRequest(this, branchInfo));
  }

  public BranchInfo loadBranch(int branchID)
  {
    return send(new LoadBranchRequest(this, branchID));
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    return send(new LoadSubBranchesRequest(this, branchID));
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    send(new LoadCommitInfosRequest(this, branch, startTime, endTime, handler));
  }

  public CDOCommitData loadCommitData(long timeStamp)
  {
    // TODO: implement CDOClientProtocol.loadCommitData(timeStamp, dataType)
    throw new UnsupportedOperationException();
  }

  public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex)
  {
    return send(new LoadChunkRequest(this, revision, feature, accessIndex, fetchIndex, fromIndex, toIndex));
  }

  public List<InternalCDORevision> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint,
      int referenceChunk, int prefetchDepth)
  {
    return send(new LoadRevisionsRequest(this, infos, branchPoint, referenceChunk, prefetchDepth));
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    return send(new LoadRevisionByVersionRequest(this, id, branchVersion, referenceChunk));
  }

  public RefreshSessionResult refresh(long lastUpdateTime,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int initialChunkSize,
      boolean enablePassiveUpdates)
  {
    return send(new RefreshSessionRequest(this, lastUpdateTime, viewedRevisions, initialChunkSize, enablePassiveUpdates));
  }

  public void openView(int viewID, CDOBranchPoint branchPoint, boolean readOnly)
  {
    send(new OpenViewRequest(this, viewID, branchPoint, readOnly));
  }

  public boolean[] changeView(int viewID, CDOBranchPoint branchPoint, List<InternalCDOObject> invalidObjects)
  {
    return send(new ChangeViewRequest(this, viewID, branchPoint, invalidObjects));
  }

  public void closeView(int viewID)
  {
    send(new CloseViewRequest(this, viewID));
  }

  public void changeSubscription(int viewID, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear)
  {
    send(new ChangeSubscriptionRequest(this, viewID, cdoIDs, subscribeMode, clear));
  }

  public void query(int viewID, AbstractQueryIterator<?> queryResult)
  {
    send(new QueryRequest(this, viewID, queryResult));
  }

  public boolean cancelQuery(int queryId)
  {
    try
    {
      return new QueryCancelRequest(this, queryId).send();
    }
    catch (Exception ignore)
    {
      return false;
    }
  }

  public RefreshSessionResult lockObjects(long lastUpdateTime,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int viewID, LockType lockType, long timeout)
      throws InterruptedException
  {
    InterruptedException interruptedException = null;
    RuntimeException runtimeException = null;

    try
    {
      return new LockObjectsRequest(this, lastUpdateTime, viewedRevisions, viewID, lockType, timeout).send();
    }
    catch (RemoteException ex)
    {
      if (ex.getCause() instanceof RuntimeException)
      {
        runtimeException = (RuntimeException)ex.getCause();
      }
      else if (ex.getCause() instanceof InterruptedException)
      {
        interruptedException = (InterruptedException)ex.getCause();
      }
      else
      {
        runtimeException = WrappedException.wrap(ex);
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    if (interruptedException != null)
    {
      throw interruptedException;
    }

    throw runtimeException;
  }

  public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType)
  {
    send(new UnlockObjectsRequest(this, view, objects, lockType));
  }

  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers)
  {
    return send(new ObjectLockedRequest(this, view, object, lockType, byOthers));
  }

  public CommitTransactionResult commitTransaction(InternalCDOCommitContext commitContext, OMMonitor monitor)
  {
    return send(new CommitTransactionRequest(this, commitContext), monitor);
  }

  public CommitTransactionResult commitTransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    return send(new CommitTransactionPhase1Request(this, xaContext), monitor);
  }

  public CommitTransactionResult commitTransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    return send(new CommitTransactionPhase2Request(this, xaContext), monitor);
  }

  public CommitTransactionResult commitTransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    return send(new CommitTransactionPhase3Request(this, xaContext), monitor);
  }

  public CommitTransactionResult commitTransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    return send(new CommitTransactionCancelRequest(this, xaContext), monitor);
  }

  public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe)
  {
    return send(new GetRemoteSessionsRequest(this, subscribe));
  }

  public Set<Integer> sendRemoteMessage(CDORemoteSessionMessage message, List<CDORemoteSession> recipients)
  {
    return send(new RemoteMessageRequest(this, message, recipients));
  }

  public boolean unsubscribeRemoteSessions()
  {
    return send(new UnsubscribeRemoteSessionsRequest(this));
  }

  public void cloneRepository(CDOCloningContext context)
  {
    send(new CloneRepositoryRequest(this, context));
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.SIGNAL_AUTHENTICATION:
      return new AuthenticationIndication(this);

    case CDOProtocolConstants.SIGNAL_BRANCH_NOTIFICATION:
      return new BranchNotificationIndication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION:
      return new CommitNotificationIndication(this);

    case CDOProtocolConstants.SIGNAL_REMOTE_SESSION_NOTIFICATION:
      return new RemoteSessionNotificationIndication(this);

    case CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE_NOTIFICATION:
      return new RemoteMessageNotificationIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }

  private <RESULT> RESULT send(RequestWithConfirmation<RESULT> request)
  {
    try
    {
      return request.send();
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  private CommitTransactionResult send(CommitTransactionRequest request, OMMonitor monitor)
  {
    try
    {
      return request.send(monitor);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  private List<InternalCDORevision> send(LoadRevisionsRequest request)
  {
    try
    {
      REVISION_LOADING.start(request);
      return send((RequestWithConfirmation<List<InternalCDORevision>>)request);
    }
    finally
    {
      REVISION_LOADING.stop(request);
    }
  }
}
