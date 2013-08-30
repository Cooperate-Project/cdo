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
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.SetupFactory
 * @model kind="package"
 * @generated
 */
public interface SetupPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "setup";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/CDO/releng/setup/1.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "setup";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  SetupPackage eINSTANCE = org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskContainerImpl <em>Task Container</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupTaskContainerImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTaskContainer()
   * @generated
   */
  int SETUP_TASK_CONTAINER = 8;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl <em>Configuration</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfiguration()
   * @generated
   */
  int CONFIGURATION = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl <em>Project</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getProject()
   * @generated
   */
  int PROJECT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl <em>Branch</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.BranchImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBranch()
   * @generated
   */
  int BRANCH = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseVersionImpl <em>Eclipse Version</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseVersionImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipseVersion()
   * @generated
   */
  int ECLIPSE_VERSION = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl <em>Installable Unit</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getInstallableUnit()
   * @generated
   */
  int INSTALLABLE_UNIT = 15;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl <em>P2 Repository</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Repository()
   * @generated
   */
  int P2_REPOSITORY = 16;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl <em>Preferences</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getPreferences()
   * @generated
   */
  int PREFERENCES = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl <em>Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTask()
   * @generated
   */
  int SETUP_TASK = 7;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.LinkLocationTaskImpl <em>Link Location Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.LinkLocationTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getLinkLocationTask()
   * @generated
   */
  int LINK_LOCATION_TASK = 13;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl <em>Eclipse Preference Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipsePreferenceTask()
   * @generated
   */
  int ECLIPSE_PREFERENCE_TASK = 20;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupImpl <em>Setup</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetup()
   * @generated
   */
  int SETUP = 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl <em>P2 Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Task()
   * @generated
   */
  int P2_TASK = 14;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.InstallTaskImpl <em>Install Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.InstallTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getInstallTask()
   * @generated
   */
  int INSTALL_TASK = 11;

  /**
   * The feature id for the '<em><b>Configuration</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_VERSION__CONFIGURATION = 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_VERSION__VERSION = 1;

  /**
   * The feature id for the '<em><b>Install Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_VERSION__INSTALL_TASKS = 2;

  /**
   * The number of structural features of the '<em>Eclipse Version</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_VERSION_FEATURE_COUNT = 3;

  /**
   * The feature id for the '<em><b>Projects</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION__PROJECTS = 0;

  /**
   * The feature id for the '<em><b>Eclipse Versions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION__ECLIPSE_VERSIONS = 1;

  /**
   * The number of structural features of the '<em>Configuration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK_CONTAINER__SETUP_TASKS = 0;

  /**
   * The number of structural features of the '<em>Task Container</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurableItemImpl <em>Configurable Item</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurableItemImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfigurableItem()
   * @generated
   */
  int CONFIGURABLE_ITEM = 2;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE_ITEM__SETUP_TASKS = SETUP_TASK_CONTAINER__SETUP_TASKS;

  /**
   * The number of structural features of the '<em>Configurable Item</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE_ITEM_FEATURE_COUNT = SETUP_TASK_CONTAINER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__SETUP_TASKS = CONFIGURABLE_ITEM__SETUP_TASKS;

  /**
   * The feature id for the '<em><b>Configuration</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__CONFIGURATION = CONFIGURABLE_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Branches</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__BRANCHES = CONFIGURABLE_ITEM_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__NAME = CONFIGURABLE_ITEM_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Project</em>' class.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_FEATURE_COUNT = CONFIGURABLE_ITEM_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__SETUP_TASKS = CONFIGURABLE_ITEM__SETUP_TASKS;

  /**
   * The feature id for the '<em><b>Project</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__PROJECT = CONFIGURABLE_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__NAME = CONFIGURABLE_ITEM_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Branch</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH_FEATURE_COUNT = CONFIGURABLE_ITEM_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__SETUP_TASKS = SETUP_TASK_CONTAINER__SETUP_TASKS;

  /**
   * The feature id for the '<em><b>User Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__USER_NAME = SETUP_TASK_CONTAINER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Install Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__INSTALL_FOLDER = SETUP_TASK_CONTAINER_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Git Prefix</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__GIT_PREFIX = SETUP_TASK_CONTAINER_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Preferences</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES_FEATURE_COUNT = SETUP_TASK_CONTAINER_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineTaskImpl <em>Api Baseline Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getApiBaselineTask()
   * @generated
   */
  int API_BASELINE_TASK = 18;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl <em>Git Clone Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getGitCloneTask()
   * @generated
   */
  int GIT_CLONE_TASK = 19;

  /**
   * The feature id for the '<em><b>Branch</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP__BRANCH = 0;

  /**
   * The feature id for the '<em><b>Eclipse Version</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP__ECLIPSE_VERSION = 1;

  /**
   * The feature id for the '<em><b>Preferences</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP__PREFERENCES = 2;

  /**
   * The number of structural features of the '<em>Setup</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_FEATURE_COUNT = 3;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__REQUIREMENTS = 0;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__RESTRICTIONS = 1;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__SCOPE = 2;

  /**
   * The number of structural features of the '<em>Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl <em>Working Set Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getWorkingSetTask()
   * @generated
   */
  int WORKING_SET_TASK = 22;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.CompoundSetupTaskImpl <em>Compound Setup Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.CompoundSetupTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getCompoundSetupTask()
   * @generated
   */
  int COMPOUND_SETUP_TASK = 9;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__SETUP_TASKS = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__NAME = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Compound Setup Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.OneTimeSetupTaskImpl <em>One Time Setup Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.OneTimeSetupTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getOneTimeSetupTask()
   * @generated
   */
  int ONE_TIME_SETUP_TASK = 10;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ONE_TIME_SETUP_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ONE_TIME_SETUP_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ONE_TIME_SETUP_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ONE_TIME_SETUP_TASK__ID = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>One Time Setup Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ONE_TIME_SETUP_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALL_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALL_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALL_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The number of structural features of the '<em>Install Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALL_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl <em>Buckminster Import Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBuckminsterImportTask()
   * @generated
   */
  int BUCKMINSTER_IMPORT_TASK = 17;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.StringVariableTaskImpl <em>String Variable Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.StringVariableTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getStringVariableTask()
   * @generated
   */
  int STRING_VARIABLE_TASK = 21;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseIniTaskImpl <em>Eclipse Ini Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseIniTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipseIniTask()
   * @generated
   */
  int ECLIPSE_INI_TASK = 12;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__REQUIREMENTS = INSTALL_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__RESTRICTIONS = INSTALL_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__SCOPE = INSTALL_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Option</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__OPTION = INSTALL_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__VALUE = INSTALL_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Vm</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__VM = INSTALL_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Eclipse Ini Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK_FEATURE_COUNT = INSTALL_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__REQUIREMENTS = INSTALL_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__RESTRICTIONS = INSTALL_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__SCOPE = INSTALL_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__PATH = INSTALL_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__NAME = INSTALL_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Link Location Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK_FEATURE_COUNT = INSTALL_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__REQUIREMENTS = INSTALL_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__RESTRICTIONS = INSTALL_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__SCOPE = INSTALL_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>P2 Repositories</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__P2_REPOSITORIES = INSTALL_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Installable Units</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__INSTALLABLE_UNITS = INSTALL_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>P2 Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK_FEATURE_COUNT = INSTALL_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>P2 Task</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT__P2_TASK = 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT__ID = 1;

  /**
   * The number of structural features of the '<em>Installable Unit</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>P2 Task</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_REPOSITORY__P2_TASK = 0;

  /**
   * The feature id for the '<em><b>Url</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_REPOSITORY__URL = 1;

  /**
   * The number of structural features of the '<em>P2 Repository</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_REPOSITORY_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__REQUIREMENTS = ONE_TIME_SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__RESTRICTIONS = ONE_TIME_SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__SCOPE = ONE_TIME_SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__ID = ONE_TIME_SETUP_TASK__ID;

  /**
   * The feature id for the '<em><b>Mspec</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__MSPEC = ONE_TIME_SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Target Platform</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__TARGET_PLATFORM = ONE_TIME_SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Bundle Pool</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__BUNDLE_POOL = ONE_TIME_SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Buckminster Import Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK_FEATURE_COUNT = ONE_TIME_SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__VERSION = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Zip Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__ZIP_LOCATION = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Api Baseline Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__NAME = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Remote Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__REMOTE_NAME = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Remote URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__REMOTE_URI = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Checkout Branch</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__CHECKOUT_BRANCH = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Git Clone Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__KEY = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__VALUE = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Eclipse Preference Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VARIABLE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VARIABLE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VARIABLE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VARIABLE_TASK__NAME = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VARIABLE_TASK__VALUE = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VARIABLE_TASK__DESCRIPTION = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>String Variable Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VARIABLE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__REQUIREMENTS = ONE_TIME_SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__RESTRICTIONS = ONE_TIME_SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__SCOPE = ONE_TIME_SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__ID = ONE_TIME_SETUP_TASK__ID;

  /**
   * The feature id for the '<em><b>Working Set Group</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__WORKING_SET_GROUP = ONE_TIME_SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Working Set Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK_FEATURE_COUNT = ONE_TIME_SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskScope <em>Task Scope</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskScope
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTaskScope()
   * @generated
   */
  int SETUP_TASK_SCOPE = 23;

  /**
   * The meta object id for the '<em>URI</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.URI
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getURI()
   * @generated
   */
  int URI = 24;

  /**
   * The meta object id for the '<em>Exception</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.lang.Exception
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getException()
   * @generated
   */
  int EXCEPTION = 25;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Configuration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Configuration
   * @generated
   */
  EClass getConfiguration();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Configuration#getProjects <em>Projects</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Projects</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Configuration#getProjects()
   * @see #getConfiguration()
   * @generated
   */
  EReference getConfiguration_Projects();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Configuration#getEclipseVersions <em>Eclipse Versions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Eclipse Versions</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Configuration#getEclipseVersions()
   * @see #getConfiguration()
   * @generated
   */
  EReference getConfiguration_EclipseVersions();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Project <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Project</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project
   * @generated
   */
  EClass getProject();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.Project#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getConfiguration()
   * @see #getProject()
   * @generated
   */
  EReference getProject_Configuration();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Project#getBranches <em>Branches</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Branches</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getBranches()
   * @see #getProject()
   * @generated
   */
  EReference getProject_Branches();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Project#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getName()
   * @see #getProject()
   * @generated
   */
  EAttribute getProject_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Branch <em>Branch</em>}'.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @return the meta object for class '<em>Branch</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch
   * @generated
   */
  EClass getBranch();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.Branch#getProject <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Project</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getProject()
   * @see #getBranch()
   * @generated
   */
  EReference getBranch_Project();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Branch#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getName()
   * @see #getBranch()
   * @generated
   */
  EAttribute getBranch_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask <em>Api Baseline Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Api Baseline Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaselineTask
   * @generated
   */
  EClass getApiBaselineTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getVersion()
   * @see #getApiBaselineTask()
   * @generated
   */
  EAttribute getApiBaselineTask_Version();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getZipLocation <em>Zip Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Zip Location</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getZipLocation()
   * @see #getApiBaselineTask()
   * @generated
   */
  EAttribute getApiBaselineTask_ZipLocation();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask <em>Git Clone Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Git Clone Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask
   * @generated
   */
  EClass getGitCloneTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getName()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteName <em>Remote Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Remote Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteName()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_RemoteName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteURI <em>Remote URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Remote URI</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteURI()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_RemoteURI();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getCheckoutBranch <em>Checkout Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Checkout Branch</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getCheckoutBranch()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_CheckoutBranch();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion <em>Eclipse Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Eclipse Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion
   * @generated
   */
  EClass getEclipseVersion();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion#getConfiguration()
   * @see #getEclipseVersion()
   * @generated
   */
  EReference getEclipseVersion_Configuration();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion#getVersion()
   * @see #getEclipseVersion()
   * @generated
   */
  EAttribute getEclipseVersion_Version();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion#getInstallTasks <em>Install Tasks</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Install Tasks</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion#getInstallTasks()
   * @see #getEclipseVersion()
   * @generated
   */
  EReference getEclipseVersion_InstallTasks();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.P2Task <em>P2 Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>P2 Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task
   * @generated
   */
  EClass getP2Task();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.P2Task#getInstallableUnits <em>Installable Units</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Installable Units</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task#getInstallableUnits()
   * @see #getP2Task()
   * @generated
   */
  EReference getP2Task_InstallableUnits();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.P2Task#getP2Repositories <em>P2 Repositories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>P2 Repositories</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task#getP2Repositories()
   * @see #getP2Task()
   * @generated
   */
  EReference getP2Task_P2Repositories();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.InstallTask <em>Install Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Install Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallTask
   * @generated
   */
  EClass getInstallTask();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.StringVariableTask <em>String Variable Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Variable Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.StringVariableTask
   * @generated
   */
  EClass getStringVariableTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.StringVariableTask#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.StringVariableTask#getName()
   * @see #getStringVariableTask()
   * @generated
   */
  EAttribute getStringVariableTask_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.StringVariableTask#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.StringVariableTask#getValue()
   * @see #getStringVariableTask()
   * @generated
   */
  EAttribute getStringVariableTask_Value();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.StringVariableTask#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.StringVariableTask#getDescription()
   * @see #getStringVariableTask()
   * @generated
   */
  EAttribute getStringVariableTask_Description();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit <em>Installable Unit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Installable Unit</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit
   * @generated
   */
  EClass getInstallableUnit();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getP2Task <em>P2 Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>P2 Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit#getP2Task()
   * @see #getInstallableUnit()
   * @generated
   */
  EReference getInstallableUnit_P2Task();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit#getId()
   * @see #getInstallableUnit()
   * @generated
   */
  EAttribute getInstallableUnit_Id();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.P2Repository <em>P2 Repository</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>P2 Repository</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository
   * @generated
   */
  EClass getP2Repository();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.P2Repository#getP2Task <em>P2 Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>P2 Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository#getP2Task()
   * @see #getP2Repository()
   * @generated
   */
  EReference getP2Repository_P2Task();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.P2Repository#getUrl <em>Url</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Url</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository#getUrl()
   * @see #getP2Repository()
   * @generated
   */
  EAttribute getP2Repository_Url();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Setup <em>Setup</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Setup</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup
   * @generated
   */
  EClass getSetup();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.setup.Setup#getBranch <em>Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Branch</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup#getBranch()
   * @see #getSetup()
   * @generated
   */
  EReference getSetup_Branch();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.setup.Setup#getEclipseVersion <em>Eclipse Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Eclipse Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup#getEclipseVersion()
   * @see #getSetup()
   * @generated
   */
  EReference getSetup_EclipseVersion();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.setup.Setup#getPreferences <em>Preferences</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Preferences</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup#getPreferences()
   * @see #getSetup()
   * @generated
   */
  EReference getSetup_Preferences();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.SetupTask <em>Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask
   * @generated
   */
  EClass getSetupTask();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getRequirements <em>Requirements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Requirements</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#getRequirements()
   * @see #getSetupTask()
   * @generated
   */
  EReference getSetupTask_Requirements();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getRestrictions <em>Restrictions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Restrictions</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#getRestrictions()
   * @see #getSetupTask()
   * @generated
   */
  EReference getSetupTask_Restrictions();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getScope <em>Scope</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Scope</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#getScope()
   * @see #getSetupTask()
   * @generated
   */
  EAttribute getSetupTask_Scope();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.WorkingSetTask <em>Working Set Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Working Set Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.WorkingSetTask
   * @generated
   */
  EClass getWorkingSetTask();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.releng.setup.WorkingSetTask#getWorkingSetGroup <em>Working Set Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Working Set Group</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.WorkingSetTask#getWorkingSetGroup()
   * @see #getWorkingSetTask()
   * @generated
   */
  EReference getWorkingSetTask_WorkingSetGroup();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask <em>Eclipse Ini Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Eclipse Ini Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask
   * @generated
   */
  EClass getEclipseIniTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask#getOption <em>Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Option</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask#getOption()
   * @see #getEclipseIniTask()
   * @generated
   */
  EAttribute getEclipseIniTask_Option();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask#getValue()
   * @see #getEclipseIniTask()
   * @generated
   */
  EAttribute getEclipseIniTask_Value();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask#isVm <em>Vm</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Vm</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask#isVm()
   * @see #getEclipseIniTask()
   * @generated
   */
  EAttribute getEclipseIniTask_Vm();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskScope <em>Task Scope</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Task Scope</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskScope
   * @generated
   */
  EEnum getSetupTaskScope();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.CompoundSetupTask <em>Compound Setup Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Compound Setup Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.CompoundSetupTask
   * @generated
   */
  EClass getCompoundSetupTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.CompoundSetupTask#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.CompoundSetupTask#getName()
   * @see #getCompoundSetupTask()
   * @generated
   */
  EAttribute getCompoundSetupTask_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.OneTimeSetupTask <em>One Time Setup Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>One Time Setup Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.OneTimeSetupTask
   * @generated
   */
  EClass getOneTimeSetupTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.OneTimeSetupTask#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.OneTimeSetupTask#getId()
   * @see #getOneTimeSetupTask()
   * @generated
   */
  EAttribute getOneTimeSetupTask_Id();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ConfigurableItem <em>Configurable Item</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Configurable Item</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ConfigurableItem
   * @generated
   */
  EClass getConfigurableItem();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask <em>Buckminster Import Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Buckminster Import Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask
   * @generated
   */
  EClass getBuckminsterImportTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getMspec <em>Mspec</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Mspec</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getMspec()
   * @see #getBuckminsterImportTask()
   * @generated
   */
  EAttribute getBuckminsterImportTask_Mspec();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getTargetPlatform <em>Target Platform</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target Platform</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getTargetPlatform()
   * @see #getBuckminsterImportTask()
   * @generated
   */
  EAttribute getBuckminsterImportTask_TargetPlatform();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getBundlePool <em>Bundle Pool</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Bundle Pool</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getBundlePool()
   * @see #getBuckminsterImportTask()
   * @generated
   */
  EAttribute getBuckminsterImportTask_BundlePool();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Preferences <em>Preferences</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Preferences</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences
   * @generated
   */
  EClass getPreferences();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getUserName <em>User Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>User Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getUserName()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_UserName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getInstallFolder <em>Install Folder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Install Folder</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getInstallFolder()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_InstallFolder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getGitPrefix <em>Git Prefix</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Git Prefix</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getGitPrefix()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_GitPrefix();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask <em>Link Location Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Link Location Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.LinkLocationTask
   * @generated
   */
  EClass getLinkLocationTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask#getPath <em>Path</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Path</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.LinkLocationTask#getPath()
   * @see #getLinkLocationTask()
   * @generated
   */
  EAttribute getLinkLocationTask_Path();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.LinkLocationTask#getName()
   * @see #getLinkLocationTask()
   * @generated
   */
  EAttribute getLinkLocationTask_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskContainer <em>Task Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Task Container</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskContainer
   * @generated
   */
  EClass getSetupTaskContainer();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskContainer#getSetupTasks <em>Setup Tasks</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Setup Tasks</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskContainer#getSetupTasks()
   * @see #getSetupTaskContainer()
   * @generated
   */
  EReference getSetupTaskContainer_SetupTasks();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask <em>Eclipse Preference Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Eclipse Preference Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask
   * @generated
   */
  EClass getEclipsePreferenceTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask#getKey <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask#getKey()
   * @see #getEclipsePreferenceTask()
   * @generated
   */
  EAttribute getEclipsePreferenceTask_Key();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask#getValue()
   * @see #getEclipsePreferenceTask()
   * @generated
   */
  EAttribute getEclipsePreferenceTask_Value();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>URI</em>'.
   * @see org.eclipse.emf.common.util.URI
   * @model instanceClass="org.eclipse.emf.common.util.URI"
   * @generated
   */
  EDataType getURI();

  /**
   * Returns the meta object for data type '{@link java.lang.Exception <em>Exception</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Exception</em>'.
   * @see java.lang.Exception
   * @model instanceClass="java.lang.Exception"
   * @generated
   */
  EDataType getException();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  SetupFactory getSetupFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl <em>Configuration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfiguration()
     * @generated
     */
    EClass CONFIGURATION = eINSTANCE.getConfiguration();

    /**
     * The meta object literal for the '<em><b>Projects</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONFIGURATION__PROJECTS = eINSTANCE.getConfiguration_Projects();

    /**
     * The meta object literal for the '<em><b>Eclipse Versions</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONFIGURATION__ECLIPSE_VERSIONS = eINSTANCE.getConfiguration_EclipseVersions();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl <em>Project</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getProject()
     * @generated
     */
    EClass PROJECT = eINSTANCE.getProject();

    /**
     * The meta object literal for the '<em><b>Configuration</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__CONFIGURATION = eINSTANCE.getProject_Configuration();

    /**
     * The meta object literal for the '<em><b>Branches</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__BRANCHES = eINSTANCE.getProject_Branches();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROJECT__NAME = eINSTANCE.getProject_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl <em>Branch</em>}' class.
     * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.BranchImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBranch()
     * @generated
     */
    EClass BRANCH = eINSTANCE.getBranch();

    /**
     * The meta object literal for the '<em><b>Project</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BRANCH__PROJECT = eINSTANCE.getBranch_Project();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BRANCH__NAME = eINSTANCE.getBranch_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineTaskImpl <em>Api Baseline Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getApiBaselineTask()
     * @generated
     */
    EClass API_BASELINE_TASK = eINSTANCE.getApiBaselineTask();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute API_BASELINE_TASK__VERSION = eINSTANCE.getApiBaselineTask_Version();

    /**
     * The meta object literal for the '<em><b>Zip Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute API_BASELINE_TASK__ZIP_LOCATION = eINSTANCE.getApiBaselineTask_ZipLocation();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl <em>Git Clone Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getGitCloneTask()
     * @generated
     */
    EClass GIT_CLONE_TASK = eINSTANCE.getGitCloneTask();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__NAME = eINSTANCE.getGitCloneTask_Name();

    /**
     * The meta object literal for the '<em><b>Remote Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__REMOTE_NAME = eINSTANCE.getGitCloneTask_RemoteName();

    /**
     * The meta object literal for the '<em><b>Remote URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__REMOTE_URI = eINSTANCE.getGitCloneTask_RemoteURI();

    /**
     * The meta object literal for the '<em><b>Checkout Branch</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__CHECKOUT_BRANCH = eINSTANCE.getGitCloneTask_CheckoutBranch();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseVersionImpl <em>Eclipse Version</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseVersionImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipseVersion()
     * @generated
     */
    EClass ECLIPSE_VERSION = eINSTANCE.getEclipseVersion();

    /**
     * The meta object literal for the '<em><b>Configuration</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ECLIPSE_VERSION__CONFIGURATION = eINSTANCE.getEclipseVersion_Configuration();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_VERSION__VERSION = eINSTANCE.getEclipseVersion_Version();

    /**
     * The meta object literal for the '<em><b>Install Tasks</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ECLIPSE_VERSION__INSTALL_TASKS = eINSTANCE.getEclipseVersion_InstallTasks();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl <em>P2 Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Task()
     * @generated
     */
    EClass P2_TASK = eINSTANCE.getP2Task();

    /**
     * The meta object literal for the '<em><b>Installable Units</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference P2_TASK__INSTALLABLE_UNITS = eINSTANCE.getP2Task_InstallableUnits();

    /**
     * The meta object literal for the '<em><b>P2 Repositories</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference P2_TASK__P2_REPOSITORIES = eINSTANCE.getP2Task_P2Repositories();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.InstallTaskImpl <em>Install Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.InstallTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getInstallTask()
     * @generated
     */
    EClass INSTALL_TASK = eINSTANCE.getInstallTask();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.StringVariableTaskImpl <em>String Variable Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.StringVariableTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getStringVariableTask()
     * @generated
     */
    EClass STRING_VARIABLE_TASK = eINSTANCE.getStringVariableTask();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_VARIABLE_TASK__NAME = eINSTANCE.getStringVariableTask_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_VARIABLE_TASK__VALUE = eINSTANCE.getStringVariableTask_Value();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_VARIABLE_TASK__DESCRIPTION = eINSTANCE.getStringVariableTask_Description();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl <em>Installable Unit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getInstallableUnit()
     * @generated
     */
    EClass INSTALLABLE_UNIT = eINSTANCE.getInstallableUnit();

    /**
     * The meta object literal for the '<em><b>P2 Task</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INSTALLABLE_UNIT__P2_TASK = eINSTANCE.getInstallableUnit_P2Task();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INSTALLABLE_UNIT__ID = eINSTANCE.getInstallableUnit_Id();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl <em>P2 Repository</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Repository()
     * @generated
     */
    EClass P2_REPOSITORY = eINSTANCE.getP2Repository();

    /**
     * The meta object literal for the '<em><b>P2 Task</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference P2_REPOSITORY__P2_TASK = eINSTANCE.getP2Repository_P2Task();

    /**
     * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute P2_REPOSITORY__URL = eINSTANCE.getP2Repository_Url();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupImpl <em>Setup</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetup()
     * @generated
     */
    EClass SETUP = eINSTANCE.getSetup();

    /**
     * The meta object literal for the '<em><b>Branch</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP__BRANCH = eINSTANCE.getSetup_Branch();

    /**
     * The meta object literal for the '<em><b>Eclipse Version</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP__ECLIPSE_VERSION = eINSTANCE.getSetup_EclipseVersion();

    /**
     * The meta object literal for the '<em><b>Preferences</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP__PREFERENCES = eINSTANCE.getSetup_Preferences();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl <em>Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTask()
     * @generated
     */
    EClass SETUP_TASK = eINSTANCE.getSetupTask();

    /**
     * The meta object literal for the '<em><b>Requirements</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP_TASK__REQUIREMENTS = eINSTANCE.getSetupTask_Requirements();

    /**
     * The meta object literal for the '<em><b>Restrictions</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP_TASK__RESTRICTIONS = eINSTANCE.getSetupTask_Restrictions();

    /**
     * The meta object literal for the '<em><b>Scope</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SETUP_TASK__SCOPE = eINSTANCE.getSetupTask_Scope();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl <em>Working Set Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getWorkingSetTask()
     * @generated
     */
    EClass WORKING_SET_TASK = eINSTANCE.getWorkingSetTask();

    /**
     * The meta object literal for the '<em><b>Working Set Group</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORKING_SET_TASK__WORKING_SET_GROUP = eINSTANCE.getWorkingSetTask_WorkingSetGroup();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseIniTaskImpl <em>Eclipse Ini Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseIniTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipseIniTask()
     * @generated
     */
    EClass ECLIPSE_INI_TASK = eINSTANCE.getEclipseIniTask();

    /**
     * The meta object literal for the '<em><b>Option</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_INI_TASK__OPTION = eINSTANCE.getEclipseIniTask_Option();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_INI_TASK__VALUE = eINSTANCE.getEclipseIniTask_Value();

    /**
     * The meta object literal for the '<em><b>Vm</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_INI_TASK__VM = eINSTANCE.getEclipseIniTask_Vm();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskScope <em>Task Scope</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.SetupTaskScope
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTaskScope()
     * @generated
     */
    EEnum SETUP_TASK_SCOPE = eINSTANCE.getSetupTaskScope();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.CompoundSetupTaskImpl <em>Compound Setup Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.CompoundSetupTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getCompoundSetupTask()
     * @generated
     */
    EClass COMPOUND_SETUP_TASK = eINSTANCE.getCompoundSetupTask();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPOUND_SETUP_TASK__NAME = eINSTANCE.getCompoundSetupTask_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.OneTimeSetupTaskImpl <em>One Time Setup Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.OneTimeSetupTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getOneTimeSetupTask()
     * @generated
     */
    EClass ONE_TIME_SETUP_TASK = eINSTANCE.getOneTimeSetupTask();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ONE_TIME_SETUP_TASK__ID = eINSTANCE.getOneTimeSetupTask_Id();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurableItemImpl <em>Configurable Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurableItemImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfigurableItem()
     * @generated
     */
    EClass CONFIGURABLE_ITEM = eINSTANCE.getConfigurableItem();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl <em>Buckminster Import Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBuckminsterImportTask()
     * @generated
     */
    EClass BUCKMINSTER_IMPORT_TASK = eINSTANCE.getBuckminsterImportTask();

    /**
     * The meta object literal for the '<em><b>Mspec</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BUCKMINSTER_IMPORT_TASK__MSPEC = eINSTANCE.getBuckminsterImportTask_Mspec();

    /**
     * The meta object literal for the '<em><b>Target Platform</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BUCKMINSTER_IMPORT_TASK__TARGET_PLATFORM = eINSTANCE.getBuckminsterImportTask_TargetPlatform();

    /**
     * The meta object literal for the '<em><b>Bundle Pool</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BUCKMINSTER_IMPORT_TASK__BUNDLE_POOL = eINSTANCE.getBuckminsterImportTask_BundlePool();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl <em>Preferences</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getPreferences()
     * @generated
     */
    EClass PREFERENCES = eINSTANCE.getPreferences();

    /**
     * The meta object literal for the '<em><b>User Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__USER_NAME = eINSTANCE.getPreferences_UserName();

    /**
     * The meta object literal for the '<em><b>Install Folder</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__INSTALL_FOLDER = eINSTANCE.getPreferences_InstallFolder();

    /**
     * The meta object literal for the '<em><b>Git Prefix</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__GIT_PREFIX = eINSTANCE.getPreferences_GitPrefix();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.LinkLocationTaskImpl <em>Link Location Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.LinkLocationTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getLinkLocationTask()
     * @generated
     */
    EClass LINK_LOCATION_TASK = eINSTANCE.getLinkLocationTask();

    /**
     * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINK_LOCATION_TASK__PATH = eINSTANCE.getLinkLocationTask_Path();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINK_LOCATION_TASK__NAME = eINSTANCE.getLinkLocationTask_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskContainerImpl <em>Task Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupTaskContainerImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTaskContainer()
     * @generated
     */
    EClass SETUP_TASK_CONTAINER = eINSTANCE.getSetupTaskContainer();

    /**
     * The meta object literal for the '<em><b>Setup Tasks</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP_TASK_CONTAINER__SETUP_TASKS = eINSTANCE.getSetupTaskContainer_SetupTasks();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl <em>Eclipse Preference Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipsePreferenceTask()
     * @generated
     */
    EClass ECLIPSE_PREFERENCE_TASK = eINSTANCE.getEclipsePreferenceTask();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_PREFERENCE_TASK__KEY = eINSTANCE.getEclipsePreferenceTask_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_PREFERENCE_TASK__VALUE = eINSTANCE.getEclipsePreferenceTask_Value();

    /**
     * The meta object literal for the '<em>URI</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.common.util.URI
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getURI()
     * @generated
     */
    EDataType URI = eINSTANCE.getURI();

    /**
     * The meta object literal for the '<em>Exception</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.Exception
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getException()
     * @generated
     */
    EDataType EXCEPTION = eINSTANCE.getException();

  }

} // SetupPackage
