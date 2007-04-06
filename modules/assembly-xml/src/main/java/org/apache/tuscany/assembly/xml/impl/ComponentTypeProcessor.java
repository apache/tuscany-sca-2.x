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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.Callback;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.Constants;
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
 * A componentType processor.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentTypeProcessor extends BaseArtifactProcessor implements StAXArtifactProcessor<ComponentType> {
    private AssemblyFactory factory;
    private StAXArtifactProcessor<Object> extensionProcessor;
    
    /**
     * Constructs a new componentType processor.
     * @param factory
     * @param policyFactory
     * @param registry
     */
    public ComponentTypeProcessor(AssemblyFactory factory, PolicyFactory policyFactory, StAXArtifactProcessor<Object> extensionProcessor) {
        super(factory, policyFactory);
        this.factory = factory;
        this.extensionProcessor = extensionProcessor;
    }
    
    /**
     * Constructs a new componentType processor.
     * @param extensionProcessor
     */
    public ComponentTypeProcessor(StAXArtifactProcessor<Object> extensionProcessor) {
        this(new DefaultAssemblyFactory(), new DefaultPolicyFactory(), extensionProcessor);
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
                            componentType = factory.createComponentType();
                            componentType.setConstrainingType(getConstrainingType(reader));
                            readPolicies(componentType, reader);
    
                        } else if (Constants.SERVICE_QNAME.equals(name)) {
    
                            // Read a <service>
                            service = factory.createService();
                            contract = service;
                            service.setName(getString(reader, Constants.NAME));
                            componentType.getServices().add(service);
                            readPolicies(service, reader);
    
                        } else if (Constants.REFERENCE_QNAME.equals(name)) {
    
                            // Read a <reference>
                            reference = factory.createReference();
                            contract = reference;
                            reference.setName(getString(reader, Constants.NAME));
                            readMultiplicity(reference, reader);
    
                            // TODO support multivalued attribute
                            ComponentService target = factory.createComponentService();
                            target.setUnresolved(true);
                            target.setName(getString(reader, Constants.TARGET));
                            reference.getTargets().add(target);
    
                            componentType.getReferences().add(reference);
                            readPolicies(reference, reader);
    
                        } else if (Constants.PROPERTY_QNAME.equals(name)) {
    
                            // Read a <property>
                            property = factory.createProperty();
                            readProperty(property, reader);
                            componentType.getProperties().add(property);
                            readPolicies(property, reader);
    
                        } else if (Constants.CALLBACK_QNAME.equals(name)) {
    
                            // Read a <callback>
                            callback = factory.createCallback();
                            contract.setCallback(callback);
                            readPolicies(callback, reader);
    
                        } else if (OPERATION.equals(name)) {
    
                            // Read an <operation>
                            Operation operation = factory.createOperation();
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
                                if (extension instanceof Interface) {
    
                                    // <service><interface> and <reference><interface>
                                    contract.setInterface((Interface)extension);
    
                                } else if (extension instanceof Binding) {
    
                                    // <service><binding> and <reference><binding>
                                    contract.getBindings().add((Binding)extension);
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

    public void write(ComponentType model, XMLStreamWriter outputSource) throws ContributionWriteException {
        // TODO Auto-generated method stub
        
    }
    
    public void resolve(ComponentType componentType, ArtifactResolver resolver) throws ContributionResolveException {

        // Resolve componen type services and references
        resolveContract(componentType.getServices(), resolver);
        resolveContract(componentType.getReferences(), resolver);
    }
    
    public void wire(ComponentType model) throws ContributionWireException {
        //TODO optimize the model 
    }
    
    public QName getArtifactType() {
        return COMPONENT_TYPE_QNAME;
    }
    
    public Class<ComponentType> getModelType() {
        return ComponentType.class;
    }
}
