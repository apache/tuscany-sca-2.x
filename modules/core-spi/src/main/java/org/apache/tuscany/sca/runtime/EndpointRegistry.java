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

import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;

/**
 * The EndpointRegistry holds the active service endpoints for the SCA domain
 */
public interface EndpointRegistry {
    void addEndpoint(Endpoint2 endpoint);
    void removeEndpoint(Endpoint2 endpoint);

    Endpoint2 getEndpoint(String uri);
    void updateEndpoint(String uri, Endpoint2 endpoint);
    List<Endpoint2> findEndpoint(EndpointReference2 endpointReference);
    List<Endpoint2> getEndpoints();

    void addEndpointReference(EndpointReference2 endpointReference);
    void removeEndpointReference(EndpointReference2 endpointReference);
    List<EndpointReference2> findEndpointReference(Endpoint2 endpoint);
    List<EndpointReference2> getEndpointRefereneces();

    void addListener(EndpointListener listener);
    void removeListener(EndpointListener listener);
    List<EndpointListener> getListeners();

}
