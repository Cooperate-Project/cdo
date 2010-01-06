/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.server.InternalView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ChangeViewIndication extends CDOReadIndication
{
  private boolean[] existanceFlags;

  public ChangeViewIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_VIEW);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    int branchID = in.readInt();
    long timeStamp = in.readLong();

    int size = in.readInt();
    List<CDOID> invalidObjects = new ArrayList<CDOID>(size);
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      invalidObjects.add(id);
    }

    InternalView view = getSession().getView(viewID);
    existanceFlags = view.changeTarget(branchID, timeStamp, invalidObjects);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeInt(existanceFlags.length);
    for (int i = 0; i < existanceFlags.length; i++)
    {
      out.writeBoolean(existanceFlags[i]);
    }
  }
}
