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
import java.util.Vector;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * A composite builder that creates endpoint models for component services.
 *
 * @version $Rev$ $Date$
 */
public class ComponentServiceEndpointBuilderImpl implements CompositeBuilder {
    private AssemblyFactory assemblyFactory;

    // Testing
    //boolean useNew = true;
    boolean useNew = false;

    public ComponentServiceEndpointBuilderImpl(AssemblyFactory assemblyFactory) {
        this.assemblyFactory = assemblyFactory;
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.ComponentReferenceEndpointReferenceBuilder";
    }

    /**
     * Create endpoint models for all component services.
     * 
     * @param composite - the top-level composite to build the models for
     * @param definitions
     * @param monitor - a Monitor for logging errors
     */
    public Composite build(Composite composite, Definitions definitions, Monitor monitor)
        throws CompositeBuilderException {

        // process component services
        if (!useNew) {
            processComponentServices(composite);
        } // end if
        processComponentServices2(composite);
        return composite;

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
    } // end method processComponentServices

    /**
     * @param composite - the composite which contains the component services
     */
    private void processComponentServices2(Composite composite) {
        for (Component component : composite.getComponents()) {
            for (ComponentService service : component.getServices()) {
                EndpointInfo theInfo = scanComponentService(component, service, null);

                List<Binding> theBindings = theInfo.getBindings();
                // Create an endpoint for each binding which applies to this service
                // and copy across the information relating to the endpoint.
                for (Binding binding : theBindings) {
                    Endpoint endpoint = assemblyFactory.createEndpoint();
                    endpoint.setComponent(theInfo.getComponent());
                    endpoint.setService(theInfo.getComponentService());
                    endpoint.setBinding(binding);
                    endpoint.setInterfaceContract(theInfo.getInterfaceContract());
                    endpoint.getRequiredIntents().addAll(theInfo.getIntents());
                    endpoint.getPolicySets().addAll(theInfo.getPolicySets());
                    endpoint.setUnresolved(false);
                    // Add the endpoint to the component service
                    if (useNew) {
                        // Add to top level and leaf level services, if different
                        service.getEndpoints().add(endpoint);
                        ComponentService leafService = theInfo.getComponentService();
                        if (service != leafService) {
                            leafService.getEndpoints().add(endpoint);
                        } // end if 
                    } // end if
                    // debug
                    // disabled for the time being - SL
                    //System.out.println( "Endpoint created for Component = " + component.getName() + " Leaf component = " + 
                    //		endpoint.getComponent().getName() + " service = " + 
                    //		endpoint.getService().getName() + " binding = " + endpoint.getBinding() );
                } // end for	
            } // end for
            // Handle composites as implementations
            if (component.getImplementation() instanceof Composite) {
                processComponentServices2((Composite)component.getImplementation());
            } // end if
        } // end for
    } // end method processComponentServices2

    /**
     * Scan a component service for endpoint information, recursing down to the leafmost promoted service if the component service is
     * implemented by a Composite service
     * @param component - the component
     * @param service - the component service
     * @param theInfo - an EndpointInfo object in which the endpoint information is accumulated. If null on entry, a new EndpointInfo object is created
     * @return - the EndpointInfo object containing the information about the component service
     */
    private EndpointInfo scanComponentService(Component component, ComponentService service, EndpointInfo theInfo) {
        if (theInfo == null) {
            theInfo = new EndpointInfo();
        } // end if

        theInfo.setBindings(service.getBindings());
        theInfo.setInterfaceContract(service.getInterfaceContract());
        theInfo.setIntents(service.getRequiredIntents());
        theInfo.setPolicySets(service.getPolicySets());

        Service implService = service.getService();
        if (implService instanceof CompositeService) {
            // If it is implemented by a Composite, scan through the promoted service
            ComponentService promotedService = ((CompositeService)implService).getPromotedService();
            Component promotedComponent = ((CompositeService)implService).getPromotedComponent();
            if (promotedService != null) {
                scanComponentService(promotedComponent, promotedService, theInfo);
            } else {
                // If its a composite service with no promoted component service, it's an error
            } // end if
        } else {
            // Otherwise the component and service are the ones at this level
            theInfo.setComponent(component);
            theInfo.setComponentService(service);
        } //end if

        return theInfo;
    } // end method scanPromotedComponentService

    private class EndpointInfo {
        private Component leafComponent = null;
        private ComponentService leafComponentService = null;
        private List<Binding> bindings = null;
        private InterfaceContract contract;
        private List<Intent> intents = new Vector<Intent>();
        private List<PolicySet> policySets = null;

        void setComponent(Component component) {
            leafComponent = component;
        } // end method

        Component getComponent() {
            return leafComponent;
        } // end method

        void setComponentService(ComponentService service) {
            leafComponentService = service;
        } // end method

        ComponentService getComponentService() {
            return leafComponentService;
        } // end method

        void setBindings(List<Binding> theBindings) {
            // RULE: Bindings from higher in the hierarchy take precedence
            if (bindings == null) {
                bindings = theBindings;
            } // end if
        } // end method

        List<Binding> getBindings() {
            return bindings;
        } // end method

        void setInterfaceContract(InterfaceContract theInterface) {
            // RULE: Interface contract from higher in the hierarchy takes precedence
            if (contract == null) {
                contract = theInterface;
            } // end if
        } // end method

        InterfaceContract getInterfaceContract() {
            return contract;
        } // end method

        List<Intent> getIntents() {
            return intents;
        } // end method

        void setIntents(List<Intent> intents) {
            // RULE: Intents accumulate from all levels in the hierarchy
            this.intents.addAll(intents);
        } // end method

        List<PolicySet> getPolicySets() {
            return policySets;
        } // end method

        void setPolicySets(List<PolicySet> policySets) {
            // RULE: Policy Sets from higher in the hierarchy override those lower
            if (this.policySets == null) {
                this.policySets = policySets;
            } // end if
        } // end method

    } // end class EndpointInfo

} // end class
