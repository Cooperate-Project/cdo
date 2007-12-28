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
package org.eclipse.net4j.util.internal.ui.actions;

import org.eclipse.net4j.util.internal.ui.views.Net4jIntrospectorView;
import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Eike Stepper
 */
public class IntrospectAction extends SafeAction
{
  private Object object;

  public IntrospectAction(Object object)
  {
    super("Introspect");
    this.object = object;
  }

  @Override
  protected void safeRun() throws Exception
  {
    Net4jIntrospectorView introspector = Net4jIntrospectorView.getInstance(true);
    introspector.setObject(object);
  }
}