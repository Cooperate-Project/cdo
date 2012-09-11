/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.ui.dialogs;

import org.eclipse.emf.cdo.releng.internal.version.IVersionBuilderArguments;
import org.eclipse.emf.cdo.releng.internal.version.VersionBuilderArguments;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ConfigurationDialog extends TitleAreaDialog implements IVersionBuilderArguments
{
  private static final String PATH_LABEL = "Path to release specification file";

  private static final String BUILDER_CONFIGURATION = "Version Builder Configuration";

  private VersionBuilderArguments values;

  private Text releasePathText;

  private Button ignoreMalformedVersionsButton;

  private Button ignoreFeatureNatureButton;

  private Button ignoreSchemaBuilderButton;

  private Button ignoreDebugOptionsButton;

  private Button ignoreMissingDependencyRangesButton;

  private Button ignoreMissingExportVersionsButton;

  private Button ignoreFeatureContentChangesButton;

  private Button ignoreFeatureContentRedundancyButton;

  public ConfigurationDialog(Shell parentShell, VersionBuilderArguments defaults)
  {
    super(parentShell);
    setHelpAvailable(false);
    values = new VersionBuilderArguments(defaults);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    newShell.setText(BUILDER_CONFIGURATION);
    super.configureShell(newShell);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle(BUILDER_CONFIGURATION);
    setMessage("Select a release specification file and check additional settings.");

    Composite dialogArea = (Composite)super.createDialogArea(parent);

    Composite composite = new Composite(dialogArea, SWT.NONE);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    new Label(composite, SWT.NONE).setText(PATH_LABEL + ": ");

    releasePathText = new Text(composite, SWT.BORDER);
    releasePathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    String releasePath = values.getReleasePath();
    if (releasePath != null)
    {
      releasePathText.setText(releasePath);
    }

    ignoreMalformedVersionsButton = new Button(composite, SWT.CHECK);
    ignoreMalformedVersionsButton.setText("Ignore malformed versions");
    ignoreMalformedVersionsButton.setSelection(values.isIgnoreMalformedVersions());

    ignoreFeatureNatureButton = new Button(composite, SWT.CHECK);
    ignoreFeatureNatureButton.setText("Ignore feature nature");
    ignoreFeatureNatureButton.setSelection(values.isIgnoreFeatureNature());

    ignoreSchemaBuilderButton = new Button(composite, SWT.CHECK);
    ignoreSchemaBuilderButton.setText("Ignore schema builder");
    ignoreSchemaBuilderButton.setSelection(values.isIgnoreSchemaBuilder());

    ignoreDebugOptionsButton = new Button(composite, SWT.CHECK);
    ignoreDebugOptionsButton.setText("Ignore debug options");
    ignoreDebugOptionsButton.setSelection(values.isIgnoreDebugOptions());

    ignoreMissingDependencyRangesButton = new Button(composite, SWT.CHECK);
    ignoreMissingDependencyRangesButton.setText("Ignore missing dependency version ranges");
    ignoreMissingDependencyRangesButton.setSelection(values.isIgnoreMissingDependencyRanges());

    ignoreMissingExportVersionsButton = new Button(composite, SWT.CHECK);
    ignoreMissingExportVersionsButton.setText("Ignore missing package export versions");
    ignoreMissingExportVersionsButton.setSelection(values.isIgnoreMissingExportVersions());

    ignoreFeatureContentRedundancyButton = new Button(composite, SWT.CHECK);
    ignoreFeatureContentRedundancyButton.setText("Ignore feature content redundancy");
    ignoreFeatureContentRedundancyButton.setSelection(values.isIgnoreFeatureContentRedundancy());

    ignoreFeatureContentChangesButton = new Button(composite, SWT.CHECK);
    ignoreFeatureContentChangesButton.setText("Ignore feature content changes");
    ignoreFeatureContentChangesButton.setSelection(values.isIgnoreFeatureContentChanges());

    releasePathText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        validate();
      }
    });

    validate();
    return dialogArea;
  }

  protected void validate()
  {
    if (releasePathText.getText().trim().length() == 0)
    {
      setErrorMessage(PATH_LABEL + " is empty.");
      return;
    }

    setErrorMessage(null);
  }

  @Override
  protected void okPressed()
  {
    values.setReleasePath(releasePathText.getText());
    values.setIgnoreMalformedVersions(ignoreMalformedVersionsButton.getSelection());
    values.setIgnoreFeatureNature(ignoreFeatureNatureButton.getSelection());
    values.setIgnoreSchemaBuilder(ignoreSchemaBuilderButton.getSelection());
    values.setIgnoreDebugOptions(ignoreDebugOptionsButton.getSelection());
    values.setIgnoreMissingDependencyRanges(ignoreMissingDependencyRangesButton.getSelection());
    values.setIgnoreMissingExportVersions(ignoreMissingExportVersionsButton.getSelection());
    values.setIgnoreFeatureContentRedundancy(ignoreFeatureContentRedundancyButton.getSelection());
    values.setIgnoreFeatureContentChanges(ignoreFeatureContentChangesButton.getSelection());
    super.okPressed();
  }

  public String getReleasePath()
  {
    return values.getReleasePath();
  }

  public String getValidatorClassName()
  {
    return values.getValidatorClassName();
  }

  public boolean isIgnoreMalformedVersions()
  {
    return values.isIgnoreMalformedVersions();
  }

  public boolean isIgnoreFeatureNature()
  {
    return values.isIgnoreFeatureNature();
  }

  public boolean isIgnoreSchemaBuilder()
  {
    return values.isIgnoreSchemaBuilder();
  }

  public boolean isIgnoreDebugOptions()
  {
    return values.isIgnoreDebugOptions();
  }

  public boolean isIgnoreMissingDependencyRanges()
  {
    return values.isIgnoreMissingDependencyRanges();
  }

  public boolean isIgnoreMissingExportVersions()
  {
    return values.isIgnoreMissingExportVersions();
  }

  public boolean isIgnoreFeatureContentRedundancy()
  {
    return values.isIgnoreFeatureContentRedundancy();
  }

  public boolean isIgnoreFeatureContentChanges()
  {
    return values.isIgnoreFeatureContentChanges();
  }

  public void applyTo(IProject project) throws CoreException
  {
    values.applyTo(project);
  }

  public int size()
  {
    return values.size();
  }

  public boolean isEmpty()
  {
    return values.isEmpty();
  }

  public String get(Object key)
  {
    return values.get(key);
  }

  public boolean containsKey(Object key)
  {
    return values.containsKey(key);
  }

  public String put(String key, String value)
  {
    return values.put(key, value);
  }

  public void putAll(Map<? extends String, ? extends String> m)
  {
    values.putAll(m);
  }

  public String remove(Object key)
  {
    return values.remove(key);
  }

  public void clear()
  {
    values.clear();
  }

  public boolean containsValue(Object value)
  {
    return values.containsValue(value);
  }

  public Set<String> keySet()
  {
    return values.keySet();
  }

  public Collection<String> values()
  {
    return values.values();
  }

  public Set<java.util.Map.Entry<String, String>> entrySet()
  {
    return values.entrySet();
  }

  @Override
  public boolean equals(Object o)
  {
    return values.equals(o);
  }

  @Override
  public int hashCode()
  {
    return values.hashCode();
  }
}
