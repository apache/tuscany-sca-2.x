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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * A composite builder that creates endpoint reference models.
 *
 * @version $Rev$ $Date$
 */
public class ComponentReferenceEndpointReferenceBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {

    Monitor monitor;
    // Testing
    //boolean useNew = true;
    boolean useNew = false;

    public ComponentReferenceEndpointReferenceBuilderImpl(ExtensionPointRegistry registry) {
        super(registry);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.ComponentReferenceEndpointReferenceBuilder";
    }

    /**
     * Create endpoint references for all component references.
     *
     * @param composite
     */
    public Composite build(Composite composite, Definitions definitions, Monitor monitor)
        throws CompositeBuilderException {
        this.monitor = monitor;

        // process component services
        processComponentReferences(composite);
        return composite;
    }

    private void processComponentReferences(Composite composite) {

        monitor.pushContext("Composite: " + composite.getName().toString());
        try {
            // index all of the components in the composite
            Map<String, Component> components = new HashMap<String, Component>();
            indexComponents(composite, components);

            // index all of the services in the composite
            Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
            indexServices(composite, componentServices);

            // create endpoint references for each component's references
            for (Component component : composite.getComponents()) {
                monitor.pushContext("Component: " + component.getName());

                try {
                    if (useNew) {
                        for (ComponentReference reference : component.getReferences()) {
                            createReferenceEndpointReferences2(composite,
                                                               component,
                                                               reference,
                                                               components,
                                                               componentServices);
                        } // end for
                    }

                    // recurse for composite implementations
                    Implementation implementation = component.getImplementation();
                    if (implementation instanceof Composite) {
                        processComponentReferences((Composite)implementation);
                    }

                    // create endpoint references to represent the component reference
                    for (ComponentReference reference : component.getReferences()) {

                        if (!useNew) {
                            createReferenceEndpointReferences(composite,
                                                              component,
                                                              reference,
                                                              components,
                                                              componentServices);
                        } // end if

                        // fix up links between endpoints and endpoint references that represent callbacks
                        for (ComponentService service : component.getServices()) {
                            if ((service.getInterfaceContract() != null) && (service.getInterfaceContract()
                                .getCallbackInterface() != null)) {
                                if (reference.getName().equals(service.getName())) {
                                    for (Endpoint endpoint : service.getEndpoints()) {
                                        endpoint.getCallbackEndpointReferences().addAll(reference
                                            .getEndpointReferences());
                                    }
                                    break;
                                } // end if
                            } // end if
                        } // end for
                    } // end for

                    // Validate that references are wired or promoted, according
                    // to their multiplicity   
                    validateReferenceMultiplicity(composite, component);

                } finally {
                    monitor.popContext();
                }
            } // end for

        } finally {
            monitor.popContext();
        }

    } // end method processCompoenntReferences

