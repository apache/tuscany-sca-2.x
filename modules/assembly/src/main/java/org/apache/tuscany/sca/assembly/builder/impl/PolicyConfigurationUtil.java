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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.QualifiedIntent;
import org.apache.tuscany.sca.policy.util.PolicyComputationUtils;
import org.apache.tuscany.sca.policy.util.PolicyValidationException;
import org.apache.tuscany.sca.policy.util.PolicyValidationUtils;

/**
 * This class contains policy computation methods common to computing implementation and binding policies
 *
 * @version $Rev$ $Date$
 */
abstract class PolicyConfigurationUtil {
    
    static private List<Intent> computeInheritableIntents(IntentAttachPointType attachPointType, 
                                                   List<Intent> inheritableIntents) throws PolicyValidationException {
        List<Intent> validInheritableIntents = new ArrayList<Intent>();
        
        //expand profile intents in inherited intents
        PolicyComputationUtils.expandProfileIntents(inheritableIntents);

        //validate if inherited intent applies to the attachpoint (binding / implementation) and 
        //only add such intents to the attachpoint (binding / implementation)
        for (Intent intent : inheritableIntents) {
            if ( !intent.isUnresolved() ) { 
                for (QName constrained : intent.getConstrains()) {
                    if ( PolicyValidationUtils.isConstrained(constrained, attachPointType)) {
                        validInheritableIntents.add(intent);
                        break;
                    }
                }
            } else {
                throw new PolicyValidationException("Policy Intent '" + intent.getName() + "' is not defined in this domain");
            }
        }
        
        return validInheritableIntents;
    }
    
    static private void normalizeIntents(IntentAttachPoint intentAttachPoint) {
        //expand profile intents specified in the attachpoint (binding / implementation)
        PolicyComputationUtils.expandProfileIntents(intentAttachPoint.getRequiredIntents());

        //remove duplicates and ...
        //where qualified form of intent exists retain it and remove the qualifiable intent
        filterDuplicatesAndQualifiableIntents(intentAttachPoint);
    }
    
    static private void trimInherentlyProvidedIntents(IntentAttachPointType attachPointType, List<Intent>intents) {
        //exclude intents that are inherently supported by the 
        //attachpoint-type (binding-type  / implementation-type)
        List<Intent> requiredIntents = new ArrayList<Intent>(intents);
        for ( Intent intent : requiredIntents ) {
            if ( isProvidedInherently(attachPointType, intent) ) {
                intents.remove(intent);
            }
        }
    }
    
    
    static void computeIntentsForOperations(IntentAttachPoint intentAttachPoint) throws PolicyValidationException {
        if ( intentAttachPoint instanceof OperationsConfigurator ) {
            computeIntentsForOperations((OperationsConfigurator)intentAttachPoint, 
                                        intentAttachPoint, 
                                        intentAttachPoint.getRequiredIntents());
        }
    }
    
    static private void computeIntentsForOperations(OperationsConfigurator opConfigurator, 
                                               IntentAttachPoint intentAttachPoint, 
                                               List<Intent> parentIntents) throws PolicyValidationException {
        IntentAttachPointType attachPointType = intentAttachPoint.getType();
        
        boolean found = false;
        for ( ConfiguredOperation confOp : opConfigurator.getConfiguredOperations() ) {
            //expand profile intents specified on operations
            PolicyComputationUtils.expandProfileIntents(confOp.getRequiredIntents());
            
            //validateIntents(confOp, attachPointType);
            
            //add intents specified for parent intent attach point (binding / implementation)
            //wherever its not overridden in the operation
            Intent tempIntent = null;
            List<Intent> attachPointOpIntents = new ArrayList<Intent>();
            for (Intent anIntent : parentIntents) {
                found = false;
            
                tempIntent = anIntent;
                while ( tempIntent instanceof QualifiedIntent ) {
                    tempIntent = ((QualifiedIntent)tempIntent).getQualifiableIntent();
                }
                
                for ( Intent opIntent : confOp.getRequiredIntents() ) {
                    if ( opIntent.getName().getLocalPart().startsWith(tempIntent.getName().getLocalPart())) {
                        found = true;
                        break;
                    }
                }
                
                if ( !found ) {
                    boolean conflict = false;
                    for (Intent excluded : anIntent.getExcludedIntents()) {
                        if (confOp.getRequiredIntents().contains(excluded)) {
                            conflict = true;
                            break;
                        }
                    }
                    if (!conflict) {
                        attachPointOpIntents.add(anIntent);
                    }
                }
            }
            
            confOp.getRequiredIntents().addAll(attachPointOpIntents);
            
            //remove duplicates and ...
            //where qualified form of intent exists retain it and remove the qualifiable intent
            filterDuplicatesAndQualifiableIntents(confOp);
            
            //exclude intents that are inherently supported by the parent
            //attachpoint-type (binding-type  / implementation-type)
            if ( attachPointType != null ) {
                List<Intent> requiredIntents = new ArrayList<Intent>(confOp.getRequiredIntents());
                for ( Intent intent : requiredIntents ) {
                    if ( isProvidedInherently(attachPointType, intent) ) {
                        confOp.getRequiredIntents().remove(intent);
                    }
                }
            }
        }
    }
    
