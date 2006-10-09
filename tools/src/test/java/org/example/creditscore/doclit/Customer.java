/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.example.creditscore.doclit;

import org.apache.tuscany.sdo.impl.DataObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Customer</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.example.creditscore.doclit.Customer#getSsn <em>Ssn</em>}</li>
 *   <li>{@link org.example.creditscore.doclit.Customer#getFirstName <em>First Name</em>}</li>
 *   <li>{@link org.example.creditscore.doclit.Customer#getLastName <em>Last Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Customer extends DataObjectImpl
{
  /**
   * The default value of the '{@link #getSsn() <em>Ssn</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSsn()
   * @generated
   * @ordered
   */
  protected static final String SSN_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSsn() <em>Ssn</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSsn()
   * @generated
   * @ordered
   */
  protected String ssn = SSN_EDEFAULT;

  /**
   * The default value of the '{@link #getFirstName() <em>First Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFirstName()
   * @generated
   * @ordered
   */
  protected static final String FIRST_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getFirstName() <em>First Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFirstName()
   * @generated
   * @ordered
   */
  protected String firstName = FIRST_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getLastName() <em>Last Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLastName()
   * @generated
   * @ordered
   */
  protected static final String LAST_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLastName() <em>Last Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLastName()
   * @generated
   * @ordered
   */
  protected String lastName = LAST_NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Customer()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return DoclitPackage.Literals.CUSTOMER;
  }

  /**
   * Returns the value of the '<em><b>Ssn</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ssn</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ssn</em>' attribute.
   * @see #setSsn(String)
   * @generated
   */
  public String getSsn()
  {
    return ssn;
  }

  /**
   * Sets the value of the '{@link org.example.creditscore.doclit.Customer#getSsn <em>Ssn</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ssn</em>' attribute.
   * @see #getSsn()
   * @generated
   */
  public void setSsn(String newSsn)
  {
    ssn = newSsn;
  }

  /**
   * Returns the value of the '<em><b>First Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>First Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>First Name</em>' attribute.
   * @see #setFirstName(String)
   * @generated
   */
  public String getFirstName()
  {
    return firstName;
  }

  /**
   * Sets the value of the '{@link org.example.creditscore.doclit.Customer#getFirstName <em>First Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>First Name</em>' attribute.
   * @see #getFirstName()
   * @generated
   */
  public void setFirstName(String newFirstName)
  {
    firstName = newFirstName;
  }

  /**
   * Returns the value of the '<em><b>Last Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Last Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Last Name</em>' attribute.
   * @see #setLastName(String)
   * @generated
   */
  public String getLastName()
  {
    return lastName;
  }

  /**
   * Sets the value of the '{@link org.example.creditscore.doclit.Customer#getLastName <em>Last Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Last Name</em>' attribute.
   * @see #getLastName()
   * @generated
   */
  public void setLastName(String newLastName)
  {
    lastName = newLastName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case DoclitPackage.CUSTOMER__SSN:
        return getSsn();
      case DoclitPackage.CUSTOMER__FIRST_NAME:
        return getFirstName();
      case DoclitPackage.CUSTOMER__LAST_NAME:
        return getLastName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case DoclitPackage.CUSTOMER__SSN:
        setSsn((String)newValue);
        return;
      case DoclitPackage.CUSTOMER__FIRST_NAME:
        setFirstName((String)newValue);
        return;
      case DoclitPackage.CUSTOMER__LAST_NAME:
        setLastName((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case DoclitPackage.CUSTOMER__SSN:
        setSsn(SSN_EDEFAULT);
        return;
      case DoclitPackage.CUSTOMER__FIRST_NAME:
        setFirstName(FIRST_NAME_EDEFAULT);
        return;
      case DoclitPackage.CUSTOMER__LAST_NAME:
        setLastName(LAST_NAME_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case DoclitPackage.CUSTOMER__SSN:
        return SSN_EDEFAULT == null ? ssn != null : !SSN_EDEFAULT.equals(ssn);
      case DoclitPackage.CUSTOMER__FIRST_NAME:
        return FIRST_NAME_EDEFAULT == null ? firstName != null : !FIRST_NAME_EDEFAULT.equals(firstName);
      case DoclitPackage.CUSTOMER__LAST_NAME:
        return LAST_NAME_EDEFAULT == null ? lastName != null : !LAST_NAME_EDEFAULT.equals(lastName);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (ssn: ");
    result.append(ssn);
    result.append(", firstName: ");
    result.append(firstName);
    result.append(", lastName: ");
    result.append(lastName);
    result.append(')');
    return result.toString();
  }

} // Customer