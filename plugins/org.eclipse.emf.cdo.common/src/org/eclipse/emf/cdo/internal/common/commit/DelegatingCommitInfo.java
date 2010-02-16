/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class DelegatingCommitInfo implements CDOCommitInfo
{
  public DelegatingCommitInfo()
  {
  }

  protected abstract CDOCommitInfo getDelegate();

  public CDOBranch getBranch()
  {
    return getDelegate().getBranch();
  }

  public CDOCommitInfoManager getCommitInfoManager()
  {
    return getDelegate().getCommitInfoManager();
  }

  public long getTimeStamp()
  {
    return getDelegate().getTimeStamp();
  }

  public String getUserID()
  {
    return getDelegate().getUserID();
  }

  public String getComment()
  {
    return getDelegate().getComment();
  }

  public List<CDOPackageUnit> getNewPackageUnits()
  {
    return getDelegate().getNewPackageUnits();
  }

  public List<CDOIDAndVersion> getNewObjects()
  {
    return getDelegate().getNewObjects();
  }

  public List<CDORevisionKey> getChangedObjects()
  {
    return getDelegate().getChangedObjects();
  }

  public List<CDOIDAndVersion> getDetachedObjects()
  {
    return getDelegate().getDetachedObjects();
  }

  public int compareTo(CDOBranchPoint o)
  {
    return getDelegate().compareTo(o);
  }
}
