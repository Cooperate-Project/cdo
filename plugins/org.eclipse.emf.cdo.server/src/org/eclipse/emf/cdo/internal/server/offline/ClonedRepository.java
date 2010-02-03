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
package org.eclipse.emf.cdo.internal.server.offline;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class ClonedRepository extends Repository.Default
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, ClonedRepository.class);

  private MasterInterface masterInterface;

  public ClonedRepository()
  {
  }

  public MasterInterface getMasterInterface()
  {
    return masterInterface;
  }

  public void setMasterInterface(MasterInterface masterInterface)
  {
    checkInactive();
    this.masterInterface = masterInterface;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(masterInterface, "masterInterface");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    masterInterface.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    masterInterface.deactivate();
    super.doDeactivate();
  }
}
