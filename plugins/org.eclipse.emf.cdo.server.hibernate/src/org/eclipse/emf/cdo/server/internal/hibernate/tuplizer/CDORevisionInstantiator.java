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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStoreAccessor;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.Instantiator;

import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public class CDORevisionInstantiator implements Instantiator
{
  private static final long serialVersionUID = 1L;

  private final EClass eClass;

  private final CDOClassifierRef classifierRef;

  public CDORevisionInstantiator(CDORevisionTuplizer tuplizer, PersistentClass mappingInfo)
  {
    eClass = tuplizer.getEClass();
    classifierRef = new CDOClassifierRef(eClass);
  }

  public Object instantiate()
  {
    // TODO CDO can't create a revision w/o CDOID
    return instantiate(null);
  }

  public Object instantiate(Serializable id)
  {
    final HibernateStoreAccessor storeAccessor = HibernateThreadContext.getCurrentStoreAccessor();
    HibernateStore store = storeAccessor.getStore();
    final CDOID cdoID = HibernateUtil.getInstance().createCDOID(classifierRef, id);
    return store.createRevision(eClass, cdoID);
  }

  public boolean isInstance(Object object)
  {
    return object instanceof InternalCDORevision;
  }
}
