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
    protected Map<Object, DomainRegistry> domainRegistries = new ConcurrentHashMap<Object, DomainRegistry>();
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

    public synchronized DomainRegistry getEndpointRegistry(String endpointRegistryURI, String domainURI) {
        if (endpointRegistryURI == null) {
            endpointRegistryURI = domainURI;
        }

        Object key = getKey(endpointRegistryURI, domainURI);

        DomainRegistry domainRegistry = domainRegistries.get(key);
        if (domainRegistry != null) {
            return domainRegistry;
        }

        domainRegistry = createEndpointRegistry(endpointRegistryURI, domainURI);

        if (domainRegistry instanceof LifeCycleListener) {
            ((LifeCycleListener)domainRegistry).start();
        }

        for (EndpointListener listener : listeners) {
            domainRegistry.addEndpointListener(listener);
        }
        domainRegistries.put(key, domainRegistry);
        return domainRegistry;
    }

    protected Object getKey(String endpointRegistryURI, String domainURI) {
        return endpointRegistryURI + "," + domainURI;
    }

    protected abstract DomainRegistry createEndpointRegistry(String endpointRegistryURI, String domainURI);

    public void stop() {
        for (DomainRegistry domainRegistry : domainRegistries.values()) {
            if (domainRegistry instanceof LifeCycleListener) {
                ((LifeCycleListener)domainRegistry).stop();
            }
        }
        domainRegistries.clear();
        listeners.clear();
    }

    public synchronized Collection<DomainRegistry> getEndpointRegistries() {
        return new ArrayList<DomainRegistry>(domainRegistries.values());
    }

    public synchronized void addListener(EndpointListener listener) {
        listeners.add(listener);
        for (DomainRegistry registry : domainRegistries.values()) {
            registry.addEndpointListener(listener);
        }
    }

    public synchronized List<EndpointListener> getListeners() {
        return listeners;
    }

    public synchronized void removeListener(EndpointListener listener) {
        listeners.remove(listener);
        for (DomainRegistry registry : domainRegistries.values()) {
            registry.removeEndpointListener(listener);
        }
    }

}
