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
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.AbstractCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class CDOListImpl extends BasicEList<Object> implements InternalCDOList
{
  public static final CDOListFactory FACTORY = new CDOListFactory()
  {
    public CDOList createList(int initialCapacity, int size, int initialChunk)
    {
      return new CDOListImpl(initialCapacity, size)
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected Object validate(int index, Object object)
        {
          // TTT
          if (revision != null)
          {
            AbstractCDORevision.checkNotDangling(revision, "index=" + index, object);
            if ("CDOResourceFolder".equals(revision.getEClass().getName()))
            {
              if (object == null)
              {
                throw new IllegalArgumentException("Node " + index + " is null: " + revision);
              }
            }

          }

          return super.validate(index, object);
        }
      };
    }
  };

  private static final long serialVersionUID = 1L;

  public CDOListImpl(int initialCapacity, int size)
  {
    super(initialCapacity);
    for (int j = 0; j < size; j++)
    {
      this.add(UNINITIALIZED);
    }
  }

  public InternalCDOList clone(EClassifier classifier)
  {
    CDOType type = CDOModelUtil.getType(classifier);
    int size = size();
    InternalCDOList list = new CDOListImpl(size, 0);
    for (int j = 0; j < size; j++)
    {
      Object value = this.get(j);
      list.add(j, type.copyValue(value));
    }

    return list;
  }

  // TTT
  protected CDORevision revision;

  // TTT
  public void setRevision(CDORevision revision)
  {
    this.revision = revision;
  }

  public Object get(int index, boolean resolve)
  {
    return super.get(index);
  }

  /**
   * There's a duplicate of this method in WrappedHibernateList!!!
   */
  public void applyReferenceAdjuster(CDOReferenceAdjuster revisionAdjuster, EStructuralFeature feature)
  {
    CDOType type = CDOModelUtil.getType(feature);
    int size = size();
    for (int i = 0; i < size; i++)
    {
      Object element = super.get(i);
      handleAdjustReference(i, element);
      Object newID = type.applyReferenceAdjuster(revisionAdjuster, element);
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
