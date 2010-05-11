/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 259402
 *    Stefan Winkler - Bug 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - Bug 249610: [DB] Support external references (Implementation)
 *    Stefan Winkler - Bug 289056: [DB] Exception "ERROR: relation "cdo_external_refs" does not exist" while executing test-suite for PostgreSQL
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IExternalReferenceManager;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.ITypeMappingRegistry;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingRegistry;
import org.eclipse.emf.cdo.server.internal.db.messages.Messages;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStore extends LongIDStore implements IDBStore, CDOAllRevisionsProvider
{
  public static final String TYPE = "db"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_CREATED = "org.eclipse.emf.cdo.server.db.repositoryCreated"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_STOPPED = "org.eclipse.emf.cdo.server.db.repositoryStopped"; //$NON-NLS-1$

  private static final String PROP_NEXT_LOCAL_CDOID = "org.eclipse.emf.cdo.server.db.nextLocalCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_CDOID = "org.eclipse.emf.cdo.server.db.lastCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_METAID = "org.eclipse.emf.cdo.server.db.lastMetaID"; //$NON-NLS-1$

  private static final String PROP_LAST_BRANCHID = "org.eclipse.emf.cdo.server.db.lastBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_LOCAL_BRANCHID = "org.eclipse.emf.cdo.server.db.lastLocalBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_COMMITTIME = "org.eclipse.emf.cdo.server.db.lastCommitTime"; //$NON-NLS-1$

  private static final String PROP_GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.db.gracefullyShutDown"; //$NON-NLS-1$

  private long creationTime;

  private boolean firstTime;

  private IMappingStrategy mappingStrategy;

  private ITypeMappingRegistry typeMappingRegistry;

  private IDBSchema dbSchema;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  private IMetaDataManager metaDataManager;

  private IExternalReferenceManager.Internal externalReferenceManager;

  @ExcludeFromDump
  private transient ProgressDistributor accessorWriteDistributor = new ProgressDistributor.Geometric()
  {
    @Override
    public String toString()
    {
      String result = "accessorWriteDistributor"; //$NON-NLS-1$
      if (getRepository() != null)
      {
        result += ": " + getRepository().getName(); //$NON-NLS-1$
      }

      return result;
    }
  };

  @ExcludeFromDump
  private transient StoreAccessorPool readerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient StoreAccessorPool writerPool = new StoreAccessorPool(this, null);

  public DBStore()
  {
    super(TYPE, set(ChangeFormat.REVISION, ChangeFormat.DELTA), //
        set(RevisionTemporality.AUDITING, RevisionTemporality.NONE), //
        set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public ITypeMappingRegistry getTypeMappingRegistry()
  {
    return typeMappingRegistry;
  }

  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
    mappingStrategy.setStore(this);

    setRevisionTemporality(mappingStrategy.hasAuditSupport() ? RevisionTemporality.AUDITING : RevisionTemporality.NONE);
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public void setDBAdapter(IDBAdapter dbAdapter)
  {
    this.dbAdapter = dbAdapter;
  }

  public IDBConnectionProvider getDBConnectionProvider()
  {
    return dbConnectionProvider;
  }

  public void setDbConnectionProvider(IDBConnectionProvider dbConnectionProvider)
  {
    this.dbConnectionProvider = dbConnectionProvider;
  }

  public void setDataSource(DataSource dataSource)
  {
    dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);
  }

  public IMetaDataManager getMetaDataManager()
  {
    return metaDataManager;
  }

  public IExternalReferenceManager getExternalReferenceManager()
  {
    return externalReferenceManager;
  }

  @Override
  public Set<ChangeFormat> getSupportedChangeFormats()
  {
    if (mappingStrategy.hasDeltaSupport())
    {
      return set(ChangeFormat.DELTA);
    }

    return set(ChangeFormat.REVISION);
  }

  public ProgressDistributor getAccessorWriteDistributor()
  {
    return accessorWriteDistributor;
  }

  public IDBSchema getDBSchema()
  {
    return dbSchema;
  }

  public Map<String, String> getPropertyValues(Set<String> names)
  {
    Connection connection = null;
    PreparedStatement selectStmt = null;

    try
    {
      connection = getConnection();
      selectStmt = connection.prepareStatement(CDODBSchema.SQL_SELECT_PROPERTIES);

      Map<String, String> result = new HashMap<String, String>();
      for (String name : names)
      {
        selectStmt.setString(1, name);
        ResultSet resultSet = null;

        try
        {
          resultSet = selectStmt.executeQuery();
          if (resultSet.next())
          {
            String value = resultSet.getString(1);
            result.put(name, value);
          }
        }
        finally
        {
          DBUtil.close(resultSet);
        }
      }

      return result;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(selectStmt);
      DBUtil.close(connection);
    }
  }

  public void setPropertyValues(Map<String, String> properties)
  {
    Connection connection = null;
    PreparedStatement deleteStmt = null;
    PreparedStatement insertStmt = null;

    try
    {
      connection = getConnection();
      deleteStmt = connection.prepareStatement(CDODBSchema.SQL_DELETE_PROPERTIES);
      insertStmt = connection.prepareStatement(CDODBSchema.SQL_INSERT_PROPERTIES);

      for (Entry<String, String> entry : properties.entrySet())
      {
        String name = entry.getKey();
        String value = entry.getValue();

        deleteStmt.setString(1, name);
        deleteStmt.executeUpdate();

        insertStmt.setString(1, name);
        insertStmt.setString(2, value);
        insertStmt.executeUpdate();
      }

      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(insertStmt);
      DBUtil.close(deleteStmt);
      DBUtil.close(connection);
    }
  }

  public void removePropertyValues(Set<String> names)
  {
    Connection connection = null;
    PreparedStatement deleteStmt = null;

    try
    {
      connection = getConnection();
      deleteStmt = connection.prepareStatement(CDODBSchema.SQL_DELETE_PROPERTIES);

      for (String name : names)
      {
        deleteStmt.setString(1, name);
        deleteStmt.executeUpdate();
      }

      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(deleteStmt);
      DBUtil.close(connection);
    }
  }

  @Override
  public DBStoreAccessor getReader(ISession session)
  {
    return (DBStoreAccessor)super.getReader(session);
  }

  @Override
  public DBStoreAccessor getWriter(ITransaction transaction)
  {
    return (DBStoreAccessor)super.getWriter(transaction);
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    return readerPool;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    return writerPool;
  }

  @Override
  protected DBStoreAccessor createReader(ISession session) throws DBException
  {
    return new DBStoreAccessor(this, session);
  }

  @Override
  protected DBStoreAccessor createWriter(ITransaction transaction) throws DBException
  {
    return new DBStoreAccessor(this, transaction);
  }

  protected Connection getConnection()
  {
    Connection connection = dbConnectionProvider.getConnection();
    if (connection == null)
    {
      throw new DBException("No connection from connection provider: " + dbConnectionProvider); //$NON-NLS-1$
    }

    try
    {
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    return connection;
  }

  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    final Map<CDOBranch, List<CDORevision>> result = new HashMap<CDOBranch, List<CDORevision>>();
    IDBStoreAccessor accessor = getReader(null);
    StoreThreadLocal.setAccessor(accessor);

    try
    {
      accessor.handleRevisions(null, null, CDOBranchPoint.UNSPECIFIED_DATE, new CDORevisionHandler()
      {
        public void handleRevision(CDORevision revision)
        {
          CDOBranch branch = revision.getBranch();
          List<CDORevision> list = result.get(branch);
          if (list == null)
          {
            list = new ArrayList<CDORevision>();
            result.put(branch, list);
          }

          list.add(revision);
        }
      });
    }
    finally
    {
      StoreThreadLocal.release();
    }

    return result;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public boolean isFirstTime()
  {
    return firstTime;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkNull(mappingStrategy, Messages.getString("DBStore.2")); //$NON-NLS-1$
    checkNull(dbAdapter, Messages.getString("DBStore.1")); //$NON-NLS-1$
    checkNull(dbConnectionProvider, Messages.getString("DBStore.0")); //$NON-NLS-1$

    checkState(getRevisionTemporality() == RevisionTemporality.AUDITING == mappingStrategy.hasAuditSupport(), Messages
        .getString("DBStore.7")); //$NON-NLS-1$

    checkState(getRevisionParallelism() == RevisionParallelism.BRANCHING == mappingStrategy.hasBranchingSupport(),
        Messages.getString("DBStore.11")); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    dbSchema = createSchema();
    metaDataManager = new MetaDataManager(this);
    LifecycleUtil.activate(metaDataManager);

    Connection connection = getConnection();
    LifecycleUtil.activate(mappingStrategy);

    // XXX Open issue: how to programmatically register typeMappings?
    typeMappingRegistry = new TypeMappingRegistry();
    LifecycleUtil.activate(typeMappingRegistry);

    try
    {
      Set<IDBTable> createdTables = CDODBSchema.INSTANCE.create(dbAdapter, connection);
      if (createdTables.contains(CDODBSchema.PROPERTIES))
      {
        firstStart(connection);
      }
      else
      {
        reStart(connection);
      }

      connection.commit();
    }
    finally
    {
      DBUtil.close(connection);
    }

    externalReferenceManager = createExternalReferenceManager();
    externalReferenceManager.setStore(this);
    LifecycleUtil.activate(externalReferenceManager);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(metaDataManager);
    metaDataManager = null;

    LifecycleUtil.deactivate(externalReferenceManager);
    externalReferenceManager = null;

    LifecycleUtil.deactivate(typeMappingRegistry);
    typeMappingRegistry = null;

    LifecycleUtil.deactivate(mappingStrategy);
    mappingStrategy = null;

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_GRACEFULLY_SHUT_DOWN, Boolean.TRUE.toString());
    map.put(PROP_REPOSITORY_STOPPED, Long.toString(getRepository().getTimeStamp()));
    map.put(PROP_NEXT_LOCAL_CDOID, Long.toString(getNextLocalObjectID()));
    map.put(PROP_LAST_CDOID, Long.toString(getLastObjectID()));
    map.put(PROP_LAST_METAID, Long.toString(getLastMetaID()));
    map.put(PROP_LAST_BRANCHID, Integer.toString(getLastBranchID()));
    map.put(PROP_LAST_LOCAL_BRANCHID, Integer.toString(getLastLocalBranchID()));
    map.put(PROP_LAST_COMMITTIME, Long.toString(getLastCommitTime()));
    setPropertyValues(map);

    readerPool.dispose();
    writerPool.dispose();
    super.doDeactivate();
  }

  protected void firstStart(Connection connection)
  {
    creationTime = getRepository().getTimeStamp();
    firstTime = true;

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_REPOSITORY_CREATED, Long.toString(creationTime));
    setPropertyValues(map);

    OM.LOG.info(MessageFormat.format(Messages.getString("DBStore.8"), creationTime)); //$NON-NLS-1$
  }

  protected void reStart(Connection connection)
  {
    Set<String> names = new HashSet<String>();
    names.add(PROP_REPOSITORY_CREATED);
    names.add(PROP_GRACEFULLY_SHUT_DOWN);

    Map<String, String> map = getPropertyValues(names);
    creationTime = Long.valueOf(map.get(PROP_REPOSITORY_CREATED));

    if (map.containsKey(PROP_GRACEFULLY_SHUT_DOWN))
    {
      names.clear();
      names.add(PROP_NEXT_LOCAL_CDOID);
      names.add(PROP_LAST_CDOID);
      names.add(PROP_LAST_METAID);
      names.add(PROP_LAST_BRANCHID);
      names.add(PROP_LAST_LOCAL_BRANCHID);
      names.add(PROP_LAST_COMMITTIME);
      map = getPropertyValues(names);

      setNextLocalObjectID(Long.valueOf(map.get(PROP_NEXT_LOCAL_CDOID)));
      setLastObjectID(Long.valueOf(map.get(PROP_LAST_CDOID)));
      setLastMetaID(Long.valueOf(map.get(PROP_LAST_METAID)));
      setLastBranchID(Integer.valueOf(map.get(PROP_LAST_BRANCHID)));
      setLastLocalBranchID(Integer.valueOf(map.get(PROP_LAST_LOCAL_BRANCHID)));
      setLastCommitTime(Long.valueOf(map.get(PROP_LAST_COMMITTIME)));
    }
    else
    {
      OM.LOG.info(Messages.getString("DBStore.9")); //$NON-NLS-1$
      long[] result = mappingStrategy.repairAfterCrash(dbAdapter, connection);

      setNextLocalObjectID(result[0]);
      setLastObjectID(result[1]);
      setLastMetaID(DBUtil.selectMaximumLong(connection, CDODBSchema.PACKAGE_INFOS_META_UB));

      int branchID = DBUtil.selectMaximumInt(connection, CDODBSchema.BRANCHES_ID);
      setLastBranchID(branchID > 0 ? branchID : 0);

      int localBranchID = DBUtil.selectMinimumInt(connection, CDODBSchema.BRANCHES_ID);
      setLastLocalBranchID(localBranchID < 0 ? localBranchID : 0);

      setLastCommitTime(result[2]);
      OM.LOG.info(MessageFormat.format(Messages.getString("DBStore.10"), getLastObjectID(), getLastMetaID())); //$NON-NLS-1$
    }

    removePropertyValues(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
  }

  protected IExternalReferenceManager.Internal createExternalReferenceManager()
  {
    return new ExternalReferenceManager();
  }

  protected IDBSchema createSchema()
  {
    String name = getRepository().getName();
    return new DBSchema(name);
  }
}
