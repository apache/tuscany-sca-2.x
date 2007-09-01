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
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.ComponentPreProcessor;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem.Severity;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.QualifiedIntent;

public class CompositeConfigurationBuilderImpl {

    private String CALLBACK_PREFIX = "$callback$.";

    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private CompositeBuilderMonitor monitor;
    private InterfaceContractMapper interfaceContractMapper;
    

    public CompositeConfigurationBuilderImpl(AssemblyFactory assemblyFactory,
                                             SCABindingFactory scaBindingFactory,
                                             InterfaceContractMapper interfaceContractMapper,
                                             CompositeBuilderMonitor monitor) {
        this.assemblyFactory = assemblyFactory;
        this.scaBindingFactory = scaBindingFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.monitor = monitor;
    }

    /**
     * Configure components in the composite.
     * 
     * @param composite
     * @param problems
     */
    public void configureComponents(Composite composite) {
        configureComponents(composite, null);
    }

    /**
     * Configure components in the composite.
     * 
     * @param composite
     * @param uri
     * @param problems
     */
    private void configureComponents(Composite composite, String uri) {
        URI parentURI;
        if (uri != null) {
            parentURI = URI.create(uri + '/');
        } else {
            parentURI = null;
        }

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {

            // Initialize component URI
            String componentURI;
            if (parentURI == null) {
                componentURI = component.getName();
            } else {
                componentURI = parentURI.resolve(component.getName()).toString();
            }
            component.setURI(componentURI);

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // Process nested composite
                configureComponents((Composite)implementation, componentURI);
            }
        }

        List<Intent> compositeIntents = null;
        List<PolicySet> compositePolicySets = null;
        if (composite instanceof PolicySetAttachPoint) {
            compositeIntents = ((PolicySetAttachPoint)composite).getRequiredIntents();
            compositePolicySets = ((PolicySetAttachPoint)composite).getPolicySets();
        }

        // Initialize service bindings
        for (Service service : composite.getServices()) {
            // Set default binding names and inherit intents and policysets
            // defined at composite level
            addInheritedIntents(compositeIntents, service.getRequiredIntents());
            addInheritedPolicySets(compositePolicySets, service.getPolicySets(), false);
            
            //inherit intents and policies from promoted service
            if ( service instanceof CompositeService ) {
                CompositeService compService = (CompositeService)service;
                addInheritedIntents(compService.getPromotedService().getRequiredIntents(), 
                                     service.getRequiredIntents());
                addInheritedPolicySets(compService.getPromotedService().getPolicySets(), 
                                     service.getPolicySets(), true);
            }
            

            // Create default SCA binding
            if (service.getBindings().isEmpty()) {
                SCABinding scaBinding = scaBindingFactory.createSCABinding();
                service.getBindings().add(scaBinding);
            }

            // Initialize binding names and URIs
            for (Binding binding : service.getBindings()) {
                
                // Binding name defaults to the service name
                if (binding.getName() == null) {
                    binding.setName(service.getName());
                }
                
                String bindingURI;
                if (binding.getURI() == null) {
                    
                    // Binding URI defaults to the binding name
                    bindingURI = String.valueOf(binding.getName());
                } else {
                    bindingURI = binding.getURI();
                }
                
                // Combine with the parent URI
                if (parentURI != null) {
                    bindingURI = parentURI.resolve(bindingURI).toString();
                }
                binding.setURI(bindingURI);
            }
            
            if (service.getCallback() != null) {
                addInheritedIntents(service.getRequiredIntents(), 
                                    service.getCallback().getRequiredIntents());
                addInheritedPolicySets(service.getPolicySets(), 
                                       service.getCallback().getPolicySets(), 
                                       false);
                for (Binding binding : service.getCallback().getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(service.getName());
                    }
                }
            }
            computeIntents(service.getBindings(), service.getRequiredIntents());
            computePolicySets(service, service.getBindings(), service.getPolicySets());
            
