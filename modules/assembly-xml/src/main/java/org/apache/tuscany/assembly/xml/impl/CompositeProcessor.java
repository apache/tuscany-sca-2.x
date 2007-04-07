/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.assembly.xml.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.Callback;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.assembly.Wire;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.util.CompositeUtil;
import org.apache.tuscany.idl.Interface;
import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.ContributionResolveException;
import org.apache.tuscany.services.spi.contribution.ContributionWireException;
import org.apache.tuscany.services.spi.contribution.ContributionWriteException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

/**
 * A composite processor.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeProcessor extends BaseArtifactProcessor implements StAXArtifactProcessor<Composite> {
    private AssemblyFactory factory;
    private StAXArtifactProcessor<Object> extensionProcessor;

    /**
     * Construct a new composite processor
     * @param assemblyFactory
     * @param policyFactory
     * @param extensionProcessor
     */
    public CompositeProcessor(AssemblyFactory factory, PolicyFactory policyFactory, StAXArtifactProcessor<Object> extensionProcessor) {
        super(factory, policyFactory);
        this.factory = factory;
        this.extensionProcessor = extensionProcessor;
    }

    /**
     * Construct a new composite processor.
     * @param extensionProcessor
     */
    public CompositeProcessor(StAXArtifactProcessor<Object> extensionProcessor) {
        this(new DefaultAssemblyFactory(), new DefaultPolicyFactory(), extensionProcessor);
    }

    public Composite read(XMLStreamReader reader) throws ContributionReadException {
        Composite composite = null;
        Composite include = null;
        Component component = null;
        Property property = null;
        ComponentService componentService = null;
        ComponentReference componentReference = null;
        ComponentProperty componentProperty = null;
        CompositeService compositeService = null;
        CompositeReference compositeReference = null;
        Contract contract = null;
        Wire wire = null;
        Callback callback = null;
        QName name = null;
        
        try {
    
            // Read the composite document
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT:
                        name = reader.getName();
    
                        if (COMPOSITE_QNAME.equals(name)) {
    
                            // Read a <composite>
                            composite = factory.createComposite();
                            composite.setName(getQName(reader, NAME));
                            composite.setAutowire(getBoolean(reader, AUTOWIRE));
                            composite.setLocal(getBoolean(reader, LOCAL));
                            composite.setConstrainingType(getConstrainingType(reader));
                            readPolicies(composite, reader);
    
                        } else if (INCLUDE_QNAME.equals(name)) {
    
                            // Read an <include>
                            include = factory.createComposite();
                            include.setUnresolved(true);
                            composite.getIncludes().add(include);
    
                        } else if (SERVICE_QNAME.equals(name)) {
                            if (component != null) {
    
                                // Read a <component><service>
                                componentService = factory.createComponentService();
                                contract = componentService;
                                componentService.setName(getString(reader, NAME));
                                component.getServices().add(componentService);
                                readPolicies(contract, reader);
                            } else {
    
                                // Read a <composite><service>
                                compositeService = factory.createCompositeService();
                                contract = compositeService;
                                compositeService.setName(getString(reader, NAME));
    
                                ComponentService promoted = factory.createComponentService();
                                promoted.setUnresolved(true);
                                promoted.setName(getString(reader, PROMOTE));
                                compositeService.setPromotedService(promoted);
    
                                composite.getServices().add(compositeService);
                                readPolicies(contract, reader);
                            }
    
                        } else if (REFERENCE_QNAME.equals(name)) {
                            if (component != null) {
    
                                // Read a <component><reference>
                                componentReference = factory.createComponentReference();
                                contract = componentReference;
                                componentReference.setName(getString(reader, NAME));
                                readMultiplicity(componentReference, reader);
    
                                // TODO support multivalued attribute
                                ComponentService target = factory.createComponentService();
                                target.setUnresolved(true);
                                target.setName(getString(reader, TARGET));
                                componentReference.getTargets().add(target);
    
                                component.getReferences().add(componentReference);
                                readPolicies(contract, reader);
                            } else {
    
                                // Read a <composite><reference>
                                compositeReference = factory.createCompositeReference();
                                contract = compositeReference;
                                compositeReference.setName(getString(reader, NAME));
                                readMultiplicity(compositeReference, reader);
    
                                // TODO support multivalued attribute
                                ComponentReference promoted = factory.createComponentReference();
                                promoted.setUnresolved(true);
                                promoted.setName(getString(reader, PROMOTE));
                                compositeReference.getPromotedReferences().add(promoted);
    
                                composite.getReferences().add(compositeReference);
                                readPolicies(contract, reader);
                            }
    
                        } else if (PROPERTY_QNAME.equals(name)) {
                            if (component != null) {
    
                                // Read a <component><property>
                                componentProperty = factory.createComponentProperty();
                                property = componentProperty;
                                readPolicies(property, reader);
                                readProperty(componentProperty, reader);
                                component.getProperties().add(componentProperty);
                            } else {
    
                                // Read a <composite><property>
                                property = factory.createProperty();
                                readPolicies(property, reader);
                                readProperty(property, reader);
                                composite.getProperties().add(property);
                            }
    
                        } else if (COMPONENT_QNAME.equals(name)) {
    
                            // Read a <component>
                            component = factory.createComponent();
                            component.setName(getString(reader, NAME));
                            component.setConstrainingType(getConstrainingType(reader));
                            composite.getComponents().add(component);
                            readPolicies(component, reader);
    
                        } else if (WIRE_QNAME.equals(name)) {
    
                            // Read a <wire>
                            wire = factory.createWire();
                            ComponentReference source = factory.createComponentReference();
                            source.setUnresolved(true);
                            source.setName(getString(reader, SOURCE));
                            wire.setSource(source);
    
                            ComponentService target = factory.createComponentService();
                            target.setUnresolved(true);
                            target.setName(getString(reader, TARGET));
                            wire.setTarget(target);
    
                            composite.getWires().add(wire);
                            readPolicies(wire, reader);
    
                        } else if (CALLBACK_QNAME.equals(name)) {
    
                            // Read a <callback>
                            callback = factory.createCallback();
                            contract.setCallback(callback);
                            readPolicies(callback, reader);
    
                        } else if (OPERATION_QNAME.equals(name)) {
    
                            // Read an <operation>
                            Operation operation = factory.createOperation();
                            operation.setName(getString(reader, NAME));
                            operation.setUnresolved(true);
                            if (callback != null) {
                                readPolicies(callback, operation, reader);
                            } else {
                                readPolicies(contract, operation, reader);
                            }
                        } else if (IMPLEMENTATION_COMPOSITE_QNAME.equals(name)) {
                            
                            // Read an implementation.composite
                            Composite implementation = factory.createComposite();
                            implementation.setName(getQName(reader, NAME));
                            implementation.setUnresolved(true);
                            component.setImplementation(implementation);
                            
                        } else {
    
                            // Read an extension element
                            Object extension = extensionProcessor.read(reader);
                            if (extension != null) {
                                if (extension instanceof Interface) {
    
                                    // <service><interface> and
                                    // <reference><interface>
                                    contract.setInterface((Interface)extension);
    
                                } else if (extension instanceof Binding) {
                                    // <service><binding> and <reference><binding>
                                    contract.getBindings().add((Binding)extension);
    
                                } else if (extension instanceof Implementation) {
    
                                    // <component><implementation>
                                    component.setImplementation((Implementation)extension);
                                }
                            }
                        }
                        break;
    
                    case XMLStreamConstants.CHARACTERS:
    
                        // Read an <include>qname</include>
                        if (include != null && INCLUDE_QNAME.equals(name)) {
                            include.setName(getQNameValue(reader, reader.getText().trim()));
                        }
    
                        break;
    
                    case END_ELEMENT:
                        name = reader.getName();
    
                        // Clear current state when reading reaching end element
                        if (SERVICE_QNAME.equals(name)) {
                            componentService = null;
                            compositeService = null;
                            contract = null;
                        } else if (INCLUDE_QNAME.equals(name)) {
                            include = null;
                        } else if (REFERENCE_QNAME.equals(name)) {
                            componentReference = null;
                            compositeReference = null;
                            contract = null;
                        } else if (PROPERTY_QNAME.equals(name)) {
                            componentProperty = null;
                            property = null;
                        } else if (COMPONENT_QNAME.equals(name)) {
                            component = null;
                        } else if (WIRE_QNAME.equals(name)) {
                            wire = null;
                        } else if (CALLBACK_QNAME.equals(name)) {
                            callback = null;
                        }
                        break;
                }
    
                // Read the next element
                if (reader.hasNext()) {
                    reader.next();
                }
            }
            return composite;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(Composite composite, XMLStreamWriter writer) throws ContributionWriteException {

        try {
            writeStartDocument(writer, COMPOSITE, new Attr(CONSTRAINING_TYPE, getConstrainingTypeAttr(composite)));
    
            for (Service service : composite.getServices()) {
                CompositeService compositeService = (CompositeService)service;
                ComponentService promotedService = compositeService.getPromotedService();
                String promote = promotedService != null ? promotedService.getName() : null;
                writeStart(writer, SERVICE, new Attr(NAME, service.getName()), new Attr(PROMOTE, promote));
                if (service.getCallback() != null) {
                    writeStart(writer, CALLBACK);
                    writeEnd(writer);
                }
                writeEnd(writer);
            }
    
            for (Component component : composite.getComponents()) {
                writeStart(writer, COMPONENT, new Attr(NAME, component.getName()));
    
                for (ComponentService service : component.getServices()) {
                    writeStart(writer, SERVICE, new Attr(NAME, service.getName()));
                    writeEnd(writer);
                    if (service.getCallback() != null) {
                        writeStart(writer, CALLBACK);
                        writeEnd(writer);
                    }
                }
    
                for (ComponentReference reference : component.getReferences()) {
                    // TODO handle multivalued target attribute
                    String target = reference.getTargets().isEmpty() ? null : reference.getTargets().get(0).getName();
                    writeStart(writer, REFERENCE,
                               new Attr(NAME, reference.getName()),
                               new Attr(TARGET,target));
                    if (reference.getCallback() != null) {
                        writeStart(writer, CALLBACK);
                        writeEnd(writer);
                    }
                    writeEnd(writer);
                }
    
                for (ComponentProperty property : component.getProperties()) {
                    writeStart(writer, PROPERTY, new Attr(NAME, property.getName()));
                    writeEnd(writer);
                }
    
                writeEnd(writer);
            }
    
            for (Reference reference : composite.getReferences()) {
                // TODO handle multivalued promote attribute
                CompositeReference compositeReference = (CompositeReference)reference;
                String promote;
                if (!compositeReference.getPromotedReferences().isEmpty())
                    promote = compositeReference.getPromotedReferences().get(0).getName();
                else
                    promote = null;
                writeStart(writer, REFERENCE,
                           new Attr(NAME, reference.getName()),
                           new Attr(PROMOTE, promote));
                if (reference.getCallback() != null) {
                    writeStart(writer, CALLBACK);
                    writeEnd(writer);
                }
                writeEnd(writer);
            }
    
            for (Property property : composite.getProperties()) {
                writeStart(writer, PROPERTY, new Attr(NAME, property.getName()));
                writeEnd(writer);
            }
    
            writeEndDocument(writer);
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(Composite composite, ArtifactResolver resolver) throws ContributionResolveException {
        
        // Resolve constraining type
        ConstrainingType constrainingType = composite.getConstrainingType(); 
        constrainingType = resolver.resolve(ConstrainingType.class, constrainingType); 
        composite.setConstrainingType(constrainingType);
        
        // Resolve includes in the composite
        for (int i = 0, n = composite.getIncludes().size(); i < n; i++) {
            Composite include = composite.getIncludes().get(i);
            include = resolver.resolve(Composite.class, include);
            composite.getIncludes().set(i, include);
        }
        
        // Resolve component implementations, services and references 
        for (Component component: composite.getComponents()) {
            constrainingType = component.getConstrainingType(); 
            constrainingType = resolver.resolve(ConstrainingType.class, constrainingType); 
            component.setConstrainingType(constrainingType);

            Implementation implementation = component.getImplementation();
            implementation = resolver.resolve(Implementation.class, implementation);
            component.setImplementation(implementation);
            
            resolveContract(component.getServices(), resolver);
            resolveContract(component.getReferences(), resolver);
        }
        
        // Resolve composite services and references
        resolveContract(composite.getServices(), resolver);
        resolveContract(composite.getReferences(), resolver);
    }

    public void wire(Composite composite) throws ContributionWireException {
        
        // Process the composite configuration
        CompositeUtil compositeUtil = new CompositeUtil(factory, composite);
        compositeUtil.configure(null);
    }

    public QName getArtifactType() {
        return COMPOSITE_QNAME;
    }
    
    public Class<Composite> getModelType() {
        return Composite.class;
    }
}
