/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Simon McDuff - http://bugs.eclipse.org/233490
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.internal.server.protocol.CommitNotificationRequest;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.SessionCreationException;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class Session extends Container<IView> implements ISession, CDOIDProvider, CDOPackageURICompressor
{
  private SessionManager sessionManager;

  private CDOServerProtocol protocol;

  private int sessionID;

  private boolean legacySupportEnabled;

  private boolean passiveUpdateEnabled = true;

  private ConcurrentMap<Integer, IView> views = new ConcurrentHashMap<Integer, IView>();

  @ExcludeFromDump
  private transient StringCompressor packageURICompressor = new StringCompressor(false);

  @ExcludeFromDump
  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  public Session(SessionManager sessionManager, CDOServerProtocol protocol, int sessionID, boolean legacySupportEnabled)
      throws SessionCreationException
  {
    this.sessionManager = sessionManager;
    this.protocol = protocol;
    this.sessionID = sessionID;
    this.legacySupportEnabled = legacySupportEnabled;
    protocol.addListener(protocolListener);

    try
    {
      activate();
    }
    catch (Exception ex)
    {
      throw new SessionCreationException(ex);
    }
  }

  public SessionManager getSessionManager()
  {
    return sessionManager;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public boolean isLegacySupportEnabled()
  {
    return legacySupportEnabled;
  }

  /**
   * @since 2.0
   */
  public boolean isPassiveUpdateEnabled()
  {
    return passiveUpdateEnabled;
  }

  /**
   * @since 2.0
   */
  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  public View[] getElements()
  {
    return getViews();
  }

  @Override
  public boolean isEmpty()
  {
    return views.isEmpty();
  }

  public View[] getViews()
  {
    return views.values().toArray(new View[views.size()]);
  }

  public IView getView(int viewID)
  {
    return views.get(viewID);
  }

  public IView closeView(int viewID)
  {
    IView view = views.remove(viewID);
    if (view != null)
    {
      fireElementRemovedEvent(view);
    }

    return view;
  }

  public IView openView(int viewID, CDOProtocolView.Type type)
  {
    IView view = createView(viewID, type);
    views.put(viewID, view);
    fireElementAddedEvent(view);
    return view;
  }

  /**
   * For tests only.
   */
  public Transaction openTransaction(int viewID, final long timeStamp)
  {
    Transaction transaction = new Transaction(this, viewID)
    {
      @Override
      protected long createTimeStamp()
      {
        return timeStamp;
      }
    };

    views.put(viewID, transaction);
    fireElementAddedEvent(transaction);
    return transaction;
  }

  private IView createView(int viewID, CDOProtocolView.Type type)
  {
    if (type == CDOProtocolView.Type.TRANSACTION)
    {
      return new Transaction(this, viewID);
    }

    return new View(this, viewID, type);
  }

  /**
   * @since 2.0
   */
  public void handleCommitNotification(long timeStamp, List<CDOIDAndVersion> dirtyIDs, List<CDORevisionDelta> deltas)
  {
    if (!isPassiveUpdateEnabled())
    {
      dirtyIDs = Collections.emptyList();
    }

    // Look if someone needs to know something about modified objects
    List<CDORevisionDelta> newDeltas = new ArrayList<CDORevisionDelta>();
    for (CDORevisionDelta delta : deltas)
    {
      CDOID lookupID = delta.getID();
      for (View view : getViews())
      {
        if (view.hasSubscription(lookupID))
        {
          newDeltas.add(delta);
          break;
        }
      }
    }
    try
    {
      if (!dirtyIDs.isEmpty() || !newDeltas.isEmpty())
      {
        new CommitNotificationRequest(protocol.getChannel(), timeStamp, dirtyIDs, newDeltas).send();
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  public CDOID provideCDOID(Object idObject)
  {
    CDOID id = (CDOID)idObject;
    if (!legacySupportEnabled || id.isNull() || id.isMeta())
    {
      return id;
    }

    CDOIDObject objectID = (CDOIDObject)id;
    if (objectID.getClassRef() == null)
    {
      CDOClassRef classRef = getClassRef(objectID);
      objectID = objectID.asLegacy(classRef);
    }

    return objectID;
  }

  public CDOClassRef getClassRef(CDOID id)
  {
    RevisionManager revisionManager = sessionManager.getRepository().getRevisionManager();
    CDOClass cdoClass = revisionManager.getObjectType(id);
    return cdoClass != null ? cdoClass.createClassRef() : StoreThreadLocal.getStoreReader().readObjectType(id);
  }

  /**
   * TODO I can't see how recursion is controlled/limited
   */
  public void collectContainedRevisions(InternalCDORevision revision, int referenceChunk, Set<CDOID> revisions,
      List<InternalCDORevision> additionalRevisions)
  {
    RevisionManager revisionManager = getSessionManager().getRepository().getRevisionManager();
    CDOClass cdoClass = revision.getCDOClass();
    CDOFeature[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      if (feature.isReference() && !feature.isMany() && feature.isContainment())
      {
        Object value = revision.getValue(feature);
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          if (!id.isNull() && !revisions.contains(id))
          {
            InternalCDORevision containedRevision = revisionManager.getRevision(id, referenceChunk);
            revisions.add(id);
            additionalRevisions.add(containedRevision);

            collectContainedRevisions(containedRevision, referenceChunk, revisions, additionalRevisions);
          }
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  public void writePackageURI(ExtendedDataOutput out, String uri) throws IOException
  {
    packageURICompressor.write(out, uri);
  }

  /**
   * @since 2.0
   */
  public String readPackageURI(ExtendedDataInput in) throws IOException
  {
    return packageURICompressor.read(in);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Session[{0}, {1}]", sessionID, protocol.getChannel());
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    protocol.removeListener(protocolListener);
    sessionManager.sessionClosed(this);
    super.doDeactivate();
  }
}
