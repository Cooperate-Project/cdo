/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Eike Stepper
 */
public class ContainmentTest extends AbstractCDOTest
{
  public void testTransientContainment() throws Exception
  {
    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Creating company");
    Company company = getModel1Factory().createCompany();

    msg("Adding supplier");
    company.getSuppliers().add(supplier);

    assertTransient(company);
    assertTransient(supplier);
    assertContent(company, supplier);
  }

  public void testBasicContainment() throws Exception
  {
    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Creating company");
    Company company = getModel1Factory().createCompany();

    msg("Adding supplier");
    company.getSuppliers().add(supplier);

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Adding company");
    resource.getContents().add(company);

    msg("Committing");
    transaction.commit();

    assertClean(resource, transaction);
    assertClean(company, transaction);
    assertClean(supplier, transaction);
    assertContent(resource, company);
    assertContent(company, supplier);
  }

  public void test3Levels() throws Exception
  {
    msg("Creating category1");
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    msg("Creating category2");
    Category category2 = getModel1Factory().createCategory();
    category2.setName("category2");

    msg("Creating category3");
    Category category3 = getModel1Factory().createCategory();
    category3.setName("category3");

    msg("Creating company");
    Company company = getModel1Factory().createCompany();

    msg("Adding categories");
    company.getCategories().add(category1);
    category1.getCategories().add(category2);
    category2.getCategories().add(category3);

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Adding company");
    resource.getContents().add(company);

    msg("Committing");
    transaction.commit();

    assertClean(resource, transaction);
    assertClean(company, transaction);
    assertClean(category1, transaction);
    assertClean(category2, transaction);
    assertClean(category3, transaction);
    assertContent(resource, company);
    assertContent(company, category1);
    assertContent(category1, category2);
    assertContent(category2, category3);
  }

  public void testSeparateView() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    {
      msg("Creating category1");
      Category category1 = getModel1Factory().createCategory();
      category1.setName("category1");

      msg("Creating category2");
      Category category2 = getModel1Factory().createCategory();
      category2.setName("category2");

      msg("Creating category3");
      Category category3 = getModel1Factory().createCategory();
      category3.setName("category3");

      msg("Creating company");
      Company company = getModel1Factory().createCompany();

      msg("Adding categories");
      company.getCategories().add(category1);
      category1.getCategories().add(category2);
      category2.getCategories().add(category3);

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Adding company");
      resource.getContents().add(company);

      msg("Committing");
      transaction.commit();
    }

    removeAllRevisions(getRepository().getRevisionManager());

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");
    assertProxy(resource);

    EList<EObject> contents = resource.getContents();
    Company company = (Company)contents.get(0);
    assertClean(company, transaction);
    assertClean(resource, transaction);
    assertContent(resource, company);

    Category category1 = company.getCategories().get(0);
    assertClean(category1, transaction);
    assertClean(company, transaction);
    assertContent(company, category1);

    Category category2 = category1.getCategories().get(0);
    assertClean(category2, transaction);
    assertClean(category1, transaction);
    assertContent(category1, category2);

