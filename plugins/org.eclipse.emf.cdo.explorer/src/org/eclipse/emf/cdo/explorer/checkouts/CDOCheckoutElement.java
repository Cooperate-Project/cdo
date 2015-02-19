/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.checkouts;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;

import org.eclipse.net4j.util.AdapterUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IAdaptable;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.4
 */
public class CDOCheckoutElement extends AdapterImpl implements IAdaptable
{
  private static final Class<CDOCheckoutElement> TYPE = CDOCheckoutElement.class;

  private final EObject delegate;

  private final List<Object> children = new ArrayList<Object>();

  public CDOCheckoutElement(EObject delegate)
  {
    this.delegate = delegate;
  }

  public Object getDelegate()
  {
    return delegate;
  }

  public Object getParent()
  {
    return CDOExplorerUtil.getParentOfEObject(delegate);
  }

  public Object[] getChildren()
  {
    return children.toArray();
  }

  public boolean hasChildren()
  {
    return !children.isEmpty();
  }

  public void addChild(Object child)
  {
    EList<Adapter> adapters = removeFrom(child);
    if (adapters != null)
    {
      synchronized (TYPE)
      {
        adapters.add(this);
      }
    }

    children.add(child);
  }

  public void reset()
  {
    children.clear();
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == TYPE;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Class adapter)
  {
    if (adapter == EObject.class)
    {
      return delegate;
    }

    return AdapterUtil.adapt(this, adapter, false);
  }

  public String toString(Object child)
  {
    return child.toString();
  }

  @Override
  public String toString()
  {
    return delegate.toString();
  }

  public static CDOCheckoutElement getFor(Object object)
  {
    if (object instanceof Notifier)
    {
      Notifier notifier = (Notifier)object;
      return (CDOCheckoutElement)EcoreUtil.getExistingAdapter(notifier, TYPE);
    }

    return null;
  }

  public static EList<Adapter> removeFrom(Object object)
  {
    if (object instanceof EObject)
    {
      EObject eObject = (EObject)object;
      EList<Adapter> adapters = eObject.eAdapters();

      synchronized (TYPE)
      {
        removeSafe(adapters);
      }

      return adapters;
    }

    return null;
  }

  private static void removeSafe(EList<Adapter> adapters)
  {
    try
    {
      for (Iterator<Adapter> it = adapters.iterator(); it.hasNext();)
      {
        Adapter adapter = it.next();
        if (adapter.isAdapterForType(TYPE))
        {
          it.remove();
        }
      }
    }
    catch (ConcurrentModificationException ex)
    {
      removeSafe(adapters);
    }
  }
}
