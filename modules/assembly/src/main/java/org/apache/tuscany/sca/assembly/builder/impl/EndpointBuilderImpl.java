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
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * creates endpoint models for component services.
 */
public class EndpointBuilderImpl {
    private AssemblyFactory assemblyFactory;

    public EndpointBuilderImpl(ExtensionPointRegistry registry) {
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
    }

    /**
     * Create endpoint models for all component services.
     * 
     * @param composite - the top-level composite to build the models for
     * @param definitions
     * @param monitor - a Monitor for logging errors
     */
    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {

        processComponentServices(composite);

    } // end method build

    private void processComponentServices(Composite composite) {

        for (Component component : composite.getComponents()) {

            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                processComponentServices((Composite)implementation);
            }

            // create an endpoint for each component service binding
            for (ComponentService service : component.getServices()) {

                /* change to finding the promoted component and service
                 * when the wire is created as storing them here leads to 
                 * the wrong URI being calculated
                Component endpointComponent = component;
                ComponentService endpointService = service;

                // TODO - EPR - We maintain all endpoints at the right level now
                //              but endpoints for promoting services must point down
                //              to the services they promote. 
                if (service.getService() instanceof CompositeService) {
                    CompositeService compositeService = (CompositeService)service.getService();
                    endpointService = ServiceConfigurationUtil.getPromotedComponentService(compositeService);
                    endpointComponent = ServiceConfigurationUtil.getPromotedComponent(compositeService);
                } // end if
                */

                for (Binding binding : service.getBindings()) {
                    Endpoint endpoint = assemblyFactory.createEndpoint();
                    endpoint.setComponent(component);
                    endpoint.setService(service);
                    endpoint.setBinding(binding);
                    endpoint.setUnresolved(false);
                    service.getEndpoints().add(endpoint);
                } // end for
            }
        }
    } 
} 
