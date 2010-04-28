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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;

/**
 * The utility responsible for finding the endpoint regstry by the scheme and creating instances for the
 * given domain
 * @tuscany.spi.extension.inheritfrom
 */
public abstract class BaseDomainRegistryFactory implements DomainRegistryFactory, LifeCycleListener {
    protected ExtensionPointRegistry registry;
    protected Map<Object, EndpointRegistry> endpointRegistries = new ConcurrentHashMap<Object, EndpointRegistry>();
    protected List<EndpointListener> listeners = new ArrayList<EndpointListener>();

    /**
     * @param extensionRegistry
     */
    public BaseDomainRegistryFactory(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    public void start() {
    }

    public synchronized EndpointRegistry getEndpointRegistry(String endpointRegistryURI, String domainURI) {
        if (endpointRegistryURI == null) {
            endpointRegistryURI = domainURI;
        }

        Object key = getKey(endpointRegistryURI, domainURI);

        EndpointRegistry endpointRegistry = endpointRegistries.get(key);
        if (endpointRegistry != null) {
            return endpointRegistry;
        }

        endpointRegistry = createEndpointRegistry(endpointRegistryURI, domainURI);

        if (endpointRegistry instanceof LifeCycleListener) {
            ((LifeCycleListener)endpointRegistry).start();
        }

        for (EndpointListener listener : listeners) {
            endpointRegistry.addListener(listener);
        }
        endpointRegistries.put(key, endpointRegistry);
        return endpointRegistry;
    }

    protected Object getKey(String endpointRegistryURI, String domainURI) {
        return endpointRegistryURI + "," + domainURI;
    }

    protected abstract EndpointRegistry createEndpointRegistry(String endpointRegistryURI, String domainURI);

    public void stop() {
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
        for (EndpointRegistry registry : endpointRegistries.values()) {
            registry.addListener(listener);
        }
    }

    public synchronized List<EndpointListener> getListeners() {
        return listeners;
    }

    public synchronized void removeListener(EndpointListener listener) {
        listeners.remove(listener);
        for (EndpointRegistry registry : endpointRegistries.values()) {
            registry.removeListener(listener);
        }
    }

}
