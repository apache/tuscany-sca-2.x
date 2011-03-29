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

package org.apache.tuscany.sca.host.http.extensibility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceHelper;

public class DefaultHttpPortAllocatorExtensionPoint implements HttpPortAllocatorExtensionPoint {
    private final static Logger logger = Logger.getLogger(DefaultHttpPortAllocatorExtensionPoint.class.getName());

    private ExtensionPointRegistry registry;
    private List<HttpPortAllocator> portAllocators = new ArrayList<HttpPortAllocator>();
    private boolean loaded;

    public DefaultHttpPortAllocatorExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }
    public void addPortAllocators(HttpPortAllocator httpPortAllocator) {
        this.portAllocators.add(httpPortAllocator);
    }

    public void removePortAllocators(HttpPortAllocator httpPortAllocator) {
        this.portAllocators.remove(httpPortAllocator);
    }

    public List<HttpPortAllocator> getPortAllocators() {
        loadServletHosts();
        return this.portAllocators;
    }

    private synchronized void loadServletHosts() {
        if (loaded)
            return;

        // Get the activator service declarations
        Collection<ServiceDeclaration> activatorDeclarations;
        try {
            // Load the port allocators by ranking
            activatorDeclarations = registry.getServiceDiscovery().getServiceDeclarations(HttpPortAllocator.class.getName(), true);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Load and instantiate http port allocators
        for (ServiceDeclaration allocatorDeclaration : activatorDeclarations) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Loading " + allocatorDeclaration.getClassName());
            }
            HttpPortAllocator allocator = null;
            try {
                Class<HttpPortAllocator> allocatorClass = (Class<HttpPortAllocator>)allocatorDeclaration.loadClass();
                try {
                    allocator = ServiceHelper.newInstance(allocatorClass, ExtensionPointRegistry.class, registry);
                } catch (NoSuchMethodException e) {
                    try {
                        allocator =
                            ServiceHelper.newInstance(allocatorClass,
                                          new Class<?>[] {ExtensionPointRegistry.class, Map.class},
                                          registry,
                                          allocatorDeclaration.getAttributes());

                    } catch (NoSuchMethodException e1) {
                        allocator = ServiceHelper.newInstance(allocatorClass);

                    }
                }
            } catch (Throwable e) {
                String optional = allocatorDeclaration.getAttributes().get("optional");
                if ("true".equalsIgnoreCase(optional)) {
                    // If the optional flag is true, just log the error
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    continue;
                } else {
                    throw new IllegalArgumentException(e);
                }
            }
            addPortAllocators(allocator);
        }

        loaded = true;
    }
}
