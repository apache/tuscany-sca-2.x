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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * Apply any <wire/> elements in the composites by creating suitable reference targets
 *
 * @version $Rev$ $Date$
 */
public class CompositeWireApplierImpl implements CompositeBuilder {
    
    private InterfaceContractMapper interfaceContractMapper;
    
    
    public CompositeWireApplierImpl(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
    }    

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeWireApplierImpl";
    }

    public Composite build(Composite composite, BuilderContext context)
        throws CompositeBuilderException {
        
        processComposite(composite, context);
        
        return composite;
    }
    
    private void processComposite(Composite composite, BuilderContext context){
        // Index components, services and references
        Map<String, Component> components = new HashMap<String, Component>();
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
        indexComponentsServicesAndReferences(composite, components, componentServices, componentReferences);

        // Connect component references as described in wires
        connectWires(composite, componentServices, componentReferences, context.getMonitor());
        
        for (Component component : composite.getComponents()) {
            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                processComposite((Composite)implementation, context);
            }    
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
                String uri = component.getName() + '/'
                        + componentService.getName();
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
            for (ComponentReference componentReference : component
                    .getReferences()) {
                String uri = component.getName() + '/'
                        + componentReference.getName();
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
                    .isCompatibleSubset(resolvedReference.getInterfaceContract(), resolvedService.getInterfaceContract())) {

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
}
