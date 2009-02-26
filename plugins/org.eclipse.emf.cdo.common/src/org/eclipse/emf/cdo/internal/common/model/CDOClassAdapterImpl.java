/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.model.CDOClassAdapter;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOClassAdapterImpl extends AdapterImpl implements CDOClassAdapter
{
  private EStructuralFeature[] allPersistentFeatures;

  private int[] featureIDMappings;

  public CDOClassAdapterImpl()
  {
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return EClass.class.isInstance(type);
  }

  @Override
  public void setTarget(Notifier newTarget)
  {
    init((EClass)newTarget);
    super.setTarget(newTarget);
  }

  public EClass getEClass()
  {
    return (EClass)getTarget();
  }

  public boolean isResource()
  {
    return CDOModelUtil.isResource(getEClass());
  }

  public boolean isResourceFolder()
  {
    return CDOModelUtil.isResourceFolder(getEClass());
  }

  public boolean isResourceNode()
  {
    return CDOModelUtil.isResourceNode(getEClass());
  }

  public EStructuralFeature[] getAllPersistentFeatures()
  {
    return allPersistentFeatures;
  }

  public int getFeatureIndex(EStructuralFeature feature)
  {
    int featureID = getEClass().getFeatureID(feature);
    return getFeatureIndex(featureID);
  }

  public int getFeatureIndex(int featureID)
  {
    return featureIDMappings[featureID];
  }

  private void init(EClass eClass)
  {
    int maxID = 0;
    List<EStructuralFeature> features = new ArrayList<EStructuralFeature>();
    for (EStructuralFeature feature : eClass.getEAllStructuralFeatures())
    {
      if (!feature.isTransient())
      {
        features.add(feature);
        int featureID = eClass.getFeatureID(feature);
        maxID = Math.max(maxID, featureID);
      }
    }

    allPersistentFeatures = features.toArray(new EStructuralFeature[features.size()]);
    featureIDMappings = new int[maxID + 1];
    for (int i = 0; i < allPersistentFeatures.length; i++)
    {
      EStructuralFeature feature = allPersistentFeatures[i];
      int featureID = eClass.getFeatureID(feature);
      featureIDMappings[featureID] = i;
    }
  }
}
