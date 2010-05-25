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

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class VariousExamples
{
  public static void openSessionAndUseTransaction()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    IConnector connector = (IConnector)IPluginContainer.INSTANCE.getElement("org.eclipse.net4j.connectors", "tcp",
        "repos.foo.org:2036");

    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName("MyRepo");

    CDOSession session = configuration.openSession();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    // Work with the resource set....
    transaction.commit();
    session.close();
  }

  public static void registerSessionWithPluginContainer()
  {
    CDOSession session = (CDOSession)IPluginContainer.INSTANCE.getElement("org.eclipse.emf.cdo.sessions", "cdo",
        "tcp://repos.foo.org:2036/MyRepo");
    CDOTransaction transaction = session.openTransaction();
    // ...
    transaction.commit();
  }
}
