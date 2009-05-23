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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadChunkRequest extends CDOClientRequest<Object>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadChunkRequest.class);

  private InternalCDORevision revision;

  private EStructuralFeature feature;

  private int accessIndex;

  private int fromIndex;

  private int toIndex;

  private int fetchIndex;

  public LoadChunkRequest(CDOClientProtocol protocol, InternalCDORevision revision, EStructuralFeature feature,
      int accessIndex, int fetchIndex, int fromIndex, int toIndex)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_CHUNK);
    this.revision = revision;
    this.feature = feature;
    this.accessIndex = accessIndex;
    this.fetchIndex = fetchIndex;
    this.fromIndex = fromIndex;
    this.toIndex = toIndex;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    CDOID id = revision.getID();
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision ID: {0}", id); //$NON-NLS-1$
    }

    out.writeCDOID(id);
    int version = revision.getVersion();
    if (revision.isTransactional())
    {
      --version;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision version: {0}", version); //$NON-NLS-1$
    }

    out.writeInt(version);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing feature: {0}", feature); //$NON-NLS-1$
    }

    out.writeCDOClassifierRef(feature.getEContainingClass());
    out.writeInt(feature.getFeatureID());
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing fromIndex: {0}", fromIndex); //$NON-NLS-1$
    }

    int diffIndex = accessIndex - fetchIndex;
    out.writeInt(fromIndex - diffIndex);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing toIndex: {0}", toIndex); //$NON-NLS-1$
    }

    out.writeInt(toIndex - diffIndex);
  }

  @Override
  protected Object confirming(CDODataInput in) throws IOException
  {
    CDOType type = CDOModelUtil.getType(feature);
    Object accessID = null;
    MoveableList<Object> list = revision.getList(feature);
    for (int i = fromIndex; i <= toIndex; i++)
    {
      Object value = type.readValue(in);
      list.set(i, value);
      if (i == accessIndex)
      {
        accessID = value;
      }
    }

    return accessID;
  }
}
