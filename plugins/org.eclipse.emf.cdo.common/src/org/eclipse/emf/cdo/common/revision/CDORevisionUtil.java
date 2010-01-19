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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDOFeatureMapEntryImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionKeyImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
   * @since 3.0
   */
  public static CDORevisionKey createRevisionKey(CDOID id, CDOBranch branch, int version)
  {
    return new CDORevisionKeyImpl(id, branch, version);
  }

  /**
   * @since 3.0
   */
  public static CDORevisionKey createRevisionKey(CDORevisionKey source)
  {
    return new CDORevisionKeyImpl(source.getID(), source.getBranch(), source.getVersion());
  }

  /**
   * @since 2.0
   */
  public static FeatureMap.Entry createFeatureMapEntry(EStructuralFeature feature, Object value)
  {
    return new CDOFeatureMapEntryImpl(feature, value);
  }

  /**
   * @since 3.0
   */
  public static CDOFeatureMapEntry createCDOFeatureMapEntry()
  {
    return new CDOFeatureMapEntryImpl();
  }

  public static Object remapID(Object value, Map<CDOIDTemp, CDOID> idMappings)
  {
    return CDORevisionImpl.remapID(value, idMappings);
  }

  /**
   * @since 3.0
   */
  public static void dumpAllRevisions(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
  {
    ArrayList<CDOBranch> branches = new ArrayList<CDOBranch>(map.keySet());
    Collections.sort(branches);

    boolean first = true;
    for (CDOBranch branch : branches)
    {
      if (first)
      {
        first = false;
      }
      else
      {
        out.println();
      }

      List<CDORevision> revisions = map.get(branch);
      Collections.sort(revisions, new Comparator<CDORevision>()
      {
        public int compare(CDORevision rev1, CDORevision rev2)
        {
          int result = rev1.getID().compareTo(rev2.getID());
          if (result == 0)
          {
            int version1 = rev1.getVersion();
            int version2 = rev2.getVersion();
            result = version1 < version2 ? -1 : version1 == version2 ? 0 : 1;
          }

          return result;
        }
      });

      for (CDORevision revision : revisions)
      {
        out.println(revision);
      }
    }
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
      return Messages.getString("CDORevisionUtil.0"); //$NON-NLS-1$
    }
  }
}
