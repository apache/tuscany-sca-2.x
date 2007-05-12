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

package org.apache.tuscany.sca.assembly.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.contribution.resolver.ModelResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.assembly.builder.impl.DefaultCompositeBuilder;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * A composite processor.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeProcessor extends BaseArtifactProcessor implements StAXArtifactProcessor<Composite> {
    
    private InterfaceContractMapper interfaceContractMapper;

    /**
     * Construct a new composite processor
     * @param assemblyFactory
     * @param policyFactory
     * @param extensionProcessor 
     */
    public CompositeProcessor(AssemblyFactory factory, PolicyFactory policyFactory,
                              InterfaceContractMapper interfaceContractMapper,
                              StAXArtifactProcessor extensionProcessor) {
        super(factory, policyFactory, extensionProcessor);
        this.interfaceContractMapper = interfaceContractMapper;
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
                            composite = assemblyFactory.createComposite();
                            composite.setName(new QName(getString(reader, TARGET_NAMESPACE), getString(reader, NAME)));
                            composite.setAutowire(getBoolean(reader, AUTOWIRE));
                            composite.setLocal(getBoolean(reader, LOCAL));
                            composite.setConstrainingType(getConstrainingType(reader));
                            readPolicies(composite, reader);
    
                        } else if (INCLUDE_QNAME.equals(name)) {
    
                            // Read an <include>
                            include = assemblyFactory.createComposite();
                            include.setUnresolved(true);
                            composite.getIncludes().add(include);
    
                        } else if (SERVICE_QNAME.equals(name)) {
                            if (component != null) {
    
                                // Read a <component><service>
                                componentService = assemblyFactory.createComponentService();
                                contract = componentService;
                                componentService.setName(getString(reader, NAME));
                                component.getServices().add(componentService);
                                readPolicies(contract, reader);
                            } else {
    
                                // Read a <composite><service>
                                compositeService = assemblyFactory.createCompositeService();
                                contract = compositeService;
                                compositeService.setName(getString(reader, NAME));
    
                                ComponentService promoted = assemblyFactory.createComponentService();
                                promoted.setUnresolved(true);
                                promoted.setName(getString(reader, PROMOTE));
                                compositeService.setPromotedService(promoted);
    
                                composite.getServices().add(compositeService);
                                readPolicies(contract, reader);
                            }
    
                        } else if (REFERENCE_QNAME.equals(name)) {
                            if (component != null) {
                                // Read a <component><reference>
                                componentReference = assemblyFactory.createComponentReference();
                                contract = componentReference;
                                componentReference.setName(getString(reader, NAME));
                                readMultiplicity(componentReference, reader);
                                componentReference.setAutowire(getBoolean(reader, AUTOWIRE));
                                readTargets(componentReference, reader);
                                componentReference.setWiredByImpl(getBoolean(reader, WIRED_BY_IMPL));
                                component.getReferences().add(componentReference);
                                readPolicies(contract, reader);
                            } else {
                                // Read a <composite><reference>
                                compositeReference = assemblyFactory.createCompositeReference();
                                contract = compositeReference;
                                compositeReference.setName(getString(reader, NAME));
                                readMultiplicity(compositeReference, reader);
                                readTargets(compositeReference, reader);
                                readPromotes(compositeReference, reader);
                                compositeReference.setWiredByImpl(getBoolean(reader, WIRED_BY_IMPL));
                                composite.getReferences().add(compositeReference);
                                readPolicies(contract, reader);
                            }
    
                        } else if (PROPERTY_QNAME.equals(name)) {
                            if (component != null) {
                                // Read a <component><property>
                                componentProperty = assemblyFactory.createComponentProperty();
                                property = componentProperty;
                                componentProperty.setSource(getString(reader, SOURCE));
                                componentProperty.setFile(getString(reader, FILE));
                                readPolicies(property, reader);
                                readProperty(componentProperty, reader);
                                component.getProperties().add(componentProperty);
                            } else {
    
                                // Read a <composite><property>
                                property = assemblyFactory.createProperty();
                                readPolicies(property, reader);
                                readProperty(property, reader);
                                composite.getProperties().add(property);
                            }
    
                        } else if (COMPONENT_QNAME.equals(name)) {
    
                            // Read a <component>
                            component = assemblyFactory.createComponent();
                            component.setName(getString(reader, NAME));
                            component.setConstrainingType(getConstrainingType(reader));
                            composite.getComponents().add(component);
                            readPolicies(component, reader);
    
                        } else if (WIRE_QNAME.equals(name)) {
    
                            // Read a <wire>
                            wire = assemblyFactory.createWire();
                            ComponentReference source = assemblyFactory.createComponentReference();
                            source.setUnresolved(true);
                            source.setName(getString(reader, SOURCE));
                            wire.setSource(source);
    
                            ComponentService target = assemblyFactory.createComponentService();
                            target.setUnresolved(true);
                            target.setName(getString(reader, TARGET));
                            wire.setTarget(target);
    
                            composite.getWires().add(wire);
                            readPolicies(wire, reader);
    
                        } else if (CALLBACK_QNAME.equals(name)) {
    
                            // Read a <callback>
                            callback = assemblyFactory.createCallback();
                            contract.setCallback(callback);
                            readPolicies(callback, reader);
    
                        } else if (OPERATION_QNAME.equals(name)) {
    
                            // Read an <operation>
                            Operation operation = assemblyFactory.createOperation();
                            operation.setName(getString(reader, NAME));
                            operation.setUnresolved(true);
                            if (callback != null) {
                                readPolicies(callback, operation, reader);
                            } else {
                                readPolicies(contract, operation, reader);
                            }
                        } else if (IMPLEMENTATION_COMPOSITE_QNAME.equals(name)) {
                            
                            // Read an implementation.composite
                            Composite implementation = assemblyFactory.createComposite();
                            implementation.setName(getQName(reader, NAME));
                            implementation.setUnresolved(true);
                            component.setImplementation(implementation);
                            
                        } else {
    
                            // Read an extension element
                            Object extension = extensionProcessor.read(reader);
                            if (extension != null) {
                                if (extension instanceof InterfaceContract) {
    
                                    // <service><interface> and
                                    // <reference><interface>
                                    contract.setInterfaceContract((InterfaceContract)extension);
    
                                } else if (extension instanceof Binding) {
                                    // <service><binding> and <reference><binding>
                                    contract.getBindings().add((Binding)extension);
    
                                } else if (extension instanceof Implementation) {
    
                                    // <component><implementation>
                                    component.setImplementation((Implementation)extension);
                                } else {
                                    
                                    // FIXME: We need to decide where to host the extensions
                                    composite.getExtensions().add(extension);
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
            writeStartDocument(writer, COMPOSITE,
                new XAttr(CONSTRAINING_TYPE, getConstrainingTypeAttr(composite)),
                new XAttr(TARGET_NAMESPACE, composite.getName().getNamespaceURI()),
                new XAttr(NAME, composite.getName().getLocalPart()));
    
            for (Service service : composite.getServices()) {
                CompositeService compositeService = (CompositeService)service;
                ComponentService promotedService = compositeService.getPromotedService();
                String promote = promotedService != null ? promotedService.getName() : null;
                writeStart(writer, SERVICE, new XAttr(NAME, service.getName()), new XAttr(PROMOTE, promote));

                extensionProcessor.write(service.getInterfaceContract(), writer);
                
                for (Binding binding: service.getBindings()) {
                    extensionProcessor.write(binding, writer);
                }
                
                if (service.getCallback() != null) {
                    writeStart(writer, CALLBACK);
                    writeEnd(writer);
                }
                writeEnd(writer);
            }
    
            for (Component component : composite.getComponents()) {
                writeStart(writer, COMPONENT, new XAttr(NAME, component.getName()));
    
                for (ComponentService service : component.getServices()) {
                    writeStart(writer, SERVICE, new XAttr(NAME, service.getName()));

                    extensionProcessor.write(service.getInterfaceContract(), writer);

                    for (Binding binding: service.getBindings()) {
                        extensionProcessor.write(binding, writer);
                    }
                    
                    if (service.getCallback() != null) {
                        writeStart(writer, CALLBACK);
                        writeEnd(writer);
                    }
                    writeEnd(writer);
                }
    
                for (ComponentReference reference : component.getReferences()) {
                    // TODO handle multivalued target attribute
                    String target = reference.getTargets().isEmpty() ? null : reference.getTargets().get(0).getName();
                    writeStart(writer, REFERENCE,
                               new XAttr(NAME, reference.getName()),
                               new XAttr(TARGET,target));
                    
                    extensionProcessor.write(reference.getInterfaceContract(), writer);
                    
                    for (Binding binding: reference.getBindings()) {
                        extensionProcessor.write(binding, writer);
                    }
                    
                    if (reference.getCallback() != null) {
                        writeStart(writer, CALLBACK);
                        writeEnd(writer);
                    }
                    writeEnd(writer);
                }
    
                for (ComponentProperty property : component.getProperties()) {
                    writeStart(writer, PROPERTY, new XAttr(NAME, property.getName()));
                    writeEnd(writer);
                }

                // Write the component implementation
                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    writeStart(writer, IMPLEMENTATION_COMPOSITE, 
                       new XAttr(NAME, composite.getName()));
                    writeEnd(writer);
                } else {
                    extensionProcessor.write(component.getImplementation(), writer);
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
                           new XAttr(NAME, reference.getName()),
                           new XAttr(PROMOTE, promote));

                extensionProcessor.write(reference.getInterfaceContract(), writer);
                
                for (Binding binding: reference.getBindings()) {
                    extensionProcessor.write(binding, writer);
                }
                
                if (reference.getCallback() != null) {
                    writeStart(writer, CALLBACK);
                    writeEnd(writer);
                }
                writeEnd(writer);
            }
    
            for (Property property : composite.getProperties()) {
                writeStart(writer, PROPERTY, new XAttr(NAME, property.getName()));
                writeEnd(writer);
            }
    
            writeEndDocument(writer);
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(Composite composite, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve constraining type
        ConstrainingType constrainingType = composite.getConstrainingType(); 
        constrainingType = resolver.resolveModel(ConstrainingType.class, constrainingType); 
        composite.setConstrainingType(constrainingType);
        
        // Resolve includes in the composite
        for (int i = 0, n = composite.getIncludes().size(); i < n; i++) {
            Composite include = composite.getIncludes().get(i);
            include = resolver.resolveModel(Composite.class, include);
            composite.getIncludes().set(i, include);
        }

        // resolve and extensions to the standard SCDL that appear in the 
        // SCDL. 
        for (int i = 0, n = composite.getExtensions().size(); i < n; i++) {
            Object model = composite.getExtensions().get(i);
            extensionProcessor.resolve(model, resolver);
        }
        
        // Resolve component implementations, services and references 
        for (Component component: composite.getComponents()) {
            constrainingType = component.getConstrainingType(); 
            constrainingType = resolver.resolveModel(ConstrainingType.class, constrainingType); 
            component.setConstrainingType(constrainingType);

            Implementation implementation = component.getImplementation();
            implementation = resolveImplementation(implementation, resolver);
            component.setImplementation(implementation);
            
            resolveContracts(component.getServices(), resolver);
            resolveContracts(component.getReferences(), resolver);
        }
        
        // Resolve composite services and references
        resolveContracts(composite.getServices(), resolver);
        resolveContracts(composite.getReferences(), resolver);
    }

    public QName getArtifactType() {
        return COMPOSITE_QNAME;
    }
    
    public Class<Composite> getModelType() {
        return Composite.class;
    }
    
    /**
     * Read list of refence targets
     * @param reference
     * @param reader
     */
    protected void readPromotes(CompositeReference reference, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, Constants.PROMOTE);
        ComponentReference promoted = null;
        if (value != null) {
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                promoted = assemblyFactory.createComponentReference();
                promoted.setUnresolved(true);
                promoted.setName(tokens.nextToken());
                reference.getPromotedReferences().add(promoted);
            }
        }
    }
}
