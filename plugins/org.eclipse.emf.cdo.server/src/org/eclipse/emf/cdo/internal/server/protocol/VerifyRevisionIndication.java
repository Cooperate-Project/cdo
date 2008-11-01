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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class VerifyRevisionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      VerifyRevisionIndication.class);

  protected long[] timeStamps;

  public VerifyRevisionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_VERIFY_REVISION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} IDs and versions", size);
    }

    RevisionManager revisionManager = getRevisionManager();
    timeStamps = new long[size];
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      int version = in.readInt();
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Read ID and version: {0}v{1}", id, version);
      }

      InternalCDORevision revision = revisionManager.getRevisionByVersion(id, 0, version);
      timeStamps[i] = revision.getRevised();
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    for (long revised : timeStamps)
    {
      out.writeLong(revised);
    }
  }
}
