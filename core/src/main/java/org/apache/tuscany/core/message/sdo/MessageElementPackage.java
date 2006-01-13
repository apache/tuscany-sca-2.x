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
 * <!-- begin-model-doc -->
 * <p/>
 * See http://www.w3.org/XML/1998/namespace.html and
 * http://www.w3.org/TR/REC-xml for information about this namespace.
 * <p/>
 * This schema document describes the XML namespace, in a form
 * suitable for import by other schema documents.
 * <p/>
 * Note that local names in this namespace are intended to be defined
 * only by the World Wide Web Consortium or its subgroups.  The
 * following names are currently defined in this namespace and should
 * not be used with conflicting semantics by any Working Group,
 * specification, or document instance:
 * <p/>
 * base (as an attribute name): denotes an attribute whose value
 * provides a URI to be used as the base for interpreting any
 * relative URIs in the scope of the element on which it
 * appears; its value is inherited.  This name is reserved
 * by virtue of its definition in the XML Base specification.
 * <p/>
 * id   (as an attribute name): denotes an attribute whose value
 * should be interpreted as if declared to be of type ID.
 * The xml:id specification is not yet a W3C Recommendation,
 * but this attribute is included here to facilitate experimentation
 * with the mechanisms it proposes.  Note that it is _not_ included
 * in the specialAttrs attribute group.
 * <p/>
 * lang (as an attribute name): denotes an attribute whose value
 * is a language code for the natural language of the content of
 * any element; its value is inherited.  This name is reserved
 * by virtue of its definition in the XML specification.
 * <p/>
 * space (as an attribute name): denotes an attribute whose
 * value is a keyword indicating what whitespace processing
 * discipline is intended for the content of the element; its
 * value is inherited.  This name is reserved by virtue of its
 * definition in the XML specification.
 * <p/>
 * Father (in any context at all): denotes Jon Bosak, the chair of
 * the original XML Working Group.  This name is reserved by
 * the following decision of the W3C XML Plenary and
 * XML Coordination groups:
 * <p/>
 * In appreciation for his vision, leadership and dedication
 * the W3C XML Plenary on this 10th day of February, 2000
 * reserves for Jon Bosak in perpetuity the XML name
 * xml:Father
 * <p/>
 * This schema defines attributes and an attribute group
 * suitable for use by
 * schemas wishing to allow xml:base, xml:lang or xml:space attributes
 * on elements they define.
 * <p/>
 * To enable this, such a schema must import this schema
 * for the XML namespace, e.g. as follows:
 * &lt;schema . . .&gt;
 * . . .
 * &lt;import namespace="http://www.w3.org/XML/1998/namespace"
 * schemaLocation="http://www.w3.org/2001/03/xml.xsd"/&gt;
 * <p/>
 * Subsequently, qualified reference to any of the attributes
 * or the group defined below will have the desired effect, e.g.
 * <p/>
 * &lt;type . . .&gt;
 * . . .
 * &lt;attributeGroup ref="xml:specialAttrs"/&gt;
 * <p/>
 * will define a type which will schema-validate an instance
 * element with any of those attributes
 * In keeping with the XML Schema WG's standard versioning
 * policy, this schema document will persist at
 * http://www.w3.org/2004/10/xml.xsd.
 * At the date of issue it can also be found at
 * http://www.w3.org/2001/xml.xsd.
 * The schema document at that URI may however change in the future,
 * in order to remain compatible with the latest version of XML Schema
 * itself, or with the XML namespace itself.  In other words, if the XML
 * Schema or XML namespaces change, the version of this document at
 * http://www.w3.org/2001/xml.xsd will change
 * accordingly; the version at
 * http://www.w3.org/2004/10/xml.xsd will not change.
 * <p/>
 * <!-- end-model-doc -->
 *
 * @model kind="package"
 * @generated
 * @see MessageElementFactory
 */
