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
import java.util.Hashtable;
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
import org.apache.tuscany.sca.policy.util.PolicyComputationUtils;
import org.apache.tuscany.sca.policy.util.PolicyValidationException;

/**
 * Policy computation methods pertaining to computing Binding policies
 */
public class BindingPolicyComputer extends PolicyComputer {
    
    public BindingPolicyComputer() {
        super();
    }
    
    public void computeBindingIntentsAndPolicySets(Contract contract)  throws PolicyValidationException {
        for (Binding binding : contract.getBindings()) {
            if (binding instanceof PolicySetAttachPoint) {
                PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)binding;
                computeIntents((IntentAttachPoint)binding, contract.getRequiredIntents());
                
                aggregateAndPruneApplicablePolicySets(contract.getApplicablePolicySets(), 
                                                      policiedBinding.getApplicablePolicySets());

                computePolicySets(policiedBinding, contract.getPolicySets());

                PolicyComputationUtils.checkForMutuallyExclusiveIntents(
                    policiedBinding.getRequiredIntents(),
                    policiedBinding.getPolicySets(),
                    policiedBinding.getType(),
                    contract.getName());

                if ( binding instanceof OperationsConfigurator && 
                        contract instanceof OperationsConfigurator ) {
                    //add or merge service operations to the binding
                    addInheritedOpConfOnBindings((OperationsConfigurator)contract,
                                                 (OperationsConfigurator)binding,
                                                 (PolicySetAttachPoint)binding);
                
                    computeIntentsForOperations((IntentAttachPoint)binding);
                    computePolicySetsForOperations(contract.getApplicablePolicySets(), 
                                                   policiedBinding);

                    for ( ConfiguredOperation confOp : ((OperationsConfigurator)binding).getConfiguredOperations() ) {
                        PolicyComputationUtils.checkForMutuallyExclusiveIntents(
                            confOp.getRequiredIntents(),
                            confOp.getPolicySets(),
                            policiedBinding.getType(),
                            contract.getName() + "." + confOp.getName());
                    }

                }
            }
        }
        
