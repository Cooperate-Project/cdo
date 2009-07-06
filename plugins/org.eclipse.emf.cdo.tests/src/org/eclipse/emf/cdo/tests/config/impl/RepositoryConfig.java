/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IUserManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public abstract class RepositoryConfig extends Config implements IRepositoryConfig
{
  public static final String PROP_TEST_REPOSITORY = "test.repository";

  public static final String PROP_TEST_REVISION_MANAGER = "test.repository.RevisionManager";

  public static final String PROP_TEST_USER_MANAGER = "test.repository.UserManager";

  public static final String PROP_TEST_QUERY_HANDLER_PROVIDER = "test.repository.QueryHandlerProvider";

  public static final String PROP_TEST_STORE = "test.repository.Store";

  private static final long serialVersionUID = 1L;

  private transient Map<String, IRepository> repositories;

  public RepositoryConfig(String name)
  {
    super(name);
  }

  public Map<String, String> getRepositoryProperties()
  {
    Map<String, String> repositoryProperties = new HashMap<String, String>();
    initRepositoryProperties(repositoryProperties);

    Map<String, Object> testProperties = getTestProperties();
    if (testProperties != null)
    {
      for (Entry<String, Object> entry : testProperties.entrySet())
      {
        if (entry.getValue() instanceof String)
        {
          repositoryProperties.put(entry.getKey(), (String)entry.getValue());
        }
      }
    }

    return repositoryProperties;
  }

  public synchronized IRepository getRepository(String name)
  {
    IRepository repository = repositories.get(name);
    if (repository == null)
    {
      repository = getTestRepository();
      if (repository != null && !ObjectUtil.equals(repository.getName(), name))
      {
        repository = null;
      }

      if (repository == null)
      {
        repository = createRepository(name);
      }

      repositories.put(name, repository);
      LifecycleUtil.activate(repository);
    }

    return repository;
  }

  protected void initRepositoryProperties(Map<String, String> props)
  {
    props.put(Props.OVERRIDE_UUID, ""); // UUID := name !!!
    props.put(Props.CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.REVISED_LRU_CAPACITY, "10000");
  }

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    repositories = new HashMap<String, IRepository>();
    IManagedContainer serverContainer = getCurrentTest().getServerContainer();
    CDONet4jServerUtil.prepareContainer(serverContainer, new IRepositoryProvider()
    {
      public IRepository getRepository(String name)
      {
        return repositories.get(name);
      }
    });

    // Start default repository
    getRepository(REPOSITORY_NAME);
  }

  @Override
  public void tearDown() throws Exception
  {
    for (Object repository : repositories.values().toArray())
    {
      LifecycleUtil.deactivate(repository);
    }

    repositories.clear();
    repositories = null;
    super.tearDown();
  }

  protected IRepository createRepository(String name)
  {
    IStore store = getTestStore();
    if (store == null)
    {
      store = createStore();
    }

    Map<String, String> props = getRepositoryProperties();
    Repository repository = (Repository)CDOServerUtil.createRepository(name, store, props);
    IRevisionManager revisionManager = getTestRevisionManager();
    if (revisionManager != null)
    {
      repository.setRevisionManager((RevisionManager)revisionManager);
    }

    IUserManager userManager = getTestUserManager();
    if (userManager != null)
    {
      SessionManager sessionManager = new SessionManager();
      sessionManager.setUserManager(userManager);
      repository.setSessionManager(sessionManager);
    }

    IQueryHandlerProvider queryHandlerProvider = getTestQueryHandlerProvider();
    if (queryHandlerProvider != null)
    {
      repository.setQueryHandlerProvider(queryHandlerProvider);
    }

    return repository;
  }

  protected abstract IStore createStore();

  protected IRepository getTestRepository()
  {
    return (IRepository)getTestProperty(PROP_TEST_REPOSITORY);
  }

  protected IRevisionManager getTestRevisionManager()
  {
    return (IRevisionManager)getTestProperty(PROP_TEST_REVISION_MANAGER);
  }

  protected IUserManager getTestUserManager()
  {
    return (IUserManager)getTestProperty(PROP_TEST_USER_MANAGER);
  }

  protected IQueryHandlerProvider getTestQueryHandlerProvider()
  {
    return (IQueryHandlerProvider)getTestProperty(PROP_TEST_QUERY_HANDLER_PROVIDER);
  }

  protected IStore getTestStore()
  {
    return (IStore)getTestProperty(PROP_TEST_STORE);
  }

  /**
   * @author Eike Stepper
   */
  public static class MEM extends RepositoryConfig
  {
    public static final MEM INSTANCE = new MEM();

    private static final long serialVersionUID = 1L;

    public MEM()
    {
      super("MEM");
    }

    @Override
    protected IStore createStore()
    {
      return MEMStoreUtil.createMEMStore();
    }
  }
}
