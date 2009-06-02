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

package org.apache.tuscany.sca.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.EndpointRegistry;

public class EndpointRegistryImpl implements EndpointRegistry {

    public static final EndpointRegistryImpl INSTANCE = new EndpointRegistryImpl();
    public static EndpointRegistryImpl getInstance() {
        return INSTANCE;
    }
    
    List<Endpoint2> endpoints = new ArrayList<Endpoint2>();
    List<EndpointReference2> endpointreferences = new ArrayList<EndpointReference2>();
    
    private EndpointRegistryImpl() {
    }
    
    public void addEndpoint(Endpoint2 endpoint) {
        endpoints.add(endpoint);
    }

    public void addEndpointReference(EndpointReference2 endpointReference) {
        endpointreferences.add(endpointReference);
    }

    public List<Endpoint2> findEndpoint(EndpointReference2 endpointReference) {
        List<Endpoint2> foundEndpoints = new ArrayList<Endpoint2>();
        if (endpointReference.getReference() != null) {
            List<ComponentService> targets = endpointReference.getReference().getTargets();
            if (targets != null) {
                for (ComponentService targetService : targets) {
                    for (Endpoint2 endpoint : endpoints) {
                        // TODO: implement more complete matching
                        if (endpoint.getComponent().getName().equals(targetService.getName())) {
                            foundEndpoints.add(endpoint);
                        }
                    }
                }
            }
        }
        return foundEndpoints;
    }

    public List<EndpointReference2> findEndpointReference(Endpoint2 endpoint) {
        return null;
    }

    public void removeEndpoint(Endpoint2 endpoint) {
        endpoints.remove(endpoint);
    }

    public void removeEndpointReference(EndpointReference2 endpointReference) {
        endpointreferences.remove(endpointReference);
    }

    public List<EndpointReference2> getEndpointRefereneces() {
        return endpointreferences;
    }

    public List<Endpoint2> getEndpoints() {
        return endpoints;
    }

}