    private void createReferenceEndpointReferences(Composite composite,
                                                   Component component,
                                                   ComponentReference reference,
                                                   Map<String, Component> components,
                                                   Map<String, ComponentService> componentServices) {

        monitor.pushContext("Reference: " + reference.getName());

        // Get reference targets
        List<ComponentService> refTargets = getReferenceTargets(reference);
        if (reference.getAutowire() == Boolean.TRUE && reference.getTargets().isEmpty()) {

            // Find suitable targets in the current composite for an
            // autowired reference
            Multiplicity multiplicity = reference.getMultiplicity();
            for (Component targetComponent : composite.getComponents()) {

                // Tuscany specific selection of the first autowire reference
                // when there are more than one (ASM_60025)
                if ((multiplicity == Multiplicity.ZERO_ONE || multiplicity == Multiplicity.ONE_ONE) && (reference
                    .getEndpointReferences().size() != 0)) {
                    break;
                }

                // Prevent autowire connecting to self
                if (targetComponent == component)
                    continue;

                for (ComponentService targetComponentService : targetComponent.getServices()) {
                    if (reference.getInterfaceContract() == null || interfaceContractMapper.isCompatible(reference
                        .getInterfaceContract(), targetComponentService.getInterfaceContract())) {

                        EndpointReference endpointRef = createEndpointRef(component, reference, false);
                        endpointRef.setTargetEndpoint(createEndpoint(targetComponent, targetComponentService, true));
                        endpointRef.setStatus(EndpointReference.WIRED_TARGET_FOUND_READY_FOR_MATCHING);
                        reference.getEndpointReferences().add(endpointRef);

                        // Stop with the first match for 0..1 and 1..1 references
                        if (multiplicity == Multiplicity.ZERO_ONE || multiplicity == Multiplicity.ONE_ONE) {
                            break;
                        } // end if
                    } // end if
                } // end for
            } // end for

            if (multiplicity == Multiplicity.ONE_N || multiplicity == Multiplicity.ONE_ONE) {
                if (reference.getEndpointReferences().size() == 0) {
                    Monitor.error(monitor,
                                  this,
                                  "assembly-validation-messages",
                                  "NoComponentReferenceTarget",
                                  reference.getName());
                }
            }

            setSingleAutoWireTarget(reference);

        } else if (!refTargets.isEmpty()) {
            // Check that the component reference does not mix the use of endpoint references
            // specified via the target attribute with the presence of binding elements
            if (bindingsIdentifyTargets(reference)) {
                error(monitor, "ReferenceEndPointMixWithTarget", composite, composite.getName().toString(), component
                    .getName(), reference.getName());
            }

            // Resolve targets specified on the component reference
            for (ComponentService target : refTargets) {

                String targetName = getComponentServiceName(target.getName());
                String bindingName = getBindingName(target.getName());
                ComponentService targetComponentService = componentServices.get(targetName);

                Component targetComponent = getComponentFromTargetName(components, targetName);

                if (targetComponentService != null) {
                    // Check that target component service provides a superset of the component reference interface
                    if (reference.getInterfaceContract() == null || interfaceContractMapper.isCompatible(reference
                        .getInterfaceContract(), targetComponentService.getInterfaceContract())) {

                        if (bindingName != null) {
                            // the user has selected a binding as part of the target name
                            Binding targetBinding = null;

                            for (Binding tmp : targetComponentService.getBindings()) {
                                if (tmp.getName().equals(bindingName)) {
                                    targetBinding = tmp;
                                    continue;
                                }
                            }

                            if (targetBinding != null) {
                                EndpointReference endpointRef = createEndpointRef(component, reference, false);
                                endpointRef.setTargetEndpoint(createEndpoint(targetComponent,
                                                                             targetComponentService,
                                                                             targetBinding,
                                                                             true));
                                endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                                // relying on the registry here to resolve the real endpoint
                                reference.getEndpointReferences().add(endpointRef);

                            } else {
                                EndpointReference endpointRef = createEndpointRef(component, reference, true);
                                endpointRef.setTargetEndpoint(createEndpoint(component, targetName));
                                endpointRef.setRemote(true);
                                endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                                reference.getEndpointReferences().add(endpointRef);
                                warning(monitor, "ComponentReferenceTargetNotFound", composite, composite.getName()
                                    .toString(), targetName);
                            }

                        } else {
                            // the user hasn't selected a binding as part of the target name

                            EndpointReference endpointRef = createEndpointRef(component, reference, false);
                            endpointRef
                                .setTargetEndpoint(createEndpoint(targetComponent, targetComponentService, true));
                            endpointRef.setStatus(EndpointReference.WIRED_TARGET_FOUND_READY_FOR_MATCHING);
                            reference.getEndpointReferences().add(endpointRef);
                        }
                    } else {
                        error(monitor,
                              "ReferenceIncompatibleInterface",
                              composite,
                              composite.getName().toString(),
                              component.getName() + "." + reference.getName(),
                              targetName);
                    }
                } else {
                    // add an unresolved endpoint reference with an unresolved endpoint to go with it
                    EndpointReference endpointRef = createEndpointRef(component, reference, true);
                    endpointRef.setTargetEndpoint(createEndpoint(component, targetName));
                    endpointRef.setRemote(true);
                    endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                    reference.getEndpointReferences().add(endpointRef);
                    warning(monitor,
                            "ComponentReferenceTargetNotFound",
                            composite,
                            composite.getName().toString(),
                            targetName);
                } // end if
            } // end for
        } // end if

        // if no endpoints have found so far the bindings hold the targets.
        if (reference.getEndpointReferences().isEmpty()) {
            for (Binding binding : reference.getBindings()) {

                String uri = binding.getURI();

                // user hasn't put a uri on the binding so it's not a target name and the assumption is that
                // the target is established via configuration of the binding element itself
                if (uri == null) {
                    // Regular forward references are UNWIRED with no endpoint if they have an SCABinding with NO targets
                    // and NO URI set - but Callbacks with an SCABinding are wired and need an endpoint
                    if (!reference.isForCallback() && (binding instanceof SCABinding))
                        continue;

                    // create endpoint reference for manually configured bindings with a resolved endpoint to
                    // signify that this reference is pointing at some unwired endpoint
                    EndpointReference endpointRef = createEndpointRef(component, reference, binding, null, false);
                    if (binding instanceof SCABinding) {
                        // Assume that the system needs to resolve this binding later as
                        // it's the SCA binding
                        endpointRef.setTargetEndpoint(createEndpoint(true));
                        endpointRef.setStatus(EndpointReference.NOT_CONFIGURED);
                    } else {
                        // The user has configured a binding so assume they know what 
                        // they are doing and mark in as already resolved. 
                        endpointRef.setTargetEndpoint(createEndpoint(false));
                        endpointRef.setStatus(EndpointReference.RESOLVED_BINDING);
                    }
                    endpointRef.setRemote(true);
                    reference.getEndpointReferences().add(endpointRef);
                    continue;
                } // end if

                // user might have put a local target name in the uri - see if it refers to a target we know about
                // - if it does the reference binding will be matched with a service binding
                // - if it doesn't it is assumed to be an external reference
                if (uri.startsWith("/")) {
                    uri = uri.substring(1);
                }

                String targetName = getComponentServiceName(uri);
                String bindingName = getBindingName(uri);

                // Resolve the target component and service
                ComponentService targetComponentService = componentServices.get(targetName);
                Component targetComponent = getComponentFromTargetName(components, targetName);

                // If the binding URI matches a component in the composite, configure an endpoint reference with
                // this component as the target.
                // If not, the binding URI is assumed to reference an external service
                if (targetComponentService != null) {

                    // Check that the target component service provides
                    // a superset of the component reference interface
                    if (reference.getInterfaceContract() == null || interfaceContractMapper.isCompatible(reference
                        .getInterfaceContract(), targetComponentService.getInterfaceContract())) {
                        if (bindingName != null) {
                            // the user has selected a binding as part of the target name
                            Binding targetBinding = null;

                            for (Binding tmp : targetComponentService.getBindings()) {
                                if (tmp.getName().equals(bindingName)) {
                                    targetBinding = tmp;
                                    continue;
                                }
                            }

                            if (targetBinding != null) {
                                EndpointReference endpointRef = createEndpointRef(component, reference, false);
                                endpointRef.setTargetEndpoint(createEndpoint(targetComponent,
                                                                             targetComponentService,
                                                                             targetBinding,
                                                                             true));
                                endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                                // relying on the registry here to resolve the real endpoint
                                reference.getEndpointReferences().add(endpointRef);

                            } else {
                                EndpointReference endpointRef = createEndpointRef(component, reference, true);
                                endpointRef.setTargetEndpoint(createEndpoint(component, targetName));
                                endpointRef.setRemote(true);
                                endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                                reference.getEndpointReferences().add(endpointRef);
                                warning(monitor, "ComponentReferenceTargetNotFound", composite, composite.getName()
                                    .toString(), targetName);
                            }

                        } else {
                            // create endpoint reference with dummy endpoint which will be replaced when policies
                            // are matched and bindings are configured later
                            EndpointReference endpointRef =
                                createEndpointRef(component, reference, binding, null, false);
                            endpointRef
                                .setTargetEndpoint(createEndpoint(targetComponent, targetComponentService, true));
                            endpointRef.setStatus(EndpointReference.WIRED_TARGET_FOUND_READY_FOR_MATCHING);
                            reference.getEndpointReferences().add(endpointRef);
                        }
                    } else {
                        warning(monitor,
                                "ReferenceIncompatibleInterface",
                                composite,
                                composite.getName().toString(),
                                reference.getName(),
                                uri);
                    }
                } else {
                    // create endpoint reference for manually configured bindings with resolved endpoint
                    // to signify that this reference is pointing at some unwired endpoint. The endpoint
                    // is given the configured binding as a representation of the endpoint configuration. 
                    EndpointReference endpointRef = createEndpointRef(component, reference, binding, null, false);
                    Endpoint endpoint = createEndpoint(false);
                    endpoint.setBinding(binding);
                    endpointRef.setTargetEndpoint(endpoint);
                    endpointRef.setRemote(true);
                    endpointRef.setStatus(EndpointReference.RESOLVED_BINDING);
                    reference.getEndpointReferences().add(endpointRef);
                } // end if
            }
        }

        monitor.popContext();

    } // end method

