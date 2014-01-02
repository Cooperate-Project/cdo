package org.eclipse.emf.cdo.releng.preferences.presentation.handlers;

/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.Property;

import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.provider.ItemProvider;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.ISources;

import java.util.ArrayList;
import java.util.List;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class NavigateHandler extends AbstractHandler
{
  protected IStructuredSelection targetSelection;

  protected Viewer viewer;

  public NavigateHandler()
  {
  }

  /**
   * the command has been executed, so extract extract the needed information
   * from the application context.
   */
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    if (viewer != null)
    {
      viewer.setSelection(targetSelection, true);
    }
    return null;
  }

  @Override
  public void setEnabled(Object evaluationContext)
  {
    IEvaluationContext evaluationContext2 = (IEvaluationContext)evaluationContext;
    Object activeEditorPart = evaluationContext2.getVariable(ISources.ACTIVE_EDITOR_NAME);
    if (activeEditorPart instanceof IViewerProvider)
    {
      viewer = ((IViewerProvider)activeEditorPart).getViewer();
    }
    Object selection = evaluationContext2.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
    if (selection instanceof IStructuredSelection)
    {
      updateSelection((IStructuredSelection)selection);
    }

    setBaseEnabled(viewer != null && !targetSelection.isEmpty());
  }

  public boolean updateSelection(IStructuredSelection selection)
  {
    List<Object> targets = new ArrayList<Object>();
    for (Object object : selection.toArray())
    {
      if (object instanceof PreferenceNode)
      {
        PreferenceNode preferenceNode = (PreferenceNode)object;
        PreferenceNode ancestor = preferenceNode.getAncestor();
        if (ancestor != null)
        {
          targets.add(ancestor);
        }
      }
      else if (object instanceof Property)
      {
        Property property = (Property)object;
        Property ancestor = property.getAncestor();
        if (ancestor != null)
        {
          targets.add(ancestor);
        }
      }
      else if (object instanceof IWrapperItemProvider)
      {
        IWrapperItemProvider wrapperItemProvider = (IWrapperItemProvider)object;
        targets.add(wrapperItemProvider.getValue());
      }
      else if (object instanceof ItemProvider)
      {
        ItemProvider itemProvider = (ItemProvider)object;
        for (Object child : itemProvider.getChildren())
        {
          if (child instanceof IWrapperItemProvider)
          {
            targets.add(((IWrapperItemProvider)child).getValue());
          }
        }
      }
    }

    targetSelection = new StructuredSelection(targets);
    return !targetSelection.isEmpty();
  }
}
