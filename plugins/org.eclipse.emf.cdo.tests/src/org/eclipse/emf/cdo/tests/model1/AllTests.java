/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.model1;


import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Test for org.eclipse.emf.cdo.tests.model1");
    suite.addTestSuite(BasicTest.class);
    suite.addTestSuite(AdapterTest.class);
    suite.addTestSuite(SerializationTest.class);
    suite.addTestSuite(NotificationTest.class);
    suite.addTestSuite(RollbackTest.class);
    suite.addTestSuite(XRefsTest.class);
    suite.addTestSuite(ExtentTest.class);
    suite.addTestSuite(OCLTest.class);
    suite.addTestSuite(Bugzilla154389Test.class);
    return suite;
  }
}
