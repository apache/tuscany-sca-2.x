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

package org.apache.tuscany.sca.binding.sca.provider;

import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * SCABindingMapper allows us to map binding.sca to any of the bindings available to the runtime 
 */
public interface SCABindingMapper {

    /**
     * Map an endpoint with binding.sca to an endpoint
     * @param endpoint The endpoint for binding.sca
     * @return The endpoint for the mapped binding
     */
    public RuntimeEndpoint map(RuntimeEndpoint endpoint);

    /**
     * Map an endpoint reference with binding.sca to an endpoint reference with the mapped binding
     * @param endpointReference
     * @return The endpoint reference for the mapped binding
     */
    public RuntimeEndpointReference map(RuntimeEndpointReference endpointReference);
    
    /**
     * Check if the remote SCA binding is supported
     * @return 
     */
    boolean isRemotable();

}
