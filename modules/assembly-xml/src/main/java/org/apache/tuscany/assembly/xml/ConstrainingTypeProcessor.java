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

package org.apache.tuscany.assembly.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.AbstractContract;
import org.apache.tuscany.assembly.AbstractProperty;
import org.apache.tuscany.assembly.AbstractReference;
import org.apache.tuscany.assembly.AbstractService;
import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtension;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.policy.PolicyFactory;

/**
 * A contrainingType content handler.
 * 
 * @version $Rev$ $Date$
 */
public class ConstrainingTypeProcessor extends BaseArtifactProcessor implements StAXArtifactProcessorExtension<ConstrainingType> {

    /**
     * Construct a new constrainingType processor.
     * @param factory
     * @param policyFactory
     * @param extensionProcessor
     */
    public ConstrainingTypeProcessor(AssemblyFactory factory, PolicyFactory policyFactory, StAXArtifactProcessorExtension extensionProcessor) {
        super(factory, policyFactory, extensionProcessor);
    }

    public ConstrainingType read(XMLStreamReader reader) throws ContributionReadException {
        ConstrainingType constrainingType = null;
        AbstractService abstractService = null;
        AbstractReference abstractReference = null;
        AbstractProperty abstractProperty = null;
        AbstractContract abstractContract = null;
        QName name = null;
        
        try {
            
            // Read the constrainingType document
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
    
                    case START_ELEMENT:
                        name = reader.getName();
                        
                        // Read a <constrainingType>
                        if (Constants.CONSTRAINING_TYPE_QNAME.equals(name)) {
                            constrainingType = assemblyFactory.createConstrainingType();
                            constrainingType.setName(new QName(getString(reader, TARGET_NAMESPACE), getString(reader, NAME)));
                            readIntents(constrainingType, reader);
    
                        } else if (Constants.SERVICE_QNAME.equals(name)) {
                            
                            // Read a <service>
                            abstractService = assemblyFactory.createAbstractService();
                            abstractContract = abstractService;
                            abstractService.setName(getString(reader, Constants.NAME));
                            constrainingType.getServices().add(abstractService);
                            readIntents(abstractService, reader);
    
                        } else if (Constants.REFERENCE_QNAME.equals(name)) {
                            
                            // Read a <reference>
                            abstractReference = assemblyFactory.createAbstractReference();
                            abstractContract = abstractReference;
                            abstractReference.setName(getString(reader, Constants.NAME));
                            readMultiplicity(abstractReference, reader);
                            constrainingType.getReferences().add(abstractReference);
                            readIntents(abstractReference, reader);
    
                        } else if (Constants.PROPERTY_QNAME.equals(name)) {
                            
                            // Read a <property>
                            abstractProperty = assemblyFactory.createAbstractProperty();
                            readAbstractProperty(abstractProperty, reader);
                            constrainingType.getProperties().add(abstractProperty);
                            readIntents(abstractProperty, reader);
                            
                        } else if (OPERATION.equals(name)) {
    
                            // Read an <operation>
                            Operation operation = assemblyFactory.createOperation();
                            operation.setName(getString(reader, NAME));
                            operation.setUnresolved(true);
                            readIntents(abstractContract, operation, reader);
                            
                        } else {
    
                            // Read an extension element
                            Object extension = extensionProcessor.read(reader);
                            if (extension instanceof InterfaceContract) {
                                
                                // <service><interface> and <reference><interface>
                                abstractContract.setInterfaceContract((InterfaceContract)extension);
                            }
                        }
                        break;
    
                    case END_ELEMENT:
                        name = reader.getName();
    
                        // Clear current state when reading reaching end element
                        if (SERVICE_QNAME.equals(name)) {
                            abstractService = null;
                            abstractContract = null;
                        } else if (REFERENCE_QNAME.equals(name)) {
                            abstractReference = null;
                            abstractContract = null;
                        } else if (PROPERTY_QNAME.equals(name)) {
                            abstractProperty = null;
                        }
                        break;
                }
                if (reader.hasNext()) {
                    reader.next();
                }
            }
            return constrainingType;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(ConstrainingType constrainingType, XMLStreamWriter writer) throws ContributionWriteException {
        
        try {
            writeStartDocument(writer, CONSTRAINING_TYPE,
               new XAttr(TARGET_NAMESPACE, constrainingType.getName().getNamespaceURI()),
               new XAttr(NAME, constrainingType.getName().getLocalPart()));
    
            for (AbstractService service : constrainingType.getServices()) {
                writeStart(writer, SERVICE, new XAttr(NAME, service.getName()));
                extensionProcessor.write(service.getInterfaceContract(), writer);
                writeEnd(writer);
            }
    
            for (AbstractReference reference : constrainingType.getReferences()) {
                writeStart(writer, REFERENCE,
                      new XAttr(NAME, reference.getName()));
                extensionProcessor.write(reference.getInterfaceContract(), writer);
                writeEnd(writer);
            }
    
            for (AbstractProperty property : constrainingType.getProperties()) {
                writeStart(writer, PROPERTY, new XAttr(NAME, property.getName()));
                writeEnd(writer);
            }
    
            writeEndDocument(writer);
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(ConstrainingType constrainingType, ArtifactResolver resolver) throws ContributionResolveException {

        // Resolve component type services and references
        resolveAbstractContracts(constrainingType.getServices(), resolver);
        resolveAbstractContracts(constrainingType.getReferences(), resolver);
    }
    
    public void wire(ConstrainingType model) throws ContributionWireException {
        // TODO Auto-generated method stub
    }

    public QName getArtifactType() {
        return CONSTRAINING_TYPE_QNAME;
    }
    
    public Class<ConstrainingType> getModelType() {
        return ConstrainingType.class;
    }
}
