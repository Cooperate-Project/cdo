/**
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Unordered List</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.UnorderedList#getContained <em>Contained</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.UnorderedList#getReferenced <em>Referenced</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getUnorderedList()
 * @model
 * @generated
 */
public interface UnorderedList extends EObject
{
  /**
   * Returns the value of the '<em><b>Contained</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.UnorderedList}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Contained</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Contained</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getUnorderedList_Contained()
   * @model containment="true" ordered="false"
   * @generated
   */
  EList<UnorderedList> getContained();

  /**
   * Returns the value of the '<em><b>Referenced</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.UnorderedList}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Referenced</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Referenced</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getUnorderedList_Referenced()
   * @model ordered="false"
   * @generated
   */
  EList<UnorderedList> getReferenced();

} // UnorderedList
