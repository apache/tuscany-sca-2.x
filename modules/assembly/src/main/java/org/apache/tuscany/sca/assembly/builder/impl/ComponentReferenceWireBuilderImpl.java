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

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that wires component references.
 *
 * @version $Rev$ $Date$
 */
public class ComponentReferenceWireBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {

    public ComponentReferenceWireBuilderImpl(ExtensionPointRegistry registry) {
        super(registry);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.ComponentReferenceWireBuilder";
    }

    public Composite build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        wireComponentReferences(composite, monitor);
        return composite;
    }

    /**
     * Wire component references to component services and connect promoted
     * services/references to component services/references inside a composite.
     * 
     * @param composite
     */
    protected void wireComponentReferences(Composite composite, Monitor monitor) {

        monitor.pushContext(composite.getName().toString());
        
        try {
            // Wire nested composites recursively
            for (Component component : composite.getComponents()) {
                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
    
                        wireComponentReferences((Composite)implementation, monitor);
                           
                }
            }
    
            // Index components, services and references
            Map<String, Component> components = new HashMap<String, Component>();
            Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
            indexComponentsServicesAndReferences(composite, components, componentServices, componentReferences);
    
            // Connect component references as described in wires
            connectWires(composite, componentServices, componentReferences, monitor);
    
    
            // Finally clear the original reference target lists as we now have
            // bindings to represent the targets
            //  for (ComponentReference componentReference : componentReferences.values()) {
            //      componentReference.getTargets().clear();
            //  }
        
        } finally {
            monitor.popContext();
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
                    warning(monitor, "WireSourceNotFound", composite, source.getName());
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
                    warning(monitor, "WireTargetNotFound", composite, source.getName());
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

                    //resolvedReference.getTargets().add(resolvedService);
                	if (wire.isReplace()) {
                		resolvedReference.getTargets().clear();
                	}
                    resolvedReference.getTargets().add(wire.getTarget());
                } else {
                    warning(monitor, "WireIncompatibleInterface", composite, source.getName(), target.getName());
                }
            }
        }

        // Clear the list of wires
        composite.getWires().clear();
    }

}
