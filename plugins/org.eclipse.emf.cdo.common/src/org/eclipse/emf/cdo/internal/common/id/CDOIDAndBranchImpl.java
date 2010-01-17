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
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOIDAndBranchImpl implements CDOIDAndBranch
{
  private CDOID id;

  private CDOBranch branch;

  public CDOIDAndBranchImpl(CDOID id, CDOBranch branch)
  {
    this.id = id;
    this.branch = branch;
  }

  public CDOIDAndBranchImpl(CDODataInput in) throws IOException
  {
    id = in.readCDOID();
    branch = in.readCDOBranch();
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOID(id);
    out.writeCDOBranch(branch);
  }

  public CDOID getID()
  {
    return id;
  }

  public CDOBranch getBranch()
  {
    return branch;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOIDAndBranch)
    {
      CDOIDAndBranch that = (CDOIDAndBranch)obj;
      return id.equals(that.getID()) && branch.equals(that.getBranch());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return id.hashCode() ^ branch.hashCode();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}:{1}", id, branch); //$NON-NLS-1$
  }
}
