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
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class SessionConfig extends Config implements ISessionConfig
{
  public static final String PROP_TEST_CREDENTIALS_PROVIDER = "test.session.CredentialsProvider";

  private static final long serialVersionUID = 1L;

  private transient Set<CDOSession> sessions;

  private transient IListener sessionListener;

  public SessionConfig(String name)
  {
    super(name);
  }

  public void startTransport() throws Exception
  {
    IAcceptor acceptor = getAcceptor();
    LifecycleUtil.activate(acceptor);

    IConnector connector = getConnector();
    LifecycleUtil.activate(connector);
  }

  public void stopTransport() throws Exception
  {
    ConfigTest currentTest = getCurrentTest();
    if (currentTest.hasClientContainer())
    {
      IConnector connector = getConnector();
      connector.close();
    }

    if (currentTest.hasServerContainer())
    {
      IAcceptor acceptor = getAcceptor();
      acceptor.close();
    }
  }

  public CDOSession openMangoSession()
  {
    return openSession(getCurrentTest().getMangoPackage());
  }

  public CDOSession openModel1Session()
  {
    return openSession(getCurrentTest().getModel1Package());
  }

  public CDOSession openModel2Session()
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(getCurrentTest().getModel2Package());
    return session;
  }

  public CDOSession openModel3Session()
  {
    return openSession(getCurrentTest().getModel3Package());
  }

  public CDOSession openSession(EPackage ePackage)
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(ePackage);
    return session;
  }

  public CDOSession openSession()
  {
    return openSession(IRepositoryConfig.REPOSITORY_NAME);
  }

  public CDOSession openSession(String repositoryName)
  {
    CDOSessionConfiguration configuration = createSessionConfiguration(repositoryName);
    CDOSession session = configuration.openSession();
    session.options().getProtocol().setTimeout(-1);
    session.addListener(sessionListener);

    synchronized (sessions)
    {
      sessions.add(session);
    }

    return session;
  }

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    sessions = new HashSet<CDOSession>();
    sessionListener = new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle session)
      {
        synchronized (sessions)
        {
          sessions.remove(session);
        }
      }
    };
  }

  @Override
  public void tearDown() throws Exception
  {
    try
    {
      if (sessions != null)
      {
        CDOSession[] array;
        synchronized (sessions)
        {
          array = sessions.toArray(new CDOSession[sessions.size()]);
        }

        for (CDOSession session : array)
        {
          session.removeListener(sessionListener);
          LifecycleUtil.deactivate(session);
        }

        synchronized (sessions)
        {
          sessions.clear();
        }
      }

      sessions = null;
      sessionListener = null;
      stopTransport();
      super.tearDown();
    }
    finally
    {
      removeDynamicPackagesFromGlobalRegistry();
    }
  }

  protected IPasswordCredentialsProvider getTestCredentialsProvider()
  {
    return (IPasswordCredentialsProvider)getTestProperty(PROP_TEST_CREDENTIALS_PROVIDER);
  }

  private CDOSessionConfiguration createSessionConfiguration(String repositoryName)
  {
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(repositoryName);
    configuration.getAuthenticator().setCredentialsProvider(getTestCredentialsProvider());

    // configuration.setExceptionHandler(new CDORetryExceptionHandler(2));

    // configuration.setExceptionHandler(new ExceptionHandler()
    // {
    // public void handleException(int attempt, Exception exception) throws Exception
    // {
    // throw exception;
    // }
    // });

    return configuration;
  }

  private void removeDynamicPackagesFromGlobalRegistry()
  {
    EPackage.Registry registry = EPackage.Registry.INSTANCE;
    for (String uri : registry.keySet().toArray(new String[registry.size()]))
    {
      Object object = registry.get(uri);
      if (object != null && object.getClass() == EPackageImpl.class)
      {
        registry.remove(uri);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Embedded extends SessionConfig
  {
    public static final String NAME = "Embedded";

    public static final Embedded INSTANCE = new Embedded();

    private static final long serialVersionUID = 1L;

    public Embedded()
    {
      super(NAME);
    }

    @Override
    public void setUp() throws Exception
    {
      super.setUp();
    }

    public IAcceptor getAcceptor()
    {
      return null;
    }

    public IConnector getConnector()
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends SessionConfig
  {
    public static final String NAME = "TCP";

    public static final TCP INSTANCE = new TCP();

    public static final String CONNECTOR_HOST = "localhost";

    private static final long serialVersionUID = 1L;

    public TCP()
    {
      super(NAME);
    }

    public IAcceptor getAcceptor()
    {
      return TCPUtil.getAcceptor(getCurrentTest().getServerContainer(), null);
    }

    public IConnector getConnector()
    {
      return TCPUtil.getConnector(getCurrentTest().getClientContainer(), CONNECTOR_HOST);
    }

    @Override
    public void setUp() throws Exception
    {
      super.setUp();

      IManagedContainer clientContainer = getCurrentTest().getClientContainer();
      TCPUtil.prepareContainer(clientContainer);

      IManagedContainer serverContainer = getCurrentTest().getServerContainer();
      if (serverContainer != clientContainer)
      {
        TCPUtil.prepareContainer(serverContainer);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class JVM extends SessionConfig
  {
    public static final String NAME = "JVM";

    public static final JVM INSTANCE = new JVM();

    public static final String ACCEPTOR_NAME = "default";

    private static final long serialVersionUID = 1L;

    public JVM()
    {
      super(NAME);
    }

    public IAcceptor getAcceptor()
    {
      return JVMUtil.getAcceptor(getCurrentTest().getServerContainer(), ACCEPTOR_NAME);
    }

    public IConnector getConnector()
    {
      return JVMUtil.getConnector(getCurrentTest().getClientContainer(), ACCEPTOR_NAME);
    }

    @Override
    public void setUp() throws Exception
    {
      super.setUp();
      JVMUtil.prepareContainer(getCurrentTest().getClientContainer());
      JVMUtil.prepareContainer(getCurrentTest().getServerContainer());
    }

    @Override
    public boolean isValid(Set<IConfig> configs)
    {
      return !configs.contains(ContainerConfig.Separated.INSTANCE);
    }
  }
}
