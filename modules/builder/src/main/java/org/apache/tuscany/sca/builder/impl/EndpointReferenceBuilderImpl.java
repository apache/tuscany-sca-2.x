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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
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
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;

/**
 * Creates endpoint reference models.
 */
public class EndpointReferenceBuilderImpl {

    private AssemblyFactory assemblyFactory;
    private InterfaceContractMapper interfaceContractMapper;
    /*
    private DomainRegistryFactory domainRegistryFactory;
    private EndpointReferenceBinder endpointReferenceBinder;
    */
    
    public EndpointReferenceBuilderImpl(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);

        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
    
        /*
        domainRegistryFactory = registry.getExtensionPoint(DomainRegistryFactory.class);
        endpointReferenceBinder = registry.getExtensionPoint(EndpointReferenceBinder.class);
        */
    }

    /**
     * Create endpoint references for all component references.
     *
     * @param composite
     */    
    public Composite build(Composite composite, BuilderContext context)
        throws CompositeBuilderException {
        Monitor monitor = context.getMonitor();

        // process component references
        processComponentReferences(composite, monitor);
        
        // validate component references
        validateComponentReferences(composite, monitor);
        
        return composite;
    }     
/*    
    // Moving toward a consistent matching model between build and runtime
    // endpoint reference matching
    public Composite build(Composite composite, BuilderContext context)
            throws CompositeBuilderException {
        // create temporary local registry for all available local endpoints
        // TODO - need a better way of getting a local registry
        EndpointRegistry registry = domainRegistryFactory.getEndpointRegistry("vm://tmp", "local");
        
        // populate the registry with all the endpoints that are present in the model
        populateLocalRegistry(composite, registry, context);
        
        // create endpoint references for each reference
        createEndpointReferences(composite, registry, context);
        
        // match all local services against the endpoint references 
        // we've just created
        matchEndpointReferences(composite, registry, context);
        
        // remove the local registry
        domainRegistryFactory.getEndpointRegistries().remove(registry);
        
        // validate component references
        // TODO - do we really need to leave this until this point?
        validateComponentReferences(composite, monitor);

        return composite;
    }  
*/

    private void processComponentReferences(Composite composite, Monitor monitor) {

        monitor.pushContext("Composite: " + composite.getName().toString());
        try {
            // Index components, services and references
            Map<String, Component> components = new HashMap<String, Component>();
            Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
            indexComponentsServicesAndReferences(composite, components, componentServices, componentReferences);

            // Connect component references as described in wires
            connectWires(composite, componentServices, componentReferences, monitor);

            // create endpoint references for each component's references
            for (Component component : composite.getComponents()) {
                monitor.pushContext("Component: " + component.getName());

                try {

                    // recurse for composite implementations
                    Implementation implementation = component.getImplementation();
                    if (implementation instanceof Composite) {
                        processComponentReferences((Composite)implementation, monitor);
                    }

                    // create endpoint references to represent the component reference
                    for (ComponentReference reference : component.getReferences()) {
                        createReferenceEndpointReferences(composite,
                                                          component,
                                                          reference,
                                                          components,
                                                          componentServices,
                                                          monitor);

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
                        
                        // push down endpoint references into the leaf component references
                        // in the case where this component reference promotes a reference from
                        // a composite implementation
                        pushDownEndpointReferences(composite,
                                                   component,
                                                   reference,
                                                   monitor);
                        
                    } // end for

                    // Validate that references are wired or promoted, according
                    // to their multiplicity. This validates as we go and catches cases
                    // where a reference has been configured directly incorrectly with its
                    // immediate multiplicity setting. We re-run this validation again later
                    // to catch to more complex cases where reference promotion causes 
                    // multiplicity errors. 
                    validateReferenceMultiplicity(composite, component, monitor);

                } finally {
                    monitor.popContext();
                }
            } // end for

        } finally {
            monitor.popContext();
        }

    } // end method processCompoenntReferences
    
    
    /**
     * The validate stage is separate from the process stage as enpoint references are
     * pushed down the hierarchy. We don't know the full set of endpoint references until
     * all processing is complete. Hence we can't validate as we go
     * 
     * @param composite
     * @param monitor
     */
    private void validateComponentReferences(Composite composite, Monitor monitor) {

        monitor.pushContext("Composite: " + composite.getName().toString());
        try {
            // create endpoint references for each component's references
            for (Component component : composite.getComponents()) {
                monitor.pushContext("Component: " + component.getName());

                try {

                    // recurse for composite implementations
                    Implementation implementation = component.getImplementation();
                    if (implementation instanceof Composite) {
                        validateComponentReferences((Composite)implementation, monitor);
                    }
                    // Validate that references are wired or promoted, according
                    // to their multiplicity   
                    validateReferenceMultiplicity(composite, component, monitor);

                } finally {
                    monitor.popContext();
                }
            }
            
        } finally {
            monitor.popContext();
        }

    }        

    protected void indexComponentsServicesAndReferences(Composite composite,
                                                        Map<String, Component> components,
                                                        Map<String, ComponentService> componentServices,
                                                        Map<String, ComponentReference> componentReferences) {

        for (Component component : composite.getComponents()) {

            // Index components by name
            components.put(component.getName(), component);

            ComponentService nonCallbackService = null;
            int nonCallbackServices = 0;
            for (ComponentService componentService : component.getServices()) {

                // Index component services by component name / service name
                String uri = component.getName() + '/' + componentService.getName();
                componentServices.put(uri, componentService);

                // count how many non-callback services there are
                // if there is only one the component name also acts as the
                // service name
                if (!componentService.isForCallback()) {
                    // Check how many non callback non-promoted services we have
                    if (nonCallbackServices == 0) {
                        nonCallbackService = componentService;
                    }
                    nonCallbackServices++;
                }
            }

            if (nonCallbackServices == 1) {
                // If we have a single non callback service, index it by
                // component name as well
                componentServices.put(component.getName(), nonCallbackService);
            }

            // Index references by component name / reference name
            for (ComponentReference componentReference : component.getReferences()) {
                String uri = component.getName() + '/' + componentReference.getName();
                componentReferences.put(uri, componentReference);
            }
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
                    Monitor.warning(monitor, this, Messages.ASSEMBLY_VALIDATION, "WireSourceNotFound", source
                        .getName());
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
                    Monitor.warning(monitor, this, Messages.ASSEMBLY_VALIDATION, "WireTargetNotFound", target
                        .getName());
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
                    if (wire.isReplace()) {
                        resolvedReference.getTargets().clear();
                    }
                    resolvedReference.getTargets().add(wire.getTarget());
                } else {
                    Monitor.warning(monitor, this, Messages.ASSEMBLY_VALIDATION, "WireIncompatibleInterface", source
                        .getName(), target.getName());
                }
            }
        }

        // Clear the list of wires
        composite.getWires().clear();
    }

    private void createReferenceEndpointReferences(Composite composite,
                                                   Component component,
                                                   ComponentReference reference,
                                                   Map<String, Component> components,
                                                   Map<String, ComponentService> componentServices,
                                                   Monitor monitor) {

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
                        
                        if (intentsMatch(reference.getRequiredIntents(), targetComponentService.getRequiredIntents())) {
                            EndpointReference endpointRef = createEndpointRef(component, reference, false);
                            endpointRef.setTargetEndpoint(createEndpoint(targetComponent, targetComponentService, true));
                            endpointRef.setStatus(EndpointReference.WIRED_TARGET_FOUND_READY_FOR_MATCHING);
                            reference.getEndpointReferences().add(endpointRef);

                            // Stop with the first match for 0..1 and 1..1 references
                            if (multiplicity == Multiplicity.ZERO_ONE || multiplicity == Multiplicity.ONE_ONE) {
                                break;
                            } // end if
                        }

                    } // end if
                } // end for
            } // end for

            if (multiplicity == Multiplicity.ONE_N || multiplicity == Multiplicity.ONE_ONE) {
                if (reference.getEndpointReferences().size() == 0) {
                    Monitor.error(monitor,
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "NoComponentReferenceTarget",
                                  reference.getName());
                }
            }

            setSingleAutoWireTarget(reference);

        } else if (!refTargets.isEmpty()) {
            // Check that the component reference does not mix the use of endpoint references
            // specified via the target attribute with the presence of binding elements
            if (bindingsIdentifyTargets(reference)) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ReferenceEndPointMixWithTarget",
                              composite.getName().toString(),
                              component.getName(),
                              reference.getName());
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
                                endpointRef.setBinding(targetBinding);
                                endpointRef.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
                                // relying on the registry here to resolve the real endpoint
                                reference.getEndpointReferences().add(endpointRef);

                            } else {
                                EndpointReference endpointRef = createEndpointRef(component, reference, true);
                                endpointRef.setTargetEndpoint(createEndpoint(component, targetName));
                                endpointRef.setRemote(true);
                                endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                                reference.getEndpointReferences().add(endpointRef);
                                Monitor.warning(monitor,
                                                this,
                                                Messages.ASSEMBLY_VALIDATION,
                                                "ComponentReferenceTargetNotFound",
                                                composite.getName().toString(),
                                                targetName);
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
                        Monitor.error(monitor,
                                      this,
                                      Messages.ASSEMBLY_VALIDATION,
                                      "ReferenceIncompatibleInterface",
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
                    Monitor.warning(monitor,
                                    this,
                                    Messages.ASSEMBLY_VALIDATION,
                                    "ComponentReferenceTargetNotFound",
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
                                endpointRef.setBinding(targetBinding);
                                endpointRef.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);                                
                                // relying on the registry here to resolve the real endpoint
                                reference.getEndpointReferences().add(endpointRef);

                            } else {
                                EndpointReference endpointRef = createEndpointRef(component, reference, true);
                                endpointRef.setTargetEndpoint(createEndpoint(component, targetName));
                                endpointRef.setRemote(true);
                                endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                                reference.getEndpointReferences().add(endpointRef);
                                Monitor.warning(monitor,
                                                this,
                                                Messages.ASSEMBLY_VALIDATION,
                                                "ComponentReferenceTargetNotFound",
                                                composite.getName().toString(),
                                                targetName);
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
                        Monitor.warning(monitor,
                                        this,
                                        Messages.ASSEMBLY_VALIDATION,
                                        "ReferenceIncompatibleInterface",
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
    
    private boolean intentsMatch(List<Intent> referenceIntents, List<Intent> serviceIntents) {
        Set<Intent> referenceIntentSet = new HashSet<Intent>(referenceIntents);
        Set<Intent> serviceIntentSet = new HashSet<Intent>(serviceIntents);
        return referenceIntentSet.equals(serviceIntentSet);
    }

    /**
     * Reference targets have to be resolved in the context in which they are 
     * defined so they can't be push down the hierarchy during the static build.
     * So we wait until we have calculated the enpoint references before pushing them 
     * down. Muliplicity errors will be caught by the multiplicity validation check that
     * comes next 
     * 
     * @param composite
     * @param component
     * @param reference
     * @param monitor
     */
    private void pushDownEndpointReferences(Composite composite,
                                            Component component,
                                            ComponentReference componentReference,
                                            Monitor monitor) {
        Reference reference = componentReference.getReference();
        
        if (reference instanceof CompositeReference) {
            List<ComponentReference> leafComponentReferences = getPromotedComponentReferences((CompositeReference)reference);
            
            // for each leaf component reference copy in the endpoint references for this
            // higher level (promoting) reference
            // TODO - the elements are inserted starting at 0 here because the code allows references multiplicity 
            //        validation constraints to be broken if the reference is autowire. At runtime the 
            //        first one is chosen if max multiplicity is 1. We have an OSOA test that assumes that
            //        promoted references overwrite leaf references. This insert gives the same effect in the
            //        autowire case. We need to think about if there is a more correct answer. 
            for (ComponentReference leafRef : leafComponentReferences){
                int insertLocation = 0;
                for (EndpointReference epr : componentReference.getEndpointReferences()){
                    // copy the epr
                    EndpointReference eprCopy = copyHigherReference(epr, leafRef);
                    leafRef.getEndpointReferences().add(insertLocation, eprCopy);
                    insertLocation++;
                }
            }
        }
        
        // TODO - what to do about callbacks in the reference promotion case
    }
    
    /**
     * Follow a reference promotion chain down to the innermost (non composite)
     * component references.
     * 
     * @param compositeReference
     * @return
     */
    private List<ComponentReference> getPromotedComponentReferences(CompositeReference compositeReference) {
        List<ComponentReference> componentReferences = new ArrayList<ComponentReference>();
        collectPromotedComponentReferences(compositeReference, componentReferences);
        return componentReferences;
    }

    /**
     * Follow a reference promotion chain down to the innermost (non composite)
     * component references.
     * 
     * @param compositeReference
     * @param componentReferences
     * @return
     */
    private void collectPromotedComponentReferences(CompositeReference compositeReference,
                                                           List<ComponentReference> componentReferences) {
        for (ComponentReference componentReference : compositeReference.getPromotedReferences()) {
            Reference reference = componentReference.getReference();
            if (reference instanceof CompositeReference) {

                // Continue to follow the reference promotion chain
                collectPromotedComponentReferences((CompositeReference)reference, componentReferences);

            } else if (reference != null) {

                // Found a non-composite reference
                componentReferences.add(componentReference);
            }
        }
    }
    
    /**
     * Copy a higher level EndpointReference down to a lower level reference which it promotes 
     * @param epRef - the endpoint reference
     * @param promotedReference - the promoted reference
     * @return - a copy of the EndpointReference with data merged from the promoted reference
     */
    private  EndpointReference copyHigherReference(EndpointReference epRef, ComponentReference promotedReference) {
        EndpointReference epRefClone = null;
        try {
            epRefClone = (EndpointReference)epRef.clone();
        } catch (Exception e) {
            // Ignore (we know that EndpointReference2 can be cloned)
        } // end try
        // Copy across details of the inner reference
        ComponentReference ref = epRefClone.getReference();
        //FIXME
        epRefClone.setReference(promotedReference);
        return epRefClone;
    }   

    private void validateReferenceMultiplicity(Composite composite, Component component, Monitor monitor) {
        for (ComponentReference componentReference : component.getReferences()) {
            if (!validateMultiplicity(componentReference.getMultiplicity(),
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
                                      Messages.ASSEMBLY_VALIDATION,
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

                    Monitor.error(monitor,
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "TooManyReferenceTargets",
                                  componentReference.getName());
                }
            }
        }

    }
    
    private boolean validateMultiplicity(Multiplicity multiplicity, List<EndpointReference> endpointReferences) {

        // In some tests multiplicity is not set
        if (multiplicity == null) {
            return true;
        }

        // Count targets
        int count = endpointReferences.size();

        switch (multiplicity) {
            case ZERO_N:
                break;
            case ZERO_ONE:
                if (count > 1) {
                    return false;
                }
                break;
            case ONE_ONE:
                if (count != 1) {
                    return false;
                }
                break;
            case ONE_N:
                if (count < 1) {
                    return false;
                }
                break;
        }
        return true;
    }    

    /**
     * Evaluates whether the bindings attached to a reference identify one or more target services.
     * @param reference - the reference
     * @return true if the bindings identify a target, false otherwise
     */
    // TODO - don't think we need this
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
    
    // ========================================================================
    // methods below in support of reference matching consolidation
    
    /*
    private void populateLocalRegistry(Composite composite, EndpointRegistry registry, BuilderContext context){
        for (Component component : composite.getComponents()) {
            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                populateLocalRegistry((Composite)implementation, registry, context);
            }
            
            for (ComponentService service : component.getServices()) {
                for (Endpoint endpoint : service.getEndpoints()){
                    registry.addEndpoint(endpoint);
                }
            }
        }
    }
    
    private void createEndpointReferences(Composite composite, EndpointRegistry registry, BuilderContext context){
        for (Component component : composite.getComponents()) {
            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                createEndpointReferences((Composite)implementation, registry, context);
            }
            
            for (ComponentReference reference : component.getReferences()) {
                processComponentReference(composite,
                                         component,
                                         reference,
                                         registry,
                                         context);
                
                // add all the endpoint references to the local registry so that they're
                // easily accessible during the matching stage
                for (EndpointReference epr : reference.getEndpointReferences()){
                    registry.addEndpointReference(epr);
                }
            }
        }        
    }
      
    private void processComponentReference(Composite composite,
                                           Component component,
                                           ComponentReference reference,
                                           EndpointRegistry registry,
                                           BuilderContext context){
        
        // Get reference targets
        List<ComponentService> refTargets = getReferenceTargets(reference);
        
        if (reference.getAutowire() == Boolean.TRUE && reference.getTargets().isEmpty()) {
            // for autowire references we create a place holder
            // endpoint reference. This is not used itself but
            // marks out the reference for special attention 
            // later in the matching process
            EndpointReference endpointRef = createEndpointRef(component, reference, false);
            endpointRef.setStatus(EndpointReference.AUTOWIRE_PLACEHOLDER);
            reference.getEndpointReferences().add(endpointRef);

        } else if (!refTargets.isEmpty()) {
            // Check that the component reference does not mix the use of endpoint references
            // specified via the target attribute with the presence of binding elements
            if (bindingsIdentifyTargets(reference)) {
                Monitor.error(context.getMonitor(),
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ReferenceEndPointMixWithTarget",
                              composite.getName().toString(),
                              component.getName(),
                              reference.getName());
            }            

            // create endpoint references for targets
            for (ComponentService target : refTargets) {
                
                EndpointReference endpointRef = createEndpointRef(component, reference, true);
                endpointRef.setTargetEndpoint(createEndpoint(component, target.getName()));
                endpointRef.setRemote(true);
                endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                reference.getEndpointReferences().add(endpointRef);
            }
        } 

        // if no endpoints have been found so far the bindings hold the targets.
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
                // - if it does the reference binding will be matched later on
                // - if it doesn't it is assumed to be an external reference
                if (uri.startsWith("/")) {
                    uri = uri.substring(1);
                }

                EndpointReference endpointRef = createEndpointRef(component, reference, binding, null, false);
                
                if (registry.findEndpoint(endpointRef).size() > 0){
                    // it do refer to an endpoint so we'll match it for real later on
                    endpointRef.setStatus(EndpointReference.WIRED_TARGET_NOT_FOUND);
                } else {
                    // it's a manually configured binding 
                    Endpoint endpoint = createEndpoint(false);
                    endpoint.setBinding(binding);
                    endpointRef.setTargetEndpoint(endpoint);
                    endpointRef.setRemote(true);
                    endpointRef.setStatus(EndpointReference.RESOLVED_BINDING);
                }
                
                // TODO - there is a hole in this logic if the uri of the binding represents
                //        a target to a service elsewhere in the domain we won't know about it
                //        with this local registry. Should test the real regsitry here also.
                
                reference.getEndpointReferences().add(endpointRef);
            }
        }
    }
 
    private void matchEndpointReferences(Composite composite, EndpointRegistry registry, BuilderContext context){
        
        // look at all the endpoint references and try to match them to 
        // endpoints
        for (EndpointReference endpointReference : registry.getEndpointReferences()){
            endpointReferenceBinder.match(registry, endpointReference);
        }
    }
    */
}