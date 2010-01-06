/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 3.0
 */
public interface CDOSessionConfiguration extends org.eclipse.emf.cdo.session.CDOSessionConfiguration
{
  public String getRepositoryName();

  public void setRepositoryName(String repositoryName);

  public IConnector getConnector();

  public void setConnector(IConnector connector);

  public IFailOverStrategy getFailOverStrategy();

  /**
   * The fail-over strategy must be set <b>before</b> the session is opened and can not be changed thereafter.
   */
  public void setFailOverStrategy(IFailOverStrategy failOverStrategy);

  /**
   * @see CDOSession#getPackageRegistry()
   */
  public CDOPackageRegistry getPackageRegistry();

  /**
   * A special package registry can be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDOSession#getPackageRegistry()
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry);

  public CDOBranchManager getBranchManager();

  public void setBranchManager(CDOBranchManager branchManager);

  /**
   * @see CDOSession#getRevisionManager()
   * @since 3.0
   */
  public CDORevisionManager getRevisionManager();

  /**
   * @see CDOSession#getRevisionManager()
   * @since 3.0
   */
  public void setRevisionManager(CDORevisionManager revisionManager);

  public org.eclipse.emf.cdo.net4j.CDOSession openSession();
}
