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

import commonj.sdo.Sequence;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relationship</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link Relationship#getValue <em>Value</em>}</li>
 * <li>{@link Relationship#getRelationshipType <em>Relationship Type</em>}</li>
 * <li>{@link Relationship#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='Relationship' kind='simple'"
 * @generated
 * @see AddressingElementPackage#getRelationship()
 */
public interface Relationship {
    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Value</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
     * extendedMetaData="name=':0' kind='simple'"
     * @generated
     * @see #setValue(String)
     * @see AddressingElementPackage#getRelationship_Value()
     */
    String getValue();

    /**
     * Sets the value of the '{@link Relationship#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Value</em>' attribute.
     * @generated
     * @see #getValue()
     */
    void setValue(String value);

    /**
     * Returns the value of the '<em><b>Relationship Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Relationship Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Relationship Type</em>' attribute.
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.QName"
     * extendedMetaData="kind='attribute' name='RelationshipType'"
     * @generated
     * @see #setRelationshipType(Object)
     * @see AddressingElementPackage#getRelationship_RelationshipType()
     */
    Object getRelationshipType();

    /**
     * Sets the value of the '{@link Relationship#getRelationshipType <em>Relationship Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Relationship Type</em>' attribute.
     * @generated
     * @see #getRelationshipType()
     */
    void setRelationshipType(Object value);

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
     * @see AddressingElementPackage#getRelationship_AnyAttribute()
     */
    Sequence getAnyAttribute();

} // Relationship
