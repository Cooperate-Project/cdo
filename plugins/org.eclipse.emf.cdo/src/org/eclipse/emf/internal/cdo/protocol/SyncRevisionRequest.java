/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.CDOTimeStampContext;
import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.CDORevisionManagerImpl;
import org.eclipse.emf.internal.cdo.CDOTimeStampContextImpl;
import org.eclipse.emf.internal.cdo.InternalCDOSession;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class SyncRevisionRequest extends CDOClientRequest<Collection<CDOTimeStampContext>>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, SyncRevisionRequest.class);

  private Map<CDOID, CDORevision> revisions;

  private InternalCDOSession session;

  private int referenceChunk;

  public SyncRevisionRequest(CDOClientProtocol protocol, InternalCDOSession session, Map<CDOID, CDORevision> revisions,
      int referenceChunk)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_SYNC, session, revisions, referenceChunk);
  }

  public SyncRevisionRequest(CDOClientProtocol protocol, short signalID, InternalCDOSession session,
      Map<CDOID, CDORevision> revisions, int referenceChunk)
  {
    super(protocol, signalID);
    this.session = session;
    this.revisions = revisions;
    this.referenceChunk = referenceChunk;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.trace("Synchronization " + revisions.size() + " objects");
    }

    out.writeInt(referenceChunk);
    out.writeInt(revisions.size());
    for (CDORevision revision : revisions.values())
    {
      out.writeCDOID(revision.getID());
      out.writeInt(revision.getVersion());
    }
  }

  @Override
  protected Collection<CDOTimeStampContext> confirming(CDODataInput in) throws IOException
  {
    CDORevisionManagerImpl revisionManager = (CDORevisionManagerImpl)getRevisionManager();
    TreeMap<Long, CDOTimeStampContext> mapofContext = new TreeMap<Long, CDOTimeStampContext>();

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDORevision revision = in.readCDORevision();
      long revised = in.readLong();

      CDORevision oldRevision = revisions.get(revision.getID());
      if (oldRevision == null)
      {
        throw new IllegalStateException("Didn't expect to receive object with id '" + revision.getID() + "'");
      }

      Set<CDOIDAndVersion> dirtyObjects = getMap(mapofContext, revised).getDirtyObjects();
      dirtyObjects.add(CDOIDUtil.createIDAndVersion(oldRevision.getID(), oldRevision.getVersion()));
      revisionManager.addCachedRevision((InternalCDORevision)revision);
    }

    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.trace("Synchronization received  " + size + " dirty objects");
    }

    size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      long revised = in.readLong();

      Collection<CDOID> detachedObjects = getMap(mapofContext, revised).getDetachedObjects();
      detachedObjects.add(id);
    }

    for (CDOTimeStampContext timestampContext : mapofContext.values())
    {
      Set<CDOIDAndVersion> dirtyObjects = timestampContext.getDirtyObjects();
      Collection<CDOID> detachedObjects = timestampContext.getDetachedObjects();

      dirtyObjects = Collections.unmodifiableSet(dirtyObjects);
      detachedObjects = Collections.unmodifiableCollection(detachedObjects);

      ((CDOTimeStampContextImpl)timestampContext).setDirtyObjects(dirtyObjects);
      ((CDOTimeStampContextImpl)timestampContext).setDetachedObjects(detachedObjects);

      session.handleSyncResponse(timestampContext.getTimeStamp(), dirtyObjects, detachedObjects);
    }

    return Collections.unmodifiableCollection(mapofContext.values());
  }

  private CDOTimeStampContext getMap(Map<Long, CDOTimeStampContext> mapOfContext, long timestamp)
  {
    CDOTimeStampContext result = mapOfContext.get(timestamp);
    if (result == null)
    {
      result = new CDOTimeStampContextImpl(timestamp);
      mapOfContext.put(timestamp, result);
    }

    return result;
  }
}
