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

import org.eclipse.emf.ecore.EFactory;
import org.osoa.sca.model.Binding;
import org.osoa.sca.model.Component;
import org.osoa.sca.model.ComponentType;
import org.osoa.sca.model.DocumentRoot;
import org.osoa.sca.model.EntryPoint;
import org.osoa.sca.model.ExternalService;
import org.osoa.sca.model.Implementation;
import org.osoa.sca.model.Interface;
import org.osoa.sca.model.JavaInterface;
import org.osoa.sca.model.Module;
import org.osoa.sca.model.ModuleComponent;
import org.osoa.sca.model.ModuleFragment;
import org.osoa.sca.model.ModuleWire;
import org.osoa.sca.model.Property;
import org.osoa.sca.model.PropertyValues;
import org.osoa.sca.model.Reference;
import org.osoa.sca.model.ReferenceValues;
import org.osoa.sca.model.SCABinding;
import org.osoa.sca.model.Service;
import org.osoa.sca.model.Subsystem;
import org.osoa.sca.model.SystemWire;
import org.osoa.sca.model.WSDLPortType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 *
 * @generated
 * @see AssemblyPackage
 */
public interface AssemblyFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    AssemblyFactory eINSTANCE = new org.apache.tuscany.model.assembly.sdo.impl.AssemblyFactoryImpl();

    /**
     * Returns a new object of class '<em>Binding</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Binding</em>'.
     * @generated
     */
    Binding createBinding();

    /**
     * Returns a new object of class '<em>Component</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Component</em>'.
     * @generated
     */
    Component createComponent();

    /**
     * Returns a new object of class '<em>Component Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Component Type</em>'.
     * @generated
     */
    ComponentType createComponentType();

    /**
     * Returns a new object of class '<em>Document Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Document Root</em>'.
     * @generated
     */
    DocumentRoot createDocumentRoot();

    /**
     * Returns a new object of class '<em>Entry Point</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Entry Point</em>'.
     * @generated
     */
    EntryPoint createEntryPoint();

    /**
     * Returns a new object of class '<em>External Service</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>External Service</em>'.
     * @generated
     */
    ExternalService createExternalService();

    /**
     * Returns a new object of class '<em>Implementation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Implementation</em>'.
     * @generated
     */
    Implementation createImplementation();

    /**
     * Returns a new object of class '<em>Interface</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Interface</em>'.
     * @generated
     */
    Interface createInterface();

    /**
     * Returns a new object of class '<em>Java Interface</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Java Interface</em>'.
     * @generated
     */
    JavaInterface createJavaInterface();

    /**
     * Returns a new object of class '<em>Module</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Module</em>'.
     * @generated
     */
    Module createModule();

    /**
     * Returns a new object of class '<em>Module Component</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Module Component</em>'.
     * @generated
     */
    ModuleComponent createModuleComponent();

    /**
     * Returns a new object of class '<em>Module Fragment</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Module Fragment</em>'.
     * @generated
     */
    ModuleFragment createModuleFragment();

    /**
     * Returns a new object of class '<em>Module Wire</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Module Wire</em>'.
     * @generated
     */
    ModuleWire createModuleWire();

    /**
     * Returns a new object of class '<em>Property</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Property</em>'.
     * @generated
     */
    Property createProperty();

    /**
     * Returns a new object of class '<em>Property Values</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Property Values</em>'.
     * @generated
     */
    PropertyValues createPropertyValues();

    /**
     * Returns a new object of class '<em>Reference</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Reference</em>'.
     * @generated
     */
    Reference createReference();

    /**
     * Returns a new object of class '<em>Reference Values</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Reference Values</em>'.
     * @generated
     */
    ReferenceValues createReferenceValues();

    /**
     * Returns a new object of class '<em>SCA Binding</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>SCA Binding</em>'.
     * @generated
     */
    SCABinding createSCABinding();

    /**
     * Returns a new object of class '<em>Service</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Service</em>'.
     * @generated
     */
    Service createService();

    /**
     * Returns a new object of class '<em>Subsystem</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Subsystem</em>'.
     * @generated
     */
    Subsystem createSubsystem();

    /**
     * Returns a new object of class '<em>System Wire</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>System Wire</em>'.
     * @generated
     */
    SystemWire createSystemWire();

    /**
     * Returns a new object of class '<em>WSDL Port Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>WSDL Port Type</em>'.
     * @generated
     */
    WSDLPortType createWSDLPortType();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
	 */
	AssemblyPackage getAssemblyPackage();

} //AssemblyFactory