    static private List<PolicySet> computeInheritablePolicySets(List<PolicySet> inheritablePolicySets,
                                                           List<PolicySet> applicablePolicySets) 
                                                               throws PolicyValidationException {
        List<PolicySet> validInheritablePolicySets = new ArrayList<PolicySet>();
        for (PolicySet policySet : inheritablePolicySets) {
            if ( !policySet.isUnresolved() ) { 
                if ( applicablePolicySets.contains(policySet) ) {
                    validInheritablePolicySets.add(policySet);
                }
            } else {
                throw new PolicyValidationException("Policy Set '" + policySet.getName()
                        + "' is not defined in this domain  ");
            }
        }
        
        return validInheritablePolicySets;
    }
    
    static private void normalizePolicySets(PolicySetAttachPoint policySetAttachPoint ) {
        //get rid of duplicate entries
        HashMap<QName, PolicySet> policySetTable = new HashMap<QName, PolicySet>();
        for ( PolicySet policySet : policySetAttachPoint.getPolicySets() ) {
            policySetTable.put(policySet.getName(), policySet);
        }
        
        policySetAttachPoint.getPolicySets().clear();
        policySetAttachPoint.getPolicySets().addAll(policySetTable.values());
            
        //expand profile intents
        for ( PolicySet policySet : policySetAttachPoint.getPolicySets() ) {
            PolicyComputationUtils.expandProfileIntents(policySet.getProvidedIntents());
        }
    }
    
    static private void computePolicySetsForOperations(List<PolicySet> applicablePolicySets,
                                                  PolicySetAttachPoint policySetAttachPoint) 
                                                                        throws PolicyValidationException {
        if ( policySetAttachPoint instanceof OperationsConfigurator ) {
            computePolicySetsForOperations(applicablePolicySets, 
                                           (OperationsConfigurator)policySetAttachPoint, 
                                           policySetAttachPoint);
        }
        
    }
    
    static private void computePolicySetsForOperations(List<PolicySet> applicablePolicySets, 
                                                  OperationsConfigurator opConfigurator,
                                                  PolicySetAttachPoint policySetAttachPoint) 
                                                                        throws PolicyValidationException {
        //String appliesTo = null;
        //String scdlFragment = "";
        HashMap<QName, PolicySet> policySetTable = new HashMap<QName, PolicySet>();
        IntentAttachPointType attachPointType = policySetAttachPoint.getType();
        
        for ( ConfiguredOperation confOp : opConfigurator.getConfiguredOperations() ) {
            //validate policysets specified for the attachPoint
            for (PolicySet policySet : confOp.getPolicySets()) {
                if ( !policySet.isUnresolved() ) {
                    //appliesTo = policySet.getAppliesTo();
        
                    //if (!PolicyValidationUtils.isPolicySetApplicable(scdlFragment, appliesTo, attachPointType)) {
                    if (!applicablePolicySets.contains(policySet)) {
                        throw new PolicyValidationException("Policy Set '" + policySet.getName() 
                                + " specified for operation " + confOp.getName()  
                            + "' does not constrain extension type  "
                            + attachPointType.getName());
        
                    }
                } else {
                    throw new PolicyValidationException("Policy Set '" + policySet.getName() 
                            + " specified for operation " + confOp.getName()  
                        + "' is not defined in this domain  ");
                }
            }
            
            //get rid of duplicate entries
            for ( PolicySet policySet : confOp.getPolicySets() ) {
                policySetTable.put(policySet.getName(), policySet);
            }
        
            confOp.getPolicySets().clear();
            confOp.getPolicySets().addAll(policySetTable.values());
            policySetTable.clear();
            
            //expand profile intents
            for ( PolicySet policySet : confOp.getPolicySets() ) {
                PolicyComputationUtils.expandProfileIntents(policySet.getProvidedIntents());
            }
        }
    }
    
        
    static private void trimProvidedIntents(List<Intent> requiredIntents, List<PolicySet> policySets) {
        for ( PolicySet policySet : policySets ) {
            trimProvidedIntents(requiredIntents, policySet);
        }
    }
    
