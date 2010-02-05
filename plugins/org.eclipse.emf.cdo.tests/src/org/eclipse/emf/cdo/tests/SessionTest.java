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

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.UserManager;

/**
 * @author Eike Stepper
 */
public class SessionTest extends AbstractCDOTest
{
  private static final String USER_ID = "stepper"; //$NON-NLS-1$

  private static final char[] PASSWORD1 = "eike2007".toCharArray(); //$NON-NLS-1$

  private static final char[] PASSWORD2 = "invalid".toCharArray(); //$NON-NLS-1$

  public void testIsSupportingAudits() throws Exception
  {
    CDOSession session = openSession();
    boolean serverAudits = getRepository().isSupportingAudits();
    boolean clientAudits = session.getRepositoryInfo().isSupportingAudits();
    assertEquals(serverAudits, clientAudits);
    session.close();
  }

  public void testLastUpdateLocal() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.createResource("ttt");
    long commitTime = transaction.commit().getTimeStamp();

    waitForUpdate(commitTime, session);
    session.close();
  }

  public void testLastUpdateRemote() throws Exception
  {
    CDOSession session1 = openSession();
    final CDOTransaction transaction = session1.openTransaction();
    transaction.createResource("ttt");
    long commitTime1 = transaction.commit().getTimeStamp();

    final CDOSession session2 = openSession();
    waitForUpdate(commitTime1, session2);

    transaction.createResource("xxx");
    long commitTime2 = transaction.commit().getTimeStamp();
    waitForUpdate(commitTime2, session2);

    session1.close();
    session2.close();
  }

  private void waitForUpdate(final long commitTime, final CDOSession session) throws InterruptedException
  {
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return commitTime == session.getLastUpdateTime();
      }
    }.assertNoTimeOut();
  }

  public void testWaitForUpdateLocal() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.createResource("ttt");
    long commitTime = transaction.commit().getTimeStamp();

    assertEquals(true, session.waitForUpdate(commitTime, DEFAULT_TIMEOUT));
    session.close();
  }

  public void testWaitForUpdateRemote() throws Exception
  {
    final CDOTransaction transaction = openSession().openTransaction();
    transaction.createResource("ttt");

    new Thread()
    {
      @Override
      public void run()
      {
        ConcurrencyUtil.sleep(4000);
        msg("Committing NOW!");
        transaction.commit();
      }
    }.start();

    CDOSession session2 = openSession();
    assertEquals(true, session2.waitForUpdate(System.currentTimeMillis() + 2000L, DEFAULT_TIMEOUT));

    transaction.getSession().close();
    session2.close();
  }

  public void testNoAuthentication() throws Exception
  {
    IRepository repository = getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD1)));

    CDOSession session = openSession("authrepo");
    assertEquals(null, session.getUserID());
    assertEquals(null, repository.getSessionManager().getSessions()[0].getUserID());
    session.close();
  }

  public void testWithAuthentication() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    IRepository repository = getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD1)));

    CDOSession session = openSession("authrepo");
    assertEquals(USER_ID, session.getUserID());
    assertEquals(USER_ID, repository.getSessionManager().getSessions()[0].getUserID());
    session.close();
  }

  public void testWithAuthenticationNoCredentialsProvider() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }

  public void testWithAuthenticationNoCredentials() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(null));

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }

  public void testWithAuthenticationWrongCredentials() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD2)));

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }

  public void testWithAuthenticationNoUserID() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(null, PASSWORD2)));

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }

  public void testWithAuthenticationNoPassword() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, null)));

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }
}
