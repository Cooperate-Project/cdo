/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.ISession;

import org.eclipse.net4j.channel.IChannel;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteSessionNotificationRequest extends CDOServerRequest
{
  private byte opcode;

  private ISession session;

  public RemoteSessionNotificationRequest(IChannel channel, byte opcode, ISession session)
  {
    super(channel, CDOProtocolConstants.SIGNAL_REMOTE_SESSION_NOTIFICATION);
    this.opcode = opcode;
    this.session = session;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeByte(opcode);
    out.writeInt(session.getSessionID());
    if (opcode == CDOProtocolConstants.REMOTE_SESSION_OPENED)
    {
      out.writeString(session.getUserID());
    }
  }
}
