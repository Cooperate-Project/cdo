/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLoader;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOSessionProtocol extends CDOProtocol, PackageLoader, RevisionLoader
{
  public void setPassiveUpdate(Map<CDOID, CDOIDAndVersion> idAndVersions, int initialChunkSize,
      boolean passiveUpdateEnabled);

  public RepositoryTimeResult getRepositoryTime();

  /**
   * @param revision
   * @param feature
   * @param accessIndex
   *          Index of the item access at the client (with modifications)
   * @param fetchIndex
   *          Index of the item access at the server (without any modifications)
   * @param fromIndex
   *          Load objects at the client from fromIndex (inclusive)
   * @param toIndex
   *          Load objects at the client to toIndex (inclusive)
   */
  public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex);

  public Collection<CDOTimeStampContext> syncRevisions(Map<CDOID, CDOIDAndVersion> allRevisions, int initialChunkSize);

  /**
   * @since 3.0
   */
  public void openView(int viewId, CDOCommonView.Type type, long timeStamp);

  public void closeView(int viewId);

  public boolean[] setAudit(int viewId, long timeStamp, List<InternalCDOObject> invalidObjects);

  public void changeSubscription(int viewId, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear);

  /**
   * @since 3.0
   */
  public void query(int viewID, AbstractQueryIterator<?> queryResult);

  public boolean cancelQuery(int queryId);

  /**
   * @since 3.0
   */
  public void lockObjects(CDOView view, Map<CDOID, CDOIDAndVersion> objects, long timeout, LockType lockType)
      throws InterruptedException;

  /**
   * @since 3.0
   */
  public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType);

  /**
   * @since 3.0
   */
  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers);

  public CommitTransactionResult commitTransaction(InternalCDOCommitContext commitContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe);

  /**
   * @since 3.0
   */
  public boolean sendCustomData(CDORemoteSession receiver, String type, byte[] data);

  /**
   * @since 3.0
   */
  public boolean unsubscribeRemoteSessions();

  /**
   * @author Eike Stepper
   */
  public final class OpenSessionResult
  {
    private int sessionID;

    private String repositoryUUID;

    private long repositoryCreationTime;

    private RepositoryTimeResult repositoryTimeResult;

    private boolean repositorySupportingAudits;

    private List<InternalCDOPackageUnit> packageUnits = new ArrayList<InternalCDOPackageUnit>();

    /**
     * @since 3.0
     */
    public OpenSessionResult(int sessionID, String repositoryUUID, long repositoryCreationTime,
        boolean repositorySupportingAudits)
    {
      this.sessionID = sessionID;
      this.repositoryUUID = repositoryUUID;
      this.repositoryCreationTime = repositoryCreationTime;
      this.repositorySupportingAudits = repositorySupportingAudits;
    }

    public int getSessionID()
    {
      return sessionID;
    }

    public String getRepositoryUUID()
    {
      return repositoryUUID;
    }

    public long getRepositoryCreationTime()
    {
      return repositoryCreationTime;
    }

    public boolean isRepositorySupportingAudits()
    {
      return repositorySupportingAudits;
    }

    public RepositoryTimeResult getRepositoryTimeResult()
    {
      return repositoryTimeResult;
    }

    public void setRepositoryTimeResult(RepositoryTimeResult repositoryTimeResult)
    {
      this.repositoryTimeResult = repositoryTimeResult;
    }

    public List<InternalCDOPackageUnit> getPackageUnits()
    {
      return packageUnits;
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class RepositoryTimeResult
  {
    private long requested;

    private long indicated;

    private long responded;

    private long confirmed;

    public RepositoryTimeResult()
    {
    }

    public long getRequested()
    {
      return requested;
    }

    public void setRequested(long requested)
    {
      this.requested = requested;
    }

    public long getIndicated()
    {
      return indicated;
    }

    public void setIndicated(long indicated)
    {
      this.indicated = indicated;
    }

    public long getResponded()
    {
      return responded;
    }

    public void setResponded(long responded)
    {
      this.responded = responded;
    }

    public long getConfirmed()
    {
      return confirmed;
    }

    public void setConfirmed(long confirmed)
    {
      this.confirmed = confirmed;
    }

    public long getAproximateRepositoryOffset()
    {
      long latency = confirmed - requested >> 1;
      long shift = confirmed - responded;
      return shift - latency;
    }

    public long getAproximateRepositoryTime()
    {
      long offset = getAproximateRepositoryOffset();
      return System.currentTimeMillis() + offset;
    }

    @Override
    public String toString()
    {
      return MessageFormat
          .format(
              "RepositoryTime[requested={0,date} {0,time}, indicated={1,date} {1,time}, responded={2,date} {2,time}, confirmed={3,date} {3,time}]", //$NON-NLS-1$
              requested, indicated, responded, confirmed);
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class CommitTransactionResult
  {
    private String rollbackMessage;

    private long timeStamp;

    // private List<CDOIDMetaRange> metaIDRanges = new ArrayList<CDOIDMetaRange>();

    private Map<CDOIDTemp, CDOID> idMappings = new HashMap<CDOIDTemp, CDOID>();

    private CDOReferenceAdjuster referenceAdjuster;

    private InternalCDOCommitContext commitContext;

    public CommitTransactionResult(InternalCDOCommitContext commitContext, String rollbackMessage)
    {
      this.rollbackMessage = rollbackMessage;
      this.commitContext = commitContext;
    }

    public CommitTransactionResult(InternalCDOCommitContext commitContext, long timeStamp)
    {
      this.timeStamp = timeStamp;
      this.commitContext = commitContext;
    }

    public CDOReferenceAdjuster getReferenceAdjuster()
    {
      if (referenceAdjuster == null)
      {
        referenceAdjuster = createReferenceAdjuster();
      }

      return referenceAdjuster;
    }

    public void setReferenceAdjuster(CDOReferenceAdjuster referenceAdjuster)
    {
      this.referenceAdjuster = referenceAdjuster;
    }

    public InternalCDOCommitContext getCommitContext()
    {
      return commitContext;
    }

    public String getRollbackMessage()
    {
      return rollbackMessage;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public Map<CDOIDTemp, CDOID> getIDMappings()
    {
      return idMappings;
    }

    public void addIDMapping(CDOIDTemp oldID, CDOID newID)
    {
      idMappings.put(oldID, newID);
    }

    // /**
    // * @since 3.0
    // */
    // public List<CDOIDMetaRange> getMetaIDRanges()
    // {
    // return metaIDRanges;
    // }
    //
    // /**
    // * @since 3.0
    // */
    // public void addMetaIDRange(CDOIDMetaRange metaIDRange)
    // {
    // metaIDRanges.add(metaIDRange);
    // }

    protected PostCommitReferenceAdjuster createReferenceAdjuster()
    {
      return new PostCommitReferenceAdjuster(commitContext.getTransaction(), new CDOIDMapper(idMappings));
    }

    /**
     * @author Simon McDuff
     */
    protected static class PostCommitReferenceAdjuster implements CDOReferenceAdjuster
    {
      private CDOIDProvider idProvider;

      private CDOIDMapper idMapper;

      public PostCommitReferenceAdjuster(CDOIDProvider idProvider, CDOIDMapper idMapper)
      {
        this.idProvider = idProvider;
        this.idMapper = idMapper;
      }

      public CDOIDProvider getIdProvider()
      {
        return idProvider;
      }

      public CDOIDMapper getIdMapper()
      {
        return idMapper;
      }

      public Object adjustReference(Object id)
      {
        if (id == null || id == CDOID.NULL)
        {
          return id;
        }

        if (idProvider != null && (id instanceof CDOID || id instanceof InternalEObject))
        {
          id = idProvider.provideCDOID(id);
        }

        return idMapper.adjustReference(id);
      }
    }
  }
}
