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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem.Severity;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.apache.tuscany.sca.policy.QualifiedIntent;

public class CompositeWireBuilderImpl {

    private CompositeBuilderMonitor monitor;
    private AssemblyFactory assemblyFactory;
    private InterfaceContractMapper interfaceContractMapper;
    
    //Represents a target component and service
    private class Target {
        Component component;
        ComponentService service;
        
        Target(Component component, ComponentService service) {
            this.component = component;
            this.service = service;
        }
        
        Component getComponent() {
            return component;
        }
        
        ComponentService getService() {
            return service;
        }
    };
    
    public CompositeWireBuilderImpl(AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper, CompositeBuilderMonitor monitor) {
        this.assemblyFactory = assemblyFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.monitor = monitor;
    }

    /**
     * Wire component references to component services and connect promoted
     * services/references to component services/references inside a composite.
     * 
     * @param composite
     */
    public void wireComposite(Composite composite) {

        // Wire nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                wireComposite((Composite)implementation);
            }
        }

        // Index components, services and references
        Map<String, Component> components = new HashMap<String, Component>();
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
        indexComponentsServicesAndReferences(composite, components, componentServices, componentReferences);

        // Connect composite services and references to the component
        // services and references that they promote
        connectCompositeServices(composite, components, componentServices);
        connectCompositeReferences(composite, componentReferences);

        // Compute the policies before connecting component references
        computePolicies(composite);

        // Connect component references to their targets
        connectComponentReferences(composite, components, componentServices, componentReferences);

        // Connect component references as described in wires
        connectWires(composite, componentServices, componentReferences);

        // Validate that references are wired or promoted, according
        // to their multiplicity
        for (ComponentReference componentReference : componentReferences.values()) {
            if (!ReferenceUtil.validateMultiplicityAndTargets(componentReference.getMultiplicity(), componentReference
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
                    if (!promoted) {
                        warning("No targets for reference: " + componentReference.getName(), composite);
                    }
                } else {
                    warning("Too many targets on reference: " + componentReference.getName(), composite);
                }
            }
        }
    }
    
    /**
     * Index components, services and references inside a composite.
     * @param composite
     * @param components
     * @param componentServices
     * @param componentReferences
     */
    private void indexComponentsServicesAndReferences(Composite composite,
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
                if (!componentService.isCallback()) {
                    
                    // Check how many non callback services we have
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
     * Report a warning.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void warning(String message, Object model) {
        monitor.problem(new ProblemImpl(Severity.WARNING, message, model));
    }

    /**
     * Connect composite services to the component services that they promote.
     * 
     * @param composite
     * @param componentServices
     * @param problems
     */
    private void connectCompositeServices(Composite composite, Map<String, Component> components, Map<String, ComponentService> componentServices) {
    
        // Propagate interfaces from inner composite components' services to
        // their component services
        for (Component component : composite.getComponents()) {
            if (component.getImplementation() instanceof Composite) {
                for (ComponentService componentService : component.getServices()) {
                    Service service = componentService.getService();
                    if (service != null) {
                        if (componentService.getInterfaceContract() == null) {
                            componentService.setInterfaceContract(service.getInterfaceContract());
                        }
                    }
                }
            }
        }
    
        // Connect composite services to the component services that they
        // promote
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;
            ComponentService componentService = compositeService.getPromotedService();
            if (componentService != null && componentService.isUnresolved()) {
                
                String promotedComponentName = compositeService.getPromotedComponent().getName(); 
                String promotedServiceName;
                if (componentService.getName() != null) {
                    promotedServiceName = promotedComponentName + '/' + componentService.getName();
                } else {
                    promotedServiceName = promotedComponentName;
                }
                ComponentService promotedService = componentServices.get(promotedServiceName);
                if (promotedService != null) {
    
                    // Point to the resolved component
                    Component promotedComponent = components.get(promotedComponentName);
                    compositeService.setPromotedComponent(promotedComponent);
                    
                    // Point to the resolved component service
                    compositeService.setPromotedService(promotedService);
    
                    // Use the interface contract from the component service if
                    // none is specified on the composite service
                    if (compositeService.getInterfaceContract() == null) {
                        compositeService.setInterfaceContract(promotedService.getInterfaceContract());
                    }
    
                } else {
                    warning("Promoted component service not found: " + promotedServiceName, composite);
                }
            }
        }
    
    }

    /**
     * Resolves promoted references.
     * 
     * @param composite
     * @param componentReferences
     * @param problems
     */
    private void connectCompositeReferences(Composite composite, Map<String, ComponentReference> componentReferences) {
    
        // Propagate interfaces from inner composite components' references to
        // their component references
        for (Component component : composite.getComponents()) {
            if (component.getImplementation() instanceof Composite) {
                for (ComponentReference componentReference : component.getReferences()) {
                    Reference reference = componentReference.getReference();
                    if (reference != null) {
                        if (componentReference.getInterfaceContract() == null) {
                            componentReference.setInterfaceContract(reference.getInterfaceContract());
                        }
                    }
                }
            }
        }
    
        // Connect composite references to the component references
        // that they promote
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences = compositeReference.getPromotedReferences();
            for (int i = 0, n = promotedReferences.size(); i < n; i++) {
                ComponentReference componentReference = promotedReferences.get(i);
                if (componentReference.isUnresolved()) {
                    String componentReferenceName = componentReference.getName();
                    componentReference = componentReferences.get(componentReferenceName);
                    if (componentReference != null) {
    
                        // Point to the resolved component reference
                        promotedReferences.set(i, componentReference);
    
                        // Use the interface contract from the component
                        // reference if none
                        // is specified on the composite reference
                        if (compositeReference.getInterfaceContract() == null) {
                            compositeReference.setInterfaceContract(componentReference.getInterfaceContract());
                        }
    
                    } else {
                        warning("Promoted component reference not found: " + componentReferenceName, composite);
                    }
                }
            }
        }
    }
    
    private List<Target> createComponentReferenceTargets(Composite composite,
                                                         Map<String, Component> components,
                                                         Map<String, ComponentService> componentServices,
                                                         ComponentReference componentReference) {
        List<Target> targets = new ArrayList<Target>();
        
        if (componentReference.getAutowire() == Boolean.TRUE) {

            // Find suitable targets in the current composite for an
            // autowired reference
            Multiplicity multiplicity = componentReference.getMultiplicity();
            for (Component targetComponent : composite.getComponents()) {
                for (ComponentService targetComponentService : targetComponent.getServices()) {
                    if (componentReference.getInterfaceContract() == null ||
                        interfaceContractMapper.isCompatible(componentReference.getInterfaceContract(), targetComponentService.getInterfaceContract())) {
                        Target target = new Target(targetComponent, targetComponentService);
                        targets.add(target);
                        if (multiplicity == Multiplicity.ZERO_ONE || multiplicity == Multiplicity.ONE_ONE) {
                            break;
                        }
                    }
                }
            }
            
            if (multiplicity == Multiplicity.ONE_N || multiplicity == Multiplicity.ONE_ONE) {
                if (targets.size() == 0) {
                    warning("No target services found for the component reference to be autowired: " + componentReference
                                .getName(),
                            componentReference);
                }
            }

        } else if (!componentReference.getTargets().isEmpty()) {

            // Resolve targets specified on the component reference
            for (ComponentService componentService : componentReference.getTargets()) {
                
                // Resolve the target component and service
                String name = componentService.getName();
                ComponentService targetComponentService = componentServices.get(name);
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
                    if (componentReference.getInterfaceContract() == null ||
                        interfaceContractMapper.isCompatible(componentReference.getInterfaceContract(), targetComponentService.getInterfaceContract())) {

                        Target target = new Target(targetComponent, targetComponentService);
                        targets.add(target);

                        // mark the reference target as resolved. Used later when we are looking to 
                        // see if an sca binding is associated with a resolved target or not
                        componentService.setUnresolved(false);
                    } else {
                        warning("Incompatible interfaces on component reference and target: " + componentReference
                                    .getName()
                                    + " : "
                                    + componentService.getName(),
                                composite);
                    }
                } else {
                    // clone all the reference bindings into the target so that they
                    // can be used for comparison when the target is resolved at runtime
                    componentService.getBindings().addAll(componentReference.getBindings());
                    
                    warning("Component reference target not found, it might be a remote service: " + componentService.getName(), composite);
                }
            }
        } else if (componentReference.getReference() != null) {

            // Resolve targets from the corresponding reference in the
            // componentType
            for (ComponentService componentService : componentReference.getReference().getTargets()) {

                // Resolve the target component and service
                String name = componentService.getName();
                ComponentService targetComponentService = componentServices.get(name);
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
                    if (componentReference.getInterfaceContract() == null ||
                        interfaceContractMapper.isCompatible(componentReference.getInterfaceContract(), targetComponentService.getInterfaceContract())) {

                        Target target = new Target(targetComponent, targetComponentService);
                        targets.add(target);
                        
                        // mark the reference target as resolved. Used later when we are looking to 
                        // see if an sca binding is associated with a resolved target or not
                        componentService.setUnresolved(false);
                    } else {
                        warning("Incompatible interfaces on component reference and target: " + componentReference
                                    .getName()
                                    + " : "
                                    + componentService.getName(),
                                composite);
                    }
                } else {
                    warning("Reference target not found: " + componentService.getName(), composite);
                }
            }
        }
        return targets;
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
                                            Map<String, ComponentReference> componentReferences) {
        for (ComponentReference componentReference : componentReferences.values()) {
            
            List<Target> targets = createComponentReferenceTargets(composite, 
                                                                   components, 
                                                                   componentServices, 
                                                                   componentReference);

            // Select the reference bindings matching the target service bindings 
            List<Binding> selectedBindings = new ArrayList<Binding>();
    
            // Handle callback
            boolean bidirectional = false;
            if (componentReference.getInterfaceContract() != null && componentReference.getInterfaceContract().getCallbackInterface() != null) {
                bidirectional = true;
            }
            List<Binding> selectedCallbackBindings = bidirectional ? new ArrayList<Binding>() : null;
    
            for (Target target : targets) {
                
                Component targetComponent = target.getComponent();
                ComponentService targetComponentService = target.getService();
                if (targetComponentService.getService() instanceof CompositeService) {
                    
                    // Find the promoted component service
                    targetComponentService = ((CompositeService)targetComponentService.getService()).getPromotedService();
                }
                
                determinePolicySet(componentReference, targetComponentService);

                // Match the binding against the bindings of the target service
                Binding selected = BindingUtil.resolveBindings(componentReference, targetComponent, targetComponentService);
                if (selected == null) {
                    warning("Component reference doesn't have a matching binding", componentReference);
                } else {
                    selectedBindings.add(selected);
                }
                if (bidirectional) {
                    Binding selectedCallback = BindingUtil.resolveCallbackBindings(componentReference, targetComponent, targetComponentService);
                    if (selectedCallback != null) {
                        selectedCallbackBindings.add(selectedCallback);
                    }
                }
            }
            
            if (!targets.isEmpty()) {

                // Add all the effective bindings
                componentReference.getBindings().clear();
                componentReference.getBindings().addAll(selectedBindings);
                if (bidirectional) {
                    componentReference.getCallback().getBindings().clear();
                    componentReference.getCallback().getBindings().addAll(selectedCallbackBindings);
                }
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
                              Map<String, ComponentReference> componentReferences) {
    
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
                    warning("Wire source not found: " + source.getName(), composite);
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
                    warning("Wire target not found: " + source.getName(), composite);
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
    
                    resolvedReference.getTargets().add(resolvedService);
                } else {
                    warning("Incompatible interfaces on wire source and target: " + source.getName()
                        + " : "
                        + target.getName(), composite);
                }
            }
        }
    
        // Clear the list of wires
        composite.getWires().clear();
    }

    /**
     * Wire composite references in nested composites.
     * 
     * @param composite
     * @param problems
     */
    public void wireCompositeReferences(Composite composite) {
    
        // Process nested composites recursively
        // [rfeng] I comment out the following loop as there's no need to do the recursive wiring.
        /*
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                wireCompositeReferences((Composite)implementation);
            }
        }
        */
    
        // Process composite references declared in this composite
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences = getPromotedComponentReferences(compositeReference);
            for (ComponentReference promotedReference : promotedReferences) {
    
                reconcileReferenceBindings(compositeReference, promotedReference);
                if (compositeReference.getInterfaceContract() != null && // can be null in unit tests
                compositeReference.getInterfaceContract().getCallbackInterface() != null) {
                    SCABinding scaCallbackBinding = promotedReference.getCallbackBinding(SCABinding.class);
                    if (promotedReference.getCallback() != null) {
                        promotedReference.getCallback().getBindings().clear();
                    } else {
                        promotedReference.setCallback(assemblyFactory.createCallback());
                    }
                    if (scaCallbackBinding != null) {
                        promotedReference.getCallback().getBindings().add(scaCallbackBinding);
                    }
                    if (compositeReference.getCallback() != null) {
                        promotedReference.getCallback().getBindings().addAll(compositeReference.getCallback()
                            .getBindings());
                    }
                }
            }
        }
    
        // Process the component references declared on components
        // in this composite
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                for (ComponentReference componentReference : component.getReferences()) {
                    Reference implReference = componentReference.getReference();
                    if (implReference != null && implReference instanceof CompositeReference) {
                        CompositeReference compositeReference = (CompositeReference)implReference;
                        List<ComponentReference> promotedReferences =
                            getPromotedComponentReferences(compositeReference);
                        for (ComponentReference promotedReference : promotedReferences) {
    
                            // Override the configuration of the promoted reference
                            reconcileReferenceBindings(componentReference, promotedReference);
                            if (componentReference.getInterfaceContract() != null && // can be null in unit tests
                            componentReference.getInterfaceContract().getCallbackInterface() != null) {
                                SCABinding scaCallbackBinding = promotedReference.getCallbackBinding(SCABinding.class);
                                if (promotedReference.getCallback() != null) {
                                    promotedReference.getCallback().getBindings().clear();
                                } else {
                                    promotedReference.setCallback(assemblyFactory.createCallback());
                                }
                                if (scaCallbackBinding != null) {
                                    promotedReference.getCallback().getBindings().add(scaCallbackBinding);
                                }
                                if (componentReference.getCallback() != null) {
                                    promotedReference.getCallback().getBindings().addAll(componentReference
                                        .getCallback().getBindings());
                                }
                            }
    
                            // Wire the promoted reference to the actual
                            // non-composite component services
                            if (promotedReference.getMultiplicity() == Multiplicity.ONE_ONE || promotedReference
                                .getMultiplicity() == Multiplicity.ONE_ONE) {
                                // promotedReference.getTargets().clear();
                            }
                            for (ComponentService target : componentReference.getTargets()) {
                                if (target.getService() instanceof CompositeService) {
    
                                    // Wire to the actual component service
                                    // promoted by a composite service
                                    CompositeService compositeService = (CompositeService)target.getService();
                                    ComponentService componentService = compositeService.getPromotedService();
                                    if (componentService != null) {
                                        promotedReference.getTargets().add(componentService);
                                    }
                                } else {
    
                                    // Wire to a non-composite target service
                                    promotedReference.getTargets().add(target);
                                }
                            }
                        }
                    }
                }
            } else {
                for (ComponentReference componentReference : component.getReferences()) {
    
                    // Wire the component reference to the actual
                    // non-composite component services
                    List<ComponentService> targets = componentReference.getTargets();
                    for (int i = 0, n = targets.size(); i < n; i++) {
                        ComponentService target = targets.get(i);
                        if (target.getService() instanceof CompositeService) {
    
                            // Wire to the actual component service
                            // promoted by a composite service
                            CompositeService compositeService = (CompositeService)target.getService();
                            ComponentService componentService = compositeService.getPromotedService();
                            if (componentService != null) {
                                targets.set(i, componentService);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Follow a reference promotion chain down to the inner most (non composite)
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
     * Follow a reference promotion chain down to the inner most (non composite)
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
     * Override the bindings for a promoted reference from an outer component
     * reference
     * 
     * @param reference
     * @param promotedReference
     */
    private void reconcileReferenceBindings(Reference reference, ComponentReference promotedReference) {
        Set<Binding> bindings = new HashSet<Binding>();
        bindings.addAll(promotedReference.getBindings());
        bindings.addAll(reference.getBindings());
        promotedReference.getBindings().clear();
        for (Binding binding : bindings) {
            if ((!(binding instanceof WireableBinding)) || binding.getURI() != null) {
                promotedReference.getBindings().add(binding);
            }
        }
        if (promotedReference.getMultiplicity() == Multiplicity.ONE_ONE || promotedReference.getMultiplicity() == Multiplicity.ZERO_ONE) {
            if (promotedReference.getBindings().size() > 1) {
                warning("Component reference " + promotedReference.getName() + " has more than one wires",
                        promotedReference);
            }
        }
        Set<Binding> callbackBindings = new HashSet<Binding>();
        if (promotedReference.getCallback() != null) {
            callbackBindings.addAll(promotedReference.getCallback().getBindings());
        }
        if (reference.getCallback() != null) {
            callbackBindings.addAll(reference.getCallback().getBindings());
        }
        promotedReference.setCallback(assemblyFactory.createCallback());
        for (Binding binding : callbackBindings) {
            if ((!(binding instanceof WireableBinding)) || binding.getURI() != null) {
                promotedReference.getCallback().getBindings().add(binding);
            }
        }
    }
    
    public void computePolicies(Composite composite) {
        List<Intent> compositeIntents = null;
        List<PolicySet> compositePolicySets = null;
        if (composite instanceof PolicySetAttachPoint) {
            compositeIntents = ((PolicySetAttachPoint)composite).getRequiredIntents();
            compositePolicySets = ((PolicySetAttachPoint)composite).getPolicySets();
        }
        
        //Initialize service bindings
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;
            
            //inherit intents and policysets defined at composite level
            addInheritedIntents(compositeIntents, service.getRequiredIntents());
            addInheritedPolicySets(compositePolicySets, service.getPolicySets(), false);
            
            //inherit intents and policies from promoted service
            addInheritedIntents(compositeService.getPromotedService().getRequiredIntents(), 
                                compositeService.getRequiredIntents());
            addInheritedPolicySets(compositeService.getPromotedService().getPolicySets(), 
                                   compositeService.getPolicySets(), true);
            
            if (compositeService.getCallback() != null) {
                addInheritedIntents(compositeService.getRequiredIntents(), 
                                    compositeService.getCallback().getRequiredIntents());
                addInheritedPolicySets(compositeService.getPolicySets(), 
                                       compositeService.getCallback().getPolicySets(), 
                                       false);
            }
            
            computeBindingIntentsAndPolicySets(service);
        }
        
        for (Reference reference : composite.getReferences()) {
            addInheritedIntents(compositeIntents, reference.getRequiredIntents());
            addInheritedPolicySets(compositePolicySets, reference.getPolicySets(), false);
            
            CompositeReference compReference = (CompositeReference)reference;
            for ( Reference promotedReference : compReference.getPromotedReferences() ) {
                addInheritedIntents(promotedReference.getRequiredIntents(), 
                                     reference.getRequiredIntents());
            
                addInheritedPolicySets(promotedReference.getPolicySets(), 
                                     reference.getPolicySets(), true);
            }
            
            if (compReference.getCallback() != null) {
                addInheritedIntents(compReference.getRequiredIntents(), 
                                    compReference.getCallback().getRequiredIntents());
                addInheritedPolicySets(compReference.getPolicySets(), 
                                       compReference.getCallback().getPolicySets(), 
                                       false);
            }
            
            computeBindingIntentsAndPolicySets(reference);
        }
        
        for (Component component : composite.getComponents()) {
            //Inherit intents defined at the composite level
            addInheritedIntents(compositeIntents, component.getRequiredIntents());
            
            // Inherit policysets defined at the composite level
            addInheritedPolicySets(compositePolicySets, component.getPolicySets(), false);
            
            for (ComponentService componentService : component.getServices()) {
                //inherit intents and policysets from component
                addInheritedIntents(component.getRequiredIntents(), componentService.getRequiredIntents());
                addInheritedPolicySets(component.getPolicySets(), componentService.getPolicySets(), false);

                Service service = componentService.getService();
                if (service != null) {
                    // reconcile intents and policysets
                    addInheritedIntents(service.getRequiredIntents(), componentService.getRequiredIntents());
                    addInheritedPolicySets(service.getPolicySets(), componentService.getPolicySets(), true);
                }
                
                if ( componentService.getCallback() != null ) {
                    addInheritedIntents(componentService.getRequiredIntents(), 
                                        componentService.getCallback().getRequiredIntents());
                    addInheritedPolicySets(componentService.getPolicySets(), 
                                           componentService.getCallback().getPolicySets(), 
                                           false);
                }
                
                //compute intents and policyset for each binding
                computeBindingIntentsAndPolicySets(componentService);

            }
            
            for (ComponentReference componentReference : component.getReferences()) {
                //inherit intents and policysets from component
                addInheritedIntents(component.getRequiredIntents(), componentReference.getRequiredIntents());
                addInheritedPolicySets(component.getPolicySets(), componentReference.getPolicySets(), false);

                Reference reference = componentReference.getReference();
                if (reference != null) {
                    // reconcile intents and policysets
                    addInheritedIntents(reference.getRequiredIntents(), componentReference.getRequiredIntents());
                    addInheritedPolicySets(reference.getPolicySets(), componentReference.getPolicySets(), true);
                }
                
                if ( componentReference.getCallback() != null ) {
                    addInheritedIntents(componentReference.getRequiredIntents(), 
                                        componentReference.getCallback().getRequiredIntents());
                    addInheritedPolicySets(componentReference.getPolicySets(), 
                                           componentReference.getCallback().getPolicySets(), 
                                           false);
                }
                
                //compute intents and policyset for each binding
                computeBindingIntentsAndPolicySets(componentReference);

            }
        }
        
    }
    
    private void addInheritedIntents(List<Intent> sourceList, List<Intent> targetList) {
        if (sourceList != null) {
            targetList.addAll(sourceList);
        }
    }
    
    private  void addInheritedPolicySets(List<PolicySet> sourceList, List<PolicySet> targetList, boolean checkOverrides) {
        //check overrides is true when policysets are to be copied from componentType to component level
        if ( checkOverrides ) {
            //aggregate all the provided intents present in the target
            List<Intent> targetProvidedIntents = new ArrayList<Intent>();
            for ( PolicySet policySet : targetList ) {
                targetProvidedIntents.addAll(policySet.getProvidedIntents());
            }
            
            //for every policy set in the source check if it provides one of the intents that is 
            //already provided by the policysets in the destination and do not copy them.
            for ( PolicySet policySet : sourceList ) {
                for ( Intent sourceProvidedIntent : policySet.getProvidedIntents() ) {
                    if ( !targetProvidedIntents.contains(sourceProvidedIntent) ) {
                        targetList.add(policySet);
                    }
                }
            }
        } else {
            targetList.addAll(sourceList);
        }
    }
    
    private void computeBindingIntentsAndPolicySets(Service service) {
        computeIntents(service.getBindings(), service.getRequiredIntents());
        computePolicySets(service, service.getBindings(), service.getPolicySets());
        if ( service.getCallback() != null ) {
            computeIntents(service.getCallback().getBindings(), 
                           service.getCallback().getRequiredIntents());
            computePolicySets(service, 
                              service.getCallback().getBindings(), 
                              service.getCallback().getPolicySets());
        }
        trimProvidedIntents(service.getBindings());
    }
    
    private void computeBindingIntentsAndPolicySets(Reference reference) {
        computeIntents(reference.getBindings(), reference.getRequiredIntents());
        computePolicySets(reference, reference.getBindings(), reference.getPolicySets());
        
        if ( reference.getCallback() != null ) {
            computeIntents(reference.getCallback().getBindings(), 
                           reference.getCallback().getRequiredIntents());
            computePolicySets(reference, 
                              reference.getCallback().getBindings(), 
                              reference.getCallback().getPolicySets());
        }
        trimProvidedIntents(reference.getBindings());
    }
    
    private void computeIntents(List<Binding> bindings, List<Intent> inheritedIntents) {
        boolean found = false;

        for (Binding binding : bindings) {
            if (binding instanceof IntentAttachPoint) {
                IntentAttachPoint policiedBinding = (IntentAttachPoint)binding;
                IntentAttachPointType bindingType = policiedBinding.getType();

                //expand profile intents
                List<Intent> expandedIntents = expandProfileIntents(policiedBinding.getRequiredIntents());
                policiedBinding.getRequiredIntents().clear();
                policiedBinding.getRequiredIntents().addAll(expandedIntents);
                
                //validate intents specified for the binding
                for (Intent intent : policiedBinding.getRequiredIntents()) {
                    for (QName constrained : intent.getConstrains()) {
                        if (bindingType.getName().getNamespaceURI().equals(constrained
                            .getNamespaceURI()) && bindingType.getName().getLocalPart()
                            .startsWith(constrained.getLocalPart())) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        warning("Policy Intent '" + intent.getName()
                            + "' does not constrain binding type  "
                            + bindingType, binding);
                    }
                }
                
                //expand profile intents in inherited intents
                expandedIntents = expandProfileIntents(inheritedIntents);
                inheritedIntents.clear();
                inheritedIntents.addAll(expandedIntents);

                //validate if inherited intent applies to the binding and 
                //only add such intents to the binding
                for (Intent intent : inheritedIntents) {
                    for (QName constrained : intent.getConstrains()) {
                        if (bindingType.getName().getNamespaceURI().equals(constrained
                            .getNamespaceURI()) && bindingType.getName().getLocalPart()
                            .startsWith(constrained.getLocalPart())) {
                            policiedBinding.getRequiredIntents().add(intent);
                            break;
                        }
                    }
                }
                
                Map<QName, Intent> intentsTable = new HashMap<QName, Intent>();
                //remove duplicates
                for ( Intent intent : policiedBinding.getRequiredIntents() ) {
                    intentsTable.put(intent.getName(), intent);
                }
                
                Map<QName, Intent> intentsTableCopy = new HashMap<QName, Intent>(intentsTable);
                //if qualified form of intent exists remove the unqualified form
                for ( Intent intent : intentsTableCopy.values() ) {
                    if ( intent instanceof QualifiedIntent ) {
                        QualifiedIntent qualifiedIntent = (QualifiedIntent)intent;
                        if ( intentsTable.get(qualifiedIntent.getQualifiableIntent().getName()) != null ) {
                            intentsTable.remove(qualifiedIntent.getQualifiableIntent().getName());
                        }
                    }
                }
                policiedBinding.getRequiredIntents().clear();
                policiedBinding.getRequiredIntents().addAll(intentsTable.values());
                
                //exclude intents that are inherently supported by the binding type
                List<Intent> requiredIntents = new ArrayList<Intent>(policiedBinding.getRequiredIntents());
                for ( Intent intent : requiredIntents ) {
                    if ( bindingType.getAlwaysProvidedIntents() != null &&
                        bindingType.getAlwaysProvidedIntents().contains(intent) ) {
                        policiedBinding.getRequiredIntents().remove(intent);
                    }
                }
                
            }
        }
    }

    private void computePolicySets(Base parent,
                                   List<Binding> bindings,
                                   List<PolicySet> inheritedPolicySets) {
        String appliesTo = null;
        HashMap<QName, PolicySet> policySetTable = new HashMap<QName, PolicySet>();
        
        for (Binding binding : bindings) {
            if (binding instanceof PolicySetAttachPoint) {
                PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)binding;
                IntentAttachPointType bindingType = policiedBinding.getType();

                //validate policysets specified for the binding
                for (PolicySet policySet : policiedBinding.getPolicySets()) {
                    appliesTo = policySet.getAppliesTo();

                    if (!isPolicySetApplicable(parent, appliesTo, bindingType)) {
                        warning("Policy Set '" + policySet.getName()
                            + "' does not apply to binding type  "
                            + bindingType, binding);

                    }
                }
                
                for (PolicySet policySet : inheritedPolicySets) {
                    appliesTo = policySet.getAppliesTo();
                    if (isPolicySetApplicable(parent, appliesTo, bindingType)) {
                        policiedBinding.getPolicySets().add(policySet);
                    }
                }
                
                //get rid of duplicate entries
                for ( PolicySet policySet : policiedBinding.getPolicySets() ) {
                    policySetTable.put(policySet.getName(), policySet);
                }
                policiedBinding.getPolicySets().clear();
                policiedBinding.getPolicySets().addAll(policySetTable.values());
                
                //expand profile intents
                List<Intent> expandedIntents = null;
                for ( PolicySet policySet : policiedBinding.getPolicySets() ) {
                    expandedIntents = expandProfileIntents(policySet.getProvidedIntents());
                    policySet.getProvidedIntents().clear();
                    policySet.getProvidedIntents().addAll(expandedIntents);
                }
            }
        }
    }
    
    private boolean isPolicySetApplicable(Base parent,
                                          String xpath,
                                          IntentAttachPointType bindingType) {
        //create a xml node out of the parent object.. i.e. write the parent object as scdl fragment
        //invoke PropertyUtil.evaluate(null, node, xpath)
        //verify the result Node's QName against the bindingType's name
        
        if (parent instanceof ComponentReference) {
        } else if (parent instanceof ComponentReference) {
        } else if (parent instanceof Component) {
        } else if (parent instanceof CompositeService) {
        } else if (parent instanceof CompositeReference) {

        }
        return true;
    }
    
    private List<Intent> expandProfileIntents(List<Intent> intents) {
        List<Intent> expandedIntents = new ArrayList<Intent>();
        for ( Intent intent : intents ) {
            if ( intent instanceof ProfileIntent ) {
                ProfileIntent profileIntent = (ProfileIntent)intent;
                List<Intent> requiredIntents = profileIntent.getRequiredIntents();
                expandedIntents.addAll(expandProfileIntents(requiredIntents));
            } else {
                expandedIntents.add(intent);
            }
        }
        return expandedIntents;
    }
    
    private void trimProvidedIntents(List<Binding> bindings) {
        for (Binding binding : bindings) {
            if (binding instanceof PolicySetAttachPoint) {
                trimProvidiedIntents((PolicySetAttachPoint)binding);
            }
        }
    }
    
    private void trimProvidiedIntents(PolicySetAttachPoint policiedBinding) {
        for ( PolicySet policySet : policiedBinding.getPolicySets() ) {
            for ( Intent providedIntent : policySet.getProvidedIntents() ) {
                if ( policiedBinding.getRequiredIntents().contains(providedIntent) ) {
                    policiedBinding.getRequiredIntents().remove(providedIntent);
                }
            }
        }
    }
    
    private void determinePolicySet(ComponentReference componentReference, ComponentService componentService) {
        //add the target component's intents to the binding
        for ( Binding aBinding : componentReference.getBindings() ) {
            if ( aBinding instanceof PolicySetAttachPoint ) {
                PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)aBinding;
                IntentAttachPointType bindingType = policiedBinding.getType();
                for ( Intent intent : componentService.getRequiredIntents() ) {
                    if ( !policiedBinding.getRequiredIntents().contains(intent) ) {
                        for (QName constrained : intent.getConstrains()) {
                            if (bindingType.getName().getNamespaceURI().equals(constrained
                                .getNamespaceURI()) && bindingType.getName().getLocalPart()
                                .startsWith(constrained.getLocalPart())) {
                                policiedBinding.getRequiredIntents().add(intent);
                                break;
                            }
                        }
                    }
                }
                trimProvidiedIntents(policiedBinding);
                //determine additional policysets that match remaining intents
                if ( policiedBinding.getRequiredIntents().size() > 0 ) {
                    //TODO: resolved to domain policy registry and attach suitable policy sets to the binding
                    //if there are intents that are not provided by any policy set throw a warning
                }
            }
            
        }
    }


}
