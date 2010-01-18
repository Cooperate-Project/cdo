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
package org.eclipse.emf.cdo.internal.common.revision.cache.noop;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class NOOPRevisionCache extends Lifecycle implements InternalCDORevisionCache
{
  public static final NOOPRevisionCache INSTANCE = new NOOPRevisionCache();

  private static final List<CDORevision> EMPTY = Collections.emptyList();

  public NOOPRevisionCache()
  {
  }

  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return this;
  }

  public boolean isSupportingBranches()
  {
    return true;
  }

  public EClass getObjectType(CDOID id)
  {
    return null;
  }

  public List<CDORevision> getCurrentRevisions()
  {
    return EMPTY;
  }

  public InternalCDORevision getRevision(CDOID id)
  {
    return null;
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    return null;
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return null;
  }

  public boolean addRevision(CDORevision revision, ReplaceCallback callback)
  {
    CheckUtil.checkArg(revision, "revision");
    return false;
  }

  public InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    return null;
  }

  public void clear()
  {
    // Do nothing
  }
}
