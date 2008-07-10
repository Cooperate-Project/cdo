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
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.MultiContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.SingleContainedElement;
import org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

public class ComplexTest extends AbstractCDOTest
{
  private static long uniqueCounter = System.currentTimeMillis();

  private model4Factory factory = model4Factory.eINSTANCE;

  private CDOResource resource1 = null;

  private CDOResource resource2 = null;

  private CDOTransaction transaction = null;

  private CDOSession session = null;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    session = openSession();
    session.getPackageRegistry().putEPackage(model4interfacesPackage.eINSTANCE);
    session.getPackageRegistry().putEPackage(model4Package.eINSTANCE);

    transaction = session.openTransaction();

    String resource1basename = "/resources/1/" + uniqueCounter;
    String resource2basename = "/resources/1/" + uniqueCounter;
    uniqueCounter++;

    resource1 = transaction.createResource(resource1basename);
    resource2 = transaction.createResource(resource2basename);

    commit();
  }

  private void commit()
  {
    transaction.commit();
  }

  public void testPlainSingleNonContainedBidirectional()
  {
    RefSingleNonContained container = factory.createRefSingleNonContained();
    SingleNonContainedElement element0 = factory.createSingleNonContainedElement();
    element0.setName("PlainSingleNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testPlainSingleContainedBidirectional()
  {
    RefSingleContained container = factory.createRefSingleContained();
    SingleContainedElement element0 = factory.createSingleContainedElement();
    element0.setName("PlainSingleContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    commit();
  }

  public void testPlainMultiNonContainedBidirectional()
  {
    RefMultiNonContained container = factory.createRefMultiNonContained();
    MultiNonContainedElement element0 = factory.createMultiNonContainedElement();
    element0.setName("PlainMultiNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    MultiNonContainedElement element1 = factory.createMultiNonContainedElement();
    element1.setName("PlainMultiNonContainedBidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testPlainMultiContainedBidirectional()
  {
    RefMultiContained container = factory.createRefMultiContained();
    MultiContainedElement element0 = factory.createMultiContainedElement();
    element0.setName("PlainMultiContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    MultiContainedElement element1 = factory.createMultiContainedElement();
    element1.setName("PlainMultiContainedBidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testPlainSingleNonContainedUnidirectional()
  {
    RefSingleNonContainedNPL container = factory.createRefSingleNonContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("PlainSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testPlainSingleContainedUnidirectional()
  {
    RefSingleContainedNPL container = factory.createRefSingleContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("PlainSingleContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    commit();
  }

  public void testPlainMultiNonContainedUnidirectional()
  {
    RefMultiNonContainedNPL container = factory.createRefMultiNonContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("PlainMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    ContainedElementNoOpposite element1 = factory.createContainedElementNoOpposite();
    element1.setName("PlainMultiNonContainedUnidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testPlainMultiContainedUnidirectional()
  {
    RefMultiContainedNPL container = factory.createRefMultiContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("PlainMultiContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    ContainedElementNoOpposite element1 = factory.createContainedElementNoOpposite();
    element1.setName("PlainMultiContainedUnidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testGenRefSingleNonContainedUnidirectional()
  {
    GenRefSingleNonContained container = factory.createGenRefSingleNonContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testGenRefSingleContainedUnidirectional()
  {
    GenRefSingleContained container = factory.createGenRefSingleContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefSingleContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);

    resource1.getContents().add(element0);

    commit();
  }

  public void testGenRefMultiNonContainedUnidirectional()
  {
    GenRefMultiNonContained container = factory.createGenRefMultiNonContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("GenRefMultiNonContainedUnidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testGenRefMultiContainedUnidirectional()
  {
    GenRefMultiContained container = factory.createGenRefMultiContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefMultiContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("GenRefMultiContainedUnidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testIfcimplSingleNonContainedBidirectional()
  {
    ImplSingleRefNonContainer container = factory.createImplSingleRefNonContainer();
    ImplSingleRefNonContainedElement element0 = factory.createImplSingleRefNonContainedElement();
    element0.setName("IfcimplSingleNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testIfcimplSingleContainedBidirectional()
  {
    ImplSingleRefContainer container = factory.createImplSingleRefContainer();
    ImplSingleRefContainedElement element0 = factory.createImplSingleRefContainedElement();
    element0.setName("IfcimplSingleContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    commit();
  }

  public void testIfcimplMultiNonContainedBidirectional()
  {
    ImplMultiRefNonContainer container = factory.createImplMultiRefNonContainer();
    ImplMultiRefNonContainedElement element0 = factory.createImplMultiRefNonContainedElement();
    element0.setName("IfcimplMultiNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    ImplMultiRefNonContainedElement element1 = factory.createImplMultiRefNonContainedElement();
    element1.setName("IfcimplMultiNonContainedBidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testIfcimplMultiContainedBidirectional()
  {
    ImplMultiRefContainer container = factory.createImplMultiRefContainer();
    ImplMultiRefContainedElement element0 = factory.createImplMultiRefContainedElement();
    element0.setName("IfcimplMultiContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    ImplMultiRefContainedElement element1 = factory.createImplMultiRefContainedElement();
    element1.setName("IfcimplMultiContainedBidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testIfcimplSingleNonContainedUnidirectional()
  {
    ImplSingleRefNonContainerNPL container = factory.createImplSingleRefNonContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("IfcimplSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testIfcimplSingleContainedUnidirectional()
  {
    ImplSingleRefContainerNPL container = factory.createImplSingleRefContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("IfcimplSingleContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    commit();
  }

  public void testIfcimplMultiNonContainedUnidirectional()
  {
    ImplMultiRefNonContainerNPL container = factory.createImplMultiRefNonContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("IfcimplMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("IfcimplMultiNonContainedUnidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testIfcimplMultiContainedUnidirectional()
  {
    ImplMultiRefContainerNPL container = factory.createImplMultiRefContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("IfcimplMultiContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("IfcimplMultiContainedUnidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testCrossResourcePlainSingleNonContainedBidirectional()
  {
    RefSingleNonContained container = factory.createRefSingleNonContained();
    SingleNonContainedElement element0 = factory.createSingleNonContainedElement();
    element0.setName("CrossResourcePlainSingleNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testCrossResourcePlainMultiNonContainedBidirectional()
  {
    RefMultiNonContained container = factory.createRefMultiNonContained();
    MultiNonContainedElement element0 = factory.createMultiNonContainedElement();
    element0.setName("CrossResourcePlainMultiNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    MultiNonContainedElement element1 = factory.createMultiNonContainedElement();
    element1.setName("CrossResourcePlainMultiNonContainedBidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testCrossResourcePlainSingleNonContainedUnidirectional()
  {
    RefSingleNonContainedNPL container = factory.createRefSingleNonContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("CrossResourcePlainSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testCrossResourcePlainMultiNonContainedUnidirectional()
  {
    RefMultiNonContainedNPL container = factory.createRefMultiNonContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("CrossResourcePlainMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    ContainedElementNoOpposite element1 = factory.createContainedElementNoOpposite();
    element1.setName("CrossResourcePlainMultiNonContainedUnidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testCrossResourceGenRefSingleNonContainedUnidirectional()
  {
    GenRefSingleNonContained container = factory.createGenRefSingleNonContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("CrossResourceGenRefSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testCrossResourceGenRefMultiNonContainedUnidirectional()
  {
    GenRefMultiNonContained container = factory.createGenRefMultiNonContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("CrossResourceGenRefMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("CrossResourceGenRefMultiNonContainedUnidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testCrossResourceIfcimplSingleNonContainedBidirectional()
  {
    ImplSingleRefNonContainer container = factory.createImplSingleRefNonContainer();
    ImplSingleRefNonContainedElement element0 = factory.createImplSingleRefNonContainedElement();
    element0.setName("CrossResourceIfcimplSingleNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testCrossResourceIfcimplMultiNonContainedBidirectional()
  {
    ImplMultiRefNonContainer container = factory.createImplMultiRefNonContainer();
    ImplMultiRefNonContainedElement element0 = factory.createImplMultiRefNonContainedElement();
    element0.setName("CrossResourceIfcimplMultiNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    ImplMultiRefNonContainedElement element1 = factory.createImplMultiRefNonContainedElement();
    element1.setName("CrossResourceIfcimplMultiNonContainedBidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testCrossResourceIfcimplSingleNonContainedUnidirectional()
  {
    ImplSingleRefNonContainerNPL container = factory.createImplSingleRefNonContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("CrossResourceIfcimplSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();
  }

  public void testCrossResourceIfcimplMultiNonContainedUnidirectional()
  {
    ImplMultiRefNonContainerNPL container = factory.createImplMultiRefNonContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("CrossResourceIfcimplMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("CrossResourceIfcimplMultiNonContainedUnidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();
  }

  public void testMultipleTransactions3()
  {

    CDOTransaction transaction1 = session.openTransaction();
    String resource1path = "/resources/3/" + uniqueCounter;
    CDOResource resource1 = transaction1.createResource(resource1path);

    CDOTransaction transaction2 = session.openTransaction();
    String resource2path = "/resources/4/" + uniqueCounter++;
    CDOResource resource2 = transaction2.createResource(resource2path);

    MultiContainedElement element0 = factory.createMultiContainedElement();
    element0.setName("MultipleTransactions-Element-0");

    MultiContainedElement element1 = factory.createMultiContainedElement();
    element1.setName("MultipleTransactions-Element-0");

    MultiContainedElement element2 = factory.createMultiContainedElement();
    element2.setName("MultipleTransactions-Element-0");

    MultiContainedElement element3 = factory.createMultiContainedElement();
    element3.setName("MultipleTransactions-Element-0");

    resource1.getContents().add(element0);
    resource1.getContents().add(element1);
    resource1.getContents().add(element2);
    resource1.getContents().add(element3);

    transaction1.commit();

    RefMultiContained container = factory.createRefMultiContained();
    resource2.getContents().add(container);

    transaction2.commit();

    CDOTransaction transaction3 = session.openTransaction();
    EList<EObject> elements = transaction3.getResource(resource1path).getContents();
    EList<EObject> containers = transaction3.getResource(resource2path).getContents();

    RefMultiContained container_work = (RefMultiContained)containers.get(0);
    for (EObject o : elements)
    {
      MultiContainedElement element_work = (MultiContainedElement)o;
      container_work.getElements().add(element_work);
      transaction3.commit();
    }
  }

  public void testMultipleTransactions2()
  {

    CDOTransaction transaction1 = session.openTransaction();
    String resource1path = "/resources/3/" + uniqueCounter;
    CDOResource resource1 = transaction1.createResource(resource1path);

    CDOTransaction transaction2 = session.openTransaction();
    String resource2path = "/resources/4/" + uniqueCounter++;
    CDOResource resource2 = transaction2.createResource(resource2path);

    MultiContainedElement element0 = factory.createMultiContainedElement();
    element0.setName("MultipleTransactions-Element-0");

    MultiContainedElement element1 = factory.createMultiContainedElement();
    element1.setName("MultipleTransactions-Element-0");

    MultiContainedElement element2 = factory.createMultiContainedElement();
    element2.setName("MultipleTransactions-Element-0");

    MultiContainedElement element3 = factory.createMultiContainedElement();
    element3.setName("MultipleTransactions-Element-0");

    resource1.getContents().add(element0);
    resource1.getContents().add(element1);
    resource1.getContents().add(element2);
    resource1.getContents().add(element3);

    transaction1.commit();

    RefMultiContained container = factory.createRefMultiContained();
    resource2.getContents().add(container);

    transaction2.commit();

    EList<EObject> elements = transaction2.getResource(resource1path).getContents();

    for (EObject o : elements)
    {
      MultiContainedElement element_work = (MultiContainedElement)o;
      container.getElements().add(element_work);
      transaction2.commit();
    }
  }
}
