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
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
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


    public EndpointReferenceBuilderImpl(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;

        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);

        UtilityExtensionPoint utils = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utils.getUtility(InterfaceContractMapper.class);

        this.endpointRegistry = utils.getUtility(EndpointRegistry.class);
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
    public void runtimeBuild(EndpointReference endpointReference) {

        if ( endpointReference.getStatus() == EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED ||
             endpointReference.getStatus() == EndpointReference.RESOLVED_BINDING ) {
            // The endpoint reference is already resolved to either
            // a service endpoint local to this composite or it has
            // a remote binding
            
            // still need to check that the callback endpoint is set correctly
            if ((endpointReference.getCallbackEndpoint() != null) &&
                (endpointReference.getCallbackEndpoint().isUnresolved() == false)){
                return;
            }

            selectCallbackBinding(endpointReference);
            
        } else if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_FOUND_READY_FOR_MATCHING ){
            // The endpoint reference is already resolved to either
            // a service endpoint but no binding was specified in the 
            // target URL            

            // TODO - EPR - endpoint selection
            //              just use the first one
            endpointReference.setTargetEndpoint(endpointReference.getTargetEndpoint().getService().getEndpoints().get(0));
            
            selectForwardBinding(endpointReference);

            selectCallbackBinding(endpointReference);
            
        } else if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_NOT_FOUND ||
                   endpointReference.getStatus() == EndpointReference.NOT_CONFIGURED){
            // The service is in a remote composite somewhere else in the domain

            // find the service in the endpoint registry
            List<Endpoint> endpoints = endpointRegistry.findEndpoint(endpointReference);

            if (endpoints.size() == 0) {
                throw new SCARuntimeException("No endpoints found for EndpointReference " + endpointReference.toString());
            }
            
            // TODO - EPR - endpoint selection
            //              just use the first one
            endpointReference.setTargetEndpoint(endpoints.get(0));

            selectForwardBinding(endpointReference);

            selectCallbackBinding(endpointReference);
            
        } else {
            // endpointReference.getStatus() == EndpointReference.NOT_CONFIGURED
            // An error as we shouldn't get here
            throw new SCARuntimeException("EndpointReference can't be resolved " + endpointReference.toString());
        }

        if (endpointReference.getStatus() != EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED &&
            endpointReference.getStatus() != EndpointReference.RESOLVED_BINDING){
            throw new SCARuntimeException("EndpointReference can't be resolved " + endpointReference.toString());
        }
    }

    private void selectForwardBinding(EndpointReference endpointReference) {

        Endpoint endpoint = endpointReference.getTargetEndpoint();
        
        endpointReference.setBinding(endpointReference.getTargetEndpoint().getBinding());
        endpointReference.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
        endpointReference.setUnresolved(false);
        
        return;
    }

    private void selectCallbackBinding(EndpointReference endpointReference) {

        // if no callback on the interface or we are creating a self reference do nothing
        if (endpointReference.getReference().getInterfaceContract() == null ||
            endpointReference.getReference().getInterfaceContract().getCallbackInterface() == null ||
            endpointReference.getReference().getName().startsWith("$self$.")){
                return;
        }

        Endpoint endpoint = endpointReference.getTargetEndpoint();

        List<Endpoint> callbackEndpoints = endpointReference.getReference().getCallbackService().getEndpoints();
        
        endpointReference.setCallbackEndpoint(callbackEndpoints.get(0));
        endpointReference.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
        endpointReference.setUnresolved(false);
    }

}
