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
package org.eclipse.emf.cdo.spi.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * @author Eike Stepper
 */
public interface InternalCDOCommitInfoManager extends CDOCommitInfoManager, ILifecycle
{
  public CommitInfoLoader getCommitInfoLoader();

  public void setCommitInfoLoader(CommitInfoLoader commitInfoLoader);

  public CDOCommitInfo createCommitInfo(CDOBranch branch, long timeStamp, String userID, String comment);

  /**
   * @author Eike Stepper
   */
  public interface CommitInfoLoader
  {
    public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler);
  }
}
