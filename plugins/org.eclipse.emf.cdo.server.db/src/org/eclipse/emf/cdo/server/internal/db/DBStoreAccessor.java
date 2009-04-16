/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.ProgressDistributable;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class DBStoreAccessor extends LongIDStoreAccessor implements IDBStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreAccessor.class);

  private Connection connection = null;

  @ExcludeFromDump
  @SuppressWarnings("unchecked")
  private final ProgressDistributable<CommitContext>[] ops = ProgressDistributor.array( //
      new ProgressDistributable.Default<CommitContext>()
      {
        public void runLoop(int index, CommitContext commitContext, OMMonitor monitor) throws Exception
        {
          DBStoreAccessor.super.write(commitContext, monitor.fork());
        }
      }, //
      new ProgressDistributable.Default<CommitContext>()
      {
        public void runLoop(int index, CommitContext commitContext, OMMonitor monitor) throws Exception
        {
          // TODO - reenable when reimplementing stmt caching
          // flush(monitor.fork());
        }
      });

  public DBStoreAccessor(DBStore store, ISession session) throws DBException
  {
    super(store, session);
  }

  public DBStoreAccessor(DBStore store, ITransaction transaction) throws DBException
  {
    super(store, transaction);
  }

  @Override
  public DBStore getStore()
  {
    return (DBStore)super.getStore();
  }

  /** TODO: check how to handle */
  public DBStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new DBStoreChunkReader(this, revision, feature);
  }

  /**
   * TODO caching?
   */
  public CloseableIterator<CDOID> readObjectIDs()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Selecting object IDs");
    }

    return getStore().getMappingStrategy().readObjectIDs(this);
  }

  /**
   * TODO caching?
   */
  public CDOClassifierRef readObjectType(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting object type: {0}", id);
    }

    return getStore().getMappingStrategy().readObjectType(this, id);
  }

  protected EClass getObjectType(CDOID id)
  {
    // TODO Replace calls to getObjectType by optimized calls to RevisionManager.getObjectType (cache!)
    CDOClassifierRef type = readObjectType(id);
    if (type == null)
    {
      return null;
    }

    IRepository repository = getStore().getRepository();
    CDOPackageRegistry packageRegistry = repository.getPackageRegistry();
    return (EClass)type.resolve(packageRegistry);
  }

  public InternalCDORevision readRevision(CDOID id, int referenceChunk, AdditionalRevisionCache cache)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}", id);
    }

    EClass eClass = getObjectType(id);
    if (eClass == null)
    {
      return null;
    }

    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(eClass, id);

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(eClass);
    if (mapping.readRevision(this, revision, referenceChunk))
    {
      return revision;
    }

    // Reading failed - revision does not exist.
    return null;
  }

  public InternalCDORevision readRevisionByTime(CDOID id, int referenceChunk, AdditionalRevisionCache cache,
      long timeStamp)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();

    if (!mappingStrategy.hasAuditSupport())
    {
      throw new UnsupportedOperationException("Mapping strategy does not support audits.");
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}, timestamp={1,date} {1,time}", id, timeStamp);
    }

    EClass eClass = getObjectType(id);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(eClass, id);

    IAuditSupport mapping = (IAuditSupport)mappingStrategy.getClassMapping(eClass);
    if (mapping.readRevisionByTime(this, revision, timeStamp, referenceChunk))
    {
      return revision;
    }

    // Reading failed - revision does not exist.
    return null;
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, int referenceChunk, AdditionalRevisionCache cache,
      int version)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();

    EClass eClass = getObjectType(id);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(eClass, id);
    IClassMapping mapping = mappingStrategy.getClassMapping(eClass);

    boolean success = false;

    if (mappingStrategy.hasAuditSupport())
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Selecting revision: {0}, version={1}", id, version);
      }

      // if audit support is present, just use the audit method
      success = ((IAuditSupport)mapping).readRevisionByVersion(this, revision, version, referenceChunk);
    }
    else
    {
      // if audit support is not present, we still have to provide a method
      // to readRevisionByVersion because TransactionCommitContext.computeDirtyObject
      // needs to lookup the base revision for a change. Hence we emulate this
      // behavior by getting the current revision and asserting that the version
      // has not changed. This is valid because if the version has changed,
      // we are in trouble because of a conflict anyways.
      if (TRACER.isEnabled())
      {
        TRACER.format("Selecting current base revision: {0}", id);
      }

      success = mapping.readRevision(this, revision, referenceChunk);

      if (success && revision.getVersion() != version)
      {
        throw new IllegalStateException("Can only retrieve current version " + revision.getVersion() + " for " + id
            + " - version requested was " + version + ".");
      }
    }

    return success ? revision : null;
  }

  /**
   * TODO: implement as query when query implementation is done?
   * 
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    mappingStrategy.queryResources(this, context);
  }

  /**
   * @since 2.0
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    // TODO: implement DBStoreAccessor.executeQuery(info, context)
    throw new UnsupportedOperationException();
  }

  public CloseableIterator<Object> createQueryIterator(CDOQueryInfo queryInfo)
  {
    throw new UnsupportedOperationException();
  }

  public void refreshRevisions()
  {
    // TODO is this empty on purpose or should it be implemented (how?)
  }

  @Override
  public void write(CommitContext context, OMMonitor monitor)
  {
    ProgressDistributor distributor = getStore().getAccessorWriteDistributor();
    distributor.run(ops, context, monitor);
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, long created, OMMonitor monitor)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();

    if (!mappingStrategy.hasDeltaSupport())
    {
      throw new UnsupportedOperationException("Mapping strategy does not support revision deltas.");
    }

    monitor.begin(revisionDeltas.length);
    try
    {
      for (InternalCDORevisionDelta delta : revisionDeltas)
      {
        writeRevisionDelta(delta, created, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeRevisionDelta(InternalCDORevisionDelta delta, long created, OMMonitor monitor)
  {
    EClass eClass = getObjectType(delta.getID());
    IDeltaSupport mapping = (IDeltaSupport)getStore().getMappingStrategy().getClassMapping(eClass);
    mapping.writeRevisionDelta(this, delta, created, monitor);
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      for (InternalCDORevision revision : revisions)
      {
        writeRevision(revision, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeRevision(InternalCDORevision revision, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision: {0}", revision);
    }

    EClass eClass = revision.getEClass();
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(eClass);
    mapping.writeRevision(this, revision, monitor);
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, long revised, OMMonitor monitor)
  {
    try
    {
      monitor.begin(detachedObjects.length);
      for (CDOID id : detachedObjects)
      {
        detachObject(id, revised, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @since 2.0
   */
  protected void detachObject(CDOID id, long revised, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Detaching object: {0}", id);
    }

    EClass eClass = getObjectType(id);
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(eClass);
    mapping.detachObject(this, id, revised, monitor);
  }

  public Connection getConnection()
  {
    return connection;
  }

  public final void commit(OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    if (TRACER.isEnabled())
    {
      TRACER.format("--- DB COMMIT ---");
    }

    try
    {
      getConnection().commit();

      // CDODBUtil.sqlDump(getStore().getDBConnectionProvider(), "select * from CDOResourceFolder");
      // CDODBUtil.sqlDump(getStore().getDBConnectionProvider(), "select * from CDOResource");
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected final void rollback(IStoreAccessor.CommitContext commitContext)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("--- DB ROLLBACK ---");
    }

    try
    {
      getConnection().rollback();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    connection = getStore().getDBConnectionProvider().getConnection();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    DBUtil.close(connection);
    connection = null;
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // Do nothing
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // TODO Check if connection is still valid.
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return getStore().getMetaDataManager().loadPackageUnit(getConnection(), packageUnit);
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return getStore().getMetaDataManager().readPackageUnits(getConnection());
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    monitor.begin(2);
    try
    {
      getStore().getMetaDataManager().writePackageUnits(getConnection(), packageUnits, monitor.fork());
      getStore().getMappingStrategy().createMapping(getConnection(), packageUnits, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }
}