    static private void determineApplicableDomainPolicySets(List<PolicySet> applicablePolicySets,
                                                     PolicySetAttachPoint policySetAttachPoint,
                                                     IntentAttachPointType intentAttachPointType) {

        if (policySetAttachPoint.getRequiredIntents().size() > 0) {

            // form a list of all intents required by the attach point
            List<Intent> combinedTargetIntents = new ArrayList<Intent>();
            combinedTargetIntents.addAll(policySetAttachPoint.getRequiredIntents());
            for (PolicySet targetPolicySet : policySetAttachPoint.getPolicySets()) {
                combinedTargetIntents.addAll(PolicyComputationUtils.findAndExpandProfileIntents(targetPolicySet.getProvidedIntents()));
            }

            //since the set of applicable policysets for this attachpoint is known 
            //we only need to check in that list if there is a policyset that matches
            for (PolicySet policySet : applicablePolicySets) {
                // do not use the policy set if it provides intents that conflict with required intents
                boolean conflict = false;
                List<Intent> providedIntents = PolicyComputationUtils.findAndExpandProfileIntents(policySet.getProvidedIntents());
                checkConflict: for (Intent intent : providedIntents) {
                    for (Intent excluded : intent.getExcludedIntents()) {
                        if (combinedTargetIntents.contains(excluded)) {
                            conflict = true;
                            break checkConflict;
                        }
                    }
                }
                if (conflict)
                    continue;
                int prevSize = policySetAttachPoint.getRequiredIntents().size();
                trimProvidedIntents(policySetAttachPoint.getRequiredIntents(), policySet);
                // if any intent was trimmed off, then this policyset must
                // be attached to the intent attachpoint's policyset
                if (prevSize != policySetAttachPoint.getRequiredIntents().size()) {
                    policySetAttachPoint.getPolicySets().add(policySet);
                }
            } 
        }
    }
    
    private static boolean isProvidedInherently(IntentAttachPointType attachPointType, Intent intent) {
        return ( attachPointType != null && 
                 (( attachPointType.getAlwaysProvidedIntents() != null &&
                     attachPointType.getAlwaysProvidedIntents().contains(intent) ) || 
                  ( attachPointType.getMayProvideIntents() != null &&
                     attachPointType.getMayProvideIntents().contains(intent) )
                 ) );
     }
    
    private static void trimProvidedIntents(List<Intent> requiredIntents, PolicySet policySet) {
        for ( Intent providedIntent : policySet.getProvidedIntents() ) {
            if ( requiredIntents.contains(providedIntent) ) {
                requiredIntents.remove(providedIntent);
            } 
        }
        
        for ( Intent mappedIntent : policySet.getMappedPolicies().keySet() ) {
            if ( requiredIntents.contains(mappedIntent) ) {
                requiredIntents.remove(mappedIntent);
            } 
        }
    }
    
    private static void filterDuplicatesAndQualifiableIntents(IntentAttachPoint intentAttachPoint) {
        //remove duplicates
        Map<QName, Intent> intentsTable = new HashMap<QName, Intent>();
        for ( Intent intent : intentAttachPoint.getRequiredIntents() ) {
            intentsTable.put(intent.getName(), intent);
        }
        
        //where qualified form of intent exists retain it and remove the qualifiable intent
        Map<QName, Intent> intentsTableCopy = new HashMap<QName, Intent>(intentsTable);
        //if qualified form of intent exists remove the unqualified form
        for ( Intent intent : intentsTableCopy.values() ) {
            if ( intent instanceof QualifiedIntent ) {
                QualifiedIntent qualifiedIntent = (QualifiedIntent)intent;
                if ( intentsTable.get(qualifiedIntent.getQualifiableIntent().getName()) != null ) {
                    intentsTable.remove(qualifiedIntent.getQualifiableIntent().getName());
                }
            }
        }
        intentAttachPoint.getRequiredIntents().clear();
        intentAttachPoint.getRequiredIntents().addAll(intentsTable.values());
    }
    
