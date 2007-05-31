/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.example.customer;

import java.io.Serializable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Customer</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.example.customer.Customer#getFirstName <em>First Name</em>}</li>
 *   <li>{@link com.example.customer.Customer#getMiddleName <em>Middle Name</em>}</li>
 *   <li>{@link com.example.customer.Customer#getLastName <em>Last Name</em>}</li>
 * </ul>
 * </p>
 *
 * @extends Serializable
 * @generated
 */
public interface Customer extends Serializable
{
  /**
   * Returns the value of the '<em><b>First Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>First Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>First Name</em>' attribute.
   * @see #setFirstName(Object)
   * @generated
   */
  Object getFirstName();

  /**
   * Sets the value of the '{@link com.example.customer.Customer#getFirstName <em>First Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>First Name</em>' attribute.
   * @see #getFirstName()
   * @generated
   */
  void setFirstName(Object value);

  /**
   * Returns the value of the '<em><b>Middle Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Middle Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Middle Name</em>' attribute.
   * @see #setMiddleName(Object)
   * @generated
   */
  Object getMiddleName();

  /**
   * Sets the value of the '{@link com.example.customer.Customer#getMiddleName <em>Middle Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Middle Name</em>' attribute.
   * @see #getMiddleName()
   * @generated
   */
  void setMiddleName(Object value);

  /**
   * Returns the value of the '<em><b>Last Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Last Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Last Name</em>' attribute.
   * @see #setLastName(Object)
   * @generated
   */
  Object getLastName();

  /**
   * Sets the value of the '{@link com.example.customer.Customer#getLastName <em>Last Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Last Name</em>' attribute.
   * @see #getLastName()
   * @generated
   */
  void setLastName(Object value);

} // Customer
