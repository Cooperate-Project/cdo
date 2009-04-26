/**
 * Copyright (c) 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.internal.server;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.derby.EmbeddedDerbyAdapter;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.apache.derby.jdbc.EmbeddedDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 * 
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.gastro.server"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  private static IAcceptor acceptor;

  public static IRepository repository;

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator
  {
    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      OM.LOG.info("Gastro server starting");
      EmbeddedDataSource dataSource = new EmbeddedDataSource();
      dataSource.setDatabaseName("/gastro");
      dataSource.setCreateDatabase("create");

      IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy();
      IDBAdapter dbAdapter = new EmbeddedDerbyAdapter();
      IDBConnectionProvider dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);
      IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);

      Map<String, String> props = new HashMap<String, String>();
      props.put(IRepository.Props.OVERRIDE_UUID, "gastro");
      props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
      props.put(IRepository.Props.VERIFYING_REVISIONS, "false");
      props.put(IRepository.Props.CURRENT_LRU_CAPACITY, "100000");
      props.put(IRepository.Props.REVISED_LRU_CAPACITY, "10000");

      repository = CDOServerUtil.createRepository("gastro", store, props);
      CDOServerUtil.addRepository(IPluginContainer.INSTANCE, repository);
      CDOServerUtil.prepareContainer(IPluginContainer.INSTANCE);

      acceptor = (IAcceptor)IPluginContainer.INSTANCE.getElement("org.eclipse.net4j.acceptors", "tcp", "0.0.0.0:2036");
      OM.LOG.info("Gastro server started");
    }

    @Override
    protected void doStop() throws Exception
    {
      OM.LOG.info("Gastro server stopping");
      LifecycleUtil.deactivate(acceptor);
      LifecycleUtil.deactivate(repository);
      OM.LOG.info("Gastro server stopped");
    }
  }
}
