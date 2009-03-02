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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.DefaultEndpointBuilder;
import org.apache.tuscany.sca.assembly.builder.EndpointBuilder;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that wires component references.
 *
 * @version $Rev$ $Date$
 */
public class ComponentReferenceWireBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {

    protected EndpointFactory endpointFactory;
    private EndpointBuilder endpointBuilder;
    
    public ComponentReferenceWireBuilderImpl(AssemblyFactory assemblyFactory, EndpointFactory endpointFactory, InterfaceContractMapper interfaceContractMapper) {
        super(assemblyFactory, null, null, null, interfaceContractMapper);
        this.endpointFactory = endpointFactory;
        this.endpointBuilder = new DefaultEndpointBuilder();
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.ComponentReferenceWireBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        wireComponentReferences(composite, monitor);
    }
    
    /**
     * Wire component references to component services and connect promoted
     * services/references to component services/references inside a composite.
     * 
     * @param composite
     */
    protected void wireComponentReferences(Composite composite, Monitor monitor) {

        // Wire nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                wireComponentReferences((Composite)implementation, monitor);
            }
        }

        // Index components, services and references
        Map<String, Component> components = new HashMap<String, Component>();
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
        indexComponentsServicesAndReferences(composite, components, componentServices, componentReferences);

        // Connect composite services and references to the component
        // services and references that they promote
        //connectCompositeServices(composite, components, componentServices);
        //connectCompositeReferences(composite, componentReferences);

        // Compute the policies before connecting component references
        //computePolicies(composite);

        // Connect component references as described in wires
        connectWires(composite, componentServices, componentReferences, monitor);

        // Connect component references to their targets
        connectComponentReferences(composite, components, componentServices, componentReferences, monitor);

        // Validate that references are wired or promoted, according
        // to their multiplicity
        for (ComponentReference componentReference : componentReferences.values()) {
            if (!ReferenceConfigurationUtil.validateMultiplicityAndTargets(componentReference.getMultiplicity(), componentReference
                .getTargets(), componentReference.getBindings())) {
                if (componentReference.getTargets().isEmpty()) {

                    // No warning if the reference is promoted out of the current composite
                    boolean promoted = false;
                    for (Reference reference : composite.getReferences()) {
                        CompositeReference compositeReference = (CompositeReference)reference;
                        if (compositeReference.getPromotedReferences().contains(componentReference)) {
                            promoted = true;
                            break;
                        }
                    }
                    if (!promoted && !componentReference.isCallback()) {
                        warning(monitor, "ReferenceWithoutTargets", composite, composite.getName().toString(), componentReference.getName());
                    }
                } else {
                    warning(monitor, "TooManyReferenceTargets", composite, componentReference.getName());
                }
            }
        }
        
        // Finally clear the original reference target lists as we now have
        // bindings to represent the targets
        for (ComponentReference componentReference : componentReferences.values()) {
            componentReference.getTargets().clear();
        }
    }  
    
    /**
     * Resolve wires and connect the sources to their targets
     * 
     * @param composite
     * @param componentServices
     * @param componentReferences
     * @param problems
     */
    private void connectWires(Composite composite,
                              Map<String, ComponentService> componentServices,
                              Map<String, ComponentReference> componentReferences,
                              Monitor monitor) {
    
        // For each wire, resolve the source reference, the target service, and
        // add it to the list of targets of the reference
        List<Wire> wires = composite.getWires();
        for (int i = 0, n = wires.size(); i < n; i++) {
            Wire wire = wires.get(i);
    
            ComponentReference resolvedReference;
            ComponentService resolvedService;
    
            // Resolve the source reference
            ComponentReference source = wire.getSource();
            if (source != null && source.isUnresolved()) {
                resolvedReference = componentReferences.get(source.getName());
                if (resolvedReference != null) {
                    wire.setSource(resolvedReference);
                } else {
                    warning(monitor, "WireSourceNotFound", composite, source.getName());
                }
            } else {
                resolvedReference = wire.getSource();
            }
    
            // Resolve the target service
            ComponentService target = wire.getTarget();
            if (target != null && target.isUnresolved()) {
                resolvedService = componentServices.get(target.getName());
                if (resolvedService != null) {
                    wire.setTarget(target);
                } else {
                    warning(monitor, "WireTargetNotFound", composite, source.getName());
                }
            } else {
                resolvedService = wire.getTarget();
            }
    
            // Add the target service to the list of targets of the
            // reference
            if (resolvedReference != null && resolvedService != null) {
                // Check that the target component service provides
                // a superset of
                // the component reference interface
                if (resolvedReference.getInterfaceContract() == null || interfaceContractMapper
                    .isCompatible(resolvedReference.getInterfaceContract(), resolvedService.getInterfaceContract())) {
    
                    //resolvedReference.getTargets().add(resolvedService);
                    resolvedReference.getTargets().add(wire.getTarget());
                } else {
                    warning(monitor, "WireIncompatibleInterface", composite, source.getName(), target.getName());
                }
            }
        }
    
        // Clear the list of wires
        composite.getWires().clear();
    }
    
    private List<Endpoint> createComponentReferenceTargets(Composite composite,
            Map<String, Component> components,
            Map<String, ComponentService> componentServices,
            ComponentReference componentReference, Monitor monitor) {

        List<Endpoint> endpoints = new ArrayList<Endpoint>();

        if (componentReference.getAutowire() == Boolean.TRUE
                && componentReference.getTargets().isEmpty()) {

            // Find suitable targets in the current composite for an
            // autowired reference
            Multiplicity multiplicity = componentReference.getMultiplicity();
            for (Component targetComponent : composite.getComponents()) {
                // prevent autowire connecting to self
                boolean skipSelf = false;
                for (ComponentReference targetComponentReference : targetComponent
                        .getReferences()) {
                    if (componentReference == targetComponentReference) {
                        skipSelf = true;
                    }
                }

                if (!skipSelf) {
                    for (ComponentService targetComponentService : targetComponent
                            .getServices()) {
                        if (componentReference.getInterfaceContract() == null
                                || interfaceContractMapper.isCompatible(
                                        componentReference
                                                .getInterfaceContract(),
                                        targetComponentService
                                                .getInterfaceContract())) {

                            Endpoint endpoint = endpointFactory
                                    .createEndpoint();
                            endpoint.setTargetName(targetComponent.getName());
                            endpoint.setSourceComponent(null); // TODO - fixed
                                                               // up at start
                            endpoint
                                    .setSourceComponentReference(componentReference);
                            endpoint.setInterfaceContract(componentReference
                                    .getInterfaceContract());
                            endpoint.setTargetComponent(targetComponent);
                            endpoint
                                    .setTargetComponentService(targetComponentService);
                            endpoint.getCandidateBindings().addAll(
                                    componentReference.getBindings());
                            endpoints.add(endpoint);

                            if (multiplicity == Multiplicity.ZERO_ONE
                                    || multiplicity == Multiplicity.ONE_ONE) {
                                break;
                            }
                        }
                    }
                }
            }

            if (multiplicity == Multiplicity.ONE_N
                    || multiplicity == Multiplicity.ONE_ONE) {
                if (endpoints.size() == 0) {
                    warning(monitor, "NoComponentReferenceTarget",
                            componentReference, componentReference.getName());
                }
            }

        } else if (!componentReference.getTargets().isEmpty()) {

            // Check if the component reference does not mix the use of
            // endpoints specified via
            // binding elements with target endpoints specified via the target
            // attribute
            for (Binding binding : componentReference.getBindings()) {
                if (binding.getURI() != null) {
                    warning(monitor, "ReferenceEndPointMixWithTarget",
                            composite, componentReference.getName());
                }
            }

            // Resolve targets specified on the component reference
            for (ComponentService componentService : componentReference
                    .getTargets()) {

                // Resolve the target component and service
                String name = componentService.getName();
                ComponentService targetComponentService = componentServices
                        .get(name);
                Component targetComponent;
                int s = name.indexOf('/');
                if (s == -1) {
                    targetComponent = components.get(name);
                } else {
                    targetComponent = components.get(name.substring(0, s));
                }

                if (targetComponentService != null) {

                    // Check that the target component service provides
                    // a superset of the component reference interface
                    if (componentReference.getInterfaceContract() == null
                            || interfaceContractMapper.isCompatible(
                                    componentReference.getInterfaceContract(),
                                    targetComponentService
                                            .getInterfaceContract())) {

                        Endpoint endpoint = endpointFactory.createEndpoint();
                        endpoint.setTargetName(targetComponent.getName());
                        endpoint.setSourceComponent(null); // TODO - fixed up at
                                                           // start
                        endpoint
                                .setSourceComponentReference(componentReference);
                        endpoint.setInterfaceContract(componentReference
                                .getInterfaceContract());
                        endpoint.setTargetComponent(targetComponent);
                        endpoint
                                .setTargetComponentService(targetComponentService);
                        endpoint.getCandidateBindings().addAll(
                                componentReference.getBindings());
                        endpoints.add(endpoint);

                        // mark the reference target as resolved. Used later
                        // when we are looking to
                        // see if an sca binding is associated with a resolved
                        // target or not
                        componentService.setUnresolved(false);
                    } else {
                        warning(monitor, "ReferenceIncompatibleInterface",
                                composite, composite.getName().toString(),
                                componentReference.getName(), componentService
                                        .getName());
                    }
                } else {
                    // add all the reference bindings into the target so that
                    // they
                    // can be used for comparison when the target is resolved at
                    // runtime
                    componentService.getBindings().addAll(
                            componentReference.getBindings());

                    Endpoint endpoint = endpointFactory.createEndpoint();
                    endpoint.setTargetName(name);
                    endpoint.setSourceComponent(null); // TODO - fixed up at
                                                       // start
                    endpoint.setSourceComponentReference(componentReference);
                    endpoint.setInterfaceContract(componentReference
                            .getInterfaceContract());
                    endpoint.getCandidateBindings().addAll(
                            componentReference.getBindings());
                    endpoints.add(endpoint);

                    // The bindings will be cloned back into the reference when
                    // the
                    // target is finally resolved.
                    warning(monitor, "ComponentReferenceTargetNotFound",
                            composite, composite.getName().toString(),
                            componentService.getName());
                }
            }
        } else if ((componentReference.getReference() != null)
                && (!componentReference.getReference().getTargets().isEmpty())) {

            // Resolve targets from the corresponding reference in the
            // componentType
            for (ComponentService componentService : componentReference
                    .getReference().getTargets()) {

                // Resolve the target component and service
                String name = componentService.getName();
                ComponentService targetComponentService = componentServices
                        .get(name);
                Component targetComponent;
                int s = name.indexOf('/');
                if (s == -1) {
                    targetComponent = components.get(name);
                } else {
                    targetComponent = components.get(name.substring(0, s));
                }

                if (targetComponentService != null) {

                    // Check that the target component service provides
                    // a superset of
                    // the component reference interface
                    if (componentReference.getInterfaceContract() == null
                            || interfaceContractMapper.isCompatible(
                                    componentReference.getInterfaceContract(),
                                    targetComponentService
                                            .getInterfaceContract())) {

                        Endpoint endpoint = endpointFactory.createEndpoint();
                        endpoint.setTargetName(targetComponent.getName());
                        endpoint.setSourceComponent(null); // TODO - fixed up at
                                                           // start
                        endpoint
                                .setSourceComponentReference(componentReference);
                        endpoint.setInterfaceContract(componentReference
                                .getInterfaceContract());
                        endpoint.setTargetComponent(targetComponent);
                        endpoint
                                .setTargetComponentService(targetComponentService);
                        endpoint.getCandidateBindings().addAll(
                                componentReference.getBindings());
                        endpoints.add(endpoint);

                        // mark the reference target as resolved. Used later
                        // when we are looking to
                        // see if an sca binding is associated with a resolved
                        // target or not
                        componentService.setUnresolved(false);
                    } else {
                        warning(monitor, "ComponentIncompatibleInterface",
                                composite, componentReference.getName(),
                                componentService.getName());
                    }
                } else {
                    // add all the reference bindings into the target so that
                    // they
                    // can be used for comparison when the target is resolved at
                    // runtime
                    componentService.getBindings().addAll(
                            componentReference.getBindings());

                    // The bindings will be cloned back into the reference when
                    // the
                    // target is finally resolved.

                    Endpoint endpoint = endpointFactory.createEndpoint();
                    endpoint.setTargetName(name);
                    endpoint.setSourceComponent(null); // TODO - fixed up at
                                                       // start
                    endpoint.setSourceComponentReference(componentReference);
                    endpoint.setInterfaceContract(componentReference
                            .getInterfaceContract());
                    endpoint.getCandidateBindings().addAll(
                            componentReference.getBindings());
                    endpoints.add(endpoint);

                    warning(monitor, "ComponentReferenceTargetNotFound",
                            composite, composite.getName().toString(),
                            componentService.getName());
                }
            }
        } else if (componentReference.getAutowire() == Boolean.TRUE) {

            // Find suitable targets in the current composite for an
            // autowired reference
            Multiplicity multiplicity = componentReference.getMultiplicity();
            for (Component targetComponent : composite.getComponents()) {
                // prevent autowire connecting to self
                boolean skipSelf = false;
                for (ComponentReference targetComponentReference : targetComponent
                        .getReferences()) {
                    if (componentReference == targetComponentReference) {
                        skipSelf = true;
                    }
                }

                if (!skipSelf) {
                    for (ComponentService targetComponentService : targetComponent
                            .getServices()) {
                        if (componentReference.getInterfaceContract() == null
                                || interfaceContractMapper.isCompatible(
                                        componentReference
                                                .getInterfaceContract(),
                                        targetComponentService
                                                .getInterfaceContract())) {

                            Endpoint endpoint = endpointFactory
                                    .createEndpoint();
                            endpoint.setTargetName(targetComponent.getName());
                            endpoint.setSourceComponent(null); // TODO - fixed
                                                               // up at start
                            endpoint
                                    .setSourceComponentReference(componentReference);
                            endpoint.setInterfaceContract(componentReference
                                    .getInterfaceContract());
                            endpoint.setTargetComponent(targetComponent);
                            endpoint
                                    .setTargetComponentService(targetComponentService);
                            endpoint.getCandidateBindings().addAll(
                                    componentReference.getBindings());
                            endpoints.add(endpoint);

                            if (multiplicity == Multiplicity.ZERO_ONE
                                    || multiplicity == Multiplicity.ONE_ONE) {
                                break;
                            }
                        }
                    }
                }
            }

            if (multiplicity == Multiplicity.ONE_N
                    || multiplicity == Multiplicity.ONE_ONE) {
                if (endpoints.size() == 0) {
                    warning(monitor, "NoComponentReferenceTarget",
                            componentReference, componentReference.getName());
                }
            }
        }

        // if no endpoints have found so far retrieve any target names that are
        // in binding URIs
        if (endpoints.isEmpty()) {
            for (Binding binding : componentReference.getBindings()) {

                String uri = binding.getURI();

                // user hasn't put a uri on the binding so it's not a target
                // name
                if (uri == null) {
                    continue;
                }

                // user might have put a local target name in the uri so get
                // the path part and see if it refers to a target we know about
                // - if it does the reference binding will be matched with a
                // service binding
                // - if it doesn't it is assumed to be an external reference
                Component targetComponent = null;
                ComponentService targetComponentService = null;
                String path = null;

                try {
                    path = URI.create(uri).getPath();
                } catch (Exception ex) {
                    // just assume that no target is identified if
                    // a URI related exception is thrown
                }

                if (path != null) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }

                    // Resolve the target component and service
                    targetComponentService = componentServices.get(path);
                    int s = path.indexOf('/');
                    if (s == -1) {
                        targetComponent = components.get(path);
                    } else {
                        targetComponent = components.get(path.substring(0, s));
                    }
                }

                // if the path of the binding URI matches a component in the
                // composite then configure an endpoint with this component as
                // the target
                // if not then the binding URI will be assumed to reference an
                // external service
                if (targetComponentService != null) {

                    // Check that the target component service provides
                    // a superset of the component reference interface
                    if (componentReference.getInterfaceContract() == null
                            || interfaceContractMapper.isCompatible(
                                    componentReference.getInterfaceContract(),
                                    targetComponentService
                                            .getInterfaceContract())) {

                        Endpoint endpoint = endpointFactory.createEndpoint();
                        endpoint.setTargetName(targetComponent.getName());
                        endpoint.setSourceComponent(null); // TODO - fixed up at
                                                           // start
                        endpoint
                                .setSourceComponentReference(componentReference);
                        endpoint.setInterfaceContract(componentReference
                                .getInterfaceContract());
                        endpoint.setTargetComponent(targetComponent);
                        endpoint
                                .setTargetComponentService(targetComponentService);
                        endpoint.getCandidateBindings().add(binding);
                        endpoints.add(endpoint);
                    } else {
                        warning(monitor, "ReferenceIncompatibleInterface",
                                composite, composite.getName().toString(),
                                componentReference.getName(), uri);
                    }
                } else {

                    // create endpoints for manually configured bindings
                    Endpoint endpoint = endpointFactory.createEndpoint();
                    endpoint.setTargetName(uri);
                    endpoint.setSourceComponent(null); // TODO - fixed up at
                                                       // start
                    endpoint.setSourceComponentReference(componentReference);
                    endpoint.setInterfaceContract(componentReference
                            .getInterfaceContract());
                    endpoint.setSourceBinding(binding);
                    endpoints.add(endpoint);
                }
            }
        }

        return endpoints;
    }
    
    /**
     * Connect references to their targets.
     * 
     * @param composite
     * @param componentServices
     * @param componentReferences
     * @param problems
     */
    private void connectComponentReferences(Composite composite,
                                            Map<String, Component> components,
                                            Map<String, ComponentService> componentServices,
                                            Map<String, ComponentReference> componentReferences,
                                            Monitor monitor){
               
        for (ComponentReference componentReference : componentReferences.values()) {
            
            List<Endpoint> endpoints = createComponentReferenceTargets(composite, 
                                                                       components, 
                                                                       componentServices, 
                                                                       componentReference,
                                                                       monitor);

            componentReference.getEndpoints().addAll(endpoints);
            
            // the result of calculating the endpoints is either that bindings have been 
            // configured manually using a URI or that targets have been provided and the 
            // endpoint remains unresolved. So all endpoints should be either resved or uresolved.
            boolean endpointsRequireAutomaticResolution = false;
            for(Endpoint endpoint : endpoints){
                endpointsRequireAutomaticResolution = endpoint.isUnresolved();
            }
            
            // build each endpoint 
            if (endpointsRequireAutomaticResolution) { 

                for(Endpoint endpoint : endpoints){
                    endpointBuilder.build(endpoint, monitor);
                }
                
                // TODO - The following step ensures that the reference binding list remains 
                //        as the record of resolved targets for now. This needs fixing so 
                //        that the endpoint takes on this responsibility. 
                componentReference.getBindings().clear();
                
                if (componentReference.getCallback() != null){
                    componentReference.getCallback().getBindings().clear();
                }
                
                for(Endpoint endpoint : endpoints){
                    if (endpoint.isUnresolved() == false){
                        componentReference.getBindings().add(endpoint.getSourceBinding());
                        
                        if (componentReference.getCallback() != null){
                            componentReference.getCallback().getBindings().add(endpoint.getSourceCallbackBinding());
                        }
                    } 
                }                
                
            } else {
                // do nothing as no targets have been specified so the bindings
                // in the reference binding list are assumed to be manually configured
            }             
        }
    }
    
}
