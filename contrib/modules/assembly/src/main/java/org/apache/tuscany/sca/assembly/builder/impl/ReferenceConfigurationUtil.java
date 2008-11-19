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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * This class encapsulates utility methods to deal with reference definitions
 *
 * @version $Rev$ $Date$
 */
abstract class ReferenceConfigurationUtil {

    /**
     * Report a warning.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private static void warning(Monitor monitor, String message, Object model, String... messageParameters) {
        if (monitor != null) {
            Problem problem = new ProblemImpl(ReferenceConfigurationUtil.class.getName(), "assembly-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    static boolean isValidMultiplicityOverride(Multiplicity definedMul, Multiplicity overridenMul) {
        if (definedMul != overridenMul) {
            switch (definedMul) {
                case ZERO_N:
                    return overridenMul == Multiplicity.ZERO_ONE;
                case ONE_N:
                    return overridenMul == Multiplicity.ONE_ONE;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }
    
    static boolean validateMultiplicityAndTargets(Multiplicity multiplicity,
                                                         List<?> targets, List<Binding> bindings) {
        
        // Count targets
        int count = targets.size();
        
        //FIXME workaround, this validation is sometimes invoked too early
        // before we get a chance to init the multiplicity attribute
        if (multiplicity == null) {
            return true;
        }
        
        switch (multiplicity) {
            case ZERO_N:
                break;
            case ZERO_ONE:
                if (count > 1) {
                    return false;
                }
                break;
            case ONE_ONE:
                if (count != 1) {
                    if (count == 0) {
                        for (Binding binding: bindings) {
                            if (!(binding instanceof OptimizableBinding) || binding.getURI()!=null) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
                break;
            case ONE_N:
                if (count < 1) {
                    if (count == 0) {
                        for (Binding binding: bindings) {
                            if (!(binding instanceof OptimizableBinding) || binding.getURI()!=null) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
                break;
        }
        return true;
    }

    /**
     * Follow a reference promotion chain down to the innermost (non composite)
     * component references.
     * 
     * @param compositeReference
     * @return
     */
    static List<ComponentReference> getPromotedComponentReferences(CompositeReference compositeReference) {
        List<ComponentReference> componentReferences = new ArrayList<ComponentReference>();
        collectPromotedComponentReferences(compositeReference, componentReferences);
        return componentReferences;
    }

    /**
     * Follow a reference promotion chain down to the innermost (non composite)
     * component references.
     * 
     * @param compositeReference
     * @param componentReferences
     * @return
     */
    private static void collectPromotedComponentReferences(CompositeReference compositeReference,
                                                           List<ComponentReference> componentReferences) {
        for (ComponentReference componentReference : compositeReference.getPromotedReferences()) {
            Reference reference = componentReference.getReference();
            if (reference instanceof CompositeReference) {
    
                // Continue to follow the reference promotion chain
                collectPromotedComponentReferences((CompositeReference)reference, componentReferences);
    
            } else if (reference != null) {
    
                // Found a non-composite reference
                componentReferences.add(componentReference);
            }
        }
    }

    /**
     * Override the bindings for a promoted reference from an outer component
     * reference
     * 
     * @param reference
     * @param promotedReference
     */
    static void reconcileReferenceBindings(Reference reference,
                                           ComponentReference promotedReference,
                                           AssemblyFactory assemblyFactory,
                                           EndpointFactory endpointFactory,
                                           Monitor monitor) {
              
        if (promotedReference.getMultiplicity() == Multiplicity.ONE_ONE ||
            promotedReference.getMultiplicity() == Multiplicity.ZERO_ONE) {
            
            // if necessary override the promoted endpoints (and bindings) with the top level bindings
            if (reference.getBindings().size() > 0 ){
                
                List<Binding> bindingsToCopyDown = new ArrayList<Binding>();
                List<Endpoint> endpointsToCopyDown = new ArrayList<Endpoint>();
                
                for (Binding binding : reference.getBindings()) {
                    if ((!(binding instanceof OptimizableBinding)) || binding.getURI() != null) {
                        bindingsToCopyDown.add(binding);
                        
                        if (reference instanceof ComponentReference){
                            for (Endpoint endpoint : ((ComponentReference)reference).getEndpoints()){
                                if ( endpoint.getSourceBinding() == binding){
                                    endpointsToCopyDown.add(endpoint);
                                    break;
                                }
                            }
                        } else {
                            // create a new endpoint to represent this promoted binding
                            Endpoint endpoint = endpointFactory.createEndpoint();
                            endpoint.setTargetName(binding.getURI());
                            endpoint.setSourceComponent(null); // TODO - fixed up at start
                            endpoint.setSourceComponentReference(promotedReference);  
                            endpoint.setInterfaceContract(reference.getInterfaceContract());
                            endpoint.setSourceBinding(binding);
                            endpointsToCopyDown.add(endpoint); 
                        }
                    }
                }
                
                if (bindingsToCopyDown.size() > 0) {
                    promotedReference.getBindings().clear();
                    promotedReference.getBindings().addAll(bindingsToCopyDown);
                    
                    promotedReference.getEndpoints().clear();
                    promotedReference.getEndpoints().addAll(endpointsToCopyDown);
                }
            }
            
            if (promotedReference.getBindings().size() > 1) {
                warning(monitor, "ComponentReferenceMoreWire", promotedReference, promotedReference.getName());                
            }
        } else {
            // if necessary merge the promoted endpoints (and bindings) with the top level bindings
            if (reference.getBindings().size() > 0 ){
                
                for (Binding binding : reference.getBindings()) {
                    if ((!(binding instanceof OptimizableBinding)) || binding.getURI() != null) {
                        promotedReference.getBindings().add(binding);
                        
                        if (reference instanceof ComponentReference){
                            for (Endpoint endpoint : ((ComponentReference)reference).getEndpoints()){
                                if ( endpoint.getSourceBinding() == binding){
                                    promotedReference.getEndpoints().add(endpoint);
                                    break;
                                }
                            }
                        } else {
                            // create a new endpoint to represent this promoted binding
                            Endpoint endpoint = endpointFactory.createEndpoint();
                            endpoint.setTargetName(binding.getURI());
                            endpoint.setSourceComponent(null); // TODO - fixed up at start
                            endpoint.setSourceComponentReference(promotedReference); 
                            endpoint.setInterfaceContract(reference.getInterfaceContract());
                            endpoint.setSourceBinding(binding);
                            promotedReference.getEndpoints().add(endpoint); 
                        }
                    }
                }                
            }            
        }
        
        Set<Binding> callbackBindings = new HashSet<Binding>();
        if (promotedReference.getCallback() != null) {
            callbackBindings.addAll(promotedReference.getCallback().getBindings());
        }
        if (reference.getCallback() != null) {
            callbackBindings.addAll(reference.getCallback().getBindings());
        }
        promotedReference.setCallback(assemblyFactory.createCallback());
        for (Binding binding : callbackBindings) {
            if ((!(binding instanceof OptimizableBinding)) || binding.getURI() != null) {
                promotedReference.getCallback().getBindings().add(binding);
            }
        }
    }

}
