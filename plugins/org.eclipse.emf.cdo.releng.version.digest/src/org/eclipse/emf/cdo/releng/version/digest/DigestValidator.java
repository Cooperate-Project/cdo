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
package org.eclipse.emf.cdo.releng.version.digest;

import org.eclipse.emf.cdo.releng.version.IBuildState;
import org.eclipse.emf.cdo.releng.version.IElement;
import org.eclipse.emf.cdo.releng.version.IRelease;
import org.eclipse.emf.cdo.releng.version.IReleaseManager;
import org.eclipse.emf.cdo.releng.version.VersionUtil;
import org.eclipse.emf.cdo.releng.version.VersionValidator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.build.IBuild;
import org.eclipse.pde.core.build.IBuildEntry;
import org.eclipse.pde.core.build.IBuildModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;

import org.osgi.framework.Version;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class DigestValidator extends VersionValidator
{
  private static final Map<IRelease, ReleaseDigest> RELEASE_DIGESTS = new WeakHashMap<IRelease, ReleaseDigest>();

  public DigestValidator()
  {
  }

  @Override
  public void updateBuildState(IBuildState buildState, IRelease release, IProject project, IResourceDelta delta,
      IModel componentModel, IProgressMonitor monitor) throws Exception
  {
    DigestValidatorState validatorState = (DigestValidatorState)buildState.getValidatorState();
    IPath releasePath = release.getFile().getFullPath();
    ReleaseDigest releaseDigest = getReleaseDigest(releasePath, release, monitor);

    // Check whether the release digest to use is still the one that has been used the for the last build.
    long timeStamp = releaseDigest.getTimeStamp();
    if (timeStamp != buildState.getValidatorTimeStamp())
    {
      // Trigger full build if the release digest to use is different from the one used for the last build
      delta = null;

      // Avoid triggering full builds after this (full) build
      buildState.setValidatorTimeStamp(timeStamp);
    }

    beforeValidation(validatorState, componentModel);
    if (validatorState == null || delta == null)
    {
      if (VersionUtil.DEBUG)
      {
        System.out.println("Digest: Full validation...");
      }

      buildState.setValidatorState(null);
      validatorState = validateFull(project, null, componentModel, monitor);
    }
    else
    {
      if (VersionUtil.DEBUG)
      {
        System.out.println("Digest: Incremental validation...");
      }

      validatorState = validateDelta(delta, validatorState, componentModel, monitor);
    }

    afterValidation(validatorState);
    if (validatorState == null)
    {
      throw new IllegalStateException("No validation state");
    }

    byte[] validatorDigest = validatorState.getDigest();
    if (VersionUtil.DEBUG)
    {
      System.out.println("DIGEST  = " + formatDigest(validatorDigest));
    }

    byte[] releasedProjectDigest = releaseDigest.get(project.getName());
    if (VersionUtil.DEBUG)
    {
      System.out.println("RELEASE = " + formatDigest(releasedProjectDigest));
    }

    boolean changedSinceRelease = !MessageDigest.isEqual(validatorDigest, releasedProjectDigest);
    buildState.setChangedSinceRelease(changedSinceRelease);
    buildState.setValidatorState(validatorState);
  }

  public DigestValidatorState validateFull(IResource resource, DigestValidatorState parentState, IModel componentModel,
      IProgressMonitor monitor) throws Exception
  {
    if (resource.getType() != IResource.PROJECT && !isConsidered(resource))
    {
      return null;
    }

    if (VersionUtil.DEBUG)
    {
      System.out.println("Digest: " + resource.getFullPath());
    }

    DigestValidatorState result = new DigestValidatorState();
    result.setName(resource.getName());
    result.setParent(parentState);

    if (resource instanceof IContainer)
    {
      IContainer container = (IContainer)resource;
      List<DigestValidatorState> memberStates = new ArrayList<DigestValidatorState>();
      for (IResource member : container.members())
      {
        DigestValidatorState memberState = validateFull(member, result, componentModel, monitor);
        if (memberState != null)
        {
          memberStates.add(memberState);
        }
      }

      byte[] digest = getFolderDigest(memberStates);
      if (VersionUtil.DEBUG)
      {
        System.out.println("Considered: " + container.getFullPath() + " --> " + formatDigest(digest));
      }

      result.setDigest(digest);
      result.setChildren(memberStates.toArray(new DigestValidatorState[memberStates.size()]));
    }
    else
    {
      IFile file = (IFile)resource;
      byte[] digest = getFileDigest(file);
      if (VersionUtil.DEBUG)
      {
        System.out.println("Considered: " + file.getFullPath() + " --> " + formatDigest(digest));
      }

      result.setDigest(digest);
    }

    return result;
  }

  public DigestValidatorState validateDelta(IResourceDelta delta, DigestValidatorState validatorState,
      IModel componentModel, IProgressMonitor monitor) throws Exception
  {
    IResource resource = delta.getResource();
    if (!resource.exists() || resource.getType() != IResource.PROJECT && !isConsidered(resource))
    {
      return null;
    }

    DigestValidatorState result = validatorState;
    switch (delta.getKind())
    {
    case IResourceDelta.ADDED:
      result = new DigestValidatorState();
      result.setName(resource.getName());

      //$FALL-THROUGH$
    case IResourceDelta.CHANGED:
      if (resource instanceof IContainer)
      {
        Set<DigestValidatorState> memberStates = new HashSet<DigestValidatorState>();
        for (IResourceDelta memberDelta : delta.getAffectedChildren())
        {
          IResource memberResource = memberDelta.getResource();
          DigestValidatorState memberState = validatorState.getChild(memberResource.getName());
          DigestValidatorState newMemberState = validateDelta(memberDelta, memberState, componentModel, monitor);
          if (newMemberState != null)
          {
            newMemberState.setParent(result);
            memberStates.add(newMemberState);
          }
        }

        IContainer container = (IContainer)resource;
        for (DigestValidatorState oldChild : validatorState.getChildren())
        {
          IResource member = container.findMember(oldChild.getName());
          if (member != null)
          {
            memberStates.add(oldChild);
          }
        }

        byte[] digest = getFolderDigest(memberStates);
        result.setDigest(digest);
        result.setChildren(memberStates.toArray(new DigestValidatorState[memberStates.size()]));
        // VersionBuilder.trace("   " + delta.getFullPath() + "  -->  " + TestResourceChangeListener.getKind(delta) +
        // " " + TestResourceChangeListener.getFlags(delta));
      }
      else
      {
        boolean changed = result == validatorState;
        if (changed && (delta.getFlags() & IResourceDelta.CONTENT) == 0)
        {
          return validatorState;
        }

        IFile file = (IFile)resource;
        byte[] digest = getFileDigest(file);
        result.setDigest(digest);
        // VersionBuilder.trace("   " + delta.getFullPath() + "  -->  " + TestResourceChangeListener.getKind(delta) +
        // " " + TestResourceChangeListener.getFlags(delta));
      }

      break;

    case IResourceDelta.REMOVED:
      result = null;
    }

    return result;
  }

  protected boolean isConsidered(IResource resource)
  {
    return !resource.isDerived();
  }

  protected void beforeValidation(DigestValidatorState validatorState, IModel componentModel) throws Exception
  {
  }

  protected void afterValidation(DigestValidatorState validatorState) throws Exception
  {
  }

  private ReleaseDigest getReleaseDigest(IPath releasePath, IRelease release, IProgressMonitor monitor)
      throws IOException, CoreException, ClassNotFoundException
  {
    IFile file = getDigestFile(releasePath);
    long localTimeStamp = file.getLocalTimeStamp();

    ReleaseDigest releaseDigest = RELEASE_DIGESTS.get(release);
    if (releaseDigest != null)
    {
      // Check whether digest file has changed on disk.
      if (localTimeStamp != releaseDigest.getTimeStamp())
      {
        // Digest file has changed. Reload it further down.
        releaseDigest = null;
      }
    }

    if (releaseDigest == null)
    {
      if (file.exists())
      {
        releaseDigest = readDigestFile(file);

        // Check whether the digest file on disk (still) matches the release spec
        if (!MessageDigest.isEqual(releaseDigest.getReleaseSpecDigest(), release.getDigest()))
        {
          releaseDigest = null;
        }
      }

      if (releaseDigest == null)
      {
        releaseDigest = createReleaseDigest(release, file, null, monitor);
      }

      releaseDigest.setTimeStamp(localTimeStamp);
      RELEASE_DIGESTS.put(release, releaseDigest);
    }

    return releaseDigest;
  }

  private byte[] getFolderDigest(Collection<DigestValidatorState> states) throws Exception
  {
    List<DigestValidatorState> list = new ArrayList<DigestValidatorState>(states);
    Collections.sort(list);

    MessageDigest digest = MessageDigest.getInstance("SHA");
    for (DigestValidatorState state : list)
    {
      byte[] bytes = state.getDigest();
      if (bytes != null)
      {
        digest.update(state.getName().getBytes());
        digest.update(bytes);
      }
    }

    return digest.digest();
  }

  private byte[] getFileDigest(IFile file) throws Exception
  {
    return VersionUtil.getSHA1(file);
  }

  private String formatDigest(byte[] digest)
  {
    StringBuilder builder = new StringBuilder();
    for (byte b : digest)
    {
      if (builder.length() != 0)
      {
        builder.append(", ");
      }

      builder.append("(byte)");
      builder.append(b);
    }

    return builder.toString();
  }

  private ReleaseDigest readDigestFile(IFile file) throws IOException, CoreException, ClassNotFoundException
  {
    ObjectInputStream stream = null;

    try
    {
      stream = new ObjectInputStream(file.getContents());
      return (ReleaseDigest)stream.readObject();
    }
    finally
    {
      if (stream != null)
      {
        try
        {
          stream.close();
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    }
  }

  private void writeReleaseDigest(ReleaseDigest releaseDigest, IFile target, IProgressMonitor monitor)
      throws IOException, CoreException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(releaseDigest);
    oos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    if (target.exists())
    {
      int i = 1;
      for (;;)
      {
        try
        {
          target.move(target.getFullPath().addFileExtension("bak" + i), true, monitor);
          break;
        }
        catch (Exception ex)
        {
          ++i;
        }
      }
    }

    target.create(bais, true, monitor);
    monitor.worked(1);
  }

  private void addWarning(List<String> warnings, String msg)
  {
    Activator.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, msg));
    if (warnings != null)
    {
      warnings.add(msg);
    }
  }

  public ReleaseDigest createReleaseDigest(IRelease release, IFile target, List<String> warnings,
      IProgressMonitor monitor) throws CoreException
  {
    monitor.beginTask(null, release.getSize() + 1);

    try
    {
      ReleaseDigest releaseDigest = new ReleaseDigest(release.getDigest());
      for (Entry<IElement, IElement> entry : release.getElements().entrySet())
      {
        String name = entry.getKey().getName();
        monitor.subTask(name);

        try
        {
          try
          {
            IElement element = entry.getValue();
            if (element.getName().endsWith(".source"))
            {
              continue;
            }

            IModel componentModel = IReleaseManager.INSTANCE.getComponentModel(element);
            if (componentModel == null)
            {
              addWarning(warnings, name + ": Component not found");
              continue;
            }

            IResource resource = componentModel.getUnderlyingResource();
            if (resource == null)
            {
              String type = componentModel instanceof IPluginModelBase ? "Plug-in" : "Feature";
              addWarning(warnings, name + ": " + type + " is not in workspace");
              continue;
            }

            Version version = VersionUtil.getComponentVersion(componentModel);

            if (!element.getVersion().equals(version))
            {
              String type = componentModel instanceof IPluginModelBase ? "Plug-in" : "Feature";
              addWarning(warnings, name + ": " + type + " version is not " + element.getVersion());
            }

            IProject project = resource.getProject();

            beforeValidation(null, componentModel);
            DigestValidatorState state = validateFull(project, null, componentModel, monitor);
            afterValidation(state);

            releaseDigest.put(state.getName(), state.getDigest());
          }
          finally
          {
            monitor.worked(1);
          }
        }
        catch (Exception ex)
        {
          addWarning(warnings, name + ": " + Activator.getStatus(ex).getMessage());
        }
      }

      writeReleaseDigest(releaseDigest, target, monitor);
      return releaseDigest;
    }
    catch (CoreException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new CoreException(Activator.getStatus(ex));
    }
    finally
    {
      monitor.done();
    }
  }

  public static IFile getDigestFile(IPath releasePath)
  {
    return VersionUtil.getFile(releasePath, "digest");
  }

  /**
   * @author Eike Stepper
   */
  public static class BuildModel extends DigestValidator
  {
    private Set<String> considered = new HashSet<String>();

    public BuildModel()
    {
    }

    @Override
    protected void beforeValidation(DigestValidatorState validatorState, IModel componentModel) throws Exception
    {
      considered.clear();
      considered.add("");

      IBuild build = getBuild(componentModel);
      IBuildEntry binIncludes = build.getEntry(IBuildEntry.BIN_INCLUDES);
      if (binIncludes != null)
      {
        for (String binInclude : binIncludes.getTokens())
        {
          IBuildEntry sources = build.getEntry("source." + binInclude);
          if (sources != null)
          {
            for (String source : sources.getTokens())
            {
              consider(source);
            }
          }
          else
          {
            consider(binInclude);
          }
        }
      }
    }

    @Override
    protected void afterValidation(DigestValidatorState validatorState) throws Exception
    {
      considered.clear();
    }

    @Override
    protected boolean isConsidered(IResource resource)
    {
      IPath path = resource.getProjectRelativePath();
      while (!path.isEmpty())
      {
        if (considered.contains(path.toString()))
        {
          return true;
        }

        path = path.removeLastSegments(1);
      }

      return false;
    }

    @SuppressWarnings("restriction")
    private IBuild getBuild(IModel componentModel) throws CoreException
    {
      IProject project = componentModel.getUnderlyingResource().getProject();
      IFile buildFile = org.eclipse.pde.internal.core.project.PDEProject.getBuildProperties(project);

      IBuildModel buildModel = null;
      if (buildFile.exists())
      {
        buildModel = new org.eclipse.pde.internal.core.build.WorkspaceBuildModel(buildFile);
        buildModel.load();
      }

      if (buildModel == null)
      {
        throw new IllegalStateException("Could not determine build model for " + getName(componentModel));
      }

      IBuild build = buildModel.getBuild();
      if (build == null)
      {
        throw new IllegalStateException("Could not determine build model for " + getName(componentModel));
      }

      return build;
    }

    @SuppressWarnings("restriction")
    private String getName(IModel componentModel)
    {
      if (componentModel instanceof IPluginModelBase)
      {
        IPluginModelBase pluginModel = (IPluginModelBase)componentModel;
        return pluginModel.getBundleDescription().getSymbolicName();
      }

      return ((org.eclipse.pde.internal.core.ifeature.IFeatureModel)componentModel).getFeature().getId();
    }

    private void consider(String path)
    {
      if (path.endsWith("/"))
      {
        path = path.substring(0, path.length() - 1);
      }

      considered.add(path);
    }
  }
}
