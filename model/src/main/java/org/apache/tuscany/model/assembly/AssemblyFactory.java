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
package org.apache.tuscany.model.assembly;

import javax.xml.namespace.QName;

import org.apache.tuscany.model.types.java.JavaInterface;
import org.apache.tuscany.model.types.wsdl.WSDLPortType;

/**
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 */
public interface AssemblyFactory {

    /**
     * Returns a new object of class '<em>Service Component</em>'.
     */
    SimpleComponent createSimpleComponent();

    /**
     * Returns a new object of class '<em>Component Type</em>'.
     */
    ComponentType createComponentType();

    /**
     * Returns a new object of class '<em>Entry Point</em>'.
     */
    EntryPoint createEntryPoint();

    /**
     * Returns a new object of class '<em>External Service</em>'.
     */
    ExternalService createExternalService();

    /**
     * Returns a new object of class '<em>Java Interface</em>'.
     */
    JavaInterface createJavaInterface();

    /**
     * Returns a new object of class '<em>Module</em>'.
     */
    Module createModule();

    /**
     * Returns a new object of class '<em>Module Component</em>'.
     */
    ModuleComponent createModuleComponent();

    /**
     * Returns a new object of class '<em>Module Fragment</em>'.
     */
    ModuleFragment createModuleFragment();

    /**
     * Returns a new object of class '<em>Reference</em>'.
     */
    Reference createReference();

    /**
     * Returns a new object of class '<em>ReferenceValue</em>'.
     */
    ConfiguredReference createConfiguredReference();

    /**
     * Returns a new object of class '<em>Service</em>'.
     */
    Service createService();

    /**
     * Returns a new object of class '<em>ReferenceValue</em>'.
     */
    ConfiguredService createConfiguredService();

    /**
     * Returns a new object of class '<em>Subsystem</em>'.
     */
    Subsystem createSubsystem();

    /**
     * Returns a new object of class '<em>Property</em>'.
     */
    Property createProperty();

    /**
     * Returns a new object of class '<em>Property Value</em>'.
     */
    ConfiguredProperty createConfiguredProperty();

    /**
     * Returns a new object of class '<em>WSDL PortType</em>'.
     */
    WSDLPortType createWSDLPortType();

    /**
     * Create a service URI from a string
     */
    ServiceURI createServiceURI(String uri);

    /**
     * Creates a service URI from a module component and a service name.
     *
     * @param moduleComponent
     * @param serviceName
     * @return
     */
    ServiceURI createServiceURI(ModuleComponent moduleComponent, String serviceName);

    /**
     * Creates a service URI from a module component and a port value.
     *
     * @param moduleComponent
     * @param portEndpoint
     * @return
     */
    ServiceURI createServiceURI(ModuleComponent moduleComponent, ConfiguredPort portEndpoint);

    /**
     * Create a qname from a URI
     */
    QName createQName(String uri);

} //AssemblyFactory
