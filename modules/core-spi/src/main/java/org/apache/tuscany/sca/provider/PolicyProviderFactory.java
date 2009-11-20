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

package org.apache.tuscany.sca.provider;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * @version $Rev$ $Date$
 */
public interface PolicyProviderFactory<M> extends ProviderFactory<M> {
    /**
     * Create policy provider for a given reference binding
     * @param endpointReference The endpoint reference 
     * @return A policy provider for the endpoint reference
     */
    PolicyProvider createReferencePolicyProvider(EndpointReference endpointReference);

    /**
     * Create policy provider for a given service binding
     * @param endpoint The endpoint
     * @return A policy provider for the endpoint
     */
    PolicyProvider createServicePolicyProvider(Endpoint endpoint);

    /**
     * Create policy provider for a given component implementation
     * @param component
     * @return A policy provider for the implementation 
     */
    PolicyProvider createImplementationPolicyProvider(RuntimeComponent component);

}
