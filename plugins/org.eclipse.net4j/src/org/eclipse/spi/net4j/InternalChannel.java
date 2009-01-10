/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public interface InternalChannel extends IChannel, IBufferProvider, ILifecycle.Introspection
{
  /**
   * @since 2.0
   */
  public void setID(short id);

  /**
   * @since 2.0
   */
  public void setUserID(String userID);

  public ExecutorService getReceiveExecutor();

  public void setReceiveExecutor(ExecutorService receiveExecutor);

  /**
   * @since 2.0
   */
  public void setMultiplexer(IChannelMultiplexer channelMultiplexer);

  public void handleBufferFromMultiplexer(IBuffer buffer);

  public Queue<IBuffer> getSendQueue();
}
