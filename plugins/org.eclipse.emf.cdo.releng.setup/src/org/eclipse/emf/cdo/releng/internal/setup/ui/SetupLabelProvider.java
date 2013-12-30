/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.SetupTask;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Eike Stepper
 */
public class SetupLabelProvider extends AdapterFactoryLabelProvider.ColorProvider
{
  private final Color DARK_GRAY;

  public SetupLabelProvider(Viewer viewer)
  {
    this(EMFUtil.ADAPTER_FACTORY, viewer);
  }

  public SetupLabelProvider(AdapterFactory adapterFactory, Viewer viewer)
  {
    super(adapterFactory, viewer);
    DARK_GRAY = viewer.getControl().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
  }

  @Override
  public Color getForeground(Object object)
  {
    if (isDisabled(object))
    {
      return DARK_GRAY;
    }

    return super.getForeground(object);
  }

  public static boolean isDisabled(Object object)
  {
    if (object instanceof EObject)
    {
      EObject eObject = (EObject)object;
      if (eObject instanceof SetupTask)
      {
        SetupTask setupTask = (SetupTask)eObject;
        if (setupTask.isDisabled())
        {
          return true;
        }
      }

      EObject eContainer = eObject.eContainer();
      if (eContainer != null)
      {
        return isDisabled(eContainer);
      }
    }

    return false;
  }
}
