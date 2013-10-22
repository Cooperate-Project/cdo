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
package org.eclipse.emf.cdo.releng.setup.presentation;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.PreferencesPackage;
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;
import org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;

import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.ui.action.ControlAction;
import org.eclipse.emf.edit.ui.action.CreateChildAction;
import org.eclipse.emf.edit.ui.action.CreateSiblingAction;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.action.LoadResourceAction;
import org.eclipse.emf.edit.ui.action.ValidateAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is the action bar contributor for the Setup model editor.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class SetupActionBarContributor extends EditingDomainActionBarContributor implements ISelectionChangedListener
{
  /**
   * This keeps track of the active editor.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IEditorPart activeEditorPart;

  /**
   * This keeps track of the current selection provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ISelectionProvider selectionProvider;

  /**
   * This action opens the Properties view.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IAction showPropertiesViewAction = new Action(
      SetupEditorPlugin.INSTANCE.getString("_UI_ShowPropertiesView_menu_item"))
  {
    @Override
    public void run()
    {
      try
      {
        getPage().showView("org.eclipse.ui.views.PropertySheet");
      }
      catch (PartInitException exception)
      {
        SetupEditorPlugin.INSTANCE.log(exception);
      }
    }
  };

  /**
   * This action refreshes the viewer of the current editor if the editor
   * implements {@link org.eclipse.emf.common.ui.viewer.IViewerProvider}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IAction refreshViewerAction = new Action(
      SetupEditorPlugin.INSTANCE.getString("_UI_RefreshViewer_menu_item"))
  {
    @Override
    public boolean isEnabled()
    {
      return activeEditorPart instanceof IViewerProvider;
    }

    @Override
    public void run()
    {
      if (activeEditorPart instanceof IViewerProvider)
      {
        Viewer viewer = ((IViewerProvider)activeEditorPart).getViewer();
        if (viewer != null)
        {
          viewer.refresh();
        }
      }
    }
  };

  /**
   * This will contain one {@link org.eclipse.emf.edit.ui.action.CreateChildAction} corresponding to each descriptor
   * generated for the current selection by the item provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<IAction> createChildActions;

  /**
   * This is the menu manager into which menu contribution items should be added for CreateChild actions.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IMenuManager createChildMenuManager;

  /**
   * This will contain one {@link org.eclipse.emf.edit.ui.action.CreateSiblingAction} corresponding to each descriptor
   * generated for the current selection by the item provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<IAction> createSiblingActions;

  /**
   * This is the menu manager into which menu contribution items should be added for CreateSibling actions.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IMenuManager createSiblingMenuManager;

  private PreferenceRecorderAction recordPreferencesAction = new PreferenceRecorderAction();

  /**
   * This creates an instance of the contributor.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupActionBarContributor()
  {
    super(ADDITIONS_LAST_STYLE);
    loadResourceAction = new LoadResourceAction();
    validateAction = new ValidateAction();
    controlAction = new ControlAction();
  }

  /**
   * This adds Separators for editor additions to the tool bar.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void contributeToToolBar(IToolBarManager toolBarManager)
  {
    toolBarManager.add(new Separator("setup-settings"));
    toolBarManager.add(recordPreferencesAction);
    toolBarManager.add(new Separator("setup-additions"));
  }

  /**
   * This adds to the menu bar a menu and some separators for editor additions,
   * as well as the sub-menus for object creation items.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void contributeToMenu(IMenuManager menuManager)
  {
    super.contributeToMenu(menuManager);

    IMenuManager submenuManager = new MenuManager(SetupEditorPlugin.INSTANCE.getString("_UI_SetupEditor_menu"),
        "org.eclipse.emf.cdo.releng.setupMenuID");
    menuManager.insertAfter("additions", submenuManager);
    submenuManager.add(new Separator("settings"));
    submenuManager.add(new Separator("actions"));
    submenuManager.add(new Separator("additions"));
    submenuManager.add(new Separator("additions-end"));

    // Prepare for CreateChild item addition or removal.
    //
    createChildMenuManager = new MenuManager(SetupEditorPlugin.INSTANCE.getString("_UI_CreateChild_menu_item"));
    submenuManager.insertBefore("additions", createChildMenuManager);

    // Prepare for CreateSibling item addition or removal.
    //
    createSiblingMenuManager = new MenuManager(SetupEditorPlugin.INSTANCE.getString("_UI_CreateSibling_menu_item"));
    submenuManager.insertBefore("additions", createSiblingMenuManager);

    // Force an update because Eclipse hides empty menus now.
    //
    submenuManager.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager menuManager)
      {
        menuManager.updateAll(true);
      }
    });

    addGlobalActions(submenuManager);
  }

  /**
   * When the active editor changes, this remembers the change and registers with it as a selection provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setActiveEditor(IEditorPart part)
  {
    super.setActiveEditor(part);
    activeEditorPart = part;

    // Switch to the new selection provider.
    //
    if (selectionProvider != null)
    {
      selectionProvider.removeSelectionChangedListener(this);
    }
    if (part == null)
    {
      selectionProvider = null;
    }
    else
    {
      selectionProvider = part.getSite().getSelectionProvider();
      selectionProvider.addSelectionChangedListener(this);

      // Fake a selection changed event to update the menus.
      //
      if (selectionProvider.getSelection() != null)
      {
        selectionChanged(new SelectionChangedEvent(selectionProvider, selectionProvider.getSelection()));
      }
    }
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionChangedListener},
   * handling {@link org.eclipse.jface.viewers.SelectionChangedEvent}s by querying for the children and siblings
   * that can be added to the selected object and updating the menus accordingly.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void selectionChangedGen(SelectionChangedEvent event)
  {
    // Remove any menu items for old selection.
    //
    if (createChildMenuManager != null)
    {
      depopulateManager(createChildMenuManager, createChildActions);
    }
    if (createSiblingMenuManager != null)
    {
      depopulateManager(createSiblingMenuManager, createSiblingActions);
    }

    // Query the new selection for appropriate new child/sibling descriptors
    //
    Collection<?> newChildDescriptors = null;
    Collection<?> newSiblingDescriptors = null;

    ISelection selection = event.getSelection();
    if (selection instanceof IStructuredSelection && ((IStructuredSelection)selection).size() == 1)
    {
      Object object = ((IStructuredSelection)selection).getFirstElement();

      EditingDomain domain = ((IEditingDomainProvider)activeEditorPart).getEditingDomain();

      newChildDescriptors = domain.getNewChildDescriptors(object, null);
      newSiblingDescriptors = domain.getNewChildDescriptors(null, object);
    }

    // Generate actions for selection; populate and redraw the menus.
    //
    createChildActions = generateCreateChildActions(newChildDescriptors, selection);
    createSiblingActions = generateCreateSiblingActions(newSiblingDescriptors, selection);

    if (createChildMenuManager != null)
    {
      populateManager(createChildMenuManager, createChildActions, null);
      createChildMenuManager.update(true);
    }
    if (createSiblingMenuManager != null)
    {
      populateManager(createSiblingMenuManager, createSiblingActions, null);
      createSiblingMenuManager.update(true);
    }
  }

  public void selectionChanged(SelectionChangedEvent event)
  {
    selectionChangedGen(event);
    recordPreferencesAction.selectionChanged(event);
  }

  /**
   * This generates a {@link org.eclipse.emf.edit.ui.action.CreateChildAction} for each object in <code>descriptors</code>,
   * and returns the collection of these actions.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<IAction> generateCreateChildActions(Collection<?> descriptors, ISelection selection)
  {
    Collection<IAction> actions = new ArrayList<IAction>();
    if (descriptors != null)
    {
      for (Object descriptor : descriptors)
      {
        actions.add(new CreateChildAction(activeEditorPart, selection, descriptor));
      }
    }
    return actions;
  }

  /**
   * This generates a {@link org.eclipse.emf.edit.ui.action.CreateSiblingAction} for each object in <code>descriptors</code>,
   * and returns the collection of these actions.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<IAction> generateCreateSiblingActions(Collection<?> descriptors, ISelection selection)
  {
    Collection<IAction> actions = new ArrayList<IAction>();
    if (descriptors != null)
    {
      for (Object descriptor : descriptors)
      {
        actions.add(new CreateSiblingAction(activeEditorPart, selection, descriptor));
      }
    }
    return actions;
  }

  /**
   * This populates the specified <code>manager</code> with {@link org.eclipse.jface.action.ActionContributionItem}s
   * based on the {@link org.eclipse.jface.action.IAction}s contained in the <code>actions</code> collection,
   * by inserting them before the specified contribution item <code>contributionID</code>.
   * If <code>contributionID</code> is <code>null</code>, they are simply added.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void populateManager(IContributionManager manager, Collection<? extends IAction> actions,
      String contributionID)
  {
    if (actions != null)
    {
      for (IAction action : actions)
      {
        if (contributionID != null)
        {
          manager.insertBefore(contributionID, action);
        }
        else
        {
          manager.add(action);
        }
      }
    }
  }

  /**
   * This removes from the specified <code>manager</code> all {@link org.eclipse.jface.action.ActionContributionItem}s
   * based on the {@link org.eclipse.jface.action.IAction}s contained in the <code>actions</code> collection.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void depopulateManager(IContributionManager manager, Collection<? extends IAction> actions)
  {
    if (actions != null)
    {
      IContributionItem[] items = manager.getItems();
      for (int i = 0; i < items.length; i++)
      {
        // Look into SubContributionItems
        //
        IContributionItem contributionItem = items[i];
        while (contributionItem instanceof SubContributionItem)
        {
          contributionItem = ((SubContributionItem)contributionItem).getInnerItem();
        }

        // Delete the ActionContributionItems with matching action.
        //
        if (contributionItem instanceof ActionContributionItem)
        {
          IAction action = ((ActionContributionItem)contributionItem).getAction();
          if (actions.contains(action))
          {
            manager.remove(contributionItem);
          }
        }
      }
    }
  }

  /**
   * This populates the pop-up menu before it appears.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void menuAboutToShow(IMenuManager menuManager)
  {
    super.menuAboutToShow(menuManager);
    MenuManager submenuManager = null;

    submenuManager = new MenuManager(SetupEditorPlugin.INSTANCE.getString("_UI_CreateChild_menu_item"));
    populateManager(submenuManager, createChildActions, null);
    menuManager.insertBefore("edit", submenuManager);

    submenuManager = new MenuManager(SetupEditorPlugin.INSTANCE.getString("_UI_CreateSibling_menu_item"));
    populateManager(submenuManager, createSiblingActions, null);
    menuManager.insertBefore("edit", submenuManager);
  }

  /**
   * This inserts global actions before the "additions-end" separator.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void addGlobalActions(IMenuManager menuManager)
  {
    menuManager.insertAfter("additions-end", new Separator("ui-actions"));
    menuManager.insertAfter("ui-actions", showPropertiesViewAction);

    refreshViewerAction.setEnabled(refreshViewerAction.isEnabled());
    menuManager.insertAfter("ui-actions", refreshViewerAction);

    super.addGlobalActions(menuManager);
  }

  /**
   * This ensures that a delete action will clean up all references to deleted objects.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected boolean removeAllReferencesOnDelete()
  {
    return true;
  }

  /**
   * @author Eike Stepper
   */
  private class PreferenceRecorderAction extends Action
  {
    private SetupTaskContainer container;

    private PreferenceNode rootPreferenceNode;

    private EContentAdapter preferenceAdapter;

    public PreferenceRecorderAction()
    {
      super("Record", AS_CHECK_BOX);
      setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/recorder.gif"));
      setToolTipText("Record preference changes into the selected setup task container");
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
      if (!isChecked())
      {
        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection)
        {
          IStructuredSelection structuredSelection = (IStructuredSelection)selection;
          if (structuredSelection.size() == 1)
          {
            Object element = structuredSelection.getFirstElement();
            if (element instanceof SetupTaskContainer)
            {
              container = (SetupTaskContainer)element;
              setEnabled(true);
              return;
            }
          }
        }

        container = null;
        setEnabled(false);
      }
    }

    @Override
    public void run()
    {
      if (isChecked())
      {
        if (activeEditorPart instanceof IViewerProvider)
        {
          Viewer viewer = ((IViewerProvider)activeEditorPart).getViewer();
          if (viewer instanceof TreeViewer)
          {
            ((TreeViewer)viewer).setExpandedState(container, true);
          }
        }

        preferenceAdapter = createPreferenceAdapter();
        rootPreferenceNode = PreferencesUtil.getRootPreferenceNode(true);
        rootPreferenceNode.eAdapters().add(preferenceAdapter);

        ChangeCommand command = new ChangeCommand(container)
        {
          @Override
          protected void doExecute()
          {
            PreferenceDialog dialog = org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn(null, null, null,
                null);
            dialog.open();
          }
        };

        EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(container);
        CommandStack commandStack = editingDomain.getCommandStack();
        commandStack.execute(command);

        rootPreferenceNode.eAdapters().remove(preferenceAdapter);
        rootPreferenceNode = null;
        preferenceAdapter = null;
        setChecked(false);
      }
    }

    private EContentAdapter createPreferenceAdapter()
    {
      return new EContentAdapter()
      {
        private Map<Property, String> map = new HashMap<Property, String>();

        @Override
        protected void setTarget(EObject target)
        {
          super.setTarget(target);
          if (target instanceof Property)
          {
            Property property = (Property)target;
            String absolutePath = property.getAbsolutePath();
            if (absolutePath.startsWith("/instance/"))
            {
              map.put(property, absolutePath);
            }
          }
        }

        @Override
        public void notifyChanged(Notification notification)
        {
          super.notifyChanged(notification);
          switch (notification.getEventType())
          {
          case Notification.SET:
            if (notification.getFeature() == PreferencesPackage.Literals.PROPERTY__VALUE)
            {
              Property property = (Property)notification.getNotifier();
              setPreference(property, property.getValue());
            }
            break;

          case Notification.ADD:
            if (notification.getFeature() == PreferencesPackage.Literals.PREFERENCE_NODE__PROPERTIES)
            {
              Property property = (Property)notification.getNewValue();
              setPreference(property, property.getValue());
            }
            break;

          case Notification.REMOVE:
            if (notification.getFeature() == PreferencesPackage.Literals.PREFERENCE_NODE__PROPERTIES)
            {
              Property property = (Property)notification.getOldValue();
              setPreference(property, null);
            }
            break;
          }
        }

        private void setPreference(Property property, String value)
        {
          String absolutePath = map.get(property);
          if (absolutePath != null)
          {
            EList<SetupTask> setupTasks = container.getSetupTasks();
            for (Iterator<SetupTask> it = setupTasks.iterator(); it.hasNext();)
            {
              SetupTask setupTask = it.next();
              if (setupTask instanceof EclipsePreferenceTask)
              {
                EclipsePreferenceTask preferenceTask = (EclipsePreferenceTask)setupTask;
                if (absolutePath.equals(preferenceTask.getKey()))
                {
                  it.remove();
                }
              }
            }

            EclipsePreferenceTask task = SetupFactory.eINSTANCE.createEclipsePreferenceTask();
            task.setKey(absolutePath);
            task.setValue(value);

            setupTasks.add(task);
          }
        }
      };
    }
  }
}
