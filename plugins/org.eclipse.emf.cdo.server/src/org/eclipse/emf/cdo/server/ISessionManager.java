/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;

import org.eclipse.net4j.util.container.IContainer;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISessionManager extends IRepositoryElement, IContainer<ISession>
{
  public ISession[] getSessions();

  /**
   * @since 2.0
   */
  public ISession getSession(int sessionID);

  /**
   * @return Never <code>null</code>
   * @since 2.0
   */
  public ISession openSession(CDOServerProtocol protocol) throws SessionCreationException;
}