public interface MessageElementPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNAME = "message";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_URI = "http://www.w3.org/2003/05/soap-envelope";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_PREFIX = "soapenv";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    MessageElementPackage eINSTANCE = org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.BodyElementImpl <em>Body Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.BodyElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getBodyElement()
     */
    int BODY_ELEMENT = 0;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int BODY_ELEMENT__ANY = 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int BODY_ELEMENT__ANY_ATTRIBUTE = 1;

    /**
     * The number of structural features of the the '<em>Body Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int BODY_ELEMENT_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.DocumentRootImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getDocumentRoot()
     */
    int DOCUMENT_ROOT = 1;

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
     * The feature id for the '<em><b>Body Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__BODY_ELEMENT = 3;

    /**
     * The feature id for the '<em><b>Envelope</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ENVELOPE = 4;

    /**
     * The feature id for the '<em><b>Fault Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__FAULT_ELEMENT = 5;

    /**
     * The feature id for the '<em><b>Header Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__HEADER_ELEMENT = 6;

    /**
     * The feature id for the '<em><b>Notl Understood Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__NOTL_UNDERSTOOD_ELEMENT = 7;

    /**
     * The feature id for the '<em><b>Upgrade Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__UPGRADE_ELEMENT = 8;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ENCODING_STYLE = 9;

    /**
     * The feature id for the '<em><b>Must Understand</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MUST_UNDERSTAND = 10;

    /**
     * The feature id for the '<em><b>Relay</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__RELAY = 11;

    /**
     * The feature id for the '<em><b>Role</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ROLE = 12;

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
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.FaultCodeElementImpl <em>Fault Code Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.FaultCodeElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getFaultCodeElement()
     */
    int FAULT_CODE_ELEMENT = 2;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_CODE_ELEMENT__VALUE = 0;

    /**
     * The feature id for the '<em><b>Subcode</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_CODE_ELEMENT__SUBCODE = 1;

    /**
     * The number of structural features of the the '<em>Fault Code Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_CODE_ELEMENT_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.FaultDetailElementImpl <em>Fault Detail Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.FaultDetailElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getFaultDetailElement()
     */
    int FAULT_DETAIL_ELEMENT = 3;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_DETAIL_ELEMENT__ANY = 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_DETAIL_ELEMENT__ANY_ATTRIBUTE = 1;

    /**
     * The number of structural features of the the '<em>Fault Detail Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_DETAIL_ELEMENT_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.FaultElementImpl <em>Fault Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.FaultElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getFaultElement()
     */
    int FAULT_ELEMENT = 4;

    /**
     * The feature id for the '<em><b>Code</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_ELEMENT__CODE = 0;

    /**
     * The feature id for the '<em><b>Reason</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_ELEMENT__REASON = 1;

    /**
     * The feature id for the '<em><b>Node</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_ELEMENT__NODE = 2;

    /**
     * The feature id for the '<em><b>Role</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_ELEMENT__ROLE = 3;

    /**
     * The feature id for the '<em><b>Detail</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_ELEMENT__DETAIL = 4;

    /**
     * The number of structural features of the the '<em>Fault Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_ELEMENT_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.FaultReasonElementImpl <em>Fault Reason Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.FaultReasonElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getFaultReasonElement()
     */
    int FAULT_REASON_ELEMENT = 5;

    /**
     * The feature id for the '<em><b>Text</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_REASON_ELEMENT__TEXT = 0;

    /**
     * The number of structural features of the the '<em>Fault Reason Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_REASON_ELEMENT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.FaultReasonTextElementImpl <em>Fault Reason Text Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.FaultReasonTextElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getFaultReasonTextElement()
     */
    int FAULT_REASON_TEXT_ELEMENT = 6;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_REASON_TEXT_ELEMENT__VALUE = 0;

    /**
     * The feature id for the '<em><b>Lang</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_REASON_TEXT_ELEMENT__LANG = 1;

    /**
     * The number of structural features of the the '<em>Fault Reason Text Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_REASON_TEXT_ELEMENT_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.FaultSubCodeElementImpl <em>Fault Sub Code Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.FaultSubCodeElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getFaultSubCodeElement()
     */
    int FAULT_SUB_CODE_ELEMENT = 7;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_SUB_CODE_ELEMENT__VALUE = 0;

    /**
     * The feature id for the '<em><b>Subcode</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_SUB_CODE_ELEMENT__SUBCODE = 1;

    /**
     * The number of structural features of the the '<em>Fault Sub Code Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int FAULT_SUB_CODE_ELEMENT_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.HeaderElementImpl <em>Header Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.HeaderElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getHeaderElement()
     */
    int HEADER_ELEMENT = 8;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int HEADER_ELEMENT__ANY = 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int HEADER_ELEMENT__ANY_ATTRIBUTE = 1;

    /**
     * The number of structural features of the the '<em>Header Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int HEADER_ELEMENT_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.MessageElementImpl <em>Message</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getMessage()
     */
    int MESSAGE = 9;

    /**
     * The feature id for the '<em><b>Header Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MESSAGE__HEADER_ELEMENT = 0;

    /**
     * The feature id for the '<em><b>Body Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MESSAGE__BODY_ELEMENT = 1;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MESSAGE__ANY_ATTRIBUTE = 2;

    /**
     * The number of structural features of the the '<em>Message</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MESSAGE_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.NotUnderstoodTypeImpl <em>Not Understood Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.NotUnderstoodTypeImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getNotUnderstoodType()
     */
    int NOT_UNDERSTOOD_TYPE = 10;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int NOT_UNDERSTOOD_TYPE__QNAME = 0;

    /**
     * The number of structural features of the the '<em>Not Understood Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int NOT_UNDERSTOOD_TYPE_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.SupportedEnvElementImpl <em>Supported Env Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.SupportedEnvElementImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getSupportedEnvElement()
     */
    int SUPPORTED_ENV_ELEMENT = 11;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUPPORTED_ENV_ELEMENT__QNAME = 0;

    /**
     * The number of structural features of the the '<em>Supported Env Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUPPORTED_ENV_ELEMENT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.message.sdo.impl.UpgradeTypeImpl <em>Upgrade Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.core.message.sdo.impl.UpgradeTypeImpl
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getUpgradeType()
     */
    int UPGRADE_TYPE = 12;

    /**
     * The feature id for the '<em><b>Supported Envelope</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int UPGRADE_TYPE__SUPPORTED_ENVELOPE = 0;

    /**
     * The number of structural features of the the '<em>Upgrade Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int UPGRADE_TYPE_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '<em>Faultcode Enum</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see Object
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementPackageImpl#getFaultcodeEnum()
     */
    int FAULTCODE_ENUM = 13;


    /**
     * Returns the meta object for class '{@link BodyElement <em>Body Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Body Element</em>'.
     * @generated
     * @see BodyElement
     */
    EClass getBodyElement();

    /**
     * Returns the meta object for the attribute list '{@link BodyElement#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see BodyElement#getAny()
     * @see #getBodyElement()
     */
    EAttribute getBodyElement_Any();

    /**
     * Returns the meta object for the attribute list '{@link BodyElement#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see BodyElement#getAnyAttribute()
     * @see #getBodyElement()
     */
    EAttribute getBodyElement_AnyAttribute();

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
     * Returns the meta object for the containment reference '{@link DocumentRoot#getBodyElement <em>Body Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Body Element</em>'.
     * @generated
     * @see DocumentRoot#getBodyElement()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_BodyElement();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getEnvelope <em>Envelope</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Envelope</em>'.
     * @generated
     * @see DocumentRoot#getEnvelope()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Envelope();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getFaultElement <em>Fault Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Fault Element</em>'.
     * @generated
     * @see DocumentRoot#getFaultElement()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_FaultElement();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getHeaderElement <em>Header Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Header Element</em>'.
     * @generated
     * @see DocumentRoot#getHeaderElement()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_HeaderElement();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getNotlUnderstoodElement <em>Notl Understood Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Notl Understood Element</em>'.
     * @generated
     * @see DocumentRoot#getNotlUnderstoodElement()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_NotlUnderstoodElement();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getUpgradeElement <em>Upgrade Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Upgrade Element</em>'.
     * @generated
     * @see DocumentRoot#getUpgradeElement()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_UpgradeElement();

    /**
     * Returns the meta object for the attribute '{@link DocumentRoot#getEncodingStyle <em>Encoding Style</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Encoding Style</em>'.
     * @generated
     * @see DocumentRoot#getEncodingStyle()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_EncodingStyle();

    /**
     * Returns the meta object for the attribute '{@link DocumentRoot#isMustUnderstand <em>Must Understand</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Must Understand</em>'.
     * @generated
     * @see DocumentRoot#isMustUnderstand()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_MustUnderstand();

    /**
     * Returns the meta object for the attribute '{@link DocumentRoot#isRelay <em>Relay</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Relay</em>'.
     * @generated
     * @see DocumentRoot#isRelay()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_Relay();

    /**
     * Returns the meta object for the attribute '{@link DocumentRoot#getRole <em>Role</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Role</em>'.
     * @generated
     * @see DocumentRoot#getRole()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_Role();

    /**
     * Returns the meta object for class '{@link FaultCodeElement <em>Fault Code Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Fault Code Element</em>'.
     * @generated
     * @see FaultCodeElement
     */
    EClass getFaultCodeElement();

    /**
     * Returns the meta object for the attribute '{@link FaultCodeElement#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Value</em>'.
     * @generated
     * @see FaultCodeElement#getValue()
     * @see #getFaultCodeElement()
     */
    EAttribute getFaultCodeElement_Value();

    /**
     * Returns the meta object for the containment reference '{@link FaultCodeElement#getSubcode <em>Subcode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Subcode</em>'.
     * @generated
     * @see FaultCodeElement#getSubcode()
     * @see #getFaultCodeElement()
     */
    EReference getFaultCodeElement_Subcode();

    /**
     * Returns the meta object for class '{@link FaultDetailElement <em>Fault Detail Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Fault Detail Element</em>'.
     * @generated
     * @see FaultDetailElement
     */
    EClass getFaultDetailElement();

    /**
     * Returns the meta object for the attribute list '{@link FaultDetailElement#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see FaultDetailElement#getAny()
     * @see #getFaultDetailElement()
     */
    EAttribute getFaultDetailElement_Any();

    /**
     * Returns the meta object for the attribute list '{@link FaultDetailElement#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see FaultDetailElement#getAnyAttribute()
     * @see #getFaultDetailElement()
     */
    EAttribute getFaultDetailElement_AnyAttribute();

    /**
     * Returns the meta object for class '{@link FaultElement <em>Fault Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Fault Element</em>'.
     * @generated
     * @see FaultElement
     */
    EClass getFaultElement();

    /**
     * Returns the meta object for the containment reference '{@link FaultElement#getCode <em>Code</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Code</em>'.
     * @generated
     * @see FaultElement#getCode()
     * @see #getFaultElement()
     */
    EReference getFaultElement_Code();

    /**
     * Returns the meta object for the containment reference '{@link FaultElement#getReason <em>Reason</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Reason</em>'.
     * @generated
     * @see FaultElement#getReason()
     * @see #getFaultElement()
     */
    EReference getFaultElement_Reason();

    /**
     * Returns the meta object for the attribute '{@link FaultElement#getNode <em>Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Node</em>'.
     * @generated
     * @see FaultElement#getNode()
     * @see #getFaultElement()
     */
    EAttribute getFaultElement_Node();

    /**
     * Returns the meta object for the attribute '{@link FaultElement#getRole <em>Role</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Role</em>'.
     * @generated
     * @see FaultElement#getRole()
     * @see #getFaultElement()
     */
    EAttribute getFaultElement_Role();

    /**
     * Returns the meta object for the containment reference '{@link FaultElement#getDetail <em>Detail</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Detail</em>'.
     * @generated
     * @see FaultElement#getDetail()
     * @see #getFaultElement()
     */
    EReference getFaultElement_Detail();

    /**
     * Returns the meta object for class '{@link FaultReasonElement <em>Fault Reason Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Fault Reason Element</em>'.
     * @generated
     * @see FaultReasonElement
     */
    EClass getFaultReasonElement();

    /**
     * Returns the meta object for the containment reference list '{@link FaultReasonElement#getText <em>Text</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Text</em>'.
     * @generated
     * @see FaultReasonElement#getText()
     * @see #getFaultReasonElement()
     */
    EReference getFaultReasonElement_Text();

    /**
     * Returns the meta object for class '{@link FaultReasonTextElement <em>Fault Reason Text Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Fault Reason Text Element</em>'.
     * @generated
     * @see FaultReasonTextElement
     */
    EClass getFaultReasonTextElement();

    /**
     * Returns the meta object for the attribute '{@link FaultReasonTextElement#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Value</em>'.
     * @generated
     * @see FaultReasonTextElement#getValue()
     * @see #getFaultReasonTextElement()
     */
    EAttribute getFaultReasonTextElement_Value();

    /**
     * Returns the meta object for the attribute '{@link FaultReasonTextElement#getLang <em>Lang</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Lang</em>'.
     * @generated
     * @see FaultReasonTextElement#getLang()
     * @see #getFaultReasonTextElement()
     */
    EAttribute getFaultReasonTextElement_Lang();

    /**
     * Returns the meta object for class '{@link FaultSubCodeElement <em>Fault Sub Code Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Fault Sub Code Element</em>'.
     * @generated
     * @see FaultSubCodeElement
     */
    EClass getFaultSubCodeElement();

    /**
     * Returns the meta object for the attribute '{@link FaultSubCodeElement#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Value</em>'.
     * @generated
     * @see FaultSubCodeElement#getValue()
     * @see #getFaultSubCodeElement()
     */
    EAttribute getFaultSubCodeElement_Value();

    /**
     * Returns the meta object for the containment reference '{@link FaultSubCodeElement#getSubcode <em>Subcode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Subcode</em>'.
     * @generated
     * @see FaultSubCodeElement#getSubcode()
     * @see #getFaultSubCodeElement()
     */
    EReference getFaultSubCodeElement_Subcode();

    /**
     * Returns the meta object for class '{@link HeaderElement <em>Header Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Header Element</em>'.
     * @generated
     * @see HeaderElement
     */
    EClass getHeaderElement();

    /**
     * Returns the meta object for the attribute list '{@link HeaderElement#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see HeaderElement#getAny()
     * @see #getHeaderElement()
     */
    EAttribute getHeaderElement_Any();

    /**
     * Returns the meta object for the attribute list '{@link HeaderElement#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see HeaderElement#getAnyAttribute()
     * @see #getHeaderElement()
     */
    EAttribute getHeaderElement_AnyAttribute();

    /**
     * Returns the meta object for class '{@link MessageElement <em>Message</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Message</em>'.
     * @generated
     * @see MessageElement
     */
    EClass getMessage();

    /**
     * Returns the meta object for the containment reference '{@link MessageElement#getHeaderElement <em>Header Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Header Element</em>'.
     * @generated
     * @see MessageElement#getHeaderElement()
     * @see #getMessage()
     */
    EReference getMessage_HeaderElement();

    /**
     * Returns the meta object for the containment reference '{@link MessageElement#getBodyElement <em>Body Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Body Element</em>'.
     * @generated
     * @see MessageElement#getBodyElement()
     * @see #getMessage()
     */
    EReference getMessage_BodyElement();

    /**
     * Returns the meta object for the attribute list '{@link MessageElement#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see MessageElement#getAnyAttribute()
     * @see #getMessage()
     */
    EAttribute getMessage_AnyAttribute();

    /**
     * Returns the meta object for class '{@link NotUnderstoodType <em>Not Understood Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Not Understood Type</em>'.
     * @generated
     * @see NotUnderstoodType
     */
    EClass getNotUnderstoodType();

    /**
     * Returns the meta object for the attribute '{@link NotUnderstoodType#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @generated
     * @see NotUnderstoodType#getQname()
     * @see #getNotUnderstoodType()
     */
    EAttribute getNotUnderstoodType_Qname();

    /**
     * Returns the meta object for class '{@link SupportedEnvElement <em>Supported Env Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Supported Env Element</em>'.
     * @generated
     * @see SupportedEnvElement
     */
    EClass getSupportedEnvElement();

    /**
     * Returns the meta object for the attribute '{@link SupportedEnvElement#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @generated
     * @see SupportedEnvElement#getQname()
     * @see #getSupportedEnvElement()
     */
    EAttribute getSupportedEnvElement_Qname();

    /**
     * Returns the meta object for class '{@link UpgradeType <em>Upgrade Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Upgrade Type</em>'.
     * @generated
     * @see UpgradeType
     */
    EClass getUpgradeType();

    /**
     * Returns the meta object for the containment reference list '{@link UpgradeType#getSupportedEnvelope <em>Supported Envelope</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Supported Envelope</em>'.
     * @generated
     * @see UpgradeType#getSupportedEnvelope()
     * @see #getUpgradeType()
     */
    EReference getUpgradeType_SupportedEnvelope();

    /**
     * Returns the meta object for data type '{@link java.lang.Object <em>Faultcode Enum</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Faultcode Enum</em>'.
     * @see java.lang.Object
     * @model instanceClass="java.lang.Object"
     *        extendedMetaData="name='faultcodeEnum' baseType='http://www.eclipse.org/emf/2003/XMLType#QName' enumeration='tns:DataEncodingUnknown tns:MustUnderstand tns:Receiver tns:Sender tns:VersionMismatch'"
	 * @generated
	 */
	EDataType getFaultcodeEnum();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MessageElementFactory getMessageFactory();

} //MessagePackage
