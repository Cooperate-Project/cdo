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
package org.eclipse.emf.cdo.common.util;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Closeable;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOQueryQueue<E> implements Queue<E>, Closeable
{
  private static final long serialVersionUID = 1L;

  private QueueEntry<E> QUEUE_CLOSED = new QueueEntry<E>();

  private PriorityBlockingQueue<QueueEntry<E>> queue;

  private boolean closed = false;

  static class QueueEntry<E> implements Comparable<QueueEntry<E>>
  {
    static AtomicLong nextSeq = new AtomicLong(0);

    private long seqNumber = 0;

    private Object internalObject;

    public QueueEntry()
    {
      seqNumber = Long.MAX_VALUE;
    }

    public QueueEntry(E object)
    {
      internalObject = object;
      seqNumber = nextSeq.getAndIncrement();
    }

    public QueueEntry(Throwable object)
    {
      internalObject = object;
      seqNumber = nextSeq.getAndIncrement();
    }

    public Object getObject()
    {
      return internalObject;
    }

    @SuppressWarnings("unchecked")
    public E getObjectWithException()
    {
      Throwable exception = getException();
      if (exception != null)
      {
        throw WrappedException.wrap((Exception)exception);
      }
      return (E)internalObject;
    }

    public Throwable getException()
    {
      if (internalObject instanceof Throwable)
      {
        return (Throwable)internalObject;
      }
      return null;
    }

    public int compareTo(QueueEntry<E> o)
    {
      if (getException() != null)
      {
        return -1;
      }
      if (o.getException() != null)
      {
        return 1;
      }

      if (this == o)
      {
        return 0;
      }
      return (seqNumber < o.seqNumber ? -1 : 1);
    }
  };

  public class CloseableBlockingIteratorImpl implements BlockingCloseableIterator<E>
  {
    private boolean closed = false;

    private E nextElement = null;

    public E peek()
    {
      if (nextElement == null)
      {
        return CDOQueryQueue.this.peek();
      }
      return nextElement;
    }

    public boolean hasNext()
    {
      privateNext(false);
      return nextElement != null;
    }

    private void privateNext(boolean failOnNull)
    {
      if (nextElement == null)
      {
        try
        {
          if (CDOQueryQueue.this.isEmpty() && CDOQueryQueue.this.isClosed())
          {
            if (failOnNull)
            {
              throw new NoSuchElementException();
            }
            return;
          }

          nextElement = take();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
        }
      }
    }

    public E next()
    {
      try
      {
        privateNext(true);
        return nextElement;
      }
      finally
      {
        nextElement = null;
      }
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }

    public void close()
    {
      this.closed = true;
    }

    public boolean isClosed()
    {
      return this.closed;
    }

  }

  public CDOQueryQueue()
  {
    queue = new PriorityBlockingQueue<QueueEntry<E>>(10);

  }

  public void setException(Throwable exception)
  {
    queue.add(new QueueEntry<E>(exception));

  }

  public void close()
  {
    synchronized (this)
    {
      if (!isClosed())
      {
        closed = true;

        queue.add(QUEUE_CLOSED);
      }
    }
  }

  public boolean isClosed()
  {
    return closed;
  }

  public boolean add(E e)
  {
    return queue.add(new QueueEntry<E>(e));
  }

  @SuppressWarnings("unchecked")
  public boolean addAll(Collection c)
  {
    return queue.addAll(c);
  }

  public void clear()
  {
    queue.clear();
  }

  @SuppressWarnings("unchecked")
  public Comparator comparator()
  {
    return queue.comparator();
  }

  public boolean contains(Object o)
  {
    return queue.contains(o);
  }

  @SuppressWarnings("unchecked")
  public boolean containsAll(Collection c)
  {
    return queue.containsAll(c);
  }

  @SuppressWarnings("unchecked")
  public int drainTo(Collection c, int maxElements)
  {
    return queue.drainTo(c, maxElements);
  }

  @SuppressWarnings("unchecked")
  public int drainTo(Collection c)
  {
    return queue.drainTo(c);
  }

  public E element()
  {
    return checkObject(queue.element());
  }

  public boolean equals(Object obj)
  {
    return queue.equals(obj);
  }

  public int hashCode()
  {
    return queue.hashCode();
  }

  public boolean isEmpty()
  {
    return queue.isEmpty();
  }

  public BlockingCloseableIterator<E> iterator()
  {
    return new CloseableBlockingIteratorImpl();
  }

  public boolean offer(E e, long timeout, TimeUnit unit)
  {
    return queue.offer(new QueueEntry<E>(e), timeout, unit);
  }

  public boolean offer(E e)
  {
    return true;
  }

  public E peek()
  {
    return checkObject(queue.peek());
  }

  public E poll(long timeout, TimeUnit unit) throws InterruptedException
  {
    return checkObject(queue.poll(timeout, unit));
  }

  public void put(E e)
  {
    queue.put(new QueueEntry<E>(e));
  }

  public int remainingCapacity()
  {
    return queue.remainingCapacity();
  }

  public E remove()
  {
    return checkObject(queue.remove());
  }

  public boolean remove(Object o)
  {
    return queue.remove(o);
  }

  @SuppressWarnings("unchecked")
  public boolean removeAll(Collection c)
  {
    return queue.removeAll(c);
  }

  @SuppressWarnings("unchecked")
  public boolean retainAll(Collection c)
  {
    return queue.retainAll(c);
  }

  public int size()
  {
    return queue.size();
  }

  public E take() throws InterruptedException
  {
    QueueEntry<E> entry = null;
    try
    {
      entry = queue.take();
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
    }
    return checkObject(entry);
  }

  public Object[] toArray()
  {
    return queue.toArray();
  }

  @SuppressWarnings("unchecked")
  public Object[] toArray(Object[] a)
  {
    return queue.toArray(a);
  }

  public String toString()
  {
    return queue.toString();
  }

  public E poll()
  {
    QueueEntry<E> entry = queue.poll();
    return checkObject(entry);
  }

  private E checkObject(QueueEntry<E> entry)
  {
    if (entry == QUEUE_CLOSED)
    {
      return null;
    }
    return entry.getObjectWithException();
  }

}
