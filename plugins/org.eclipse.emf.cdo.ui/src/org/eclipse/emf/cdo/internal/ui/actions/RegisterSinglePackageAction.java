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
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.session.CDOPackageType;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RegisterSinglePackageAction extends RegisterPackagesAction
{
  private String packageURI;

  private EPackage.Registry registry = EPackage.Registry.INSTANCE;

  public RegisterSinglePackageAction(IWorkbenchPage page, CDOSession session, String packageURI,
      CDOPackageType packageType)
  {
    super(page, packageURI, "Register the package " + packageURI, getDescriptor(packageType), session);
    this.packageURI = packageURI;
  }

  @Override
  protected List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session)
  {
    EPackage ePackage = registry.getEPackage(packageURI);
    if (ePackage != null)
    {
      return Collections.singletonList(ePackage);
    }

    return Collections.emptyList();
  }

  private static ImageDescriptor getDescriptor(CDOPackageType packageType)
  {
    switch (packageType)
    {
    case LEGACY:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_LEGACY);

    case NATIVE:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_NATIVE);

    default:
      return null;
    }
  }
}
