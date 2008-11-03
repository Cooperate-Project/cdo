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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IFeatureMapping;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class ClassMapping implements IClassMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ClassMapping.class);

  private MappingStrategy mappingStrategy;

  private CDOClass cdoClass;

  private IDBTable table;

  private Set<IDBTable> affectedTables = new HashSet<IDBTable>();

  private List<IAttributeMapping> attributeMappings;

  private List<IReferenceMapping> referenceMappings;

  private String selectPrefix;

  public ClassMapping(MappingStrategy mappingStrategy, CDOClass cdoClass, CDOFeature[] features)
  {
    this.mappingStrategy = mappingStrategy;
    this.cdoClass = cdoClass;

    String tableName = mappingStrategy.getTableName(cdoClass);
    table = addTable(tableName);
    initTable(table, hasFullRevisionInfo());
    if (features != null)
    {
      attributeMappings = createAttributeMappings(features);
      referenceMappings = createReferenceMappings(features);

      // // Special handling of CDOResource table
      // CDOResourceClass resourceClass = getResourceClass();
      // if (cdoClass == resourceClass)
      // {
      // // Create a unique ids to prevent duplicate resource paths
      // for (IAttributeMapping attributeMapping : attributeMappings)
      // {
      // if (attributeMapping.getFeature() == resourceClass.getCDOPathFeature())
      // {
      // IDBField versionField = table.getField(CDODBSchema.ATTRIBUTES_VERSION);
      // IDBField pathField = attributeMapping.getField();
      // pathField.setPrecision(760);// MYSQL key limitation 767
      // pathField.setNotNull(true);
      //
      // // TODO Provide better design for store capabilities and repository support
      // // Example: Currently a store can not specify that it does not support non-auditing mode!
      // if (false && !mappingStrategy.getStore().getRepository().isSupportingAudits())
      // {
      // // Create a unique ids to prevent duplicate resource paths
      // table.addIndex(IDBIndex.Type.UNIQUE, versionField, pathField);
      // }
      //
      // break;
      // }
      // }
      // }
    }

    selectPrefix = createSelectPrefix();
  }

  public MappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public CDOClass getCDOClass()
  {
    return cdoClass;
  }

  public IDBTable getTable()
  {
    return table;
  }

  public Set<IDBTable> getAffectedTables()
  {
    return affectedTables;
  }

  protected void initTable(IDBTable table, boolean full)
  {
    table.addField(CDODBSchema.ATTRIBUTES_ID, DBType.BIGINT, true);
    table.addField(CDODBSchema.ATTRIBUTES_VERSION, DBType.INTEGER, true);
    if (full)
    {
      table.addField(CDODBSchema.ATTRIBUTES_CLASS, DBType.INTEGER, true);
      table.addField(CDODBSchema.ATTRIBUTES_CREATED, DBType.BIGINT, true);
      table.addField(CDODBSchema.ATTRIBUTES_REVISED, DBType.BIGINT, true);
      table.addField(CDODBSchema.ATTRIBUTES_RESOURCE, DBType.BIGINT, true);
      table.addField(CDODBSchema.ATTRIBUTES_CONTAINER, DBType.BIGINT, true);
      table.addField(CDODBSchema.ATTRIBUTES_FEATURE, DBType.INTEGER, true);
    }
  }

  protected void appendRevisionInfos(StringBuilder builder, InternalCDORevision revision, boolean full)
  {
    builder.append(CDOIDUtil.getLong(revision.getID()));
    builder.append(", ");
    builder.append(revision.getVersion());
    if (full)
    {
      builder.append(", ");
      builder.append(ServerInfo.getDBID(revision.getCDOClass()));
      builder.append(", ");
      builder.append(revision.getCreated());
      builder.append(", ");
      builder.append(revision.getRevised());
      builder.append(", ");
      builder.append(CDOIDUtil.getLong(revision.getResourceID()));
      builder.append(", ");
      builder.append(CDOIDUtil.getLong((CDOID)revision.getContainerID()));
      builder.append(", ");
      builder.append(revision.getContainingFeatureID());
    }
  }

  protected int sqlUpdate(IDBStoreAccessor accessor, String sql) throws DBException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    try
    {
      Statement statement = accessor.getStatement();
      return statement.executeUpdate(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected IDBTable addTable(String name)
  {
    IDBTable table = mappingStrategy.getStore().getDBSchema().addTable(name);
    affectedTables.add(table);
    return table;
  }

  protected IDBField addField(CDOFeature cdoFeature, IDBTable table) throws DBException
  {
    String fieldName = mappingStrategy.getFieldName(cdoFeature);
    DBType fieldType = getDBType(cdoFeature);
    int fieldLength = getDBLength(cdoFeature);

    IDBField field = table.addField(fieldName, fieldType, fieldLength);
    affectedTables.add(table);
    return field;
  }

  protected DBType getDBType(CDOFeature cdoFeature)
  {
    return DBStore.getDBType(cdoFeature.getType());
  }

  protected int getDBLength(CDOFeature cdoFeature)
  {
    // Derby: The maximum length for a VARCHAR string is 32,672 characters.
    CDOType type = cdoFeature.getType();
    return type == CDOType.STRING || type == CDOType.CUSTOM ? 32672 : IDBField.DEFAULT;
  }

  protected IDBAdapter getDBAdapter()
  {
    IDBStore store = mappingStrategy.getStore();
    return store.getDBAdapter();
  }

  protected String createSelectPrefix()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");

    if (hasFullRevisionInfo())
    {
      builder.append(CDODBSchema.ATTRIBUTES_VERSION);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_FEATURE);
    }
    else
    {
      if (attributeMappings == null)
      {
        // Only references
        return null;
      }
    }

    if (attributeMappings != null)
    {
      for (IAttributeMapping attributeMapping : attributeMappings)
      {
        builder.append(", ");
        builder.append(attributeMapping.getField());
      }
    }

    builder.append(" FROM ");
    builder.append(table);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=");
    return builder.toString();
  }

  public IFeatureMapping getFeatureMapping(CDOFeature feature)
  {
    if (feature.isReference() && mappingStrategy.getToMany() != ToMany.LIKE_ATTRIBUTES)
    {
      return getReferenceMapping(feature);
    }

    return getAttributeMapping(feature);
  }

  public List<IAttributeMapping> getAttributeMappings()
  {
    return attributeMappings;
  }

  public List<IReferenceMapping> getReferenceMappings()
  {
    return referenceMappings;
  }

  public IReferenceMapping getReferenceMapping(CDOFeature feature)
  {
    // TODO Optimize this?
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      if (referenceMapping.getFeature() == feature)
      {
        return referenceMapping;
      }
    }

    return null;
  }

  public IAttributeMapping getAttributeMapping(CDOFeature feature)
  {
    // TODO Optimize this?
    for (IAttributeMapping attributeMapping : attributeMappings)
    {
      if (attributeMapping.getFeature() == feature)
      {
        return attributeMapping;
      }
    }

    return null;
  }

  protected List<IAttributeMapping> createAttributeMappings(CDOFeature[] features)
  {
    List<IAttributeMapping> attributeMappings = new ArrayList<IAttributeMapping>();
    for (CDOFeature feature : features)
    {
      if (feature.isReference())
      {
        if (!feature.isMany())
        {
          attributeMappings.add(createToOneReferenceMapping(feature));
        }
      }
      else
      {
        attributeMappings.add(createAttributeMapping(feature));
      }
    }

    return attributeMappings.isEmpty() ? null : attributeMappings;
  }

  protected List<IReferenceMapping> createReferenceMappings(CDOFeature[] features)
  {
    List<IReferenceMapping> referenceMappings = new ArrayList<IReferenceMapping>();
    for (CDOFeature feature : features)
    {
      if (feature.isReference() && feature.isMany())
      {
        referenceMappings.add(createReferenceMapping(feature));
      }
    }

    return referenceMappings.isEmpty() ? null : referenceMappings;
  }

  protected AttributeMapping createAttributeMapping(CDOFeature feature)
  {
    CDOType type = feature.getType();
    if (type == CDOType.BOOLEAN || type == CDOType.BOOLEAN_OBJECT)
    {
      return new AttributeMapping.AMBoolean(this, feature);
    }
    else if (type == CDOType.BYTE || type == CDOType.BYTE_OBJECT)
    {
      return new AttributeMapping.AMByte(this, feature);
    }
    else if (type == CDOType.CHAR || type == CDOType.CHARACTER_OBJECT)
    {
      return new AttributeMapping.AMCharacter(this, feature);
    }
    else if (type == CDOType.DATE)
    {
      return new AttributeMapping.AMDate(this, feature);
    }
    else if (type == CDOType.DOUBLE || type == CDOType.DOUBLE_OBJECT)
    {
      return new AttributeMapping.AMDouble(this, feature);
    }
    else if (type == CDOType.FLOAT || type == CDOType.FLOAT_OBJECT)
    {
      return new AttributeMapping.AMFloat(this, feature);
    }
    else if (type == CDOType.INT || type == CDOType.INTEGER_OBJECT)
    {
      return new AttributeMapping.AMInteger(this, feature);
    }
    else if (type == CDOType.LONG || type == CDOType.LONG_OBJECT)
    {
      return new AttributeMapping.AMLong(this, feature);
    }
    else if (type == CDOType.OBJECT)
    {
      return new AttributeMapping.AMObject(this, feature);
    }
    else if (type == CDOType.SHORT || type == CDOType.SHORT_OBJECT)
    {
      return new AttributeMapping.AMShort(this, feature);
    }
    else if (type == CDOType.STRING || type == CDOType.CUSTOM)
    {
      return new AttributeMapping.AMString(this, feature);
    }

    throw new ImplementationError("Unrecognized CDOType: " + type);
  }

  protected ToOneReferenceMapping createToOneReferenceMapping(CDOFeature feature)
  {
    return new ToOneReferenceMapping(this, feature);
  }

  protected ReferenceMapping createReferenceMapping(CDOFeature feature)
  {
    return new ReferenceMapping(this, feature, ToMany.PER_REFERENCE);
  }

  public Object createReferenceMappingKey(CDOFeature cdoFeature)
  {
    return cdoFeature;
  }

  protected abstract boolean hasFullRevisionInfo();

  public void writeRevision(IDBStoreAccessor accessor, CDORevision revision)
  {
    if (revision.getVersion() > 1 && hasFullRevisionInfo())
    {
      writeRevisedRow(accessor, (InternalCDORevision)revision);
    }

    if (revision.isResourceFolder() || revision.isResource())
    {
      checkDuplicateResources(accessor, revision);
    }

    // Write attribute table always (even without modeled attributes!)
    writeAttributes(accessor, (InternalCDORevision)revision);

    // Write reference tables only if they exist
    if (referenceMappings != null)
    {
      writeReferences(accessor, (InternalCDORevision)revision);
    }
  }

  protected abstract void checkDuplicateResources(IDBStoreAccessor accessor, CDORevision revision)
      throws IllegalStateException;

  public void detachObject(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    if (hasFullRevisionInfo())
    {
      writeRevisedRow(accessor, id, revised);
    }
  }

  protected void writeRevisedRow(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(table);
    builder.append(" SET ");
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=");
    builder.append(revision.getCreated() - 1);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=");
    builder.append(CDOIDUtil.getLong(revision.getID()));
    builder.append(" AND ");
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append("=");
    builder.append(revision.getVersion() - 1);
    sqlUpdate(accessor, builder.toString());
  }

  protected void writeRevisedRow(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(table);
    builder.append(" SET ");
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=");
    builder.append(revised);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=");
    builder.append(CDOIDUtil.getLong(id));
    builder.append(" AND ");
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0");
    sqlUpdate(accessor, builder.toString());
  }

  protected void writeAttributes(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append(" VALUES (");
    appendRevisionInfos(builder, revision, hasFullRevisionInfo());

    if (attributeMappings != null)
    {
      for (IAttributeMapping attributeMapping : attributeMappings)
      {
        builder.append(", ");
        attributeMapping.appendValue(builder, revision);
      }
    }

    builder.append(")");
    sqlUpdate(accessor, builder.toString());
  }

  protected void writeReferences(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.writeReference(accessor, revision);
    }
  }

  public void readRevision(IDBStoreAccessor accessor, CDORevision revision, int referenceChunk)
  {
    String where = mappingStrategy.createWhereClause(CDORevision.UNSPECIFIED_DATE);
    readRevision(accessor, (InternalCDORevision)revision, where, referenceChunk);
  }

  public void readRevisionByTime(IDBStoreAccessor accessor, CDORevision revision, long timeStamp, int referenceChunk)
  {
    String where = mappingStrategy.createWhereClause(timeStamp);
    readRevision(accessor, (InternalCDORevision)revision, where, referenceChunk);
  }

  public void readRevisionByVersion(IDBStoreAccessor accessor, CDORevision revision, int version, int referenceChunk)
  {
    String where = CDODBSchema.ATTRIBUTES_VERSION + "=" + version;
    readRevision(accessor, (InternalCDORevision)revision, where, referenceChunk);
  }

  protected void readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, String where, int referenceChunk)
  {
    // Read attribute table always (even without modeled attributes!)
    readAttributes(accessor, revision, where);

    // Read reference tables only if they exist
    if (referenceMappings != null)
    {
      readReferences(accessor, revision, referenceChunk);
    }
  }

  protected void readAttributes(IDBStoreAccessor accessor, InternalCDORevision revision, String where)
  {
    long id = CDOIDUtil.getLong(revision.getID());
    StringBuilder builder = new StringBuilder(selectPrefix);
    builder.append(id);
    builder.append(" AND (");
    builder.append(where);
    builder.append(")");

    String sql = builder.toString();
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    ResultSet resultSet = null;

    try
    {
      resultSet = accessor.getStatement().executeQuery(sql);
      if (!resultSet.next())
      {
        throw new IllegalStateException("Revision not found: " + id);
      }

      int i = 0;
      if (hasFullRevisionInfo())
      {
        revision.setVersion(resultSet.getInt(++i));
        revision.setCreated(resultSet.getLong(++i));
        revision.setRevised(resultSet.getLong(++i));
        revision.setResourceID(CDOIDUtil.createLong(resultSet.getLong(++i)));
        revision.setContainerID(CDOIDUtil.createLong(resultSet.getLong(++i)));
        revision.setContainingFeatureID(resultSet.getInt(++i));
      }

      if (attributeMappings != null)
      {
        for (IAttributeMapping attributeMapping : attributeMappings)
        {
          attributeMapping.extractValue(resultSet, ++i, revision);
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  protected void readReferences(IDBStoreAccessor accessor, InternalCDORevision revision, int referenceChunk)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.readReference(accessor, revision, referenceChunk);
    }
  }
}
