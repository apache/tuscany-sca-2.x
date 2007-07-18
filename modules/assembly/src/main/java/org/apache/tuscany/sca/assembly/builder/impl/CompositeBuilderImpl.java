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
import org.apache.tuscany.sca.assembly.WireableBinding;
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
import org.apache.tuscany.sca.assembly.builder.ComponentPreProcessor;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem;
import org.apache.tuscany.sca.assembly.builder.Problem.Severity;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;

/**
 * A builder that handles the configuration of the components inside a composite
 * and the wiring of component references to component services.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeBuilderImpl implements CompositeBuilder {

    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private InterfaceContractMapper interfaceContractMapper;
    private CompositeBuilderMonitor monitor;

    /**
     * Constructs a new composite util.
     * 
     * @param assemblyFactory
     * @param interfaceContractMapper
     */
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory,
                                InterfaceContractMapper interfaceContractMapper,
                                CompositeBuilderMonitor monitor) {
        this.assemblyFactory = assemblyFactory;
        this.scaBindingFactory = scaBindingFactory;
        this.interfaceContractMapper = interfaceContractMapper;

        if (monitor != null) {
            this.monitor = monitor;
        } else {
            // Create a default monitor that does nothing.
            this.monitor = new CompositeBuilderMonitor() {
                public void problem(Problem problem) {
                }
            };
        }
    }

    public void build(Composite composite) throws CompositeBuilderException {

        // Collect and fuse includes
        fuseIncludes(composite);

        // Expand nested composites
        expandComposites(composite);

        // Configure all components
        configureComponents(composite);

        // Wire the composite
        wireComposite(composite);

        // Activate composite services
        activateCompositeServices(composite);

        // Wire composite references
        wireCompositeReferences(composite);
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
    protected void fuseIncludes(Composite composite) {

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
                                   Map<String, ComponentService> componentServices) {

        // Connect each component service to the corresponding service
        for (ComponentService componentService : component.getServices()) {
            Service service = services.get(componentService.getName());
            if (service != null) {
                componentService.setService(service);
            } else {
                warning("Service not found for component service: " + component.getName()
                    + "/"
                    + componentService.getName(), component);
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
                            warning("Component service interface incompatible with service interface: " + component
                                .getName()
                                + "/"
                                + componentService.getName(), component);
                        }
                    }
                } else {
                    componentService.setInterfaceContract(service.getInterfaceContract());
                }

                // Reconcile bindings
                if (componentService.getBindings().isEmpty()) {
                    componentService.getBindings().addAll(service.getBindings());
                }

                // Reconcile callback bindings
                if (componentService.getCallback() == null) {
                    componentService.setCallback(service.getCallback());
                    if (componentService.getCallback() == null) {
                        // Create an empty callback to avoid null check
                        componentService.setCallback(assemblyFactory.createCallback());
                    }
                } else if (componentService.getCallback().getBindings().isEmpty() && service.getCallback() != null) {
                    componentService.getCallback().getBindings().addAll(service.getCallback().getBindings());
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
                                     Map<String, ComponentReference> componentReferences) {

        // Connect each component reference to the corresponding reference
        for (ComponentReference componentReference : component.getReferences()) {
            Reference reference = references.get(componentReference.getName());
            if (reference != null) {
                componentReference.setReference(reference);
            } else {
                if (!componentReference.getName().startsWith("$self$.")) {
                    warning("Reference not found for component reference: " + component.getName()
                        + "/"
                        + componentReference.getName(), component);
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
                        warning("Component reference multiplicity incompatible with reference multiplicity: " + component
                                    .getName()
                                    + "/"
                                    + componentReference.getName(),
                                component);
                    }
                } else {
                    componentReference.setMultiplicity(reference.getMultiplicity());
                }

                // Reconcile interface
                if (componentReference.getInterfaceContract() != null) {
                    if (!componentReference.getInterfaceContract().equals(reference.getInterfaceContract())) {
                        if (!interfaceContractMapper.isCompatible(reference.getInterfaceContract(), componentReference
                            .getInterfaceContract())) {
                            warning("Component reference interface incompatible with reference interface: " + component
                                .getName()
                                + "/"
                                + componentReference.getName(), component);
                        }
                    }
                } else {
                    componentReference.setInterfaceContract(reference.getInterfaceContract());
                }

                // Reconcile bindings
                if (componentReference.getBindings().isEmpty()) {
                    componentReference.getBindings().addAll(reference.getBindings());
                }

                // Reconcile callback bindings
                if (componentReference.getCallback() == null) {
                    componentReference.setCallback(reference.getCallback());
                    if (componentReference.getCallback() == null) {
                        // Create an empty callback to avoid null check
                        componentReference.setCallback(assemblyFactory.createCallback());
                    }

                } else if (componentReference.getCallback().getBindings().isEmpty() && reference.getCallback() != null) {
                    componentReference.getCallback().getBindings().addAll(reference.getCallback().getBindings());
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
                                     Map<String, ComponentProperty> componentProperties) {

        // Connect component properties to their properties
        for (ComponentProperty componentProperty : component.getProperties()) {
            Property property = properties.get(componentProperty.getName());
            if (property != null) {
                componentProperty.setProperty(property);
            } else {
                warning("Property not found for component property: " + component.getName()
                    + "/"
                    + componentProperty.getName(), component);
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
                    warning("Component property mustSupply attribute incompatible with property: " + component
                        .getName()
                        + "/"
                        + componentProperty.getName(), component);
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
                    warning("No value configured on a mustSupply property: " + component.getName()
                        + "/"
                        + componentProperty.getName(), component);
                }

                // Check that a a component property does not override the
                // many attribute
                if (!property.isMany() && componentProperty.isMany()) {
                    warning("Component property many attribute incompatible with property: " + component.getName()
                        + "/"
                        + componentProperty.getName(), component);
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
                    warning("No type specified on component property: " + component.getName()
                        + "/"
                        + componentProperty.getName(), component);
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
    protected void configureComponents(Composite composite) {
        configureComponents(composite, null);
    }

    private void indexImplementationPropertiesServicesAndReferences(Component component,
                                                                    Map<String, Service> services,
                                                                    Map<String, Reference> references,
                                                                    Map<String, Property> properties) {
        // First check that the component has a resolved implementation
        Implementation implementation = component.getImplementation();
        if (implementation == null) {

            // A component must have an implementation
            warning("No implementation for component: " + component.getName(), component);

        } else if (implementation.isUnresolved()) {

            // The implementation must be fully resolved
            warning("Component implementation not found: " + component.getName() + " : " + implementation.getURI(),
                    component);

        } else {

            // Index properties, services and references, also check for
            // duplicates
            for (Property property : implementation.getProperties()) {
                if (properties.containsKey(property.getName())) {
                    warning("Duplicate property name: " + component.getName() + "/" + property.getName(), component);
                } else {
                    properties.put(property.getName(), property);
                }
            }
            for (Service service : implementation.getServices()) {
                if (services.containsKey(service.getName())) {
                    warning("Duplicate service name: " + component.getName() + "/" + service.getName(), component);
                } else {
                    services.put(service.getName(), service);
                }
            }
            for (Reference reference : implementation.getReferences()) {
                if (references.containsKey(reference.getName())) {
                    warning("Duplicate reference name: " + component.getName() + "/" + reference.getName(), component);
                } else {
                    references.put(reference.getName(), reference);
                }
            }
        }

    }

    private void indexComponentPropertiesServicesAndReferences(Component component,
                                                               Map<String, ComponentService> componentServices,
                                                               Map<String, ComponentReference> componentReferences,
                                                               Map<String, ComponentProperty> componentProperties) {
        for (ComponentService componentService : component.getServices()) {
            if (componentServices.containsKey(componentService.getName())) {
                warning("Duplicate component service name: " + component.getName() + "/" + componentService.getName(),
                        component);
            } else {
                componentServices.put(componentService.getName(), componentService);
            }

            // Initialize binding names
            for (Binding binding : componentService.getBindings()) {
                if (binding.getName() == null) {
                    binding.setName(componentService.getName());
                }
            }
            if (componentService.getCallback() != null) {
                for (Binding binding : componentService.getCallback().getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(componentService.getName());
                    }
                }
            }
        }
        for (ComponentReference componentReference : component.getReferences()) {
            if (componentReferences.containsKey(componentReference.getName())) {
                warning("Duplicate component reference name: " + component.getName()
                    + "/"
                    + componentReference.getName(), component);
            } else {
                componentReferences.put(componentReference.getName(), componentReference);
            }

            // Initialize binding names
            for (Binding binding : componentReference.getBindings()) {
                if (binding.getName() == null) {
                    binding.setName(componentReference.getName());
                }
            }
            if (componentReference.getCallback() != null) {
                for (Binding binding : componentReference.getCallback().getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(componentReference.getName());
                    }
                }
            }
        }
        for (ComponentProperty componentProperty : component.getProperties()) {
            if (componentProperties.containsKey(componentProperty.getName())) {
                warning("Duplicate component property name: " + component.getName() + "/" + componentProperty.getName(),
                        component);
            } else {
                componentProperties.put(componentProperty.getName(), componentProperty);
            }
        }

    }

    /**
     * Configure components in the composite.
     * 
     * @param composite
     * @param uri
     * @param problems
     */
    private void configureComponents(Composite composite, String uri) {

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
                configureComponents((Composite)implementation, componentURI);
            }
        }

        // Set default binding names
        for (Service service : composite.getServices()) {
            for (Binding binding : service.getBindings()) {
                if (binding.getName() == null) {
                    binding.setName(service.getName());
                }
            }
            if (service.getCallback() != null) {
                for (Binding binding : service.getCallback().getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(service.getName());
                    }
                }
            }
        }
        for (Reference reference : composite.getReferences()) {
            for (Binding binding : reference.getBindings()) {
                if (binding.getName() == null) {
                    binding.setName(reference.getName());
                }
            }
            if (reference.getCallback() != null) {
                for (Binding binding : reference.getCallback().getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(reference.getName());
                    }
                }
            }
        }

        // Initialize all component services and references
        Map<String, Component> components = new HashMap<String, Component>();
        for (Component component : composite.getComponents()) {

            // Index all components and check for duplicates
            if (components.containsKey(component.getName())) {
                warning("Duplicate component name: " + composite.getName() + " : " + component.getName(), composite);
            } else {
                components.put(component.getName(), component);
            }

            // Propagate the autowire flag from the composite to components
            if (composite.isAutowire()) {
                component.setAutowire(true);
            }

            if (component.getImplementation() instanceof ComponentPreProcessor) {
                ((ComponentPreProcessor)component.getImplementation()).preProcess(component);
            }

            Map<String, Service> services = new HashMap<String, Service>();
            Map<String, Reference> references = new HashMap<String, Reference>();
            Map<String, Property> properties = new HashMap<String, Property>();
            //Index properties, services and references
            indexImplementationPropertiesServicesAndReferences(component, services, references, properties);

            Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
            Map<String, ComponentProperty> componentProperties = new HashMap<String, ComponentProperty>();
            //Index component services, references and properties
            // Also check for duplicates
            indexComponentPropertiesServicesAndReferences(component,
                                                          componentServices,
                                                          componentReferences,
                                                          componentProperties);

            // Reconcile component services/references/properties and
            // implementation
            // services/references and create component
            // services/references/properties
            // for the services/references declared by the implementation
            reconcileServices(component, services, componentServices);
            reconcileReferences(component, references, componentReferences);
            reconcileProperties(component, properties, componentProperties);

            // Create self references to the component's services
            if (!(component.getImplementation() instanceof Composite)) {
                createSelfReferences(component);
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
                Binding selected = resolveBindings(componentReference, target);
                if (selected == null) {
                    warning("Component reference doesn't have a matching binding", componentReference);
                } else {
                    selectedBindings.add(selected);
                }
                if (bidirectional) {
                    Binding selectedCallback = resolveCallbackBindings(componentReference, target);
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
    protected void activateCompositeServices(Composite composite) {

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // First process nested composites
                activateCompositeServices((Composite)implementation);

                // Process the component services declared on components
                // in this composite
                for (ComponentService componentService : component.getServices()) {
                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {

                        // Get the inner most promoted service
                        ComponentService promotedService = getPromotedComponentService(compositeService);
                        if (promotedService != null) {

                            // Default to use the interface from the promoted service
                            if (compositeService.getInterfaceContract() == null) {
                                compositeService.setInterfaceContract(promotedService.getInterfaceContract());
                            }
                            if (componentService.getInterfaceContract() == null) {
                                componentService.setInterfaceContract(promotedService.getInterfaceContract());
                            }

                            // Create a new component service to represent this composite
                            // service on the promoted component
                            SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                            if (scaBinding != null) {
                                Component promotedComponent = scaBinding.getComponent();
                                ComponentService newComponentService = assemblyFactory.createComponentService();
                                newComponentService.setName("$promoted$." + compositeService.getName());
                                //newComponentService.setService(compositeService);
                                promotedComponent.getServices().add(newComponentService);
                                newComponentService.getBindings().add(scaBinding);
                                newComponentService.getBindings().addAll(compositeService.getBindings());
                                newComponentService.setInterfaceContract(compositeService.getInterfaceContract());
                                if (compositeService.getInterfaceContract() != null && // can be null in unit tests
                                compositeService.getInterfaceContract().getCallbackInterface() != null) {
                                    SCABinding scaCallbackBinding =
                                        promotedService.getCallbackBinding(SCABinding.class);
                                    newComponentService.setCallback(assemblyFactory.createCallback());
                                    if (scaCallbackBinding != null) {
                                        newComponentService.getCallback().getBindings().add(scaCallbackBinding);
                                    }
                                    if (compositeService.getCallback() != null) {
                                        newComponentService.getCallback().getBindings().addAll(compositeService
                                            .getCallback().getBindings());
                                    }
                                }

                                // FIXME: [rfeng] Set the service to promoted
                                newComponentService.setService(promotedService.getService());

                                // Change the composite service to now promote the newly
                                // created component service directly
                                compositeService.setPromotedService(newComponentService);

                            } else {
                                warning("Promoted component service not found: " + promotedService.getName(), composite);
                            }
                        }
                    }
                }
            }
        }

        // Process composite services declared in this composite
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;

            // Get the inner most promoted service
            ComponentService promotedService = getPromotedComponentService(compositeService);
            if (promotedService != null) {

                // Default to use the interface from the promoted service
                if (compositeService.getInterfaceContract() == null && promotedService.getInterfaceContract() != null) {
                    compositeService.setInterfaceContract(promotedService.getInterfaceContract());
                }

                // Create a new component service to represent this composite
                // service on the promoted component
                SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                if (scaBinding != null) {
                    Component promotedComponent = scaBinding.getComponent();
                    ComponentService newComponentService = assemblyFactory.createComponentService();
                    newComponentService.setName("$promoted$." + compositeService.getName());
                    //newComponentService.setService(compositeService);
                    promotedComponent.getServices().add(newComponentService);
                    newComponentService.getBindings().add(scaBinding);
                    newComponentService.getBindings().addAll(compositeService.getBindings());
                    newComponentService.setInterfaceContract(compositeService.getInterfaceContract());
                    if (compositeService.getInterfaceContract() != null && // can be null in unit tests
                    compositeService.getInterfaceContract().getCallbackInterface() != null) {
                        SCABinding scaCallbackBinding = promotedService.getCallbackBinding(SCABinding.class);
                        newComponentService.setCallback(assemblyFactory.createCallback());
                        if (scaCallbackBinding != null) {
                            newComponentService.getCallback().getBindings().add(scaCallbackBinding);
                        }
                        if (compositeService.getCallback() != null) {
                            newComponentService.getCallback().getBindings().addAll(compositeService.getCallback()
                                .getBindings());
                        }
                    }

                    // FIXME: [rfeng] Set the service to promoted
                    newComponentService.setService(promotedService.getService());

                    // Change the composite service to now promote the newly
                    // created component service directly
                    compositeService.setPromotedService(newComponentService);
                } else {
                    warning("Promoted component service not found: " + promotedService.getName(), composite);
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
    protected void wireCompositeReferences(Composite composite) {

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

    /**
     * Wire component references to component services and connect promoted
     * services/references to component services/references inside a composite.
     * 
     * @param composite
     * @param problems
     */
    protected void wireComposite(Composite composite) {

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

    /**
     * Expand composite component implementations.
     * 
     * @param composite
     * @param problems
     */
    protected void expandComposites(Composite composite) {
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
                expandComposites(clone);
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
            componentReference.setCallback(service.getCallback());
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

    /**
     * Choose a binding for the reference based on the bindings available on the
     * service
     * 
     * @param reference The component reference
     * @param service The component service
     * @return Resolved binding
     */
    protected Binding resolveBindings(ComponentReference reference, ComponentService service) {
        List<Binding> source = reference.getBindings();
        List<Binding> target = service.getBindings();

        return matchBinding(service, source, target);

    }

    private Binding matchBinding(ComponentService service, List<Binding> source, List<Binding> target) {
        List<Binding> matched = new ArrayList<Binding>();
        // Find the corresponding bindings from the service side
        for (Binding binding : source) {
            for (Binding serviceBinding : target) {
                if (binding.getClass() == serviceBinding.getClass()) {
                    Binding cloned = binding;
                    // TODO: We need to clone the reference binding
                    try {
                        cloned = (Binding)((WireableBinding)binding).clone();
                        WireableBinding endpoint = ((WireableBinding)cloned);
                        // FIXME: This is a hack to get the target component
                        SCABinding scaBinding = service.getBinding(SCABinding.class);
                        if (scaBinding != null) {
                            endpoint.setTargetComponent(scaBinding.getComponent());
                        }
                        endpoint.setTargetComponentService(service);
                        endpoint.setTargetBinding(serviceBinding);
                        cloned.setURI(serviceBinding.getURI());
                    } catch (Exception e) {
                        // warning("The binding doesn't support clone: " + binding.getClass().getSimpleName(), binding);
                    }
                    matched.add(cloned);
                    break;
                }
            }
        }
        if (matched.isEmpty()) {
            // No matching binding
            return null;
        } else {
            for (Binding binding : matched) {
                // If binding.sca is present, 
                if (SCABinding.class.isInstance(binding)) {
                    return binding;
                }
            }
            // Use the first one
            return matched.get(0);
        }
    }

    /**
     * @param reference
     * @param service
     * @return
     */
    protected Binding resolveCallbackBindings(ComponentReference reference, ComponentService service) {
        List<Binding> source = reference.getCallback().getBindings();
        List<Binding> target = service.getCallback().getBindings();

        return matchBinding(service, source, target);
    }

    /**
     * Report an error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    @SuppressWarnings("unused")
    private void error(String message, Object model) {
        monitor.problem(new ProblemImpl(Severity.ERROR, message, model));
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

}
