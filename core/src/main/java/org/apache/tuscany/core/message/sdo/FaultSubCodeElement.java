/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.message.sdo;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Fault Sub Code Element</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link FaultSubCodeElement#getValue <em>Value</em>}</li>
 * <li>{@link FaultSubCodeElement#getSubcode <em>Subcode</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='subcode' kind='elementOnly'"
 * @generated
 * @see MessageElementPackage#getFaultSubCodeElement()
 */
public interface FaultSubCodeElement {
    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Value</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.QName" required="true"
     * extendedMetaData="kind='element' name='Value' namespace='##targetNamespace'"
     * @generated
     * @see #setValue(Object)
     * @see MessageElementPackage#getFaultSubCodeElement_Value()
     */
    Object getValue();

    /**
     * Sets the value of the '{@link FaultSubCodeElement#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Value</em>' attribute.
     * @generated
     * @see #getValue()
     */
    void setValue(Object value);

    /**
     * Returns the value of the '<em><b>Subcode</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Subcode</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Subcode</em>' containment reference.
     * @model containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='Subcode' namespace='##targetNamespace'"
     * @generated
     * @see #setSubcode(FaultSubCodeElement)
     * @see MessageElementPackage#getFaultSubCodeElement_Subcode()
     */
    FaultSubCodeElement getSubcode();

    /**
     * Sets the value of the '{@link FaultSubCodeElement#getSubcode <em>Subcode</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Subcode</em>' containment reference.
     * @generated
     * @see #getSubcode()
     */
    void setSubcode(FaultSubCodeElement value);

} // FaultSubCodeElement
