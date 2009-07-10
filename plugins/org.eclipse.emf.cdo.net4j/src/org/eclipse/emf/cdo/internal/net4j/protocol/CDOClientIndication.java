/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;

import org.eclipse.emf.internal.cdo.revision.CDOListWithElementProxiesImpl;

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.StringIO;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOClientIndication extends Indication
{
  public CDOClientIndication(CDOClientProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  public CDOClientProtocol getProtocol()
  {
    return (CDOClientProtocol)super.getProtocol();
  }

  protected InternalCDOSession getSession()
  {
    return (InternalCDOSession)getProtocol().getSession();
  }

  @Override
  protected final void indicating(ExtendedDataInputStream in) throws Exception
  {
    indicating(new CDODataInputImpl(in)
    {
      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return getSession();
      }

      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListWithElementProxiesImpl.FACTORY;
      }

      @Override
      protected CDOPackageRegistry getPackageRegistry()
      {
        return getSession().getPackageRegistry();
      }

      @Override
      protected CDORevisionManager getRevisionManager()
      {
        return getSession().getRevisionManager();
      }
    });
  }

  protected abstract void indicating(CDODataInput in) throws IOException;
}
