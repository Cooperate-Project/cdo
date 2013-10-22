/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.preferences.impl;

import org.eclipse.emf.cdo.releng.preferences.PreferenceItem;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.PreferencesFactory;
import org.eclipse.emf.cdo.releng.preferences.PreferencesPackage;
import org.eclipse.emf.cdo.releng.preferences.Property;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PreferencesPackageImpl extends EPackageImpl implements PreferencesPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass preferenceItemEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass preferenceNodeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType escapedStringEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.releng.preferences.PreferencesPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private PreferencesPackageImpl()
  {
    super(eNS_URI, PreferencesFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link PreferencesPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static PreferencesPackage init()
  {
    if (isInited)
    {
      return (PreferencesPackage)EPackage.Registry.INSTANCE.getEPackage(PreferencesPackage.eNS_URI);
    }

    // Obtain or create and register package
    PreferencesPackageImpl thePreferencesPackage = (PreferencesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof PreferencesPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new PreferencesPackageImpl());

    isInited = true;

    // Create package meta-data objects
    thePreferencesPackage.createPackageContents();

    // Initialize created meta-data
    thePreferencesPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    thePreferencesPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(PreferencesPackage.eNS_URI, thePreferencesPackage);
    return thePreferencesPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPreferenceItem()
  {
    return preferenceItemEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferenceItem_Root()
  {
    return (EReference)preferenceItemEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferenceItem_AbsolutePath()
  {
    return (EAttribute)preferenceItemEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferenceItem_ScopeRelativePath()
  {
    return (EAttribute)preferenceItemEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferenceItem_Name()
  {
    return (EAttribute)preferenceItemEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferenceItem_Scope()
  {
    return (EReference)preferenceItemEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceItem__GetParent()
  {
    return preferenceItemEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceItem__GetItem__String()
  {
    return preferenceItemEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceItem__GetInScope__String()
  {
    return preferenceItemEClass.getEOperations().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceItem__GetInScope()
  {
    return preferenceItemEClass.getEOperations().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPreferenceNode()
  {
    return preferenceNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferenceNode_Children()
  {
    return (EReference)preferenceNodeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferenceNode_Parent()
  {
    return (EReference)preferenceNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferenceNode_Properties()
  {
    return (EReference)preferenceNodeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferenceNode_Location()
  {
    return (EAttribute)preferenceNodeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceNode__GetNode__String()
  {
    return preferenceNodeEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceNode__GetProperty__String()
  {
    return preferenceNodeEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceNode__GetInScope__String()
  {
    return preferenceNodeEClass.getEOperations().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceNode__GetInScope()
  {
    return preferenceNodeEClass.getEOperations().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getProperty()
  {
    return propertyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getProperty_Parent()
  {
    return (EReference)propertyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProperty_Value()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getProperty__GetInScope__String()
  {
    return propertyEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getProperty__GetInScope()
  {
    return propertyEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getEscapedString()
  {
    return escapedStringEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferencesFactory getPreferencesFactory()
  {
    return (PreferencesFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    preferenceItemEClass = createEClass(PREFERENCE_ITEM);
    createEReference(preferenceItemEClass, PREFERENCE_ITEM__ROOT);
    createEReference(preferenceItemEClass, PREFERENCE_ITEM__SCOPE);
    createEAttribute(preferenceItemEClass, PREFERENCE_ITEM__ABSOLUTE_PATH);
    createEAttribute(preferenceItemEClass, PREFERENCE_ITEM__SCOPE_RELATIVE_PATH);
    createEAttribute(preferenceItemEClass, PREFERENCE_ITEM__NAME);
    createEOperation(preferenceItemEClass, PREFERENCE_ITEM___GET_PARENT);
    createEOperation(preferenceItemEClass, PREFERENCE_ITEM___GET_ITEM__STRING);
    createEOperation(preferenceItemEClass, PREFERENCE_ITEM___GET_IN_SCOPE__STRING);
    createEOperation(preferenceItemEClass, PREFERENCE_ITEM___GET_IN_SCOPE);

    preferenceNodeEClass = createEClass(PREFERENCE_NODE);
    createEReference(preferenceNodeEClass, PREFERENCE_NODE__PARENT);
    createEReference(preferenceNodeEClass, PREFERENCE_NODE__CHILDREN);
    createEReference(preferenceNodeEClass, PREFERENCE_NODE__PROPERTIES);
    createEAttribute(preferenceNodeEClass, PREFERENCE_NODE__LOCATION);
    createEOperation(preferenceNodeEClass, PREFERENCE_NODE___GET_NODE__STRING);
    createEOperation(preferenceNodeEClass, PREFERENCE_NODE___GET_PROPERTY__STRING);
    createEOperation(preferenceNodeEClass, PREFERENCE_NODE___GET_IN_SCOPE__STRING);
    createEOperation(preferenceNodeEClass, PREFERENCE_NODE___GET_IN_SCOPE);

    propertyEClass = createEClass(PROPERTY);
    createEReference(propertyEClass, PROPERTY__PARENT);
    createEAttribute(propertyEClass, PROPERTY__VALUE);
    createEOperation(propertyEClass, PROPERTY___GET_IN_SCOPE__STRING);
    createEOperation(propertyEClass, PROPERTY___GET_IN_SCOPE);

    // Create data types
    escapedStringEDataType = createEDataType(ESCAPED_STRING);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    preferenceNodeEClass.getESuperTypes().add(getPreferenceItem());
    propertyEClass.getESuperTypes().add(getPreferenceItem());

    // Initialize classes, features, and operations; add parameters
    initEClass(preferenceItemEClass, PreferenceItem.class, "PreferenceItem", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPreferenceItem_Root(), getPreferenceNode(), null, "root", null, 0, 1, PreferenceItem.class,
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        IS_DERIVED, IS_ORDERED);
    initEReference(getPreferenceItem_Scope(), getPreferenceNode(), null, "scope", null, 0, 1, PreferenceItem.class,
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        IS_DERIVED, IS_ORDERED);
    initEAttribute(getPreferenceItem_AbsolutePath(), ecorePackage.getEString(), "absolutePath", null, 1, 1,
        PreferenceItem.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getPreferenceItem_ScopeRelativePath(), ecorePackage.getEString(), "scopeRelativePath", null, 1, 1,
        PreferenceItem.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getPreferenceItem_Name(), ecorePackage.getEString(), "name", null, 1, 1, PreferenceItem.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEOperation(getPreferenceItem__GetParent(), getPreferenceNode(), "getParent", 0, 1, IS_UNIQUE, IS_ORDERED);

    EOperation op = initEOperation(getPreferenceItem__GetItem__String(), getPreferenceItem(), "getItem", 0, 1,
        IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "path", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getPreferenceItem__GetInScope__String(), getPreferenceItem(), "getInScope", 0, 1, IS_UNIQUE,
        IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "scopeName", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getPreferenceItem__GetInScope(), getPreferenceItem(), "getInScope", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(preferenceNodeEClass, PreferenceNode.class, "PreferenceNode", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPreferenceNode_Parent(), getPreferenceNode(), getPreferenceNode_Children(), "parent", null, 0, 1,
        PreferenceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPreferenceNode_Children(), getPreferenceNode(), getPreferenceNode_Parent(), "children", null, 0,
        -1, PreferenceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPreferenceNode_Properties(), getProperty(), getProperty_Parent(), "properties", null, 0, -1,
        PreferenceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    getPreferenceNode_Properties().getEKeys().add(getPreferenceItem_Name());
    initEAttribute(getPreferenceNode_Location(), ecorePackage.getEString(), "location", null, 0, 1,
        PreferenceNode.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED,
        IS_ORDERED);

    op = initEOperation(getPreferenceNode__GetNode__String(), getPreferenceNode(), "getNode", 0, 1, IS_UNIQUE,
        IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getPreferenceNode__GetProperty__String(), getProperty(), "getProperty", 0, 1, IS_UNIQUE,
        IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getPreferenceNode__GetInScope__String(), getPreferenceNode(), "getInScope", 0, 1, IS_UNIQUE,
        IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "scopeName", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getPreferenceNode__GetInScope(), getPreferenceNode(), "getInScope", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(propertyEClass, Property.class, "Property", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getProperty_Parent(), getPreferenceNode(), getPreferenceNode_Properties(), "parent", null, 0, 1,
        Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProperty_Value(), getEscapedString(), "value", null, 0, 1, Property.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    op = initEOperation(getProperty__GetInScope__String(), getProperty(), "getInScope", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "scopeName", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getProperty__GetInScope(), getProperty(), "getInScope", 0, 1, IS_UNIQUE, IS_ORDERED);

    // Initialize data types
    initEDataType(escapedStringEDataType, String.class, "EscapedString", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} // PreferencesPackageImpl
