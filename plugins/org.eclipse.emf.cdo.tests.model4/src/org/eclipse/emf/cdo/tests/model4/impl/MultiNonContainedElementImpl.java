/**
 * <copyright>
 * </copyright>
 *
 * $Id: MultiNonContainedElementImpl.java,v 1.2.8.2 2008-09-17 12:15:12 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.model4Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Multi Non Contained Element</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.MultiNonContainedElementImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.MultiNonContainedElementImpl#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class MultiNonContainedElementImpl extends CDOObjectImpl implements MultiNonContainedElement
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected MultiNonContainedElementImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return model4Package.Literals.MULTI_NON_CONTAINED_ELEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    return (String)eGet(model4Package.Literals.MULTI_NON_CONTAINED_ELEMENT__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eSet(model4Package.Literals.MULTI_NON_CONTAINED_ELEMENT__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public RefMultiNonContained getParent()
  {
    return (RefMultiNonContained)eGet(model4Package.Literals.MULTI_NON_CONTAINED_ELEMENT__PARENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setParent(RefMultiNonContained newParent)
  {
    eSet(model4Package.Literals.MULTI_NON_CONTAINED_ELEMENT__PARENT, newParent);
  }

} // MultiNonContainedElementImpl
