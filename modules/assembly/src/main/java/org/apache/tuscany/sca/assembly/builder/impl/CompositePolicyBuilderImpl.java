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
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * A composite builder that computes policy sets based on attached intents and policy sets.
 * Useful if you want to build the model without making any runtime decisions such as
 * reference/services matching
 *
 * @version $Rev$ $Date$
 */
public class CompositePolicyBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {

    public CompositePolicyBuilderImpl(AssemblyFactory assemblyFactory,
                                      InterfaceContractMapper interfaceContractMapper) {
        super(assemblyFactory, null, null, null, interfaceContractMapper);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositePolicyBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        computePolicies(composite, monitor);
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

    private void inheritFromService(PolicySubject subject, Service service) {
        if (service instanceof ComponentService) {
            inheritFromService(subject, ((ComponentService)service).getService());
        } else if (service instanceof CompositeService) {
            CompositeService compositeService = (CompositeService)service;
            inherit(subject, compositeService.getPromotedComponent());
            inheritFromService(subject, compositeService.getPromotedService());
        }
        inherit(subject, service);
    }

    private void inheritFromReference(PolicySubject subject, Reference reference) {
        if (reference instanceof ComponentReference) {
            inheritFromReference(subject, ((ComponentReference)reference).getReference());
        } else if (reference instanceof CompositeReference) {
            CompositeReference compositeReference = (CompositeReference)reference;
            for (ComponentReference componentReference : compositeReference.getPromotedReferences()) {
                inheritFromReference(subject, componentReference);
            }
        }
        inherit(subject, reference);
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

    private void validate(PolicySubject subject) {
        Set<Intent> intents = new HashSet<Intent>(subject.getRequiredIntents());

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

    }

    protected void computePolicies(Composite composite, Monitor monitor) {

        // compute policies recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                computePolicies((Composite)implementation, monitor);
            }
        }

        for (Component component : composite.getComponents()) {
            isMutualExclusive(component, component.getImplementation());

            for (ComponentService componentService : component.getServices()) {
                isMutualExclusive(componentService, componentService.getService());

                if (componentService.getInterfaceContract() != null) {
                    isMutualExclusive(componentService.getInterfaceContract().getInterface(), componentService
                        .getService().getInterfaceContract().getInterface());
                    isMutualExclusive(componentService.getInterfaceContract().getCallbackInterface(), componentService
                        .getService().getInterfaceContract().getCallbackInterface());
                }

                for (Endpoint2 ep : componentService.getEndpoints()) {
                    // Inherit from the componentType.service.interface
                    inherit(ep, componentService.getService().getInterfaceContract().getInterface());
                    // Inherit from the component.service.interface
                    inherit(ep, componentService.getInterfaceContract().getInterface());
                    // Inherit from the componentType/service 
                    inherit(ep, component.getImplementation(), componentService.getService());
                    // Find the corresponding binding in the componentType and inherit the intents/policySets
                    for (Binding binding : componentService.getService().getBindings()) {
                        if (isEqual(ep.getBinding().getName(), binding.getName()) && (binding instanceof PolicySubject)) {
                            isMutualExclusive((PolicySubject)ep.getBinding(), (PolicySubject)binding);
                            // Inherit from componentType.service.binding
                            inherit(ep, binding);
                            break;
                        }
                    }
                    // Inherit from composite/component/service/binding
                    inherit(ep, composite, ep.getComponent(), ep.getService(), ep.getBinding());

                    // Replace profile intents with their required intents
                    // Remove the intents whose @contraints do not include the current element
                    // Replace unqualified intents if there is a qualified intent in the list
                    // Replace qualifiable intents with the default qualied intent
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
                    inherit(epr, componentReference.getReference().getInterfaceContract().getInterface());
                    // Inherit from the component.reference.interface
                    inherit(epr, componentReference.getInterfaceContract().getInterface());
                    // Inherit from the componentType/reference 
                    inherit(epr, component.getImplementation(), componentReference.getReference());
                    // Find the corresponding binding in the componentType and inherit the intents/policySets
                    for (Binding binding : componentReference.getReference().getBindings()) {
                        if (isEqual(epr.getBinding().getName(), binding.getName()) && (binding instanceof PolicySubject)) {
                            isMutualExclusive((PolicySubject)epr.getBinding(), (PolicySubject)binding);
                            // Inherit from componentType.reference.binding
                            inherit(epr, binding);
                            break;
                        }
                    }
                    // Inherit from composite/component/reference/binding
                    inherit(epr, composite, epr.getComponent(), epr.getReference(), epr.getBinding());

                    // Replace profile intents with their required intents
                    // Remove the intents whose @contraints do not include the current element
                    // Replace unqualified intents if there is a qualified intent in the list
                    // Replace qualifiable intents with the default qualied intent
                }
            }

            Implementation implemenation = component.getImplementation();
            try {
                PolicyConfigurationUtil.computeImplementationIntentsAndPolicySets(implemenation, component);
            } catch (Exception e) {
                error(monitor, "PolicyRelatedException", implemenation, e);
                //throw new RuntimeException(e);
            }
        }

        //compute policies for composite service bindings
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;

            // Composite service inherits the policySets and intents from the promoted component service
            Component promotedComponent = compositeService.getPromotedComponent();
            // Promoted component service inherits from the component type service
            // as well as the structural hierarchy, i.e., composite/promotedComponent
            ComponentService promotedService = compositeService.getPromotedService();
            // We need to calculate the inherited intents/policySets for the promoted
            // service first
            isMutualExclusive(compositeService, promotedService);
        }

        //compute policies for composite reference bindings
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;

            // Composite reference inherits the policySets and intents from the promoted component references
            for (ComponentReference promotedReference : compositeReference.getPromotedReferences()) {

                // Promoted component reference inherits from the component type reference
                // as well as the structural hierarchy, i.e., composite/promotedComponent
                // We need to calculate the inherited intents/policySets for the promoted
                // reference first
                isMutualExclusive(compositeReference, promotedReference);

            }
        }

    }
}
