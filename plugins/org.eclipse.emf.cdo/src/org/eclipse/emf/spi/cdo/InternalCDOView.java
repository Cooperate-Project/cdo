/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOView extends CDOView, CDOIDProvider, ILifecycle
{
  public void setViewID(int viewId);

  public InternalCDOSession getSession();

  public void setSession(InternalCDOSession session);

  public InternalCDOViewSet getViewSet();

  public void setViewSet(InternalCDOViewSet viewSet);

  @Deprecated
  public CDOFeatureAnalyzer getFeatureAnalyzer();

  @Deprecated
  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer);

  /**
   * Returns an unmodifiable map of the objects managed by this view.
   *
   * @since 4.0
   */
  public Map<CDOID, InternalCDOObject> getObjects();

  /**
   * @since 4.0
   */
  public CDOStore getStore();

  public InternalCDOTransaction toTransaction();

  public void attachResource(CDOResourceImpl resource);

  /**
   * @since 3.0
   */
  public void handleObjectStateChanged(InternalCDOObject object, CDOState oldState, CDOState newState);

  /**
   * @deprecated As of 4.2. use {@link #invalidate(CDOBranch, long, List, List, Map, boolean, boolean)}
   */
  @Deprecated
  public void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions, boolean async);

  /**
   * @since 4.2
   */
  public void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions, boolean async,
      boolean clearResourcePathCache);

  /**
   * @since 3.0
   */
  public void setLastUpdateTime(long lastUpdateTime);

  /**
   * @since 3.0
   */
  public void collectViewedRevisions(Map<CDOID, InternalCDORevision> revisions);

  public void dispatchLoadNotification(InternalCDOObject object);

  public void remapObject(CDOID oldID);

  /**
   * @since 4.2
   */
  public void clearResourcePathCacheIfNecessary(CDORevisionDelta delta);

  public CDOID getResourceNodeID(String path);

  public void registerProxyResource(CDOResourceImpl resource);

  public void registerObject(InternalCDOObject object);

  public void deregisterObject(InternalCDOObject object);

  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand);

  /**
   * @since 3.0
   */
  public void prefetchRevisions(CDOID id, int depth);

  public Object convertObjectToID(Object potentialObject);

  public Object convertObjectToID(Object potentialObject, boolean onlyPersistedID);

  public Object convertIDToObject(Object potentialID);

  /**
   * @since 3.0
   */
  public boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers);

  /**
   * @since 4.1
   */
  public boolean isObjectNew(CDOID id);

  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter);

  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter);

  public void subscribe(EObject eObject, Adapter adapter);

  public void unsubscribe(EObject eObject, Adapter adapter);

  public boolean hasSubscription(CDOID id);

  /**
   * @since 4.1
   */
  public void handleLockNotification(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo);

  /**
   * @since 4.1
   */
  public CDOLockState[] getLockStates(Collection<CDOID> ids);

  /**
   * @since 4.2
   */
  public ViewAndState getViewAndState(CDOState state);

  /**
   * Optimizes the storage of {@link CDOObject#cdoView()} and {@link CDOObject#cdoState()}. All objects of a view
   * share a small number of {@link CDOState} literals, so they are moved into a final AbstractCDOView.viewAndStates array.
   * For the {@link CDOState#TRANSIENT TRANSIENT} state, where there is no view associated with a {@link CDOObject}, this class
   * maintains a static {@link #VIEW_AND_STATES} array.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  public static final class ViewAndState
  {
    private static final CDOState[] STATE_VALUES = CDOState.values();

    private static final ViewAndState[] VIEW_AND_STATES = create(null);

    public static final ViewAndState TRANSIENT = VIEW_AND_STATES[CDOState.TRANSIENT.ordinal()];

    public final InternalCDOView view;

    public final CDOState state;

    public ViewAndState(InternalCDOView view, CDOState state)
    {
      this.view = view;
      this.state = state;
    }

    public ViewAndState getViewAndState(CDOState state)
    {
      if (view != null)
      {
        return view.getViewAndState(state);
      }

      return VIEW_AND_STATES[state.ordinal()];
    }

    @Override
    public String toString()
    {
      return "ViewAndState[view=" + view + ", state=" + state + "]";
    }

    public static ViewAndState[] create(InternalCDOView view)
    {
      ViewAndState[] viewAndStates = new ViewAndState[STATE_VALUES.length];
      for (CDOState state : STATE_VALUES)
      {
        viewAndStates[state.ordinal()] = new ViewAndState(view, state);
      }

      return viewAndStates;
    }
  }
}
