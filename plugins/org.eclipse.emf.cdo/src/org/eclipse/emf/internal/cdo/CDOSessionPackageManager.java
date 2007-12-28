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
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.protocol.util.TransportException;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.LoadPackageRequest;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.signal.IFailOverStrategy;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class CDOSessionPackageManager extends CDOPackageManagerImpl
{
  private CDOSessionImpl session;

  public CDOSessionPackageManager(CDOSessionImpl session)
  {
    this.session = session;
    ModelUtil.addModelInfos(this);
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public void addPackageProxies(Collection<CDOPackageInfo> packageInfos)
  {
    for (CDOPackageInfo info : packageInfos)
    {
      String packageURI = info.getPackageURI();
      boolean dynamic = info.isDynamic();
      CDOIDRange metaIDRange = info.getMetaIDRange();

      CDOPackageImpl proxy = new CDOPackageImpl(this, packageURI, dynamic, metaIDRange);
      addPackage(proxy);
      session.getPackageRegistry().putPackageDescriptor(proxy);
    }
  }

  @Override
  protected void resolve(CDOPackageImpl cdoPackage)
  {
    if (!cdoPackage.isDynamic())
    {
      String uri = cdoPackage.getPackageURI();
      EPackage ePackage = session.getPackageRegistry().getEPackage(uri);
      if (ePackage != null)
      {
        ModelUtil.initializeCDOPackage(ePackage, cdoPackage);
        return;
      }
    }

    try
    {
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
      LoadPackageRequest request = new LoadPackageRequest(session.getChannel(), cdoPackage);
      failOverStrategy.send(request);

      if (!cdoPackage.isDynamic())
      {
        OM.LOG.info("Dynamic package created for " + cdoPackage.getPackageURI());
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  @Override
  protected String provideEcore(CDOPackageImpl cdoPackage)
  {
    EPackage ePackage = ModelUtil.getEPackage(cdoPackage, session.getPackageRegistry());
    return EMFUtil.ePackageToString(ePackage);
  }
}
