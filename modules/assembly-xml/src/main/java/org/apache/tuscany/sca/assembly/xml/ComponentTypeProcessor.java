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
import static org.apache.tuscany.sca.assembly.xml.Constants.CALLBACK;
import static org.apache.tuscany.sca.assembly.xml.Constants.CALLBACK_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.COMPONENT_TYPE;
import static org.apache.tuscany.sca.assembly.xml.Constants.COMPONENT_TYPE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.ELEMENT;
import static org.apache.tuscany.sca.assembly.xml.Constants.IMPLEMENTATION;
import static org.apache.tuscany.sca.assembly.xml.Constants.MANY;
import static org.apache.tuscany.sca.assembly.xml.Constants.MUST_SUPPLY;
import static org.apache.tuscany.sca.assembly.xml.Constants.NAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.OPERATION_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.PROPERTY;
import static org.apache.tuscany.sca.assembly.xml.Constants.PROPERTY_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.REFERENCE;
import static org.apache.tuscany.sca.assembly.xml.Constants.REFERENCE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.SERVICE;
import static org.apache.tuscany.sca.assembly.xml.Constants.SERVICE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.TYPE;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Extensible;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.w3c.dom.Document;

/**
 * A componentType processor.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentTypeProcessor extends BaseAssemblyProcessor implements StAXArtifactProcessor<ComponentType> {
    
    /**
     * Constructs a new componentType processor.
     * 
     * @param modelFactories
     * @param extensionProcessor
     * @param extensionAttributeProcessor
     * @param monitor
     */
    public ComponentTypeProcessor(FactoryExtensionPoint modelFactories,
                                  StAXArtifactProcessor extensionProcessor,
                                  StAXAttributeProcessor extensionAttributeProcessor) {
        super(modelFactories, extensionProcessor);
    }
    
    public ComponentType read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException {
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
                            componentType.setConstrainingType(readConstrainingType(reader));
    
                        } else if (Constants.SERVICE_QNAME.equals(name)) {
    
                            // Read a <service>
                            service = assemblyFactory.createService();
                            contract = service;
                            service.setName(getString(reader, Constants.NAME));
                            componentType.getServices().add(service);
                            policyProcessor.readPolicies(service, reader);
    
                        } else if (Constants.REFERENCE_QNAME.equals(name)) {
    
                            // Read a <reference>
                            reference = assemblyFactory.createReference();
                            contract = reference;
                            reference.setName(getString(reader, Constants.NAME));
                            reference.setWiredByImpl(getBoolean(reader, Constants.WIRED_BY_IMPL));
                            readMultiplicity(reference, reader);
                            readTargets(reference, reader);
                            componentType.getReferences().add(reference);
                            policyProcessor.readPolicies(reference, reader);
    
                        } else if (Constants.PROPERTY_QNAME.equals(name)) {
    
                            // Read a <property>
                            property = assemblyFactory.createProperty();
                            readAbstractProperty(property, reader, context);
                            policyProcessor.readPolicies(property, reader);
                            
                            // Read the property value
                            Document value = readPropertyValue(property.getXSDElement(), property.getXSDType(), property.isMany(), reader, context);
                            property.setValue(value);
                            
                            componentType.getProperties().add(property);
                            
                        } else if (Constants.IMPLEMENTATION_QNAME.equals(name)) {
                            
                            // Read an <implementation> element
                            policyProcessor.readPolicies(componentType, reader);
                            
                        } else if (Constants.CALLBACK_QNAME.equals(name)) {
    
                            // Read a <callback>
                            callback = assemblyFactory.createCallback();
                            contract.setCallback(callback);
                            policyProcessor.readPolicies(callback, reader);
    
                        } else if (OPERATION_QNAME.equals(name)) {
    
                            // Read an <operation>
                            Operation operation = new OperationImpl();
                            operation.setName(getString(reader, NAME));
                            operation.setUnresolved(true);
                            if (callback != null) {
                                policyProcessor.readPolicies(callback, operation, reader);
                            } else {
                                policyProcessor.readPolicies(contract, operation, reader);
                            }
                        } else {
    
                            // Read an extension element
                            Object extension = extensionProcessor.read(reader, context);
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
                                } else {
                                    
                                    // Add the extension element to the current element
                                    if (callback != null) {
                                        callback.getExtensions().add(extension);
                                    } else if (contract != null) {
                                        contract.getExtensions().add(extension);
                                    } else if (property != null) {
                                        property.getExtensions().add(extension);
                                    } else {
                                        if (componentType instanceof Extensible) {
                                            ((Extensible)componentType).getExtensions().add(extension);
                                        }
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
        }
        catch (XMLStreamException e) {
            ContributionReadException ex = new ContributionReadException(e);
            error(context.getMonitor(), "XMLStreamException", reader, ex);
        }
        
        return componentType;
    }
    
    public void write(ComponentType componentType, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        
        // Write <componentType> element
        writeStartDocument(writer, COMPONENT_TYPE,
               writeConstrainingType(componentType));

        // Write <service> elements
        for (Service service : componentType.getServices()) {
            writeStart(writer, SERVICE, new XAttr(NAME, service.getName()),
                       policyProcessor.writePolicies(service));

            if (service.getInterfaceContract() != null) {
                extensionProcessor.write(service.getInterfaceContract(), writer, context);
            }
            
            for (Binding binding: service.getBindings()) {
                extensionProcessor.write(binding, writer, context);
            }
            
            if (service.getCallback() != null) {
                Callback callback = service.getCallback();
                writeStart(writer, CALLBACK, policyProcessor.writePolicies(callback));

                for (Binding binding: callback.getBindings()) {
                    extensionProcessor.write(binding, writer, context);
                }
                for (Object extension: callback.getExtensions()) {
                    extensionProcessor.write(extension, writer, context);
                }
                
                writeEnd(writer);
            }
            
            for (Object extension: service.getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }
            
            writeEnd(writer);
        }

        // Write <reference> elements
        for (Reference reference : componentType.getReferences()) {
            
            writeStart(writer, REFERENCE,
                  new XAttr(NAME, reference.getName()),
                  writeMultiplicity(reference),
                  writeTargets(reference),
                  policyProcessor.writePolicies(reference));

            extensionProcessor.write(reference.getInterfaceContract(), writer, context);
            
            for (Binding binding: reference.getBindings()) {
                extensionProcessor.write(binding, writer, context);
            }
            
            if (reference.getCallback() != null) {
                Callback callback = reference.getCallback();
                writeStart(writer, CALLBACK,
                           policyProcessor.writePolicies(callback));

                for (Binding binding: callback.getBindings()) {
                    extensionProcessor.write(binding, writer, context);
                }
                for (Object extension: callback.getExtensions()) {
                    extensionProcessor.write(extension, writer, context);
                }
                
                writeEnd(writer);
            }

            for (Object extension: reference.getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }
            
            writeEnd(writer);
        }

        // Write <property> elements
        for (Property property : componentType.getProperties()) {
            writeStart(writer,
                       PROPERTY,
                       new XAttr(NAME, property.getName()),
                       new XAttr(MUST_SUPPLY, property.isMustSupply()),
                       new XAttr(MANY, property.isMany()),
                       new XAttr(TYPE, property.getXSDType()),
                       new XAttr(ELEMENT, property.getXSDElement()),
                       policyProcessor.writePolicies(property));

            // Write property value
            writePropertyValue(property.getValue(), property.getXSDElement(), property.getXSDType(), writer);

            // Write extensions
            for (Object extension : property.getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }

            writeEnd(writer);
        }

        // Write extension elements
        if (componentType instanceof Extensible) {
            for (Object extension: ((Extensible)componentType).getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }
        }
        
        // Write <implementation> elements if the componentType has
        // any intents or policySets
        boolean writeImplementation = false;
        if (componentType instanceof PolicySubject) {
            if (!((PolicySubject)componentType).getRequiredIntents().isEmpty()) {
                writeImplementation = true;
            }
        }
        if (componentType instanceof PolicySubject) {
            if (!((PolicySubject)componentType).getPolicySets().isEmpty()) {
                writeImplementation = true;
            }
        }
        if (writeImplementation) {
            writeStart(writer, IMPLEMENTATION,
                       policyProcessor.writePolicies(componentType));
        }
        
        writeEndDocument(writer);
    }
    
    public void resolve(ComponentType componentType, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

        // Resolve component type services and references
        resolveContracts(componentType.getServices(), resolver, context);
        resolveContracts(componentType.getReferences(), resolver, context);
    }
    
    public QName getArtifactType() {
        return COMPONENT_TYPE_QNAME;
    }
    
    public Class<ComponentType> getModelType() {
        return ComponentType.class;
    }
}
