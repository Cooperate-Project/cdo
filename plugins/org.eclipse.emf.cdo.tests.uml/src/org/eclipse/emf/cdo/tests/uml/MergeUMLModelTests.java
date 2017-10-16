/*
 * Copyright (c) 2004-2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.uml;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * @author Stephan Seifermann
 */
@Requires({ ModelConfig.CAPABILITY_LEGACY, IRepositoryConfig.CAPABILITY_BRANCHING })
public class MergeUMLModelTests extends AbstractCDOTest
{

  private CDOTransaction masterTransaction;

  private CDOTransaction branchTransaction;

  private String resourcePath;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    CDOSession session = openSession();

    masterTransaction = session.openTransaction();
    resourcePath = getResourcePath("model.uml");
    CDOResource umlResource = masterTransaction.createResource(resourcePath);
    Model umlModel = createUMLExampleModel();
    umlResource.getContents().add(umlModel);
    CDOCommitInfo masterBase = masterTransaction.commit();
    umlResource.unload();

    CDOBranch branch = CDOUtil.createBranch(masterBase, "branch");
    branchTransaction = session.openTransaction(branch);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    CDOSession session = masterTransaction.getSession();
    IOUtil.closeSilent(masterTransaction);
    IOUtil.closeSilent(branchTransaction);
    IOUtil.closeSilent(session);
    super.doTearDown();
  }

  private static Model createUMLExampleModel()
  {
    Model umlModel = UMLFactory.eINSTANCE.createModel();
    Class carClass = (Class)umlModel.createPackagedElement("car", UMLPackage.Literals.CLASS);
    Class wheelClass = (Class)umlModel.createPackagedElement("wheel", UMLPackage.Literals.CLASS);
    Association hasAssociation = carClass.createAssociation(false, AggregationKind.COMPOSITE_LITERAL, null, 4, -1, wheelClass, true,
        AggregationKind.NONE_LITERAL, null, 1, 1);
    hasAssociation.setName("has");
    Class driverClass = (Class)umlModel.createPackagedElement("driver", UMLPackage.Literals.CLASS);
    driverClass.createOwnedAttribute("ownedCar", carClass);
    return umlModel;
  }

  public void testMergeClassRename() throws CommitException
  {
    Model umlModel = getUMLModel(branchTransaction);
    Class hasAssociation = (Class)umlModel.getPackagedElement("driver");
    hasAssociation.setName("goodDriver");
    CDOCommitInfo branchCommit = branchTransaction.commit();

    CDOChangeSetData mergeChangeData = masterTransaction.merge(branchCommit.getBranch(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(1, mergeChangeData.getChangedObjects().size());
    assertEquals(0, mergeChangeData.getDetachedObjects().size());
    assertEquals(0, mergeChangeData.getNewObjects().size());

    CDOCommitInfo mergeCommitInfo = masterTransaction.commit();
    assertEquals(1, mergeCommitInfo.getChangedObjects().size());
    assertEquals(0, mergeCommitInfo.getDetachedObjects().size());
    assertEquals(0, mergeCommitInfo.getNewObjects().size());
  }

  public void testMergeAssociationRename() throws CommitException
  {
    Model umlModel = getUMLModel(branchTransaction);
    Association hasAssociation = (Association)umlModel.getPackagedElement("has");
    hasAssociation.setName("consistsOf");
    CDOCommitInfo branchCommit = branchTransaction.commit();

    CDOChangeSetData mergeChangeData = masterTransaction.merge(branchCommit.getBranch(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(1, mergeChangeData.getChangedObjects().size());
    assertEquals(0, mergeChangeData.getDetachedObjects().size());
    assertEquals(0, mergeChangeData.getNewObjects().size());

    CDOCommitInfo mergeCommitInfo = masterTransaction.commit();
    assertEquals(1, mergeCommitInfo.getChangedObjects().size());
    assertEquals(0, mergeCommitInfo.getDetachedObjects().size());
    assertEquals(0, mergeCommitInfo.getNewObjects().size());
  }

  private Model getUMLModel(CDOView view)
  {
    CDOResource umlResource = view.getResource(resourcePath);
    return (Model)umlResource.getContents().get(0);
  }

}
