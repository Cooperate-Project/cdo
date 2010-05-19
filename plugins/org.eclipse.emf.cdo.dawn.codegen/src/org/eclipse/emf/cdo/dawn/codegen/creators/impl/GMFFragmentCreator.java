/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.creators.impl;

import org.eclipse.emf.cdo.dawn.codegen.actions.GenerateDawnGenModelAction;
import org.eclipse.emf.cdo.dawn.codegen.creators.Creator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.util.ProjectCreationHelper;
import org.eclipse.emf.cdo.dawn.codegen.util.Utils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.mwe.core.WorkflowRunner;
import org.eclipse.emf.mwe.core.monitor.NullProgressMonitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Fluegge
 */
public class GMFFragmentCreator implements Creator
{
  private final IResource selectedElement;

  private final String PLUGIN_NATURE = "org.eclipse.pde.PluginNature";

  private final String JAVA_NATURE = JavaCore.NATURE_ID;

  public GMFFragmentCreator(IResource selectedElement)
  {
    this.selectedElement = selectedElement;
  }

  public void create(IProgressMonitor monitor)
  {
    IFile dawnGenModelFile = (IFile)selectedElement;

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
        GenerateDawnGenModelAction.dawngenmodelFileExtension, new XMIResourceFactoryImpl());

    Resource dawnGenModelResource = resourceSet.getResource(URI.createURI(dawnGenModelFile.getRawLocationURI()
        .toString()), true);

    DawnGMFGenerator dawnGMFGenerator = (DawnGMFGenerator)dawnGenModelResource.getContents().get(0);

    monitor.subTask("Create client fragment's basic structure");

    // String[] natures = new String[] { JavaCore.NATURE_ID, PDE.PLUGIN_NATURE };
    String[] natures = new String[] { JAVA_NATURE, PLUGIN_NATURE };

    ProjectCreationHelper projectCreationHelper = new ProjectCreationHelper();
    projectCreationHelper.setName(dawnGMFGenerator.getFragmentName());
    projectCreationHelper.setNatures(natures);
    IProject project;
    try
    {
      project = projectCreationHelper.createProject();
      projectCreationHelper.createJavaProject(project);

      monitor.worked(100);

      monitor.subTask("Creating folder structure...");
      projectCreationHelper.createFolder("lib", project);
      projectCreationHelper.createFolder("META-INF", project);
      projectCreationHelper.createFolder("icons", project);

      monitor.worked(100);

      monitor.subTask("Creating generic content...");
      createGenericContent(monitor, project.getLocation());
      ProjectCreationHelper.refreshProject(project, monitor);
    }
    catch (CoreException e1)
    {
      e1.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      e.printStackTrace();
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }

    monitor.worked(100);

  }

  private void createGenericContent(IProgressMonitor monitor, IPath location)
  {
    IFile file = (IFile)selectedElement;
    Map<String, String> properties = new HashMap<String, String>();

    Map<String, ?> slotMap = new HashMap<String, Object>();
    try
    {
      String dawnGenFile = file.getRawLocationURI().toString();// ;file.getLocation().toFile().getAbsoluteFile().toURI().toURL().toString();

      IFile dawnGen = (IFile)selectedElement;
      IProject hostProject = dawnGen.getProject();

      Utils.setPackage(hostProject.getName());

      String ouputFolder = location.toFile().getAbsoluteFile().toString();// + "/" + hostProject.getName()+".diagram";

      properties.put("model", dawnGenFile);
      properties.put("src-gen", ouputFolder);

      URL workFlowURL = this.getClass().getClassLoader().getResource("/workflow/gmfFragmentGenerator.oaw");
      WorkflowRunner workflowRunner = new WorkflowRunner();
      String workflow = FileLocator.toFileURL(workFlowURL).getFile();

      workflowRunner.run(workflow, new NullProgressMonitor(), properties, slotMap);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
