/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;

/**
 * 
 */
public class ObjectExistsConverter extends Converter
{

  public ObjectExistsConverter()
  {
    super(Object.class, Boolean.class);
  }

  public Object convert(Object fromObject)
  {
    return fromObject != null && SecurityModelUtil.isEditable(fromObject);
  }

  public static UpdateValueStrategy createUpdateValueStrategy()
  {
    UpdateValueStrategy result = new UpdateValueStrategy();
    result.setConverter(new ObjectExistsConverter());
    return result;
  }
}
