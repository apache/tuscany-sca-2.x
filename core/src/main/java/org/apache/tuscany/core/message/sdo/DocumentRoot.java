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
 * <li>{@link DocumentRoot#getBodyElement <em>Body Element</em>}</li>
 * <li>{@link DocumentRoot#getEnvelope <em>Envelope</em>}</li>
 * <li>{@link DocumentRoot#getFaultElement <em>Fault Element</em>}</li>
 * <li>{@link DocumentRoot#getHeaderElement <em>Header Element</em>}</li>
 * <li>{@link DocumentRoot#getNotlUnderstoodElement <em>Notl Understood Element</em>}</li>
 * <li>{@link DocumentRoot#getUpgradeElement <em>Upgrade Element</em>}</li>
 * <li>{@link DocumentRoot#getEncodingStyle <em>Encoding Style</em>}</li>
 * <li>{@link DocumentRoot#isMustUnderstand <em>Must Understand</em>}</li>
 * <li>{@link DocumentRoot#isRelay <em>Relay</em>}</li>
 * <li>{@link DocumentRoot#getRole <em>Role</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 * @see MessageElementPackage#getDocumentRoot()
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
     * @see MessageElementPackage#getDocumentRoot_Mixed()
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
     * @see MessageElementPackage#getDocumentRoot_XMLNSPrefixMap()
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
     * @see MessageElementPackage#getDocumentRoot_XSISchemaLocation()
     */
    Map getXSISchemaLocation();

    /**
     * Returns the value of the '<em><b>Body Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Body Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Body Element</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='Body' namespace='##targetNamespace'"
     * @generated
     * @see #setBodyElement(BodyElement)
     * @see MessageElementPackage#getDocumentRoot_BodyElement()
     */
    BodyElement getBodyElement();

    /**
     * Sets the value of the '{@link DocumentRoot#getBodyElement <em>Body Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Body Element</em>' containment reference.
     * @generated
     * @see #getBodyElement()
     */
    void setBodyElement(BodyElement value);

    /**
     * Returns the value of the '<em><b>Envelope</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Envelope</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Envelope</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='Envelope' namespace='##targetNamespace'"
     * @generated
     * @see #setEnvelope(MessageElement)
     * @see MessageElementPackage#getDocumentRoot_Envelope()
     */
    MessageElement getEnvelope();

    /**
     * Sets the value of the '{@link DocumentRoot#getEnvelope <em>Envelope</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Envelope</em>' containment reference.
     * @generated
     * @see #getEnvelope()
     */
    void setEnvelope(MessageElement value);

    /**
     * Returns the value of the '<em><b>Fault Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Fault Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Fault Element</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='Fault' namespace='##targetNamespace'"
     * @generated
     * @see #setFaultElement(FaultElement)
     * @see MessageElementPackage#getDocumentRoot_FaultElement()
     */
    FaultElement getFaultElement();

    /**
     * Sets the value of the '{@link DocumentRoot#getFaultElement <em>Fault Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Fault Element</em>' containment reference.
     * @generated
     * @see #getFaultElement()
     */
    void setFaultElement(FaultElement value);

    /**
     * Returns the value of the '<em><b>Header Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Header Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Header Element</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='Header' namespace='##targetNamespace'"
     * @generated
     * @see #setHeaderElement(HeaderElement)
     * @see MessageElementPackage#getDocumentRoot_HeaderElement()
     */
    HeaderElement getHeaderElement();

    /**
     * Sets the value of the '{@link DocumentRoot#getHeaderElement <em>Header Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Header Element</em>' containment reference.
     * @generated
     * @see #getHeaderElement()
     */
    void setHeaderElement(HeaderElement value);

    /**
     * Returns the value of the '<em><b>Notl Understood Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Notl Understood Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Notl Understood Element</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='NotUnderstood' namespace='##targetNamespace'"
     * @generated
     * @see #setNotlUnderstoodElement(NotUnderstoodType)
     * @see MessageElementPackage#getDocumentRoot_NotlUnderstoodElement()
     */
    NotUnderstoodType getNotlUnderstoodElement();

    /**
     * Sets the value of the '{@link DocumentRoot#getNotlUnderstoodElement <em>Notl Understood Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Notl Understood Element</em>' containment reference.
     * @generated
     * @see #getNotlUnderstoodElement()
     */
    void setNotlUnderstoodElement(NotUnderstoodType value);

    /**
     * Returns the value of the '<em><b>Upgrade Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Upgrade Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Upgrade Element</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='Upgrade' namespace='##targetNamespace'"
     * @generated
     * @see #setUpgradeElement(UpgradeType)
     * @see MessageElementPackage#getDocumentRoot_UpgradeElement()
     */
    UpgradeType getUpgradeElement();

    /**
     * Sets the value of the '{@link DocumentRoot#getUpgradeElement <em>Upgrade Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Upgrade Element</em>' containment reference.
     * @generated
     * @see #getUpgradeElement()
     */
    void setUpgradeElement(UpgradeType value);

    /**
     * Returns the value of the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Encoding Style</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Encoding Style</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
     * extendedMetaData="kind='attribute' name='encodingStyle' namespace='##targetNamespace'"
     * @generated
     * @see #setEncodingStyle(String)
     * @see MessageElementPackage#getDocumentRoot_EncodingStyle()
     */
    String getEncodingStyle();

    /**
     * Sets the value of the '{@link DocumentRoot#getEncodingStyle <em>Encoding Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Encoding Style</em>' attribute.
     * @generated
     * @see #getEncodingStyle()
     */
    void setEncodingStyle(String value);

    /**
     * Returns the value of the '<em><b>Must Understand</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Must Understand</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Must Understand</em>' attribute.
     * @model default="0" unique="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     * extendedMetaData="kind='attribute' name='mustUnderstand' namespace='##targetNamespace'"
     * @generated
     * @see #isSetMustUnderstand()
     * @see #unsetMustUnderstand()
     * @see #setMustUnderstand(boolean)
     * @see MessageElementPackage#getDocumentRoot_MustUnderstand()
     */
    boolean isMustUnderstand();

    /**
     * Sets the value of the '{@link DocumentRoot#isMustUnderstand <em>Must Understand</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Must Understand</em>' attribute.
     * @generated
     * @see #isSetMustUnderstand()
     * @see #unsetMustUnderstand()
     * @see #isMustUnderstand()
     */
    void setMustUnderstand(boolean value);

    /**
     * Unsets the value of the '{@link DocumentRoot#isMustUnderstand <em>Must Understand</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #isSetMustUnderstand()
     * @see #isMustUnderstand()
     * @see #setMustUnderstand(boolean)
     */
    void unsetMustUnderstand();

    /**
     * Returns whether the value of the '{@link DocumentRoot#isMustUnderstand <em>Must Understand</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return whether the value of the '<em>Must Understand</em>' attribute is set.
     * @generated
     * @see #unsetMustUnderstand()
     * @see #isMustUnderstand()
     * @see #setMustUnderstand(boolean)
     */
    boolean isSetMustUnderstand();

    /**
     * Returns the value of the '<em><b>Relay</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Relay</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Relay</em>' attribute.
     * @model default="0" unique="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     * extendedMetaData="kind='attribute' name='relay' namespace='##targetNamespace'"
     * @generated
     * @see #isSetRelay()
     * @see #unsetRelay()
     * @see #setRelay(boolean)
     * @see MessageElementPackage#getDocumentRoot_Relay()
     */
    boolean isRelay();

    /**
     * Sets the value of the '{@link DocumentRoot#isRelay <em>Relay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Relay</em>' attribute.
     * @generated
     * @see #isSetRelay()
     * @see #unsetRelay()
     * @see #isRelay()
     */
    void setRelay(boolean value);

    /**
     * Unsets the value of the '{@link DocumentRoot#isRelay <em>Relay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #isSetRelay()
     * @see #isRelay()
     * @see #setRelay(boolean)
     */
    void unsetRelay();

    /**
     * Returns whether the value of the '{@link DocumentRoot#isRelay <em>Relay</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return whether the value of the '<em>Relay</em>' attribute is set.
     * @generated
     * @see #unsetRelay()
     * @see #isRelay()
     * @see #setRelay(boolean)
     */
    boolean isSetRelay();

    /**
     * Returns the value of the '<em><b>Role</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Role</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Role</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
     * extendedMetaData="kind='attribute' name='role' namespace='##targetNamespace'"
     * @generated
     * @see #setRole(String)
     * @see MessageElementPackage#getDocumentRoot_Role()
     */
    String getRole();

    /**
     * Sets the value of the '{@link org.apache.tuscany.core.message.sdo.DocumentRoot#getRole <em>Role</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Role</em>' attribute.
     * @see #getRole()
	 * @generated
	 */
	void setRole(String value);

} // DocumentRoot
