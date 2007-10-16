/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.protocol.CollaborationLeftIndication;
import org.eclipse.net4j.buddies.internal.protocol.MessageIndication;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ISession;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class ClientProtocol extends SignalProtocol
{
  public ClientProtocol()
  {
  }

  public String getType()
  {
    return ProtocolConstants.PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case ProtocolConstants.SIGNAL_BUDDY_ADDED:
      return new BuddyAddedIndication();

    case ProtocolConstants.SIGNAL_BUDDY_REMOVED:
      return new BuddyRemovedIndication();

    case ProtocolConstants.SIGNAL_BUDDY_STATE:
      return new ClientBuddyStateIndication();

    case ProtocolConstants.SIGNAL_COLLABORATION_INITIATED:
      return new CollaborationInitiatedIndication();

    case ProtocolConstants.SIGNAL_COLLABORATION_LEFT:
      return new CollaborationLeftIndication(getSelf());

    case ProtocolConstants.SIGNAL_FACILITY_INSTALLED:
      return new FacilityInstalledIndication();

    case ProtocolConstants.SIGNAL_MESSAGE:
      return new MessageIndication(getSelf());
    }

    return null;
  }

  protected IBuddy getSelf()
  {
    ISession session = (ISession)getInfraStructure();
    return session.getSelf();
  }
}
