/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance 
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceFolderClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceNodeClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOFeature;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.internal.cdo.CDOSessionPackageManagerImpl;
import org.eclipse.emf.internal.cdo.InternalCDOSession;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Eike Stepper
 */
public final class ModelUtil
{
  private static final ContextTracer MODEL_TRACER = new ContextTracer(OM.DEBUG_MODEL, ModelUtil.class);

  private ModelUtil()
  {
  }

  public static String getParentURI(EPackage ePackage)
  {
    EPackage superPackage = ePackage.getESuperPackage();
    String parentURI = superPackage == null ? null : superPackage.getNsURI();
    return parentURI;
  }

  public static EPackage getTopLevelPackage(EPackage ePackage)
  {
    EPackage superPackage = ePackage.getESuperPackage();
    return superPackage == null ? ePackage : getTopLevelPackage(superPackage);
  }

  public static CDOType getCDOType(EStructuralFeature eFeature)
  {
    if (eFeature instanceof EReference)
    {
      throw new ImplementationError("Should only be called for attributes");
      // return CDOTypeImpl.OBJECT;
    }

    EClassifier classifier = eFeature.getEType();
    if (classifier.getEPackage() == EcorePackage.eINSTANCE)
    {
      int classifierID = classifier.getClassifierID();
      switch (classifierID)
      {
      case EcorePackage.EBOOLEAN:
      case EcorePackage.EBOOLEAN_OBJECT:
      case EcorePackage.EBYTE:
      case EcorePackage.EBYTE_OBJECT:
      case EcorePackage.ECHAR:
      case EcorePackage.ECHARACTER_OBJECT:
      case EcorePackage.EDATE:
      case EcorePackage.EDOUBLE:
      case EcorePackage.EDOUBLE_OBJECT:
      case EcorePackage.EFLOAT:
      case EcorePackage.EFLOAT_OBJECT:
      case EcorePackage.EINT:
      case EcorePackage.EINTEGER_OBJECT:
      case EcorePackage.ELONG:
      case EcorePackage.ELONG_OBJECT:
      case EcorePackage.ESHORT:
      case EcorePackage.ESHORT_OBJECT:
      case EcorePackage.EFEATURE_MAP_ENTRY:
        CDOType type = CDOModelUtil.getType(classifierID);
        if (type == CDOType.OBJECT)
        {
          throw new ImplementationError("Attributes can not be of type OBJECT");
        }

        return type;

      case EcorePackage.ESTRING:
        return CDOType.STRING;
      }
    }

    if (classifier instanceof EDataType)
    {
      return CDOType.CUSTOM;
    }

    throw new IllegalArgumentException("Invalid attribute type: " + classifier);
  }

  public static void initializeCDOPackage(EPackage ePackage, CDOPackage cdoPackage)
  {
    ((InternalCDOPackage)cdoPackage).setClientInfo(ePackage);
    for (EClass eClass : EMFUtil.getPersistentClasses(ePackage))
    {
      CDOClass cdoClass = createCDOClass(eClass, cdoPackage);
      ((InternalCDOPackage)cdoPackage).addClass(cdoClass);
    }
  }

  public static CDOPackage getCDOPackage(EPackage ePackage, CDOSessionPackageManagerImpl packageManager)
  {
    String packageURI = ePackage.getNsURI();
    CDOPackage cdoPackage = packageManager.lookupPackage(packageURI);
    if (cdoPackage == null)
    {
      EPackage topLevelPackage = getTopLevelPackage(ePackage);
      if (topLevelPackage != ePackage)
      {
        getCDOPackage(topLevelPackage, packageManager);
        cdoPackage = packageManager.lookupPackage(packageURI);
      }
      else
      {
        cdoPackage = addCDOPackage(topLevelPackage, packageManager);
      }
    }

    return cdoPackage;
  }

  public static CDOClass getCDOClass(EClass eClass, CDOSessionPackageManagerImpl packageManager)
  {
    CDOPackage cdoPackage = getCDOPackage(eClass.getEPackage(), packageManager);
    return cdoPackage.lookupClass(eClass.getClassifierID());
  }

  public static CDOFeature getCDOFeature(EStructuralFeature eFeature, CDOSessionPackageManagerImpl packageManager)
  {
    CDOClass cdoClass = getCDOClass(eFeature.getEContainingClass(), packageManager);
    return cdoClass.lookupFeature(eFeature.getFeatureID());
  }

  static CDOPackage addCDOPackage(EPackage ePackage, CDOSessionPackageManagerImpl packageManager)
  {
    CDOPackage cdoPackage = createCDOPackage(ePackage, packageManager);
    packageManager.addPackage(cdoPackage);

    for (EPackage subPackage : ePackage.getESubpackages())
    {
      addCDOPackage(subPackage, packageManager);
    }

    return cdoPackage;
  }

