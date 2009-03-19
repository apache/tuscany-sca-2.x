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

import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that creates endpoint models for component services.
 *
 * @version $Rev$ $Date$
 */
public class ComponentServiceEndpointBuilderImpl implements CompositeBuilder {
    private AssemblyFactory assemblyFactory;

    public ComponentServiceEndpointBuilderImpl(AssemblyFactory assemblyFactory) {
        this.assemblyFactory = assemblyFactory;
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.ComponentReferenceEndpointReferenceBuilder";
    }

    /**
     * Create endpoint models for all component services.
     * 
     * @param composite
     */
    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
    
        // process top level composite services
        // TODO - I don't think OASIS spec doesn't allows composite references in the domain composite
        //
        // processCompositeServices(composite);
        
        // process component services
        processComponentServices(composite);
 
    }
    
    private void processCompositeServices(Composite composite) {
        // top level services are treated slightly differently
        // as no component will use these top level services 
        // as part of its component type. In this case we push down 
        // the service configuration to create a new endpoint on the
        // component from which the service is promoted
        for (Service service : composite.getServices()) {
            
            Component promotedComponent = ((CompositeService)service).getPromotedComponent();
            ComponentService promotedService = ((CompositeService)service).getPromotedService();
            
            if (promotedService != null) {
                for (Binding binding : service.getBindings()){
                    Endpoint2 endpoint = assemblyFactory.createEndpoint();
                    endpoint.setComponent(promotedComponent);
                    endpoint.setService(promotedService);
                    endpoint.setBinding(binding);
                    endpoint.setUnresolved(false);
                    promotedService.getEndpoints().add(endpoint);
                }
            }
        }
    }
    
    private void processComponentServices(Composite composite) {

        for (Component component : composite.getComponents()) {
           
            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                processComponentServices((Composite)implementation);
            }

            // create an endpoint for each component service binding
            for (ComponentService service : component.getServices()) {
                
                Component endpointComponent = component;
                ComponentService endpointService = service;
                
                // TODO - EPR - We maintain all endpoints at the right level now
                //              but endpoints for promoting services must point down
                //              to the services they promote. 
                if (service.getService() instanceof CompositeService) {
                    CompositeService compositeService = (CompositeService)service.getService();
                    endpointService = ServiceConfigurationUtil.getPromotedComponentService(compositeService);
                    endpointComponent = ServiceConfigurationUtil.getPromotedComponent(compositeService);
                } 
                
                // if this service has a callback get the callback endpoint references
                List<EndpointReference2> callbackEndpointReferences = null;
                
                if ((service.getInterfaceContract() != null) &&
                    (service.getInterfaceContract().getCallbackInterface() != null)){
                    // find the callback reference
                    for ( Reference reference : component.getReferences()){
                        if ( reference.getName().equals(service.getName())){
                            callbackEndpointReferences = reference.getEndpointReferences();
                            break;
                        }
                    } 
                }
                
                for (Binding binding : service.getBindings()){
                    Endpoint2 endpoint = assemblyFactory.createEndpoint();
                    endpoint.setComponent(endpointComponent);
                    endpoint.setService(endpointService);
                    endpoint.setBinding(binding);
                    endpoint.getCallbackEndpointReferences().addAll(callbackEndpointReferences);
                    endpoint.setUnresolved(false);
                    service.getEndpoints().add(endpoint);
                }
            }
        }
    }

}
