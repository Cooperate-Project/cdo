/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

/**
 * @author Eike Stepper
 */
public class ResourceTest extends AbstractCDOTest
{
  public void testAttachDetachResourceDepth1_Delete() throws Exception
  {
    attachDetachResourceDepth1(1, true, 0);
  }

  public void testAttachDetachResourceDepth1_Remove() throws Exception
  {
    attachDetachResourceDepth1(1, false, 0);
  }

  public void testAttachDetachResourceDepth2_Delete() throws Exception
  {
    attachDetachResourceDepth1(2, true, 1);
  }

  public void testAttachDetachResourceDepth2_Remove() throws Exception
  {
    attachDetachResourceDepth1(2, false, 1);
  }

  public void testAttachDetachResourceDepth3_Delete() throws Exception
  {
    attachDetachResourceDepth1(3, true, 2);
  }

  public void testAttachDetachResourceDepth3_Remove() throws Exception
  {
    attachDetachResourceDepth1(3, false, 2);
  }

  public void testAttachDetachResourceDepth3_Remove_Tree() throws Exception
  {
    attachDetachResourceDepth1(3, false, 1);
  }

  public void testRootResourceFromURI() throws Exception
  {
    URI rootResourceURI = null;
    URI resourceURI = null;
    String resourcePath = "test1";
    {
      CDOSession session = openModel1Session();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      resourceURI = URI.createURI("cdo:/" + resourcePath);
      Resource res1 = resourceSet.createResource(resourceURI);

      transaction.commit();
      rootResourceURI = EcoreUtil.getURI(transaction.getRootResource());
      resourceURI = EcoreUtil.getURI((EObject)res1);
    }

    CDOSession session = openModel1Session();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);
    CDOResource rootResource = (CDOResource)resourceSet.getEObject(rootResourceURI, true);
    assertProxy(rootResource);
    assertSame(rootResource, transaction.getRootResource());

    CDOResource resource = (CDOResource)resourceSet.getEObject(resourceURI, true);
    assertClean(resource, transaction);
    assertSame(resource, transaction.getResource(resourcePath));

