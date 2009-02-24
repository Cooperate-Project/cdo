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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public final class FeatureServerInfo extends ServerInfo
{
  private FeatureServerInfo(int id)
  {
    super(id);
  }

  public static FeatureServerInfo setDBID(EStructuralFeature cdoFeature, int id)
  {
    FeatureServerInfo serverInfo = new FeatureServerInfo(id);
    ((InternalCDOFeature)cdoFeature).setServerInfo(serverInfo);
    return serverInfo;
  }
}
