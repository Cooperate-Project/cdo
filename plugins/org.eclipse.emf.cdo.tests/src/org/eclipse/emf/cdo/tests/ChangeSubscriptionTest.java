/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Simon McDuff
 */
public class ChangeSubscriptionTest extends AbstractCDOTest
{
  public void testSameSession() throws Exception
  {
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    final Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    final CDOSession session = openSession();

    // ************************************************************* //

    final CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final CDOResource resourceA = transaction.createResource("/test1");
    resourceA.getContents().add(companyA);

    transaction.commit();
    final TestAdapter adapter = new TestAdapter();
    category1A.eAdapters().add(adapter);

    // ************************************************************* //

    final CDOTransaction transaction2 = session.openTransaction();

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A)
        .cdoID(), true));
    category1B.setName("CHANGED NAME");
    assertEquals(0, adapter.getNotifications().length);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // Commit notifications from the same session always have full deltas
        Notification[] notifications = adapter.getNotifications();
        return notifications.length == 1;
      }
    }.assertNoTimeOut();

    // Removing policy
    transaction.options().removeChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter.clearNotifications();

    category1B.setName("CHANGED NAME_VERSION 2");
    assertEquals(0, adapter.getNotifications().length);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // Commit notifications from the same session always have full deltas
        Notification[] notifications = adapter.getNotifications();
        return notifications.length == 1;
      }
    }.assertNoTimeOut();
  }

  public void testSameSession_WithoutPolicy() throws Exception
  {
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    final Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    final CDOSession session = openSession();

    // ************************************************************* //

    final CDOTransaction transaction = session.openTransaction();

    final CDOResource resourceA = transaction.createResource("/test1");
    resourceA.getContents().add(companyA);

    transaction.commit();
    final TestAdapter adapter = new TestAdapter();
    category1A.eAdapters().add(adapter);

    // ************************************************************* //

    final CDOTransaction transaction2 = session.openTransaction();

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A)
        .cdoID(), true));
    category1B.setName("CHANGED NAME");
    assertEquals(0, adapter.getNotifications().length);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // Commit notifications from the same session always have full deltas
        Notification[] notifications = adapter.getNotifications();
        return notifications.length == 1;
      }
    }.assertNoTimeOut();

    // Adding policy
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter.clearNotifications();

    category1B.setName("CHANGED NAME_VERSION 2");
    assertEquals(0, adapter.getNotifications().length);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // Commit notifications from the same session always have full deltas
        Notification[] notifications = adapter.getNotifications();
        return notifications.length == 1;
      }
    }.assertNoTimeOut();
  }

  public void testSeparateSession() throws Exception
  {
    Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resourceA = transaction.createResource("/test1");
    resourceA.getContents().add(companyA);
    transaction.commit();

    final TestAdapter adapter = new TestAdapter();
    category1A.eAdapters().add(adapter);

    // ************************************************************* //

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(),
        true));
    category1B.setName("CHANGED NAME");
    assertEquals(0, adapter.getNotifications().length);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // Change subscription leads to delta nnotification
        Notification[] notifications = adapter.getNotifications();
        return notifications.length == 1;
      }
    }.assertNoTimeOut();

    // Removing policy
    transaction.options().removeChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter.clearNotifications();

    category1B.setName("CHANGED NAME_VERSION 2");
    assertEquals(0, adapter.getNotifications().length);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // No change subscription, other session ==> no delta notification
        Notification[] notifications = adapter.getNotifications();
        return notifications.length != 0;
      }
    }.assertTimeOut();
  }

  public void testSeparateSession_WithoutPolicy() throws Exception
  {
    Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resourceA = transaction.createResource("/test1");
    resourceA.getContents().add(companyA);
    transaction.commit();

    final TestAdapter adapter = new TestAdapter();
    category1A.eAdapters().add(adapter);

    // ************************************************************* //

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(),
        true));
    category1B.setName("CHANGED NAME");
    assertEquals(0, adapter.getNotifications().length);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // No change subscription, other session ==> no delta notification
        Notification[] notifications = adapter.getNotifications();
        return notifications.length != 0;
      }
    }.assertTimeOut();

    // Adding policy
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter.clearNotifications();

    category1B.setName("CHANGED NAME_VERSION 2");
    assertEquals(0, adapter.getNotifications().length);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // Change subscription leads to delta nnotification
        Notification[] notifications = adapter.getNotifications();
        return notifications.length == 1;
      }
    }.assertNoTimeOut();
  }

  public void testTemporaryObject() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

    // ************************************************************* //

    msg("Creating category1");
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Creating company");
    final Company companyA = getModel1Factory().createCompany();

    msg("Adding categories");
    companyA.getCategories().add(category1A);

    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");

    final TestAdapter adapter = new TestAdapter();
    category1A.eAdapters().add(adapter);

    transaction.commit();

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openSession();
    final CDOTransaction transaction2 = session2.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A)
        .cdoID(), true));

    msg("Changing name");
    category1B.setName("CHANGED NAME");

    assertEquals(0, adapter.getNotifications().length);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().length == 1;
      }
    }.assertNoTimeOut();
  }

  public void testSeparateSession_CUSTOM() throws Exception
  {
    CDOIDFilterChangeSubscriptionPolicy customPolicy = new CDOIDFilterChangeSubscriptionPolicy();

    msg("Opening session");
    final CDOSession session = openSession();

    // ************************************************************* //

    msg("Creating category1");
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Creating company");
    final Company companyA = getModel1Factory().createCompany();

    msg("Adding categories");
    companyA.getCategories().add(category1A);

    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();

    transaction.options().addChangeSubscriptionPolicy(customPolicy);

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    final TestAdapter adapter = new TestAdapter();

    customPolicy.getCdoIDs().add(CDOUtil.getCDOObject(category1A).cdoID());

    category1A.eAdapters().add(adapter);
    companyA.eAdapters().add(adapter);

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openSession();
    final CDOTransaction transaction2 = session2.openTransaction();

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A)
        .cdoID(), true));
    final Company company1B = (Company)CDOUtil.getEObject(transaction2.getObject(
        CDOUtil.getCDOObject(companyA).cdoID(), true));

    msg("Changing name");
    category1B.setName("CHANGED NAME");
    company1B.setName("TEST1");

    assertEquals(0, adapter.getNotifications().length);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().length == 1;
      }
    }.assertNoTimeOut();

    // Switching policy to the other
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    adapter.clearNotifications();

    msg("Changing name");
    category1B.setName("CHANGED NAME_VERSION 2");
    company1B.setName("TEST2");

    assertEquals(0, adapter.getNotifications().length);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().length == 2;
      }
    }.assertNoTimeOut();
  }

  public void testNotificationChain() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

    // ************************************************************* //

    msg("Creating category1");
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Creating company");
    final Company companyA = getModel1Factory().createCompany();

    msg("Adding categories");
    companyA.getCategories().add(category1A);

    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();

    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    final TestAdapter adapter = new TestAdapter();

    companyA.eAdapters().add(adapter);

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openSession();
    final CDOTransaction transaction2 = session2.openTransaction();

    final Company company1B = (Company)CDOUtil.getEObject(transaction2.getObject(
        CDOUtil.getCDOObject(companyA).cdoID(), true));

    msg("Changing name");
    company1B.setName("TEST1");
    company1B.setCity("CITY1");

    final Category category2B = getModel1Factory().createCategory();
    company1B.getCategories().add(category2B);

    assertEquals(0, adapter.getNotifications().length);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().length == 3;
      }
    }.assertNoTimeOut();

    int count = 0;
    for (Notification notification : adapter.getNotifications())
    {
      CDODeltaNotification cdoNotification = (CDODeltaNotification)notification;
      if (adapter.getNotifications().length - 1 == count)
      {
        assertEquals(false, cdoNotification.hasNext());
      }
      else
      {
        assertEquals(true, cdoNotification.hasNext());
      }

      if (notification.getFeature() == getModel1Package().getCategory_Name())
      {
        assertEquals(Notification.SET, notification.getEventType());
        assertEquals("TEST1", notification.getNewStringValue());
      }
      else if (notification.getFeature() == getModel1Package().getAddress_City())
      {
        assertEquals(Notification.SET, notification.getEventType());
        assertEquals("CITY1", notification.getNewStringValue());
      }
      else if (notification.getFeature() == getModel1Package().getCompany_Categories())
      {
        assertEquals(Notification.ADD, notification.getEventType());
        assertEquals(1, notification.getPosition());
        assertEquals(transaction.getObject(CDOUtil.getCDOObject(category2B).cdoID(), true), notification.getNewValue());
      }
      else
      {
        assertEquals(false, false);
      }

      count++;
    }
  }

  public void testRemoveContained() throws Exception
  {
    List<Category> categories = new ArrayList<Category>();
    categories.add(getModel1Factory().createCategory());

    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().addAll(categories);
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(company);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource("/test1");
    Company company2 = (Company)resource2.getContents().get(0);

    Object[] strongRefs = company2.getCategories().toArray(); // Keep those in memory
    msg(strongRefs);

    final TestAdapter adapter = new TestAdapter();
    company2.eAdapters().add(adapter);

    company.getCategories().removeAll(categories);
    transaction.commit();

    final Object[] oldValue = { null };
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        for (Notification notification : adapter.getNotifications())
        {
          if (notification.getEventType() == Notification.REMOVE
              && notification.getFeature() == getModel1Package().getCompany_Categories())
          {
            oldValue[0] = notification.getOldValue();
            return true;
          }
        }

        return false;
      }
    }.assertNoTimeOut();

    assertInstanceOf(Category.class, oldValue[0]);
  }

  public void testRemoveManyContained() throws Exception
  {
    List<Category> categories = new ArrayList<Category>();
    categories.add(getModel1Factory().createCategory());
    categories.add(getModel1Factory().createCategory());
    categories.add(getModel1Factory().createCategory());
    categories.add(getModel1Factory().createCategory());

    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().addAll(categories);
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(company);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource("/test1");
    Company company2 = (Company)resource2.getContents().get(0);

    Object[] strongRefs = company2.getCategories().toArray(); // Keep those in memory
    msg(strongRefs);

    final TestAdapter adapter = new TestAdapter();
    company2.eAdapters().add(adapter);

    company.getCategories().removeAll(categories);
    transaction.commit();

    final Object[] oldValue = { null };
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        for (Notification notification : adapter.getNotifications())
        {
          if (notification.getEventType() == Notification.REMOVE_MANY
              && notification.getFeature() == getModel1Package().getCompany_Categories())
          {
            oldValue[0] = notification.getOldValue();
            return true;
          }
        }

        return false;
      }
    }.assertNoTimeOut();

    assertInstanceOf(Collection.class, oldValue[0]);
    assertEquals(categories.size(), ((Collection<?>)oldValue[0]).size());
  }

  public void testRemoveXRef() throws Exception
  {
    List<OrderDetail> details = new ArrayList<OrderDetail>();
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());

    Product1 product = getModel1Factory().createProduct1();
    product.getOrderDetails().addAll(details);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(product);
    resource.getContents().addAll(details);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource("/test1");
    Product1 product2 = (Product1)resource2.getContents().get(0);

    Object[] strongRefs = product2.getOrderDetails().toArray(); // Keep those in memory
    msg(strongRefs);

    final TestAdapter adapter = new TestAdapter();
    product2.eAdapters().add(adapter);

    details.remove(0);
    details.remove(0);
    details.remove(0);
    details.remove(1);
    details.remove(1);
    details.remove(1);
    product.getOrderDetails().removeAll(details);
    transaction.commit();

    final Object[] oldValue = { null };
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        for (Notification notification : adapter.getNotifications())
        {
          if (notification.getEventType() == Notification.REMOVE
              && notification.getFeature() == getModel1Package().getProduct1_OrderDetails())
          {
            oldValue[0] = notification.getOldValue();
            return true;
          }
        }

        return false;
      }
    }.assertNoTimeOut();

    assertInstanceOf(OrderDetail.class, oldValue[0]);
  }

  public void testRemoveManyXRef() throws Exception
  {
    List<OrderDetail> details = new ArrayList<OrderDetail>();
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());

    Product1 product = getModel1Factory().createProduct1();
    product.getOrderDetails().addAll(details);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(product);
    resource.getContents().addAll(details);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource("/test1");
    Product1 product2 = (Product1)resource2.getContents().get(0);

    Object[] strongRefs = product2.getOrderDetails().toArray(); // Keep those in memory
    msg(strongRefs);

    final TestAdapter adapter = new TestAdapter();
    product2.eAdapters().add(adapter);

    details.remove(0);
    details.remove(0);
    details.remove(0);
    details.remove(4);
    details.remove(4);
    details.remove(4);
    product.getOrderDetails().removeAll(details);
    transaction.commit();

    final Object[] oldValue = { null };
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        for (Notification notification : adapter.getNotifications())
        {
          if (notification.getEventType() == Notification.REMOVE_MANY
              && notification.getFeature() == getModel1Package().getProduct1_OrderDetails())
          {
            oldValue[0] = notification.getOldValue();
            return true;
          }
        }

        return false;
      }
    }.assertNoTimeOut();

    assertInstanceOf(Collection.class, oldValue[0]);
    assertEquals(details.size(), ((Collection<?>)oldValue[0]).size());
  }

  /**
   * @author Simon McDuff
   */
  private class CDOIDFilterChangeSubscriptionPolicy implements CDOAdapterPolicy
  {
    private Set<CDOID> cdoIDs = new HashSet<CDOID>();

    public CDOIDFilterChangeSubscriptionPolicy()
    {
    }

    public boolean isValid(EObject eObject, Adapter object)
    {
      return cdoIDs.contains(((InternalCDOObject)eObject).cdoID());
    }

    public Set<CDOID> getCdoIDs()
    {
      return cdoIDs;
    }
  }
}
