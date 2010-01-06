/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class OpenViewRequest extends CDOClientRequest<Boolean>
{
  private int viewID;

  private int branchID;

  private long timeStamp;

  private boolean readOnly;

  public OpenViewRequest(CDOClientProtocol protocol, int viewID, int branchID, long timeStamp, boolean readOnly)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_VIEW);
    this.viewID = viewID;
    this.branchID = branchID;
    this.timeStamp = timeStamp;
    this.readOnly = readOnly;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeBoolean(readOnly);
    out.writeInt(viewID);
    out.writeInt(branchID);
    out.writeLong(timeStamp);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
