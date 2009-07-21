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
package org.eclipse.emf.cdo.common.revision;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOReferenceAdjustable
{
  /**
   * @since 3.0
   */
  public void applyReferenceAdjuster(CDOReferenceAdjuster referenceAdjuster);
}
