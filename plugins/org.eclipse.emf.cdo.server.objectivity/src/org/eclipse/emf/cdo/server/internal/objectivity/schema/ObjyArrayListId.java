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

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.as.app.ooBaseType;
import com.objy.db.app.ooId;

/**
 * @author Simon McDuff
 */
public class ObjyArrayListId extends ObjyArrayList<ooId>
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyArrayListId.class);

  static public String className = "ObjyArrayListId";

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(ObjyArrayListId.className) == null
        && top_mod.resolve_proposed_class(ObjyArrayListId.className) == null)
    {

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Schema not found for ooArrayListId. Adding ooArrayListId");
      }

      boolean inProcess = top_mod.proposed_classes().hasNext();

      // Proposed_Class A = new Proposed_Class(ooArrayListId.ClassName);
      Proposed_Class A = top_mod.propose_new_class(ObjyArrayListId.className);

      A.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj" /* "ooObj" */);

      A.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyArrayList.sizeName, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT32 // Type of numeric data
          ); // Default value

      A.add_varray_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyArrayList.arrayName, // Attribute name
          1, // # elements in fixed-size array
          false, "ooObj");

      // top_mod.propose_new_class(A);
      if (!inProcess)
      {
        top_mod.activate_proposals(true, true);
      }

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("SCHEMA changed : ooArrayListId added");
      }
    }

  }

  public ObjyArrayListId(Class_Object classObject)
  {
    super(classObject);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void setValue(long index, ooId newValue)
  {
    getVArray().update();
    getVArray().set_ooId(index, newValue);
  }

  @Override
  protected ooId getValue(long index)
  {
    // System.out.println(" - ooArrayListId.getValue() at index: " + index +
    // "  for classObject: " + classObject.objectID().getStoreString() );
    return getVArray().get_ooId(index);
  }

  public ooId[] getAll(int index, int chunkSize)
  {
    long size = size();
    // System.out.println("ooArrayListId.getAll() - size:" + size);

    if (chunkSize != -1)
    {
      size = Math.min(size, chunkSize);
    }

    ooId[] ooIds = new ooId[(int)size];
    for (int i = 0; i < size; i++)
    {
      ooIds[i] = getValue(i + index);
    }
    return ooIds;
  }

}
