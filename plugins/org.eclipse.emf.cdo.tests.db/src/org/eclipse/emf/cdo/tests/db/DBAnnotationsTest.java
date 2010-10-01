/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.sql.ResultSet;

/**
 * Test different DB annotations.
 * 
 * @author Kai Schlamp
 */
public class DBAnnotationsTest extends AbstractCDOTest
{
  public void testLengthAnnotationPositive() throws Exception
  {
    msg("Opening session");
    EPackage model1 = createModel();
    addLengthAnnotation(model1, "8");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a product that has a name with an allowed string length.");
    EClass eClass = (EClass)model1.getEClassifier("Product1");
    EObject product = model1.getEFactoryInstance().create(eClass);
    product.eSet(eClass.getEStructuralFeature("name"), "01234567");

    resource.getContents().add(product);
    transaction.commit();
    transaction.close();
    session.close();
  }

  public void testLengthAnnotationNegative() throws Exception
  {
    // HSQL does not support length annotations
    skipConfig(AllTestsDBHsqldb.Hsqldb.INSTANCE);
    skipConfig(AllTestsDBHsqldbNonAudit.HsqldbNonAudit.INSTANCE);
    // XXX PSQL fails, too - need to investigate
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);

    msg("Opening session");
    EPackage model1 = createModel();
    addLengthAnnotation(model1, "8");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a product that has a name with an invalid string length.");
    EClass eClass = (EClass)model1.getEClassifier("Product1");
    EObject product = model1.getEFactoryInstance().create(eClass);
    product.eSet(eClass.getEStructuralFeature("name"), "012345678");

    resource.getContents().add(product);

