/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Eike Stepper & Simon McDuff - bug 204890
 *    Simon McDuff - bug 246705
 *    Simon McDuff - bug 246622
 *    Christian W. Damus (CEA) - bug 400236: get internal instance of objects in ID conversion
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOContainerFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOUnsetFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.text.MessageFormat;
import java.util.List;

/**
 * CDORevision needs to follow these rules:<br>
 * - Keep CDOID only when the object (!isNew && !isTransient) // Only when CDOID will not changed.<br>
 * - Keep EObject for external reference, new, transient and that until commit time.<br>
 * It is important since these objects could changed and we need to keep a reference to {@link EObject} until the end.
 * It is the reason why {@link CDOStoreImpl} always call {@link InternalCDOView#convertObjectToID(Object, boolean)} with
 * true.
 *
 * @author Eike Stepper
 */
public final class CDOStoreImpl implements CDOStore
{
  private final ContextTracer TRACER = new ContextTracer(OM.DEBUG_STORE, CDOStoreImpl.class);

  private InternalCDOView view;

  /**
   * @since 2.0
   */
  public CDOStoreImpl(InternalCDOView view)
  {
    this.view = view;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView getView()
  {
    return view;
  }

  /**
   * @since 2.0
   */
  public void setContainer(InternalEObject eObject, CDOResource newResource, InternalEObject newEContainer,
      int newContainerFeatureID)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("setContainer({0}, {1}, {2}, {3})", cdoObject, newResource, newEContainer, newContainerFeatureID); //$NON-NLS-1$
      }

      Object newContainerID = newEContainer == null ? CDOID.NULL : cdoObject.cdoView().convertObjectToID(newEContainer,
          true);
      CDOID newResourceID = newResource == null ? CDOID.NULL : newResource.cdoID();

