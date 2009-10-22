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

import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * creates endpoint models for component services.
 */
public class EndpointBuilderImpl implements CompositeBuilder {
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
    public Composite build(Composite composite, BuilderContext context) throws CompositeBuilderException {

        processComponentServices(composite, context);
        return composite;

    } 

    private void processComponentServices(Composite composite, BuilderContext context) {

        Monitor monitor = context.getMonitor();
        
        for (Component component : composite.getComponents()) {

            try {
                monitor.pushContext("Component: " + component.getName().toString());
    
                // recurse for composite implementations
                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    processComponentServices((Composite)implementation, context);
                }
    
                // create an endpoint for each component service binding
                for (ComponentService service : component.getServices()) {
                    try {
                        monitor.pushContext("Service: " + service.getName());
                        
                        //verify JAX-WS async assertions as in JavaCAA section 11.1
                        List<Operation> asyncOperations = null;
                        try {
                            asyncOperations = (List<Operation>) service.getInterfaceContract().getInterface().getAttributes().get("JAXWS-ASYNC-OPERATIONS");
                        }catch(Exception e) {
                            //ignore
                        }
                        
                        if(asyncOperations != null) {
                            if( ! asyncOperations.isEmpty()) {
        
                                //error JCA100006
        
                                //FIXME create a java validation message resource bundle
                                Monitor.error(monitor, 
                                              this, 
                                              null,
                                              "[JCA100006] JAX-WS client-side asynchronous pooling and callback methods are not allowed in service interfaces", 
                                              service, 
                                              service.getName());                  
                            }
                        }
        
        
                        
        
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
                    } finally {
                        monitor.popContext();
                    }                   
                }
            } finally {
                monitor.popContext();
            }                  
        }
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.EndpointBuilder";
    }
}
