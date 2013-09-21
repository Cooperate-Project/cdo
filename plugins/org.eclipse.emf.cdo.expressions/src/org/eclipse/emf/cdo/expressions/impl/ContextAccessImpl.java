/**
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.ContextAccess;
import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Context Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ContextAccessImpl extends AccessImpl implements ContextAccess
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ContextAccessImpl()
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
    return ExpressionsPackage.Literals.CONTEXT_ACCESS;
  }

  @Override
  protected Object evaluate(EvaluationContext context, String name)
  {
    return context.get(name);
  }

} // ContextValueImpl