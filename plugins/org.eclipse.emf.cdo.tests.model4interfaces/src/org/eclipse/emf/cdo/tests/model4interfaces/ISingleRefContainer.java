/**
 * <copyright>
 * </copyright>
 *
 * $Id: ISingleRefContainer.java,v 1.2.8.2 2008-09-17 12:14:44 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4interfaces;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>ISingle Ref Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getISingleRefContainer()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ISingleRefContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' containment reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement#getParent <em>Parent</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Element</em>' containment reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Element</em>' containment reference.
   * @see #setElement(ISingleRefContainedElement)
   * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getISingleRefContainer_Element()
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  ISingleRefContainedElement getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer#getElement
   * <em>Element</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Element</em>' containment reference.
   * @see #getElement()
   * @generated
   */
  void setElement(ISingleRefContainedElement value);

} // ISingleRefContainer