  /**
   * @see EMFUtil#getPersistentFeatures(org.eclipse.emf.common.util.EList)
   * @see http://www.eclipse.org/newsportal/article.php?id=26780&group=eclipse.tools.emf#26780
   */
  static CDOPackage createCDOPackage(EPackage ePackage, CDOSessionPackageManagerImpl packageManager)
  {
    InternalCDOSession session = packageManager.getSession();
    String uri = ePackage.getNsURI();
    String parentURI = getParentURI(ePackage);
    String name = ePackage.getName();
    boolean dynamic = EMFUtil.isDynamicEPackage(ePackage);
    String ecore = null;
    CDOIDMetaRange idRange = null;

    if (parentURI == null)
    {
      if (!EcorePackage.eINSTANCE.getNsURI().equals(uri))
      {
        ecore = EMFUtil.ePackageToString(ePackage, session.getPackageRegistry());
      }

      idRange = session.registerEPackage(ePackage);
    }

    CDOPackage cdoPackage = CDOModelUtil.createPackage(packageManager, uri, name, ecore, dynamic, idRange, parentURI);
    initializeCDOPackage(ePackage, cdoPackage);
    return cdoPackage;
  }

  static CDOClass createCDOClass(EClass eClass, CDOPackage containingPackage)
  {
    InternalCDOClass cdoClass = (InternalCDOClass)CDOModelUtil.createClass(containingPackage, eClass.getClassifierID(),
        eClass.getName(), eClass.isAbstract());
    cdoClass.setClientInfo(eClass);

    for (EClass superType : eClass.getESuperTypes())
    {
      CDOClassRef classRef = createClassRef(superType);
      cdoClass.addSuperType(classRef);
    }

    // Bugs: 247978 Make sure featureIndex are properly set for dynamic classes
    eClass.getEAllStructuralFeatures();

    for (EStructuralFeature eFeature : EMFUtil.getPersistentFeatures(eClass.getEStructuralFeatures()))
    {
      CDOFeature cdoFeature = createCDOFeature(eFeature, cdoClass);
      cdoClass.addFeature(cdoFeature);
    }

    return cdoClass;
  }

  static CDOFeature createCDOFeature(EStructuralFeature eFeature, CDOClass containingClass)
  {
    InternalCDOFeature cdoFeature = (InternalCDOFeature)(EMFUtil.isReference(eFeature) ? createCDOReference(
        (EReference)eFeature, containingClass) : createCDOAttribute((EAttribute)eFeature, containingClass));
    cdoFeature.setClientInfo(eFeature);
    return cdoFeature;
  }

  static CDOFeature createCDOReference(EReference eFeature, CDOClass containingClass)
  {
    CDOPackageManager packageManager = containingClass.getPackageManager();
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOClassRef classRef = createClassRef(eFeature.getEType());
    boolean many = eFeature.isMany();
    boolean containment = EMFUtil.isContainment(eFeature);
    CDOFeature cdoFeature = CDOModelUtil.createReference(containingClass, featureID, name, new CDOClassProxy(classRef,
        packageManager), many, containment);

    EReference opposite = eFeature.getEOpposite();
    if (MODEL_TRACER.isEnabled() && opposite != null)
    {
      MODEL_TRACER.format("Opposite info: package={0}, class={1}, feature={2}", opposite.getEContainingClass()
          .getEPackage().getNsURI(), opposite.getEContainingClass().getName(), opposite.getName());
    }

    return cdoFeature;
  }

  static CDOFeature createCDOAttribute(EAttribute eFeature, CDOClass containingClass)
  {
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOType type = getCDOType(eFeature);
    boolean many = EMFUtil.isMany(eFeature);
    Object defaultValue = eFeature.getDefaultValue();
    if (type == CDOType.CUSTOM)
    {
      defaultValue = EcoreUtil.convertToString((EDataType)eFeature.getEType(), defaultValue);
    }

    return CDOModelUtil.createAttribute(containingClass, featureID, name, type, defaultValue, many);
  }

  public static EPackage getEPackage(CDOPackage cdoPackage, CDOPackageRegistry packageRegistry)
  {
    EPackage ePackage = (EPackage)cdoPackage.getClientInfo();
    if (ePackage == null)
    {
      String uri = cdoPackage.getPackageURI();
      ePackage = packageRegistry.getEPackage(uri);
      if (ePackage == null)
      {
        ePackage = createEPackage(cdoPackage);
        packageRegistry.put(uri, ePackage);
      }

      ((InternalCDOPackage)cdoPackage).setClientInfo(ePackage);
    }

    return ePackage;
  }

  public static EClass getEClass(CDOClass cdoClass, CDOPackageRegistry packageRegistry)
  {
    EClass eClass = (EClass)cdoClass.getClientInfo();
    if (eClass == null)
    {
      EPackage ePackage = getEPackage(cdoClass.getContainingPackage(), packageRegistry);
      eClass = (EClass)ePackage.getEClassifier(cdoClass.getName());
      ((InternalCDOClass)cdoClass).setClientInfo(eClass);
    }

    return eClass;
  }

