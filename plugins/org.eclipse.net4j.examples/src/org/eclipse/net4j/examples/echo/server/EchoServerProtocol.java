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
package org.eclipse.net4j.examples.echo.server;

import org.eclipse.net4j.examples.echo.EchoProtocol;
import org.eclipse.net4j.protocol.ServerProtocolFactory;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EchoServerProtocol extends SignalProtocol<Object> implements EchoProtocol
{
  public EchoServerProtocol()
  {
  }

  public String getType()
  {
    return PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case ECHO_SIGNAL:
      return new EchoIndication();

    default:
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends ServerProtocolFactory
  {
    public Factory()
    {
      super(PROTOCOL_NAME);
    }

    public Object create(String description) throws ProductCreationException
    {
      return new EchoServerProtocol();
    }
  }
}