    private void validateReferenceMultiplicity(Composite composite, Component component) {
        for (ComponentReference componentReference : component.getReferences()) {
            if (!ReferenceConfigurationUtil.validateMultiplicityAndTargets(componentReference.getMultiplicity(),
                                                                           componentReference.getEndpointReferences())) {
                if (componentReference.getEndpointReferences().isEmpty()) {

                    // No error if the reference is promoted out of the current composite
                    boolean promoted = false;
                    for (Reference reference : composite.getReferences()) {
                        CompositeReference compositeReference = (CompositeReference)reference;
                        if (compositeReference.getPromotedReferences().contains(componentReference)) {
                            promoted = true;
                            break;
                        }
                    }
                    if (!promoted && !componentReference.isForCallback() && !componentReference.isWiredByImpl()) {
                        Monitor.error(monitor,
                                      this,
                                      "assembly-validation-messages",
                                      "ReferenceWithoutTargets",
                                      composite.getName().toString(),
                                      componentReference.getName());
                    }
                } else {
                    // no error if reference is autowire and more targets
                    // than multiplicity have been found 
                    if (componentReference.getAutowire() == Boolean.TRUE) {
                        break;
                    }

                    // TUSCANY-3132  first example of updated error handling                  
                    Monitor.error(monitor,
                                  this,
                                  "assembly-validation-messages",
                                  "TooManyReferenceTargets",
                                  componentReference.getName());
                }
            }
        }

    }

