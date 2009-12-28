/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.common.protocol;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOProtocolConstants
{
  public static final String PROTOCOL_NAME = "cdo"; //$NON-NLS-1$

  // //////////////////////////////////////////////////////////////////////
  // Signal IDs

  public static final short SIGNAL_OPEN_SESSION = 1;

  public static final short SIGNAL_AUTHENTICATION = 2;

  public static final short SIGNAL_VIEWS_CHANGED = 4;

  public static final short SIGNAL_LOAD_PACKAGES = 6;

  public static final short SIGNAL_LOAD_REVISION = 7;

  public static final short SIGNAL_LOAD_REVISION_BY_TIME = 8;

  public static final short SIGNAL_LOAD_REVISION_BY_VERSION = 9;

  public static final short SIGNAL_LOAD_CHUNK = 10;

  public static final short SIGNAL_VERIFY_REVISION = 11;

  public static final short SIGNAL_COMMIT_TRANSACTION = 12;

  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE1 = 13;

  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE2 = 14;

  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE3 = 15;

  public static final short SIGNAL_COMMIT_TRANSACTION_CANCEL = 16;

  public static final short SIGNAL_COMMIT_NOTIFICATION = 17;

  public static final short SIGNAL_QUERY = 18;

  public static final short SIGNAL_QUERY_CANCEL = 19;

  public static final short SIGNAL_SYNC_REVISIONS = 20;

  public static final short SIGNAL_PASSIVE_UPDATE = 21;

  public static final short SIGNAL_CHANGE_SUBSCRIPTION = 22;

  public static final short SIGNAL_SET_AUDIT = 23;

  public static final short SIGNAL_REPOSITORY_TIME = 24;

  public static final short SIGNAL_LOCK_OBJECTS = 25;

  public static final short SIGNAL_UNLOCK_OBJECTS = 26;

  public static final short SIGNAL_OBJECT_LOCKED = 27;

  public static final short SIGNAL_GET_REMOTE_SESSIONS = 28;

  /**
   * @since 3.0
   */
  public static final short SIGNAL_REMOTE_MESSAGE = 29;

  /**
   * @since 3.0
   */
  public static final short SIGNAL_REMOTE_MESSAGE_NOTIFICATION = 30;

  public static final short SIGNAL_UNSUBSCRIBE_REMOTE_SESSIONS = 31;

  public static final short SIGNAL_REMOTE_SESSION_NOTIFICATION = 32;

  // //////////////////////////////////////////////////////////////////////
  // Session Management

  public static final int ERROR_REPOSITORY_NOT_FOUND = -1;

  public static final int ERROR_NO_SESSION = -2;

  // //////////////////////////////////////////////////////////////////////
  // View Management

  public static final byte VIEW_CLOSED = -1;

  // //////////////////////////////////////////////////////////////////////
  // Query Support

  public static final String QUERY_LANGUAGE_RESOURCES = "resources"; //$NON-NLS-1$

  public static final String QUERY_LANGUAGE_RESOURCES_FOLDER_ID = "folder"; //$NON-NLS-1$

  public static final String QUERY_LANGUAGE_RESOURCES_EXACT_MATCH = "exactMatch"; //$NON-NLS-1$

  // //////////////////////////////////////////////////////////////////////
  // Locking Objects

  public static final int RELEASE_ALL_LOCKS = -1;

  // //////////////////////////////////////////////////////////////////////
  // Remote Sessions

  public static final int NO_MORE_REMOTE_SESSIONS = -1;

  public static final byte REMOTE_SESSION_OPENED = 1;

  public static final byte REMOTE_SESSION_CLOSED = 2;

  public static final byte REMOTE_SESSION_SUBSCRIBED = 3;

  public static final byte REMOTE_SESSION_UNSUBSCRIBED = 4;

  /**
   * @since 3.0
   */
  public static final byte REMOTE_SESSION_CUSTOM_DATA = 5;
}
