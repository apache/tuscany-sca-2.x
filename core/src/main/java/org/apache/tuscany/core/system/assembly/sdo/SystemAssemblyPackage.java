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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

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
 * @see org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyFactory
 * @model kind="package"
 * @generated
 */
public interface SystemAssemblyPackage extends EPackage{
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "assembly";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://org.apache.tuscany/xmlns/system/0.9";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "ext";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    SystemAssemblyPackage eINSTANCE = org.apache.tuscany.core.system.assembly.sdo.impl.SystemAssemblyPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.system.assembly.sdo.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.core.system.assembly.sdo.impl.DocumentRootImpl
     * @see org.apache.tuscany.core.system.assembly.sdo.impl.SystemAssemblyPackageImpl#getDocumentRoot()
     * @generated
     */
    int DOCUMENT_ROOT = 0;

    /**
     * The feature id for the '<em><b>Mixed</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MIXED = 0;

    /**
     * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

    /**
     * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

    /**
     * The feature id for the '<em><b>Binding System</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__BINDING_SYSTEM = 3;

    /**
     * The feature id for the '<em><b>Implementation System</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__IMPLEMENTATION_SYSTEM = 4;

    /**
     * The number of structural features of the the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.system.assembly.sdo.impl.SystemBindingImpl <em>System Binding</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.core.system.assembly.sdo.impl.SystemBindingImpl
     * @see org.apache.tuscany.core.system.assembly.sdo.impl.SystemAssemblyPackageImpl#getSystemBinding()
     * @generated
     */
    int SYSTEM_BINDING = 1;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_BINDING__URI = AssemblyPackage.BINDING__URI;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_BINDING__ANY = AssemblyPackage.BINDING_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_BINDING__ANY_ATTRIBUTE = AssemblyPackage.BINDING_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>System Binding</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_BINDING_FEATURE_COUNT = AssemblyPackage.BINDING_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.core.system.assembly.sdo.impl.SystemImplementationImpl <em>System Implementation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.core.system.assembly.sdo.impl.SystemImplementationImpl
     * @see org.apache.tuscany.core.system.assembly.sdo.impl.SystemAssemblyPackageImpl#getSystemImplementation()
     * @generated
     */
    int SYSTEM_IMPLEMENTATION = 2;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_IMPLEMENTATION__ANY = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_IMPLEMENTATION__CLASS = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_IMPLEMENTATION__ANY_ATTRIBUTE = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>System Implementation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_IMPLEMENTATION_FEATURE_COUNT = AssemblyPackage.IMPLEMENTATION_FEATURE_COUNT + 3;


    /**
     * Returns the meta object for class '{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Document Root</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.DocumentRoot
     * @generated
     */
    EClass getDocumentRoot();

    /**
     * Returns the meta object for the attribute list '{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_Mixed();

    /**
     * Returns the meta object for the map '{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XMLNSPrefixMap();

    /**
     * Returns the meta object for the map '{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XSISchemaLocation();

    /**
     * Returns the meta object for the containment reference '{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getBindingSystem <em>Binding System</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Binding System</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getBindingSystem()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_BindingSystem();

    /**
     * Returns the meta object for the containment reference '{@link org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getImplementationSystem <em>Implementation System</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Implementation System</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.DocumentRoot#getImplementationSystem()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_ImplementationSystem();

    /**
     * Returns the meta object for class '{@link org.apache.tuscany.core.system.assembly.sdo.SystemBinding <em>System Binding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>System Binding</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemBinding
     * @generated
     */
    EClass getSystemBinding();

    /**
     * Returns the meta object for the attribute list '{@link org.apache.tuscany.core.system.assembly.sdo.SystemBinding#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemBinding#getAny()
     * @see #getSystemBinding()
     * @generated
     */
    EAttribute getSystemBinding_Any();

    /**
     * Returns the meta object for the attribute list '{@link org.apache.tuscany.core.system.assembly.sdo.SystemBinding#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemBinding#getAnyAttribute()
     * @see #getSystemBinding()
     * @generated
     */
    EAttribute getSystemBinding_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.apache.tuscany.core.system.assembly.sdo.SystemImplementation <em>System Implementation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>System Implementation</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemImplementation
     * @generated
     */
    EClass getSystemImplementation();

    /**
     * Returns the meta object for the attribute list '{@link org.apache.tuscany.core.system.assembly.sdo.SystemImplementation#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemImplementation#getAny()
     * @see #getSystemImplementation()
     * @generated
     */
    EAttribute getSystemImplementation_Any();

    /**
     * Returns the meta object for the attribute '{@link org.apache.tuscany.core.system.assembly.sdo.SystemImplementation#getClass_ <em>Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Class</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemImplementation#getClass_()
     * @see #getSystemImplementation()
     * @generated
     */
    EAttribute getSystemImplementation_Class();

    /**
     * Returns the meta object for the attribute list '{@link org.apache.tuscany.core.system.assembly.sdo.SystemImplementation#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @see org.apache.tuscany.core.system.assembly.sdo.SystemImplementation#getAnyAttribute()
     * @see #getSystemImplementation()
     * @generated
     */
    EAttribute getSystemImplementation_AnyAttribute();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    SystemAssemblyFactory getSystemAssemblyFactory();

} //ExtensionsAssemblyPackage
