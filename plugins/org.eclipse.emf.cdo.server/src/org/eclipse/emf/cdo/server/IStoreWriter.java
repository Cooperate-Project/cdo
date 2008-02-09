/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=201266
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.protocol.id.CDOIDTemp;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackageManager;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IStoreWriter extends IStoreReader
{
  public IView getView();

  public void beginCommit(CommitContext context);

  public void createNewIDs(CommitContext context, CDORevision[] newObjects, CDOID[] newIDs);

  public void finishCommit(CommitContext context, CDORevision[] newObjects, CDORevision[] dirtyObjects);

  public void finishCommit(CommitContext context, CDORevision[] newObjects, CDORevisionDelta[] dirtyObjectDeltas);

  public void cancelCommit(CommitContext context);

  /**
   * Represents the state of a single, logical commit operation which is driven through multiple calls to several
   * methods on the {@link IStoreWriter} API. All these method calls get the same <code>CommitContext</code> instance
   * passed so that the implementor of the {@link IStoreWriter} can track the state and progress of the commit
   * operation.
   * 
   * @author Eike Stepper
   */
  public interface CommitContext
  {
    /**
     * Returns the ID of the transactional view (<code>ITransaction</code>) which is the scope of the commit
     * operation represented by this <code>CommitContext</code>.
     */
    public int getTransactionID();

    /**
     * Returns the time stamp of this commit operation.
     */
    public long getTimeStamp();

    /**
     * Returns the temporary, transactional package manager associated with the commit operation represented by this
     * <code>CommitContext</code>. In addition to the packages registered with the session this package manager also
     * contains the new packages that are part of this commit operation.
     */
    public CDOPackageManager getPackageManager();

    /**
     * Returns an array of the new packages that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public CDOPackage[] getNewPackages();

    /**
     * Returns the number of new objects that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public int getNumberOfNewObjects();

    /**
     * Returns the number of dirty objects that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public int getNumberOfDirtyObjects();

    /**
     * Returns an unmodifiable list of the temporary meta ID ranges of the new packages as they are received by the
     * framework.
     */
    public List<CDOIDMetaRange> getMetaIDRanges();

    /**
     * Returns an unmodifiable map from all temporary IDs (meta or not) to their persistent counter parts. It is
     * initially populated with the mappings of all new meta objects.
     */
    public Map<CDOIDTemp, CDOID> getIdMappings();
  }
}
