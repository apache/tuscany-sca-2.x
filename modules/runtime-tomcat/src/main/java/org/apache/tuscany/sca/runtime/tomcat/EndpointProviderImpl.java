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

package org.apache.tuscany.sca.runtime.tomcat;

import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.EndpointProvider;

/** 
 * The endpoint binding provider allows unresolved endpoints to be plumbed into
 * the runtime start and message send processing as a hook to late resolution
 * of target services
 */
public class EndpointProviderImpl implements EndpointProvider {

    private final static Logger logger = Logger.getLogger(EndpointProviderImpl.class.getName());

    private Endpoint endpoint;

    public EndpointProviderImpl(ExtensionPointRegistry extensionPoints,
                                Endpoint endpoint) {
        this.endpoint = endpoint;        
    }

    public void start() {
        if (endpoint.isUnresolved()){
            // Resolve the endpoint binding here
            
            logger.info("resolving endpoint: " + endpoint.getTargetName());
        }
    }

    public void stop() {
        if (!endpoint.isUnresolved()){
            // Currently the CompositeActivator stop() should take care of the providers and 
            // wires that have been added. 
        }
    }
}
