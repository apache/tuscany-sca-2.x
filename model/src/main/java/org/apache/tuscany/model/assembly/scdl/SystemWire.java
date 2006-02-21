/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl;

import commonj.sdo.Sequence;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>System Wire</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getSourceGroup <em>Source Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getSource <em>Source</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getTargetGroup <em>Target Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getTarget <em>Target</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getAny <em>Any</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface SystemWire
{
  /**
   * Returns the value of the '<em><b>Source Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Group</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Group</em>' attribute list.
   * @generated
   */
  Sequence getSourceGroup();

  /**
   * Returns the value of the '<em><b>Source</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source</em>' containment reference.
   * @see #setSource(Object)
   * @generated
   */
  Object getSource();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getSource <em>Source</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source</em>' containment reference.
   * @see #getSource()
   * @generated
   */
  void setSource(Object value);

  /**
   * Returns the value of the '<em><b>Target Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target Group</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Group</em>' attribute list.
   * @generated
   */
  Sequence getTargetGroup();

  /**
   * Returns the value of the '<em><b>Target</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target</em>' containment reference.
   * @see #setTarget(Object)
   * @generated
   */
  Object getTarget();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.SystemWire#getTarget <em>Target</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target</em>' containment reference.
   * @see #getTarget()
   * @generated
   */
  void setTarget(Object value);

  /**
   * Returns the value of the '<em><b>Any</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Any</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Any</em>' attribute list.
   * @generated
   */
  Sequence getAny();

} // SystemWire
