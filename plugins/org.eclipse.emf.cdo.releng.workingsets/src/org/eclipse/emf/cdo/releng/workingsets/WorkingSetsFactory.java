/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.workingsets;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage
 * @generated
 */
public interface WorkingSetsFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  WorkingSetsFactory eINSTANCE = org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Working Set</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Working Set</em>'.
   * @generated
   */
  WorkingSet createWorkingSet();

  /**
   * Returns a new object of class '<em>Working Set Group</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Working Set Group</em>'.
   * @generated
   */
  WorkingSetGroup createWorkingSetGroup();

  /**
   * Returns a new object of class '<em>Name Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Name Predicate</em>'.
   * @generated
   */
  NamePredicate createNamePredicate();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  WorkingSetsPackage getWorkingSetsPackage();

} // WorkingSetsFactory