    transaction.close();
    session.close();
  }

  public void testCreateResource_FromResourceSet() throws Exception
  {
    CDOSession session = openModel1Session();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    final URI uri = URI.createURI("cdo:/test1");
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertActive(resource);
    assertNew(resource, transaction);
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(CDOURIUtil.createResourceURI(session, "test1"), resource.getURI());
    assertEquals("test1", resource.getName());
    assertEquals(null, resource.getFolder());

    transaction.getRootResource().getContents().contains(resource);
    transaction.commit();

    CDOObject cdoResource = CDOUtil.getCDOObject(resource);
    CDOObject cdoRootResource = CDOUtil.getCDOObject(transaction.getRootResource());
    assertClean(cdoResource, transaction);
    assertClean(cdoRootResource, transaction);
    assertEquals(CDOID.NULL, cdoResource.cdoRevision().data().getContainerID());
    assertEquals(cdoRootResource.cdoID(), cdoResource.cdoRevision().data().getResourceID());
    assertEquals(cdoRootResource.cdoID(), cdoRootResource.cdoRevision().data().getResourceID());
    assertEquals(true, transaction.getResourceSet().getResources().contains(resource));
    assertEquals(true, transaction.getResourceSet().getResources().contains(transaction.getRootResource()));

    transaction.getRootResource().getContents().remove(resource);
    assertEquals(false, transaction.getResourceSet().getResources().contains(resource));
    assertEquals(true, transaction.getResourceSet().getResources().contains(transaction.getRootResource()));

  }

  public void testCreateNestedResource_FromResourceSet() throws Exception
  {
    CDOSession session = openModel1Session();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    final URI uri = URI.createURI("cdo:/folder/test1");
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertActive(resource);
    assertNew(resource, transaction);
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(CDOURIUtil.createResourceURI(session, "folder/test1"), resource.getURI());
    assertEquals("test1", resource.getName());

    CDOResourceFolder folder = resource.getFolder();
    assertNotNull(folder);
    assertEquals("folder", folder.getName());
    assertEquals(null, folder.getFolder());
  }

  public void testCreateResource_FromTransaction() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    // Test if Resource is well-formed after CDOResourceFactoryImpl.
    // Adapter will be called right after and could be used!
    transaction.getResourceSet().eAdapters().add(new TestAdapter());

    CDOResource resource = transaction.createResource("/test1");
    assertActive(resource);

    CDOResource resourceCopy = transaction.getOrCreateResource("/test1");
    assertEquals(resource, resourceCopy);
    assertNew(resource, transaction);
    assertEquals(CDOURIUtil.createResourceURI(session, "test1"), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
  }

  public void testCreateResource_WithDeepPath() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/org/eclipse/net4j/core");
      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/org/eclipse/net4j/core");
    assertEquals(CDOURIUtil.createResourceURI(session, "/org/eclipse/net4j/core"), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    session.close();
  }

  public void testLoadAbsentResource_FromResourceSet() throws Exception
  {
    CDOSession session = openModel1Session();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    final URI uri = URI.createURI("cdo:/test1");
    CDOResource resource = (CDOResource)resourceSet.getResource(uri, false);
    assertEquals(null, resource);

    try
    {
      resourceSet.getResource(uri, true);
    }
    catch (Exception ignore)
    {
    }

    transaction.close();
  }

  public void testRemoveResourceWithCloseView() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      ResourceSet rset = transaction.getResourceSet();
      CDOResource resource = transaction.createResource("/test1");
      assertActive(resource);

      transaction.commit();
      Assert.assertEquals(2, rset.getResources().size());
      Assert.assertEquals(1, CDOUtil.getViewSet(rset).getViews().length);

      transaction.close();
      Assert.assertEquals(0, CDOUtil.getViewSet(rset).getViews().length);
      Assert.assertEquals(0, rset.getResources().size());
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = (CDOResource)transaction.getResourceSet().getResource(
          CDOURIUtil.createResourceURI(transaction, "/test1"), true);
      assertNotNull(resource);
      assertEquals(transaction.getResourceSet(), resource.getResourceSet());
      assertEquals(1, transaction.getResourceSet().getResources().size());
      assertEquals(CDOState.PROXY, resource.cdoState());
      assertEquals(transaction, resource.cdoView());
      assertNull(resource.cdoRevision());
    }
  }

  public void testAttachManyResources() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("/my/resource1");
    CDOResource resource2 = transaction.createResource("/my/resource2");
    CDOResource resource3 = transaction.createResource("/my/resource3");

    List<Resource> tobeRemoved = new ArrayList<Resource>();
    tobeRemoved.add(resource1);
    tobeRemoved.add(resource3);
    assertEquals(4, transaction.getResourceSet().getResources().size());

    transaction.getResourceSet().getResources().removeAll(tobeRemoved);
    assertEquals(2, transaction.getResourceSet().getResources().size());
    assertEquals(null, transaction.getResourceSet().getResource(resource1.getURI(), false));
    assertEquals(resource2, transaction.getResourceSet().getResource(resource2.getURI(), false));
    assertEquals(null, transaction.getResourceSet().getResource(resource3.getURI(), false));

    transaction.getResourceSet().getResources().addAll(tobeRemoved);
    assertEquals(4, transaction.getResourceSet().getResources().size());
    assertEquals(resource1, transaction.getResourceSet().getResource(resource1.getURI(), false));
    assertEquals(resource2, transaction.getResourceSet().getResource(resource2.getURI(), false));
    assertEquals(resource3, transaction.getResourceSet().getResource(resource3.getURI(), false));

    transaction.commit();
    session.close();
  }

  public void testDetachManyResources() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("/my/resource1");
    CDOResource resource2 = transaction.createResource("/my/resource2");
    CDOResource resource3 = transaction.createResource("/my/resource3");

    List<Resource> tobeRemoved = new ArrayList<Resource>();
    tobeRemoved.add(resource1);
    tobeRemoved.add(resource3);
    assertEquals(4, transaction.getResourceSet().getResources().size());

    transaction.getResourceSet().getResources().removeAll(tobeRemoved);
    assertEquals(2, transaction.getResourceSet().getResources().size());
    assertEquals(null, transaction.getResourceSet().getResource(resource1.getURI(), false));
    assertEquals(resource2, transaction.getResourceSet().getResource(resource2.getURI(), false));
    assertEquals(null, transaction.getResourceSet().getResource(resource3.getURI(), false));

    transaction.commit();
    session.close();
  }

  public void testCommitMultipleResources()
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/level1/level2-A/level3");
      transaction.createResource("/level1/level2-B/level3");
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.getResource("/level1/level2-A/level3");
    CDOResource resource2 = transaction.getResource("/level1/level2-B/level3");
    assertEquals("/level1/level2-A/level3", resource1.getPath());
    assertEquals("/level1/level2-B/level3", resource2.getPath());
    session.close();
  }

  public void testLoadMultipleResources()
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/level1/level2-A/level3");
      transaction.createResource("/level1/level2-B/level3");
      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.getResource("/level1/level2-A/level3");
    CDOResource resource2 = transaction.getResource("/level1/level2-B/level3");
    assertEquals("/level1/level2-A/level3", resource1.getPath());
    assertEquals("/level1/level2-B/level3", resource2.getPath());
    session.close();
  }

  public void testDuplicatePath() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    transaction.createResource("/my/resource");
    transaction.commit();

    transaction.createResource("/my/resource");

    try
    {
      transaction.commit();
      fail("TransactionException expected");
    }
    catch (TransactionException expected)
    {
      // Success
    }
    finally
    {
      session.close();
    }
  }

  public void testDuplicatePathAfterDetach() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");
    transaction.commit();

    resource.delete(null);
    transaction.commit();

    transaction.createResource("/my/resource");
    transaction.commit();
    session.close();
  }

  public void testChangePathFromDepth0ToDepth0() throws Exception
  {
    changePath(0, 0);
  }

  public void testChangePathFromDepth0ToDepth1() throws Exception
  {
    changePath(0, 1);
  }

  public void testChangePathFromDepth0ToDepth2() throws Exception
  {
    changePath(0, 2);
  }

  public void testChangePathFromDepth0ToDepth3() throws Exception
  {
    changePath(0, 3);
  }

  public void testChangePathFromDepth3ToDepth3() throws Exception
  {
    changePath(3, 3);
  }

  public void testChangePathFromDepth3ToDepth2() throws Exception
  {
    changePath(3, 2);
  }

  public void testChangePathFromDepth3ToDepth1() throws Exception
  {
    changePath(3, 1);
  }

  public void testChangePathFromDepth3ToDepth0() throws Exception
  {
    changePath(3, 0);
  }

  public void testChangeResourceURI() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      transaction.commit();

      URI uri = URI.createURI("cdo://repo1/renamed");
      assertEquals(CDOURIUtil.createResourceURI(session, "/renamed"), uri);
      resource.setURI(uri);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    assertFalse(transaction.hasResource("/my/resource"));
    assertTrue(transaction.hasResource("/renamed"));
  }

  public void testChangeResourceFolderURI() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      transaction.commit();

      URI uri = URI.createURI("cdo://repo1/renamed");
      assertEquals(CDOURIUtil.createResourceURI(session, "/renamed"), uri);
      resource.setURI(uri);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    assertFalse(transaction.hasResource("/my/resource"));
    assertTrue(transaction.hasResource("/renamed"));
  }

  public void testPathNotNull() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
      assertEquals("/res1", resource.getPath());
      assertEquals(CDOURIUtil.createResourceURI(session, "/res1"), resource.getURI());

      transaction.commit();
      assertEquals("/res1", resource.getPath());
      assertEquals(CDOURIUtil.createResourceURI(session, "/res1"), resource.getURI());
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/res1");
      assertEquals("/res1", resource.getPath());
      assertEquals(CDOURIUtil.createResourceURI(session, "/res1"), resource.getURI());

      CDOResource resource2 = transaction.getOrCreateResource("/res2");
      assertEquals("/res2", resource2.getPath());
      assertEquals(CDOURIUtil.createResourceURI(session, "/res2"), resource2.getURI());

      transaction.commit();
      assertEquals("/res2", resource2.getPath());
      assertEquals(CDOURIUtil.createResourceURI(session, "/res2"), resource2.getURI());
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOView view = session.openView();
      CDOResource resource2 = view.getResource("/res2");
      assertEquals("/res2", resource2.getPath());
      assertEquals(CDOURIUtil.createResourceURI(session, "/res2"), resource2.getURI());
      session.close();
    }
  }

  /**
   * http://bugs.eclipse.org/208689
   */
  public void testQueryResources() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      createResource(transaction, "/aresource");
      createResource(transaction, "/aaresource");
      createResource(transaction, "/abresource");
      createResource(transaction, "/acresource");
      createResource(transaction, "/adresource");
      createResource(transaction, "/aeresource");
      createResource(transaction, "/bresource");
      createResource(transaction, "/baresource");
      createResource(transaction, "/bbresource");
      createResource(transaction, "/bcresource");
      createResource(transaction, "/bdresource");
      createResource(transaction, "/beresource");
      createResource(transaction, "/bearesource");
      createResource(transaction, "/bebresource");
      createResource(transaction, "/cresource");
      createResource(transaction, "/caresource");
      createResource(transaction, "/caresource2");
      createResource(transaction, "/caresource3");
      createResource(transaction, "/cbresource");
      createResource(transaction, "/ccresource");
      createResource(transaction, "/cdresource");
      createResource(transaction, "/ceresource");
      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    queryResources(view, "a", 6);
    queryResources(view, "b", 8);
    queryResources(view, "c", 8);
    queryResources(view, "be", 3);
    queryResources(view, "ca", 3);
    session.close();
  }

  /**
   * http://bugs.eclipse.org/208689
   */
  public void testQueryModifiedResources() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      createResource(transaction, "/aresource");
      createResource(transaction, "/aaresource");
      createResource(transaction, "/abresource");
      createResource(transaction, "/acresource");
      createResource(transaction, "/adresource");
      createResource(transaction, "/aeresource");
      createResource(transaction, "/bresource");
      createResource(transaction, "/baresource");
      createResource(transaction, "/bbresource");
      createResource(transaction, "/bcresource");
      createResource(transaction, "/bdresource");
      createResource(transaction, "/beresource");
      createResource(transaction, "/bearesource");
      createResource(transaction, "/bebresource");
      createResource(transaction, "/cresource");
      createResource(transaction, "/caresource");
      createResource(transaction, "/caresource2");
      createResource(transaction, "/caresource3");
      createResource(transaction, "/cbresource");
      createResource(transaction, "/ccresource");
      createResource(transaction, "/cdresource");
      createResource(transaction, "/ceresource");
      transaction.commit();
      modifyResource(transaction, "/aresource");
      modifyResource(transaction, "/aaresource");
      modifyResource(transaction, "/abresource");
      modifyResource(transaction, "/acresource");
      modifyResource(transaction, "/adresource");
      modifyResource(transaction, "/aeresource");
      modifyResource(transaction, "/bresource");
      modifyResource(transaction, "/baresource");
      modifyResource(transaction, "/bbresource");
      modifyResource(transaction, "/bcresource");
      modifyResource(transaction, "/bdresource");
      modifyResource(transaction, "/beresource");
      modifyResource(transaction, "/bearesource");
      modifyResource(transaction, "/bebresource");
      modifyResource(transaction, "/cresource");
      modifyResource(transaction, "/caresource");
      modifyResource(transaction, "/caresource2");
      modifyResource(transaction, "/caresource3");
      modifyResource(transaction, "/cbresource");
      modifyResource(transaction, "/ccresource");
      modifyResource(transaction, "/cdresource");
      modifyResource(transaction, "/ceresource");
      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    queryResources(view, "a", 6);
    queryResources(view, "b", 8);
    queryResources(view, "c", 8);
    queryResources(view, "be", 3);
    queryResources(view, "ca", 3);
    session.close();
  }

  public void testDeleteResource() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = createResource(transaction, "/resource1");
    CDOID resourceID = resource.cdoID();

    CDOObject object = (CDOObject)resource.getContents().get(0);
    CDOID objectID = object.cdoID();

    transaction.commit();
    resource.delete(null);
    transaction.commit();
    transaction.close();

    CDOView view = session.openView();
    assertEquals(false, view.hasResource("/resource1"));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResource("/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    session.close();
  }

  public void testDeleteResourceFresh() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = createResource(transaction, "/resource1");
    CDOID resourceID = resource.cdoID();

    CDOObject object = (CDOObject)resource.getContents().get(0);
    CDOID objectID = object.cdoID();

    transaction.commit();
    resource.delete(null);
    transaction.commit();
    transaction.close();
    clearCache(getRepository().getRevisionManager());

    CDOView view = session.openView();
    assertEquals(false, view.hasResource("/resource1"));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResource("/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    session.close();
  }

  public void testDeleteResourceDifferentSession() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource resource = createResource(transaction, "/resource1");
    CDOID resourceID = resource.cdoID();

    CDOObject object = (CDOObject)resource.getContents().get(0);
    CDOID objectID = object.cdoID();

    transaction.commit();
    assertEquals(true, view.hasResource("/resource1"));
    assertEquals(resource.getURI(), view.getResource("/resource1").getURI());

    resource.delete(null);
    transaction.commit();
    transaction.close();

    assertEquals(false, view.hasResource("/resource1"));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResource("/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    session.close();
  }

  public void testDeleteResourceDifferentSessionFresh() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource resource = createResource(transaction, "/resource1");
    CDOID resourceID = resource.cdoID();

    CDOObject object = (CDOObject)resource.getContents().get(0);
    CDOID objectID = object.cdoID();

    transaction.commit();
    assertEquals(true, view.hasResource("/resource1"));
    assertEquals(resource.getURI(), view.getResource("/resource1").getURI());

    resource.delete(null);
    transaction.commit();
    transaction.close();
    clearCache(getRepository().getRevisionManager());

    assertEquals(false, view.hasResource("/resource1"));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResource("/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    session.close();
  }

  public void testDeleteResourceFolder() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = createResource(transaction, "/folder/resource1");
    CDOObject object = (CDOObject)resource.getContents().get(0);
    transaction.commit();

    CDOResourceFolder folder = resource.getFolder();
    CDOID folderID = folder.cdoID();
    CDOID resourceID = resource.cdoID();
    CDOID objectID = object.cdoID();

    folder.delete(null);
    transaction.commit();
    transaction.close();

    CDOView view = session.openView();
    assertEquals(false, view.hasResource("/folder/resource1"));

    try
    {
      view.getResourceNode("/folder");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResourceNode("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResource("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getObject(folderID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    session.close();
  }

  public void testDeleteResourceFolderFresh() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = createResource(transaction, "/folder/resource1");
    CDOObject object = (CDOObject)resource.getContents().get(0);
    transaction.commit();

    CDOResourceFolder folder = resource.getFolder();
    CDOID folderID = folder.cdoID();
    CDOID resourceID = resource.cdoID();
    CDOID objectID = object.cdoID();

    folder.delete(null);
    transaction.commit();
    transaction.close();
    clearCache(getRepository().getRevisionManager());

    CDOView view = session.openView();
    assertEquals(false, view.hasResource("/folder/resource1"));

    try
    {
      view.getResourceNode("/folder");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResourceNode("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResource("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getObject(folderID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    session.close();
  }

  public void testDeleteResourceFolderDifferentSession() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource resource = createResource(transaction, "/folder/resource1");
    CDOResourceFolder folder = resource.getFolder();
    CDOObject object = (CDOObject)resource.getContents().get(0);

    transaction.commit();
    assertEquals(true, view.hasResource("/folder/resource1"));
    assertEquals(resource.getURI(), view.getResource("/folder/resource1").getURI());

    CDOID folderID = folder.cdoID();
    CDOID resourceID = resource.cdoID();
    CDOID objectID = object.cdoID();

    folder.delete(null);
    transaction.commit();
    transaction.close();

    assertEquals(false, view.hasResource("/folder/resource1"));

    try
    {
      view.getResourceNode("/folder");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResourceNode("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResource("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getObject(folderID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    session.close();
  }

  public void testDeleteResourceFolderDifferentSessionFresh() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource resource = createResource(transaction, "/folder/resource1");
    CDOResourceFolder folder = resource.getFolder();
    CDOObject object = (CDOObject)resource.getContents().get(0);

    transaction.commit();
    assertEquals(true, view.hasResource("/folder/resource1"));
    assertEquals(resource.getURI(), view.getResource("/folder/resource1").getURI());

    CDOID folderID = folder.cdoID();
    CDOID resourceID = resource.cdoID();
    CDOID objectID = object.cdoID();

    folder.delete(null);
    transaction.commit();
    transaction.close();
    clearCache(getRepository().getRevisionManager());

    assertEquals(false, view.hasResource("/folder/resource1"));

    try
    {
      view.getResourceNode("/folder");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResourceNode("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getResource("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception success)
    {
    }

    try
    {
      view.getObject(folderID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException success)
    {
    }

    session.close();
  }

  /**
   * Create resource with the following pattern /test1/test2/test3 for a depth 3. <br>
   * After it will remove the resource with the following rule:<br>
   * if calldelete is true <code>resource.delete(null)</code> <br>
   * if calldelete is false it will use the depthtoRemove to call <code>object.remove(resource)</code><br>
   * deptToRemove = /0/1/2/...<br>
   * It will remove it from parent folder (depthtoRemove - 1);
   */
  private void attachDetachResourceDepth1(int depth, boolean callDelete, int depthtoRemove) throws Exception
  {
    CDOSession session = openModel1Session();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);
    CDOResource rootResource = transaction.getRootResource();
    assertSame(rootResource, rootResource.eResource());
    String path = "";
    List<String> names = new ArrayList<String>();
    for (int i = 0; i < depth; i++)
    {
      String name = "test" + String.valueOf(i + 1);
      names.add(name);
      path += "/" + name;
    }

    final URI uri = URI.createURI("cdo:" + path);
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertEquals(names.get(names.size() - 1), resource.getName());

    transaction.commit();
    List<CDOResourceNode> nodesList = new ArrayList<CDOResourceNode>();
    CDOResource resourceByLookup = null;
    CDOResourceNode next = null;
    for (int i = 0; i < depth; i++)
    {
      if (i == 0)
      {
        next = (CDOResourceNode)rootResource.getContents().get(0);
      }
      else
      {
        next = ((CDOResourceFolder)next).getNodes().get(0);
      }

      nodesList.add(next);
    }

    resourceByLookup = (CDOResource)next;
    assertSame(resource, resourceByLookup);
    assertClean(resourceByLookup, transaction);
    assertEquals(true, resourceSet.getResources().contains(resourceByLookup));

    CDOObject cdoParent = null;
    CDOObject cdoRootResource = CDOUtil.getCDOObject(rootResource);
    for (int i = 0; i < depth; i++)
    {
      CDOResourceNode resourceNode = nodesList.get(i);
      CDOObject cdoResourceNode = CDOUtil.getCDOObject(resourceNode);

      if (i == 0)
      {
        assertEquals(cdoRootResource.cdoID(), cdoResourceNode.cdoRevision().data().getResourceID());
        assertEquals(CDOID.NULL, cdoResourceNode.cdoRevision().data().getContainerID());
      }
      else
      {
        assertEquals(CDOID.NULL, cdoResourceNode.cdoRevision().data().getResourceID());
        assertEquals(cdoParent.cdoID(), cdoResourceNode.cdoRevision().data().getContainerID());
      }

      cdoParent = cdoResourceNode;
    }

    if (callDelete)
    {
      resource.delete(null);
      depthtoRemove = depth;
    }
    else
    {
      CDOResourceNode node = nodesList.get(depthtoRemove);
      if (depthtoRemove == 0)
      {
        rootResource.getContents().remove(node);
      }
      else
      {
        CDOResourceFolder parentFolder = (CDOResourceFolder)nodesList.get(depthtoRemove - 1);
        assertEquals(parentFolder, node.getFolder());
        parentFolder.getNodes().remove(node);
      }
    }

    for (int i = depthtoRemove; i < depth; i++)
    {
      CDOResourceNode transientNode = nodesList.get(i);
      assertTransient(transientNode);
      if (transientNode instanceof CDOResource)
      {
        assertEquals(false, resourceSet.getResources().contains(transientNode));
      }

      assertEquals(null, transientNode.eResource());
      if (i == depthtoRemove)
      {
        assertEquals(null, transientNode.eContainer());
      }
      else
      {
        assertEquals(cdoParent, transientNode.eContainer());
      }

      cdoParent = transientNode;
    }

    transaction.commit();
  }

  private void changePath(int depthFrom, int depthTo) throws Exception
  {
    String prefixA = "testA";
    String prefixB = "testB";

    String oldPath = createPath(prefixA, depthFrom, "test");
    String newPath = createPath(prefixB, depthTo, "test2");
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(oldPath);
      Order order = getModel1Factory().createOrder();
      resource.getContents().add(order);

      String path = CDOURIUtil.extractResourcePath(resource.getURI());
      assertEquals(oldPath, path);
      assertEquals(depthFrom, CDOURIUtil.analyzePath(resource.getURI()).size() - 1);

      transaction.commit();

      CDOID idBeforeChangePath = CDOUtil.getCDOObject(resource).cdoID();
      CDOID idBeforeChangePathOrder = CDOUtil.getCDOObject(order).cdoID();

      msg("New path");
      resource.setPath(newPath);
      path = CDOURIUtil.extractResourcePath(resource.getURI());
      assertEquals(depthTo, CDOURIUtil.analyzePath(resource.getURI()).size() - 1);
      assertEquals(newPath, path);
      transaction.commit();

      CDOID idAfterChangePath = CDOUtil.getCDOObject(resource).cdoID();
      assertEquals(idBeforeChangePath, idAfterChangePath);

      CDOID idAfterChangePathOrder = CDOUtil.getCDOObject(order).cdoID();
      assertEquals(idBeforeChangePathOrder, idAfterChangePathOrder);

      Resource resourceRenamed = transaction.getResourceSet().getResource(
          CDOURIUtil.createResourceURI(session, newPath), false);

      assertEquals(resource, resourceRenamed);
      assertClean(resource, transaction);
      assertClean(order, transaction);
      session.close();
    }

    clearCache(getRepository().getRevisionManager());
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    try
    {
      transaction.getResourceSet().getResource(CDOURIUtil.createResourceURI(session, oldPath), true);
      fail("Doesn't exist");
    }
    catch (Exception ex)
    {
    }

    Resource resource = transaction.getResourceSet().getResource(CDOURIUtil.createResourceURI(session, newPath), true);
    assertNotNull(resource);
  }

  private String createPath(String namePrefix, int depth, String name)
  {
    String path = "";
    for (int i = 0; i < depth; i++)
    {
      String localName = namePrefix + String.valueOf(i + 1);
      path += "/" + localName;
    }

    path += "/" + name;
    return path;
  }

  private CDOResource createResource(CDOTransaction transaction, String path)
  {
    Product1 p = getModel1Factory().createProduct1();
    p.setName("test-" + path);
    p.setVat(VAT.VAT0);

    CDOResource resource = transaction.createResource(path);
    resource.getContents().add(p);
    return resource;
  }

  private CDOResource modifyResource(CDOTransaction transaction, String path)
  {
    Product1 p = getModel1Factory().createProduct1();
    p.setName("test-" + path + "-modified");
    p.setVat(VAT.VAT0);

    CDOResource resource = transaction.getResource(path);
    resource.getContents().add(p);
    return resource;
  }

  private void queryResources(CDOView view, String pathPrefix, int expected)
  {
    msg("Path prefix: " + pathPrefix);
    List<CDOResourceNode> nodes = view.queryResources(null, pathPrefix, false);
    for (CDOResourceNode node : nodes)
    {
      msg("Result: " + node.getPath());
    }

    assertEquals(expected, nodes.size());
  }

  static class TestAdapter extends AdapterImpl
  {
    @Override
    public void notifyChanged(Notification msg)
    {
      super.notifyChanged(msg);
      if (msg.getNewValue() instanceof CDOResource)
      {
        ((CDOResource)msg.getNewValue()).getPath();
      }
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return super.isAdapterForType(type);
    }
  };
}
