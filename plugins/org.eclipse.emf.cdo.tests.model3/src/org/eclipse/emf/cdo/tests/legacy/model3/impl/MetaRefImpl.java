/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.legacy.model3.impl;

import org.eclipse.emf.cdo.tests.legacy.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.MetaRef;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Meta Ref</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model3.impl.MetaRefImpl#getEPackageRef <em>EPackage Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MetaRefImpl extends EObjectImpl implements MetaRef
{
  /**
   * The cached value of the '{@link #getEPackageRef() <em>EPackage Ref</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getEPackageRef()
   * @generated
   * @ordered
   */
  protected EPackage ePackageRef;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected MetaRefImpl()
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
    return Model3Package.Literals.META_REF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EPackage getEPackageRef()
  {
    if (ePackageRef != null && ePackageRef.eIsProxy())
    {
      InternalEObject oldEPackageRef = (InternalEObject)ePackageRef;
      ePackageRef = (EPackage)eResolveProxy(oldEPackageRef);
      if (ePackageRef != oldEPackageRef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model3Package.META_REF__EPACKAGE_REF,
              oldEPackageRef, ePackageRef));
      }
    }
    return ePackageRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EPackage basicGetEPackageRef()
  {
    return ePackageRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setEPackageRef(EPackage newEPackageRef)
  {
    EPackage oldEPackageRef = ePackageRef;
    ePackageRef = newEPackageRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.META_REF__EPACKAGE_REF, oldEPackageRef,
          ePackageRef));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model3Package.META_REF__EPACKAGE_REF:
      if (resolve)
        return getEPackageRef();
      return basicGetEPackageRef();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model3Package.META_REF__EPACKAGE_REF:
      setEPackageRef((EPackage)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model3Package.META_REF__EPACKAGE_REF:
      setEPackageRef((EPackage)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model3Package.META_REF__EPACKAGE_REF:
      return ePackageRef != null;
    }
    return super.eIsSet(featureID);
  }

} // MetaRefImpl
