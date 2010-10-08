/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IListener;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public interface IDawnListener extends CDOTransactionHandler, IListener
{
  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event);

  public void handleTransactionConflictEvent(CDOTransactionConflictEvent event);
}
