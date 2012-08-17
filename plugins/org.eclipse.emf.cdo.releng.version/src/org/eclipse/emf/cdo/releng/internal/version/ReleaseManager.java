/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.version;

import org.eclipse.emf.cdo.releng.version.IElement;
import org.eclipse.emf.cdo.releng.version.IElement.Type;
import org.eclipse.emf.cdo.releng.version.IRelease;
import org.eclipse.emf.cdo.releng.version.IReleaseManager;
import org.eclipse.emf.cdo.releng.version.VersionUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import org.osgi.framework.Version;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class ReleaseManager implements IReleaseManager
{
  private Map<IRelease, Long> releases = new WeakHashMap<IRelease, Long>();

  private SAXParserFactory parserFactory;

  public ReleaseManager()
  {
  }

  private SAXParser getParser() throws ParserConfigurationException, SAXException
  {
    if (parserFactory == null)
    {
      parserFactory = SAXParserFactory.newInstance();
    }

    return parserFactory.newSAXParser();
  }

  public synchronized IRelease getRelease(IFile file) throws CoreException
  {
    try
    {
      for (Entry<IRelease, Long> entry : releases.entrySet())
      {
        IRelease release = entry.getKey();
        if (release.getFile().equals(file))
        {
          long timeStamp = entry.getValue();
          if (file.getLocalTimeStamp() == timeStamp)
          {
            return release;
          }

          releases.remove(release);
          break;
        }
      }

      if (!file.exists())
      {
        throw new FileNotFoundException(file.getFullPath().toString());
      }

      IRelease release = new Release(getParser(), file);
      releases.put(release, file.getLocalTimeStamp());
      return release;
    }
    catch (CoreException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex));
    }
  }

  public synchronized IRelease createRelease(IFile file) throws CoreException, IOException, NoSuchAlgorithmException
  {
    Release release = new Release(file);
    String path = file.getFullPath().toString();

    Map<IElement, IElement> elements = release.getElements();
    for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
    {
      if (project.isOpen())
      {
        VersionBuilderArguments args = new VersionBuilderArguments(project);
        String releasePath = args.getReleasePath();
        if (path.equals(releasePath))
        {
          IModel componentModel = VersionUtil.getComponentModel(project);
          IElement element = createElement(componentModel, true);
          elements.put(element, element);
        }
      }
    }

    Set<IElement> keySet = elements.keySet();
    ArrayList<IElement> queue = new ArrayList<IElement>(keySet);
    for (int i = 0; i < queue.size(); i++)
    {
      IElement element = queue.get(i);
      for (IElement child : element.getChildren())
      {
        if (!elements.containsKey(child))
        {
          IModel childModel = getComponentModel(child);
          if (childModel != null)
          {
            IElement topElement = createElement(childModel, true);
            queue.add(topElement);
            elements.put(topElement, topElement);
          }
          else
          {
            elements.put(child, child);
          }
        }
      }
    }

    release.write();
    releases.put(release, file.getLocalTimeStamp());
    return release;
  }

  public IElement createElement(IModel componentModel, boolean withFeatureContent)
  {
    if (componentModel instanceof IPluginModelBase)
    {
      IPluginModelBase pluginModel = (IPluginModelBase)componentModel;
      BundleDescription description = pluginModel.getBundleDescription();
      if (description == null)
      {
        throw new IllegalStateException("No bundle description for " + pluginModel.getInstallLocation());
      }

      String name = description.getSymbolicName();
      Version version = description.getVersion();
      return new Element(Type.PLUGIN, name, version);
    }

    return createFeatureElement(componentModel, withFeatureContent);
  }

  @SuppressWarnings("restriction")
  private IElement createFeatureElement(IModel componentModel, boolean withContent)
  {
    org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel = (org.eclipse.pde.internal.core.ifeature.IFeatureModel)componentModel;
    org.eclipse.pde.internal.core.ifeature.IFeature feature = featureModel.getFeature();

    String name = feature.getId();
    Version version = new Version(feature.getVersion());
    IElement element = new Element(Type.FEATURE, name, version);

    if (withContent)
    {
      String licenseFeatureID = feature.getLicenseFeatureID();
      if (licenseFeatureID.length() != 0)
      {
        IElement child = new Element(IElement.Type.FEATURE, licenseFeatureID, feature.getLicenseFeatureVersion());
        element.getChildren().add(child);
      }

      for (org.eclipse.pde.internal.core.ifeature.IFeatureChild versionable : feature.getIncludedFeatures())
      {
        IElement child = new Element(IElement.Type.FEATURE, versionable.getId(), versionable.getVersion());
        element.getChildren().add(child);
      }

      for (org.eclipse.pde.internal.core.ifeature.IFeaturePlugin versionable : feature.getPlugins())
      {
        IElement child = new Element(IElement.Type.PLUGIN, versionable.getId(), versionable.getVersion());
        element.getChildren().add(child);
      }
    }

    return element;
  }

  @SuppressWarnings("restriction")
  public IModel getComponentModel(IElement element)
  {
    String name = element.getName();
    if (element.getType() == IElement.Type.PLUGIN)
    {
      IModel pluginModel = PluginRegistry.findModel(name);
      if (pluginModel != null)
      {
        return pluginModel;
      }
    }

    org.eclipse.pde.internal.core.FeatureModelManager manager = org.eclipse.pde.internal.core.PDECore.getDefault()
        .getFeatureModelManager();
    org.eclipse.pde.internal.core.ifeature.IFeatureModel[] featureModels = manager.getWorkspaceModels();

    org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel = getFeatureModel(name, featureModels);
    if (featureModel == null)
    {
      featureModels = manager.getExternalModels();
      featureModel = getFeatureModel(name, featureModels);
    }

    return featureModel;
  }

  @SuppressWarnings("restriction")
  private org.eclipse.pde.internal.core.ifeature.IFeatureModel getFeatureModel(String name,
      org.eclipse.pde.internal.core.ifeature.IFeatureModel[] featureModels)
  {
    Version highestVersion = null;
    org.eclipse.pde.internal.core.ifeature.IFeatureModel highestModel = null;

    for (org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel : featureModels)
    {
      org.eclipse.pde.internal.core.ifeature.IFeature feature = featureModel.getFeature();
      String id = feature.getId();
      if (id.equals(name))
      {
        Version newVersion = new Version(feature.getVersion());
        if (highestVersion == null || highestVersion.compareTo(newVersion) < 0)
        {
          highestVersion = newVersion;
          highestModel = featureModel;
        }
      }
    }

    if (highestModel == null)
    {
      return null;
    }

    return highestModel;
  }
}
