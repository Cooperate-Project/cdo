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
package org.eclipse.emf.cdo.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOCommit extends CDOBranchPoint
{
  public String getUserID();

  public List<String> getComments();

  // public Set<CDOID> getNewResources();
  //
  // public Set<CDOID> getNewObjects();
  //
  // public Set<CDOID> getDetachedObjects();
  //
  // public Set<CDOID> getDirtyObjects();
}
