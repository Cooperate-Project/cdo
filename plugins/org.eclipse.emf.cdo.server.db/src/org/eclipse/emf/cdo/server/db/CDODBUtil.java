/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings
 *      https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444
 *
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.SmartPreparedStatementCache;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalAuditMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalNonAuditMappingStrategy;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public final class CDODBUtil
{
  /**
   * @since 2.0
   */
  public static final int DEFAULT_STATEMENT_CACHE_CAPACITY = 200;

  /**
   * @since 2.0
   */
  public static final String EXT_POINT_MAPPING_STRATEGIES = "mappingStrategies"; //$NON-NLS-1$

  private CDODBUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static IDBStore createStore(IMappingStrategy mappingStrategy, IDBAdapter dbAdapter,
      IDBConnectionProvider dbConnectionProvider)
  {
    DBStore store = new DBStore();
    store.setMappingStrategy(mappingStrategy);
    store.setDBAdapter(dbAdapter);
    store.setDbConnectionProvider(dbConnectionProvider);
    mappingStrategy.setStore(store);
    return store;
  }

  /**
   * @since 2.0
   */
  public static IMappingStrategy createHorizontalMappingStrategy(boolean auditing)
  {
    if (auditing)
    {
      return new HorizontalAuditMappingStrategy();
    }

    return new HorizontalNonAuditMappingStrategy();
  }

  /**
   * Can only be used when Eclipse is running. In standalone scenarios create the mapping strategy instance by directly
   * calling the constructor of the mapping strategy class.
   * 
   * @see #createHorizontalMappingStrategy()
   * @since 2.0
   */
  public static IMappingStrategy createMappingStrategy(String type)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT_MAPPING_STRATEGIES);
    for (final IConfigurationElement element : elements)
    {
      if ("mappingStrategy".equals(element.getName())) //$NON-NLS-1$
      {
        String typeAttr = element.getAttribute("type"); //$NON-NLS-1$
        if (ObjectUtil.equals(typeAttr, type))
        {
          try
          {
            return (IMappingStrategy)element.createExecutableExtension("class"); //$NON-NLS-1$
          }
          catch (CoreException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }

    return null;
  }

  /**
   * Get the long value of a CDOID (by delegating to {@link CDOIDUtil#getLong(org.eclipse.emf.cdo.common.id.CDOID)}) In
   * addition, provide a check for external IDs which are not supported by the DBStore
   * 
   * @param id
   *          the ID to convert to long
   * @return the long value of the ID
   * @throws IllegalArgumentException
   *           if the ID is not convertibla
   * @since 2.0
   */
  public static long getLong(CDOID id)
  {
    if (id != null && id.getType() == CDOID.Type.EXTERNAL_OBJECT)
    {
      throw new IllegalArgumentException("DBStore does not support external references: " + id); //$NON-NLS-1$
    }

    return CDOIDUtil.getLong(id);
  }

  /**
   * Execute update on the given prepared statement and handle common cases of return values.
   * 
   * @param stmt
   *          the prepared statement
   * @param exactlyOne
   *          if <code>true</code>, the update count is checked to be <code>1</code>. Else the update result is only
   *          checked so that the update was successful (i.e. result code != Statement.EXECUTE_FAILED).
   * @return the update count / execution result as returned by {@link PreparedStatement#executeUpdate()}. Can be used
   *         by the caller to perform more advanced checks.
   * @throws SQLException
   *           if {@link PreparedStatement#executeUpdate()} throws it.
   * @throws IllegalStateException
   *           if the check indicated by <code>excatlyOne</code> indicates an error.
   * @since 2.0
   */
  public static int sqlUpdate(PreparedStatement stmt, boolean exactlyOne) throws SQLException
  {
    DBUtil.trace(stmt.toString());
    int result = stmt.executeUpdate();

    // basic check of update result
    if (exactlyOne && result != 1)
    {
      throw new IllegalStateException(stmt.toString() + " returned Update count " + result + " (expected: 1)"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (result == Statement.EXECUTE_FAILED)
    {
      throw new IllegalStateException(stmt.toString() + " returned EXECUTE_FAILED."); //$NON-NLS-1$
    }

    return result;
  }

  /**
   * For debugging purposes ONLY!
   * 
   * @deprecated Should only be used when debugging.
   * @since 2.0
   */
  @Deprecated
  public static void sqlDump(Connection conn, String sql)
  {
    ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDODBUtil.class);
    ResultSet rs = null;
    try
    {
      TRACER.format("Dumping output of {0}", sql); //$NON-NLS-1$
      rs = conn.createStatement().executeQuery(sql);
      int numCol = rs.getMetaData().getColumnCount();

      StringBuilder row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append(String.format("%10s | ", rs.getMetaData().getColumnLabel(c))); //$NON-NLS-1$
      }

      TRACER.trace(row.toString());

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------+--"); //$NON-NLS-1$
      }

      TRACER.trace(row.toString());

      while (rs.next())
      {
        row = new StringBuilder();
        for (int c = 1; c <= numCol; c++)
        {
          row.append(String.format("%10s | ", rs.getString(c))); //$NON-NLS-1$
        }

        TRACER.trace(row.toString());
      }

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------+-"); //$NON-NLS-1$
      }

      TRACER.trace(row.toString());
    }
    catch (SQLException ex)
    {
      // NOP
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (SQLException ex)
        {
          // NOP
        }
      }
    }
  }

  /**
   * For debugging purposes ONLY!
   * 
   * @deprecated Should only be used when debugging.
   * @since 2.0
   */
  @Deprecated
  public static void sqlDump(IDBConnectionProvider connectionProvider, String sql)
  {
    Connection connection = connectionProvider.getConnection();

    try
    {
      sqlDump(connection, sql);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * Creates a prepared statement cache with the {@link CDODBUtil#DEFAULT_STATEMENT_CACHE_CAPACITY default capacity}.
   * 
   * @since 2.0
   * @see CDODBUtil#createStatementCache(int)
   */
  public static IPreparedStatementCache createStatementCache()
  {
    return createStatementCache(DEFAULT_STATEMENT_CACHE_CAPACITY);
  }

  /**
   * Creates a prepared statement cache with the given capacity.
   * 
   * @since 2.0
   */
  public static IPreparedStatementCache createStatementCache(int capacity)
  {
    return new SmartPreparedStatementCache(capacity);
  }
}
