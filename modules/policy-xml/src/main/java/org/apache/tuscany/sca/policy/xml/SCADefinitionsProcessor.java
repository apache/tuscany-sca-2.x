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

package org.apache.tuscany.sca.policy.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor.XAttr;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.SCADefinitions;
import org.apache.tuscany.sca.policy.impl.SCADefinitionsImpl;

/**
 * Processor for SCA Definitions
 * 
 */
public class SCADefinitionsProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<SCADefinitions>, PolicyConstants {
    
    private StAXArtifactProcessor<Object> extensionProcessor;
    private ModelResolver definitionsResolver;
    
    /**
     * Construct a new (sca) definitions processor
     * @param policyFactory
     * @param extensionProcessor 
     */
    public SCADefinitionsProcessor(PolicyFactory policyFactory,
                              StAXArtifactProcessor<Object> extensionProcessor) {
        this.extensionProcessor = extensionProcessor;
    }
    
    /**
     * Construct a new (sca) definitions processor
     * @param policyFactory
     * @param extensionProcessor 
     * @param modelResolver 
     */
    public SCADefinitionsProcessor(PolicyFactory policyFactory,
                              StAXArtifactProcessor<Object> extensionProcessor,
                              ModelResolver modelResolver) {
        this.extensionProcessor = extensionProcessor;
        this.definitionsResolver = modelResolver;
    }

    public SCADefinitions read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        QName name = null;
        SCADefinitions definitions = null;

        // Read the composite document
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT: {
                    name = reader.getName();
                    if ( SCA_DEFINITIONS_QNAME.equals(name)) {
                        definitions = new SCADefinitionsImpl();
                        definitions.setTargetNamespace(reader.getAttributeValue(null, TARGET_NAMESPACE));
                    } else {
                        Object extension = extensionProcessor.read(reader);
                        if (extension != null) {
                            if ( extension instanceof Intent ) {
                                definitions.getPolicyIntents().add((Intent)extension);
                            } else if ( extension instanceof PolicySet ) {
                                definitions.getPolicySets().add((PolicySet)extension);
                            } else if ( extension instanceof IntentAttachPointType ) {
                                IntentAttachPointType type = (IntentAttachPointType)extension;
                                if ( type.getName().getLocalPart().startsWith(BINDING)) {
                                    definitions.getBindingTypes().add((IntentAttachPointType)extension);
                                } else if ( type.getName().getLocalPart().startsWith(IMPLEMENTATION)) {
                                    definitions.getImplementationTypes().add((IntentAttachPointType)extension);
                                }
                            } 
                            
                            if ( getDefinitionsResolver() != null ) {
                                getDefinitionsResolver().addModel(extension);
                            }
                        }
                        break;
                    }
                }

                case XMLStreamConstants.CHARACTERS:
                    break;

                case END_ELEMENT:
                    break;
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return definitions;
    }
    
    public void write(SCADefinitions definitions, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        writeStartDocument(writer, SCA10_NS, SCA_DEFINITIONS, 
                               new XAttr(TARGET_NAMESPACE, definitions.getTargetNamespace().toString()));
    
        for (Intent policyIntent : definitions.getPolicyIntents()) {
            extensionProcessor.write(policyIntent, writer);
        }
        
        for (PolicySet policySet : definitions.getPolicySets()) {
            extensionProcessor.write(policySet, writer);
        }
        
        for (IntentAttachPointType bindingType : definitions.getBindingTypes()) {
            extensionProcessor.write(bindingType, writer);
        }
        
        for (IntentAttachPointType implType : definitions.getImplementationTypes()) {
            extensionProcessor.write(implType, writer);
        }

        writeEndDocument(writer);
    }
    
    public void resolve(SCADefinitions scaDefns, ModelResolver resolver) throws ContributionResolveException {
        for (int count = 0, size = scaDefns.getPolicyIntents().size(); count < size; count++) {
            Intent intent = scaDefns.getPolicyIntents().get(count);
            extensionProcessor.resolve(intent, resolver);
        }
        
        for (int count = 0, size = scaDefns.getPolicySets().size(); count < size; count++) {
            PolicySet policySet = scaDefns.getPolicySets().get(count);
            extensionProcessor.resolve(policySet, resolver);
        }
        
        for (int count = 0, size = scaDefns.getBindingTypes().size(); count < size; count++) {
            IntentAttachPointType bindingType = scaDefns.getBindingTypes().get(count);
            extensionProcessor.resolve(bindingType, resolver);
        }
        
        for (int count = 0, size = scaDefns.getImplementationTypes().size(); count < size; count++) {
            IntentAttachPointType implType = scaDefns.getImplementationTypes().get(count);
            extensionProcessor.resolve(implType, resolver);
        }
    }
    
    public QName getArtifactType() {
        return SCA_DEFINITIONS_QNAME;
    }
    
    public Class<SCADefinitions> getModelType() {
        return SCADefinitions.class;
    }

    public ModelResolver getDefinitionsResolver() {
        return definitionsResolver;
    }

    public void setDefinitionsResolver(ModelResolver definitionsResolver) {
        this.definitionsResolver = definitionsResolver;
    }
}
