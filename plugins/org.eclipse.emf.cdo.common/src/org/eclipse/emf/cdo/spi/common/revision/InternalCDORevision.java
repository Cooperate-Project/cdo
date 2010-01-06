/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjustable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDORevision extends CDORevision, CDORevisionData, CDOReferenceAdjustable
{
  /**
   * @since 3.0
   */
  public CDOClassInfo getClassInfo();

  public void setID(CDOID id);

  public void setVersion(int version);

  /**
   * @since 3.0
   */
  public int setTransactional(boolean on);

  public void setCreated(long created);

  public void setRevised(long revised);

  public void setResourceID(CDOID resourceID);

  public void setContainerID(Object containerID);

  public void setContainingFeatureID(int containingFeatureID);

  public void add(EStructuralFeature feature, int index, Object value);

  public void clear(EStructuralFeature feature);

  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex);

  public Object remove(EStructuralFeature feature, int index);

  public Object set(EStructuralFeature feature, int index, Object value);

  public void unset(EStructuralFeature feature);

  /**
   * Should never return {@link InternalCDORevision#NIL}
   */
  public Object getValue(EStructuralFeature feature);

  public Object setValue(EStructuralFeature feature, Object value);

  public void setList(EStructuralFeature feature, InternalCDOList list);

  public CDOList getList(EStructuralFeature feature);

  /**
   * @param size
   *          the size of a new list to be created if this revision has no list so far, or -1 to skip list creation and
   *          return <code>null</code> in this case.
   */
  public CDOList getList(EStructuralFeature feature, int size);

  /**
   * @since 3.0
   */
  public void read(CDODataInput in) throws IOException;

  public void write(CDODataOutput out, int referenceChunk) throws IOException;

  /**
   * @since 3.0
   */
  public void convertEObjects(CDOIDProvider oidProvider);
}
