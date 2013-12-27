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
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class FileUtil
{
  public static void rename(File from) throws IOException, InterruptedException
  {
    File to = new File(from.getParentFile(), from.getName() + "." + System.currentTimeMillis());
    rename(from, to);
  }

  public static void rename(File from, File to) throws IOException, InterruptedException
  {
    for (int i = 0; i < 1000; i++)
    {
      if (from.renameTo(to))
      {
        return;
      }

      Thread.sleep(5);
    }

    throw new IOException("Could not rename '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "'");
  }

  public static void deleteAsync(final File file) throws IOException, InterruptedException
  {
    new Job("Deleting old files")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          delete(file, monitor);
          return Status.OK_STATUS;
        }
        catch (Exception ex)
        {
          Activator.log(ex);
          return Activator.getStatus(ex);
        }
      }
    }.schedule();
  }

  public static void delete(File file, IProgressMonitor monitor) throws IOException, InterruptedException
  {
    List<File> files = listAllFiles(file);
    if (files.isEmpty())
    {
      return;
    }

    monitor.beginTask("Deleting files in " + file.getAbsolutePath(), files.size());

    try
    {
      Collections.reverse(files);
      for (File child : files)
      {
        String childPath = child.getAbsolutePath();
        monitor.setTaskName("Deleting file " + childPath);

        doDelete(child);

        monitor.worked(1);
        if (monitor.isCanceled())
        {
          throw new OperationCanceledException();
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private static void doDelete(File file) throws IOException, InterruptedException
  {
    for (int i = 0; i < 1000; i++)
    {
      if (file.delete())
      {
        return;
      }

      Thread.sleep(5);
    }

    throw new IOException("Could not delete '" + file.getAbsolutePath() + "'");
  }

  private static List<File> listAllFiles(File file)
  {
    List<File> result = new ArrayList<File>();
    if (file != null && file.exists())
    {
      listAllFiles(file, result);
    }

    return result;
  }

  private static void listAllFiles(File file, List<File> result)
  {
    result.add(file);
    if (file.isDirectory())
    {
      for (File child : file.listFiles())
      {
        listAllFiles(child, result);
      }
    }
  }

  public static String encodeFileName(String name)
  {
    return name.replace(':', '_').replace('/', '_');
  }
}
