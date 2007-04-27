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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Base;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.assembly.Wire;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;

/**
 * A utility class that handles the configuration of the components inside a 
 * composite and the wiring of component references to component services.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeUtil {

    private AssemblyFactory assemblyFactory;
    private Composite composite;

    public CompositeUtil(AssemblyFactory assemblyFactory, Composite composite) {
        this.assemblyFactory = assemblyFactory;
        this.composite = composite;
    }

    public CompositeUtil(Composite composite) {
        this(new DefaultAssemblyFactory(), composite);
    }

    public void configure(List<Base> problems) {
        if (problems == null) {
            problems = new ArrayList<Base>() {
                private static final long serialVersionUID = 4819831446590718923L;

                
                @Override
                public boolean add(Base o) {
                    //TODO Use a monitor to report configuration problems
                    
                    // Uncommenting the following two lines can be useful to detect
                    // and troubleshoot SCA assembly XML composite configuration
                    // problems.
                    
//                    System.err.println("Composite configuration problem:");
//                    new PrintUtil(System.err).print(o);
                    return super.add(o);
                }
            };
        }

        // Collect and fuse includes
        List<Composite> includes = new ArrayList<Composite>();
        collectIncludes(composite, includes);
        fuseIncludes(composite, includes);
        configureComponents(problems);
        wireReferences(problems);
    }

    /**
     * Collect all includes in a graph of includes
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
     * Copy a list of includes into a composite
     * @param composite
     * @param includes
     */
    private void fuseIncludes(Composite composite, List<Composite> includes) {
        for (Composite include : includes) {
            include = include.copy();
            composite.getComponents().addAll(include.getComponents());
            composite.getServices().addAll(include.getServices());
            composite.getReferences().addAll(include.getReferences());
            composite.getProperties().addAll(include.getProperties());
            composite.getWires().addAll(include.getWires());
            composite.getPolicySets().addAll(include.getPolicySets());
            composite.getRequiredIntents().addAll(include.getRequiredIntents());
        }
        composite.getIncludes().clear();
    }

    /**
     * Reconcile component services and services defined on the component
     * type.
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
        for (ComponentService componentService: component.getServices()) {
            Service service = componentService.getService();
            if (service != null) {
    
                // Reconcile interface
                if (componentService.getInterfaceContract() != null) {
                    if (!componentService.getInterfaceContract().equals(service.getInterfaceContract())) {
                        if (!InterfaceUtil.checkInterfaceCompatibility(service.getInterfaceContract(), 
                                                                       componentService.getInterfaceContract())) {
                            problems.add(componentService);
                        }
                    }
                } else {
                    componentService.setInterfaceContract(service.getInterfaceContract());
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
                problems.add(componentReference);
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
        for (ComponentReference componentReference: component.getReferences()) {
            Reference reference = componentReference.getReference();
            if (reference != null) {
    
                // Reconcile multiplicity
                if (componentReference.getMultiplicity() != null) {
                    if (!ReferenceUtil.isValidMultiplicityOverride(reference.getMultiplicity(), 
                                                                  componentReference.getMultiplicity())) {
                        problems.add(componentReference);
                    }
                } else {
                    componentReference.setMultiplicity(reference.getMultiplicity());
                }
                
                // Reconcile interface
                if (componentReference.getInterfaceContract() != null) {
                    if (!componentReference.getInterfaceContract().equals(reference.getInterfaceContract())) {
                        if (!InterfaceUtil.checkInterfaceCompatibility(reference.getInterfaceContract(), 
                                                                       componentReference.getInterfaceContract())) {
                            problems.add(componentReference);
                        }
                    }
                } else {
                    componentReference.setInterfaceContract(reference.getInterfaceContract());
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
        for (ComponentProperty componentProperty: component.getProperties()) {
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
                if (componentProperty.getXSDElement() == null &&
                    componentProperty.getXSDType() == null) {
                    problems.add(componentProperty);
                }
            }
        }
    }

    /**
     * Configure components in the composite.
     * @param problems
     */
    private void configureComponents(List<Base> problems) {

        // Initialize all component services and references
        for (Component component : composite.getComponents()) {
            Map<String, Service> services = new HashMap<String, Service>();
            Map<String, Reference> references = new HashMap<String, Reference>();
            Map<String, Property> properties = new HashMap<String, Property>();
            
            // Check that the component has a resolved implementation
            Implementation implementation = component.getImplementation();
            if (implementation == null) {
                
                // A component must have an implementation
                problems.add(component);
            } else if (implementation.isUnresolved()) {
                
                // The implementation must be fully resolved 
                problems.add(implementation);
            } else {

                // Index properties, services and references
                for (Service service : implementation.getServices()) {
                    services.put(service.getName(), service);
                }
                for (Reference reference : implementation.getReferences()) {
                    references.put(reference.getName(), reference);
                }
                for (Property property : implementation.getProperties()) {
                    properties.put(property.getName(), property);
                }
            }

            // Index component services, references and properties
            Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
            Map<String, ComponentProperty> componentProperties = new HashMap<String, ComponentProperty>();
            for (ComponentService componentService : component.getServices()) {
                componentServices.put(componentService.getName(), componentService);
            }
            for (ComponentReference componentReference : component.getReferences()) {
                componentReferences.put(componentReference.getName(), componentReference);
            }
            for (ComponentProperty componentProperty : component.getProperties()) {
                componentProperties.put(componentProperty.getName(), componentProperty);
            }

            // Reconcile component services/references/properties and implementation
            // services/references and create component services/references/properties
            // for the services/references declared by the implementation
            reconcileServices(component, services, componentServices, problems);
            reconcileReferences(component, references, componentReferences, problems);
            reconcileProperties(component, properties, componentProperties, problems);
        }
    }
    
    /**
     * Create SCA bindings for component services and references.
     * @param componentServices
     * @param componentReferences
     * @param problems
     */
    private void createSCABindings(Map<String, ComponentService> componentServices,
                              Map<String, ComponentReference> componentReferences,
                              List<Base> problems) {
        
        for (Component component : composite.getComponents()) {
            int i =0;
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
                    componentReference.getBindings().add(scaBinding);
                }
                scaBinding.setURI(uri);
                scaBinding.setComponent(component);
            }
        }
        
    }

    /**
     * Resolves promoted services
     * 
     * @param componentServices
     * @param problems
     */
    private void connectPromotedServices(
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
                    
                    // Use the interface contract from the component service if none
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
     * Resolves promoted references
     * @param componentReferences
     * @param problems
     */
    private void connectPromotedReferences(
                                           Map<String, ComponentReference> componentReferences,
                                           List<Base> problems) {

        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences =
                compositeReference.getPromotedReferences();
            for (int i = 0, n = promotedReferences.size(); i < n; i++) {
                ComponentReference componentReference = promotedReferences.get(i);
                if (componentReference.isUnresolved()) {
                    componentReference = componentReferences.get(componentReference.getName());
                    if (componentReference != null) {

                        // Point to the resolved component reference
                        promotedReferences.set(i, componentReference);
                        componentReference.promotedAs().add(compositeReference);

                        // Use the interface contract from the component reference if none
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
     * Connect references to their targets
     * @param componentServices
     * @param componentReferences
     * @param problems
     */
    private void connectReferenceTargets(
                                      Map<String, ComponentService> componentServices,
                                      Map<String, ComponentReference> componentReferences,
                                      List<Base> problems) {
        
        for (ComponentReference componentReference : componentReferences.values()) {
            List<ComponentService> targets = componentReference.getTargets();
            if (!targets.isEmpty()) {
                for (int i = 0, n = targets.size(); i < n; i++) {
                    ComponentService target = targets.get(i);
                    if (target.isUnresolved()) {
                        ComponentService resolved = componentServices.get(target.getName());
                        if (resolved != null) {
                            targets.set(i, resolved);
                        } else {
                            problems.add(target);
                        }
                    }
                }
            } else if (componentReference.getReference() != null) {

                // Wire reference targets from the corresponding reference in
                // the componentType
                for (ComponentService target : componentReference.getReference().getTargets()) {
                    if (target.isUnresolved()) {
                        ComponentService resolved = componentServices.get(target.getName());
                        if (resolved != null) {
                            targets.add(resolved);
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
     * @param componentServices
     * @param componentReferences
     * @param problems
     */
    private void connectWiredReferences(
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
     * Wire the references inside the composite.
     * 
     * @param problems
     */
    private void wireReferences(List<Base> problems) {

        // Index and bind all component services and references
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
        
        // Create SCA bindings on all component services and references
        createSCABindings(componentServices, componentReferences, problems);

        // Resolve promoted services and references
        connectPromotedServices(componentServices, problems);
        connectPromotedReferences(componentReferences, problems);
        
        // Connect references to their targets
        connectReferenceTargets(componentServices, componentReferences, problems);

        // Connect references as described in wires
        connectWiredReferences(componentServices, componentReferences, problems);

        // Validate that references are wired or promoted, according
        // to their multiplicity
        for (ComponentReference componentReference : componentReferences.values()) {
            if (!ReferenceUtil.validateMultiplicityAndTargets(
                                                              componentReference.getMultiplicity(), 
                                                              componentReference.getTargets(),
                                                              componentReference.promotedAs())) {
                problems.add(componentReference);
            }
         }
    }
}
