/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDORevisionPrefetchingPolicyImpl implements CDORevisionPrefetchingPolicy
{
  private int chunkSize;

  public CDORevisionPrefetchingPolicyImpl(int chunkSize)
  {
    this.chunkSize = chunkSize;
  }

  public Collection<CDOID> loadAhead(CDORevisionManager revisionManager, int branchID, long timeStamp, EObject eObject,
      EStructuralFeature feature, CDOList list, int accessIndex, CDOID accessID)
  {
    if (chunkSize > 1 && !revisionManager.containsRevision(accessID, branchID, timeStamp))
    {
      int fromIndex = accessIndex;
      int toIndex = Math.min(accessIndex + chunkSize, list.size()) - 1;

      Set<CDOID> notRegistered = new HashSet<CDOID>();
      for (int i = fromIndex; i <= toIndex; i++)
      {
        Object element = list.get(i);
        if (element instanceof CDOID)
        {
          CDOID idElement = (CDOID)element;
          if (!idElement.isTemporary())
          {
            if (!revisionManager.containsRevision(idElement, branchID, timeStamp))
            {
              if (!notRegistered.contains(idElement))
              {
                notRegistered.add(idElement);
              }
            }
          }
        }
      }

      return notRegistered;
    }

    return Collections.emptySet();
  }
}
