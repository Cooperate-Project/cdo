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
package org.eclipse.emf.cdo.dawn.tests.ui;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DawnCreationWizardSWTBotTest extends AbstractDawnUITest
{
  private static SWTGefBot bot;

  @BeforeClass
  public static void beforeClass() throws Exception
  {
    bot = new SWTGefBot();
    bot.viewByTitle("Welcome").close();
  }

  @Override
  @Before
  public void setUp() throws Exception
  {
    super.setUp();
  }

  @Override
  @After
  public void tearDown() throws Exception
  {
    closeAllEditors();
    super.tearDown();
  }

  @Test
  public void createNewDawnDiagram() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();
    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("default.acore_diagram");
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default.acore"));
      assertEquals(true, resourceExists("/default.acore_diagram"));
    }
  }

  @Test
  public void createNewDawnDiagramBothPages() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");

    bot.button("Next >").click();
    bot.button("Next >").click();
    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("default.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/default.acore"));
      assertEquals(true, resourceExists("/default.acore_diagram"));
    }
  }

  @Test
  public void createNewDawnDiagramBothPagesSetName() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();

    shell = bot.shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("test.acore_diagram");

    bot.button("Next >").click();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("test.acore_diagram");
    assertNotNull(editor);
    editor.close();
  }

  @Test
  public void createNewDawnDiagramBothPagesSetDifferenNames() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();

    shell = bot.shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("notational.acore_diagram");

    bot.button("Next >").click();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    assertEquals("notational.acore", fileSemanticNameLabel.getText());

    fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("semantic.acore");

    fileSemanticNameLabel = bot.textWithLabel("File name:");
    assertEquals("semantic.acore", fileSemanticNameLabel.getText());

    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("notational.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/semantic.acore"));
      assertEquals(true, resourceExists("/notational.acore_diagram"));
    }
  }

  @Test
  public void createNewDawnDiagramEmptyNotationalResourceName() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();

    shell = bot.shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("");

    Keyboard keyboard = KeyboardFactory.getDefaultKeyboard(fileNameLabel.widget, null);
    fileNameLabel.setFocus();
    fileNameLabel.typeText("x", 500);
    keyboard.pressShortcut(Keystrokes.BS);
    assertEquals(false, bot.button("Next >").isEnabled());
    bot.button("Cancel").click();
  }

  @Test
  public void createNewDawnDiagramEmptySemanticResourceName() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();

    shell = bot.shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("notational.acore_diagram");

    bot.button("Next >").click();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    Keyboard keyboard = KeyboardFactory.getDefaultKeyboard(fileSemanticNameLabel.widget, null);
    fileSemanticNameLabel.setFocus();
    fileSemanticNameLabel.typeText("x", 500);

    keyboard.pressShortcut(Keystrokes.BS);
    assertEquals(false, bot.button("Next >").isEnabled());
    bot.button("Cancel").click();
  }

  @Test
  public void createNewDawnDiagramSelectFolder() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      final URI uri = URI.createURI("cdo:/folder/dummy");
      resourceSet.createResource(uri);
      transaction.commit();
    }

    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();

    shell = bot.shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("test.acore_diagram");

    SWTBotTree tree = bot.tree(0);

    selectFolder(tree.getAllItems(), "folder", false);
    bot.button("Next >").click();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("test.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.acore"));
      assertEquals(true, resourceExists("/folder/test.acore_diagram"));
    }
  }

  @Test
  public void createNewDawnDiagramTypeFolder() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      final URI uri = URI.createURI("cdo:/folder/dummy");
      resourceSet.createResource(uri);
      transaction.commit();
    }

    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();

    shell = bot.shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("test.acore_diagram");

    SWTBotText folder = bot.textWithLabel("Enter or select the parent folder: ");
    folder.setText("/folder");
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";

    bot.button("Next >").click();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("test.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.acore"));
      assertEquals(true, resourceExists("/folder/test.acore_diagram"));
    }
  }

  @Test
  public void createNewDawnDiagramSelectDifferentFolders() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      resourceSet.createResource(URI.createURI("cdo:/folder1/dummy"));
      resourceSet.createResource(URI.createURI("cdo:/folder2/dummy"));

      transaction.commit();
    }

    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();

    shell = bot.shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("test.acore_diagram");

    SWTBotTree tree = bot.tree(0);

    selectFolder(tree.getAllItems(), "folder1", false);
    bot.button("Next >").click();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    tree = bot.tree(0);
    selectFolder(tree.getAllItems(), "folder2", false);

    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("test.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder1/test.acore_diagram"));
      assertEquals(true, resourceExists("/folder2/test.acore"));
    }
  }
}
