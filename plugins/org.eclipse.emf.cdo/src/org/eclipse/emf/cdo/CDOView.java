/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - http://bugs.eclipse.org/208689
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.util.ReadOnlyException;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.ref.ReferenceType;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A read-only view to the <em>current</em> (i.e. latest) state of the object graph in the repository of the underlying
 * {@link CDOSession session}.
 * <p>
 * Objects that are accessed through this view are unchangeable for the client. Each attempt to call a mutator on one of
 * these objects or one of their reference collections will result in a {@link ReadOnlyException} being thrown
 * immediately.
 * <p>
 * A view is opened through API of the underlying session like this:
 * 
 * <pre>
 *   CDOSession session = ...
 *   CDOView view = session.openView();
 *   ...
 * </pre>
 * 
 * CDOView instances <b>must not</b> be accessed through concurrent client threads.
 * <p>
 * Since a CDOObject, in a {@link CDOState#TRANSIENT non-TRANSIENT} state, is only meaningful in combination with its
 * dedicated view they must also not be accessed through concurrent client threads. Please note that at arbitrary times
 * an arbitrary number of framework background threads are allowed to use and modify a CDOview and its CDOObjects.
 * Whenever you are iterating over a number of CDOObjects and need to ensure that they are not modified by the framework
 * at the same time it is strongly recommended to acquire the {@link #getLock() view lock} and protect your code
 * appropriately.
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOView extends CDOProtocolView, INotifier
{
  /**
   * Returns the {@link CDOSession session} this view was opened by.
   * 
   * @return The session this view was opened by, or <code>null</code> if this view is closed.
   * @see #close()
   * @see #isClosed()
   * @see CDOSession#openView()
   * @see CDOSession#openView(ResourceSet)
   * @see CDOSession#openAudit(long)
   * @see CDOSession#openAudit(ResourceSet, long)
   * @see CDOSession#openTransaction()
   * @see CDOSession#openTransaction(ResourceSet)
   */
  public CDOSession getSession();

  /**
   * Returns the {@link CDOViewSet view set} this view is associated with.
   * 
   * @return The view set this view is associated with, never <code>null</code>.
   * @see CDOViewSet#getViews()
   * @since 2.0
   */
  public CDOViewSet getViewSet();

  /**
   * Returns the {@link ResourceSet resource set} this view is associated with.
   * <p>
   * Same as calling <tt>getViewSet().getResourceSet()</tt>.
   * 
   * @see CDOViewSet#getResourceSet()
   */
  public ResourceSet getResourceSet();

  /**
   * @since 2.0
   * @deprecated This API is provisional and subject to change or removal.
   */
  @Deprecated
  public URIHandler getURIHandler();

  /**
   * Returns a reentrant lock that can be used to prevent the framework from writing to any object in this view (as it
   * is caused, for example, by passive updates).
   * <p>
   * Acquiring this lock provides a means to safely iterate over multiple model elements without being affected by
   * unanticipated remote updates, like in the following example:
   * 
   * <pre>
   *    CDOResource resource = view.getResource(&quot;/orders/order-4711&quot;);
   *    PurchaseOrder order = (PurchaseOrder)resource.getContents().get(0);
   * 
   *    ReentrantLock lock = view.getLock();
   *    if (!lock.tryLock(5L, TimeUnit.SECONDS))
   *    {
   *      throw new TimeoutException();
   *    }
   * 
   *    try
   *    {
   *      float sum = 0;
   *      for (OrderDetail detail : order.getOrderDetails())
   *      {
   *        sum += detail.getPrice();
   *      }
   * 
   *      System.out.println(&quot;Sum: &quot; + sum);
   *    }
   *    finally
   *    {
   *      lock.unlock();
   *    }
   *  }
   * </pre>
   * 
   * Note that this method really just returns the lock instance but does <b>not</b> acquire the lock! The above example
   * acquires the lock with a timeout that expires after five seconds.
   * 
   * @since 2.0
   */
  public ReentrantLock getLock();

  /**
   * Returns always <code>false</code>.
   * <p>
   * This method has a special implementation in {@link CDOTransaction} as well.
   * 
   * @see CDOTransaction#isDirty()
   */
  public boolean isDirty();

  /**
   * Returns always <code>false</code>.
   * <p>
   * This method has a special implementation in {@link CDOTransaction} as well.
   * 
   * @see CDOTransaction#hasConflict()
   */
  public boolean hasConflict();

  /**
   * Returns <code>true</code> if a resource with the given path exists in the repository, <code>false</code>.
   * 
   * @see #getResource(String, boolean)
   */
  public boolean hasResource(String path);

  /**
   * @see ResourceSet#getResource(URI, boolean)
   * @since 2.0
   */
  public CDOResource getResource(String path, boolean loadInDemand);

  /**
   * @see ResourceSet#getResource(URI, boolean)
   */
  public CDOResource getResource(String path);

  /**
   * Returns the root resource of the repository.
   * <p>
   * The root resource is a special resource with only {@link CDOResourceNode CDOResourceNodes} in its contents list.
   * You can use it as the main entry into the new resource and folder structure.
   * 
   * @since 2.0
   */
  public CDOResource getRootResource();

  /**
   * Returns a list of the resources in the given folder with a name equal to or starting with the value of the name
   * parameter.
   * 
   * @param folder
   *          The folder to search in, or <code>null</code> for top level resource nodes.
   * @param name
   *          the name or prefix of the resource nodes to return.
   * @param exactMatch
   *          <code>true</code> if the complete name of the resource must match, <code>false</code> if only a common
   *          prefix of the name must match.
   * @since 2.0
   */
  public List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch);

  /**
   * Returns an iterator over the resources in the given folder with a name equal to or starting with the value of the
   * name parameter. The underlying query will be executed asynchronously.
   * 
   * @param folder
   *          The folder to search in, or <code>null</code> for top level resource nodes.
   * @param name
   *          the name or prefix of the resource nodes to return.
   * @param exactMatch
   *          <code>true</code> if the complete name of the resource must match, <code>false</code> if only a common
   *          prefix of the name must match.
   * @since 2.0
   */
  public CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name,
      boolean exactMatch);

  /**
   * Returns the object for the given CDOID.
   * 
   * @param loadOnDemand
   *          whether to create and load the object, if it doesn't already exist.
   * @return the object resolved by the CDOID if the id is not <code>null</code>, or <code>null</code> if there isn't
   *         one and loadOnDemand is <code>false</code>.
   */
  public CDOObject getObject(CDOID id, boolean loadOnDemand);

  /**
   * Returns the object for the given CDOID.
   * <p>
   * Same as <code>getObject(id, true)</code>.
   * 
   * @see getObject(CDOID, boolean)
   */
  public CDOObject getObject(CDOID id);

  /**
   * Takes an object from a (possibly) different view and <em>contextifies</em> it for the usage with this view.
   * <ul>
   * <li>If the given object is contained in this view it is returned unmodified.
   * <li>If the given object can not be cast to {@link CDOObject} it is returned unmodified.
   * <li>If the view of the given object is contained in a different session an <code>IllegalArgumentException</code> is
   * thrown.
   * <li>If <code>null</code> is passed <code>null</code> is returned.
   * </ul>
   * 
   * @since 2.0
   */
  public <T extends EObject> T getObject(T objectFromDifferentView);

  /**
   * Returns <code>true</code> if an {@link CDOObject object} with the given {@link CDOID id} is currently registered in
   * this view, <code>false</code> otherwise.
   */
  public boolean isObjectRegistered(CDOID id);

  /**
   * Reloads the given {@link CDOObject objects} from the repository.
   */
  public int reload(CDOObject... objects);

  /**
   * @since 2.0
   */
  public void lockObjects(Collection<? extends CDOObject> objects, RWLockManager.LockType lockType, long timeout)
      throws InterruptedException;

  /**
   * Unlocks the given locked objects of this view.
   * 
   * @since 2.0
   */
  public void unlockObjects(Collection<? extends CDOObject> objects, RWLockManager.LockType lockType);

  /**
   * Unlocks all locked objects of this view.
   * 
   * @since 2.0
   */
  public void unlockObjects();

  /**
   * @since 2.0
   */
  public CDOQuery createQuery(String language, String queryString);

  /**
   * @since 2.0
   */
  public Options options();

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  public interface Options
  {
    /**
     */
    public static final int NO_REVISION_PREFETCHING = 1;

    /**
     * Returns the reference type to be used in the internal object cache.
     * 
     * @return Either {@link ReferenceType#STRONG STRONG}, {@link ReferenceType#SOFT SOFT} or {@link ReferenceType#WEAK
     *         WEAK}.
     */
    public ReferenceType getCacheReferenceType();

    /**
     * Sets the reference type to be used in the internal object cache to either {@link ReferenceType#STRONG STRONG},
     * {@link ReferenceType#SOFT SOFT} or {@link ReferenceType#WEAK WEAK}. If <code>null</code> is passed the default
     * reference type {@link ReferenceType#SOFT SOFT} is set. If the given reference type does not differ from the one
     * being currently set the new value is ignored and <code>false</code> is returned. Otherwise existing object
     * references are converted to the new type and <code>true</code> is returned.
     */
    public boolean setCacheReferenceType(ReferenceType referenceType);

    /**
     * Returns <code>true</code> if the {@link CDOObject objects} in this view will notify their
     * {@link org.eclipse.emf.common.notify.Adapter adapters} about the fact that they are <em>invalidated</em> (due to
     * remote changes), <code>false</code> otherwise.
     * 
     * @see CDOInvalidationNotification
     */
    public boolean isInvalidationNotificationEnabled();

    /**
     * Specifies whether the {@link CDOObject objects} in this view will notify their
     * {@link org.eclipse.emf.common.notify.Adapter adapters} about the fact that they are <em>invalidated</em> (due to
     * remote changes) or not.
     * 
     * @see CDOInvalidationNotification
     */
    public void setInvalidationNotificationEnabled(boolean enabled);

    /**
     * Returns the current {@link CDOAdapterPolicy change subscription policy}.
     * 
     * @return The current change subscription policy, never <code>null</code>.
     * @see #setChangeSubscriptionPolicy(CDOAdapterPolicy)
     */
    public CDOAdapterPolicy getChangeSubscriptionPolicy();

    /**
     * Specifies the change subscription policy. By default, the value is set to {@link CDOAdapterPolicy#NONE}.
     * <p>
     * To activate the policy, you must do the following: <br>
     * <code>view.setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy.ALL);</code>
     * <p>
     * To register an object, you must add an adapter to the object in which you are interested:<br>
     * <code>eObject.eAdapters().add(myAdapter);</code>
     * <p>
     * By activating this feature, each object having at least one adapter that matches the current policy will be
     * registered with the server and will be notified for each change occurring in the scope of any other transaction.
     * <p>
     * {@link CDOAdapterPolicy#NONE} - Disabled. <br>
     * {@link CDOAdapterPolicy#ALL} - Enabled for all adapters used.<br>
     * {@link CDOAdapterPolicy#ONLY_CDO_ADAPTER} - Enabled only for adapters that implement {@link CDOAdapter}. <br>
     * Any other class that implement {@link CDOAdapterPolicy} will enable for whatever rules defined in that class.
     * <br>
     * <p>
     * If <code>myAdapter</code> in the above example matches the current policy, <code>eObject</code> will be
     * registered with the server and you will receive all changes from other transaction.
     * <p>
     * When the policy is changed all objects in the cache will automatically be recalculated.
     * <p>
     * You can subscribe to temporary objects. Even if you cannot receive notifications from other
     * {@link CDOTransaction} for these because they are only local to you, at commit time these objects will be
     * registered automatically.
     * <p>
     * <b>Note:</b> It can be used with <code>CDOSession.setPassiveUpdate(false)</code>. In this case, it will receive
     * changes without having the objects changed.
     */
    public void setChangeSubscriptionPolicy(CDOAdapterPolicy policy);

    /**
     * Sets the reference type to be used when an adapter is used to an object.
     * <p>
     * When <code>CDOView.setStrongReference(CDOAdapterPolicy.ALL)</code> is used, it is possible that the target object
     * will be GC. In that case, the adapter will never received notifications. By Default the value is at
     * <code>CDOAdapterPolicy.ALL</code>
     */
    public void setStrongReferencePolicy(CDOAdapterPolicy policy);

    /**
     * Returns the CDORevisionPrefetchingPolicy in used.
     */
    public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy();

    /**
     * The CDORevisionPrefetchingPolicy feature of the CDOView allows CDO users to fetch many objects at a time.
     * <p>
     * The difference between the CDOCollectionLoadingPolicy feature and the CDORevisionPrefetchingPolicy feature is
     * subtle. The CDOCollectionLoadingPolicy feature determines how and when to fetch CDOIDs, while the
     * CDORevisionPrefetchingPolicy feature determines how and when to resolve CDOIDs (i.e. fetch the target objects).
     * <p>
     * <code>view.options().setRevisionPrefetchingPolicy (CDOUtil.createRevisionPrefetchingPolicy(10));</code>
     * <p>
     * The end-user could provide its own implementation of the CDORevisionPrefetchingPolicy interface.
     */
    public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy);
  }
}
