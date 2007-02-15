/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class Indication1 extends IndicationWithResponse
{
  private int data;

  @Override
  protected short getSignalID()
  {
    return TestSignalProtocol.SIGNAL1;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    data = in.readInt();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeInt(data);
  }
}
