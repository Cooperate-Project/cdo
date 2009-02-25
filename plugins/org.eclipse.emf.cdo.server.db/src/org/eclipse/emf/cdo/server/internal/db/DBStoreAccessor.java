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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.TODO;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.StoreAccessor;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.ProgressDistributable;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStoreAccessor extends StoreAccessor implements IDBStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreAccessor.class);

  private IJDBCDelegate jdbcDelegate;

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
          jdbcDelegate.flush(monitor.fork());
        }
      });

  public DBStoreAccessor(DBStore store, ISession session) throws DBException
  {
    super(store, session);
    initJDBCDelegate(store);
  }

  public DBStoreAccessor(DBStore store, ITransaction transaction) throws DBException
  {
    super(store, transaction);
    initJDBCDelegate(store);
  }

  private void initJDBCDelegate(DBStore store)
  {
    jdbcDelegate = store.getJDBCDelegateProvider().getJDBCDelegate();
    jdbcDelegate.setConnectionProvider(store.getDBConnectionProvider());
    jdbcDelegate.setReadOnly(isReader());
  }

  public IJDBCDelegate getJDBCDelegate()
  {
    return jdbcDelegate;
  }

  @Override
  public DBStore getStore()
  {
    return (DBStore)super.getStore();
  }

  public DBStoreChunkReader createChunkReader(CDORevision revision, EStructuralFeature feature)
  {
    return new DBStoreChunkReader(this, revision, feature);
  }

  public final Collection<CDOPackageInfo> readPackageInfos()
  {
    final Collection<CDOPackageInfo> result = new ArrayList<CDOPackageInfo>(0);
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        String packageURI = (String)values[0];
        boolean dynamic = getBoolean(values[1]);
        long lowerBound = (Long)values[2];
        long upperBound = (Long)values[3];
        CDOIDMetaRange metaIDRange = lowerBound == 0 ? null : CDOIDUtil.createMetaRange(CDOIDUtil
            .createMeta(lowerBound), (int)(upperBound - lowerBound) + 1);
        String parentURI = (String)values[4];

        result.add(new CDOPackageInfo(packageURI, parentURI, dynamic, metaIDRange));
        return true;
      }
    };

    DBUtil.select(jdbcDelegate.getConnection(), rowHandler, CDODBSchema.PACKAGES_URI, CDODBSchema.PACKAGES_DYNAMIC,
        CDODBSchema.PACKAGES_RANGE_LB, CDODBSchema.PACKAGES_RANGE_UB, CDODBSchema.PACKAGES_PARENT);
    return result;
  }

  public final void readPackage(EPackage cdoPackage)
  {
    String where = CDODBSchema.PACKAGES_URI.getName() + " = '" + cdoPackage.getNsURI() + "'";
    Object[] values = DBUtil.select(jdbcDelegate.getConnection(), where, CDODBSchema.PACKAGES_ID,
        CDODBSchema.PACKAGES_NAME);
    PackageServerInfo.setDBID(cdoPackage, (Integer)values[0]);
    ((InternalEPackage)cdoPackage).setName((String)values[1]);
    readClasses(cdoPackage);
    mapPackages(cdoPackage);
  }

  protected final void readClasses(final EPackage cdoPackage)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        int classID = (Integer)values[0];
        int classifierID = (Integer)values[1];
        String name = (String)values[2];
        boolean isAbstract = getBoolean(values[3]);
        EClass cdoClass = CDOModelUtil.createClass(cdoPackage, classifierID, name, isAbstract);
        ClassServerInfo.setDBID(cdoClass, classID);
        ((InternalEPackage)cdoPackage).addClass(cdoClass);
        readSuperTypes(cdoClass, classID);
        readFeatures(cdoClass, classID);
        return true;
      }
    };

    String where = CDODBSchema.CLASSES_PACKAGE.getName() + "=" + ServerInfo.getDBID(cdoPackage);
    DBUtil.select(jdbcDelegate.getConnection(), rowHandler, where, CDODBSchema.CLASSES_ID,
        CDODBSchema.CLASSES_CLASSIFIER, CDODBSchema.CLASSES_NAME, CDODBSchema.CLASSES_ABSTRACT);
  }

  protected final void readSuperTypes(final EClass cdoClass, int classID)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        String packageURI = (String)values[0];
        int classifierID = (Integer)values[1];
        ((InternalEClass)cdoClass).addSuperType(CDOModelUtil.createClassRef(packageURI, classifierID));
        return true;
      }
    };

    String where = CDODBSchema.SUPERTYPES_TYPE.getName() + "=" + classID;
    DBUtil.select(jdbcDelegate.getConnection(), rowHandler, where, CDODBSchema.SUPERTYPES_SUPERTYPE_PACKAGE,
        CDODBSchema.SUPERTYPES_SUPERTYPE_CLASSIFIER);
  }

  protected final void readFeatures(final EClass cdoClass, int classID)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        int featureID = (Integer)values[1];
        String name = (String)values[2];
        CDOType type = CDOModelUtil.getType((Integer)values[3]);
        boolean many = getBoolean(values[6]);

        EStructuralFeature feature;
        if (type == CDOType.OBJECT)
        {
          String packageURI = (String)values[4];
          int classifierID = (Integer)values[5];
          boolean containment = getBoolean(values[7]);
          CDOClassifierRef classRef = CDOModelUtil.createClassRef(packageURI, classifierID);
          EClassProxy referenceType = new EClassProxy(classRef, cdoClass.getPackageUnitManager());
          feature = CDOModelUtil.createReference(cdoClass, featureID, name, referenceType, many, containment);
        }
        else
        {
          feature = CDOModelUtil.createAttribute(cdoClass, featureID, name, type, null, many);
        }

        FeatureServerInfo.setDBID(feature, (Integer)values[0]);
        ((InternalEClass)cdoClass).addFeature(feature);
        return true;
      }
    };

    String where = CDODBSchema.FEATURES_CLASS.getName() + "=" + classID;
    DBUtil.select(jdbcDelegate.getConnection(), rowHandler, where, CDODBSchema.FEATURES_ID,
        CDODBSchema.FEATURES_FEATURE, CDODBSchema.FEATURES_NAME, CDODBSchema.FEATURES_TYPE,
        CDODBSchema.FEATURES_REFERENCE_PACKAGE, CDODBSchema.FEATURES_REFERENCE_CLASSIFIER, CDODBSchema.FEATURES_MANY,
        CDODBSchema.FEATURES_CONTAINMENT);
  }

  public final void readPackageEcore(EPackage cdoPackage)
  {
    String where = CDODBSchema.PACKAGES_URI.getName() + " = '" + cdoPackage.getNsURI() + "'";
    Object[] values = DBUtil.select(jdbcDelegate.getConnection(), where, CDODBSchema.PACKAGES_ECORE);
    ((InternalEPackage)cdoPackage).setEcore((String)values[0]);
  }

  public final String readPackageURI(int packageID)
  {
    String where = CDODBSchema.PACKAGES_ID.getName() + "=" + packageID;
    Object[] uri = DBUtil.select(jdbcDelegate.getConnection(), where, CDODBSchema.PACKAGES_URI);
    return (String)uri[0];
  }

  public CloseableIterator<CDOID> readObjectIDs()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Selecting object IDs");
    }

    return getStore().getMappingStrategy().readObjectIDs(this);
  }

  public CDOClassifierRef readObjectType(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting object type: {0}", id);
    }

    return getStore().getMappingStrategy().readObjectType(this, id);
  }

  public final CDOClassifierRef readClassRef(int classID)
  {
    String where = CDODBSchema.CLASSES_ID.getName() + "=" + classID;
    Object[] res = DBUtil.select(jdbcDelegate.getConnection(), where, CDODBSchema.CLASSES_CLASSIFIER,
        CDODBSchema.CLASSES_PACKAGE);
    int classifierID = (Integer)res[0];
    String packageURI = readPackageURI((Integer)res[1]);
    return new CDOClassifierRef(packageURI, classifierID);
  }

  public CDORevision readRevision(CDOID id, int referenceChunk)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}", id);
    }

    EClass cdoClass = getObjectType(id);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(cdoClass, id);

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    if (mapping.readRevision(this, revision, referenceChunk))
    {
      return revision;
    }

    // Reading failed - revision does not exist.
    return null;
  }

  public CDORevision readRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}, timestamp={1,date} {1,time}", id, timeStamp);
    }

    EClass cdoClass = getObjectType(id);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(cdoClass, id);

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    if (mapping.readRevisionByTime(this, revision, timeStamp, referenceChunk))
    {
      return revision;
    }

    // Reading failed - revision does not exist.
    return null;
  }

  public CDORevision readRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}, version={1}", id, version);
    }

    EClass cdoClass = getObjectType(id);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(cdoClass, id);

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    if (mapping.readRevisionByVersion(this, revision, version, referenceChunk))
    {
      return revision;
    }

    // Reading failed - revision does not exist.
    return null;
  }

  /**
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

  protected EClass getObjectType(CDOID id)
  {
    // TODO Replace calls to getObjectType by optimized calls to RevisionManager.getObjectType (cache!)
    IRepository repository = getStore().getRepository();
    IPackageManager packageManager = repository.getPackageManager();
    CDOClassifierRef type = readObjectType(id);
    return (EClass)type.resolve(packageManager);
  }

  public CloseableIterator<Object> createQueryIterator(CDOQueryInfo queryInfo)
  {
    throw new UnsupportedOperationException();
  }

  public void refreshRevisions()
  {
  }

  @Override
  public void write(CommitContext context, OMMonitor monitor)
  {
    ProgressDistributor distributor = getStore().getAccessorWriteDistributor();
    distributor.run(ops, context, monitor);
  }

  @Override
  protected final void writePackages(EPackage[] cdoPackages, OMMonitor monitor)
  {
    try
    {
      monitor.begin(2);
      fillSystemTables(cdoPackages, monitor.fork());

      createModelTables(cdoPackages, monitor);
    }
    finally
    {
      monitor.done();
    }
  }

  private void fillSystemTables(EPackage[] cdoPackages, OMMonitor monitor)
  {
    // new PackageWriter(cdoPackages, monitor)
    // {
    // @Override
    // protected void writePackage(InternalEPackage cdoPackage, OMMonitor monitor)
    // {
    // int id = getStore().getNextPackageID();
    // PackageServerInfo.setDBID(cdoPackage, id);
    // if (TRACER.isEnabled())
    // {
    // TRACER.format("Writing package: {0} --> {1}", cdoPackage, id);
    // }
    //
    // String packageURI = cdoPackage.getPackageURI();
    // String name = cdoPackage.getName();
    // String ecore = cdoPackage.getEcore();
    // boolean dynamic = cdoPackage.isDynamic();
    // CDOIDMetaRange metaIDRange = cdoPackage.getMetaIDRange();
    // long lowerBound = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getLowerBound()).getLongValue();
    // long upperBound = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getUpperBound()).getLongValue();
    // String parentURI = cdoPackage.getParentURI();
    //
    // String sql = "INSERT INTO " + CDODBSchema.PACKAGES + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    // DBUtil.trace(sql);
    // PreparedStatement pstmt = null;
    //
    // monitor.begin();
    // Async async = monitor.forkAsync();
    //
    // try
    // {
    // pstmt = jdbcDelegate.getPreparedStatement(sql);
    // pstmt.setInt(1, id);
    // pstmt.setString(2, packageURI);
    // pstmt.setString(3, name);
    // pstmt.setString(4, ecore);
    // pstmt.setBoolean(5, dynamic);
    // pstmt.setLong(6, lowerBound);
    // pstmt.setLong(7, upperBound);
    // pstmt.setString(8, parentURI);
    //
    // if (pstmt.execute())
    // {
    // throw new DBException("No result set expected");
    // }
    //
    // if (pstmt.getUpdateCount() == 0)
    // {
    // throw new DBException("No row inserted into table " + CDODBSchema.PACKAGES);
    // }
    // }
    // catch (SQLException ex)
    // {
    // throw new DBException(ex);
    // }
    // finally
    // {
    // DBUtil.close(pstmt);
    // async.stop();
    // monitor.done();
    // }
    // }
    //
    // @Override
    // protected final void writeClass(InternalEClass cdoClass, OMMonitor monitor)
    // {
    // monitor.begin();
    // Async async = monitor.forkAsync();
    //
    // try
    // {
    // int id = getStore().getNextClassID();
    // ClassServerInfo.setDBID(cdoClass, id);
    //
    // EPackage cdoPackage = cdoClass.getContainingPackage();
    // int packageID = ServerInfo.getDBID(cdoPackage);
    // int classifierID = cdoClass.getClassifierID();
    // String name = cdoClass.getName();
    // boolean isAbstract = cdoClass.isAbstract();
    // DBUtil.insertRow(jdbcDelegate.getConnection(), getStore().getDBAdapter(), CDODBSchema.CLASSES, id, packageID,
    // classifierID, name, isAbstract);
    // }
    // finally
    // {
    // async.stop();
    // monitor.done();
    // }
    // }
    //
    // @Override
    // protected final void writeSuperType(InternalEClass type, EClassProxy superType, OMMonitor monitor)
    // {
    // monitor.begin();
    // Async async = monitor.forkAsync();
    //
    // try
    // {
    // int id = ClassServerInfo.getDBID(type);
    // String packageURI = superType.getPackageURI();
    // int classifierID = superType.getClassifierID();
    // DBUtil.insertRow(jdbcDelegate.getConnection(), getStore().getDBAdapter(), CDODBSchema.SUPERTYPES, id,
    // packageURI, classifierID);
    // }
    // finally
    // {
    // async.stop();
    // monitor.done();
    // }
    // }
    //
    // @Override
    // protected void writeFeature(InternalCDOFeature feature, OMMonitor monitor)
    // {
    // monitor.begin();
    // Async async = monitor.forkAsync();
    //
    // try
    // {
    // int id = getStore().getNextFeatureID();
    // FeatureServerInfo.setDBID(feature, id);
    //
    // int classID = ServerInfo.getDBID(feature.getContainingClass());
    // String name = feature.getName();
    // int featureID = feature.getFeatureID();
    // int type = feature.getType().getTypeID();
    // EClassProxy reference = feature.getReferenceTypeProxy();
    // String packageURI = reference == null ? null : reference.getPackageURI();
    // int classifierID = reference == null ? 0 : reference.getClassifierID();
    // boolean many = feature.isMany();
    // boolean containment = feature.isContainment();
    // int idx = feature.getFeatureIndex();
    // DBUtil.insertRow(jdbcDelegate.getConnection(), getStore().getDBAdapter(), CDODBSchema.FEATURES, id, classID,
    // featureID, name, type, packageURI, classifierID, many, containment, idx);
    // }
    // finally
    // {
    // async.stop();
    // monitor.done();
    // }
    // }
    // }.run();
  }

  private void createModelTables(EPackage[] cdoPackages, OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      Set<IDBTable> affectedTables = mapPackages(cdoPackages);
      getStore().getDBAdapter().createTables(affectedTables, jdbcDelegate.getConnection());
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void writeRevisionDeltas(CDORevisionDelta[] revisionDeltas, long created, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void writeRevisions(CDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      for (CDORevision revision : revisions)
      {
        writeRevision(revision, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeRevision(CDORevision revision, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision: {0}", revision);
    }

    EClass cdoClass = revision.getEClass();
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(cdoClass);
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

    EClass cdoClass = getObjectType(id);
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(cdoClass);
    mapping.detachObject(this, id, revised, monitor);
  }

  /**
   * TODO Move this somehow to DBAdapter
   */
  protected Boolean getBoolean(Object value)
  {
    if (value == null)
    {
      return null;
    }

    if (value instanceof Boolean)
    {
      return (Boolean)value;
    }

    if (value instanceof Number)
    {
      return ((Number)value).intValue() != 0;
    }

    throw new IllegalArgumentException("Not a boolean value: " + value);
  }

  protected Set<IDBTable> mapPackages(EPackage... cdoPackages)
  {
    Set<IDBTable> affectedTables = new HashSet<IDBTable>();
    if (cdoPackages != null && cdoPackages.length != 0)
    {
      for (EPackage cdoPackage : cdoPackages)
      {
        Set<IDBTable> tables = mapClasses(TODO.getAllPersistentClasses(cdoPackage));
        affectedTables.addAll(tables);
      }
    }

    return affectedTables;
  }

  protected Set<IDBTable> mapClasses(EClass... cdoClasses)
  {
    Set<IDBTable> affectedTables = new HashSet<IDBTable>();
    if (cdoClasses != null && cdoClasses.length != 0)
    {
      IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
      for (EClass cdoClass : cdoClasses)
      {
        IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
        if (mapping != null)
        {
          affectedTables.addAll(mapping.getAffectedTables());
        }
      }
    }

    return affectedTables;
  }

  public final void commit(OMMonitor monitor)
  {
    jdbcDelegate.commit(monitor);
  }

  @Override
  protected final void rollback(CommitContext context)
  {
    jdbcDelegate.rollback();
  }

  @Override
  protected void doActivate() throws Exception
  {
    LifecycleUtil.activate(jdbcDelegate);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(jdbcDelegate);
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // Do nothing
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // Do nothing
  }
}
