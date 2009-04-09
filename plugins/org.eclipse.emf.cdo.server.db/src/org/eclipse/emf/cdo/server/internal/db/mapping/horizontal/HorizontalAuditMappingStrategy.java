/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public class HorizontalAuditMappingStrategy extends AbstractHorizontalMappingStrategy
{
  @Override
  public boolean hasAuditSupport()
  {
    return true;
  }

  @Override
  public boolean hasDeltaSupport()
  {
    return false;
  }

  @Override
  public IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalAuditClassMapping(this, eClass);
  }

  @Override
  public IListMapping doCreateManyMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new ListTableAuditMapping(this, containingClass, feature);
  }
}
