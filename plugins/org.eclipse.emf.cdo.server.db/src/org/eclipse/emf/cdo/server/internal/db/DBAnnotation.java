/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Kai Schlamp
 */
public enum DBAnnotation
{
  TABLE_NAME("tableName"), //
  COLUMN_NAME("columnName"), //
  COLUMN_TYPE("columnType"), //
  COLUMN_LENGTH("columnLength");

  public static final String SOURCE_URI = "http://www.eclipse.org/CDO/DBStore";

  private String keyword;

  private DBAnnotation(String keyword)
  {
    this.keyword = keyword;
  }

  public String getKeyword()
  {
    return keyword == null ? super.toString() : keyword;
  }

  /**
   * @return A non-empty string or <code>null</code>.
   */
  public String getValue(EModelElement element)
  {
    String value = EcoreUtil.getAnnotation(element, SOURCE_URI, keyword);
    if (value != null && value.length() == 0)
    {
      return null;
    }

    return value;
  }

  @Override
  public String toString()
  {
    return getKeyword();
  }
}
