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
package org.eclipse.emf.cdo.releng.predicates.util;

import org.eclipse.emf.cdo.releng.predicates.AndPredicate;
import org.eclipse.emf.cdo.releng.predicates.BuilderPredicate;
import org.eclipse.emf.cdo.releng.predicates.FilePredicate;
import org.eclipse.emf.cdo.releng.predicates.NamePredicate;
import org.eclipse.emf.cdo.releng.predicates.NaturePredicate;
import org.eclipse.emf.cdo.releng.predicates.NotPredicate;
import org.eclipse.emf.cdo.releng.predicates.OrPredicate;
import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.cdo.releng.predicates.PredicatesPackage;
import org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage
 * @generated
 */
public class PredicatesAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static PredicatesPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PredicatesAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = PredicatesPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PredicatesSwitch<Adapter> modelSwitch = new PredicatesSwitch<Adapter>()
  {
    @Override
    public Adapter casePredicate(Predicate object)
    {
      return createPredicateAdapter();
    }

    @Override
    public Adapter caseNamePredicate(NamePredicate object)
    {
      return createNamePredicateAdapter();
    }

    @Override
    public Adapter caseRepositoryPredicate(RepositoryPredicate object)
    {
      return createRepositoryPredicateAdapter();
    }

    @Override
    public Adapter caseAndPredicate(AndPredicate object)
    {
      return createAndPredicateAdapter();
    }

    @Override
    public Adapter caseOrPredicate(OrPredicate object)
    {
      return createOrPredicateAdapter();
    }

    @Override
    public Adapter caseNotPredicate(NotPredicate object)
    {
      return createNotPredicateAdapter();
    }

    @Override
    public Adapter caseNaturePredicate(NaturePredicate object)
    {
      return createNaturePredicateAdapter();
    }

    @Override
    public Adapter caseBuilderPredicate(BuilderPredicate object)
    {
      return createBuilderPredicateAdapter();
    }

    @Override
    public Adapter caseFilePredicate(FilePredicate object)
    {
      return createFilePredicateAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.Predicate <em>Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.Predicate
   * @generated
   */
  public Adapter createPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.NamePredicate <em>Name Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.NamePredicate
   * @generated
   */
  public Adapter createNamePredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate <em>Repository Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate
   * @generated
   */
  public Adapter createRepositoryPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.AndPredicate <em>And Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.AndPredicate
   * @generated
   */
  public Adapter createAndPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.OrPredicate <em>Or Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.OrPredicate
   * @generated
   */
  public Adapter createOrPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.NotPredicate <em>Not Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.NotPredicate
   * @generated
   */
  public Adapter createNotPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.NaturePredicate <em>Nature Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.NaturePredicate
   * @generated
   */
  public Adapter createNaturePredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.BuilderPredicate <em>Builder Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.BuilderPredicate
   * @generated
   */
  public Adapter createBuilderPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.predicates.FilePredicate <em>File Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.predicates.FilePredicate
   * @generated
   */
  public Adapter createFilePredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // PredicatesAdapterFactory
