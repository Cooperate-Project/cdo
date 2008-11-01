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
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class CollaborationInitiatedNotification extends Request
{
  private long collaborationID;

  private Collection<IBuddy> buddies;

  private String[] facilityTypes;

  /**
   * @since 2.0
   */
  public CollaborationInitiatedNotification(BuddiesServerProtocol protocol, long collaborationID,
      Collection<IBuddy> buddies, String[] facilityTypes)
  {
    super(protocol, ProtocolConstants.SIGNAL_COLLABORATION_INITIATED);
    this.collaborationID = collaborationID;
    this.buddies = buddies;
    this.facilityTypes = facilityTypes;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeLong(collaborationID);
    ProtocolUtil.writeBuddies(out, buddies);
    ProtocolUtil.writeFacilityTypes(out, facilityTypes);
  }
}
