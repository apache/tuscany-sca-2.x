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

package org.apache.tuscany.sca.binding.sca.axis2;

import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.endpoint.impl.EndpointProviderImpl;

/** 
 * The endpoint binding provider allows unresolved endpoints to be plumbed into
 * the runtime start and message send processing as a hook to late resolution
 * of target services
 * 
 * @version $Rev$ $Date$
 */
public class TestEndpointProviderImpl extends EndpointProviderImpl {

    private static final Logger logger = Logger.getLogger(TestEndpointProviderImpl.class.getName());

    private Endpoint endpoint;
    private int startedCount = 0;
    
    public TestEndpointProviderImpl(ExtensionPointRegistry extensionPoints,
                                    Endpoint endpoint) {
        super(extensionPoints, endpoint);    
        this.endpoint = endpoint;
    }
        
    public void start() {
        if (endpoint.isUnresolved()){
            
            if (startedCount == 1) {
                // pretend to resolve the endpoint. In this test case to what we know 
                // it should be
                logger.info("Fixing up endpoint soure binding");
                endpoint.setSourceBinding(endpoint.getCandidateBindings().get(0));
                endpoint.getSourceBinding().setURI("http://localhost:8085/BHelloWorldServiceRemote");
            } else {
                startedCount++;
            }
        }
    }   
    
}
