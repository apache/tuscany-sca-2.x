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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

public class EndpointRegistryImpl implements EndpointRegistry {
    private final Logger logger = Logger.getLogger(EndpointRegistryImpl.class.getName());

    static List<Endpoint2> endpoints = new ArrayList<Endpoint2>();
    static List<EndpointReference2> endpointreferences = new ArrayList<EndpointReference2>();

    private List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();

    public EndpointRegistryImpl(ExtensionPointRegistry extensionPoints) {
    }

    public void addEndpoint(Endpoint2 endpoint) {
        endpoints.add(endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(endpoint);
        }
        logger.info("EndpointRegistry: Add endpoint - " + endpoint.toString());
    }

    public void addEndpointReference(EndpointReference2 endpointReference) {
        endpointreferences.add(endpointReference);
        logger.info("EndpointRegistry: Add endpoint reference - " + endpointReference.toString());
    }

    /**
     * Parse the component/service/binding URI into an array of parts (componentURI, serviceName, bindingName)
     * @param uri
     * @return
     */
    private String[] parse(String uri) {
        String[] names = new String[3];
        int index = uri.lastIndexOf('#');
        if (index == -1) {
            names[0] = uri;
        } else {
            names[0] = uri.substring(0, index);
            String str = uri.substring(index + 1);
            if (str.startsWith("service-binding(") && str.endsWith(")")) {
                str = str.substring("service-binding(".length(), str.length() - 1);
                String[] parts = str.split("/");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid service-binding URI: " + uri);
                }
                names[1] = parts[0];
                names[2] = parts[1];
            } else if (str.startsWith("service(") && str.endsWith(")")) {
                str = str.substring("service(".length(), str.length() - 1);
                names[1] = str;
            } else {
                throw new IllegalArgumentException("Invalid component/service/binding URI: " + uri);
            }
        }
        return names;
    }

    private boolean matches(String target, String uri) {
        String[] parts1 = parse(target);
        String[] parts2 = parse(uri);
        for (int i = 0; i < parts1.length; i++) {
            if (parts1[i] == null || parts1[i].equals(parts2[i])) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public List<Endpoint2> findEndpoint(EndpointReference2 endpointReference) {
        List<Endpoint2> foundEndpoints = new ArrayList<Endpoint2>();

        logger.info("EndpointRegistry: Find endpoint for reference - " + endpointReference.toString());

        if (endpointReference.getReference() != null) {
            Endpoint2 targetEndpoint = endpointReference.getTargetEndpoint();
            for (Endpoint2 endpoint : endpoints) {
                // TODO: implement more complete matching
                if (matches(targetEndpoint.getURI(), endpoint.getURI())) {
                    foundEndpoints.add(endpoint);
                    logger.info("EndpointRegistry: Found endpoint with matching service  - " + endpoint);
                }
                // else the service name doesn't match
            }
        }

        return foundEndpoints;
    }

    public List<EndpointReference2> findEndpointReference(Endpoint2 endpoint) {
        return null;
    }

    public void removeEndpoint(Endpoint2 endpoint) {
        endpoints.remove(endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(endpoint);
        }
        logger.info("EndpointRegistry: Remove endpoint - " + endpoint.toString());
    }

    public void removeEndpointReference(EndpointReference2 endpointReference) {
        endpointreferences.remove(endpointReference);
        logger.info("EndpointRegistry: Remove endpoint reference - " + endpointReference.toString());
    }

    public List<EndpointReference2> getEndpointRefereneces() {
        return endpointreferences;
    }

    public List<Endpoint2> getEndpoints() {
        return endpoints;
    }

    public void addListener(EndpointListener listener) {
        listeners.add(listener);
    }

    public List<EndpointListener> getListeners() {
        return listeners;
    }

    public void removeListener(EndpointListener listener) {
        listeners.remove(listener);
    }

    public Endpoint2 getEndpoint(String uri) {
        for (Endpoint2 ep : endpoints) {
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

    public void updateEndpoint(String uri, Endpoint2 endpoint) {
        Endpoint2 oldEndpoint = getEndpoint(uri);
        if (oldEndpoint == null) {
            throw new IllegalArgumentException("Endpoint is not found: " + uri);
        }
        endpoints.remove(oldEndpoint);
        addEndpoint(endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEndpoint, endpoint);
        }
    }

}
