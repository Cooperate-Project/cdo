/**
 * Copyright (c) 2004 - 2009 Martin Taal and others. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the CDOPackageUnitImpl as a Hibernate persistable object.
 * 
 * @author Martin Taal
 */
public class HibernateCDOPackageUnit implements InternalCDOPackageUnit
{
  private String nsUri;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateCDOPackageUnit.class);

  private InternalCDOPackageRegistry packageRegistry;

  private State state = State.PROXY;

  private Type type;

  private Type originalType;

  private long timeStamp;

  private InternalCDOPackageInfo[] packageInfos;

  public HibernateCDOPackageUnit()
  {
  }

  public HibernateCDOPackageUnit(CDOPackageUnit cdoPackageUnit)
  {
    setType(cdoPackageUnit.getType());
    setPackageInfos((InternalCDOPackageInfo[])cdoPackageUnit.getPackageInfos());
    setOriginalType(cdoPackageUnit.getOriginalType());
    setPackageRegistry((InternalCDOPackageRegistry)cdoPackageUnit.getPackageRegistry());
    setTimeStamp(cdoPackageUnit.getTimeStamp());
  }

  public String getNsUri()
  {
    if (nsUri == null)
    {
      nsUri = getPackageInfos()[0].getPackageURI();
    }

    return nsUri;
  }

  public void setNsUri(String nsUri)
  {
    this.nsUri = nsUri;
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }

  public String getID()
  {
    try
    {
      return getTopLevelPackageInfo().getPackageURI();
    }
    catch (RuntimeException ex)
    {
      return "UNINITIALIZED";
    }
  }

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    this.state = state;
    if (state == State.LOADED)
    {
      type = null;
    }
  }

  public Type getType()
  {
    if (getOriginalType() == Type.DYNAMIC)
    {
      type = Type.DYNAMIC;
    }
    else if (type == null || type == Type.UNKNOWN)
    {
      if (state == State.PROXY)
      {
        type = CDOPackageTypeRegistry.INSTANCE.lookup(getID());
      }
      else
      {
        InternalCDOPackageInfo packageInfo = getTopLevelPackageInfo();
        EPackage ePackage = packageInfo.getEPackage();
        type = CDOPackageTypeRegistry.INSTANCE.lookup(ePackage);
      }

      if (type == null)
      {
        type = Type.UNKNOWN;
      }
    }

    return type;
  }

  public Type getOriginalType()
  {
    return originalType;
  }

  public void setOriginalType(Type originalType)
  {
    this.originalType = originalType;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public InternalCDOPackageInfo getTopLevelPackageInfo()
  {
    if (packageInfos == null || packageInfos.length == 0)
    {
      throw new IllegalStateException("Package unit not initialized");
    }

    return packageInfos[0];
  }

  public InternalCDOPackageInfo getPackageInfo(String packageURI)
  {
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      if (packageInfo.getPackageURI().equals(packageURI))
      {
        return packageInfo;
      }
    }

    return null;
  }

  public InternalCDOPackageInfo[] getPackageInfos()
  {
    return packageInfos;
  }

  public void setPackageInfos(InternalCDOPackageInfo[] packageInfos)
  {
    this.packageInfos = packageInfos;
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      packageInfo.setPackageUnit(this);
    }
  }

  public EPackage[] getEPackages(boolean loadOnDemand)
  {
    List<EPackage> result = new ArrayList<EPackage>();
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      EPackage ePackage = packageInfo.getEPackage(loadOnDemand);
      if (ePackage != null)
      {
        result.add(ePackage);
      }
    }

    return result.toArray(new EPackage[result.size()]);
  }

  public boolean isSystem()
  {
    return getTopLevelPackageInfo().isSystemPackage();
  }

  public void init(EPackage ePackage)
  {
    EPackage topLevelPackage = EMFUtil.getTopLevelPackage(ePackage);
    List<InternalCDOPackageInfo> result = new ArrayList<InternalCDOPackageInfo>();
    initPackageInfos(topLevelPackage, result);
    packageInfos = result.toArray(new InternalCDOPackageInfo[result.size()]);

    setState(State.NEW);
    originalType = getType();
  }

  public void dispose()
  {
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      EPackage ePackage = packageInfo.getEPackage(false);
      if (ePackage != null)
      {
        synchronized (ePackage)
        {
          ePackage.eAdapters().remove(packageInfo);
        }
      }
    }

    packageInfos = null;
    setState(State.DISPOSED);
  }

  public synchronized void load()
  {
    if (state == State.PROXY)
    {
      EPackage[] ePackages = null;
      ePackages = loadPackagesFromGlobalRegistry();
      if (ePackages == null)
      {
        ePackages = packageRegistry.getPackageLoader().loadPackages(this);
      }

      for (EPackage ePackage : ePackages)
      {
        String packageURI = ePackage.getNsURI();
        InternalCDOPackageInfo packageInfo = getPackageInfo(packageURI);
        synchronized (ePackage)
        {
          EMFUtil.addAdapter(ePackage, packageInfo);
          EcoreUtil.resolveAll(ePackage);
        }
      }

      setState(State.LOADED);
    }
  }

  public void write(CDODataOutput out, boolean withPackages) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0}", this);
    }

    out.writeBoolean(withPackages);
    if (withPackages)
    {
      CDOModelUtil.writePackage(out, packageInfos[0].getEPackage(), true, packageRegistry);
    }

    out.writeCDOPackageUnitType(originalType);
    out.writeLong(timeStamp);
    out.writeInt(packageInfos.length);
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      out.writeCDOPackageInfo(packageInfo);
    }
  }

  public void read(CDODataInput in, InternalCDOPackageRegistry packageRegistry) throws IOException
  {
    EPackage ePackage = null;
    boolean withPackages = in.readBoolean();
    if (withPackages)
    {
      ePackage = CDOModelUtil.readPackage(in, packageRegistry);
      setState(State.LOADED);
    }

    originalType = in.readCDOPackageUnitType();
    timeStamp = in.readLong();
    packageInfos = new InternalCDOPackageInfo[in.readInt()];
    for (int i = 0; i < packageInfos.length; i++)
    {
      packageInfos[i] = (InternalCDOPackageInfo)in.readCDOPackageInfo();
      packageInfos[i].setPackageUnit(this);
    }

    if (ePackage != null)
    {
      attachPackageInfos(ePackage);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Read {0}", this);
    }
  }

  public int compareTo(CDOPackageUnit o)
  {
    return getID().compareTo(o.getID());
  }

  @Override
  public String toString()
  {
    String fmt = "CDOPackageUnit[id={0}, state={1}, type={2}, originalType={3}, timeStamp={4,date} {4,time}]";
    return MessageFormat.format(fmt, getID(), getState(), getType(), getOriginalType(), getTimeStamp());
  }

  private void initPackageInfos(EPackage ePackage, List<InternalCDOPackageInfo> result)
  {
    InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
    packageInfo.setPackageUnit(this);
    packageInfo.setPackageURI(ePackage.getNsURI());
    packageInfo.setParentURI(ePackage.getESuperPackage() == null ? null : ePackage.getESuperPackage().getNsURI());
    packageInfo.setMetaIDRange(packageRegistry.getMetaInstanceMapper().mapMetaInstances(ePackage));
    EMFUtil.addAdapter(ePackage, packageInfo);

    packageRegistry.basicPut(ePackage.getNsURI(), ePackage);
    result.add(packageInfo);
    for (EPackage subPackage : ePackage.getESubpackages())
    {
      initPackageInfos(subPackage, result);
    }
  }

  private void attachPackageInfos(EPackage ePackage)
  {
    InternalCDOPackageInfo packageInfo = getPackageInfo(ePackage.getNsURI());
    if (packageInfo != null)
    {
      EMFUtil.addAdapter(ePackage, packageInfo);
    }

    for (EPackage subPackage : ePackage.getESubpackages())
    {
      attachPackageInfos(subPackage);
    }
  }

  private EPackage[] loadPackagesFromGlobalRegistry()
  {
    EPackage[] ePackages = new EPackage[packageInfos.length];
    for (int i = 0; i < ePackages.length; i++)
    {
      ePackages[i] = EPackage.Registry.INSTANCE.getEPackage(packageInfos[i].getPackageURI());
      if (ePackages[i] == null)
      {
        return null;
      }
    }

    return ePackages;
  }

  public void setType(Type type)
  {
    this.type = type;
  }
}
