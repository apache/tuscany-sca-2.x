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

package org.apache.tuscany.sca.runtime;

import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;

/**
 * The EndpointRegistry holds the active service endpoints for the SCA domain
 */
public interface EndpointRegistry {
    /**
     * Add an enpoint to the registry. If the endpoint URI is the same as an existing endpoint in the registry,
     * the existing one will be updated
     * @param endpoint
     */
    void addEndpoint(Endpoint endpoint);
    
    /**
     * Remove an enpoint from the registry
     * @param endpoint
     */
    void removeEndpoint(Endpoint endpoint);

    /**
     * Look up an enpoint from the registry
     * @param uri The endpoint URI
     * @return
     */
    Endpoint getEndpoint(String uri);
    
    /**
     * Get all endpoints in the registry
     * @return
     */
    Collection<Endpoint> getEndpoints();
    
    List<Endpoint> findEndpoint(String uri);
    List<Endpoint> findEndpoint(EndpointReference endpointReference);

    void addEndpointReference(EndpointReference endpointReference);
    void removeEndpointReference(EndpointReference endpointReference);
    // List<EndpointReference> findEndpointReference(Endpoint endpoint);
    List<EndpointReference> getEndpointReferences();

    void addListener(EndpointListener listener);
    void removeListener(EndpointListener listener);

}
