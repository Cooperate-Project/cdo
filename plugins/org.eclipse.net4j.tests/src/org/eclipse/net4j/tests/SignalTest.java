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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.tests.signal.Request1;
import org.eclipse.net4j.tests.signal.Request2;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.container.Container;

import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class SignalTest extends AbstractTCPTest
{
  @Override
  protected Container createContainer()
  {
    Container container = super.createContainer();
    container.register(new TestSignalProtocol.Factory());
    return container;
  }

  public void testInteger() throws Exception
  {
    startTransport();
    Channel channel = getConnector().openChannel(TestSignalProtocol.PROTOCOL_ID);
    int data = 0x0a;
    int result = new Request1(channel, data).send();
    assertEquals(data, result);
  }

  public void testArray() throws Exception
  {
    startTransport();
    Channel channel = getConnector().openChannel(TestSignalProtocol.PROTOCOL_ID);
    byte[] data = TinyData.getBytes();
    byte[] result = new Request2(channel, data).send();
    assertTrue(Arrays.equals(data, result));
  }
}
