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
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.EndpointBuilder;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * A factory for the Endpoint model.
 * 
 * @version $Rev$ $Date$
 */
public abstract class EndpointBuilderImpl implements EndpointBuilder {
    
    public EndpointBuilderImpl (){
    }
    
    private void warning(Monitor monitor, String message, Object model, String... messageParameters) {
        if (monitor != null){
            Problem problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }
    
    private void error(Monitor monitor, String message, Object model, Exception ex) {
        if (monitor != null){
            Problem problem = null;
            problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }
    }

    /**
     * Resolve an endpoint against the provided target information and the 
     * set of candidate bindings. 
     */
    public void build(Endpoint endpoint, Monitor monitor) {

        // If this endpoint is not fully configured then don't try and resolve it
        if (endpoint.getTargetComponentService() == null){
            return;
        }
        
        // Does the reference expect callbacks
        boolean bidirectional = false;
        
        if (endpoint.getSourceComponentReference().getInterfaceContract() != null && 
            endpoint.getSourceComponentReference().getInterfaceContract().getCallbackInterface() != null) {
            bidirectional = true;
        }
        
        // if the target service is a promoted service then find the
        // service it promotes
        if (endpoint.getTargetComponentService().getService() instanceof CompositeService) {
            CompositeService compositeService = (CompositeService) endpoint.getTargetComponentService().getService();
            // Find the promoted component service
            ComponentService promotedComponentService = ServiceConfigurationUtil.getPromotedComponentService(compositeService);
            if (promotedComponentService != null && !promotedComponentService.isUnresolved()) {
                endpoint.setTargetComponentService(promotedComponentService);
            }
        }
        
        try  {
            PolicyConfigurationUtil.determineApplicableBindingPolicySets(endpoint.getSourceComponentReference(), 
                                                                         endpoint.getTargetComponentService());
        } catch ( Exception e ) {
            error(monitor, "PolicyRelatedException", endpoint, e);
        }    
        

        // Match the binding against the bindings of the target service
        Binding resolvedBinding = matchBinding(endpoint.getTargetComponent(),
                                               endpoint.getTargetComponentService(),
                                               endpoint.getCandidateBindings(),
                                               endpoint.getTargetComponentService().getBindings());
        if (resolvedBinding == null) {
            warning(monitor, "NoMatchingBinding", 
                    endpoint.getSourceComponentReference(),
                    endpoint.getSourceComponentReference().getName(), 
                    endpoint.getTargetComponentService().getName());
        } else {
            endpoint.setSourceBinding(resolvedBinding);
        }
        
        if (bidirectional) {
            Binding resolvedCallbackBinding = matchBinding(endpoint.getTargetComponent(),
                                                           endpoint.getTargetComponentService(),
                                                           endpoint.getSourceComponentReference().getCallback().getBindings(),
                                                           endpoint.getTargetComponentService().getCallback().getBindings());
            if (resolvedBinding == null) {
                warning(monitor, "NoMatchingCallbackBinding", 
                        endpoint.getSourceComponentReference(),
                        endpoint.getSourceComponentReference().getName(), 
                        endpoint.getTargetComponentService().getName());
            } else {
                endpoint.setSourceCallbackBinding(resolvedCallbackBinding);
            }
        }       
    }  
    
    private boolean hasCompatiblePolicySets(Binding refBinding, Binding svcBinding) {
        boolean isCompatible = true;
        if ( refBinding instanceof PolicySubject && svcBinding instanceof PolicySubject ) {
            //TODO : need to add more compatibility checks at the policy attachment levels
            for ( PolicySet svcPolicySet : ((PolicySubject)svcBinding).getPolicySets() ) {
                isCompatible = false;
                for ( PolicySet refPolicySet : ((PolicySubject)refBinding).getPolicySets() ) {
                    if ( svcPolicySet.equals(refPolicySet) ) {
                        isCompatible = true;
                        break;
                    }
                }
                //if there exists no matching policy set in the reference binding
                if ( !isCompatible ) {
                    return isCompatible;
                }
            }
        }
        return isCompatible;
    }
    
    
    private Binding matchBinding(Component targetComponent, ComponentService targetComponentService, List<Binding> source, List<Binding> target) {
        List<Binding> matched = new ArrayList<Binding>();
        // Find the corresponding bindings from the service side
        for (Binding binding : source) {
            for (Binding serviceBinding : target) {
                if (binding.getClass() == serviceBinding.getClass() && 
                    hasCompatiblePolicySets(binding, serviceBinding)) {

                    try {
                        Binding cloned = (Binding)binding.clone();
                        
                        //Customise the binding name to make it unique 
                        // regardless of how many bindings or targets there are
                        if ( targetComponent != null){
                            cloned.setName(binding.getName());
                        } else {
                            cloned.setName(binding.getName());
                        }
                        
                        // Set the binding URI to the URI of the target service
                        // that has been matched
                        if (binding.getURI() == null) {
                            cloned.setURI(serviceBinding.getURI());
                        }
                        
                        if (binding instanceof OptimizableBinding) {
                            OptimizableBinding endpoint = ((OptimizableBinding)cloned);
                            endpoint.setTargetComponent(targetComponent);
                            endpoint.setTargetComponentService(targetComponentService);
                            endpoint.setTargetBinding(serviceBinding);
                        } 
                           
                        matched.add(cloned);
                        break;
                    } catch (Exception ex) {
                        // do nothing 
                    }                   
                }
            }
        }
        if (matched.isEmpty()) {
            // No matching binding
            return null;
        } else {
            for (Binding binding : matched) {
                // If binding.sca is present, use it
                if (SCABinding.class.isInstance(binding)) {
                    return binding;
                }
            }
            // Use the first one
            return matched.get(0);
        }
    }    
    
}
