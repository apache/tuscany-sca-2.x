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


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that makes the connections between composite services and references
 * and the services and references that they promote
 *
 * @version $Rev$ $Date$
 */
public class CompositePromotionBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {

    public CompositePromotionBuilderImpl(AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper) {
        super(assemblyFactory, null, null, null, interfaceContractMapper);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositePromotionBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        connectCompositeReferencesAndServices(composite, monitor);
    }
    
    /**
     * Connect composite references and services to the reference and services that they promote.
     * 
     * @param composite
     * @param componentServices
     * @param problems
     */
    protected void connectCompositeReferencesAndServices(Composite composite, Monitor monitor){
        // Wire nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                connectCompositeReferencesAndServices((Composite)implementation, monitor);
            }
        }

        // Index components, services and references
        Map<String, Component> components = new HashMap<String, Component>();
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
        indexComponentsServicesAndReferences(composite, components, componentServices, componentReferences);

        // Connect composite services and references to the component
        // services and references that they promote
        connectCompositeServices(composite, components, componentServices, monitor);
        connectCompositeReferences(composite, components, componentReferences, monitor);
    }
            
    /**
     * Connect composite services to the component services that they promote.
     * 
     * @param composite
     * @param componentServices
     * @param problems
     */
    private void connectCompositeServices(Composite composite,
                                          Map<String, Component> components,
                                          Map<String, ComponentService> componentServices,
                                          Monitor monitor) {
    
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
    
        // Connect composite services to the component services that they promote
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;
            ComponentService componentService = compositeService.getPromotedService();
            if (componentService != null && componentService.isUnresolved()) {
                
                String promotedComponentName = compositeService.getPromotedComponent().getName(); 
                String promotedServiceName;
                if (componentService.getName() != null) {
                	if( compositeService.isCallback() ) {
                		// For callbacks the name already has the form "componentName"/"servicename"
                		promotedServiceName = componentService.getName();
                	} else {
                		promotedServiceName = promotedComponentName + '/' + componentService.getName();
                	}
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
                    InterfaceContract compositeServiceInterfaceContract = compositeService.getInterfaceContract();
                    InterfaceContract promotedServiceInterfaceContract = promotedService.getInterfaceContract();
                    if (compositeServiceInterfaceContract == null) {
                        compositeService.setInterfaceContract(promotedServiceInterfaceContract);
                    } else if (promotedServiceInterfaceContract != null) {
                        // Check the compositeServiceInterfaceContract and promotedServiceInterfaceContract
                        boolean isCompatible = interfaceContractMapper.isCompatible(compositeServiceInterfaceContract, promotedServiceInterfaceContract);
                        if(!isCompatible){
                            warning(monitor, "ServiceInterfaceNotSubSet", compositeService, promotedServiceName);
                        }
                    }
    
                } else {
                	// MJE 15/05/2005 - Priority raised from "warning" to "error" to fix TUSCANY-3034
                    error(monitor, "PromotedServiceNotFound", composite, composite.getName().toString(), promotedServiceName);
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
                                            Map<String, Component> components,
                                            Map<String, ComponentReference> componentReferences, Monitor monitor) {
    
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
    
        // Connect composite references to the component references that they promote
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences = compositeReference.getPromotedReferences();
            for (int i = 0, n = promotedReferences.size(); i < n; i++) {
                ComponentReference componentReference = promotedReferences.get(i);
                if (componentReference.isUnresolved()) {
                    String componentReferenceName = componentReference.getName();
                    componentReference = componentReferences.get(componentReferenceName);
                    if (componentReference != null) {
                        // Set the promoted component
                        Component promotedComponent = compositeReference.getPromotedComponents().get(i);
                        promotedComponent = components.get(promotedComponent.getName());
                        compositeReference.getPromotedComponents().set(i, promotedComponent);
                        
                        componentReference.setPromoted( true );
                        
                        // Point to the resolved component reference
                        promotedReferences.set(i, componentReference);
    
                        // Use the interface contract from the component reference if none
                        // is specified on the composite reference
                        InterfaceContract compositeReferenceInterfaceContract = compositeReference.getInterfaceContract();
                        InterfaceContract componentReferenceInterfaceContract = componentReference.getInterfaceContract();
                        if (compositeReferenceInterfaceContract == null) {
                            compositeReference.setInterfaceContract(componentReferenceInterfaceContract);
                        } else if (componentReferenceInterfaceContract != null) {
                            // Check that the componentInterfaceContract is a subset of the compositeInterfaceContract
                            boolean isCompatible = interfaceContractMapper.isCompatible( componentReferenceInterfaceContract, compositeReferenceInterfaceContract);
                            if (!isCompatible) {
                                warning(monitor, "ReferenceInterfaceNotSubSet", compositeReference, componentReferenceName);
                            }
                        }
                    } else {
                        warning(monitor, "PromotedReferenceNotFound", composite, composite.getName().toString(), componentReferenceName);
                    }
                }
            }
        }
    }    
}
