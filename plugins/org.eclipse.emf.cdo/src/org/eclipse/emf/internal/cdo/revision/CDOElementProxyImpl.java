/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.internal.cdo.revision;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDORevisionManager;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOElementProxy;
import org.eclipse.emf.spi.cdo.InternalCDORevisionManager;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class CDOElementProxyImpl implements CDOElementProxy
{
  private int index;

  public CDOElementProxyImpl(int index)
  {
    this.index = index;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public Object resolve(CDORevisionManager revisionManager, CDORevision revision, EStructuralFeature feature, int index)
  {
    return ((InternalCDORevisionManager)revisionManager).resolveElementProxy(revision, feature, index, getIndex());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOElementProxy[{0}]", index); //$NON-NLS-1$
  }
}
