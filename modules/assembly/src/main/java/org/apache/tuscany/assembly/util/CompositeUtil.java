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
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Implementation;
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
            problems = new ArrayList<Base>();
        }
        init(problems);
        wire(problems);
    }

    private void init(List<Base> problems) {

        // Init all component services and references
        for (Component component : composite.getComponents()) {
            Map<String, Service> services = new HashMap<String, Service>();
            Map<String, Reference> references = new HashMap<String, Reference>();

            // Index services and references
            Implementation implementation = component.getImplementation();
            if (implementation != null) {
                for (Service service : implementation.getServices()) {
                    services.put(service.getName(), service);
                }
                for (Reference reference : implementation.getReferences()) {
                    references.put(reference.getName(), reference);
                }
            }

            // Index component services and references
            Map<String, ComponentService> cservices = new HashMap<String, ComponentService>();
            Map<String, ComponentReference> creferences = new HashMap<String, ComponentReference>();
            for (ComponentService componentService : component.getServices()) {
                cservices.put(componentService.getName(), componentService);
            }
            for (ComponentReference componentReference : component.getReferences()) {
                creferences.put(componentReference.getName(), componentReference);
            }

            // Reconcile component services/references and implementation
            // services/references
            for (ComponentService componentService : cservices.values()) {
                Service service = services.get(componentService.getName());
                if (service != null) {
                    componentService.setService(service);
                } else {
                    problems.add(componentService);
                }
            }
            for (ComponentReference componentReference : creferences.values()) {
                Reference reference = references.get(componentReference.getName());
                if (reference != null) {
                    componentReference.setReference(reference);
                } else {
                    problems.add(componentReference);
                }
            }

            // Create component services/references for the services/references
            // declared by
            // the implementation
            for (Service service : services.values()) {
                if (!cservices.containsKey(service.getName())) {
                    ComponentService componentService = assemblyFactory.createComponentService();
                    componentService.setName(service.getName());
                    componentService.setService(service);
                    component.getServices().add(componentService);
                }
            }
            for (Reference reference : references.values()) {
                if (!cservices.containsKey(reference.getName())) {
                    ComponentReference componentReference = assemblyFactory.createComponentReference();
                    componentReference.setName(reference.getName());
                    componentReference.setReference(reference);
                    component.getReferences().add(componentReference);
                }
            }
        }
    }

    private void wire(List<Base> problems) {

        // Index and bind all component services and references
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
        for (Component component : composite.getComponents()) {
            for (ComponentService componentService : component.getServices()) {
                String uri;
                if (componentService.getName() != null) {
                    uri = component.getName() + '/' + componentService.getName();
                } else {
                    uri = component.getName();
                }
                componentServices.put(uri, componentService);

                // Create and configure an SCA binding for the service
                SCABinding scaBinding = componentService.getBinding(SCABinding.class);
                if (scaBinding == null) {
                    scaBinding = assemblyFactory.createSCABinding();
                    componentService.getBindings().add(scaBinding);
                }
                scaBinding.setURI(uri);
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
            List<ComponentReference> promotedReferences = compositeReference.getPromotedReferences();
            for (int i = 0, n = promotedReferences.size(); i < n; i++) {
                ComponentReference componentReference = promotedReferences.get(i);
                if (componentReference.isUnresolved()) {
                    ComponentReference resolved = componentReferences.get(componentReference.getName());
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
    }

}
