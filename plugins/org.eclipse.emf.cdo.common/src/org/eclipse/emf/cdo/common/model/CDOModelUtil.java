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
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOClassifier.Kind;
import org.eclipse.emf.cdo.internal.common.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOModelUtil
{
  private CDOModelUtil()
  {
  }

  // ///////////////////////////////////////////////

  public static CDOType getType(int typeID)
  {
    CDOTypeImpl type = CDOTypeImpl.ids.get(typeID);
    if (type == null)
    {
      throw new IllegalStateException("No type for id " + typeID);
    }

    return type;
  }

  public static CDOPackage createPackage(CDOPackageManager packageManager, String packageURI, String name,
      String ecore, boolean dynamic, CDOIDMetaRange metaIDRange, String parentURI)
  {
    return new CDOPackageImpl(packageManager, packageURI, name, ecore, dynamic, metaIDRange, parentURI);
  }

  public static CDOPackage createProxyPackage(CDOPackageManager packageManager, String packageURI, boolean dynamic,
      CDOIDMetaRange metaIDRange, String parentURI)
  {
    return new CDOPackageImpl(packageManager, packageURI, dynamic, metaIDRange, parentURI);
  }

  public static CDOClass createClass(CDOPackage containingPackage, int classifierID, String name, boolean isAbstract)
  {
    return new CDOClassImpl(containingPackage, classifierID, name, isAbstract);
  }

  /**
   * @since 2.0
   */
  public static CDOFeature createAttribute(CDOClass containingClass, int featureID, String name, CDOType type,
      Object defaultValue, boolean many)
  {
    return new CDOFeatureImpl(containingClass, featureID, name, type, defaultValue, many);
  }

  public static CDOFeature createReference(CDOClass containingClass, int featureID, String name,
      CDOClassProxy referenceType, boolean many, boolean containment)
  {
    return new CDOFeatureImpl(containingClass, featureID, name, referenceType, many, containment);
  }

  public static CDOClassifierRef createClassRef(String packageURI, int classifierID)
  {
    return new CDOClassRefImpl(packageURI, classifierID);
  }

  /**
   * @since 2.0
   */
  public static CDOClassifier createClassifierRef(Kind kind, CDOPackageManager packageManager)
  {
    switch (kind)
    {
    case CLASS:
      return new CDOClassRef(packageManager);
    case TYPE:
    case ENUM:

    default:
      throw new IllegalArgumentException("kind: " + kind);
    }
  }
}
