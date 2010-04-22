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
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class Net4jDBTest extends AbstractCDOTest
{
  private static final String TABLE_NAME = "testTable";

  private static final String FIELD_NAME = "testField";

  private DBStore store;

  private ArrayList<Pair<DBType, Object>> columns = new ArrayList<Pair<DBType, Object>>();

  private Connection connection;

  public void testBigInt() throws Exception
  {
    registerColumn(DBType.BIGINT, Long.MAX_VALUE);
    registerColumn(DBType.BIGINT, Long.MIN_VALUE);
    registerColumn(DBType.BIGINT, 0L);
    registerColumn(DBType.BIGINT, 42L);
    doTest(TABLE_NAME);
  }

  public void testBinary() throws Exception
  {
    registerColumn(DBType.BINARY, new byte[0]);

    byte[] data = new byte[100];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.BINARY, data);
    doTest(TABLE_NAME);
  }

  public void testVarBinary() throws Exception
  {
    registerColumn(DBType.VARBINARY, new byte[0]);

    byte[] data = new byte[100];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.VARBINARY, data);
    doTest(TABLE_NAME);
  }

  public void testLongVarBinary() throws Exception
  {
    registerColumn(DBType.LONGVARBINARY, new byte[0]);

    byte[] data = new byte[100];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.LONGVARBINARY, data);
    doTest(TABLE_NAME);
  }

  public void testBit() throws Exception
  {
    registerColumn(DBType.BIT, true);
    registerColumn(DBType.BIT, false);
    doTest(TABLE_NAME);
  }

  public void testBlob() throws Exception
  {
    registerColumn(DBType.BLOB, new byte[0]);

    byte[] data = new byte[1000000];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.BLOB, data);
    doTest(TABLE_NAME);
  }

  public void testBoolean() throws Exception
  {
    registerColumn(DBType.BOOLEAN, true);
    registerColumn(DBType.BOOLEAN, false);
    doTest(TABLE_NAME);
  }

  public void testChar() throws Exception
  {
    registerColumn(DBType.CHAR, "0");
    registerColumn(DBType.CHAR, "a");
    registerColumn(DBType.CHAR, "\255");
    registerColumn(DBType.CHAR, "\u1234");
    doTest(TABLE_NAME);
  }

  public void testClob() throws Exception
  {
    registerColumn(DBType.CLOB, "");
    registerColumn(DBType.CLOB, "Test");

    StringBuffer b = new StringBuffer();
    for (int i = 0; i < 1000000; i++)
    {
      b.append("x");
    }

    registerColumn(DBType.CLOB, b.toString());
    doTest(TABLE_NAME);
  }

  public void testTinyInt() throws Exception
  {
    registerColumn(DBType.TINYINT, Byte.MAX_VALUE);
    registerColumn(DBType.TINYINT, Byte.MIN_VALUE);
    registerColumn(DBType.TINYINT, new Byte("0"));
    registerColumn(DBType.TINYINT, new Integer(42).byteValue());
    doTest(TABLE_NAME);
  }

  public void testSmallInt() throws Exception
  {
    registerColumn(DBType.SMALLINT, Short.MAX_VALUE);
    registerColumn(DBType.SMALLINT, Short.MIN_VALUE);
    registerColumn(DBType.SMALLINT, (short)-1);
    registerColumn(DBType.SMALLINT, (short)5);
    doTest(TABLE_NAME);
  }

  public void testInteger() throws Exception
  {
    registerColumn(DBType.INTEGER, Integer.MAX_VALUE);
    registerColumn(DBType.INTEGER, Integer.MIN_VALUE);
    registerColumn(DBType.INTEGER, -1);
    registerColumn(DBType.INTEGER, 5);
    doTest(TABLE_NAME);
  }

  public void testFloat() throws Exception
  {
    registerColumn(DBType.FLOAT, Float.MAX_VALUE);
    registerColumn(DBType.FLOAT, Float.MIN_VALUE);
    registerColumn(DBType.FLOAT, -.1f);
    registerColumn(DBType.FLOAT, 3.33333f);
    doTest(TABLE_NAME);
  }

  public void testReal() throws Exception
  {
    registerColumn(DBType.REAL, Float.MAX_VALUE);
    registerColumn(DBType.REAL, Float.MIN_VALUE);
    registerColumn(DBType.REAL, -.1f);
    registerColumn(DBType.REAL, 3.33333f);
    doTest(TABLE_NAME);
  }

  public void testDouble() throws Exception
  {
    registerColumn(DBType.DOUBLE, new Double(Double.MAX_VALUE));
    registerColumn(DBType.DOUBLE, new Double(Double.MIN_VALUE));
    registerColumn(DBType.DOUBLE, -.1d);
    registerColumn(DBType.DOUBLE, 3.33333d);
    doTest(TABLE_NAME);
  }

  public void testNumeric() throws Exception
  {
    String numberLiteral1 = "12345678901234567890123456789012";
    String numberLiteral2 = "10000000000000000000000000000000";

    for (int precision = 1; precision < 32; precision++)
    {
      BigInteger numberInteger1 = new BigInteger(numberLiteral1.substring(0, precision));
      BigInteger numberInteger2 = new BigInteger(numberLiteral2.substring(0, precision));

      for (int scale = 0; scale <= precision; scale++)
      {
        BigDecimal numberDecimal1 = new BigDecimal(numberInteger1, scale);
        BigDecimal numberDecimal2 = new BigDecimal(numberInteger2, scale);

        registerColumn(DBType.NUMERIC, numberDecimal1);
        registerColumn(DBType.NUMERIC, numberDecimal2);

        doTest(TABLE_NAME + precision + "_" + scale);
        columns.clear();
      }
    }
  }

  public void testDecimal() throws Exception
  {
    String numberLiteral1 = "12345678901234567890123456789012";
    String numberLiteral2 = "10000000000000000000000000000000";

    for (int precision = 1; precision < 32; precision++)
    {
      BigInteger numberInteger1 = new BigInteger(numberLiteral1.substring(0, precision));
      BigInteger numberInteger2 = new BigInteger(numberLiteral2.substring(0, precision));

      for (int scale = 0; scale <= precision; scale++)
      {
        BigDecimal numberDecimal1 = new BigDecimal(numberInteger1, scale);
        BigDecimal numberDecimal2 = new BigDecimal(numberInteger2, scale);

        registerColumn(DBType.DECIMAL, numberDecimal1);
        registerColumn(DBType.DECIMAL, numberDecimal2);

        doTest(TABLE_NAME + precision + "_" + scale);
        columns.clear();
      }
    }
  }

  public void testVarChar() throws Exception
  {
    registerColumn(DBType.VARCHAR, "");
    registerColumn(DBType.VARCHAR, "\n");
    registerColumn(DBType.VARCHAR, "\t");
    registerColumn(DBType.VARCHAR, "\r");
    registerColumn(DBType.VARCHAR, "\u1234");
    registerColumn(DBType.VARCHAR, "The quick brown fox jumps over the lazy dog.");
    registerColumn(DBType.VARCHAR, "\\,:\",\'");

    doTest(TABLE_NAME);
  }

  public void testLongVarChar() throws Exception
  {
    registerColumn(DBType.LONGVARCHAR, "");
    registerColumn(DBType.LONGVARCHAR, "\n");
    registerColumn(DBType.LONGVARCHAR, "\t");
    registerColumn(DBType.LONGVARCHAR, "\r");
    registerColumn(DBType.LONGVARCHAR, "\u1234");
    registerColumn(DBType.LONGVARCHAR, "The quick brown fox jumps over the lazy dog.");
    registerColumn(DBType.LONGVARCHAR, "\\,:\",\'");

    doTest(TABLE_NAME);
  }

  public void testDate() throws Exception
  {
    registerColumn(DBType.DATE, new GregorianCalendar(2010, 04, 21).getTimeInMillis());
    registerColumn(DBType.DATE, new GregorianCalendar(1950, 04, 21).getTimeInMillis());
    registerColumn(DBType.DATE, new GregorianCalendar(2030, 12, 31).getTimeInMillis());
    registerColumn(DBType.DATE, new GregorianCalendar(0, 0, 0).getTimeInMillis());

    doTest(TABLE_NAME);
  }

  public void testTime() throws Exception
  {
    registerColumn(DBType.TIME, HOURS_toMillis(10));
    registerColumn(DBType.TIME, 0l);
    registerColumn(DBType.TIME, HOURS_toMillis(11) + MINUTES_toMillis(59) + TimeUnit.SECONDS.toMillis(59));

    // Following tests fail on H2 as 24h == 1 day => 0
    //
    // registerColumn(DBType.TIME, HOURS_toMillis(24));

    doTest(TABLE_NAME);
  }

  private long HOURS_toMillis(int hours)
  {
    return 1000L * 60L * 60L * hours;
  }

  private long MINUTES_toMillis(int minutes)
  {
    return 1000L * 60L * minutes;
  }

  public void testTimestamp() throws Exception
  {
    registerColumn(DBType.TIME, HOURS_toMillis(10));
    registerColumn(DBType.TIME, 0l);
    registerColumn(DBType.TIME, HOURS_toMillis(11) + MINUTES_toMillis(59) + TimeUnit.SECONDS.toMillis(59));

    // Following tests fail on H2 as 24h == 1 day => 0
    //
    // registerColumn(DBType.TIME, HOURS_toMillis(24));

    doTest(TABLE_NAME);
  }

  private void registerColumn(DBType type, Object value)
  {
    Pair<DBType, Object> column = new Pair<DBType, Object>(type, value);
    columns.add(column);
  }

  private void prepareTable(String tableName)
  {
    IDBSchema schema = store.getDBSchema();
    IDBTable table = schema.addTable(tableName);
    int c = 0;

    for (Pair<DBType, Object> column : columns)
    {
      switch (column.getElement1())
      {
      case NUMERIC:
      case DECIMAL:
        BigDecimal value = (BigDecimal)column.getElement2();
        table.addField(FIELD_NAME + c++, column.getElement1(), value.precision(), value.scale());
        break;

      default:
        table.addField(FIELD_NAME + c++, column.getElement1());
        break;
      }
    }

    store.getDBAdapter().createTables(Arrays.asList(table), connection);
  }

  private void writeValues(String tableName) throws Exception
  {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExtendedDataOutputStream outs = new ExtendedDataOutputStream(output);

    boolean first = true;
    StringBuilder builder = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
    for (Pair<DBType, Object> column : columns)
    {
      writeTypeValue(outs, column.getElement1(), column.getElement2());
      if (first)
      {
        builder.append("?");
        first = false;
      }
      else
      {
        builder.append(", ?");
      }
    }

    builder.append(")");
    String sql = builder.toString();

    outs.close();
    output.flush();
    byte[] buffer = output.toByteArray();
    output.close();

    ByteArrayInputStream input = new ByteArrayInputStream(buffer);
    ExtendedDataInputStream ins = new ExtendedDataInputStream(input);

    PreparedStatement stmt = connection.prepareStatement(sql);
    int c = 1;

    for (Pair<DBType, Object> column : columns)
    {
      column.getElement1().readValue(ins, stmt, c++);
    }

    stmt.executeUpdate();

    stmt.close();
    ins.close();
    input.close();
  }

  private void checkValues(String tableName) throws Exception
  {
    Statement stmt = connection.createStatement();
    ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tableName);
    assertTrue(resultSet.next());

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExtendedDataOutputStream outs = new ExtendedDataOutputStream(output);

    int c = 1;
    for (Pair<DBType, Object> column : columns)
    {
      column.getElement1().writeValue(outs, resultSet, c++);
    }

    resultSet.close();
    stmt.close();

    outs.close();
    output.flush();
    byte[] buffer = output.toByteArray();
    output.close();

    ByteArrayInputStream input = new ByteArrayInputStream(buffer);
    ExtendedDataInputStream ins = new ExtendedDataInputStream(input);

    for (Pair<DBType, Object> column : columns)
    {
      Object actual = readTypeValue(ins, column.getElement1());
      Class<? extends Object> type = column.getElement2().getClass();
      if (type.isArray())
      {
        Class<?> componentType = type.getComponentType();
        if (componentType == byte.class)
        {
          assertEquals("Error with type " + column.getElement1(), true, Arrays.equals((byte[])column.getElement2(),
              (byte[])actual));
        }
        else if (componentType == char.class)
        {
          assertEquals("Error with type " + column.getElement1(), true, Arrays.equals((char[])column.getElement2(),
              (char[])actual));
        }
        else
        {
          throw new IllegalStateException("Unexpected component type: " + componentType);
        }
      }
      else
      {
        assertEquals("Error with type " + column.getElement1(), column.getElement2(), actual);
      }
    }
  }

  private void doTest(String tableName) throws Exception
  {
    store = (DBStore)getRepository().getStore();
    connection = store.getDBConnectionProvider().getConnection();

    prepareTable(tableName);
    writeValues(tableName);
    checkValues(tableName);

    connection = null;
    store = null;
  }

  private void writeTypeValue(ExtendedDataOutputStream outs, DBType type, Object value) throws IOException
  {
    switch (type)
    {
    case BOOLEAN:
    case BIT:
      outs.writeBoolean((Boolean)value);
      return;

    case TINYINT:
      outs.writeByte((Byte)value);
      return;

    case CHAR:
      outs.writeString((String)value);
      return;

    case SMALLINT:
      outs.writeShort((Short)value);
      return;

    case INTEGER:
      outs.writeInt((Integer)value);
      return;

    case FLOAT:
      outs.writeFloat((Float)value);
      return;

    case REAL:
      outs.writeFloat((Float)value);
      return;

    case DOUBLE:
      outs.writeDouble((Double)value);
      return;

    case NUMERIC:
    case DECIMAL:
    {
      BigDecimal bigDecimal = (BigDecimal)value;
      outs.writeByteArray(bigDecimal.unscaledValue().toByteArray());
      outs.writeInt(bigDecimal.scale());
      return;
    }

    case VARCHAR:
    case LONGVARCHAR:
      outs.writeString((String)value);
      return;

    case CLOB:
    {
      long length = ((String)value).length();
      StringReader source = new StringReader((String)value);
      try
      {
        outs.writeLong(length);
        while (length-- > 0)
        {
          int c = source.read();
          outs.writeChar(c);
        }
      }
      finally
      {
        IOUtil.close(source);
      }

      return;
    }

    case BIGINT:
    case DATE:
    case TIME:
    case TIMESTAMP:
      outs.writeLong((Long)value);
      return;

    case BINARY:
    case VARBINARY:
    case LONGVARBINARY:
      outs.writeByteArray((byte[])value);
      return;

    case BLOB:
    {
      long length = ((byte[])value).length;
      ByteArrayInputStream source = new ByteArrayInputStream((byte[])value);
      try
      {
        outs.writeLong(length);
        while (length-- > 0)
        {
          int b = source.read();
          outs.writeByte(b + Byte.MIN_VALUE);
        }
      }
      finally
      {
        IOUtil.close(source);
      }

      return;
    }

    default:
      throw new UnsupportedOperationException("not implemented");
    }
  }

  private Object readTypeValue(ExtendedDataInputStream ins, DBType type) throws IOException
  {
    switch (type)
    {
    case BOOLEAN:
    case BIT:
      return ins.readBoolean();

    case CHAR:
      return ins.readString();

    case TINYINT:
      return ins.readByte();

    case SMALLINT:
      return ins.readShort();

    case INTEGER:
      return ins.readInt();

    case FLOAT:
    case REAL:
      return ins.readFloat();

    case DOUBLE:
      return ins.readDouble();

    case NUMERIC:
    case DECIMAL:
    {
      byte[] array = ins.readByteArray();
      if (array == null)
      {
        return null;
      }

      BigInteger unscaled = new BigInteger(array);
      int scale = ins.readInt();
      return new BigDecimal(unscaled, scale);
    }

    case VARCHAR:
    case LONGVARCHAR:
      return ins.readString();

    case CLOB:
    {
      StringWriter result = new StringWriter();
      try
      {
        long length = ins.readLong();
        while (length-- > 0)
        {
          char c = ins.readChar();
          result.append(c);
        }
      }
      finally
      {
        IOUtil.close(result);
      }
      return result.toString();
    }

    case DATE:
    case BIGINT:
    case TIME:
    case TIMESTAMP:
      return ins.readLong();

    case BINARY:
    case VARBINARY:
    case LONGVARBINARY:
      return ins.readByteArray();

    case BLOB:
    {
      ByteArrayOutputStream result = new ByteArrayOutputStream();

      try
      {
        long length = ins.readLong();
        while (length-- > 0)
        {
          int b = ins.readByte();
          result.write(b - Byte.MIN_VALUE);
        }
      }
      finally
      {
        IOUtil.close(result);
      }

      return result.toByteArray();
    }

    default:
      throw new UnsupportedOperationException("not implemented");
    }
  }
}
