/**
 * <copyright>
 * </copyright>
 *
 * $Id: EPackageDefImpl.java,v 1.1 2008-12-28 18:05:25 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.EPackageDef;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import java.net.URI;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Package Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.cdodefs.impl.EPackageDefImpl#getNsURI <em>Ns URI</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class EPackageDefImpl extends DefImpl implements EPackageDef
{
  /**
   * The default value of the '{@link #getNsURI() <em>Ns URI</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getNsURI()
   * @generated
   * @ordered
   */
  protected static final String NS_URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getNsURI() <em>Ns URI</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getNsURI()
   * @generated
   * @ordered
   */
  protected String nsURI = NS_URI_EDEFAULT;

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  protected EPackageDefImpl()
  {
		super();
	}

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  protected EClass eStaticClass()
  {
		return CDODefsPackage.Literals.EPACKAGE_DEF;
	}

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  public String getNsURI()
  {
		return nsURI;
	}

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  public void setNsURI(String newNsURI)
  {
		String oldNsURI = nsURI;
		nsURI = newNsURI;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.EPACKAGE_DEF__NS_URI, oldNsURI, nsURI));
	}

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
		switch (featureID) {
			case CDODefsPackage.EPACKAGE_DEF__NS_URI:
				return getNsURI();
		}
		return super.eGet(featureID, resolve, coreType);
	}

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public void eSet(int featureID, Object newValue)
  {
		switch (featureID) {
			case CDODefsPackage.EPACKAGE_DEF__NS_URI:
				setNsURI((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public void eUnset(int featureID)
  {
		switch (featureID) {
			case CDODefsPackage.EPACKAGE_DEF__NS_URI:
				setNsURI(NS_URI_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public boolean eIsSet(int featureID)
  {
		switch (featureID) {
			case CDODefsPackage.EPACKAGE_DEF__NS_URI:
				return NS_URI_EDEFAULT == null ? nsURI != null : !NS_URI_EDEFAULT.equals(nsURI);
		}
		return super.eIsSet(featureID);
	}

  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public String toString()
  {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (nsURI: ");
		result.append(nsURI);
		result.append(')');
		return result.toString();
	}

  @Override
  protected void validateDefinition()
  {
    CheckUtil.checkState(eIsSet(CDODefsPackage.EPACKAGE_DEF__NS_URI) && URI.create(getNsURI()) != null,
        "nsURI not set or not valid!");
  }

} // CDOPackageDefImpl
