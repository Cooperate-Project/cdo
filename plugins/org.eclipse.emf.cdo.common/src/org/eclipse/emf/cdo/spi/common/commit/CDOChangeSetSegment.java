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
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.net4j.util.ObjectUtil;

import java.text.MessageFormat;
import java.util.LinkedList;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class CDOChangeSetSegment
{
  private CDOBranch branch;

  private long startTime;

  private long endTime;

  public CDOChangeSetSegment(CDOBranch branch, long startTime, long endTime)
  {
    this.branch = branch;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public CDOBranch getBranch()
  {
    return branch;
  }

  public long getStartTime()
  {
    return startTime;
  }

  public long getEndTime()
  {
    return endTime;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Segment[{0}, {1}, {2}]", branch, startTime, endTime); //$NON-NLS-1$
  }

  public static CDOChangeSetSegment[] createFrom(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    LinkedList<CDOChangeSetSegment> result = new LinkedList<CDOChangeSetSegment>();
    CDOBranch startBranch = startPoint.getBranch();
    CDOBranch endBranch = endPoint.getBranch();

    while (!ObjectUtil.equals(startBranch, endBranch))
    {
      CDOBranchPoint base = endBranch.getBase();
      result.addFirst(new CDOChangeSetSegment(endBranch, base.getTimeStamp(), endPoint.getTimeStamp()));
      endPoint = base;
    }

    result.addFirst(new CDOChangeSetSegment(startBranch, startPoint.getTimeStamp(), endPoint.getTimeStamp()));
    return result.toArray(new CDOChangeSetSegment[result.size()]);
  }
}
