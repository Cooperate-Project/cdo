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
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogMonitor;

import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.core.target.LoadTargetDefinitionJob;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public final class TargetPlatformUtil
{
  public static void setTargetPlatform(String targetPlatformPath, String name, boolean active, ProgressLog progress)
      throws Exception
  {
    ITargetPlatformService service = null;

    try
    {
      service = ServiceUtil.getService(ITargetPlatformService.class);
      ITargetDefinition target = null;
      for (ITargetHandle targetHandle : service.getTargets(new ProgressLogMonitor(progress)))
      {
        ITargetDefinition candidate = targetHandle.getTargetDefinition();
        ITargetLocation[] containers = candidate.getTargetLocations();
        if (containers == null || containers.length != 1)
        {
          continue;
        }

        if (targetPlatformPath.equals(containers[0].getLocation(true)))
        {
          ITargetHandle activeHandle = service.getWorkspaceTargetHandle();
          if (activeHandle != null && activeHandle.equals(targetHandle))
          {
            // This target is already active. Nothing left to do here
            return;
          }

          target = candidate;
          break;
        }
      }

      if (target == null)
      {
        target = service.newTarget();
        ITargetLocation container = service.newDirectoryLocation(targetPlatformPath);
        target.setTargetLocations(new ITargetLocation[] { container });
        target.setName(name);
        File tpDir = new File(targetPlatformPath);
        if (!tpDir.isDirectory())
        {
          tpDir.mkdirs();
          if (!tpDir.isDirectory())
          {
            throw new IOException("Unable to create target platform directory: " + tpDir);
          }
        }
      }

      service.saveTargetDefinition(target);
      if (active)
      {
        setTargetActive(target, new ProgressLogMonitor(progress));
      }
    }
    finally
    {
      ServiceUtil.ungetService(service);
    }
  }

  public static void setTargetActive(ITargetDefinition target, IProgressMonitor monitor) throws Exception
  {
    MonitorUtils.begin(monitor, 100);

    try
    {
      target.resolve(MonitorUtils.subMonitor(monitor, 50));

      LoadTargetDefinitionJob job = new LoadTargetDefinitionJob(target);
      IStatus status = job.run(MonitorUtils.subMonitor(monitor, 50));
      if (status.getSeverity() == IStatus.ERROR)
      {
        throw new CoreException(status);
      }
    }
    finally
    {
      MonitorUtils.done(monitor);
    }
  }
}
