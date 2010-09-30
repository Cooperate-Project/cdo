/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.gmf;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class RollbackTest extends AbstractDawnUITest
{
  private static SWTGefBot bot;

  @BeforeClass
  public static void beforeClass() throws Exception
  {
    bot = new SWTGefBot();
    DawnSWTBotUtil.initTest(bot);
  }

  @Override
  @Before
  public void setUp() throws Exception
  {
    super.setUp();
    bot = new SWTGefBot();
    DawnSWTBotUtil.initTest(bot);
    bot.viewByTitle("CDO Sessions").close();
  }

  @Override
  @After
  public void tearDown() throws Exception
  {
    // closeAllEditors();
    sleep(1000);
    super.tearDown();
  }

  @Test
  public void testGMFAClassConflictMove() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", bot);
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", bot, editor);
    editor.save();

    editor.drag(100, 100, 200, 200);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      assertEquals(1, diagram.getChildren().size());

      Node nodeA = (Node)diagram.getChildren().get(0);

      DawnSWTBotUtil.moveNodeRemotely(nodeA, 200, 300);

      transaction.commit();
    }
    sleep(500);

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    SWTBotGefEditPart classBEditpart = aClassEditParts.get(0);

    assertEquals(true, DawnAcoreTestUtil.showsConflict(classBEditpart.part()));
    editor.clickContextMenu("Solve Conflict");

    bot.button("yes").click();

    assertEquals(false, ((IDawnEditor)editor.getReference().getEditor(false)).getView().hasConflict());

    EditPart part = DawnAcoreTestUtil.getAClassEditParts(editor).get(0).part();

    assertEquals(true, DawnSWTBotUtil.checkNodePosition((Node)part.getModel(), 200, 300));

    editor.save();
  }
}
