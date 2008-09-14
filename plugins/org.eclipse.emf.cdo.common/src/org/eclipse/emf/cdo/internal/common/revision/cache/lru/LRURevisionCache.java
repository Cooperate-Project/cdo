/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision.cache.lru;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.revision.cache.EvictionEventImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LRURevisionCache extends Lifecycle implements CDORevisionCache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, LRURevisionCache.class);

  private Map<CDOID, RevisionHolder> revisions = new HashMap<CDOID, RevisionHolder>();

  private int capacityCurrent;

  private int capacityRevised;

  private LRU currentLRU;

  private LRU revisedLRU;

  public LRURevisionCache()
  {
  }

  public int getCapacityCurrent()
  {
    return capacityCurrent;
  }

  /**
   * Sets the capacity of LRU cache for <em>current</em> revisions. A value of zero disables eviction completely such
   * that the cache will grow indefinetely.
   */
  public void setCapacityCurrent(int capacity)
  {
    capacityCurrent = capacity;
    if (currentLRU != null)
    {
      currentLRU.capacity(capacity);
    }
  }

  public int getCapacityRevised()
  {
    return capacityRevised;
  }

  /**
   * Sets the capacity of LRU cache for old (<em>revised</em>) revisions. A value of zero disables eviction completely
   * such that the cache will grow indefinetely.
   */
  public void setCapacityRevised(int capacity)
  {
    capacityRevised = capacity;
    if (revisedLRU != null)
    {
      revisedLRU.capacity(capacity);
    }
  }

  public List<CDORevision> getRevisions()
  {
    List<CDORevision> currentRevisions = new ArrayList<CDORevision>();
    for (RevisionHolder holder : revisions.values())
    {
      InternalCDORevision revision = holder.getRevision();
      if (revision != null && revision.isCurrent())
      {
        currentRevisions.add(revision);
      }
    }

    return currentRevisions;
  }

  public CDOClass getObjectType(CDOID id)
  {
    RevisionHolder holder = revisions.get(id);
    if (holder == null)
    {
      return null;
    }

    InternalCDORevision revision = holder.getRevision();
    return revision.getCDOClass();
  }

  public synchronized InternalCDORevision getRevision(CDOID id)
  {
    RevisionHolder holder = revisions.get(id);
    InternalCDORevision revision = holder == null ? null : holder.getRevision();
    if (revision == null || !revision.isCurrent())
    {
      return null;
    }

    return revision;
  }

  public synchronized InternalCDORevision getRevisionByTime(CDOID id, long timeStamp)
  {
    RevisionHolder holder = revisions.get(id);
    while (holder != null)
    {
      int indicator = holder.compareTo(timeStamp);
      if (indicator == 1)
      {
        // timeStamp is after holder timeSpan
        holder = holder.getNext();
      }
      else if (indicator == 0)
      {
        // timeStamp is within holder timeSpan
        return holder.getRevision();
      }
      else
      {
        // timeStamp is before holder timeSpan
        break;
      }
    }

    return null;
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, int version)
  {
    RevisionHolder holder = revisions.get(id);
    while (holder != null)
    {
      int holderVersion = holder.getVersion();
      if (holderVersion > version)
      {
        holder = holder.getNext();
      }
      else if (holderVersion == version)
      {
        return holder.getRevision();
      }
      else
      {
        break;
      }
    }

    return null;
  }

  public boolean addRevision(InternalCDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding revision: {0}, created={1,date} {1,time}, revised={2,date} {2,time}, current={3}",
          revision, revision.getCreated(), revision.getRevised(), revision.isCurrent());
    }

    RevisionHolder newHolder = createHolder(revision);
    LRU list = revision.isCurrent() ? currentLRU : revisedLRU;
    list.add((DLRevisionHolder)newHolder);

    int version = revision.getVersion();
    RevisionHolder lastHolder = null;
    RevisionHolder holder = revisions.get(revision.getID());
    while (holder != null)
    {
      int holderVersion = holder.getVersion();
      if (holderVersion > version)
      {
        lastHolder = holder;
        holder = holder.getNext();
      }
      else if (holderVersion == version)
      {
        return false;
      }
      else
      {
        break;
      }
    }

    adjustHolder(revision, newHolder, lastHolder, holder);
    return true;
  }

  public synchronized InternalCDORevision removeRevision(CDOID id, int version)
  {
    InternalCDORevision revision = null;
    RevisionHolder holder = revisions.get(id);
    while (holder != null)
    {
      int holderVersion = holder.getVersion();
      if (holderVersion > version)
      {
        holder = holder.getNext();
      }
      else
      {
        if (holderVersion == version)
        {
          revision = holder.getRevision();
          removeHolder(holder);
        }

        holder = null;
      }
    }

    return revision;
  }

  public synchronized boolean removeCachedRevisions(CDOID id)
  {
    RevisionHolder lookupHolder = revisions.get(id);
    RevisionHolder holder = lookupHolder;
    while (holder != null)
    {
      RevisionHolder nextHolder = holder.getNext();
      removeHolder(holder);
      holder = nextHolder;
    }

    return lookupHolder != null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    currentLRU = new LRU(capacityCurrent);
    revisedLRU = new LRU(capacityRevised);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    currentLRU = null;
    revisedLRU = null;
    super.doDeactivate();
  }

  protected RevisionHolder createHolder(InternalCDORevision revision)
  {
    return new LRURevisionHolder(revision);
  }

  private void adjustHolder(InternalCDORevision revision, RevisionHolder holder, RevisionHolder prevHolder,
      RevisionHolder nextHolder)
  {
    if (prevHolder != null)
    {
      if (nextHolder == null)
      {
        nextHolder = prevHolder.getNext();
      }

      holder.setPrev(prevHolder);
      holder.setNext(nextHolder);
      prevHolder.setNext(holder);
    }
    else
    {
      holder.setNext(nextHolder);
      revisions.put(revision.getID(), holder);
    }

    reviseHolder(holder, nextHolder);
  }

  private void reviseHolder(RevisionHolder holder, RevisionHolder nextHolder)
  {
    if (nextHolder != null)
    {
      nextHolder.setPrev(holder);
      if (holder.isCurrent() && nextHolder.isCurrent())
      {
        currentLRU.remove((DLRevisionHolder)nextHolder);
        revisedLRU.add((DLRevisionHolder)nextHolder);

        InternalCDORevision oldRevision = nextHolder.getRevision();
        if (oldRevision != null)
        {
          oldRevision.setRevised(holder.getCreated() - 1);
        }
      }
    }
  }

  private synchronized void removeHolder(RevisionHolder holder)
  {
    CDOID id = holder.getID();
    RevisionHolder prev = holder.getPrev();
    RevisionHolder next = holder.getNext();
    if (next != null)
    {
      next.setPrev(prev);
    }

    if (prev != null)
    {
      prev.setNext(next);
    }
    else
    {
      if (next != null)
      {
        revisions.put(id, next);
      }
      else
      {
        revisions.remove(id);
      }
    }

    holder.setPrev(null);
    holder.setNext(null);
  }

  /**
   * @author Eike Stepper
   */
  private final class LRU extends LRURevisionList
  {
    public LRU(int capacity)
    {
      super(capacity);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("LRU[size={0}, capacity={1}]", size(), capacity());
    }

    @Override
    protected void evict(LRURevisionHolder holder)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Evicting revision {0}v{1}", holder.getID(), holder.getVersion());
      }

      // Remember some values before the holder may be changed
      InternalCDORevision revision = holder.getRevision();
      boolean revised = !holder.isCurrent();

      super.evict(holder);
      removeHolder(holder);

      if (revision != null)
      {
        if (this == currentLRU && revised)
        {
          addRevision(revision);
        }
        else
        {
          fireEvent(new EvictionEventImpl(LRURevisionCache.this, revision));
        }
      }
    }
  }
}
