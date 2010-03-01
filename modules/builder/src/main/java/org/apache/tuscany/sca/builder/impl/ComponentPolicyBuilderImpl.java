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

package org.apache.tuscany.sca.builder.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.PolicyExpression;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.policy.Qualifier;

/**
 * A composite builder that computes policy sets based on attached intents and policy sets.
 * Useful if you want to build the model without making any runtime decisions such as
 * reference/services matching
 *
 * @version $Rev$ $Date$
 */
public class ComponentPolicyBuilderImpl {

    protected BuilderExtensionPoint builders;

    public ComponentPolicyBuilderImpl(ExtensionPointRegistry registry) {
        this.builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
    }

    /**
     * Report a warning.
     *
     * @param monitor
     * @param problems
     * @param message
     * @param model
     */
    protected void warning(Monitor monitor, String message, Object model, Object... messageParameters) {
        Monitor.warning(monitor, this, Messages.ASSEMBLY_VALIDATION, message, messageParameters);
    }

    /**
     * Report a error.
     *
     * @param monitor
     * @param problems
     * @param message
     * @param model
     */
    protected void error(Monitor monitor, String message, Object model, Object... messageParameters) {
        Monitor.error(monitor, this, Messages.ASSEMBLY_VALIDATION, message, messageParameters);
    }

    /**
     * Inherit the intents and policySets from the list of models
     * @param ignoreExclusiveIntents TODO
     * @param models
     * @param intents
     * @param policySets
     */
    protected void inherit(PolicySubject policySubject, boolean ignoreExclusiveIntents, Object... models) {
        for (Object model : models) {
            if (model instanceof PolicySubject) {
                PolicySubject subject = (PolicySubject)model;

                if (!ignoreExclusiveIntents) {
                    // The intents are merged and the exclusion check will be done after
                    policySubject.getRequiredIntents().addAll(subject.getRequiredIntents());
                } else {
                    Set<Intent> intents = new HashSet<Intent>();
                    for (Intent i1 : subject.getRequiredIntents()) {
                        boolean exclusive = false;
                        for (Intent i2 : policySubject.getRequiredIntents()) {
                            if (i1.getExcludedIntents().contains(i2) || i2.getExcludedIntents().contains(i1)) {
                                exclusive = true;
                                break;
                            }
                        }
                        if (!exclusive) {
                            intents.add(i1);
                        }
                    }
                    policySubject.getRequiredIntents().addAll(intents);
                }
                policySubject.getPolicySets().addAll(subject.getPolicySets());
            }
        }
    }

    protected void configure(PolicySubject subject1, PolicySubject subject2, BuilderContext context) {
        if (subject1 != null) {
            resolveAndCheck(subject1, context);
        }
        if (subject2 != null) {
            resolveAndCheck(subject2, context);
        }
        inherit(subject1, false, subject2);
        checkMutualExclusion(subject1, context);
    }

    protected void configure(ComponentService componentService, BuilderContext context) {
        Service service = componentService.getService();
        if (service != null) {
            configure(componentService, service, context);
            configureBindings(componentService, service, context);
        }
    }

    private void configureBindings(Contract componentContract, Contract componentTypeContract, BuilderContext context) {
        if (componentTypeContract == null) {
            return;
        }
        Map<String, Binding> componentTypeContractBindings = new HashMap<String, Binding>();
        for (Binding binding : componentTypeContract.getBindings()) {
            componentTypeContractBindings.put(binding.getName(), binding);
        }
        for (Binding binding : componentContract.getBindings()) {
            Binding componentTypeBinding = componentTypeContractBindings.get(binding.getName());
            if (binding instanceof PolicySubject) {
                inherit((PolicySubject)binding, false, componentTypeBinding, context);
            }
        }
    }

    protected void configure(ComponentReference componentReference, BuilderContext context) {
        Reference reference = componentReference.getReference();
        if (reference != null) {
            configure(componentReference, reference, context);
            configureBindings(componentReference, reference, context);
        }
    }

    protected void configure(CompositeService compositeService, BuilderContext context) {
        configure(compositeService, compositeService.getPromotedService(), context);
    }

