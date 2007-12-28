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
package org.eclipse.net4j.util.om.pref;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface OMPreferencesChangeEvent<T> extends IEvent
{
  public OMPreference<T> getPreference();

  public T getOldValue();

  public T getNewValue();
}