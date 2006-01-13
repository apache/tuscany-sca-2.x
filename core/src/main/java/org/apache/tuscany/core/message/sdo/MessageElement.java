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

import commonj.sdo.Sequence;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Message</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link MessageElement#getHeaderElement <em>Header Element</em>}</li>
 * <li>{@link MessageElement#getBodyElement <em>Body Element</em>}</li>
 * <li>{@link MessageElement#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='Envelope' kind='elementOnly'"
 * @generated
 * @see MessageElementPackage#getMessage()
 */
public interface MessageElement {
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
     * @model containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='Header' namespace='##targetNamespace'"
     * @generated
     * @see #setHeaderElement(HeaderElement)
     * @see MessageElementPackage#getMessage_HeaderElement()
     */
    HeaderElement getHeaderElement();

    /**
     * Sets the value of the '{@link MessageElement#getHeaderElement <em>Header Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Header Element</em>' containment reference.
     * @generated
     * @see #getHeaderElement()
     */
    void setHeaderElement(HeaderElement value);

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
     * @model containment="true" resolveProxies="false" required="true"
     * extendedMetaData="kind='element' name='Body' namespace='##targetNamespace'"
     * @generated
     * @see #setBodyElement(BodyElement)
     * @see MessageElementPackage#getMessage_BodyElement()
     */
    BodyElement getBodyElement();

    /**
     * Sets the value of the '{@link MessageElement#getBodyElement <em>Body Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Body Element</em>' containment reference.
     * @generated
     * @see #getBodyElement()
     */
    void setBodyElement(BodyElement value);

    /**
     * Returns the value of the '<em><b>Any Attribute</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Any Attribute</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Any Attribute</em>' attribute list.
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     * extendedMetaData="kind='attributeWildcard' wildcards='##other' name=':2' processing='lax'"
     * @generated
     * @see MessageElementPackage#getMessage_AnyAttribute()
     */
    Sequence getAnyAttribute();

} // Message
