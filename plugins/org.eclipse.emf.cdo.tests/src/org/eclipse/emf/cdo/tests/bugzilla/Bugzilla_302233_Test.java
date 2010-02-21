/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * unset of CDO does not work correctly
 * <p>
 * See bug 302233
 * 
 * @author Martin Fluegge
 */
public class Bugzilla_302233_Test extends AbstractCDOTest
{
  public void testBugzilla_302233() throws Exception
  {
    {
      Order order = getModel1Factory().createOrder();
      EStructuralFeature feature = getModel1Package().getOrder_OrderDetails();

      assertEquals(false, order.eIsSet(feature));

      order.eUnset(feature);

      for (int i = 0; i < 10; i++)
      {
        OrderDetail orderDetail = getModel1Factory().createOrderDetail();
        order.getOrderDetails().add(orderDetail);
      }

      order.eUnset(feature);

      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(getModel1Package());

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(order);

      for (int i = 0; i < 10; i++)
      {
        OrderDetail orderDetail = getModel1Factory().createOrderDetail();
        order.getOrderDetails().add(orderDetail);
      }

      transaction.commit();

      order.eUnset(feature);

      assertEquals(false, order.eIsSet(getModel1Package().getOrder_OrderDetails()));

      session.close();
    }
  }
}
