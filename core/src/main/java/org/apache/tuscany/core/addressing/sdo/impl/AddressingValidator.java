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
package org.apache.tuscany.core.addressing.sdo.impl;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeValidator;

import org.apache.tuscany.core.addressing.sdo.AddressingElementFactory;
import org.apache.tuscany.core.addressing.sdo.AddressingElementPackage;
import org.apache.tuscany.core.addressing.sdo.AttributedQName;
import org.apache.tuscany.core.addressing.sdo.AttributedURI;
import org.apache.tuscany.core.addressing.sdo.DocumentRoot;
import org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement;
import org.apache.tuscany.core.addressing.sdo.ReferenceParameters;
import org.apache.tuscany.core.addressing.sdo.ReferenceProperties;
import org.apache.tuscany.core.addressing.sdo.Relationship;
import org.apache.tuscany.core.addressing.sdo.ReplyAfter;
import org.apache.tuscany.core.addressing.sdo.ServiceName;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 *
 * @generated
 * @see org.apache.tuscany.core.addressing.sdo.AddressingElementPackage
 */
public class AddressingValidator extends EObjectValidator {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static final AddressingValidator INSTANCE = new AddressingValidator();

    /**
     * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.eclipse.emf.common.util.Diagnostic#getSource()
     * @see org.eclipse.emf.common.util.Diagnostic#getCode()
     */
    public static final String DIAGNOSTIC_SOURCE = "org.apache.tuscany.addressing";

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
    public AddressingValidator() {
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
        return AddressingElementPackage.eINSTANCE;
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
        case AddressingElementPackage.ATTRIBUTED_QNAME:
            return validateAttributedQName((AttributedQName) value, diagnostics, context);
        case AddressingElementPackage.ATTRIBUTED_URI:
            return validateAttributedURI((AttributedURI) value, diagnostics, context);
        case AddressingElementPackage.DOCUMENT_ROOT:
            return validateDocumentRoot((DocumentRoot) value, diagnostics, context);
        case AddressingElementPackage.ENDPOINT_REFERENCE:
            return validateEndpointReference((EndpointReferenceElement) value, diagnostics, context);
        case AddressingElementPackage.REFERENCE_PARAMETERS:
            return validateReferenceParameters((ReferenceParameters) value, diagnostics, context);
        case AddressingElementPackage.REFERENCE_PROPERTIES:
            return validateReferenceProperties((ReferenceProperties) value, diagnostics, context);
        case AddressingElementPackage.RELATIONSHIP:
            return validateRelationship((Relationship) value, diagnostics, context);
        case AddressingElementPackage.REPLY_AFTER:
            return validateReplyAfter((ReplyAfter) value, diagnostics, context);
        case AddressingElementPackage.SERVICE_NAME:
            return validateServiceName((ServiceName) value, diagnostics, context);
        case AddressingElementPackage.FAULT_SUBCODE_VALUES:
            return validateFaultSubcodeValues(value, diagnostics, context);
        case AddressingElementPackage.RELATIONSHIP_TYPE_VALUES:
            return validateRelationshipTypeValues(value, diagnostics, context);
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
    public boolean validateAttributedQName(AttributedQName attributedQName, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) attributedQName, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateAttributedURI(AttributedURI attributedURI, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) attributedURI, diagnostics, context);
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
    public boolean validateEndpointReference(EndpointReferenceElement endpointReference, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) endpointReference, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateReferenceParameters(ReferenceParameters referenceParameters, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) referenceParameters, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateReferenceProperties(ReferenceProperties referenceProperties, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) referenceProperties, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateRelationship(Relationship relationship, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) relationship, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateReplyAfter(ReplyAfter replyAfter, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) replyAfter, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateServiceName(ServiceName serviceName, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) serviceName, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultSubcodeValues(Object faultSubcodeValues, DiagnosticChain diagnostics, Map context) {
        boolean result = validateFaultSubcodeValues_Enumeration(faultSubcodeValues, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #validateFaultSubcodeValues_Enumeration
     */
    public static final Collection FAULT_SUBCODE_VALUES__ENUMERATION__VALUES =
            wrapEnumerationValues
                    (new Object[]{
                            AddressingElementFactory.eINSTANCE.createFromString(AddressingElementPackage.eINSTANCE.getFaultSubcodeValues(), "wsa:InvalidMessageInformationHeader"),
                            AddressingElementFactory.eINSTANCE.createFromString(AddressingElementPackage.eINSTANCE.getFaultSubcodeValues(), "wsa:MessageInformationHeaderRequired"),
                            AddressingElementFactory.eINSTANCE.createFromString(AddressingElementPackage.eINSTANCE.getFaultSubcodeValues(), "wsa:DestinationUnreachable"),
                            AddressingElementFactory.eINSTANCE.createFromString(AddressingElementPackage.eINSTANCE.getFaultSubcodeValues(), "wsa:ActionNotSupported"),
                            AddressingElementFactory.eINSTANCE.createFromString(AddressingElementPackage.eINSTANCE.getFaultSubcodeValues(), "wsa:EndpointUnavailable")
                    });

    /**
     * Validates the Enumeration constraint of '<em>Fault Subcode Values</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateFaultSubcodeValues_Enumeration(Object faultSubcodeValues, DiagnosticChain diagnostics, Map context) {
        boolean result = FAULT_SUBCODE_VALUES__ENUMERATION__VALUES.contains(faultSubcodeValues);
        if (!result && diagnostics != null)
            reportEnumerationViolation(AddressingElementPackage.eINSTANCE.getFaultSubcodeValues(), faultSubcodeValues, FAULT_SUBCODE_VALUES__ENUMERATION__VALUES, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateRelationshipTypeValues(Object relationshipTypeValues, DiagnosticChain diagnostics, Map context) {
        boolean result = validateRelationshipTypeValues_Enumeration(relationshipTypeValues, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #validateRelationshipTypeValues_Enumeration
     */
    public static final Collection RELATIONSHIP_TYPE_VALUES__ENUMERATION__VALUES =
            wrapEnumerationValues
                    (new Object[]{
                            AddressingElementFactory.eINSTANCE.createFromString(AddressingElementPackage.eINSTANCE.getRelationshipTypeValues(), "wsa:Reply")
                    });

    /**
     * Validates the Enumeration constraint of '<em>Relationship Type Values</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateRelationshipTypeValues_Enumeration(Object relationshipTypeValues, DiagnosticChain diagnostics, Map context) {
        boolean result = RELATIONSHIP_TYPE_VALUES__ENUMERATION__VALUES.contains(relationshipTypeValues);
        if (!result && diagnostics != null)
            reportEnumerationViolation(AddressingElementPackage.eINSTANCE.getRelationshipTypeValues(), relationshipTypeValues, RELATIONSHIP_TYPE_VALUES__ENUMERATION__VALUES, diagnostics, context);
		return result; 
	}

} //AddressingValidator
