/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefMultiNonContained.java,v 1.2.8.1 2008-09-17 08:57:42 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Multi Non Contained</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContained#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContained()
 * @model
 * @generated
 */
public interface RefMultiNonContained extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContained_Elements()
   * @see org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<MultiNonContainedElement> getElements();

} // RefMultiNonContained
