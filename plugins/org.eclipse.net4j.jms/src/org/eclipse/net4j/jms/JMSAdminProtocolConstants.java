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
package org.eclipse.net4j.jms;

/**
 * @author Eike Stepper
 */
public interface JMSAdminProtocolConstants
{
  public static final String PROTOCOL_NAME = "jmsadmin";

  public static final short SIGNAL_CREATE_DESTINATION = 1;

  public static final byte DESTINATION_TYPE_QUEUE = JMSProtocolConstants.DESTINATION_TYPE_QUEUE;

  public static final byte DESTINATION_TYPE_TOPIC = JMSProtocolConstants.DESTINATION_TYPE_TOPIC;
}
