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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;

/**
 * @author Eike Stepper
 */
public class SessionTest extends AbstractCDOTest
{
  public void testIsSupportingAudits() throws Exception
  {
    CDOSession session = openModel1Session();
    assertEquals(getRepository().isSupportingAudits(), session.repository().isSupportingAudits());
    session.close();
  }
}