  public static EStructuralFeature getEFeature(CDOFeature cdoFeature, CDOPackageRegistry packageRegistry)
  {
    EStructuralFeature eFeature = (EStructuralFeature)cdoFeature.getClientInfo();
    if (eFeature == null)
    {
      EClass eClass = getEClass(cdoFeature.getContainingClass(), packageRegistry);
      eFeature = eClass.getEStructuralFeature(cdoFeature.getFeatureID());
      ((InternalCDOFeature)cdoFeature).setClientInfo(eFeature);
    }

    return eFeature;
  }

  static EPackage createEPackage(CDOPackage cdoPackage)
  {
    if (cdoPackage.isDynamic())
    {
      return createDynamicEPackage(cdoPackage);
    }

    EPackage ePackage = getGeneratedEPackage(cdoPackage);
    if (ePackage == null)
    {
      throw new CDOException("Generated package locally not available: " + cdoPackage.getPackageURI());
    }

    return ePackage;
  }

  static EPackage getGeneratedEPackage(CDOPackage cdoPackage)
  {
    String packageURI = cdoPackage.getPackageURI();
    if (packageURI.equals(EcorePackage.eINSTANCE.getNsURI()))
    {
      return EcorePackage.eINSTANCE;
    }

    EPackage.Registry registry = EPackage.Registry.INSTANCE;
    return registry.getEPackage(packageURI);
  }

  static EPackage createDynamicEPackage(CDOPackage cdoPackage)
  {
    CDOPackage topLevelPackage = cdoPackage.getTopLevelPackage();
    String ecore = topLevelPackage.getEcore();
    EPackageImpl topLevelPackageEPackage = (EPackageImpl)EMFUtil.ePackageFromString(ecore);
    EPackageImpl ePackage = prepareDynamicEPackage(topLevelPackageEPackage, cdoPackage.getPackageURI());
    return ePackage;
  }

  static EPackageImpl prepareDynamicEPackage(EPackageImpl ePackage, String nsURI)
  {
    EMFUtil.prepareDynamicEPackage(ePackage);
    EPackageImpl result = ObjectUtil.equals(ePackage.getNsURI(), nsURI) ? ePackage : null;
    for (EPackage subPackage : ePackage.getESubpackages())
    {
      EPackageImpl p = prepareDynamicEPackage((EPackageImpl)subPackage, nsURI);
      if (p != null && result == null)
      {
        result = p;
      }
    }

    return result;
  }

  public static CDOClassRef createClassRef(EClassifier classifier)
  {
    if (classifier instanceof EClass)
    {
      String packageURI = classifier.getEPackage().getNsURI();
      int classifierID = classifier.getClassifierID();
      return CDOModelUtil.createClassRef(packageURI, classifierID);
    }

    return null;
  }

  public static void addModelInfos(CDOSessionPackageManagerImpl packageManager)
  {
    // Ecore
    CDOCorePackage corePackage = packageManager.getCDOCorePackage();
    ((InternalCDOPackage)corePackage).setClientInfo(EcorePackage.eINSTANCE);
    ((InternalCDOClass)corePackage.getCDOObjectClass()).setClientInfo(EcorePackage.eINSTANCE.getEObject());

    // Eresource
    if (!ObjectUtil.equals(CDOResourcePackage.PACKAGE_URI, EresourcePackage.eNS_URI))
    {
      throw new ImplementationError();
    }

    CDOResourcePackage resourcePackage = packageManager.getCDOResourcePackage();
    ((InternalCDOPackage)resourcePackage).setClientInfo(EresourcePackage.eINSTANCE);

    CDOResourceNodeClass resourceNodeClass = resourcePackage.getCDOResourceNodeClass();
    ((InternalCDOClass)resourceNodeClass).setClientInfo(EresourcePackage.eINSTANCE.getCDOResourceNode());
    ((InternalCDOFeature)resourceNodeClass.getCDOFolderFeature()).setClientInfo(EresourcePackage.eINSTANCE
        .getCDOResourceNode_Folder());
    ((InternalCDOFeature)resourceNodeClass.getCDONameFeature()).setClientInfo(EresourcePackage.eINSTANCE
        .getCDOResourceNode_Name());

    CDOResourceFolderClass resourceFolderClass = resourcePackage.getCDOResourceFolderClass();
    ((InternalCDOClass)resourceFolderClass).setClientInfo(EresourcePackage.eINSTANCE.getCDOResourceFolder());
    ((InternalCDOFeature)resourceFolderClass.getCDONodesFeature()).setClientInfo(EresourcePackage.eINSTANCE
        .getCDOResourceFolder_Nodes());

    CDOResourceClass resourceClass = resourcePackage.getCDOResourceClass();
    ((InternalCDOClass)resourceClass).setClientInfo(EresourcePackage.eINSTANCE.getCDOResource());
    ((InternalCDOFeature)resourceClass.getCDOContentsFeature()).setClientInfo(EresourcePackage.eINSTANCE
        .getCDOResource_Contents());
  }
}
