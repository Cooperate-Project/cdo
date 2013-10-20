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
package org.eclipse.emf.cdo.releng.predicates;

import org.eclipse.core.resources.IProject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Repository Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate#getProject <em>Project</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getRepositoryPredicate()
 * @model
 * @generated
 */
public interface RepositoryPredicate extends Predicate
{
  /**
   * Returns the value of the '<em><b>Project</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Project</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Project</em>' attribute.
   * @see #setProject(IProject)
   * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getRepositoryPredicate_Project()
   * @model dataType="org.eclipse.emf.cdo.releng.predicates.Project"
   * @generated
   */
  IProject getProject();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate#getProject <em>Project</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Project</em>' attribute.
   * @see #getProject()
   * @generated
   */
  void setProject(IProject value);

} // RepositoryPredicate
