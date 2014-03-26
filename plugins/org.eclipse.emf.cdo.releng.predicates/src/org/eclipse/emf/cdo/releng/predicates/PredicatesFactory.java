/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.predicates;

import org.eclipse.emf.cdo.releng.internal.predicates.ExternalProject.AbstractXMLDescriptionFactory.Eclipse;
import org.eclipse.emf.cdo.releng.internal.predicates.ExternalProject.AbstractXMLDescriptionFactory.Maven;

import org.eclipse.emf.ecore.EFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;

import java.io.File;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage
 * @generated
 */
public interface PredicatesFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  PredicatesFactory eINSTANCE = org.eclipse.emf.cdo.releng.predicates.impl.PredicatesFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Name Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Name Predicate</em>'.
   * @generated
   */
  NamePredicate createNamePredicate();

  /**
   * Returns a new object of class '<em>Comment Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Comment Predicate</em>'.
   * @generated
   */
  CommentPredicate createCommentPredicate();

  /**
   * Returns a new object of class '<em>Location Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Location Predicate</em>'.
   * @generated
   */
  LocationPredicate createLocationPredicate();

  NamePredicate createNamePredicate(String pattern);

  /**
   * Returns a new object of class '<em>Repository Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Repository Predicate</em>'.
   * @generated
   */
  RepositoryPredicate createRepositoryPredicate();

  RepositoryPredicate createRepositoryPredicate(IProject project);

  /**
   * Returns a new object of class '<em>And Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And Predicate</em>'.
   * @generated
   */
  AndPredicate createAndPredicate();

  AndPredicate createAndPredicate(Predicate... operands);

  /**
   * Returns a new object of class '<em>Or Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or Predicate</em>'.
   * @generated
   */
  OrPredicate createOrPredicate();

  OrPredicate createOrPredicate(Predicate... operands);

  /**
   * Returns a new object of class '<em>Not Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Not Predicate</em>'.
   * @generated
   */
  NotPredicate createNotPredicate();

  NotPredicate createNotPredicate(Predicate operand);

  /**
   * Returns a new object of class '<em>Nature Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nature Predicate</em>'.
   * @generated
   */
  NaturePredicate createNaturePredicate();

  NaturePredicate createNaturePredicate(String nature);

  /**
   * Returns a new object of class '<em>Builder Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Builder Predicate</em>'.
   * @generated
   */
  BuilderPredicate createBuilderPredicate();

  BuilderPredicate createBuilderPredicate(String builder);

  /**
   * Returns a new object of class '<em>File Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>File Predicate</em>'.
   * @generated
   */
  FilePredicate createFilePredicate();

  FilePredicate createFilePredicate(String filePattern);

  FilePredicate createFilePredicate(String filePattern, String contentPattern);

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  PredicatesPackage getPredicatesPackage();

  IProject loadProject(File folder);

  IProject loadProject(File folder, ProjectDescriptionFactory... factories);

  /**
   * @author Eike Stepper
   */
  public interface ProjectDescriptionFactory
  {
    public static final ProjectDescriptionFactory ECLIPSE = new Eclipse();

    public static final ProjectDescriptionFactory MAVEN = new Maven();

    public IProjectDescription createDescription(File folder) throws Exception;
  }

} // PredicatesFactory
