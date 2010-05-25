/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByVersionIndication extends LoadRevisionIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      LoadRevisionByVersionIndication.class);

  private int version;

  public LoadRevisionByVersionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    super.indicating(in);
    version = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read version: {0}", version); //$NON-NLS-1$
    }
  }

  @Override
  protected InternalCDORevision getRevision(CDOID id)
  {
    return getRepository().getRevisionManager().getRevisionByVersion(id, referenceChunk, version);
  }
}
