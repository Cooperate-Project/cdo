/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Eike Stepper
 */
public class BundlePoolPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  public void init(IWorkbench workbench)
  {
    noDefaultAndApplyButton();
  }

  @Override
  protected Control createContents(Composite parent)
  {
    return new BundlePoolComposite(parent, 0, SWT.NONE);
  }
}
