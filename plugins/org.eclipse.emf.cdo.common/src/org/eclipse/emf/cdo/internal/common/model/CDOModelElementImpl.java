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
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOModelElement;

import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public abstract class CDOModelElementImpl implements InternalCDOModelElement
{
  private static final ContextTracer MODEL_TRACER = new ContextTracer(OM.DEBUG_MODEL, CDOModelElementImpl.class);

  private CDOPackage containingPackage;

  private transient Object clientInfo;

  private transient Object serverInfo;

  protected CDOModelElementImpl()
  {
  }

  protected CDOModelElementImpl(CDOPackage containingPackage)
  {
    this.containingPackage = containingPackage;
  }

  public CDOPackage getContainingPackage()
  {
    return containingPackage;
  }

  public void setContainingPackage(CDOPackage containingPackage)
  {
    this.containingPackage = containingPackage;
  }

  public CDOPackageManager getPackageManager()
  {
    return containingPackage.getPackageManager();
  }

  public Object getClientInfo()
  {
    return clientInfo;
  }

  public void setClientInfo(Object clientInfo)
  {
    if (MODEL_TRACER.isEnabled())
    {
      MODEL_TRACER.format("Setting client info: {0} --> {1}", this, clientInfo);
    }

    this.clientInfo = clientInfo;
  }

  public Object getServerInfo()
  {
    return serverInfo;
  }

  public void setServerInfo(Object serverInfo)
  {
    if (MODEL_TRACER.isEnabled())
    {
      MODEL_TRACER.format("Setting server info: {0} --> {1}", this, serverInfo);
    }

    this.serverInfo = serverInfo;
  }
}
