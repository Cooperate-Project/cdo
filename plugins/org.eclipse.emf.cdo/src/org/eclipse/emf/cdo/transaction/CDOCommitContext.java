/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;

import java.util.List;
import java.util.Map;

/**
 * Provides a context for commit operations.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOCommitContext
{
  /**
   * Returns the {@link CDOTransaction transaction} associated with this commit context.
   */
  public CDOTransaction getTransaction();

  /**
   * Returns a list of the new {@link CDOPackage packages} that are to be committed with this commit context.
   */
  public List<CDOPackage> getNewPackages();

  /**
   * Returns a map of the new {@link CDOResource resources} that are to be committed with this commit context.
   */
  public Map<CDOID, CDOResource> getNewResources();

  /**
   * Returns a map of the new {@link CDOObject objects} that are to be committed with this commit context.
   */
  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * Returns a map of the dirty {@link CDOObject objects} that are to be committed with this commit context.
   */
  public Map<CDOID, CDOObject> getDirtyObjects();

  /**
   * Returns a map of the {@link CDORevisionDelta revision deltas} that are to be committed with this commit context.
   */
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas();

  /**
   * Returns a map of the detached {@link CDOObject objects} that are to be committed with this commit context.
   */
  public Map<CDOID, CDOObject> getDetachedObjects();
}
