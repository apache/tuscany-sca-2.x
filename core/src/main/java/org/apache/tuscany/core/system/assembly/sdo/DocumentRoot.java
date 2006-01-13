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
package org.apache.tuscany.core.system.assembly.sdo;

import java.util.Map;

import commonj.sdo.Sequence;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getBindingSystem <em>Binding System</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getImplementationSystem <em>Implementation System</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage#getDocumentRoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
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
     * @return the value of the '<em>Mixed</em>' attribute list.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage#getDocumentRoot_Mixed()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='elementWildcard' name=':mixed'"
     * @generated
     */
    Sequence getMixed();

    /**
     * Returns the value of the '<em><b>XMLNS Prefix Map</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>XMLNS Prefix Map</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>XMLNS Prefix Map</em>' map.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage#getDocumentRoot_XMLNSPrefixMap()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String" transient="true"
     *        extendedMetaData="kind='attribute' name='xmlns:prefix'"
     * @generated
     */
    Map getXMLNSPrefixMap();

    /**
     * Returns the value of the '<em><b>XSI Schema Location</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>XSI Schema Location</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>XSI Schema Location</em>' map.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage#getDocumentRoot_XSISchemaLocation()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String" transient="true"
     *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
     * @generated
     */
    Map getXSISchemaLocation();

    /**
     * Returns the value of the '<em><b>Binding System</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Binding System</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Binding System</em>' containment reference.
     * @see #setBindingSystem(SystemBinding)
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage#getDocumentRoot_BindingSystem()
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='binding.system' namespace='##targetNamespace' affiliation='http://www.osoa.org/xmlns/sca/0.9#binding'"
     * @generated
     */
    SystemBinding getBindingSystem();

    /**
     * Sets the value of the '{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getBindingSystem <em>Binding System</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Binding System</em>' containment reference.
     * @see #getBindingSystem()
     * @generated
     */
    void setBindingSystem(SystemBinding value);

    /**
     * Returns the value of the '<em><b>Implementation System</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Implementation System</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Implementation System</em>' containment reference.
     * @see #setImplementationSystem(SystemImplementation)
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage#getDocumentRoot_ImplementationSystem()
     * @model containment="true" resolveProxies="false" upper="-2" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='implementation.system' namespace='##targetNamespace' affiliation='http://www.osoa.org/xmlns/sca/0.9#implementation'"
     * @generated
     */
    SystemImplementation getImplementationSystem();

    /**
     * Sets the value of the '{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getImplementationSystem <em>Implementation System</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Implementation System</em>' containment reference.
     * @see #getImplementationSystem()
     * @generated
     */
    void setImplementationSystem(SystemImplementation value);

} // DocumentRoot
