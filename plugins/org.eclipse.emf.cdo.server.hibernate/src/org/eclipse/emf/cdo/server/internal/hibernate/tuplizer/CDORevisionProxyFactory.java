/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.type.AbstractComponentType;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDORevisionProxyFactory implements ProxyFactory
{
  private String entityName;

  public CDORevisionProxyFactory()
  {
  }

  @SuppressWarnings("rawtypes")
  public void postInstantiate(String entityName, Class persistentClass, Set interfaces, Method getIdentifierMethod,
      Method setIdentifierMethod, AbstractComponentType componentIdType) throws HibernateException
  {
    this.entityName = entityName;
  }

  public HibernateProxy getProxy(Serializable id, SessionImplementor session) throws HibernateException
  {
    return new CDORevisionProxy(new CDORevisionLazyInitializer(entityName, id, session));
  }
}
