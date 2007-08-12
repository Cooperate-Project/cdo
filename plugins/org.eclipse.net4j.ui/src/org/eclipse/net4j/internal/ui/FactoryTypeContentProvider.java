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
package org.eclipse.net4j.internal.ui;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

/**
 * @author Eike Stepper
 */
public class FactoryTypeContentProvider extends StructuredContentProvider<IManagedContainer>
{
  private String productGroup;

  public FactoryTypeContentProvider(String productGroup)
  {
    this.productGroup = productGroup;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public Object[] getElements(Object inputElement)
  {
    return getInput().getFactoryTypes(productGroup).toArray();
  }

  @Override
  protected void connectInput(IManagedContainer input)
  {
    input.addListener(this);
    input.getFactoryRegistry().addListener(this);
  }

  @Override
  protected void disconnectInput(IManagedContainer input)
  {
    input.removeListener(this);
    input.getFactoryRegistry().removeListener(this);
  }
}
