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


import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.util.PolicyComputationUtils;

/**
 * A composite builder that computes policy sets based on attached intents and policy sets.
 * Useful if you want to build the model without making any runtime decisions such as
 * reference/services matching
 *
 * @version $Rev$ $Date$
 */
public class CompositePolicyBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {

    public CompositePolicyBuilderImpl(AssemblyFactory assemblyFactory, EndpointFactory endpointFactory, InterfaceContractMapper interfaceContractMapper) {
        super(assemblyFactory, null, null, null, interfaceContractMapper);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositePolicyBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        computePolicies(composite, monitor);
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

            // Inherit default policies from the component to component-level contracts.
            // This must be done BEFORE computing implementation policies because the
            // implementation policy computer removes from the component any
            // intents and policy sets that don't apply to implementations.
            PolicyConfigurationUtil.inheritDefaultPolicies(component, component.getServices());
            PolicyConfigurationUtil.inheritDefaultPolicies(component, component.getReferences());

            Implementation implemenation = component.getImplementation(); 
            try {
                PolicyConfigurationUtil.computeImplementationIntentsAndPolicySets(implemenation, component);
            } catch ( Exception e ) {
                error(monitor, "PolicyRelatedException", implemenation, e);
                //throw new RuntimeException(e);
            }

            for (ComponentService componentService : component.getServices()) {
                Service service = componentService.getService();
                if (service != null) {
                    // reconcile intents and policysets from componentType
                     PolicyComputationUtils.addInheritedIntents(service.getRequiredIntents(), componentService.getRequiredIntents());
                     PolicyComputationUtils.addInheritedPolicySets(service.getPolicySets(), componentService.getPolicySets(), true);
                     
                }
                
                try {
                    //compute intents and policyset for each binding
                    //addInheritedOpConfOnBindings(componentService);
                    PolicyConfigurationUtil.computeBindingIntentsAndPolicySets(componentService);
                    PolicyConfigurationUtil.determineApplicableBindingPolicySets(componentService, null);
    
                } catch ( Exception e ) {
                    error(monitor, "PolicyRelatedException", componentService, e);
                    //throw new RuntimeException(e);
                }
            }
        
            for (ComponentReference componentReference : component.getReferences()) {
                Reference reference = componentReference.getReference();
                if (reference != null) {
                    // reconcile intents and policysets
                    PolicyComputationUtils.addInheritedIntents(reference.getRequiredIntents(), componentReference.getRequiredIntents());
                    PolicyComputationUtils.addInheritedPolicySets(reference.getPolicySets(), componentReference.getPolicySets(), true);
                }
                
               
                try {
                    //compute intents and policyset for each binding
                    //addInheritedOpConfOnBindings(componentReference);
                    PolicyConfigurationUtil.computeBindingIntentsAndPolicySets(componentReference);
                    PolicyConfigurationUtil.determineApplicableBindingPolicySets(componentReference, null);
    
                
                    if ( componentReference.getCallback() != null ) {
                        PolicyComputationUtils.addInheritedIntents(componentReference.getRequiredIntents(), 
                                            componentReference.getCallback().getRequiredIntents());
                        PolicyComputationUtils.addInheritedPolicySets(componentReference.getPolicySets(), 
                                               componentReference.getCallback().getPolicySets(), 
                                               false);
                    }
                } catch ( Exception e ) {
                    error(monitor, "PolicyRelatedException", componentReference, e);
                    //throw new RuntimeException(e);
                }
            }
        }

        PolicyConfigurationUtil.inheritDefaultPolicies(composite, composite.getServices());
        PolicyConfigurationUtil.inheritDefaultPolicies(composite, composite.getReferences());

        //compute policies for composite service bindings
        for (Service service : composite.getServices()) {
            addPoliciesFromPromotedService((CompositeService)service);
            try {
                //add or merge service operations to the binding
                //addInheritedOpConfOnBindings(service);
                PolicyConfigurationUtil.computeBindingIntentsAndPolicySets(service);
                PolicyConfigurationUtil.determineApplicableBindingPolicySets(service, null);
            } catch ( Exception e ) {
                error(monitor, "PolicyRelatedException", service, e);
                //throw new RuntimeException(e);
            }
                
        }
    
        for (Reference reference : composite.getReferences()) {
            CompositeReference compReference = (CompositeReference)reference;
            addPoliciesFromPromotedReference(compReference);
            try {
               
                if (compReference.getCallback() != null) {
                    PolicyComputationUtils.addInheritedIntents(compReference.getRequiredIntents(), 
                                        compReference.getCallback().getRequiredIntents());
                    PolicyComputationUtils.addInheritedPolicySets(compReference.getPolicySets(), 
                                           compReference.getCallback().getPolicySets(), 
                                           false);
                }
                
                PolicyConfigurationUtil.computeBindingIntentsAndPolicySets(reference);
                PolicyConfigurationUtil.determineApplicableBindingPolicySets(reference, null);
            } catch ( Exception e ) {
                error(monitor, "PolicyRelatedException", reference, e);
                //throw new RuntimeException(e);
            }
        }
    }
    
    private void addPoliciesFromPromotedService(CompositeService compositeService) {
        //inherit intents and policies from promoted service
        PolicyComputationUtils.addInheritedIntents(compositeService.getPromotedService().getRequiredIntents(), 
                            compositeService.getRequiredIntents());
        PolicyComputationUtils.addInheritedPolicySets(compositeService.getPromotedService().getPolicySets(), 
                               compositeService.getPolicySets(), true);
    }
    
    private void addPoliciesFromPromotedReference(CompositeReference compositeReference) {
        for ( Reference promotedReference : compositeReference.getPromotedReferences() ) {
           PolicyComputationUtils.addInheritedIntents(promotedReference.getRequiredIntents(), 
                               compositeReference.getRequiredIntents());
       
           PolicyComputationUtils.addInheritedPolicySets(promotedReference.getPolicySets(), 
                                  compositeReference.getPolicySets(), true);
        }
    } 
    
}
