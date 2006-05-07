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

import org.apache.tuscany.model.assembly.Part;
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
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.AtomicComponent;
import org.apache.tuscany.model.assembly.Subsystem;
import org.apache.tuscany.model.assembly.Wire;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.ImportWSDL;
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

    public AtomicComponent createSimpleComponent() {
        return new AtomicComponentImpl();
    }

    public ComponentType createComponentType() {
        return new ComponentTypeImpl();
    }

    public EntryPoint createEntryPoint() {
        return new EntryPointImpl();
    }

    public ExternalService createExternalService() {
        return new ExternalServiceImpl();
    }

    public JavaServiceContract createJavaServiceContract() {
        return new JavaServiceContractImpl();
    }

    public Module createModule() {
        return new ModuleImpl();
    }

    public ModuleFragment createModuleFragment() {
        return new ModuleFragmentImpl();
    }

    public ModuleComponent createModuleComponent() {
        return new ModuleComponentImpl();
    }

    public Property createProperty() {
        return new PropertyImpl();
    }

    public ConfiguredProperty createConfiguredProperty() {
        return new ConfiguredPropertyImpl();
    }

    public Reference createReference() {
        return new ReferenceImpl();
    }

    public ConfiguredReference createConfiguredReference() {
        return new ConfiguredReferenceImpl();
    }

    public Service createService() {
        return new ServiceImpl();
    }

    public ConfiguredService createConfiguredService() {
        return new ConfiguredServiceImpl();
    }

    public Subsystem createSubsystem() {
        return new SubsystemImpl();
    }

    public WSDLServiceContract createWSDLServiceContract() {
        return new WSDLServiceContractImpl();
    }

    public ServiceURI createServiceURI(String uri) {
        return new ServiceURIImpl(uri);
    }

    public ServiceURI createServiceURI(ModuleComponent moduleComponent, String serviceName) {
        return new ServiceURIImpl(moduleComponent, serviceName);
    }

    public ServiceURI createServiceURI(ModuleComponent moduleComponent, Part part, ConfiguredPort configuredPort) {
        return new ServiceURIImpl(moduleComponent, part, configuredPort);
    }

    public QName createQName(String uri) {
        int h = uri.indexOf('#');
        return new QName(uri.substring(0, h), uri.substring(h + 1));
    }

    public Wire createWire() {
        return new WireImpl();
    }

    public Reference createReference(String name, Class<?> service) {
        return createReference(name,service,Multiplicity.ZERO_ONE);
    }
    
    public Reference createReference(String name, Class<?> service, Multiplicity multiplicity){
        JavaServiceContract refContract = createJavaServiceContract();
        refContract.setInterface(service);
        Reference reference = createReference();
        reference.setName(name);
        reference.setServiceContract(refContract);
        reference.setMultiplicity(multiplicity);
        return reference;
    }

     public ConfiguredReference createConfiguredReference(String name, String... targets) {
        ConfiguredReference ref = createConfiguredReference();
        ref.setName(name);
        for (String target : targets) {
            ref.getTargets().add(target);
        }
        return ref;
    }

    public EntryPoint createEntryPoint(String entryPointName, ServiceContract serviceContract, Binding binding, String targetName) {
        // create and configure the exposed service
        Service service = createService();
        service.setName(entryPointName);
        service.setServiceContract(serviceContract);
        ConfiguredService configuredService = createConfiguredService();
        configuredService.setPort(service);

        // create and configure a reference to target
        Reference reference = createReference();
        reference.setMultiplicity(Multiplicity.ONE_ONE);
        reference.setServiceContract(serviceContract);
        ConfiguredReference configuredReference = createConfiguredReference(null, targetName);
        configuredReference.setPort(reference);

        return createEntryPoint(entryPointName, configuredService, binding, configuredReference);
    }

    public EntryPoint createEntryPoint(String entryPointName, ConfiguredService configuredService, Binding binding, ConfiguredReference configuredReference) {
        EntryPoint entryPoint = createEntryPoint();
        entryPoint.setName(entryPointName);
        entryPoint.setConfiguredService(configuredService);
        entryPoint.setConfiguredReference(configuredReference);
        entryPoint.getBindings().add((Binding)binding);
        return entryPoint;
    }

    public Property createProperty(String name, Class<?> type) {
        Property property = createProperty();
        property.setName(name);
        property.setType(type);
        return property;
    }

    public ImportWSDL createImportWSDL() {
        return new ImportWSDLImpl();
    }

    public ImportWSDL createImportWSDL(String location, String namespace) {
        ImportWSDL importWSDL = createImportWSDL();
        importWSDL.setLocation(location);
        importWSDL.setNamespace(namespace);
        return importWSDL;
    }
}
