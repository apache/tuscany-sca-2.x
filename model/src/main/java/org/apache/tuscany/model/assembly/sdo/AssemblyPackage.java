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
package org.apache.tuscany.model.assembly.sdo;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
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
 * @see AssemblyFactory
 */
public interface AssemblyPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNAME = "assembly";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_URI = "http://www.osoa.org/xmlns/sca/0.9";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_PREFIX = "sca";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    AssemblyPackage eINSTANCE = org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.BindingImpl <em>Binding</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.BindingImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getBinding()
     */
    int BINDING = 0;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int BINDING__URI = 0;

    /**
     * The number of structural features of the the '<em>Binding</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int BINDING_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ComponentImpl <em>Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ComponentImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getComponent()
     */
    int COMPONENT = 1;

    /**
     * The feature id for the '<em><b>Implementation</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT__IMPLEMENTATION = 0;

    /**
     * The feature id for the '<em><b>Properties</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT__PROPERTIES = 1;

    /**
     * The feature id for the '<em><b>References</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT__REFERENCES = 2;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT__ANY = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT__NAME = 4;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT__ANY_ATTRIBUTE = 5;

    /**
     * The number of structural features of the the '<em>Component</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ComponentTypeImpl <em>Component Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ComponentTypeImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getComponentType()
     */
    int COMPONENT_TYPE = 2;

    /**
     * The feature id for the '<em><b>Services</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT_TYPE__SERVICES = 0;

    /**
     * The feature id for the '<em><b>References</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT_TYPE__REFERENCES = 1;

    /**
     * The feature id for the '<em><b>Properties</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT_TYPE__PROPERTIES = 2;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT_TYPE__ANY = 3;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT_TYPE__ANY_ATTRIBUTE = 4;

    /**
     * The number of structural features of the the '<em>Component Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPONENT_TYPE_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.DocumentRootImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getDocumentRoot()
     */
    int DOCUMENT_ROOT = 3;

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
     * The feature id for the '<em><b>Binding</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__BINDING = 3;

    /**
     * The feature id for the '<em><b>Binding Sca</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__BINDING_SCA = 4;

    /**
     * The feature id for the '<em><b>Component Type</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__COMPONENT_TYPE = 5;

    /**
     * The feature id for the '<em><b>Implementation</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__IMPLEMENTATION = 6;

    /**
     * The feature id for the '<em><b>Interface</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__INTERFACE = 7;

    /**
     * The feature id for the '<em><b>Interface Java</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__INTERFACE_JAVA = 8;

    /**
     * The feature id for the '<em><b>Interface Wsdl</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__INTERFACE_WSDL = 9;

    /**
     * The feature id for the '<em><b>Module</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MODULE = 10;

    /**
     * The feature id for the '<em><b>Module Fragment</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MODULE_FRAGMENT = 11;

    /**
     * The feature id for the '<em><b>Source</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__SOURCE = 12;

    /**
     * The feature id for the '<em><b>Source Epr</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__SOURCE_EPR = 13;

    /**
     * The feature id for the '<em><b>Source Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__SOURCE_URI = 14;

    /**
     * The feature id for the '<em><b>Subsystem</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__SUBSYSTEM = 15;

    /**
     * The feature id for the '<em><b>Target</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__TARGET = 16;

    /**
     * The feature id for the '<em><b>Target Epr</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__TARGET_EPR = 17;

    /**
     * The feature id for the '<em><b>Target Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__TARGET_URI = 18;

    /**
     * The number of structural features of the the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 19;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.EntryPointImpl <em>Entry Point</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.EntryPointImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getEntryPoint()
     */
    int ENTRY_POINT = 4;

    /**
     * The feature id for the '<em><b>Interface Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__INTERFACE_GROUP = 0;

    /**
     * The feature id for the '<em><b>Interface</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__INTERFACE = 1;

    /**
     * The feature id for the '<em><b>Binding Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__BINDING_GROUP = 2;

    /**
     * The feature id for the '<em><b>Binding</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__BINDING = 3;

    /**
     * The feature id for the '<em><b>References</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__REFERENCES = 4;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__ANY = 5;

    /**
     * The feature id for the '<em><b>Multiplicity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__MULTIPLICITY = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__NAME = 7;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT__ANY_ATTRIBUTE = 8;

    /**
     * The number of structural features of the the '<em>Entry Point</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ENTRY_POINT_FEATURE_COUNT = 9;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ExternalServiceImpl <em>External Service</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ExternalServiceImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getExternalService()
     */
    int EXTERNAL_SERVICE = 5;

    /**
     * The feature id for the '<em><b>Interface Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE__INTERFACE_GROUP = 0;

    /**
     * The feature id for the '<em><b>Interface</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE__INTERFACE = 1;

    /**
     * The feature id for the '<em><b>Bindings Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE__BINDINGS_GROUP = 2;

    /**
     * The feature id for the '<em><b>Bindings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE__BINDINGS = 3;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE__ANY = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE__NAME = 5;

    /**
     * The feature id for the '<em><b>Overridable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE__OVERRIDABLE = 6;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE__ANY_ATTRIBUTE = 7;

    /**
     * The number of structural features of the the '<em>External Service</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int EXTERNAL_SERVICE_FEATURE_COUNT = 8;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ImplementationImpl <em>Implementation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ImplementationImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getImplementation()
     */
    int IMPLEMENTATION = 6;

    /**
     * The number of structural features of the the '<em>Implementation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int IMPLEMENTATION_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.InterfaceImpl <em>Interface</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.InterfaceImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getInterface()
     */
    int INTERFACE = 7;

    /**
     * The number of structural features of the the '<em>Interface</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int INTERFACE_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.JavaInterfaceImpl <em>Java Interface</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.JavaInterfaceImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getJavaInterface()
     */
    int JAVA_INTERFACE = 8;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_INTERFACE__ANY = INTERFACE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Callback Interface</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_INTERFACE__CALLBACK_INTERFACE = INTERFACE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Interface</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_INTERFACE__INTERFACE = INTERFACE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_INTERFACE__ANY_ATTRIBUTE = INTERFACE_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Java Interface</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int JAVA_INTERFACE_FEATURE_COUNT = INTERFACE_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ModuleFragmentElementImpl <em>Module Fragment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ModuleFragmentElementImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getModuleFragment()
     */
    int MODULE_FRAGMENT = 11;

    /**
     * The feature id for the '<em><b>Entry Points</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FRAGMENT__ENTRY_POINTS = 0;

    /**
     * The feature id for the '<em><b>Components</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FRAGMENT__COMPONENTS = 1;

    /**
     * The feature id for the '<em><b>External Services</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FRAGMENT__EXTERNAL_SERVICES = 2;

    /**
     * The feature id for the '<em><b>Wires</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FRAGMENT__WIRES = 3;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FRAGMENT__ANY = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FRAGMENT__NAME = 5;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FRAGMENT__ANY_ATTRIBUTE = 6;

    /**
     * The number of structural features of the the '<em>Module Fragment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FRAGMENT_FEATURE_COUNT = 7;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ModuleImpl <em>Module</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ModuleImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getModule()
     */
    int MODULE = 9;

    /**
     * The feature id for the '<em><b>Entry Points</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE__ENTRY_POINTS = MODULE_FRAGMENT__ENTRY_POINTS;

    /**
     * The feature id for the '<em><b>Components</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE__COMPONENTS = MODULE_FRAGMENT__COMPONENTS;

    /**
     * The feature id for the '<em><b>External Services</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE__EXTERNAL_SERVICES = MODULE_FRAGMENT__EXTERNAL_SERVICES;

    /**
     * The feature id for the '<em><b>Wires</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE__WIRES = MODULE_FRAGMENT__WIRES;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE__ANY = MODULE_FRAGMENT__ANY;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE__NAME = MODULE_FRAGMENT__NAME;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE__ANY_ATTRIBUTE = MODULE_FRAGMENT__ANY_ATTRIBUTE;

    /**
     * The number of structural features of the the '<em>Module</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_FEATURE_COUNT = MODULE_FRAGMENT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ModuleComponentImpl <em>Module Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ModuleComponentImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getModuleComponent()
     */
    int MODULE_COMPONENT = 10;

    /**
     * The feature id for the '<em><b>Properties</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_COMPONENT__PROPERTIES = 0;

    /**
     * The feature id for the '<em><b>References</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_COMPONENT__REFERENCES = 1;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_COMPONENT__ANY = 2;

    /**
     * The feature id for the '<em><b>Module</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_COMPONENT__MODULE = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_COMPONENT__NAME = 4;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_COMPONENT__URI = 5;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_COMPONENT__ANY_ATTRIBUTE = 6;

    /**
     * The number of structural features of the the '<em>Module Component</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_COMPONENT_FEATURE_COUNT = 7;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ModuleWireImpl <em>Module Wire</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ModuleWireImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getModuleWire()
     */
    int MODULE_WIRE = 12;

    /**
     * The feature id for the '<em><b>Source Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_WIRE__SOURCE_URI = 0;

    /**
     * The feature id for the '<em><b>Target Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_WIRE__TARGET_URI = 1;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_WIRE__ANY = 2;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_WIRE__ANY_ATTRIBUTE = 3;

    /**
     * The number of structural features of the the '<em>Module Wire</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODULE_WIRE_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl <em>Property</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getProperty()
     */
    int PROPERTY = 13;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY__ANY = 0;

    /**
     * The feature id for the '<em><b>Default</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY__DEFAULT = 1;

    /**
     * The feature id for the '<em><b>Many</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY__MANY = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY__NAME = 3;

    /**
     * The feature id for the '<em><b>Required</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY__REQUIRED = 4;

    /**
     * The feature id for the '<em><b>Property Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY__TYPE = 5;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY__ANY_ATTRIBUTE = 6;

    /**
     * The number of structural features of the the '<em>Property</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY_FEATURE_COUNT = 7;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.PropertyValuesImpl <em>Property Values</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyValuesImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getPropertyValues()
     */
    int PROPERTY_VALUES = 14;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY_VALUES__ANY = 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY_VALUES__ANY_ATTRIBUTE = 1;

    /**
     * The number of structural features of the the '<em>Property Values</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int PROPERTY_VALUES_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ReferenceImpl <em>Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ReferenceImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getReference()
     */
    int REFERENCE = 15;

    /**
     * The feature id for the '<em><b>Interface Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE__INTERFACE_GROUP = 0;

    /**
     * The feature id for the '<em><b>Interface</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE__INTERFACE = 1;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE__ANY = 2;

    /**
     * The feature id for the '<em><b>Multiplicity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE__MULTIPLICITY = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE__NAME = 4;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE__ANY_ATTRIBUTE = 5;

    /**
     * The number of structural features of the the '<em>Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ReferenceValuesImpl <em>Reference Values</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ReferenceValuesImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getReferenceValues()
     */
    int REFERENCE_VALUES = 16;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE_VALUES__ANY = 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE_VALUES__ANY_ATTRIBUTE = 1;

    /**
     * The number of structural features of the the '<em>Reference Values</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int REFERENCE_VALUES_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.SCABindingImpl <em>SCA Binding</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.SCABindingImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getSCABinding()
     */
    int SCA_BINDING = 17;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SCA_BINDING__URI = BINDING__URI;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SCA_BINDING__ANY = BINDING_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SCA_BINDING__ANY_ATTRIBUTE = BINDING_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>SCA Binding</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SCA_BINDING_FEATURE_COUNT = BINDING_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.ServiceImpl <em>Service</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.ServiceImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getService()
     */
    int SERVICE = 18;

    /**
     * The feature id for the '<em><b>Interface Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE__INTERFACE_GROUP = 0;

    /**
     * The feature id for the '<em><b>Interface</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE__INTERFACE = 1;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE__ANY = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE__NAME = 3;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE__ANY_ATTRIBUTE = 4;

    /**
     * The number of structural features of the the '<em>Service</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SERVICE_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.SubsystemImpl <em>Subsystem</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.SubsystemImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getSubsystem()
     */
    int SUBSYSTEM = 19;

    /**
     * The feature id for the '<em><b>Entry Points</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM__ENTRY_POINTS = 0;

    /**
     * The feature id for the '<em><b>Module Components</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM__MODULE_COMPONENTS = 1;

    /**
     * The feature id for the '<em><b>External Services</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM__EXTERNAL_SERVICES = 2;

    /**
     * The feature id for the '<em><b>Wires</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM__WIRES = 3;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM__ANY = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM__NAME = 5;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM__URI = 6;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM__ANY_ATTRIBUTE = 7;

    /**
     * The number of structural features of the the '<em>Subsystem</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SUBSYSTEM_FEATURE_COUNT = 8;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.SystemWireImpl <em>System Wire</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.SystemWireImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getSystemWire()
     */
    int SYSTEM_WIRE = 20;

    /**
     * The feature id for the '<em><b>Source Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SYSTEM_WIRE__SOURCE_GROUP = 0;

    /**
     * The feature id for the '<em><b>Source</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SYSTEM_WIRE__SOURCE = 1;

    /**
     * The feature id for the '<em><b>Target Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SYSTEM_WIRE__TARGET_GROUP = 2;

    /**
     * The feature id for the '<em><b>Target</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SYSTEM_WIRE__TARGET = 3;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SYSTEM_WIRE__ANY = 4;

    /**
     * The number of structural features of the the '<em>System Wire</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int SYSTEM_WIRE_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.assembly.sdo.impl.WSDLPortTypeImpl <em>WSDL Port Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.impl.WSDLPortTypeImpl
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getWSDLPortType()
     */
    int WSDL_PORT_TYPE = 21;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WSDL_PORT_TYPE__ANY = INTERFACE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Callback Interface</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WSDL_PORT_TYPE__CALLBACK_INTERFACE = INTERFACE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Interface</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WSDL_PORT_TYPE__INTERFACE = INTERFACE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WSDL_PORT_TYPE__ANY_ATTRIBUTE = INTERFACE_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>WSDL Port Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int WSDL_PORT_TYPE_FEATURE_COUNT = INTERFACE_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link org.osoa.sca.model.OverrideOptionsEnum <em>Override Options</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.osoa.sca.model.OverrideOptionsEnum
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getOverrideOptions()
     */
    int OVERRIDE_OPTIONS = 22;

    /**
     * The meta object id for the '<em>Multiplicity</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see String
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getMultiplicity()
     */
    int MULTIPLICITY = 23;

    /**
     * The meta object id for the '<em>Override Options Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.osoa.sca.model.OverrideOptionsEnum
     * @see org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl#getOverrideOptionsObject()
     */
    int OVERRIDE_OPTIONS_OBJECT = 24;


    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Binding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Binding</em>'.
     * @generated
     * @see org.osoa.sca.model.Binding
     */
    EClass getBinding();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Binding#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @generated
     * @see org.osoa.sca.model.Binding#getUri()
     * @see #getBinding()
     */
    EAttribute getBinding_Uri();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Component <em>Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Component</em>'.
     * @generated
     * @see org.osoa.sca.model.Component
     */
    EClass getComponent();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.Component#getImplementation <em>Implementation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Implementation</em>'.
     * @generated
     * @see org.osoa.sca.model.Component#getImplementation()
     * @see #getComponent()
     */
    EReference getComponent_Implementation();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.Component#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Properties</em>'.
     * @generated
     * @see org.osoa.sca.model.Component#getPropertyValues()
     * @see #getComponent()
     */
    EReference getComponent_Properties();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.Component#getReferences <em>References</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>References</em>'.
     * @generated
     * @see org.osoa.sca.model.Component#getReferenceValues()
     * @see #getComponent()
     */
    EReference getComponent_References();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Component#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.Component#getAny()
     * @see #getComponent()
     */
    EAttribute getComponent_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Component#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.Component#getName()
     * @see #getComponent()
     */
    EAttribute getComponent_Name();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Component#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.Component#getAnyAttribute()
     * @see #getComponent()
     */
    EAttribute getComponent_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.ComponentType <em>Component Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Component Type</em>'.
     * @generated
     * @see org.osoa.sca.model.ComponentType
     */
    EClass getComponentType();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.ComponentType#getServices <em>Services</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Services</em>'.
     * @generated
     * @see org.osoa.sca.model.ComponentType#getServices()
     * @see #getComponentType()
     */
    EReference getComponentType_Services();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.ComponentType#getReferences <em>References</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>References</em>'.
     * @generated
     * @see org.osoa.sca.model.ComponentType#getReferences()
     * @see #getComponentType()
     */
    EReference getComponentType_References();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.ComponentType#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Properties</em>'.
     * @generated
     * @see org.osoa.sca.model.ComponentType#getProperties()
     * @see #getComponentType()
     */
    EReference getComponentType_Properties();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ComponentType#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.ComponentType#getAny()
     * @see #getComponentType()
     */
    EAttribute getComponentType_Any();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ComponentType#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.ComponentType#getAnyAttribute()
     * @see #getComponentType()
     */
    EAttribute getComponentType_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Document Root</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot
     */
    EClass getDocumentRoot();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_Mixed();

    /**
     * Returns the meta object for the map '{@link org.osoa.sca.model.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XMLNSPrefixMap();

    /**
     * Returns the meta object for the map '{@link org.osoa.sca.model.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XSISchemaLocation();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Binding</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getBinding()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Binding();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getBindingSca <em>Binding Sca</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Binding Sca</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getBindingSca()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_BindingSca();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getComponentType <em>Component Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Component Type</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getComponentType()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_ComponentType();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getImplementation <em>Implementation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Implementation</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getImplementation()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Implementation();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getInterface()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Interface();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getInterfaceJava <em>Interface Java</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Interface Java</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getInterfaceJava()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_InterfaceJava();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getInterfaceWsdl <em>Interface Wsdl</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Interface Wsdl</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getInterfaceWsdl()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_InterfaceWsdl();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getModule <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Module</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getModule()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Module();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getModuleFragment <em>Module Fragment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Module Fragment</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getModuleFragment()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_ModuleFragment();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Source</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getSource()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Source();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getSourceEpr <em>Source Epr</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Source Epr</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getSourceEpr()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_SourceEpr();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.DocumentRoot#getSourceUri <em>Source Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Source Uri</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getSourceUri()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_SourceUri();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getSubsystem <em>Subsystem</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Subsystem</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getSubsystem()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Subsystem();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Target</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getTarget()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_Target();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.DocumentRoot#getTargetEpr <em>Target Epr</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Target Epr</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getTargetEpr()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_TargetEpr();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.DocumentRoot#getTargetUri <em>Target Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Target Uri</em>'.
     * @generated
     * @see org.osoa.sca.model.DocumentRoot#getTargetUri()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_TargetUri();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.EntryPoint <em>Entry Point</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Entry Point</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint
     */
    EClass getEntryPoint();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.EntryPoint#getInterfaceGroup <em>Interface Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Interface Group</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getInterfaceGroup()
     * @see #getEntryPoint()
     */
    EAttribute getEntryPoint_InterfaceGroup();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.EntryPoint#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getInterface()
     * @see #getEntryPoint()
     */
    EReference getEntryPoint_Interface();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.EntryPoint#getBindingGroup <em>Binding Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Binding Group</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getBindingGroup()
     * @see #getEntryPoint()
     */
    EAttribute getEntryPoint_BindingGroup();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.EntryPoint#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Binding</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getBindings()
     * @see #getEntryPoint()
     */
    EReference getEntryPoint_Binding();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.EntryPoint#getReferences <em>References</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>References</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getReferences()
     * @see #getEntryPoint()
     */
    EReference getEntryPoint_References();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.EntryPoint#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getAny()
     * @see #getEntryPoint()
     */
    EAttribute getEntryPoint_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.EntryPoint#getMultiplicity <em>Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Multiplicity</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getMultiplicity()
     * @see #getEntryPoint()
     */
    EAttribute getEntryPoint_Multiplicity();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.EntryPoint#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getName()
     * @see #getEntryPoint()
     */
    EAttribute getEntryPoint_Name();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.EntryPoint#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.EntryPoint#getAnyAttribute()
     * @see #getEntryPoint()
     */
    EAttribute getEntryPoint_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.ExternalService <em>External Service</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>External Service</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService
     */
    EClass getExternalService();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ExternalService#getInterfaceGroup <em>Interface Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Interface Group</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService#getInterfaceGroup()
     * @see #getExternalService()
     */
    EAttribute getExternalService_InterfaceGroup();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.ExternalService#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService#getInterface()
     * @see #getExternalService()
     */
    EReference getExternalService_Interface();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ExternalService#getBindingsGroup <em>Bindings Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Bindings Group</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService#getBindingsGroup()
     * @see #getExternalService()
     */
    EAttribute getExternalService_BindingsGroup();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.ExternalService#getBindings <em>Bindings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Bindings</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService#getBindings()
     * @see #getExternalService()
     */
    EReference getExternalService_Bindings();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ExternalService#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService#getAny()
     * @see #getExternalService()
     */
    EAttribute getExternalService_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.ExternalService#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService#getName()
     * @see #getExternalService()
     */
    EAttribute getExternalService_Name();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.ExternalService#getOverridable <em>Overridable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Overridable</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService#getOverridable()
     * @see #getExternalService()
     */
    EAttribute getExternalService_Overridable();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ExternalService#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.ExternalService#getAnyAttribute()
     * @see #getExternalService()
     */
    EAttribute getExternalService_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Implementation <em>Implementation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Implementation</em>'.
     * @generated
     * @see org.osoa.sca.model.Implementation
     */
    EClass getImplementation();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Interface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.Interface
     */
    EClass getInterface();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.JavaInterface <em>Java Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Java Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaInterface
     */
    EClass getJavaInterface();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.JavaInterface#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaInterface#getAny()
     * @see #getJavaInterface()
     */
    EAttribute getJavaInterface_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.JavaInterface#getCallbackInterface <em>Callback Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Callback Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaInterface#getCallbackInterface()
     * @see #getJavaInterface()
     */
    EAttribute getJavaInterface_CallbackInterface();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.JavaInterface#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaInterface#getInterface()
     * @see #getJavaInterface()
     */
    EAttribute getJavaInterface_Interface();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.JavaInterface#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.JavaInterface#getAnyAttribute()
     * @see #getJavaInterface()
     */
    EAttribute getJavaInterface_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Module <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Module</em>'.
     * @generated
     * @see org.osoa.sca.model.Module
     */
    EClass getModule();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.ModuleComponent <em>Module Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Module Component</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleComponent
     */
    EClass getModuleComponent();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.ModuleComponent#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Properties</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleComponent#getPropertyValues()
     * @see #getModuleComponent()
     */
    EReference getModuleComponent_Properties();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.ModuleComponent#getReferences <em>References</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>References</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleComponent#getReferenceValues()
     * @see #getModuleComponent()
     */
    EReference getModuleComponent_References();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ModuleComponent#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleComponent#getAny()
     * @see #getModuleComponent()
     */
    EAttribute getModuleComponent_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.ModuleComponent#getModule <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Module</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleComponent#getModule()
     * @see #getModuleComponent()
     */
    EAttribute getModuleComponent_Module();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.ModuleComponent#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleComponent#getName()
     * @see #getModuleComponent()
     */
    EAttribute getModuleComponent_Name();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.ModuleComponent#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleComponent#getUri()
     * @see #getModuleComponent()
     */
    EAttribute getModuleComponent_Uri();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ModuleComponent#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleComponent#getAnyAttribute()
     * @see #getModuleComponent()
     */
    EAttribute getModuleComponent_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.ModuleFragment <em>Module Fragment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Module Fragment</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleFragment
     */
    EClass getModuleFragment();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.ModuleFragment#getEntryPoints <em>Entry Points</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Entry Points</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleFragment#getEntryPoints()
     * @see #getModuleFragment()
     */
    EReference getModuleFragment_EntryPoints();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.ModuleFragment#getComponents <em>Components</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Components</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleFragment#getComponents()
     * @see #getModuleFragment()
     */
    EReference getModuleFragment_Components();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.ModuleFragment#getExternalServices <em>External Services</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>External Services</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleFragment#getExternalServices()
     * @see #getModuleFragment()
     */
    EReference getModuleFragment_ExternalServices();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.ModuleFragment#getWires <em>Wires</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Wires</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleFragment#getWires()
     * @see #getModuleFragment()
     */
    EReference getModuleFragment_Wires();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ModuleFragment#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleFragment#getAny()
     * @see #getModuleFragment()
     */
    EAttribute getModuleFragment_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.ModuleFragment#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleFragment#getName()
     * @see #getModuleFragment()
     */
    EAttribute getModuleFragment_Name();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ModuleFragment#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleFragment#getAnyAttribute()
     * @see #getModuleFragment()
     */
    EAttribute getModuleFragment_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.ModuleWire <em>Module Wire</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Module Wire</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleWire
     */
    EClass getModuleWire();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.ModuleWire#getSourceUri <em>Source Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Source Uri</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleWire#getSourceUri()
     * @see #getModuleWire()
     */
    EAttribute getModuleWire_SourceUri();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.ModuleWire#getTargetUri <em>Target Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Target Uri</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleWire#getTargetUri()
     * @see #getModuleWire()
     */
    EAttribute getModuleWire_TargetUri();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ModuleWire#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleWire#getAny()
     * @see #getModuleWire()
     */
    EAttribute getModuleWire_Any();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ModuleWire#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.ModuleWire#getAnyAttribute()
     * @see #getModuleWire()
     */
    EAttribute getModuleWire_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Property <em>Property</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Property</em>'.
     * @generated
     * @see org.osoa.sca.model.Property
     */
    EClass getProperty();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Property#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.Property#getAny()
     * @see #getProperty()
     */
    EAttribute getProperty_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Property#getDefault <em>Default</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Default</em>'.
     * @generated
     * @see org.osoa.sca.model.Property#getDefault()
     * @see #getProperty()
     */
    EAttribute getProperty_Default();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Property#isMany <em>Many</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Many</em>'.
     * @generated
     * @see org.osoa.sca.model.Property#isMany()
     * @see #getProperty()
     */
    EAttribute getProperty_Many();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Property#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.Property#getName()
     * @see #getProperty()
     */
    EAttribute getProperty_Name();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Property#isRequired <em>Required</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Required</em>'.
     * @generated
     * @see org.osoa.sca.model.Property#isRequired()
     * @see #getProperty()
     */
    EAttribute getProperty_Required();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Property#getPropertyType <em>Property Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Property Type</em>'.
     * @generated
     * @see org.osoa.sca.model.Property#getType_()
     * @see #getProperty()
     */
    EAttribute getProperty_PropertyType();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Property#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.Property#getAnyAttribute()
     * @see #getProperty()
     */
    EAttribute getProperty_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.PropertyValues <em>Property Values</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Property Values</em>'.
     * @generated
     * @see org.osoa.sca.model.PropertyValues
     */
    EClass getPropertyValues();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.PropertyValues#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.PropertyValues#getAny()
     * @see #getPropertyValues()
     */
    EAttribute getPropertyValues_Any();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.PropertyValues#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.PropertyValues#getAnyAttribute()
     * @see #getPropertyValues()
     */
    EAttribute getPropertyValues_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Reference <em>Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Reference</em>'.
     * @generated
     * @see org.osoa.sca.model.Reference
     */
    EClass getReference();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Reference#getInterfaceGroup <em>Interface Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Interface Group</em>'.
     * @generated
     * @see org.osoa.sca.model.Reference#getInterfaceGroup()
     * @see #getReference()
     */
    EAttribute getReference_InterfaceGroup();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.Reference#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.Reference#getInterface()
     * @see #getReference()
     */
    EReference getReference_Interface();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Reference#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.Reference#getAny()
     * @see #getReference()
     */
    EAttribute getReference_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Reference#getMultiplicity <em>Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Multiplicity</em>'.
     * @generated
     * @see org.osoa.sca.model.Reference#getMultiplicity()
     * @see #getReference()
     */
    EAttribute getReference_Multiplicity();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Reference#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.Reference#getName()
     * @see #getReference()
     */
    EAttribute getReference_Name();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Reference#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.Reference#getAnyAttribute()
     * @see #getReference()
     */
    EAttribute getReference_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.ReferenceValues <em>Reference Values</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Reference Values</em>'.
     * @generated
     * @see org.osoa.sca.model.ReferenceValues
     */
    EClass getReferenceValues();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ReferenceValues#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.ReferenceValues#getAny()
     * @see #getReferenceValues()
     */
    EAttribute getReferenceValues_Any();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.ReferenceValues#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.ReferenceValues#getAnyAttribute()
     * @see #getReferenceValues()
     */
    EAttribute getReferenceValues_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.SCABinding <em>SCA Binding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>SCA Binding</em>'.
     * @generated
     * @see org.osoa.sca.model.SCABinding
     */
    EClass getSCABinding();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.SCABinding#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.SCABinding#getAny()
     * @see #getSCABinding()
     */
    EAttribute getSCABinding_Any();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.SCABinding#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.SCABinding#getAnyAttribute()
     * @see #getSCABinding()
     */
    EAttribute getSCABinding_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Service <em>Service</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Service</em>'.
     * @generated
     * @see org.osoa.sca.model.Service
     */
    EClass getService();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Service#getInterfaceGroup <em>Interface Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Interface Group</em>'.
     * @generated
     * @see org.osoa.sca.model.Service#getInterfaceGroup()
     * @see #getService()
     */
    EAttribute getService_InterfaceGroup();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.Service#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.Service#getInterface()
     * @see #getService()
     */
    EReference getService_Interface();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Service#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.Service#getAny()
     * @see #getService()
     */
    EAttribute getService_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Service#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.Service#getName()
     * @see #getService()
     */
    EAttribute getService_Name();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Service#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.Service#getAnyAttribute()
     * @see #getService()
     */
    EAttribute getService_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.Subsystem <em>Subsystem</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Subsystem</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem
     */
    EClass getSubsystem();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.Subsystem#getEntryPoints <em>Entry Points</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Entry Points</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem#getEntryPoints()
     * @see #getSubsystem()
     */
    EReference getSubsystem_EntryPoints();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.Subsystem#getModuleComponents <em>Module Components</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Module Components</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem#getModuleComponents()
     * @see #getSubsystem()
     */
    EReference getSubsystem_ModuleComponents();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.Subsystem#getExternalServices <em>External Services</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>External Services</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem#getExternalServices()
     * @see #getSubsystem()
     */
    EReference getSubsystem_ExternalServices();

    /**
     * Returns the meta object for the containment reference list '{@link org.osoa.sca.model.Subsystem#getWires <em>Wires</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Wires</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem#getWires()
     * @see #getSubsystem()
     */
    EReference getSubsystem_Wires();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Subsystem#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem#getAny()
     * @see #getSubsystem()
     */
    EAttribute getSubsystem_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Subsystem#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem#getName()
     * @see #getSubsystem()
     */
    EAttribute getSubsystem_Name();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.Subsystem#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem#getUri()
     * @see #getSubsystem()
     */
    EAttribute getSubsystem_Uri();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.Subsystem#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.Subsystem#getAnyAttribute()
     * @see #getSubsystem()
     */
    EAttribute getSubsystem_AnyAttribute();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.SystemWire <em>System Wire</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>System Wire</em>'.
     * @generated
     * @see org.osoa.sca.model.SystemWire
     */
    EClass getSystemWire();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.SystemWire#getSourceGroup <em>Source Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Source Group</em>'.
     * @generated
     * @see org.osoa.sca.model.SystemWire#getSourceGroup()
     * @see #getSystemWire()
     */
    EAttribute getSystemWire_SourceGroup();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.SystemWire#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Source</em>'.
     * @generated
     * @see org.osoa.sca.model.SystemWire#getSource()
     * @see #getSystemWire()
     */
    EReference getSystemWire_Source();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.SystemWire#getTargetGroup <em>Target Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Target Group</em>'.
     * @generated
     * @see org.osoa.sca.model.SystemWire#getTargetGroup()
     * @see #getSystemWire()
     */
    EAttribute getSystemWire_TargetGroup();

    /**
     * Returns the meta object for the containment reference '{@link org.osoa.sca.model.SystemWire#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Target</em>'.
     * @generated
     * @see org.osoa.sca.model.SystemWire#getTarget()
     * @see #getSystemWire()
     */
    EReference getSystemWire_Target();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.SystemWire#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.SystemWire#getAny()
     * @see #getSystemWire()
     */
    EAttribute getSystemWire_Any();

    /**
     * Returns the meta object for class '{@link org.osoa.sca.model.WSDLPortType <em>WSDL Port Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>WSDL Port Type</em>'.
     * @generated
     * @see org.osoa.sca.model.WSDLPortType
     */
    EClass getWSDLPortType();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.WSDLPortType#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see org.osoa.sca.model.WSDLPortType#getAny()
     * @see #getWSDLPortType()
     */
    EAttribute getWSDLPortType_Any();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.WSDLPortType#getCallbackInterface <em>Callback Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Callback Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.WSDLPortType#getCallbackInterface()
     * @see #getWSDLPortType()
     */
    EAttribute getWSDLPortType_CallbackInterface();

    /**
     * Returns the meta object for the attribute '{@link org.osoa.sca.model.WSDLPortType#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Interface</em>'.
     * @generated
     * @see org.osoa.sca.model.WSDLPortType#getInterface()
     * @see #getWSDLPortType()
     */
    EAttribute getWSDLPortType_Interface();

    /**
     * Returns the meta object for the attribute list '{@link org.osoa.sca.model.WSDLPortType#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see org.osoa.sca.model.WSDLPortType#getAnyAttribute()
     * @see #getWSDLPortType()
     */
    EAttribute getWSDLPortType_AnyAttribute();

    /**
     * Returns the meta object for enum '{@link org.osoa.sca.model.OverrideOptionsEnum <em>Override Options</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for enum '<em>Override Options</em>'.
     * @generated
     * @see org.osoa.sca.model.OverrideOptionsEnum
     */
    EEnum getOverrideOptions();

    /**
     * Returns the meta object for data type '{@link String <em>Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for data type '<em>Multiplicity</em>'.
     * @model instanceClass="java.lang.String"
     * extendedMetaData="name='Multiplicity' baseType='http://www.eclipse.org/emf/2003/XMLType#string' enumeration='0..1 1..1 0..n 1..n'"
     * @generated
     * @see String
     */
    EDataType getMultiplicity();

    /**
     * Returns the meta object for data type '{@link org.osoa.sca.model.OverrideOptionsEnum <em>Override Options Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Override Options Object</em>'.
	 * @see org.osoa.sca.model.OverrideOptionsEnum
	 * @model instanceClass="org.apache.tuscany.model.assembly.binding.OverrideOptions"
	 *        extendedMetaData="name='OverrideOptions:Object' baseType='OverrideOptions'" 
	 * @generated
	 */
	EDataType getOverrideOptionsObject();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AssemblyFactory getAssemblyFactory();

	/**
	 * Custom code
	 */
	
	/**
	 * Merge the given package with this package
	 * @param ePackage
	 */
	void merge(EPackage ePackage);

} //AssemblyPackage
