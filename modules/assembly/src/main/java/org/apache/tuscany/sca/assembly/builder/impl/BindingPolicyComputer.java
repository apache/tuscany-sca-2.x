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

package org.apache.tuscany.sca.assembly.builder.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.util.PolicyValidationException;

/**
 * Policy computation methods pertaining to computing Binding policies
 */
public class BindingPolicyComputer extends PolicyComputer {
    
    public BindingPolicyComputer(List<PolicySet> domainPolicySets) {
        super(domainPolicySets);
    }
    
    public void computeBindingIntentsAndPolicySets(Contract contract)  throws PolicyValidationException {
        computeIntents(contract.getBindings(), contract.getRequiredIntents());
        computePolicySets(contract.getApplicablePolicySets(), contract.getBindings(), contract.getPolicySets());
        
        for ( Binding binding : contract.getBindings() ) {
            if ( binding instanceof IntentAttachPoint ) {
                computeIntentsForOperations((IntentAttachPoint)binding);
            }
            
            if ( binding instanceof PolicySetAttachPoint ) {
                computePolicySetsForOperations(((PolicySetAttachPoint)binding).getApplicablePolicySets(), 
                                               (PolicySetAttachPoint)binding);
            }
        }
        
        if ( contract.getCallback() != null ) {
            computeIntents(contract.getCallback().getBindings(), 
                           contract.getCallback().getRequiredIntents());
            computePolicySets(contract.getApplicablePolicySets(), 
                              contract.getCallback().getBindings(), 
                              contract.getCallback().getPolicySets());
        }
    }
    
    private void computeIntents(List<Binding> bindings, List<Intent> inheritedIntents) throws PolicyValidationException {
        for (Binding binding : bindings) {
            if (binding instanceof IntentAttachPoint) {
                IntentAttachPoint policiedBinding = (IntentAttachPoint)binding;
                //since the parent component could also contain intents that apply to implementation
                //and binding elements within, we filter out only those that apply to this binding type
                List<Intent> prunedIntents = computeInheritableIntents(policiedBinding.getType(), 
                                                                       inheritedIntents);
                policiedBinding.getRequiredIntents().addAll(prunedIntents);
                
                computeIntents(policiedBinding);
                //trimInherentlyProvidedIntents(policiedBinding.getType(), 
                //                              policiedBinding.getRequiredIntents());
                
                computeIntentsForOperations((IntentAttachPoint)policiedBinding);
            }
        }
    }
    
    private void computePolicySets(List<PolicySet> applicablePolicySets,
                                   List<Binding> bindings,
                                   List<PolicySet> inheritedPolicySets) throws PolicyValidationException {
        for (Binding binding : bindings) {
            if ( binding instanceof PolicySetAttachPoint ) {
                PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)binding;
                
                List<PolicySet> prunedPolicySets = computeInheritablePolicySets(policiedBinding,
                                                                                inheritedPolicySets);
                policiedBinding.getPolicySets().addAll(prunedPolicySets);
                computePolicySets(policiedBinding);
                computePolicySetsForOperations(applicablePolicySets, policiedBinding);

            }
        }
    }
    
    public void determineApplicableBindingPolicySets(Contract source, Contract target) throws PolicyComputationException {
        List<Intent> intentsCopy = null;
        for (Binding aBinding : source.getBindings()) {
            if (aBinding instanceof PolicySetAttachPoint) {
                PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)aBinding;
                IntentAttachPointType bindingType = policiedBinding.getType();

                
                intentsCopy = new ArrayList<Intent>(policiedBinding.getRequiredIntents());
                // add the target component's intents to the reference binding
                if (target != null) {
                    for (Intent intent : target.getRequiredIntents()) {
                        if (!policiedBinding.getRequiredIntents().contains(intent)) {
                            for (QName constrained : intent.getConstrains()) {
                                if (bindingType != null && bindingType.getName().getNamespaceURI()
                                    .equals(constrained.getNamespaceURI())
                                    && bindingType.getName().getLocalPart().startsWith(constrained
                                        .getLocalPart())) {
                                    policiedBinding.getRequiredIntents().add(intent);
                                    break;
                                }
                            }
                        }
                    }
                }
                
                //trim intents specified in operations.  First check for policysets specified on the operation
                //and then in the parent implementation
                if ( aBinding instanceof OperationsConfigurator ) {
                    OperationsConfigurator opConfigurator = (OperationsConfigurator)aBinding;
                    
                    for ( ConfiguredOperation confOp : opConfigurator.getConfiguredOperations() ) {
                        List<Intent> opsIntentsCopy = new ArrayList<Intent>(confOp.getRequiredIntents());
                        
                        trimInherentlyProvidedIntents(policiedBinding.getType(), 
                                                      confOp.getRequiredIntents());
                        trimProvidedIntents(confOp.getRequiredIntents(), confOp.getPolicySets());
                        trimProvidedIntents(confOp.getRequiredIntents(), policiedBinding.getPolicySets());
                        
                        if (domainPolicySets != null) {
                            determineApplicableDomainPolicySets(policiedBinding.getApplicablePolicySets(), 
                                                                confOp,
                                                                policiedBinding.getType());
        
                            if (confOp.getRequiredIntents().size() > 0) {
                                new PolicyComputationException("The following are unfulfilled intents for operations configured in "
                                        + "binding - " + aBinding.getName() + "\nUnfulfilled Intents = " +
                                        confOp.getRequiredIntents());
                            }
                        }
                        
                        //the intents list could have been trimmed when matching for policysets
                        //since the bindings may need the original set of intents we copy that back
                        confOp.getRequiredIntents().clear();
                        confOp.getRequiredIntents().addAll(opsIntentsCopy);
                        
                    }
                }

                trimInherentlyProvidedIntents(policiedBinding.getType(), 
                                              policiedBinding.getRequiredIntents());
                trimProvidedIntents(policiedBinding.getRequiredIntents(), policiedBinding
                    .getPolicySets());

                // determine additional policysets that match remaining intents
                // TODO: resolved to domain policy registry and attach suitable
                // policy sets to the binding
                // for now using the SCA Definitions instead of registry
                // if there are intents that are not provided by any policy set
                // throw a warning
                determineApplicableDomainPolicySets(source, policiedBinding);
                
                //the intents list could have been trimmed when matching for policysets
                //since the bindings may need the original set of intents we copy that back
                policiedBinding.getRequiredIntents().clear();
                policiedBinding.getRequiredIntents().addAll(intentsCopy);
                
            }
        }
    }
    
    private void determineApplicableDomainPolicySets(Contract contract, 
                                                     PolicySetAttachPoint policiedBinding) 
                                                            throws PolicyComputationException {
        if ( domainPolicySets != null) {
            determineApplicableDomainPolicySets(policiedBinding.getApplicablePolicySets(), 
                                                policiedBinding,
                                                policiedBinding.getType());
            
            if ( policiedBinding.getRequiredIntents().size() > 0 ) {
                if ( contract instanceof Service ) {
                    throw new PolicyComputationException("The following are unfulfilled intents for " +
                            "binding in service - " + contract.getName() + "\nUnfulfilled Intents = " + 
                            policiedBinding.getRequiredIntents());
                } else {
                    throw new PolicyComputationException("The are unfulfilled intents for " +
                            "binding in reference - " + contract.getName() + "\nUnfulfilled Intents = " + 
                            policiedBinding.getRequiredIntents());
                }
            }
        }
    }
}
