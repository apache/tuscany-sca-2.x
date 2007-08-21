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
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.Policy;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;


/* 
 * Processor for handling xml models of PolicySet definitions
 */
public class PolicySetProcessor implements StAXArtifactProcessor<PolicySet>, PolicyConstants {

    private PolicyFactory policyFactory;
    protected StAXArtifactProcessor<Object> extensionProcessor;
    

    public PolicySetProcessor(PolicyFactory policyFactory, StAXArtifactProcessor<Object> extensionProcessor) {
        this.policyFactory = policyFactory;
        this.extensionProcessor = extensionProcessor;
    }

    public PolicySet read(XMLStreamReader reader) throws ContributionReadException {
        try {
            PolicySet policySet = policyFactory.createPolicySet();
            policySet.setName(getQName(reader, NAME));
            policySet.setAppliesTo(reader.getAttributeValue(null, APPLIES_TO));
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
                                throw new ContributionReadException("Intent Map provides for Intent not spcified as provided by parent PolicySet - " +policySet.getName());
                            }
                        } else if ( POLICY_SET_REFERENCE_QNAME.equals(name) )  {
                            PolicySet referredPolicySet = policyFactory.createPolicySet();
                            referredPolicySet.setName(getQName(reader, NAME));
                            policySet.getReferencedPolicySets().add(referredPolicySet);
                        } else {
                            Object extension = extensionProcessor.read(reader);
                            if ( extension instanceof Policy ) {
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
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    
    public void readIntentMap(XMLStreamReader reader, PolicySet policySet, Intent mappedIntent) throws ContributionReadException {
        QName name = reader.getName();
        Map<Intent, List<Policy>> mappedPolicies = policySet.getMappedPolicies();
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
                                    throw new ContributionReadException("Intent provided by IntentMap " + 
                                                                    providedIntent + " does not match parent qualifier " + qualifierName +
                                                                    " in policyset - " + policySet);
                                }
                            } else {
                                Object extension = extensionProcessor.read(reader);
                                if ( extension instanceof Policy ) {
                                    List<Policy> policyList = mappedPolicies.get(qualifiedIntent);
                                    if ( policyList == null ) {
                                        policyList = new ArrayList<Policy>();
                                        mappedPolicies.put(qualifiedIntent, policyList);
                                        
                                        if (qualifierName.equals(defaultQualifier)) {
                                            mappedPolicies.put(mappedIntent, policyList);
                                        }
                                    }
                                    policyList.add((Policy)extension);
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
                                    List<Policy> policyList = mappedPolicies.get(defaultQualifiedIntent);
                                    if ( policyList != null ) {
                                        mappedPolicies.put(mappedIntent, policyList);
                                    } else {
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
                throw new ContributionReadException(e);
            }
        }
    }
    
    public void write(PolicySet policySet, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <sca:policySet>
            writer.writeStartElement(SCA10_NS, POLICY_SET);
            writer.writeNamespace(policySet.getName().getPrefix(), policySet.getName().getNamespaceURI());
            writer.writeAttribute(NAME, 
                                  policySet.getName().getPrefix() + COLON + policySet.getName().getLocalPart());
            writer.writeAttribute(APPLIES_TO, policySet.getAppliesTo());
            
            writeProvidedIntents(policySet, writer);
            
            
            writer.writeEndElement();
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    protected void readProvidedIntents(PolicySet policySet, XMLStreamReader reader) {
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
    
    protected void writeProvidedIntents(PolicySet policySet, XMLStreamWriter writer) throws XMLStreamException {
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

    //FIXME This method is never used
//    private void resolveProvidedIntents(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
//        boolean isUnresolved = false;
//        if (policySet != null && policySet.isUnresolved()) {
//            //resolve all provided intents
//            List<Intent> providedIntents = new ArrayList<Intent>(); 
//            for (Intent providedIntent : policySet.getProvidedIntents()) {
//                if ( providedIntent.isUnresolved() ) {
//                    //policyIntent.getRequiredIntents().remove(requiredIntent);
//                    providedIntent = resolver.resolveModel(Intent.class, providedIntent);
//                    providedIntents.add(providedIntent);
//                    if (providedIntent.isUnresolved()) {
//                        isUnresolved = true;
//                    }
//                }
//            }
//            policySet.getProvidedIntents().clear();
//            policySet.getProvidedIntents().addAll(providedIntents);
//        }
//        policySet.setUnresolved(isUnresolved);
//    }

    //FIXME This method is never used
//   private void resolveIntentsInMappedPolicies(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
//       Map<Intent, List<Policy>> mappedPolicies = new Hashtable<Intent, List<Policy>>();   
//       boolean isUnresolved = false;
//       for ( Intent mappedIntent : policySet.getMappedPolicies().keySet() ) {
//           if ( mappedIntent.isUnresolved() ) {
//               //policyIntent.getRequiredIntents().remove(requiredIntent);
//               mappedIntent = resolver.resolveModel(Intent.class, mappedIntent);
//               mappedPolicies.put(mappedIntent, policySet.getMappedPolicies().get(mappedIntent));
//               if (mappedIntent.isUnresolved()) {
//                   isUnresolved = true;
//               }
//           }
//       }
//       
//       policySet.getMappedPolicies().clear();
//       policySet.getMappedPolicies().putAll(mappedPolicies);
//       policySet.setUnresolved(isUnresolved);
//   }
  
   private void resolvePolicies(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
       boolean unresolved = false;
       for ( Object o : policySet.getPolicies() ) {
           extensionProcessor.resolve(o, resolver);
           if ( o instanceof Policy && ((Policy)o).isUnresolved() ) {
              unresolved = true;
           }
       }
       policySet.setUnresolved(unresolved);
   }
   
   public void resolve(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
       if ( policySet.isUnresolved() ) {
           //resolve the policy attachments
           resolvePolicies(policySet, resolver);
            
           if ( !policySet.isUnresolved() ) {
                resolver.addModel(policySet);
           }
       }
    }   
    
    public QName getArtifactType() {
        return POLICY_SET_QNAME;
    }
    
    public Class<PolicySet> getModelType() {
        return PolicySet.class;
    }
    
    protected QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }
    
    protected QName getQName(XMLStreamReader reader, String name) {
        String qname = reader.getAttributeValue(null, name);
        return getQNameValue(reader, qname);
    }
    

    protected String getString(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name);
    }
}
