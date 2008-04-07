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
package org.eclipse.net4j.db.ddl;

import org.eclipse.net4j.db.DBType;

/**
 * @author Eike Stepper
 */
public interface IDBTable extends IDBSchemaElement
{
  public IDBField addField(String name, DBType type);

  public IDBField addField(String name, DBType type, boolean notNull);

  public IDBField addField(String name, DBType type, int precision);

  public IDBField addField(String name, DBType type, int precision, boolean notNull);

  public IDBField addField(String name, DBType type, int precision, int scale);

  public IDBField addField(String name, DBType type, int precision, int scale, boolean notNull);

  public IDBField getField(String name);

  public IDBField getField(int index);

  public int getFieldCount();

  public IDBField[] getFields();

  public IDBIndex addIndex(IDBIndex.Type type, IDBField... fields);

  public int getIndexCount();

  public IDBIndex[] getIndices();

  public IDBIndex getPrimaryKeyIndex();

  public String sqlInsert();
}
