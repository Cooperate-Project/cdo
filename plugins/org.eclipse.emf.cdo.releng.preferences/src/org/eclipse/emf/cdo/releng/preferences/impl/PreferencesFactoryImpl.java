/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.preferences.impl;

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.PreferencesFactory;
import org.eclipse.emf.cdo.releng.preferences.PreferencesPackage;
import org.eclipse.emf.cdo.releng.preferences.Property;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PreferencesFactoryImpl extends EFactoryImpl implements PreferencesFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static PreferencesFactory init()
  {
    try
    {
      PreferencesFactory thePreferencesFactory = (PreferencesFactory)EPackage.Registry.INSTANCE
          .getEFactory(PreferencesPackage.eNS_URI);
      if (thePreferencesFactory != null)
      {
        return thePreferencesFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new PreferencesFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferencesFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case PreferencesPackage.PREFERENCE_NODE:
      return createPreferenceNode();
    case PreferencesPackage.PROPERTY:
      return createProperty();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case PreferencesPackage.ESCAPED_STRING:
      return createEscapedStringFromString(eDataType, initialValue);
    case PreferencesPackage.URI:
      return createURIFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case PreferencesPackage.ESCAPED_STRING:
      return convertEscapedStringToString(eDataType, instanceValue);
    case PreferencesPackage.URI:
      return convertURIToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferenceNode createPreferenceNode()
  {
    PreferenceNodeImpl preferenceNode = new PreferenceNodeImpl();
    return preferenceNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Property createProperty()
  {
    PropertyImpl property = new PropertyImpl();
    return property;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createEscapedString(String literal)
  {
    return (String)super.createFromString(PreferencesPackage.Literals.ESCAPED_STRING, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createEscapedStringFromString(EDataType eDataType, String initialValue)
  {
    return createEscapedString(initialValue);
  }

  private static final String[] ESCAPES = { "\\000", "\\001", "\\002", "\\003", "\\004", "\\005", "\\006", "\\007",
      "\\010", "\\t", "\\n", "\\013", "\\014", "\\r", "\\016", "\\017", "\\020", "\\021", "\\022", "\\023", "\\024",
      "\\025", "\\026", "\\027", "\\030", "\\031", "\\032", "\\033", "\\034", "\\035", "\\036", "\\037" };

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createEscapedStringFromString(String literal)
  {
    if (literal == null)
    {
      return null;
    }

    StringBuilder result = new StringBuilder();
    for (int i = 0, length = literal.length(); i < length; ++i)
    {
      char c = literal.charAt(i);
      if (c == '\\')
      {
        if (++i < length)
        {
          c = literal.charAt(i);
          if (c == 't')
          {
            result.append('\t');
            continue;
          }
          else if (c == 'r')
          {
            result.append('\r');
            continue;
          }
          else if (c == 'n')
          {
            result.append('\n');
            continue;
          }
          else if (c == '\\')
          {
            result.append('\\');
            continue;
          }
          else if (i + 2 < length && c >= '0' && c <= '7' && literal.charAt(i + 1) >= '0'
              && literal.charAt(i + 1) <= '7' && literal.charAt(i + 2) >= '0' && literal.charAt(i + 2) <= '7')
          {
            result.append((char)Integer.parseInt(literal.substring(i, i + 3), 8));
            i += 2;
            continue;
          }
        }
      }
      result.append(c);
    }
    return result.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEscapedString(String instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }

    StringBuilder result = new StringBuilder();
    for (int i = 0, length = instanceValue.length(); i < length; ++i)
    {
      char c = instanceValue.charAt(i);
      if (c < ESCAPES.length)
      {
        result.append(ESCAPES[c]);
      }
      else if (c == '\\')
      {
        result.append("\\\\");
      }
      else if (c == '\177')
      {
        result.append("\\177");
      }
      else
      {
        result.append(c);
      }
    }
    return result.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertEscapedStringToString(EDataType eDataType, Object instanceValue)
  {
    return convertEscapedString((String)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public URI createURI(String literal)
  {
    if (literal == null)
    {
      return null;
    }

    String[] segments = literal.split("/");
    int length = segments.length;
    if (length == 0)
    {
      return URI.createHierarchicalURI(null, "", null, null, null);
    }

    URI result = URI.createURI("");
    StringBuilder property = null;
    boolean startProperty = false;
    int start = -1;
    for (int i = 0; i < length; ++i)
    {
      String segment = segments[i];
      if (property != null)
      {
        if (startProperty)
        {
          property.append('/');
        }
        else
        {
          startProperty = true;
        }

        property.append(segment);
      }
      else if (segment.length() == 0)
      {
        if (i == 0)
        {
          if (i != length - 1)
          {
            result = URI.createHierarchicalURI(null, "", null, null, null);
          }

          start = 1;
        }
        else
        {
          property = new StringBuilder();
        }
      }
      else if (i == start)
      {
        result = URI.createHierarchicalURI(null, URI.encodeAuthority(segment, false), null, null, null);
      }
      else
      {
        result = result.appendSegment(URI.encodeSegment(segment, false));
      }
    }

    if (property != null)
    {
      result = result.appendSegment(URI.encodeSegment(property.toString(), false));
    }

    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public URI createURIFromString(EDataType eDataType, String initialValue)
  {
    return createURI(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertURI(URI instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }

    StringBuilder result = new StringBuilder();
    String authority = instanceValue.authority();
    if (authority != null)
    {
      result.append('/');
      result.append(URI.decode(authority));
    }

    for (String segment : instanceValue.segments())
    {
      if (result.length() != 0)
      {
        result.append('/');
      }

      String decodedSegment = URI.decode(segment);
      if (decodedSegment.contains("/"))
      {
        result.append('/');
      }

      result.append(decodedSegment);
    }

    return result.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertURIToString(EDataType eDataType, Object instanceValue)
  {
    return convertURI((URI)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferencesPackage getPreferencesPackage()
  {
    return (PreferencesPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static PreferencesPackage getPackage()
  {
    return PreferencesPackage.eINSTANCE;
  }

} // PreferencesFactoryImpl
