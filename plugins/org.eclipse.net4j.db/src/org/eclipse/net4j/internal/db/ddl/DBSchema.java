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
package org.eclipse.net4j.internal.db.ddl;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBSchema extends DBSchemaElement implements IDBSchema
{
  private String name;

  private Map<String, DBTable> tables = new HashMap<String, DBTable>();

  private boolean locked;

  public DBSchema(String name)
  {
    this.name = name;
  }

  public String getFullName()
  {
    return name;
  }

  public IDBSchema getSchema()
  {
    return this;
  }

  public String getName()
  {
    return name;
  }

  public DBTable addTable(String name) throws DBException
  {
    assertUnlocked();
    if (tables.containsKey(name))
    {
      throw new DBException("DBTable exists: " + name);
    }

    DBTable table = new DBTable(this, name);
    tables.put(name, table);
    return table;
  }

  public DBTable getTable(String name)
  {
    return tables.get(name);
  }

  public DBTable[] getTables()
  {
    return tables.values().toArray(new DBTable[tables.size()]);
  }

  public boolean isLocked()
  {
    return locked;
  }

  public boolean lock()
  {
    return locked = true;
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    return dbAdapter.createTables(tables.values(), connection);
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      return create(dbAdapter, connection);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    return create(dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  void assertUnlocked() throws DBException
  {
    if (locked)
    {
      throw new DBException("DBSchema locked: " + name);
    }
  }
}
