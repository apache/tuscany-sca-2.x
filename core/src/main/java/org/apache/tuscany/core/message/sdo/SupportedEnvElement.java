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
 * A representation of the model object '<em><b>Supported Env Element</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link SupportedEnvElement#getQname <em>Qname</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='SupportedEnvType' kind='empty'"
 * @generated
 * @see MessageElementPackage#getSupportedEnvElement()
 */
public interface SupportedEnvElement {
    /**
     * Returns the value of the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Qname</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Qname</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.QName" required="true"
     * extendedMetaData="kind='attribute' name='qname'"
     * @generated
     * @see #setQname(Object)
     * @see MessageElementPackage#getSupportedEnvElement_Qname()
     */
    Object getQname();

    /**
     * Sets the value of the '{@link SupportedEnvElement#getQname <em>Qname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Qname</em>' attribute.
     * @generated
     * @see #getQname()
     */
    void setQname(Object value);

} // SupportedEnvElement
