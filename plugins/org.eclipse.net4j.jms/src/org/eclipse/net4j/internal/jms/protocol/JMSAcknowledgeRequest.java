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
package org.eclipse.net4j.internal.jms.protocol;

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class JMSAcknowledgeRequest extends Request
{
  private int sessionID;

  /**
   * @since 2.0
   */
  public JMSAcknowledgeRequest(JMSClientProtocol protocol, int sessionID)
  {
    super(protocol);
    this.sessionID = sessionID;
  }

  @Override
  protected short getSignalID()
  {
    return JMSProtocolConstants.SIGNAL_ACKNOWLEDGE;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeInt(sessionID);
  }
}
