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
package org.apache.tuscany.container.java.assembly.sdo;

import java.util.Map;

import commonj.sdo.Sequence;
import org.osoa.sca.model.JavaImplementation;

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
 * <li>{@link DocumentRoot#getImplementationJava <em>Implementation Java</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 * @see JavaAssemblyPackage#getDocumentRoot()
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
     * @see JavaAssemblyPackage#getDocumentRoot_Mixed()
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
     * @see JavaAssemblyPackage#getDocumentRoot_XMLNSPrefixMap()
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
     * @see JavaAssemblyPackage#getDocumentRoot_XSISchemaLocation()
     */
    Map getXSISchemaLocation();

    /**
     * Returns the value of the '<em><b>Implementation Java</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Implementation Java</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Implementation Java</em>' containment reference.
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     * extendedMetaData="kind='element' name='implementation.java' namespace='##targetNamespace' affiliation='http://www.osoa.org/xmlns/sca/0.9#implementation'"
     * @generated
     * @see #setImplementationJava(JavaImplementation)
     * @see JavaAssemblyPackage#getDocumentRoot_ImplementationJava()
     */
    JavaImplementation getImplementationJava();

    /**
     * Sets the value of the '{@link DocumentRoot#getImplementationJava <em>Implementation Java</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Implementation Java</em>' containment reference.
     * @generated
     * @see #getImplementationJava()
     */
    void setImplementationJava(JavaImplementation value);

} // DocumentRoot
