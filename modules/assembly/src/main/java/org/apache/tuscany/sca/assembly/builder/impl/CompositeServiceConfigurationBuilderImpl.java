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
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that handles the configuration of composite services.
 *
 * @version $Rev$ $Date$
 */
public class CompositeServiceConfigurationBuilderImpl implements CompositeBuilder {
    private AssemblyFactory assemblyFactory;

    public CompositeServiceConfigurationBuilderImpl(ExtensionPointRegistry registry) {
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeServiceConfigurationBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {

        // Process nested composites recursively
        configureNestedCompositeServices(composite);

        // TODO - In OASIS the treatment of top level composite services it different
        // Process top level composite services
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;

            // Get the next lower level promoted service
            ComponentService promotedService = compositeService.getPromotedService();
            if (promotedService != null) {

                // Set the bindings using the top level bindings to override the lower level bindings
                if (!compositeService.isOverridingBindings() && promotedService.isOverridingBindings()) {
                    compositeService.getBindings().clear();
                    for (Binding binding : promotedService.getBindings()) {
                        try {
                            compositeService.getBindings().add((Binding)binding.clone());
                        } catch (CloneNotSupportedException ex) {
                            // this binding can't be used in the promoted service
                        }
                    }
                }
                if (compositeService.getInterfaceContract() != null && compositeService.getInterfaceContract()
                    .getCallbackInterface() != null) {
                    if (!isCallbackOverridingBindings(compositeService) 
                        && isCallbackOverridingBindings(promotedService)) {
                        if (compositeService.getCallback() != null) {
                            compositeService.getCallback().getBindings().clear();
                        } else {
                            compositeService.setCallback(assemblyFactory.createCallback());
                        }
                        for (Binding binding : promotedService.getCallback().getBindings()) {
                            try {
                                compositeService.getCallback().getBindings().add((Binding)binding.clone());
                            } catch (CloneNotSupportedException ex) {
                                // this binding can't be used in the promoted service
                            }
                        }
                    }
                }
            }
        }
    }

    private void configureNestedCompositeServices(Composite composite) {

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // First process nested composites
                configureNestedCompositeServices((Composite)implementation);

                // Process the component services declared on components in this composite
                for (ComponentService componentService : component.getServices()) {
                    Service implService = componentService.getService();
                    if (implService != null && implService instanceof CompositeService) {
                        CompositeService compositeService = (CompositeService)implService;

                        // Get the next lower level promoted service
                        ComponentService promotedService = compositeService.getPromotedService();
                        if (promotedService != null) {

                            // Set the bindings using the top level bindings to override the lower level bindings
                            if (!compositeService.isOverridingBindings() && promotedService.isOverridingBindings()) {
                                compositeService.getBindings().clear();
                                for (Binding binding : promotedService.getBindings()) {
                                    try {
                                        compositeService.getBindings().add((Binding)binding.clone());
                                    } catch (CloneNotSupportedException ex) {
                                        // this binding can't be used in the promoted service
                                    }
                                }
                            }
                            if (!componentService.isOverridingBindings() && compositeService.isOverridingBindings()) {
                                componentService.getBindings().clear();
                                componentService.getBindings().addAll(compositeService.getBindings());
                            }
                            if (componentService.getInterfaceContract() != null && componentService
                                .getInterfaceContract().getCallbackInterface() != null) {
                                if (!isCallbackOverridingBindings(compositeService) 
                                    && isCallbackOverridingBindings(promotedService)) {
                                    if (compositeService.getCallback() != null) {
                                        compositeService.getCallback().getBindings().clear();
                                    } else {
                                        compositeService.setCallback(assemblyFactory.createCallback());
                                    }
                                    for (Binding binding : promotedService.getCallback().getBindings()) {
                                        try {
                                            compositeService.getCallback().getBindings().add((Binding)binding.clone());
                                        } catch (CloneNotSupportedException ex) {
                                            // this binding can't be used in the promoted service
                                        }
                                    }
                                }
                                if (!(isCallbackOverridingBindings(componentService)) && isCallbackOverridingBindings(compositeService)) {
                                    if (componentService.getCallback() != null) {
                                        componentService.getCallback().getBindings().clear();
                                    } else {
                                        componentService.setCallback(assemblyFactory.createCallback());
                                    }
                                    componentService.getCallback().getBindings().addAll(compositeService.getCallback()
                                        .getBindings());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean isCallbackOverridingBindings(Contract contract) {
        Callback callback = contract.getCallback();
        return callback != null && !callback.getBindings().isEmpty();
    }

}
