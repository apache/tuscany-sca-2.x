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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private MappedList<EndpointRegistry, Endpoint> endpoints = new MappedList<EndpointRegistry, Endpoint>();
    private MappedList<EndpointRegistry, EndpointReference> endpointreferences =
        new MappedList<EndpointRegistry, EndpointReference>();

    private List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();

    public EndpointRegistryImpl(ExtensionPointRegistry extensionPoints) {
    }

    public void addEndpoint(Endpoint endpoint) {
        endpoints.putValue(this, endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(endpoint);
        }
        logger.info("Add endpoint - " + endpoint.toString());
    }

    public void addEndpointReference(EndpointReference endpointReference) {
        endpointreferences.putValue(this, endpointReference);
        logger.info("Add endpoint reference - " + endpointReference.toString());
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

    public List<Endpoint> findEndpoint(EndpointReference endpointReference) {
        List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();

        logger.info("Find endpoint for reference - " + endpointReference.toString());

        if (endpointReference.getReference() != null) {
            Endpoint targetEndpoint = endpointReference.getTargetEndpoint();
            for (List<Endpoint> collection : endpoints.values()) {
                for (Endpoint endpoint : collection) {
                    // TODO: implement more complete matching
                    if (matches(targetEndpoint.getURI(), endpoint.getURI())) {
                        foundEndpoints.add(endpoint);
                        logger.info("Found endpoint with matching service  - " + endpoint);
                    }
                    // else the service name doesn't match
                }
            }
        }

        return foundEndpoints;
    }

    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        return null;
    }

    public void removeEndpoint(Endpoint endpoint) {
        endpoints.removeValue(this, endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(endpoint);
        }
        logger.info("Remove endpoint - " + endpoint.toString());
    }

    public void removeEndpointReference(EndpointReference endpointReference) {
        endpointreferences.removeValue(this, endpointReference);
        logger.info("Remove endpoint reference - " + endpointReference.toString());
    }

    public List<EndpointReference> getEndpointRefereneces() {
        return endpointreferences.getAllValues();
    }

    public List<Endpoint> getEndpoints() {
        return endpoints.getAllValues();
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

    public Endpoint getEndpoint(String uri) {
        for (List<Endpoint> collection : endpoints.values()) {
            for (Endpoint ep : collection) {
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
        }
        return null;

    }

    public void updateEndpoint(String uri, Endpoint endpoint) {
        Endpoint oldEndpoint = getEndpoint(uri);
        if (oldEndpoint == null) {
            throw new IllegalArgumentException("Endpoint is not found: " + uri);
        }
        endpoints.removeValue(this, oldEndpoint);
        endpoints.putValue(this, endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEndpoint, endpoint);
        }
    }

    public void start() {
    }

    public void stop() {
        List<Endpoint> localEndpoints = endpoints.remove(this);
        if (localEndpoints != null) {
            for (Endpoint endpoint : localEndpoints) {
                removeEndpoint(endpoint);
            }
        }
        List<EndpointReference> localEndpointReferences = endpointreferences.remove(this);
        if (localEndpointReferences != null) {
            for (EndpointReference endpointReference : localEndpointReferences) {
                removeEndpointReference(endpointReference);
            }
        }
        listeners.clear();
    }

    private static class MappedList<K, V> extends ConcurrentHashMap<K, List<V>> {
        private static final long serialVersionUID = -8926174610229029369L;

        public boolean putValue(K key, V value) {
            List<V> collection = get(key);
            if (collection == null) {
                collection = new ArrayList<V>();
                put(key, collection);
            }
            return collection.add(value);
        }

        public boolean putValue(K key, List<? extends V> value) {
            List<V> collection = get(key);
            if (collection == null) {
                collection = new ArrayList<V>();
                put(key, collection);
            }
            return collection.addAll(value);
        }

        public boolean removeValue(K key, V value) {
            List<V> collection = get(key);
            if (collection == null) {
                return false;
            }
            return collection.remove(value);
        }

        public List<V> getAllValues() {
            List<V> values = new ArrayList<V>();
            for (List<V> collection : values()) {
                values.addAll(collection);
            }
            return values;
        }

    }

}
