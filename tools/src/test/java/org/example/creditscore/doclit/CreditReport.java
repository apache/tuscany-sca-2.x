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
 * A representation of the model object '<em><b>Credit Report</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.example.creditscore.doclit.CreditReport#getScore <em>Score</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CreditReport extends DataObjectImpl
{
  /**
   * The default value of the '{@link #getScore() <em>Score</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getScore()
   * @generated
   * @ordered
   */
  protected static final int SCORE_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getScore() <em>Score</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getScore()
   * @generated
   * @ordered
   */
  protected int score = SCORE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CreditReport()
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
    return DoclitPackage.Literals.CREDIT_REPORT;
  }

  /**
   * Returns the value of the '<em><b>Score</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Score</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Score</em>' attribute.
   * @see #setScore(int)
   * @generated
   */
  public int getScore()
  {
    return score;
  }

  /**
   * Sets the value of the '{@link org.example.creditscore.doclit.CreditReport#getScore <em>Score</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Score</em>' attribute.
   * @see #getScore()
   * @generated
   */
  public void setScore(int newScore)
  {
    score = newScore;
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
      case DoclitPackage.CREDIT_REPORT__SCORE:
        return new Integer(getScore());
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
      case DoclitPackage.CREDIT_REPORT__SCORE:
        setScore(((Integer)newValue).intValue());
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
      case DoclitPackage.CREDIT_REPORT__SCORE:
        setScore(SCORE_EDEFAULT);
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
      case DoclitPackage.CREDIT_REPORT__SCORE:
        return score != SCORE_EDEFAULT;
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
    result.append(" (score: ");
    result.append(score);
    result.append(')');
    return result.toString();
  }

} // CreditReport