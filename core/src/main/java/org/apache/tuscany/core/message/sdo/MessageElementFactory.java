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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 *
 * @generated
 * @see MessageElementPackage
 */
public interface MessageElementFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    MessageElementFactory eINSTANCE = new org.apache.tuscany.core.message.sdo.impl.MessageElementFactoryImpl();

    /**
     * Returns a new object of class '<em>Body Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Body Element</em>'.
     * @generated
     */
    BodyElement createBodyElement();

    /**
     * Returns a new object of class '<em>Document Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Document Root</em>'.
     * @generated
     */
    DocumentRoot createDocumentRoot();

    /**
     * Returns a new object of class '<em>Fault Code Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Fault Code Element</em>'.
     * @generated
     */
    FaultCodeElement createFaultCodeElement();

    /**
     * Returns a new object of class '<em>Fault Detail Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Fault Detail Element</em>'.
     * @generated
     */
    FaultDetailElement createFaultDetailElement();

    /**
     * Returns a new object of class '<em>Fault Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Fault Element</em>'.
     * @generated
     */
    FaultElement createFaultElement();

    /**
     * Returns a new object of class '<em>Fault Reason Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Fault Reason Element</em>'.
     * @generated
     */
    FaultReasonElement createFaultReasonElement();

    /**
     * Returns a new object of class '<em>Fault Reason Text Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Fault Reason Text Element</em>'.
     * @generated
     */
    FaultReasonTextElement createFaultReasonTextElement();

    /**
     * Returns a new object of class '<em>Fault Sub Code Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Fault Sub Code Element</em>'.
     * @generated
     */
    FaultSubCodeElement createFaultSubCodeElement();

    /**
     * Returns a new object of class '<em>Header Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Header Element</em>'.
     * @generated
     */
    HeaderElement createHeaderElement();

    /**
     * Returns a new object of class '<em>Message</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Message</em>'.
     * @generated
     */
    MessageElement createMessageElement();

    /**
     * Returns a new object of class '<em>Not Understood Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Not Understood Type</em>'.
     * @generated
     */
    NotUnderstoodType createNotUnderstoodType();

    /**
     * Returns a new object of class '<em>Supported Env Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Supported Env Element</em>'.
     * @generated
     */
    SupportedEnvElement createSupportedEnvElement();

    /**
     * Returns a new object of class '<em>Upgrade Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Upgrade Type</em>'.
     * @generated
     */
    UpgradeType createUpgradeType();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    MessageElementPackage getMessagePackage();

} //MessageFactory
