/**
 * <copyright>
 * </copyright>
 *
 * $Id: GenRefSingleContained.java,v 1.2.8.2 2008-09-17 12:15:07 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Gen Ref Single Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.GenRefSingleContained#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getGenRefSingleContained()
 * @model
 * @generated
 */
public interface GenRefSingleContained extends EObject
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Element</em>' containment reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Element</em>' containment reference.
   * @see #setElement(EObject)
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getGenRefSingleContained_Element()
   * @model containment="true"
   * @generated
   */
  EObject getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.GenRefSingleContained#getElement <em>Element</em>}'
   * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Element</em>' containment reference.
   * @see #getElement()
   * @generated
   */
  void setElement(EObject value);

} // GenRefSingleContained
