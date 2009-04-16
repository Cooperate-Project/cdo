/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444  
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBType;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This is a list-table mapping for audit mode. It has ID and version columns and no delta support.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public class AuditListTableMapping extends AbstractListTableMapping
{
  private static final FieldInfo[] KEY_FIELDS = { new FieldInfo(CDODBSchema.LIST_REVISION_ID, DBType.BIGINT),
      new FieldInfo(CDODBSchema.LIST_REVISION_VERSION, DBType.INTEGER) };

  public AuditListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
  }

  @Override
  protected FieldInfo[] getKeyFields()
  {
    return KEY_FIELDS;
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    stmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
    stmt.setInt(2, revision.getVersion());
  }

  public void objectRevised(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    // the audit list mapping does not care about revised references -> NOP
  }
}