    protected void configure(CompositeReference compositeReference, BuilderContext context) {
        for (ComponentReference reference : compositeReference.getPromotedReferences()) {
            configure(compositeReference, reference, context);
        }
    }

    public void configure(Component component, BuilderContext context) {
        // Inherit the intents and policySets from the componentType
        configure(component, component.getImplementation(), context);
        // Inherit the intents and policySets from the componentType
        for (ComponentReference componentReference : component.getReferences()) {
            configure(componentReference, context);
        }
        for (ComponentService componentService : component.getServices()) {
            configure(componentService, context);
        }
    }

    /**
     * Check if a single policy subject requires multually exclusive intents
     * @param subject1 - the policy subject to check
     * @param context - context containing useful things like the monitor instance
     * @return true if the policy subject contains mutually exclusive intents
     */
    protected boolean checkMutualExclusion(PolicySubject subject1, BuilderContext context) {
        if (subject1 == null) {
            return false;
        }
        for (Intent i1 : subject1.getRequiredIntents()) {
            for (Intent i2 : subject1.getRequiredIntents()) {
                if ((i1 != i2) && (i1.getExcludedIntents().contains(i2) || i2.getExcludedIntents().contains(i1))) {
                    error(context.getMonitor(), "MutuallyExclusiveIntents", new Object[] {subject1}, i1, i2);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if two policy subjects requires multually exclusive intents
     * @param subject1
     * @param subject2
     * @param monitor 
     * @return
     */
    protected boolean checkMutualExclusion(PolicySubject subject1, PolicySubject subject2, BuilderContext context) {
        if (subject1 == subject2 || subject1 == null || subject2 == null) {
            return false;
        }
        for (Intent i1 : subject1.getRequiredIntents()) {
            for (Intent i2 : subject2.getRequiredIntents()) {
                if (i1.getExcludedIntents().contains(i2) || i2.getExcludedIntents().contains(i1)) {
                    error(context.getMonitor(), "MutuallyExclusiveIntents", new Object[] {subject1, subject2}, i1, i2);
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean resolveAndCheck(PolicySubject subject, BuilderContext context) {
        if (subject == null) {
            return false;
        }
        // FIXME: [rfeng] Should we resolve the intents during the "build" phase?
        resolveAndNormalize(subject, context);
        List<Intent> intents = subject.getRequiredIntents();
        int size = intents.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Intent i1 = intents.get(i);
                Intent i2 = intents.get(j);
                if (i1 != i2 && i1.getExcludedIntents().contains(i2) || i2.getExcludedIntents().contains(i1)) {
                    error(context.getMonitor(), "MutuallyExclusiveIntents", subject, i1, i2);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if two names are equal
     * @param name1
     * @param name2
     * @return
     */
    protected boolean isEqual(String name1, String name2) {
        if (name1 == name2) {
            return true;
        }
        if (name1 != null) {
            return name1.equals(name2);
        } else {
            return name2.equals(name1);
        }
    }

    protected static Intent resolve(Definitions definitions, Intent proxy) {
        for (Intent i : definitions.getIntents()) {
            if (i.equals(proxy)) {
                return i;
            }
            for (Intent qi : i.getQualifiedIntents()) {
                if (qi.equals(proxy)) {
                    return qi;
                }
            }
        }
        return null;
    }

    protected void resolveAndNormalize(PolicySubject subject, BuilderContext context) {
        Definitions definitions = context.getDefinitions();
        Set<Intent> intents = new HashSet<Intent>();
        if (definitions != null) {
            for (Intent i : subject.getRequiredIntents()) {
                Intent resolved = resolve(definitions, i);
                if (resolved != null) {
                    intents.add(resolved);
                } else {
                    error(context.getMonitor(), "IntentNotFound", subject, i);
                    // Intent cannot be resolved
                }
            }
        }

        // Replace profile intents with their required intents
        while (!intents.isEmpty()) {
            boolean profileIntentsFound = false;
            Set<Intent> copy = new HashSet<Intent>(intents);
            for (Intent i : copy) {
                if (!i.getRequiredIntents().isEmpty()) {
                    intents.remove(i);
                    intents.addAll(i.getRequiredIntents());
                    profileIntentsFound = true;
                }
            }
            if (!profileIntentsFound) {
                // No more profileIntents
                break;
            }
        }

        // Remove the intents whose @contraints do not include the current element
        // Replace unqualified intents if there is a qualified intent in the list
        Set<Intent> copy = new HashSet<Intent>(intents);
        for (Intent i : copy) {
            if (i.getQualifiableIntent() != null) {
                intents.remove(i.getQualifiableIntent());
            }
        }

        // Replace qualifiable intents with the default qualified intent
        copy = new HashSet<Intent>(intents);
        for (Intent i : copy) {
            if (i.getDefaultQualifiedIntent() != null) {
                intents.remove(i);
                intents.add(i.getDefaultQualifiedIntent());
            }
        }

        subject.getRequiredIntents().clear();
        subject.getRequiredIntents().addAll(intents);

        // resolve policy set names that have been specified for the
        // policy subject against the real policy sets from the 
        // definitions files
        Set<PolicySet> policySets = new HashSet<PolicySet>();
        if (definitions != null) {
            for (PolicySet policySet : subject.getPolicySets()) {
                int index = definitions.getPolicySets().indexOf(policySet);
                if (index != -1) {
                    policySets.add(definitions.getPolicySets().get(index));
                } else {
                    // PolicySet cannot be resolved
                    warning(context.getMonitor(), "PolicySetNotFound", subject, policySet);
                }
            }
        }

        // find the policy sets that satisfy the intents that are now
        // attached to the policy subject. From the OASIS policy
        // spec CD02 rev7:
        //  1272 A policySet provides an intent if any of the statements are true:
        //  1273 1. The intent is contained in the policySet @provides list.
        //  1274 2. The intent is a qualified intent and the unqualified form of the intent is contained in the policySet
        //  1275 @provides list.
        //  1276 3. The policySet @provides list contains a qualified form of the intent (where the intent is qualifiable).
        for (Intent intent : subject.getRequiredIntents()) {
            boolean intentMatched = false;
            
            loop: for (PolicySet ps : definitions.getPolicySets()) {
                // FIXME: We will have to check the policy references and intentMap too
                // as well as the appliesTo
                if (ps.getProvidedIntents().contains(intent)) {
                    policySets.add(ps);
                    intentMatched = true;
                    break;
                }
                
                for (Intent psProvidedIntent : ps.getProvidedIntents()){
                    if (isQualifiedBy(psProvidedIntent, intent)){
                        policySets.add(ps);
                        intentMatched = true;
                        break loop;
                    }
                }
                
                for (IntentMap map : ps.getIntentMaps()) {
                    for (Qualifier q : map.getQualifiers()) {
                        if (intent.equals(q.getIntent())) {
                            policySets.add(ps);
                            intentMatched = true;
                            break loop;
                        }
                    }
                }
            }
            
            if (!intentMatched){
                // Raise a warning as we have an intent that doesn't have a matching 
                // policy set at this start. 
                // TODO - this could be because the intent is provided by and extension
                //        and hence there is no explicit policy set. Need and extra piece
                //        of processing to walk through the extension models. 
                warning(context.getMonitor(), "IntentNotSatisfied", subject, intent.getName(), subject.toString());
            }
        }

        subject.getPolicySets().clear();
        subject.getPolicySets().addAll(policySets);

    }

    protected Set<QName> getPolicyNames(PolicySubject subject) {
        if (subject == null) {
            return Collections.emptySet();
        }
        Set<QName> names = new HashSet<QName>();
        for (PolicySet ps : subject.getPolicySets()) {
            for (PolicyExpression exp : ps.getPolicies()) {
                names.add(exp.getName());
            }
        }
        return names;
    }
    
    protected boolean isQualifiedBy(Intent qualifiableIntent, Intent qualifiedIntent){
        if (qualifiedIntent.getQualifiableIntent() == qualifiableIntent){
            return true;
        } else {
            return false;
        }
    }

}
