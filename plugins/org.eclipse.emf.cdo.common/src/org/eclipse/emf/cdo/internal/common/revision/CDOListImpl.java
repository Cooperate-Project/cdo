/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.collection.MoveableArrayList;

/**
 * @author Simon McDuff
 */
public class CDOListImpl extends MoveableArrayList<Object> implements InternalCDOList
{
  public static final CDOListFactory FACTORY = new CDOListFactory()
  {
    public CDOList createList(int initialCapacity, int size, int initialChunk)
    {
      return new CDOListImpl(initialCapacity, size);
    }
  };

  private static final long serialVersionUID = 1L;

  public CDOListImpl(int initialCapacity, int size)
  {
    super(initialCapacity);
    for (int j = 0; j < size; j++)
    {
      this.add(InternalCDORevision.UNINITIALIZED);
    }
  }

  public InternalCDOList clone(CDOType type)
  {
    int size = size();
    InternalCDOList list = new CDOListImpl(size, 0);
    for (int j = 0; j < size; j++)
    {
      Object value = this.get(j);
      list.add(j, type.copyValue(value));
    }

    return list;
  }

  public Object get(int index, boolean resolve)
  {
    return super.get(index);
  }

  public void adjustReferences(CDOReferenceAdjuster revisionAdjuster, CDOType type)
  {
    int size = size();
    for (int i = 0; i < size; i++)
    {
      Object element = super.get(i);
      handleAdjustReference(i, element);
      Object newID = type.adjustReferences(revisionAdjuster, element);
      if (newID != element)
      {
        super.set(i, newID);
      }
    }
  }

  protected void handleAdjustReference(int index, Object element)
  {
  }
}
