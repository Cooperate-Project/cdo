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
package org.eclipse.emf.cdo.releng.predicates.impl;

import org.eclipse.emf.cdo.releng.predicates.PredicatesPackage;
import org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.egit.core.GitProvider;
import org.eclipse.egit.core.project.GitProjectData;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.team.core.RepositoryProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Repository Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.predicates.impl.RepositoryPredicateImpl#getProject <em>Project</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RepositoryPredicateImpl extends MinimalEObjectImpl.Container implements RepositoryPredicate
{
  /**
   * The default value of the '{@link #getProject() <em>Project</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProject()
   * @generated
   * @ordered
   */
  protected static final IProject PROJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getProject() <em>Project</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProject()
   * @generated
   * @ordered
   */
  protected IProject project = PROJECT_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RepositoryPredicateImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return PredicatesPackage.Literals.REPOSITORY_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IProject getProject()
  {
    return project;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setProject(IProject newProject)
  {
    IProject oldProject = project;
    project = newProject;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, PredicatesPackage.REPOSITORY_PREDICATE__PROJECT,
          oldProject, project));
    }
  }

  private String getGitDirAbsolutePath(IProject project)
  {
    if (project != null)
    {
      RepositoryProvider provider = RepositoryProvider.getProvider(project);
      if (provider != null)
      {
        try
        {
          if (provider instanceof GitProvider)
          {
            GitProvider gitProvider = (GitProvider)provider;
            GitProjectData data = gitProvider.getData();
            RepositoryMapping repositoryMapping = data.getRepositoryMapping(project);
            IPath gitDirAbsolutePath = repositoryMapping.getGitDirAbsolutePath();
            return gitDirAbsolutePath == null ? null : gitDirAbsolutePath.toString();
          }
        }
        catch (Throwable throwable)
        {
          // Ignore
        }

        try
        {
          // http://fossies.org/linux/privat/subclipse-1.6.18.tar.gz:a/subclipse-1.6.18/org.tigris.subversion.subclipse.core/src/org/tigris/subversion/subclipse/core/SVNTeamProvider.java
          Class<? extends RepositoryProvider> providerClass = provider.getClass();
          Method getSVNWorkspaceRootMethod = providerClass.getMethod("getSVNWorkspaceRoot");
          Object svnWorkspaceRoot = getSVNWorkspaceRootMethod.invoke(provider);
          Class<? extends Object> workspaceRootClass = svnWorkspaceRoot.getClass();
          Method getRepositoryMethod = workspaceRootClass.getMethod("getRepository");
          Object repositoryLocation = getRepositoryMethod.invoke(svnWorkspaceRoot);
          Class<? extends Object> repositoryLocationClass = repositoryLocation.getClass();
          Method getLocationMethod = repositoryLocationClass.getMethod("getLocation");
          Object location = getLocationMethod.invoke(repositoryLocation);
          return location == null ? null : location.toString();
        }
        catch (Throwable throwable)
        {
          // Ignore
        }

        try
        {
          // http://dev.eclipse.org/svnroot/technology/org.eclipse.subversive/trunk/org.eclipse.team.svn.core/src/org/eclipse/team/svn/core/SVNTeamProvider.java
          Class<? extends RepositoryProvider> providerClass = provider.getClass();
          Method getRepositoryLocationMethod = providerClass.getMethod("getRepositoryLocation");
          Object repositoryLocation = getRepositoryLocationMethod.invoke(provider);
          Class<? extends Object> repositoryLocationClass = repositoryLocation.getClass();
          Method getRepositoryRootUrlMethod = repositoryLocationClass.getMethod("getRepositoryRootUrl");
          Object respositoryRootURL = getRepositoryRootUrlMethod.invoke(repositoryLocation);
          return respositoryRootURL == null ? null : respositoryRootURL.toString();
        }
        catch (Throwable throwable)
        {
          // Ignore
        }

        return "Unknown repo";
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean matches(IProject project)
  {
    String prototypeGitDirAbsolutePath = getGitDirAbsolutePath(getProject());
    String gitDirAbsolutePath = getGitDirAbsolutePath(project);
    return prototypeGitDirAbsolutePath == null ? gitDirAbsolutePath == null : prototypeGitDirAbsolutePath
        .equals(gitDirAbsolutePath);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case PredicatesPackage.REPOSITORY_PREDICATE__PROJECT:
      return getProject();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case PredicatesPackage.REPOSITORY_PREDICATE__PROJECT:
      setProject((IProject)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case PredicatesPackage.REPOSITORY_PREDICATE__PROJECT:
      setProject(PROJECT_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case PredicatesPackage.REPOSITORY_PREDICATE__PROJECT:
      return PROJECT_EDEFAULT == null ? project != null : !PROJECT_EDEFAULT.equals(project);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case PredicatesPackage.REPOSITORY_PREDICATE___MATCHES__IPROJECT:
      return matches((IProject)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (project: ");
    result.append(project);
    result.append(')');
    return result.toString();
  }

} // RepositoryPredicateImpl
