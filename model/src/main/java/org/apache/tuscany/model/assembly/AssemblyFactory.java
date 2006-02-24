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

import org.apache.tuscany.model.types.java.JavaServiceContract;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;

/**
 * The Factory for the assembly model.
 * Provides a create method for each non-abstract class of the model.
 */
public interface AssemblyFactory {

    /**
     * Returns a new SimpleComponent.
     * @return
     */
    SimpleComponent createSimpleComponent();

    /**
     * Returns a new ComponentType.
     * @return
     */
    ComponentType createComponentType();

    /**
     * Returns a new EntryPoint.
     * @return
     */
    EntryPoint createEntryPoint();

    /**
     * Returns a new ExternalService.
     * @return
     */
    ExternalService createExternalService();

    /**
     * Returns a new JavaServiceContract.
     * @return
     */
    JavaServiceContract createJavaServiceContract();

    /**
     * Returns a new Module.
     * @return
     */
    Module createModule();

    /**
     * Returns a new ModuleComponent.
     * @return
     */
    ModuleComponent createModuleComponent();

    /**
     * Returns a new ModuleFragment.
     * @return
     */
    ModuleFragment createModuleFragment();

    /**
     * Returns a new Reference.
     * @return
     */
    Reference createReference();

    /**
     * Returns a new ConfiguredReference.
     * @return
     */
    ConfiguredReference createConfiguredReference();

    /**
     * Returns a new Service.
     * @return
     */
    Service createService();

    /**
     * Returns a new ConfiguredService.
     * @return
     */
    ConfiguredService createConfiguredService();

    /**
     * Returns a new Subsystem.
     * @return
     */
    Subsystem createSubsystem();

    /**
     * Returns Property.
     * @return
     */
    Property createProperty();

    /**
     * Returns a new ConfiguredProperty.
     * @return
     */
    ConfiguredProperty createConfiguredProperty();

    /**
     * Returns a new WSDLServiceContract.
     * @return
     */
    WSDLServiceContract createWSDLServiceContract();

    /**
     * Create a new ServiceURI from the given uri string.
     * @param uri
     * @return
     */
    ServiceURI createServiceURI(String uri);

    /**
     * Creates a new ServiceURI from a module component and a service name.
     * @param moduleComponent
     * @param serviceName
     * @return
     */
    ServiceURI createServiceURI(ModuleComponent moduleComponent, String serviceName);

    /**
     * Creates a new ServiceURI from a module component and configured port.
     * @param moduleComponent
     * @param configuredPort
     * @return
     */
    ServiceURI createServiceURI(ModuleComponent moduleComponent, ConfiguredPort configuredPort);

    /**
     * Create a qname from a URI
     * @param uri
     * @return
     */
    QName createQName(String uri);

    /**
     * Create a wire
     * @return
     */
    Wire createWire();
}
