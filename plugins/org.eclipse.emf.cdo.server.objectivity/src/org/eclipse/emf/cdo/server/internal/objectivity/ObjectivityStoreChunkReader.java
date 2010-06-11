/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreAccessor;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreChunkReader;
import org.eclipse.emf.cdo.spi.server.StoreChunkReader;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

public class ObjectivityStoreChunkReader extends StoreChunkReader implements IObjectivityStoreChunkReader
{

  public ObjectivityStoreChunkReader(IStoreAccessor accessor, CDORevision revision, EStructuralFeature feature)
  {
    super(accessor, revision, feature);
    // TODO Auto-generated constructor stub
  }

  @Override
  public IObjectivityStoreAccessor getAccessor()
  {
    return (IObjectivityStoreAccessor)super.getAccessor();
  }

  public List<Chunk> executeRead()
  {
    CDOID cdoId = getRevision().getID();
    ObjyObject objyObject = getAccessor().getObject(cdoId);
    ObjyObject objyRevision = objyObject.getRevisionByVersion(getRevision().getVersion());

    List<Chunk> chunks = getChunks();

    for (Chunk chunk : chunks)
    {
      int chunkStartIndex = chunk.getStartIndex();
      int chunkSize = chunk.size();

      // get the data from the feature.
      Object[] objects = objyRevision.fetch((ObjectivityStoreAccessor)getAccessor(), getFeature(), chunkStartIndex,
          chunkSize);
      // although we asked for a chunkSize we might get less.
      for (int i = 0; i < objects.length; i++)
      {
        chunk.add(i, objects[i]);
      }
    }
    return chunks;
  }
}
