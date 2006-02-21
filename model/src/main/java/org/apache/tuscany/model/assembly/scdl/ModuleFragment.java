/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl;

import commonj.sdo.Sequence;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module Fragment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getEntryPoint <em>Entry Point</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getComponent <em>Component</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getExternalService <em>External Service</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getWire <em>Wire</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface ModuleFragment
{
  /**
   * Returns the value of the '<em><b>Entry Point</b></em>' containment reference list.
   * The list contents are of type {@link org.apache.tuscany.model.assembly.scdl.EntryPoint}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Entry Point</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Entry Point</em>' containment reference list.
   * @generated
   */
  List getEntryPoint();

  /**
   * Returns the value of the '<em><b>Component</b></em>' containment reference list.
   * The list contents are of type {@link org.apache.tuscany.model.assembly.scdl.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component</em>' containment reference list.
   * @generated
   */
  List getComponent();

  /**
   * Returns the value of the '<em><b>External Service</b></em>' containment reference list.
   * The list contents are of type {@link org.apache.tuscany.model.assembly.scdl.ExternalService}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>External Service</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>External Service</em>' containment reference list.
   * @generated
   */
  List getExternalService();

  /**
   * Returns the value of the '<em><b>Wire</b></em>' containment reference list.
   * The list contents are of type {@link org.apache.tuscany.model.assembly.scdl.ModuleWire}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Wire</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Wire</em>' containment reference list.
   * @generated
   */
  List getWire();

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
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.ModuleFragment#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

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

} // ModuleFragment
