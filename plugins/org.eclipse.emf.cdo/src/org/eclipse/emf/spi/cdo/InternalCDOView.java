/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.CDOStore;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDOView extends CDOView, ILifecycle
{
  public void setViewID(int viewId);

  public InternalCDOSession getSession();

  public void setSession(InternalCDOSession session);

  public InternalCDOViewSet getViewSet();

  public void setViewSet(InternalCDOViewSet viewSet);

  public CDOFeatureAnalyzer getFeatureAnalyzer();

  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer);

  public CDOStore getStore();

  public InternalCDOTransaction toTransaction();

  public void attachResource(CDOResourceImpl resource);

  /**
   * Returns the conflicting objects.
   */
  public Set<CDOObject> handleInvalidation(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedOIDs);

  public Set<CDOObject> handleInvalidationWithoutNotification(Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedOIDs, Set<InternalCDOObject> dirtyObjects, Set<InternalCDOObject> detachedObjects);

  public void getCDOIDAndVersion(Map<CDOID, CDOIDAndVersion> uniqueObjects, Collection<? extends CDOObject> cdoObjects);

  public void handleChangeSubscription(Collection<CDORevisionDelta> deltas, Collection<CDOID> detachedObjects);

  public InternalCDOObject[] getObjectsArray();

  public void remapObject(CDOID oldID);

  public CDOID getResourceNodeID(String path);

  public void registerProxyResource(CDOResourceImpl resource);

  public void registerObject(InternalCDOObject object);

  public void deregisterObject(InternalCDOObject object);

  public InternalCDOObject removeObject(CDOID id);

  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand);

  /**
   * @since 3.0
   */
  public CDOID convertObjectToID(Object potentialObject);

  /**
   * @since 3.0
   */
  public CDOID convertObjectToID(Object potentialObject, boolean onlyPersistedID);

  /**
   * @since 3.0
   */
  public InternalEObject convertIDToObject(Object potentialID);

  /**
   * @since 3.0
   */
  public boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers);

  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter);

  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter);

  public void subscribe(EObject eObject, Adapter adapter);

  public void unsubscribe(EObject eObject, Adapter adapter);

  public boolean hasSubscription(CDOID id);

  /**
   * Each time CDORevision or CDOState of an CDOObject is modified, ensure that no concurrent access is modifying it at
   * the same time. Uses {@link InternalCDOView#getStateLock()} to be thread safe.
   * <p>
   * In the case where {@link CDOObject#cdoRevision()} or {@link CDOObject#cdoState()} is called without using this
   * lock, it is not guarantee that the state didn't change immediately after.
   * <p>
   * <code>
   * if (cdoObject.cdoState() != CDOState.PROXY)
   * {
   *  // At this point could be a proxy!
   *  cdoObject.cdoRevision();
   * }
   * </code>
   * <p>
   * The reason were we didn't use {@link CDOView#getLock()} is to not allow the access of that lock to the users since
   * it is very critical. Instead of giving this API to the end-users, a better API should be given in the CDOObject to
   * give them want they need.
   */
  public ReentrantLock getStateLock();
}
