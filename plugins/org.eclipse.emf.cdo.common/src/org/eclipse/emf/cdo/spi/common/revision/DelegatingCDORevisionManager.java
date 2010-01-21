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
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class DelegatingCDORevisionManager extends Lifecycle implements InternalCDORevisionManager
{
  public DelegatingCDORevisionManager()
  {
  }

  public InternalCDORevisionCache getCache()
  {
    return getDelegate().getCache();
  }

  public void setCache(CDORevisionCache cache)
  {
    getDelegate().setCache(cache);
  }

  public void setFactory(CDORevisionFactory factory)
  {
    getDelegate().setFactory(factory);
  }

  public CDORevisionFactory getFactory()
  {
    return getDelegate().getFactory();
  }

  public RevisionLoader getRevisionLoader()
  {
    return getDelegate().getRevisionLoader();
  }

  public void setRevisionLoader(RevisionLoader revisionLoader)
  {
    getDelegate().setRevisionLoader(revisionLoader);
  }

  public RevisionLocker getRevisionLocker()
  {
    return getDelegate().getRevisionLocker();
  }

  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    getDelegate().setRevisionLocker(revisionLocker);
  }

  public boolean isSupportingBranches()
  {
    return getDelegate().isSupportingBranches();
  }

  public void setSupportingBranches(boolean on)
  {
    getDelegate().setSupportingBranches(on);
  }

  public boolean addRevision(CDORevision revision)
  {
    return getDelegate().addRevision(revision);
  }

  public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    return getDelegate().containsRevision(id, branchPoint);
  }

  public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return getDelegate().containsRevisionByVersion(id, branchVersion);
  }

  public EClass getObjectType(CDOID id)
  {
    return getDelegate().getObjectType(id);
  }

  public CDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk,
      boolean loadOnDemand)
  {
    return getDelegate().getRevisionByVersion(id, branchVersion, referenceChunk, loadOnDemand);
  }

  public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand)
  {
    return getDelegate().getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
  }

  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk,
      int prefetchDepth, boolean loadOnDemand)
  {
    return getDelegate().getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
  }

  public void reviseLatest(CDOID id, CDOBranch branch)
  {
    getDelegate().reviseLatest(id, branch);
  }

  public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
  {
    getDelegate().reviseVersion(id, branchVersion, timeStamp);
  }

  @Override
  protected void doActivate() throws Exception
  {
    if (isDelegatingLifecycle())
    {
      getDelegate().activate();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (isDelegatingLifecycle())
    {
      getDelegate().deactivate();
    }
  }

  protected boolean isDelegatingLifecycle()
  {
    return true;
  }

  protected abstract InternalCDORevisionManager getDelegate();
}
