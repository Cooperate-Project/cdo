/***************************************************************************
 * Copyright (c) 2004 - 2008 Martin Taal and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;

import org.eclipse.net4j.internal.util.collection.MoveableArrayList;

import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;

/**
 * @author Martin Taal
 */

// Howto handle hibernate lists:
// - a new owner: the owner is persisted and its lists are replaced with hibernate
// persistentlist, the hibernate persitentlist will have a delegate (internally) which is the list which was previously
// present in the owner.
// - an existing owner: the owner is read from the db and hibernate will set a persistentlist
// directly
//
// The solution also needs to handle the following:
// - cdo does not have direct java references but stores cdoids in the list while hibernate expects real java object
// references.
// - cdo uses a moveablearraylist and not the standard arraylist
// 
// The solution:
// - never return null when hibernate asks for the current value of the manyreference, always
// return a MoveableArrayList so that hibernate uses that as the delegate, set the MoveableArrayList
public class CDOManyReferenceGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOManyReferenceGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    // check if there is already a persistentcollection
    final PersistentCollection collection = PersistableListHolder.getInstance().getListMapping(target, getCDOFeature());
    if (collection != null)
    {
      return collection;
    }

    // not yet, get the moveablearraylist
    @SuppressWarnings("unchecked")
    MoveableArrayList<Object> list = (MoveableArrayList<Object>)super.get(target);
    if (list == null)
    {
      // TODO: what initial size?
      list = new MoveableArrayList<Object>(10);
      final InternalCDORevision revision = (InternalCDORevision)target;
      revision.setValue(getCDOFeature(), list);
    }

    // wrap the moveablearraylist
    final HibernateMoveableListWrapper wrapper = new HibernateMoveableListWrapper();
    wrapper.setDelegate(list);

    // and return it
    return wrapper;
  }
}
