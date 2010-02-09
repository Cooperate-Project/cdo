/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Andre Dietisheim
 */
public class CDOCommitInfoManagerImpl extends Lifecycle implements InternalCDOCommitInfoManager
{
  private CommitInfoLoader commitInfoLoader;

  public CDOCommitInfoManagerImpl()
  {
  }

  public CommitInfoLoader getCommitInfoLoader()
  {
    return commitInfoLoader;
  }

  public void setCommitInfoLoader(CommitInfoLoader commitInfoLoader)
  {
    checkInactive();
    this.commitInfoLoader = commitInfoLoader;
  }

  public CDOCommitInfo createCommitInfo(CDOBranch branch, long timeStamp, String userID, String comment)
  {
    checkActive();
    return new CDOCommitInfoImpl(this, branch, timeStamp, userID, comment);
  }

  public void getCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    checkActive();
    commitInfoLoader.loadCommitInfos(branch, startTime, endTime, handler);
  }

  public void getCommitInfos(CDOBranch branch, CDOCommitInfoHandler handler)
  {
    getCommitInfos(branch, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
  }

  public void getCommitInfos(long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    getCommitInfos(null, startTime, endTime, handler);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(commitInfoLoader, "commitInfoLoader"); //$NON-NLS-1$
  }
}
