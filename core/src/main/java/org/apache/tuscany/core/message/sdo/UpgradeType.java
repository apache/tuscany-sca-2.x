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

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Upgrade Type</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link UpgradeType#getSupportedEnvelope <em>Supported Envelope</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='UpgradeType' kind='elementOnly'"
 * @generated
 * @see MessageElementPackage#getUpgradeType()
 */
public interface UpgradeType {
    /**
     * Returns the value of the '<em><b>Supported Envelope</b></em>' containment reference list.
     * The list contents are of type {@link SupportedEnvElement}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Supported Envelope</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Supported Envelope</em>' containment reference list.
     * @model type="org.apache.tuscany.message.SupportedEnvElement" containment="true" resolveProxies="false" required="true"
     * extendedMetaData="kind='element' name='SupportedEnvelope' namespace='##targetNamespace'"
     * @generated
     * @see MessageElementPackage#getUpgradeType_SupportedEnvelope()
     */
    List getSupportedEnvelope();

} // UpgradeType
