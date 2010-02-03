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
package org.eclipse.emf.cdo.session.remote;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDORemoteSessionEvent extends IEvent
{
  /**
   * @since 3.0
   */
  public CDORemoteSessionManager getSource();

  public CDORemoteSession getRemoteSession();

  /**
   * @author Eike Stepper
   */
  public interface SubscriptionChanged extends CDORemoteSessionEvent
  {
    public boolean isSubscribed();
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public interface MessageReceived extends CDORemoteSessionEvent
  {
    public CDORemoteSessionMessage getMessage();
  }
}
