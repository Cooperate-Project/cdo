/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/226778    
 *    Simon McDuff - http://bugs.eclipse.org/213402   
 **************************************************************************/
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalTempImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaRangeImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.spi.common.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.CDOIDLibraryDescriptorImpl;
import org.eclipse.emf.cdo.spi.common.CDOIDLongImpl;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOIDUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MODEL, CDOIDUtil.class);

  private CDOIDUtil()
  {
  }

  public static long getLong(CDOID id)
  {
    if (id == null)
    {
      return 0L;
    }

    switch (id.getType())
    {
    case NULL:
      return 0L;

    case OBJECT:
    case LEGACY_OBJECT:
      if (id instanceof CDOIDLongImpl)
      {
        return ((CDOIDLongImpl)id).getLongValue();
      }

      throw new IllegalArgumentException("Unknown CDOIDObject implementation: " + id.getClass().getName());

    case META:
      return ((CDOIDMeta)id).getLongValue();

    case TEMP_META:
    case TEMP_OBJECT:
      throw new IllegalArgumentException("id instanceof CDOIDTemp");

    default:
      throw new ImplementationError();
    }
  }

  public static CDOIDTemp createTempMeta(int value)
  {
    return new CDOIDTempMetaImpl(value);
  }

  public static CDOIDTemp createTempObject(int value)
  {
    return new CDOIDTempObjectImpl(value);
  }

  /**
   * @since 2.0
   */
  public static CDOIDExternal createExternal(String uri)
  {
    return new CDOIDExternalImpl(uri);
  }

  /**
   * @since 2.0
   */
  public static CDOIDExternal createExternalTemp(String uri)
  {
    return new CDOIDExternalTempImpl(uri);
  }

  public static CDOID createLong(long value)
  {
    if (value == 0L)
    {
      return CDOID.NULL;
    }

    return new CDOIDLongImpl(value);
  }

  /**
   * Format of the URI fragment.
   * <p>
   * Non-legacy: <code>&lt;ID TYPE>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * <p>
   * Legacy: <code>&lt;ID TYPE>/&lt;PACKAGE URI>/&lt;CLASSIFIER ID>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * 
   * @since 2.0
   */
  public static CDOID read(String uriFragment, CDOIDObjectFactory factory)
  {
    byte ordinal = Byte.valueOf(uriFragment.substring(0, 1));
    if (TRACER.isEnabled())
    {
      try
      {
        String type = Type.values()[ordinal].toString();
        TRACER.format("Reading CDOID of type {0} ({1})", ordinal, type);
      }
      catch (RuntimeException ex)
      {
        TRACER.trace(ex);
      }
    }

    Type type = Type.values()[ordinal];
    String fragment = uriFragment.substring(2);
    switch (type)
    {
    case NULL:
      return CDOID.NULL;

    case TEMP_OBJECT:
      return new CDOIDTempObjectImpl(Integer.valueOf(fragment));

    case TEMP_META:
      return new CDOIDTempMetaImpl(Integer.valueOf(fragment));

    case META:
      return new CDOIDMetaImpl(Long.valueOf(fragment));

    case EXTERNAL_OBJECT:
      return new CDOIDExternalImpl(fragment);

    case EXTERNAL_TEMP_OBJECT:
      return new CDOIDExternalTempImpl(fragment);

    case OBJECT:
    {
      CDOIDObject id = factory.createCDOIDObject(fragment);
      ((AbstractCDOID)id).read(fragment);
      return id;
    }

    case LEGACY_OBJECT:
    {
      int packageIndex = fragment.indexOf("/");
      String packageURI = fragment.substring(0, packageIndex);
      int classifierIndex = fragment.indexOf("/", packageIndex + 1);

      String strClassifier = fragment.substring(packageIndex, classifierIndex);
      int classifierID = Integer.valueOf(strClassifier);

      CDOClassRef cdoClassRef = CDOModelUtil.createClassRef(packageURI, classifierID);
      CDOIDObject id = factory.createCDOIDObject(fragment.substring(classifierIndex + 1));
      ((AbstractCDOID)id).read(fragment);
      return id.asLegacy(cdoClassRef);
    }

    default:
      throw new IllegalArgumentException("Invalid ID type : " + uriFragment);
    }
  }

  /**
   * Format of the uri fragment.
   * <p>
   * Non-legacy: <code>&lt;ID TYPE>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * <p>
   * Legacy: <code>&lt;ID TYPE>/&lt;PACKAGE URI>/&lt;CLASSIFIER ID>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * 
   * @since 2.0
   */
  public static void write(StringBuilder builder, CDOID id)
  {
    if (id == null)
    {
      id = CDOID.NULL;
    }

    Type type = id.getType();
    int ordinal = type.ordinal();
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing CDOID of type {0} ({1})", ordinal, type);
    }

    builder.append(ordinal);
    switch (type)
    {
    case NULL:
    case TEMP_OBJECT:
    case TEMP_META:
    case META:
    case OBJECT:
      break;

    case LEGACY_OBJECT:
      CDOIDObject legacy = (CDOIDObject)id;
      builder.append("/" + legacy.getClassRef().getPackageURI());
      builder.append("/" + legacy.getClassRef().getClassifierID());
      break;

    default:
      throw new ImplementationError();
    }

    builder.append("/" + id.toURIFragment());
  }

  public static CDOIDMeta createMeta(long value)
  {
    return new CDOIDMetaImpl(value);
  }

  public static CDOIDMetaRange createMetaRange(CDOID lowerBound, int count)
  {
    return new CDOIDMetaRangeImpl(lowerBound, count);
  }

  public static CDOIDLibraryDescriptor readLibraryDescriptor(ExtendedDataInput in) throws IOException
  {
    return new CDOIDLibraryDescriptorImpl(in);
  }

  public static CDOIDAndVersion createIDAndVersion(CDOID id, int version)
  {
    return new CDOIDAndVersionImpl(id, version);
  }
}
