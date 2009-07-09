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
package org.eclipse.emf.cdo.tests.db.capabilities;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;

import org.hsqldb.jdbc.jdbcDataSource;

/**
 * @author Stefan Winkler
 */
public class HsqlTest extends AbstractCapabilityTest
{
  private IDBConnectionProvider provider;

  public HsqlTest()
  {
    super("hsqldb");
    jdbcDataSource hsqlds = new jdbcDataSource();
    hsqlds.setDatabase("jdbc:hsqldb:file:c:/temp/hsql_test");
    hsqlds.setUser("sa");

    provider = DBUtil.createConnectionProvider(hsqlds);
  }

  @Override
  protected IDBConnectionProvider getConnectionProvider()
  {
    return provider;
  }
}
