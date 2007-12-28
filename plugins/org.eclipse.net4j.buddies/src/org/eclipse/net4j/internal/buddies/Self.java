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
package org.eclipse.net4j.internal.buddies;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.internal.protocol.Buddy;
import org.eclipse.net4j.buddies.internal.protocol.Membership;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IAccount;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IMembership;
import org.eclipse.net4j.internal.buddies.protocol.InitiateCollaborationRequest;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Self extends Buddy
{
  private IAccount account;

  protected Self(ClientSession session, IAccount account, Set<String> facilityTypes)
  {
    super(session, facilityTypes);
    this.account = account;
  }

  @Override
  public ClientSession getSession()
  {
    return (ClientSession)super.getSession();
  }

  public String getUserID()
  {
    return account.getUserID();
  }

  public IAccount getAccount()
  {
    return account;
  }

  public IMembership[] initiate(Collection<IBuddy> buddies)
  {
    try
    {
      ClientSession session = getSession();
      IChannel channel = session.getChannel();
      long id = new InitiateCollaborationRequest(channel, buddies).send(ProtocolConstants.TIMEOUT);

      BuddyCollaboration collaboration = new BuddyCollaboration(session, id);
      LifecycleUtil.activate(collaboration);
      Membership.create(this, collaboration);

      List<IMembership> memberships = new ArrayList<IMembership>();
      for (IBuddy buddy : buddies)
      {
        IMembership membership = Membership.create(buddy, collaboration);
        memberships.add(membership);
      }

      return memberships.toArray(new IMembership[memberships.size()]);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public IMembership join(long collaborationID)
  {
    // TODO Implement method Self.join()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public IMembership join(Object invitationToken)
  {
    // TODO Implement method Self.join()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IMembership membership : getMemberships())
    {
      LifecycleUtil.deactivate(membership.getCollaboration());
    }

    super.doDeactivate();
  }
}
