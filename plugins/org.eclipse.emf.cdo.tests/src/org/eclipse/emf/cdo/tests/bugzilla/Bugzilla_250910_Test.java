/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 250910: IllegalArgumentException: created > revised
 * <p>
 * See https://bugs.eclipse.org/250910
 * 
 * @author Simon McDuff
 */
public class Bugzilla_250910_Test extends AbstractCDOTest
{
  public void testBugzilla_250910() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    Company company = getModel1Factory().createCompany();

    res.getContents().add(company);
    transaction1.commit();

    CDOID id = CDOUtil.getCDOObject(company).cdoID();

    for (int i = 0; i < 20; i++)
    {
      TestAdapter testAdapter = new TestAdapter();
      CDOSession session2 = openModel1Session();
      CDOTransaction transaction2 = session2.openTransaction();
      company.setName(String.valueOf(i));

      transaction2.getLock().lock();

      transaction1.commit();

      transaction2.options().setInvalidationNotificationEnabled(true);
      Company company2 = (Company)transaction2.getObject(id, true);
      company2.eAdapters().add(testAdapter);

      transaction2.getLock().unlock();
      assertEquals(String.valueOf(i), company2.getName());
      // Need a way to test if an error occured in the invalidation process.
    }
  }

  /**
   * @author Simon McDuff
   */
  private static class TestAdapter implements Adapter
  {
    private List<Notification> notifications = new ArrayList<Notification>();

    private Notifier notifier;

    public TestAdapter()
    {
    }

    public Notifier getTarget()
    {
      return notifier;
    }

    public boolean isAdapterForType(Object type)
    {
      return false;
    }

    public void notifyChanged(Notification notification)
    {
      notifications.add(notification);
    }

    public void setTarget(Notifier newTarget)
    {
      notifier = newTarget;
    }
  }
}
