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
package org.apache.tuscany.core.addressing.sdo;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @model kind="package"
 * @generated
 * @see AddressingElementFactory
 */
public interface AddressingElementPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNAME = "addressing";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_URI = "http://schemas.xmlsoap.org/ws/2004/08/addressing";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_PREFIX = "wsa";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    AddressingElementPackage eINSTANCE = org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.AttributedQNameImpl <em>Attributed QName</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.AttributedQNameImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getAttributedQName()
     */
    int ATTRIBUTED_QNAME = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ATTRIBUTED_QNAME__VALUE = 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ATTRIBUTED_QNAME__ANY_ATTRIBUTE = 1;

    /**
     * The number of structural features of the the '<em>Attributed QName</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ATTRIBUTED_QNAME_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.AttributedURIImpl <em>Attributed URI</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.AttributedURIImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getAttributedURI()
     */
    int ATTRIBUTED_URI = 1;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ATTRIBUTED_URI__VALUE = 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ATTRIBUTED_URI__ANY_ATTRIBUTE = 1;

    /**
     * The number of structural features of the the '<em>Attributed URI</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ATTRIBUTED_URI_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.DocumentRootImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getDocumentRoot()
     */
    int DOCUMENT_ROOT = 2;

    /**
     * The feature id for the '<em><b>Mixed</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MIXED = 0;

    /**
     * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

    /**
     * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

    /**
     * The feature id for the '<em><b>Action</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ACTION = 3;

    /**
     * The feature id for the '<em><b>Endpoint Reference</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ENDPOINT_REFERENCE = 4;

    /**
     * The feature id for the '<em><b>Fault To</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__FAULT_TO = 5;

    /**
     * The feature id for the '<em><b>From</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__FROM = 6;

    /**
     * The feature id for the '<em><b>Message ID</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MESSAGE_ID = 7;

    /**
     * The feature id for the '<em><b>Relates To</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__RELATES_TO = 8;

    /**
     * The feature id for the '<em><b>Reply After</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__REPLY_AFTER = 9;

    /**
     * The feature id for the '<em><b>Reply To</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__REPLY_TO = 10;

    /**
     * The feature id for the '<em><b>To</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__TO = 11;

    /**
     * The feature id for the '<em><b>Action1</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ACTION1 = 12;

    /**
     * The number of structural features of the the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 13;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.EndpointReferenceElementImpl <em>Endpoint Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.EndpointReferenceElementImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getEndpointReference()
     */
    int ENDPOINT_REFERENCE = 3;

    /**
     * The feature id for the '<em><b>Address Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENDPOINT_REFERENCE__ADDRESS_ELEMENT = 0;

    /**
     * The feature id for the '<em><b>Reference Properties Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT = 1;

    /**
     * The feature id for the '<em><b>Reference Parameters Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT = 2;

    /**
     * The feature id for the '<em><b>Port Type Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT = 3;

    /**
     * The feature id for the '<em><b>Service Name Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT = 4;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENDPOINT_REFERENCE__ANY = 5;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENDPOINT_REFERENCE__ANY_ATTRIBUTE = 6;

    /**
     * The number of structural features of the the '<em>Endpoint Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENDPOINT_REFERENCE_FEATURE_COUNT = 7;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.ReferenceParametersImpl <em>Reference Parameters</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.ReferenceParametersImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getReferenceParameters()
     */
    int REFERENCE_PARAMETERS = 4;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE_PARAMETERS__ANY = 0;

    /**
     * The number of structural features of the the '<em>Reference Parameters</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE_PARAMETERS_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.ReferencePropertiesImpl <em>Reference Properties</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.ReferencePropertiesImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getReferenceProperties()
     */
    int REFERENCE_PROPERTIES = 5;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE_PROPERTIES__ANY = 0;

    /**
     * The number of structural features of the the '<em>Reference Properties</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE_PROPERTIES_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.RelationshipImpl <em>Relationship</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.RelationshipImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getRelationship()
     */
    int RELATIONSHIP = 6;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RELATIONSHIP__VALUE = 0;

    /**
     * The feature id for the '<em><b>Relationship Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RELATIONSHIP__RELATIONSHIP_TYPE = 1;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RELATIONSHIP__ANY_ATTRIBUTE = 2;

    /**
     * The number of structural features of the the '<em>Relationship</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.ReplyAfterImpl <em>Reply After</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.ReplyAfterImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getReplyAfter()
     */
    int REPLY_AFTER = 7;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REPLY_AFTER__VALUE = 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REPLY_AFTER__ANY_ATTRIBUTE = 1;

    /**
     * The number of structural features of the the '<em>Reply After</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REPLY_AFTER_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.addressing.sdo.impl.ServiceNameImpl <em>Service Name</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.addressing.sdo.impl.ServiceNameImpl
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getServiceName()
     */
    int SERVICE_NAME = 8;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE_NAME__VALUE = 0;

    /**
     * The feature id for the '<em><b>Port Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE_NAME__PORT_NAME = 1;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE_NAME__ANY_ATTRIBUTE = 2;

    /**
     * The number of structural features of the the '<em>Service Name</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE_NAME_FEATURE_COUNT = 3;


    /**
     * The meta object id for the '<em>Fault Subcode Values</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see Object
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getFaultSubcodeValues()
     */
    int FAULT_SUBCODE_VALUES = 9;

    /**
     * The meta object id for the '<em>Relationship Type Values</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see Object
     * @see org.apache.tuscany.core.addressing.sdo.impl.AddressingElementPackageImpl#getRelationshipTypeValues()
     */
    int RELATIONSHIP_TYPE_VALUES = 10;


    /**
     * Returns the meta object for class '{@link AttributedQName <em>Attributed QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Attributed QName</em>'.
     * @generated
     * @see AttributedQName
     */
    EClass getAttributedQName();

    /**
     * Returns the meta object for the attribute '{@link AttributedQName#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Value</em>'.
     * @generated
     * @see AttributedQName#getValue()
     * @see #getAttributedQName()
     */
    EAttribute getAttributedQName_Value();

    /**
     * Returns the meta object for the attribute list '{@link AttributedQName#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see AttributedQName#getAnyAttribute()
     * @see #getAttributedQName()
     */
    EAttribute getAttributedQName_AnyAttribute();

    /**
     * Returns the meta object for class '{@link AttributedURI <em>Attributed URI</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Attributed URI</em>'.
     * @generated
     * @see AttributedURI
     */
    EClass getAttributedURI();

    /**
     * Returns the meta object for the attribute '{@link AttributedURI#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Value</em>'.
     * @generated
     * @see AttributedURI#getValue()
     * @see #getAttributedURI()
     */
    EAttribute getAttributedURI_Value();

    /**
     * Returns the meta object for the attribute list '{@link AttributedURI#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see AttributedURI#getAnyAttribute()
     * @see #getAttributedURI()
     */
    EAttribute getAttributedURI_AnyAttribute();

    /**
     * Returns the meta object for class '{@link DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Document Root</em>'.
     * @generated
     * @see DocumentRoot
     */
    EClass getDocumentRoot();

    /**
     * Returns the meta object for the attribute list '{@link DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @generated
     * @see DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_Mixed();

    /**
     * Returns the meta object for the map '{@link DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @generated
     * @see DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XMLNSPrefixMap();

    /**
     * Returns the meta object for the map '{@link DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @generated
     * @see DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XSISchemaLocation();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getAction <em>Action</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Action</em>'.
     * @generated
     * @see DocumentRoot#getAction()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Action();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getEndpointReference <em>Endpoint Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Endpoint Reference</em>'.
     * @generated
     * @see DocumentRoot#getEndpointReference()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_EndpointReference();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getFaultTo <em>Fault To</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Fault To</em>'.
     * @generated
     * @see DocumentRoot#getFaultTo()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_FaultTo();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getFrom <em>From</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>From</em>'.
     * @generated
     * @see DocumentRoot#getFrom()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_From();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getMessageID <em>Message ID</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Message ID</em>'.
     * @generated
     * @see DocumentRoot#getMessageID()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_MessageID();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getRelatesTo <em>Relates To</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Relates To</em>'.
     * @generated
     * @see DocumentRoot#getRelatesTo()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_RelatesTo();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getReplyAfter <em>Reply After</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Reply After</em>'.
     * @generated
     * @see DocumentRoot#getReplyAfter()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_ReplyAfter();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getReplyTo <em>Reply To</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Reply To</em>'.
     * @generated
     * @see DocumentRoot#getReplyTo()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_ReplyTo();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getTo <em>To</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>To</em>'.
     * @generated
     * @see DocumentRoot#getTo()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_To();

    /**
     * Returns the meta object for the attribute '{@link DocumentRoot#getAction1 <em>Action1</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Action1</em>'.
     * @generated
     * @see DocumentRoot#getAction1()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_Action1();

    /**
     * Returns the meta object for class '{@link EndpointReferenceElement <em>Endpoint Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Endpoint Reference</em>'.
     * @generated
     * @see EndpointReferenceElement
     */
    EClass getEndpointReference();

    /**
     * Returns the meta object for the containment reference '{@link EndpointReferenceElement#getAddressElement <em>Address Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Address Element</em>'.
     * @generated
     * @see EndpointReferenceElement#getAddressElement()
     * @see #getEndpointReference()
     */
    EReference getEndpointReference_AddressElement();

    /**
     * Returns the meta object for the containment reference '{@link EndpointReferenceElement#getReferencePropertiesElement <em>Reference Properties Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Reference Properties Element</em>'.
     * @generated
     * @see EndpointReferenceElement#getReferencePropertiesElement()
     * @see #getEndpointReference()
     */
    EReference getEndpointReference_ReferencePropertiesElement();

    /**
     * Returns the meta object for the containment reference '{@link EndpointReferenceElement#getReferenceParametersElement <em>Reference Parameters Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Reference Parameters Element</em>'.
     * @generated
     * @see EndpointReferenceElement#getReferenceParametersElement()
     * @see #getEndpointReference()
     */
    EReference getEndpointReference_ReferenceParametersElement();

    /**
     * Returns the meta object for the containment reference '{@link EndpointReferenceElement#getPortTypeElement <em>Port Type Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Port Type Element</em>'.
     * @generated
     * @see EndpointReferenceElement#getPortTypeElement()
     * @see #getEndpointReference()
     */
    EReference getEndpointReference_PortTypeElement();

    /**
     * Returns the meta object for the containment reference '{@link EndpointReferenceElement#getServiceNameElement <em>Service Name Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Service Name Element</em>'.
     * @generated
     * @see EndpointReferenceElement#getServiceNameElement()
     * @see #getEndpointReference()
     */
    EReference getEndpointReference_ServiceNameElement();

    /**
     * Returns the meta object for the attribute list '{@link EndpointReferenceElement#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see EndpointReferenceElement#getAny()
     * @see #getEndpointReference()
     */
    EAttribute getEndpointReference_Any();

    /**
     * Returns the meta object for the attribute list '{@link EndpointReferenceElement#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see EndpointReferenceElement#getAnyAttribute()
     * @see #getEndpointReference()
     */
    EAttribute getEndpointReference_AnyAttribute();

    /**
     * Returns the meta object for class '{@link ReferenceParameters <em>Reference Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Reference Parameters</em>'.
     * @generated
     * @see ReferenceParameters
     */
    EClass getReferenceParameters();

    /**
     * Returns the meta object for the attribute list '{@link ReferenceParameters#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see ReferenceParameters#getAny()
     * @see #getReferenceParameters()
     */
    EAttribute getReferenceParameters_Any();

    /**
     * Returns the meta object for class '{@link ReferenceProperties <em>Reference Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Reference Properties</em>'.
     * @generated
     * @see ReferenceProperties
     */
    EClass getReferenceProperties();

    /**
     * Returns the meta object for the attribute list '{@link ReferenceProperties#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see ReferenceProperties#getAny()
     * @see #getReferenceProperties()
     */
    EAttribute getReferenceProperties_Any();

    /**
     * Returns the meta object for class '{@link Relationship <em>Relationship</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Relationship</em>'.
     * @generated
     * @see Relationship
     */
    EClass getRelationship();

    /**
     * Returns the meta object for the attribute '{@link Relationship#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Value</em>'.
     * @generated
     * @see Relationship#getValue()
     * @see #getRelationship()
     */
    EAttribute getRelationship_Value();

    /**
     * Returns the meta object for the attribute '{@link Relationship#getRelationshipType <em>Relationship Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Relationship Type</em>'.
     * @generated
     * @see Relationship#getRelationshipType()
     * @see #getRelationship()
     */
    EAttribute getRelationship_RelationshipType();

    /**
     * Returns the meta object for the attribute list '{@link Relationship#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see Relationship#getAnyAttribute()
     * @see #getRelationship()
     */
    EAttribute getRelationship_AnyAttribute();

    /**
     * Returns the meta object for class '{@link ReplyAfter <em>Reply After</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Reply After</em>'.
     * @generated
     * @see ReplyAfter
     */
    EClass getReplyAfter();

    /**
     * Returns the meta object for the attribute '{@link ReplyAfter#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Value</em>'.
     * @generated
     * @see ReplyAfter#getValue()
     * @see #getReplyAfter()
     */
    EAttribute getReplyAfter_Value();

    /**
     * Returns the meta object for the attribute list '{@link ReplyAfter#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see ReplyAfter#getAnyAttribute()
     * @see #getReplyAfter()
     */
    EAttribute getReplyAfter_AnyAttribute();

    /**
     * Returns the meta object for class '{@link ServiceName <em>Service Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Service Name</em>'.
     * @generated
     * @see ServiceName
     */
    EClass getServiceName();

    /**
     * Returns the meta object for the attribute '{@link ServiceName#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Value</em>'.
     * @generated
     * @see ServiceName#getValue()
     * @see #getServiceName()
     */
    EAttribute getServiceName_Value();

    /**
     * Returns the meta object for the attribute '{@link ServiceName#getPortName <em>Port Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Port Name</em>'.
     * @generated
     * @see ServiceName#getPortName()
     * @see #getServiceName()
     */
    EAttribute getServiceName_PortName();

    /**
     * Returns the meta object for the attribute list '{@link ServiceName#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see ServiceName#getAnyAttribute()
     * @see #getServiceName()
     */
    EAttribute getServiceName_AnyAttribute();

    /**
     * Returns the meta object for data type '{@link Object <em>Fault Subcode Values</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for data type '<em>Fault Subcode Values</em>'.
     * @model instanceClass="java.lang.Object"
     * extendedMetaData="name='FaultSubcodeValues' baseType='http://www.eclipse.org/emf/2003/XMLType#QName' enumeration='wsa:InvalidMessageInformationHeader wsa:MessageInformationHeaderRequired wsa:DestinationUnreachable wsa:ActionNotSupported wsa:EndpointUnavailable'"
     * @generated
     * @see Object
     */
    EDataType getFaultSubcodeValues();

    /**
     * Returns the meta object for data type '{@link java.lang.Object <em>Relationship Type Values</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Relationship Type Values</em>'.
     * @see java.lang.Object
     * @model instanceClass="java.lang.Object"
     *        extendedMetaData="name='RelationshipTypeValues' baseType='http://www.eclipse.org/emf/2003/XMLType#QName' enumeration='wsa:Reply'"
     * @generated
     */
    EDataType getRelationshipTypeValues();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AddressingElementFactory getAddressingFactory();

} //AddressingPackage
