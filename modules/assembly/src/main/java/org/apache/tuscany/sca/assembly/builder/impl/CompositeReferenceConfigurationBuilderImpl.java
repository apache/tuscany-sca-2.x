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
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that handles the configuration of composite references.
 *
 * @version $Rev: 651179 $ $Date: 2008-04-24 08:21:13 +0100 (Thu, 24 Apr 2008) $
 */
public class CompositeReferenceConfigurationBuilderImpl implements CompositeBuilder {
    private AssemblyFactory assemblyFactory;

    public CompositeReferenceConfigurationBuilderImpl(AssemblyFactory assemblyFactory) {
        this.assemblyFactory = assemblyFactory;
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeReferenceConfigurationBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {

        // Process nested composites recursively
        configureNestedCompositeReferences(composite);

        // Process top level composite references
        // TODO - In OASIS the treament of top level composite references is different so need to review
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;

            // Get the next lower level promoted reference
            List<ComponentReference> promotedRefs = compositeReference.getPromotedReferences();
            if (!promotedRefs.isEmpty()) {
                if (promotedRefs.size() == 1) {
                    ComponentReference promotedReference = promotedRefs.get(0);

                    // Set the bindings using the top level bindings to override the lower level bindings
                    if (compositeReference.isOverridingBindings()) {
                        compositeReference.setPromotionOverride(true);
                    } else if (promotedReference.isOverridingBindings()) {
                        compositeReference.getBindings().clear();
                        for (Binding binding : promotedReference.getBindings()) {
                            try {
                                compositeReference.getBindings().add((Binding)binding.clone());
                            } catch (CloneNotSupportedException ex) {
                                // this binding can't be used in the promoted reference
                            }
                        }
                    }
                    if (compositeReference.getInterfaceContract() != null && compositeReference.getInterfaceContract()
                        .getCallbackInterface() != null) {
                        if (isCallbackOverridingBindings(compositeReference)) {
                        compositeReference.setPromotionOverride(true);
                        } else if (isCallbackOverridingBindings(promotedReference)) {
                            if (compositeReference.getCallback() != null) {
                                compositeReference.getCallback().getBindings().clear();
                            } else {
                                compositeReference.setCallback(assemblyFactory.createCallback());
                            }
                            for (Binding binding : promotedReference.getCallback().getBindings()) {
                                try {
                                    compositeReference.getCallback().getBindings().add((Binding)binding.clone());
                                } catch (CloneNotSupportedException ex) {
                                    // this binding can't be used in the promoted reference
                                }
                            }
                        }
                    }
                } else {
                    // This composite reference promotes multiple component references.
                    // Because the component reference bindings can all be different, we don't
                    // copy any of them up to this composite reference, which will therefore always
                    // have its own binding, even if it's only the default SCA binding.
                    if (compositeReference.isOverridingBindings() || (compositeReference
                        .getCallback() != null && !compositeReference.getCallback().getBindings().isEmpty())) {
                        compositeReference.setPromotionOverride(true);
                    }
                }
            }
        }
    }

    private void configureNestedCompositeReferences(Composite composite) {

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // First process nested composites
                configureNestedCompositeReferences((Composite)implementation);

                // Process the component references declared on components in this composite
                for (ComponentReference componentReference : component.getReferences()) {
                    Reference implReference = componentReference.getReference();
                    if (implReference != null && implReference instanceof CompositeReference) {
                        CompositeReference compositeReference = (CompositeReference)implReference;

                        // Get the next lower level promoted reference
                        List<ComponentReference> promotedRefs = compositeReference.getPromotedReferences();
                        if (!promotedRefs.isEmpty()) {
                            if (promotedRefs.size() == 1) {
                                ComponentReference promotedReference = promotedRefs.get(0);

                                // Set the bindings using the top level bindings to override the lower level bindings
                                if (!compositeReference.isOverridingBindings() && promotedReference
                                    .isOverridingBindings()) {
                                    compositeReference.getBindings().clear();
                                    for (Binding binding : promotedReference.getBindings()) {
                                        try {
                                            compositeReference.getBindings().add((Binding)binding.clone());
                                        } catch (CloneNotSupportedException ex) {
                                            // this binding can't be used in the promoted reference
                                        }
                                    }
                                }
                                if (componentReference.isOverridingBindings()) {
                                    componentReference.setPromotionOverride(true);
                                } else if (compositeReference.isOverridingBindings()) {
                                    componentReference.getBindings().clear();
                                    componentReference.getBindings().addAll(compositeReference.getBindings());
                                }
                                if (componentReference.getInterfaceContract() != null && componentReference
                                    .getInterfaceContract().getCallbackInterface() != null) {
                                    if (!isCallbackOverridingBindings(compositeReference) 
                                        && isCallbackOverridingBindings(promotedReference)) {
                                        if (compositeReference.getCallback() != null) {
                                            compositeReference.getCallback().getBindings().clear();
                                        } else {
                                            compositeReference.setCallback(assemblyFactory.createCallback());
                                        }
                                        compositeReference.getCallback().getBindings().addAll(promotedReference
                                            .getCallback().getBindings());
                                    }
                                    if (componentReference.getCallback() != null && !componentReference
                                        .getCallback().getBindings().isEmpty()) {
                                        componentReference.setPromotionOverride(true);
                                    } else if (isCallbackOverridingBindings(compositeReference)) {
                                        if (componentReference.getCallback() != null) {
                                            componentReference.getCallback().getBindings().clear();
                                        } else {
                                            componentReference.setCallback(assemblyFactory.createCallback());
                                        }
                                        for (Binding binding : compositeReference.getCallback().getBindings()) {
                                            try {
                                                componentReference.getCallback().getBindings().add((Binding)binding
                                                    .clone());
                                            } catch (CloneNotSupportedException ex) {
                                                // this binding can't be used in the promoted reference
                                            }
                                        }
                                    }
                                }
                            } else {
                                // This component reference promotes multiple lower-level component references.
                                // Because the lower-level component reference bindings can all be different,
                                // we don't copy any of them up to this component reference, which will therefore
                                // always have its own binding, even if it's only the default SCA binding.
                                if (componentReference.isOverridingBindings() 
                                    || isCallbackOverridingBindings(componentReference)) {
                                    componentReference.setPromotionOverride(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean isCallbackOverridingBindings(Contract contract) {
        Callback callback = contract.getCallback();
        return callback != null && !callback.getBindings().isEmpty();
    }

}
