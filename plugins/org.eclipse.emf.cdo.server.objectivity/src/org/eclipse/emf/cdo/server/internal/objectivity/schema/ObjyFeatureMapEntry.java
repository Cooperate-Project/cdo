/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.String_Value;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.as.app.ooBaseType;
import com.objy.db.app.ooId;

public class ObjyFeatureMapEntry
{
  // caching some details.
  protected long metaId;

  protected String tagName;

  protected ooId object;

  protected Class_Object classObject;

  public static final String MapEntryClassName = "ObjyFeatureMapEntry";

  public static final String MetaId = "metaId";

  public static final String EntryName = "tagName";

  public static final String EntryObject = "object";

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(MapEntryClassName) == null && top_mod.resolve_proposed_class(MapEntryClassName) == null)
    {
      // Proposed_Class B = new Proposed_Class(MapEntryClassName);
      Proposed_Class B = top_mod.propose_new_class(MapEntryClassName);
      B.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj");

      B.add_basic_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          ObjyFeatureMapEntry.MetaId, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT64 // type
      );

      B.add_embedded_class_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          ObjyFeatureMapEntry.EntryName, // Attribute name
          1, // # elements in fixed-size array
          "ooUtf8String"); // type
      B.add_ref_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyFeatureMapEntry.EntryObject, // Attribute name
          1, // # elements in fixed-size array
          "ooObj", // Type of numeric data
          false); // Short reference

      // top_mod.propose_new_class(B);
    }
  }

  /****
   * Factory.
   * 
   * @param tagName
   * @param oid
   */
  public ObjyFeatureMapEntry(String tagName, ooId oid, long metaId, ooId near)
  {
    this.tagName = tagName;
    object = oid;
    this.metaId = metaId;

    classObject = Class_Object.new_persistent_object(ObjySchema.getObjyClass(MapEntryClassName).getASClass(), near,
        false);
    Numeric_Value numericValue = new Numeric_Value(metaId);
    classObject.nset_numeric(MetaId, numericValue);
    String_Value stringValue = classObject.nget_string(EntryName);
    stringValue.update();
    String newValue = this.tagName;
    if (newValue == null)
    {
      newValue = "";
    }
    stringValue.set(newValue);

    classObject.nset_ooId(EntryObject, object);

  }

  public ObjyFeatureMapEntry(Class_Object classObject)
  {
    this.classObject = classObject;

    Numeric_Value numericValue = classObject.nget_numeric(MetaId);
    metaId = numericValue.longValue();

    String_Value value = classObject.nget_string(EntryName);
    // for objy10.0 -> this.tagName = (value == null || value.toString() == null || value.toString().isEmpty()) ? null :
    // value.toString();
    tagName = value == null || value.toString() == null ? null : value.toString();

    object = classObject.nget_ooId(EntryObject);
  }

  public String getTagName()
  {
    return tagName;
  }

  public void setTagName(String tagName)
  {
    this.tagName = tagName;
  }

  public ooId getObject()
  {
    return object;
  }

  public void setObject(ooId object)
  {
    this.object = object;
  }

  public ooId getOid()
  {
    return classObject.objectID();
  }

  public long getMetaId()
  {
    return metaId;
  }

  public void setMetaId(long metaId)
  {
    this.metaId = metaId;
  }
}
