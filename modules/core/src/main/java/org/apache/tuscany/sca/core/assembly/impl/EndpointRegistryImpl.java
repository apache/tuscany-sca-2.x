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

package org.apache.tuscany.sca.core.assembly.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.runtime.BaseEndpointRegistry;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

/**
 * A EndpointRegistry implementation that sees registrations from the same JVM
 */
public class EndpointRegistryImpl extends BaseEndpointRegistry implements EndpointRegistry, LifeCycleListener {
    private final Logger logger = Logger.getLogger(EndpointRegistryImpl.class.getName());

    private List<Endpoint> endpoints = new ArrayList<Endpoint>();

    public EndpointRegistryImpl(ExtensionPointRegistry extensionPoints, String endpointRegistryURI, String domainURI) {
        super(extensionPoints, null, endpointRegistryURI, domainURI);
    }

    public synchronized void addEndpoint(Endpoint endpoint) {
        endpoints.add(endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(endpoint);
        }
        logger.info("Add endpoint - " + endpoint.toString());
    }

    public List<Endpoint> findEndpoint(String uri) {
        List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();
        for (Endpoint endpoint : endpoints) {
            if (matches(uri, endpoint.getURI())) {
                foundEndpoints.add(endpoint);
                logger.fine("Found endpoint with matching service  - " + endpoint);
            }
            // else the service name doesn't match
        }
        return foundEndpoints;
    }
    
    public synchronized void removeEndpoint(Endpoint endpoint) {
        endpoints.remove(endpoint);
        endpointRemoved(endpoint);
    }

    public synchronized List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public synchronized Endpoint getEndpoint(String uri) {
        for (Endpoint ep : endpoints) {
            String epURI =
                ep.getComponent().getURI() + "#" + ep.getService().getName() + "/" + ep.getBinding().getName();
            if (epURI.equals(uri)) {
                return ep;
            }
            if (ep.getBinding().getName() == null || ep.getBinding().getName().equals(ep.getService().getName())) {
                epURI = ep.getComponent().getURI() + "#" + ep.getService().getName();
                if (epURI.equals(uri)) {
                    return ep;
                }
            }
        }
        return null;

    }

    public synchronized void updateEndpoint(String uri, Endpoint endpoint) {
        Endpoint oldEndpoint = getEndpoint(uri);
        if (oldEndpoint == null) {
            throw new IllegalArgumentException("Endpoint is not found: " + uri);
        }
        endpoints.remove(oldEndpoint);
        endpoints.add(endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEndpoint, endpoint);
        }
    }

    public synchronized void start() {
    }

    public synchronized void stop() {
        for (Iterator<Endpoint> i = endpoints.iterator(); i.hasNext();) {
            Endpoint ep = i.next();
            i.remove();
            endpointRemoved(ep);
        }
        endpointreferences.clear();
        listeners.clear();
    }

}
