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
package org.eclipse.emf.cdo.session;

import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.ecore.EPackage;

/**
 * Can only be used with Eclipse running!
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOPackageTypeRegistry extends IRegistry<String, CDOPackageType>
{
  // public static final CDOPackageTypeRegistry INSTANCE =
  // org.eclipse.emf.internal.cdo.session.CDOPackageTypeRegistryImpl.INSTANCE;

  public void register(EPackage ePackage);

  public void registerLegacy(String packageURI);

  public void registerNative(String packageURI);
}
