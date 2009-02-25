/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.io;

import org.eclipse.emf.cdo.common.TODO;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDODataOutputImpl implements CDODataOutput
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDODataOutputImpl.class);

  private ExtendedDataOutput out;

  public CDODataOutputImpl(ExtendedDataOutput out)
  {
    this.out = out;
  }

  public ExtendedDataOutput getDelegate()
  {
    return out;
  }

  public void write(byte[] b, int off, int len) throws IOException
  {
    out.write(b, off, len);
  }

  public void write(byte[] b) throws IOException
  {
    out.write(b);
  }

  public void write(int b) throws IOException
  {
    out.write(b);
  }

  public void writeBoolean(boolean v) throws IOException
  {
    out.writeBoolean(v);
  }

  public void writeByte(int v) throws IOException
  {
    out.writeByte(v);
  }

  public void writeByteArray(byte[] b) throws IOException
  {
    out.writeByteArray(b);
  }

  public void writeBytes(String s) throws IOException
  {
    out.writeBytes(s);
  }

  public void writeChar(int v) throws IOException
  {
    out.writeChar(v);
  }

  public void writeChars(String s) throws IOException
  {
    out.writeChars(s);
  }

  public void writeDouble(double v) throws IOException
  {
    out.writeDouble(v);
  }

  public void writeFloat(float v) throws IOException
  {
    out.writeFloat(v);
  }

  public void writeInt(int v) throws IOException
  {
    out.writeInt(v);
  }

  public void writeLong(long v) throws IOException
  {
    out.writeLong(v);
  }

  public void writeObject(Object object) throws IOException
  {
    out.writeObject(object);
  }

  public void writeShort(int v) throws IOException
  {
    out.writeShort(v);
  }

  public void writeString(String str) throws IOException
  {
    out.writeString(str);
  }

  public void writeUTF(String str) throws IOException
  {
    out.writeUTF(str);
  }

  public void writeCDOPackageUnit(CDOPackageUnit packageUnit) throws IOException
  {
    writeBoolean(packageUnit.isDynamic());
    ((InternalCDOPackageUnit)packageUnit).write(this);
  }

  public void writeCDOPackageInfo(CDOPackageInfo packageInfo) throws IOException
  {
    ((InternalCDOPackageInfo)packageInfo).write(this);
  }

  public void writeEClassifierRef(CDOClassifierRef eClassifierRef) throws IOException
  {
    eClassifierRef.write(this);
  }

  public void writeEClassifierRef(EClassifier eClassifier) throws IOException
  {
    writeEClassifierRef(new CDOClassifierRef(eClassifier));
  }

  public void writeEPackageURI(String uri) throws IOException
  {
    getPackageURICompressor().writePackageURI(this, uri);
  }

  public void writeEPackage(EPackage cdoPackage) throws IOException
  {
    TODO.writeEPackage(this, cdoPackage);
  }

  public void writeCDOID(CDOID id) throws IOException
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

    writeByte(ordinal);
    ((AbstractCDOID)id).write(this);
  }

  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException
  {
    ((CDOIDAndVersionImpl)idAndVersion).write(this);
  }

  public void writeCDOIDMetaRange(CDOIDMetaRange metaRange) throws IOException
  {
    if (metaRange == null)
    {
      writeBoolean(false);
    }
    else
    {
      writeBoolean(true);
      writeCDOID(metaRange.getLowerBound());
      writeInt(metaRange.size());
    }
  }

  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException
  {
    if (revision != null)
    {
      writeBoolean(true);
      ((InternalCDORevision)revision).write(this, referenceChunk);
    }
    else
    {
      writeBoolean(false);
    }
  }

  public void writeCDOList(CDOList list, EStructuralFeature feature, int referenceChunk) throws IOException
  {
    // TODO Simon: Could most of this stuff be moved into the list?
    // (only if protected methods of this class don't need to become public)
    int size = list == null ? 0 : list.size();
    if (size > 0)
    {
      // Need to adjust the referenceChunk in case where we do not have enough value in the list.
      // Even if the referenceChunk is specified, a provider of data could have override that value.
      int sizeToLook = referenceChunk == CDORevision.UNCHUNKED ? size : Math.min(referenceChunk, size);
      for (int i = 0; i < sizeToLook; i++)
      {
        Object element = list.get(i, false);
        if (element == CDORevisionUtil.UNINITIALIZED)
        {
          referenceChunk = i;
          break;
        }
      }
    }

    if (referenceChunk != CDORevision.UNCHUNKED && referenceChunk < size)
    {
      // This happens only on server-side
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing feature {0}: size={1}, referenceChunk={2}", feature, size, referenceChunk);
      }

      out.writeInt(-size);
      out.writeInt(referenceChunk);
      size = referenceChunk;
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing feature {0}: size={1}", feature, size);
      }

      out.writeInt(size);
    }

    for (int j = 0; j < size; j++)
    {
      Object value = list.get(j, false);
      if (value != null && feature instanceof EReference)
      {
        value = getIDProvider().provideCDOID(value);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("    " + value);
      }

      TODO.writeFeatureValue(this, value, feature);
    }
  }

  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException
  {
    ((CDORevisionDeltaImpl)revisionDelta).write(this);
  }

  public void writeCDOFeatureDelta(CDOFeatureDelta featureDelta, EClass cdoClass) throws IOException
  {
    ((CDOFeatureDeltaImpl)featureDelta).write(this, cdoClass);
  }

  public void writeCDORevisionOrPrimitive(Object value) throws IOException
  {
    TODO.writeCDORevisionOrPrimitive(this, value);
  }

  public void writeCDORevisionOrPrimitiveOrClass(Object value) throws IOException
  {
    if (value instanceof EClass)
    {
      writeBoolean(true);
      writeEClassifierRef((EClass)value);
    }
    else
    {
      writeBoolean(false);
      writeCDORevisionOrPrimitive(value);
    }
  }

  public void writeCDOLockType(RWLockManager.LockType lockType) throws IOException
  {
    writeBoolean(lockType == RWLockManager.LockType.WRITE ? true : false);
  }

  protected abstract CDOPackageURICompressor getPackageURICompressor();
}
