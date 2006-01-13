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


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.impl.XMLTypePackageImpl;

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
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class MessageElementPackageImpl extends EPackageImpl implements MessageElementPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass bodyElementEClass = null;

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
    private EClass faultCodeElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass faultDetailElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass faultElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass faultReasonElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass faultReasonTextElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass faultSubCodeElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass headerElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass messageEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass notUnderstoodTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass supportedEnvElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass upgradeTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType faultcodeEnumEDataType = null;

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
     * @see org.apache.tuscany.core.message.sdo.MessageElementPackage#eNS_URI
     * @see #init()
     */
    private MessageElementPackageImpl() {
        super(eNS_URI, MessageElementFactory.eINSTANCE);
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
    public static MessageElementPackage init() {
        if (isInited) return (MessageElementPackage) EPackage.Registry.INSTANCE.getEPackage(MessageElementPackage.eNS_URI);

        // Obtain or create and register package
        MessageElementPackageImpl theMessagePackage = (MessageElementPackageImpl) (EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof MessageElementPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new MessageElementPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        XMLTypePackageImpl.init();

        // Create package meta-data objects
        theMessagePackage.createPackageContents();

        // Initialize created meta-data
        theMessagePackage.initializePackageContents();

        // Register package validator
        EValidator.Registry.INSTANCE.put
                (theMessagePackage,
                        new EValidator.Descriptor() {
                            public EValidator getEValidator() {
                                return MessageValidator.INSTANCE;
                            }
                        });

        // Mark meta-data to indicate it can't be changed
        theMessagePackage.freeze();

        return theMessagePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getBodyElement() {
        return bodyElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getBodyElement_Any() {
        return (EAttribute) bodyElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getBodyElement_AnyAttribute() {
        return (EAttribute) bodyElementEClass.getEStructuralFeatures().get(1);
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
    public EReference getDocumentRoot_BodyElement() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_Envelope() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_FaultElement() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_HeaderElement() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_NotlUnderstoodElement() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_UpgradeElement() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_EncodingStyle() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_MustUnderstand() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_Relay() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_Role() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getFaultCodeElement() {
        return faultCodeElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getFaultCodeElement_Value() {
        return (EAttribute) faultCodeElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getFaultCodeElement_Subcode() {
        return (EReference) faultCodeElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getFaultDetailElement() {
        return faultDetailElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getFaultDetailElement_Any() {
        return (EAttribute) faultDetailElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getFaultDetailElement_AnyAttribute() {
        return (EAttribute) faultDetailElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getFaultElement() {
        return faultElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getFaultElement_Code() {
        return (EReference) faultElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getFaultElement_Reason() {
        return (EReference) faultElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getFaultElement_Node() {
        return (EAttribute) faultElementEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getFaultElement_Role() {
        return (EAttribute) faultElementEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getFaultElement_Detail() {
        return (EReference) faultElementEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getFaultReasonElement() {
        return faultReasonElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getFaultReasonElement_Text() {
        return (EReference) faultReasonElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getFaultReasonTextElement() {
        return faultReasonTextElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getFaultReasonTextElement_Value() {
        return (EAttribute) faultReasonTextElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getFaultReasonTextElement_Lang() {
        return (EAttribute) faultReasonTextElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getFaultSubCodeElement() {
        return faultSubCodeElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getFaultSubCodeElement_Value() {
        return (EAttribute) faultSubCodeElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getFaultSubCodeElement_Subcode() {
        return (EReference) faultSubCodeElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getHeaderElement() {
        return headerElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getHeaderElement_Any() {
        return (EAttribute) headerElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getHeaderElement_AnyAttribute() {
        return (EAttribute) headerElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getMessage() {
        return messageEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getMessage_HeaderElement() {
        return (EReference) messageEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getMessage_BodyElement() {
        return (EReference) messageEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getMessage_AnyAttribute() {
        return (EAttribute) messageEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getNotUnderstoodType() {
        return notUnderstoodTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getNotUnderstoodType_Qname() {
        return (EAttribute) notUnderstoodTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getSupportedEnvElement() {
        return supportedEnvElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSupportedEnvElement_Qname() {
        return (EAttribute) supportedEnvElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getUpgradeType() {
        return upgradeTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getUpgradeType_SupportedEnvelope() {
        return (EReference) upgradeTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getFaultcodeEnum() {
        return faultcodeEnumEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public MessageElementFactory getMessageFactory() {
        return (MessageElementFactory) getEFactoryInstance();
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
        bodyElementEClass = createEClass(BODY_ELEMENT);
        createEAttribute(bodyElementEClass, BODY_ELEMENT__ANY);
        createEAttribute(bodyElementEClass, BODY_ELEMENT__ANY_ATTRIBUTE);

        documentRootEClass = createEClass(DOCUMENT_ROOT);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
        createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
        createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
        createEReference(documentRootEClass, DOCUMENT_ROOT__BODY_ELEMENT);
        createEReference(documentRootEClass, DOCUMENT_ROOT__ENVELOPE);
        createEReference(documentRootEClass, DOCUMENT_ROOT__FAULT_ELEMENT);
        createEReference(documentRootEClass, DOCUMENT_ROOT__HEADER_ELEMENT);
        createEReference(documentRootEClass, DOCUMENT_ROOT__NOTL_UNDERSTOOD_ELEMENT);
        createEReference(documentRootEClass, DOCUMENT_ROOT__UPGRADE_ELEMENT);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__ENCODING_STYLE);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__MUST_UNDERSTAND);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__RELAY);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__ROLE);

        faultCodeElementEClass = createEClass(FAULT_CODE_ELEMENT);
        createEAttribute(faultCodeElementEClass, FAULT_CODE_ELEMENT__VALUE);
        createEReference(faultCodeElementEClass, FAULT_CODE_ELEMENT__SUBCODE);

        faultDetailElementEClass = createEClass(FAULT_DETAIL_ELEMENT);
        createEAttribute(faultDetailElementEClass, FAULT_DETAIL_ELEMENT__ANY);
        createEAttribute(faultDetailElementEClass, FAULT_DETAIL_ELEMENT__ANY_ATTRIBUTE);

        faultElementEClass = createEClass(FAULT_ELEMENT);
        createEReference(faultElementEClass, FAULT_ELEMENT__CODE);
        createEReference(faultElementEClass, FAULT_ELEMENT__REASON);
        createEAttribute(faultElementEClass, FAULT_ELEMENT__NODE);
        createEAttribute(faultElementEClass, FAULT_ELEMENT__ROLE);
        createEReference(faultElementEClass, FAULT_ELEMENT__DETAIL);

        faultReasonElementEClass = createEClass(FAULT_REASON_ELEMENT);
        createEReference(faultReasonElementEClass, FAULT_REASON_ELEMENT__TEXT);

        faultReasonTextElementEClass = createEClass(FAULT_REASON_TEXT_ELEMENT);
        createEAttribute(faultReasonTextElementEClass, FAULT_REASON_TEXT_ELEMENT__VALUE);
        createEAttribute(faultReasonTextElementEClass, FAULT_REASON_TEXT_ELEMENT__LANG);

        faultSubCodeElementEClass = createEClass(FAULT_SUB_CODE_ELEMENT);
        createEAttribute(faultSubCodeElementEClass, FAULT_SUB_CODE_ELEMENT__VALUE);
        createEReference(faultSubCodeElementEClass, FAULT_SUB_CODE_ELEMENT__SUBCODE);

        headerElementEClass = createEClass(HEADER_ELEMENT);
        createEAttribute(headerElementEClass, HEADER_ELEMENT__ANY);
        createEAttribute(headerElementEClass, HEADER_ELEMENT__ANY_ATTRIBUTE);

        messageEClass = createEClass(MESSAGE);
        createEReference(messageEClass, MESSAGE__HEADER_ELEMENT);
        createEReference(messageEClass, MESSAGE__BODY_ELEMENT);
        createEAttribute(messageEClass, MESSAGE__ANY_ATTRIBUTE);

        notUnderstoodTypeEClass = createEClass(NOT_UNDERSTOOD_TYPE);
        createEAttribute(notUnderstoodTypeEClass, NOT_UNDERSTOOD_TYPE__QNAME);

        supportedEnvElementEClass = createEClass(SUPPORTED_ENV_ELEMENT);
        createEAttribute(supportedEnvElementEClass, SUPPORTED_ENV_ELEMENT__QNAME);

        upgradeTypeEClass = createEClass(UPGRADE_TYPE);
        createEReference(upgradeTypeEClass, UPGRADE_TYPE__SUPPORTED_ENVELOPE);

        // Create data types
        faultcodeEnumEDataType = createEDataType(FAULTCODE_ENUM);
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
        initEClass(bodyElementEClass, BodyElement.class, "BodyElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getBodyElement_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, BodyElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getBodyElement_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, BodyElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_BodyElement(), this.getBodyElement(), null, "bodyElement", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Envelope(), this.getMessage(), null, "envelope", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_FaultElement(), this.getFaultElement(), null, "faultElement", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_HeaderElement(), this.getHeaderElement(), null, "headerElement", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_NotlUnderstoodElement(), this.getNotUnderstoodType(), null, "notlUnderstoodElement", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_UpgradeElement(), this.getUpgradeType(), null, "upgradeElement", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getDocumentRoot_EncodingStyle(), theXMLTypePackage.getAnyURI(), "encodingStyle", null, 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDocumentRoot_MustUnderstand(), theXMLTypePackage.getBoolean(), "mustUnderstand", "0", 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDocumentRoot_Relay(), theXMLTypePackage.getBoolean(), "relay", "0", 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDocumentRoot_Role(), theXMLTypePackage.getAnyURI(), "role", null, 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(faultCodeElementEClass, FaultCodeElement.class, "FaultCodeElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getFaultCodeElement_Value(), this.getFaultcodeEnum(), "value", null, 1, 1, FaultCodeElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getFaultCodeElement_Subcode(), this.getFaultSubCodeElement(), null, "subcode", null, 0, 1, FaultCodeElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(faultDetailElementEClass, FaultDetailElement.class, "FaultDetailElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getFaultDetailElement_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, FaultDetailElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFaultDetailElement_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, FaultDetailElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(faultElementEClass, FaultElement.class, "FaultElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getFaultElement_Code(), this.getFaultCodeElement(), null, "code", null, 1, 1, FaultElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getFaultElement_Reason(), this.getFaultReasonElement(), null, "reason", null, 1, 1, FaultElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFaultElement_Node(), theXMLTypePackage.getAnyURI(), "node", null, 0, 1, FaultElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFaultElement_Role(), theXMLTypePackage.getAnyURI(), "role", null, 0, 1, FaultElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getFaultElement_Detail(), this.getFaultDetailElement(), null, "detail", null, 0, 1, FaultElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(faultReasonElementEClass, FaultReasonElement.class, "FaultReasonElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getFaultReasonElement_Text(), this.getFaultReasonTextElement(), null, "text", null, 1, -1, FaultReasonElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(faultReasonTextElementEClass, FaultReasonTextElement.class, "FaultReasonTextElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getFaultReasonTextElement_Value(), theXMLTypePackage.getString(), "value", null, 0, 1, FaultReasonTextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFaultReasonTextElement_Lang(), theXMLTypePackage.getLanguage(), "lang", null, 1, 1, FaultReasonTextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(faultSubCodeElementEClass, FaultSubCodeElement.class, "FaultSubCodeElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getFaultSubCodeElement_Value(), theXMLTypePackage.getQName(), "value", null, 1, 1, FaultSubCodeElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getFaultSubCodeElement_Subcode(), this.getFaultSubCodeElement(), null, "subcode", null, 0, 1, FaultSubCodeElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(headerElementEClass, HeaderElement.class, "HeaderElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getHeaderElement_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, HeaderElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getHeaderElement_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, HeaderElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(messageEClass, MessageElement.class, "Message", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getMessage_HeaderElement(), this.getHeaderElement(), null, "headerElement", null, 0, 1, MessageElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getMessage_BodyElement(), this.getBodyElement(), null, "bodyElement", null, 1, 1, MessageElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMessage_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, MessageElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(notUnderstoodTypeEClass, NotUnderstoodType.class, "NotUnderstoodType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getNotUnderstoodType_Qname(), theXMLTypePackage.getQName(), "qname", null, 1, 1, NotUnderstoodType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(supportedEnvElementEClass, SupportedEnvElement.class, "SupportedEnvElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getSupportedEnvElement_Qname(), theXMLTypePackage.getQName(), "qname", null, 1, 1, SupportedEnvElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(upgradeTypeEClass, UpgradeType.class, "UpgradeType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getUpgradeType_SupportedEnvelope(), this.getSupportedEnvElement(), null, "supportedEnvelope", null, 1, -1, UpgradeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize data types
        initEDataType(faultcodeEnumEDataType, Object.class, "FaultcodeEnum", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

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
                (bodyElementEClass,
                        source,
                        new String[]{
                                "name", "Body",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getBodyElement_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##any",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getBodyElement_AnyAttribute(),
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
                (getDocumentRoot_BodyElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Body",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_Envelope(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Envelope",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_FaultElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Fault",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_HeaderElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Header",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_NotlUnderstoodElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "NotUnderstood",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_UpgradeElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Upgrade",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_EncodingStyle(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "encodingStyle",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_MustUnderstand(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "mustUnderstand",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_Relay(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "relay",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_Role(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "role",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (faultCodeElementEClass,
                        source,
                        new String[]{
                                "name", "faultcode",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getFaultCodeElement_Value(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Value",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getFaultCodeElement_Subcode(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Subcode",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (faultcodeEnumEDataType,
                        source,
                        new String[]{
                                "name", "faultcodeEnum",
                                "baseType", "http://www.eclipse.org/emf/2003/XMLType#QName",
                                "enumeration", "tns:DataEncodingUnknown tns:MustUnderstand tns:Receiver tns:Sender tns:VersionMismatch"
                        });
        addAnnotation
                (faultDetailElementEClass,
                        source,
                        new String[]{
                                "name", "detail",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getFaultDetailElement_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##any",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getFaultDetailElement_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##other",
                                "name", ":1",
                                "processing", "lax"
                        });
        addAnnotation
                (faultElementEClass,
                        source,
                        new String[]{
                                "name", "Fault",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getFaultElement_Code(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Code",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getFaultElement_Reason(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Reason",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getFaultElement_Node(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Node",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getFaultElement_Role(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Role",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getFaultElement_Detail(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Detail",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (faultReasonElementEClass,
                        source,
                        new String[]{
                                "name", "faultreason",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getFaultReasonElement_Text(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Text",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (faultReasonTextElementEClass,
                        source,
                        new String[]{
                                "name", "reasontext",
                                "kind", "simple"
                        });
        addAnnotation
                (getFaultReasonTextElement_Value(),
                        source,
                        new String[]{
                                "name", ":0",
                                "kind", "simple"
                        });
        addAnnotation
                (getFaultReasonTextElement_Lang(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "lang",
                                "namespace", "http://www.w3.org/XML/1998/namespace"
                        });
        addAnnotation
                (faultSubCodeElementEClass,
                        source,
                        new String[]{
                                "name", "subcode",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getFaultSubCodeElement_Value(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Value",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getFaultSubCodeElement_Subcode(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Subcode",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (headerElementEClass,
                        source,
                        new String[]{
                                "name", "Header",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getHeaderElement_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##any",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getHeaderElement_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##other",
                                "name", ":1",
                                "processing", "lax"
                        });
        addAnnotation
                (messageEClass,
                        source,
                        new String[]{
                                "name", "Envelope",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getMessage_HeaderElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Header",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getMessage_BodyElement(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "Body",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getMessage_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##other",
                                "name", ":2",
                                "processing", "lax"
                        });
        addAnnotation
                (notUnderstoodTypeEClass,
                        source,
                        new String[]{
                                "name", "NotUnderstoodType",
                                "kind", "empty"
                        });
        addAnnotation
                (getNotUnderstoodType_Qname(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "qname"
                        });
        addAnnotation
                (supportedEnvElementEClass,
                        source,
                        new String[]{
                                "name", "SupportedEnvType",
                                "kind", "empty"
                        });
        addAnnotation
                (getSupportedEnvElement_Qname(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "qname"
                        });
        addAnnotation
                (upgradeTypeEClass,
                        source,
                        new String[]{
                                "name", "UpgradeType",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getUpgradeType_SupportedEnvelope(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "SupportedEnvelope",
			 "namespace", "##targetNamespace"
		   });
	}
	
	/**
	 * Custom code
	 */
	
	/**
	 * Constructor
	 */
	protected MessageElementPackageImpl(EFactory factory) {
		super(eNS_URI, factory);
	}

} //MessagePackageImpl
