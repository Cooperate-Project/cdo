/**
 * Copyright (c) 2009 - 2010 Martin Fluegge (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.actions;

import org.eclipse.emf.cdo.dawn.diagram.part.IDawnDiagramEditor;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.dawn.synchronize.DawnConflictHelper;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Martin Fluegge
 */
public class HandleConflictsAction implements IObjectActionDelegate
{

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HandleConflictsAction.class);

  private Object selectedElement;

  public final static String ID = "org.eclipse.emf.cdo.dawn.actions.HandleConflictAction";

  public HandleConflictsAction()
  {
  }

  public void run(IAction action)
  {

    if (TRACER.isEnabled())
    {
      TRACER.format("Start solving conflicts for {0}", selectedElement); //$NON-NLS-1$
    }

    IEditorPart activeEditor = DawnEditorHelper.getActiveEditor();
    if (activeEditor instanceof IDawnDiagramEditor)
    {
      MessageDialog dialog = new MessageDialog(DawnEditorHelper.getActiveShell(), "Conflict", null,
          "There are conflicts in your diagram. Would you like to rollback your current transaction?",
          MessageDialog.QUESTION, new String[] { "yes", "no", "Cancel" }, 1);

      switch (dialog.open())
      {
      case 0: // yes
        DawnConflictHelper.rollback((DiagramDocumentEditor)activeEditor);
        break;
      case 1: // no
        break;
      default: // cancel
        break;
      }
    }
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    selectedElement = null;
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection structuredSelection = (IStructuredSelection)selection;
      if (structuredSelection.getFirstElement() instanceof EditPart)
      {
        selectedElement = structuredSelection.getFirstElement();
      }
    }
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }
}
