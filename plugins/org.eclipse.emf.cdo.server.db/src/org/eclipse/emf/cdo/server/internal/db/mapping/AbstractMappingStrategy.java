/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.ObjectIDIterator;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public abstract class AbstractMappingStrategy extends Lifecycle implements IMappingStrategy
{
  protected static final String NAME_SEPARATOR = "_";

  protected static final String TYPE_PREFIX_FEATURE = "F";

  protected static final String TYPE_PREFIX_CLASS = "C";

  protected static final String TYPE_PREFIX_PACKAGE = "P";

  protected static final String FEATEURE_TABLE_SUFFIX = "_list";

  // private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractMappingStrategy.class);

  private IDBStore store;

  private Map<String, String> properties;

  private Map<Pair<EClass, EStructuralFeature>, IListMapping> listMappings;

  private Map<EClass, IClassMapping> classMappings;

  public AbstractMappingStrategy()
  {
    classMappings = new HashMap<EClass, IClassMapping>();
    listMappings = new HashMap<Pair<EClass, EStructuralFeature>, IListMapping>();
  }

  // -- property related methods -----------------------------------------

  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  private int getMaxTableNameLength()
  {
    String value = getProperties().get(PROP_MAX_TABLE_NAME_LENGTH);
    return value == null ? store.getDBAdapter().getMaxTableNameLength() : Integer.valueOf(value);
  }

  private int getMaxFieldNameLength()
  {
    String value = getProperties().get(PROP_MAX_FIELD_NAME_LENGTH);
    return value == null ? store.getDBAdapter().getMaxFieldNameLength() : Integer.valueOf(value);
  }

  private boolean isQualifiedNames()
  {
    String value = getProperties().get(PROP_QUALIFIED_NAMES);
    return value == null ? false : Boolean.valueOf(value);
  }

  private boolean isForceNamesWithID()
  {
    String value = getProperties().get(PROP_FORCE_NAMES_WITH_ID);
    return value == null ? false : Boolean.valueOf(value);
  }

  private String getTableNamePrefix()
  {
    String value = getProperties().get(PROP_TABLE_NAME_PREFIX);
    return StringUtil.safe(value);
  }

  // -- getters and setters ----------------------------------------------

  public final IDBStore getStore()
  {
    return store;
  }

  public final void setStore(IDBStore dbStore)
  {
    store = dbStore;
  }

  protected final IMetaDataManager getMetaDataManager()
  {
    return getStore().getMetaDataManager();
  }

  public abstract boolean hasAuditSupport();

  public abstract boolean hasDeltaSupport();

  // -- object id related methods ----------------------------------------

  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor dbStoreAccessor)
  {
    Collection<EClass> classes = getClassesWithObjectInfo();
    final Iterator<EClass> classIt = classes.iterator();

    return new ObjectIDIterator(this, dbStoreAccessor)
    {
      private PreparedStatement currentStatement = null;

      @Override
      protected ResultSet getNextResultSet()
      {
        while (classIt.hasNext())
        {
          EClass eClass = classIt.next();
          IClassMapping mapping = getClassMapping(eClass);
          currentStatement = mapping.createObjectIdStatement(getAccessor());

          ResultSet rset = null;
          try
          {
            rset = currentStatement.executeQuery();
            return rset;
          }
          catch (SQLException ex)
          {
            DBUtil.close(rset); // only on error
            DBUtil.close(currentStatement); // only on error
            throw new DBException(ex);
          }
        }
        return null;
      }

      @Override
      protected void closeCurrentResultSet()
      {
        super.closeCurrentResultSet();
        DBUtil.close(currentStatement);
        currentStatement = null;
      }
    };

  };

  public abstract CDOClassifierRef readObjectType(IDBStoreAccessor dbStoreAccessor, CDOID id);

  public abstract long repairAfterCrash(IDBAdapter dbAdapter, Connection connection);

  protected abstract Collection<EClass> getClassesWithObjectInfo();

  // -- resource query handling ------------------------------------------

  public abstract void queryResources(IDBStoreAccessor dbStoreAccessor, QueryResourcesContext context);

  // -- database name demangling methods ---------------------------------

  public String getTableName(ENamedElement element)
  {
    String name = null;
    String typePrefix = null;

    if (element instanceof EClass)
    {
      name = isQualifiedNames() ? EMFUtil.getQualifiedName((EClass)element, NAME_SEPARATOR) : element.getName();
      typePrefix = TYPE_PREFIX_CLASS;
    }
    else if (element instanceof EPackage)
    {
      name = isQualifiedNames() ? EMFUtil.getQualifiedName((EPackage)element, NAME_SEPARATOR) : element.getName();
      typePrefix = TYPE_PREFIX_PACKAGE;
    }
    else
    {
      throw new ImplementationError("Unknown element: " + element);
    }

    String prefix = getTableNamePrefix();
    if (prefix.length() != 0 && !prefix.endsWith(NAME_SEPARATOR))
    {
      prefix += NAME_SEPARATOR;
    }

    return getName(prefix + name, typePrefix + getMetaDataManager().getMetaID(element), getMaxTableNameLength());
  }

  public String getTableName(EClass eClass, EStructuralFeature feature)
  {
    String name = isQualifiedNames() ? EMFUtil.getQualifiedName(eClass, NAME_SEPARATOR) : eClass.getName();
    name += NAME_SEPARATOR;
    name += feature.getName();
    name += FEATEURE_TABLE_SUFFIX;

    String prefix = getTableNamePrefix();
    if (prefix.length() != 0 && !prefix.endsWith(NAME_SEPARATOR))
    {
      prefix += NAME_SEPARATOR;
    }

    return getName(prefix + name, TYPE_PREFIX_FEATURE + getMetaDataManager().getMetaID(feature),
        getMaxTableNameLength());
  }

  public String getFieldName(EStructuralFeature feature)
  {
    return getName(feature.getName(), TYPE_PREFIX_FEATURE + getMetaDataManager().getMetaID(feature),
        getMaxFieldNameLength());
  }

  private String getName(String name, String suffix, int maxLength)
  {
    boolean forceNamesWithID = isForceNamesWithID();
    if (store.getDBAdapter().isReservedWord(name))
    {
      forceNamesWithID = true;
    }

    if (name.length() > maxLength || forceNamesWithID)
    {
      suffix = NAME_SEPARATOR + suffix.replace('-', 'S');
      int length = Math.min(name.length(), maxLength - suffix.length());
      name = name.substring(0, length) + suffix;
    }

    return name;
  }

  // -- factories for mapping of classes, values, lists ------------------

  public void createMapping(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      mapPackageUnits(packageUnits);
      getStore().getDBAdapter().createTables(getModelTables(), connection);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  private void mapPackageInfos(InternalCDOPackageInfo[] packageInfos)
  {
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      EPackage ePackage = packageInfo.getEPackage();
      if (!CDOModelUtil.isCorePackage(ePackage))
      {
        mapClasses(EMFUtil.getPersistentClasses(ePackage));
      }
    }
  }

  private void mapClasses(EClass... eClasses)
  {
    for (EClass eClass : eClasses)
    {
      createClassMapping(eClass);
    }
  }

  private void mapPackageUnits(InternalCDOPackageUnit[] packageUnits)
  {
    if (packageUnits != null && packageUnits.length != 0)
    {
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        mapPackageInfos(packageUnit.getPackageInfos());
      }
    }
  }

  private Set<IDBTable> getModelTables()
  {
    Set<IDBTable> tables = new HashSet<IDBTable>();

    for (IClassMapping mapping : classMappings.values())
    {
      tables.addAll(mapping.getDBTables());
    }

    for (IListMapping mapping : listMappings.values())
    {
      tables.addAll(mapping.getDBTables());
    }

    return tables;
  }

  private IClassMapping createClassMapping(EClass eClass)
  {
    IClassMapping mapping = doCreateClassMapping(eClass);

    if (mapping != null)
    {
      classMappings.put(eClass, mapping);
    }

    return mapping;
  }

  protected abstract IClassMapping doCreateClassMapping(EClass eClass);

  public final Map<EClass, IClassMapping> getClassMappings()
  {
    return classMappings;
  }

  public final IClassMapping getClassMapping(EClass eClass)
  {
    return classMappings.get(eClass);
  }

  public ITypeMapping createValueMapping(EStructuralFeature feature)
  {
    CDOType type = CDOModelUtil.getType(feature.getEType());

    if (type == CDOType.BOOLEAN || type == CDOType.BOOLEAN_OBJECT)
    {
      return new TypeMapping.TMBoolean(this, feature);
    }
    else if (type == CDOType.BYTE || type == CDOType.BYTE_OBJECT)
    {
      return new TypeMapping.TMByte(this, feature);
    }
    else if (type == CDOType.CHAR || type == CDOType.CHARACTER_OBJECT)
    {
      return new TypeMapping.TMCharacter(this, feature);
    }
    else if (type == CDOType.DATE)
    {
      return new TypeMapping.TMDate(this, feature);
    }
    else if (type == CDOType.DOUBLE || type == CDOType.DOUBLE_OBJECT)
    {
      return new TypeMapping.TMDouble(this, feature);
    }
    else if (type == CDOType.FLOAT || type == CDOType.FLOAT_OBJECT)
    {
      return new TypeMapping.TMFloat(this, feature);
    }
    else if (type == CDOType.INT || type == CDOType.INTEGER_OBJECT)
    {
      return new TypeMapping.TMInteger(this, feature);
    }
    else if (type == CDOType.LONG || type == CDOType.LONG_OBJECT)
    {
      return new TypeMapping.TMLong(this, feature);
    }
    else if (type == CDOType.OBJECT)
    {
      return new TypeMapping.TMObject(this, feature);
    }
    else if (type == CDOType.SHORT || type == CDOType.SHORT_OBJECT)
    {
      return new TypeMapping.TMShort(this, feature);
    }
    else if (type == CDOType.ENUM)
    {
      return new TypeMapping.TMEnum(this, feature);
    }
    else if (type == CDOType.STRING || type == CDOType.CUSTOM)
    {
      return new TypeMapping.TMString(this, feature);
    }

    throw new ImplementationError("Unrecognized CDOType: " + type);
  }

  public final IListMapping createListMapping(EClass containingClass, EStructuralFeature feature)
  {
    IListMapping mapping = doCreateManyMapping(containingClass, feature);
    if (mapping != null)
    {
      listMappings.put(new Pair<EClass, EStructuralFeature>(containingClass, feature), mapping);
    }
    return mapping;
  }

  public abstract IListMapping doCreateManyMapping(EClass containingClass, EStructuralFeature feature);

  public final Map<Pair<EClass, EStructuralFeature>, IListMapping> getListMappings()
  {
    return listMappings;
  }

  public final IListMapping getListMapping(EStructuralFeature feature)
  {
    return listMappings.get(feature);
  }
}
