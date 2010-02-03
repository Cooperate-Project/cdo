/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.view.CDOViewEvent;

import java.util.Map;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOTransactionFinishedEvent extends CDOViewEvent
{
  public Type getType();

  public Map<CDOIDTemp, CDOID> getIDMappings();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    COMMITTED, ROLLED_BACK
  }
}
