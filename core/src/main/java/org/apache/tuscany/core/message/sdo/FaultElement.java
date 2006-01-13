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
 * A representation of the model object '<em><b>Fault Element</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <!-- begin-model-doc -->
 * <p/>
 * Fault reporting structure
 * <p/>
 * <!-- end-model-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link FaultElement#getCode <em>Code</em>}</li>
 * <li>{@link FaultElement#getReason <em>Reason</em>}</li>
 * <li>{@link FaultElement#getNode <em>Node</em>}</li>
 * <li>{@link FaultElement#getRole <em>Role</em>}</li>
 * <li>{@link FaultElement#getDetail <em>Detail</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='Fault' kind='elementOnly'"
 * @generated
 * @see MessageElementPackage#getFaultElement()
 */
public interface FaultElement {
    /**
     * Returns the value of the '<em><b>Code</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Code</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Code</em>' containment reference.
     * @model containment="true" resolveProxies="false" required="true"
     * extendedMetaData="kind='element' name='Code' namespace='##targetNamespace'"
     * @generated
     * @see #setCode(FaultCodeElement)
     * @see MessageElementPackage#getFaultElement_Code()
     */
    FaultCodeElement getCode();

    /**
     * Sets the value of the '{@link FaultElement#getCode <em>Code</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Code</em>' containment reference.
     * @generated
     * @see #getCode()
     */
    void setCode(FaultCodeElement value);

    /**
     * Returns the value of the '<em><b>Reason</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Reason</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Reason</em>' containment reference.
     * @model containment="true" resolveProxies="false" required="true"
     * extendedMetaData="kind='element' name='Reason' namespace='##targetNamespace'"
     * @generated
     * @see #setReason(FaultReasonElement)
     * @see MessageElementPackage#getFaultElement_Reason()
     */
    FaultReasonElement getReason();

    /**
     * Sets the value of the '{@link FaultElement#getReason <em>Reason</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Reason</em>' containment reference.
     * @generated
     * @see #getReason()
     */
    void setReason(FaultReasonElement value);

    /**
     * Returns the value of the '<em><b>Node</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Node</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Node</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
     * extendedMetaData="kind='element' name='Node' namespace='##targetNamespace'"
     * @generated
     * @see #setNode(String)
     * @see MessageElementPackage#getFaultElement_Node()
     */
    String getNode();

    /**
     * Sets the value of the '{@link FaultElement#getNode <em>Node</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Node</em>' attribute.
     * @generated
     * @see #getNode()
     */
    void setNode(String value);

    /**
     * Returns the value of the '<em><b>Role</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Role</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Role</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
     * extendedMetaData="kind='element' name='Role' namespace='##targetNamespace'"
     * @generated
     * @see #setRole(String)
     * @see MessageElementPackage#getFaultElement_Role()
     */
    String getRole();

    /**
     * Sets the value of the '{@link FaultElement#getRole <em>Role</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Role</em>' attribute.
     * @generated
     * @see #getRole()
     */
    void setRole(String value);

    /**
     * Returns the value of the '<em><b>Detail</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Detail</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Detail</em>' containment reference.
     * @model containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='Detail' namespace='##targetNamespace'"
     * @generated
     * @see #setDetail(FaultDetailElement)
     * @see MessageElementPackage#getFaultElement_Detail()
     */
    FaultDetailElement getDetail();

    /**
     * Sets the value of the '{@link org.apache.tuscany.core.message.sdo.FaultElement#getDetail <em>Detail</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Detail</em>' containment reference.
     * @see #getDetail()
     * @generated
     */
    void setDetail(FaultDetailElement value);

} // FaultElement
