/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Set;

/**
 * Only deal with transaction process.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOUserTransaction
{
  /**
   * @since 3.0
   */
  public CDOCommitInfo commit() throws CommitException;

  /**
   * @since 3.0
   */
  public CDOCommitInfo commit(IProgressMonitor progressMonitor) throws CommitException;

  public void rollback();

  /**
   * Creates a save point in the {@link CDOTransaction} that can be used to roll back a part of the transaction
   * <p>
   * Save points do not involve the server side, everything is done on the client side.
   * <p>
   * 
   * @since 3.0
   */
  public CDOUserSavepoint setSavepoint();

  /**
   * @since 3.0
   */
  public CDOUserSavepoint getLastSavepoint();

  /**
   * @since 4.0
   */
  public void setCommittables(Set<EObject> committables);

  /**
   * @since 4.0
   */
  public Set<EObject> getCommittables();
}
