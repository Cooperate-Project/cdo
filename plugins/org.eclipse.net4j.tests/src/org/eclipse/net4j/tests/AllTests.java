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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.tests.bugzilla.Bugzilla241463_Test;
import org.eclipse.net4j.util.tests.ExtendedIOTest;
import org.eclipse.net4j.util.tests.MonitorTest;
import org.eclipse.net4j.util.tests.MultiMapTest;
import org.eclipse.net4j.util.tests.ReferenceValueMapTest;
import org.eclipse.net4j.util.tests.SecurityTest;
import org.eclipse.net4j.util.tests.SortedFileMapTest;
import org.eclipse.net4j.util.tests.SynchronizingCorrelatorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Tests for Net4j");
    suite.addTestSuite(MonitorTest.class);
    suite.addTestSuite(MultiMapTest.class);
    suite.addTestSuite(SortedFileMapTest.class);
    suite.addTestSuite(SynchronizingCorrelatorTest.class);
    suite.addTestSuite(ReferenceValueMapTest.class);
    suite.addTestSuite(BufferPoolTest.class);
    suite.addTestSuite(ExtendedIOTest.class);
    suite.addTestSuite(Bugzilla241463_Test.class);
    suite.addTestSuite(ChannelTest.JVM.class);
    suite.addTestSuite(ChannelTest.TCP.class);
    suite.addTestSuite(TCPConnectorTest.class);
    suite.addTestSuite(TransportTest.JVM.class);
    suite.addTestSuite(TransportTest.TCP.class);
    suite.addTestSuite(SignalTest.class);
    suite.addTestSuite(SignalMonitorTest.class);
    suite.addTestSuite(ExceptionTest.class);
    suite.addTestSuite(SecurityTest.class);
    return suite;
  }
}
