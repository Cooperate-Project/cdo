/**
 * <copyright>
 * </copyright>
 *
 * $Id: EtypesFactory.java,v 1.1.2.1 2010-09-27 16:17:21 estepper Exp $
 */
package org.eclipse.emf.cdo.etypes;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model.
 * 
 * @since 4.0 <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.etypes.EtypesPackage
 * @generated
 */
public interface EtypesFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  EtypesFactory eINSTANCE = org.eclipse.emf.cdo.etypes.impl.EtypesFactoryImpl.init();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  EtypesPackage getEtypesPackage();

} // EtypesFactory
