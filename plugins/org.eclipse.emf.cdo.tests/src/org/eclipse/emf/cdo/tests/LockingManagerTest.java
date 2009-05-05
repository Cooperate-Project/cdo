/**
 * Copyright (c) 2004 - 2009 Eike Stepper() throws Exception{} Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution() throws Exception{} and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.transaction.TransactionException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Simon McDuff
 */
public class LockingManagerTest extends AbstractCDOTest
{
  public void testBasicUpgradeFromReadToWriteLock() throws Exception
  {
    final RWLockManager<Integer, Integer> lockingManager = new RWLockManager<Integer, Integer>();

    Runnable step1 = new Runnable()
    {
      public void run()
      {
        Set<Integer> keys = new HashSet<Integer>();
        keys.add(1);
        try
        {
          lockingManager.lock(RWLockManager.LockType.WRITE, 1, keys, 50000);
        }
        catch (InterruptedException ex)
        {
          fail("Should not have exception");
        }
      }
    };

    ExecutorService executors = Executors.newFixedThreadPool(10);
    Set<Integer> keys = new HashSet<Integer>();
    keys.add(1);
    keys.add(2);
    keys.add(3);
    keys.add(4);

    msg("Context 1 have readlock 1,2,3,4");
    lockingManager.lock(RWLockManager.LockType.READ, 1, keys, 1000);
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.READ, 1, 1));
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.READ, 1, 2));
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.READ, 1, 3));
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.READ, 1, 4));

    keys.clear();
    keys.add(1);
    keys.add(2);
    keys.add(3);
    msg("Context 2 have readlock 1,2,3");
    lockingManager.lock(RWLockManager.LockType.READ, 2, keys, 1000);
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.READ, 2, 1));
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.READ, 2, 2));
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.READ, 2, 3));
    assertEquals(true, lockingManager.hasLockByOthers(RWLockManager.LockType.READ, 2, 1));
    assertEquals(true, lockingManager.hasLockByOthers(RWLockManager.LockType.READ, 1, 1));

    keys.clear();
    keys.add(4);
    msg("Context 1 have readlock 1,2,3,4 and writeLock 4");
    lockingManager.lock(RWLockManager.LockType.WRITE, 1, keys, 1000);
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.READ, 1, 4));
    assertEquals(true, lockingManager.hasLock(RWLockManager.LockType.WRITE, 1, 4));

    keys.clear();
    keys.add(1);
    try
    {
      lockingManager.lock(RWLockManager.LockType.WRITE, 1, keys, 1000);
      fail("Should not have exception");
    }
    catch (RuntimeException expected)
    {
    }

    executors.execute(step1);
    executors.execute(step1);

    Thread.sleep(1000);

    keys.clear();
    keys.add(1);
    keys.add(2);
    keys.add(3);
    lockingManager.unlock(RWLockManager.LockType.READ, 2, keys);
    ITimeOuter timeOuter = new PollingTimeOuter(200, 100)
    {
      @Override
      protected boolean successful()
      {
        return lockingManager.hasLock(RWLockManager.LockType.WRITE, 1, 1);
      }
    };

    assertEquals(false, timeOuter.timedOut());
  }

  public void testBasicWrongUnlock() throws Exception
  {
    final RWLockManager<Integer, Integer> lockingManager = new RWLockManager<Integer, Integer>();
    Set<Integer> keys = new HashSet<Integer>();
    keys.add(1);
    lockingManager.lock(RWLockManager.LockType.READ, 1, keys, 10000);
    lockingManager.unlock(RWLockManager.LockType.READ, 1, keys);
    try
    {
      lockingManager.unlock(RWLockManager.LockType.READ, 1, keys);
      fail("Should have an exception");
    }
    catch (IllegalMonitorStateException exception)
    {
    }
  }

  public void testReadTimeout() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoWriteLock().lock();

    CDOTransaction transaction2 = session.openTransaction();
    CDOObject company2 = transaction2.getObject(cdoCompany.cdoID());

    long start = System.currentTimeMillis();
    assertEquals(false, company2.cdoWriteLock().tryLock());
    assertEquals(true, System.currentTimeMillis() - start < 300);

    start = System.currentTimeMillis();
    assertEquals(false, company2.cdoWriteLock().tryLock(2, TimeUnit.SECONDS));
    assertEquals(true, System.currentTimeMillis() - start > 2000);
  }

  public void testReadLockByOthers() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoReadLock().lock();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource("/res1").getContents().get(0);

    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);
    assertEquals(false, cdoCompany2.cdoWriteLock().isLockedByOthers());
    assertEquals(true, cdoCompany2.cdoReadLock().isLockedByOthers());
  }

  public void testWriteLockByOthers() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoWriteLock().lock();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource("/res1").getContents().get(0);

    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);
    assertEquals(true, cdoCompany2.cdoWriteLock().isLockedByOthers());
    assertEquals(false, cdoCompany2.cdoReadLock().isLockedByOthers());
  }

  public void testWriteLock() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource("/res1").getContents().get(0);

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);

    transaction.lockObjects(Collections.singletonList(cdoCompany), RWLockManager.LockType.WRITE, CDOLock.WAIT);

    try
    {
      transaction2.lockObjects(Collections.singletonList(cdoCompany2), RWLockManager.LockType.WRITE, 1000);
      fail("Should have an exception");
    }
    catch (TimeoutRuntimeException ex)
    {
    }

    company2.setCity("Ottawa");

    try
    {
      transaction2.commit();
      fail("Should have an exception");
    }
    catch (TransactionException exception)
    {
    }
  }

  public void testWriteLockViaObject() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource("/res1").getContents().get(0);

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);

    cdoCompany.cdoWriteLock().lock();

    boolean acquired = cdoCompany2.cdoWriteLock().tryLock(1000L, TimeUnit.MILLISECONDS);
    assertEquals(false, acquired);

    company2.setCity("Ottawa");

    try
    {
      transaction2.commit();
      fail("Should have an exception");
    }
    catch (TransactionException exception)
    {
    }
  }

  public void testWriteLockFromDifferenceTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource("/res1").getContents().get(0);

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);

    transaction.lockObjects(Collections.singletonList(cdoCompany), RWLockManager.LockType.WRITE, CDOLock.WAIT);

    try
    {
      transaction2.lockObjects(Collections.singletonList(cdoCompany2), RWLockManager.LockType.WRITE, 1000);
      fail("Should have an exception");
    }
    catch (TimeoutRuntimeException ex)
    {
    }
  }

  public void testReadLockAndCommitFromDifferenceTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource("/res1").getContents().get(0);
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    transaction.lockObjects(Collections.singletonList(cdoCompany), RWLockManager.LockType.READ, CDOLock.WAIT);
    company2.setCity("Ottawa");

    try
    {
      transaction2.commit();
      fail("Should have an exception");
    }
    catch (TransactionException exception)
    {
    }
  }

  public void testWriteLockAndCommitFromDifferenceTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource("/res1").getContents().get(0);
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    transaction.lockObjects(Collections.singletonList(cdoCompany), RWLockManager.LockType.WRITE, CDOLock.WAIT);
    company2.setCity("Ottawa");

    try
    {
      transaction2.commit();
      fail("Should have an exception");
    }
    catch (TransactionException expected)
    {
      IOUtil.print(expected);
    }
  }

  public void testReadLockAndCommitSameTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoReadLock().lock();

    company.setCity("Ottawa");
    transaction.commit();
  }

  public void testWriteLockAndCommitSameTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoWriteLock().lock();

    company.setCity("Ottawa");
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());
    assertEquals(false, cdoCompany.cdoReadLock().isLocked());

    transaction.commit();

    assertEquals(false, cdoCompany.cdoWriteLock().isLocked());
    assertEquals(false, cdoCompany.cdoReadLock().isLocked());
  }

  public void testWriteLockAndRollback() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoWriteLock().lock();
    company.setCity("Ottawa");

    transaction.rollback();
    assertEquals(false, cdoCompany.cdoWriteLock().isLocked());
  }

  public void testLockUnlock() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    cdoCompany.cdoReadLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(false, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoWriteLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoReadLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoWriteLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoReadLock().unlock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoReadLock().unlock();
    assertEquals(false, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoWriteLock().unlock();
    assertEquals(false, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoWriteLock().unlock();
    assertEquals(false, cdoCompany.cdoReadLock().isLocked());
    assertEquals(false, cdoCompany.cdoWriteLock().isLocked());

    /********************/

    cdoCompany.cdoReadLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(false, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoWriteLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoReadLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoWriteLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoWriteLock().unlock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoWriteLock().unlock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(false, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoReadLock().unlock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());
    assertEquals(false, cdoCompany.cdoWriteLock().isLocked());

    cdoCompany.cdoReadLock().unlock();
    assertEquals(false, cdoCompany.cdoReadLock().isLocked());
    assertEquals(false, cdoCompany.cdoWriteLock().isLocked());
  }

  public void testTransactionClose() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    Repository repo = (Repository)getRepository();
    CDOSession session = openModel1Session();

    CDOTransaction transaction = session.openTransaction();
    IView view = repo.getSessionManager().getSession(session.getSessionID()).getView(transaction.getViewID());
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoReadLock().lock();
    transaction.close();
    assertEquals(false, repo.getLockManager().hasLock(RWLockManager.LockType.READ, view, cdoCompany.cdoID()));
  }

  public void testSessionClose() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    Repository repo = (Repository)getRepository();
    CDOSession session = openModel1Session();

    CDOTransaction transaction = session.openTransaction();
    IView view = repo.getSessionManager().getSession(session.getSessionID()).getView(transaction.getViewID());
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoReadLock().lock();
    session.close();

    sleep(100);
    assertEquals(false, repo.getLockManager().hasLock(RWLockManager.LockType.READ, view, cdoCompany.cdoID()));
  }

  public void testBugzilla_270345() throws Exception
  {
    Company company1 = getModel1Factory().createCompany();
    Company company2 = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    CDOTransaction transaction2 = session.openTransaction();
    CDOResource res = transaction1.getOrCreateResource("/res1");
    res.getContents().add(company1);
    res.getContents().add(company2);
    transaction1.commit();
    CDOObject cdoCompany1 = CDOUtil.getCDOObject(company1);
    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);

    cdoCompany1.cdoWriteLock().lock();
    assertEquals(true, cdoCompany1.cdoWriteLock().isLocked());
    Company companyFrom2 = (Company)transaction2.getObject(cdoCompany2.cdoID());
    companyFrom2.setCity("sss");
    transaction2.commit();
    assertEquals(true, cdoCompany1.cdoWriteLock().isLocked());
  }

  public void testAutoReleaseLockFalse_commit() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();
    transaction.options().setAutoReleaseLocksEnabled(false);

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoWriteLock().lock();
    cdoCompany.cdoReadLock().lock();

    msg("Test with read/write lock");
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());

    company.setCity("Ottawa");
    transaction.commit();
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());

    msg("Clean locks");
    transaction.unlockObjects(null, null);

    msg("Test with read lock");
    cdoCompany.cdoReadLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());

    company.setCity("Toronto");
    transaction.commit();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());

    transaction.options().setAutoReleaseLocksEnabled(true);
    transaction.commit();
    assertEquals(false, cdoCompany.cdoReadLock().isLocked());
  }

  public void testAutoReleaseLockFalse_rollback() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();
    transaction.options().setAutoReleaseLocksEnabled(false);

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoWriteLock().lock();
    cdoCompany.cdoReadLock().lock();

    msg("Test with read/write lock");
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());

    company.setCity("Ottawa");
    transaction.rollback();
    assertEquals(true, cdoCompany.cdoWriteLock().isLocked());
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());

    msg("Clean locks");
    transaction.unlockObjects(null, null);

    msg("Test with read lock");
    cdoCompany.cdoReadLock().lock();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());

    company.setCity("Toronto");
    transaction.rollback();
    assertEquals(true, cdoCompany.cdoReadLock().isLocked());

    transaction.options().setAutoReleaseLocksEnabled(true);
    transaction.rollback();
    assertEquals(false, cdoCompany.cdoReadLock().isLocked());
  }

  public void testWriteLockPerformance() throws Exception
  {
    final int ITERATION = 1000;
    Company company = getModel1Factory().createCompany();

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res1");
    res.getContents().add(company);
    transaction.commit();

    long start = System.currentTimeMillis();
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    // 335-418 locks/sec
    for (int i = 0; i < ITERATION; i++)
    {
      cdoCompany.cdoWriteLock().lock();
    }

    msg("Lock " + ITERATION / ((double)(System.currentTimeMillis() - start) / 1000) + " objects/sec");
  }
}
