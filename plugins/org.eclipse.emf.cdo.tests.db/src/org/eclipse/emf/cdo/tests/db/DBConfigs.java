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

import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.AttributeTest;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.AuditTestSameSession;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.BranchingTestSameSession;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.FeatureMapTest;
import org.eclipse.emf.cdo.tests.MEMStoreQueryTest;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.XRefTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259869_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_303807_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class DBConfigs extends AllConfigs
{
  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    testClasses.add(Net4jDBTest.class);
    testClasses.add(DBAnnotationsTest.class);
    testClasses.add(DBStoreTest.class);
    testClasses.add(SQLQueryTest.class);

    super.initTestClasses(testClasses);
    testClasses.remove(MEMStoreQueryTest.class);

    // remove BranchingTests because most mappings do not support it
    // Subclasses should add Banching tests if supported
    if (!hasBranchingSupport())
    {
      testClasses.remove(BranchingTest.class);
      testClasses.remove(BranchingTestSameSession.class);
      testClasses.remove(MergingTest.class);
      testClasses.remove(Bugzilla_303807_Test.class);
    }

    if (!hasAuditSupport())
    {
      // non-audit mode - remove audit tests
      testClasses.remove(AuditTest.class);
      testClasses.remove(AuditTestSameSession.class);
      testClasses.remove(Bugzilla_252214_Test.class);
    }

    // fails because of Bug 284109
    testClasses.remove(XATransactionTest.class);
    testClasses.add(DISABLE_XATransactionTest.class);

    // XXX Range-based audit mapping does not support queryXRefs for now
    testClasses.remove(XRefTest.class);
    testClasses.add(DISABLE_XRefTest.class);

    // ------------ tests below only fail for PostgreSQL
    // ------------ therefore they are overridden and
    // ------------ skipConfig for PSQL is used temporarily
    // XXX [PSQL] disabled because of Bug 289445
    testClasses.remove(AttributeTest.class);
    testClasses.add(DISABLE_AttributeTest.class);

    testClasses.remove(FeatureMapTest.class);
    testClasses.add(DISABLE_FeatureMapTest.class);

    // XXX [PSQL] disabled because of Bug 290095
    // using skipconfig in DBAnnotationTest

    // XXX [PSQL] disabled because of Bug 290097
    testClasses.remove(ExternalReferenceTest.class);
    testClasses.add(DISABLE_ExternalReferenceTest.class);

    // XXX [PSQL] disabled because of Bug 290097
    testClasses.remove(Bugzilla_259869_Test.class);
    testClasses.add(DISABLE_Bugzilla_259869_Test.class);
  }

  protected abstract boolean hasBranchingSupport();

  protected abstract boolean hasAuditSupport();
}
