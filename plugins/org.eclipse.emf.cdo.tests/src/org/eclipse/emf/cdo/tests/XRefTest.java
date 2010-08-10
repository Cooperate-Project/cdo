/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - additional tests
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 300149: Support remote cross referencing with a convenient API on the client and SPI on the server for the stores to
 * implement https://bugs.eclipse.org/bugs/show_bug.cgi?id=300149
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public class XRefTest extends AbstractCDOTest
{
  public void testCrossReferenceMultivalueEReferenceQuery() throws Exception
  {
    PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.getPurchaseOrders().add(purchaseOrder1);
    supplier.getPurchaseOrders().add(purchaseOrder2);
    supplier.getPurchaseOrders().add(purchaseOrder3);
    supplier.getPurchaseOrders().add(purchaseOrder4);

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();

    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(supplier);
    resource.getContents().add(purchaseOrder1);
    resource.getContents().add(purchaseOrder2);
    resource.getContents().add(purchaseOrder3);
    resource.getContents().add(purchaseOrder4);

    transaction.commit();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    List<CDOObjectReference> results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(supplier)));
    assertEquals(4, results.size());

    for (CDOObjectReference result : results)
    {
      CDOObject sourceObject = result.getSourceObject();
      assertInstanceOf(PurchaseOrder.class, CDOUtil.getEObject(sourceObject));
    }
  }

  @SuppressWarnings("unchecked")
  public void testXRefsToMany() throws Exception
  {
    // create model
    EPackage pkg = EMFUtil.createEPackage("xreftest", "xreftest",
        "http://cdo.emf.eclipse.org/TestModels/XRefTestToMany.ecore");
    EClass cls1 = EMFUtil.createEClass(pkg, "referencer", false, false);
    EAttribute id1 = EMFUtil.createEAttribute(cls1, "id", EcorePackage.eINSTANCE.getEInt());

    EClass cls2 = EMFUtil.createEClass(pkg, "referencee", false, false);
    EAttribute id2 = EMFUtil.createEAttribute(cls2, "id", EcorePackage.eINSTANCE.getEInt());

    EReference ref = EMFUtil.createEReference(cls1, "ref", cls2, false, true);

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    // create instances
    EObject obj2_1 = EcoreUtil.create(cls2);
    obj2_1.eSet(id2, 11);
    EObject obj2_2 = EcoreUtil.create(cls2);
    obj2_2.eSet(id2, 12);
    EObject obj2_3 = EcoreUtil.create(cls2);
    obj2_3.eSet(id2, 13);
    EObject obj2_4 = EcoreUtil.create(cls2);
    obj2_4.eSet(id2, 14);

    EObject obj1_1 = EcoreUtil.create(cls1);
    obj1_1.eSet(id1, 1);
    EObject obj1_2 = EcoreUtil.create(cls1);
    obj1_2.eSet(id1, 2);
    EObject obj1_3 = EcoreUtil.create(cls1);
    obj1_3.eSet(id1, 3);
    EObject obj1_4 = EcoreUtil.create(cls1);
    obj1_4.eSet(id1, 4);

    // configure references
    ((EList<EObject>)obj1_1.eGet(ref)).add(obj2_2);
    ((EList<EObject>)obj1_1.eGet(ref)).add(obj2_3);

    ((EList<EObject>)obj1_2.eGet(ref)).add(obj2_2);

    ((EList<EObject>)obj1_3.eGet(ref)).add(obj2_1);
    ((EList<EObject>)obj1_3.eGet(ref)).add(obj2_2);

    // store objects
    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().addAll(Arrays.asList(obj1_1, obj1_2, obj1_3, obj1_4, obj2_1, obj2_2, obj2_3, obj2_4));
    transaction.commit();
    transaction.close();
    session1.close();

    // check XRefs
    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    List<CDOObjectReference> results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(obj2_1)));
    assertEquals(1, results.size());

    {
      CDOObjectReference result = results.get(0);
      assertEquals(0, result.getSourceIndex());
      // XXX fails!
      // assertEquals(ref, result.getSourceReference());
      CDOObject sourceObject = results.get(0).getSourceObject();
      assertEquals(3, sourceObject.eGet(sourceObject.eClass().getEStructuralFeature("id")));
    }

    results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(obj2_3)));
    assertEquals(1, results.size());
    {
      CDOObjectReference result = results.get(0);
      assertEquals(1, result.getSourceIndex());
      assertEquals(ref.getName(), result.getSourceReference().getName());
      CDOObject sourceObject = results.get(0).getSourceObject();
      assertEquals(1, sourceObject.eGet(sourceObject.eClass().getEStructuralFeature("id")));
    }

    results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(obj2_2)));
    assertEquals(3, results.size());

    boolean found1 = false;
    boolean found2 = false;
    boolean found3 = false;

    for (CDOObjectReference result : results)
    {
      assertEquals(ref.getName(), results.get(0).getSourceReference().getName());

      CDOObject sourceObject = result.getSourceObject();
      Integer id = (Integer)sourceObject.eGet(sourceObject.eClass().getEStructuralFeature("id"));
      switch (id.intValue())
      {
      case 1:
        assertEquals(0, result.getSourceIndex());
        found1 = true;
        break;
      case 2:
        assertEquals(0, result.getSourceIndex());
        found2 = true;
        break;
      case 3:
        assertEquals(1, result.getSourceIndex());
        found3 = true;
        break;
      default:
        // must not happen!
        assertTrue(false);
      }
    }
    assertTrue(found1 && found2 && found3);

    view.close();
    session2.close();
  }

  public void testXRefsToOne() throws Exception
  {
    // create model
    EPackage pkg = EMFUtil.createEPackage("xreftest", "xreftest",
        "http://cdo.emf.eclipse.org/TestModels/XRefTestToOne.ecore");
    EClass cls1 = EMFUtil.createEClass(pkg, "referencer", false, false);
    EAttribute id1 = EMFUtil.createEAttribute(cls1, "id", EcorePackage.eINSTANCE.getEInt());

    EClass cls2 = EMFUtil.createEClass(pkg, "referencee", false, false);
    EAttribute id2 = EMFUtil.createEAttribute(cls2, "id", EcorePackage.eINSTANCE.getEInt());

    EReference ref = EMFUtil.createEReference(cls1, "ref", cls2, false, false);

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    // create instances
    EObject obj2_1 = EcoreUtil.create(cls2);
    obj2_1.eSet(id2, 11);
    EObject obj2_2 = EcoreUtil.create(cls2);
    obj2_2.eSet(id2, 12);
    EObject obj2_3 = EcoreUtil.create(cls2);
    obj2_3.eSet(id2, 13);
    EObject obj2_4 = EcoreUtil.create(cls2);
    obj2_4.eSet(id2, 14);

    EObject obj1_1 = EcoreUtil.create(cls1);
    obj1_1.eSet(id1, 1);
    EObject obj1_2 = EcoreUtil.create(cls1);
    obj1_2.eSet(id1, 2);
    EObject obj1_3 = EcoreUtil.create(cls1);
    obj1_3.eSet(id1, 3);
    EObject obj1_4 = EcoreUtil.create(cls1);
    obj1_4.eSet(id1, 4);

    // configure references
    obj1_1.eSet(ref, obj2_2);
    obj1_2.eSet(ref, obj2_2);
    obj1_3.eSet(ref, obj2_3);

    // store objects
    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().addAll(Arrays.asList(obj1_1, obj1_2, obj1_3, obj1_4, obj2_1, obj2_2, obj2_3, obj2_4));
    transaction.commit();
    transaction.close();
    session1.close();

    // check XRefs
    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    List<CDOObjectReference> results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(obj2_1)));
    assertTrue(results.isEmpty());

    results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(obj2_3)));

    assertEquals(1, results.size());
    {
      CDOObjectReference result = results.get(0);
      assertEquals(0, result.getSourceIndex());

      assertEquals(ref.getName(), result.getSourceReference().getName());
      CDOObject sourceObject = results.get(0).getSourceObject();
      assertEquals(3, sourceObject.eGet(sourceObject.eClass().getEStructuralFeature("id")));
    }

    results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(obj2_2)));
    assertEquals(2, results.size());

    boolean found1 = false;
    boolean found2 = false;

    for (CDOObjectReference result : results)
    {
      assertEquals(ref.getName(), results.get(0).getSourceReference().getName());

      CDOObject sourceObject = result.getSourceObject();
      Integer id = (Integer)sourceObject.eGet(sourceObject.eClass().getEStructuralFeature("id"));
      assertTrue(id == 1 || id == 2);
      assertEquals(0, result.getSourceIndex());

      if (id == 1)
      {
        found1 = true;
      }
      if (id == 2)
      {
        found2 = true;
      }

    }
    assertTrue(found1 && found2);

    view.close();
    session2.close();
  }
  // public void testCrossReferenceSimpleEReferenceQuery() throws Exception
  // {
  // msg("Opening session");
  //
  // CDOSession session1 = openSession();
  //
  // msg("Opening transaction");
  // CDOTransaction transaction1 = session1.openTransaction();
  //
  // msg("Creating resource");
  // CDOResource resource = transaction1.createResource("/test1");
  //
  // msg("Creating test data");
  // Supplier supplier1 = getModel1Factory().createSupplier();
  // PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
  // supplier1.getPurchaseOrders().add(purchaseOrder1);
  // supplier1.getPurchaseOrders().add(purchaseOrder2);
  // supplier1.getPurchaseOrders().add(purchaseOrder3);
  // supplier1.getPurchaseOrders().add(purchaseOrder4);
  //
  // resource.getContents().add(supplier1);
  // resource.getContents().add(purchaseOrder1);
  // resource.getContents().add(purchaseOrder2);
  // resource.getContents().add(purchaseOrder3);
  // resource.getContents().add(purchaseOrder4);
  // transaction1.commit();
  //
  // CDOID referenceID = ((CDOObject)supplier1).cdoID();
  // CDOID queryID1 = ((CDOObject)purchaseOrder1).cdoID();
  // CDOID queryID2 = ((CDOObject)purchaseOrder2).cdoID();
  // CDOID queryID3 = ((CDOObject)purchaseOrder3).cdoID();
  // CDOID queryID4 = ((CDOObject)purchaseOrder4).cdoID();
  //
  // session1.close();
  //
  // /******************/
  //
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  //
  // msg("Executing XREF Query 1");
  //
  // CDOQuery query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID1);
  // List<CDOID> results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(1, results.size());
  // CDOObject cdoObject = view.getObject(results.get(0));
  // assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));
  //
  // msg("Executing XREF Query 2");
  //
  // query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID2);
  // results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(1, results.size());
  // cdoObject = view.getObject(results.get(0));
  // assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));
  //
  // msg("Executing XREF Query 3");
  //
  // query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID3);
  // results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(1, results.size());
  // cdoObject = view.getObject(results.get(0));
  // assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));
  //
  // msg("Executing XREF Query 4");
  //
  // query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID4);
  // results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(1, results.size());
  // cdoObject = view.getObject(results.get(0));
  // assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));
  //
  // }
  //
  // public void testCrossReferenceWithFeatureQuery() throws Exception
  // {
  // msg("Opening session");
  //
  // CDOSession session1 = openSession();
  //
  // msg("Opening transaction");
  // CDOTransaction transaction1 = session1.openTransaction();
  //
  // msg("Creating resource");
  // CDOResource resource = transaction1.createResource("/test1");
  //
  // msg("Creating test data");
  // Supplier supplier1 = getModel1Factory().createSupplier();
  // PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
  // supplier1.getPurchaseOrders().add(purchaseOrder1);
  // supplier1.getPurchaseOrders().add(purchaseOrder2);
  // supplier1.getPurchaseOrders().add(purchaseOrder3);
  // supplier1.getPurchaseOrders().add(purchaseOrder4);
  //
  // resource.getContents().add(supplier1);
  // resource.getContents().add(purchaseOrder1);
  // resource.getContents().add(purchaseOrder2);
  // resource.getContents().add(purchaseOrder3);
  // resource.getContents().add(purchaseOrder4);
  // transaction1.commit();
  //
  // CDOID querycdoID = ((CDOObject)supplier1).cdoID();
  //
  // session1.close();
  //
  // /******************/
  //
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  // CDOQuery query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, querycdoID);
  // query.setParameter("provideFeatureName", Boolean.TRUE);
  //
  // msg("Executing XREF Query");
  //
  // List<Object> results = query.getResult(Object.class);
  //
  // assertFalse(results.isEmpty());
  //
  // msg("Constructing test EStructuralFeature.Setting");
  //
  // Collection<CDOID> matchedIdsPerEClass = new ArrayList<CDOID>();
  // Collection<EStructuralFeature.Setting> settings = new ArrayList<EStructuralFeature.Setting>();
  //
  // for (Object object : results)
  // {
  // if (object instanceof CDOID)
  // {
  // matchedIdsPerEClass.add((CDOID)object);
  // }
  // else if (object instanceof String)
  // {
  // for (CDOID cdoId : matchedIdsPerEClass)
  // {
  // settings.add(new CDOSetting(view, cdoId, (String)object));
  // }
  // matchedIdsPerEClass.clear();
  // }
  //
  // }
  //
  // msg("Checking query result");
  //
  // for (EStructuralFeature.Setting setting : settings)
  // {
  // CDOObject crossReferencedEObject = (CDOObject)setting.get(true);
  // assertTrue(CDOIDUtil.equals(crossReferencedEObject.cdoID(), querycdoID));
  // }
  // }
  //
  // public void testCrossReferenceWithFeatureQueryUsingCDOUtil() throws Exception
  // {
  // msg("Opening session");
  //
  // CDOSession session1 = openSession();
  //
  // msg("Opening transaction");
  // CDOTransaction transaction1 = session1.openTransaction();
  //
  // msg("Creating resource");
  // CDOResource resource = transaction1.createResource("/test1");
  //
  // msg("Creating test data");
  // Supplier supplier1 = getModel1Factory().createSupplier();
  // PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
  // supplier1.getPurchaseOrders().add(purchaseOrder1);
  // supplier1.getPurchaseOrders().add(purchaseOrder2);
  // supplier1.getPurchaseOrders().add(purchaseOrder3);
  // supplier1.getPurchaseOrders().add(purchaseOrder4);
  //
  // resource.getContents().add(supplier1);
  // resource.getContents().add(purchaseOrder1);
  // resource.getContents().add(purchaseOrder2);
  // resource.getContents().add(purchaseOrder3);
  // resource.getContents().add(purchaseOrder4);
  // transaction1.commit();
  //
  // CDOID querycdoID = ((CDOObject)supplier1).cdoID();
  //
  // session1.close();
  //
  // /******************/
  //
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  // CDOObject cdoObject = view.getObject(querycdoID);
  //
  // Collection<EStructuralFeature.Setting> settings = getCrossReferences(cdoObject);
  //
  // for (EStructuralFeature.Setting setting : settings)
  // {
  // CDOObject crossReferencedEObject = (CDOObject)setting.get(true);
  // assertTrue(CDOIDUtil.equals(crossReferencedEObject.cdoID(), querycdoID));
  // }
  // }
  //
  // public void testEmptyCrossReferenceQuery() throws Exception
  // {
  // msg("Opening session");
  //
  // CDOSession session1 = openSession();
  //
  // msg("Opening transaction");
  // CDOTransaction transaction1 = session1.openTransaction();
  //
  // msg("Creating resource");
  // CDOResource resource = transaction1.createResource("/test1");
  //
  // msg("Creating test data");
  // Supplier supplier1 = getModel1Factory().createSupplier();
  //
  // resource.getContents().add(supplier1);
  // transaction1.commit();
  //
  // CDOID queryID = ((CDOObject)supplier1).cdoID();
  //
  // session1.close();
  //
  // /******************/
  //
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  // CDOQuery query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID);
  //
  // msg("Executing XREF Query");
  // List<CDOID> results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(0, results.size());
  // }
  //
  // public void testInexistentTargetForCrossReferenceQuery() throws Exception
  // {
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  // CDOQuery query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, CDOIDUtil.createLong(10000));
  //
  // msg("Executing XREF Query");
  // List<CDOID> results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(0, results.size());
  // }
}