    private static void validateIntents(ConfiguredOperation confOp, IntentAttachPointType attachPointType) throws PolicyValidationException {
        boolean found = false;
        if ( attachPointType != null ) {
            //validate intents specified against the parent (binding / implementation)
            found = false;
            for (Intent intent : confOp.getRequiredIntents()) {
                if ( !intent.isUnresolved() ) {
                    for (QName constrained : intent.getConstrains()) {
                        if (PolicyValidationUtils.isConstrained(constrained, attachPointType)) {
                            found = true;
                            break;
                        }
                    }
        
                    if (!found) {
                        throw new PolicyValidationException("Policy Intent '" + intent.getName() 
                                + " specified for operation " + confOp.getName()  
                            + "' does not constrain extension type  "
                            + attachPointType.getName());
                    }
                } else {
                    throw new PolicyValidationException("Policy Intent '" + intent.getName() 
                            + " specified for operation " + confOp.getName()  
                        + "' is not defined in this domain  ");
                }
            }
        }
    }

    static void computeBindingIntentsAndPolicySets(Contract contract)  throws PolicyValidationException {
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

    private static void computeIntents(IntentAttachPoint policiedBinding, List<Intent> inheritedIntents) 
                                                                    throws PolicyValidationException {
            //since the parent component could also contain intents that apply to implementation
            //and binding elements within, we filter out only those that apply to this binding type
            List<Intent> prunedIntents = computeInheritableIntents(policiedBinding.getType(), 
                                                                   inheritedIntents);
            policiedBinding.getRequiredIntents().addAll(prunedIntents);
            
            normalizeIntents(policiedBinding);
    }

    private static void computePolicySets(PolicySetAttachPoint policiedBinding,
                                   List<PolicySet> inheritedPolicySets) throws PolicyValidationException {
                
        List<PolicySet> prunedPolicySets = computeInheritablePolicySets(inheritedPolicySets,
                                                                        policiedBinding.getApplicablePolicySets());
        policiedBinding.getPolicySets().addAll(prunedPolicySets);
        normalizePolicySets(policiedBinding);
    }

    static void determineApplicableBindingPolicySets(Contract source, Contract target) throws PolicyConfigurationException {
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
                            new PolicyConfigurationException("The following are unfulfilled intents for operations configured in "
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

    static private void determineApplicableDomainPolicySets(Contract contract, 
                                                     PolicySetAttachPoint policiedBinding) 
                                                            throws PolicyConfigurationException {
        //if ( domainPolicySets != null) {
            determineApplicableDomainPolicySets(policiedBinding.getApplicablePolicySets(), 
                                                policiedBinding,
                                                policiedBinding.getType());
            
            if ( policiedBinding.getRequiredIntents().size() > 0 ) {
                if ( contract instanceof Service ) {
                    throw new PolicyConfigurationException("The following are unfulfilled intents for " +
                            "binding in service - " + contract.getName() + "\nUnfulfilled Intents = " + 
                            policiedBinding.getRequiredIntents());
                } else {
                    throw new PolicyConfigurationException("The are unfulfilled intents for " +
                            "binding in reference - " + contract.getName() + "\nUnfulfilled Intents = " + 
                            policiedBinding.getRequiredIntents());
                }
            }
        //}
    }

    private static void addInheritedOpConfOnBindings(OperationsConfigurator source, 
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

    private static void aggregateAndPruneApplicablePolicySets(List<PolicySet> source, List<PolicySet> target) {
        target.addAll(source);
        //strip duplicates
        Hashtable<QName, PolicySet> policySetTable = new Hashtable<QName, PolicySet>();
        for ( PolicySet policySet : target ) {
            policySetTable.put(policySet.getName(), policySet);
        }
        
        target.clear();
        target.addAll(policySetTable.values());
    }

    static <C extends Contract> void inheritDefaultPolicies(Base parent, List<C> contracts) {
    
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

    static void computeImplementationIntentsAndPolicySets(Implementation implementation, Component parent)  
                                                                throws PolicyValidationException, PolicyConfigurationException {
        if ( implementation instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policiedImplementation = (PolicySetAttachPoint)implementation;
            //since for an implementation the component has its policy intents and policysets its possible
            //that there are some intents there that does not constrain the implementation.. so prune 
            List<Intent> prunedIntents = computeInheritableIntents(policiedImplementation.getType(), 
                                                                   parent.getRequiredIntents());
            parent.getRequiredIntents().clear();
            parent.getRequiredIntents().addAll(prunedIntents);
            normalizeIntents(parent);
    
            computeIntentsForOperations((OperationsConfigurator)parent,
                                        (IntentAttachPoint)implementation,
                                        parent.getRequiredIntents());
            
            
            List<PolicySet> prunedPolicySets = computeInheritablePolicySets(parent.getPolicySets(),
                                                                            parent.getApplicablePolicySets());
            parent.getPolicySets().clear();
            parent.getPolicySets().addAll(prunedPolicySets);
            normalizePolicySets(parent);
    
            PolicyComputationUtils.checkForMutuallyExclusiveIntents(
                parent.getRequiredIntents(),
                parent.getPolicySets(),
                policiedImplementation.getType(),
                parent.getName());
    
            computePolicySetsForOperations(parent.getApplicablePolicySets(), 
                                           (OperationsConfigurator)parent, 
                                           (PolicySetAttachPoint)implementation);
    
            for ( ConfiguredOperation confOp : ((OperationsConfigurator)parent).getConfiguredOperations() ) {
                PolicyComputationUtils.checkForMutuallyExclusiveIntents(
                    confOp.getRequiredIntents(),
                    confOp.getPolicySets(),
                    policiedImplementation.getType(),
                    parent.getName() + "." + confOp.getName());
            }
            
            determineApplicableImplementationPolicySets(parent);
    
        }
    }

    static private void determineApplicableImplementationPolicySets(Component component) throws PolicyConfigurationException {
        List<Intent> intentsCopy = null;
        if ( component.getImplementation() instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policiedImplementation = (PolicySetAttachPoint)component.getImplementation();
           
            //trim intents specified in operations.  First check for policysets specified on the operation
            //and then in the parent implementation
            if ( component instanceof OperationsConfigurator ) {
                OperationsConfigurator opConfigurator = (OperationsConfigurator)component;
                
                for ( ConfiguredOperation confOp : opConfigurator.getConfiguredOperations() ) {
                    intentsCopy = new ArrayList<Intent>(confOp.getRequiredIntents());
                    trimInherentlyProvidedIntents(policiedImplementation.getType(), 
                                                  confOp.getRequiredIntents());
                    trimProvidedIntents(confOp.getRequiredIntents(), confOp.getPolicySets());
                    trimProvidedIntents(confOp.getRequiredIntents(), component.getPolicySets());
                    
                    determineApplicableDomainPolicySets(component.getApplicablePolicySets(), 
                                                        confOp,
                                                        policiedImplementation.getType());
    
                    if (confOp.getRequiredIntents().size() > 0) {
                        new PolicyConfigurationException("The following are unfulfilled intents for operations configured in "
                                + "component implementation - " + component.getName() + "\nUnfulfilled Intents = " +
                                confOp.getRequiredIntents());
                    }
                    
                    //the intents list could have been trimmed when matching for policysets
                    //since the implementation may need the original set of intents we copy that back
                    confOp.getRequiredIntents().clear();
                    confOp.getRequiredIntents().addAll(intentsCopy);
                }
            }
                
            intentsCopy = new ArrayList<Intent>(component.getRequiredIntents());
            trimInherentlyProvidedIntents(policiedImplementation.getType(), 
                                          component.getRequiredIntents());
            trimProvidedIntents(component.getRequiredIntents(), component.getPolicySets());
                
            //determine additional policysets that match remaining intents
            //if there are intents that are not provided by any policy set throw a warning
            //TODO: resolved to domain policy registry and attach suitable policy sets to the implementation
            //...for now using the SCA Definitions instead of registry
            //if ( domainPolicySets != null)  {
                determineApplicableDomainPolicySets(component.getApplicablePolicySets(), 
                                                    component,
                                                    policiedImplementation.getType());
                                                    
                if (component.getRequiredIntents().size() > 0) {
                    throw new PolicyConfigurationException("The following are unfulfilled intents for component implementation - " + component
                        .getName() + "\nUnfulfilled Intents = " + component.getRequiredIntents());
                }
            //}
            
            //the intents list could have been trimmed when matching for policysets
            //since the bindings may need the original set of intents we copy that back
            component.getRequiredIntents().clear();
            component.getRequiredIntents().addAll(intentsCopy);
        }
    }
}
