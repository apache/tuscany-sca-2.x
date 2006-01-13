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

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeValidator;

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
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 *
 * @generated
 * @see org.apache.tuscany.core.message.sdo.MessageElementPackage
 */
public class MessageValidator extends EObjectValidator {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static final MessageValidator INSTANCE = new MessageValidator();

    /**
     * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.eclipse.emf.common.util.Diagnostic#getSource()
     * @see org.eclipse.emf.common.util.Diagnostic#getCode()
     */
    public static final String DIAGNOSTIC_SOURCE = "org.apache.tuscany.message";

    /**
     * A constant with a fixed name that can be used as the base value for additional hand written constants.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

    /**
     * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

    /**
     * The cached base package validator.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected XMLTypeValidator xmlTypeValidator;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public MessageValidator() {
        super();
        xmlTypeValidator = XMLTypeValidator.INSTANCE;
    }

    /**
     * Returns the package of this validator switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EPackage getEPackage() {
        return MessageElementPackage.eINSTANCE;
    }

    /**
     * Calls <code>validateXXX</code> for the corresonding classifier of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map context) {
        switch (classifierID) {
        case MessageElementPackage.BODY_ELEMENT:
            return validateBodyElement((BodyElement) value, diagnostics, context);
        case MessageElementPackage.DOCUMENT_ROOT:
            return validateDocumentRoot((DocumentRoot) value, diagnostics, context);
        case MessageElementPackage.FAULT_CODE_ELEMENT:
            return validateFaultCodeElement((FaultCodeElement) value, diagnostics, context);
        case MessageElementPackage.FAULT_DETAIL_ELEMENT:
            return validateFaultDetailElement((FaultDetailElement) value, diagnostics, context);
        case MessageElementPackage.FAULT_ELEMENT:
            return validateFaultElement((FaultElement) value, diagnostics, context);
        case MessageElementPackage.FAULT_REASON_ELEMENT:
            return validateFaultReasonElement((FaultReasonElement) value, diagnostics, context);
        case MessageElementPackage.FAULT_REASON_TEXT_ELEMENT:
            return validateFaultReasonTextElement((FaultReasonTextElement) value, diagnostics, context);
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT:
            return validateFaultSubCodeElement((FaultSubCodeElement) value, diagnostics, context);
        case MessageElementPackage.HEADER_ELEMENT:
            return validateHeaderElement((HeaderElement) value, diagnostics, context);
        case MessageElementPackage.MESSAGE:
            return validateMessage((MessageElement) value, diagnostics, context);
        case MessageElementPackage.NOT_UNDERSTOOD_TYPE:
            return validateNotUnderstoodType((NotUnderstoodType) value, diagnostics, context);
        case MessageElementPackage.SUPPORTED_ENV_ELEMENT:
            return validateSupportedEnvElement((SupportedEnvElement) value, diagnostics, context);
        case MessageElementPackage.UPGRADE_TYPE:
            return validateUpgradeType((UpgradeType) value, diagnostics, context);
        case MessageElementPackage.FAULTCODE_ENUM:
            return validateFaultcodeEnum(value, diagnostics, context);
        default:
            return true;
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateBodyElement(BodyElement bodyElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) bodyElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateDocumentRoot(DocumentRoot documentRoot, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) documentRoot, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultCodeElement(FaultCodeElement faultCodeElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) faultCodeElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultDetailElement(FaultDetailElement faultDetailElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) faultDetailElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultElement(FaultElement faultElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) faultElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultReasonElement(FaultReasonElement faultReasonElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) faultReasonElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultReasonTextElement(FaultReasonTextElement faultReasonTextElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) faultReasonTextElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultSubCodeElement(FaultSubCodeElement faultSubCodeElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) faultSubCodeElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateHeaderElement(HeaderElement headerElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) headerElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateMessage(MessageElement message, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) message, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateNotUnderstoodType(NotUnderstoodType notUnderstoodType, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) notUnderstoodType, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateSupportedEnvElement(SupportedEnvElement supportedEnvElement, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) supportedEnvElement, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateUpgradeType(UpgradeType upgradeType, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) upgradeType, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultcodeEnum(Object faultcodeEnum, DiagnosticChain diagnostics, Map context) {
        boolean result = validateFaultcodeEnum_Enumeration(faultcodeEnum, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #validateFaultcodeEnum_Enumeration
     */
    public static final Collection FAULTCODE_ENUM__ENUMERATION__VALUES =
            wrapEnumerationValues
                    (new Object[]{
                            MessageElementFactory.eINSTANCE.createFromString(MessageElementPackage.eINSTANCE.getFaultcodeEnum(), "tns:DataEncodingUnknown"),
                            MessageElementFactory.eINSTANCE.createFromString(MessageElementPackage.eINSTANCE.getFaultcodeEnum(), "tns:MustUnderstand"),
                            MessageElementFactory.eINSTANCE.createFromString(MessageElementPackage.eINSTANCE.getFaultcodeEnum(), "tns:Receiver"),
                            MessageElementFactory.eINSTANCE.createFromString(MessageElementPackage.eINSTANCE.getFaultcodeEnum(), "tns:Sender"),
                            MessageElementFactory.eINSTANCE.createFromString(MessageElementPackage.eINSTANCE.getFaultcodeEnum(), "tns:VersionMismatch")
                    });

    /**
     * Validates the Enumeration constraint of '<em>Faultcode Enum</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateFaultcodeEnum_Enumeration(Object faultcodeEnum, DiagnosticChain diagnostics, Map context) {
        boolean result = FAULTCODE_ENUM__ENUMERATION__VALUES.contains(faultcodeEnum);
        if (!result && diagnostics != null)
            reportEnumerationViolation(MessageElementPackage.eINSTANCE.getFaultcodeEnum(), faultcodeEnum, FAULTCODE_ENUM__ENUMERATION__VALUES, diagnostics, context);
		return result; 
	}

} //MessageValidator
