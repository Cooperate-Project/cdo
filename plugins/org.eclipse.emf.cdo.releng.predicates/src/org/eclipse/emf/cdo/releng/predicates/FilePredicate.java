/**
 */
package org.eclipse.emf.cdo.releng.predicates;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>File Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.predicates.FilePredicate#getFilePattern <em>File Pattern</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.predicates.FilePredicate#getContentPattern <em>Content Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getFilePredicate()
 * @model
 * @generated
 */
public interface FilePredicate extends Predicate
{
  /**
   * Returns the value of the '<em><b>File Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>File Pattern</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>File Pattern</em>' attribute.
   * @see #setFilePattern(String)
   * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getFilePredicate_FilePattern()
   * @model required="true"
   * @generated
   */
  String getFilePattern();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.predicates.FilePredicate#getFilePattern <em>File Pattern</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>File Pattern</em>' attribute.
   * @see #getFilePattern()
   * @generated
   */
  void setFilePattern(String value);

  /**
   * Returns the value of the '<em><b>Content Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Content Pattern</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Content Pattern</em>' attribute.
   * @see #setContentPattern(String)
   * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getFilePredicate_ContentPattern()
   * @model
   * @generated
   */
  String getContentPattern();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.predicates.FilePredicate#getContentPattern <em>Content Pattern</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Content Pattern</em>' attribute.
   * @see #getContentPattern()
   * @generated
   */
  void setContentPattern(String value);

} // FilePredicate
