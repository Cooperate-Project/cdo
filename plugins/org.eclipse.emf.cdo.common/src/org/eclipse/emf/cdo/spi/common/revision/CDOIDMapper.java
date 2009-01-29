/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;

import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOIDMapper implements CDOReferenceAdjuster
{
  private Map<CDOIDTemp, CDOID> idMappings;

  public CDOIDMapper(Map<CDOIDTemp, CDOID> idMappings)
  {
    this.idMappings = idMappings;
  }

  public Map<CDOIDTemp, CDOID> getIDMappings()
  {
    return idMappings;
  }

  public Object adjustReference(Object value)
  {
    return CDORevisionUtil.remapID(value, idMappings);
  }
}
