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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.apache.tuscany.core.addressing.AddressingFactory;
import org.apache.tuscany.core.addressing.impl.AddressingFactoryImpl;
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
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class AddressingElementFactoryImpl extends EFactoryImpl implements AddressingElementFactory {
    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AddressingElementFactoryImpl() {
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
        case AddressingElementPackage.ATTRIBUTED_QNAME:
            return (EObject) createAttributedQName();
        case AddressingElementPackage.ATTRIBUTED_URI:
            return (EObject) createAttributedURI();
        case AddressingElementPackage.DOCUMENT_ROOT:
            return (EObject) createDocumentRoot();
        case AddressingElementPackage.ENDPOINT_REFERENCE:
            return (EObject) createEndpointReferenceElement();
        case AddressingElementPackage.REFERENCE_PARAMETERS:
            return (EObject) createReferenceParameters();
        case AddressingElementPackage.REFERENCE_PROPERTIES:
            return (EObject) createReferenceProperties();
        case AddressingElementPackage.RELATIONSHIP:
            return (EObject) createRelationship();
        case AddressingElementPackage.REPLY_AFTER:
            return (EObject) createReplyAfter();
        case AddressingElementPackage.SERVICE_NAME:
            return (EObject) createServiceName();
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
        case AddressingElementPackage.FAULT_SUBCODE_VALUES:
            return createFaultSubcodeValuesFromString(eDataType, initialValue);
        case AddressingElementPackage.RELATIONSHIP_TYPE_VALUES:
            return createRelationshipTypeValuesFromString(eDataType, initialValue);
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
        case AddressingElementPackage.FAULT_SUBCODE_VALUES:
            return convertFaultSubcodeValuesToString(eDataType, instanceValue);
        case AddressingElementPackage.RELATIONSHIP_TYPE_VALUES:
            return convertRelationshipTypeValuesToString(eDataType, instanceValue);
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
    public AttributedQName createAttributedQName() {
        AttributedQNameImpl attributedQName = new AttributedQNameImpl();
        return attributedQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AttributedURI createAttributedURI() {
        AttributedURIImpl attributedURI = new AttributedURIImpl();
        return attributedURI;
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
    public EndpointReferenceElement createEndpointReferenceGen() {
        EndpointReferenceElementImpl endpointReference = new EndpointReferenceElementImpl();
        return endpointReference;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ReferenceParameters createReferenceParameters() {
        ReferenceParametersImpl referenceParameters = new ReferenceParametersImpl();
        return referenceParameters;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ReferenceProperties createReferenceProperties() {
        ReferencePropertiesImpl referenceProperties = new ReferencePropertiesImpl();
        return referenceProperties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Relationship createRelationship() {
        RelationshipImpl relationship = new RelationshipImpl();
        return relationship;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ReplyAfter createReplyAfter() {
        ReplyAfterImpl replyAfter = new ReplyAfterImpl();
        return replyAfter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ServiceName createServiceName() {
        ServiceNameImpl serviceName = new ServiceNameImpl();
        return serviceName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object createFaultSubcodeValuesFromString(EDataType eDataType, String initialValue) {
        return (Object) XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.eINSTANCE.getQName(), initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String convertFaultSubcodeValuesToString(EDataType eDataType, Object instanceValue) {
        return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.eINSTANCE.getQName(), instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object createRelationshipTypeValuesFromString(EDataType eDataType, String initialValue) {
        return (Object) XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.eINSTANCE.getQName(), initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String convertRelationshipTypeValuesToString(EDataType eDataType, Object instanceValue) {
        return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.eINSTANCE.getQName(), instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AddressingElementPackage getAddressingPackage() {
        return (AddressingElementPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @deprecated
     */
    public static AddressingElementPackage getPackage() {
        return AddressingElementPackage.eINSTANCE;
    }

    /**
     * Custom code
     */

    private AddressingFactory endpointReferenceFactory = new AddressingFactoryImpl();

    /**
     * @see org.apache.tuscany.core.addressing.sdo.AddressingElementFactory#createEndpointReferenceElement()
     */
    public EndpointReferenceElement createEndpointReferenceElement() {
        return (EndpointReferenceElement) endpointReferenceFactory.createEndpointReference();
	}

} //AddressingFactoryImpl
