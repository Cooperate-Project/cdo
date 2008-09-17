/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefMultiNonContainedNPL.java,v 1.2.8.2 2008-09-17 12:15:07 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Multi Non Contained NPL</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContainedNPL()
 * @model
 * @generated
 */
public interface RefMultiNonContainedNPL extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContainedNPL_Elements()
   * @model
   * @generated
   */
  EList<ContainedElementNoOpposite> getElements();

} // RefMultiNonContainedNPL
