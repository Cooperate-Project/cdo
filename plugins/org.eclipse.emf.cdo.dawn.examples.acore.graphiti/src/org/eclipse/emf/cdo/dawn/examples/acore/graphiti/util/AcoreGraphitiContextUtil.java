/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util;

/**
 * @author Martin Fluegge
 */
public class AcoreGraphitiContextUtil
{
  public static String CONNECTTION_TYPE = "ConnectionType";

  public enum ConnectionType
  {
    SUBCLASSES, IMPLEMENTATIONS, AGGREGATIONS, COMPOSITIONS, ASSOCIATIONS
  }
}