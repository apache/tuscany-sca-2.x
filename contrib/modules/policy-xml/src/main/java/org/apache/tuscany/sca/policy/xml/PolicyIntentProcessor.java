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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.apache.tuscany.sca.policy.QualifiedIntent;

/**
 * Processor for handling XML models of PolicyIntent definitions
 *
 * @version $Rev$ $Date$
 */
abstract class PolicyIntentProcessor<T extends Intent> extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<T>, PolicyConstants {

    private PolicyFactory policyFactory;
    private Monitor monitor;

    public PolicyIntentProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.monitor = monitor;
    }
    
    public PolicyIntentProcessor(PolicyFactory policyFactory, Monitor monitor) {
        this.policyFactory = policyFactory;
        this.monitor = monitor;
    }
    
    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
    	 if (monitor != null) {
    		 Problem problem = new ProblemImpl(this.getClass().getName(), "policy-xml-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
    	     monitor.problem(problem);
    	 }        
    }

    public T read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        Intent policyIntent = null;
        String policyIntentName = reader.getAttributeValue(null, NAME);
        if (policyIntentName == null) {
            error("IntentNameMissing", reader);
            return (T)policyIntent;
        }
        
        // Read an <sca:intent>
        if (reader.getAttributeValue(null, REQUIRES) != null) {
            policyIntent = policyFactory.createProfileIntent();
        } else if ( policyIntentName != null && policyIntentName.indexOf(QUALIFIER) != -1) {
            policyIntent = policyFactory.createQualifiedIntent();
            
            int qualifierIndex = policyIntentName.lastIndexOf(QUALIFIER);
            Intent qualifiableIntent = policyFactory.createIntent();
            qualifiableIntent.setUnresolved(true);
            qualifiableIntent.setName(new QName(policyIntentName.substring(0, qualifierIndex)));
            
            ((QualifiedIntent)policyIntent).setQualifiableIntent(qualifiableIntent);
        } else {
            policyIntent = policyFactory.createIntent();
        }
        policyIntent.setName(new QName(policyIntentName));
        
        if ( policyIntent instanceof ProfileIntent ) {
            readRequiredIntents((ProfileIntent)policyIntent, reader);
        }
        else {
            readExcludedIntents(policyIntent, reader);
        }
        
        readConstrainedArtifacts(policyIntent, reader);

        int event = reader.getEventType();
        QName name = null;
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT : {
                    name = reader.getName();
                    if (DESCRIPTION_QNAME.equals(name)) {
                        policyIntent.setDescription(reader.getElementText());
                    }
                    break;
                }
            }
            if (event == END_ELEMENT && POLICY_INTENT_QNAME.equals(reader.getName())) {
                break;
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return (T)policyIntent;
    }
    
    public void write(T policyIntent, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        // Write an <sca:intent>
        writer.writeStartElement(PolicyConstants.SCA10_NS, INTENT);
        writer.writeNamespace(policyIntent.getName().getPrefix(), policyIntent.getName().getNamespaceURI());
        writer.writeAttribute(PolicyConstants.NAME, 
                              policyIntent.getName().getPrefix() + COLON + policyIntent.getName().getLocalPart());
        if (policyIntent instanceof ProfileIntent) {
            ProfileIntent profileIntent = (ProfileIntent)policyIntent;
            if (profileIntent.getRequiredIntents() != null && 
                profileIntent.getRequiredIntents().size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (Intent requiredIntents : profileIntent.getRequiredIntents()) {
                    sb.append(requiredIntents.getName());
                    sb.append(" ");
                }
                writer.writeAttribute(PolicyConstants.REQUIRES, sb.toString());
            }
        }
        else {
            if (policyIntent.getExcludedIntents() != null && 
                policyIntent.getExcludedIntents().size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (Intent excludedIntents : policyIntent.getExcludedIntents()) {
                    sb.append(excludedIntents.getName());
                    sb.append(" ");
                }
                writer.writeAttribute(PolicyConstants.EXCLUDES, sb.toString());
            }
        }
        
        if (!(policyIntent instanceof QualifiedIntent) ) {
            if (policyIntent.getConstrains() != null && 
                policyIntent.getConstrains().size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (QName contrainedArtifact : policyIntent.getConstrains()) {
                    sb.append(contrainedArtifact.toString());
                    sb.append(" ");
                }
                writer.writeAttribute(CONSTRAINS, sb.toString());
            } else {
            	error("ContrainsAttributeMissing", policyIntent, policyIntent.getName());
                //throw new ContributionWriteException("Contrains attribute missing from " +
                                        //"Policy Intent Definition" + policyIntent.getName());
            }
        }
        
        if ( policyIntent.getDescription() != null && policyIntent.getDescription().length() > 0) {
            writer.writeStartElement(PolicyConstants.SCA10_NS, DESCRIPTION);
            writer.writeCData(policyIntent.getDescription());
            writer.writeEndElement();
        }
        
        writer.writeEndElement();
    }

    //FIXME This method is never used
