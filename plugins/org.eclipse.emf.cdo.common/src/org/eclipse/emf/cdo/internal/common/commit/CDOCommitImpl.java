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
import org.eclipse.emf.cdo.common.commit.CDOCommit;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;

/**
 * @author Eike Stepper
 */
public class CDOCommitImpl extends CDOBranchPointImpl implements CDOCommit
{
  private String userID;

  private String comment;

  public CDOCommitImpl(CDOBranch branch, long timeStamp, String userID, String comment)
  {
    super(branch, timeStamp);
    this.userID = userID;
    this.comment = comment;
  }

  public String getUserID()
  {
    return userID;
  }

  public String getComment()
  {
    return comment;
  }
}
