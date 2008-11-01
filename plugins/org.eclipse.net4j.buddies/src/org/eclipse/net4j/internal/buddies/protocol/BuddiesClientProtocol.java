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

import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.internal.common.protocol.MessageIndication;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.buddies.ClientSession;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * @author Eike Stepper
 */
public class BuddiesClientProtocol extends SignalProtocol<ClientSession>
{
  private static final long GET_SESSION_TIMEOUT = 20000;

  private static final int GET_SESSION_INTERVAL = 100;

  public BuddiesClientProtocol(IConnector connector)
  {
    open(connector);
  }

  public String getType()
  {
    return ProtocolConstants.PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case ProtocolConstants.SIGNAL_BUDDY_ADDED:
      return new BuddyAddedIndication(this);

    case ProtocolConstants.SIGNAL_BUDDY_REMOVED:
      return new BuddyRemovedIndication(this);

    case ProtocolConstants.SIGNAL_BUDDY_STATE:
      return new ClientBuddyStateIndication(this);

    case ProtocolConstants.SIGNAL_COLLABORATION_INITIATED:
      return new CollaborationInitiatedIndication(this);

    case ProtocolConstants.SIGNAL_COLLABORATION_LEFT:
      return new ClientCollaborationLeftIndication(this, getSelf());

    case ProtocolConstants.SIGNAL_FACILITY_INSTALLED:
      return new FacilityInstalledIndication(this);

    case ProtocolConstants.SIGNAL_MESSAGE:
      return new MessageIndication(this, getSelf());

    default:
      return null;
    }
  }

  protected Self getSelf()
  {
    ISession session = getInfraStructure();
    return (Self)session.getSelf();
  }

  public ClientSession getSession()
  {
    int max = (int)(GET_SESSION_TIMEOUT / GET_SESSION_INTERVAL);
    for (int i = 0; i < max; i++)
    {
      ClientSession session = getInfraStructure();
      if (session == null)
      {
        ConcurrencyUtil.sleep(GET_SESSION_INTERVAL);
      }
      else
      {
        return session;
      }
    }

    throw new IllegalStateException("No session after " + max + " milliseconds");
  }
}
