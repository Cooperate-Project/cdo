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
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBSchema;

/**
 * @author Eike Stepper
 */
public class DBField extends DBElement implements IDBField
{
  private static final int DEFAULT_PRECISION = 255;

  private DBTable table;

  private String name;

  private DBType type;

  private int precision;

  private int scale;

  private boolean notNull;

  private int position;

  public DBField(DBTable table, String name, DBType type, int precision, int scale, boolean notNull, int position)
  {
    this.table = table;
    this.name = name;
    this.type = type;
    this.precision = precision;
    this.scale = scale;
    this.notNull = notNull;
    this.position = position;
  }

  public IDBSchema getSchema()
  {
    return table.getSchema();
  }

  public DBTable getTable()
  {
    return table;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public DBType getType()
  {
    return type;
  }

  public void setType(DBType type)
  {
    this.type = type;
  }

  public int getPrecision()
  {
    return precision == DEFAULT ? DEFAULT_PRECISION : precision;
  }

  public void setPrecision(int precision)
  {
    this.precision = precision;
  }

  public int getScale()
  {
    return scale;
  }

  public void setScale(int scale)
  {
    this.scale = scale;
  }

  public boolean isNotNull()
  {
    return notNull;
  }

  public void setNotNull(boolean on)
  {
    notNull = on;
  }

  public int getPosition()
  {
    return position;
  }

  public String getFullName()
  {
    return table.getName() + "." + name;
  }

  public String formatPrecision()
  {
    return "(" + getPrecision() + ")";
  }

  public String formatPrecisionAndScale()
  {
    if (scale == DEFAULT)
    {
      return "(" + getPrecision() + ")";
    }

    return "(" + getPrecision() + ", " + scale + ")";
  }

  public void appendValue(StringBuilder builder, Object value)
  {
    type.appendValue(builder, value);
  }
}
