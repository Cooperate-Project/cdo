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
package org.eclipse.emf.cdo.dawn.codegen;

import org.eclipse.core.runtime.Plugin;

import org.osgi.framework.BundleContext;

/**
 * @author Martin Fluegge
 */
public class DawnCodeGenPlugin extends Plugin
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.dawn.codegen"; //$NON-NLS-1$

  private static DawnCodeGenPlugin plugin;

  public DawnCodeGenPlugin()
  {
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static DawnCodeGenPlugin getDefault()
  {
    return plugin;
  }
}
