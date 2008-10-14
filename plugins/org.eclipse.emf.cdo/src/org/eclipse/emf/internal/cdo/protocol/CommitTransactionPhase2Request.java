/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalTempImpl;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.internal.cdo.CDOXATransactionCommitContext;
import org.eclipse.emf.internal.cdo.InternalCDOTransaction;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.URI;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 * Phase 2 consist of sending the mapping of temporary CDOID/final CDOID from other CDOTransaction.
 * <p>
 * It will return confirmation only when the commit is ready to flush to disk.
 * 
 * @author Simon McDuff
 */
public class CommitTransactionPhase2Request extends CommitTransactionRequest
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL,
      CommitTransactionPhase1Request.class);

  public CommitTransactionPhase2Request(CDOClientProtocol protocol, final CDOXATransactionCommitContext xaTransaction)
  {
    super(protocol, xaTransaction);
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE2;
  }

  @Override
  protected CDOXATransactionCommitContext getCommitContext()
  {
    return (CDOXATransactionCommitContext)super.getCommitContext();
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    requestingTransactionInfo(out);
    requestingIdMapping(out);
  }

  @Override
  protected CommitTransactionResult confirming(CDODataInput in) throws IOException
  {
    return confirmingCheckError(in);
  }

  /**
   * Write ids that are needed. only If it needs to
   */
  protected void requestingIdMapping(CDODataOutput out) throws IOException
  {
    CDOXATransactionCommitContext context = getCommitContext();
    Map<CDOIDExternalTempImpl, InternalCDOTransaction> requestedIDs = context.getRequestedIDs();
    int size = requestedIDs.size();
    out.writeInt(size);
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Number of ids requested: {0}", size);
    }

    for (Entry<CDOIDExternalTempImpl, InternalCDOTransaction> entry : requestedIDs.entrySet())
    {
      CDOIDExternalTempImpl tempID = entry.getKey();
      URI oldURIExternal = URI.createURI(tempID.toURIFragment());
      CDOID oldCDOID = CDOIDUtil.read(oldURIExternal.fragment(), null);

      CDOXATransactionCommitContext commitContext = context.getTransactionManager().getCommitContext(entry.getValue());
      if (commitContext == null)
      {
        throw new IllegalStateException("Missing informations. " + entry.getValue() + " isn't involved in the commit.");
      }

      CDOID newID = commitContext.getResult().getIDMappings().get(oldCDOID);
      if (newID == null)
      {
        throw new IllegalStateException("Missing informations. " + oldCDOID.toURIFragment()
            + " isn't mapped in the commit.");
      }

      CDOID newIDExternal = CDOURIUtil.convertExternalCDOID(oldURIExternal, newID);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("ID mapping: {0} --> {1}", tempID.toURIFragment(), newIDExternal.toURIFragment());
      }
      out.writeCDOID(tempID);
      out.writeCDOID(newIDExternal);

      context.getResult().addIDMapping(tempID, newIDExternal);
    }
  }
}
