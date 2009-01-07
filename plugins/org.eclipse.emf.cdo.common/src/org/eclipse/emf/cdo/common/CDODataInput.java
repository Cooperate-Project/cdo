/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 * 		Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassifier;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.io.ExtendedDataInput;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDODataInput extends ExtendedDataInput
{
  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public String readCDOPackageURI() throws IOException;

  public CDOType readCDOType() throws IOException;

  public CDOClassifier readCDOClassifierRef(CDOPackageManager packageManager) throws IOException;

  public CDOClass readCDOClassRefAndResolve() throws IOException;

  public void readCDOPackage(CDOPackage cdoPackage) throws IOException;

  public CDOPackage readCDOPackage() throws IOException;

  public CDOClassifier readCDOClassifier(CDOPackage containingPackage) throws IOException;

  public CDOFeature readCDOFeature(CDOClass containingClass) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDOID readCDOID() throws IOException;

  public CDOIDAndVersion readCDOIDAndVersion() throws IOException;

  public CDOIDMetaRange readCDOIDMetaRange() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDORevision readCDORevision() throws IOException;

  public CDOList readCDOList(CDORevision revision, CDOFeature feature) throws IOException;

  public CDORevisionDelta readCDORevisionDelta() throws IOException;

  public CDOFeatureDelta readCDOFeatureDelta(CDOClass cdoClass) throws IOException;

  /**
   * Read either a CDORevision or a primitive value.
   */
  public Object readCDORevisionOrPrimitive() throws IOException;

  /**
   * Read either a CDORevision, a primitive value or a CDOClass.
   */
  public Object readCDORevisionOrPrimitiveOrClass() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public RWLockManager.LockType readCDOLockType() throws IOException;
}
