/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.INotificationManager;
import org.eclipse.emf.cdo.server.IStoreWriter.CommitContext;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class NotificationManager extends Lifecycle implements INotificationManager
{
  private Repository repository;

  public NotificationManager(Repository repository)
  {
    this.repository = repository;
  }

  public Repository getRepository()
  {
    return repository;
  }

  public void notifyCommit(Session session, CommitContext commitContext)
  {
    CDORevisionDelta[] arrayOfDeltas = commitContext.getDirtyObjectDeltas();
    CDOID[] arrayOfDetachedObjects = commitContext.getDetachedObjects();
    
    int dirtyIDSize = arrayOfDeltas == null ? 0 : arrayOfDeltas.length;
    int detachedObjectsSize = arrayOfDetachedObjects == null ? 0 : arrayOfDetachedObjects.length;
    
    if (dirtyIDSize > 0 || detachedObjectsSize > 0)
    {
      List<CDOIDAndVersion> dirtyIDs = new ArrayList<CDOIDAndVersion>(dirtyIDSize);
      List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>(dirtyIDSize);
      for (int i = 0; i < dirtyIDSize; i++)
      {
        CDORevisionDelta delta = arrayOfDeltas[i];
        CDOIDAndVersion dirtyIDAndVersion = CDOIDUtil.createIDAndVersion(delta.getID(), delta.getOriginVersion());
        dirtyIDs.add(dirtyIDAndVersion);
        deltas.add(delta);
      }
      List<CDOID> detachedObjects = new ArrayList<CDOID>(detachedObjectsSize);
      for (int i = 0; i < detachedObjectsSize; i++)
      {
        detachedObjects.add(arrayOfDetachedObjects[i]);
      }
      SessionManager sessionManager = repository.getSessionManager();
      sessionManager.handleCommitNotification(commitContext.getTimeStamp(), dirtyIDs, detachedObjects, deltas, session);
    }
  }
}
