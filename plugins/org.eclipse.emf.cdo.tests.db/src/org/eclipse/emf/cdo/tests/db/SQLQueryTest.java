/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 *    Kai Schlamp - Bug 284812: [DB] Query non CDO object fails
 *    Stefan Winkler - Bug 284812: [DB] Query non CDO object fails
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.internal.db.SQLQueryHandler;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Test different aspects of SQL querying using the CDO query api.
 * 
 * @author Kai Schlamp
 */
public class SQLQueryTest extends AbstractCDOTest
{
  private static final int NUM_OF_PRODUCTS = 20;

  private static final int NUM_OF_CUSTOMERS = 5;

  private static final int NUM_OF_PRODUCTS_CUSTOMER = NUM_OF_PRODUCTS / NUM_OF_CUSTOMERS;

  private static final int NUM_OF_SALES_ORDERS = 5;

  public void testSimpleQueries() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for products");
      CDOQuery cdoQuery = transaction.createQuery("sql", "SELECT CDO_ID FROM PRODUCT1");
      final List<Product1> products = cdoQuery.getResult(Product1.class);
      assertEquals(NUM_OF_PRODUCTS, products.size());
    }

    {
      msg("Query for products with a specific name");
      CDOQuery cdoQuery = transaction.createQuery("sql", "SELECT CDO_ID FROM PRODUCT1 WHERE name=:name");
      cdoQuery.setParameter("name", "" + 1);
      final List<Product1> products = cdoQuery.getResult(Product1.class);
      assertEquals(1, products.size());
    }

    {
      msg("Query for Customers");
      CDOQuery cdoQuery = transaction.createQuery("sql", "SELECT CDO_ID FROM CUSTOMER");
      final List<Customer> customers = cdoQuery.getResult(Customer.class);
      assertEquals(NUM_OF_CUSTOMERS, customers.size());
    }

    {
      msg("Query for products with VAT15");
      CDOQuery cdoQuery = transaction.createQuery("sql", "SELECT CDO_ID FROM PRODUCT1 WHERE VAT =:vat");
      cdoQuery.setParameter("vat", VAT.VAT15.getValue());
      final List<Product1> products = cdoQuery.getResult(Product1.class);
      assertEquals(10, products.size());
      for (Product1 p : products)
      {
        assertEquals(p.getVat(), VAT.VAT15);
      }
    }

    transaction.commit();
    enableConsole();
  }

  public void testFunctions() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Count products");
      CDOQuery cdoQuery = transaction.createQuery("sql", "SELECT COUNT(*) from PRODUCT1");
      cdoQuery.setParameter(SQLQueryHandler.CDO_OBJECT_QUERY, false);
      final List<Long> counts = cdoQuery.getResult(Long.class);
      assertEquals(counts.size(), 1);
      assertEquals(counts.get(0), new Long(NUM_OF_PRODUCTS));
    }

    transaction.commit();
    enableConsole();
  }

  public void testComplexQuerySalesOrderJoinCustomerProduct() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customers");
      CDOQuery customerQuery = transaction.createQuery("sql", "SELECT CDO_ID FROM CUSTOMER ORDER BY NAME");
      final List<Customer> customers = customerQuery.getResult(Customer.class);
      assertEquals(NUM_OF_CUSTOMERS, customers.size());

      msg("Query for products");
      CDOQuery productQuery = transaction.createQuery("sql", "SELECT CDO_ID FROM PRODUCT1");
      final List<Product1> products = productQuery.getResult(Product1.class);
      assertEquals(NUM_OF_PRODUCTS, products.size());
    }

    transaction.commit();
    enableConsole();
  }

  public void testPaging() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for products in pages");
      int pageSize = 5;
      int numOfPages = NUM_OF_PRODUCTS / pageSize;
      final List<Product1> allProducts = new ArrayList<Product1>();
      for (int page = 0; page < numOfPages; page++)
      {
        CDOQuery productQuery = transaction.createQuery("sql", "SELECT CDO_ID FROM PRODUCT1");
        productQuery.setMaxResults(pageSize);
        productQuery.setParameter(SQLQueryHandler.FIRST_RESULT, page * pageSize);
        final List<Product1> queriedProducts = productQuery.getResult(Product1.class);
        assertTrue(queriedProducts.size() <= pageSize);
        // a product should not have been read yet
        for (Product1 newProduct : queriedProducts)
        {
          assertTrue(!allProducts.contains(newProduct));
        }

        allProducts.addAll(queriedProducts);
      }

      assertEquals(NUM_OF_PRODUCTS, allProducts.size());
    }

    transaction.commit();
    enableConsole();
  }

  public void testIterator() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for products");
      CDOQuery productQuery = transaction.createQuery("sql", "SELECT CDO_ID FROM PRODUCT1");
      final CloseableIterator<Product1> iterator = productQuery.getResultAsync(Product1.class);
      int counter = 0;
      while (iterator.hasNext())
      {
        final Product1 product = iterator.next();
        // meaningless but do something
        assertTrue(product != null);
        counter++;
        if (counter == NUM_OF_PRODUCTS / 2)
        {
          iterator.close();
        }
      }
    }

    transaction.commit();
    enableConsole();
  }

  public void testNonCdoObjectQueries() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customer street strings.");
      CDOQuery cdoQuery = transaction.createQuery("sql", "SELECT STREET FROM REPO1.CUSTOMER");
      cdoQuery.setParameter("cdoObjectQuery", false);
      List<String> streets = new ArrayList<String>(cdoQuery.getResult(String.class));
      for (int i = 0; i < 5; i++)
      {
        assertTrue(streets.contains("Street " + i));
      }
    }
  }

  public void testNonCdoObjectQueries_Null() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customer city strings.");
      CDOQuery cdoQuery = transaction.createQuery("sql", "SELECT CITY FROM REPO1.CUSTOMER");
      cdoQuery.setParameter("cdoObjectQuery", false);
      List<String> cities = new ArrayList<String>(cdoQuery.getResult(String.class));

      assertTrue(cities.contains(null));
      for (int i = 1; i < 5; i++)
      {
        assertTrue(cities.contains("City " + i));
      }
    }
  }

  private void createTestSet(CDOSession session)
  {
    // disableConsole();
    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    fillResource(resource);

    msg("Committing");
    transaction.commit();
    enableConsole();
  }

  private void fillResource(CDOResource resource)
  {
    msg("Creating Testset");
    final List<Product1> products = new ArrayList<Product1>();
    for (int i = 0; i < NUM_OF_PRODUCTS; i++)
    {
      products.add(createProduct(i));
    }

    resource.getContents().addAll(products);

    int productCounter = 0;
    for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
    {
      final Customer customer = getModel1Factory().createCustomer();

      if (i == 0)
      {
        // set first city null for null-test-case
        customer.setCity(null);
      }
      else
      {
        customer.setCity("City " + i);
      }
      customer.setName(i + "");
      customer.setStreet("Street " + i);
      resource.getContents().add(customer);

      final List<Product1> customerProducts = products.subList(productCounter, productCounter
          + NUM_OF_PRODUCTS_CUSTOMER);
      for (int k = 0; k < NUM_OF_SALES_ORDERS; k++)
      {
        resource.getContents().add(createSalesOrder(i * 10 + k, customer, customerProducts));
      }

      productCounter += NUM_OF_PRODUCTS_CUSTOMER;
    }
  }

  private SalesOrder createSalesOrder(int num, Customer customer, List<Product1> products)
  {
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setCustomer(customer);
    salesOrder.setId(num);
    createOrderDetail(salesOrder, num, products);
    return salesOrder;
  }

  private List<OrderDetail> createOrderDetail(Order order, int index, List<Product1> products)
  {
    final List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
    int count = 0;
    for (Product1 product : products)
    {
      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setPrice(count++ * index * 1.1f);
      orderDetail.setProduct(product);
    }

    return orderDetails;
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

    return product;
  }
}
