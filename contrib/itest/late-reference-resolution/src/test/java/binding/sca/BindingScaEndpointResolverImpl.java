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

package binding.sca;


import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.endpointresolver.EndpointResolver;

import calculator.LateReferenceResolutionTestCase;

/** 
 * The endpoint resolver allows unresolved endpoints to be plumbed into
 * the runtime start and message send processing as a hook to late resolution
 * of target services
 * 
 * @version $Rev$ $Date$
 */
public class BindingScaEndpointResolverImpl implements EndpointResolver {

    private final static Logger logger = Logger.getLogger(BindingScaEndpointResolverImpl.class.getName());

    private Endpoint endpoint;
    private SCABinding binding;

    public BindingScaEndpointResolverImpl(ExtensionPointRegistry extensionPoints,
                                          Endpoint endpoint, 
                                          Binding binding) {
        this.endpoint = endpoint;
        this.binding = (SCABinding)binding;
    }

    public void start(){
        // do nothing
    } 
    
    public void resolve() {
        if (endpoint.isUnresolved()){
            // I can cheat here because I know this test only has 
            // SCA Bindings in it so all I have to do it find the URL 
            // of the endpoint
            String targetURL = LateReferenceResolutionTestCase.registry.locateService(endpoint.getTargetName());
            binding.setURI(targetURL);
            endpoint.setSourceBinding(binding);
        }
    }
    
    public void stop(){
        // do nothing
    }    
}
