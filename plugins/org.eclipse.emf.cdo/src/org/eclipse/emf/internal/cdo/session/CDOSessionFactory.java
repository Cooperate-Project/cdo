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
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public class CDOSessionFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.sessions";

  public static final String TYPE = "cdo";

  private static final String TRUE = Boolean.TRUE.toString();

  public CDOSessionFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public CDOSession create(String description)
  {
    try
    {
      URI uri = new URI(description);
      String query = uri.getQuery();
      if (StringUtil.isEmpty(query))
      {
        throw new IllegalArgumentException("Query is empty: " + description);
      }

      Map<String, String> result = new HashMap<String, String>();
      StringTokenizer tokenizer = new StringTokenizer(query, "&");
      while (tokenizer.hasMoreTokens())
      {
        String parameter = tokenizer.nextToken();
        if (!StringUtil.isEmpty(parameter))
        {
          int pos = parameter.indexOf('=');
          if (pos == -1)
          {
            String key = parameter.trim();
            result.put(key, "");
          }
          else
          {
            String key = parameter.substring(0, pos).trim();
            String value = parameter.substring(pos + 1);
            result.put(key, value);
          }
        }
      }

      String repositoryName = result.get("repositoryName");
      boolean automaticPackageRegistry = TRUE.equals(result.get("automaticPackageRegistry"));
      return createSession(repositoryName, automaticPackageRegistry, null);
    }
    catch (URISyntaxException ex)
    {
      throw new IllegalArgumentException(ex);
    }
  }

  public static CDOSession get(IManagedContainer container, String description)
  {
    return (CDOSession)container.getElement(PRODUCT_GROUP, TYPE, description);
  }

  /**
   * @since 2.0
   */
  public static InternalCDOSession createSession(String repositoryName, boolean automaticPackageRegistry,
      IFailOverStrategy failOverStrategy)
  {
    InternalCDOSession session = SessionUtil.createSession();
    if (automaticPackageRegistry)
    {
      CDOPackageRegistryImpl.Eager packageRegistry = new CDOPackageRegistryImpl.Eager();
      packageRegistry.setSession(session);
      session.setPackageRegistry(packageRegistry);
    }

    session.setRepositoryName(repositoryName);
    session.getProtocol().setFailOverStrategy(failOverStrategy);
    return session;
  }
}
