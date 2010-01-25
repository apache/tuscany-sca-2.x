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

package org.apache.tuscany.sca.core.runtime;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

/**
 * The utility responsible for finding the endpoint regstry by the scheme and creating instances for the
 * given domain
 */
public class DefaultDomainRegistryFactory implements DomainRegistryFactory, LifeCycleListener {
    private ExtensionPointRegistry extensionRegistry;
    private Map<String, ServiceDeclaration> declarations = new HashMap<String, ServiceDeclaration>();
    private Map<String, EndpointRegistry> endpointRegistries = new ConcurrentHashMap<String, EndpointRegistry>();
    private List<EndpointListener> listeners = new ArrayList<EndpointListener>();

    /**
     * @param extensionRegistry
     */
    public DefaultDomainRegistryFactory(ExtensionPointRegistry extensionRegistry) {
        super();
        this.extensionRegistry = extensionRegistry;
    }

    public void start() {
        Collection<ServiceDeclaration> sds = null;
        try {
            sds = extensionRegistry.getServiceDiscovery().getServiceDeclarations(EndpointRegistry.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        for (ServiceDeclaration sd : sds) {
            String scheme = sd.getAttributes().get("scheme");
            if (scheme != null) {
                scheme = scheme.toLowerCase();
            }
            declarations.put(scheme, sd);
        }
    }

    public synchronized EndpointRegistry getEndpointRegistry(String endpointRegistryURI, String domainURI) {
        if (endpointRegistryURI == null) {
            endpointRegistryURI = domainURI;
        }
        
        String key;
        if (endpointRegistryURI.startsWith("tuscany:")){ 
            key = "tuscany:," + domainURI;
        } else {
            key = endpointRegistryURI + "," + domainURI;
        }

        EndpointRegistry endpointRegistry = endpointRegistries.get(key);
        if (endpointRegistry != null) {
            return endpointRegistry;
        }
        
        // see if its a tuscany: one (TODO: need to clean all this up)
        endpointRegistry = endpointRegistries.get("tuscany:," + domainURI);
        if (endpointRegistry != null) {
            return endpointRegistry;
        }

        URI uri = URI.create(endpointRegistryURI);
        String scheme = uri.getScheme();
        if (scheme != null) {
            scheme = scheme.toLowerCase();
        } else {
        	scheme = "vm";
        }

        ServiceDeclaration sd = declarations.get(scheme);

        try {
            Class<?> implClass = sd.loadClass();
            Constructor<?> constructor = null;
            try {
                constructor = implClass.getConstructor(ExtensionPointRegistry.class, String.class, String.class);
                endpointRegistry =
                    (EndpointRegistry)constructor.newInstance(extensionRegistry, endpointRegistryURI, domainURI);
            } catch (NoSuchMethodException e) {
                constructor =
                    implClass.getConstructor(ExtensionPointRegistry.class, Map.class, String.class, String.class);
                endpointRegistry =
                    (EndpointRegistry)constructor.newInstance(extensionRegistry,
                                                              sd.getAttributes(),
                                                              endpointRegistryURI,
                                                              domainURI);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        if (endpointRegistry instanceof LifeCycleListener) {
            ((LifeCycleListener)endpointRegistry).start();
        }
        
        for (EndpointListener listener : listeners) {
            endpointRegistry.addListener(listener);
        }
        endpointRegistries.put(key, endpointRegistry);
        return endpointRegistry;
    }

    public void stop() {
        declarations.clear();
        for (EndpointRegistry endpointRegistry : endpointRegistries.values()) {
            if (endpointRegistry instanceof LifeCycleListener) {
                ((LifeCycleListener)endpointRegistry).stop();
            }
        }
        endpointRegistries.clear();
        listeners.clear();
    }

    public synchronized Collection<EndpointRegistry> getEndpointRegistries() {
        return new ArrayList<EndpointRegistry>(endpointRegistries.values());
    }

    public synchronized void addListener(EndpointListener listener) {
        listeners.add(listener);
        for(EndpointRegistry registry: endpointRegistries.values()) {
            registry.addListener(listener);
        }
    }

    public synchronized List<EndpointListener> getListeners() {
        return listeners;
    }

    public synchronized void removeListener(EndpointListener listener) {
        listeners.remove(listener);
        for(EndpointRegistry registry: endpointRegistries.values()) {
            registry.removeListener(listener);
        }
    }
}
