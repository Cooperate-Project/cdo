/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;

/**
 * @author Eike Stepper
 */
public class HsqldbTest extends AbstractDBTest<HSQLDBDataSource>
{
  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new org.eclipse.net4j.db.internal.hsqldb.HSQLDBAdapter();
  }

  @Override
  protected void configureDataSourcer(HSQLDBDataSource dataSource)
  {
    dataSource.setDatabase("jdbc:hsqldb:mem:dbtest");
    dataSource.setUser("sa");
  }
}
