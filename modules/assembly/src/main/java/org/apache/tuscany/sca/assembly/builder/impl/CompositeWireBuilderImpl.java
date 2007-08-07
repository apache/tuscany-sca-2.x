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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
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
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem.Severity;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;

public class CompositeWireBuilderImpl {

    private CompositeBuilderMonitor monitor;
    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private InterfaceContractMapper interfaceContractMapper;
    
    public CompositeWireBuilderImpl(AssemblyFactory assemblyFactory, SCABindingFactory scaBindingFactory, InterfaceContractMapper interfaceContractMapper, CompositeBuilderMonitor monitor) {
        this.assemblyFactory = assemblyFactory;
        this.scaBindingFactory = scaBindingFactory;
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

        // Index and bind all component services and references
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();

        // Create SCA bindings on all component services and references
        createSCABindings(composite, componentServices, componentReferences);

        // Connect composite services and references to the component
        // services and references that they promote
        connectCompositeServices(composite, componentServices);
        connectCompositeReferences(composite, componentReferences);

        // Connect component references to their targets
        connectComponentReferences(composite, componentServices, componentReferences);

        // Connect component references as described in wires
        connectWires(composite, componentServices, componentReferences);

        // Resolve sourced properties
        resolveSourcedProperties(composite, null);

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
     * Create SCA bindings for component services and references.
     * 
     * @param composite
     * @param componentServices
     * @param componentReferences
     * @param problems
     */
    private void createSCABindings(Composite composite,
                                   Map<String, ComponentService> componentServices,
                                   Map<String, ComponentReference> componentReferences) {
    
        for (Component component : composite.getComponents()) {
            int i = 0;
            for (ComponentService componentService : component.getServices()) {
                String uri = component.getName() + '/' + componentService.getName();
                componentServices.put(uri, componentService);
                if (i == 0) {
                    componentServices.put(component.getName(), componentService);
                }
                i++;
    
                // Create and configure an SCA binding for the service
                if (componentService.getBindings().isEmpty()) {
                    SCABinding scaBinding = componentService.getBinding(SCABinding.class);
                    if (scaBinding == null) {
                        scaBinding = scaBindingFactory.createSCABinding();
                        scaBinding.setName(componentService.getName());
                        componentService.getBindings().add(scaBinding);
                    }
                    scaBinding.setComponent(component);
                    scaBinding.setURI(uri);
                }
    
                // if service has a callback, create and configure an SCA binding for the callback
                if (componentService.getInterfaceContract() != null && // can be null in unit tests
                componentService.getInterfaceContract().getCallbackInterface() != null) {
                    if (componentService.getCallback() != null && componentService.getCallback().getBindings()
                        .isEmpty()) {
                        SCABinding scaCallbackBinding = componentService.getCallbackBinding(SCABinding.class);
                        if (scaCallbackBinding == null) {
                            scaCallbackBinding = scaBindingFactory.createSCABinding();
                            scaCallbackBinding.setName(componentService.getName());
                            if (componentService.getCallback() == null) {
                                componentService.setCallback(assemblyFactory.createCallback());
                            }
                            componentService.getCallback().getBindings().add(scaCallbackBinding);
                        }
                        scaCallbackBinding.setComponent(component);
                    }
                }
            }
            for (ComponentReference componentReference : component.getReferences()) {
                String uri = component.getName() + '/' + componentReference.getName();
                componentReferences.put(uri, componentReference);
    
                if (componentReference.getBindings().isEmpty()) {
                    // Create and configure an SCA binding for the reference
                    SCABinding scaBinding = componentReference.getBinding(SCABinding.class);
                    if (scaBinding == null) {
                        scaBinding = scaBindingFactory.createSCABinding();
                        scaBinding.setName(componentReference.getName());
                        componentReference.getBindings().add(scaBinding);
                    }
                    scaBinding.setComponent(component);
                }
    
                // if reference has a callback, create and configure an SCA binding for the callback
                if (componentReference.getInterfaceContract() != null && // can be null in unit tests
                componentReference.getInterfaceContract().getCallbackInterface() != null) {
                    if (componentReference.getCallback() != null && componentReference.getCallback().getBindings()
                        .isEmpty()) {
                        SCABinding scaCallbackBinding = componentReference.getCallbackBinding(SCABinding.class);
                        if (scaCallbackBinding == null) {
                            scaCallbackBinding = scaBindingFactory.createSCABinding();
                            scaCallbackBinding.setName(componentReference.getName());
                            if (componentReference.getCallback() == null) {
                                componentReference.setCallback(assemblyFactory.createCallback());
                            }
                            componentReference.getCallback().getBindings().add(scaCallbackBinding);
                        }
                        scaCallbackBinding.setComponent(component);
                    }
                }
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
    private void connectCompositeServices(Composite composite, Map<String, ComponentService> componentServices) {
    
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
                ComponentService promotedService = componentServices.get(componentService.getName());
                if (promotedService != null) {
    
                    // Point to the resolved component service
                    compositeService.setPromotedService(promotedService);
    
                    // Use the interface contract from the component service if
                    // none is specified on the composite service
                    if (compositeService.getInterfaceContract() == null) {
                        compositeService.setInterfaceContract(promotedService.getInterfaceContract());
                    }
    
                } else {
                    warning("Promoted component service not found: " + componentService.getName(), composite);
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

    /**
     * Connect references to their targets.
     * 
     * @param composite
     * @param componentServices
     * @param componentReferences
     * @param problems
     */
    private void connectComponentReferences(Composite composite,
                                            Map<String, ComponentService> componentServices,
                                            Map<String, ComponentReference> componentReferences) {
    
        for (ComponentReference componentReference : componentReferences.values()) {
            List<ComponentService> targets = componentReference.getTargets();
    
            if (componentReference.isAutowire()) {
    
                // Find suitable targets in the current composite for an
                // autowired reference
                Multiplicity multiplicity = componentReference.getMultiplicity();
                for (Component component : composite.getComponents()) {
                    for (ComponentService componentService : component.getServices()) {
                        if (componentReference.getInterfaceContract() == null || interfaceContractMapper
                            .isCompatible(componentReference.getInterfaceContract(), componentService
                                .getInterfaceContract())) {
    
                            targets.add(componentService);
                            if (multiplicity == Multiplicity.ZERO_ONE || multiplicity == Multiplicity.ONE_ONE) {
                                break;
                            }
                        }
                    }
                }
    
            } else if (!targets.isEmpty()) {
    
                // Resolve targets specified on the component reference
                for (int i = 0, n = targets.size(); i < n; i++) {
                    ComponentService target = targets.get(i);
                    if (target.isUnresolved()) {
                        ComponentService resolved = componentServices.get(target.getName());
                        if (resolved != null) {
    
                            // Check that the target component service provides
                            // a superset of
                            // the component reference interface
                            if (componentReference.getInterfaceContract() == null || interfaceContractMapper
                                .isCompatible(componentReference.getInterfaceContract(), resolved
                                    .getInterfaceContract())) {
    
                                targets.set(i, resolved);
                            } else {
                                warning("Incompatible interfaces on component reference and target: " + componentReference
                                            .getName()
                                            + " : "
                                            + target.getName(),
                                        composite);
                            }
                        } else {
                            warning("Component reference target not found: " + target.getName(), composite);
                        }
                    }
                }
            } else if (componentReference.getReference() != null) {
    
                // Resolve targets from the corresponding reference in the
                // componentType
                for (ComponentService target : componentReference.getReference().getTargets()) {
                    if (target.isUnresolved()) {
                        ComponentService resolved = componentServices.get(target.getName());
                        if (resolved != null) {
    
                            // Check that the target component service provides
                            // a superset of
                            // the component reference interface
                            if (componentReference.getInterfaceContract() == null || interfaceContractMapper
                                .isCompatible(componentReference.getInterfaceContract(), resolved
                                    .getInterfaceContract())) {
    
                                targets.add(resolved);
                            } else {
                                warning("Incompatible interfaces on component reference and target: " + componentReference
                                            .getName()
                                            + " : "
                                            + target.getName(),
                                        composite);
                            }
                        } else {
                            warning("Reference target not found: " + target.getName(), composite);
                        }
                    }
                }
            }
            // [rfeng] For any targets, select the matching binding for the reference
            List<Binding> selectedBindings = new ArrayList<Binding>();
    
            // Handle callback
            boolean bidirectional = false;
            if (componentReference.getInterfaceContract() != null && componentReference.getInterfaceContract()
                .getCallbackInterface() != null) {
                bidirectional = true;
            }
            List<Binding> selectedCallbackBindings = bidirectional ? new ArrayList<Binding>() : null;
    
            for (ComponentService service : targets) {
                ComponentService target = service;
                if (service.getService() instanceof CompositeService) {
                    // Normalize the service to be the final target
                    target = ((CompositeService)service.getService()).getPromotedService();
                }
                Binding selected = BindingUtil.resolveBindings(componentReference, target);
                if (selected == null) {
                    warning("Component reference doesn't have a matching binding", componentReference);
                } else {
                    selectedBindings.add(selected);
                }
                if (bidirectional) {
                    Binding selectedCallback = BindingUtil.resolveCallbackBindings(componentReference, target);
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
                // Remove the targets since they have been normalized as bindings
                targets.clear();
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
     * @param composite
     */
    private void resolveSourcedProperties(Composite composite, List<ComponentProperty> propertySettings) {
        // Resolve properties
        Map<String, Property> compositeProperties = new HashMap<String, Property>();
        ComponentProperty componentProperty = null;
        for (Property p : composite.getProperties()) {
            componentProperty = getComponentPropertyByName(p.getName(), propertySettings);
            if (componentProperty != null) {
                compositeProperties.put(p.getName(), componentProperty);
            } else {
                compositeProperties.put(p.getName(), p);
            }
        }
    
        for (Component component : composite.getComponents()) {
            try {
                PropertyUtil.sourceComponentProperties(compositeProperties, component);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Implementation impl = component.getImplementation();
            if (impl instanceof Composite) {
                resolveSourcedProperties((Composite)impl, component.getProperties());
            }
        }
    }

    private ComponentProperty getComponentPropertyByName(String propertyName, List<ComponentProperty> properties) {
        if (properties != null) {
            for (ComponentProperty aProperty : properties) {
                if (aProperty.getName().equals(propertyName)) {
                    return aProperty;
                }
            }
        }
        return null;
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
                    CompositeReference compositeReference = (CompositeReference)componentReference.getReference();
                    if (compositeReference != null) {
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

}
