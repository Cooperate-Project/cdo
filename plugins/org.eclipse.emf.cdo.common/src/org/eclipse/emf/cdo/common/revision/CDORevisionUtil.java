/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.core.CDOFeatureMapEntryDataType;
import org.eclipse.emf.cdo.internal.common.model.core.CDOFeatureMapEntryDataTypeImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDORevisionUtil
{
  public static final Object UNINITIALIZED = new Uninitialized();

  private CDORevisionUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static CDORevision create(CDOClass cdoClass, CDOID id)
  {
    return new CDORevisionImpl(cdoClass, id);
  }

  /**
   * @since 2.0
   */
  public static CDORevision read(CDODataInput in) throws IOException
  {
    return new CDORevisionImpl(in);
  }

  /**
   * @since 2.0
   */
  public static CDOFeatureMapEntryDataType createFeatureMapEntry(String uri, Object value)
  {
    return new CDOFeatureMapEntryDataTypeImpl(uri, value);
  }

  public static Object remapID(Object value, Map<CDOIDTemp, CDOID> idMappings)
  {
    return CDORevisionImpl.remapID(value, idMappings);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Uninitialized
  {
    public Uninitialized()
    {
    }

    @Override
    public String toString()
    {
      return "UNINITIALIZED";
    }
  }
}
