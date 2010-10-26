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

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.definitions.DefinitionsFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ExternalAttachment;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.Qualifier;

/**
 * Processor for SCA Definitions
 *
 * @version $Rev$ $Date$
 */
public class DefinitionsProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<Definitions> {
    private ExtensionPointRegistry registry;
    private StAXArtifactProcessorExtensionPoint processors;
    private StAXArtifactProcessor<Object> extensionProcessor;
    private DefinitionsFactory definitionsFactory;
    private PolicyFactory policyFactory;

    public static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    public static final String BINDING = "binding";
    public static final String IMPLEMENTATION = "implementation";
    public static final String DEFINITIONS = "definitions";
    public static final QName DEFINITIONS_QNAME = new QName(SCA11_NS, DEFINITIONS);
    public static final String TARGET_NAMESPACE = "targetNamespace";
    public static final String NAME = "name";

    public DefinitionsProcessor(ExtensionPointRegistry registry,
                                StAXArtifactProcessor<Object> extensionProcessor) {
        this.registry = registry;
        this.extensionProcessor = extensionProcessor;
        this.processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        FactoryExtensionPoint factoryExtensionPoint = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.definitionsFactory = factoryExtensionPoint.getFactory(DefinitionsFactory.class);
        this.policyFactory = factoryExtensionPoint.getFactory(PolicyFactory.class);        
    }

    public Definitions read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        QName name = null;
        Definitions definitions = null;
        String targetNamespace = null;

        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT: {
                    name = reader.getName();
                    if (DEFINITIONS_QNAME.equals(name)) {
                        definitions = definitionsFactory.createDefinitions();
                        targetNamespace = getURIString(reader, TARGET_NAMESPACE);
                        definitions.setTargetNamespace(targetNamespace);
                    } else {
                        Object extension = extensionProcessor.read(reader, context);
                        if (extension != null) {
                            if (extension instanceof Intent) {
                                Intent intent = (Intent)extension;
                                intent.setName(new QName(targetNamespace, intent.getName().getLocalPart()));
                                definitions.getIntents().add(intent);
                                for (Intent i : intent.getQualifiedIntents()) {
                                    i.setName(new QName(targetNamespace, i.getName().getLocalPart()));
                                }
                            } else if (extension instanceof PolicySet) {
                                PolicySet policySet = (PolicySet)extension;
                                policySet.setName(new QName(targetNamespace, policySet.getName().getLocalPart()));
                                definitions.getPolicySets().add(policySet);
                            } else if (extension instanceof Binding) {
                                Binding binding = (Binding)extension;
                                definitions.getBindings().add(binding);
                            } else if (extension instanceof BindingType) {
                                definitions.getBindingTypes().add((BindingType)extension);
                            } else if (extension instanceof ImplementationType) {
                                definitions.getImplementationTypes().add((ImplementationType)extension);
                            } else if (extension instanceof ExternalAttachment) {
                            	definitions.getExternalAttachments().add((ExternalAttachment)extension);
                            }
                        }
                        break;
                    }
                }

                case XMLStreamConstants.CHARACTERS:
                    break;

