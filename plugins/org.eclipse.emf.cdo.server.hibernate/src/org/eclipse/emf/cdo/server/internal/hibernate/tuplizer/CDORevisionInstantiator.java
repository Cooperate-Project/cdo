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
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.Instantiator;

import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public class CDORevisionInstantiator implements Instantiator
{
  private static final long serialVersionUID = 1L;

  private CDOClass cdoClass;

  private IRevisionManager revisionManager;

  public CDORevisionInstantiator(CDORevisionTuplizer tuplizer, PersistentClass mappingInfo)
  {
    cdoClass = tuplizer.getCDOClass();
    final HibernateStore hbStore = HibernateStore.getCurrentHibernateStore();
    IRepository repository = hbStore.getRepository();
    revisionManager = repository.getRevisionManager();
  }

  public Object instantiate()
  {
    // TODO CDO can't create a revision w/o CDOID
    return instantiate(null);
  }

  public Object instantiate(Serializable id)
  {
    return CDORevisionUtil.create(revisionManager, cdoClass, CDOIDHibernateFactoryImpl.getInstance().createCDOID(id,
        cdoClass.getName()));
  }

  public boolean isInstance(Object object)
  {
    return object instanceof InternalCDORevision;
  }
}
