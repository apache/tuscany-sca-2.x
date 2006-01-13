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
 * A representation of the model object '<em><b>Fault Reason Text Element</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link FaultReasonTextElement#getValue <em>Value</em>}</li>
 * <li>{@link FaultReasonTextElement#getLang <em>Lang</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='reasontext' kind='simple'"
 * @generated
 * @see MessageElementPackage#getFaultReasonTextElement()
 */
public interface FaultReasonTextElement {
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
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     * extendedMetaData="name=':0' kind='simple'"
     * @generated
     * @see #setValue(String)
     * @see MessageElementPackage#getFaultReasonTextElement_Value()
     */
    String getValue();

    /**
     * Sets the value of the '{@link FaultReasonTextElement#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Value</em>' attribute.
     * @generated
     * @see #getValue()
     */
    void setValue(String value);

    /**
     * Returns the value of the '<em><b>Lang</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Lang</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Lang</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.Language" required="true"
     * extendedMetaData="kind='attribute' name='lang' namespace='http://www.w3.org/XML/1998/namespace'"
     * @generated
     * @see #setLang(String)
     * @see MessageElementPackage#getFaultReasonTextElement_Lang()
     */
    String getLang();

    /**
     * Sets the value of the '{@link FaultReasonTextElement#getLang <em>Lang</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Lang</em>' attribute.
     * @generated
     * @see #getLang()
     */
    void setLang(String value);

} // FaultReasonTextElement
