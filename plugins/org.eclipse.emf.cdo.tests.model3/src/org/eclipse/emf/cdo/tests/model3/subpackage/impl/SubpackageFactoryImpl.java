/**
 * <copyright>
 * </copyright>
 *
 * $Id: SubpackageFactoryImpl.java,v 1.3.2.2 2008-09-17 12:14:38 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3.subpackage.impl;

import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class SubpackageFactoryImpl extends EFactoryImpl implements SubpackageFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static SubpackageFactory init()
  {
    try
    {
      SubpackageFactory theSubpackageFactory = (SubpackageFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/tests/subpackage/1.0.0");
      if (theSubpackageFactory != null)
      {
        return theSubpackageFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new SubpackageFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public SubpackageFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case SubpackagePackage.CLASS2:
      return createClass2();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Class2 createClass2()
  {
    Class2Impl class2 = new Class2Impl();
    return class2;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public SubpackagePackage getSubpackagePackage()
  {
    return (SubpackagePackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static SubpackagePackage getPackage()
  {
    return SubpackagePackage.eINSTANCE;
  }

} // SubpackageFactoryImpl