//    private Intent resolveRequiredIntents(ProfileIntent policyIntent, ModelResolver resolver) throws ContributionResolveException {
//        boolean isUnresolved = false;
//        //FIXME: Need to check for cyclic references first i.e an A requiring B and then B requiring A... 
//        if (policyIntent != null && policyIntent.isUnresolved()) {
//            
//            //resolve all required intents
//            List<Intent> requiredIntents = new ArrayList<Intent>(); 
//            for (Intent requiredIntent : policyIntent.getRequiredIntents()) {
//                if ( requiredIntent.isUnresolved() ) {
//                    //policyIntent.getRequiredIntents().remove(requiredIntent);
//                    requiredIntent = resolver.resolveModel(Intent.class, requiredIntent);
//                    requiredIntents.add(requiredIntent);
//                    if (requiredIntent.isUnresolved()) {
//                        isUnresolved = true;
//                    }
//                }
//            }
//            policyIntent.getRequiredIntents().clear();
//            policyIntent.getRequiredIntents().addAll(requiredIntents);
//        }
//        policyIntent.setUnresolved(isUnresolved);
//        
//        return policyIntent;
//    }
    
    //FIXME This method is never used
//    private Intent resolveQualifiableIntent(QualifiedIntent policyIntent, ModelResolver resolver) throws ContributionResolveException {
//        boolean isUnresolved = false;
//
//        if (policyIntent != null && policyIntent.isUnresolved()) {
//            //resolve the qualifiable intent
//            Intent qualifiableIntent = 
//                resolver.resolveModel(Intent.class, policyIntent.getQualifiableIntent());
//            policyIntent.setQualifiableIntent(qualifiableIntent);
//            isUnresolved = qualifiableIntent.isUnresolved();
//        }
//        policyIntent.setUnresolved(isUnresolved);
//        
//        return policyIntent;
//    }
    
    private void resolveContrainedArtifacts(Intent policyIntent, ModelResolver resolver) {
        //FIXME : need to figure out this resolution. 
        policyIntent.setUnresolved(false);
    }
    
    private void resolveProfileIntent(ProfileIntent policyIntent, ModelResolver resolver)
        throws ContributionResolveException {
        // FIXME: Need to check for cyclic references first i.e an A requiring B
        // and then B requiring A...
        if (policyIntent != null) {
            // resolve all required intents
            List<Intent> requiredIntents = new ArrayList<Intent>();
            for (Intent requiredIntent : policyIntent.getRequiredIntents()) {
                if (requiredIntent.isUnresolved()) {
                    Intent resolvedRequiredIntent = resolver.resolveModel(Intent.class, requiredIntent);
                    // At this point, when the required intent is not resolved, it does not mean 
                    // its undeclared, chances are that their dependency are not resolved yet. 
                    // Lets try to resolve them first.
                    if (resolvedRequiredIntent.isUnresolved()) {
                        if (resolvedRequiredIntent instanceof ProfileIntent) {
                            if ((((ProfileIntent)resolvedRequiredIntent).getRequiredIntents()).contains(policyIntent)) {
                                error("CyclicReferenceFound", resolver, requiredIntent, policyIntent);
                                return;
                            }
                            resolveDependent(resolvedRequiredIntent, resolver);
                        }
                    }
                
                    if (!resolvedRequiredIntent.isUnresolved()) {
                        requiredIntents.add(resolvedRequiredIntent);
                    } else {
                    	error("RequiredIntentNotFound", resolver, requiredIntent, policyIntent);
                    	return;
                        //throw new ContributionResolveException("Required Intent - " + requiredIntent
                                                    //+ " not found for ProfileIntent " + policyIntent);
                    }
                } else {
                    requiredIntents.add(requiredIntent);
                }
            }
            policyIntent.getRequiredIntents().clear();
            policyIntent.getRequiredIntents().addAll(requiredIntents);
        }
    }

    private void resolveQualifiedIntent(QualifiedIntent policyIntent, ModelResolver resolver)
        throws ContributionResolveException {
        if (policyIntent != null) {
            //resolve the qualifiable intent
            Intent qualifiableIntent = policyIntent.getQualifiableIntent();
            if (qualifiableIntent.isUnresolved()) {
                Intent resolvedQualifiableIntent = resolver.resolveModel(Intent.class, qualifiableIntent);
                // At this point, when the qualifiable intent is not resolved, it does not mean 
                // its undeclared, chances are that their dependency are not resolved yet. 
                // Lets try to resolve them first.
                if (resolvedQualifiableIntent.isUnresolved()) {
                    if (resolvedQualifiableIntent instanceof QualifiedIntent) {
                        resolveDependent(resolvedQualifiableIntent, resolver);
                    }
                }
                
                if (!resolvedQualifiableIntent.isUnresolved()) {
                    policyIntent.setQualifiableIntent(resolvedQualifiableIntent);
                } else {
                	error("QualifiableIntentNotFound", resolver, qualifiableIntent, policyIntent);
                    //throw new ContributionResolveException("Qualifiable Intent - " + qualifiableIntent
                                                    //+ " not found for QualifiedIntent " + policyIntent);
                }    
            }
        }
    }
    
    public void resolveDependent(Intent policyIntent, ModelResolver resolver) throws ContributionResolveException {
        if (policyIntent instanceof ProfileIntent)
            resolveProfileIntent((ProfileIntent)policyIntent, resolver);
        
        if (policyIntent instanceof QualifiedIntent)
            resolveQualifiedIntent((QualifiedIntent)policyIntent, resolver);
        
        resolveContrainedArtifacts(policyIntent, resolver);
    }
    
    public void resolve(T policyIntent, ModelResolver resolver) throws ContributionResolveException {
        if (policyIntent instanceof ProfileIntent) {
            resolveProfileIntent((ProfileIntent)policyIntent, resolver);
        }
        else {
            resolveExcludedIntents(policyIntent, resolver);
        }

        if (policyIntent instanceof QualifiedIntent) {
            resolveQualifiedIntent((QualifiedIntent)policyIntent, resolver);
        }
        
        resolveContrainedArtifacts(policyIntent, resolver);

        /* This is too late in the processing
        if ( !policyIntent.isUnresolved() ) {
            resolver.addModel(policyIntent);
        }
        */
    }
    
    public QName getArtifactType() {
        return POLICY_INTENT_QNAME;
    }
    
    private void readConstrainedArtifacts(Intent policyIntent, XMLStreamReader reader) throws ContributionReadException {
        String value = reader.getAttributeValue(null, CONSTRAINS);
        if ( policyIntent instanceof QualifiedIntent && value != null) {
        	error("ErrorInPolicyIntentDefinition", policyIntent, policyIntent.getName(), QUALIFIED_INTENT_CONSTRAINS_ERROR);
            //String errorMsg = "Error in PolicyIntent Definition - " + policyIntent.getName() + QUALIFIED_INTENT_CONSTRAINS_ERROR;
            //throw new ContributionReadException(errorMsg);
        } else {
            if (value != null) {
                List<QName> constrainedArtifacts = policyIntent.getConstrains();
                for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                    QName qname = getQNameValue(reader, tokens.nextToken());
                    constrainedArtifacts.add(qname);
                }
            }
        }
    }
    
    private void readRequiredIntents(ProfileIntent policyIntent, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, REQUIRES);
        if (value != null) {
            List<Intent> requiredIntents = policyIntent.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                intent.setUnresolved(true);
                requiredIntents.add(intent);
            }
        }
    }

    private void readExcludedIntents(Intent policyIntent, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, "excludes");
        if (value != null) {
            List<Intent> excludedIntents = policyIntent.getExcludedIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                intent.setUnresolved(true);
                excludedIntents.add(intent);
            }
        }
    }

    private void resolveExcludedIntents(Intent policyIntent, ModelResolver resolver)
        throws ContributionResolveException {
        if (policyIntent != null) {
            // resolve all excluded intents
            List<Intent> excludedIntents = new ArrayList<Intent>();
            for (Intent excludedIntent : policyIntent.getExcludedIntents()) {
                if (excludedIntent.isUnresolved()) {
                    Intent resolvedExcludedIntent = resolver.resolveModel(Intent.class, excludedIntent);                                     
                    if (!resolvedExcludedIntent.isUnresolved()) {
                        excludedIntents.add(resolvedExcludedIntent);
                    } else {
                    	error("ExcludedIntentNotFound", resolver, excludedIntent, policyIntent);
                    	return;
                        //throw new ContributionResolveException("Excluded Intent " + excludedIntent
                                                         //+ " not found for intent " + policyIntent);
                    }
                } else {
                    excludedIntents.add(excludedIntent);
                }
            }
            policyIntent.getExcludedIntents().clear();
            policyIntent.getExcludedIntents().addAll(excludedIntents);
        }
    }

}
