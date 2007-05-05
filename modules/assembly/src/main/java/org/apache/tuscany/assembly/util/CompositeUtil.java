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
package org.apache.tuscany.assembly.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Base;
import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.assembly.Wire;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;

/**
 * A utility class that handles the configuration of the components inside a
 * composite and the wiring of component references to component services.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeUtil {

    private AssemblyFactory assemblyFactory;
    private InterfaceContractMapper interfaceContractMapper;

    /**
     * Constructs a new composite util.
     * 
     * @param assemblyFactory
     * @param interfaceContractMapper
     */
    public CompositeUtil(AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper) {
        this.assemblyFactory = assemblyFactory;
        this.interfaceContractMapper = interfaceContractMapper;
    }

    /**
     * Configure and wire a composite.
     * 
     * @param composite
     * @param problems
     */
    public void configureAndWire(Composite composite, List<Base> problems) {

        // Collect and fuse includes
        fuseIncludes(composite, problems);

        // Expand nested composites
        expandComposites(composite, problems);

        // Configure all components
        configureComponents(composite, problems);

        // Wire the composite
        wireComposite(composite, problems);

        // Activate composite services
        activateCompositeServices(composite, problems);

        // Wire composite references
        wireCompositeReferences(composite, problems);
    }

    /**
     * Configure and wire a composite.
     * 
     * @param composite
     * @param problems
     * @deprecated
     */
    public void oldConfigureAndWire(Composite composite, List<Base> problems) {

        // Collect and fuse includes
        fuseIncludes(composite, problems);

        // Configure all components
        configureComponents(composite, problems);

        // Wire the composite
        wireComposite(composite, problems);

    }

    /**
     * Collect all includes in a graph of includes.
     * 
     * @param composite
     * @param includes
     */
    private void collectIncludes(Composite composite, List<Composite> includes) {
        for (Composite include : composite.getIncludes()) {
            includes.add(include);
            collectIncludes(include, includes);
        }
    }

    /**
     * Copy a list of includes into a composite.
     * 
     * @param composite
     * @param includes
     */
    protected void fuseIncludes(Composite composite, List<Base> problems) {

        // First collect all includes
        List<Composite> includes = new ArrayList<Composite>();
        collectIncludes(composite, includes);

        // Then clone them
        for (Composite include : includes) {
            Composite clone;
            try {
                clone = (Composite)include.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            composite.getComponents().addAll(clone.getComponents());
            composite.getServices().addAll(clone.getServices());
            composite.getReferences().addAll(clone.getReferences());
            composite.getProperties().addAll(clone.getProperties());
            composite.getWires().addAll(clone.getWires());
            composite.getPolicySets().addAll(clone.getPolicySets());
            composite.getRequiredIntents().addAll(clone.getRequiredIntents());
        }

        // Clear the list of includes
        composite.getIncludes().clear();
    }

    /**
     * Reconcile component services and services defined on the component type.
     * 
     * @param component
     * @param services
     * @param componentServices
     * @param problems
     */
    private void reconcileServices(Component component,
                                   Map<String, Service> services,
                                   Map<String, ComponentService> componentServices,
                                   List<Base> problems) {

        // Connect each component service to the corresponding service
        for (ComponentService componentService : component.getServices()) {
            Service service = services.get(componentService.getName());
            if (service != null) {
                componentService.setService(service);
            } else {
                problems.add(componentService);
            }
        }

        // Create a component service for each service
        if (component.getImplementation() != null) {
            for (Service service : component.getImplementation().getServices()) {
                if (!componentServices.containsKey(service.getName())) {
                    ComponentService componentService = assemblyFactory.createComponentService();
                    componentService.setName(service.getName());
                    componentService.setService(service);
                    component.getServices().add(componentService);
                }
            }
        }

        // Reconcile each component service with its service
        for (ComponentService componentService : component.getServices()) {
            Service service = componentService.getService();
            if (service != null) {

                // Reconcile interface
                if (componentService.getInterfaceContract() != null) {
                    if (!componentService.getInterfaceContract().equals(service.getInterfaceContract())) {
                        if (!interfaceContractMapper.isCompatible(componentService.getInterfaceContract(), service
                            .getInterfaceContract())) {
                            problems.add(componentService);
                        }
                    }
                } else {
                    componentService.setInterfaceContract(service.getInterfaceContract());
                }

                // Reconcile bindings
                if (componentService.getBindings().isEmpty()) {
                    componentService.getBindings().addAll(service.getBindings());
                }
            }

        }
    }

    /**
     * Reconcile component references with the references defined on the
     * component type.
     * 
     * @param component
     * @param references
     * @param componentReferences
     * @param problems
     */
    private void reconcileReferences(Component component,
                                     Map<String, Reference> references,
                                     Map<String, ComponentReference> componentReferences,
                                     List<Base> problems) {

        // Connect each component reference to the corresponding reference
        for (ComponentReference componentReference : component.getReferences()) {
            Reference reference = references.get(componentReference.getName());
            if (reference != null) {
                componentReference.setReference(reference);
            } else {
                if (!componentReference.getName().startsWith("$self$.")) {
                    problems.add(componentReference);
                }
            }
        }

        // Create a component reference for each reference
        if (component.getImplementation() != null) {
            for (Reference reference : component.getImplementation().getReferences()) {
                if (!componentReferences.containsKey(reference.getName())) {
                    ComponentReference componentReference = assemblyFactory.createComponentReference();
                    componentReference.setName(reference.getName());
                    componentReference.setReference(reference);
                    component.getReferences().add(componentReference);
                }
            }
        }

        // Reconcile each component reference with its reference
        for (ComponentReference componentReference : component.getReferences()) {
            Reference reference = componentReference.getReference();
            if (reference != null) {

                // Reconcile multiplicity
                if (componentReference.getMultiplicity() != null) {
                    if (!ReferenceUtil.isValidMultiplicityOverride(reference.getMultiplicity(), componentReference
                        .getMultiplicity())) {
                        problems.add(componentReference);
                    }
                } else {
                    componentReference.setMultiplicity(reference.getMultiplicity());
                }

                // Reconcile interface
                if (componentReference.getInterfaceContract() != null) {
                    if (!componentReference.getInterfaceContract().equals(reference.getInterfaceContract())) {
                        if (!interfaceContractMapper.isCompatible(reference.getInterfaceContract(), componentReference
                            .getInterfaceContract())) {
                            problems.add(componentReference);
                        }
                    }
                } else {
                    componentReference.setInterfaceContract(reference.getInterfaceContract());
                }

                // Reconcile bindings
                if (componentReference.getBindings().isEmpty()) {
                    componentReference.getBindings().addAll(reference.getBindings());
                }

                // Propagate autowire setting from the component
                if (component.isAutowire()) {
                    componentReference.setAutowire(true);
                }

                // Reconcile targets
                if (componentReference.getTargets().isEmpty()) {
                    componentReference.getTargets().addAll(reference.getTargets());
                }
            }
        }
    }

    /**
     * Reconcile component properties and the properties defined by the
     * component type.
     * 
     * @param component
     * @param properties
     * @param componentProperties
     * @param problems
     */
    private void reconcileProperties(Component component,
                                     Map<String, Property> properties,
                                     Map<String, ComponentProperty> componentProperties,
                                     List<Base> problems) {

        // Connect component properties to their properties
        for (ComponentProperty componentProperty : component.getProperties()) {
            Property property = properties.get(componentProperty.getName());
            if (property != null) {
                componentProperty.setProperty(property);
            } else {
                problems.add(componentProperty);
            }
        }

        // Create component properties for all properties
        if (component.getImplementation() != null) {
            for (Property property : component.getImplementation().getProperties()) {
                if (!componentProperties.containsKey(property.getName())) {
                    ComponentProperty componentProperty = assemblyFactory.createComponentProperty();
                    componentProperty.setName(property.getName());
                    componentProperty.setMany(property.isMany());
                    componentProperty.setXSDElement(property.getXSDElement());
                    componentProperty.setXSDType(property.getXSDType());
                    componentProperty.setProperty(property);
                    component.getProperties().add(componentProperty);
                }
            }
        }

        // Reconcile component properties and their properties
        for (ComponentProperty componentProperty : component.getProperties()) {
            Property property = componentProperty.getProperty();
            if (property != null) {

                // Check that a component property does not override the
                // mustSupply attribute
                if (!property.isMustSupply() && componentProperty.isMustSupply()) {
                    problems.add(componentProperty);
                }

                // Default to the mustSupply attribute specified on the property
                if (!componentProperty.isMustSupply())
                    componentProperty.setMustSupply(property.isMustSupply());

                // Default to the value specified on the property
                if (componentProperty.getValue() == null) {
                    componentProperty.setValue(property.getValue());
                }

                // Check that a value is supplied
                if (componentProperty.getValue() == null && property.isMustSupply()) {
                    problems.add(componentProperty);
                }

                // Check that a a component property does not override the
                // many attribute
                if (!property.isMany() && componentProperty.isMany()) {
                    problems.add(componentProperty);
                }

                // Default to the many attribute defined on the property
                componentProperty.setMany(property.isMany());

                // Default to the type and element defined on the property
                if (componentProperty.getXSDType() == null) {
                    componentProperty.setXSDType(property.getXSDType());
                }
                if (componentProperty.getXSDElement() == null) {
                    componentProperty.setXSDElement(property.getXSDElement());
                }

                // Check that a type or element are specified
                if (componentProperty.getXSDElement() == null && componentProperty.getXSDType() == null) {
                    problems.add(componentProperty);
                }
            }
        }
    }

    /**
     * Configure components in the composite.
     * 
     * @param composite
     * @param problems
     */
    protected void configureComponents(Composite composite, List<Base> problems) {
        configureComponents(composite, null, problems);
    }

    /**
     * Configure components in the composite.
     * 
     * @param composite
     * @param uri
     * @param problems
     */
    private void configureComponents(Composite composite, String uri, List<Base> problems) {

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {

            // Initialize component URI
            String componentURI;
            if (uri == null) {
                componentURI = component.getName();
            } else {
                componentURI = uri + "/" + component.getName();
            }
            component.setURI(componentURI);

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // Process nested composite
                configureComponents((Composite)implementation, componentURI, problems);
            }
        }

        // Set default binding names
        for (Service service : composite.getServices()) {
            for (Binding binding : service.getBindings()) {
                if (binding.getName() == null) {
                    binding.setName(service.getName());
                }
            }
        }
        for (Reference reference : composite.getReferences()) {
            for (Binding binding : reference.getBindings()) {
                if (binding.getName() == null) {
                    binding.setName(reference.getName());
                }
            }
        }

        // Initialize all component services and references
        Map<String, Component> components = new HashMap<String, Component>();
        for (Component component : composite.getComponents()) {

            // Index all components and check for duplicates
            if (components.containsKey(component.getName())) {
                problems.add(component);
            } else {
                components.put(component.getName(), component);
            }

            // Propagate the autowire flag from the composite to components
            if (composite.isAutowire()) {
                component.setAutowire(true);
            }

            // Index properties, services and references
            Map<String, Service> services = new HashMap<String, Service>();
            Map<String, Reference> references = new HashMap<String, Reference>();
            Map<String, Property> properties = new HashMap<String, Property>();

            // First check that the component has a resolved implementation
            Implementation implementation = component.getImplementation();
            if (implementation == null) {

                // A component must have an implementation
                problems.add(component);
            } else if (implementation.isUnresolved()) {

                // The implementation must be fully resolved
                problems.add(implementation);
            } else {

                // Index properties, services and references, also check for
                // duplicates
                for (Property property : implementation.getProperties()) {
                    if (properties.containsKey(property.getName())) {
                        problems.add(property);
                    } else {
                        properties.put(property.getName(), property);
                    }
                }
                for (Service service : implementation.getServices()) {
                    if (services.containsKey(service.getName())) {
                        problems.add(service);
                    } else {
                        services.put(service.getName(), service);
                    }
                }
                for (Reference reference : implementation.getReferences()) {
                    if (references.containsKey(reference.getName())) {
                        problems.add(reference);
                    } else {
                        references.put(reference.getName(), reference);
                    }
                }
            }

            // Index component services, references and properties
            // Also check for duplicates
            Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
            Map<String, ComponentProperty> componentProperties = new HashMap<String, ComponentProperty>();
            for (ComponentService componentService : component.getServices()) {
                if (componentServices.containsKey(componentService.getName())) {
                    problems.add(componentService);
                } else {
                    componentServices.put(componentService.getName(), componentService);
                }

                // Initialize binding names
                for (Binding binding : componentService.getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(componentService.getName());
                    }
                }
            }
            for (ComponentReference componentReference : component.getReferences()) {
                if (componentReferences.containsKey(componentReference.getName())) {
                    problems.add(componentReference);
                } else {
                    componentReferences.put(componentReference.getName(), componentReference);
                }

                // Initialize binding names
                for (Binding binding : componentReference.getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(componentReference.getName());
                    }
                }
            }
            for (ComponentProperty componentProperty : component.getProperties()) {
                if (componentProperties.containsKey(componentProperty.getName())) {
                    problems.add(componentProperty);
                } else {
                    componentProperties.put(componentProperty.getName(), componentProperty);
                }
            }

            // Reconcile component services/references/properties and
            // implementation
            // services/references and create component
            // services/references/properties
            // for the services/references declared by the implementation
            reconcileServices(component, services, componentServices, problems);
            reconcileReferences(component, references, componentReferences, problems);
            reconcileProperties(component, properties, componentProperties, problems);

            // Create self references to the component's services
            createSelfReferences(component);
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
                                   Map<String, ComponentReference> componentReferences,
                                   List<Base> problems) {

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
                SCABinding scaBinding = componentService.getBinding(SCABinding.class);
                if (scaBinding == null) {
                    scaBinding = assemblyFactory.createSCABinding();
                    scaBinding.setName(componentService.getName());
                    componentService.getBindings().add(scaBinding);
                }
                scaBinding.setURI(uri);
                scaBinding.setComponent(component);
            }
            for (ComponentReference componentReference : component.getReferences()) {
                String uri = component.getName() + '/' + componentReference.getName();
                componentReferences.put(uri, componentReference);

                // Create and configure an SCA binding for the reference
                SCABinding scaBinding = componentReference.getBinding(SCABinding.class);
                if (scaBinding == null) {
                    scaBinding = assemblyFactory.createSCABinding();
                    scaBinding.setName(componentReference.getName());
                    componentReference.getBindings().add(scaBinding);
                }
                scaBinding.setURI(uri);
                scaBinding.setComponent(component);
            }
        }

    }

    /**
     * Connect composite services to the component services that they promote.
     * 
     * @param composite
     * @param componentServices
     * @param problems
     */
    private void connectCompositeServices(Composite composite,
                                          Map<String, ComponentService> componentServices,
                                          List<Base> problems) {

        for (Service service : composite.getServices()) {

            CompositeService compositeService = (CompositeService)service;
            ComponentService componentService = compositeService.getPromotedService();
            if (componentService != null && componentService.isUnresolved()) {
                componentService = componentServices.get(componentService.getName());
                if (componentService != null) {

                    // Point to the resolved component service
                    compositeService.setPromotedService(componentService);
                    componentService.promotedAs().add(compositeService);

                    // Use the interface contract from the component service if
                    // none
                    // is specified on the composite service
                    if (compositeService.getInterfaceContract() == null) {
                        compositeService.setInterfaceContract(componentService.getInterfaceContract());
                    }

                } else {
                    problems.add(compositeService);
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
    private void connectCompositeReferences(Composite composite,
                                            Map<String, ComponentReference> componentReferences,
                                            List<Base> problems) {

        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences = compositeReference.getPromotedReferences();
            for (int i = 0, n = promotedReferences.size(); i < n; i++) {
                ComponentReference componentReference = promotedReferences.get(i);
                if (componentReference.isUnresolved()) {
                    componentReference = componentReferences.get(componentReference.getName());
                    if (componentReference != null) {

                        // Point to the resolved component reference
                        promotedReferences.set(i, componentReference);
                        componentReference.promotedAs().add(compositeReference);

                        // Use the interface contract from the component
                        // reference if none
                        // is specified on the composite reference
                        if (compositeReference.getInterfaceContract() == null) {
                            compositeReference.setInterfaceContract(componentReference.getInterfaceContract());
                        }

                    } else {
                        problems.add(compositeReference);
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
                                            Map<String, ComponentReference> componentReferences,
                                            List<Base> problems) {

        for (ComponentReference componentReference : componentReferences.values()) {
            List<ComponentService> targets = componentReference.getTargets();

            if (componentReference.isAutowire()) {

                // Find suitable targets in the current composite for an
                // autowired
                // reference
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
                                problems.add(target);
                            }
                        } else {
                            problems.add(target);
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
                                problems.add(target);
                            }
                        } else {
                            problems.add(target);
                        }
                    }
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
                              Map<String, ComponentReference> componentReferences,
                              List<Base> problems) {

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
                    problems.add(source);
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
                    problems.add(source);
                }
            } else {
                resolvedService = wire.getTarget();
            }

            // Add the target service to the list of targets of the
            // reference
            if (resolvedReference != null && resolvedService != null) {
                resolvedReference.getTargets().add(resolvedService);
            }
        }

        // Clear the list of wires
        composite.getWires().clear();
    }

    /**
     * Follow a service promotion chain down to the inner most (non composite)
     * component service.
     * 
     * @param topCompositeService
     * @return
     */
    private ComponentService getPromotedComponentService(CompositeService compositeService) {
        ComponentService componentService = compositeService.getPromotedService();
        if (componentService != null) {
            Service service = componentService.getService();
            if (componentService.getName() != null && service instanceof CompositeService) {

                // Continue to follow the service promotion chain
                return getPromotedComponentService((CompositeService)service);

            } else {

                // Found a non-composite service
                return componentService;
            }
        } else {

            // No promoted service
            return null;
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
     * Activate composite services in nested composites.
     * 
     * @param composite
     * @param problems
     */
    protected void activateCompositeServices(Composite composite, List<Base> problems) {

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // First process nested composites
                activateCompositeServices((Composite)implementation, problems);

                // Process the component services declared on components
                // in this composite
                for (ComponentService componentService : component.getServices()) {
                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        ComponentService promotedService = getPromotedComponentService(compositeService);
                        if (promotedService != null) {

                            // Add the component service to the innermost
                            // promoted component
                            SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                            if (scaBinding != null) {
                                Component promotedComponent = scaBinding.getComponent();
                                promotedComponent.getServices().add(componentService);
                            } else {
                                problems.add(promotedService);
                            }
                        }
                    }
                }
            }
        }

        // Process composite services declared in this composite
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;
            ComponentService promotedService = getPromotedComponentService(compositeService);
            if (promotedService != null) {

                // Create a new component service to represent this composite
                // service
                // on the promoted component
                SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                if (scaBinding != null) {
                    Component component = scaBinding.getComponent();
                    ComponentService newComponentService = assemblyFactory.createComponentService();
                    newComponentService.setName(null);
                    newComponentService.setService(compositeService);
                    component.getServices().add(newComponentService);
                    newComponentService.getBindings().add(scaBinding);
                    newComponentService.getBindings().addAll(compositeService.getBindings());
                    newComponentService.setInterfaceContract(compositeService.getInterfaceContract());
                    newComponentService.setCallback(compositeService.getCallback());

                    // Change the composite service to now promote the newly
                    // created
                    // component service directly
                    compositeService.setPromotedService(newComponentService);
                } else {
                    problems.add(promotedService);
                }
            }
        }
    }

    /**
     * Wire composite references in nested composites.
     * 
     * @param composite
     * @param problems
     */
    protected void wireCompositeReferences(Composite composite, List<Base> problems) {

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                wireCompositeReferences((Composite)implementation, problems);
            }
        }

        // Process composite references declared in this composite
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences = getPromotedComponentReferences(compositeReference);
            for (ComponentReference promotedReference : promotedReferences) {

                // Override the configuration of the promoted reference
                SCABinding scaBinding = promotedReference.getBinding(SCABinding.class);
                promotedReference.getBindings().clear();
                promotedReference.getBindings().add(scaBinding);
                promotedReference.getBindings().addAll(compositeReference.getBindings());
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
                        List<ComponentReference> promotedReferences = getPromotedComponentReferences(compositeReference);
                        for (ComponentReference promotedReference : promotedReferences) {

                            // Override the configuration of the promoted
                            // reference
                            SCABinding scaBinding = promotedReference.getBinding(SCABinding.class);
                            promotedReference.getBindings().clear();
                            promotedReference.getBindings().add(scaBinding);
                            promotedReference.getBindings().addAll(componentReference.getBindings());

                            // Wire the promoted reference to the actual
                            // non-composite
                            // component services
                            promotedReference.getTargets().clear();
                            for (ComponentService target : componentReference.getTargets()) {
                                if (target.getService() instanceof CompositeService) {

                                    // Wire to the actual component service
                                    // promoted by a
                                    // composite service
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
            }
        }
    }

    /**
     * Wire component references to component services and connect promoted
     * services/references to component services/references inside a composite.
     * 
     * @param composite
     * @param problems
     */
    protected void wireComposite(Composite composite, List<Base> problems) {

        // Wire nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                wireComposite((Composite)implementation, problems);
            }
        }

        // Index and bind all component services and references
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();

        // Create SCA bindings on all component services and references
        createSCABindings(composite, componentServices, componentReferences, problems);

        // Connect composite services and references to the component
        // services and references that they promote
        connectCompositeServices(composite, componentServices, problems);
        connectCompositeReferences(composite, componentReferences, problems);

        // Connect component references to their targets
        connectComponentReferences(composite, componentServices, componentReferences, problems);

        // Connect component references as described in wires
        connectWires(composite, componentServices, componentReferences, problems);

        resolveSourcedProperties(composite);

        // Validate that references are wired or promoted, according
        // to their multiplicity
        for (ComponentReference componentReference : componentReferences.values()) {
            if (!ReferenceUtil.validateMultiplicityAndTargets(componentReference.getMultiplicity(), componentReference
                .getTargets(), componentReference.promotedAs())) {
                problems.add(componentReference);
            }
        }
    }

    // TODO: Please review this code
    /**
     * @param composite
     */
    private void resolveSourcedProperties(Composite composite) {
        // Resolve properties
        Map<String, Property> compositeProperties = new HashMap<String, Property>();
        for (Property p : composite.getProperties()) {
            compositeProperties.put(p.getName(), p);
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
                resolveSourcedProperties((Composite)impl);
            }
        }
    }

    /**
     * Expand composite component implementations.
     * 
     * @param composite
     * @param problems
     */
    protected void expandComposites(Composite composite, List<Base> problems) {
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                Composite compositeImplementation = (Composite)implementation;
                Composite clone;
                try {
                    clone = (Composite)compositeImplementation.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                component.setImplementation(clone);
                expandComposites(clone, problems);
            }
        }
    }

    /**
     * For all the services, create a corresponding self-reference.
     * 
     * @param component
     */
    private void createSelfReferences(Component component) {
        for (ComponentService service : component.getServices()) {
            ComponentReference componentReference = assemblyFactory.createComponentReference();
            componentReference.setName("$self$." + service.getName());
            componentReference.getBindings().addAll(service.getBindings());
            ComponentService componentService = assemblyFactory.createComponentService();
            componentService.setName(component.getName() + "/" + service.getName());
            componentService.setUnresolved(true);
            componentReference.getTargets().add(componentService);
            componentReference.getPolicySets().addAll(service.getPolicySets());
            componentReference.getRequiredIntents().addAll(service.getRequiredIntents());
            componentReference.setInterfaceContract(service.getInterfaceContract());
            componentReference.setMultiplicity(Multiplicity.ONE_ONE);
            component.getReferences().add(componentReference);
        }
    }

}
