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
package org.eclipse.net4j.internal.ui;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

import org.eclipse.spi.net4j.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class ConnectorContentProvider extends StructuredContentProvider<IManagedContainer>
{
  public static final String PRODUCT_GROUP = ConnectorFactory.PRODUCT_GROUP;

  public ConnectorContentProvider()
  {
  }

  public Object[] getElements(Object inputElement)
  {
    return getInput().getElements(PRODUCT_GROUP);
  }

  @Override
  protected void connectInput(IManagedContainer input)
  {
    input.addListener(this);
  }

  @Override
  protected void disconnectInput(IManagedContainer input)
  {
    input.removeListener(this);
  }
}
