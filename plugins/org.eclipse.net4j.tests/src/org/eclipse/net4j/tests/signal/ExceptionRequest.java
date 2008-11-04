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
package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;


/**
 * @author Eike Stepper
 */
public class ExceptionRequest extends RequestWithConfirmation<Boolean>
{
  private boolean exceptionInIndicating;

  public ExceptionRequest(TestSignalProtocol protocol, boolean exceptionInIndicating)
  {
    super(protocol, TestSignalProtocol.SIGNAL_EXCEPTION);
    this.exceptionInIndicating = exceptionInIndicating;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeBoolean(exceptionInIndicating);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readBoolean();
  }
}
