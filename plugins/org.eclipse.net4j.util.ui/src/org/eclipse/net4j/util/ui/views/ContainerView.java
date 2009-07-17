/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.internal.ui.SharedIcons;
import org.eclipse.net4j.util.internal.ui.actions.IntrospectAction;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;

public abstract class ContainerView extends ViewPart implements ISelectionProvider, ISetSelectionTarget
{
  private Shell shell;

  private ContainerItemProvider<IContainer<Object>> itemProvider;

  private TreeViewer viewer;

  private ISelectionChangedListener selectionListener = new ISelectionChangedListener()
  {
    public void selectionChanged(SelectionChangedEvent event)
    {
      ITreeSelection selection = (ITreeSelection)event.getSelection();
      IActionBars bars = getViewSite().getActionBars();
      ContainerView.this.selectionChanged(bars, selection);
    }
  };

  private Action refreshAction = new RefreshAction();

  public ContainerView()
  {
  }

  public Shell getShell()
  {
    return shell;
  }

  public TreeViewer getViewer()
  {
    return viewer;
  }

  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  public void resetInput()
  {
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        try
        {
          IContainer<?> container = getContainer();
          viewer.setInput(container);
        }
        catch (RuntimeException ignore)
        {
        }
      }
    };

    try
    {
      Display display = getDisplay();
      if (display.getThread() == Thread.currentThread())
      {
        runnable.run();
      }
      else
      {
        display.asyncExec(runnable);
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  /**
   * @since 3.0
   */
  public ISelection getSelection()
  {
    if (viewer != null)
    {
      return viewer.getSelection();
    }

    return StructuredSelection.EMPTY;
  }

  /**
   * @since 3.0
   */
  public void setSelection(ISelection selection)
  {
    if (viewer != null)
    {
      viewer.setSelection(selection);
    }
  }

  /**
   * @since 3.0
   */
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.addSelectionChangedListener(listener);
    }
  }

  /**
   * @since 3.0
   */
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.removeSelectionChangedListener(listener);
    }
  }

  public void selectReveal(ISelection selection)
  {
    if (viewer != null)
    {
      viewer.setSelection(selection, true);
    }
  }

  @Override
  public final void createPartControl(Composite parent)
  {
    shell = parent.getShell();
    Composite composite = UIUtil.createGridComposite(parent, 1);

    Control control = createUI(composite);
    control.setLayoutData(UIUtil.createGridData());

    hookContextMenu();
    hookDoubleClick();
    contributeToActionBars();
  }

  protected Control createUI(Composite parent)
  {
    itemProvider = createContainerItemProvider();
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(itemProvider);
    viewer.setLabelProvider(itemProvider);
    viewer.setSorter(new ContainerNameSorter());
    resetInput();
    viewer.addSelectionChangedListener(selectionListener);
    getSite().setSelectionProvider(this);
    return viewer.getControl();
  }

  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new ContainerItemProvider<IContainer<Object>>(getRootElementFilter())
    {
      @Override
      public Image getImage(Object obj)
      {
        Image image = getElementImage(obj);
        if (image == null)
        {
          image = super.getImage(obj);
        }

        return image;
      }

      @Override
      public String getText(Object obj)
      {
        String text = getElementText(obj);
        if (text == null)
        {
          text = super.getText(obj);
        }

        return text;
      }
    };
  }

  protected String getElementText(Object element)
  {
    return null;
  }

  protected Image getElementImage(Object element)
  {
    return null;
  }

  protected IElementFilter getRootElementFilter()
  {
    return null;
  }

  protected abstract IContainer<?> getContainer();

  protected void hookDoubleClick()
  {
    viewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        ITreeSelection selection = (ITreeSelection)viewer.getSelection();
        Object object = selection.getFirstElement();
        doubleClicked(object);
      }
    });
  }

  protected void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        ITreeSelection selection = (ITreeSelection)viewer.getSelection();
        fillContextMenu(manager, selection);
      }
    });

    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }

  protected void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    manager.add(refreshAction);
    manager.add(new IntrospectAction(getViewer()));
  }

  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    itemProvider.fillContextMenu(manager, selection);
  }

  protected void selectionChanged(IActionBars bars, ITreeSelection selection)
  {
  }

  protected void doubleClicked(Object object)
  {
    if (object != null && viewer.isExpandable(object))
    {
      if (viewer.getExpandedState(object))
      {
        viewer.collapseToLevel(object, TreeViewer.ALL_LEVELS);
      }
      else
      {
        viewer.expandToLevel(object, 1);
      }
    }
  }

  protected void closeView()
  {
    try
    {
      getSite().getShell().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            getSite().getPage().hideView(ContainerView.this);
            ContainerView.this.dispose();
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  protected void showMessage(String message)
  {
    showMessage(MessageType.INFORMATION, message);
  }

  protected boolean showMessage(MessageType type, String message)
  {
    switch (type)
    {
    case INFORMATION:
      MessageDialog.openInformation(viewer.getControl().getShell(), getTitle(), message);
      return true;

    case ERROR:
      MessageDialog.openError(viewer.getControl().getShell(), getTitle(), message);
      return true;

    case WARNING:
      MessageDialog.openWarning(viewer.getControl().getShell(), getTitle(), message);
      return true;

    case CONFIRM:
      return MessageDialog.openConfirm(viewer.getControl().getShell(), getTitle(), message);

    case QUESTION:
      return MessageDialog.openQuestion(viewer.getControl().getShell(), getTitle(), message);

    default:
      return true;
    }
  }

  public void refreshViewer(boolean updateLabels)
  {
    refreshElement(null, updateLabels);
  }

  public void refreshElement(final Object element, final boolean updateLabels)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            if (element != null)
            {
              viewer.refresh(element, updateLabels);
            }
            else
            {
              viewer.refresh(updateLabels);
            }
          }
          catch (RuntimeException ignore)
          {
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
    }
  }

  public void updateLabels(final Object element)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            viewer.update(element, null);
          }
          catch (RuntimeException ignore)
          {
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
    }
  }

  public void revealElement(final Object element)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            viewer.reveal(element);
          }
          catch (RuntimeException ignore)
          {
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
    }
  }

  protected Display getDisplay()
  {
    Display display = viewer.getControl().getDisplay();
    if (display == null)
    {
      display = UIUtil.getDisplay();
    }

    return display;
  }

  public static ImageDescriptor getAddImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.TOOL_ADD);
  }

  public static ImageDescriptor getDeleteImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.TOOL_DELETE);
  }

  public static ImageDescriptor getRefreshImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.TOOL_REFRESH);
  }

  protected static enum MessageType
  {
    INFORMATION, ERROR, WARNING, CONFIRM, QUESTION
  }

  /**
   * @author Eike Stepper
   */
  private final class RefreshAction extends SafeAction
  {
    private RefreshAction()
    {
      super(Messages.getString("ContainerView_1"), Messages.getString("ContainerView_2"), getRefreshImageDescriptor()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void safeRun() throws Exception
    {
      viewer.refresh(false);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class Default<CONTAINER extends IContainer<?>> extends ContainerView
  {
    private CONTAINER container;

    public Default()
    {
    }

    @Override
    protected CONTAINER getContainer()
    {
      return container;
    }

    public void setContainer(CONTAINER container)
    {
      if (this.container != container)
      {
        this.container = container;
        resetInput();
      }
    }
  }
}
