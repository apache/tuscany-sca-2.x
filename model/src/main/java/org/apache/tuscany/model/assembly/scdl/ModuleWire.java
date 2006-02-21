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
 * A representation of the model object '<em><b>Module Wire</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getSourceUri <em>Source Uri</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getTargetUri <em>Target Uri</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface ModuleWire
{
  /**
   * Returns the value of the '<em><b>Source Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Uri</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Uri</em>' attribute.
   * @see #setSourceUri(String)
   * @generated
   */
  String getSourceUri();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getSourceUri <em>Source Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source Uri</em>' attribute.
   * @see #getSourceUri()
   * @generated
   */
  void setSourceUri(String value);

  /**
   * Returns the value of the '<em><b>Target Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target Uri</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Uri</em>' attribute.
   * @see #setTargetUri(String)
   * @generated
   */
  String getTargetUri();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.ModuleWire#getTargetUri <em>Target Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target Uri</em>' attribute.
   * @see #getTargetUri()
   * @generated
   */
  void setTargetUri(String value);

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

  /**
   * Returns the value of the '<em><b>Any Attribute</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Any Attribute</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Any Attribute</em>' attribute list.
   * @generated
   */
  Sequence getAnyAttribute();

} // ModuleWire
