/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOPackageInfoImpl extends AdapterImpl implements InternalCDOPackageInfo
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOPackageInfoImpl.class);

  private InternalCDOPackageUnit packageUnit;

  private String packageURI;

  private String parentURI;

  private CDOIDMetaRange metaIDRange;

  public CDOPackageInfoImpl()
  {
  }

  public InternalCDOPackageUnit getPackageUnit()
  {
    return packageUnit;
  }

  public void setPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    this.packageUnit = packageUnit;
  }

  public String getPackageURI()
  {
    return packageURI;
  }

  public void setPackageURI(String packageURI)
  {
    this.packageURI = packageURI;
  }

  public String getParentURI()
  {
    return parentURI;
  }

  public void setParentURI(String parentURI)
  {
    this.parentURI = parentURI;
  }

  public CDOIDMetaRange getMetaIDRange()
  {
    return metaIDRange;
  }

  public void setMetaIDRange(CDOIDMetaRange metaIDRange)
  {
    this.metaIDRange = metaIDRange;
  }

  public void write(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0}", this);
    }

    out.writeCDOPackageURI(packageURI);
    out.writeCDOPackageURI(parentURI);
    out.writeCDOIDMetaRange(getMetaIDRange());
  }

  public void read(CDODataInput in) throws IOException
  {
    packageURI = in.readCDOPackageURI();
    parentURI = in.readCDOPackageURI();
    metaIDRange = in.readCDOIDMetaRange();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read {0}", this);
    }
  }

  public EFactory getEFactory()
  {
    return getEPackage().getEFactoryInstance();
  }

  public EPackage getEPackage()
  {
    return getEPackage(true);
  }

  public EPackage getEPackage(boolean loadOnDemand)
  {
    EPackage ePackage = (EPackage)getTarget();
    if (ePackage != null)
    {
      return ePackage;
    }

    if (loadOnDemand)
    {
      packageUnit.load();
      return (EPackage)getTarget();
    }

    return null;
  }

  public boolean isCorePackage()
  {
    return CDOModelUtil.isCorePackage(getEPackage());
  }

  public boolean isResourcePackage()
  {
    return CDOModelUtil.isResourcePackage(getEPackage());
  }

  public boolean isSystemPackage()
  {
    return CDOModelUtil.isSystemPackage(getEPackage());
  }

  public int compareTo(CDOPackageInfo o)
  {
    return getPackageURI().compareTo(o.getPackageURI());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOPackageInfo[packageURI={0}, parentURI={1}, metaIDRange={2}]", packageURI,
        parentURI, metaIDRange);
  }
}
