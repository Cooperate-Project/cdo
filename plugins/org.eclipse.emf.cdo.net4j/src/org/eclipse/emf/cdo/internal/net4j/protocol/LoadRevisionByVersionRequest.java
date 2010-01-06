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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByVersionRequest extends AbstractLoadRevisionRequest
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionByVersionRequest.class);

  private int version;

  public LoadRevisionByVersionRequest(CDOClientProtocol protocol, CDOID id, int version, int referenceChunk,
      int prefetchDepth)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION, Collections.singleton(id), referenceChunk,
        prefetchDepth);
    this.version = version;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    super.requesting(out);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing version: {0}", version); //$NON-NLS-1$
    }

    out.writeInt(version);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format(
        "{0}(ids={1}, referenceChunk={2}, prefetchDepth={3}, version={4})", getClass().getSimpleName(), getIDs(), //$NON-NLS-1$
        getReferenceChunk(), getPrefetchDepth(), version);
  }
}
