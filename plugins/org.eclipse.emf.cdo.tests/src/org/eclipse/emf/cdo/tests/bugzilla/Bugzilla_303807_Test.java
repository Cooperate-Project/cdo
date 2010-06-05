/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * InvalidObjectException when switching branch
 * <p>
 * See bug 303807
 * 
 * @author Victor Roldan Betancort
 */
public class Bugzilla_303807_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    skipUnlessBranching();
  }

  public void testBugzilla_303807() throws Exception
  {
    CDOSession session = openSession();

    // Commit to main branch a new resource
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    transaction.createResource("/test1");
    transaction.commit();

    // Switch transaction to a new branch
    CDOBranch newBranch = mainBranch.createBranch("foobar");
    transaction.setBranch(newBranch);

    transaction.getRootResource().getContents().size();
  }
}
