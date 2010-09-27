/*
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyReferenceCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyReferenceRequest;

/**
 * @generated
 */
public class AClassAssociationsItemSemanticEditPolicy extends AcoreBaseItemSemanticEditPolicy
{

  /**
   * @generated
   */
  public AClassAssociationsItemSemanticEditPolicy()
  {
    super(AcoreElementTypes.AClassAssociations_4003);
  }

  /**
   * @generated
   */
  protected Command getDestroyReferenceCommand(DestroyReferenceRequest req)
  {
    return getGEFWrapper(new DestroyReferenceCommand(req));
  }

}
