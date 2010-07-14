/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version;

import org.eclipse.emf.cdo.releng.version.Release.Element;
import org.eclipse.emf.cdo.releng.version.Release.Element.Type;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import org.osgi.framework.Version;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class VersionBuilder extends IncrementalProjectBuilder
{
  public static final String BUILDER_ID = "org.eclipse.emf.cdo.releng.version.VersionBuilder";

  public static final String RELEASE_PATH_ARGUMENT = "release.path";

  public static final String VALIDATOR_CLASS_ARGUMENT = "validator.class";

  public static boolean DEBUG = false;

  private static final Path MANIFEST_PATH = new Path("META-INF/MANIFEST.MF");

  private Release release;

  public VersionBuilder()
  {
  }

  @Override
  protected final IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
      throws CoreException
  {
    IProject project = getProject();
    IProject[] releaseProject = null;

    BuildState buildState = Activator.getBuildState(project);
    boolean fullBuild = buildState.getReleaseTag() == null;
    VersionValidator validator = null;

    monitor.beginTask(null, 1);

    try
    {
      Markers.deleteAllMarkers(project);

      IPluginModelBase pluginModel = PluginRegistry.findModel(getProject());
      if (pluginModel == null)
      {
        throw new IllegalStateException("Could not locate the plugin model base for project: " + getProject().getName());
      }

      checkDependencyRanges(pluginModel);
      checkPackageExports(pluginModel);

      /*
       * Determine validator to use
       */

      IFile projectDescription = project.getFile(new Path(".project"));
      String validatorClass = (String)args.get(VALIDATOR_CLASS_ARGUMENT);
      if (validatorClass == null)
      {
        validatorClass = "org.eclipse.emf.cdo.releng.version.digest.DigestValidator$BuildModel";
      }

      try
      {
        Class<?> c = Class.forName(validatorClass, true, VersionBuilder.class.getClassLoader());
        validator = (VersionValidator)c.newInstance();
      }
      catch (Exception ex)
      {
        String msg = ex.getLocalizedMessage() + ": " + validatorClass;
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR, ".*(" + validatorClass + ").*");
        return releaseProject;
      }

      trace(validator.getClass().getName() + ": " + project.getName());

      /*
       * Determine release data to validate against
       */

      String releasePath = (String)args.get(RELEASE_PATH_ARGUMENT);
      if (releasePath == null)
      {
        validator.abort(buildState, project, null, monitor);
        String msg = "Build command argument missing: " + RELEASE_PATH_ARGUMENT;
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR, ".*(" + BUILDER_ID + ").*");
        return releaseProject;
      }

      try
      {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(releasePath));
        releaseProject = new IProject[] { file.getProject() };

        Release release = ReleaseManager.INSTANCE.getRelease(file);
        boolean releaseHasChanged = !release.getTag().equals(buildState.getReleaseTag());
        if (releaseHasChanged)
        {
          buildState.setReleaseTag(release.getTag());
          fullBuild = true;
        }

        this.release = release;
      }
      catch (CoreException ex)
      {
        String msg = "Problem with release spec: " + releasePath;
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR, releasePath);
        validator.abort(buildState, project, null, monitor);
        return releaseProject;
      }

      /*
       * Determine if a validation is needed or if the version has already been increased properly
       */

      Element element = getElement(pluginModel);
      Element releaseElement = release.getElements().get(element.getName());
      if (releaseElement == null)
      {
        validator.abort(buildState, project, null, monitor);
        trace("Project has not been released: " + project.getName());
        return releaseProject;
      }

      Version elementVersion = element.getVersion();
      Version releaseVersion = releaseElement.getVersion();
      Version nextVersion = new Version(releaseVersion.getMajor(), releaseVersion.getMinor(), releaseVersion.getMicro()
          + (release.isIntegration() ? 100 : 1));
      int comparison = releaseVersion.compareTo(elementVersion);
      if (comparison < 0)
      {
        if (!nextVersion.equals(elementVersion))
        {
          if (elementVersion.getMajor() == nextVersion.getMajor()
              && elementVersion.getMinor() == nextVersion.getMinor())
          {
            addVersionMarker("Version should be " + nextVersion);
          }
        }

        validator.abort(buildState, project, null, monitor);
        return releaseProject;
      }

      if (comparison > 0)
      {
        validator.abort(buildState, project, null, monitor);
        addVersionMarker("Version has been decreased after release " + releaseVersion);
        return releaseProject;
      }

      /*
       * Do the validation
       */

      IResourceDelta delta = null;
      fullBuild |= kind == FULL_BUILD;
      if (!fullBuild)
      {
        delta = getDelta(project);
      }

      validator.updateBuildState(buildState, releasePath, release, project, delta, pluginModel, monitor);

      try
      {
        if (buildState.isChangedSinceRelease())
        {
          addVersionMarker("Version must be increased to " + nextVersion);
        }
      }
      catch (Exception ignore)
      {
        Activator.log(ignore);
      }
    }
    catch (Exception ex)
    {
      try
      {
        if (validator != null)
        {
          validator.abort(buildState, project, ex, monitor);
        }

        String msg = Activator.log(ex);
        Markers.addMarker(project, msg);
      }
      catch (Exception ignore)
      {
        Activator.log(ignore);
      }
    }
    finally
    {
      monitor.done();
    }

    return releaseProject;
  }

  private Element getElement(IPluginModelBase pluginModel) throws CoreException
  {
    BundleDescription description = pluginModel.getBundleDescription();
    String name = description.getSymbolicName();
    Version version = description.getVersion();
    return new Element(name, version, Type.PLUGIN);
  }

  private void checkDependencyRanges(IPluginModelBase pluginModel) throws CoreException, IOException
  {
    BundleDescription description = pluginModel.getBundleDescription();
    for (BundleSpecification requiredBundle : description.getRequiredBundles())
    {
      VersionRange range = requiredBundle.getVersionRange();
      if (isUnspecified(range.getMaximum()))
      {
        addRequireMarker(requiredBundle.getName(), "dependency must specify a version range");
      }
      else
      {
        if (!range.getIncludeMinimum())
        {
          addRequireMarker(requiredBundle.getName(), "dependency range must include the minimum");
        }

        if (range.getIncludeMaximum())
        {
          addRequireMarker(requiredBundle.getName(), "dependency range must not include the maximum");
        }
      }
    }

    for (ImportPackageSpecification importPackage : description.getImportPackages())
    {
      VersionRange range = importPackage.getVersionRange();
      if (isUnspecified(range.getMaximum()))
      {
        addImportMarker(importPackage.getName(), "dependency must specify a version range");
      }
      else
      {
        if (!range.getIncludeMinimum())
        {
          addImportMarker(importPackage.getName(), "dependency range must include the minimum");
        }

        if (range.getIncludeMaximum())
        {
          addImportMarker(importPackage.getName(), "dependency range must not include the maximum");
        }
      }
    }
  }

  private boolean isUnspecified(Version version)
  {
    if (version.getMajor() != Integer.MAX_VALUE)
    {
      return false;
    }

    if (version.getMinor() != Integer.MAX_VALUE)
    {
      return false;
    }

    if (version.getMicro() != Integer.MAX_VALUE)
    {
      return false;
    }

    return true;
  }

  private void checkPackageExports(IPluginModelBase pluginModel) throws CoreException, IOException
  {
    BundleDescription description = pluginModel.getBundleDescription();
    String bundleName = description.getSymbolicName();
    Version bundleVersion = Release.normalizeVersion(description.getVersion());

    for (ExportPackageDescription packageExport : description.getExportPackages())
    {
      String packageName = packageExport.getName();
      if (isBundlePackage(packageName, bundleName))
      {
        Version packageVersion = packageExport.getVersion();
        if (packageVersion != null && !packageVersion.equals(Version.emptyVersion)
            && !packageVersion.equals(bundleVersion))
        {
          addExportMarker(packageName);
        }
      }
    }
  }

  private boolean isBundlePackage(String packageName, String bundleName)
  {
    if (packageName.startsWith(bundleName))
    {
      return true;
    }

    int lastDot = bundleName.lastIndexOf('.');
    if (lastDot != -1)
    {
      String bundleStart = bundleName.substring(0, lastDot);
      String bundleEnd = bundleName.substring(lastDot + 1);
      if (packageName.startsWith(bundleStart + ".internal." + bundleEnd))
      {
        return true;
      }

      if (packageName.startsWith(bundleStart + ".spi." + bundleEnd))
      {
        return true;
      }
    }

    return false;
  }

  private void addRequireMarker(String name, String message) throws CoreException, IOException
  {
    IFile file = getProject().getFile(MANIFEST_PATH);
    String regex = ".* " + name.replaceAll("\\.", "\\\\.") + ";bundle-version=\"([^\\\"]*)\".*";
    Markers.addMarker(file, "'" + name + "' " + message, IMarker.SEVERITY_ERROR, regex);
  }

  private void addImportMarker(String name, String message) throws CoreException, IOException
  {
    IFile file = getProject().getFile(MANIFEST_PATH);
    String regex = ".* " + name.replaceAll("\\.", "\\\\.") + ";version=\"([^\\\"]*)\".*";
    Markers.addMarker(file, "'" + name + "' " + message, IMarker.SEVERITY_ERROR, regex);
  }

  private void addExportMarker(String name) throws CoreException, IOException
  {
    IFile file = getProject().getFile(MANIFEST_PATH);
    String message = "'" + name + "' export has wrong version information";
    String regex = ".* " + name.replaceAll("\\.", "\\\\.") + ";version=\"([0123456789\\.]*)\".*";
    Markers.addMarker(file, message, IMarker.SEVERITY_ERROR, regex);
  }

  private void addVersionMarker(String message) throws CoreException, IOException
  {
    IFile file = getProject().getFile(MANIFEST_PATH);
    String regex = "Bundle-Version: *([^ ]*)";
    Markers.addMarker(file, message, IMarker.SEVERITY_ERROR, regex);
  }

  public static void trace(String msg)
  {
    if (DEBUG)
    {
      System.out.println(msg);
    }
  }
}
