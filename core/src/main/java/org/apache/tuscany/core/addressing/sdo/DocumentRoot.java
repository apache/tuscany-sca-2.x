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

import java.util.Map;

import commonj.sdo.Sequence;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link DocumentRoot#getMixed <em>Mixed</em>}</li>
 * <li>{@link DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 * <li>{@link DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 * <li>{@link DocumentRoot#getAction <em>Action</em>}</li>
 * <li>{@link DocumentRoot#getEndpointReference <em>Endpoint Reference</em>}</li>
 * <li>{@link DocumentRoot#getFaultTo <em>Fault To</em>}</li>
 * <li>{@link DocumentRoot#getFrom <em>From</em>}</li>
 * <li>{@link DocumentRoot#getMessageID <em>Message ID</em>}</li>
 * <li>{@link DocumentRoot#getRelatesTo <em>Relates To</em>}</li>
 * <li>{@link DocumentRoot#getReplyAfter <em>Reply After</em>}</li>
 * <li>{@link DocumentRoot#getReplyTo <em>Reply To</em>}</li>
 * <li>{@link DocumentRoot#getTo <em>To</em>}</li>
 * <li>{@link DocumentRoot#getAction1 <em>Action1</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 * @see AddressingElementPackage#getDocumentRoot()
 */
public interface DocumentRoot {
    /**
     * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Mixed</em>' attribute list.
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     * extendedMetaData="kind='elementWildcard' name=':mixed'"
     * @generated
     * @see AddressingElementPackage#getDocumentRoot_Mixed()
     */
    Sequence getMixed();

    /**
     * Returns the value of the '<em><b>XMLNS Prefix Map</b></em>' map.
     * The key is of type {@link String},
     * and the value is of type {@link String},
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>XMLNS Prefix Map</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>XMLNS Prefix Map</em>' map.
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String" transient="true"
     * extendedMetaData="kind='attribute' name='xmlns:prefix'"
     * @generated
     * @see AddressingElementPackage#getDocumentRoot_XMLNSPrefixMap()
     */
    Map getXMLNSPrefixMap();

    /**
     * Returns the value of the '<em><b>XSI Schema Location</b></em>' map.
     * The key is of type {@link String},
     * and the value is of type {@link String},
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>XSI Schema Location</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>XSI Schema Location</em>' map.
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String" transient="true"
     * extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
     * @generated
     * @see AddressingElementPackage#getDocumentRoot_XSISchemaLocation()
     */
    Map getXSISchemaLocation();

    /**
     * Returns the value of the '<em><b>Action</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Action</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Action</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='Action' namespace='##targetNamespace'"
     * @generated
     * @see #setAction(AttributedURI)
     * @see AddressingElementPackage#getDocumentRoot_Action()
     */
    AttributedURI getAction();

    /**
     * Sets the value of the '{@link DocumentRoot#getAction <em>Action</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Action</em>' containment reference.
     * @generated
     * @see #getAction()
     */
    void setAction(AttributedURI value);

    /**
     * Returns the value of the '<em><b>Endpoint Reference</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Endpoint Reference</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Endpoint Reference</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='EndpointReference' namespace='##targetNamespace'"
     * @generated
     * @see #setEndpointReference(EndpointReferenceElement)
     * @see AddressingElementPackage#getDocumentRoot_EndpointReference()
     */
    EndpointReferenceElement getEndpointReference();

    /**
     * Sets the value of the '{@link DocumentRoot#getEndpointReference <em>Endpoint Reference</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Endpoint Reference</em>' containment reference.
     * @generated
     * @see #getEndpointReference()
     */
    void setEndpointReference(EndpointReferenceElement value);

    /**
     * Returns the value of the '<em><b>Fault To</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Fault To</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Fault To</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='FaultTo' namespace='##targetNamespace'"
     * @generated
     * @see #setFaultTo(EndpointReferenceElement)
     * @see AddressingElementPackage#getDocumentRoot_FaultTo()
     */
    EndpointReferenceElement getFaultTo();

    /**
     * Sets the value of the '{@link DocumentRoot#getFaultTo <em>Fault To</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Fault To</em>' containment reference.
     * @generated
     * @see #getFaultTo()
     */
    void setFaultTo(EndpointReferenceElement value);

    /**
     * Returns the value of the '<em><b>From</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>From</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>From</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='From' namespace='##targetNamespace'"
     * @generated
     * @see #setFrom(EndpointReferenceElement)
     * @see AddressingElementPackage#getDocumentRoot_From()
     */
    EndpointReferenceElement getFrom();

    /**
     * Sets the value of the '{@link DocumentRoot#getFrom <em>From</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>From</em>' containment reference.
     * @generated
     * @see #getFrom()
     */
    void setFrom(EndpointReferenceElement value);

    /**
     * Returns the value of the '<em><b>Message ID</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Message ID</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Message ID</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='MessageID' namespace='##targetNamespace'"
     * @generated
     * @see #setMessageID(AttributedURI)
     * @see AddressingElementPackage#getDocumentRoot_MessageID()
     */
    AttributedURI getMessageID();

    /**
     * Sets the value of the '{@link DocumentRoot#getMessageID <em>Message ID</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Message ID</em>' containment reference.
     * @generated
     * @see #getMessageID()
     */
    void setMessageID(AttributedURI value);

    /**
     * Returns the value of the '<em><b>Relates To</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Relates To</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Relates To</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='RelatesTo' namespace='##targetNamespace'"
     * @generated
     * @see #setRelatesTo(Relationship)
     * @see AddressingElementPackage#getDocumentRoot_RelatesTo()
     */
    Relationship getRelatesTo();

    /**
     * Sets the value of the '{@link DocumentRoot#getRelatesTo <em>Relates To</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Relates To</em>' containment reference.
     * @generated
     * @see #getRelatesTo()
     */
    void setRelatesTo(Relationship value);

    /**
     * Returns the value of the '<em><b>Reply After</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Reply After</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Reply After</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='ReplyAfter' namespace='##targetNamespace'"
     * @generated
     * @see #setReplyAfter(ReplyAfter)
     * @see AddressingElementPackage#getDocumentRoot_ReplyAfter()
     */
    ReplyAfter getReplyAfter();

    /**
     * Sets the value of the '{@link DocumentRoot#getReplyAfter <em>Reply After</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Reply After</em>' containment reference.
     * @generated
     * @see #getReplyAfter()
     */
    void setReplyAfter(ReplyAfter value);

    /**
     * Returns the value of the '<em><b>Reply To</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Reply To</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Reply To</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='ReplyTo' namespace='##targetNamespace'"
     * @generated
     * @see #setReplyTo(EndpointReferenceElement)
     * @see AddressingElementPackage#getDocumentRoot_ReplyTo()
     */
    EndpointReferenceElement getReplyTo();

    /**
     * Sets the value of the '{@link DocumentRoot#getReplyTo <em>Reply To</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Reply To</em>' containment reference.
     * @generated
     * @see #getReplyTo()
     */
    void setReplyTo(EndpointReferenceElement value);

    /**
     * Returns the value of the '<em><b>To</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>To</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>To</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='To' namespace='##targetNamespace'"
     * @generated
     * @see #setTo(AttributedURI)
     * @see AddressingElementPackage#getDocumentRoot_To()
     */
    AttributedURI getTo();

    /**
     * Sets the value of the '{@link DocumentRoot#getTo <em>To</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>To</em>' containment reference.
     * @generated
     * @see #getTo()
     */
    void setTo(AttributedURI value);

    /**
     * Returns the value of the '<em><b>Action1</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Action1</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Action1</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
     * extendedMetaData="kind='attribute' name='Action' namespace='##targetNamespace'"
     * @generated
     * @see #setAction1(String)
     * @see AddressingElementPackage#getDocumentRoot_Action1()
     */
    String getAction1();

    /**
     * Sets the value of the '{@link org.apache.tuscany.core.addressing.sdo.DocumentRoot#getAction1 <em>Action1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Action1</em>' attribute.
     * @see #getAction1()
     * @generated
	 */
	void setAction1(String value);

} // DocumentRoot
