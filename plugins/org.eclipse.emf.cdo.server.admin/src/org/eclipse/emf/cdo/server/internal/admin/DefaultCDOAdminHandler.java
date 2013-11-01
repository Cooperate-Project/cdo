/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.admin.RepositoryConfigurationManager;
import org.eclipse.emf.cdo.server.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.server.spi.admin.CDOAdminHandler;
import org.eclipse.emf.cdo.server.spi.admin.CDOAdminHandler2;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.util.WrappedException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public class DefaultCDOAdminHandler implements CDOAdminHandler2, ContainerAware
{

  private final String type;

  private IManagedContainer container = IPluginContainer.INSTANCE;

  private RepositoryConfigurationManager configurationManager;

  public DefaultCDOAdminHandler(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return type;
  }

  public IManagedContainer getManagedContainer()
  {
    return container;
  }

  public void setManagedContainer(IManagedContainer container)
  {
    this.container = container;
  }

  public IRepository createRepository(String name, Map<String, Object> properties)
  {
    RepositoryConfigurationManager configManager = requireConfigurationManager();

    try
    {
      Document configDocument = getConfiguration(name, properties);

      IRepository result = configManager.addRepository(name, configDocument);
      LifecycleUtil.activate(result);

      OM.LOG.info("Repository created: " + name);

      return result;
    }
    catch (RuntimeException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new WrappedException("Failed to add repository.", e);
    }
  }

  public void deleteRepository(IRepository repository)
  {
    RepositoryConfigurationManager configManager = requireConfigurationManager();
    configManager.removeRepository(repository);

    OM.LOG.info("Repository deleted: " + repository.getName());
  }

  public boolean canDelete(IRepository repository)
  {
    RepositoryConfigurationManager configManager = getConfigurationManager();
    return configManager != null && configManager.canRemoveRepository(repository);
  }

  public void authenticateAdministrator() throws SecurityException
  {
    RepositoryConfigurationManager configManager = requireConfigurationManager();
    configManager.authenticateAdministrator();
  }

  protected RepositoryConfigurationManager getConfigurationManager()
  {
    if (configurationManager == null)
    {
      IManagedContainer container = requireContainer();

      if (container == null)
      {
        throw new IllegalStateException("No container."); //$NON-NLS-1$
      }

      Object[] available = container.getElements(RepositoryConfigurationManager.Factory.PRODUCT_GROUP);
      if (available.length > 0)
      {
        configurationManager = (RepositoryConfigurationManager)available[0];
        configurationManager.addListener(new LifecycleEventAdapter()
        {
          @Override
          protected void onDeactivated(ILifecycle lifecycle)
          {
            configurationManager = null;
            lifecycle.removeListener(this);
          }
        });
      }
    }

    return configurationManager;
  }

  protected IManagedContainer requireContainer()
  {
    IManagedContainer result = getManagedContainer();

    if (result == null)
    {
      throw new IllegalStateException("No container."); //$NON-NLS-1$
    }

    return result;
  }

  protected RepositoryConfigurationManager requireConfigurationManager()
  {
    RepositoryConfigurationManager result = getConfigurationManager();
    if (result == null)
    {
      throw new SecurityException("Remote administration of repositories requires a repository configuration manager."); //$NON-NLS-1$
    }
    return result;
  }

  protected Document getConfiguration(String name, Map<String, Object> properties) throws Exception
  {
    // Make a copy that we can modify
    properties = new java.util.HashMap<String, Object>(properties);

    DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    // Get some known configuration templates
    boolean hasSecurityManager = Boolean.TRUE.equals(properties.remove(CDOAdmin.PROPERTY_SECURITY_MANAGER));
    boolean hasHomeFolders = Boolean.TRUE.equals(properties.remove(CDOAdmin.PROPERTY_SECURITY_HOME_FOLDERS));

    // Parse the store XML. Do this now so that the applicable properties are removed from the map
    Element storeConfig = parseStoreConfig(docBuilder, properties);

    Document result = docBuilder.newDocument();

    Element repository = result.createElement("repository"); //$NON-NLS-1$
    repository.setAttribute("name", name); //$NON-NLS-1$
    result.appendChild(repository);

    // apply name/value properties of the repository
    for (Map.Entry<String, Object> entry : properties.entrySet())
    {
      Element property = result.createElement("property"); //$NON-NLS-1$
      property.setAttribute("name", entry.getKey()); //$NON-NLS-1$
      property.setAttribute("value", stringValue(entry.getValue())); //$NON-NLS-1$
      repository.appendChild(property);
    }

    if (hasSecurityManager)
    {
      configureSecurity(result, repository, hasHomeFolders);
    }

    Node adopted = result.adoptNode(storeConfig);
    if (adopted == null)
    {
      // OK, then import it (which creates a copy)
      adopted = result.importNode(storeConfig, true);
    }
    repository.appendChild(adopted);

    return result;
  }

  protected String stringValue(Object object)
  {
    return object == null ? "" : object.toString(); //$NON-NLS-1$
  }

  protected void configureSecurity(Document configuration, Element repository, boolean hasHomeFolders)
  {
    String realmPath = "/security"; //$NON-NLS-1$
    if (hasHomeFolders)
    {
      realmPath = realmPath + ":annotation:home(/home)"; //$NON-NLS-1$
    }

    Element securityManager = configuration.createElement("securityManager"); //$NON-NLS-1$
    securityManager.setAttribute("type", "default"); //$NON-NLS-1$ //$NON-NLS-2$
    securityManager.setAttribute("description", realmPath); //$NON-NLS-1$

    repository.appendChild(securityManager);
  }

  protected Element parseStoreConfig(DocumentBuilder documentBuilder, Map<String, Object> properties)
  {
    String storeConfigText = stringValue(properties.remove(CDOAdmin.PROPERTY_STORE_XML_CONFIG));

    try
    {
      Document document = documentBuilder.parse(new InputSource(new StringReader(storeConfigText)));
      return document.getDocumentElement();
    }
    catch (IOException e)
    {
      throw new WrappedException("I/O exception in parsing in-memory XML string", e); //$NON-NLS-1$
    }
    catch (SAXException e)
    {
      throw new IllegalArgumentException("Invalid XML store configuration.", e); //$NON-NLS-1$
    }
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   */
  public static final class Factory extends CDOAdminHandler.Factory
  {
    public static final String TYPE = "default"; //$NON-NLS-1$

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public CDOAdminHandler create(String description) throws ProductCreationException
    {
      return new DefaultCDOAdminHandler(TYPE);
    }
  }
}
