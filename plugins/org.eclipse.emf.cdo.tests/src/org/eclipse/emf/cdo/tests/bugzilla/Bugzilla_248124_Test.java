/***************************************************************************
 * Copyright (c) 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/

package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.util.CDOURIUtil;

/**
 * CDOView.hasResource() is not aware of deleted resources
 * <p>
 * See https://bugs.eclipse.org/248117
 * 
 * @author Victor Roldan Betancort
 */
public class Bugzilla_248124_Test extends AbstractCDOTest
{
  public void testBugzilla_248124_hasResourceWithCommit() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();
    res.delete(null);
    transaction1.commit();
    CDOView view = session.openView();
    assertFalse(view.hasResource(resourcePath));
  }

  public void testBugzilla_248124_getResourceWithCommit() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();
    res.delete(null);
    transaction1.commit();
    CDOView view = session.openView();

    try
    {
      view.getResourceSet().getResource(CDOURIUtil.createResourceURI(view, resourcePath), true);
      fail("Cannot modify view");
    }
    catch (Exception ex)
    {
    }

    CDOView transaction2 = session.openTransaction();
    CDOResource resource = (CDOResource)transaction2.getResourceSet().getResource(
        CDOURIUtil.createResourceURI(view, resourcePath), true);
    assertNew(resource, transaction2);
    // Need to implement errors
    // assertEquals(1, resource.getErrors().size());
  }

  public void testBugzilla_248124_hasResourceWithoutCommit() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());

    res.delete(null);
    assertFalse(transaction1.hasResource(resourcePath));
  }

  public void testBugzilla_248124_getResourceWithoutCommit() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();
    res.delete(null);

    CDOResource resource = (CDOResource)transaction1.getResourceSet().getResource(
        CDOURIUtil.createResourceURI(transaction1, resourcePath), true);
    assertNew(resource, transaction1);

    // Need to implement errors
    // assertEquals(1, resource.getErrors().size());
  }
}
