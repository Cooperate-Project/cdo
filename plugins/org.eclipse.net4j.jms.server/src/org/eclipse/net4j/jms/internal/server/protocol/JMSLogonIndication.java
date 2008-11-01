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
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.jms.internal.server.Server;
import org.eclipse.net4j.jms.internal.server.ServerConnection;
import org.eclipse.net4j.jms.internal.server.bundle.OM;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class JMSLogonIndication extends IndicationWithResponse
{
  private boolean ok;

  public JMSLogonIndication(JMSServerProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_LOGON);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    String userName = in.readString();
    String password = in.readString();
    ServerConnection connection = Server.INSTANCE.logon(userName, password);
    if (connection == null)
    {
      OM.LOG.error("Access to JMS server denied");
      return;
    }

    JMSServerProtocol protocol = (JMSServerProtocol)getProtocol();
    connection.setProtocol(protocol);
    protocol.setInfraStructure(connection);
    ok = true;
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeBoolean(ok);
  }
}
