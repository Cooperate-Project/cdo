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
package org.eclipse.internal.net4j.acceptor;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.TransportConfig;
import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalAcceptor;
import org.eclipse.spi.net4j.InternalConnector;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class Acceptor extends Container<IConnector> implements InternalAcceptor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_ACCEPTOR, Acceptor.class);

  private ITransportConfig config;

  private transient IListener connectorListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      removeConnector((IConnector)lifecycle);
    }
  };

  private Set<IConnector> acceptedConnectors = new HashSet<IConnector>(0);

  public Acceptor()
  {
  }

  public ITransportConfig getConfig()
  {
    if (config == null)
    {
      config = new TransportConfig();
    }

    return config;
  }

  public void setConfig(ITransportConfig config)
  {
    this.config = config;
  }

  public IConnector[] getAcceptedConnectors()
  {
    synchronized (acceptedConnectors)
    {
      return acceptedConnectors.toArray(new IConnector[acceptedConnectors.size()]);
    }
  }

  @Override
  public boolean isEmpty()
  {
    return acceptedConnectors.isEmpty();
  }

  public IConnector[] getElements()
  {
    return getAcceptedConnectors();
  }

  public void prepareConnector(InternalConnector connector)
  {
    connector.setConfig(getConfig());
  }

  public void addConnector(InternalConnector connector)
  {
    synchronized (acceptedConnectors)
    {
      acceptedConnectors.add(connector);
    }

    connector.addListener(connectorListener);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Added connector " + connector); //$NON-NLS-1$
    }

    fireElementAddedEvent(connector);
  }

  public void removeConnector(IConnector connector)
  {
    connector.removeListener(connectorListener);
    synchronized (acceptedConnectors)
    {
      acceptedConnectors.remove(connector);
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Removed connector " + connector); //$NON-NLS-1$
    }

    fireElementRemovedEvent(connector);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (getConfig().getBufferProvider() == null)
    {
      throw new IllegalStateException("getConfig().getBufferProvider() == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IConnector connector : getAcceptedConnectors())
    {
      LifecycleUtil.deactivate(connector);
    }

    super.doDeactivate();
  }
}
