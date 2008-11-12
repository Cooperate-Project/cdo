/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal.monitor;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface ISignalMonitor
{
  public boolean isCanceled();

  public void checkCanceled() throws MonitorCanceledException;

  public void begin(int totalWork) throws MonitorCanceledException;

  public void worked(int work) throws MonitorCanceledException;

  public void done();

  public int getTotalWork();

  public int getWork();

  public ISignalMonitor fork(int work);
}
