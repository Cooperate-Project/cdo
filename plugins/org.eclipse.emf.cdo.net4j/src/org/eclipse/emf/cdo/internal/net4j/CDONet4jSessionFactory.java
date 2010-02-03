/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionFactory extends CDOSessionFactory
{
  public static final String TYPE = "cdo"; //$NON-NLS-1$

  public CDONet4jSessionFactory()
  {
    super(TYPE);
  }

  public static CDOSession get(IManagedContainer container, String description)
  {
    return (CDOSession)container.getElement(PRODUCT_GROUP, TYPE, description);
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDOSession createSession(String repositoryName, boolean automaticPackageRegistry)
  {
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();

    // The session will be activated by the container
    configuration.setActivateOnOpen(false);
    configuration.setRepositoryName(repositoryName);
    // if (automaticPackageRegistry)
    // {
    // configuration.setPackageRegistry(new CDOPackageRegistryImpl.Eager());
    // }

    return (InternalCDOSession)configuration.openSession();
  }
}
