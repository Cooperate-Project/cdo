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

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IFacility;
import org.eclipse.net4j.internal.buddies.BuddyCollaboration;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class FacilityInstalledIndication extends Indication
{
  public FacilityInstalledIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_FACILITY_INSTALLED;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    long collaborationID = in.readLong();
    String facilityType = in.readString();

    IBuddySession session = (IBuddySession)getProtocol().getInfraStructure();
    Self self = (Self)session.getSelf();
    BuddyCollaboration collaboration = (BuddyCollaboration)self.getCollaboration(collaborationID);
    if (collaboration != null)
    {
      IFacility facility = collaboration.createFacility(facilityType);
      collaboration.addFacility(facility, true);
    }
  }
}
