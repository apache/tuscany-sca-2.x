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
package org.apache.tuscany.model.assembly.impl;

import javax.xml.namespace.QName;

import org.apache.tuscany.model.assembly.AggregatePart;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.assembly.Subsystem;
import org.apache.tuscany.model.assembly.Wire;
import org.apache.tuscany.model.types.java.JavaServiceContract;
import org.apache.tuscany.model.types.java.impl.JavaServiceContractImpl;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.apache.tuscany.model.types.wsdl.impl.WSDLServiceContractImpl;

/**
 * Default implementation of AssemblyFactory
 */
public class AssemblyFactoryImpl implements AssemblyFactory {

    /**
     * Constructor
     */
    public AssemblyFactoryImpl() {
        super();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createSimpleComponent()
     */
    public SimpleComponent createSimpleComponent() {
        return new SimpleComponentImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createComponentType()
     */
    public ComponentType createComponentType() {
        return new ComponentTypeImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createEntryPoint()
     */
    public EntryPoint createEntryPoint() {
        return new EntryPointImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createExternalService()
     */
    public ExternalService createExternalService() {
        return new ExternalServiceImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createJavaServiceContract()
     */
    public JavaServiceContract createJavaServiceContract() {
        return new JavaServiceContractImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createModule()
     */
    public Module createModule() {
        return new ModuleImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createModuleFragment()
     */
    public ModuleFragment createModuleFragment() {
        return new ModuleFragmentImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createModuleComponent()
     */
    public ModuleComponent createModuleComponent() {
        return new ModuleComponentImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createProperty()
     */
    public Property createProperty() {
        return new PropertyImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createConfiguredProperty()
     */
    public ConfiguredProperty createConfiguredProperty() {
        return new ConfiguredPropertyImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createReference()
     */
    public Reference createReference() {
        return new ReferenceImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createConfiguredReference()
     */
    public ConfiguredReference createConfiguredReference() {
        return new ConfiguredReferenceImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createService()
     */
    public Service createService() {
        return new ServiceImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createConfiguredService()
     */
    public ConfiguredService createConfiguredService() {
        return new ConfiguredServiceImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createSubsystem()
     */
    public Subsystem createSubsystem() {
        return new SubsystemImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createWSDLServiceContract()
     */
    public WSDLServiceContract createWSDLServiceContract() {
        return new WSDLServiceContractImpl();
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createServiceURI(java.lang.String)
     */
    public ServiceURI createServiceURI(String uri) {
        return new ServiceURIImpl(uri);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createServiceURI(org.apache.tuscany.model.assembly.ModuleComponent, java.lang.String)
     */
    public ServiceURI createServiceURI(ModuleComponent moduleComponent, String serviceName) {
        return new ServiceURIImpl(moduleComponent, serviceName);
    }

    /*
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createServiceURI(org.apache.tuscany.model.assembly.ModuleComponent, org.apache.tuscany.model.assembly.AggregatePart, org.apache.tuscany.model.assembly.ConfiguredPort)
     */
    public ServiceURI createServiceURI(ModuleComponent moduleComponent, AggregatePart aggregatePart, ConfiguredPort configuredPort) {
        return new ServiceURIImpl(moduleComponent, aggregatePart, configuredPort);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createQName(java.lang.String)
     */
    public QName createQName(String uri) {
        int h = uri.indexOf('#');
        return new QName(uri.substring(0, h), uri.substring(h + 1));
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyFactory#createWire()
     */
    public Wire createWire() {
        return new WireImpl();
    }

    public Reference createReference(String name, Class<?> service) {
        JavaServiceContract refContract = createJavaServiceContract();
        refContract.setInterface(service);
        Reference reference = createReference();
        reference.setName(name);
        reference.setServiceContract(refContract);
        return reference;
    }

    public ConfiguredReference createConfiguredReference(String name, String target) {
        ConfiguredReference ref = createConfiguredReference();
        ref.setName(name);
        ref.setTarget(target);
        return ref;
    }
}
