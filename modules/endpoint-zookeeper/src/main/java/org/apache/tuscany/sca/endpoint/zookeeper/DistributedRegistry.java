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

package org.apache.tuscany.sca.endpoint.zookeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * 
 */
public class DistributedRegistry extends AbstractDistributedMap<Endpoint> implements EndpointRegistry,
    LifeCycleListener {

    private static final Logger logger = Logger.getLogger(DistributedRegistry.class.getName());
    private List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();
    private List<EndpointReference> endpointreferences = new CopyOnWriteArrayList<EndpointReference>();

    private ExtensionPointRegistry registry;
    private String domainURI;
    private String registryURI;
    private String hosts = null;
    private int sessionTimeout = 100;

    /**
     * 
     */
    public DistributedRegistry(ExtensionPointRegistry registry,
                               Map<String, String> attributes,
                               String domainRegistryURI,
                               String domainURI) {
        super(null, null, null);
        this.domainURI = domainURI;
        this.registryURI = domainRegistryURI;
        Map<String, String> config = parseURI(attributes, registryURI);
        hosts = config.get("hosts");
        String timeout = config.get("sessionTimeout");
        if (timeout != null) {
            sessionTimeout = Integer.parseInt(timeout.trim());
        }
    }

    public void start() {
        try {
            zooKeeper = new ZooKeeper(registryURI, sessionTimeout, null);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void stop() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                throw new ServiceRuntimeException(e);
            }
            zooKeeper = null;
        }
    }

    private Map<String, String> parseURI(Map<String, String> attributes, String domainRegistryURI) {
        Map<String, String> map = new HashMap<String, String>();
        if (attributes != null) {
            map.putAll(attributes);
        }
        // Should be zookeeper:host1:port1,host2:port2?sessionTimeout=100
        int index = domainRegistryURI.indexOf(':');
        String path = domainRegistryURI.substring(index + 1);

        index = path.indexOf('?');
        if (index == -1) {
            map.put("hosts", path);
            return map;
        }
        map.put("hosts", path.substring(0, index));
        String query = path.substring(index + 1);
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        String[] params = query.split("&");
        for (String param : params) {
            index = param.indexOf('=');
            if (index != -1) {
                map.put(param.substring(0, index), param.substring(index + 1));
            }
        }
        return map;
    }

    public void addEndpoint(Endpoint endpoint) {
        put(endpoint.getURI(), endpoint);
        logger.info("Add endpoint - " + endpoint);
    }

    public void addEndpointReference(EndpointReference endpointReference) {
        endpointreferences.add(endpointReference);
        logger.fine("Add endpoint reference - " + endpointReference);
    }

    public void addListener(EndpointListener listener) {
        listeners.add(listener);
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

        logger.fine("Find endpoint for reference - " + endpointReference);

        if (endpointReference.getReference() != null) {
            Endpoint targetEndpoint = endpointReference.getTargetEndpoint();
            String uri = targetEndpoint.getURI();
            lookup(foundEndpoints, uri);
        }
        return foundEndpoints;
    }

    private void lookup(List<Endpoint> foundEndpoints, String uri) {
        for (Object v : values()) {
            Endpoint endpoint = (Endpoint)v;
            // TODO: implement more complete matching
            logger.fine("Matching against - " + endpoint);
            if (matches(uri, endpoint.getURI())) {
                // if (!entry.isPrimary()) {
                ((RuntimeEndpoint)endpoint).bind(registry, this);
                // }
                foundEndpoints.add(endpoint);
                logger.fine("Found endpoint with matching service  - " + endpoint);
            }
            // else the service name doesn't match
        }
    }

    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        return endpointreferences;
    }

    public Endpoint getEndpoint(String uri) {
        return get(uri);
    }

    public List<EndpointReference> getEndpointRefereneces() {
        return endpointreferences;
    }

    public Collection<Endpoint> getEndpoints() {
        return new ArrayList<Endpoint>(values());
    }

    public List<EndpointListener> getListeners() {
        return listeners;
    }

    public void removeEndpoint(Endpoint endpoint) {
        remove(endpoint.getURI());
        logger.info("Remove endpoint - " + endpoint);
    }

    public void removeEndpointReference(EndpointReference endpointReference) {
        endpointreferences.remove(endpointReference);
        logger.fine("Remove endpoint reference - " + endpointReference);
    }

    public void removeListener(EndpointListener listener) {
        listeners.remove(listener);
    }

    public void updateEndpoint(String uri, Endpoint endpoint) {
        Endpoint oldEndpoint = getEndpoint(uri);
        if (oldEndpoint == null) {
            throw new IllegalArgumentException("Endpoint is not found: " + uri);
        }
        put(endpoint.getURI(), endpoint);
    }

    public void entryAdded(Endpoint value) {
        ((RuntimeEndpoint)value).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(value);
        }
    }

    public void entryRemoved(Endpoint value) {
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(value);
        }
    }

    public void entryUpdated(Endpoint oldEp, Endpoint newEp) {
        ((RuntimeEndpoint)newEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEp, newEp);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        super.process(event);
        String path = event.getPath();
        if (path == null || !path.startsWith(getPath(root))) {
            return;
        }
        switch (event.getType()) {
            case NodeChildrenChanged:
                break;
            case NodeCreated:
            case NodeDataChanged:
                Endpoint ep = getData(path);
                entryAdded(ep);
                break;
            case NodeDeleted:
                entryRemoved(null);
                break;
        }
    }

    public List<EndpointReference> getEndpointReferences() {
        return endpointreferences;
    }

    public List<Endpoint> findEndpoint(String uri) {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        lookup(endpoints, uri);
        return endpoints;
    }

}
