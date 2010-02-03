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
package org.eclipse.emf.cdo.tests.db;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AutomatedTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite(AutomatedTests.class.getName());
    suite.addTest(AllTestsDBHsqldb.suite());
    suite.addTest(AllTestsDBHsqldbNonAudit.suite());
    suite.addTest(AllTestsDBH2.suite());
    suite.addTest(AllTestsDBH2NonAudit.suite());
    return suite;
  }
}
