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
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.common.messages.Messages;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class CDOTypeImpl implements CDOType
{
  private static Map<Integer, CDOTypeImpl> ids = new HashMap<Integer, CDOTypeImpl>();

  private static final byte BOOLEAN_DEFAULT_PRIMITIVE = 0;

  private static final char CHARACTER_DEFAULT_PRIMITIVE = 0;

  private static final short SHORT_DEFAULT_PRIMITIVE = 0;

  public static final Boolean BOOLEAN_DEFAULT = new Boolean(false);

  public static final Byte BYTE_DEFAULT = new Byte(BOOLEAN_DEFAULT_PRIMITIVE);

  public static final Character CHARACTER_DEFAULT = new Character(CHARACTER_DEFAULT_PRIMITIVE);

  public static final Double DOUBLE_DEFAULT = new Double(0.0);

  public static final Float FLOAT_DEFAULT = new Float(0.0);

  public static final Integer INTEGER_DEFAULT = new Integer(0);

  public static final Long LONG_DEFAULT = new Long(0L);

  public static final Short SHORT_DEFAULT = new Short(SHORT_DEFAULT_PRIMITIVE);

  public static final CDOType BOOLEAN = new CDOTypeImpl("BOOLEAN", EcorePackage.EBOOLEAN, false, BOOLEAN_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      boolean v = (Boolean)(value == null ? getDefaultValue() : value);
      out.writeBoolean(v);
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      boolean v = in.readBoolean();
      return new Boolean(v);
    }
  };

  public static final CDOType BYTE = new CDOTypeImpl("BYTE", EcorePackage.EBYTE, false, BYTE_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeByte((Byte)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return new Byte(in.readByte());
    }
  };

  public static final CDOType CHAR = new CDOTypeImpl("CHAR", EcorePackage.ECHAR, false, CHARACTER_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeChar(((Character)(value == null ? getDefaultValue() : value)).charValue());
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return new Character(in.readChar());
    }
  };

  public static final CDOType DOUBLE = new CDOTypeImpl("DOUBLE", EcorePackage.EDOUBLE, false, DOUBLE_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeDouble((Double)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return new Double(in.readDouble());
    }
  };

  public static final CDOType FLOAT = new CDOTypeImpl("FLOAT", EcorePackage.EFLOAT, false, FLOAT_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeFloat((Float)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return new Float(in.readFloat());
    }
  };

  public static final CDOType INT = new CDOTypeImpl("INT", EcorePackage.EINT, false, INTEGER_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeInt((Integer)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return new Integer(in.readInt());
    }
  };

  public static final CDOType LONG = new CDOTypeImpl("LONG", EcorePackage.ELONG, false, LONG_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeLong((Long)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return new Long(in.readLong());
    }
  };

  public static final CDOType SHORT = new CDOTypeImpl("SHORT", EcorePackage.ESHORT, false, SHORT_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeShort((Short)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return new Short(in.readShort());
    }
  };

  public static final CDOType BIG_DECIMAL = new CDOTypeImpl("BIG_DECIMAL", EcorePackage.EBIG_DECIMAL, true) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value == null)
      {
        out.writeByteArray(null);
      }
      else
      {
        BigDecimal bigDecimal = (BigDecimal)value;
        out.writeByteArray(bigDecimal.unscaledValue().toByteArray());
        out.writeInt(bigDecimal.scale());
      }
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      byte[] array = in.readByteArray();
      if (array == null)
      {
        return null;
      }

      BigInteger unscaled = new BigInteger(array);
      int scale = in.readInt();
      return new BigDecimal(unscaled, scale);
    }
  };

  public static final CDOType BIG_INTEGER = new CDOTypeImpl("BIG_INTEGER", EcorePackage.EBIG_INTEGER, true) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value == null)
      {
        out.writeByteArray(null);
      }
      else
      {
        out.writeByteArray(((BigInteger)value).toByteArray());
      }
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      byte[] array = in.readByteArray();
      if (array == null)
      {
        return null;
      }

      return new BigInteger(array);
    }
  };

  public static final CDOType OBJECT = new CDOTypeImpl("OBJECT", EcorePackage.EOBJECT, true, CDOID.NULL) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value instanceof CDORevision)
      {
        out.writeCDOID(((CDORevision)value).getID());
      }
      else
      {
        out.writeCDOID((CDOID)value);
      }
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return in.readCDOID();
    }

    @Override
    public Object doAdjustReferences(CDOReferenceAdjuster adjuster, Object value)
    {
      return adjuster.adjustReference(value);
    }
  };

  public static final CDOType BOOLEAN_OBJECT = new ObjectType("BOOLEAN_OBJECT", EcorePackage.EBOOLEAN_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeBoolean((Boolean)value);
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readBoolean();
    }
  };

  public static final CDOType BYTE_OBJECT = new ObjectType("BYTE_OBJECT", EcorePackage.EBYTE_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeByte((Byte)value);
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readByte();
    }
  };

  public static final CDOType CHARACTER_OBJECT = new ObjectType("CHARACTER_OBJECT", EcorePackage.ECHARACTER_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeChar((Character)value);
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readChar();
    }
  };

  public static final CDOType DATE = new ObjectType("DATE", EcorePackage.EDATE) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeLong(((Date)value).getTime());
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return new Date(in.readLong());
    }
  };

  public static final CDOType DOUBLE_OBJECT = new ObjectType("DOUBLE_OBJECT", EcorePackage.EDOUBLE_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeDouble((Double)value);
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readDouble();
    }
  };

  public static final CDOType FLOAT_OBJECT = new ObjectType("FLOAT_OBJECT", EcorePackage.EFLOAT_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeFloat((Float)value);
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readFloat();
    }
  };

  public static final CDOType INTEGER_OBJECT = new ObjectType("INTEGER_OBJECT", EcorePackage.EINTEGER_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeInt((Integer)value);
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readInt();
    }
  };

  public static final CDOType LONG_OBJECT = new ObjectType("LONG_OBJECT", EcorePackage.ELONG_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeLong((Long)value);
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readLong();
    }
  };

  public static final CDOType SHORT_OBJECT = new ObjectType("SHORT_OBJECT", EcorePackage.ESHORT_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeShort((Short)value);
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readShort();
    }
  };

  public static final CDOType STRING = new CDOTypeImpl("STRING", EcorePackage.ESTRING, true) //$NON-NLS-1$
  {
    @SuppressWarnings("cast")
    @Override
    public Object copyValue(Object value)
    {
      return (String)value;
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeString((String)value);
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return in.readString();
    }
  };

  public static final CDOType BYTE_ARRAY = new CDOTypeImpl("BYTE_ARRAY", EcorePackage.EBYTE_ARRAY, true) //$NON-NLS-1$
  {
    @Override
    public Object copyValue(Object value)
    {
      if (value == null)
      {
        return null;
      }

      byte[] array = (byte[])value;
      byte[] result = new byte[array.length];
      System.arraycopy(value, 0, result, 0, array.length);
      return result;
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeByteArray((byte[])value);
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return in.readByteArray();
    }
  };

  /**
   * TODO Transfer integers!
   */
  public static final CDOType ENUM_ORDINAL = new ObjectType("ENUM_ORDINAL", 998) //$NON-NLS-1$
  {
    @SuppressWarnings("cast")
    @Override
    public Object copyValue(Object value)
    {
      return (Integer)value;
    }

    @Override
    public void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeInt((Integer)value);
    }

    @Override
    public Object doReadValue(CDODataInput in) throws IOException
    {
      return in.readInt();
    }

    @Override
    public Object convertToCDO(EClassifier type, Object value)
    {
      for (EEnumLiteral literal : ((EEnum)type).getELiterals())
      {
        if (literal == value || literal.getInstance() == value)
        {
          return literal.getValue();
        }
      }

      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOTypeImpl.23"), value)); //$NON-NLS-1$
    }

    @Override
    public Object convertToEMF(EClassifier type, Object value)
    {
      return ((EEnum)type).getEEnumLiteral((Integer)value).getInstance();
    }
  };

  public static final CDOType ENUM_LITERAL = new ObjectType("ENUM_LITERAL", 1001) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      EEnum eEnum;
      if (value instanceof EEnumLiteral)
      {
        eEnum = ((EEnumLiteral)value).getEEnum();
      }
      else
      {
        eEnum = findEnum(out.getPackageRegistry(), value);
      }

      out.writeCDOClassifierRef(eEnum);
      out.writeInt(((Enumerator)value).getValue());
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      EEnum eEnum = (EEnum)in.readCDOClassifierRefAndResolve();
      int ordinal = in.readInt();

      EEnumLiteral literal = eEnum.getEEnumLiteral(ordinal);
      if (literal == null)
      {
        throw new IllegalArgumentException("Enum literal " + ordinal + " not found in " + eEnum);
      }

      return literal.getInstance();
    }

    private EEnum findEnum(CDOPackageRegistry registry, Object value)
    {
      Set<String> keys = registry.getAllKeys();

      // First try all the packages that are already resolved
      for (String nsURI : keys)
      {
        Object possiblePackage = registry.getWithDelegation(nsURI, false);
        if (possiblePackage instanceof EPackage)
        {
          EPackage ePackage = (EPackage)possiblePackage;
          EEnum eEnum = findEnum(ePackage, value);
          if (eEnum != null)
          {
            return eEnum;
          }
        }
      }

      // Then try all the package descriptors
      for (String nsURI : keys)
      {
        Object possiblePackage = registry.getWithDelegation(nsURI, false);
        if (possiblePackage instanceof EPackage.Descriptor)
        {
          EPackage ePackage = registry.getEPackage(nsURI);
          EEnum eEnum = findEnum(ePackage, value);
          if (eEnum != null)
          {
            return eEnum;
          }
        }
      }

      throw new IllegalArgumentException("EENum instance " + value.getClass().getName() + " not supported");
    }

    private EEnum findEnum(EPackage ePackage, Object value)
    {
      for (EClassifier eClassifier : ePackage.getEClassifiers())
      {
        if (eClassifier instanceof EEnum)
        {
          EEnum eEnum = (EEnum)eClassifier;
          if (eEnum.getInstanceClass() != null && eEnum.getInstanceClass() == value.getClass())
          {
            return eEnum;
          }
        }
      }

      return null;
    }
  };

  public static final CDOType CUSTOM = new CDOTypeImpl("CUSTOM", 999, true) //$NON-NLS-1$
  {
    @SuppressWarnings("cast")
    @Override
    public Object copyValue(Object value)
    {
      return (String)value;
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeString((String)value);
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      return in.readString();
    }

    @Override
    public Object convertToEMF(EClassifier eType, Object value)
    {
      return EcoreUtil.createFromString((EDataType)eType, (String)value);
    }

    @Override
    public Object convertToCDO(EClassifier eType, Object value)
    {
      return EcoreUtil.convertToString((EDataType)eType, value);
    }
  };

  public static final CDOType FEATURE_MAP_ENTRY = new CDOTypeImpl("FEATURE_MAP_ENTRY", EcorePackage.EFEATURE_MAP_ENTRY, //$NON-NLS-1$
      false)
  {
    @Override
    public Object copyValue(Object value)
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature innerFeature = entry.getEStructuralFeature();
      Object innerValue = entry.getValue();
      CDOType innerType = CDOModelUtil.getType(innerFeature.getEType());

      Object innerCopy = innerType.copyValue(innerValue);
      return CDORevisionUtil.createFeatureMapEntry(innerFeature, innerCopy);
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    public Object readValue(CDODataInput in) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object doAdjustReferences(CDOReferenceAdjuster adjuster, Object value)
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature innerFeature = entry.getEStructuralFeature();
      Object innerValue = entry.getValue();
      CDOType innerType = CDOModelUtil.getType(innerFeature.getEType());

      Object innerCopy = innerType.adjustReferences(adjuster, innerValue);
      if (innerCopy != innerValue)
      {
        value = CDORevisionUtil.createFeatureMapEntry(innerFeature, innerCopy);
      }

      return value;
    }
  };

  public static final CDOType OBJECT_ARRAY = new ObjectType("OBJECT_ARRAY", 1000) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      final Object[] objects = (Object[])value;
      out.writeInt(objects.length);
      for (Object object : objects)
      {
        final CDOType cdoType;
        if (object instanceof BigDecimal)
        {
          cdoType = CDOType.BIG_DECIMAL;
        }
        else if (object instanceof BigInteger)
        {
          cdoType = CDOType.BIG_INTEGER;
        }
        else if (object instanceof Boolean)
        {
          cdoType = CDOType.BOOLEAN_OBJECT;
        }
        else if (object instanceof Byte)
        {
          cdoType = CDOType.BYTE_OBJECT;
        }
        else if (object instanceof byte[])
        {
          cdoType = CDOType.BYTE_ARRAY;
        }
        else if (object instanceof Character)
        {
          cdoType = CDOType.CHARACTER_OBJECT;
        }
        else if (object instanceof Date)
        {
          cdoType = CDOType.DATE;
        }
        else if (object instanceof Double)
        {
          cdoType = CDOType.DOUBLE_OBJECT;
        }
        else if (object instanceof EEnumLiteral)
        {
          cdoType = CDOType.ENUM_LITERAL;
        }
        else if (object instanceof FeatureMap.Entry)
        {
          cdoType = CDOType.FEATURE_MAP_ENTRY;
        }
        else if (object instanceof Float)
        {
          cdoType = CDOType.FLOAT_OBJECT;
        }
        else if (object instanceof Integer)
        {
          cdoType = CDOType.INTEGER_OBJECT;
        }
        else if (object instanceof Long)
        {
          cdoType = CDOType.LONG_OBJECT;
        }
        else if (object instanceof Short)
        {
          cdoType = CDOType.SHORT_OBJECT;
        }
        else if (object instanceof String)
        {
          cdoType = CDOType.STRING;
        }
        else if (object instanceof CDOID || object instanceof CDORevision)
        {
          cdoType = CDOType.OBJECT;
        }
        else if (object == null)
        {
          cdoType = CDOType.OBJECT;
        }
        else
        {
          throw new IllegalArgumentException("Object type " + object.getClass().getName() + " is not supported.");
        }

        out.writeInt(cdoType.getTypeID());
        cdoType.writeValue(out, object);
      }
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      int size = in.readInt();
      final Object[] objects = new Object[size];
      for (int i = 0; i < size; i++)
      {
        int typeID = in.readInt();
        CDOType cdoType = CDOModelUtil.getType(typeID);
        objects[i] = cdoType.readValue(in);
      }

      return objects;
    }

    @Override
    public Object doAdjustReferences(CDOReferenceAdjuster adjuster, Object value)
    {
      // CHECK: should the same object array be returned with updated values
      // or a new object array?
      final Object[] objects = (Object[])value;
      int i = 0;
      for (Object object : objects)
      {
        if (object instanceof CDOID)
        {
          objects[i++] = adjuster.adjustReference(object);
        }
        else
        {
          objects[i++] = object;
        }
      }

      return objects;
    }
  };

  private String name;

  private int typeID;

  private boolean canBeNull;

  private Object defaultValue;

  private CDOTypeImpl(String name, int typeID, boolean canBeNull, Object defaultValue)
  {
    this.name = name;
    this.typeID = typeID;
    this.canBeNull = canBeNull;
    this.defaultValue = defaultValue;
    ids.put(typeID, this);
  }

  private CDOTypeImpl(String name, int typeID, boolean canBeNull)
  {
    this(name, typeID, canBeNull, null);
  }

  public String getName()
  {
    return name;
  }

  public int getTypeID()
  {
    return typeID;
  }

  public boolean canBeNull()
  {
    return canBeNull;
  }

  public Object getDefaultValue()
  {
    return defaultValue;
  }

  @Override
  public String toString()
  {
    return name;
  }

  public Object copyValue(Object value)
  {
    return value == null ? getDefaultValue() : value;
  }

  public void write(CDODataOutput out) throws IOException
  {
    // TODO Use byte IDs
    out.writeInt(typeID);
  }

  final public Object adjustReferences(CDOReferenceAdjuster adjuster, Object value)
  {
    return value == null ? null : doAdjustReferences(adjuster, value);
  }

  protected Object doAdjustReferences(CDOReferenceAdjuster adjuster, Object value)
  {
    return value;
  }

  /**
   * @since 2.0
   */
  public Object convertToEMF(EClassifier feature, Object value)
  {
    return value;
  }

  /**
   * @since 2.0
   */
  public Object convertToCDO(EClassifier feature, Object value)
  {
    return value;
  }

  public static CDOType getType(int typeID)
  {
    CDOTypeImpl type = ids.get(typeID);
    if (type == null)
    {
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOModelUtil.6"), typeID));
    }

    return type;
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class ObjectType extends CDOTypeImpl
  {
    public ObjectType(String name, int typeID)
    {
      super(name, typeID, true);
    }

    public final void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value == null)
      {
        out.writeBoolean(false);
      }
      else
      {
        out.writeBoolean(true);
        doWriteValue(out, value);
      }
    }

    protected abstract void doWriteValue(CDODataOutput out, Object value) throws IOException;

    public final Object readValue(CDODataInput in) throws IOException
    {
      boolean notNull = in.readBoolean();
      if (notNull)
      {
        return doReadValue(in);
      }

      return null;
    }

    protected abstract Object doReadValue(CDODataInput in) throws IOException;
  }
}
