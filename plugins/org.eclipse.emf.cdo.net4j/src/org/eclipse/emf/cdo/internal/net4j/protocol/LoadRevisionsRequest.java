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

import org.eclipse.emf.cdo.common.CDOFetchRule;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLoader.MissingRevisionInfo;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LoadRevisionsRequest extends CDOClientRequest<List<InternalCDORevision>>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionsRequest.class);

  private Collection<MissingRevisionInfo> infos;

  private CDOBranchPoint branchPoint;

  private int referenceChunk;

  private int prefetchDepth;

  public LoadRevisionsRequest(CDOClientProtocol protocol, Collection<MissingRevisionInfo> infos,
      CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISIONS);
    this.infos = infos;
    this.branchPoint = branchPoint;
    this.referenceChunk = referenceChunk;
    this.prefetchDepth = prefetchDepth;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing referenceChunk: {0}", referenceChunk); //$NON-NLS-1$
    }

    out.writeInt(referenceChunk);
    int size = infos.size();
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} infos", size); //$NON-NLS-1$
    }

    if (prefetchDepth == 0)
    {
      out.writeInt(size);
    }
    else
    {
      out.writeInt(-size);
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing prefetchDepth: {0}", prefetchDepth); //$NON-NLS-1$
      }

      out.writeInt(prefetchDepth);
    }

    Collection<CDOID> ids = new ArrayList<CDOID>(size);
    for (MissingRevisionInfo info : infos)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing info: {0}", info); //$NON-NLS-1$
      }

      info.write(out);
      ids.add(info.getID());
    }

    out.writeCDOBranchPoint(branchPoint);

    CDOFetchRuleManager ruleManager = getSession().getFetchRuleManager();
    List<CDOFetchRule> fetchRules = ruleManager.getFetchRules(ids);
    if (fetchRules == null || fetchRules.size() <= 0)
    {
      out.writeInt(0);
    }
    else
    {
      // At this point, fetch size is more than one.
      int fetchSize = fetchRules.size();
      CDOID contextID = ruleManager.getContext();

      out.writeInt(fetchSize);
      out.writeInt(ruleManager.getCollectionLoadingPolicy().getInitialChunkSize());
      out.writeCDOID(contextID);

      for (CDOFetchRule fetchRule : fetchRules)
      {
        fetchRule.write(out);
      }
    }
  }

  @Override
  protected List<InternalCDORevision> confirming(CDODataInput in) throws IOException
  {
    int size = infos.size();
    ArrayList<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>(size);
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} revisions", size); //$NON-NLS-1$
    }

    for (MissingRevisionInfo info : infos)
    {
      InternalCDORevision revision = info.readResult(in);
      revisions.add(revision);
    }

    int additionalSize = in.readInt();
    if (additionalSize != 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} additional revisions", additionalSize); //$NON-NLS-1$
      }

      InternalCDORevisionManager revisionManager = getSession().getRevisionManager();
      for (int i = 0; i < additionalSize; i++)
      {
        InternalCDORevision revision = (InternalCDORevision)in.readCDORevision();
        if (revision != null)
        {
          revisionManager.getCache().addRevision(revision);
        }
      }
    }

    return revisions;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format(
        "LoadRevisionsRequest(infos={0}, branchPoint={1}, referenceChunk={2}, prefetchDepth={3})", infos, branchPoint,
        referenceChunk, prefetchDepth);
  }
}
