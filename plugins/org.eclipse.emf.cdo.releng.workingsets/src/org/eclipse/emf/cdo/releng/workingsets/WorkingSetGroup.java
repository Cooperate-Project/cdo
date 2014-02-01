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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Working Set Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup#getWorkingSets <em>Working Sets</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getWorkingSetGroup()
 * @model
 * @generated
 */
public interface WorkingSetGroup extends EObject
{
  /**
   * Returns the value of the '<em><b>Working Sets</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Working Sets</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Working Sets</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getWorkingSetGroup_WorkingSets()
   * @model containment="true" keys="name"
   * @generated
   */
  EList<WorkingSet> getWorkingSets();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  WorkingSet getWorkingSet(String name);

} // WorkingSetGroup
