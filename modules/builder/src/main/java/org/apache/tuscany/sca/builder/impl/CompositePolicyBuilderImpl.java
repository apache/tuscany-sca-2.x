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

import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;

/**
 * A composite builder that computes policy sets based on attached intents and policy sets.
 * Useful if you want to build the model without making any runtime decisions such as
 * reference/services matching
 *
 * @version $Rev$ $Date$
 */
public class CompositePolicyBuilderImpl extends ComponentPolicyBuilderImpl implements CompositeBuilder {

    private CompositeBuilder policyAppliesToBuilder = null;
    
    public CompositePolicyBuilderImpl(ExtensionPointRegistry registry) {
        super(registry);
        
        policyAppliesToBuilder = new PolicyAppliesToBuilderImpl(registry);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositePolicyBuilder";
    }

    public Composite build(Composite composite, BuilderContext context) throws CompositeBuilderException {
        computePolicies(composite, context);
        checkPolicies(composite, context);
        buildPolicies(composite, context);
        return composite;
    }

    protected void computePolicies(Composite composite, BuilderContext context) {
        Monitor monitor = context.getMonitor();
        monitor.pushContext("Composite: " + composite.getName().toString());

        try {
            resolveAndCheck(composite, context);

            // compute policies recursively
            for (Component component : composite.getComponents()) {
                monitor.pushContext("Component: " + component.getName());

                //resolve component level
                resolveAndCheck(component, context);
                
                try {
                    Implementation implementation = component.getImplementation();
                    
                    for (ComponentService componentService : component.getServices()) {
                        monitor.pushContext("Service: " + componentService.getName());

                        try {
                            resolveAndCheck(componentService, context);

                            if (componentService.getInterfaceContract() != null) {
                                resolveAndCheck(componentService.getInterfaceContract().getInterface(), context);

                                resolveAndCheck(componentService.getInterfaceContract().getCallbackInterface(), context);

                            }

                            for (Endpoint ep : componentService.getEndpoints()) {
                                if (componentService.getInterfaceContract() != null) {
                                    // Inherit from the component.service.interface
                                    inherit(ep, Intent.Type.interaction, true, componentService.getInterfaceContract().getInterface());
                                }
                                
                                // Inherit from composite/component/service
                                inherit(ep, Intent.Type.interaction, true, composite, ep.getComponent(), ep.getService());
                                
                                // Inherit from binding
                                inherit(ep, Intent.Type.interaction, true, ep.getBinding());

                                // Replace profile intents with their required intents
                                // Replace unqualified intents if there is a qualified intent in the list
                                // Replace qualifiable intents with the default qualied intent
                                resolveAndNormalize(ep, context);
                                
                                // Remove the intents whose @contraints do not include the current element
                                removeConstrainedIntents(ep, context);
                                
                                // check that all intents are resolved
                                checkIntentsResolved(ep, context);

                                // check that the resulting endpoint has no mutually exclusive intents
                                checkMutualExclusion(ep, context);
                            }
                        } finally {
                            monitor.popContext();
                        }
                    }

                    for (ComponentReference componentReference : component.getReferences()) {
                        monitor.pushContext("Reference: " + componentReference.getName().toString());

                        try {

                            if (componentReference.getInterfaceContract() != null) {
                                resolveAndCheck(componentReference.getInterfaceContract().getInterface(), context);

                                resolveAndCheck(componentReference.getInterfaceContract().getCallbackInterface(),
                                                context);
                            }

                            for (EndpointReference epr : componentReference.getEndpointReferences()) {

                                // Inherit from the component.reference.interface
                                if (componentReference.getInterfaceContract() != null) {
                                    inherit(epr, Intent.Type.interaction, true, componentReference.getInterfaceContract().getInterface());
                                }

                                // Inherit from composite/component/reference
                                inherit(epr, Intent.Type.interaction, true, composite, epr.getComponent(), epr.getReference());
                                
                                // Inherit from binding
                                inherit(epr, Intent.Type.interaction, true, epr.getBinding());

                                // Replace profile intents with their required intents
                                // Replace unqualified intents if there is a qualified intent in the list
                                // Replace qualifiable intents with the default qualified intent
                                resolveAndNormalize(epr, context);
                                
                                // Remove the intents whose @contraints do not include the current element
                                removeConstrainedIntents(epr, context);
                                
                                // check that all intents are resolved
                                checkIntentsResolved(epr, context);

                                // check that the resulting endpoint reference has no mutually exclusive intents
                                checkMutualExclusion(epr, context);
                            }
                        } finally {
                            monitor.popContext();
                        }
                    }

                    if (implementation instanceof Composite) {
                        inherit(implementation, Intent.Type.implementation, true, component, composite);
                        checkIntentsResolved(implementation, context);
                        computePolicies((Composite)implementation, context);
                    } else {
                        resolveAndCheck(implementation, context);
                        if (implementation != null) {
                            inherit(implementation, Intent.Type.implementation, true, component, composite);
                            
                            // Remove the intents whose @contraints do not include the current element
                            removeConstrainedIntents(implementation, context);
                            
                            // check that all intents are resolved
                            checkIntentsResolved(implementation, context);
                        }
                    }
                } finally {
                    monitor.popContext();
                }
            }
        } finally {
            monitor.popContext();
        }
    }
    
