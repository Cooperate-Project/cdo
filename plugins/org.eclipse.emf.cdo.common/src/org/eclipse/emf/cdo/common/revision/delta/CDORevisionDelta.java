/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 */
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import java.util.List;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionDelta
{
  public CDOID getID();

  /**
   * Specify the version of the object BEFORE it was modified.
   */
  public int getOriginVersion();

  /**
   * Specify the version of the object AFTER it was modified.
   */
  public int getDirtyVersion();

  public List<CDOFeatureDelta> getFeatureDeltas();

  public void apply(CDORevision revision);

  public void accept(CDOFeatureDeltaVisitor visitor);
}
