/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IMEMStore;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.ecore.EObject;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class AbstractSyncingTest extends AbstractCDOTest
{
  protected static final long SLEEP_MILLIS = 100;

  public OfflineConfig getOfflineConfig()
  {
    return (OfflineConfig)getRepositoryConfig();
  }

  @Override
  public boolean isValid()
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    return repositoryConfig instanceof OfflineConfig;
  }

  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(OfflineConfig.PROP_TEST_RAW_REPLICATION, isRawReplication());
    testProperties.put(OfflineConfig.PROP_TEST_SQUEEZE_COMMIT_INFOS, isSqueezedCommitInfos());
    testProperties.put(OfflineConfig.PROP_TEST_DELAYED_COMMIT_HANDLING, getTestDelayedCommitHandling());
    testProperties.put(OfflineConfig.PROP_TEST_FAILOVER, isFailover());
    return testProperties;
  }

  protected boolean isFailover()
  {
    return false;
  }

  protected boolean isRawReplication()
  {
    return false;
  }

  protected boolean isSqueezedCommitInfos()
  {
    return false;
  }

  protected long getTestDelayedCommitHandling()
  {
    return 0L;
  }

  protected static void checkEvent(TestListener listener, int newPackageUnits, int newObjects, int changedObjects,
      int detachedObjects)
  {
    IEvent[] events = listener.getEvents();
    assertEquals(1, events.length);

    IEvent event = events[0];
    if (event instanceof CDOSessionInvalidationEvent)
    {
      CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
      assertEquals(newPackageUnits, e.getNewPackageUnits().size());
      assertEquals(newObjects, e.getNewObjects().size());
      assertEquals(changedObjects, e.getChangedObjects().size());
      assertEquals(detachedObjects, e.getDetachedObjects().size());
    }
    else
    {
      fail("Invalid event: " + event);
    }

    listener.clearEvents();
  }

  protected static void checkRevision(EObject object, InternalRepository repository, String location)
  {
    // Check if revision arrived in cache
    checkRevision(object, repository.getRevisionManager().getCache().getAllRevisions(), location + " cache");

    // Check if revision arrived in store
    InternalStore store = repository.getStore();
    if (store instanceof IMEMStore)
    {
      checkRevision(object, ((IMEMStore)store).getAllRevisions(), location + " store");
    }
  }

  protected static void checkRevision(EObject object, Map<CDOBranch, List<CDORevision>> allRevisions, String location)
  {
    CDORevision revision = CDOUtil.getCDOObject(object).cdoRevision();
    List<CDORevision> revisions = allRevisions.get(revision.getBranch());
    for (CDORevision rev : revisions)
    {
      if (revision.equals(rev))
      {
        return;
      }
    }

    fail("Revision missing from " + location + ": " + revision);
  }

  protected static void checkRevisions(InternalRepository repository, long timeStamp, int expectedRevisions)
      throws InterruptedException
  {
    long lastCommitTimeStamp = repository.getLastCommitTimeStamp();
    while (lastCommitTimeStamp < timeStamp)
    {
      lastCommitTimeStamp = repository.waitForCommit(DEFAULT_TIMEOUT);
    }

    InternalStore store = repository.getStore();
    if (store instanceof MEMStore)
    {
      Map<CDOBranch, List<CDORevision>> allRevisions = ((MEMStore)store).getAllRevisions();
      System.out.println("\n\n\n\n\n\n\n\n\n\n" + CDORevisionUtil.dumpAllRevisions(allRevisions));

      List<CDORevision> revisions = allRevisions.get(repository.getBranchManager().getMainBranch());
      assertEquals(expectedRevisions, revisions.size());
    }
  }

  protected static void waitForOnline(CDOCommonRepository repository)
  {
    while (repository.getState() != CDOCommonRepository.State.ONLINE)
    {
      System.out.println("Waiting for ONLINE...");
      sleep(SLEEP_MILLIS);
    }
  }

  protected static void waitForOffline(CDOCommonRepository repository)
  {
    while (repository.getState() == CDOCommonRepository.State.ONLINE)
    {
      System.out.println("Waiting for OFFLINE...");
      sleep(SLEEP_MILLIS);
    }
  }

  protected static CDOCommitInfo commitAndWaitForArrival(CDOTransaction transaction, CDOSession receiver)
  {
    CDOCommitInfo commitInfo = transaction.commit();
    long timeStamp = commitInfo.getTimeStamp();
    while (receiver.getLastUpdateTime() < timeStamp)
    {
      System.out.println("Waiting for arrival of commit " + CDOCommonUtil.formatTimeStamp(timeStamp));
      sleep(SLEEP_MILLIS);
    }

    return commitInfo;
  }

  protected static CDOCommitInfo commitAndWaitForArrival(CDOTransaction transaction, IRepository receiver)
  {
    CDOCommitInfo commitInfo = transaction.commit();
    long timeStamp = commitInfo.getTimeStamp();
    while (receiver.getLastCommitTimeStamp() < timeStamp)
    {
      System.out.println("Waiting for arrival of commit " + CDOCommonUtil.formatTimeStamp(timeStamp));
      sleep(SLEEP_MILLIS);
    }

    return commitInfo;
  }
}
