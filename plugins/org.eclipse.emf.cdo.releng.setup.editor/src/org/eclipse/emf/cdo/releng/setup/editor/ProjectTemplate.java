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
package org.eclipse.emf.cdo.releng.setup.editor;

import org.eclipse.emf.cdo.releng.internal.setup.AdditionalRequirementsGenerator;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public abstract class ProjectTemplate
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.releng.setup.projectTemplates";

  private final String label;

  private final String description;

  private final Project project;

  private Container container;

  protected ProjectTemplate(String label, String description)
  {
    this.label = label;
    this.description = StringUtil.safe(description);

    project = SetupFactory.eINSTANCE.createProject();
    project.eAdapters().add(new AdditionalRequirementsGenerator());
  }

  protected ProjectTemplate(String label)
  {
    this(label, null);
  }

  public final String getLabel()
  {
    return label;
  }

  public final String getDescription()
  {
    return description;
  }

  public final Project getProject()
  {
    return getProject(false);
  }

  public final Project getProject(boolean clear)
  {
    if (clear)
    {
      project.getSetupTasks().clear();
      project.getBranches().clear();
    }

    return project;
  }

  public Container getContainer()
  {
    return container;
  }

  public void init(Container container)
  {
    this.container = container;
  }

  @Override
  public final String toString()
  {
    return getLabel();
  }

  public boolean isValid(Project project)
  {
    return !project.getBranches().isEmpty();
  }

  public boolean isValid(Branch branch)
  {
    return !StringUtil.isEmpty(branch.getName());
  }

  public abstract Control createControl(Composite parent);

  protected final Branch addBranch()
  {
    return addBranch(null);
  }

  protected final Branch addBranch(String name)
  {
    Branch branch = SetupFactory.eINSTANCE.createBranch();
    branch.setName(name);

    project.getBranches().add(branch);
    return branch;
  }

  public static boolean contains(Collection<?> collection, Class<?> type)
  {
    for (Object object : collection)
    {
      if (type.isInstance(object))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  public interface Container
  {
    public TreeViewer getPreViewer();

    public TableViewer getPropertiesViewer();

    public void validate();
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    /**
     * @deprecated Use {@link #createProjectTemplate()} instead.
     */
    @Deprecated
    public final Object create(String description) throws ProductCreationException
    {
      throw new ProductCreationException();
    }

    public abstract ProjectTemplate createProjectTemplate();
  }
}
