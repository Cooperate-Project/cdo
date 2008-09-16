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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.legacy.Hook;
import org.eclipse.emf.cdo.tests.legacy.LegacyFactory;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Eike Stepper
 */
public class LegacyTest extends AbstractCDOTest
{
  public void _testCommit() throws Exception
  {
    Hook hook = createHook("Mr. Hook");

    CDOSession session = openLegacySession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(hook);
    transaction.commit();

    CDOObject cdoHook = CDOUtil.adaptLegacy(hook);
    assertEquals(CDOState.CLEAN, cdoHook.cdoState());
    assertEquals(CDOState.CLEAN, resource.cdoState());
    session.close();
  }

  public void _testLoad() throws Exception
  {
    {
      CDOSession session = openLegacySession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(createHook("Mr. Hook"));
      transaction.commit();
      session.close();
    }

    CDOSession session = openLegacySession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/test1");
    EList<EObject> contents = resource.getContents();

    Hook hook = (Hook)contents.get(0);
    CDOObject cdoHook = CDOUtil.adaptLegacy(hook);
    assertEquals(CDOState.PROXY, cdoHook.cdoState());
    assertEquals(CDOState.CLEAN, resource.cdoState());

    String name = hook.getName();
    assertEquals("Mr. Hook", name);
    assertEquals(CDOState.CLEAN, cdoHook.cdoState());
    session.close();
  }

  public void testReferences() throws Exception
  {
    {
      Hook hook = createHook("Mr. Hook");
      EList<Hook> children = hook.getChildren();
      children.add(createHook("Hook 1"));
      children.add(createHook("Hook 2"));
      children.add(createHook("Hook 3"));

      CDOSession session = openLegacySession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(hook);
      transaction.commit();

      CDOObject cdoHook = CDOUtil.adaptLegacy(hook);
      assertEquals(CDOState.CLEAN, cdoHook.cdoState());
      assertEquals(CDOState.CLEAN, resource.cdoState());
      session.close();
    }

    CDOSession session = openLegacySession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/test1");
    EList<EObject> contents = resource.getContents();

    Hook hook = (Hook)contents.get(0);
    CDOObject cdoHook = CDOUtil.adaptLegacy(hook);
    assertEquals(CDOState.CLEAN, cdoHook.cdoState());

    EList<Hook> children = hook.getChildren();
    assertEquals(CDOState.CLEAN, cdoHook.cdoState());
    assertEquals(CDOState.CLEAN, resource.cdoState());

    Hook h = children.get(0);
    URI proxyURI = ((InternalEObject)h).eProxyURI();
    
    int size = children.size();
    assertEquals(3, size);

    assertEquals(CDOState.CLEAN, cdoHook.cdoState());
    session.close();
  }

  private Hook createHook(String name)
  {
    Hook hook = LegacyFactory.eINSTANCE.createHook();
    hook.setName(name);
    return hook;
  }
}
