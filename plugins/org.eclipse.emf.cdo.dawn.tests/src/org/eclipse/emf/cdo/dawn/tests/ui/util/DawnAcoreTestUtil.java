/**
 * Copyright (c) 2010 Martin Fluegge (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.util;

import org.eclipse.emf.cdo.dawn.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAggregationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAssociationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassCompositionsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassImplementedInterfacesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassSubClassesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreViewProvider;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.core.providers.IViewProvider;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

import org.hamcrest.Description;

import java.util.List;

/**
 * @author Martin Fluegge
 */
public class DawnAcoreTestUtil
{
  public static final String A_CLASS = "AClass";

  public static final String A_INTERFACE = "AInterface";

  public static final String A_ATTRIBUTE = "AAttribute";

  public static final String A_OPERATION = "AOperation";

  public static final String CONNECTION_IHERITS = "inherits";

  public static final String CONNECTION_IMPLEMENTS = "implements";

  public static final String CONNECTION_ASSOCIATION = "association";

  public static final String CONNECTION_AGGREGATION = "aggregation";

  public static final String CONNECTION_COMPOSITION = "composition";

  private static IViewProvider viewProvider = new AcoreViewProvider();

  public static SWTBotGefEditor openNewAcoreEditor(String diagramResourceName, SWTGefBot bot)
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();
    bot.button("Finish").click();
    SWTBotGefEditor editor = bot.gefEditor(diagramResourceName);
    return editor;
  }

  public static List<SWTBotGefEditPart> getAClassEditParts(SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> editParts = editor.editParts(new AbstractMatcher<AClassEditPart>()
    {
      @Override
      protected boolean doMatch(Object item)
      {
        return item instanceof AClassEditPart;
      }

      public void describeTo(Description description)
      {
      }
    });
    return editParts;
  }

  public static List<SWTBotGefEditPart> getAInterfaceEditParts(SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> editParts = editor.editParts(new AbstractMatcher<AInterfaceEditPart>()
    {
      @Override
      protected boolean doMatch(Object item)
      {
        return item instanceof AInterfaceEditPart;
      }

      public void describeTo(Description description)
      {
      }
    });
    return editParts;
  }

  public static List<SWTBotGefEditPart> getAClassAssociationsEditParts(SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> editParts = getAClassEditParts(editor);
    return DawnSWTBotUtil.getConnectionEditParts(editor, AClassAssociationsEditPart.class, editParts);
  }

  public static Node createNewAClassRemote(Diagram diagram, AClass newAClass)
  {
    String type = AcoreVisualIDRegistry.getType(AClassEditPart.VISUAL_ID);
    return DawnSWTBotUtil.createNewNodeRemote(diagram, newAClass, type);
  }

  // public static Edge createNewAssociationRemote(Node source, Node target)
  // {
  // String type = AcoreVisualIDRegistry.getType(AClassAssociationsEditPart.VISUAL_ID);
  // Edge newEdge = DawnSWTBotUtil.createEdgeRemote(source, target, type);
  // return newEdge;
  // }

  public static Edge createNewAssociationRemote(Node source, Node target, List<RelativeBendpoint> bendpoints)
  {
    String type = AcoreVisualIDRegistry.getType(AClassAssociationsEditPart.VISUAL_ID);
    return DawnSWTBotUtil.createEdgeRemote(source, target, type, AcoreElementTypes.AClassAssociations_4003, bendpoints,
        viewProvider);
  }

  public static Edge createNewImplementsRelationRemote(Node source, Node target, List<RelativeBendpoint> bendpoints)
  {
    String type = AcoreVisualIDRegistry.getType(AClassImplementedInterfacesEditPart.VISUAL_ID);
    return DawnSWTBotUtil.createEdgeRemote(source, target, type, AcoreElementTypes.AClassImplementedInterfaces_4002,
        bendpoints, viewProvider);
  }

  public static Edge createNewInheritanceRelationRemote(Node source, Node target, List<RelativeBendpoint> bendpoints)
  {
    String type = AcoreVisualIDRegistry.getType(AClassSubClassesEditPart.VISUAL_ID);
    return DawnSWTBotUtil.createEdgeRemote(source, target, type, AcoreElementTypes.AClassSubClasses_4001, bendpoints,
        viewProvider);
  }

  public static Edge createNewCompositionRemote(Node source, Node target, List<RelativeBendpoint> bendpoints)
  {
    String type = AcoreVisualIDRegistry.getType(AClassCompositionsEditPart.VISUAL_ID);
    return DawnSWTBotUtil.createEdgeRemote(source, target, type, AcoreElementTypes.AClassCompositions_4005, bendpoints,
        viewProvider);
  }

  public static Edge createNewAggregationRemote(Node source, Node target, List<RelativeBendpoint> bendpoints)
  {
    String type = AcoreVisualIDRegistry.getType(AClassAggregationsEditPart.VISUAL_ID);
    return DawnSWTBotUtil.createEdgeRemote(source, target, type, AcoreElementTypes.AClassAggregations_4004, bendpoints,
        viewProvider);
  }

  public static void sleep(int seconds)
  {
    try
    {
      Thread.sleep(seconds);
    }
    catch (InterruptedException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public static boolean showsConflict(EditPart editPart)
  {
    if (editPart instanceof AClassEditPart || editPart instanceof AInterfaceEditPart)
    {
      GraphicalEditPart e = (GraphicalEditPart)editPart;

      IFigure figure = e.getFigure();
      return ((LineBorder)figure.getBorder()).getColor().equals(DawnAppearancer.COLOR_DELETE_CONFLICT);
    }
    return false;
  }
}
