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
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

/**
 * A EndpointRegistry implementation that sees registrations from the same JVM
 */
public class EndpointRegistryImpl implements EndpointRegistry, LifeCycleListener {
    private final Logger logger = Logger.getLogger(EndpointRegistryImpl.class.getName());

    private List<Endpoint> endpoints = new ArrayList<Endpoint>();
    private List<EndpointReference> endpointreferences = new ArrayList<EndpointReference>();
    private List<EndpointListener> listeners = new ArrayList<EndpointListener>();

    public EndpointRegistryImpl(ExtensionPointRegistry extensionPoints, String endpointRegistryURI, String domainURI) {
    }

    public synchronized void addEndpoint(Endpoint endpoint) {
        endpoints.add(endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(endpoint);
        }
        logger.info("Add endpoint - " + endpoint.toString());
    }

    public synchronized void addEndpointReference(EndpointReference endpointReference) {
        endpointreferences.add(endpointReference);
        logger.fine("Add endpoint reference - " + endpointReference.toString());
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

    public synchronized List<Endpoint> findEndpoint(EndpointReference endpointReference) {
        List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();

        logger.fine("Find endpoint for reference - " + endpointReference.toString());

        if (endpointReference.getReference() != null) {
            Endpoint targetEndpoint = endpointReference.getTargetEndpoint();
            foundEndpoints.addAll(findEndpoint(targetEndpoint.getURI()));
        }

        return foundEndpoints;
    }
    
    protected List<Endpoint> findEndpoint(String uri) {
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
    

    public synchronized List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        return null;
    }

    public synchronized void removeEndpoint(Endpoint endpoint) {
        endpoints.remove(endpoint);
        endpointRemoved(endpoint);
    }

    private void endpointRemoved(Endpoint endpoint) {
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(endpoint);
        }
        logger.info("Remove endpoint - " + endpoint.toString());
    }

    public synchronized void removeEndpointReference(EndpointReference endpointReference) {
        endpointreferences.remove(endpointReference);
        logger.fine("Remove endpoint reference - " + endpointReference.toString());
    }

    public synchronized List<EndpointReference> getEndpointReferences() {
        return endpointreferences;
    }

    public synchronized List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public synchronized void addListener(EndpointListener listener) {
        listeners.add(listener);
    }

    public synchronized List<EndpointListener> getListeners() {
        return listeners;
    }

    public synchronized void removeListener(EndpointListener listener) {
        listeners.remove(listener);
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