    /**
     * Create Endpoint References for a component reference inside a given composite
     * @param composite - the composite
     * @param component - the component
     * @param reference - the component reference
     * @param components - a map of the components in the composite
     * @param componentServices - a map of the component services in the composite
     */
    private void createReferenceEndpointReferences2(Composite composite,
                                                    Component component,
                                                    ComponentReference reference,
                                                    Map<String, Component> components,
                                                    Map<String, ComponentService> componentServices) {
        // Find all the leafmost component references related to this component reference
        EndpointrefInfo refInfo = gatherEndpointrefInfo(component, reference, null);

        List<Endpoint> endpoints =
            getReferenceEndpoints(composite, component, reference, components, componentServices);

        Multiplicity multiplicity = reference.getMultiplicity();
        if (multiplicity == Multiplicity.ONE_N || multiplicity == Multiplicity.ONE_ONE) {
            // If there are no endpoints defined and also no endpoint references already present
            // then this reference is unwired, which is an error - the existing endpoint references
            // will have been attached to a nested reference when a promoting reference has its endpoint
            // references computed
            if (endpoints.size() == 0 && !reference.isPromoted() && !reference.isWiredByImpl()) {
                warning(monitor, "ReferenceWithoutTargets", reference, composite.getName().toString(), reference
                    .getName());
            } // end if
        } // end if

        // An endpoint reference is created for the combination of each leafmost component reference and
        // each discovered endpoint
        for (int i = 0; i < refInfo.getRefs().size(); i++) {
            Component leafComponent = refInfo.getComponents().get(i);
            ComponentReference leafReference = refInfo.getRefs().get(i);
            boolean unresolved = false;

            for (Endpoint endpoint : endpoints) {
                if (endpoint.isUnresolved() && endpoint.getComponent() == null && endpoint.getService() == null) {
                    unresolved = true;
                } else {
                    unresolved = false;
                } // end if
                // Create an EndpointReference pointing at the endpoint
                EndpointReference endRef =
                    createEndpointRef(leafComponent, leafReference, endpoint.getBinding(), endpoint, unresolved);
                // Add the EndpointReference to the top level AND the leaf level reference, if not the same!!
                if (useNew) {
                    leafReference.getEndpointReferences().add(endRef);
                    if (leafReference != reference) {
                        reference.getEndpointReferences().add(endRef);
                    } // end if
                } // end if
                // (Debug) For the moment, print out the results
                // disable for the time being - SL
                //System.out.println( "Created endpointRef. Component = " + component.getName() + " Reference = " +
                //		reference.getName() + " LeafComponent = " + endRef.getComponent().getName() + " LeafReference = " +
                //		endRef.getReference().getName() + " Binding = " + endRef.getBinding() + " target Component = " +
                //		endpoint.getComponent() + " target Service = " + endpoint.getService() );
            } // end for
        } // end for

    } // end method createReferenceEndpointReferences2

    private List<Endpoint> getReferenceEndpoints(Composite composite,
                                                 Component component,
                                                 ComponentReference reference,
                                                 Map<String, Component> components,
                                                 Map<String, ComponentService> componentServices) {
        // Target services for a component reference are specified in one of a number of ways, in order:
        // 1. Component services declared by the @target attribute of the reference
        // 2. Service endpoints inside or outside the domain through bindings with configured endpoints
        // 3. If @autowire=true is declared, component services within the composite containing the component which
        //    are compatible with the reference
        // 1. takes precedence over 2. - 3. is only used if neither of the other applies

        List<Endpoint> endpoints = new ArrayList<Endpoint>();

        // Get targets for references that are callbacks...
        if (getReferenceCallbackEndpoints(composite, component, reference, components, componentServices, endpoints)) {

            // Get reference targets declared by @target attribute
        } else if (getReferenceTargetEndpoints(composite,
                                               component,
                                               reference,
                                               components,
                                               componentServices,
                                               endpoints)) {

            // TODO - need to ensure that <wire/> elements are handled correctly
            // Get reference targets identified by configured <binding/> subelements
        } else if (getReferenceBindingEndpoints(composite,
                                                component,
                                                reference,
                                                components,
                                                componentServices,
                                                endpoints)) {

            // Get reference targets identified by @autowire
        } else {
            getReferenceAutowireEndpoints(composite, component, reference, components, componentServices, endpoints);
        } // end if
        return endpoints;
    } // end method getReferenceEndpoints

    /**
     * Gets the callback endpoints of a reference that is a callback reference
     * @param composite - the composite
     * @param component - the component
     * @param reference - the reference
     * @param components - a mapped list of the components in the composite
     * @param componentServices - a mapped list of the componentServices in the composite
     * @param endpoints - a list of the endpoints (in/out parameter)
     * @return - true if the reference is a callback reference, false otherwise
     */
    private boolean getReferenceCallbackEndpoints(Composite composite,
                                                  Component component,
                                                  ComponentReference reference,
                                                  Map<String, Component> components,
                                                  Map<String, ComponentService> componentServices,
                                                  List<Endpoint> endpoints) {
        // Only if this reference is a callback are there any endpoints of this kind
        if (reference.isForCallback()) {
            // add an unresolved endpoint reference with an unresolved endpoint to go with it
            // there will be one of these for each binding on the reference
            for (Binding binding : reference.getBindings()) {
                Endpoint endpoint = createEndpoint(true);
                endpoint.setBinding(binding);
                endpoints.add(endpoint);
            } // end for
            return true;
        } else {
            return false;
        } // end if

    } // end method getReferenceCallbackEndpoints

