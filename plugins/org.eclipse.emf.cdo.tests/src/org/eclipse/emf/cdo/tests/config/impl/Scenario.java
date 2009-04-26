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

import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.IContainerConfig;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Scenario implements IScenario
{
  public static final String STATE_FILE = "cdo_config_test.state";

  private static final long serialVersionUID = 1L;

  private IContainerConfig containerConfig;

  private IRepositoryConfig repositoryConfig;

  private ISessionConfig sessionConfig;

  private IModelConfig modelConfig;

  private transient Set<IConfig> configs;

  private transient ConfigTest currentTest;

  public Scenario()
  {
  }

  public synchronized IContainerConfig getContainerConfig()
  {
    return containerConfig;
  }

  public synchronized void setContainerConfig(IContainerConfig containerConfig)
  {
    configs = null;
    this.containerConfig = containerConfig;
    if (containerConfig != null)
    {
      containerConfig.setCurrentTest(currentTest);
    }
  }

  public synchronized IRepositoryConfig getRepositoryConfig()
  {
    return repositoryConfig;
  }

  public synchronized void setRepositoryConfig(IRepositoryConfig repositoryConfig)
  {
    configs = null;
    this.repositoryConfig = repositoryConfig;
    if (repositoryConfig != null)
    {
      repositoryConfig.setCurrentTest(currentTest);
    }
  }

  public synchronized ISessionConfig getSessionConfig()
  {
    return sessionConfig;
  }

  public synchronized void setSessionConfig(ISessionConfig sessionConfig)
  {
    configs = null;
    this.sessionConfig = sessionConfig;
    if (sessionConfig != null)
    {
      sessionConfig.setCurrentTest(currentTest);
    }
  }

  public synchronized IModelConfig getModelConfig()
  {
    return modelConfig;
  }

  public synchronized void setModelConfig(IModelConfig modelConfig)
  {
    configs = null;
    this.modelConfig = modelConfig;
    if (modelConfig != null)
    {
      modelConfig.setCurrentTest(currentTest);
    }
  }

  @Override
  public synchronized String toString()
  {
    return MessageFormat.format("Scenario[{0}, {1}, {2}, {3}]", //
        getContainerConfig(), getRepositoryConfig(), getSessionConfig(), getModelConfig());
  }

  public synchronized Set<IConfig> getConfigs()
  {
    if (configs == null)
    {
      configs = new HashSet<IConfig>();
      configs.add(getContainerConfig());
      configs.add(getRepositoryConfig());
      configs.add(getSessionConfig());
      configs.add(getModelConfig());
    }

    return configs;
  }

  public synchronized boolean isValid()
  {
    Set<IConfig> configs = getConfigs();
    for (IConfig config : configs)
    {
      if (!config.isValid(configs))
      {
        return false;
      }
    }

    return true;
  }

  public synchronized ConfigTest getCurrentTest()
  {
    return currentTest;
  }

  public synchronized void setCurrentTest(ConfigTest currentTest)
  {
    this.currentTest = currentTest;
    if (containerConfig != null)
    {
      containerConfig.setCurrentTest(currentTest);
    }

    if (repositoryConfig != null)
    {
      repositoryConfig.setCurrentTest(currentTest);
    }

    if (sessionConfig != null)
    {
      sessionConfig.setCurrentTest(currentTest);
    }

    if (modelConfig != null)
    {
      modelConfig.setCurrentTest(currentTest);
    }
  }

  public synchronized void setUp() throws Exception
  {
    try
    {
      getContainerConfig().setUp();
    }
    finally
    {
      try
      {
        getRepositoryConfig().setUp();
      }
      finally
      {
        try
        {
          getSessionConfig().setUp();
        }
        finally
        {
          getModelConfig().setUp();
        }
      }
    }
  }

  public synchronized void tearDown() throws Exception
  {
    getModelConfig().tearDown();
    getSessionConfig().tearDown();
    getRepositoryConfig().tearDown();
    getContainerConfig().tearDown();
  }

  public synchronized void save()
  {
    File file = getStateFile();
    ObjectOutputStream stream = null;

    try
    {
      stream = new ObjectOutputStream(IOUtil.openOutputStream(file));
      stream.writeObject(this);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      IOUtil.close(stream);
    }
  }

  public static IScenario load()
  {
    File file = getStateFile();
    if (file.exists())
    {
      FileInputStream stream = IOUtil.openInputStream(file);

      try
      {
        return (IScenario)new ObjectInputStream(stream).readObject();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
      finally
      {
        IOUtil.close(stream);
      }
    }

    return null;
  }

  public static File getStateFile()
  {
    String home = System.getProperty("user.home");
    if (home != null)
    {
      return new File(home, STATE_FILE);
    }

    return new File(STATE_FILE);
  }

  public static IScenario getDefault()
  {
    return Default.INSTANCE;
  }

  /**
   * @author Eike Stepper
   */
  private static final class Default extends Scenario
  {
    public static final IScenario INSTANCE = new Default();

    private static final long serialVersionUID = 1L;

    private Default()
    {
      setContainerConfig(ContainerConfig.Combined.INSTANCE);
      setRepositoryConfig(RepositoryConfig.MEM.INSTANCE);
      setSessionConfig(SessionConfig.TCP.INSTANCE);
      setModelConfig(ModelConfig.Native.INSTANCE);
    }
  }
}
