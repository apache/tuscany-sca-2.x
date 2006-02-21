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
 * A representation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.Component#getImplementationGroup <em>Implementation Group</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.Component#getImplementation <em>Implementation</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.Component#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.Component#getReferences <em>References</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.Component#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.Component#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.Component#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface Component
{
  /**
   * Returns the value of the '<em><b>Implementation Group</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Implementation Group</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Implementation Group</em>' attribute list.
   * @generated
   */
  Sequence getImplementationGroup();

  /**
   * Returns the value of the '<em><b>Implementation</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Implementation</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Implementation</em>' containment reference.
   * @see #setImplementation(Implementation)
   * @generated
   */
  Implementation getImplementation();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.Component#getImplementation <em>Implementation</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Implementation</em>' containment reference.
   * @see #getImplementation()
   * @generated
   */
  void setImplementation(Implementation value);

  /**
   * Returns the value of the '<em><b>Properties</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Properties</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Properties</em>' containment reference.
   * @see #setProperties(PropertyValues)
   * @generated
   */
  PropertyValues getProperties();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.Component#getProperties <em>Properties</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Properties</em>' containment reference.
   * @see #getProperties()
   * @generated
   */
  void setProperties(PropertyValues value);

  /**
   * Returns the value of the '<em><b>References</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>References</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>References</em>' containment reference.
   * @see #setReferences(ReferenceValues)
   * @generated
   */
  ReferenceValues getReferences();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.Component#getReferences <em>References</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>References</em>' containment reference.
   * @see #getReferences()
   * @generated
   */
  void setReferences(ReferenceValues value);

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
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.Component#getName <em>Name</em>}' attribute.
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

} // Component
