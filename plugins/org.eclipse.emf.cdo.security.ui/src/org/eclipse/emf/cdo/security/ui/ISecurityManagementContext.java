/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.ui;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.security.ui.bundle.OM;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IAdaptable;

/**
 * An optional security-management context that may be provided as an {@linkplain IAdaptable adapter}
 * by the view part to which the "Manage Security" command is contributed.
 */
public interface ISecurityManagementContext
{

  ISecurityManagementContext DEFAULT = new Default();

  /**
   * Obtains a view in which to open the security resource for editing.  If at all possible, this
   * should be a writable {@linkplain CDOTransaction transaction}.  If necessary, implementors are
   * welcome to open a new session logged in as the Administrator for this purpose.
   * 
   * @see #getSecurityResource(CDOView)
   * @see #disconnect(CDOView)
   */
  CDOView connect(CDOSession session);

  /**
   * Releases a {@code view} previously {@linkplain #connect(CDOSession) obtained} from this context.
   * The caller must not attempt to use the {@code view} after this point because in all likelihood
   * it will be closed.
   * 
   * @see #connect(CDOSession)
   */
  void disconnect(CDOView view);

  /**
   * Obtains the resource containing the security model for presentation in the Security Management
   * editor.
   */
  CDOResource getSecurityResource(CDOView view);

  //
  // Nested types
  //

  class Default implements ISecurityManagementContext
  {
    public CDOView connect(CDOSession session)
    {
      if (session.isClosed())
      {
        return null;
      }
      if (User.ADMINISTRATOR.equals(session.getUserID()))
      {
        return session.openTransaction();
      }
      return session.openView();
    }

    public void disconnect(CDOView view)
    {
      view.close();
    }

    public CDOResource getSecurityResource(CDOView view)
    {
      CDOResource result = null;

      try
      {
        result = view.getResource("/security"); //$NON-NLS-1$
      }
      catch (Exception e)
      {
        OM.LOG.warn("Security model resource not available.", e); //$NON-NLS-1$
      }

      return result;
    }
  }
}
