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
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
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
public class CompositePolicyBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {
    public CompositePolicyBuilderImpl(ExtensionPointRegistry registry) {
        super(registry);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositePolicyBuilder";
    }

    public Composite build(Composite composite, Definitions definitions, Monitor monitor)
        throws CompositeBuilderException {
        computePolicies(composite, definitions, monitor);
        buildPolicies(composite, definitions, monitor);
        return composite;
    }

    /**
     * Inherit the intents and policySets from the list of models
     * @param intents
     * @param policySets
     * @param models
     */
    private void inherit(PolicySubject policySubject, Object... models) {
        for (Object model : models) {
            if (model instanceof PolicySubject) {
                PolicySubject subject = (PolicySubject)model;
                // FIXME: We should ignore the mutually exclusive intents from different levels
                policySubject.getRequiredIntents().addAll(subject.getRequiredIntents());
                policySubject.getPolicySets().addAll(subject.getPolicySets());
            }
        }
    }

    /**
     * Check if two policy subjects requires multually exclusive intents
     * @param subject1
     * @param subject2
     * @param monitor 
     * @return
     */
    private boolean checkMutualExclusion(PolicySubject subject1, PolicySubject subject2, Monitor monitor) {
        if (subject1 == subject2 || subject1 == null || subject2 == null) {
            return false;
        }
        for (Intent i1 : subject1.getRequiredIntents()) {
            for (Intent i2 : subject1.getRequiredIntents()) {
                if (i1.getExcludedIntents().contains(i2) || i2.getExcludedIntents().contains(i1)) {
                    error(monitor, "MutuallyExclusiveIntents", new Object[] {subject1, subject2}, i1, i2);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Inherit the policySets and intents from the implementation hierarchy
     * @param subject
     * @param composite
     * @param component
     * @param service
     */
    private void inheritFromService(PolicySubject subject, Composite composite, Component component, Service service) {
        if (service == null) {
            return;
        }
        if (service instanceof ComponentService) {
            // component!=null
            if (component.getImplementation() instanceof Composite) {
                composite = (Composite)component.getImplementation();
            }
            inheritFromService(subject, composite, component, ((ComponentService)service).getService());
            // Component service also inherits the intents/policySets from composite/component
            inherit(subject, composite, component);
        } else if (service instanceof CompositeService) {
            // composite!=null, component is not used
            CompositeService compositeService = (CompositeService)service;
            // Handle the promoted component service
            inheritFromService(subject, composite, compositeService.getPromotedComponent(), compositeService
                .getPromotedService());
        }
        // For atomic service, the composite is not used
        inherit(subject, component.getImplementation(), service);
    }

    /**
     * Inherit the policySets and intents from the implementation hierarchy
     * @param subject
     * @param composite
     * @param component
     * @param reference
     */
    private void inheritFromReference(PolicySubject subject,
                                      Composite composite,
                                      Component component,
                                      Reference reference) {
        if (reference == null) {
            return;
        }
        if (reference instanceof ComponentReference) {
            // component!=null
            if (component.getImplementation() instanceof Composite) {
                composite = (Composite)component.getImplementation();
            }
            inheritFromReference(subject, composite, component, ((ComponentReference)reference).getReference());
        } else if (reference instanceof CompositeReference) {
            CompositeReference compositeReference = (CompositeReference)reference;
            for (int i = 0, n = compositeReference.getPromotedReferences().size(); i < n; i++) {
                inheritFromReference(subject,
                                     composite,
                                     compositeReference.getPromotedComponents().get(i),
                                     compositeReference.getPromotedReferences().get(i));
            }
        }
        // Inherit from the componentType/reference
        inherit(subject, component.getImplementation(), reference);
    }

    /**
     * Check if two names are equal
     * @param name1
     * @param name2
     * @return
     */
    private boolean isEqual(String name1, String name2) {
        if (name1 == name2) {
            return true;
        }
        if (name1 != null) {
            return name1.equals(name2);
        } else {
            return name2.equals(name1);
        }
    }

    private Intent resolve(Definitions definitions, Intent proxy) {
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

    private void resolveAndNormalize(PolicySubject subject, Definitions definitions, Monitor monitor) {

        Set<Intent> intents = new HashSet<Intent>();
        if (definitions != null) {
            for (Intent i : subject.getRequiredIntents()) {
                Intent resolved = resolve(definitions, i);
                if (resolved != null) {
                    intents.add(resolved);
                } else {
                    warning(monitor, "IntentNotFound", subject, i);
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

        Set<PolicySet> policySets = new HashSet<PolicySet>();
        if (definitions != null) {
            for (PolicySet policySet : subject.getPolicySets()) {
                int index = definitions.getPolicySets().indexOf(policySet);
                if (index != -1) {
                    policySets.add(definitions.getPolicySets().get(index));
                } else {
                    // PolicySet cannot be resolved
                    warning(monitor, "PolicySetNotFound", subject, policySet);
                }
            }
        }

        for (Intent intent : subject.getRequiredIntents()) {
            loop: for (PolicySet ps : definitions.getPolicySets()) {
                // FIXME: We will have to check the policy references and intentMap too
                // as well as the appliesTo
                if (ps.getProvidedIntents().contains(intent)) {
                    policySets.add(ps);
                    break;
                }
                for (IntentMap map : ps.getIntentMaps()) {
                    for (Qualifier q : map.getQualifiers()) {
                        if (intent.equals(q.getIntent())) {
                            policySets.add(ps);
                            break loop;
                        }
                    }
                }
            }
        }

        subject.getPolicySets().clear();
        subject.getPolicySets().addAll(policySets);

    }

    protected void computePolicies(Composite composite, Definitions definitions, Monitor monitor) {

        // compute policies recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                computePolicies((Composite)implementation, definitions, monitor);
            }
        }

        for (Component component : composite.getComponents()) {
            // Check component against implementation
            checkMutualExclusion(component, component.getImplementation(), monitor);

            for (ComponentService componentService : component.getServices()) {
                // Check component/service against componentType/service 
                checkMutualExclusion(componentService, componentService.getService(), monitor);

                if (componentService.getInterfaceContract() != null && componentService.getService() != null) {
                    checkMutualExclusion(componentService.getInterfaceContract().getInterface(), componentService
                        .getService().getInterfaceContract().getInterface(), monitor);
                    checkMutualExclusion(componentService.getInterfaceContract().getCallbackInterface(),
                                         componentService.getService().getInterfaceContract().getCallbackInterface(),
                                         monitor);
                }

                for (Endpoint ep : componentService.getEndpoints()) {
                    // Inherit from the componentType.service.interface
                    if (componentService.getService() != null && componentService.getService().getInterfaceContract() != null) {
                        inherit(ep, componentService.getService().getInterfaceContract().getInterface());
                    }
                    if (componentService.getInterfaceContract() != null) {
                        // Inherit from the component.service.interface
                        inherit(ep, componentService.getInterfaceContract().getInterface());
                    }
                    // Inherit from the componentType/service
                    inheritFromService(ep, composite, component, componentService.getService());
                    // Find the corresponding binding in the componentType and inherit the intents/policySets
                    if (componentService.getService() != null) {
                        for (Binding binding : componentService.getService().getBindings()) {
                            if (isEqual(ep.getBinding().getName(), binding.getName()) && (binding instanceof PolicySubject)) {
                                checkMutualExclusion((PolicySubject)ep.getBinding(), (PolicySubject)binding, monitor);
                                // Inherit from componentType.service.binding
                                inherit(ep, binding);
                                break;
                            }
                        }
                    }
                    // Inherit from composite/component/service
                    inheritFromService(ep, composite, ep.getComponent(), ep.getService());
                    // Inherit from binding
                    inherit(ep, ep.getBinding());

                    // Replace profile intents with their required intents
                    // Remove the intents whose @contraints do not include the current element
                    // Replace unqualified intents if there is a qualified intent in the list
                    // Replace qualifiable intents with the default qualied intent
                    resolveAndNormalize(ep, definitions, monitor);
                }
            }

            for (ComponentReference componentReference : component.getReferences()) {
                // Check component/reference against componentType/reference
                checkMutualExclusion(componentReference, componentReference.getReference(), monitor);

                if (componentReference.getInterfaceContract() != null && componentReference.getReference() != null) {
                    checkMutualExclusion(componentReference.getInterfaceContract().getInterface(), componentReference
                        .getReference().getInterfaceContract().getInterface(), monitor);
                    checkMutualExclusion(componentReference.getInterfaceContract().getCallbackInterface(),
                                         componentReference.getReference().getInterfaceContract()
                                             .getCallbackInterface(),
                                         monitor);
                }

                for (EndpointReference epr : componentReference.getEndpointReferences()) {
                    // Inherit from the componentType.reference.interface
                    if (componentReference.getReference() != null && componentReference.getReference()
                        .getInterfaceContract() != null) {
                        inherit(epr, componentReference.getReference().getInterfaceContract().getInterface());
                    }
                    // Inherit from the component.reference.interface
                    if (componentReference.getInterfaceContract() != null) {
                        inherit(epr, componentReference.getInterfaceContract().getInterface());
                    }
                    // Inherit from the componentType/reference
                    inheritFromReference(epr, composite, component, componentReference.getReference());
                    // Find the corresponding binding in the componentType and inherit the intents/policySets
                    if (componentReference.getReference() != null) {
                        for (Binding binding : componentReference.getReference().getBindings()) {
                            if (epr.getBinding() != null && isEqual(epr.getBinding().getName(), binding.getName())
                                && (binding instanceof PolicySubject)) {
                                checkMutualExclusion((PolicySubject)epr.getBinding(), (PolicySubject)binding, monitor);
                                // Inherit from componentType.reference.binding
                                inherit(epr, binding);
                                break;
                            }
                        }
                    }
                    // Inherit from composite/component/reference/binding
                    inheritFromReference(epr, composite, epr.getComponent(), epr.getReference());
                    inherit(epr, epr.getBinding());

                    // Replace profile intents with their required intents
                    // Remove the intents whose @contraints do not include the current element
                    // Replace unqualified intents if there is a qualified intent in the list
                    // Replace qualifiable intents with the default qualied intent
                    resolveAndNormalize(epr, definitions, monitor);
                }
            }

            Implementation implementation = component.getImplementation();
            if (implementation != null) {
                inherit(implementation, component, composite);
            }
            // How to deal with implementation level policySets/intents
        }
    }
    
    private Set<QName> getPolicyNames(PolicySubject subject) {
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
    
    protected void buildPolicies(Composite composite, Definitions definitions, Monitor monitor) {

        // compute policies recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                buildPolicies((Composite)implementation, definitions, monitor);
            }
        }

        for (Component component : composite.getComponents()) {

            for (ComponentService componentService : component.getServices()) {
                for (Endpoint ep : componentService.getEndpoints()) {
                    for (QName policyType : getPolicyNames(ep)) {
                        PolicyBuilder builder = builders.getPolicyBuilder(policyType);
                        if (builder != null) {
                            builder.build(ep, definitions, monitor);
                        }
                    }
                }
            }

            for (ComponentReference componentReference : component.getReferences()) {
                for (EndpointReference epr : componentReference.getEndpointReferences()) {
                    for (QName policyType : getPolicyNames(epr)) {
                        PolicyBuilder builder = builders.getPolicyBuilder(policyType);
                        if (builder != null) {
                            builder.build(epr, definitions, monitor);
                        }
                    }
                }
            }

            Implementation implementation = component.getImplementation();
            if (implementation != null) {
                for (QName policyType : getPolicyNames(implementation)) {
                    PolicyBuilder builder = builders.getPolicyBuilder(policyType);
                    if (builder != null) {
                        builder.build(component, implementation, definitions, monitor);
                    }
                }
            }
        }
    }
}
