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

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.builder.EndpointBuilder;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * A factory for the Endpoint model.
 * 
 * @version $Rev$ $Date$
 */
public abstract class EndpointBuilderImpl implements EndpointBuilder {
    
    private Monitor monitor;
    
    public EndpointBuilderImpl (Monitor monitor){
        this.monitor = monitor;
    }
    
    private void warning(String message, Object model, String... messageParameters) {
        Problem problem = new ProblemImpl(this.getClass().getName(), "assembly-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
        monitor.problem(problem);
    }    

    /**
     * Resolve an endpoint against the provided target information and the 
     * set of candidate bindings. 
     */
    public void build(Endpoint endpoint) {

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
            endpoint.setTargetComponentService(ServiceConfigurationUtil.getPromotedComponentService(compositeService));
        }
        
        try  {
            PolicyConfigurationUtil.determineApplicableBindingPolicySets(endpoint.getSourceComponentReference(), 
                                                                         endpoint.getTargetComponentService());
        } catch ( Exception e ) {
            warning("Policy related exception: " + e, e);
        }    
        

        // Match the binding against the bindings of the target service
        Binding resolvedBinding = BindingConfigurationUtil.matchBinding(endpoint.getTargetComponent(),
                                                                        endpoint.getTargetComponentService(),
                                                                        endpoint.getCandidateBindings(),
                                                                        endpoint.getTargetComponentService().getBindings());
        if (resolvedBinding == null) {
            warning("NoMatchingBinding", 
                    endpoint.getSourceComponentReference(),
                    endpoint.getSourceComponentReference().getName(), 
                    endpoint.getTargetComponentService().getName());
        } else {
            endpoint.setSourceBinding(resolvedBinding);
        }
        
        if (bidirectional) {
            Binding resolvedCallbackBinding = BindingConfigurationUtil.matchBinding(endpoint.getTargetComponent(),
                                                                                    endpoint.getTargetComponentService(),
                                                                                    endpoint.getSourceComponentReference().getCallback().getBindings(),
                                                                                    endpoint.getTargetComponentService().getCallback().getBindings());
            if (resolvedBinding == null) {
                warning("NoMatchingCallbackBinding", 
                        endpoint.getSourceComponentReference(),
                        endpoint.getSourceComponentReference().getName(), 
                        endpoint.getTargetComponentService().getName());
            } else {
                endpoint.setSourceCallbackBinding(resolvedCallbackBinding);
            }
        }       
    }  
    
}