                case END_ELEMENT:
                    name = reader.getName();
                    if (DEFINITIONS_QNAME.equals(name)) {
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

    public void write(Definitions definitions, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
        XMLStreamException {

        writeStartDocument(writer, SCA11_NS, DEFINITIONS, new XAttr(TARGET_NAMESPACE, definitions.getTargetNamespace()));

        for (Intent policyIntent : definitions.getIntents()) {
            extensionProcessor.write(policyIntent, writer, context);
        }

        for (PolicySet policySet : definitions.getPolicySets()) {
            extensionProcessor.write(policySet, writer, context);
        }
        
        for (Binding binding : definitions.getBindings()) {
            extensionProcessor.write(binding, writer, context);
        }

        for (BindingType bindingType : definitions.getBindingTypes()) {
            extensionProcessor.write(bindingType, writer, context);
        }

        for (ImplementationType implType : definitions.getImplementationTypes()) {
            extensionProcessor.write(implType, writer, context);
        }

        writeEndDocument(writer);
    }

    public void resolve(Definitions scaDefns, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        
        // start by adding all of the top level artifacts into the resolver as there
        // are many cross artifact references in a definitions file and we don't want
        // to be dependent on the order things appear

        List<Intent> intents = new ArrayList<Intent>();
        List<PolicySet> policySets = new ArrayList<PolicySet>();
        List<PolicySet> referredPolicySets = new ArrayList<PolicySet>();

        for (Intent intent : scaDefns.getIntents()) {
            intents.add(intent);
            resolver.addModel(intent, context);
            for (Intent i : intent.getQualifiedIntents()) {
                intents.add(i);
                resolver.addModel(i, context);
            }
        }

        for (PolicySet policySet : scaDefns.getPolicySets()) {
            if (policySet.getReferencedPolicySets().isEmpty()) {
                policySets.add(policySet);
            } else {
                referredPolicySets.add(policySet);
            }

            resolver.addModel(policySet, context);
        }

        for (BindingType bindingType : scaDefns.getBindingTypes()) {
            resolver.addModel(bindingType, context);
        }

        for (ImplementationType implType : scaDefns.getImplementationTypes()) {
            resolver.addModel(implType, context);
        }

        // now resolve everything to ensure that any references between
        // artifacts are satisfied

        for (Intent policyIntent : intents)
            extensionProcessor.resolve(policyIntent, resolver, context);

        for (PolicySet policySet : policySets)
            extensionProcessor.resolve(policySet, resolver, context);

        for (PolicySet policySet : referredPolicySets)
            extensionProcessor.resolve(policySet, resolver, context);
        
        for (Binding binding : scaDefns.getBindings()) {
            extensionProcessor.resolve(binding, resolver, context);
        }
        
        for (BindingType bindingType : scaDefns.getBindingTypes()) {
            extensionProcessor.resolve(bindingType, resolver, context);
            if (processors.getProcessor(bindingType.getType())  == null){
                Monitor.error(context.getMonitor(), 
                              this, 
                              "org.apache.tuscany.sca.definitions.xml.definitions-xml-validation-messages", 
                              "BindingTypeNotFound", 
                              bindingType.getType().toString());
            }
        }

        for (ImplementationType implementationType : scaDefns.getImplementationTypes()) {
            extensionProcessor.resolve(implementationType, resolver, context);
            if (processors.getProcessor(implementationType.getType())  == null){
                Monitor.error(context.getMonitor(), 
                              this, 
                              "org.apache.tuscany.sca.definitions.xml.definitions-xml-validation-messages", 
                              "ImplementationTypeNotFound", 
                              implementationType.getType().toString());
            }
        }
        

        // Flat intentMap structure by creating a policySet for each one
        List<PolicySet> copy = new ArrayList<PolicySet>(scaDefns.getPolicySets());
        for (PolicySet policySet : copy) {
        	// Add PolicySets to model based on qualified intents. The policy builder will be responsible for assigning
        	// the correct policy set. 
        	// For example, ManagedTransactionPolicySet will result in:
        	// ManagedTransactionPolicySet  (original PolicySet, must exist for matching at build time)
        	// ManagedTransactionPolicySet.managedTransaction.global
        	// ManagedTransactionPolicySet.managedTransaction.local
            
            //process intent maps
            for(IntentMap intentMap : policySet.getIntentMaps()) {
                for(Qualifier qualifier : intentMap.getQualifiers()) {
           //         remove = true;
                    
                    PolicySet qualifiedPolicySet = policyFactory.createPolicySet();
                    qualifiedPolicySet.setAppliesTo(policySet.getAppliesTo());
                    qualifiedPolicySet.setAppliesToXPathExpression(policySet.getAttachToXPathExpression());
                    qualifiedPolicySet.setAttachTo(policySet.getAttachTo());
                    qualifiedPolicySet.setAttachToXPathExpression(policySet.getAttachToXPathExpression());

                    String qualifiedLocalName = policySet.getName().getLocalPart() + "." + qualifier.getIntent().getName().getLocalPart();
                    qualifiedPolicySet.setName(new QName(policySet.getName().getNamespaceURI(), qualifiedLocalName));
                    qualifiedPolicySet.getProvidedIntents().clear();
                    qualifiedPolicySet.getProvidedIntents().add(qualifier.getIntent());
                    qualifiedPolicySet.getPolicies().clear();
                    qualifiedPolicySet.getPolicies().addAll(qualifier.getPolicies());

                    scaDefns.getPolicySets().add(qualifiedPolicySet);
                }
            }
            
//            if(remove) {
//                scaDefns.getPolicySets().remove(policySet);
//            }
        }
    }

    public QName getArtifactType() {
        return DEFINITIONS_QNAME;
    }

    public Class<Definitions> getModelType() {
        return Definitions.class;
    }

}
