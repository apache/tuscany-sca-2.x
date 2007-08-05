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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.Policy;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.apache.tuscany.sca.policy.QualifiedIntent;
import org.apache.tuscany.sca.policy.SCADefinitions;

/**
 * Provides a concrete implementation for a SCADefinitionsBuilder
 *
 */
public class SCADefinitionsBuilderImpl implements SCADefinitionsBuilder {
    
    public void build(SCADefinitions scaDefns) throws SCADefinitionsBuilderException {
        Map<QName, Intent> definedIntents = new HashMap<QName, Intent>();
        for ( Intent intent : scaDefns.getPolicyIntents() ) {
            definedIntents.put(intent.getName(), intent);
        }
        
        Map<QName, PolicySet> definedPolicySets = new HashMap<QName, PolicySet>();
        for ( PolicySet policySet : scaDefns.getPolicySets() ) {
            definedPolicySets.put(policySet.getName(), policySet);
        }
        buildPolicyIntents(scaDefns, definedIntents);
        buildPolicySets(scaDefns, definedPolicySets, definedIntents);
    }
    
    private void buildPolicyIntents(SCADefinitions scaDefns, Map<QName, Intent> definedIntents) throws SCADefinitionsBuilderException {
        for (Intent policyIntent : scaDefns.getPolicyIntents()) {
            if ( policyIntent instanceof ProfileIntent ) {
                buildProfileIntent((ProfileIntent)policyIntent, definedIntents);
            } 
        
            if ( policyIntent instanceof QualifiedIntent ) {
                buildQualifiedIntent((QualifiedIntent)policyIntent, definedIntents);
            }
        }
    }
    
    private void buildPolicySets(SCADefinitions scaDefns, 
                                 Map<QName, PolicySet> definedPolicySets, 
                                 Map<QName, Intent> definedIntents) throws SCADefinitionsBuilderException {
        
        for ( PolicySet policySet : scaDefns.getPolicySets() ) {
            buildProvidedIntents(policySet, definedIntents);
            buildIntentsInMappedPolicies(policySet, definedIntents);
            buildReferredPolicySets(policySet, definedPolicySets);
        }
        
        for ( PolicySet policySet : scaDefns.getPolicySets() ) {
            for ( PolicySet referredPolicySet : policySet.getReferencedPolicySets() ) {
                includeReferredPolicySets(policySet, referredPolicySet);
            }
        }
    }
    
    private void buildProfileIntent(ProfileIntent policyIntent, Map<QName, Intent> definedIntents) throws SCADefinitionsBuilderException {
        //FIXME: Need to check for cyclic references first i.e an A requiring B and then B requiring A... 
        if (policyIntent != null ) {
            //resolve all required intents
            List<Intent> requiredIntents = new ArrayList<Intent>(); 
            for (Intent requiredIntent : policyIntent.getRequiredIntents()) {
                if ( requiredIntent.isUnresolved() ) {
                    Intent resolvedRequiredIntent = definedIntents.get(requiredIntent.getName());
                    if ( resolvedRequiredIntent != null ) {
                        requiredIntents.add(resolvedRequiredIntent); 
                    } else {
                        throw new SCADefinitionsBuilderException("Required Intent - " + requiredIntent + 
                                                             " not found for ProfileIntent " + policyIntent);
                        
                    }
                }
            }
            policyIntent.getRequiredIntents().clear();
            policyIntent.getRequiredIntents().addAll(requiredIntents);
        }
    }
    
    private void buildQualifiedIntent(QualifiedIntent policyIntent, Map<QName, Intent> definedIntents) throws SCADefinitionsBuilderException {
        if (policyIntent != null) {
            //resolve the qualifiable intent
            Intent qualifiableIntent = policyIntent.getQualifiableIntent();
            if ( qualifiableIntent.isUnresolved() ) {
                Intent resolvedQualifiableIntent = definedIntents.get(qualifiableIntent.getName());
                
                if ( resolvedQualifiableIntent != null ) {
                    policyIntent.setQualifiableIntent(resolvedQualifiableIntent);
                } else {
                    throw new SCADefinitionsBuilderException("Qualifiable Intent - " + qualifiableIntent + 
                                                             " not found for QualifiedIntent " + policyIntent);
                }
                    
            }
        }
    }
    
    private void buildProvidedIntents(PolicySet policySet, Map<QName, Intent> definedIntents) throws SCADefinitionsBuilderException {
        if (policySet != null ) {
            //resolve all provided intents
            List<Intent> providedIntents = new ArrayList<Intent>(); 
            for (Intent providedIntent : policySet.getProvidedIntents()) {
                if ( providedIntent.isUnresolved() ) {
                    Intent resolvedProvidedIntent = definedIntents.get(providedIntent.getName());
                        if ( resolvedProvidedIntent != null ) {
                            providedIntents.add(resolvedProvidedIntent); 
                        } else {
                            throw new SCADefinitionsBuilderException("Provided Intent - " + providedIntent + 
                                                                     " not found for PolicySet " + policySet);
                                
                        }
                }
            }
            policySet.getProvidedIntents().clear();
            policySet.getProvidedIntents().addAll(providedIntents);
        }
    }
    
    private void buildIntentsInMappedPolicies(PolicySet policySet, Map<QName, Intent> definedIntents) throws SCADefinitionsBuilderException {
        Map<Intent, List<Policy>> mappedPolicies = new Hashtable<Intent, List<Policy>>();   
        for ( Intent mappedIntent : policySet.getMappedPolicies().keySet() ) {
            if ( mappedIntent.isUnresolved() ) {
                Intent resolvedMappedIntent = definedIntents.get(mappedIntent.getName());
                
                if ( resolvedMappedIntent != null ) {
                    mappedPolicies.put(resolvedMappedIntent, policySet.getMappedPolicies().get(mappedIntent));
                } else {
                    throw new SCADefinitionsBuilderException("Mapped Intent - " + mappedIntent + 
                                                             " not found for PolicySet " + policySet);
                        
                }
            }
        }
        
        policySet.getMappedPolicies().clear();
        policySet.getMappedPolicies().putAll(mappedPolicies);
    }
    
    
    private void buildReferredPolicySets(PolicySet policySet, Map<QName, PolicySet> definedPolicySets) throws SCADefinitionsBuilderException {

        List<PolicySet> referredPolicySets = new ArrayList<PolicySet>(); 
        for ( PolicySet referredPolicySet : policySet.getReferencedPolicySets() ) {
            if ( referredPolicySet.isUnresolved() ) {
                PolicySet resolvedReferredPolicySet = definedPolicySets.get(referredPolicySet.getName());
                if ( resolvedReferredPolicySet != null ) {
                    referredPolicySets.add(resolvedReferredPolicySet);
                } else {
                    throw new SCADefinitionsBuilderException("Referred PolicySet - " + referredPolicySet +
                                                             "not found for PolicySet - " + policySet);
                }
            }
        }
        policySet.getReferencedPolicySets().clear();
        policySet.getReferencedPolicySets().addAll(referredPolicySets);
    }
    
    private void includeReferredPolicySets(PolicySet policySet, PolicySet referredPolicySet) {
        for ( PolicySet furtherReferredPolicySet : referredPolicySet.getReferencedPolicySets() ) {
            includeReferredPolicySets(referredPolicySet, furtherReferredPolicySet);
        }
        policySet.getPolicies().addAll(referredPolicySet.getPolicies());
        policySet.getMappedPolicies().putAll(referredPolicySet.getMappedPolicies());
    }
}