    Category category3 = category2.getCategories().get(0);
    assertClean(category3, transaction);
    assertClean(category2, transaction);
    assertContent(category2, category3);
    assertClean(category3, transaction);
  }

  public void testSeparateSession() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Creating category1");
      Category category1 = getModel1Factory().createCategory();
      category1.setName("category1");

      msg("Creating category2");
      Category category2 = getModel1Factory().createCategory();
      category2.setName("category2");

      msg("Creating category3");
      Category category3 = getModel1Factory().createCategory();
      category3.setName("category3");

      msg("Creating company");
      Company company = getModel1Factory().createCompany();

      msg("Adding categories");
      company.getCategories().add(category1);
      category1.getCategories().add(category2);
      category2.getCategories().add(category3);

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Adding company");
      resource.getContents().add(company);

      msg("Committing");
      transaction.commit();
    }

    removeAllRevisions(getRepository().getRevisionManager());

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");
    assertProxy(resource);

    EList<EObject> contents = resource.getContents();
    Company company = (Company)contents.get(0);
    assertClean(company, transaction);
    assertClean(resource, transaction);
    assertContent(resource, company);

    Category category1 = company.getCategories().get(0);
    assertClean(category1, transaction);
    assertClean(company, transaction);
    assertContent(company, category1);

    Category category2 = category1.getCategories().get(0);
    assertClean(category2, transaction);
    assertClean(category1, transaction);
    assertContent(category1, category2);

    Category category3 = category2.getCategories().get(0);
    assertClean(category3, transaction);
    assertClean(category2, transaction);
    assertContent(category2, category3);
    assertClean(category3, transaction);
  }

  public void testSetSingleContainment() throws Exception
  {
    Address address = getModel1Factory().createAddress();
    address.setName("Stepper");
    address.setStreet("Home Ave. 007");
    address.setCity("Berlin");

    SpecialPurchaseOrder order = getModel2Factory().createSpecialPurchaseOrder();
    order.setShippingAddress(address);

    CDOSession session = openModel2Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    resource.getContents().add(order);
    transaction.commit();

    assertClean(resource, transaction);
    assertClean(order, transaction);
    assertClean(address, transaction);
    assertContent(resource, order);
    assertContent(order, address);
  }

  // TODO Re-include TC after fixing detachment
  public void _testUnsetSingleContainment() throws Exception
  {
    Address address = getModel1Factory().createAddress();
    address.setName("Stepper");
    address.setStreet("Home Ave. 007");
    address.setCity("Berlin");

    SpecialPurchaseOrder order = getModel2Factory().createSpecialPurchaseOrder();
    order.setShippingAddress(address);

    CDOSession session = openModel2Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    resource.getContents().add(order);
    transaction.commit();

    order.setShippingAddress(null);
    transaction.commit();

    assertClean(resource, transaction);
    assertClean(order, transaction);
    // TODO Uncomment transient check after fixing detachment
    // assertTransient(address);
    assertContent(resource, order);
    assertNull(order.getShippingAddress());
  }

  public void testObjectNotSameResourceThanItsContainerCDOANDXMI() throws Exception
  {
    byte[] data = null;
    {
      CDOSession session = openModel1Session();
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

      CDOTransaction transaction = session.openTransaction(resourceSet);
      Resource resource1 = resourceSet.createResource(URI.createURI("test://1"));
      Resource resource2 = transaction.createResource("test");

      EPackage packageObject = createDynamicEPackage();
      EClass eClass = (EClass)packageObject.getEClassifier("SchoolBook");

      EObject container = packageObject.getEFactoryInstance().create(eClass);
      Order contained = getModel1Factory().createOrder();

      resource1.getContents().add(container);
      resource2.getContents().add(contained);

      container.eSet(container.eClass().getEStructuralFeature("proxyElement"), contained);

      assertEquals(resource1, container.eResource());
      assertEquals(resource2, contained.eResource());

      // If the relationship is define has resolveProxy this is true if not.. this is false.
      assertEquals(container, contained.eContainer());
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      resource1.save(outputStream, null);
      data = outputStream.toByteArray();
      transaction.commit();
    }

    removeAllRevisions(getRepository().getRevisionManager());
    EPackage packageObject = createDynamicEPackage();

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(packageObject);
    CDOTransaction transaction = session.openTransaction(resourceSet);

    resourceSet.getPackageRegistry().put(packageObject.getNsURI(), packageObject);
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

    Resource resource1 = resourceSet.createResource(URI.createURI("test://1"));
    resource1.load(new ByteArrayInputStream(data), null);
    Resource resource2 = transaction.getResource("test");

    EObject container = resource1.getContents().get(0);
    Order order = (Order)resource2.getContents().get(0);

    assertEquals(resource1.getContents().get(0), order.eContainer());
    resource2.getContents().remove(order);

    Order order2 = (Order)container.eGet(container.eClass().getEStructuralFeature("proxyElement"));
    assertSame(order, order2);
  }

  public void testObjectNotSameResourceThanItsContainerCDO() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      ResourceSet resourceSet = new ResourceSetImpl();

      CDOTransaction transaction = session.openTransaction(resourceSet);
      Resource resource1 = transaction.createResource("testA");
      Resource resource2 = transaction.createResource("testB");

      EPackage packageObject = createDynamicEPackage();
      session.getPackageRegistry().putEPackage(packageObject);
      EClass eClass = (EClass)packageObject.getEClassifier("SchoolBook");

      EObject container = packageObject.getEFactoryInstance().create(eClass);
      Order contained = getModel1Factory().createOrder();

      resource1.getContents().add(container);
      resource2.getContents().add(contained);

      container.eSet(container.eClass().getEStructuralFeature("proxyElement"), contained);

      assertEquals(resource1, container.eResource());
      assertEquals(resource2, contained.eResource());

      // If the relationship is define has resolveProxy this is true if not.. this is false.
      assertEquals(container, contained.eContainer());
      transaction.commit();
    }

    removeAllRevisions(getRepository().getRevisionManager());

    ResourceSet resourceSet = new ResourceSetImpl();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction(resourceSet);
    EPackage packageObject = createDynamicEPackage();
    session.getPackageRegistry().putEPackage(packageObject);
    Resource resource1 = transaction.getResource("testA");
    Resource resource2 = transaction.getResource("testB");

    EObject container = resource1.getContents().get(0);
    Order order = (Order)resource2.getContents().get(0);

    assertEquals(resource1.getContents().get(0), order.eContainer());
    resource2.getContents().remove(order);

    Order order2 = (Order)container.eGet(container.eClass().getEStructuralFeature("proxyElement"));
    assertSame(order, order2);

  }

  public void testObjectNotSameResourceThanItsContainer_WithoutCDO() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();

    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

    Resource resource1 = resourceSet.createResource(URI.createURI("test://1"));
    Resource resource2 = resourceSet.createResource(URI.createURI("test://2"));
    EPackage packageObject = createDynamicEPackage();
    EClass eClass = (EClass)packageObject.getEClassifier("SchoolBook");

    EObject container = packageObject.getEFactoryInstance().create(eClass);
    EObject contained = packageObject.getEFactoryInstance().create(eClass);

    resource1.getContents().add(container);
    resource2.getContents().add(contained);

    container.eSet(container.eClass().getEStructuralFeature("proxyElement"), contained);
    // resource1.getContents().add(container);

    assertEquals(resource1, container.eResource());
    assertEquals(resource2, contained.eResource());

    // If the relationship is define has resolveProxy this is true if not.. this is false.
    assertEquals(container, contained.eContainer());
  }

  // Do not support legacy system
  public void _testBug246540() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource("/my/resource1");

    for (EClassifier eClassifier : EcorePackage.eINSTANCE.getEClassifiers())
    {
      resource.getContents().add(eClassifier);
    }

    transaction.commit();
    session.close();
  }

  private EPackage createDynamicEPackage()
  {
    final EcoreFactory efactory = EcoreFactory.eINSTANCE;
    final EcorePackage epackage = EcorePackage.eINSTANCE;

    EClass schoolBookEClass = efactory.createEClass();
    schoolBookEClass.setName("SchoolBook");

    // create a new attribute for this EClass
    EAttribute level = efactory.createEAttribute();
    level.setName("level");
    level.setEType(epackage.getEInt());
    schoolBookEClass.getEStructuralFeatures().add(level);

    EReference proxyElement = efactory.createEReference();
    proxyElement.setName("proxyElement");
    proxyElement.setEType(epackage.getEObject());
    proxyElement.setResolveProxies(true);
    proxyElement.setContainment(true);
    schoolBookEClass.getEStructuralFeatures().add(proxyElement);

    EReference element = efactory.createEReference();
    element.setName("element");
    element.setEType(epackage.getEObject());
    element.setContainment(true);
    element.setResolveProxies(false);
    schoolBookEClass.getEStructuralFeatures().add(element);

    // Create a new EPackage and add the new EClasses
    EPackage schoolPackage = efactory.createEPackage();
    schoolPackage.setName("elv");
    schoolPackage.setNsPrefix("elv");
    schoolPackage.setNsURI("http:///www.elver.org/School");
    schoolPackage.getEClassifiers().add(schoolBookEClass);
    return schoolPackage;

  }
}
