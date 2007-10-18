/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryElement;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.internal.util.container.Container;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Eike Stepper
 */
public class Repository extends Container<IRepositoryElement> implements IRepository
{
  public static final String PROP_OVERRIDE_UUID = "overrideUUID";

  public static final String PROP_SUPPORTING_AUDITS = "supportingAudits";

  public static final String PROP_VERIFYING_REVISIONS = "verifyingRevisions";

  public static final String PROP_REMEMBERING_KNOWN_TYPES = "rememberingKnownTypes";

  public static final String PROP_CURRENT_LRU_CAPACITY = "currentLRUCapacity";

  public static final String PROP_REVISED_LRU_CAPACITY = "revisedLRUCapacity";

  private static final long INITIAL_META_ID_VALUE = 1;

  private String name;

  private IStore store;

  private String uuid;

  private Map<String, String> properties;

  private Boolean supportingAudits;

  private Boolean verifyingRevisions;

  private Boolean rememberingKnownTypes;

  private TypeManager typeManager = createTypeManager();

  private PackageManager packageManager = createPackageManager();

  private SessionManager sessionManager = createSessionManager();

  private ResourceManager resourceManager = createResourceManager();

  private RevisionManager revisionManager = createRevisionManager();

  private IRepositoryElement[] elements;

  private long nextMetaIDValue = INITIAL_META_ID_VALUE;

  public Repository()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public IStore getStore()
  {
    return store;
  }

  public void setStore(IStore store)
  {
    this.store = store;
  }

  public String getUUID()
  {
    if (uuid == null)
    {
      String value = getProperties().get(PROP_OVERRIDE_UUID);
      uuid = StringUtil.isEmpty(value) ? UUID.randomUUID().toString() : value;
    }

    return uuid;
  }

  public Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public boolean isSupportingAudits()
  {
    if (supportingAudits == null)
    {
      String value = getProperties().get(PROP_SUPPORTING_AUDITS);
      supportingAudits = value == null ? false : Boolean.valueOf(value);
    }

    return supportingAudits;
  }

  public boolean isVerifyingRevisions()
  {
    if (verifyingRevisions == null)
    {
      String value = getProperties().get(PROP_VERIFYING_REVISIONS);
      verifyingRevisions = value == null ? false : Boolean.valueOf(value);
    }

    return verifyingRevisions;
  }

  public boolean isRememberingKnownTypes()
  {
    if (rememberingKnownTypes == null)
    {
      String value = getProperties().get(PROP_REMEMBERING_KNOWN_TYPES);
      rememberingKnownTypes = value == null ? false : Boolean.valueOf(value);
    }

    return rememberingKnownTypes;
  }

  public TypeManager getTypeManager()
  {
    return typeManager;
  }

  public PackageManager getPackageManager()
  {
    return packageManager;
  }

  public SessionManager getSessionManager()
  {
    return sessionManager;
  }

  public ResourceManager getResourceManager()
  {
    return resourceManager;
  }

  public RevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  public IRepositoryElement[] getElements()
  {
    return elements;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  public CDOIDRange getMetaIDRange(long count)
  {
    long lowerBound = nextMetaIDValue;
    nextMetaIDValue += count;
    nextMetaIDValue += count;
    return CDOIDRangeImpl.create(lowerBound, nextMetaIDValue - 2);
  }

  public void setNextMetaIDValue(long nextMetaIDValue)
  {
    this.nextMetaIDValue = nextMetaIDValue;
  }

  public long getNextMetaIDValue()
  {
    return nextMetaIDValue;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Repository[{0}, {1}]", name, uuid);
  }

  protected TypeManager createTypeManager()
  {
    return new TypeManager(this);
  }

  protected PackageManager createPackageManager()
  {
    return new PackageManager(this);
  }

  protected SessionManager createSessionManager()
  {
    return new SessionManager(this);
  }

  protected ResourceManager createResourceManager()
  {
    return new ResourceManager(this);
  }

  protected RevisionManager createRevisionManager()
  {
    return new RevisionManager(this);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (StringUtil.isEmpty(name))
    {
      throw new IllegalArgumentException("name is null or empty");
    }

    if (store == null)
    {
      throw new IllegalArgumentException("store is null");
    }

    if (isSupportingAudits() && !store.hasAuditingSupport())
    {
      throw new IllegalStateException("Store without auditing support");
    }

    elements = new IRepositoryElement[] { packageManager, sessionManager, resourceManager, revisionManager,
        typeManager, store };
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    activateRepository();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    deactivateRepository();
    super.doDeactivate();
  }

  protected void activateRepository() throws Exception
  {
    LifecycleUtil.activate(store);
    typeManager.setPersistent(!store.hasEfficientTypeLookup());
    typeManager.activate();
    packageManager.activate();
    if (store.hasCrashed())
    {
      store.repairAfterCrash();
    }

    sessionManager.activate();
    resourceManager.activate();
    revisionManager.activate();
  }

  protected void deactivateRepository()
  {
    revisionManager.deactivate();
    resourceManager.deactivate();
    sessionManager.deactivate();
    packageManager.deactivate();
    typeManager.deactivate();
    LifecycleUtil.deactivate(store);
  }
}
