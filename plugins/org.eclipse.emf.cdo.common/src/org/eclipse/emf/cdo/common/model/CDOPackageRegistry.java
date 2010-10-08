/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOPackageRegistry extends EPackage.Registry
{
  public boolean isReplacingDescriptors();

  /**
   * Registers an {@link EPackage} with this package registry.
   */
  public Object putEPackage(EPackage ePackage);

  /**
   * @since 3.0
   */
  public CDOPackageUnit getPackageUnit(String id);

  public CDOPackageUnit getPackageUnit(EPackage ePackage);

  /**
   * Returns all package units that are registered in this package registry.
   * 
   * @since 3.0
   */
  public CDOPackageUnit[] getPackageUnits();

  /**
   * @since 3.0
   */
  public CDOPackageUnit[] getPackageUnits(long startTime, long endTime);

  public CDOPackageInfo getPackageInfo(EPackage ePackage);

  /**
   * Returns all package infos that are registered in this package registry.
   */
  public CDOPackageInfo[] getPackageInfos();

  /**
   * @since 4.0
   */
  public EEnumLiteral getEnumLiteralFor(Enumerator value);
}
