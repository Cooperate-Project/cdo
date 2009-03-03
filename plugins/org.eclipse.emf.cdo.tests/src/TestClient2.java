import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.emf.ecore.EObject;

import junit.framework.Assert;

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

/**
 * @author Eike Stepper
 */
public class TestClient2 extends Assert
{
  public static void main(String[] args) throws Exception
  {
    // EMFUtil.registerPackage(Model1Package.eINSTANCE);

    PrintTraceHandler.CONSOLE.setShortContext(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);

    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    CDOUtil.prepareContainer(container);
    LifecycleUtil.activate(container);

    IConnector connector = (IConnector)container.getElement("org.eclipse.net4j.connectors", "tcp", "localhost");

    CDOSessionConfiguration sessionConfiguration = CDONet4jUtil.createSessionConfiguration();
    sessionConfiguration.setRepositoryName(TestServer.REPOSITORY_NAME);
    sessionConfiguration.setConnector(connector);
    CDOSession session = sessionConfiguration.openSession();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("res");
    for (EObject object : resource.getContents())
    {
      System.out.println(object);
    }

    OMPlatform.INSTANCE.setDebugging(false);
    LifecycleUtil.deactivate(session);
    LifecycleUtil.deactivate(connector);
    LifecycleUtil.deactivate(container);
  }
}
