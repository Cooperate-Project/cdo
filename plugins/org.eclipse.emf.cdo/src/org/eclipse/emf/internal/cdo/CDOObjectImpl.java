/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 *    Eike Stepper & Simon McDuff - http://bugs.eclipse.org/204890 
 *    Simon McDuff - http://bugs.eclipse.org/246705
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.GenUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.DelegatingEcoreEList;
import org.eclipse.emf.ecore.util.DelegatingFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Eike Stepper
 */
public class CDOObjectImpl extends EStoreEObjectImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOObjectImpl.class);

  private CDOID id;

  private CDOState state;

  private CDOViewImpl cdoView;

  private CDOResourceImpl resource;

  private InternalCDORevision revision;

  public CDOObjectImpl()
  {
    state = CDOState.TRANSIENT;
    eContainer = null;
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOState cdoState()
  {
    return state;
  }

  public InternalCDORevision cdoRevision()
  {
    return revision;
  }

  public CDOClass cdoClass()
  {
    return getCDOClass(this);
  }

  public CDOViewImpl cdoView()
  {
    return cdoView;
  }

  public CDOResourceImpl cdoResource()
  {
    if (this instanceof CDOResourceImpl)
    {
      resource = (CDOResourceImpl)this;
    }

    return resource;
  }

  public void cdoReload()
  {
    CDOStateMachine.INSTANCE.reload(this);
  }

  public void cdoInternalSetID(CDOID id)
  {
    if (id == null)
    {
      throw new IllegalArgumentException("id == null");
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id);
    }

    this.id = id;
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      try
      {
        return this.state;
      }
      finally
      {
        this.state = state;
      }
    }

    // TODO Detect duplicate cdoInternalSetState() calls
    return null;
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision);
    }

    this.revision = (InternalCDORevision)revision;
  }

  public void cdoInternalSetView(CDOView view)
  {
    CDOViewImpl impl = (CDOViewImpl)view;
    cdoView = impl;
    eSetStore(impl.getStore());
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    if (this instanceof CDOResourceImpl)
    {
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resource: {0}", resource);
    }

    this.resource = (CDOResourceImpl)resource;
  }

  public void cdoInternalPostLoad()
  {
    // Do nothing
  }

  public void cdoInternalPostAttach()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating revision for {0}", this);
    }

    CDOViewImpl view = cdoView();
    revision.setContainerID(eContainer == null ? CDOID.NULL : cdoView().convertObjectToID(eContainer, true));
    revision.setContainingFeatureID(eContainerFeatureID);
    Resource directResource = eDirectResource();
    if (directResource instanceof CDOResource)
    {
      CDOResource cdoResource = (CDOResource)directResource;
      cdoInternalSetResource(cdoResource);
      revision.setResourceID(cdoResource.cdoID());
    }

    eSettings();

    EClass eClass = eClass();
    for (int i = 0; i < eClass.getFeatureCount(); i++)
    {
      EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
      if (!eFeature.isTransient())
      {
        populateRevisionFeature(view, revision, eFeature, eSettings, i);
      }
    }

    if (eBasicAdapters() != null)
    {
      for (Adapter adapter : eBasicAdapters())
      {
        view.subscribe(this, adapter);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void populateRevisionFeature(CDOViewImpl view, InternalCDORevision revision, EStructuralFeature eFeature,
      Object[] eSettings, int i)
  {
    CDOFeature cdoFeature = ModelUtil.getCDOFeature(eFeature, view.getSession().getPackageManager());
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating feature {0}", cdoFeature);
    }

    Object setting = eSettings[i];
    if (setting == null)
    {
      setting = eFeature.getDefaultValue();
    }

    if (cdoFeature.isMany())
    {
      if (setting != null)
      {
        int index = 0;
        EList<Object> list = (EList<Object>)setting;
        for (Object value : list)
        {
          if (cdoFeature.isReference())
          {
            value = view.convertObjectToID(value, true);
          }

          revision.add(cdoFeature, index++, value);
        }
      }
    }
    else
    {
      if (cdoFeature.isReference())
      {
        setting = view.convertObjectToID(setting, true);
      }
      else
      {
        if (cdoFeature.getType() == CDOType.CUSTOM)
        {
          setting = EcoreUtil.convertToString((EDataType)eFeature.getEType(), setting);
        }
        else if (setting == null && GenUtil.isPrimitiveType(eFeature.getEType()))
        {
          setting = eFeature.getDefaultValue();
        }
      }

      revision.set(cdoFeature, 0, setting);
    }

    if (eSettings != null)
    {
      eSettings[i] = null;
    }
  }

  public void cdoInternalPostDetach()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating revision for {0}", this);
    }

    CDOViewImpl view = cdoView();
    eContainer = null;
    eContainerFeatureID = 0;

    eSettings();

    EClass eClass = eClass();
    for (int i = 0; i < eClass.getFeatureCount(); i++)
    {
      EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
      if (!eFeature.isTransient())
      {
        depopulateRevisionFeature(view, revision, eFeature, eSettings, i);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void depopulateRevisionFeature(CDOViewImpl view, InternalCDORevision revision, EStructuralFeature eFeature,
      Object[] eSettings, int i)
  {
    CDOFeature cdoFeature = ModelUtil.getCDOFeature(eFeature, view.getSession().getPackageManager());
    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating feature {0}", cdoFeature);
    }

    boolean isReference = cdoFeature.isReference();
    if (cdoFeature.isMany())
    {
      eSettings[i] = null;
      List<Object> setting = (List<Object>)super.dynamicGet(eFeature.getFeatureID());
      List<Object> list = revision.getList(cdoFeature);
      for (Object value : list)
      {
        if (isReference)
        {
          value = view.convertIDToObject(value);
        }

        setting.add(value);
      }
    }
    else
    {
      Object value = revision.getValue(cdoFeature);
      if (isReference)
      {
        value = view.convertIDToObject(value);
      }
      else if (cdoFeature.getType() == CDOType.CUSTOM)
      {
        value = EcoreUtil.createFromString((EDataType)eFeature.getEType(), (String)value);
      }

      eSettings[i] = value;
    }
  }

  public void cdoInternalPreCommit()
  {
    // Do nothing
  }

  public InternalEObject cdoInternalInstance()
  {
    return this;
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    return eDynamicFeature(dynamicFeatureID);
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized EList<Adapter> eAdapters()
  {
    if (eAdapters == null)
    {
      eAdapters = new EAdapterList<Adapter>(this)
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected void didAdd(int index, Adapter newObject)
        {
          if (!FSMUtil.isTransient(CDOObjectImpl.this))
          {
            cdoView().subscribe(CDOObjectImpl.this, newObject);
          }
        }

        @Override
        protected void didRemove(int index, Adapter oldObject)
        {
          if (!FSMUtil.isTransient(CDOObjectImpl.this))
          {
            cdoView().unsubscribe(CDOObjectImpl.this, oldObject);
          }
        }
      };
    }

    return eAdapters;
  }

  @Override
  protected FeatureMap createFeatureMap(EStructuralFeature eStructuralFeature)
  {
    return new CDOStoreFeatureMap(eStructuralFeature);
  }

  @Override
  protected EList<?> createList(final EStructuralFeature eStructuralFeature)
  {
    final EClassifier eType = eStructuralFeature.getEType();
    if (eType.getInstanceClassName() == "java.util.Map$Entry")
    {
      class EStoreEcoreEMap extends EcoreEMap<Object, Object>
      {
        private static final long serialVersionUID = 1L;

        public EStoreEcoreEMap()
        {
          super((EClass)eType, eType.getInstanceClass(), null);
          delegateEList = new CDOStoreEList<BasicEMap.Entry<Object, Object>>(eStructuralFeature)
          {
            private static final long serialVersionUID = 1L;

            @Override
            protected void didAdd(int index, BasicEMap.Entry<Object, Object> newObject)
            {
              EStoreEcoreEMap.this.doPut(newObject);
            }

            @Override
            protected void didSet(int index, BasicEMap.Entry<Object, Object> newObject,
                BasicEMap.Entry<Object, Object> oldObject)
            {
              didRemove(index, oldObject);
              didAdd(index, newObject);
            }

            @Override
            protected void didRemove(int index, BasicEMap.Entry<Object, Object> oldObject)
            {
              EStoreEcoreEMap.this.doRemove(oldObject);
            }

            @Override
            protected void didClear(int size, Object[] oldObjects)
            {
              EStoreEcoreEMap.this.doClear();
            }

            @Override
            protected void didMove(int index, BasicEMap.Entry<Object, Object> movedObject, int oldIndex)
            {
              EStoreEcoreEMap.this.doMove(movedObject);
            }
          };

          size = delegateEList.size();
        }
      }

      return new EStoreEcoreEMap();
    }

    return new CDOStoreEList<Object>(eStructuralFeature);
  }

  @Override
  protected void eInitializeContainer()
  {
    throw new ImplementationError();
  }

  @Override
  protected void eSetDirectResource(Internal resource)
  {
    if (FSMUtil.isTransient(this))
    {
      super.eSetDirectResource(resource);
    }
    else if (resource instanceof CDOResourceImpl || resource == null)
    {

      this.resource = (CDOResourceImpl)resource;
      getStore().setContainer(this, cdoResource(), eInternalContainer(), eContainerFeatureID());
    }
    else
    {
      throw new IllegalArgumentException("Resource needs to be an instanceof CDOResourceImpl");
    }
  }

  @Override
  public Internal eDirectResource()
  {
    if (this instanceof Internal)
    {
      return (Internal)this;
    }

    if (FSMUtil.isTransient(this))
    {
      return super.eDirectResource();
    }

    return cdoResource();
  }

  /**
   * Don't cache non-transient features in this CDOObject's {@link #eSettings()}.
   */
  @Override
  protected boolean eIsCaching()
  {
    return false;
  }

  @Override
  public Object dynamicGet(int dynamicFeatureID)
  {
    if (FSMUtil.isTransient(this))
    {
      if (eSettings == null)
      {
        return null;
      }

      return eSettings[dynamicFeatureID];
    }

    // Delegate to CDOStore
    return super.dynamicGet(dynamicFeatureID);
  }

  @Override
  public boolean eIsSet(EStructuralFeature feature)
  {
    if (FSMUtil.isTransient(this))
    {
      // TODO What about defaultValues != null?
      if (eSettings == null)
      {
        return false;
      }

      return eSettings[eDynamicFeatureID(feature)] != null;
    }

    // Delegate to CDOStore
    return super.eIsSet(feature);
  }

  @Override
  public void dynamicSet(int dynamicFeatureID, Object value)
  {
    if (FSMUtil.isTransient(this))
    {
      eSettings(); // Important to create eSettings array if necessary
      eSettings[dynamicFeatureID] = value;
    }
    else
    {
      // Delegate to CDOStore
      super.dynamicSet(dynamicFeatureID, value);
    }
  }

  @Override
  public void dynamicUnset(int dynamicFeatureID)
  {
    if (FSMUtil.isTransient(this))
    {
      if (eSettings != null)
      {
        eSettings[dynamicFeatureID] = null;
      }
    }
    else
    {
      // Delegate to CDOStore
      super.dynamicUnset(dynamicFeatureID);
    }
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    InternalEObject container;
    if (FSMUtil.isTransient(this))
    {
      container = eContainer;
    }
    else
    {
      // Delegate to CDOStore
      container = getStore().getContainer(this);
    }

    if (container instanceof CDOResource)
    {
      return null;
    }

    return container;
  }

  @Override
  public int eContainerFeatureID()
  {
    if (FSMUtil.isTransient(this))
    {
      return eContainerFeatureID;
    }

    // Delegate to CDOStore
    return getStore().getContainingFeatureID(this);
  }

  /**
   * Code took from {@link BasicEObjectImpl#eBasicSetContainer} and modify it to detect when object are moved in the
   * same context.
   */
  @Override
  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
      NotificationChain msgs)
  {
    InternalEObject oldContainer = eInternalContainer();
    Resource.Internal oldResource = eDirectResource();
    Resource.Internal newResource = null;
    if (oldResource != null)
    {
      if (newContainer != null && !eContainmentFeature(this, newContainer, newContainerFeatureID).isResolveProxies())
      {
        msgs = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, msgs);
        eSetDirectResource(null);
        newResource = newContainer.eInternalResource();
      }
      else
      {
        oldResource = null;
      }
    }
    else
    {
      if (oldContainer != null)
      {
        oldResource = oldContainer.eInternalResource();
      }
      if (newContainer != null)
      {
        newResource = newContainer.eInternalResource();
      }
    }
    CDOView oldView = cdoView;
    CDOView newView = newResource != null && newResource instanceof CDOResource ? ((CDOResource)newResource).cdoView()
        : null;

    boolean moved = oldView != null && oldView == newView;

    if (!moved && oldResource != null)
    {
      oldResource.detached(this);
    }

    int oldContainerFeatureID = eContainerFeatureID();
    eBasicSetContainer(newContainer, newContainerFeatureID);

    if (!moved && oldResource != newResource && newResource != null)
    {
      newResource.attached(this);
    }

    if (eNotificationRequired())
    {
      if (oldContainer != null && oldContainerFeatureID >= 0 && oldContainerFeatureID != newContainerFeatureID)
      {
        ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, oldContainerFeatureID,
            oldContainer, null);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }
      if (newContainerFeatureID >= 0)
      {
        ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, newContainerFeatureID,
            oldContainerFeatureID == newContainerFeatureID ? oldContainer : null, newContainer);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }
    }
    return msgs;
  }

  /**
   * Code took from {@link BasicEObjectImpl#eSetResource} and modify it to detect when object are moved in the same
   * context.
   */
  @Override
  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    Resource.Internal oldResource = eDirectResource();

    CDOView oldView = cdoView;
    CDOView newView = resource != null && resource instanceof CDOResource ? ((CDOResource)resource).cdoView() : null;

    boolean isSameView = oldView != null && oldView == newView;

    if (oldResource != null)
    {
      notifications = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, notifications);

      // When setting the resource to null we assume that detach has already been called in the resource implementation
      //

      if (!isSameView && resource != null)
      {
        oldResource.detached(this);
      }
    }

    InternalEObject oldContainer = eInternalContainer();
    if (oldContainer != null && !isSameView)
    {
      if (eContainmentFeature().isResolveProxies())
      {
        Resource.Internal oldContainerResource = oldContainer.eInternalResource();
        if (oldContainerResource != null)
        {
          // If we're not setting a new resource, attach it to the old container's resource.
          if (resource == null)
          {
            oldContainerResource.attached(this);
          }
          // If we didn't detach it from an old resource already, detach it from the old container's resource.
          //
          else if (oldResource == null)
          {
            oldContainerResource.detached(this);
          }
        }
      }
      else
      {
        notifications = eBasicRemoveFromContainer(notifications);
        notifications = eBasicSetContainer(null, -1, notifications);
      }
    }

    eSetDirectResource(resource);

    return notifications;
  }

  @Override
  protected void eBasicSetContainer(InternalEObject newEContainer, int newContainerFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting container: {0}, featureID={1}", newEContainer, newContainerFeatureID);
    }

    if (FSMUtil.isTransient(this))
    {
      super.eBasicSetContainer(newEContainer, newContainerFeatureID);

    }
    else
    {
      getStore().setContainer(this, cdoResource(), newEContainer, newContainerFeatureID);
    }
  }

  /**
   * Specializing the behaviour of {@link #equals(Object)} is not permitted as per {@link EObject} specification.
   */
  @Override
  public final boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  @Override
  public String toString()
  {
    if (id == null)
    {
      return eClass().getName() + "?";
    }

    return eClass().getName() + "@" + id;
  }

  static CDOClass getCDOClass(InternalCDOObject cdoObject)
  {
    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    CDOSessionPackageManagerImpl packageManager = view.getSession().getPackageManager();
    return ModelUtil.getCDOClass(cdoObject.eClass(), packageManager);
  }

  private CDOStore getStore()
  {
    return (CDOStore)eStore();
  }

  /**
   * TODO Remove this when EMF has fixed http://bugs.eclipse.org/197487
   * 
   * @author Eike Stepper
   */
  public class CDOStoreEList<E> extends DelegatingEcoreEList.Dynamic<E>
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreEList(EStructuralFeature eStructuralFeature)
    {
      super(CDOObjectImpl.this, eStructuralFeature);
    }

    @Override
    protected List<E> delegateList()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    @Override
    protected void delegateAdd(int index, Object object)
    {
      getStore().add(owner, eStructuralFeature, index, object);
    }

    @Override
    protected void delegateAdd(Object object)
    {
      delegateAdd(delegateSize(), object);
    }

    @Override
    protected List<E> delegateBasicList()
    {
      int size = delegateSize();
      if (size == 0)
      {
        return ECollections.emptyEList();
      }

      Object[] data = getStore().toArray(owner, eStructuralFeature);
      return new EcoreEList.UnmodifiableEList<E>(owner, eStructuralFeature, data.length, data);
    }

    @Override
    protected void delegateClear()
    {
      getStore().clear(owner, eStructuralFeature);
    }

    @Override
    protected boolean delegateContains(Object object)
    {
      return getStore().contains(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateContainsAll(Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (!delegateContains(o))
        {
          return false;
        }
      }
      return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateGet(int index)
    {
      return (E)getStore().get(owner, eStructuralFeature, index);
    }

    @Override
    protected int delegateHashCode()
    {
      return getStore().hashCode(owner, eStructuralFeature);
    }

    @Override
    protected int delegateIndexOf(Object object)
    {
      return getStore().indexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateIsEmpty()
    {
      return getStore().isEmpty(owner, eStructuralFeature);
    }

    @Override
    protected Iterator<E> delegateIterator()
    {
      return iterator();
    }

    @Override
    protected int delegateLastIndexOf(Object object)
    {
      return getStore().lastIndexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected ListIterator<E> delegateListIterator()
    {
      return listIterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateRemove(int index)
    {
      return (E)getStore().remove(owner, eStructuralFeature, index);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateSet(int index, E object)
    {
      return (E)getStore().set(owner, eStructuralFeature, index, object);
    }

    @Override
    protected int delegateSize()
    {
      return getStore().size(owner, eStructuralFeature);
    }

    @Override
    protected Object[] delegateToArray()
    {
      return getStore().toArray(owner, eStructuralFeature);
    }

    @Override
    protected <T> T[] delegateToArray(T[] array)
    {
      return getStore().toArray(owner, eStructuralFeature, array);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateMove(int targetIndex, int sourceIndex)
    {
      return (E)getStore().move(owner, eStructuralFeature, targetIndex, sourceIndex);
    }

    @Override
    protected boolean delegateEquals(Object object)
    {
      if (object == this)
      {
        return true;
      }

      if (!(object instanceof List))
      {
        return false;
      }

      List<?> list = (List<?>)object;
      if (list.size() != delegateSize())
      {
        return false;
      }

      for (ListIterator<?> i = list.listIterator(); i.hasNext();)
      {
        Object element = i.next();
        if (element == null ? get(i.previousIndex()) != null : !element.equals(get(i.previousIndex())))
        {
          return false;
        }
      }

      return true;
    }

    @Override
    protected String delegateToString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");
      for (int i = 0, size = size(); i < size;)
      {
        Object value = delegateGet(i);
        stringBuffer.append(String.valueOf(value));
        if (++i < size)
        {
          stringBuffer.append(", ");
        }
      }
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }

  /**
   * TODO Remove this when EMF has fixed http://bugs.eclipse.org/197487
   * 
   * @author Eike Stepper
   */
  public class CDOStoreFeatureMap extends DelegatingFeatureMap
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreFeatureMap(EStructuralFeature eStructuralFeature)
    {
      super(CDOObjectImpl.this, eStructuralFeature);
    }

    @Override
    protected List<FeatureMap.Entry> delegateList()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    @Override
    protected void delegateAdd(int index, Entry object)
    {
      getStore().add(owner, eStructuralFeature, index, object);
    }

    @Override
    protected void delegateAdd(Entry object)
    {
      delegateAdd(delegateSize(), object);
    }

    @Override
    protected List<FeatureMap.Entry> delegateBasicList()
    {
      int size = delegateSize();
      if (size == 0)
      {
        return ECollections.emptyEList();
      }

      Object[] data = getStore().toArray(owner, eStructuralFeature);
      return new EcoreEList.UnmodifiableEList<FeatureMap.Entry>(owner, eStructuralFeature, data.length, data);
    }

    @Override
    protected void delegateClear()
    {
      getStore().clear(owner, eStructuralFeature);
    }

    @Override
    protected boolean delegateContains(Object object)
    {
      return getStore().contains(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateContainsAll(Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (!delegateContains(o))
        {
          return false;
        }
      }
      return true;
    }

    @Override
    protected Entry delegateGet(int index)
    {
      return (Entry)getStore().get(owner, eStructuralFeature, index);
    }

    @Override
    protected int delegateHashCode()
    {
      return getStore().hashCode(owner, eStructuralFeature);
    }

    @Override
    protected int delegateIndexOf(Object object)
    {
      return getStore().indexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateIsEmpty()
    {
      return getStore().isEmpty(owner, eStructuralFeature);
    }

    @Override
    protected Iterator<FeatureMap.Entry> delegateIterator()
    {
      return iterator();
    }

    @Override
    protected int delegateLastIndexOf(Object object)
    {
      return getStore().lastIndexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected ListIterator<FeatureMap.Entry> delegateListIterator()
    {
      return listIterator();
    }

    @Override
    protected Entry delegateRemove(int index)
    {
      return (Entry)getStore().remove(owner, eStructuralFeature, index);
    }

    @Override
    protected Entry delegateSet(int index, Entry object)
    {
      return (Entry)getStore().set(owner, eStructuralFeature, index, object);
    }

    @Override
    protected int delegateSize()
    {
      return getStore().size(owner, eStructuralFeature);
    }

    @Override
    protected Object[] delegateToArray()
    {
      return getStore().toArray(owner, eStructuralFeature);
    }

    @Override
    protected <T> T[] delegateToArray(T[] array)
    {
      return getStore().toArray(owner, eStructuralFeature, array);
    }

    @Override
    protected Entry delegateMove(int targetIndex, int sourceIndex)
    {
      return (Entry)getStore().move(owner, eStructuralFeature, targetIndex, sourceIndex);
    }

    @Override
    protected String delegateToString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");
      for (int i = 0, size = size(); i < size;)
      {
        Object value = delegateGet(i);
        stringBuffer.append(String.valueOf(value));
        if (++i < size)
        {
          stringBuffer.append(", ");
        }
      }
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }
}
