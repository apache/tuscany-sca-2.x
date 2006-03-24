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
 * The Factory for the assembly model. Provides a create method for each non-abstract class of the model.
 */
public interface AssemblyFactory {

    /**
     * Returns a new SimpleComponent.
     *
     * @return a new SimpleComponent
     */
    SimpleComponent createSimpleComponent();

    /**
     * Returns a new ComponentType.
     *
     * @return a new ComponentType
     */
    ComponentType createComponentType();

    /**
     * Returns a new EntryPoint.
     *
     * @return a new EntryPoint
     */
    EntryPoint createEntryPoint();

    /**
     * Returns a new ExternalService.
     *
     * @return a new ExternalService
     */
    ExternalService createExternalService();

    /**
     * Returns a new JavaServiceContract.
     *
     * @return a new JavaServiceContract
     */
    JavaServiceContract createJavaServiceContract();

    /**
     * Returns a new Module.
     *
     * @return a new Module
     */
    Module createModule();

    /**
     * Returns a new ModuleComponent.
     *
     * @return a new ModuleComponent
     */
    ModuleComponent createModuleComponent();

    /**
     * Returns a new ModuleFragment.
     *
     * @return a new ModuleFragment
     */
    ModuleFragment createModuleFragment();

    /**
     * Returns a new Reference.
     *
     * @return a new Reference
     */
    Reference createReference();

    /**
     * Returns a new ConfiguredReference.
     *
     * @return a new ConfiguredReference
     */
    ConfiguredReference createConfiguredReference();

    /**
     * Returns a new Service.
     *
     * @return a new Service
     */
    Service createService();

    /**
     * Returns a new ConfiguredService.
     *
     * @return a new ConfiguredService
     */
    ConfiguredService createConfiguredService();

    /**
     * Returns a new Subsystem.
     *
     * @return a new Subsystem
     */
    Subsystem createSubsystem();

    /**
     * Returns a new Property.
     *
     * @return a new Property
     */
    Property createProperty();

    /**
     * Returns a new ConfiguredProperty.
     *
     * @return a new ConfiguredProperty
     */
    ConfiguredProperty createConfiguredProperty();

    /**
     * Returns a new WSDLServiceContract.
     *
     * @return a new WSDLServiceContract
     */
    WSDLServiceContract createWSDLServiceContract();

    /**
     * Create a new ServiceURI from the given uri string.
     *
     * @param uri the URI for the service
     * @return a new ServiceURI created from the supplied URI
     */
    ServiceURI createServiceURI(String uri);

    /**
     * Creates a new ServiceURI from a module component and a service name.
     *
     * @param moduleComponent the module component exposing the service
     * @param serviceName     the name of the service exposed by the module
     * @return a new ServiceURI for the exposed service
     */
    ServiceURI createServiceURI(ModuleComponent moduleComponent, String serviceName);

    /**
     * Creates a new ServiceURI from a module component and configured port.
     *
     * @param moduleComponent the module component exposing the service
     * @param aggregatePart   the aggregatePart that is providing the service
     * @param configuredPort  the port on the aggregatePart
     * @return a new serviceURI for the exposed service
     */
    ServiceURI createServiceURI(ModuleComponent moduleComponent, AggregatePart aggregatePart, ConfiguredPort configuredPort);

    /**
     * Create a qname from a URI
     *
     * @param uri
     * @return a new qname
     */
    QName createQName(String uri);

    /**
     * Create a wire
     *
     * @return an new wire
     */
    Wire createWire();

    /**
     * Helper method for creating a reference.
     *
     * @param name    the name of the reference
     * @param service the Java type of the service
     * @return a Reference
     */
    Reference createReference(String name, Class<?> service);

    /**
     * Helper method for creating a reference.
     *
     * @param name         the name of the reference
     * @param service      the Java type of the service
     * @param multiplicity the multiplicity of the reference
     * @return a Reference
     */
    Reference createReference(String name, Class<?> service, Multiplicity multiplicity);

    /**
     * Helper method for creating a configured reference.
     *
     * @param name    the name of the reference
     * @param targets the targets for the reference
     * @return a ConfiguredReference
     */
    ConfiguredReference createConfiguredReference(String name, String... targets);

    /**
     * Helper method for creating an EntryPoint wired to a single target.
     *
     * @param entryPointName  the name of the entry point
     * @param serviceContract the service contract the EntryPoint should expose
     * @param binding         the binding the EntryPoint should use
     * @param targetName      the target the EntryPoint exposes
     * @return an EntryPoint that exposes the supplied service and is wired to the target
     */
    EntryPoint createEntryPoint(String entryPointName, ServiceContract serviceContract, Binding binding, String targetName);

    /**
     * Helper method for creating an EntryPoint wired to a single target.
     *
     * @param entryPointName      the name of the entry point
     * @param configuredService   the configured service to expose
     * @param binding             the binding the EntryPoint should use
     * @param configuredReference a configured reference to the target
     * @return an EntryPoint that exposes the supplied service using the supplied bindng and which is wired using the supplied reference
     */
    EntryPoint createEntryPoint(String entryPointName, ConfiguredService configuredService, Binding binding, ConfiguredReference configuredReference);
}
