/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

/**
 * @author Eike Stepper
 */
public interface IStore extends IRepositoryElement
{
  public void setRepository(IRepository repository);

  public String getStoreType();

  /**
   * Returns if this store supports the writing of modified revisions in terms of deltas.
   * <p>
   * The contract includes that store implementations with write delta support must also implement
   * {@link IStoreWriter#writeRevisionDelta(org.eclipse.emf.cdo.internal.protocol.revision.delta.CDORevisionDeltaImpl) IStoreWriter.writeRevisionDelta(CDORevisionDeltaImpl)}
   * to not throw an <code>UnsupportedOperationException</code>.
   * 
   * @return <code>true</code> if this store supports the writing of modified revisions in terms of deltas,
   *         <code>false</code> otherwise.
   */
  public boolean hasWriteDeltaSupport();

  /**
   * Returns if this store supports the retrieval of historical revisions.
   * <p>
   * The contract includes that store implementations with auditing support must also implement
   * {@link IStoreReader#readRevisionByTime(org.eclipse.emf.cdo.protocol.CDOID, int, long) IStoreReader.readRevisionByTime(CDOID, int, long)}
   * to not throw an <code>UnsupportedOperationException</code>.
   * 
   * @return <code>true</code> if this store supports the retrieval of historical revisions, <code>false</code>
   *         otherwise.
   */
  public boolean hasAuditingSupport();

  /**
   * Returns if this store supports the storage of concurrent revisions in separate branches.
   * <p>
   * Note: This is reserved for future use by the framework. There is currently no support for branching in the
   * framework!
   * 
   * @return <code>true</code> if this store supports the storage of concurrent revisions in separate branches,
   *         <code>false</code> otherwise.
   */
  public boolean hasBranchingSupport();

  /**
   * Returns if this store supports the efficient lookup of object types.
   * <p>
   * The contract includes that store implementations with efficient type lookup support must also implement
   * {@link IStoreReader#readObjectType(org.eclipse.emf.cdo.protocol.CDOID) IStoreReader.readObjectType(CDOID)} in a way
   * that it performs similar to a <code>HashMap</code> or <code>TreeMap</code>.
   * 
   * @return <code>true</code> if this store supports the efficient lookup of object types, <code>false</code>
   *         otherwise.
   */
  public boolean hasEfficientTypeLookup();

  public boolean wasCrashed();

  public void repairAfterCrash();

  /**
   * Returns a reader that can be used to read from this store in the context of the given session.
   * 
   * @param session
   *          The session that should be used as a context for read access or <code>null</code>. The store
   *          implementor is free to interpret and use the session in a manner suitable for him or ignore it at all. It
   *          is meant only as a hint. Implementor can use it as a key into a cache and/or register a
   *          {@link org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter LifecycleEventAdapter} with it to
   *          intercept cleanup on session close. Note however that the session can be <code>null</code>, for example
   *          during startup of the server while the repositories are initialized but before any user session has been
   *          opened.
   * @return a reader that can be used to read from this store in the context of the given session, never
   *         <code>null</code>.
   */
  public IStoreReader getReader(ISession session);

  /**
   * Returns a writer that can be used to write to this store in the context of the given view. The given view is always
   * marked as a transaction.
   * 
   * @param view
   *          The view that must be used as a context for write access. The store implementor is free to interpret and
   *          use the view in a manner suitable for him or ignore it at all. It is meant only as a hint. Implementor can
   *          use it as a key into a cache and/or register a
   *          {@link org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter LifecycleEventAdapter} with it to
   *          intercept cleanup on view close.
   * @return a writer that can be used to write to this store in the context of the given view, never <code>null</code>.
   */
  public IStoreWriter getWriter(IView view);
}
