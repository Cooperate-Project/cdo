/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.tcp.TCPConnector;
import org.eclipse.net4j.tcp.TCPSelector;
import org.eclipse.net4j.tcp.TCPSelectorListener;
import org.eclipse.net4j.transport.ConnectorException;
import org.eclipse.net4j.transport.ConnectorState;
import org.eclipse.net4j.transport.IBuffer;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.Channel;
import org.eclipse.internal.net4j.transport.Connector;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Queue;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTCPConnector extends Connector implements TCPConnector, TCPSelectorListener.Active
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_CONNECTOR, AbstractTCPConnector.class);

  private SocketChannel socketChannel;

  private TCPSelector selector;

  private SelectionKey selectionKey;

  private IBuffer inputBuffer;

  private ControlChannelImpl controlChannel;

  private String host;

  private int port;

  public AbstractTCPConnector()
  {
  }

  public String getHost()
  {
    return host;
  }

  public int getPort()
  {
    return port;
  }

  public TCPSelector getSelector()
  {
    return selector;
  }

  public void setSelector(TCPSelector selector)
  {
    this.selector = selector;
  }

  public SocketChannel getSocketChannel()
  {
    return socketChannel;
  }

  /**
   * SocketChannel must already be non-blocking!
   */
  public void setSocketChannel(SocketChannel socketChannel)
  {
    this.socketChannel = socketChannel;
  }

  /**
   * Called by {@link Channel} each time a new buffer is available for
   * multiplexing. This or another buffer can be dequeued from the outputQueue
   * of the {@link Channel}.
   */
  public void multiplexBuffer(IChannel channel)
  {
    checkSelectionKey();
    selector.setWriteInterest(selectionKey, true);
  }

  public void registered(SelectionKey selectionKey)
  {
    this.selectionKey = selectionKey;
    if (isServer())
    {
      selector.setConnectInterest(selectionKey, false);
    }
  }

  public void handleConnect(TCPSelector selector, SocketChannel channel)
  {
    try
    {
      if (!channel.finishConnect())
      {
        return;
      }
    }
    catch (Exception ex)
    {
      return;
    }

    try
    {
      checkSelectionKey();
      selector.setConnectInterest(selectionKey, false);
      setState(ConnectorState.NEGOTIATING);
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
      deactivate();
    }
  }

  public void handleRead(TCPSelector selector, SocketChannel socketChannel)
  {
    try
    {
      if (inputBuffer == null)
      {
        inputBuffer = getBufferProvider().provideBuffer();
      }

      ByteBuffer byteBuffer = inputBuffer.startGetting(socketChannel);
      if (byteBuffer != null)
      {
        short channelIndex = inputBuffer.getChannelIndex();
        Channel channel = channelIndex == ControlChannelImpl.CONTROL_CHANNEL_ID ? controlChannel
            : getChannel(channelIndex);
        if (channel != null)
        {
          channel.handleBufferFromMultiplexer(inputBuffer);
        }
        else
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Discarding buffer from unknown channel"); //$NON-NLS-1$
          }

          inputBuffer.release();
        }

        inputBuffer = null;
      }
    }
    catch (ClosedChannelException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
      deactivate();
    }
  }

  public void handleWrite(TCPSelector selector, SocketChannel socketChannel)
  {
    try
    {
      boolean moreToWrite = false;
      for (Queue<IBuffer> bufferQueue : getChannelBufferQueues())
      {
        IBuffer buffer = bufferQueue.peek();
        if (buffer != null)
        {
          if (buffer.write(socketChannel))
          {
            bufferQueue.poll();
            buffer.release();

            if (!moreToWrite)
            {
              moreToWrite = !bufferQueue.isEmpty();
            }
          }
          else
          {
            moreToWrite = true;
            break;
          }
        }
      }

      if (!moreToWrite)
      {
        checkSelectionKey();
        selector.setWriteInterest(selectionKey, false);
      }
    }
    catch (NullPointerException ignore)
    {
      ;
    }
    catch (ClosedChannelException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
      deactivate();
    }
  }

  @Override
  protected List<Queue<IBuffer>> getChannelBufferQueues()
  {
    List<Queue<IBuffer>> queues = super.getChannelBufferQueues();
    Queue<IBuffer> controlQueue = controlChannel.getSendQueue();
    if (!controlQueue.isEmpty())
    {
      queues.add(controlQueue);
    }

    return queues;
  }

  @Override
  protected void registerChannelWithPeer(short channelIndex, String protocolID) throws ConnectorException
  {
    try
    {
      if (!controlChannel.registerChannel(channelIndex, protocolID))
      {
        throw new ConnectorException("Failed to register channel with peer"); //$NON-NLS-1$
      }
    }
    catch (ConnectorException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }
  }

  @Override
  protected void removeChannel(Channel channel)
  {
    if (isConnected())
    {
      controlChannel.deregisterChannel(channel.getChannelIndex());
    }

    super.removeChannel(channel);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (socketChannel == null)
    {
      throw new IllegalStateException("socketChannel == null");
    }

    if (selector == null)
    {
      throw new IllegalStateException("selector == null");
    }

    if (host == null)
    {
      throw new IllegalStateException("host == null");
    }

    if (port == 0)
    {
      throw new IllegalStateException("port == 0");
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    controlChannel = new ControlChannelImpl(this);
    controlChannel.activate();
    selector.registerAsync(socketChannel, this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    Exception exception = null;

    try
    {
      controlChannel.deactivate();
    }
    catch (Exception ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      controlChannel = null;
    }

    try
    {
      socketChannel.close();
    }
    catch (Exception ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      socketChannel = null;
    }

    try
    {
      super.doDeactivate();
    }
    catch (Exception ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }

    if (exception != null)
    {
      throw exception;
    }
  }

  private void checkSelectionKey()
  {
    if (selectionKey == null)
    {
      throw new IllegalStateException("selectionKey == null");
    }
  }
}