        if ( contract.getCallback() != null ) {
            for (Binding binding : contract.getCallback().getBindings()) {
                if (binding instanceof PolicySetAttachPoint) {
                    PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)binding;
                    computeIntents((IntentAttachPoint)binding, contract.getCallback().getRequiredIntents());
            
                    aggregateAndPruneApplicablePolicySets(contract.getApplicablePolicySets(), 
                                                          policiedBinding.getApplicablePolicySets());

                    computePolicySets(policiedBinding, contract.getCallback().getPolicySets());

                    PolicyComputationUtils.checkForMutuallyExclusiveIntents(
                        policiedBinding.getRequiredIntents(),
                        policiedBinding.getPolicySets(),
                        policiedBinding.getType(),
                        contract.getName() + " callback");

                }
            }
        }
    }
    
    
    private void computeIntents(IntentAttachPoint policiedBinding, List<Intent> inheritedIntents) 
                                                                    throws PolicyValidationException {
            //since the parent component could also contain intents that apply to implementation
            //and binding elements within, we filter out only those that apply to this binding type
            List<Intent> prunedIntents = computeInheritableIntents(policiedBinding.getType(), 
                                                                   inheritedIntents);
            policiedBinding.getRequiredIntents().addAll(prunedIntents);
            
            normalizeIntents(policiedBinding);
    }
    
    
    private void computePolicySets(PolicySetAttachPoint policiedBinding,
                                   List<PolicySet> inheritedPolicySets) throws PolicyValidationException {
                
        List<PolicySet> prunedPolicySets = computeInheritablePolicySets(inheritedPolicySets,
                                                                        policiedBinding.getApplicablePolicySets());
        policiedBinding.getPolicySets().addAll(prunedPolicySets);
        normalizePolicySets(policiedBinding);
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
                        
                        determineApplicableDomainPolicySets(policiedBinding.getApplicablePolicySets(), 
                                                            confOp,
                                                            policiedBinding.getType());
    
                        if (confOp.getRequiredIntents().size() > 0) {
                            new PolicyComputationException("The following are unfulfilled intents for operations configured in "
                                    + "binding - " + aBinding.getName() + "\nUnfulfilled Intents = " +
                                    confOp.getRequiredIntents());
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
        //if ( domainPolicySets != null) {
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
        //}
    }
    
    private void addInheritedOpConfOnBindings(OperationsConfigurator source, 
                                              OperationsConfigurator target,
                                              PolicySetAttachPoint attachPoint) throws PolicyValidationException {
        boolean found = false;
        
        List<ConfiguredOperation> additionalOperations = new ArrayList<ConfiguredOperation>();
        for ( ConfiguredOperation sourceConfOp : source.getConfiguredOperations() ) {
            for ( ConfiguredOperation targetConfOp : target.getConfiguredOperations() ) {
                if ( sourceConfOp.getName().equals(targetConfOp.getName())) {
                    List<Intent> prunedIntents = computeInheritableIntents(attachPoint.getType(), 
                                                                           sourceConfOp.getRequiredIntents());
                    PolicyComputationUtils.addInheritedIntents(prunedIntents, 
                                                               targetConfOp.getRequiredIntents());
                    
                    List<PolicySet> prunedPolicySets  = computeInheritablePolicySets(sourceConfOp.getPolicySets(), 
                                                                                     attachPoint.getApplicablePolicySets());
                    PolicyComputationUtils.addInheritedPolicySets(prunedPolicySets, targetConfOp.getPolicySets(), true);
                    found = true;
                    break;
                }
            }
            
            if ( !found ) {
                additionalOperations.add(sourceConfOp);
            }
        }
        
        if ( !additionalOperations.isEmpty() ) {
            target.getConfiguredOperations().addAll(additionalOperations);
        }
    }
    
    /*private void addInheritedOpConfOnBindings(Contract contract) {      
        for ( Binding binding : contract.getBindings() ) {
            if ( binding instanceof OperationsConfigurator ) {
                addInheritedOperationConfigurations(contract, (OperationsConfigurator)binding);
            }
        }
    }*/
    
    private void aggregateAndPruneApplicablePolicySets(List<PolicySet> source, List<PolicySet> target) {
        target.addAll(source);
        //strip duplicates
        Hashtable<QName, PolicySet> policySetTable = new Hashtable<QName, PolicySet>();
        for ( PolicySet policySet : target ) {
            policySetTable.put(policySet.getName(), policySet);
        }
        
        target.clear();
        target.addAll(policySetTable.values());
    }

    protected <C extends Contract> void inheritDefaultPolicies(Base parent, List<C> contracts) {

        for (Contract contract : contracts) {

            // The contract inherits default policies from the parent composite/component.
            if ( parent instanceof PolicySetAttachPoint )  {
                PolicyComputationUtils.addDefaultPolicies(
                                         ((PolicySetAttachPoint)parent).getRequiredIntents(),
                                         ((PolicySetAttachPoint)parent).getPolicySets(),
                                         contract.getRequiredIntents(),
                                         contract.getPolicySets());
            }

            // The contract's configured operations inherit default policies from the contract.
            for ( ConfiguredOperation confOp : contract.getConfiguredOperations() ) {
                PolicyComputationUtils.addDefaultPolicies(
                                         contract.getRequiredIntents(),
                                         contract.getPolicySets(),
                                         confOp.getRequiredIntents(),
                                         confOp.getPolicySets());
            }

            // The contract's callback inherits default policies from the contract.
            if (contract.getCallback() != null) {
                PolicyComputationUtils.addDefaultPolicies(
                                         contract.getRequiredIntents(),
                                         contract.getPolicySets(),
                                         contract.getCallback().getRequiredIntents(),
                                         contract.getCallback().getPolicySets());
            }

        }
    }

}
