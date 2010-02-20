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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistryPopulator;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.mango.MangoValue;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class PackageRegistryTest extends AbstractCDOTest
{
  public void testGeneratedPackage() throws Exception
  {
    {
      // Create resource in session 1
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  public void testCommitTwoPackages() throws Exception
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(getModel1Package());
      session.getPackageRegistry().putEPackage(getModel2Package());
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      SpecialPurchaseOrder specialPurchaseOrder = getModel2Factory().createSpecialPurchaseOrder();
      specialPurchaseOrder.setDiscountCode("12345");
      res.getContents().add(specialPurchaseOrder);
      transaction.commit();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    SpecialPurchaseOrder specialPurchaseOrder = (SpecialPurchaseOrder)res.getContents().get(0);
    assertEquals("12345", specialPurchaseOrder.getDiscountCode());
  }

  public void testCommitUnrelatedPackage() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
      res.getContents().add(purchaseOrder);

      transaction.commit();
      session.close();
    }

    CDOSession session = openMangoSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    MangoValue value = getMangoFactory().createMangoValue();
    value.setName("V0");
    res.getContents().add(value);

    transaction.commit();
    session.close();
  }

  public void testCommitNestedPackages() throws Exception
  {
    CDOSession session = openSession();
    assertEquals(2, session.getPackageRegistry().size());

    session.getPackageRegistry().putEPackage(getModel3SubpackagePackage());
    assertEquals(4, session.getPackageRegistry().size());

    session.close();
  }

  public void testCommitTopLevelPackages() throws Exception
  {
    CDOSession session = openSession();
    assertEquals(2, session.getPackageRegistry().size());

    session.getPackageRegistry().putEPackage(getModel3Package());
    assertEquals(4, session.getPackageRegistry().size());

    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res");

    Class1 class1 = getModel3Factory().createClass1();
    res.getContents().add(class1);
    transaction.commit();

    EPackage model3Package = session.getPackageRegistry().getEPackage(getModel3Package().getNsURI());
    assertNotNull(model3Package);
    session.close();
  }

  public void testLoadNestedPackages() throws Exception
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(getModel3Package());

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Class1 class1 = getModel3Factory().createClass1();
      res.getContents().add(class1);
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    EPackage model3Package = session.getPackageRegistry().getEPackage(getModel3Package().getNsURI());
    assertNotNull(model3Package);

    EPackage subPackage = session.getPackageRegistry().getEPackage(getModel3SubpackagePackage().getNsURI());
    assertNotNull(subPackage);
    session.close();
  }

  public void testCommitCircularPackages() throws Exception
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(Model3Package.eINSTANCE);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res1 = transaction.createResource("/res1");
      CDOResource res2 = transaction.createResource("/res2");

      Class1 class1 = getModel3Factory().createClass1();
      Class2 class2 = getModel3SubpackageFactory().createClass2();
      class1.getClass2().add(class2);

      res1.getContents().add(class1);
      res2.getContents().add(class2);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res1 = transaction.getResource("/res1");

      Class1 class1 = (Class1)res1.getContents().get(0);
      assertNotNull(class1);
      EList<Class2> class22 = class1.getClass2();
      Class2 class2 = class22.get(0);
      assertNotNull(class2);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res2 = transaction.getResource("/res2");

    Class2 class2 = (Class2)res2.getContents().get(0);
    assertNotNull(class2);
    Class1 class1 = class2.getClass1().get(0);
    assertNotNull(class1);
  }

  public void testPackageRegistry() throws Exception
  {
    {
      // Create resource in session 1
      CDOSession session = openSession(IRepositoryConfig.REPOSITORY_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  /**
   * Bug 249383: Dynamic models in the global EPackage.Registry are not committed bug 249383
   */
  public void testGlobalDynamicPackageEager() throws Exception
  {
    String nsURI = "http://dynamic";

    try
    {
      EPackage p = EcoreFactory.eINSTANCE.createEPackage();
      p.setName("dynamic");
      p.setNsPrefix("dynamic");
      p.setNsURI(nsURI);

      EClass c = EcoreFactory.eINSTANCE.createEClass();
      c.setName("DClass");

      p.getEClassifiers().add(c);
      EPackage.Registry.INSTANCE.put(nsURI, p);

      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(p);
      p = session.getPackageRegistry().getEPackage(nsURI);

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      EFactory factory = p.getEFactoryInstance();
      EObject object = factory.create(c);

      res.getContents().add(object);
      transaction.commit();
      session.close();
    }
    finally
    {
      EPackage.Registry.INSTANCE.remove(nsURI);
    }
  }

  /**
   * Bug 249383: Dynamic models in the global EPackage.Registry are not committed bug 249383
   */
  public void testGlobalDynamicPackage() throws Exception
  {
    String nsURI = "http://dynamic";

    try
    {
      EPackage p = EcoreFactory.eINSTANCE.createEPackage();
      p.setName("dynamic");
      p.setNsPrefix("dynamic");
      p.setNsURI(nsURI);

      EClass c = EcoreFactory.eINSTANCE.createEClass();
      c.setName("DClass");

      p.getEClassifiers().add(c);
      CDOUtil.prepareDynamicEPackage(p);
      EPackage.Registry.INSTANCE.put(nsURI, p);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      EFactory factory = p.getEFactoryInstance();
      EObject object = factory.create(c);

      res.getContents().add(object);
      transaction.commit();
      session.close();
    }
    catch (Exception ex)
    {
      EPackage.Registry.INSTANCE.remove(nsURI);
    }
  }

  public void testGlobalDynamicPackageUnprepared() throws Exception
  {
    String nsURI = "http://dynamic";

    try
    {
      EPackage p = EcoreFactory.eINSTANCE.createEPackage();
      p.setName("dynamic");
      p.setNsPrefix("dynamic");
      p.setNsURI(nsURI);

      EClass c = EcoreFactory.eINSTANCE.createEClass();
      c.setName("DClass");

      p.getEClassifiers().add(c);
      EPackage.Registry.INSTANCE.put(nsURI, p);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      EFactory factory = p.getEFactoryInstance();
      EObject object = factory.create(c);

      res.getContents().add(object);
      transaction.commit();
      session.close();

      fail("Expected: IllegalArgumentException: Use CDOFactory to create dynamic object");
    }
    catch (IllegalArgumentException success)
    {
      // SUCCESS
    }
    finally
    {
      EPackage.Registry.INSTANCE.remove(nsURI);
    }
  }

  public void testDynamicPackageFactory() throws Exception
  {
    // -Dorg.eclipse.emf.ecore.EPackage.Registry.INSTANCE=org.eclipse.emf.ecore.impl.CDOPackageRegistryImpl

    {
      EPackage model1 = loadModel("model1.ecore");
      EClass companyClass = (EClass)model1.getEClassifier("Company");
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
      Resource model1Resource = model1.eResource();

      // Create resource in session 1
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(model1);
      assertEquals(model1Resource, model1.eResource());

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      EFactory factory = model1.getEFactoryInstance();
      EObject company = factory.create(companyClass);
      company.eSet(nameAttribute, "Eike");
      res.getContents().add(company);
      transaction.commit();
      session.close();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    CDOObject company = CDOUtil.getCDOObject(res.getContents().get(0));
    EClass companyClass = company.eClass();
    EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
    String name = (String)company.eGet(nameAttribute);
    assertEquals("Eike", name);
    session.close();
  }

  public void testDynamicPackageNewInstance() throws Exception
  {
    {
      EPackage model1 = loadModel("model1.ecore");
      EClass companyClass = (EClass)model1.getEClassifier("Company");
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");

      // Create resource in session 1
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(model1);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      CDOObject company = CDOUtil.getCDOObject(EcoreUtil.create(companyClass));
      company.eSet(nameAttribute, "Eike");
      res.getContents().add(company);
      transaction.commit();
      session.close();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    CDOObject company = CDOUtil.getCDOObject(res.getContents().get(0));
    EClass companyClass = company.eClass();
    EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
    String name = (String)company.eGet(nameAttribute);
    assertEquals("Eike", name);
    session.close();
  }

  public void testDuplicatePackageRegistration() throws Exception
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();

    try
    {
      {
        CDOTransaction transaction = session1.openTransaction();
        CDOResource res = transaction.createResource("/res1");

        Company company = getModel1Factory().createCompany();
        company.setName("Company1");
        res.getContents().add(company);
        transaction.commit();
        sleep(1000); // Give session2 a chance to react
      }

      CDOPackageRegistry packageRegistry = session2.getPackageRegistry();
      Model1Package model1Package = getModel1Package();
      packageRegistry.putEPackage(model1Package);

      CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(model1Package);
      assertEquals(false, packageUnit.getTopLevelPackageInfo().getMetaIDRange().isTemporary());
      assertEquals(CDOPackageUnit.State.LOADED, packageUnit.getState());

      {
        CDOTransaction transaction = session2.openTransaction();
        CDOResource res = transaction.createResource("/res2");

        Company company = getModel1Factory().createCompany();
        company.setName("Company2");
        res.getContents().add(company);
        transaction.commit();
      }
    }
    finally
    {
      session1.close();
      session2.close();
    }
  }

  public void testReuseCommittedPackage() throws Exception
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();

    try
    {
      {
        CDOTransaction transaction = session1.openTransaction();
        CDOResource res = transaction.createResource("/res1");

        Company company = getModel1Factory().createCompany();
        company.setName("Company1");
        res.getContents().add(company);
        transaction.commit();
      }

      {
        CDOTransaction transaction = session2.openTransaction();
        CDOResource res = transaction.createResource("/res2");

        Company company = getModel1Factory().createCompany();
        company.setName("Company2");
        res.getContents().add(company);
        transaction.commit();
      }
    }
    finally
    {
      session1.close();
      session2.close();
    }
  }

  public void testConcurrentPackageRegistration() throws Exception
  {
    CDOSession session1 = openModel1Session();
    CDOSession session2 = openModel1Session();

    try
    {
      {
        CDOTransaction transaction = session1.openTransaction();
        CDOResource res = transaction.createResource("/res1");

        Company company = getModel1Factory().createCompany();
        company.setName("Company1");
        res.getContents().add(company);
        transaction.commit();
        sleep(1000); // Give session2 a chance to react
      }

      CDOPackageRegistry packageRegistry = session2.getPackageRegistry();
      Model1Package model1Package = getModel1Package();

      CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(model1Package);
      assertEquals(false, packageUnit.getTopLevelPackageInfo().getMetaIDRange().isTemporary());
      assertEquals(CDOPackageUnit.State.LOADED, packageUnit.getState());

      {
        CDOTransaction transaction = session2.openTransaction();
        CDOResource res = transaction.createResource("/res2");

        Company company = getModel1Factory().createCompany();
        company.setName("Company2");
        res.getContents().add(company);
        transaction.commit();
      }
    }
    finally
    {
      session1.close();
      session2.close();
    }
  }

  public void testPopulator() throws Exception
  {
    String nsURI = "http://dynamic";
    EPackage.Registry registry = new EPackageRegistryImpl();

    {
      EPackage p = EcoreFactory.eINSTANCE.createEPackage();
      p.setName("dynamic");
      p.setNsPrefix("dynamic");
      p.setNsURI(nsURI);

      EClass c = EcoreFactory.eINSTANCE.createEClass();
      c.setName("DClass");

      p.getEClassifiers().add(c);
      registry.put(nsURI, p);

      CDOSession session = openSession();
      CDOPackageRegistryPopulator.populate(registry, session.getPackageRegistry());

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  public void testPopulatorGlobal() throws Exception
  {
    String nsURI = "http://dynamic";
    EPackage.Registry registry = EPackage.Registry.INSTANCE;

    try
    {
      {
        EPackage p = EcoreFactory.eINSTANCE.createEPackage();
        p.setName("dynamic");
        p.setNsPrefix("dynamic");
        p.setNsURI(nsURI);

        EClass c = EcoreFactory.eINSTANCE.createEClass();
        c.setName("DClass");

        p.getEClassifiers().add(c);
        registry.put(nsURI, p);

        CDOSession session = openSession();
        CDOPackageRegistryPopulator.populate(registry, session.getPackageRegistry());

        CDOTransaction transaction = session.openTransaction();
        CDOResource res = transaction.createResource("/res");

        Company company = getModel1Factory().createCompany();
        company.setName("Eike");
        res.getContents().add(company);
        transaction.commit();
      }

      // Load resource in session 2
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource("/res");

      Company company = (Company)res.getContents().get(0);
      assertEquals("Eike", company.getName());
    }
    finally
    {
      EPackage.Registry.INSTANCE.remove(nsURI);
    }
  }

  public void testLaziness() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  private static EPackage loadModel(String fileName) throws IOException
  {
    URI uri = URI.createURI("file://" + fileName);
    XMIResource resource = new XMIResourceImpl(uri);
    resource.setEncoding("UTF-8");
    resource.load(OM.BUNDLE.getInputStream(fileName), null);
    return (EPackage)resource.getContents().get(0);
  }
}
