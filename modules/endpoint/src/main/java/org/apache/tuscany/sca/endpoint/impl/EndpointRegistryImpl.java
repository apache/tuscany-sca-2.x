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

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.EndpointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

public class EndpointRegistryImpl implements EndpointRegistry {

    //public static final EndpointRegistryImpl INSTANCE = new EndpointRegistryImpl();
    //public static EndpointRegistryImpl getInstance() {
    //    return INSTANCE;
    //}
    
    static List<Endpoint2> endpoints = new ArrayList<Endpoint2>();
    static List<EndpointReference2> endpointreferences = new ArrayList<EndpointReference2>();
    
    public EndpointRegistryImpl(ExtensionPointRegistry extensionPoints) {
    }
    
    public void addEndpoint(Endpoint2 endpoint) {
        endpoints.add(endpoint);
        System.out.println("EndpointRegistry: Add endpoint - " + endpoint.toString());
    }

    public void addEndpointReference(EndpointReference2 endpointReference) {
        endpointreferences.add(endpointReference);
        System.out.println("EndpointRegistry: Add endpoint reference - " + endpointReference.toString());
    }

    public List<Endpoint2> findEndpoint(EndpointReference2 endpointReference) {
        List<Endpoint2> foundEndpoints = new ArrayList<Endpoint2>();
        
        System.out.println("EndpointRegistry: Find endpoint for reference - " + endpointReference.toString());
        
        if (endpointReference.getReference() != null) {
            Endpoint2 targetEndpoint = endpointReference.getTargetEndpoint();
            for (Endpoint2 endpoint : endpoints) {
                // TODO: implement more complete matching
                if (endpoint.getComponentName().equals(targetEndpoint.getComponentName())) {
                    if ( (targetEndpoint.getServiceName() != null) && 
                         (endpoint.getServiceName().equals(targetEndpoint.getServiceName())) ){
                        foundEndpoints.add(endpoint);
                        System.out.println("EndpointRegistry: Found endpoint with matching service  - " + endpoint.toString());
                    } else if (targetEndpoint.getServiceName() == null) {
                        foundEndpoints.add(endpoint);
                        System.out.println("EndpointRegistry: Found endpoint with matching component  - " + endpoint.toString());
                    }
                    // else the service name doesn't match
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
        System.out.println("EndpointRegistry: Remove endpoint - " + endpoint.toString());
    }

    public void removeEndpointReference(EndpointReference2 endpointReference) {
        endpointreferences.remove(endpointReference);
        System.out.println("EndpointRegistry: Remove endpoint reference - " + endpointReference.toString());
    }

    public List<EndpointReference2> getEndpointRefereneces() {
        return endpointreferences;
    }

    public List<Endpoint2> getEndpoints() {
        return endpoints;
    }

}
