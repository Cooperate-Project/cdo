/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemProvider<INPUT> extends StructuredContentProvider<INPUT> implements ITreeContentProvider,
    ILabelProvider, IColorProvider, IFontProvider
{
  public static final Object[] NO_ELEMENTS = {};

  private List<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>(0);

  public ItemProvider()
  {
  }

  public final Object[] getElements(Object parent)
  {
    return getChildren(parent);
  }

  public boolean hasChildren(Object parent)
  {
    return getChildren(parent).length != 0;
  }

  public String getText(Object obj)
  {
    return obj.toString();
  }

  public Image getImage(Object obj)
  {
    String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
    return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
  }

  public Color getBackground(Object element)
  {
    return null;
  }

  public Color getForeground(Object element)
  {
    if (!LifecycleUtil.isActive(element))
    {
      return getDisplay().getSystemColor(SWT.COLOR_GRAY);
    }

    return null;
  }

  public Font getFont(Object element)
  {
    return null;
  }

  public boolean isLabelProperty(Object element, String property)
  {
    return true;
  }

  public ILabelProviderListener[] getListeners()
  {
    synchronized (listeners)
    {
      return listeners.toArray(new ILabelProviderListener[listeners.size()]);
    }
  }

  public void addListener(ILabelProviderListener listener)
  {
    synchronized (listeners)
    {
      listeners.add(listener);
    }
  }

  public void removeListener(ILabelProviderListener listener)
  {
    synchronized (listeners)
    {
      listeners.remove(listener);
    }
  }

  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
  }

  protected void fireLabelProviderChanged()
  {
    fireLabelProviderChanged(new LabelProviderChangedEvent(this));
  }

  protected void fireLabelProviderChanged(Object element)
  {
    fireLabelProviderChanged(new LabelProviderChangedEvent(this, element));
  }

  protected void fireLabelProviderChanged(Object[] elements)
  {
    fireLabelProviderChanged(new LabelProviderChangedEvent(this, elements));
  }

  /**
   * Fires a label provider changed event to all registered listeners Only listeners registered at the time this method
   * is called are notified.
   * 
   * @param event
   *          a label provider changed event
   * @see ILabelProviderListener#labelProviderChanged
   */
  private void fireLabelProviderChanged(LabelProviderChangedEvent event)
  {
    for (ILabelProviderListener listener : getListeners())
    {
      try
      {
        listener.labelProviderChanged(event);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }
}