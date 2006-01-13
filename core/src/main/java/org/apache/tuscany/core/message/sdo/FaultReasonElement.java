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
 * A representation of the model object '<em><b>Fault Reason Element</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link FaultReasonElement#getText <em>Text</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='faultreason' kind='elementOnly'"
 * @generated
 * @see MessageElementPackage#getFaultReasonElement()
 */
public interface FaultReasonElement {
    /**
     * Returns the value of the '<em><b>Text</b></em>' containment reference list.
     * The list contents are of type {@link FaultReasonTextElement}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Text</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Text</em>' containment reference list.
     * @model type="org.apache.tuscany.message.FaultReasonTextElement" containment="true" resolveProxies="false" required="true"
     * extendedMetaData="kind='element' name='Text' namespace='##targetNamespace'"
     * @generated
     * @see MessageElementPackage#getFaultReasonElement_Text()
     */
    List getText();

} // FaultReasonElement
