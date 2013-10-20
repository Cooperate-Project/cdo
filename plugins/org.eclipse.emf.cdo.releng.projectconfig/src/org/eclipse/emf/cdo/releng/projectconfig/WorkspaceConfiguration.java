/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.projectconfig;

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Workspace Configuration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getProjects <em>Projects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getDefaultPreferenceNode <em>Default Preference Node</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getInstancePreferenceNode <em>Instance Preference Node</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getWorkspaceConfiguration()
 * @model
 * @generated
 */
public interface WorkspaceConfiguration extends EObject
{
  /**
   * Returns the value of the '<em><b>Projects</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.projectconfig.Project}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.projectconfig.Project#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Projects</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Projects</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getWorkspaceConfiguration_Projects()
   * @see org.eclipse.emf.cdo.releng.projectconfig.Project#getConfiguration
   * @model opposite="configuration" containment="true"
   * @generated
   */
  EList<Project> getProjects();

  /**
   * Returns the value of the '<em><b>Default Preference Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default Preference Node</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Preference Node</em>' reference.
   * @see #setDefaultPreferenceNode(PreferenceNode)
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getWorkspaceConfiguration_DefaultPreferenceNode()
   * @model required="true"
   * @generated
   */
  PreferenceNode getDefaultPreferenceNode();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getDefaultPreferenceNode <em>Default Preference Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Preference Node</em>' reference.
   * @see #getDefaultPreferenceNode()
   * @generated
   */
  void setDefaultPreferenceNode(PreferenceNode value);

  /**
   * Returns the value of the '<em><b>Instance Preference Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Instance Preference Node</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Instance Preference Node</em>' reference.
   * @see #setInstancePreferenceNode(PreferenceNode)
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getWorkspaceConfiguration_InstancePreferenceNode()
   * @model required="true"
   * @generated
   */
  PreferenceNode getInstancePreferenceNode();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getInstancePreferenceNode <em>Instance Preference Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Instance Preference Node</em>' reference.
   * @see #getInstancePreferenceNode()
   * @generated
   */
  void setInstancePreferenceNode(PreferenceNode value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  void applyPreferenceProfiles();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  void updatePreferenceProfileReferences();

} // WorkspaceConfiguration
