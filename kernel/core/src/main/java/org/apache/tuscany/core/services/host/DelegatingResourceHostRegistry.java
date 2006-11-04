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

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
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
    private static final String SCA_LOCALHOST_PREFIX = "SCA://localhost/";
    private Map<String, ResourceHost> resourceHosts = new HashMap<String, ResourceHost>();
    private CompositeComponent parent;

    /**
     * Creates a new delegating registry.
     *
     * @param parent the composite to resolve SCA resources against
     */
    public DelegatingResourceHostRegistry(@Autowire CompositeComponent parent) {
        this.parent = parent;
    }

    public void register(String uri, ResourceHost host) {
        resourceHosts.put(uri, host);
    }

    public void unregister(String uri) {
        resourceHosts.remove(uri);
    }

    public <T> T resolveResource(Class<T> type) throws ResourceResolutionException {
        T instance = parent.resolveSystemExternalInstance(type);
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
            String name;
            if (mappedName.startsWith(SCA_LOCALHOST_PREFIX)) {
                name = mappedName.substring(SCA_LOCALHOST_PREFIX.length());
            } else {
                name = mappedName.substring(SCA_PREFIX.length());
            }
            // resolve against the composite
            SCAObject child = parent.getSystemChild(name);
            // only expose services
            if (child instanceof org.apache.tuscany.spi.component.Service) {
                return type.cast(child.getServiceInstance());
            }
            return null;
        } else {
            int pos = mappedName.indexOf("://");
            if (pos == -1) {
                ResourceResolutionException e = new ResourceResolutionException("Invalid resource URI");
                e.setIdentifier(mappedName);
                throw e;
            }
            String uri = mappedName.substring(0, pos + 3);
            ResourceHost host = resourceHosts.get(uri);
            if (host == null) {
                ResourceResolutionException e = new ResourceResolutionException("No resource host for URI");
                e.setIdentifier(uri);
                throw e;
            }
            return host.resolveResource(type, mappedName);
        }
    }
}
