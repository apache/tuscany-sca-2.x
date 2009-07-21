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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.policy.util.PolicyComputationUtils;
import org.apache.tuscany.sca.policy.util.PolicyValidationException;
import org.apache.tuscany.sca.policy.util.PolicyValidationUtils;

/**
 * This class contains policy computation methods common to computing implementation and binding policies
 *
 * @version $Rev$ $Date$
 */
abstract class PolicyConfigurationUtil {

    private static List<Intent> computeInheritableIntents(ExtensionType attachPointType, List<Intent> inheritableIntents)
        throws PolicyValidationException {
        List<Intent> validInheritableIntents = new ArrayList<Intent>();

        //expand profile intents in inherited intents
        PolicyComputationUtils.expandProfileIntents(inheritableIntents);

        //validate if inherited intent applies to the attachpoint (binding / implementation) and
        //only add such intents to the attachpoint (binding / implementation)
        for (Intent intent : inheritableIntents) {
            if (!intent.isUnresolved()) {
                for (ExtensionType constrained : intent.getConstrainedTypes()) {
                    if (PolicyValidationUtils.isConstrained(constrained, attachPointType)) {
                        validInheritableIntents.add(intent);
                        break;
                    }
                }
            } else {
                throw new PolicyValidationException("Policy Intent '" + intent.getName()
                    + "' is not defined in this domain");
            }
        }

        return validInheritableIntents;
    }

    private static void normalizeIntents(PolicySubject intentAttachPoint) {
        //expand profile intents specified in the attachpoint (binding / implementation)
        PolicyComputationUtils.expandProfileIntents(intentAttachPoint.getRequiredIntents());

        //remove duplicates and ...
        //where qualified form of intent exists retain it and remove the qualifiable intent
        filterDuplicatesAndQualifiableIntents(intentAttachPoint);
    }

    private static void trimInherentlyProvidedIntents(ExtensionType attachPointType, List<Intent> intents) {
        //exclude intents that are inherently supported by the
        //attachpoint-type (binding-type  / implementation-type)
        List<Intent> requiredIntents = new ArrayList<Intent>(intents);
        for (Intent intent : requiredIntents) {
            if (isProvidedInherently(attachPointType, intent)) {
                intents.remove(intent);
            }
        }
    }

    private static List<PolicySet> computeInheritablePolicySets(List<PolicySet> inheritablePolicySets)
        throws PolicyValidationException {
        // FIXME:
        return inheritablePolicySets;
    }

    private static void normalizePolicySets(PolicySubject subject) {
        //get rid of duplicate entries
        Set<PolicySet> policySetTable = new HashSet<PolicySet>(subject.getPolicySets());

        subject.getPolicySets().clear();
        subject.getPolicySets().addAll(policySetTable);

        //expand profile intents
        for (PolicySet policySet : subject.getPolicySets()) {
            PolicyComputationUtils.expandProfileIntents(policySet.getProvidedIntents());
        }
    }

    private static void trimProvidedIntents(List<Intent> requiredIntents, List<PolicySet> policySets) {
        for (PolicySet policySet : policySets) {
            trimProvidedIntents(requiredIntents, policySet);
        }
    }

