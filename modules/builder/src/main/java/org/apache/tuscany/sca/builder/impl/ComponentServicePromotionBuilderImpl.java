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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that handles the creation of promoted services.
 *
 * @version $Rev$ $Date$
 */
public class ComponentServicePromotionBuilderImpl implements CompositeBuilder {
    private AssemblyFactory assemblyFactory;

    public ComponentServicePromotionBuilderImpl(AssemblyFactory assemblyFactory) {
        this.assemblyFactory = assemblyFactory;
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.ComponentServicePromotionBuilder";
    }

    public Composite build(Composite composite, Definitions definitions, Monitor monitor)
        throws CompositeBuilderException {

        // Process nested composites recursively
        configureNestedCompositeServices(composite);
        return composite;
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

                        // Get the innermost promoted service
                        ComponentService promotedService =
                            ServiceConfigurationUtil.getPromotedComponentService(compositeService);
                        if (promotedService != null) {
                            Component promotedComponent =
                                ServiceConfigurationUtil.getPromotedComponent(compositeService);

                            // Create a new component service to represent this
                            // component service on the promoted component
                            ComponentService newComponentService = assemblyFactory.createComponentService();
                            newComponentService.setName("$promoted$" + component.getName()
                                + "$slash$"
                                + componentService.getName());
                            promotedComponent.getServices().add(newComponentService);
                            newComponentService.setService(promotedService.getService());
                            newComponentService.getBindings().addAll(componentService.getBindings());
                            newComponentService.setInterfaceContract(componentService.getInterfaceContract());

                            if (componentService.getInterfaceContract() != null && componentService
                                .getInterfaceContract().getCallbackInterface() != null) {
                                newComponentService.setCallback(assemblyFactory.createCallback());
                                newComponentService.getCallback().getBindings().addAll(componentService.getCallback()
                                    .getBindings());
                            }

                            // Change the composite service to now promote the
                            // newly created component service directly
                            compositeService.setPromotedComponent(promotedComponent);
                            compositeService.setPromotedService(newComponentService);
                        }
                    }
                }
            }
        }
    }

}
