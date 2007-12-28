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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ProtocolUtil;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class InviteBuddiesNotification extends Request
{
  private long collaborationID;

  private Collection<IBuddy> buddies;

  public InviteBuddiesNotification(IChannel channel, long collaborationID, Collection<IBuddy> buddies)
  {
    super(channel);
    this.buddies = buddies;
    this.collaborationID = collaborationID;
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_INVITE_BUDDIES;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeLong(collaborationID);
    ProtocolUtil.writeBuddies(out, buddies);
  }
}
