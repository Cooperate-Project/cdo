/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simon McDuff
 */
public class LockObjectsRequest extends CDOClientRequest<Object>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LockObjectsRequest.class);

  private CDOView view;

  private RWLockManager.LockType lockType;

  private Collection<? extends CDOObject> objects;

  private long timeout;

  public LockObjectsRequest(CDOClientProtocol protocol, CDOView view, Collection<? extends CDOObject> objects,
      long timeout, RWLockManager.LockType lockType)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OBJECTS);
    this.view = view;
    this.objects = objects;
    this.timeout = timeout;
    this.lockType = lockType;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(view.getViewID());
    out.writeCDOLockType(lockType);
    out.writeLong(timeout);

    if (TRACER.isEnabled())
    {
      TRACER.format("Locking of type {0} requested for view {1} with timeout {2}", //$NON-NLS-1$
          lockType == RWLockManager.LockType.READ ? "read" : "write", view.getViewID(), timeout); //$NON-NLS-1$ //$NON-NLS-2$
    }

    out.writeInt(objects.size());
    for (CDOObject object : objects)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Locking requested for objects {0}", object.cdoID()); //$NON-NLS-1$
      }

      out.writeCDOID(object.cdoID());
    }
  }

  @Override
  protected Object confirming(CDODataInput in) throws IOException
  {
    in.readBoolean();
    return null;
  }
}