    /**
     * Gets the endpoints for the services identified by the @target attribute of a reference
     * @param composite - the composite
     * @param component - the component
     * @param reference - the component reference
     * @param components - a mapped list of the components in the composite
     * @param componentServices - a mapped list of the componentServices in the composite
     * @param endpoints - a list of the endpoints (in/out parameter)
     * @return true if the @target attribute was set, false otherwise
     */
    private boolean getReferenceTargetEndpoints(Composite composite,
                                                Component component,
                                                ComponentReference reference,
                                                Map<String, Component> components,
                                                Map<String, ComponentService> componentServices,
                                                List<Endpoint> endpoints) {

        List<ComponentService> refTargets = getReferenceTargets(reference);
        if (!refTargets.isEmpty()) {
            // Resolve targets specified on the component reference
            for (ComponentService target : refTargets) {

                String targetName = target.getName();
                Component targetComponent = getComponentFromTargetName(components, targetName);
                ComponentService targetComponentService = componentServices.get(targetName);

                if (targetComponentService != null) {
                    // Check that target component service provides a superset of the component reference interface
                    if (InterfaceContractIsCompatible(reference, targetComponentService)) {
                        // create endpoint reference -  with dummy endpoint which will be replaced when policies
                        // are matched and bindings are configured later
                        // TODO
                        Endpoint endpoint = selectTargetEndpoint(reference, targetComponentService);
                        // SL - Turn off for now
                        //System.out.println("Selected Endpoint: component=" + endpoint.getComponent().getName() +
                        //		" service=" + endpoint.getService().getName() +
                        //		" binding=" + endpoint.getBinding().toString());
                        Endpoint endpoint2 = createEndpoint(targetComponent, targetComponentService, true);
                        endpoint2.setBinding(endpoint.getBinding());
                        endpoints.add(endpoint2);
                    } else {
                        warning(monitor,
                                "ReferenceIncompatibleInterface",
                                composite,
                                composite.getName().toString(),
                                reference.getName(),
                                targetName);
                    }
                } else {
                    // add an unresolved endpoint reference with an unresolved endpoint to go with it
                    endpoints.add(createEndpoint(true));
                    warning(monitor,
                            "ComponentReferenceTargetNotFound",
                            composite,
                            composite.getName().toString(),
                            targetName);
                } // end if
            } // end for
            return true;
        } else {
            return false;
        } // end if

    } // end method getReferenceTargetEndpoints

    /**
     * Selects one endpoint of a target service which is compatible with the policy requirements of a reference
     * @param reference - the reference (carries policy data with it)
     * @param service - the target service
     * @return - an endpoint belonging to the service which is compatible with the reference.
     * This will in practice select a particular binding on the service if there is more than one endpoint on the
     * service.  If there are no matches, this method returns null
     */
    private Endpoint selectTargetEndpoint(ComponentReference reference, ComponentService service) {

        // Return the first endpoint with a Binding which is compatible with the policy requirements on
        // the reference
        for (Endpoint endpoint : service.getEndpoints()) {
            return endpoint;
        } //end for

        return null;
    } // end method selectTargetEndpoint

    /**
     * Gets the endpoints for the services identified by the <binding/> subelements of a reference
     * @param composite - the composite
     * @param component - the component
     * @param reference - the component reference
     * @param components - a mapped list of the components in the composite
     * @param componentServices - a mapped list of the componentServices in the composite
     * @param endpoints - a list of the endpoints (in/out parameter)
     * @return true if the <binding/> subelements identify target services, false otherwise
     */
    private boolean getReferenceBindingEndpoints(Composite composite,
                                                 Component component,
                                                 ComponentReference reference,
                                                 Map<String, Component> components,
                                                 Map<String, ComponentService> componentServices,
                                                 List<Endpoint> endpoints) {
        // Get service endpoints declared by <binding/> subelements
        if (bindingsIdentifyTargets(reference)) {
            for (Binding binding : reference.getBindings()) {

                String uri = binding.getURI();

                // user hasn't put a uri on the binding so it's not a target name and the assumption is that
                // the target is established via configuration of the binding element itself
                if (uri == null) {
                    // Regular forward references are UNWIRED with no endpoint if they have an SCABinding with NO targets
                    // and NO URI set - but Callbacks with an SCABinding are wired and need an endpoint
                    if (!reference.isForCallback() && (binding instanceof SCABinding))
                        continue;

                    // create an unwired endpoint containing the binding
                    Endpoint endpoint = createEndpoint(false);
                    endpoint.setBinding(binding);
                    endpoints.add(endpoint);
                    continue;
                } // end if

                // user might have put a local target name in the uri - see if it refers to a target we know about
                // - if it does the reference binding will be matched with a service binding
                // - if it doesn't it is assumed to be an external reference
                if (uri.startsWith("/"))
                    uri = uri.substring(1);

                // Resolve the target component and service
                ComponentService targetComponentService = componentServices.get(uri);
                Component targetComponent = getComponentFromTargetName(components, uri);

                // If the binding URI matches a component in the composite, configure an endpoint reference with
                // this component as the target.
                // If not, the binding URI is assumed to reference an external service
                if (targetComponentService != null) {
                    // Check that the target component service provides
                    // a superset of the component reference interface
                    if (InterfaceContractIsCompatible(reference, targetComponentService)) {
                        // create dummy endpoint which will be replaced when policies
                        // are matched and bindings are configured later
                        endpoints.add(createEndpoint(targetComponent, targetComponentService, true));
                    } else {
                        warning(monitor,
                                "ReferenceIncompatibleInterface",
                                composite,
                                composite.getName().toString(),
                                reference.getName(),
                                uri);
                    } // end if
                } else {
                    // create resolved endpoint to signify that this reference is pointing at some unwired endpoint
                    endpoints.add(createEndpoint(false));
                } // end if
            } // end for
            return true;
        } else {
            return false;
        } // end if

    } // end method getReferenceBindingEndpoints