    try
    {
      transaction.commit();
      fail("Committing too long data did not result in an exception");
    }
    catch (Exception success)
    {
    }
    finally
    {
      transaction.close();
      session.close();
    }
  }

  public void testLengthAnnotationByMetaData() throws CommitException
  {
    // XXX [PSQL] disabled because of Bug 290095
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);

    msg("Opening session");
    EPackage model1 = createModel();
    addLengthAnnotation(model1, "8");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a product.");
    EClass eClass = (EClass)model1.getEClassifier("Product1");
    EObject product = model1.getEFactoryInstance().create(eClass);
    resource.getContents().add(product);

    transaction.commit();
    transaction.close();
    session.close();

    msg("Check if column size was correctly set.");
    new DBStoreVerifier(getRepository())
    {
      @Override
      protected void doVerify() throws Exception
      {
        ResultSet rset = getMetaData().getColumns(null, null, "PRODUCT1", "NAME");
        rset.next();
        assertEquals("8", rset.getString(7));
      }
    }.verify();
  }

  public void testTypeAnnotationByMetaData() throws CommitException
  {
    // XXX [PSQL] disabled because of Bug 290095
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);

    // HSQL does not support type annotations
    skipConfig(AllTestsDBHsqldb.Hsqldb.INSTANCE);
    skipConfig(AllTestsDBHsqldbNonAudit.HsqldbNonAudit.INSTANCE);

    msg("Opening session");
    EPackage model1 = createModel();
    addTypeAnnotation(model1, "CLOB");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a category.");
    EClass eClass = (EClass)model1.getEClassifier("Category");
    EObject category = model1.getEFactoryInstance().create(eClass);

    resource.getContents().add(category);
    transaction.commit();
    transaction.close();
    session.close();

    msg("Check if column type was correctly set.");
    new DBStoreVerifier(getRepository())
    {
      @Override
      protected void doVerify() throws Exception
      {
        ResultSet rset = getMetaData().getColumns(null, null, "CATEGORY", "NAME");
        rset.next();
        assertEquals("CLOB", rset.getString(6));
      }
    }.verify();
  }

  public void testTableNameAnnotationByMetaData() throws CommitException
  {
    // XXX [PSQL] disabled because of Bug 290095
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);

    msg("Opening session");
    EPackage model1 = createModel();
    addTableNameAnnotation(model1, "Subject");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a category.");
    EClass eClass = (EClass)model1.getEClassifier("Category");
    EObject category = model1.getEFactoryInstance().create(eClass);

    resource.getContents().add(category);
    transaction.commit();
    transaction.close();
    session.close();

    msg("Check if table name was correctly set.");
    new DBStoreVerifier(getRepository())
    {
      @Override
      protected void doVerify() throws Exception
      {
        ResultSet rset = getMetaData().getTables(null, null, "SUBJECT", null);
        rset.next();
        assertEquals("SUBJECT", rset.getString(3));
      }
    }.verify();
  }

  public void testColumnNameAnnotationByMetaData() throws CommitException
  {
    // XXX [PSQL] disabled because of Bug 290095
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);

    msg("Opening session");
    EPackage model1 = createModel();
    addColumnNameAnnotation(model1, "TOPIC");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a category.");
    EClass eClass = (EClass)model1.getEClassifier("Category");
    EObject category = model1.getEFactoryInstance().create(eClass);

    resource.getContents().add(category);
    transaction.commit();
    transaction.close();
    session.close();

    msg("Check if table name was correctly set.");
    new DBStoreVerifier(getRepository())
    {
      @Override
      protected void doVerify() throws Exception
      {
        ResultSet rset = getMetaData().getColumns(null, null, "CATEGORY", "TOPIC");
        rset.next();
        assertEquals("TOPIC", rset.getString(4));
      }
    }.verify();
  }

  public void testColumnNameTypeAnnotationByMetaData() throws CommitException
  {
    // HSQL does not support type annotations
    skipConfig(AllTestsDBHsqldb.Hsqldb.INSTANCE);
    skipConfig(AllTestsDBHsqldbNonAudit.HsqldbNonAudit.INSTANCE);

    // XXX [PSQL] disabled because of Bug 290095
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);

    msg("Opening session");
    EPackage model1 = createModel();
    addColumnNameAndTypeAnnoation(model1, "TOPIC", "CLOB");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a category.");
    EClass eClass = (EClass)model1.getEClassifier("Category");
    EObject category = model1.getEFactoryInstance().create(eClass);

    resource.getContents().add(category);
    transaction.commit();
    transaction.close();
    session.close();

    msg("Check if table name was correctly set.");
    new DBStoreVerifier(getRepository())
    {
      @Override
      protected void doVerify() throws Exception
      {
        ResultSet rset = getMetaData().getColumns(null, null, "CATEGORY", "TOPIC");
        rset.next();
        assertEquals("TOPIC", rset.getString(4));
        assertEquals("CLOB", rset.getString(6));
      }
    }.verify();
  }

  public void testTableMappingAnnotationByMetaData() throws CommitException
  {
    msg("Opening session");
    EPackage model1 = createModel();
    addTableMappingAnnotation(model1, "OrderDetail", "Company");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a category.");
    EClass eClass = (EClass)model1.getEClassifier("Category");
    EObject category = model1.getEFactoryInstance().create(eClass);

    resource.getContents().add(category);
    transaction.commit();
    transaction.close();
    session.close();

    msg("Check if table name was correctly set.");
    new DBStoreVerifier(getRepository())
    {
      @Override
      protected void doVerify() throws Exception
      {
        ResultSet rset = getMetaData().getTables(null, null, null, null);

        boolean orderDetailTableCreated = false;
        boolean companyTableCreated = false;
        boolean categoryTableCreated = false;

        while (rset.next())
        {
          String tableName = rset.getString(3);
          if ("ORDERDETAIL".equalsIgnoreCase(tableName))
          {
            orderDetailTableCreated = true;
          }
          else if ("COMPANY".equalsIgnoreCase(tableName))
          {
            companyTableCreated = true;
          }
          else if ("CATEGORY".equalsIgnoreCase(tableName))
          {
            categoryTableCreated = true;
          }
        }

        assertEquals(false, orderDetailTableCreated);
        assertEquals(false, companyTableCreated);
        assertEquals(true, categoryTableCreated);
      }
    }.verify();
  }

  private EPackage createModel()
  {
    EPackage ePackage = EcoreUtil.copy(getModel1Package());
    ePackage.setNsURI(ePackage.getNsURI() + "-dynamic");
    return ePackage;
  }

  private void addLengthAnnotation(EPackage model1, String value)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("http://www.eclipse.org/CDO/DBStore");
    annotation.getDetails().put("columnLength", value);

    EClass product1 = (EClass)model1.getEClassifier("Product1");
    EStructuralFeature element = product1.getEStructuralFeature(Model1Package.PRODUCT1__NAME);
    element.getEAnnotations().add(annotation);
  }

  private void addTypeAnnotation(EPackage model1, String value)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("http://www.eclipse.org/CDO/DBStore");
    annotation.getDetails().put("columnType", value);

    EClass category = (EClass)model1.getEClassifier("Category");
    EStructuralFeature element = category.getEStructuralFeature(Model1Package.CATEGORY__NAME);
    element.getEAnnotations().add(annotation);
  }

  private void addTableNameAnnotation(EPackage model1, String value)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("http://www.eclipse.org/CDO/DBStore");
    annotation.getDetails().put("tableName", value);

    EClass category = (EClass)model1.getEClassifier("Category");
    category.getEAnnotations().add(annotation);
  }

  private void addColumnNameAnnotation(EPackage model1, String value)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("http://www.eclipse.org/CDO/DBStore");
    annotation.getDetails().put("columnName", value);

    EClass category = (EClass)model1.getEClassifier("Category");
    EStructuralFeature element = category.getEStructuralFeature(Model1Package.CATEGORY__NAME);
    element.getEAnnotations().add(annotation);
  }

  private void addColumnNameAndTypeAnnoation(EPackage model1, String name, String type)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("http://www.eclipse.org/CDO/DBStore");
    annotation.getDetails().put("columnName", name);
    annotation.getDetails().put("columnType", type);

    EClass category = (EClass)model1.getEClassifier("Category");
    EStructuralFeature element = category.getEStructuralFeature(Model1Package.CATEGORY__NAME);
    element.getEAnnotations().add(annotation);
  }

  private void addTableMappingAnnotation(EPackage model1, String... unmappedTables)
  {
    for (String unmappedTable : unmappedTables)
    {
      EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
      annotation.setSource("http://www.eclipse.org/CDO/DBStore");
      annotation.getDetails().put("tableMapping", "NONE");
      
      // ID is defined in plugin.xml
      annotation.getDetails().put("typeMapping", "org.eclipse.emf.cdo.tests.db.EIntToVarchar");

      EClass orderDetail = (EClass)model1.getEClassifier(unmappedTable);
      orderDetail.getEAnnotations().add(annotation);
    }
  }
}