    private static void determineApplicableDomainPolicySets(List<PolicySet> applicablePolicySets,
                                                            PolicySubject policySetAttachPoint,
                                                            ExtensionType intentAttachPointType) {

        if (policySetAttachPoint.getRequiredIntents().size() > 0) {

            // form a list of all intents required by the attach point
            List<Intent> combinedTargetIntents = new ArrayList<Intent>();
            combinedTargetIntents.addAll(policySetAttachPoint.getRequiredIntents());
            for (PolicySet targetPolicySet : policySetAttachPoint.getPolicySets()) {
                combinedTargetIntents.addAll(PolicyComputationUtils.findAndExpandProfileIntents(targetPolicySet
                    .getProvidedIntents()));
            }

            //since the set of applicable policysets for this attachpoint is known
            //we only need to check in that list if there is a policyset that matches
            for (PolicySet policySet : applicablePolicySets) {
                // do not use the policy set if it provides intents that conflict with required intents
                boolean conflict = false;
                List<Intent> providedIntents =
                    PolicyComputationUtils.findAndExpandProfileIntents(policySet.getProvidedIntents());
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

    private static boolean isProvidedInherently(ExtensionType attachPointType, Intent intent) {
        return (attachPointType != null && ((attachPointType.getAlwaysProvidedIntents() != null && attachPointType
            .getAlwaysProvidedIntents().contains(intent)) || (attachPointType.getMayProvidedIntents() != null && attachPointType
            .getMayProvidedIntents().contains(intent))));
    }

    private static void trimProvidedIntents(List<Intent> requiredIntents, PolicySet policySet) {
        for (Intent providedIntent : policySet.getProvidedIntents()) {
            if (requiredIntents.contains(providedIntent)) {
                requiredIntents.remove(providedIntent);
            }
        }

        for (IntentMap intentMap : policySet.getIntentMaps()) {
            if (requiredIntents.contains(intentMap.getProvidedIntent())) {
                requiredIntents.remove(intentMap.getProvidedIntent());
            }
        }
    }

    private static void filterDuplicatesAndQualifiableIntents(PolicySubject intentAttachPoint) {
        //remove duplicates
        Map<QName, Intent> intentsTable = new HashMap<QName, Intent>();
        for (Intent intent : intentAttachPoint.getRequiredIntents()) {
            intentsTable.put(intent.getName(), intent);
        }

        //where qualified form of intent exists retain it and remove the qualifiable intent
        Map<QName, Intent> intentsTableCopy = new HashMap<QName, Intent>(intentsTable);
        //if qualified form of intent exists remove the unqualified form
        for (Intent intent : intentsTableCopy.values()) {
            if (intent.getQualifiableIntent() != null) {
                if (intentsTable.get(intent.getQualifiableIntent().getName()) != null) {
                    intentsTable.remove(intent.getQualifiableIntent().getName());
                }
            }
        }
        intentAttachPoint.getRequiredIntents().clear();
        intentAttachPoint.getRequiredIntents().addAll(intentsTable.values());
    }

    static void computeBindingIntentsAndPolicySets(Contract contract) throws PolicyValidationException {
        for (Binding binding : contract.getBindings()) {
            if (binding instanceof PolicySubject) {
                PolicySubject policiedBinding = (PolicySubject)binding;
                computeIntents((PolicySubject)binding, contract.getRequiredIntents());

                computePolicySets(policiedBinding, contract.getPolicySets());

                PolicyComputationUtils.checkForMutuallyExclusiveIntents(policiedBinding.getRequiredIntents(),
                                                                        policiedBinding.getPolicySets(),
                                                                        policiedBinding.getExtensionType(),
                                                                        contract.getName());
            }
        }

        if (contract.getCallback() != null) {
            for (Binding binding : contract.getCallback().getBindings()) {
                if (binding instanceof PolicySubject) {
                    PolicySubject policiedBinding = (PolicySubject)binding;
                    computeIntents((PolicySubject)binding, contract.getCallback().getRequiredIntents());

                    computePolicySets(policiedBinding, contract.getCallback().getPolicySets());

                    PolicyComputationUtils.checkForMutuallyExclusiveIntents(policiedBinding.getRequiredIntents(),
                                                                            policiedBinding.getPolicySets(),
                                                                            policiedBinding.getExtensionType(),
                                                                            contract.getName() + " callback");

                }
            }
        }
    }

    private static void computeIntents(PolicySubject policiedBinding, List<Intent> inheritedIntents)
        throws PolicyValidationException {
        //since the parent component could also contain intents that apply to implementation
        //and binding elements within, we filter out only those that apply to this binding type
        List<Intent> prunedIntents = computeInheritableIntents(policiedBinding.getExtensionType(), inheritedIntents);
        policiedBinding.getRequiredIntents().addAll(prunedIntents);

        normalizeIntents(policiedBinding);
    }

    private static void computePolicySets(PolicySubject policiedBinding, List<PolicySet> inheritedPolicySets)
        throws PolicyValidationException {

        List<PolicySet> prunedPolicySets = computeInheritablePolicySets(inheritedPolicySets);
        policiedBinding.getPolicySets().addAll(prunedPolicySets);
        normalizePolicySets(policiedBinding);
    }

    static void determineApplicableBindingPolicySets(Contract source, Contract target)
        throws PolicyConfigurationException {
        List<Intent> intentsCopy = null;
        for (Binding aBinding : source.getBindings()) {
            if (aBinding instanceof PolicySubject) {
                PolicySubject policiedBinding = (PolicySubject)aBinding;
                ExtensionType bindingType = policiedBinding.getExtensionType();

                intentsCopy = new ArrayList<Intent>(policiedBinding.getRequiredIntents());
                // add the target component's intents to the reference binding
                if (target != null) {
                    for (Intent intent : target.getRequiredIntents()) {
                        if (!policiedBinding.getRequiredIntents().contains(intent)) {
                            for (ExtensionType constrained : intent.getConstrainedTypes()) {
                                if (bindingType != null && bindingType.getType().getNamespaceURI().equals(constrained
                                    .getType().getNamespaceURI())
                                    && bindingType.getType().getLocalPart().startsWith(constrained.getType()
                                        .getLocalPart())) {
                                    policiedBinding.getRequiredIntents().add(intent);
                                    break;
                                }
                            }
                        }
                    }
                }

                trimInherentlyProvidedIntents(policiedBinding.getExtensionType(), policiedBinding.getRequiredIntents());
                trimProvidedIntents(policiedBinding.getRequiredIntents(), policiedBinding.getPolicySets());

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

    private static void determineApplicableDomainPolicySets(Contract contract, PolicySubject policiedBinding)
        throws PolicyConfigurationException {
        // FIXME:
        /*
        //if ( domainPolicySets != null) {
            determineApplicableDomainPolicySets(policiedBinding.getApplicablePolicySets(),
                                                policiedBinding,
                                                policiedBinding.getExtensionType());

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
         */
    }

    private static void aggregateAndPruneApplicablePolicySets(List<PolicySet> source, List<PolicySet> target) {
        target.addAll(source);
        //strip duplicates
        Hashtable<QName, PolicySet> policySetTable = new Hashtable<QName, PolicySet>();
        for (PolicySet policySet : target) {
            policySetTable.put(policySet.getName(), policySet);
        }

        target.clear();
        target.addAll(policySetTable.values());
    }

    static <C extends Contract> void inheritDefaultPolicies(Base parent, List<C> contracts) {

        for (Contract contract : contracts) {

            // The contract inherits default policies from the parent composite/component.
            if (parent instanceof PolicySubject) {
                PolicyComputationUtils.addDefaultPolicies(((PolicySubject)parent).getRequiredIntents(),
                                                          ((PolicySubject)parent).getPolicySets(),
                                                          contract.getRequiredIntents(),
                                                          contract.getPolicySets());
            }

            // The contract's callback inherits default policies from the contract.
            if (contract.getCallback() != null) {
                PolicyComputationUtils.addDefaultPolicies(contract.getRequiredIntents(),
                                                          contract.getPolicySets(),
                                                          contract.getCallback().getRequiredIntents(),
                                                          contract.getCallback().getPolicySets());
            }

        }
    }

    static void computeImplementationIntentsAndPolicySets(Implementation implementation, Component parent)
        throws PolicyValidationException, PolicyConfigurationException {
        if (implementation instanceof PolicySubject) {
            PolicySubject policiedImplementation = (PolicySubject)implementation;
            //since for an implementation the component has its policy intents and policysets its possible
            //that there are some intents there that does not constrain the implementation.. so prune
            List<Intent> prunedIntents =
                computeInheritableIntents(policiedImplementation.getExtensionType(), parent.getRequiredIntents());
            parent.getRequiredIntents().clear();
            parent.getRequiredIntents().addAll(prunedIntents);
            normalizeIntents(parent);

            List<PolicySet> prunedPolicySets = computeInheritablePolicySets(parent.getPolicySets());
            parent.getPolicySets().clear();
            parent.getPolicySets().addAll(prunedPolicySets);
            normalizePolicySets(parent);

            PolicyComputationUtils.checkForMutuallyExclusiveIntents(parent.getRequiredIntents(),
                                                                    parent.getPolicySets(),
                                                                    policiedImplementation.getExtensionType(),
                                                                    parent.getName());

            determineApplicableImplementationPolicySets(parent);

        }
    }

    private static void determineApplicableImplementationPolicySets(Component component)
        throws PolicyConfigurationException {
        List<Intent> intentsCopy = null;
        if (component.getImplementation() instanceof PolicySubject) {
            PolicySubject policiedImplementation = (PolicySubject)component.getImplementation();

            intentsCopy = new ArrayList<Intent>(component.getRequiredIntents());
            trimInherentlyProvidedIntents(policiedImplementation.getExtensionType(), component.getRequiredIntents());
            trimProvidedIntents(component.getRequiredIntents(), component.getPolicySets());

            //determine additional policysets that match remaining intents
            //if there are intents that are not provided by any policy set throw a warning
            //TODO: resolved to domain policy registry and attach suitable policy sets to the implementation
            //...for now using the SCA Definitions instead of registry
            //if ( domainPolicySets != null)  {

            if (component.getRequiredIntents().size() > 0) {
                throw new PolicyConfigurationException(
                                                       "The following are unfulfilled intents for component implementation - " + component
                                                           .getName()
                                                           + "\nUnfulfilled Intents = "
                                                           + component.getRequiredIntents());
            }
            //}

            //the intents list could have been trimmed when matching for policysets
            //since the bindings may need the original set of intents we copy that back
            component.getRequiredIntents().clear();
            component.getRequiredIntents().addAll(intentsCopy);
        }
    }
}
