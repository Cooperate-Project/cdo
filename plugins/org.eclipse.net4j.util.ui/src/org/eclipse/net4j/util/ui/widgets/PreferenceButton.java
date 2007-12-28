/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.om.pref.OMPreference;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 */
public class PreferenceButton
{
  private OMPreference<Boolean> preference;

  private Button button;

  public PreferenceButton(Composite parent, int style, String text, final OMPreference<Boolean> preference)
  {
    this.preference = preference;

    button = new Button(parent, style);
    button.setText(text);
    button.setSelection(preference.getValue());
    button.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        preference.setValue(button.getSelection());
      }
    });
  }

  public OMPreference<Boolean> getPreference()
  {
    return preference;
  }

  public Button getButton()
  {
    return button;
  }

  public int getAlignment()
  {
    return button.getAlignment();
  }

  public Image getImage()
  {
    return button.getImage();
  }

  public boolean getSelection()
  {
    return button.getSelection();
  }

  public String getText()
  {
    return button.getText();
  }

  public void setAlignment(int alignment)
  {
    button.setAlignment(alignment);
  }

  public void setImage(Image image)
  {
    button.setImage(image);
  }

  public void setSelection(boolean selected)
  {
    button.setSelection(selected);
  }

  public void setText(String string)
  {
    button.setText(string);
  }

  public boolean setFocus()
  {
    return button.setFocus();
  }
}
