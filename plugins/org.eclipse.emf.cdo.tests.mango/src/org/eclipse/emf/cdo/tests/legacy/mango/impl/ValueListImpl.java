/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValueListImpl.java,v 1.1.2.1 2008-09-17 13:23:30 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.mango.impl;

import org.eclipse.emf.cdo.tests.legacy.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.mango.Value;
import org.eclipse.emf.cdo.tests.mango.ValueList;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Value List</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.mango.impl.ValueListImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.mango.impl.ValueListImpl#getValues <em>Values</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ValueListImpl extends EObjectImpl implements ValueList
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getValues() <em>Values</em>}' reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getValues()
   * @generated
   * @ordered
   */
  protected EList<Value> values;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ValueListImpl()
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
    return MangoPackage.Literals.VALUE_LIST;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    eFireRead(MangoPackage.VALUE_LIST__NAME);
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eFireWrite(MangoPackage.VALUE_LIST__NAME);
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MangoPackage.VALUE_LIST__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<Value> getValues()
  {
    if (values == null)
    {
      values = new EObjectResolvingEList<Value>(Value.class, this, MangoPackage.VALUE_LIST__VALUES)
          .readWriteFiringList();
    }
    return values;
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
    case MangoPackage.VALUE_LIST__NAME:
      return getName();
    case MangoPackage.VALUE_LIST__VALUES:
      return getValues();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case MangoPackage.VALUE_LIST__NAME:
      setName((String)newValue);
      return;
    case MangoPackage.VALUE_LIST__VALUES:
      getValues().clear();
      getValues().addAll((Collection<? extends Value>)newValue);
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
    case MangoPackage.VALUE_LIST__NAME:
      setName(NAME_EDEFAULT);
      return;
    case MangoPackage.VALUE_LIST__VALUES:
      getValues().clear();
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
    case MangoPackage.VALUE_LIST__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case MangoPackage.VALUE_LIST__VALUES:
      return values != null && !values.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // ValueListImpl
