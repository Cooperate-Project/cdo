/**
 * Copyright (c) 2004 - 2009 Springsite B.V. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - http://bugs.eclipse.org/238300
 */
package org.eclipse.emf.cdo.server.hibernate.internal.teneo;

import org.eclipse.emf.cdo.server.hibernate.internal.teneo.bundle.OM;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateMappingProvider;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.teneo.extension.ExtensionManager;
import org.eclipse.emf.teneo.extension.ExtensionManagerFactory;
import org.eclipse.emf.teneo.hibernate.cdo.CDOHelper;

import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Uses the ecore string in the ePackages of the store to generate a mapping.
 * 
 * @author Martin Taal
 * @author Eike Stepper
 */
public class TeneoHibernateMappingProvider extends HibernateMappingProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TeneoHibernateMappingProvider.class);

  private ExtensionManager extensionManager = ExtensionManagerFactory.getInstance().create();

  public TeneoHibernateMappingProvider()
  {
  }

  public ExtensionManager getExtensionManager()
  {
    return extensionManager;
  }

  @Override
  public HibernateStore getHibernateStore()
  {
    return (HibernateStore)super.getHibernateStore();
  }

  public void addMapping(Configuration configuration)
  {
    final String mapping = generateMapping();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Generated hibernate mapping:");
      TRACER.trace(mapping);
    }

    configuration.addXML(mapping);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Added mapping to configuration");
    }
  }

  // the passed modelObjects collection is defined as a collection of Objects
  // to prevent binary dependency on emf.
  public String generateMapping()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Generating Hibernate Mapping");
    }

    final Properties properties = HibernateUtil.getInstance().getPropertiesFromStore(getHibernateStore());

    // translate the list of EPackages to an array
    final List<EPackage> epacks = getHibernateStore().getPackageHandler().getEPackages();
    final EPackage[] ePackageArray = epacks.toArray(new EPackage[epacks.size()]);
    properties.put("teneo.mapping.also_map_as_class", "false");
    String hbm = CDOHelper.getInstance().generateMapping(ePackageArray, properties, extensionManager);
    System.err.println(hbm);
    // to solve an issue with older versions of teneo
    hbm = hbm.replaceAll("_cont", "cont");
    return hbm;
  }

  // this will check the global package registry and read the epackages from
  // there if the epackage is already present there
  protected List<EPackage> resolveSubPackages(EPackage ePackage)
  {
    final List<EPackage> result = new ArrayList<EPackage>();
    resolveSubPackages(ePackage, result);
    return result;
  }

  private void resolveSubPackages(EPackage ePackage, List<EPackage> result)
  {
    EPackage globalPackage = EPackage.Registry.INSTANCE.getEPackage(ePackage.getNsURI());
    if (globalPackage != null)
    {
      ePackage = globalPackage;
    }

    result.add(ePackage);
    for (EPackage subEPackage : ePackage.getESubpackages())
    {
      resolveSubPackages(subEPackage, result);
    }
  }
}
