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
package org.eclipse.net4j;

import org.eclipse.net4j.protocol.ClientProtocolFactory;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.protocol.ServerProtocolFactory;
import org.eclipse.net4j.util.concurrent.NonBlockingLongCounter;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ContainerProtocolProvider implements IProtocolProvider
{
  private static NonBlockingLongCounter counter = new NonBlockingLongCounter();

  private IManagedContainer container;

  private String productGroup;

  protected ContainerProtocolProvider(IManagedContainer container, String productGroup)
  {
    this.container = container;
    this.productGroup = productGroup;
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public IProtocol getProtocol(String type)
  {
    return (IProtocol)container.getElement(productGroup, type, "protocol-" + counter.increment(), false);
  }

  /**
   * @author Eike Stepper
   */
  public static class Client extends ContainerProtocolProvider
  {
    public Client(IManagedContainer container)
    {
      super(container, ClientProtocolFactory.PRODUCT_GROUP);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Server extends ContainerProtocolProvider
  {
    public Server(IManagedContainer container)
    {
      super(container, ServerProtocolFactory.PRODUCT_GROUP);
    }
  }
}