    /**
     * Gets the endpoints for the services identified by the @autowire attribute of a reference
     * @param composite - the composite
     * @param component - the component
     * @param reference - the component reference
     * @param components - a mapped list of the components in the composite
     * @param componentServices - a mapped list of the componentServices in the composite
     * @param endpoints - a list of the endpoints (in/out parameter)
     * @return true if the @autowire attribute was set, false otherwise
     */
    private boolean getReferenceAutowireEndpoints(Composite composite,
                                                  Component component,
                                                  ComponentReference reference,
                                                  Map<String, Component> components,
                                                  Map<String, ComponentService> componentServices,
                                                  List<Endpoint> endpoints) {
        // Get compatible target services if @autowire=true is specified
        if (reference.getAutowire() == Boolean.TRUE) {

            Multiplicity multiplicity = reference.getMultiplicity();
            for (Component targetComponent : composite.getComponents()) {

                // Prevent autowire connecting to self
                if (targetComponent == component)
                    continue;

                for (ComponentService targetComponentService : targetComponent.getServices()) {
                    if (InterfaceContractIsCompatible(reference, targetComponentService)) {
                        // create endpoint reference - with a dummy endpoint which will be replaced when policies
                        // are matched and bindings are configured later
                        endpoints.add(createEndpoint(targetComponent, targetComponentService, true));

                        // Stop with the first match for 0..1 and 1..1 references
                        if (multiplicity == Multiplicity.ZERO_ONE || multiplicity == Multiplicity.ONE_ONE) {
                            break;
                        } // end if
                    } // end if
                } // end for
            } // end for
            return true;
        } else {
            return false;
        } // end if

    } // end method getReferenceAutowireEndpoints

    /**
     * Evaluates if the interface contract of a component service is a compatible superset of the interface contract
     * of a component reference
     * @param reference - the component reference
     * @param service - the component service
     * @return - true if the interface of the service is a compatible superset of the interface of the reference, false otherwise
     */
    private boolean InterfaceContractIsCompatible(ComponentReference reference, ComponentService service) {
        if (reference.getInterfaceContract() == null)
            return true;
        return interfaceContractMapper.isCompatible(reference.getInterfaceContract(), service.getInterfaceContract());
    } // end method InterfaceContractIsCompatible

    /**
     * Gather the Endpoint reference information for a component reference
     * - gathers information from deeper in the hierarchy for a component which is implemented by a composite
     * @param component - the component
     * @param reference - the component reference
     * @param refInfo - a reference info datastructure where the endpoint reference information is gathered.  Can be null, in
     * which case this method will allocate and return an instance of refInfo.
     * @return - an EndpointrefInfo - the same object as the refInfo parameter, unless that parameter is null in which case
     *           it is a new object
     */
    private EndpointrefInfo gatherEndpointrefInfo(Component component,
                                                  ComponentReference reference,
                                                  EndpointrefInfo refInfo) {
        if (refInfo == null)
            refInfo = new EndpointrefInfo();
        // Deal with the cases where there is an error in the configuration
        if (reference.isUnresolved())
            return refInfo;

        refInfo.setContract(reference.getInterfaceContract());
        // RULE: If the interface contract is not already set at this level, then it must be
        // identical across all the next level elements - otherwise they can be subsets
        boolean equalInterfaces = false;
        if (refInfo.getContract() == null)
            equalInterfaces = true;

        refInfo.addIntents(reference.getRequiredIntents());
        if (reference.getReference() instanceof CompositeReference) {
            // It's a composite reference - get hold of the set of promoted references
            CompositeReference compRef = (CompositeReference)reference.getReference();
            List<Component> components = compRef.getPromotedComponents();
            List<ComponentReference> componentRefs = compRef.getPromotedReferences();
            // Scan over all promoted references
            for (int i = 0; i < componentRefs.size(); i++) {
                refInfo.setContractEqual(equalInterfaces);
                gatherEndpointrefInfo(components.get(i), componentRefs.get(i), refInfo);
            } // end for
        } else {
            // Otherwise it's a leaf node reference which must be recorded as an endpoint reference
            refInfo.addRef(reference);
            refInfo.addComponent(component);
        } // end if
        // RULE: Any PolicySets at this level override PolicySets from lower levels
        refInfo.setPolicySets(reference.getPolicySets());

        return refInfo;
    } // end method gatherEndpointrefInfo

