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
 * A representation of the model object '<em><b>Entry Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getInterfaceGroup <em>Interface Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getBindingGroup <em>Binding Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getBinding <em>Binding</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getReference <em>Reference</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getMultiplicity <em>Multiplicity</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface EntryPoint
{
  /**
   * Returns the value of the '<em><b>Interface Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Interface Group</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Interface Group</em>' attribute list.
   * @generated
   */
  Sequence getInterfaceGroup();

  /**
   * Returns the value of the '<em><b>Interface</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Interface</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Interface</em>' containment reference.
   * @see #setInterface(Interface)
   * @generated
   */
  Interface getInterface();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getInterface <em>Interface</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Interface</em>' containment reference.
   * @see #getInterface()
   * @generated
   */
  void setInterface(Interface value);

  /**
   * Returns the value of the '<em><b>Binding Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Binding Group</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Binding Group</em>' attribute list.
   * @generated
   */
  Sequence getBindingGroup();

  /**
   * Returns the value of the '<em><b>Binding</b></em>' containment reference list.
   * The list contents are of type {@link org.apache.tuscany.model.assembly.scdl.Binding}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Binding</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Binding</em>' containment reference list.
   * @generated
   */
  List getBinding();

  /**
   * Returns the value of the '<em><b>Reference</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Reference</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reference</em>' attribute list.
   * @generated
   */
  List getReference();

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
   * Returns the value of the '<em><b>Multiplicity</b></em>' attribute.
   * The default value is <code>"1..1"</code>.
   * The literals are from the enumeration {@link org.apache.tuscany.model.assembly.scdl.Multiplicity}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Multiplicity</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Multiplicity</em>' attribute.
   * @see org.apache.tuscany.model.assembly.scdl.Multiplicity
   * @see #isSetMultiplicity()
   * @see #unsetMultiplicity()
   * @see #setMultiplicity(Multiplicity)
   * @generated
   */
  Multiplicity getMultiplicity();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getMultiplicity <em>Multiplicity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Multiplicity</em>' attribute.
   * @see org.apache.tuscany.model.assembly.scdl.Multiplicity
   * @see #isSetMultiplicity()
   * @see #unsetMultiplicity()
   * @see #getMultiplicity()
   * @generated
   */
  void setMultiplicity(Multiplicity value);

  /**
   * Unsets the value of the '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getMultiplicity <em>Multiplicity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetMultiplicity()
   * @see #getMultiplicity()
   * @see #setMultiplicity(Multiplicity)
   * @generated
   */
  void unsetMultiplicity();

  /**
   * Returns whether the value of the '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getMultiplicity <em>Multiplicity</em>}' attribute is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Multiplicity</em>' attribute is set.
   * @see #unsetMultiplicity()
   * @see #getMultiplicity()
   * @see #setMultiplicity(Multiplicity)
   * @generated
   */
  boolean isSetMultiplicity();

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
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.EntryPoint#getName <em>Name</em>}' attribute.
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

} // EntryPoint
