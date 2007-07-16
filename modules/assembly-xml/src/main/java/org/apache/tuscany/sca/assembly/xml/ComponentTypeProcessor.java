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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * A componentType processor.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentTypeProcessor extends BaseArtifactProcessor implements StAXArtifactProcessor<ComponentType> {
    
    /**
     * Constructs a new componentType processor.
     * @param factory
     * @param policyFactory
     * @param registry
     */
    public ComponentTypeProcessor(AssemblyFactory factory, PolicyFactory policyFactory, StAXArtifactProcessor extensionProcessor) {
        super(factory, policyFactory, extensionProcessor);
    }
    
    public ComponentType read(XMLStreamReader reader) throws ContributionReadException {
        ComponentType componentType = null;
        Service service = null;
        Reference reference = null;
        Contract contract = null;
        Property property = null;
        Callback callback = null;
        QName name = null;
        
        try {
    
            // Read the componentType document
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT:
                        name = reader.getName();
    
                        if (Constants.COMPONENT_TYPE_QNAME.equals(name)) {
    
                            // Read a <componentType>
                            componentType = assemblyFactory.createComponentType();
                            componentType.setConstrainingType(getConstrainingType(reader));
                            readPolicies(componentType, reader);
    
                        } else if (Constants.SERVICE_QNAME.equals(name)) {
    
                            // Read a <service>
                            service = assemblyFactory.createService();
                            contract = service;
                            service.setName(getString(reader, Constants.NAME));
                            componentType.getServices().add(service);
                            readPolicies(service, reader);
    
                        } else if (Constants.REFERENCE_QNAME.equals(name)) {
                            // Read a <reference>
                            reference = assemblyFactory.createReference();
                            contract = reference;
                            reference.setName(getString(reader, Constants.NAME));
                            reference.setWiredByImpl(getBoolean(reader, Constants.WIRED_BY_IMPL));
                            readMultiplicity(reference, reader);
                            readTargets(reference, reader);
                            componentType.getReferences().add(reference);
                            readPolicies(reference, reader);
    
                        } else if (Constants.PROPERTY_QNAME.equals(name)) {
    
                            // Read a <property>
                            property = assemblyFactory.createProperty();
                            readPolicies(property, reader);
                            readProperty(property, reader);
                            componentType.getProperties().add(property);
                            
                        } else if (Constants.IMPLEMENTATION_QNAME.equals(name)) {
                            
                            // Read an <implementation> element
                            readPolicies(componentType, reader);
                            
                        } else if (Constants.CALLBACK_QNAME.equals(name)) {
    
                            // Read a <callback>
                            callback = assemblyFactory.createCallback();
                            contract.setCallback(callback);
                            readPolicies(callback, reader);
    
                        } else if (OPERATION.equals(name)) {
    
                            // Read an <operation>
                            Operation operation = assemblyFactory.createOperation();
                            operation.setName(getString(reader, NAME));
                            operation.setUnresolved(true);
                            if (callback != null) {
                                readPolicies(callback, operation, reader);
                            } else {
                                readPolicies(contract, operation, reader);
                            }
                        } else {
    
                            // Read an extension element
                            Object extension = extensionProcessor.read(reader);
                            if (extension != null) {
                                if (extension instanceof InterfaceContract) {
    
                                    // <service><interface> and <reference><interface>
                                    contract.setInterfaceContract((InterfaceContract)extension);
    
                                } else if (extension instanceof Binding) {
    
                                    // <service><binding> and <reference><binding>
                                    if (callback != null) {
                                        callback.getBindings().add((Binding)extension);
                                    } else {
                                        contract.getBindings().add((Binding)extension);
                                    }
                                }
                            }
                        }
                        break;
    
                    case END_ELEMENT:
                        name = reader.getName();
    
                        // Clear current state when reading reaching end element
                        if (SERVICE_QNAME.equals(name)) {
                            service = null;
                            contract = null;
                        } else if (REFERENCE_QNAME.equals(name)) {
                            reference = null;
                            contract = null;
                        } else if (PROPERTY_QNAME.equals(name)) {
                            property = null;
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
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
        return componentType;
    }
    
    public void validate(ComponentType componentType, List<Base> problems) {
        if (problems == null) {
            problems = new ArrayList<Base>();
        }
        validatePropertyDefinitions(componentType.getProperties(), problems);
    }
    
    public void validatePropertyDefinitions(List<Property> properties, List<Base> problems) {
        for(Property aProperty : properties) {
            if (aProperty.isMustSupply() && aProperty.getValue() != null) {
                problems.add(aProperty);
            }
        }
    }

    public void write(ComponentType componentType, XMLStreamWriter writer) throws ContributionWriteException {
        
        try {
            writeStartDocument(writer, COMPONENT_TYPE,
                   new XAttr(CONSTRAINING_TYPE, getConstrainingTypeAttr(componentType)));
    
            for (Service service : componentType.getServices()) {
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
    
            for (Reference reference : componentType.getReferences()) {
                // TODO handle multivalued target attribute
                String target = reference.getTargets().isEmpty() ? null : reference.getTargets().get(0).getName();
                writeStart(writer, REFERENCE,
                      new XAttr(NAME, reference.getName()),
                      new XAttr(TARGET, target));

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
    
            for (Property property : componentType.getProperties()) {
                writeStart(writer, PROPERTY, new XAttr(NAME, property.getName()));
                writeEnd(writer);
            }
    
            writeEndDocument(writer);
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(ComponentType componentType, ModelResolver resolver) throws ContributionResolveException {

        // Resolve component type services and references
        resolveContracts(componentType.getServices(), resolver);
        resolveContracts(componentType.getReferences(), resolver);
    }
    
    public QName getArtifactType() {
        return COMPONENT_TYPE_QNAME;
    }
    
    public Class<ComponentType> getModelType() {
        return ComponentType.class;
    }
}
