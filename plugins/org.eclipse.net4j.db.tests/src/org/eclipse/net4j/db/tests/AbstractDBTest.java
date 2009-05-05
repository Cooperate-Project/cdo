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
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractDBTest<DATA_SOURCE extends DataSource> extends AbstractOMTest
{
  protected IDBAdapter dbAdapter;

  protected IDBConnectionProvider dbConnectionProvider;

  @SuppressWarnings("unchecked")
  @Override
  protected void doSetUp() throws Exception
  {
    dbAdapter = createDBAdapter();
    DATA_SOURCE dataSource = (DATA_SOURCE)dbAdapter.createJDBCDataSource();
    configureDataSource(dataSource);
    dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);
  }

  @Override
  protected void doTearDown() throws Exception
  {
  }

  protected abstract IDBAdapter createDBAdapter();

  protected abstract void configureDataSource(DATA_SOURCE dataSource);

  protected Connection getConnection()
  {
    return dbConnectionProvider.getConnection();
  }

  public void testDBTypes() throws Exception
  {
    IDBSchema schema = DBUtil.createSchema("testDBTypes");
    DBType[] dbTypes = DBType.values();

    int count = 0;
    int i = 0;
    for (DBType dbType : dbTypes)
    {
      IDBTable table = schema.addTable("table_" + i);
      table.addField("field", dbType);
      ++count;

      if (dbAdapter.isTypeIndexable(dbType))
      {
        IDBTable idx_table = schema.addTable("idx_table" + i);
        IDBField idx_field = idx_table.addField("field", dbType);
        idx_table.addIndex(IDBIndex.Type.NON_UNIQUE, idx_field);
        ++count;

        IDBTable uni_table = schema.addTable("uni_table" + i);
        IDBField uni_field = uni_table.addField("field", dbType);
        uni_table.addIndex(IDBIndex.Type.UNIQUE, uni_field);
        ++count;

        IDBTable pk_table = schema.addTable("pk_table" + i);
        IDBField pk_field = pk_table.addField("field", dbType);
        pk_table.addIndex(IDBIndex.Type.PRIMARY_KEY, pk_field);
        ++count;
      }

      ++i;
    }

    Set<IDBTable> tables = schema.create(dbAdapter, dbConnectionProvider);
    assertEquals(count, tables.size());
  }

  public void testEscapeStrings() throws Exception
  {
    IDBSchema schema = DBUtil.createSchema("testEscapeStrings");
    IDBTable table = schema.addTable("testtable");
    IDBField field = table.addField("strval", DBType.VARCHAR, 255);
    schema.create(dbAdapter, dbConnectionProvider);

    insertString(field, "My name is 'nobody', not body");
    insertString(field, "a = 'hello'");
    insertString(field, "'hello' == a");
    insertString(field, "'hello'");
  }

  private void insertString(IDBField field, String val)
  {
    Connection connection = getConnection();
    IDBTable table = field.getTable();

    try
    {
      DBUtil.insertRow(connection, dbAdapter, table, val);
      Object[] result = DBUtil.select(connection, (String)null, field);
      assertEquals(val, result[0]);
    }
    finally
    {
      DBUtil.update(connection, "DELETE FROM " + table);
    }
  }
}
