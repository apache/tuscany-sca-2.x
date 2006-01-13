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
package org.apache.tuscany.core.message.sdo.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.message.sdo.BodyElement;
import org.apache.tuscany.core.message.sdo.DocumentRoot;
import org.apache.tuscany.core.message.sdo.FaultCodeElement;
import org.apache.tuscany.core.message.sdo.FaultDetailElement;
import org.apache.tuscany.core.message.sdo.FaultElement;
import org.apache.tuscany.core.message.sdo.FaultReasonElement;
import org.apache.tuscany.core.message.sdo.FaultReasonTextElement;
import org.apache.tuscany.core.message.sdo.FaultSubCodeElement;
import org.apache.tuscany.core.message.sdo.HeaderElement;
import org.apache.tuscany.core.message.sdo.MessageElement;
import org.apache.tuscany.core.message.sdo.MessageElementFactory;
import org.apache.tuscany.core.message.sdo.MessageElementPackage;
import org.apache.tuscany.core.message.sdo.NotUnderstoodType;
import org.apache.tuscany.core.message.sdo.SupportedEnvElement;
import org.apache.tuscany.core.message.sdo.UpgradeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class MessageElementFactoryImpl extends EFactoryImpl implements MessageElementFactory {
    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public MessageElementFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case MessageElementPackage.BODY_ELEMENT:
            return (EObject) createBodyElement();
        case MessageElementPackage.DOCUMENT_ROOT:
            return (EObject) createDocumentRoot();
        case MessageElementPackage.FAULT_CODE_ELEMENT:
            return (EObject) createFaultCodeElement();
        case MessageElementPackage.FAULT_DETAIL_ELEMENT:
            return (EObject) createFaultDetailElement();
        case MessageElementPackage.FAULT_ELEMENT:
            return (EObject) createFaultElement();
        case MessageElementPackage.FAULT_REASON_ELEMENT:
            return (EObject) createFaultReasonElement();
        case MessageElementPackage.FAULT_REASON_TEXT_ELEMENT:
            return (EObject) createFaultReasonTextElement();
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT:
            return (EObject) createFaultSubCodeElement();
        case MessageElementPackage.HEADER_ELEMENT:
            return (EObject) createHeaderElement();
        case MessageElementPackage.MESSAGE:
            return (EObject) createMessageElement();
        case MessageElementPackage.NOT_UNDERSTOOD_TYPE:
            return (EObject) createNotUnderstoodType();
        case MessageElementPackage.SUPPORTED_ENV_ELEMENT:
            return (EObject) createSupportedEnvElement();
        case MessageElementPackage.UPGRADE_TYPE:
            return (EObject) createUpgradeType();
        default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
        case MessageElementPackage.FAULTCODE_ENUM:
            return createFaultcodeEnumFromString(eDataType, initialValue);
        default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
        case MessageElementPackage.FAULTCODE_ENUM:
            return convertFaultcodeEnumToString(eDataType, instanceValue);
        default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public BodyElement createBodyElement() {
        BodyElementImpl bodyElement = new BodyElementImpl();
        return bodyElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public DocumentRoot createDocumentRoot() {
        DocumentRootImpl documentRoot = new DocumentRootImpl();
        return documentRoot;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultCodeElement createFaultCodeElement() {
        FaultCodeElementImpl faultCodeElement = new FaultCodeElementImpl();
        return faultCodeElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultDetailElement createFaultDetailElement() {
        FaultDetailElementImpl faultDetailElement = new FaultDetailElementImpl();
        return faultDetailElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultElement createFaultElement() {
        FaultElementImpl faultElement = new FaultElementImpl();
        return faultElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultReasonElement createFaultReasonElement() {
        FaultReasonElementImpl faultReasonElement = new FaultReasonElementImpl();
        return faultReasonElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultReasonTextElement createFaultReasonTextElement() {
        FaultReasonTextElementImpl faultReasonTextElement = new FaultReasonTextElementImpl();
        return faultReasonTextElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultSubCodeElement createFaultSubCodeElement() {
        FaultSubCodeElementImpl faultSubCodeElement = new FaultSubCodeElementImpl();
        return faultSubCodeElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public HeaderElement createHeaderElement() {
        HeaderElementImpl headerElement = new HeaderElementImpl();
        return headerElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public MessageElement createMessageElementGen() {
        MessageElementImpl message = new MessageElementImpl();
        return message;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotUnderstoodType createNotUnderstoodType() {
        NotUnderstoodTypeImpl notUnderstoodType = new NotUnderstoodTypeImpl();
        return notUnderstoodType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public SupportedEnvElement createSupportedEnvElement() {
        SupportedEnvElementImpl supportedEnvElement = new SupportedEnvElementImpl();
        return supportedEnvElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public UpgradeType createUpgradeType() {
        UpgradeTypeImpl upgradeType = new UpgradeTypeImpl();
        return upgradeType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object createFaultcodeEnumFromString(EDataType eDataType, String initialValue) {
        return (Object) XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.eINSTANCE.getQName(), initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String convertFaultcodeEnumToString(EDataType eDataType, Object instanceValue) {
        return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.eINSTANCE.getQName(), instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public MessageElementPackage getMessagePackage() {
        return (MessageElementPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @deprecated
     */
    public static MessageElementPackage getPackage() {
        return MessageElementPackage.eINSTANCE;
    }

    /**
     * Custom code
     */

    private MessageFactory messageFactory = new MessageFactoryImpl();

    /**
     * @see org.apache.tuscany.core.message.sdo.MessageElementFactory#createMessageElement()
     */
    public MessageElement createMessageElement() {
        return (MessageElement)messageFactory.createMessage();
	}
	

} //MessageFactoryImpl