    /**
     * A class used to gather endpoint reference information for a component reference
     * - handles the information in a promotion hierarchy where the component reference is implemented
     * by a composite reference.
     * @author MikeEdwards
     */
    private class EndpointrefInfo {
        private List<Component> components = new ArrayList<Component>();
        private List<ComponentReference> refs = new ArrayList<ComponentReference>();
        private InterfaceContract contract = null;
        private List<Intent> intents = new ArrayList<Intent>();
        private List<PolicySet> policySets = null;
        private boolean contractEqual = false;

        /**
         * Sets whether new contracts must be equal to the current contract or not
         * @param isEqual - true means that Contracts must be equal to the current contract - false means that Contracts
         * can be subsets of the current contract
         */
        void setContractEqual(boolean isEqual) {
            contractEqual = isEqual;
        }

        boolean getContractEqual() {
            return contractEqual;
        }

        List<PolicySet> getPolicySets() {
            return policySets;
        }

        void setPolicySets(List<PolicySet> policySets) {
            this.policySets = policySets;
        }

        List<Component> getComponents() {
            return components;
        }

        void addComponent(Component component) {
            this.components.add(component);
        }

        List<ComponentReference> getRefs() {
            return refs;
        }

        void addRef(ComponentReference ref) {
            this.refs.add(ref);
        }

        InterfaceContract getContract() {
            return contract;
        }

        /**
         * Set the contract - with checking of the contract if a contract is already set
         * @param contract - the contract to set
         */
        void setContract(InterfaceContract contract) {
            // Add the contract if there is no existing contract set
            if (this.contract == null) {
                this.contract = contract;
            } else {
                // RULE: Raise an error if the new contract is not a subset of the existing contract
                if (contractEqual) {
                    // Contracts must be equal
                    if (!interfaceContractMapper.isEqual(this.contract, contract)) {
                        warning(monitor, "ReferencePromotionInterfacesNotEqual", this.contract.toString(), contract
                            .toString());
                    } // end if
                } else {
                    // Contract must be subset
                    if (!interfaceContractMapper.isCompatible(contract, this.contract)) {
                        warning(monitor, "ReferencePromotionIncompatibleInterface", this.contract.toString(), contract
                            .toString());
                    } // end if
                } // end if
            } // end if
        }

        List<Intent> getIntents() {
            return intents;
        }

        /**
         * Accumulate intents
         * @param intents
         */
        void addIntents(List<Intent> intents) {
            this.intents.addAll(intents);
        }

    } // end class EndpointrefInfo

    /**
     * Evaluates whether the bindings attached to a reference identify one or more target services.
     * @param reference - the reference
     * @return true if the bindings identify a target, false otherwise
     */
    private boolean bindingsIdentifyTargets(ComponentReference reference) {
        for (Binding binding : reference.getBindings()) {
            // <binding.sca without a URI does not identify a target
            if ((binding instanceof SCABinding) && (binding.getURI() == null))
                continue;
            // any other binding implies a target
            // TODO Processing for other binding types
            return true;
        } // end for
        return false;
    } // end bindingsIdentifyTargets

    /**
     * Helper method which obtains a list of targets for a reference
     * @param reference - Component reference
     * @return - the list of targets, which will be empty if there are no targets
     */
    private List<ComponentService> getReferenceTargets(ComponentReference reference) {
        List<ComponentService> theTargets = reference.getTargets();
        if (theTargets.isEmpty()) {
            // Component reference list of targets is empty, try the implementation reference
            if (reference.getReference() != null) {
                theTargets = reference.getReference().getTargets();
            } // end if
        } // end if
        return theTargets;
    } // end method getReferenceTargets

    /**
     * Target names can take the form 
     *   component/service/binding
     * This extracts the component/service part
     * 
     * @param targetName
     * @return String the component/service name
     */
    private String getComponentServiceName(String targetName) {
        String[] parts = targetName.split("/");

        if (parts.length > 1) {
            return parts[0] + "/" + parts[1];
        } else {
            return parts[0];
        }
    }

    /**
     * Target names can take the form 
     *   component/service/binding
     * This extracts the binding part and returns
     * it. If there is no binding part it returns null
     * 
     * @param targetName
     * @return String the binding name or null if there is no binding name
     */
    private String getBindingName(String targetName) {
        String[] parts = targetName.split("/");

        if (parts.length == 3) {
            return parts[2];
        } else {
            return null;
        }
    }

    /**
     * Helper method that finds the Component given a target name
     * @param components
     * @param targetName
     * @return the component
     */
    private Component getComponentFromTargetName(Map<String, Component> components, String targetName) {
        Component theComponent;
        int s = targetName.indexOf('/');
        if (s == -1) {
            theComponent = components.get(targetName);
        } else {
            theComponent = components.get(targetName.substring(0, s));
        }
        return theComponent;
    } // end method getComponentFromTargetName