    /**
     * This is mainly about removing policies that don't "applyTo" the element where
     * they have ended up after all the attachment and inheritance processing
     * 
     * @param composite
     * @param context
     */
    protected void checkPolicies(Composite composite, BuilderContext context) throws CompositeBuilderException{
        policyAppliesToBuilder.build(composite, context);
    }

    protected void buildPolicies(Composite composite, BuilderContext context) {

        // build policies recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                buildPolicies((Composite)implementation, context);
            }
        }

        for (Component component : composite.getComponents()) {

            for (ComponentService componentService : component.getServices()) {
                for (Endpoint ep : componentService.getEndpoints()) {
                    Set<QName> policyNames = getPolicyNames(ep);
                    
                    // check that only one policy language is present in the endpoint's policy sets
                    if (policyNames.size() > 1){
                        error(context.getMonitor(), 
                              "MultiplePolicyLanguagesInEP", 
                              this,
                              ep.toString(), 
                              policyNames.toString());
                    } else {
                        for (QName policyType : policyNames) {
                            PolicyBuilder builder = builders.getPolicyBuilder(policyType);
                            if (builder != null) {
                                builder.build(ep, context);
                            }
                        }
                    }
                }
            }

            for (ComponentReference componentReference : component.getReferences()) {
                for (EndpointReference epr : componentReference.getEndpointReferences()) {
                    Set<QName> policyNames = getPolicyNames(epr);
                    
                    // check that only one policy language is present in the endpoint references's policy sets
                    if (policyNames.size() > 1){
                        error(context.getMonitor(),  
                              "MultiplePolicyLanguagesInEPR",
                              this,
                              epr.toString(), 
                              policyNames.toString());
                    } else {                    
                        for (QName policyType : policyNames) {
                            PolicyBuilder builder = builders.getPolicyBuilder(policyType);
                            if (builder != null) {
                                builder.build(epr, context);
                            }
                        }
                    }
                }
            }

            Implementation implementation = component.getImplementation();
            if (implementation != null) {
                Set<QName> policyNames = getPolicyNames(implementation);
                
                // check that only one policy language is present in the implementations's policy sets
                if (policyNames.size() > 1){
                    error(context.getMonitor(), 
                          "MultiplePolicyLanguagesInImplementation", 
                          this,
                          component.toString(), 
                          policyNames.toString());
                } else {
                    for (QName policyType : policyNames) {
                        PolicyBuilder builder = builders.getPolicyBuilder(policyType);
                        if (builder != null) {
                            builder.build(component, implementation, context);
                        }
                    }
                }
            }
        }
    }
}
