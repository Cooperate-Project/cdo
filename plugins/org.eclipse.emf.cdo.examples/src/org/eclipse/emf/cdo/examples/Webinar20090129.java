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
package org.eclipse.emf.cdo.examples;

import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Package;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Webinar20090129
{
  private final static EPackage MODEL = Model1Package.eINSTANCE;

  public static void xmlSetup() throws IOException
  {
    ResourceSet rs = new ResourceSetImpl();
    rs.getResourceFactoryRegistry().getExtensionToFactoryMap() //
        .put("xml", new XMLResourceFactoryImpl()); //$NON-NLS-1$
    rs.getPackageRegistry().put(MODEL.getNsURI(), MODEL);

    URI uri = URI.createFileURI("C:/business/company.xml"); //$NON-NLS-1$
    Resource resource = rs.getResource(uri, true);
    resource.setTrackingModification(true);

    Company company = (Company)resource.getContents().get(0);
    executeBusinessLogic(company);

    if (resource.isModified())
    {
      resource.save(null);
    }
  }

  private static void executeBusinessLogic(Company company)
  {
  }
}