            if ( service.getCallback() != null ) {
                computeIntents(service.getCallback().getBindings(), 
                               service.getCallback().getRequiredIntents());
                computePolicySets(service, 
                                  service.getCallback().getBindings(), 
                                  service.getCallback().getPolicySets());
            }
        }

        // Initialize reference bindings
        for (Reference reference : composite.getReferences()) {
            addInheritedIntents(compositeIntents, reference.getRequiredIntents());
            addInheritedPolicySets(compositePolicySets, reference.getPolicySets(), false);

            //inherit intents and policies from promoted service
            if ( reference instanceof CompositeReference ) {
                CompositeReference compReference = (CompositeReference)reference;
                for ( Reference promotedReference : compReference.getPromotedReferences() ) {
                    addInheritedIntents(promotedReference.getRequiredIntents(), 
                                         reference.getRequiredIntents());
                
                    addInheritedPolicySets(promotedReference.getPolicySets(), 
                                         reference.getPolicySets(), true);
                }
            }
            
            // Create default SCA binding
            if (reference.getBindings().isEmpty()) {
                SCABinding scaBinding = scaBindingFactory.createSCABinding();
                reference.getBindings().add(scaBinding);
            }

            // Set binding names
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
            
            computeIntents(reference.getBindings(), reference.getRequiredIntents());
            computePolicySets(reference, reference.getBindings(), reference.getPolicySets());
            
            if ( reference.getCallback() != null ) {
                computeIntents(reference.getCallback().getBindings(), 
                               reference.getCallback().getRequiredIntents());
                computePolicySets(reference, 
                                  reference.getCallback().getBindings(), 
                                  reference.getCallback().getPolicySets());
            }
        }

        // Initialize all component services and references
        Map<String, Component> components = new HashMap<String, Component>();
        for (Component component : composite.getComponents()) {
            URI componentURI = URI.create(component.getURI() + '/');

            // Index all components and check for duplicates
            if (components.containsKey(component.getName())) {
                warning("Duplicate component name: " + composite.getName()
                    + " : "
                    + component.getName(), composite);
            } else {
                components.put(component.getName(), component);
            }

            // Propagate the autowire flag from the composite to components
            if (component.getAutowire() == null) {
                component.setAutowire(composite.getAutowire());
            }

            // Inherit intents defined at the composite level
            addInheritedIntents(compositeIntents, component.getRequiredIntents());
            
            // Inherit policysets defined at the composite level
            addInheritedPolicySets(compositePolicySets, component.getPolicySets(), false);

            if (component.getImplementation() instanceof ComponentPreProcessor) {
                ((ComponentPreProcessor)component.getImplementation()).preProcess(component);
            }

            // Index properties, services and references
            Map<String, Service> services = new HashMap<String, Service>();
            Map<String, Reference> references = new HashMap<String, Reference>();
            Map<String, Property> properties = new HashMap<String, Property>();
            indexImplementationPropertiesServicesAndReferences(component,
                                                               services,
                                                               references,
                                                               properties);

            // Index component services, references and properties
            // Also check for duplicates
            Map<String, ComponentService> componentServices =
                new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences =
                new HashMap<String, ComponentReference>();
            Map<String, ComponentProperty> componentProperties =
                new HashMap<String, ComponentProperty>();
            indexComponentPropertiesServicesAndReferences(component,
                                                          componentServices,
                                                          componentReferences,
                                                          componentProperties);

            // Reconcile component services/references/properties and
            // implementation services/references and create component
            // services/references/properties for the services/references
            // declared by the implementation
            reconcileServices(component, services, componentServices);
            reconcileReferences(component, references, componentReferences);
            reconcileProperties(component, properties, componentProperties);

            // Configure or create callback services for component's references
            // with callbacks
            configureCallbackServices(component, componentServices);

            // Configure or create callback references for component's services
            // with callbacks
            configureCallbackReferences(component, componentReferences);

            // Create self references to the component's services
//            if (!(component.getImplementation() instanceof Composite)) {
//                createSelfReferences(component);
//            }

            // Initialize service bindings
            for (ComponentService componentService : component.getServices()) {

                // Create default SCA binding
                if (componentService.getBindings().isEmpty()) {
                    SCABinding scaBinding = scaBindingFactory.createSCABinding();
                    componentService.getBindings().add(scaBinding);
                }

                // Set binding names and URIs
                for (Binding binding : componentService.getBindings()) {
                    
                    // Binding name defaults to the service name
                    if (binding.getName() == null) {
                        binding.setName(componentService.getName());
                    }

                    String bindingURI;
                    if (binding.getURI() == null) {
                        if (componentServices.size() > 1) {
                            // Binding URI defaults component URI / binding name
                            bindingURI = String.valueOf(binding.getName());
                            bindingURI = componentURI.resolve(bindingURI).toString();
                        } else {
                            // If there's only one service then binding URI defaults
                            // to the component URI
                            bindingURI = componentURI.toString();
                        }
                    } else {
                        // Combine the specified binding URI with the component URI
                        bindingURI = binding.getURI();
                        bindingURI = componentURI.resolve(bindingURI).toString();
                    }
                    
                    binding.setURI(bindingURI);
                }
                if (componentService.getCallback() != null) {
                    for (Binding binding : componentService.getCallback().getBindings()) {
                        if (binding.getName() == null) {
                            binding.setName(componentService.getName());
                        }
                    }
                }
            }

            // Initialize reference bindings
            for (ComponentReference componentReference : component.getReferences()) {

                // Create default SCA binding
                if (componentReference.getBindings().isEmpty()) {
                    SCABinding scaBinding = scaBindingFactory.createSCABinding();
                    componentReference.getBindings().add(scaBinding);
                }

                // Set binding names
                for (Binding binding : componentReference.getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(componentReference.getName());
                    }
                }
                if (componentReference.getCallback() != null) {
                    for (Binding binding : componentReference.getCallback().getBindings()) {
                        if (binding.getName() == null) {
                            binding.setName(CALLBACK_PREFIX + componentReference.getName());
                        }
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
                                + componentProperty.getName(),
                            component);
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
                    warning("Component property many attribute incompatible with property: " + component
                                .getName()
                                + "/"
                                + componentProperty.getName(),
                            component);
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
                    ComponentReference componentReference =
                        assemblyFactory.createComponentReference();
                    componentReference.setIsCallback(reference.isCallback());
                    componentReference.setName(reference.getName());
                    componentReference.setReference(reference);
                    component.getReferences().add(componentReference);
                }
            }
        }

        // Reconcile each component reference with its reference
        for (ComponentReference componentReference : component.getReferences()) {
            // inherit intents and policysets from component
            addInheritedIntents(component.getRequiredIntents(), componentReference.getRequiredIntents());
            addInheritedPolicySets(component.getPolicySets(), componentReference.getPolicySets(), false);

            Reference reference = componentReference.getReference();
            if (reference != null) {
                // reconcile intents and policysets
                addInheritedIntents(reference.getRequiredIntents(), componentReference.getRequiredIntents());
                addInheritedPolicySets(reference.getPolicySets(), componentReference.getPolicySets(), true);

                // Reconcile multiplicity
                if (componentReference.getMultiplicity() != null) {
                    if (!ReferenceUtil.isValidMultiplicityOverride(reference.getMultiplicity(),
                                                                   componentReference
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
                    if (!componentReference.getInterfaceContract().equals(reference
                        .getInterfaceContract())) {
                        if (!interfaceContractMapper.isCompatible(reference.getInterfaceContract(),
                                                                  componentReference
                                                                      .getInterfaceContract())) {
                            warning("Component reference interface incompatible with reference interface: " + component
                                        .getName()
                                        + "/"
                                        + componentReference.getName(),
                                    component);
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

                } else if (componentReference.getCallback().getBindings().isEmpty() && reference
                    .getCallback() != null) {
                    componentReference.getCallback().getBindings().addAll(reference.getCallback()
                        .getBindings());
                }
                
                // Propagate autowire setting from the component
                if (componentReference.getAutowire() == null) {
                    componentReference.setAutowire(component.getAutowire());
                }

                // Reconcile targets
                if (componentReference.getTargets().isEmpty()) {
                    componentReference.getTargets().addAll(reference.getTargets());
                }
            }
            
            //compute intents and policyset for each binding
            computeIntents(componentReference.getBindings(), 
                           componentReference.getRequiredIntents());
            computePolicySets(componentReference,
                              componentReference.getBindings(),
                              componentReference.getPolicySets());
            
            if ( componentReference.getCallback() != null ) {
                //compute intents and policyset for each callback binding
                computeIntents(componentReference.getCallback().getBindings(), 
                               componentReference.getRequiredIntents());
                computePolicySets(componentReference,
                                  componentReference.getCallback().getBindings(),
                                  componentReference.getPolicySets());
                

            }
            
        }
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
                    componentService.setIsCallback(service.isCallback());
                    String name = service.getName();
                    componentService.setName(name);
                    componentService.setService(service);
                    component.getServices().add(componentService);
                    componentServices.put(name, componentService);
                }
            }
        }

        //Reconcile each component service with its service
        for (ComponentService componentService : component.getServices()) {
            // inherit intents and policysets from component
            addInheritedIntents(component.getRequiredIntents(), componentService.getRequiredIntents());
            addInheritedPolicySets(component.getPolicySets(), componentService.getPolicySets(), false);

            Service service = componentService.getService();
            if (service != null) {
                // reconcile intents and policysets
                addInheritedIntents(service.getRequiredIntents(), componentService.getRequiredIntents());
                addInheritedPolicySets(service.getPolicySets(), 
                                       componentService.getPolicySets(), 
                                       true);
                // Reconcile interface
                if (componentService.getInterfaceContract() != null) {
                    if (!componentService.getInterfaceContract().equals(service
                        .getInterfaceContract())) {
                        if (!interfaceContractMapper.isCompatible(componentService
                            .getInterfaceContract(), service.getInterfaceContract())) {
                            warning("Component service interface incompatible with service interface: " + component
                                        .getName()
                                        + "/"
                                        + componentService.getName(),
                                    component);
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
                } else if (componentService.getCallback().getBindings().isEmpty() && service
                    .getCallback() != null) {
                    componentService.getCallback().getBindings().addAll(service.getCallback()
                        .getBindings());
                }
            }
            //compute intents and policyset for each binding
            computeIntents(componentService.getBindings(), 
                           componentService.getRequiredIntents());
            computePolicySets(componentService,
                              componentService.getBindings(),
                              componentService.getPolicySets());
            
            if ( componentService.getCallback() != null ) {
                //compute intents and policyset for each binding
                computeIntents(componentService.getCallback().getBindings(), 
                               componentService.getRequiredIntents());
                computePolicySets(componentService,
                                  componentService.getCallback().getBindings(),
                                  componentService.getPolicySets());
            }
        }
    }

    private void indexComponentPropertiesServicesAndReferences(Component component,
                                                               Map<String, ComponentService> componentServices,
                                                               Map<String, ComponentReference> componentReferences,
                                                               Map<String, ComponentProperty> componentProperties) {
        for (ComponentService componentService : component.getServices()) {
            if (componentServices.containsKey(componentService.getName())) {
                warning("Duplicate component service name: " + component.getName()
                    + "/"
                    + componentService.getName(), component);
            } else {
                componentServices.put(componentService.getName(), componentService);
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
        }
        for (ComponentProperty componentProperty : component.getProperties()) {
            if (componentProperties.containsKey(componentProperty.getName())) {
                warning("Duplicate component property name: " + component.getName()
                    + "/"
                    + componentProperty.getName(), component);
            } else {
                componentProperties.put(componentProperty.getName(), componentProperty);
            }
        }

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
            warning("Component implementation not found: " + component.getName()
                + " : "
                + implementation.getURI(), component);

        } else {

            // Index properties, services and references, also check for
            // duplicates
            for (Property property : implementation.getProperties()) {
                if (properties.containsKey(property.getName())) {
                    warning("Duplicate property name: " + component.getName()
                        + "/"
                        + property.getName(), component);
                } else {
                    properties.put(property.getName(), property);
                }
            }
            for (Service service : implementation.getServices()) {
                if (services.containsKey(service.getName())) {
                    warning("Duplicate service name: " + component.getName()
                        + "/"
                        + service.getName(), component);
                } else {
                    services.put(service.getName(), service);
                }
            }
            for (Reference reference : implementation.getReferences()) {
                if (references.containsKey(reference.getName())) {
                    warning("Duplicate reference name: " + component.getName()
                        + "/"
                        + reference.getName(), component);
                } else {
                    references.put(reference.getName(), reference);
                }
            }
        }

    }

    /**
     * For all the references with callbacks, create a corresponding callback
     * service.
     * 
     * @param component
     */
    private void configureCallbackServices(Component component,
                                           Map<String, ComponentService> componentServices) {
        for (ComponentReference reference : component.getReferences()) {
            if (reference.getInterfaceContract() != null && // can be null in
                                                            // unit tests
            reference.getInterfaceContract().getCallbackInterface() != null) {
                ComponentService service =
                    componentServices.get(CALLBACK_PREFIX + reference.getName());
                if (service == null) {
                    service = createCallbackService(component, reference);
                }
                if (reference.getCallback() != null) {
                    if (service.getBindings().isEmpty()) {
                        service.getBindings().addAll(reference.getCallback().getBindings());
                    }
                    if (service.getPolicySets().isEmpty()) {
                        service.getPolicySets().addAll(reference.getCallback().getPolicySets());
                    }
                    if (service.getRequiredIntents().isEmpty()) {
                        service.getRequiredIntents().addAll(reference.getCallback()
                            .getRequiredIntents());
                    }
                }
                reference.setCallbackService(service);
            }
        }
    }

    /**
     * Create a callback service for a component reference
     * 
     * @param component
     * @param reference
     */
    private ComponentService createCallbackService(Component component, ComponentReference reference) {
        ComponentService componentService = assemblyFactory.createComponentService();
        componentService.setIsCallback(true);
        componentService.setName(CALLBACK_PREFIX + reference.getName());
        try {
            InterfaceContract contract =
                (InterfaceContract)reference.getInterfaceContract().clone();
            contract.setInterface(contract.getCallbackInterface());
            contract.setCallbackInterface(null);
            componentService.setInterfaceContract(contract);
        } catch (CloneNotSupportedException e) {
            // will not happen
        }
        Reference implReference = reference.getReference();
        if (implReference != null) {
            Service implService = assemblyFactory.createService();
            implService.setName(CALLBACK_PREFIX + implReference.getName());
            try {
                InterfaceContract implContract =
                    (InterfaceContract)implReference.getInterfaceContract().clone();
                implContract.setInterface(implContract.getCallbackInterface());
                implContract.setCallbackInterface(null);
                implService.setInterfaceContract(implContract);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            componentService.setService(implService);
        }
        component.getServices().add(componentService);
        return componentService;
    }

    /**
     * For all the services with callbacks, create a corresponding callback
     * reference.
     * 
     * @param component
     */
    private void configureCallbackReferences(Component component,
                                             Map<String, ComponentReference> componentReferences) {
        for (ComponentService service : component.getServices()) {
            if (service.getInterfaceContract() != null && // can be null in
                                                            // unit tests
            service.getInterfaceContract().getCallbackInterface() != null) {
                ComponentReference reference =
                    componentReferences.get(CALLBACK_PREFIX + service.getName());
                if (reference == null) {
                    reference = createCallbackReference(component, service);
                }
                if (service.getCallback() != null) {
                    if (reference.getBindings().isEmpty()) {
                        reference.getBindings().addAll(service.getCallback().getBindings());
                    }
                    if (reference.getPolicySets().isEmpty()) {
                        reference.getPolicySets().addAll(service.getCallback().getPolicySets());
                    }
                    if (reference.getRequiredIntents().isEmpty()) {
                        reference.getRequiredIntents().addAll(service.getCallback()
                            .getRequiredIntents());
                    }
                }
                service.setCallbackReference(reference);
            }
        }
    }

    /**
     * Create a callback reference for a component service
     * 
     * @param component
     * @param service
     */
    private ComponentReference createCallbackReference(Component component, ComponentService service) {
        ComponentReference componentReference = assemblyFactory.createComponentReference();
        componentReference.setIsCallback(true);
        componentReference.setName(CALLBACK_PREFIX + service.getName());
        try {
            InterfaceContract contract = (InterfaceContract)service.getInterfaceContract().clone();
            contract.setInterface(contract.getCallbackInterface());
            contract.setCallbackInterface(null);
            componentReference.setInterfaceContract(contract);
        } catch (CloneNotSupportedException e) {
            // will not happen
        }
        Service implService = service.getService();
        if (implService != null) {
            Reference implReference = assemblyFactory.createReference();
            implReference.setName(CALLBACK_PREFIX + implService.getName());
            try {
                InterfaceContract implContract =
                    (InterfaceContract)implService.getInterfaceContract().clone();
                implContract.setInterface(implContract.getCallbackInterface());
                implContract.setCallbackInterface(null);
                implReference.setInterfaceContract(implContract);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            componentReference.setReference(implReference);
        }
        component.getReferences().add(componentReference);
        return componentReference;
    }

    /**
     * Activate composite services in nested composites.
     * 
     * @param composite
     * @param problems
     */
    public void activateCompositeServices(Composite composite) {

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // First process nested composites
                activateCompositeServices((Composite)implementation);

                // Process the component services declared on components
                // in this composite
                for (ComponentService componentService : component.getServices()) {
                    Service implService = componentService.getService();
                    if (implService != null && implService instanceof CompositeService) {
                        CompositeService compositeService = (CompositeService)implService;

                        // Get the inner most promoted service
                        ComponentService promotedService =
                            getPromotedComponentService(compositeService);
                        if (promotedService != null) {
                            Component promotedComponent = getPromotedComponent(compositeService);

                            // Default to use the interface from the promoted
                            // service
                            if (compositeService.getInterfaceContract() == null) {
                                compositeService.setInterfaceContract(promotedService
                                    .getInterfaceContract());
                            }
                            if (componentService.getInterfaceContract() == null) {
                                componentService.setInterfaceContract(promotedService
                                    .getInterfaceContract());
                            }

                            // Create a new component service to represent this
                            // composite service on the promoted component
                            ComponentService newComponentService =
                                assemblyFactory.createComponentService();
                            newComponentService.setName("$promoted$." + compositeService.getName());
                            promotedComponent.getServices().add(newComponentService);
                            newComponentService.setService(promotedService.getService());
                            newComponentService.getBindings()
                                .addAll(compositeService.getBindings());
                            newComponentService.setInterfaceContract(compositeService
                                .getInterfaceContract());
                            if (compositeService.getInterfaceContract() != null && compositeService
                                .getInterfaceContract().getCallbackInterface() != null) {
                                newComponentService.setCallback(assemblyFactory.createCallback());
                                if (compositeService.getCallback() != null) {
                                    newComponentService.getCallback().getBindings()
                                        .addAll(compositeService.getCallback().getBindings());
                                }
                            }

                            // Change the composite service to now promote the
                            // newly created component service directly
                            compositeService.setPromotedComponent(promotedComponent);
                            compositeService.setPromotedService(newComponentService);
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
                Component promotedComponent = getPromotedComponent(compositeService);

                // Default to use the interface from the promoted service
                if (compositeService.getInterfaceContract() == null && promotedService
                    .getInterfaceContract() != null) {
                    compositeService.setInterfaceContract(promotedService.getInterfaceContract());
                }

                // Create a new component service to represent this composite
                // service on the promoted component
                ComponentService newComponentService = assemblyFactory.createComponentService();
                newComponentService.setName("$promoted$." + compositeService.getName());
                promotedComponent.getServices().add(newComponentService);
                newComponentService.setService(promotedService.getService());
                newComponentService.getBindings().addAll(compositeService.getBindings());
                newComponentService.setInterfaceContract(compositeService.getInterfaceContract());
                if (compositeService.getInterfaceContract() != null && compositeService
                    .getInterfaceContract().getCallbackInterface() != null) {
                    newComponentService.setCallback(assemblyFactory.createCallback());
                    if (compositeService.getCallback() != null) {
                        newComponentService.getCallback().getBindings().addAll(compositeService
                            .getCallback().getBindings());
                    }
                }

                // Change the composite service to now promote the newly
                // created component service directly
                compositeService.setPromotedComponent(promotedComponent);
                compositeService.setPromotedService(newComponentService);
            }
        }
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
     * Follow a service promotion chain down to the inner most (non composite)
     * component.
     * 
     * @param compositeService
     * @return
     */
    private Component getPromotedComponent(CompositeService compositeService) {
        ComponentService componentService = compositeService.getPromotedService();
        if (componentService != null) {
            Service service = componentService.getService();
            if (componentService.getName() != null && service instanceof CompositeService) {

                // Continue to follow the service promotion chain
                return getPromotedComponent((CompositeService)service);

            } else {

                // Found a non-composite service
                return compositeService.getPromotedComponent();
            }
        } else {

            // No promoted service
            return null;
        }
    }

    private void computeIntents(List<Binding> bindings, List<Intent> inheritedIntents) {
        boolean found = false;

        for (Binding binding : bindings) {
            if (binding instanceof IntentAttachPoint) {
                IntentAttachPoint policiedBinding = (IntentAttachPoint)binding;
                IntentAttachPointType bindingType = policiedBinding.getType();

                // validate intents specified for the binding
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
                
                //if qualified form of intent exists remove the unqualified form
                for ( Intent intent : intentsTable.values() ) {
                    if ( intent instanceof QualifiedIntent ) {
                        QualifiedIntent qualifiedIntent = (QualifiedIntent)intent;
                        if ( intentsTable.get(qualifiedIntent.getQualifiableIntent().getName()) != null ) {
                            intentsTable.remove(qualifiedIntent.getQualifiableIntent().getName());
                        }
                    }
                }
                policiedBinding.getRequiredIntents().clear();
                policiedBinding.getRequiredIntents().addAll(intentsTable.values());
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
}
