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
import org.apache.tuscany.sca.assembly.EndpointReference;
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
            Problem problem = monitor.createProblem(ReferenceConfigurationUtil.class.getName(), "assembly-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
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
     * Override the bindings for a promoted reference from an outer component reference
     * 
     * @param reference - the outer level reference
     * @param promotedReference - the inner level promoted reference
     */
    static void reconcileReferenceBindings(Reference reference,
                                           ComponentReference promotedReference,
                                           AssemblyFactory assemblyFactory,
                                           Monitor monitor) {
              
        if (reference.getEndpointReferences().size() > 0){
            if (promotedReference.getMultiplicity() == Multiplicity.ONE_ONE ||
                    promotedReference.getMultiplicity() == Multiplicity.ZERO_ONE) {
            	// Override any existing wires for 0..1 and 1..1 multiplicity
            	promotedReference.getEndpointReferences().clear();
            	// For 0..1 and 1..1, there should not be more than 1 endpoint reference
                if (reference.getEndpointReferences().size() > 1) {
                    warning(monitor, "ComponentReferenceMoreWire", promotedReference, promotedReference.getName());                
                } // end if
            } // end if
            // Clone the EndpointReferences from the outer level and add to the promoted reference
            for( EndpointReference epRef : reference.getEndpointReferences()){
            	EndpointReference epRefClone = copyHigherReference( epRef, promotedReference );
            	promotedReference.getEndpointReferences().add(epRefClone);
            } // end for
        } // end if
        
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
            } // end if
        } // end for
    } // end method reconcileReferenceBindings
    
    /**
     * Copy a higher level EndpointReference down to a lower level reference which it promotes 
     * @param epRef - the endpoint reference
     * @param promotedReference - the promoted reference
     * @return - a copy of the EndpointReference with data merged from the promoted reference
     */
    private static EndpointReference copyHigherReference( EndpointReference epRef, ComponentReference promotedReference ) {
    	EndpointReference epRefClone = null;
    	try {
    		epRefClone = (EndpointReference) epRef.clone();
    	} catch (Exception e) {
    		// Ignore (we know that EndpointReference2 can be cloned)
    	} // end try
    	// Copy across details of the inner reference
    	ComponentReference ref = epRefClone.getReference();
    	//FIXME
    	epRefClone.setReference(promotedReference);
    	return epRefClone;
    } // end copyHigherReference

} // end class
