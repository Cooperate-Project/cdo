/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.common.protocol.CDOProtocolImpl;
import org.eclipse.emf.cdo.net4j.CDOSessionProtocol;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class CDOClientProtocol extends CDOProtocolImpl implements CDOSessionProtocol
{
  public CDOClientProtocol()
  {
  }

  @Override
  public CDOSession getSession()
  {
    return (CDOSession)super.getSession();
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION:
      return new CommitNotificationIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (!(getInfraStructure() instanceof CDOSession))
    {
      throw new IllegalStateException("No session");
    }
  }
}
