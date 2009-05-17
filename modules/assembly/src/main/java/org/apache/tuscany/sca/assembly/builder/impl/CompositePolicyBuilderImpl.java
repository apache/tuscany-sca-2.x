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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * A composite builder that computes policy sets based on attached intents and policy sets.
 * Useful if you want to build the model without making any runtime decisions such as
 * reference/services matching
 *
 * @version $Rev$ $Date$
 */
public class CompositePolicyBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {

    public CompositePolicyBuilderImpl(AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper) {
        super(assemblyFactory, null, null, null, interfaceContractMapper);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositePolicyBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        computePolicies(composite, definitions, monitor);
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
                policySubject.getRequiredIntents().addAll(subject.getRequiredIntents());
                policySubject.getPolicySets().addAll(subject.getPolicySets());
            }
        }
    }

    /**
     * Check if two policy subjects requires multually exclusive intents
     * @param subject1
     * @param subject2
     * @return
     */
    private boolean isMutualExclusive(PolicySubject subject1, PolicySubject subject2) {
        if (subject1 == subject2 || subject1 == null || subject2 == null) {
            return false;
        }
        for (Intent i1 : subject1.getRequiredIntents()) {
            for (Intent i2 : subject1.getRequiredIntents()) {
                if (i1.getExcludedIntents().contains(i2) || i2.getExcludedIntents().contains(i1)) {
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

    private void resolveAndNormalize(PolicySubject subject, Definitions definitions, Monitor monitor) {

        Set<Intent> intents = new HashSet<Intent>();
        if (definitions != null) {
            for (Intent i : subject.getRequiredIntents()) {
                int index = definitions.getIntents().indexOf(i);
                if (index != -1) {
                    intents.add(definitions.getIntents().get(index));
                } else {
                    warning(monitor, "intent-not-found", subject, i.getName().toString());
                    // Intent cannot be resolved
                }
            }
        }

        // Replace profile intents with their required intents
        boolean profileIntentsFound = false;
        while (true) {
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
                }
            }
        }

        for (PolicySet policySet : policySets) {
            List<Intent> provided = policySet.getProvidedIntents();
            // FIXME: Check if required intents are provided by the policy sets
        }

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
            isMutualExclusive(component, component.getImplementation());

            for (ComponentService componentService : component.getServices()) {
                isMutualExclusive(componentService, componentService.getService());

                if (componentService.getInterfaceContract() != null && componentService.getService() != null) {
                    isMutualExclusive(componentService.getInterfaceContract().getInterface(), componentService
                        .getService().getInterfaceContract().getInterface());
                    isMutualExclusive(componentService.getInterfaceContract().getCallbackInterface(), componentService
                        .getService().getInterfaceContract().getCallbackInterface());
                }

                for (Endpoint2 ep : componentService.getEndpoints()) {
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
                                isMutualExclusive((PolicySubject)ep.getBinding(), (PolicySubject)binding);
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
                isMutualExclusive(componentReference, componentReference.getReference());

                if (componentReference.getInterfaceContract() != null) {
                    isMutualExclusive(componentReference.getInterfaceContract().getInterface(), componentReference
                        .getReference().getInterfaceContract().getInterface());
                    isMutualExclusive(componentReference.getInterfaceContract().getCallbackInterface(),
                                      componentReference.getReference().getInterfaceContract().getCallbackInterface());
                }

                for (EndpointReference2 epr : componentReference.getEndpointReferences()) {
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
                                isMutualExclusive((PolicySubject)epr.getBinding(), (PolicySubject)binding);
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
            // How to deal with implementation level policySets/intents
        }
    }
}
