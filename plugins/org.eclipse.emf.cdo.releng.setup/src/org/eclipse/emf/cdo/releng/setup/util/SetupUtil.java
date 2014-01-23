/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.emf.cdo.releng.internal.setup.AbstractSetupTaskContext;

/**
 * @author Eike Stepper
 */
public final class SetupUtil
{
  private SetupUtil()
  {
  }

  public static String escape(String string)
  {
    return AbstractSetupTaskContext.escape(string);
  }

  public static String encodePath(String path)
  {
    return path.replace(':', '_').replace('/', '_').replace('\\', '_');
  }
}
