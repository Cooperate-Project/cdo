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
package org.eclipse.emf.cdo.internal.common.model.resource;

import org.eclipse.emf.cdo.common.model.EPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceNodeClass;
import org.eclipse.emf.cdo.internal.common.model.EClassImpl;

/**
 * @author Eike Stepper
 */
public class CDOResourceNodeClassImpl extends EClassImpl implements CDOResourceNodeClass
{
  private CDOFolderFeatureImpl cdoFolderFeature;

  private CDONameFeatureImpl cdoNameFeature;

  public CDOResourceNodeClassImpl(EPackage containingPackage, CDOPackageManager packageManager)
  {
    super(containingPackage, CLASSIFIER_ID, NAME, true);
    addFeature(cdoFolderFeature = new CDOFolderFeatureImpl(this, packageManager));
    addFeature(cdoNameFeature = new CDONameFeatureImpl(this));
  }

  @Override
  public boolean isResourceNode()
  {
    return true;
  }

  @Override
  public boolean isResourceFolder()
  {
    return false;
  }

  @Override
  public boolean isResource()
  {
    return false;
  }

  public CDOFolderFeatureImpl getCDOFolderFeature()
  {
    return cdoFolderFeature;
  }

  public CDONameFeatureImpl getCDONameFeature()
  {
    return cdoNameFeature;
  }
}
