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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionData
{
  /**
   * @since 2.0
   */
  public CDORevision revision();

  public CDOID getResourceID();

  /**
   * @since 2.0
   */
  public Object getContainerID();

  public int getContainingFeatureID();

  /**
   * @since 2.0
   */
  public Object get(EStructuralFeature feature, int index);

  /**
   * @since 2.0
   */
  public int size(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public boolean isEmpty(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public boolean contains(EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public int indexOf(EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public int lastIndexOf(EStructuralFeature feature, Object value);

  public <T> T[] toArray(EStructuralFeature feature, T[] array);

  /**
   * @since 2.0
   */
  public Object[] toArray(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public int hashCode(EStructuralFeature feature);
}
