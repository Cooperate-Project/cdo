/**
 * Copyright (c) 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.teneo.hibernate.annotations.HbEDataTypeAnnotator;

/**
 * Makes sure that the default type is a String.
 * 
 * @author Martin Taal
 * @since 3.0
 */
public class CDOEDataTypeAnnotator extends HbEDataTypeAnnotator
{
  public CDOEDataTypeAnnotator()
  {
  }

  @Override
  public String getDefaultUserType()
  {
    return "string"; //$NON-NLS-1$
  }
}
