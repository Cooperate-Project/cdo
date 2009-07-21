/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class RevisionAdjuster extends CDOFeatureDeltaVisitorImpl
{
  private CDOReferenceAdjuster referenceAdjuster;

  private InternalCDORevision revision;

  public RevisionAdjuster(CDOReferenceAdjuster referenceAdjuster)
  {
    this.referenceAdjuster = referenceAdjuster;
  }

  public void adjustRevision(InternalCDORevision revision, CDORevisionDelta revisionDelta)
  {
    this.revision = revision;
    revisionDelta.accept(this);
  }

  @Override
  public void visit(CDOContainerFeatureDelta delta)
  {
    // Delta value must have been adjusted before!
    revision.setContainerID(referenceAdjuster.adjustReference(revision.getContainerID()));
    revision.setResourceID(referenceAdjuster.adjustReference(revision.getResourceID()));
  }

  @Override
  public void visit(CDOAddFeatureDelta delta)
  {
    // Delta value must have been adjusted before!
    revision.setValue(delta.getFeature(), delta.getValue());
  }

  @Override
  public void visit(CDOSetFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    CDORevisionImpl.checkNoFeatureMap(feature);

    Object value = delta.getValue();
    if (value instanceof CDOID)
    {
      revision.setValue(feature, referenceAdjuster.adjustReference((CDOID)value));
    }
  }

  @Override
  public void visit(CDOListFeatureDelta deltas)
  {
    EStructuralFeature feature = deltas.getFeature();
    CDORevisionImpl.checkNoFeatureMap(feature);

    InternalCDOList list = (InternalCDOList)revision.getValue(feature);
    if (feature instanceof EReference)
    {
      int[] indices = ((CDOListFeatureDeltaImpl)deltas).reconstructAddedIndices().getElement2();
      for (int i = 1; i <= indices[0]; i++)
      {
        int index = indices[i];
        Object value = list.get(index);
        if (value instanceof CDOID)
        {
          value = referenceAdjuster.adjustReference((CDOID)value);
          list.set(index, value);
        }
      }
    }
  }
}
