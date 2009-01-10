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
package org.eclipse.net4j.buddies.internal.ui;

import org.eclipse.net4j.buddies.common.IBuddyContainer;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

/**
 * @author Eike Stepper
 */
public class BuddiesContentProvider extends StructuredContentProvider<IBuddyContainer>
{
  public BuddiesContentProvider()
  {
  }

  public Object[] getElements(Object inputElement)
  {
    return getInput().getElements();
  }

  @Override
  protected void connectInput(IBuddyContainer input)
  {
    input.addListener(this);
  }

  @Override
  protected void disconnectInput(IBuddyContainer input)
  {
    input.removeListener(this);
  }
}