      CDOFeatureDelta delta = new CDOContainerFeatureDeltaImpl(newResourceID, newContainerID, newContainerFeatureID);
      getRevisionForWriting(cdoObject, delta);
    }
  }

  public InternalEObject getContainer(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getContainer({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return (InternalEObject)convertIDToObject(cdoObject.cdoView(), cdoObject,
          EcorePackage.eINSTANCE.eContainingFeature(), -1, revision.getContainerID());
    }
  }

  public int getContainingFeatureID(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getContainingFeatureID({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.getContainingFeatureID();
    }
  }

  /**
   * @since 2.0
   */
  public InternalEObject getResource(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getResource({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return (InternalEObject)convertIDToObject(cdoObject.cdoView(), cdoObject,
          EcorePackage.eINSTANCE.eContainingFeature(), -1, revision.getResourceID());
    }
  }

  public EStructuralFeature getContainingFeature(InternalEObject eObject)
  {
    throw new UnsupportedOperationException("Use getContainingFeatureID() instead"); //$NON-NLS-1$
  }

  public Object get(InternalEObject eObject, EStructuralFeature feature, int index)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("get({0}, {1}, {2})", cdoObject, feature, index); //$NON-NLS-1$
      }

      CDOFeatureAnalyzer featureAnalyzer = view.options().getFeatureAnalyzer();

      featureAnalyzer.preTraverseFeature(cdoObject, feature, index);
      InternalCDORevision revision = getRevisionForReading(cdoObject);

      Object value = revision.get(feature, index);
      value = convertToEMF(eObject, revision, feature, index, value);

      featureAnalyzer.postTraverseFeature(cdoObject, feature, index, value);
      return value;
    }
  }

  public boolean isSet(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("isSet({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      if (feature.isMany())
      {
        CDOList list = revision.getList(feature);
        return list != null && !list.isEmpty();
      }

      Object value = revision.getValue(feature);
      if (feature.isUnsettable())
      {
        return value != null;
      }

      if (value == null)
      {
        return false;
      }

      value = convertToEMF(eObject, revision, feature, NO_INDEX, value);
      Object defaultValue = feature.getDefaultValue();
      return !ObjectUtil.equals(value, defaultValue);
    }
  }

  public int size(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("size({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.size(feature);
    }
  }

  public boolean isEmpty(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("isEmpty({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.isEmpty(feature);
    }
  }

  public boolean contains(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("contains({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      Object convertedValue = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      boolean result = revision.contains(feature, convertedValue);

      // Special handling of detached (TRANSIENT) objects, see bug 354395
      if (!result && value != convertedValue && value instanceof EObject)
      {
        result = revision.contains(feature, value);
      }

      return result;
    }
  }

  public int indexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("indexOf({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.indexOf(feature, value);
    }
  }

  public int lastIndexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("lastIndexOf({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.lastIndexOf(feature, value);
    }
  }

  public int hashCode(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("hashCode({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.hashCode(feature);
    }
  }

  public Object[] toArray(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("toArray({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      Object[] result = revision.toArray(feature);
      for (int i = 0; i < result.length; i++)
      {
        result[i] = convertToEMF(eObject, revision, feature, i, result[i]);
      }

      // // TODO Clarify feature maps
      // if (feature instanceof EReference)
      // {
      // for (int i = 0; i < result.length; i++)
      // {
      // result[i] = resolveProxy(revision, feature, i, result[i]);
      // result[i] = convertIdToObject(cdoObject.cdoView(), eObject, feature, i, result[i]);
      // }
      // }

      return result;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(InternalEObject eObject, EStructuralFeature feature, T[] a)
  {
    synchronized (view)
    {
      Object[] array = toArray(eObject, feature);
      int size = array.length;

      if (a.length < size)
      {
        a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
      }

      System.arraycopy(array, 0, a, 0, size);
      if (a.length > size)
      {
        a[size] = null;
      }

      return a;
    }
  }

  public Object set(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("set({0}, {1}, {2}, {3})", cdoObject, feature, index, value); //$NON-NLS-1$
      }

      InternalCDORevision oldRevision = getRevisionForReading(cdoObject);
      Object oldValue = oldRevision.get(feature, index);
      oldValue = convertToEMF(eObject, oldRevision, feature, index, oldValue);

      if (!ObjectUtil.equals(value, oldValue))
      {
        value = convertToCDO(cdoObject, feature, value);

        CDOFeatureDelta delta = new CDOSetFeatureDeltaImpl(feature, index, value, oldValue);
        InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
        revision.set(feature, index, value);
      }

      return oldValue;
    }
  }

  public void unset(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("unset({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      CDOFeatureDelta delta = new CDOUnsetFeatureDeltaImpl(feature);
      getRevisionForWriting(cdoObject, delta);
    }
  }

  public void add(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("add({0}, {1}, {2}, {3})", cdoObject, feature, index, value); //$NON-NLS-1$
      }

      checkManyValued(feature);
      value = convertToCDO(cdoObject, feature, value);

      CDOFeatureDelta delta = new CDOAddFeatureDeltaImpl(feature, index, value);
      getRevisionForWriting(cdoObject, delta);
      // InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      // revision.add(feature, index, value);
    }
  }

  public Object remove(InternalEObject eObject, EStructuralFeature feature, int index)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("remove({0}, {1}, {2})", cdoObject, feature, index); //$NON-NLS-1$
      }

      Object oldValue = getListElement(cdoObject, feature, index);
      oldValue = convertToEMF(eObject, cdoObject.cdoRevision(), feature, index, oldValue);

      CDOFeatureDelta delta = new CDORemoveFeatureDeltaImpl(feature, index, oldValue);
      getRevisionForWriting(cdoObject, delta);

      // InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      // try
      // {
      // oldValue = convertToEMF(eObject, revision, feature, index, oldValue);
      // }
      // finally
      // {
      // revision.remove(feature, index);
      // }

      return oldValue;
    }
  }

  private Object getListElement(InternalCDOObject object, EStructuralFeature feature, int index)
  {
    checkManyValued(feature);

    // Bug 293283 / Bug 314387
    InternalCDORevision readLockedRevision = getRevisionForReading(object);
    CDOList list = readLockedRevision.getList(feature);
    int size = list.size();
    if (index < 0 || size <= index)
    {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    return readLockedRevision.get(feature, index);
  }

  private void checkManyValued(EStructuralFeature feature)
  {
    if (!feature.isMany())
    {
      throw new UnsupportedOperationException("Single-valued features have no list elements");
    }
  }

  public void clear(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("clear({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      CDOFeatureDelta delta = new CDOClearFeatureDeltaImpl(feature);
      getRevisionForWriting(cdoObject, delta);
      // InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      // // TODO Handle containment remove!!!
      // revision.clear(feature);
    }
  }

  public Object move(InternalEObject eObject, EStructuralFeature feature, int target, int source)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("move({0}, {1}, {2}, {3})", cdoObject, feature, target, source); //$NON-NLS-1$
      }

      Object value = getListElement(cdoObject, feature, source);
      CDOFeatureDelta delta = new CDOMoveFeatureDeltaImpl(feature, target, source, value);

      InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      // value = revision.move(feature, target, source);

      value = convertToEMF(eObject, revision, feature, target, value);
      return value;
    }
  }

  public EObject create(EClass eClass)
  {
    throw new UnsupportedOperationException("Use the generated factory to create objects"); //$NON-NLS-1$
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOStore[{0}]", view); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public Object resolveProxy(InternalCDORevision revision, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      if (value instanceof CDOElementProxy)
      {
        // Resolve proxy
        CDOElementProxy proxy = (CDOElementProxy)value;
        value = view.getSession().resolveElementProxy(revision, feature, index, proxy.getIndex());
      }

      return value;
    }
  }

  /**
   * @since 3.0
   */
  public Object convertToCDO(InternalCDOObject object, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      if (value != null)
      {
        if (feature instanceof EReference)
        {
          value = view.convertObjectToID(value, true);
        }
        else if (FeatureMapUtil.isFeatureMap(feature))
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)value;
          EStructuralFeature innerFeature = entry.getEStructuralFeature();
          Object innerValue = entry.getValue();
          Object convertedValue = view.convertObjectToID(innerValue);
          if (convertedValue != innerValue)
          {
            value = CDORevisionUtil.createFeatureMapEntry(innerFeature, convertedValue);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature.getEType());
          if (type != null)
          {
            value = type.convertToCDO(feature.getEType(), value);
          }
        }
      }

      return value;
    }
  }

  /**
   * @since 2.0
   */
  public Object convertToEMF(EObject eObject, InternalCDORevision revision, EStructuralFeature feature, int index,
      Object value)
  {
    synchronized (view)
    {
      if (value != null)
      {
        if (feature.isMany())
        {
          if (index == EStore.NO_INDEX)
          {
            return value;
          }

          value = resolveProxy(revision, feature, index, value);
          if (value instanceof CDOID)
          {
            CDOID id = (CDOID)value;
            CDOList list = revision.getList(feature);
            CDORevisionPrefetchingPolicy policy = view.options().getRevisionPrefetchingPolicy();

            InternalCDOSession session = view.getSession();
            InternalCDORevisionManager revisionManager = session.getRevisionManager();
            List<CDOID> listOfIDs = policy.loadAhead(revisionManager, view, eObject, feature, list, index, id);
            if (!listOfIDs.isEmpty())
            {
              int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();
              revisionManager.getRevisions(listOfIDs, view, initialChunkSize, CDORevision.DEPTH_NONE, true);
            }
          }
        }

        if (feature instanceof EReference)
        {
          value = convertIDToObject(view, eObject, feature, index, value);
        }
        else if (FeatureMapUtil.isFeatureMap(feature))
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)value;
          EStructuralFeature innerFeature = entry.getEStructuralFeature();
          Object innerValue = entry.getValue();
          Object convertedValue = convertIDToObject(view, eObject, feature, index, innerValue);
          if (convertedValue != innerValue)
          {
            value = FeatureMapUtil.createEntry(innerFeature, convertedValue);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature.getEType());
          if (type != null)
          {
            value = type.convertToEMF(feature.getEType(), value);
          }
        }
      }

      return value;
    }
  }

  private Object convertIDToObject(InternalCDOView view, EObject eObject, EStructuralFeature feature, int index,
      Object value)
  {
    try
    {
      value = view.convertIDToObject(value);
    }
    catch (ObjectNotFoundException ex)
    {
      if (value instanceof CDOID)
      {
        value = view.options().getStaleReferencePolicy().processStaleReference(eObject, feature, index, ex.getID());
      }
    }

    return getInternalInstance(value);
  }

  private InternalCDOObject getCDOObject(Object object)
  {
    return FSMUtil.adapt(object, view);
  }

  private Object getInternalInstance(Object object)
  {
    if (object instanceof InternalCDOObject)
    {
      return ((InternalCDOObject)object).cdoInternalInstance();
    }

    return object;
  }

  private static InternalCDORevision getRevisionForReading(InternalCDOObject cdoObject)
  {
    return safe(CDOStateMachine2.INSTANCE.read(cdoObject));
  }

  private static InternalCDORevision getRevisionForWriting(InternalCDOObject cdoObject, CDOFeatureDelta delta)
  {
    return safe(CDOStateMachine2.INSTANCE.write(cdoObject, delta));
  }

  private static InternalCDORevision getRevision(InternalCDOObject cdoObject)
  {
    return safe(cdoObject.cdoRevision());
  }

  private static InternalCDORevision safe(InternalCDORevision revision)
  {
    if (revision == null)
    {
      throw new IllegalStateException("revision == null");
    }

    return revision;
  }
}
