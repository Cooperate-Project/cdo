/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.security;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.BaseDialog;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CredentialsDialog extends BaseDialog<Viewer>
{
  private static final String TITLE = Messages.getString("CredentialsDialog_0"); //$NON-NLS-1$

  private static final String MESSAGE = Messages.getString("CredentialsDialog_1"); //$NON-NLS-1$

  private Text userIDControl;

  private Text passwordControl;

  private IPasswordCredentials credentials;

  public CredentialsDialog(Shell shell)
  {
    super(shell, DEFAULT_SHELL_STYLE | SWT.APPLICATION_MODAL, TITLE, MESSAGE, OM.Activator.INSTANCE.getDialogSettings());
  }

  public IPasswordCredentials getCredentials()
  {
    return credentials;
  }

  @Override
  protected void createUI(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(UIUtil.createGridData());

    new Label(composite, SWT.NONE).setText(Messages.getString("CredentialsDialog_2")); //$NON-NLS-1$
    userIDControl = new Text(composite, SWT.BORDER);

    new Label(composite, SWT.NONE).setText(Messages.getString("CredentialsDialog_3")); //$NON-NLS-1$
    passwordControl = new Text(composite, SWT.BORDER | SWT.PASSWORD);
  }

  @Override
  protected void okPressed()
  {
    String userID = userIDControl.getText();
    String password = passwordControl.getText();
    credentials = new PasswordCredentials(userID, password.toCharArray());
    super.okPressed();
  }
}
