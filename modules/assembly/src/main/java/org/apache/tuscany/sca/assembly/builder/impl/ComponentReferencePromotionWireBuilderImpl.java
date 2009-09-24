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

import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that handles the creation of promoted composite services.
 *
 * @version $Rev$ $Date$
 */
public class ComponentReferencePromotionWireBuilderImpl implements CompositeBuilder {
    private AssemblyFactory assemblyFactory;

    public ComponentReferencePromotionWireBuilderImpl(AssemblyFactory assemblyFactory) {
        this.assemblyFactory = assemblyFactory;
    }

    public Composite build(Composite composite, Definitions definitions, Monitor monitor)
        throws CompositeBuilderException {
        wireCompositeReferences(composite, monitor);
        return composite;
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.ComponentReferencePromotionWireBuilder";
    }

    /**
     * Wire composite references in nested composites.
     * 
     * @param composite
     * @param problems
     */
    private void wireCompositeReferences(Composite composite, Monitor monitor) {

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                wireCompositeReferences((Composite)implementation, monitor);
            }
        }

        // Process component references declared on components in this composite
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                for (ComponentReference componentReference : component.getReferences()) {
                    Reference implReference = componentReference.getReference();
                    if (implReference != null && implReference instanceof CompositeReference) {

                        // If the component reference is wired, it is a promotion override
                        if (!componentReference.getEndpointReferences().isEmpty()) {
                            componentReference.setPromotionOverride(true);
                        }

                        // If the component reference is a promotion override, override the
                        // configuration of the promoted reference  
                        if (componentReference.isPromotionOverride()) {
                            CompositeReference compositeReference = (CompositeReference)implReference;
                            List<ComponentReference> promotedReferences =
                                ReferenceConfigurationUtil.getPromotedComponentReferences(compositeReference);
                            for (ComponentReference promotedReference : promotedReferences) {
                                ReferenceConfigurationUtil.reconcileReferenceBindings(componentReference,
                                                                                      promotedReference,
                                                                                      assemblyFactory,
                                                                                      monitor);
                                if (componentReference.getInterfaceContract() != null && // can be null in unit tests
                                componentReference.getInterfaceContract().getCallbackInterface() != null) {
                                    SCABinding scaCallbackBinding =
                                        promotedReference.getCallbackBinding(SCABinding.class);
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
                                /* TODO - let endpoint references worry about target service
                                                               // Wire the promoted reference to the actual non-composite component services
                                                               if (promotedReference.getMultiplicity() == Multiplicity.ONE_ONE) {
                                                                   // promotedReference.getTargets().clear();
                                                               }
                                                               for (ComponentService target : componentReference.getTargets()) {
                                                                   if (target.getService() instanceof CompositeService) {
                                       
                                                                       // Wire to the actual component service
                                                                       // promoted by a composite service
                                                                       CompositeService compositeService = (CompositeService)target.getService();
                                                                       // Find the promoted component service
                                                                       ComponentService componentService =
                                                                           ServiceConfigurationUtil.getPromotedComponentService(compositeService);
                                                                       if (componentService != null) {
                                                                           promotedReference.getTargets().add(componentService);
                                                                       }
                                                                   } else {
                                       
                                                                       // Wire to a non-composite target service
                                                                       promotedReference.getTargets().add(target);
                                                                   }
                                                               }
                                */
                            }
                        }
                    }
                }
            } else {
                /* TODO - let endpoint references worry about target servicep
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
                */
            }
        }
    }

}
