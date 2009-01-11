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
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class CDOClearFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOClearFeatureDelta
{
  public CDOClearFeatureDeltaImpl(CDOFeature feature)
  {
    super(feature);
  }

  public CDOClearFeatureDeltaImpl(CDODataInput in, CDOClass cdoClass) throws IOException
  {
    super(in, cdoClass);
  }

  public Type getType()
  {
    return Type.CLEAR;
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).clear(getFeature());
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public void adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    // Do nothing
  }
}
