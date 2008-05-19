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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
import org.apache.tuscany.sca.policy.PolicySet;


/**
 * Processor for handling XML models of PolicySet definitions
 *
 * @version $Rev$ $Date$
 */
public class PolicySetProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<PolicySet>, PolicyConstants {

    private PolicyFactory policyFactory;
    private StAXArtifactProcessor<Object> extensionProcessor;
    private XPathFactory xpathFactory = XPathFactory.newInstance();
    private Monitor monitor;
    
    public PolicySetProcessor(ModelFactoryExtensionPoint modelFactories, 
    						  StAXArtifactProcessor<Object> extensionProcessor,
    						  Monitor monitor) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.extensionProcessor = extensionProcessor;
        this.monitor = monitor;
    }
    
    public PolicySetProcessor(PolicyFactory policyFactory, 
    						  StAXArtifactProcessor<Object> extensionProcessor,
    						  Monitor monitor) {
        this.policyFactory = policyFactory;
        this.extensionProcessor = extensionProcessor;
        this.monitor = monitor;
    }
    
    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
     */
     private void error(String message, Object model, Exception ex) {
    	 if (monitor != null) {
    		 Problem problem = new ProblemImpl(this.getClass().getName(), "policy-xml-validation-messages", Severity.ERROR, model, message, ex);
    	     monitor.problem(problem);
    	 }        
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

    public PolicySet read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        String policySetName = reader.getAttributeValue(null, NAME);
        
        PolicySet policySet = policyFactory.createPolicySet();
        policySet.setName(new QName(policySetName));
        String appliesTo = reader.getAttributeValue(null, APPLIES_TO);
        String alwaysAppliesTo = reader.getAttributeValue(TUSCANY_NS, ALWAYS_APPLIES_TO);
        
        //TODO: with 1.0 version of specs the applies to xpath is given related to the immediate
        //parent whereas the runtime evaluates the xpath aginst the composite element.  What the runtime
        //is doing is what the future version of the specs could be tending towards.  When that happens
        //this 'if' must be deleted
        if ( appliesTo != null && !appliesTo.startsWith("/") ) {
            appliesTo = "//" + appliesTo;
        }
        
        if ( alwaysAppliesTo != null && !alwaysAppliesTo.startsWith("/") ) {
            alwaysAppliesTo = "//" + alwaysAppliesTo;
        }
        
        policySet.setAppliesTo(appliesTo);
        policySet.setAlwaysAppliesTo(alwaysAppliesTo);

        XPath path = xpathFactory.newXPath(); 
        path.setNamespaceContext(reader.getNamespaceContext());
        try {
            if (appliesTo != null) {
                policySet.setAppliesToXPathExpression(path.compile(appliesTo));
            }
            if (alwaysAppliesTo != null) {
                policySet.setAlwaysAppliesToXPathExpression(path.compile(alwaysAppliesTo));
            }
        } catch (XPathExpressionException e) {
        	ContributionReadException ce = new ContributionReadException(e);
        	error("ContributionReadException", policySet, ce);
            throw ce;
        }  
        
        readProvidedIntents(policySet, reader);
        
        int event = reader.getEventType();
        QName name = null;
        reader.next();
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT : {
                    name = reader.getName();
                    if ( POLICY_INTENT_MAP_QNAME.equals(name) ) {
                        Intent mappedIntent = policyFactory.createIntent();
                        mappedIntent.setName(getQName(reader, PROVIDES));
                        if ( policySet.getProvidedIntents().contains(mappedIntent) ) {
                            readIntentMap(reader, policySet, mappedIntent);
                        } else {
                        	error("IntentNotSpecified", policySet, policySetName);
                            throw new ContributionReadException("Intent Map provides for Intent not specified as provided by parent PolicySet - " + policySetName);
                        }
                    } else if ( POLICY_SET_REFERENCE_QNAME.equals(name) )  {
                        PolicySet referredPolicySet = policyFactory.createPolicySet();
                        referredPolicySet.setName(getQName(reader, NAME));
                        policySet.getReferencedPolicySets().add(referredPolicySet);
                    } /*else if ( WS_POLICY_QNAME.equals(name) )  {
                        OMElement policyElement = loadElement(reader);
                        org.apache.neethi.Policy wsPolicy = PolicyEngine.getPolicy(policyElement);
                        policySet.getPolicies().add(wsPolicy);
                    } */ else {
                        Object extension = extensionProcessor.read(reader);
                        if ( extension != null ) {
                            policySet.getPolicies().add(extension);
                        }
                    }
                    break;
                }
            }
            if ( event == END_ELEMENT ) {
                if ( POLICY_SET_QNAME.equals(reader.getName()) ) {
                    break;
                } 
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return policySet;
    }
    
    
    public void readIntentMap(XMLStreamReader reader, PolicySet policySet, Intent mappedIntent) throws ContributionReadException {
        QName name = reader.getName();
        Map<Intent, List<Object>> mappedPolicies = policySet.getMappedPolicies();
        if ( POLICY_INTENT_MAP_QNAME.equals(name) ) {
            //Intent mappedIntent = policyFactory.createIntent();
            //mappedIntent.setName(getQName(reader, PROVIDES));
            String defaultQualifier = getString(reader, DEFAULT);
            
            String qualifierName = null;
            String qualfiedIntentName = null;
            Intent qualifiedIntent = null;
        
            int event = reader.getEventType();
            try {
                reader.next();
                while (reader.hasNext()) {
                    event = reader.getEventType();
                    switch (event) {
                        case START_ELEMENT : {
                            name = reader.getName();
                            if ( POLICY_INTENT_MAP_QUALIFIER_QNAME.equals(name)) {
                                qualifierName = getString(reader, NAME);
                                qualfiedIntentName = mappedIntent.getName().getLocalPart() + 
                                                            QUALIFIER + qualifierName;
                                qualifiedIntent = policyFactory.createIntent();
                                qualifiedIntent.setName(new QName(mappedIntent.getName().getNamespaceURI(),
                                                                  qualfiedIntentName)); 
                            } else if ( POLICY_INTENT_MAP_QNAME.equals(name) ) {
                                QName providedIntent = getQName(reader, PROVIDES);
                                if ( qualifierName.equals(providedIntent.getLocalPart()) ) {
                                    readIntentMap(reader, policySet, qualifiedIntent);
                                } else {
                                	error("IntentMapDoesNotMatch", providedIntent, providedIntent, qualifierName, policySet);
                                    throw new ContributionReadException("Intent provided by IntentMap " + 
                                                                    providedIntent + " does not match parent qualifier " + qualifierName +
                                                                    " in policyset - " + policySet);
                                }
                            }/* else if ( WS_POLICY_QNAME.equals(name) )  {
                                OMElement policyElement = loadElement(reader);
                                Policy wsPolicy = PolicyEngine.getPolicy(policyElement);
                                policySet.getPolicies().add(wsPolicy);
                                
                                List<Object> policyList = mappedPolicies.get(qualifiedIntent);
                                if ( policyList == null ) {
                                    policyList = new ArrayList<Object>();
                                    mappedPolicies.put(qualifiedIntent, policyList);
                                    
                                    if (qualifierName.equals(defaultQualifier)) {
                                        mappedPolicies.put(mappedIntent, policyList);
                                    }
                                }
                                policyList.add((Policy)wsPolicy);
                            }*/ else {
                                Object extension = extensionProcessor.read(reader);
                                if ( extension != null ) {
                                    List<Object> policyList = mappedPolicies.get(qualifiedIntent);
                                    if ( policyList == null ) {
                                        policyList = new ArrayList<Object>();
                                        mappedPolicies.put(qualifiedIntent, policyList);
                                        
                                        if (qualifierName.equals(defaultQualifier)) {
                                            mappedPolicies.put(mappedIntent, policyList);
                                        }
                                    }
                                    policyList.add(extension);
                                }
                            }
                            break;
                        }
                        case END_ELEMENT : {
                            if ( POLICY_INTENT_MAP_QNAME.equals(reader.getName()) ) {
                                if ( defaultQualifier != null ) {
                                    String qualifiedIntentName = mappedIntent.getName().getLocalPart() + QUALIFIER + defaultQualifier;
                                    Intent defaultQualifiedIntent = policyFactory.createIntent();
                                    defaultQualifiedIntent.setName(new QName(mappedIntent.getName().getNamespaceURI(),
                                                                             qualifiedIntentName));
                                    List<Object> policyList = mappedPolicies.get(defaultQualifiedIntent);
                                    if ( policyList != null ) {
                                        mappedPolicies.put(mappedIntent, policyList);
                                    } else {
                                    	error("UnableToMapPolicies", mappedPolicies, mappedIntent, policySet);
                                        throw new ContributionReadException("Unable to map policies for default qualifier in IntentMap for - " +
                                                                            mappedIntent + " in policy set - " + policySet);
                                    }
                                    defaultQualifier = null;
                                }
                            } 
                            break;
                        }
                    }
                    if ( event == END_ELEMENT && POLICY_INTENT_MAP_QNAME.equals(reader.getName()) ) {
                        break;
                    }
                    //Read the next element
                    if (reader.hasNext()) {
                        reader.next();
                    }
                }
            }  catch (XMLStreamException e) {
            	ContributionReadException ce = new ContributionReadException(e);
            	error("ContributionReadException", reader, ce);
                throw ce;
            }
        }
    }
    
    public void write(PolicySet policySet, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        // Write an <sca:policySet>
        writer.writeStartElement(SCA10_NS, POLICY_SET);
        writer.writeNamespace(policySet.getName().getPrefix(), policySet.getName().getNamespaceURI());
        writer.writeAttribute(NAME, 
                              policySet.getName().getPrefix() + COLON + policySet.getName().getLocalPart());
        writer.writeAttribute(APPLIES_TO, policySet.getAppliesTo());
        writer.writeAttribute(TUSCANY_NS, ALWAYS_APPLIES_TO, policySet.getAlwaysAppliesTo());
        
        writeProvidedIntents(policySet, writer);
        
        writer.writeEndElement();
    }
    
    private void readProvidedIntents(PolicySet policySet, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, PROVIDES);
        if (value != null) {
            List<Intent> providedIntents = policySet.getProvidedIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                providedIntents.add(intent);
            }
        }
    }
    
    private void writeProvidedIntents(PolicySet policySet, XMLStreamWriter writer) throws XMLStreamException {
        if (policySet.getProvidedIntents() != null && 
            policySet.getProvidedIntents().size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Intent providedIntents : policySet.getProvidedIntents()) {
                sb.append(providedIntents.getName());
                sb.append(" ");
            }
            writer.writeAttribute(PolicyConstants.PROVIDES, sb.toString());
        }
    }

   private void resolvePolicies(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
       boolean unresolved = false;
       for ( Object o : policySet.getPolicies() ) {
           extensionProcessor.resolve(o, resolver);
           /*if ( o instanceof Policy && ((Policy)o).isUnresolved() ) {
              unresolved = true;
           }*/
       }
       policySet.setUnresolved(unresolved);
   }
   
   
    
    public QName getArtifactType() {
        return POLICY_SET_QNAME;
    }
    
    public Class<PolicySet> getModelType() {
        return PolicySet.class;
    }
    
    private void resolveProvidedIntents(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
        if (policySet != null) {
            //resolve all provided intents
            List<Intent> providedIntents = new ArrayList<Intent>();
            for (Intent providedIntent : policySet.getProvidedIntents()) {
                if (providedIntent.isUnresolved()) {
                    Intent resolvedProvidedIntent = resolver.resolveModel(Intent.class, providedIntent);
                    if (resolvedProvidedIntent != null) {
                        providedIntents.add(resolvedProvidedIntent);
                    } else {
                    	error("ProvidedIntentNotFound", policySet, providedIntent, policySet);
                        throw new ContributionResolveException("Provided Intent - " + providedIntent
                            + " not found for PolicySet "
                            + policySet);

                    }
                } else {
                    providedIntents.add(providedIntent);
                }
            }
            policySet.getProvidedIntents().clear();
            policySet.getProvidedIntents().addAll(providedIntents);
        }
     }
    
    private void resolveIntentsInMappedPolicies(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
        Map<Intent, List<Object>> mappedPolicies = new Hashtable<Intent, List<Object>>();
        for (Map.Entry<Intent, List<Object>> entry : policySet.getMappedPolicies().entrySet()) {
            Intent mappedIntent = entry.getKey();
            if (mappedIntent.isUnresolved()) {
                Intent resolvedMappedIntent = resolver.resolveModel(Intent.class, mappedIntent);
    
                if (resolvedMappedIntent != null) {
                    mappedPolicies.put(resolvedMappedIntent, entry.getValue());
                } else {
                	error("MappedIntentNotFound", policySet, mappedIntent, policySet);
                    throw new ContributionResolveException("Mapped Intent - " + mappedIntent
                        + " not found for PolicySet "
                        + policySet);
    
                }
            } else {
                mappedPolicies.put(mappedIntent, entry.getValue());
            }
        }

        policySet.getMappedPolicies().clear();
        policySet.getMappedPolicies().putAll(mappedPolicies);
    }
    
    private void resolveReferredPolicySets(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
    
        List<PolicySet> referredPolicySets = new ArrayList<PolicySet>();
        for (PolicySet referredPolicySet : policySet.getReferencedPolicySets()) {
            if (referredPolicySet.isUnresolved()) {
                PolicySet resolvedReferredPolicySet = resolver.resolveModel(PolicySet.class, referredPolicySet);
                if (resolvedReferredPolicySet != null) {
                    referredPolicySets.add(resolvedReferredPolicySet);
                } else {
                	error("ReferredPolicySetNotFound", policySet, referredPolicySet, policySet);
                    throw new ContributionResolveException("Referred PolicySet - " + referredPolicySet
                        + "not found for PolicySet - "
                        + policySet);
                }
            } else {
                referredPolicySets.add(referredPolicySet);
            }
        }
        policySet.getReferencedPolicySets().clear();
        policySet.getReferencedPolicySets().addAll(referredPolicySets);
    }
    
    private void includeReferredPolicySets(PolicySet policySet, PolicySet referredPolicySet) {
        for (PolicySet furtherReferredPolicySet : referredPolicySet.getReferencedPolicySets()) {
            includeReferredPolicySets(referredPolicySet, furtherReferredPolicySet);
        }
        policySet.getPolicies().addAll(referredPolicySet.getPolicies());
        policySet.getMappedPolicies().putAll(referredPolicySet.getMappedPolicies());
    }
    
    public void resolve(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
        resolveProvidedIntents(policySet, resolver);
        resolveIntentsInMappedPolicies(policySet, resolver);
        resolveReferredPolicySets(policySet, resolver);
        
        for (PolicySet referredPolicySet : policySet.getReferencedPolicySets()) {
            includeReferredPolicySets(policySet, referredPolicySet);
        }
        
        if ( policySet.isUnresolved() ) {
            //resolve the policy attachments
            resolvePolicies(policySet, resolver);
             
            if ( !policySet.isUnresolved() ) {
                 resolver.addModel(policySet);
            }
        }
     }   
 }
