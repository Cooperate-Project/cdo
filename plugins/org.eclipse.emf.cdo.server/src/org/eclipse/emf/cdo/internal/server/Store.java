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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class Store extends Lifecycle implements IStore
{
  private static final long INITIAL_OID_VALUE = 2;

  private String type;

  private Map<String, String> properties;

  private IRepository repository;

  private long nextOIDValue = INITIAL_OID_VALUE;

  public Store(String type)
  {
    this.type = type;
  }

  public String getStoreType()
  {
    return type;
  }

  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public IRepository getRepository()
  {
    return repository;
  }

  public void setRepository(IRepository repository)
  {
    this.repository = repository;
  }

  public CDOID getNextCDOID()
  {
    CDOID id = CDOIDUtil.create(nextOIDValue);
    ++nextOIDValue;
    ++nextOIDValue;
    return id;
  }

  public long getNextOIDValue()
  {
    return nextOIDValue;
  }

  public void setNextOIDValue(long nextOIDValue)
  {
    this.nextOIDValue = nextOIDValue;
  }

  public boolean wasCrashed()
  {
    return nextOIDValue == 0L;
  }

  public boolean hasWriteDeltaSupport()
  {
    return false;
  }

  public boolean hasAuditingSupport()
  {
    return false;
  }

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public IStoreReader getReader(ISession session)
  {
    return createReader(session);
  }

  protected abstract IStoreReader createReader(ISession session);

  public IStoreWriter getWriter(IView view)
  {
    return createWriter(view);
  }

  protected abstract IStoreWriter createWriter(IView view);

  protected void releaseAccessor(IStoreAccessor accessor)
  {
  }
}
