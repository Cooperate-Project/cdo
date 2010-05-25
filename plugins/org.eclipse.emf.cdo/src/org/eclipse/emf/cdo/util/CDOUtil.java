/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingPolicyImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOXATransactionImpl;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.view.CDORevisionPrefetchingPolicyImpl;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public final class CDOUtil
{
  private CDOUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static boolean prepareDynamicEPackage(EPackage startPackage)
  {
    if (CDOFactoryImpl.prepareDynamicEPackage(startPackage))
    {
      for (EPackage subPackage : startPackage.getESubpackages())
      {
        prepareDynamicEPackage(subPackage);
      }

      return true;
    }

    return false;
  }

  /**
   * @since 2.0
   */
  public static CDORevisionPrefetchingPolicy createRevisionPrefetchingPolicy(int chunkSize)
  {
    if (chunkSize <= 0)
    {
      return CDORevisionPrefetchingPolicy.NO_PREFETCHING;
    }

    return new CDORevisionPrefetchingPolicyImpl(chunkSize);
  }

  /**
   * @since 2.0
   */
  public static CDOCollectionLoadingPolicy createCollectionLoadingPolicy(int initialChunkSize, int resolveChunkSize)
  {
    if (initialChunkSize == CDORevision.UNCHUNKED && resolveChunkSize == CDORevision.UNCHUNKED)
    {
      return CDOCollectionLoadingPolicy.DEFAULT;
    }

    return new CDOCollectionLoadingPolicyImpl(initialChunkSize, resolveChunkSize);
  }

  /**
   * @since 2.0
   */
  public static CDOXATransaction createXATransaction(CDOViewSet viewSet)
  {
    CDOXATransaction xaTransaction = new CDOXATransactionImpl();
    if (viewSet != null)
    {
      xaTransaction.add(viewSet);
    }

    return xaTransaction;
  }

  /**
   * @since 2.0
   */
  public static CDOXATransaction createXATransaction()
  {
    return createXATransaction(null);
  }

  /**
   * @since 2.0
   */
  public static CDOXATransaction getXATransaction(CDOViewSet viewSet)
  {
    EList<Adapter> adapters = viewSet.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOXATransactionImpl.CDOXAInternalAdapter)
      {
        return ((CDOXATransactionImpl.CDOXAInternalAdapter)adapter).getCDOXA();
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static CDOViewSet getViewSet(ResourceSet resourceSet)
  {
    EList<Adapter> adapters = resourceSet.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOViewSet)
      {
        return (CDOViewSet)adapter;
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static void load(EObject eObject, CDOView view)
  {
    InternalCDOObject cdoObject = FSMUtil.adapt(eObject, view);
    CDOStateMachine.INSTANCE.read(cdoObject);

    for (Iterator<InternalCDOObject> it = FSMUtil.iterator(cdoObject.eContents(), (InternalCDOView)view); it.hasNext();)
    {
      InternalCDOObject content = it.next();
      load(content, view);
    }
  }

  /**
   * @since 2.0
   */
  public static EObject getEObject(EObject object)
  {
    if (object instanceof InternalCDOObject)
    {
      return ((InternalCDOObject)object).cdoInternalInstance();
    }

    return object;
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EObject object)
  {
    if (object instanceof CDOObject)
    {
      return (CDOObject)object;
    }

    return FSMUtil.adaptLegacy((InternalEObject)object);
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EModelElement object, CDOView view)
  {
    return FSMUtil.adaptMeta((InternalEObject)object, view);
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EGenericType object, CDOView view)
  {
    return FSMUtil.adaptMeta((InternalEObject)object, view);
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EStringToStringMapEntryImpl object, CDOView view)
  {
    return FSMUtil.adaptMeta(object, view);
  }

  /**
   * @since 2.0
   */
  public static CDORevision getRevisionByVersion(CDOObject object, int version)
  {
    if (FSMUtil.isTransient(object))
    {
      return null;
    }

    CDORevision revision = CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
    if (revision.getVersion() != version)
    {
      CDOSession session = object.cdoView().getSession();
      if (!session.repository().isSupportingAudits())
      {
        throw new IllegalStateException(Messages.getString("CDOUtil.0")); //$NON-NLS-1$
      }

      return session.getRevisionManager().getRevisionByVersion(object.cdoID(), 0, version);
    }

    return revision;
  }

  /**
   * @since 2.0
   */
  public static EList<Resource> getResources(ResourceSet resourceSet)
  {
    EList<Resource> result = new BasicEList<Resource>();
    EList<Resource> resources = resourceSet.getResources();
    for (Resource resource : resources)
    {
      if (resource instanceof CDOResource)
      {
        CDOResource cdoResource = (CDOResource)resource;
        if (cdoResource.isRoot())
        {
          continue;
        }
      }

      result.add(resource);
    }

    return result;
  }

  /**
   * Returns <code>true</code> if the given {@link CDOSession session} contains a dirty {@link CDOTransaction
   * transaction}, <code>false</code> otherwise.
   * 
   * @since 2.0
   * @see CDOTransaction
   */
  public static boolean isSessionDirty(CDOSession session)
  {
    for (CDOView view : session.getElements())
    {
      if (view.isDirty())
      {
        return true;
      }
    }

    return false;
  }
}
