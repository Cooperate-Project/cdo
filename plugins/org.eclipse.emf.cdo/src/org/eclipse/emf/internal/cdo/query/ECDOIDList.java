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

package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.common.util.EList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Simon McDuff
 */
public class ECDOIDList<T> implements EList<T>
{
	private List<Object> listOfIDs;
	
	private CDOView cdoView;

	public ECDOIDList(CDOView view, List<Object> list)
	{
		this.listOfIDs = list;
		this.cdoView = view;
	}
  @SuppressWarnings("unchecked")
	protected T adapt(CDOID object)
	{
		if (object.isNull())
			return null;

		return (T)cdoView.getObject((CDOID)object, true);
	}

	public boolean add(T o)
	{
		throw new UnsupportedOperationException();
	}

	public void add(int index, T element)
	{
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public T get(int index)
	{
		Object object = this.listOfIDs.get(index);
		if (object instanceof CDOID)
		{
			object = adapt((CDOID)object);
			// replace the old object with the new
			listOfIDs.set(index, object);
		}
		return (T)object;
	}

	public boolean isEmpty()
	{
		return listOfIDs.isEmpty();
	}
	
	class ECDOIDListIterator implements Iterator<T>
  {
    ListIterator<Object> iterator;

    ECDOIDListIterator(ListIterator<Object> itr)
    {
      this.iterator = itr;
    }

    public boolean hasNext()
    {
      return iterator.hasNext();
    }
    @SuppressWarnings("unchecked")
    public T next()
    {
      Object object = iterator.next();
      if (object instanceof CDOID)
      {
        object = adapt((CDOID)object);
        // replace the old object with the new
        iterator.set(object);
      }
      return (T)object;
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }

  }

	public Iterator<T> iterator()
	{
		return new ECDOIDListIterator(this.listOfIDs.listIterator());
	}
  public void move(int newPosition, T object)
  {
  }
  public T move(int newPosition, int oldPosition)
  {
    return null;
  }
  public boolean addAll(Collection<? extends T> arg0)
  {
    return false;
  }
  public boolean addAll(int arg0, Collection<? extends T> arg1)
  {
    return false;
  }
  public void clear()
  {
  }
  public boolean contains(Object arg0)
  {
    return false;
  }
  public boolean containsAll(Collection<?> arg0)
  {
    return false;
  }
  public int indexOf(Object arg0)
  {
    return 0;
  }
  public int lastIndexOf(Object arg0)
  {
    return 0;
  }
  public ListIterator<T> listIterator()
  {
    return null;
  }
  public ListIterator<T> listIterator(int arg0)
  {
    return null;
  }
  public boolean remove(Object arg0)
  {
    return false;
  }
  public T remove(int arg0)
  {
    return null;
  }
  public boolean removeAll(Collection<?> arg0)
  {
    return false;
  }
  public boolean retainAll(Collection<?> arg0)
  {
    return false;
  }
  public T set(int arg0, T arg1)
  {
    return null;
  }
  public int size()
  {
    return 0;
  }
  public List<T> subList(int arg0, int arg1)
  {
    return null;
  }
  public Object[] toArray()
  {
    return null;
  }
  public <T> T[] toArray(T[] arg0)
  {
    return null;
  }


}
