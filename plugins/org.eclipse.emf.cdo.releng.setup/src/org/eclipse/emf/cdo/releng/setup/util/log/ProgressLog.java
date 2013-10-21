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
package org.eclipse.emf.cdo.releng.setup.util.log;

import org.eclipse.core.runtime.IStatus;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public interface ProgressLog
{
  public static final AtomicReference<ProgressLog> INSTANCE = new AtomicReference<ProgressLog>();

  public boolean isCancelled();

  public void log(String line);

  public void log(IStatus status);
}
