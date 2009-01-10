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
package org.eclipse.net4j.jms.server;

/**
 * @author Eike Stepper
 */
public interface IStore
{
  public String getStoreType();

  public String getInstanceID();

  public IStoreTransaction startTransaction();

  public void commitTransaction(IStoreTransaction transaction);

  public void rollbackTransaction(IStoreTransaction transaction);
}
