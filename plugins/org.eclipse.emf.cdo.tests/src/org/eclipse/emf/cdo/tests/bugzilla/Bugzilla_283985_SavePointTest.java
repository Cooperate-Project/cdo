/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_283985_SavePointTest extends AbstractCDOTest
{
  private Order order1, order2;

  private OrderDetail detail1, detail2, detail3, detail4;

  private CDOSession session;

  private CDOTransaction transaction;

  private CDOResource resource;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    Model1Factory factory = Model1Factory.eINSTANCE;

    order1 = factory.createOrder();
    order2 = factory.createOrder();
    detail1 = factory.createOrderDetail();
    detail2 = factory.createOrderDetail();
    detail3 = factory.createOrderDetail();
    detail4 = factory.createOrderDetail();

    order1.getOrderDetails().add(detail1);
    order1.getOrderDetails().add(detail2);
    order1.getOrderDetails().add(detail3);
    order1.getOrderDetails().add(detail4);

    session = openModel1Session();
    transaction = session.openTransaction();
    resource = transaction.getOrCreateResource("/r1");
    resource.getContents().clear();
    resource.getContents().add(order1);
    resource.getContents().add(order2);
    transaction.commit();
  }

  @Override
  public void tearDown() throws Exception
  {
    transaction.close();
    session.close();
    super.tearDown();
  }

  public void test1()
  {
    CDOID id = CDOUtil.getCDOObject(detail1).cdoID();

    order1.getOrderDetails().remove(detail1);
    assertTransient(detail1);

    transaction.setSavepoint();
    order1.getOrderDetails().add(detail1);
    assertDirty(detail1, transaction);

    transaction.commit();
    assertEquals(id, (CDOUtil.getCDOObject(detail1)).cdoID());
    assertEquals(detail1, transaction.getObject(id));
    assertClean(detail1, transaction);
  }

  public void test2()
  {
    InternalCDOSavepoint sp = (InternalCDOSavepoint)transaction.setSavepoint();
    order1.getOrderDetails().remove(detail1);
    assertTransient(detail1);

    assertTrue(sp.getDetachedObjects().containsValue(detail1));

    sp = (InternalCDOSavepoint)transaction.setSavepoint();
    assertTrue(sp.getPreviousSavepoint().getDetachedObjects().containsValue(detail1));

    order1.getOrderDetails().add(detail1);
    assertTrue(sp.getReattachedObjects().containsValue(detail1));
    assertDirty(detail1, transaction);

    sp.rollback();
    System.out.println(((CDOObject)detail1).cdoState());
    assertTransient(detail1);

    transaction.commit();
    assertFalse(order1.getOrderDetails().contains(detail1));
  }

  public void test3()
  {
    CDOID id = CDOUtil.getCDOObject(detail1).cdoID();

    transaction.setSavepoint();
    assertClean(detail1, transaction);

    order1.getOrderDetails().remove(detail1);
    transaction.setSavepoint();
    assertTransient(detail1);

    order1.getOrderDetails().add(detail1);
    transaction.setSavepoint();
    assertDirty(detail1, transaction);

    order1.getOrderDetails().remove(detail1);
    assertTransient(detail1);

    transaction.getLastSavepoint().rollback();
    assertDirty(detail1, transaction);

    transaction.commit();

    assertTrue(order1.getOrderDetails().contains(detail1));
    assertEquals(id, ((CDOObject)detail1).cdoID());
  }
}
