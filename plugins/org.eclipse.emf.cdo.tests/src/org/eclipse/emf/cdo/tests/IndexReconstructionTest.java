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
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;

import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class IndexReconstructionTest extends AbstractCDOTest
{
  private CDOTransaction transaction;

  private CDOResource resource;

  private Set<ReconstructedIndex> expectedIndices;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    OMPlatform.INSTANCE.setDebugging(false);
    CDOSession session = openModel1Session();
    transaction = session.openTransaction();
    resource = transaction.createResource("/test1");
    expectedIndices = new HashSet<ReconstructedIndex>();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    CDOSession session = transaction.getSession();
    expectedIndices = null;
    transaction.close();
    transaction = null;
    session.close();
    session = null;
    super.doTearDown();
  }

  public void testAddBefore1() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(0, createSalesOrder());
    customer.getSalesOrders().add(0, createSalesOrder());
    customer.getSalesOrders().add(0, createSalesOrder());
    customer.getSalesOrders().add(0, createSalesOrder());

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 0);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 1);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 2);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 3);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 4);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    checkReconstructedIndices();
  }

  public void testAddBefore4() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(0, createSalesOrder());
    customer.getSalesOrders().add(0, createSalesOrder());
    customer.getSalesOrders().add(0, createSalesOrder());
    customer.getSalesOrders().add(0, createSalesOrder());

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 0);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 1);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  public void testAddAfter1() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 1);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 4);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 2);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 3);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 4);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    checkReconstructedIndices();
  }

  public void testAddAfter4() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 4);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 5);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 6);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  public void testRemove1() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().remove(0);

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 4);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 5);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  public void testRemove4() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().remove(0);
    customer.getSalesOrders().remove(0);
    customer.getSalesOrders().remove(0);
    customer.getSalesOrders().remove(0);

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 0);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 1);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  public void testRemoveAdded() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  public void testRemoveLastAdded() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);

    dumpReconstructedIndices();
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  public void testReAdded() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().remove(2);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 4);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 9);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 10);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 11);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 12);
    checkReconstructedIndices();
  }

  public void testMoveBehindEnd() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().move(7, 2);
    customer.getSalesOrders().move(7, 2);

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 4);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  public void testMoveBackward() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().move(5, 0);
    customer.getSalesOrders().move(5, 0);

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 6);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  public void testMoveForward() throws Exception
  {
    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    transaction.commit();

    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().add(createSalesOrder());
    customer.getSalesOrders().move(2, 7);
    customer.getSalesOrders().move(2, 7);

    dumpReconstructedIndices();
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 2);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 3);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 6);
    expectIndex(customer.cdoID(), Model1Package.eINSTANCE.getCustomer_SalesOrders(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 5);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 6);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 7);
    expectIndex(resource.cdoID(), EresourcePackage.eINSTANCE.getCDOResource_Contents(), 8);
    checkReconstructedIndices();
  }

  private SalesOrder createSalesOrder()
  {
    SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    resource.getContents().add(salesOrder);
    return salesOrder;
  }

  private void dumpReconstructedIndices()
  {
    Collection<CDORevisionDelta> revisionDeltas = transaction.getRevisionDeltas().values();
    for (CDORevisionDelta revisionDelta : revisionDeltas)
    {
      for (CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas())
      {
        if (featureDelta instanceof CDOListFeatureDeltaImpl)
        {
          int[] indices = ((CDOListFeatureDeltaImpl)featureDelta).reconstructAddedIndices();
          if (indices[0] != 0)
          {
            System.out.print(revisionDelta.getID());
            System.out.print(": ");
            System.out.print(featureDelta.getFeature().getContainingClass().getName());
            System.out.print(".");
            System.out.print(featureDelta.getFeature().getName());
            System.out.print("=");
            for (int i = 1; i <= indices[0]; i++)
            {
              System.out.print(indices[i] + " ");
            }

            System.out.println();
          }
        }
      }
    }
  }

  private void expectIndex(CDOID id, EStructuralFeature eFeature, int index)
  {
    CDOFeature feature = transaction.getSession().getPackageManager().convert(eFeature);
    ReconstructedIndex expectedIndex = new ReconstructedIndex(id, feature, index);
    if (!expectedIndices.add(expectedIndex))
    {
      fail("Duplicate expected ids: " + expectedIndex);
    }
  }

  private void checkReconstructedIndices()
  {
    boolean fail = false;
    Collection<CDORevisionDelta> revisionDeltas = transaction.getRevisionDeltas().values();
    for (CDORevisionDelta revisionDelta : revisionDeltas)
    {
      for (CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas())
      {
        if (featureDelta instanceof CDOListFeatureDeltaImpl)
        {
          int[] indices = ((CDOListFeatureDeltaImpl)featureDelta).reconstructAddedIndices();
          for (int i = 1; i <= indices[0]; i++)
          {
            ReconstructedIndex expectedIndex = new ReconstructedIndex(revisionDelta.getID(), featureDelta.getFeature(),
                indices[i]);
            if (!expectedIndices.remove(expectedIndex))
            {
              System.out.println("Reconstructed but not expected ids: " + expectedIndex);
              fail = true;
            }
          }
        }
      }
    }

    if (!expectedIndices.isEmpty())
    {
      System.out.println("Expected but not reconstructed indices: " + expectedIndices);
      fail = true;
    }

    if (fail)
    {
      fail("Detailed cause has been written to the console");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ReconstructedIndex
  {
    private CDOID id;

    private CDOFeature feature;

    private int index;

    public ReconstructedIndex(CDOID id, CDOFeature feature, int index)
    {
      this.feature = feature;
      this.id = id;
      this.index = index;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof ReconstructedIndex)
      {
        ReconstructedIndex that = (ReconstructedIndex)obj;
        return id.equals(that.id) && feature.equals(that.feature) && index == that.index;
      }

      return false;
    }

    @Override
    public int hashCode()
    {
      return id.hashCode() ^ feature.hashCode() ^ index;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("{0}: {1}.{2}={3}", id, feature.getContainingClass().getName(), feature.getName(),
          index);
    }
  }
}
