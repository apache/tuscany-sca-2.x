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

package org.apache.tuscany.sca.definitions.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.definitions.impl.SCADefinitionsImpl;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.apache.tuscany.sca.policy.QualifiedIntent;

/**
 * Processor for SCA Definitions
 *
 * @version $Rev$ $Date$
 */
public class SCADefinitionsProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<SCADefinitions> {
    
    private StAXArtifactProcessor<Object> extensionProcessor;
    private Monitor monitor;
    
    public static final String BINDING = "binding";
    public static final String IMPLEMENTATION = "implementation";
    public static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    public static final String SCA_DEFINITIONS = "definitions";
    public static final QName SCA_DEFINITIONS_QNAME = new QName(SCA10_NS, SCA_DEFINITIONS);
    public static final String TARGET_NAMESPACE = "targetNamespace";
    public static final String NAME = "name";
    
    /**
     * Construct a new definitions processor
     * @param extensionProcessor 
     * @param modelResolver 
     */
    public SCADefinitionsProcessor(StAXArtifactProcessor<Object> extensionProcessor,
                              ModelResolver modelResolver, Monitor monitor) {
        this.extensionProcessor = extensionProcessor;
        this.monitor = monitor;
    }
    
    public SCADefinitionsProcessor(ExtensionPointRegistry extensionPoints, 
    		                       StAXArtifactProcessor<Object> extensionProcessor,
    		                       Monitor monitor) {
        this.extensionProcessor = extensionProcessor;
        this.monitor = monitor;
    }

    public SCADefinitions read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        QName name = null;
        SCADefinitions definitions = null;
        String targetNamespace = null;

        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT: {
                    name = reader.getName();
                    if ( SCA_DEFINITIONS_QNAME.equals(name)) {
                        definitions = new SCADefinitionsImpl();
                        targetNamespace = reader.getAttributeValue(null, TARGET_NAMESPACE);
                        definitions.setTargetNamespace(targetNamespace);
                    } else {
                        Object extension = extensionProcessor.read(reader);
                        if (extension != null) {
                            if ( extension instanceof Intent ) {
                                Intent intent = (Intent)extension;
                                intent.setName(new QName(targetNamespace, 
                                                                      intent.getName().getLocalPart()));
                                if ( intent instanceof QualifiedIntent ) {
                                    QualifiedIntent qualifiedIntent = (QualifiedIntent)intent;
                                    qualifiedIntent.getQualifiableIntent().
                                            setName(new QName(targetNamespace,
                                                              qualifiedIntent.getQualifiableIntent().getName().getLocalPart()));
                                }
                                
                                // FIXME: Workaround for TUSCANY-2499
                                intent.setUnresolved(false);
                                
                                definitions.getPolicyIntents().add(intent);
                            } else if ( extension instanceof PolicySet ) {
                                PolicySet policySet = (PolicySet)extension;
                                policySet.setName(new QName(targetNamespace, 
                                                            policySet.getName().getLocalPart()));
                                definitions.getPolicySets().add(policySet);
                            } else if ( extension instanceof IntentAttachPointType ) {
                                IntentAttachPointType type = (IntentAttachPointType)extension;
                                if ( type.getName().getLocalPart().startsWith(BINDING)) {
                                    definitions.getBindingTypes().add((IntentAttachPointType)extension);
                                } else if ( type.getName().getLocalPart().startsWith(IMPLEMENTATION)) {
                                    definitions.getImplementationTypes().add((IntentAttachPointType)extension);
                                }
                            } 
                        }
                        break;
                    }
                }

                case XMLStreamConstants.CHARACTERS:
                    break;

                case END_ELEMENT:
                    name = reader.getName();
                    if ( SCA_DEFINITIONS_QNAME.equals(name)) {
                        return definitions;
                    }
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
                               new XAttr(TARGET_NAMESPACE, definitions.getTargetNamespace()));
    
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
        // start by adding all of the top level artifacts into the resolver as there
        // are many cross artifact references in a definitions file and we don't want
        // to be dependent on the order things appear
    	
    	List<Intent> simpleIntents = new ArrayList<Intent>();
    	List<ProfileIntent> profileIntents = new ArrayList<ProfileIntent>();
    	List<QualifiedIntent> qualifiedIntents = new ArrayList<QualifiedIntent>();
        List<PolicySet> simplePolicySets = new ArrayList<PolicySet>();
        List<PolicySet> referredPolicySets = new ArrayList<PolicySet>();
        
        for (Intent policyIntent : scaDefns.getPolicyIntents()) {
        	if (policyIntent instanceof ProfileIntent)
        	    profileIntents.add((ProfileIntent)policyIntent);
        	else if (policyIntent instanceof QualifiedIntent)
        	    qualifiedIntents.add((QualifiedIntent)policyIntent);
        	else simpleIntents.add(policyIntent);
        	
        	resolver.addModel(policyIntent);
        }        
        
        for (PolicySet policySet : scaDefns.getPolicySets()) {
            if (policySet.getReferencedPolicySets().isEmpty())
                simplePolicySets.add(policySet);
            else referredPolicySets.add(policySet);
            
            resolver.addModel(policySet);
        }
        
        for (IntentAttachPointType bindingType : scaDefns.getBindingTypes()) {
            resolver.addModel(bindingType);
        }
        
        for (IntentAttachPointType implType : scaDefns.getImplementationTypes()) {
            resolver.addModel(implType);
        }
        
        // now resolve everything to ensure that any references between
        // artifacts are satisfied
        
        for (Intent policyIntent : simpleIntents)
            extensionProcessor.resolve(policyIntent, resolver);
        
        for (ProfileIntent policyIntent : profileIntents)
            extensionProcessor.resolve(policyIntent, resolver);
        
        for (QualifiedIntent policyIntent : qualifiedIntents)
            extensionProcessor.resolve(policyIntent, resolver);
        
        for (PolicySet policySet : simplePolicySets)
            extensionProcessor.resolve(policySet, resolver);
        
        for (PolicySet policySet : referredPolicySets) 
            extensionProcessor.resolve(policySet, resolver);        
        
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

}
