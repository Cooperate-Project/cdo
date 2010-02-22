/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

/**
 * Notifies about a change of the branch point of the view.
 * 
 * @author Victor Roldan Betancort
 * @since 3.0
 */
public interface CDOViewTargetChangedEvent extends CDOViewEvent
{
  public CDOBranchPoint getBranchPoint();
}
