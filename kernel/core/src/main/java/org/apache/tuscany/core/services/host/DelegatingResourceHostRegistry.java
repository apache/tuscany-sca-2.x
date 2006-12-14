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
package org.apache.tuscany.core.services.host;

import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.host.ResourceHostRegistry;
import org.apache.tuscany.spi.host.ResourceResolutionException;

/**
 * The default implementation of a <code>ResourceRegisty</code> that resolves resources in the <code>SCA://</code>
 * namespace against its parent composite and delegates resolution to registered <code>ResourceHost</code>s for other
 * namespaces. The search order for resources resolved by type starts with the SCA namespace and proceeds to hosts in
 * the order they were registered.
 *
 * @version $Rev$ $Date$
 */
@Service(interfaces = {ResourceHost.class, ResourceHostRegistry.class})
public class DelegatingResourceHostRegistry implements ResourceHost, ResourceHostRegistry {
    private static final String SCA_PREFIX = "SCA://";
    private Map<String, ResourceHost> resourceHosts = new HashMap<String, ResourceHost>();
    private Map<Class<?>, Object> systemResources = new HashMap<Class<?>, Object>();
    private Map<Key, Object> mappedSystemResources = new HashMap<Key, Object>();

    public DelegatingResourceHostRegistry() {
    }

    public void registerResourceHost(String uri, ResourceHost host) {
        resourceHosts.put(uri, host);
    }

    public void unregisterResourceHost(String uri) {
        resourceHosts.remove(uri);
    }

    public void registerResource(Class<?> type, Object resource) {
        systemResources.put(type, resource);
    }

    public void registerResource(Class<?> type, String name, Object resource) {
        mappedSystemResources.put(new Key(type, name), resource);
    }

    public void unregisterResource(Class<?> type, String name) {
        mappedSystemResources.remove(new Key(type, name));
    }

    public void unregisterResource(Class<?> type) {
        systemResources.remove(type);
    }

    public <T> T resolveResource(Class<T> type) throws ResourceResolutionException {
        T instance = type.cast(systemResources.get(type));
        if (instance == null) {
            for (ResourceHost host : resourceHosts.values()) {
                instance = host.resolveResource(type);
                if (instance != null) {
                    return instance;
                }
            }
        }
        return instance;
    }

    public <T> T resolveResource(Class<T> type, String mappedName) throws ResourceResolutionException {
        if (mappedName.startsWith(SCA_PREFIX)) {
            String name = mappedName.substring(SCA_PREFIX.length());
            return type.cast(mappedSystemResources.get(new Key(type, name)));
        } else {
            int pos = mappedName.indexOf("://");
            if (pos == -1) {
                return type.cast(mappedSystemResources.get(new Key(type, mappedName)));
            }
            String uri = mappedName.substring(0, pos + 3);
            ResourceHost host = resourceHosts.get(uri);
            if (host == null) {
                throw new ResourceResolutionException("No resource host for URI", uri);
            }
            return host.resolveResource(type, mappedName);
        }
    }

    private class Key {
        private Class<?> clazz;
        private String name;

        public Key(Class<?> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        public Key(Class<?> clazz) {
            this.clazz = clazz;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key key = (Key) o;

            if (clazz != null ? !clazz.equals(key.clazz) : key.clazz != null) {
                return false;
            }
            if (name != null ? !name.equals(key.name) : key.name != null) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int result;
            result = clazz != null ? clazz.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
