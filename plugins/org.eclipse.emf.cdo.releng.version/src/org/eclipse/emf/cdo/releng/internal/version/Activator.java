/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.version;

import org.eclipse.emf.cdo.releng.version.VersionUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Activator extends Plugin
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.releng.version";

  private static final String IGNORED_RELEASES = "ignoredReleases.bin";

  private static final String BUILD_STATES = "buildStates.bin";

  private static Activator plugin;

  private static IResourceChangeListener postBuildListener;

  private static IgnoredReleases ignoredReleases;

  private static BuildStates buildStates;

  public Activator()
  {
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;

    try
    {
      ignoredReleases = load(IGNORED_RELEASES);
    }
    catch (Throwable t)
    {
      //$FALL-THROUGH$
    }
    finally
    {
      if (ignoredReleases == null)
      {
        ignoredReleases = new IgnoredReleases();
      }
    }

    try
    {
      buildStates = load(BUILD_STATES);
    }
    catch (Throwable t)
    {
      //$FALL-THROUGH$
    }
    finally
    {
      File stateFile = getStateFile(BUILD_STATES);
      if (stateFile.exists())
      {
        stateFile.delete(); // Future indication for possible workspace crash
      }

      if (buildStates == null)
      {
        buildStates = new BuildStates();
      }
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    if (postBuildListener != null)
    {
      ResourcesPlugin.getWorkspace().removeResourceChangeListener(postBuildListener);
      postBuildListener = null;
    }

    if (!buildStates.isEmpty())
    {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      for (Iterator<Entry<String, BuildState>> it = buildStates.entrySet().iterator(); it.hasNext();)
      {
        Entry<String, BuildState> entry = it.next();
        String projectName = entry.getKey();
        IProject project = root.getProject(projectName);
        if (!project.exists())
        {
          it.remove();
        }
        else
        {
          BuildState buildState = entry.getValue();
          buildState.serializeValidatorState();
        }
      }

      save(BUILD_STATES, buildStates);
    }

    ignoredReleases = null;
    buildStates = null;
    plugin = null;
    super.stop(context);
  }

  public static void setPostBuildListener(IResourceChangeListener postBuildListener)
  {
    Activator.postBuildListener = postBuildListener;
  }

  public static Set<String> getIgnoredReleases()
  {
    return ignoredReleases;
  }

  public static BuildState getBuildState(IProject project)
  {
    String name = project.getName();
    BuildState buildState = buildStates.get(name);
    if (buildState == null)
    {
      buildState = new BuildState();
      buildStates.put(name, buildState);
    }

    return buildState;
  }

  public static void clearBuildState(IProject project)
  {
    String name = project.getName();
    buildStates.remove(name);
  }

  public static void log(String message)
  {
    plugin.getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
  }

  public static void log(IStatus status)
  {
    plugin.getLog().log(status);
  }

  public static String log(Throwable t)
  {
    IStatus status = getStatus(t);
    log(status);
    return status.getMessage();
  }

  public static IStatus getStatus(Throwable t)
  {
    if (t instanceof CoreException)
    {
      CoreException coreException = (CoreException)t;
      return coreException.getStatus();
    }

    String msg = t.getLocalizedMessage();
    if (msg == null || msg.length() == 0)
    {
      msg = t.getClass().getName();
    }

    return new Status(IStatus.ERROR, PLUGIN_ID, msg, t);
  }

  private static File getStateFile(String name)
  {
    File stateFolder = Platform.getStateLocation(plugin.getBundle()).toFile();
    return new File(stateFolder, name);
  }

  private static <T> T load(String fileName) throws IOException, ClassNotFoundException
  {
    ObjectInputStream stream = null;

    try
    {
      File stateFile = getStateFile(fileName);
      stream = new ObjectInputStream(new FileInputStream(stateFile));

      @SuppressWarnings("unchecked")
      T object = (T)stream.readObject();
      return object;
    }
    finally
    {
      VersionUtil.close(stream);
    }
  }

  private static void save(String fileName, Object object)
  {
    ObjectOutputStream stream = null;

    try
    {
      File file = getStateFile(fileName);
      stream = new ObjectOutputStream(new FileOutputStream(file));
      stream.writeObject(object);
    }
    catch (Throwable ex)
    {
      log(ex);
    }
    finally
    {
      VersionUtil.close(stream);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class IgnoredReleases extends HashSet<String>
  {
    private static final long serialVersionUID = 1L;

    public IgnoredReleases()
    {
    }

    @Override
    public boolean add(String releasePath)
    {
      if (super.add(releasePath))
      {
        save();
        return true;
      }

      return false;
    }

    @Override
    public boolean remove(Object releasePath)
    {
      if (super.remove(releasePath))
      {
        save();
        return true;
      }

      return false;
    }

    private void save()
    {
      Activator.save(IGNORED_RELEASES, ignoredReleases);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class BuildStates extends HashMap<String, BuildState>
  {
    private static final long serialVersionUID = 2L;

    public BuildStates()
    {
    }
  }
}
