/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.internal.db.jdbc.AbstractJDBCDelegate;

import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * Interface for all JDBC related activities regarding revisions.
 * 
 * @author Stefan Winkler
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients. Please extend the abstract class
 *              {@link AbstractJDBCDelegate} instead.
 */
public interface IJDBCDelegate
{
  /**
   * Insert a reference row. Note: this is likely to be replaced by an implementation that supports storing multiple
   * references in one batch.
   */
  public void insertReference(CDORevision sourceRevision, int index, CDOID targetId, IReferenceMapping referenceMapping);

  /**
   * Insert an attribute row.
   */
  public void insertAttributes(CDORevision revision, IClassMapping classMapping);

  /**
   * Set the revised date of a specific revision's previous version.
   */
  public void updateRevised(CDORevision revision, IClassMapping classMapping);

  /**
   * Set the revised date of all unrevised rows of cdoid
   */
  public void updateRevised(CDOID cdoid, long revised, IClassMapping classMapping);

  /**
   * Select a revision's attributes
   * 
   * @return <code>true</code> if the revision attributes have been successfully loaded.<br>
   *         <code>false</code> if the revision does not exist in the database.
   */
  public boolean selectRevisionAttributes(CDORevision revision, IClassMapping classMapping, String where);

  /**
   * Select a revision's references (or a part thereof)
   */
  public void selectRevisionReferences(CDORevision revision, IReferenceMapping referenceMapping, int referenceChunk);

  /**
   * Select a revision's reference's chunks
   */
  public void selectRevisionReferenceChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks,
      IReferenceMapping referenceMapping, String where);

  /**
   * Get the connection object of this JDBC delegate
   */
  public Connection getConnection();

  /**
   * Get the one omnipresent statement object of this JDBC delegate
   */
  public Statement getStatement();

  /**
   * Do any outstanding writes (e.g. execute batches). Called any number of times - but at least once immediately before
   * commit().
   * 
   * @see IStoreAccessor#write(org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext, OMMonitor)
   */
  public void flush(OMMonitor monitor);

  /**
   * Do a commit on the JDBC connection.
   */
  public void commit(OMMonitor monitor);

  /**
   * Do a rollback on the JDBC connection.
   */
  public void rollback();

  /**
   * Get a prepared statement. The caller is responsible of closing it.
   */
  public PreparedStatement getPreparedStatement(String sql);

  /**
   * Set a connection provider to provide the delegate with the DB connection. This may only be called before
   * activation.
   */
  public void setConnectionProvider(IDBConnectionProvider connectionProvider);

  /**
   * Set a flag indicating that this delegate maintains a read-only DB connection. This may only be called before
   * activation.
   */
  public void setReadOnly(boolean reader);
}
