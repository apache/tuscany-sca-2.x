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
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.endpointresolver.EndpointResolver;
import org.apache.tuscany.sca.endpointresolver.EndpointResolverFactory;
import org.apache.tuscany.sca.endpointresolver.EndpointResolverFactoryExtensionPoint;


/** 
 * The endpoint resolver allows unresolved endpoints to be plumbed into
 * the runtime start and message send processing as a hook to late resolution
 * of target services
 * 
 * @version $Rev$ $Date$
 */
public class EndpointResolverImpl implements EndpointResolver {

    private final static Logger logger = Logger.getLogger(EndpointResolverImpl.class.getName());

    private Endpoint endpoint;
    private List<EndpointResolver> endpointResolvers = new ArrayList<EndpointResolver>();    

    public EndpointResolverImpl(ExtensionPointRegistry extensionPoints,
                                Endpoint endpoint) {
        this.endpoint = endpoint;
        
        EndpointResolverFactoryExtensionPoint resolverFactories = 
            extensionPoints.getExtensionPoint(EndpointResolverFactoryExtensionPoint.class);
        
        for (Binding binding : endpoint.getCandidateBindings()){
            EndpointResolverFactory resolverFactory = resolverFactories.getEndpointResolverFactory(binding.getClass());
            
            // if the binding in question has a endpoint resolver factory they try and 
            // create an endpoint resolver
            if (resolverFactory != null){
                EndpointResolver resolver = resolverFactory.createEndpointResolver(endpoint, binding);
                
                if (resolver != null){
                    endpointResolvers.add(resolver);
                }
            }
        }
    }
    
    public void start(){
        // do nothing
    }
    
    public void stop(){
        // do nothing
    }    

    public void resolve() {
        if (endpoint.isUnresolved()){
            // Resolve the endpoint binding here
            
            // first do any general resolution that's required
            
            // ask the bindings to resolve the endpoint one by one
            for (EndpointResolver resolver : endpointResolvers){
                resolver.resolve();
                if (endpoint.isUnresolved() != true){
                    break;
                }
            }
            
            if (endpoint.isUnresolved()){
                // TODO: TUSCANY-2580: if its still unresolved use the first candidate binding
                endpoint.setSourceBinding(endpoint.getCandidateBindings().get(0));
                endpoint.getSourceBinding().setURI(endpoint.getTargetName());
            }

            if (endpoint.isUnresolved() != true){
                // If we have to build the endpoint because we are matching
                // intents and policies then we do that now. If the binding
                // is just configured by setting its uri we can just do local binding
                // configuration here
                
                // EndpointBuilderImpl.build(endpoint);
            } else {
                // raise an exception saying the endpoint can't be resolved
            }
        }
    }

}
