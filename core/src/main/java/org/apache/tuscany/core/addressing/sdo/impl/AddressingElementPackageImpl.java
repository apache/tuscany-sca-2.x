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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.impl.XMLTypePackageImpl;

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
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class AddressingElementPackageImpl extends EPackageImpl implements AddressingElementPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass attributedQNameEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass attributedURIEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass documentRootEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass endpointReferenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass referenceParametersEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass referencePropertiesEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass relationshipEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass replyAfterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass serviceNameEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType faultSubcodeValuesEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType relationshipTypeValuesEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.apache.tuscany.core.addressing.sdo.AddressingElementPackage#eNS_URI
     * @see #init()
     */
    private AddressingElementPackageImpl() {
        super(eNS_URI, AddressingElementFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     */
    public static AddressingElementPackage init() {
        if (isInited) return (AddressingElementPackage) EPackage.Registry.INSTANCE.getEPackage(AddressingElementPackage.eNS_URI);

        // Obtain or create and register package
        AddressingElementPackageImpl theAddressingPackage = (AddressingElementPackageImpl) (EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof AddressingElementPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new AddressingElementPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        XMLTypePackageImpl.init();

        // Create package meta-data objects
        theAddressingPackage.createPackageContents();

        // Initialize created meta-data
        theAddressingPackage.initializePackageContents();

        // Register package validator
        EValidator.Registry.INSTANCE.put
                (theAddressingPackage,
                        new EValidator.Descriptor() {
                            public EValidator getEValidator() {
                                return AddressingValidator.INSTANCE;
                            }
                        });

        // Mark meta-data to indicate it can't be changed
        theAddressingPackage.freeze();

        return theAddressingPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getAttributedQName() {
        return attributedQNameEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getAttributedQName_Value() {
        return (EAttribute) attributedQNameEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getAttributedQName_AnyAttribute() {
        return (EAttribute) attributedQNameEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getAttributedURI() {
        return attributedURIEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getAttributedURI_Value() {
        return (EAttribute) attributedURIEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getAttributedURI_AnyAttribute() {
        return (EAttribute) attributedURIEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getDocumentRoot() {
        return documentRootEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_Mixed() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_XMLNSPrefixMap() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_XSISchemaLocation() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_Action() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_EndpointReference() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_FaultTo() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_From() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_MessageID() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_RelatesTo() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_ReplyAfter() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_ReplyTo() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_To() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_Action1() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getEndpointReference() {
        return endpointReferenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getEndpointReference_AddressElement() {
        return (EReference) endpointReferenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getEndpointReference_ReferencePropertiesElement() {
        return (EReference) endpointReferenceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getEndpointReference_ReferenceParametersElement() {
        return (EReference) endpointReferenceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getEndpointReference_PortTypeElement() {
        return (EReference) endpointReferenceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getEndpointReference_ServiceNameElement() {
        return (EReference) endpointReferenceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getEndpointReference_Any() {
        return (EAttribute) endpointReferenceEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getEndpointReference_AnyAttribute() {
        return (EAttribute) endpointReferenceEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getReferenceParameters() {
        return referenceParametersEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReferenceParameters_Any() {
        return (EAttribute) referenceParametersEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getReferenceProperties() {
        return referencePropertiesEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReferenceProperties_Any() {
        return (EAttribute) referencePropertiesEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getRelationship() {
        return relationshipEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getRelationship_Value() {
        return (EAttribute) relationshipEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getRelationship_RelationshipType() {
        return (EAttribute) relationshipEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getRelationship_AnyAttribute() {
        return (EAttribute) relationshipEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getReplyAfter() {
        return replyAfterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReplyAfter_Value() {
        return (EAttribute) replyAfterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReplyAfter_AnyAttribute() {
        return (EAttribute) replyAfterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getServiceName() {
        return serviceNameEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getServiceName_Value() {
        return (EAttribute) serviceNameEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getServiceName_PortName() {
        return (EAttribute) serviceNameEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getServiceName_AnyAttribute() {
        return (EAttribute) serviceNameEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getFaultSubcodeValues() {
        return faultSubcodeValuesEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getRelationshipTypeValues() {
        return relationshipTypeValuesEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AddressingElementFactory getAddressingFactory() {
        return (AddressingElementFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        attributedQNameEClass = createEClass(ATTRIBUTED_QNAME);
        createEAttribute(attributedQNameEClass, ATTRIBUTED_QNAME__VALUE);
        createEAttribute(attributedQNameEClass, ATTRIBUTED_QNAME__ANY_ATTRIBUTE);

        attributedURIEClass = createEClass(ATTRIBUTED_URI);
        createEAttribute(attributedURIEClass, ATTRIBUTED_URI__VALUE);
        createEAttribute(attributedURIEClass, ATTRIBUTED_URI__ANY_ATTRIBUTE);

        documentRootEClass = createEClass(DOCUMENT_ROOT);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
        createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
        createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
        createEReference(documentRootEClass, DOCUMENT_ROOT__ACTION);
        createEReference(documentRootEClass, DOCUMENT_ROOT__ENDPOINT_REFERENCE);
        createEReference(documentRootEClass, DOCUMENT_ROOT__FAULT_TO);
        createEReference(documentRootEClass, DOCUMENT_ROOT__FROM);
        createEReference(documentRootEClass, DOCUMENT_ROOT__MESSAGE_ID);
        createEReference(documentRootEClass, DOCUMENT_ROOT__RELATES_TO);
        createEReference(documentRootEClass, DOCUMENT_ROOT__REPLY_AFTER);
        createEReference(documentRootEClass, DOCUMENT_ROOT__REPLY_TO);
        createEReference(documentRootEClass, DOCUMENT_ROOT__TO);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__ACTION1);

        endpointReferenceEClass = createEClass(ENDPOINT_REFERENCE);
        createEReference(endpointReferenceEClass, ENDPOINT_REFERENCE__ADDRESS_ELEMENT);
        createEReference(endpointReferenceEClass, ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT);
        createEReference(endpointReferenceEClass, ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT);
        createEReference(endpointReferenceEClass, ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT);
        createEReference(endpointReferenceEClass, ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT);
        createEAttribute(endpointReferenceEClass, ENDPOINT_REFERENCE__ANY);
        createEAttribute(endpointReferenceEClass, ENDPOINT_REFERENCE__ANY_ATTRIBUTE);

        referenceParametersEClass = createEClass(REFERENCE_PARAMETERS);
        createEAttribute(referenceParametersEClass, REFERENCE_PARAMETERS__ANY);

        referencePropertiesEClass = createEClass(REFERENCE_PROPERTIES);
        createEAttribute(referencePropertiesEClass, REFERENCE_PROPERTIES__ANY);

        relationshipEClass = createEClass(RELATIONSHIP);
        createEAttribute(relationshipEClass, RELATIONSHIP__VALUE);
        createEAttribute(relationshipEClass, RELATIONSHIP__RELATIONSHIP_TYPE);
        createEAttribute(relationshipEClass, RELATIONSHIP__ANY_ATTRIBUTE);

        replyAfterEClass = createEClass(REPLY_AFTER);
        createEAttribute(replyAfterEClass, REPLY_AFTER__VALUE);
        createEAttribute(replyAfterEClass, REPLY_AFTER__ANY_ATTRIBUTE);

        serviceNameEClass = createEClass(SERVICE_NAME);
        createEAttribute(serviceNameEClass, SERVICE_NAME__VALUE);
        createEAttribute(serviceNameEClass, SERVICE_NAME__PORT_NAME);
        createEAttribute(serviceNameEClass, SERVICE_NAME__ANY_ATTRIBUTE);

        // Create data types
        faultSubcodeValuesEDataType = createEDataType(FAULT_SUBCODE_VALUES);
        relationshipTypeValuesEDataType = createEDataType(RELATIONSHIP_TYPE_VALUES);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        XMLTypePackageImpl theXMLTypePackage = (XMLTypePackageImpl) EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

        // Add supertypes to classes

        // Initialize classes and features; add operations and parameters
        initEClass(attributedQNameEClass, AttributedQName.class, "AttributedQName", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getAttributedQName_Value(), theXMLTypePackage.getQName(), "value", null, 0, 1, AttributedQName.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getAttributedQName_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, AttributedQName.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(attributedURIEClass, AttributedURI.class, "AttributedURI", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getAttributedURI_Value(), theXMLTypePackage.getAnyURI(), "value", null, 0, 1, AttributedURI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getAttributedURI_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, AttributedURI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Action(), this.getAttributedURI(), null, "action", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_EndpointReference(), this.getEndpointReference(), null, "endpointReference", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_FaultTo(), this.getEndpointReference(), null, "faultTo", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_From(), this.getEndpointReference(), null, "from", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_MessageID(), this.getAttributedURI(), null, "messageID", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_RelatesTo(), this.getRelationship(), null, "relatesTo", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_ReplyAfter(), this.getReplyAfter(), null, "replyAfter", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_ReplyTo(), this.getEndpointReference(), null, "replyTo", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_To(), this.getAttributedURI(), null, "to", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getDocumentRoot_Action1(), theXMLTypePackage.getAnyURI(), "action1", null, 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(endpointReferenceEClass, EndpointReferenceElement.class, "EndpointReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getEndpointReference_AddressElement(), this.getAttributedURI(), null, "addressElement", null, 1, 1, EndpointReferenceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEndpointReference_ReferencePropertiesElement(), this.getReferenceProperties(), null, "referencePropertiesElement", null, 0, 1, EndpointReferenceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEndpointReference_ReferenceParametersElement(), this.getReferenceParameters(), null, "referenceParametersElement", null, 0, 1, EndpointReferenceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEndpointReference_PortTypeElement(), this.getAttributedQName(), null, "portTypeElement", null, 0, 1, EndpointReferenceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEndpointReference_ServiceNameElement(), this.getServiceName(), null, "serviceNameElement", null, 0, 1, EndpointReferenceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEndpointReference_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, EndpointReferenceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEndpointReference_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, EndpointReferenceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(referenceParametersEClass, ReferenceParameters.class, "ReferenceParameters", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getReferenceParameters_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ReferenceParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(referencePropertiesEClass, ReferenceProperties.class, "ReferenceProperties", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getReferenceProperties_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ReferenceProperties.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(relationshipEClass, Relationship.class, "Relationship", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getRelationship_Value(), theXMLTypePackage.getAnyURI(), "value", null, 0, 1, Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getRelationship_RelationshipType(), theXMLTypePackage.getQName(), "relationshipType", null, 0, 1, Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getRelationship_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(replyAfterEClass, ReplyAfter.class, "ReplyAfter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getReplyAfter_Value(), theXMLTypePackage.getNonNegativeInteger(), "value", null, 0, 1, ReplyAfter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getReplyAfter_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ReplyAfter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(serviceNameEClass, ServiceName.class, "ServiceName", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getServiceName_Value(), theXMLTypePackage.getQName(), "value", null, 0, 1, ServiceName.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getServiceName_PortName(), theXMLTypePackage.getNCName(), "portName", null, 0, 1, ServiceName.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getServiceName_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ServiceName.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize data types
        initEDataType(faultSubcodeValuesEDataType, Object.class, "FaultSubcodeValues", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(relationshipTypeValuesEDataType, Object.class, "RelationshipTypeValues", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);

        // Create annotations
        // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
        createExtendedMetaDataAnnotations();
    }

    /**
     * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void createExtendedMetaDataAnnotations() {
        String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
        addAnnotation
                (attributedQNameEClass,
                        source,
                        new String[]{
                                "name", "AttributedQName",
                                "kind", "simple"
                        });
        addAnnotation
                (getAttributedQName_Value(),
                        source,
                        new String[]{
                                "name", ":0",
                                "kind", "simple"
                        });
        addAnnotation
                (getAttributedQName_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##other",
                                "name", ":1",
                                "processing", "lax"
                        });
        addAnnotation
                (attributedURIEClass,
                        source,
                        new String[]{
                                "name", "AttributedURI",
                                "kind", "simple"
                        });
        addAnnotation
                (getAttributedURI_Value(),
                        source,
                        new String[]{
                                "name", ":0",
                                "kind", "simple"
                        });
        addAnnotation
                (getAttributedURI_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##other",
                                "name", ":1",
                                "processing", "lax"
                        });
        addAnnotation
                (documentRootEClass,
                        source,
                        new String[]{
                                "name", "",
                                "kind", "mixed"
                        });
        addAnnotation
                (getDocumentRoot_Mixed(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "name", ":mixed"
                        });
        addAnnotation
                (getDocumentRoot_XMLNSPrefixMap(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "xmlns:prefix"
                        });
        addAnnotation
                (getDocumentRoot_XSISchemaLocation(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "xsi:schemaLocation"
                        });
        addAnnotation
                (getDocumentRoot_Action(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Action",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_EndpointReference(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "EndpointReference",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_FaultTo(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "FaultTo",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_From(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "From",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_MessageID(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "MessageID",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_RelatesTo(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "RelatesTo",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_ReplyAfter(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "ReplyAfter",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_ReplyTo(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "ReplyTo",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_To(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "To",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_Action1(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "Action",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (endpointReferenceEClass,
                        source,
                        new String[]{
                                "name", "EndpointReferenceType",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getEndpointReference_AddressElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Address",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getEndpointReference_ReferencePropertiesElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "ReferenceProperties",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getEndpointReference_ReferenceParametersElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "ReferenceParameters",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getEndpointReference_PortTypeElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "PortType",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getEndpointReference_ServiceNameElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "ServiceName",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getEndpointReference_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":5",
                                "processing", "lax"
                        });
        addAnnotation
                (getEndpointReference_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##other",
                                "name", ":6",
                                "processing", "lax"
                        });
        addAnnotation
                (faultSubcodeValuesEDataType,
                        source,
                        new String[]{
                                "name", "FaultSubcodeValues",
                                "baseType", "http://www.eclipse.org/emf/2003/XMLType#QName",
                                "enumeration", "wsa:InvalidMessageInformationHeader wsa:MessageInformationHeaderRequired wsa:DestinationUnreachable wsa:ActionNotSupported wsa:EndpointUnavailable"
                        });
        addAnnotation
                (referenceParametersEClass,
                        source,
                        new String[]{
                                "name", "ReferenceParametersType",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getReferenceParameters_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##any",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (referencePropertiesEClass,
                        source,
                        new String[]{
                                "name", "ReferencePropertiesType",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getReferenceProperties_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##any",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (relationshipEClass,
                        source,
                        new String[]{
                                "name", "Relationship",
                                "kind", "simple"
                        });
        addAnnotation
                (getRelationship_Value(),
                        source,
                        new String[]{
                                "name", ":0",
                                "kind", "simple"
                        });
        addAnnotation
                (getRelationship_RelationshipType(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "RelationshipType"
                        });
        addAnnotation
                (getRelationship_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##other",
                                "name", ":2",
                                "processing", "lax"
                        });
        addAnnotation
                (relationshipTypeValuesEDataType,
                        source,
                        new String[]{
                                "name", "RelationshipTypeValues",
                                "baseType", "http://www.eclipse.org/emf/2003/XMLType#QName",
                                "enumeration", "wsa:Reply"
                        });
        addAnnotation
                (replyAfterEClass,
                        source,
                        new String[]{
                                "name", "ReplyAfterType",
                                "kind", "simple"
                        });
        addAnnotation
                (getReplyAfter_Value(),
                        source,
                        new String[]{
                                "name", ":0",
                                "kind", "simple"
                        });
        addAnnotation
                (getReplyAfter_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##other",
                                "name", ":1",
                                "processing", "strict"
                        });
        addAnnotation
                (serviceNameEClass,
                        source,
                        new String[]{
                                "name", "ServiceNameType",
                                "kind", "simple"
                        });
        addAnnotation
                (getServiceName_Value(),
                        source,
                        new String[]{
                                "name", ":0",
                                "kind", "simple"
                        });
        addAnnotation
                (getServiceName_PortName(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "PortName"
                        });
        addAnnotation
                (getServiceName_AnyAttribute(),
		   source, 
		   new String[] {
			 "kind", "attributeWildcard",
			 "wildcards", "##other",
			 "name", ":2",
			 "processing", "lax"
		   });
	}

} //AddressingPackageImpl
