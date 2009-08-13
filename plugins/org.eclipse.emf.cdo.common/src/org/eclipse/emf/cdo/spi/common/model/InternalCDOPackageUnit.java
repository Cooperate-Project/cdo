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
package org.eclipse.emf.cdo.spi.common.model;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;

import org.eclipse.emf.ecore.EPackage;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDOPackageUnit extends CDOPackageUnit
{
  public InternalCDOPackageRegistry getPackageRegistry();

  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry);

  public void setState(State state);

  public void setOriginalType(Type originalType);

  public void setTimeStamp(long timeStamp);

  public InternalCDOPackageInfo getTopLevelPackageInfo();

  public InternalCDOPackageInfo getPackageInfo(String packageURI);

  public InternalCDOPackageInfo[] getPackageInfos();

  public void setPackageInfos(InternalCDOPackageInfo[] packageInfos);

  public void load();

  public void write(CDODataOutput out, boolean withPackages) throws IOException;

  public void read(CDODataInput in, InternalCDOPackageRegistry packageRegistry) throws IOException;

  public void init(EPackage ePackage);

  public void dispose();
}
