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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager.CommitInfoLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLoader;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalRepository extends IRepository, PackageLoader, BranchLoader, RevisionLoader, CommitInfoLoader
{
  public void setName(String name);

  public void setType(Type type);

  public void setState(State state);

  public InternalStore getStore();

  public void setStore(InternalStore store);

  public void setProperties(Map<String, String> properties);

  public InternalCDOBranchManager getBranchManager();

  public void setBranchManager(InternalCDOBranchManager branchManager);

  public InternalCDOPackageRegistry getPackageRegistry();

  public InternalCDOPackageRegistry getPackageRegistry(boolean considerCommitContext);

  public InternalCDORevisionManager getRevisionManager();

  public void setRevisionManager(InternalCDORevisionManager revisionManager);

  public InternalCDOCommitInfoManager getCommitInfoManager();

  public InternalSessionManager getSessionManager();

  public void setSessionManager(InternalSessionManager sessionManager);

  public InternalLockManager getLockManager();

  public InternalQueryManager getQueryManager();

  public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider);

  public InternalCommitManager getCommitManager();

  public InternalCommitContext createCommitContext(InternalTransaction transaction);

  public void setRootResourceID(CDOID rootResourceID);

  /**
   * Returns a commit time stamp that is guaranteed to be unique in the given branch. If <code>null</code> is passed the
   * time stamp is expected to e used with a new branch, hence will be unique by definition.
   */
  public long createCommitTimeStamp(CDOBranch branch, OMMonitor monitor);

  public IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, int chunkStart,
      int chunkEnd);

  public void notifyReadAccessHandlers(InternalSession session, CDORevision[] revisions,
      List<CDORevision> additionalRevisions);

  public void notifyWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext,
      boolean beforeCommit, OMMonitor monitor);

  public void replicate(CDOReplicationContext context);

  public CDOReplicationInfo replicateRaw(CDODataOutput out, int lastReplicatedBranchID, long lastReplicatedCommitTime)
      throws IOException;

  public CDOChangeSetData getChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint);

  public Set<CDOID> getMergeData(CDORevisionAvailabilityInfo ancestorInfo, CDORevisionAvailabilityInfo targetInfo,
      CDORevisionAvailabilityInfo sourceInfo);
}
