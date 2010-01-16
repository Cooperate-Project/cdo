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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class ResourcesQueryHandler implements IQueryHandler
{
  public ResourcesQueryHandler()
  {
  }

  public void executeQuery(final CDOQueryInfo info, final IQueryContext context)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.queryResources(new IStoreAccessor.QueryResourcesContext()
    {
      public CDOBranchPoint getBranchPoint()
      {
        return new CDOBranchPointImpl(context.getBranch(), context.getTimeStamp());
      }

      public CDOID getFolderID()
      {
        return (CDOID)info.getParameters().get(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_FOLDER_ID);
      }

      public String getName()
      {
        return info.getQueryString();
      }

      public boolean exactMatch()
      {
        return (Boolean)info.getParameters().get(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_EXACT_MATCH);
      }

      public int getMaxResults()
      {
        return info.getMaxResults();
      }

      public boolean addResource(CDOID resourceID)
      {
        return context.addResult(resourceID);
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends QueryHandlerFactory
  {
    public Factory()
    {
      super(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES);
    }

    @Override
    public IQueryHandler create(String description) throws ProductCreationException
    {
      return new ResourcesQueryHandler();
    }

    public static IQueryHandler get(IManagedContainer container, String queryLanguage)
    {
      return (IQueryHandler)container.getElement(PRODUCT_GROUP, CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES, null);
    }
  }
}