    /**
     * Helper method to create an Endpoint Reference
     * @param component
     * @param reference
     * @param binding
     * @param endpoint
     * @param unresolved
     * @return the endpoint reference
     */
    private EndpointReference createEndpointRef(Component component,
                                                ComponentReference reference,
                                                Binding binding,
                                                Endpoint endpoint,
                                                boolean unresolved) {
        EndpointReference endpointRef = createEndpointRef(component, reference, unresolved);
        endpointRef.setBinding(binding);
        endpointRef.setTargetEndpoint(endpoint);
        return endpointRef;
    } // end method

    /**
     * Helper method to create an Endpoint Reference
     * @param component
     * @param reference
     * @param unresolved
     * @return the endpoint reference
     */
    private EndpointReference createEndpointRef(Component component, ComponentReference reference, boolean unresolved) {
        EndpointReference endpointRef = assemblyFactory.createEndpointReference();
        endpointRef.setComponent(component);
        endpointRef.setReference(reference);
        endpointRef.setUnresolved(unresolved);
        return endpointRef;
    } // end method createEndpointRef

    /**
     * Helper method to create an endpoint
     * @param component
     * @param service
     * @param unresolved
     * @return the endpoint
     */
    private Endpoint createEndpoint(Component component, ComponentService service, boolean unresolved) {
        Endpoint endpoint = createEndpoint(unresolved);
        endpoint.setComponent(component);
        endpoint.setService(service);
        endpoint.setUnresolved(unresolved);
        return endpoint;
    } // end method createEndpoint

    /**
     * Helper method to create an endpoint
     * @param component
     * @param service
     * @param binding
     * @param unresolved
     * @return the endpoint
     */
    private Endpoint createEndpoint(Component component, ComponentService service, Binding binding, boolean unresolved) {
        Endpoint endpoint = createEndpoint(unresolved);
        endpoint.setComponent(component);
        endpoint.setService(service);
        endpoint.setBinding(binding);
        endpoint.setUnresolved(unresolved);
        return endpoint;
    } // end method createEndpoint    

    /**
     * Helper method to create an Endpoint
     * @param unresolved
     * @return the endpoint
     */
    private Endpoint createEndpoint(boolean unresolved) {
        Endpoint endpoint = assemblyFactory.createEndpoint();
        endpoint.setUnresolved(unresolved);
        return endpoint;
    } // end method createEndpoint

    /**
     * Helper method to create an Endpoint
     *
     * @param component The component that owns the reference
     * @param targetName It can be one of the following formats
     * <ul>
     * <li>componentName
     * <li>componentName/serviceName
     * <li>componentName/serviceName/bindingName
     * </ul>
     * @return the endpoint
     */
    private Endpoint createEndpoint(Component component, String targetName) {
        String[] parts = targetName.split("/");
        if (parts.length < 1 || parts.length > 3) {
            throw new IllegalArgumentException("Invalid target URI: " + targetName);
        }

        // Find the parent uri
        String uri = component.getURI();
        int index = uri.lastIndexOf('/');
        if (index == -1) {
            uri = "";
        } else {
            uri = uri.substring(0, index);
        }

        if (parts.length >= 1) {
            // Append the target component name
            if (uri.length() == 0) {
                uri = parts[0];
            } else {
                uri = uri + "/" + parts[0];
            }
        }
        if (parts.length == 3) {
            // <componentURI>#service-binding(serviceName/bindingName)
            uri = uri + "#service-binding(" + parts[1] + "/" + parts[2] + ")";
        } else if (parts.length == 2) {
            // <componentURI>#service(serviceName)
            uri = uri + "#service(" + parts[1] + ")";
        }

        Endpoint endpoint = assemblyFactory.createEndpoint();
        endpoint.setUnresolved(true);
        endpoint.setURI(uri);
        return endpoint;
    } // end method createEndpoint

    /**
     * ASM_5021: where a <reference/> of a <component/> has @autowire=true 
     * and where the <reference/> has a <binding/> child element which 
     * declares a single target service,  the reference is wired only to 
     * the single service identified by the <wire/> element
     */
    private void setSingleAutoWireTarget(ComponentReference reference) {
        if (reference.getEndpointReferences().size() > 1 && reference.getBindings() != null
            && reference.getBindings().size() == 1) {
            String uri = reference.getBindings().get(0).getURI();
            if (uri != null) {
                if (uri.indexOf('/') > -1) {
                    // TODO: must be a way to avoid this fiddling
                    int i = uri.indexOf('/');
                    String c = uri.substring(0, i);
                    String s = uri.substring(i + 1);
                    uri = c + "#service(" + s + ")";
                }
                for (EndpointReference er : reference.getEndpointReferences()) {
                    if (er.getTargetEndpoint() != null && uri.equals(er.getTargetEndpoint().getURI())) {
                        reference.getEndpointReferences().clear();
                        reference.getEndpointReferences().add(er);
                        return;
                    }
                }
            }
        }
    }

} // end class
