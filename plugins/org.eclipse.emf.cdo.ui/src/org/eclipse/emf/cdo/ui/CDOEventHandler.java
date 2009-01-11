/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.internal.ui.ItemsProcessor;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOEventHandler
{
  private CDOView view;

  private TreeViewer treeViewer;

  private IListener sessionListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IContainerEvent<?>)
      {
        IContainerEvent<?> e = (IContainerEvent<?>)event;
        if (e.getDeltaElement() == view && e.getDeltaKind() == IContainerDelta.Kind.REMOVED)
        {
          viewClosed();
        }
      }
      else if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          viewClosed();
        }
      }
    }
  };

  private IListener viewListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewInvalidationEvent)
      {
        CDOViewInvalidationEvent e = (CDOViewInvalidationEvent)event;
        viewInvalidated(e.getDirtyObjects());
      }
      else if (event instanceof CDOTransactionFinishedEvent)
      {
        // CDOTransactionFinishedEvent e = (CDOTransactionFinishedEvent)event;
        // if (e.getType() == CDOTransactionFinishedEvent.Type.COMMITTED)
        // {
        // Map<CDOID, CDOID> idMappings = e.getIDMappings();
        // HashSet<CDOID> newOIDs = new HashSet<CDOID>(idMappings.values());
        // new ItemsProcessor(view)
        // {
        // @Override
        // protected void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject)
        // {
        // viewer.update(cdoObject.cdoInternalInstance(), null);
        // }
        // }.processCDOObjects(treeViewer, newOIDs);
        // }
        // else
        {
          refreshTreeViewer();
        }

        viewDirtyStateChanged();
      }
      else if (event instanceof CDOTransactionStartedEvent)
      {
        viewDirtyStateChanged();
      }
      else if (event instanceof CDOTransactionConflictEvent)
      {
        CDOTransactionConflictEvent e = (CDOTransactionConflictEvent)event;
        viewConflict(e.getConflictingObject(), e.isFirstConflict());
      }
    }
  };

  private IListener preferenceListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      @SuppressWarnings("unchecked")
      OMPreferencesChangeEvent<Boolean> preferenceChangeEvent = (OMPreferencesChangeEvent<Boolean>)event;
      if (OM.PREF_EDITOR_AUTO_RELOAD.getName().equals(preferenceChangeEvent.getPreference().getName()))
      {
        if (preferenceChangeEvent.getNewValue().booleanValue())
        {
          refreshTreeViewer();
        }
      }
      else if (OM.PREF_LABEL_DECORATION.getName().equals(preferenceChangeEvent.getPreference().getName()))
      {
        // Fire a LabelProviderChangedEvent in case user changed decoration pattern
        try
        {
          treeViewer.getControl().getDisplay().syncExec(new Runnable()
          {
            public void run()
            {
              try
              {
                PlatformUI.getWorkbench().getDecoratorManager().update(CDOLabelDecorator.DECORATOR_ID);
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
    }
  };

  public CDOEventHandler(CDOView view, TreeViewer treeViewer)
  {
    this.view = view;
    this.treeViewer = treeViewer;
    wirePreferences();
    view.getSession().addListener(sessionListener);
    view.addListener(viewListener);
  }

  public void dispose()
  {
    if (view != null)
    {
      view.removeListener(viewListener);
      CDOSession session = view.getSession();
      if (session != null)
      {
        session.removeListener(sessionListener);
      }
    }

    unwirePreferences();
    view = null;
    treeViewer = null;
  }

  public CDOView getView()
  {
    return view;
  }

  public TreeViewer getTreeViewer()
  {
    return treeViewer;
  }

  /**
   * @since 2.0
   */
  public void setTreeViewer(TreeViewer viewer)
  {
    treeViewer = viewer;
  }

  /**
   * @since 2.0
   */
  public void refreshTreeViewer()
  {
    try
    {
      treeViewer.getControl().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            treeViewer.refresh(true);
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

  /**
   * @since 2.0
   */
  public boolean isAutoReloadEnabled()
  {
    return OM.PREF_EDITOR_AUTO_RELOAD.getValue();
  }

  /**
   * @since 2.0
   */
  protected void wirePreferences()
  {
    OM.PREFS.addListener(preferenceListener);
  }

  /**
   * @since 2.0
   */
  protected void unwirePreferences()
  {
    OM.PREFS.removeListener(preferenceListener);
  }

  /**
   * @since 2.0
   */
  protected void viewInvalidated(Set<? extends CDOObject> dirtyObjects)
  {
    new ItemsProcessor(view)
    {
      @Override
      protected void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject)
      {
        objectInvalidated(cdoObject);
        if (isAutoReloadEnabled())
        {
          viewer.refresh(cdoObject.cdoInternalInstance(), true);
        }
      }
    }.processCDOObjects(treeViewer, dirtyObjects);
  }

  protected void objectInvalidated(InternalCDOObject cdoObject)
  {
  }

  protected void viewDirtyStateChanged()
  {
  }

  protected void viewConflict(CDOObject conflictingObject, boolean firstConflict)
  {
  }

  protected void viewClosed()
  {
  }
}
