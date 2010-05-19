/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */

package org.eclipse.emf.cdo.server.internal.objectivity;

import org.eclipse.emf.cdo.server.internal.objectivity.db.FdManager;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreConfig;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ObjectivityStoreConfig extends Lifecycle implements IObjectivityStoreConfig
{
  private static final long serialVersionUID = 1L;

  FdManager fdManager = new FdManager();

  public ObjectivityStoreConfig()
  {
    // fdManager.deleteFD();
    fdManager.configure();
  }

  public ObjectivityStoreConfig(Element storeConfig)
  {
    // TODO - implement me!!!
    // for now we'll just call the default configuration...
    getFdProperties(storeConfig);
    fdManager.configure();
  }

  public ObjectivityStoreConfig(String name)
  {
    // create an FD with that name.
    fdManager.configure(name);
  }

  @Override
  public void doActivate()
  {
    System.out.println("ObjectivityStoreConfig.doActivate()");
    fdManager.deleteFD();
    fdManager.configure();
  }

  @Override
  public void doDeactivate()
  {
    System.out.println("ObjectivityStoreConfig.doDeactivate()");
    fdManager.deleteFD();
  }

  public String getFdName()
  {
    return fdManager.getFd();
  }

  public void resetFD()
  {
    System.out.println("ObjectivityStoreConfig.resetFD() - Start.");
    fdManager.removeData();
    System.out.println("ObjectivityStoreConfig.resetFD() - END.");
  }

  private void getFdProperties(Element storeConfig)
  {
    NodeList fdConfigs = storeConfig.getElementsByTagName("fdConfig"); //$NON-NLS-1$
    if (fdConfigs.getLength() != 1)
    {
      throw new IllegalStateException("FD configuration is missing"); //$NON-NLS-1$
    }

    Element fdConfig = (Element)fdConfigs.item(0);
    String fdName = fdConfig.getAttribute("name"); //$NON-NLS-1$
    String lockServerHost = fdConfig.getAttribute("lockServerHost"); //$NON-NLS-1$
    String fdDirPath = fdConfig.getAttribute("fdDirPath"); //$NON-NLS-1$
    //    String dbDirPath = fdConfig.getAttribute("dbDirPath"); //$NON-NLS-1$
    String fdFileHost = fdConfig.getAttribute("fdFileHost"); //$NON-NLS-1$
    String fdNumber = fdConfig.getAttribute("fdNumber"); //$NON-NLS-1$
    String pageSize = fdConfig.getAttribute("pageSize"); //$NON-NLS-1$

    fdManager.setFdName(fdName);
    fdManager.setFdDirPath(fdDirPath);
    fdManager.setFdNumber(fdNumber);
    fdManager.setFdFileHost(fdFileHost);
    fdManager.setLockServerHost(lockServerHost);
    fdManager.setPageSize(pageSize);

  }
}
