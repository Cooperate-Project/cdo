/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.presentation.templates;

import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.editor.ProjectTemplate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Eike Stepper
 */
public class SimpleProjectTemplate extends ProjectTemplate
{
  public SimpleProjectTemplate()
  {
    super("simple", "Simple project");
  }

  @Override
  public Control createControl(Composite parent, Container container, Project project)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new FillLayout());

    new Label(composite, SWT.NONE).setText(getLabel());

    return composite;
  }
}
