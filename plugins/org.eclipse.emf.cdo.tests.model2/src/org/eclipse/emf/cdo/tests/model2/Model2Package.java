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
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.cdo.tests.model1.Model1Package;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model2.Model2Factory
 * @model kind="package"
 * @generated
 */
public interface Model2Package extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "model2";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model2/1.0.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "model2";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Model2Package eINSTANCE = org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl
   * <em>Special Purchase Order</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getSpecialPurchaseOrder()
   * @generated
   */
  int SPECIAL_PURCHASE_ORDER = 0;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__ORDER_DETAILS = Model1Package.PURCHASE_ORDER__ORDER_DETAILS;

  /**
   * The feature id for the '<em><b>Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__DATE = Model1Package.PURCHASE_ORDER__DATE;

  /**
   * The feature id for the '<em><b>Supplier</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__SUPPLIER = Model1Package.PURCHASE_ORDER__SUPPLIER;

  /**
   * The feature id for the '<em><b>Discount Code</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE = Model1Package.PURCHASE_ORDER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Shipping Address</b></em>' containment reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS = Model1Package.PURCHASE_ORDER_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Special Purchase Order</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER_FEATURE_COUNT = Model1Package.PURCHASE_ORDER_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl <em>Task Container</em>}
   * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTaskContainer()
   * @generated
   */
  int TASK_CONTAINER = 1;

  /**
   * The feature id for the '<em><b>Tasks</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TASK_CONTAINER__TASKS = 0;

  /**
   * The number of structural features of the '<em>Task Container</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int TASK_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl <em>Task</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.TaskImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTask()
   * @generated
   */
  int TASK = 2;

  /**
   * The feature id for the '<em><b>Task Container</b></em>' container reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TASK__TASK_CONTAINER = 0;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TASK__DESCRIPTION = 1;

  /**
   * The feature id for the '<em><b>Done</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TASK__DONE = 2;

  /**
   * The number of structural features of the '<em>Task</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TASK_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.Unsettable1Impl <em>Unsettable1</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.Unsettable1Impl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getUnsettable1()
   * @generated
   */
  int UNSETTABLE1 = 3;

  /**
   * The feature id for the '<em><b>Unsettable Boolean</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_BOOLEAN = 0;

  /**
   * The feature id for the '<em><b>Unsettable Byte</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_BYTE = 1;

  /**
   * The feature id for the '<em><b>Unsettable Char</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_CHAR = 2;

  /**
   * The feature id for the '<em><b>Unsettable Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_DATE = 3;

  /**
   * The feature id for the '<em><b>Unsettable Double</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_DOUBLE = 4;

  /**
   * The feature id for the '<em><b>Unsettable Float</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_FLOAT = 5;

  /**
   * The feature id for the '<em><b>Unsettable Int</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_INT = 6;

  /**
   * The feature id for the '<em><b>Unsettable Long</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_LONG = 7;

  /**
   * The feature id for the '<em><b>Unsettable Short</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_SHORT = 8;

  /**
   * The feature id for the '<em><b>Unsettable String</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_STRING = 9;

  /**
   * The feature id for the '<em><b>Unsettable VAT</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1__UNSETTABLE_VAT = 10;

  /**
   * The number of structural features of the '<em>Unsettable1</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE1_FEATURE_COUNT = 11;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.Unsettable2WithDefaultImpl
   * <em>Unsettable2 With Default</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.Unsettable2WithDefaultImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getUnsettable2WithDefault()
   * @generated
   */
  int UNSETTABLE2_WITH_DEFAULT = 4;

  /**
   * The feature id for the '<em><b>Unsettable Boolean</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_BOOLEAN = 0;

  /**
   * The feature id for the '<em><b>Unsettable Byte</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_BYTE = 1;

  /**
   * The feature id for the '<em><b>Unsettable Char</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_CHAR = 2;

  /**
   * The feature id for the '<em><b>Unsettable Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_DATE = 3;

  /**
   * The feature id for the '<em><b>Unsettable Double</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_DOUBLE = 4;

  /**
   * The feature id for the '<em><b>Unsettable Float</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_FLOAT = 5;

  /**
   * The feature id for the '<em><b>Unsettable Int</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_INT = 6;

  /**
   * The feature id for the '<em><b>Unsettable Long</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_LONG = 7;

  /**
   * The feature id for the '<em><b>Unsettable Short</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_SHORT = 8;

  /**
   * The feature id for the '<em><b>Unsettable String</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_STRING = 9;

  /**
   * The feature id for the '<em><b>Unsettable VAT</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_VAT = 10;

  /**
   * The number of structural features of the '<em>Unsettable2 With Default</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int UNSETTABLE2_WITH_DEFAULT_FEATURE_COUNT = 11;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.PersistentContainmentImpl
   * <em>Persistent Containment</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.PersistentContainmentImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getPersistentContainment()
   * @generated
   */
  int PERSISTENT_CONTAINMENT = 5;

  /**
   * The feature id for the '<em><b>Attr Before</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PERSISTENT_CONTAINMENT__ATTR_BEFORE = 0;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PERSISTENT_CONTAINMENT__CHILDREN = 1;

  /**
   * The feature id for the '<em><b>Attr After</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PERSISTENT_CONTAINMENT__ATTR_AFTER = 2;

  /**
   * The number of structural features of the '<em>Persistent Containment</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PERSISTENT_CONTAINMENT_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl
   * <em>Transient Container</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTransientContainer()
   * @generated
   */
  int TRANSIENT_CONTAINER = 6;

  /**
   * The feature id for the '<em><b>Attr Before</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TRANSIENT_CONTAINER__ATTR_BEFORE = 0;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TRANSIENT_CONTAINER__PARENT = 1;

  /**
   * The feature id for the '<em><b>Attr After</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TRANSIENT_CONTAINER__ATTR_AFTER = 2;

  /**
   * The number of structural features of the '<em>Transient Container</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TRANSIENT_CONTAINER_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.NotUnsettableImpl <em>Not Unsettable</em>}
   * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.NotUnsettableImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getNotUnsettable()
   * @generated
   */
  int NOT_UNSETTABLE = 7;

  /**
   * The feature id for the '<em><b>Not Unsettable Boolean</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_BOOLEAN = 0;

  /**
   * The feature id for the '<em><b>Not Unsettable Byte</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_BYTE = 1;

  /**
   * The feature id for the '<em><b>Not Unsettable Char</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_CHAR = 2;

  /**
   * The feature id for the '<em><b>Not Unsettable Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_DATE = 3;

  /**
   * The feature id for the '<em><b>Not Unsettable Double</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_DOUBLE = 4;

  /**
   * The feature id for the '<em><b>Not Unsettable Float</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_FLOAT = 5;

  /**
   * The feature id for the '<em><b>Not Unsettable Int</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_INT = 6;

  /**
   * The feature id for the '<em><b>Not Unsettable Long</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_LONG = 7;

  /**
   * The feature id for the '<em><b>Not Unsettable Short</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_SHORT = 8;

  /**
   * The feature id for the '<em><b>Not Unsettable String</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_STRING = 9;

  /**
   * The feature id for the '<em><b>Not Unsettable VAT</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE__NOT_UNSETTABLE_VAT = 10;

  /**
   * The number of structural features of the '<em>Not Unsettable</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_FEATURE_COUNT = 11;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.NotUnsettableWithDefaultImpl
   * <em>Not Unsettable With Default</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.NotUnsettableWithDefaultImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getNotUnsettableWithDefault()
   * @generated
   */
  int NOT_UNSETTABLE_WITH_DEFAULT = 8;

  /**
   * The feature id for the '<em><b>Not Unsettable Boolean</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BOOLEAN = 0;

  /**
   * The feature id for the '<em><b>Not Unsettable Byte</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BYTE = 1;

  /**
   * The feature id for the '<em><b>Not Unsettable Char</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_CHAR = 2;

  /**
   * The feature id for the '<em><b>Not Unsettable Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DATE = 3;

  /**
   * The feature id for the '<em><b>Not Unsettable Double</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DOUBLE = 4;

  /**
   * The feature id for the '<em><b>Not Unsettable Float</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_FLOAT = 5;

  /**
   * The feature id for the '<em><b>Not Unsettable Int</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_INT = 6;

  /**
   * The feature id for the '<em><b>Not Unsettable Long</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_LONG = 7;

  /**
   * The feature id for the '<em><b>Not Unsettable Short</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_SHORT = 8;

  /**
   * The feature id for the '<em><b>Not Unsettable String</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_STRING = 9;

  /**
   * The feature id for the '<em><b>Not Unsettable VAT</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_VAT = 10;

  /**
   * The number of structural features of the '<em>Not Unsettable With Default</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NOT_UNSETTABLE_WITH_DEFAULT_FEATURE_COUNT = 11;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl <em>Map Holder</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getMapHolder()
   * @generated
   */
  int MAP_HOLDER = 9;

  /**
   * The feature id for the '<em><b>Integer To String Map</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__INTEGER_TO_STRING_MAP = 0;

  /**
   * The feature id for the '<em><b>String To String Map</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__STRING_TO_STRING_MAP = 1;

  /**
   * The feature id for the '<em><b>String To VAT Map</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__STRING_TO_VAT_MAP = 2;

  /**
   * The feature id for the '<em><b>String To Address Containment Map</b></em>' map. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP = 3;

  /**
   * The feature id for the '<em><b>String To Address Reference Map</b></em>' map. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP = 4;

  /**
   * The feature id for the '<em><b>EObject To EObject Map</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP = 5;

  /**
   * The feature id for the '<em><b>EObject To EObject Key Contained Map</b></em>' map. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP = 6;

  /**
   * The feature id for the '<em><b>EObject To EObject Both Contained Map</b></em>' map. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP = 7;

  /**
   * The feature id for the '<em><b>EObject To EObject Value Contained Map</b></em>' map. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP = 8;

  /**
   * The number of structural features of the '<em>Map Holder</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MAP_HOLDER_FEATURE_COUNT = 9;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.StringToStringMapImpl
   * <em>String To String Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.StringToStringMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getStringToStringMap()
   * @generated
   */
  int STRING_TO_STRING_MAP = 10;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_STRING_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_STRING_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>String To String Map</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_STRING_MAP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.IntegerToStringMapImpl
   * <em>Integer To String Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.IntegerToStringMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getIntegerToStringMap()
   * @generated
   */
  int INTEGER_TO_STRING_MAP = 11;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INTEGER_TO_STRING_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INTEGER_TO_STRING_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>Integer To String Map</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INTEGER_TO_STRING_MAP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.StringToVATMapImpl
   * <em>String To VAT Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.StringToVATMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getStringToVATMap()
   * @generated
   */
  int STRING_TO_VAT_MAP = 12;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_VAT_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_VAT_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>String To VAT Map</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_VAT_MAP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.StringToAddressContainmentMapImpl
   * <em>String To Address Containment Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.StringToAddressContainmentMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getStringToAddressContainmentMap()
   * @generated
   */
  int STRING_TO_ADDRESS_CONTAINMENT_MAP = 13;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_ADDRESS_CONTAINMENT_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_ADDRESS_CONTAINMENT_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>String To Address Containment Map</em>' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_ADDRESS_CONTAINMENT_MAP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.StringToAddressReferenceMapImpl
   * <em>String To Address Reference Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.StringToAddressReferenceMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getStringToAddressReferenceMap()
   * @generated
   */
  int STRING_TO_ADDRESS_REFERENCE_MAP = 14;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_ADDRESS_REFERENCE_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_ADDRESS_REFERENCE_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>String To Address Reference Map</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STRING_TO_ADDRESS_REFERENCE_MAP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectMapImpl
   * <em>EObject To EObject Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getEObjectToEObjectMap()
   * @generated
   */
  int EOBJECT_TO_EOBJECT_MAP = 15;

  /**
   * The feature id for the '<em><b>Key</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>EObject To EObject Map</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_MAP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectKeyContainedMapImpl
   * <em>EObject To EObject Key Contained Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectKeyContainedMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getEObjectToEObjectKeyContainedMap()
   * @generated
   */
  int EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP = 16;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>EObject To EObject Key Contained Map</em>' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectBothContainedMapImpl
   * <em>EObject To EObject Both Contained Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectBothContainedMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getEObjectToEObjectBothContainedMap()
   * @generated
   */
  int EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP = 17;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>EObject To EObject Both Contained Map</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectValueContainedMapImpl
   * <em>EObject To EObject Value Contained Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectValueContainedMapImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getEObjectToEObjectValueContainedMap()
   * @generated
   */
  int EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP = 18;

  /**
   * The feature id for the '<em><b>Key</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE = 1;

  /**
   * The number of structural features of the '<em>EObject To EObject Value Contained Map</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP_FEATURE_COUNT = 2;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder
   * <em>Special Purchase Order</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Special Purchase Order</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder
   * @generated
   */
  EClass getSpecialPurchaseOrder();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getDiscountCode <em>Discount Code</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Discount Code</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getDiscountCode()
   * @see #getSpecialPurchaseOrder()
   * @generated
   */
  EAttribute getSpecialPurchaseOrder_DiscountCode();

  /**
   * Returns the meta object for the containment reference '
   * {@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getShippingAddress <em>Shipping Address</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Shipping Address</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getShippingAddress()
   * @see #getSpecialPurchaseOrder()
   * @generated
   */
  EReference getSpecialPurchaseOrder_ShippingAddress();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.TaskContainer <em>Task Container</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Task Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.TaskContainer
   * @generated
   */
  EClass getTaskContainer();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.tests.model2.TaskContainer#getTasks <em>Tasks</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Tasks</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.TaskContainer#getTasks()
   * @see #getTaskContainer()
   * @generated
   */
  EReference getTaskContainer_Tasks();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.Task <em>Task</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Task</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Task
   * @generated
   */
  EClass getTask();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model2.Task#getTaskContainer
   * <em>Task Container</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the container reference '<em>Task Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Task#getTaskContainer()
   * @see #getTask()
   * @generated
   */
  EReference getTask_TaskContainer();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Task#getDescription
   * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Task#getDescription()
   * @see #getTask()
   * @generated
   */
  EAttribute getTask_Description();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Task#isDone <em>Done</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Done</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Task#isDone()
   * @see #getTask()
   * @generated
   */
  EAttribute getTask_Done();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1 <em>Unsettable1</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Unsettable1</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1
   * @generated
   */
  EClass getUnsettable1();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#isUnsettableBoolean
   * <em>Unsettable Boolean</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Boolean</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#isUnsettableBoolean()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableBoolean();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableByte
   * <em>Unsettable Byte</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Byte</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableByte()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableByte();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableChar
   * <em>Unsettable Char</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Char</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableChar()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableChar();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableDate
   * <em>Unsettable Date</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableDate()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableDate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableDouble
   * <em>Unsettable Double</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Double</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableDouble()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableDouble();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableFloat
   * <em>Unsettable Float</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Float</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableFloat()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableFloat();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableInt
   * <em>Unsettable Int</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Int</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableInt()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableInt();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableLong
   * <em>Unsettable Long</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Long</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableLong()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableLong();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableShort
   * <em>Unsettable Short</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Short</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableShort()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableShort();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableString
   * <em>Unsettable String</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable String</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableString()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableString();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableVAT
   * <em>Unsettable VAT</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable VAT</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable1#getUnsettableVAT()
   * @see #getUnsettable1()
   * @generated
   */
  EAttribute getUnsettable1_UnsettableVAT();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault
   * <em>Unsettable2 With Default</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Unsettable2 With Default</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault
   * @generated
   */
  EClass getUnsettable2WithDefault();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#isUnsettableBoolean <em>Unsettable Boolean</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Boolean</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#isUnsettableBoolean()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableBoolean();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableByte <em>Unsettable Byte</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Byte</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableByte()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableByte();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableChar <em>Unsettable Char</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Char</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableChar()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableChar();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDate <em>Unsettable Date</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDate()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableDate();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDouble <em>Unsettable Double</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Double</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDouble()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableDouble();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableFloat <em>Unsettable Float</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Float</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableFloat()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableFloat();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableInt <em>Unsettable Int</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Int</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableInt()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableInt();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableLong <em>Unsettable Long</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Long</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableLong()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableLong();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableShort <em>Unsettable Short</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable Short</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableShort()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableShort();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableString <em>Unsettable String</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable String</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableString()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableString();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableVAT <em>Unsettable VAT</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Unsettable VAT</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableVAT()
   * @see #getUnsettable2WithDefault()
   * @generated
   */
  EAttribute getUnsettable2WithDefault_UnsettableVAT();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.PersistentContainment
   * <em>Persistent Containment</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Persistent Containment</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.PersistentContainment
   * @generated
   */
  EClass getPersistentContainment();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getAttrBefore <em>Attr Before</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Attr Before</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.PersistentContainment#getAttrBefore()
   * @see #getPersistentContainment()
   * @generated
   */
  EAttribute getPersistentContainment_AttrBefore();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getChildren <em>Children</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.PersistentContainment#getChildren()
   * @see #getPersistentContainment()
   * @generated
   */
  EReference getPersistentContainment_Children();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getAttrAfter <em>Attr After</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Attr After</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.PersistentContainment#getAttrAfter()
   * @see #getPersistentContainment()
   * @generated
   */
  EAttribute getPersistentContainment_AttrAfter();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.TransientContainer
   * <em>Transient Container</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Transient Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.TransientContainer
   * @generated
   */
  EClass getTransientContainer();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getAttrBefore <em>Attr Before</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Attr Before</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.TransientContainer#getAttrBefore()
   * @see #getTransientContainer()
   * @generated
   */
  EAttribute getTransientContainer_AttrBefore();

  /**
   * Returns the meta object for the container reference '
   * {@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent <em>Parent</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent()
   * @see #getTransientContainer()
   * @generated
   */
  EReference getTransientContainer_Parent();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getAttrAfter
   * <em>Attr After</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Attr After</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.TransientContainer#getAttrAfter()
   * @see #getTransientContainer()
   * @generated
   */
  EAttribute getTransientContainer_AttrAfter();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.NotUnsettable <em>Not Unsettable</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Not Unsettable</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable
   * @generated
   */
  EClass getNotUnsettable();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#isNotUnsettableBoolean <em>Not Unsettable Boolean</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Boolean</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#isNotUnsettableBoolean()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableBoolean();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableByte <em>Not Unsettable Byte</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Byte</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableByte()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableByte();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableChar <em>Not Unsettable Char</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Char</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableChar()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableChar();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableDate <em>Not Unsettable Date</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableDate()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableDate();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableDouble <em>Not Unsettable Double</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Double</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableDouble()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableDouble();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableFloat <em>Not Unsettable Float</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Float</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableFloat()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableFloat();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableInt <em>Not Unsettable Int</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Int</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableInt()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableInt();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableLong <em>Not Unsettable Long</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Long</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableLong()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableLong();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableShort <em>Not Unsettable Short</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Short</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableShort()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableShort();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableString <em>Not Unsettable String</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable String</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableString()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableString();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableVAT <em>Not Unsettable VAT</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable VAT</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettable#getNotUnsettableVAT()
   * @see #getNotUnsettable()
   * @generated
   */
  EAttribute getNotUnsettable_NotUnsettableVAT();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault
   * <em>Not Unsettable With Default</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Not Unsettable With Default</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault
   * @generated
   */
  EClass getNotUnsettableWithDefault();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#isNotUnsettableBoolean
   * <em>Not Unsettable Boolean</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Boolean</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#isNotUnsettableBoolean()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableBoolean();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableByte <em>Not Unsettable Byte</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Byte</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableByte()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableByte();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableChar <em>Not Unsettable Char</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Char</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableChar()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableChar();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableDate <em>Not Unsettable Date</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableDate()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableDate();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableDouble
   * <em>Not Unsettable Double</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Double</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableDouble()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableDouble();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableFloat
   * <em>Not Unsettable Float</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Float</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableFloat()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableFloat();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableInt <em>Not Unsettable Int</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Int</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableInt()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableInt();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableLong <em>Not Unsettable Long</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Long</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableLong()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableLong();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableShort
   * <em>Not Unsettable Short</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable Short</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableShort()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableShort();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableString
   * <em>Not Unsettable String</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable String</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableString()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableString();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableVAT <em>Not Unsettable VAT</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Not Unsettable VAT</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault#getNotUnsettableVAT()
   * @see #getNotUnsettableWithDefault()
   * @generated
   */
  EAttribute getNotUnsettableWithDefault_NotUnsettableVAT();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.MapHolder <em>Map Holder</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Map Holder</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder
   * @generated
   */
  EClass getMapHolder();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getIntegerToStringMap
   * <em>Integer To String Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>Integer To String Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getIntegerToStringMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_IntegerToStringMap();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToStringMap
   * <em>String To String Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>String To String Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToStringMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_StringToStringMap();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToVATMap
   * <em>String To VAT Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>String To VAT Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToVATMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_StringToVATMap();

  /**
   * Returns the meta object for the map '
   * {@link org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToAddressContainmentMap
   * <em>String To Address Containment Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>String To Address Containment Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToAddressContainmentMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_StringToAddressContainmentMap();

  /**
   * Returns the meta object for the map '
   * {@link org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToAddressReferenceMap
   * <em>String To Address Reference Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>String To Address Reference Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToAddressReferenceMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_StringToAddressReferenceMap();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectMap
   * <em>EObject To EObject Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>EObject To EObject Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_EObjectToEObjectMap();

  /**
   * Returns the meta object for the map '
   * {@link org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectKeyContainedMap
   * <em>EObject To EObject Key Contained Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>EObject To EObject Key Contained Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectKeyContainedMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_EObjectToEObjectKeyContainedMap();

  /**
   * Returns the meta object for the map '
   * {@link org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectBothContainedMap
   * <em>EObject To EObject Both Contained Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>EObject To EObject Both Contained Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectBothContainedMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_EObjectToEObjectBothContainedMap();

  /**
   * Returns the meta object for the map '
   * {@link org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectValueContainedMap
   * <em>EObject To EObject Value Contained Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the map '<em>EObject To EObject Value Contained Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectValueContainedMap()
   * @see #getMapHolder()
   * @generated
   */
  EReference getMapHolder_EObjectToEObjectValueContainedMap();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>String To String Map</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>String To String Map</em>'.
   * @see java.util.Map.Entry
   * @model keyDataType="org.eclipse.emf.ecore.EString" valueDataType="org.eclipse.emf.ecore.EString"
   * @generated
   */
  EClass getStringToStringMap();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getStringToStringMap()
   * @generated
   */
  EAttribute getStringToStringMap_Key();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getStringToStringMap()
   * @generated
   */
  EAttribute getStringToStringMap_Value();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>Integer To String Map</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Integer To String Map</em>'.
   * @see java.util.Map.Entry
   * @model keyDataType="org.eclipse.emf.ecore.EIntegerObject" valueDataType="org.eclipse.emf.ecore.EString"
   * @generated
   */
  EClass getIntegerToStringMap();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getIntegerToStringMap()
   * @generated
   */
  EAttribute getIntegerToStringMap_Key();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getIntegerToStringMap()
   * @generated
   */
  EAttribute getIntegerToStringMap_Value();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>String To VAT Map</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>String To VAT Map</em>'.
   * @see java.util.Map.Entry
   * @model keyDataType="org.eclipse.emf.ecore.EString" valueDataType="org.eclipse.emf.cdo.tests.model1.VAT"
   * @generated
   */
  EClass getStringToVATMap();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getStringToVATMap()
   * @generated
   */
  EAttribute getStringToVATMap_Key();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getStringToVATMap()
   * @generated
   */
  EAttribute getStringToVATMap_Value();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>String To Address Containment Map</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>String To Address Containment Map</em>'.
   * @see java.util.Map.Entry
   * @model keyDataType="org.eclipse.emf.ecore.EString" valueType="org.eclipse.emf.cdo.tests.model1.Address"
   *        valueContainment="true"
   * @generated
   */
  EClass getStringToAddressContainmentMap();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getStringToAddressContainmentMap()
   * @generated
   */
  EAttribute getStringToAddressContainmentMap_Key();

  /**
   * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getStringToAddressContainmentMap()
   * @generated
   */
  EReference getStringToAddressContainmentMap_Value();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>String To Address Reference Map</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>String To Address Reference Map</em>'.
   * @see java.util.Map.Entry
   * @model keyDataType="org.eclipse.emf.ecore.EString" valueType="org.eclipse.emf.cdo.tests.model1.Address"
   * @generated
   */
  EClass getStringToAddressReferenceMap();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getStringToAddressReferenceMap()
   * @generated
   */
  EAttribute getStringToAddressReferenceMap_Key();

  /**
   * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getStringToAddressReferenceMap()
   * @generated
   */
  EReference getStringToAddressReferenceMap_Value();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>EObject To EObject Map</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>EObject To EObject Map</em>'.
   * @see java.util.Map.Entry
   * @model keyType="org.eclipse.emf.ecore.EObject" valueType="org.eclipse.emf.ecore.EObject"
   * @generated
   */
  EClass getEObjectToEObjectMap();

  /**
   * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getEObjectToEObjectMap()
   * @generated
   */
  EReference getEObjectToEObjectMap_Key();

  /**
   * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getEObjectToEObjectMap()
   * @generated
   */
  EReference getEObjectToEObjectMap_Value();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>EObject To EObject Key Contained Map</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>EObject To EObject Key Contained Map</em>'.
   * @see java.util.Map.Entry
   * @model keyType="org.eclipse.emf.ecore.EObject" keyContainment="true" valueType="org.eclipse.emf.ecore.EObject"
   * @generated
   */
  EClass getEObjectToEObjectKeyContainedMap();

  /**
   * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Key</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getEObjectToEObjectKeyContainedMap()
   * @generated
   */
  EReference getEObjectToEObjectKeyContainedMap_Key();

  /**
   * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getEObjectToEObjectKeyContainedMap()
   * @generated
   */
  EReference getEObjectToEObjectKeyContainedMap_Value();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>EObject To EObject Both Contained Map</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>EObject To EObject Both Contained Map</em>'.
   * @see java.util.Map.Entry
   * @model keyType="org.eclipse.emf.ecore.EObject" keyContainment="true" valueType="org.eclipse.emf.ecore.EObject"
   *        valueContainment="true"
   * @generated
   */
  EClass getEObjectToEObjectBothContainedMap();

  /**
   * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Key</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getEObjectToEObjectBothContainedMap()
   * @generated
   */
  EReference getEObjectToEObjectBothContainedMap_Key();

  /**
   * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getEObjectToEObjectBothContainedMap()
   * @generated
   */
  EReference getEObjectToEObjectBothContainedMap_Value();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>EObject To EObject Value Contained Map</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>EObject To EObject Value Contained Map</em>'.
   * @see java.util.Map.Entry
   * @model keyType="org.eclipse.emf.ecore.EObject" valueType="org.eclipse.emf.ecore.EObject" valueContainment="true"
   * @generated
   */
  EClass getEObjectToEObjectValueContainedMap();

  /**
   * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getEObjectToEObjectValueContainedMap()
   * @generated
   */
  EReference getEObjectToEObjectValueContainedMap_Key();

  /**
   * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getEObjectToEObjectValueContainedMap()
   * @generated
   */
  EReference getEObjectToEObjectValueContainedMap_Value();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model2Factory getModel2Factory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * 
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl
     * <em>Special Purchase Order</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getSpecialPurchaseOrder()
     * @generated
     */
    EClass SPECIAL_PURCHASE_ORDER = eINSTANCE.getSpecialPurchaseOrder();

    /**
     * The meta object literal for the '<em><b>Discount Code</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE = eINSTANCE.getSpecialPurchaseOrder_DiscountCode();

    /**
     * The meta object literal for the '<em><b>Shipping Address</b></em>' containment reference feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS = eINSTANCE.getSpecialPurchaseOrder_ShippingAddress();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl
     * <em>Task Container</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTaskContainer()
     * @generated
     */
    EClass TASK_CONTAINER = eINSTANCE.getTaskContainer();

    /**
     * The meta object literal for the '<em><b>Tasks</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference TASK_CONTAINER__TASKS = eINSTANCE.getTaskContainer_Tasks();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl <em>Task</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.TaskImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTask()
     * @generated
     */
    EClass TASK = eINSTANCE.getTask();

    /**
     * The meta object literal for the '<em><b>Task Container</b></em>' container reference feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference TASK__TASK_CONTAINER = eINSTANCE.getTask_TaskContainer();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute TASK__DESCRIPTION = eINSTANCE.getTask_Description();

    /**
     * The meta object literal for the '<em><b>Done</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute TASK__DONE = eINSTANCE.getTask_Done();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.Unsettable1Impl
     * <em>Unsettable1</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.Unsettable1Impl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getUnsettable1()
     * @generated
     */
    EClass UNSETTABLE1 = eINSTANCE.getUnsettable1();

    /**
     * The meta object literal for the '<em><b>Unsettable Boolean</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_BOOLEAN = eINSTANCE.getUnsettable1_UnsettableBoolean();

    /**
     * The meta object literal for the '<em><b>Unsettable Byte</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_BYTE = eINSTANCE.getUnsettable1_UnsettableByte();

    /**
     * The meta object literal for the '<em><b>Unsettable Char</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_CHAR = eINSTANCE.getUnsettable1_UnsettableChar();

    /**
     * The meta object literal for the '<em><b>Unsettable Date</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_DATE = eINSTANCE.getUnsettable1_UnsettableDate();

    /**
     * The meta object literal for the '<em><b>Unsettable Double</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_DOUBLE = eINSTANCE.getUnsettable1_UnsettableDouble();

    /**
     * The meta object literal for the '<em><b>Unsettable Float</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_FLOAT = eINSTANCE.getUnsettable1_UnsettableFloat();

    /**
     * The meta object literal for the '<em><b>Unsettable Int</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_INT = eINSTANCE.getUnsettable1_UnsettableInt();

    /**
     * The meta object literal for the '<em><b>Unsettable Long</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_LONG = eINSTANCE.getUnsettable1_UnsettableLong();

    /**
     * The meta object literal for the '<em><b>Unsettable Short</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_SHORT = eINSTANCE.getUnsettable1_UnsettableShort();

    /**
     * The meta object literal for the '<em><b>Unsettable String</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_STRING = eINSTANCE.getUnsettable1_UnsettableString();

    /**
     * The meta object literal for the '<em><b>Unsettable VAT</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE1__UNSETTABLE_VAT = eINSTANCE.getUnsettable1_UnsettableVAT();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.Unsettable2WithDefaultImpl
     * <em>Unsettable2 With Default</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.Unsettable2WithDefaultImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getUnsettable2WithDefault()
     * @generated
     */
    EClass UNSETTABLE2_WITH_DEFAULT = eINSTANCE.getUnsettable2WithDefault();

    /**
     * The meta object literal for the '<em><b>Unsettable Boolean</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_BOOLEAN = eINSTANCE.getUnsettable2WithDefault_UnsettableBoolean();

    /**
     * The meta object literal for the '<em><b>Unsettable Byte</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_BYTE = eINSTANCE.getUnsettable2WithDefault_UnsettableByte();

    /**
     * The meta object literal for the '<em><b>Unsettable Char</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_CHAR = eINSTANCE.getUnsettable2WithDefault_UnsettableChar();

    /**
     * The meta object literal for the '<em><b>Unsettable Date</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_DATE = eINSTANCE.getUnsettable2WithDefault_UnsettableDate();

    /**
     * The meta object literal for the '<em><b>Unsettable Double</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_DOUBLE = eINSTANCE.getUnsettable2WithDefault_UnsettableDouble();

    /**
     * The meta object literal for the '<em><b>Unsettable Float</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_FLOAT = eINSTANCE.getUnsettable2WithDefault_UnsettableFloat();

    /**
     * The meta object literal for the '<em><b>Unsettable Int</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_INT = eINSTANCE.getUnsettable2WithDefault_UnsettableInt();

    /**
     * The meta object literal for the '<em><b>Unsettable Long</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_LONG = eINSTANCE.getUnsettable2WithDefault_UnsettableLong();

    /**
     * The meta object literal for the '<em><b>Unsettable Short</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_SHORT = eINSTANCE.getUnsettable2WithDefault_UnsettableShort();

    /**
     * The meta object literal for the '<em><b>Unsettable String</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_STRING = eINSTANCE.getUnsettable2WithDefault_UnsettableString();

    /**
     * The meta object literal for the '<em><b>Unsettable VAT</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_VAT = eINSTANCE.getUnsettable2WithDefault_UnsettableVAT();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.PersistentContainmentImpl
     * <em>Persistent Containment</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.PersistentContainmentImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getPersistentContainment()
     * @generated
     */
    EClass PERSISTENT_CONTAINMENT = eINSTANCE.getPersistentContainment();

    /**
     * The meta object literal for the '<em><b>Attr Before</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute PERSISTENT_CONTAINMENT__ATTR_BEFORE = eINSTANCE.getPersistentContainment_AttrBefore();

    /**
     * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference PERSISTENT_CONTAINMENT__CHILDREN = eINSTANCE.getPersistentContainment_Children();

    /**
     * The meta object literal for the '<em><b>Attr After</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute PERSISTENT_CONTAINMENT__ATTR_AFTER = eINSTANCE.getPersistentContainment_AttrAfter();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl
     * <em>Transient Container</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTransientContainer()
     * @generated
     */
    EClass TRANSIENT_CONTAINER = eINSTANCE.getTransientContainer();

    /**
     * The meta object literal for the '<em><b>Attr Before</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute TRANSIENT_CONTAINER__ATTR_BEFORE = eINSTANCE.getTransientContainer_AttrBefore();

    /**
     * The meta object literal for the '<em><b>Parent</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference TRANSIENT_CONTAINER__PARENT = eINSTANCE.getTransientContainer_Parent();

    /**
     * The meta object literal for the '<em><b>Attr After</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute TRANSIENT_CONTAINER__ATTR_AFTER = eINSTANCE.getTransientContainer_AttrAfter();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.NotUnsettableImpl
     * <em>Not Unsettable</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.NotUnsettableImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getNotUnsettable()
     * @generated
     */
    EClass NOT_UNSETTABLE = eINSTANCE.getNotUnsettable();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Boolean</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_BOOLEAN = eINSTANCE.getNotUnsettable_NotUnsettableBoolean();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Byte</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_BYTE = eINSTANCE.getNotUnsettable_NotUnsettableByte();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Char</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_CHAR = eINSTANCE.getNotUnsettable_NotUnsettableChar();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Date</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_DATE = eINSTANCE.getNotUnsettable_NotUnsettableDate();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Double</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_DOUBLE = eINSTANCE.getNotUnsettable_NotUnsettableDouble();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Float</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_FLOAT = eINSTANCE.getNotUnsettable_NotUnsettableFloat();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Int</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_INT = eINSTANCE.getNotUnsettable_NotUnsettableInt();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Long</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_LONG = eINSTANCE.getNotUnsettable_NotUnsettableLong();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Short</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_SHORT = eINSTANCE.getNotUnsettable_NotUnsettableShort();

    /**
     * The meta object literal for the '<em><b>Not Unsettable String</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_STRING = eINSTANCE.getNotUnsettable_NotUnsettableString();

    /**
     * The meta object literal for the '<em><b>Not Unsettable VAT</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE__NOT_UNSETTABLE_VAT = eINSTANCE.getNotUnsettable_NotUnsettableVAT();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.NotUnsettableWithDefaultImpl
     * <em>Not Unsettable With Default</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.NotUnsettableWithDefaultImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getNotUnsettableWithDefault()
     * @generated
     */
    EClass NOT_UNSETTABLE_WITH_DEFAULT = eINSTANCE.getNotUnsettableWithDefault();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Boolean</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BOOLEAN = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableBoolean();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Byte</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BYTE = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableByte();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Char</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_CHAR = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableChar();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Date</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DATE = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableDate();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Double</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DOUBLE = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableDouble();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Float</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_FLOAT = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableFloat();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Int</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_INT = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableInt();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Long</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_LONG = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableLong();

    /**
     * The meta object literal for the '<em><b>Not Unsettable Short</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_SHORT = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableShort();

    /**
     * The meta object literal for the '<em><b>Not Unsettable String</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_STRING = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableString();

    /**
     * The meta object literal for the '<em><b>Not Unsettable VAT</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_VAT = eINSTANCE
        .getNotUnsettableWithDefault_NotUnsettableVAT();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl <em>Map Holder</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getMapHolder()
     * @generated
     */
    EClass MAP_HOLDER = eINSTANCE.getMapHolder();

    /**
     * The meta object literal for the '<em><b>Integer To String Map</b></em>' map feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__INTEGER_TO_STRING_MAP = eINSTANCE.getMapHolder_IntegerToStringMap();

    /**
     * The meta object literal for the '<em><b>String To String Map</b></em>' map feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__STRING_TO_STRING_MAP = eINSTANCE.getMapHolder_StringToStringMap();

    /**
     * The meta object literal for the '<em><b>String To VAT Map</b></em>' map feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__STRING_TO_VAT_MAP = eINSTANCE.getMapHolder_StringToVATMap();

    /**
     * The meta object literal for the '<em><b>String To Address Containment Map</b></em>' map feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP = eINSTANCE.getMapHolder_StringToAddressContainmentMap();

    /**
     * The meta object literal for the '<em><b>String To Address Reference Map</b></em>' map feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP = eINSTANCE.getMapHolder_StringToAddressReferenceMap();

    /**
     * The meta object literal for the '<em><b>EObject To EObject Map</b></em>' map feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP = eINSTANCE.getMapHolder_EObjectToEObjectMap();

    /**
     * The meta object literal for the '<em><b>EObject To EObject Key Contained Map</b></em>' map feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP = eINSTANCE
        .getMapHolder_EObjectToEObjectKeyContainedMap();

    /**
     * The meta object literal for the '<em><b>EObject To EObject Both Contained Map</b></em>' map feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP = eINSTANCE
        .getMapHolder_EObjectToEObjectBothContainedMap();

    /**
     * The meta object literal for the '<em><b>EObject To EObject Value Contained Map</b></em>' map feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP = eINSTANCE
        .getMapHolder_EObjectToEObjectValueContainedMap();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.StringToStringMapImpl
     * <em>String To String Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.StringToStringMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getStringToStringMap()
     * @generated
     */
    EClass STRING_TO_STRING_MAP = eINSTANCE.getStringToStringMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute STRING_TO_STRING_MAP__KEY = eINSTANCE.getStringToStringMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute STRING_TO_STRING_MAP__VALUE = eINSTANCE.getStringToStringMap_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.IntegerToStringMapImpl
     * <em>Integer To String Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.IntegerToStringMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getIntegerToStringMap()
     * @generated
     */
    EClass INTEGER_TO_STRING_MAP = eINSTANCE.getIntegerToStringMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute INTEGER_TO_STRING_MAP__KEY = eINSTANCE.getIntegerToStringMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute INTEGER_TO_STRING_MAP__VALUE = eINSTANCE.getIntegerToStringMap_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.StringToVATMapImpl
     * <em>String To VAT Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.StringToVATMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getStringToVATMap()
     * @generated
     */
    EClass STRING_TO_VAT_MAP = eINSTANCE.getStringToVATMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute STRING_TO_VAT_MAP__KEY = eINSTANCE.getStringToVATMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute STRING_TO_VAT_MAP__VALUE = eINSTANCE.getStringToVATMap_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.StringToAddressContainmentMapImpl
     * <em>String To Address Containment Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.StringToAddressContainmentMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getStringToAddressContainmentMap()
     * @generated
     */
    EClass STRING_TO_ADDRESS_CONTAINMENT_MAP = eINSTANCE.getStringToAddressContainmentMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute STRING_TO_ADDRESS_CONTAINMENT_MAP__KEY = eINSTANCE.getStringToAddressContainmentMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference STRING_TO_ADDRESS_CONTAINMENT_MAP__VALUE = eINSTANCE.getStringToAddressContainmentMap_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.StringToAddressReferenceMapImpl
     * <em>String To Address Reference Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.StringToAddressReferenceMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getStringToAddressReferenceMap()
     * @generated
     */
    EClass STRING_TO_ADDRESS_REFERENCE_MAP = eINSTANCE.getStringToAddressReferenceMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute STRING_TO_ADDRESS_REFERENCE_MAP__KEY = eINSTANCE.getStringToAddressReferenceMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference STRING_TO_ADDRESS_REFERENCE_MAP__VALUE = eINSTANCE.getStringToAddressReferenceMap_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectMapImpl
     * <em>EObject To EObject Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getEObjectToEObjectMap()
     * @generated
     */
    EClass EOBJECT_TO_EOBJECT_MAP = eINSTANCE.getEObjectToEObjectMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference EOBJECT_TO_EOBJECT_MAP__KEY = eINSTANCE.getEObjectToEObjectMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference EOBJECT_TO_EOBJECT_MAP__VALUE = eINSTANCE.getEObjectToEObjectMap_Value();

    /**
     * The meta object literal for the '
     * {@link org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectKeyContainedMapImpl
     * <em>EObject To EObject Key Contained Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectKeyContainedMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getEObjectToEObjectKeyContainedMap()
     * @generated
     */
    EClass EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP = eINSTANCE.getEObjectToEObjectKeyContainedMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' containment reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP__KEY = eINSTANCE.getEObjectToEObjectKeyContainedMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP__VALUE = eINSTANCE.getEObjectToEObjectKeyContainedMap_Value();

    /**
     * The meta object literal for the '
     * {@link org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectBothContainedMapImpl
     * <em>EObject To EObject Both Contained Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectBothContainedMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getEObjectToEObjectBothContainedMap()
     * @generated
     */
    EClass EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP = eINSTANCE.getEObjectToEObjectBothContainedMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' containment reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP__KEY = eINSTANCE.getEObjectToEObjectBothContainedMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP__VALUE = eINSTANCE.getEObjectToEObjectBothContainedMap_Value();

    /**
     * The meta object literal for the '
     * {@link org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectValueContainedMapImpl
     * <em>EObject To EObject Value Contained Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model2.impl.EObjectToEObjectValueContainedMapImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getEObjectToEObjectValueContainedMap()
     * @generated
     */
    EClass EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP = eINSTANCE.getEObjectToEObjectValueContainedMap();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY = eINSTANCE.getEObjectToEObjectValueContainedMap_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE = eINSTANCE.getEObjectToEObjectValueContainedMap_Value();

  }

} // Model2Package
