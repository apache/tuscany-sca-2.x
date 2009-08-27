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

package org.apache.tuscany.sca.endpoint.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.EndpointReferenceBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.oasisopen.sca.SCARuntimeException;

/**
 * An builder that takes endpoint references and resolves them. It either finds local
 * service endpoints if they are available or asks the domain. The main function here
 * is to perform binding and policy matching.
 * This is a separate builder in case it is required by undresolved endpoints
 * once the runtime has started.
 *
 * @version $Rev$ $Date$
 */
public class EndpointReferenceBuilderImpl implements EndpointReferenceBuilder {

    protected ExtensionPointRegistry extensionPoints;
    protected AssemblyFactory assemblyFactory;
    protected InterfaceContractMapper interfaceContractMapper;
    protected EndpointRegistry endpointRegistry;
    private Monitor monitor;


    public EndpointReferenceBuilderImpl(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;

        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);

        UtilityExtensionPoint utils = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utils.getUtility(InterfaceContractMapper.class);
        this.endpointRegistry = utils.getUtility(EndpointRegistry.class);
        MonitorFactory monitorFactory = utils.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
    }
    
    /**
     * Build a composite
     *
     * @param endpoint
     * @param monitor
     */
    public void buildtimeBuild(Composite composite) { 
        // TODO - ready for reorganization of the builders
        //        build all the endpoint references in a composite
        //        that it is possible to build in order to get any
        //        errors out as early as possible. Any that can't
        //        be built now must wait until runtime
    }

    /**
     * Build a single endpoint reference
     *
     * @param endpoint
     * @param monitor
     */
    public Problem runtimeBuild(EndpointReference endpointReference) {
        
        Problem problem = null;

        if ( endpointReference.getStatus() == EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED ||
             endpointReference.getStatus() == EndpointReference.RESOLVED_BINDING ) {
            // The endpoint reference is already resolved to either
            // a service endpoint local to this composite or it has
            // a remote binding
            
            // still need to check that the callback endpoint is set correctly
            if (hasCallback(endpointReference) &&
                endpointReference.getCallbackEndpoint() == null &&
                endpointReference.getCallbackEndpoint().isUnresolved() == true ){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            } 
        } else if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_FOUND_READY_FOR_MATCHING ){
            // The endpoint reference is already resolved to either
            // a service endpoint but no binding was specified in the 
            // target URL and/or the policies have yet to be matched.         
            
            problem = selectForwardEndpoint(endpointReference,
                                            endpointReference.getTargetEndpoint().getService().getEndpoints());

            if (problem == null && hasCallback(endpointReference)){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            } 
            
        } else if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_NOT_FOUND ||
                   endpointReference.getStatus() == EndpointReference.NOT_CONFIGURED){
            // The service is in a remote composite somewhere else in the domain

            // find the service in the endpoint registry
            List<Endpoint> endpoints = endpointRegistry.findEndpoint(endpointReference);

            if (endpoints.size() == 0) {
                problem = monitor.createProblem(this.getClass().getName(), 
                                                "endpoint-validation-messages", 
                                                Problem.Severity.ERROR, 
                                                this, 
                                                "NoEndpointsFound", 
                                                endpointReference.toString());
            }

            problem = selectForwardEndpoint(endpointReference,
                                            endpoints);

            if (problem == null && hasCallback(endpointReference)){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            }             
        } 
        
        if (problem != null){
            return problem;
        }

        if (endpointReference.getStatus() != EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED &&
            endpointReference.getStatus() != EndpointReference.RESOLVED_BINDING){
            problem = monitor.createProblem(this.getClass().getName(), 
                                            "endpoint-validation-messages", 
                                            Problem.Severity.ERROR, 
                                            this, 
                                            "EndpointReferenceCantBeMatched", 
                                            endpointReference.toString());
        }
        
        return problem;
    }

    private Problem selectForwardEndpoint(EndpointReference endpointReference, List<Endpoint> endpoints) {    
             
        Endpoint matchedEndpoint = null;
        
        if (endpointReference.getReference().getName().startsWith("$self$.")){
            // just select the first one and don't do any policy matching
            matchedEndpoint = endpoints.get(0);
        } else {
            // find the first endpoint that matches this endpoint reference
            for (Endpoint endpoint : endpoints){
                if (haveMatchingPolicy(endpointReference, endpoint)){
                    matchedEndpoint = endpoint;
                }
            }
        }
        
        if (matchedEndpoint == null){
            return null;
        }
        
        endpointReference.setTargetEndpoint(matchedEndpoint);
        endpointReference.setBinding(endpointReference.getTargetEndpoint().getBinding());
        endpointReference.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
        endpointReference.setUnresolved(false);
        
        return null;
    }
    
    private boolean hasCallback(EndpointReference endpointReference){
        if (endpointReference.getReference().getInterfaceContract() == null ||
            endpointReference.getReference().getInterfaceContract().getCallbackInterface() == null ||
            endpointReference.getReference().getName().startsWith("$self$.")){
            return false;
        } else {
            return true;
        }
    }

    private Problem selectCallbackEndpoint(EndpointReference endpointReference, List<Endpoint> endpoints) {

        Problem problem = null;
        
        // find the first callback endpoint that matches a callback endpoint reference
        // at the service
        Endpoint matchedEndpoint = null;
        match:
        for ( EndpointReference callbackEndpointReference : endpointReference.getTargetEndpoint().getCallbackEndpointReferences()){
            for (Endpoint endpoint : endpoints){
                if (haveMatchingPolicy(callbackEndpointReference, endpoint)){
                    matchedEndpoint = endpoint;
                    break match;
                }
            }
        }
        
        if (matchedEndpoint == null){
            return null;
        }
        
        endpointReference.setCallbackEndpoint(matchedEndpoint);
        
        return problem;
    }
    
    private boolean haveMatchingPolicy(EndpointReference endpointReference, Endpoint endpoint){
        
        // if no policy sets or intents are present then they match
        if ((endpointReference.getRequiredIntents().size() == 0) &&
            (endpoint.getRequiredIntents().size() == 0) &&
            (endpointReference.getPolicySets().size() == 0) &&
            (endpoint.getPolicySets().size() == 0)) {
            return true;
        }
        
        // if there are different numbers of intents 
        // then they don't match
        if (endpointReference.getRequiredIntents().size() !=
            endpoint.getRequiredIntents().size()) {
            return false;
        }
        
        // if there are different numbers of policy sets 
        // then they don't match
        if (endpointReference.getPolicySets().size() !=
            endpoint.getPolicySets().size()) {
            return false;
        }        
        
        // check intents for compatibility
        for(Intent intentEPR : endpointReference.getRequiredIntents()){
            boolean matched = false;
            for (Intent intentEP : endpoint.getRequiredIntents()){ 
                if (intentEPR.getName().equals(intentEP.getName())){
                    matched = true;
                    break;
                }
            }
            if (matched == false){
                return false;
            }
        }
        
        // check policy sets for compatibility. The list of policy sets
        // may be a subset of the list of intents as some of the intents 
        // may be directly provided. We can't just rely on intent compatibility
        // as different policy sets might have been attached at each end to 
        // satisfy the listed intents
        
        // if all of the policy sets on the endpoint reference match a 
        // policy set on the endpoint then they match
        for(PolicySet policySetEPR : endpointReference.getPolicySets()){
            boolean matched = false;
            for (PolicySet policySetEP : endpoint.getPolicySets()){ 
                // find if there is a policy set with the same name
                if (policySetEPR.getName().equals(policySetEP.getName())){
                    matched = true;
                    break;
                }
                // find if the policies inside the policy set match the 
                // policies inside a policy set on the endpoint
                
                // TODO - need a policy specific matcher to do this
                //        so need a new extension point
                
            }
            
            if (matched == false){
                return false;
            }
        }
        
        return true;
    }

}
