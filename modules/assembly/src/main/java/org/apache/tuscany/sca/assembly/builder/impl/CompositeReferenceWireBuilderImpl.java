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
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that wires composite references.
 *
 * @version $Rev$ $Date$
 */
public class CompositeReferenceWireBuilderImpl implements CompositeBuilder {
    private AssemblyFactory assemblyFactory;
    private EndpointFactory endpointFactory;
    private Monitor monitor;

    public CompositeReferenceWireBuilderImpl(AssemblyFactory assemblyFactory, EndpointFactory endpointFactory, Monitor monitor) {
        this.assemblyFactory = assemblyFactory;
        this.endpointFactory = endpointFactory;
        this.monitor = monitor;
    }

    /**
     * Wire composite references in a deployment composite.
     * 
     * @param composite
     */
    public void build(Composite composite) throws CompositeBuilderException {
    
        // Process composite references declared in this composite
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;

            // If the composite reference is a promotion override, override the
            // configuration of the promoted reference.  
            //FIXME: Policy configuration or wiring of domain-level composite references
            // doesn't cause a promotion override, unless the composite reference has
            // additional bindings.  Do we need to detect this and force an override?
            if (compositeReference.isPromotionOverride()) {
                List<ComponentReference> promotedReferences =
                    ReferenceConfigurationUtil.getPromotedComponentReferences(compositeReference);
                for (ComponentReference promotedReference : promotedReferences) {
                    ReferenceConfigurationUtil.reconcileReferenceBindings(
                            compositeReference, promotedReference, assemblyFactory, endpointFactory, monitor);
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
        }
    }

}
