/**
 * Copyright (c) 2004 - 2009 Martin Taal and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;

/**
 * @author Martin Taal
 */
public class CDOContainingFeatureNameGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOContainingFeatureNameGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    return ((InternalCDORevision)target).getContainingFeatureID();
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }

}
