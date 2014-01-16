/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.PreferencesFactory;
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.SetupUtil;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Eclipse Preference Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EclipsePreferenceTaskImpl extends SetupTaskImpl implements EclipsePreferenceTask
{
  private static final IWorkspaceRoot WORKSPACE_ROOT = ResourcesPlugin.getWorkspace().getRoot();

  /**
   * The default value of the '{@link #getKey() <em>Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getKey()
   * @generated
   * @ordered
   */
  protected static final String KEY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getKey() <em>Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getKey()
   * @generated
   * @ordered
   */
  protected String key = KEY_EDEFAULT;

  /**
   * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected static final String VALUE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected String value = VALUE_EDEFAULT;

  private transient PreferencesUtil.PreferenceProperty preferenceProperty;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EclipsePreferenceTaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.ECLIPSE_PREFERENCE_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getKey()
  {
    return key;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setKey(String newKey)
  {
    String oldKey = key;
    key = newKey;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.ECLIPSE_PREFERENCE_TASK__KEY, oldKey, key));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setValue(String newValue)
  {
    String oldValue = value;
    value = newValue;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.ECLIPSE_PREFERENCE_TASK__VALUE, oldValue,
          value));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE_PREFERENCE_TASK__KEY:
      return getKey();
    case SetupPackage.ECLIPSE_PREFERENCE_TASK__VALUE:
      return getValue();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE_PREFERENCE_TASK__KEY:
      setKey((String)newValue);
      return;
    case SetupPackage.ECLIPSE_PREFERENCE_TASK__VALUE:
      setValue((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE_PREFERENCE_TASK__KEY:
      setKey(KEY_EDEFAULT);
      return;
    case SetupPackage.ECLIPSE_PREFERENCE_TASK__VALUE:
      setValue(VALUE_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE_PREFERENCE_TASK__KEY:
      return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
    case SetupPackage.ECLIPSE_PREFERENCE_TASK__VALUE:
      return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (key: ");
    result.append(key);
    result.append(", value: ");
    result.append(value);
    result.append(')');
    return result.toString();
  }

  @Override
  public int getPriority()
  {
    return getKey().startsWith("/project") ? PROJECT_PRIORITY : PRIORITY;
  }

  @Override
  public Object getOverrideToken()
  {
    return createToken(new Path(getKey()).makeAbsolute().toString());
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    preferenceProperty = new PreferencesUtil.PreferenceProperty(key);

    String oldValue = preferenceProperty.get(null);
    if (ObjectUtil.equals(getValue(), oldValue))
    {
      return false;
    }

    return true;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    String key = getKey();

    // Ignore project-specific preferences for projects that don't exist in the workspace.
    URI uri = PreferencesFactory.eINSTANCE.createURI(key);
    if ("project".equals(uri.authority()))
    {
      if (!WORKSPACE_ROOT.getProject(URI.decode(uri.segment(0))).isAccessible())
      {
        context.log("Ignoring preference " + key + " = " + value);
        return;
      }
    }

    final String value = getValue();
    context.log("Setting preference " + key + " = " + value);

    performUI(context, new RunnableWithContext()
    {
      public void run(SetupTaskContext context) throws Exception
      {
        preferenceProperty.set(value);
        preferenceProperty.getNode().flush();
      }
    });
  }

  @Override
  public void collectSniffers(List<Sniffer> sniffers)
  {
    sniffers.add(new PreferenceSniffer("configuration"));
    sniffers.add(new PreferenceSniffer("instance"));
    sniffers.add(new PreferenceSniffer("project")
    {
      @Override
      public int getWork()
      {
        return 100;
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  private class PreferenceSniffer extends BasicSniffer
  {
    private String scope;

    public PreferenceSniffer(String scope)
    {
      super(EclipsePreferenceTaskImpl.this);
      setLabel(StringUtil.cap(scope) + "-scoped preferences");
      setDescription("Creates tasks for the non-default Eclipse preferences in the " + scope + " scope.");
      this.scope = scope;
    }

    @Override
    public int getPriority()
    {
      return scope.equals("project") ? PROJECT_PRIORITY : PRIORITY;
    }

    public void sniff(SetupTaskContainer container, List<Sniffer> dependencies, IProgressMonitor monitor)
        throws Exception
    {
      try
      {
        PreferenceNode node = PreferencesUtil.getRootPreferenceNode().getNode(scope);
        if (node != null)
        {
          List<Property> properties = new ArrayList<Property>();
          collectNonDefaultProperties(node, properties);

          if (!properties.isEmpty())
          {
            monitor.beginTask("", properties.size());

            CompoundSetupTask compound = getCompound(container, getLabel());
            for (Property property : properties)
            {
              addTaskForProperty(compound, property);
              monitor.worked(1);
            }
          }
        }
      }
      finally
      {
        monitor.done();
      }
    }

    private void collectNonDefaultProperties(PreferenceNode node, List<Property> properties)
    {
      for (PreferenceNode child : node.getChildren())
      {
        collectNonDefaultProperties(child, properties);
      }

      for (Property property : node.getProperties())
      {
        if (property.isNonDefault())
        {
          properties.add(property);
        }
      }
    }

    private void addTaskForProperty(CompoundSetupTask compound, Property property)
    {
      URI path = property.getAbsolutePath();
      for (String segment : path.trimSegments(1).segments())
      {
        compound = getCompound(compound, segment);
      }

      EclipsePreferenceTask task = SetupFactory.eINSTANCE.createEclipsePreferenceTask();
      task.setKey(PreferencesFactory.eINSTANCE.convertURI(path));
      task.setValue(SetupUtil.escape(property.getValue()));
      compound.getSetupTasks().add(task);
    }
  }

} // EclipsePreferenceTaskImpl
