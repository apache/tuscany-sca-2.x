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
 * A temporary utility class that resolves wires in a composite.
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
                
                //FIXME Print problems to help with debugging, will need to
                // hook this with monitoring later
                
                @Override
                public boolean add(Base o) {
                    System.err.println("Composite configuration problem: " + o);
                    return super.add(o);
                }
            };
        }
        init(problems);
        wire(problems);
    }

    private void collectIncludes(Composite composite, List<Composite> includes) {
        for (Composite include : composite.getIncludes()) {
            includes.add(include);
            collectIncludes(include, includes);
        }
    }

    private void initializePropsSvcRefs(Component component,
                                        Map<String, Service> implServices,
                                        Map<String, Reference> implReferences,
                                        Map<String, Property> implProperties,
                                        Map<String, ComponentService> compServices,
                                        Map<String, ComponentReference> compReferences,
                                        Map<String, ComponentProperty> compProperties) {
        // Index services and references
        Implementation implementation = component.getImplementation();
        if (implementation != null) {
            for (Service service : implementation.getServices()) {
                implServices.put(service.getName(), service);
            }
            for (Reference reference : implementation.getReferences()) {
                implReferences.put(reference.getName(), reference);
            }
            for (Property property : implementation.getProperties()) {
                implProperties.put(property.getName(), property);
            }
        }

        for (ComponentService componentService : component.getServices()) {
            compServices.put(componentService.getName(), componentService);
        }
        for (ComponentReference componentReference : component.getReferences()) {
            compReferences.put(componentReference.getName(), componentReference);
        }
        for (ComponentProperty componentProperty : component.getProperties()) {
            compProperties.put(componentProperty.getName(), componentProperty);
        }

    }

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

    private void reconcileComponentServices(Component component,
                                            Map<String, Service> implServices,
                                            Map<String, ComponentService> compServices,
                                            List<Base> problems) {
        for (ComponentService componentService : compServices.values()) {
            Service service = implServices.get(componentService.getName());
            if (service != null) {
                componentService.setService(service);
            } else {
                problems.add(componentService);
            }
        }

        for (Service service : implServices.values()) {
            if (!compServices.containsKey(service.getName())) {
                ComponentService componentService = assemblyFactory.createComponentService();
                componentService.setName(service.getName());
                componentService.setService(service);
                component.getServices().add(componentService);
            }
        }
    }

    private void reconcileComponentReferences(Component component,
                                              Map<String, Reference> implReferences,
                                              Map<String, ComponentReference> compReferences,
                                              List<Base> problems) {
        for (ComponentReference componentReference : compReferences.values()) {
            Reference reference = implReferences.get(componentReference.getName());
            if (reference != null) {
                componentReference.setReference(reference);
            } else {
                problems.add(componentReference);
            }
        }

        for (Reference reference : implReferences.values()) {
            if (!compReferences.containsKey(reference.getName())) {
                ComponentReference componentReference = assemblyFactory.createComponentReference();
                componentReference.setName(reference.getName());
                componentReference.setReference(reference);
                componentReference.setMultiplicity(reference.getMultiplicity());
                componentReference.getTargets().addAll(reference.getTargets());
                componentReference.setInterfaceContract(reference.getInterfaceContract());
                component.getReferences().add(componentReference);
                if (!ReferenceUtil.validateMultiplicityAndTargets(componentReference.getMultiplicity(), 
                                                                  componentReference.getTargets())) {
                     problems.add(componentReference);
                 }
            } else {
                ComponentReference compRef = compReferences.get(reference.getName());
                if (compRef.getMultiplicity() != null) {
                    if (!ReferenceUtil.isValidMultiplicityOverride(reference.getMultiplicity(), 
                                                                  compRef.getMultiplicity())) {
                        problems.add(compRef);
                    }
                } else {
                    compRef.setMultiplicity(reference.getMultiplicity());
                }
                
                if (compRef.getInterfaceContract() != null) {
                    if (!compRef.getInterfaceContract().equals(reference.getInterfaceContract())) {
                        if (!InterfaceUtil.checkInterfaceCompatibility(reference.getInterfaceContract(), 
                                                                       compRef.getInterfaceContract())) {
                            problems.add(compRef);
                        }
                    }
                } else {
                    compRef.setInterfaceContract(reference.getInterfaceContract());
                }
                
                if (compRef.getTargets().isEmpty()) {
                    compRef.getTargets().addAll(reference.getTargets());
                    if (!ReferenceUtil.validateMultiplicityAndTargets(compRef.getMultiplicity(), 
                                                                     compRef.getTargets())) {
                        problems.add(compRef);
                    }
                }
                
            }
        }
    }

    private void reconcileComponentProperties(Component component,
                                              Map<String, Property> implProperties,
                                              Map<String, ComponentProperty> compProperties,
                                              List<Base> problems) {
        for (ComponentProperty componentProperty : compProperties.values()) {
            Property property = implProperties.get(componentProperty.getName());
            if (property != null) {
                componentProperty.setProperty(property);
                if (componentProperty.getValue() == null && property.isMustSupply()) {
                    problems.add(componentProperty);
                }
            } else {
                problems.add(componentProperty);
            }
        }

        for (Property property : implProperties.values()) {
            if (!compProperties.containsKey(property.getName())) {
                if (!property.isMustSupply()) {
                    ComponentProperty componentProperty = assemblyFactory.createComponentProperty();
                    componentProperty.setName(property.getName());
                    componentProperty.setProperty(property);
                    component.getProperties().add(componentProperty);
                } else {
                    problems.add(property);
                }
            }
        }
    }

    private void init(List<Base> problems) {
        Map<String, Service> implServices = null;
        Map<String, Reference> implReferences = null;
        Map<String, Property> implProperties = null;
        Map<String, ComponentService> compServices = null;
        Map<String, ComponentReference> compReferences = null;
        Map<String, ComponentProperty> compProperties = null;

        // Bring includes in
        List<Composite> includes = new ArrayList<Composite>();
        collectIncludes(composite, includes);
        fuseIncludes(composite, includes);

        // Init all component services and references
        for (Component component : composite.getComponents()) {
            implServices = new HashMap<String, Service>();
            implReferences = new HashMap<String, Reference>();
            implProperties = new HashMap<String, Property>();
            compServices = new HashMap<String, ComponentService>();
            compReferences = new HashMap<String, ComponentReference>();
            compProperties = new HashMap<String, ComponentProperty>();

            initializePropsSvcRefs(component,
                                   implServices,
                                   implReferences,
                                   implProperties,
                                   compServices,
                                   compReferences,
                                   compProperties);

            // Reconcile component services/references/properties and implementation
            // services/references and Create component services/references/properties
            //for the services/references declared by the implementation
            reconcileComponentServices(component, implServices, compServices, problems);
            reconcileComponentReferences(component, implReferences, compReferences, problems);
            reconcileComponentProperties(component, implProperties, compProperties, problems);
        }
    }

    private void wire(List<Base> problems) {

        // Index and bind all component services and references
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
        
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

        // Resolve promoted services and references
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;
            ComponentService componentService = compositeService.getPromotedService();
            if (componentService != null && componentService.isUnresolved()) {
                ComponentService resolved = componentServices.get(componentService.getName());
                if (resolved != null) {
                    compositeService.setPromotedService(resolved);
                } else {
                    problems.add(compositeService);
                }
            }
        }
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences =
                compositeReference.getPromotedReferences();
            for (int i = 0, n = promotedReferences.size(); i < n; i++) {
                ComponentReference componentReference = promotedReferences.get(i);
                if (componentReference.isUnresolved()) {
                    ComponentReference resolved =
                        componentReferences.get(componentReference.getName());
                    if (resolved != null) {
                        promotedReferences.set(i, resolved);
                    } else {
                        problems.add(compositeReference);
                    }
                }
            }
        }

        // Wire references to their targets
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

        // Wire references as specified in wires
        List<Wire> wires = composite.getWires();
        for (int i = 0, n = wires.size(); i < n; i++) {
            Wire wire = wires.get(i);

            ComponentReference resolvedReference;
            ComponentService resolvedService;

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
            if (resolvedReference != null && resolvedService != null) {
                resolvedReference.getTargets().add(resolvedService);
            }
        }

        // Clear wires
        composite.getWires().clear();
    }

}
