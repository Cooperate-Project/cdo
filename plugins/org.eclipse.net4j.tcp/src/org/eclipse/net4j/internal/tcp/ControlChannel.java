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
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ISynchronizer;
import org.eclipse.net4j.util.concurrent.SynchronizingCorrelator;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiationContext.Receiver;

import org.eclipse.internal.net4j.buffer.BufferUtil;

import org.eclipse.spi.net4j.Channel;
import org.eclipse.spi.net4j.InternalChannel;

import java.nio.ByteBuffer;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class ControlChannel extends Channel
{
  public static final short CONTROL_CHANNEL_INDEX = IBuffer.CONTROL_CHANNEL;

  public static final byte OPCODE_NEGOTIATION = 1;

  public static final byte OPCODE_REGISTRATION = 2;

  public static final byte OPCODE_REGISTRATION_ACK = 3;

  public static final byte OPCODE_DEREGISTRATION = 4;

  public static final byte OPCODE_DEREGISTRATION_ACK = 5;

  public static final byte SUCCESS = 1;

  public static final byte FAILURE = 0;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ControlChannel.class);

  private SynchronizingCorrelator<Short, Boolean> acknowledgements = new SynchronizingCorrelator<Short, Boolean>();

  public ControlChannel(TCPConnector connector)
  {
    setID(CONTROL_CHANNEL_INDEX);
    setMultiplexer(connector);
    setReceiveExecutor(connector.getConfig().getReceiveExecutor());
    setUserID(connector.getUserID());
  }

  public TCPConnector getConnector()
  {
    return (TCPConnector)getMultiplexer();
  }

  public boolean registerChannel(short channelID, long timeout, IProtocol<?> protocol)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering channel {0} with protocol {1}", channelID, protocol);
    }

    assertValidChannelID(channelID);
    ISynchronizer<Boolean> acknowledgement = acknowledgements.correlate(channelID);

    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(OPCODE_REGISTRATION);
    byteBuffer.putShort(channelID);
    BufferUtil.putUTF8(byteBuffer, protocol == null ? null : protocol.getType());
    handleBuffer(buffer);

    Boolean acknowledged = acknowledgement.get(timeout);
    if (acknowledged == null)
    {
      throw new TimeoutRuntimeException("Registration timeout after " + timeout + " milliseconds");
    }

    return acknowledged;
  }

  public boolean deregisterChannel(short channelID, long timeout)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deregistering channel {0}", channelID);
    }

    assertValidChannelID(channelID);
    ISynchronizer<Boolean> acknowledgement = acknowledgements.correlate(channelID);

    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(OPCODE_DEREGISTRATION);
    byteBuffer.putShort(channelID);
    handleBuffer(buffer);

    Boolean acknowledged = acknowledgement.get(timeout);
    if (acknowledged == null)
    {
      throw new TimeoutRuntimeException("Deregistration timeout after " + timeout + " milliseconds");
    }

    return acknowledged;
  }

  @Override
  public void handleBufferFromMultiplexer(IBuffer buffer)
  {
    try
    {
      ByteBuffer byteBuffer = buffer.getByteBuffer();
      byte opcode = byteBuffer.get();
      switch (opcode)
      {
      case OPCODE_NEGOTIATION:
      {
        assertNegotiating();
        INegotiationContext negotiationContext = getConnector().getNegotiationContext();
        while (negotiationContext == null)
        {
          ConcurrencyUtil.sleep(20);
          negotiationContext = getConnector().getNegotiationContext();
        }

        Receiver receiver = negotiationContext.getReceiver();
        receiver.receiveBuffer(negotiationContext, byteBuffer);
        break;
      }

      case OPCODE_REGISTRATION:
      {
        assertConnected();
        short channelID = byteBuffer.getShort();
        assertValidChannelID(channelID);
        boolean success = true;

        try
        {
          byte[] handlerFactoryUTF8 = BufferUtil.getByteArray(byteBuffer);
          String protocolID = BufferUtil.fromUTF8(handlerFactoryUTF8);
          InternalChannel channel = getConnector().inverseOpenChannel(channelID, protocolID);
          if (channel == null)
          {
            throw new ConnectorException("Could not open channel");
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          success = false;
        }

        sendStatus(OPCODE_REGISTRATION_ACK, channelID, success);
        break;
      }

      case OPCODE_DEREGISTRATION:
      {
        assertConnected();
        boolean success = true;
        short channelID = byteBuffer.getShort();
        if (channelID == CONTROL_CHANNEL_INDEX)
        {
          throw new ImplementationError();
        }

        try
        {
          getConnector().inverseCloseChannel(channelID);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          success = false;
        }

        sendStatus(OPCODE_DEREGISTRATION_ACK, channelID, success);
        break;
      }

      case OPCODE_REGISTRATION_ACK:
      case OPCODE_DEREGISTRATION_ACK:
      {
        assertConnected();
        short channelID = byteBuffer.getShort();
        boolean success = byteBuffer.get() == SUCCESS;
        acknowledgements.put(channelID, success);
        break;
      }

      default:
        OM.LOG.error("Invalid opcode: " + opcode); //$NON-NLS-1$
        getConnector().deactivate();
      }
    }
    finally
    {
      buffer.release();
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Channel[Control, {0}]", getLocation()); //$NON-NLS-1$
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    finishDeactivate(true);
  }

  private void sendStatus(byte opcode, short channelID, boolean status)
  {
    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(opcode);
    byteBuffer.putShort(channelID);
    byteBuffer.put(status ? SUCCESS : FAILURE);
    handleBuffer(buffer);
  }

  private void assertNegotiating()
  {
    if (!getConnector().isNegotiating())
    {
      getConnector().deactivate();
      throw new IllegalStateException("Connector is not negotiating");
    }
  }

  private void assertConnected()
  {
    if (!getConnector().isConnected())
    {
      throw new IllegalStateException("Connector is not connected");
    }
  }

  private void assertValidChannelID(short channelID)
  {
    if (channelID <= CONTROL_CHANNEL_INDEX)
    {
      throw new IllegalArgumentException("channelID <= CONTROL_CHANNEL_ID"); //$NON-NLS-1$
    }
  }
}
