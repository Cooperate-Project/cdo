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
package org.eclipse.emf.cdo.releng.preferences.presentation;

import org.eclipse.emf.cdo.releng.preferences.provider.PreferencesItemProviderAdapterFactory;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchWindow;

import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public class AllPreferencesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  private IWorkbench workbench;

  public AllPreferencesPreferencePage()
  {
    super("<taken from plugin.xml>");
    setDescription("Manage all preferences:");
    noDefaultAndApplyButton();
  }

  public void init(IWorkbench workbench)
  {
    this.workbench = workbench;
  }

  @Override
  protected Control createContents(Composite parent)
  {
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    layout.numColumns = 1;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);

    TreeViewer treeViewer = new TreeViewer(composite);
    PreferencesItemProviderAdapterFactory adapterFactory = new PreferencesItemProviderAdapterFactory();
    treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
    treeViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
    treeViewer.setInput(PreferencesUtil.getRootPreferenceNode());
    treeViewer.getControl().setLayoutData(
        new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

    return composite;
  }

  @Override
  protected void contributeButtons(Composite parent)
  {
    super.contributeButtons(parent);

    GridLayout gridLayout = (GridLayout)parent.getLayout();
    gridLayout.numColumns = 1;

    Button editButton = new Button(parent, SWT.PUSH);
    editButton.setText("Edit...");

    Dialog.applyDialogFont(editButton);
    int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
    Point minButtonSize = editButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    data.widthHint = Math.max(widthHint, minButtonSize.x);

    editButton.setLayoutData(data);
    editButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        // Invoke the close method on the preference dialog, but avoid using internal API, so do it reflectively.
        IPreferencePageContainer container = getContainer();

        try
        {
          Method method = container.getClass().getMethod("close");
          method.invoke(container);
        }
        catch (Throwable ex)
        {
          PreferencesEditorPlugin.INSTANCE.log(ex);
        }

        openWorkingSetsEditor();
      }
    });

  }

  protected void openWorkingSetsEditor()
  {
    final IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
    Display display = activeWorkbenchWindow.getShell().getDisplay();
    display.asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          IEditorInput editorInput = new URIEditorInput(PreferencesUtil.ROOT_PREFERENCE_NODE_URI.trimSegments(1)
              .appendSegment("All.preferences"), "All Preferences");
          IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
          activePage.openEditor(editorInput, "org.eclipse.emf.cdo.releng.preferences.presentation.PreferencesEditorID");
          activePage.showView(IPageLayout.ID_PROP_SHEET);
        }
        catch (Exception ex)
        {
          PreferencesEditorPlugin.INSTANCE.log(ex);
        }
      }
    });
  }
}
