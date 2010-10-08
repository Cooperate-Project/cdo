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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class OCLQueryTest extends AbstractCDOTest
{
  private static final int NUM_OF_PRODUCTS = 20;

  private static final int NUM_OF_CUSTOMERS = 5;

  private static final int NUM_OF_PRODUCTS_CUSTOMER = NUM_OF_PRODUCTS / NUM_OF_CUSTOMERS;

  private static final int NUM_OF_SALES_ORDERS = 5;

  private CDOTransaction transaction;

  private List<Product1> products = new ArrayList<Product1>();

  private List<Customer> customers = new ArrayList<Customer>();

  private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

  private List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    CDOSession session = openSession();
    transaction = session.openTransaction();
    createTestSet(transaction);
  }

  public void testAllProducts() throws Exception
  {
    CDOQuery query = transaction.createQuery("ocl", "Product1.allInstances()");
    query.setParameter("context", getModel1Package().getProduct1());

    List<Product1> products = query.getResult(Product1.class);
    assertEquals(NUM_OF_PRODUCTS, products.size());
  }

  public void testAllCustomers() throws Exception
  {
    CDOQuery query = transaction.createQuery("ocl", "Customer.allInstances()");
    query.setParameter("context", getModel1Package().getCustomer());

    List<Customer> customers = query.getResult(Customer.class);
    assertEquals(NUM_OF_CUSTOMERS, customers.size());
  }

  public void testAllProductsWithName() throws Exception
  {
    CDOQuery query = transaction.createQuery("ocl", "Product1.allInstances()->select(p | p.name='1')");
    query.setParameter("context", getModel1Package().getProduct1());

    List<Product1> products = query.getResult(Product1.class);
    assertEquals(1, products.size());
  }

  public void testAllProductsWithNameParameter() throws Exception
  {
    CDOQuery query = transaction.createQuery("ocl", "Product1.allInstances()->select(p | p.name=myname)");
    query.setParameter("context", getModel1Package().getProduct1());
    query.setParameter("myname", "1");

    List<Product1> products = query.getResult(Product1.class);
    assertEquals(1, products.size());
  }

  public void testAllProductsWithVAT() throws Exception
  {
    CDOQuery query = transaction.createQuery("ocl", "Product1.allInstances()->select(p | p.vat=VAT::vat15)");
    query.setParameter("context", getModel1Package().getProduct1());

    List<Product1> products = query.getResult(Product1.class);
    assertEquals(10, products.size());
    for (Product1 p : products)
    {
      assertEquals(p.getVat(), VAT.VAT15);
    }
  }

  public void testAllProductsWithVATParameter() throws Exception
  {
    CDOQuery query = transaction.createQuery("ocl", "Product1.allInstances()->select(p | p.vat=myvat)");
    query.setParameter("context", getModel1Package().getProduct1());
    query.setParameter("myvat", VAT.VAT15);

    List<Product1> products = query.getResult(Product1.class);
    assertEquals(10, products.size());
    for (Product1 p : products)
    {
      assertEquals(p.getVat(), VAT.VAT15);
    }
  }

  public void testProductIterator() throws Exception
  {
    CDOQuery query = transaction.createQuery("ocl", "Product1.allInstances()");
    query.setParameter("context", getModel1Package().getProduct1());

    int counter = 0;
    for (CloseableIterator<Product1> it = query.getResultAsync(Product1.class); it.hasNext();)
    {
      Product1 product = it.next();
      assertTrue(product != null); // meaningless but do something

      if (++counter == NUM_OF_PRODUCTS / 2)
      {
        it.close();
        break;
      }
    }
  }

  private CDOResource createTestSet(CDOTransaction transaction) throws CommitException
  {
    disableConsole();
    CDOResource resource = transaction.createResource("/test1");
    fillResource(resource);
    transaction.commit();
    enableConsole();
    return resource;
  }

  private void fillResource(CDOResource resource)
  {
    msg("Creating Testset");
    List<Product1> products = new ArrayList<Product1>();
    for (int i = 0; i < NUM_OF_PRODUCTS; i++)
    {
      products.add(createProduct(i));
    }

    resource.getContents().addAll(products);

    int productCounter = 0;
    for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
    {
      Customer customer = createCustomer(i);
      resource.getContents().add(customer);

      List<Product1> customerProducts = products.subList(productCounter, productCounter + NUM_OF_PRODUCTS_CUSTOMER);
      for (int k = 0; k < NUM_OF_SALES_ORDERS; k++)
      {
        resource.getContents().add(createSalesOrder(i * 10 + k, customer, customerProducts));
      }

      productCounter += NUM_OF_PRODUCTS_CUSTOMER;
    }
  }

  private Customer createCustomer(int i)
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setCity(i == 0 ? null : "City " + i); // set first city null for null-test-case
    customer.setName("" + i);
    customer.setStreet("Street " + i);

    customers.add(customer);
    return customer;
  }

  private SalesOrder createSalesOrder(int num, Customer customer, List<Product1> products)
  {
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setCustomer(customer);
    salesOrder.setId(num);
    salesOrder.getOrderDetails().addAll(createOrderDetails(num, products));

    salesOrders.add(salesOrder);
    return salesOrder;
  }

  private List<OrderDetail> createOrderDetails(int index, List<Product1> products)
  {
    List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

    int count = 0;
    for (Product1 product : products)
    {
      OrderDetail orderDetail = createOrderDetail(product, count++ * index * 1.1f);
      orderDetails.add(orderDetail);
    }

    return orderDetails;
  }

  private OrderDetail createOrderDetail(Product1 product, float price)
  {
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(price);
    orderDetail.setProduct(product);

    orderDetails.add(orderDetail);
    return orderDetail;
  }

  private Product1 createProduct(int index)
  {
    Product1 product = getModel1Factory().createProduct1();
    product.setDescription("Description " + index);
    product.setName("" + index);
    if (index < 10)
    {
      product.setVat(VAT.VAT15);
    }
    else
    {
      product.setVat(VAT.VAT7);
    }

    products.add(product);
    return product;
  }
}
