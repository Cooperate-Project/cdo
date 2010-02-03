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
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.hibernate.CDOHibernateUtil;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.teneo.TeneoUtil;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.WrappedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class HibernateConfig extends RepositoryConfig
{
  public static final HibernateConfig INSTANCE = new HibernateConfig();

  public static final String MAPPING_FILE = "mappingfile";

  private static final long serialVersionUID = 1L;

  private Map<String, String> additionalProperties = new HashMap<String, String>();

  public HibernateConfig()
  {
    super("Hibernate");
  }

  @Override
  protected void initRepositoryProperties(Map<String, String> props)
  {
    super.initRepositoryProperties(props);
    props.put(Props.SUPPORTING_AUDITS, "false");
    props.put(Props.VERIFYING_REVISIONS, "false");

    try
    {
      final Properties teneoProperties = new Properties();
      teneoProperties.putAll(getAdditionalProperties());
      teneoProperties.load(getClass().getResourceAsStream("/app.properties"));
      for (Object key : teneoProperties.keySet())
      {
        props.put((String)key, teneoProperties.getProperty((String)key));
      }
    }
    catch (Exception e)
    {
      throw WrappedException.wrap(e);
    }
  }

  @Override
  protected IStore createStore(String repoName)
  {
    IHibernateMappingProvider mappingProvider = TeneoUtil.createMappingProvider();
    return CDOHibernateUtil.createStore(mappingProvider);
  }

  public Map<String, String> getAdditionalProperties()
  {
    return additionalProperties;
  }
}
