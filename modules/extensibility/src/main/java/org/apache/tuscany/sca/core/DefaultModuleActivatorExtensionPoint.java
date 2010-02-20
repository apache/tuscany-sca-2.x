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

package org.apache.tuscany.sca.core;

import static org.apache.tuscany.sca.extensibility.ServiceHelper.newInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;

/**
 * Default implementation of an extension point to hold Tuscany module activators.
 *
 * @version $Rev$ $Date$
 */
public class DefaultModuleActivatorExtensionPoint implements ModuleActivatorExtensionPoint {
    private final static Logger logger = Logger.getLogger(DefaultModuleActivatorExtensionPoint.class.getName());
    private List<ModuleActivator> activators = new ArrayList<ModuleActivator>();
    private boolean loadedActivators;
    private boolean started;
    private ExtensionPointRegistry registry;

    /**
     * Constructs a new extension point.
     */
    public DefaultModuleActivatorExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public void addModuleActivator(ModuleActivator activator) {
        activators.add(activator);
    }

    public List<ModuleActivator> getModuleActivators() {
        loadModuleActivators();
        return activators;
    }

    public void removeModuleActivator(ModuleActivator activator) {
        if (activators.remove(activator)) {
            activator.stop();
        }
    }

    /**
     * Dynamically load module activators declared under META-INF/services
     */
    private synchronized void loadModuleActivators() {
        if (loadedActivators)
            return;

        // Get the activator service declarations
        Collection<ServiceDeclaration> activatorDeclarations;
        try {
            // Load the module activators by ranking
            activatorDeclarations = registry.getServiceDiscovery().getServiceDeclarations(ModuleActivator.class.getName(), true);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Load and instantiate module activators
        for (ServiceDeclaration activatorDeclaration : activatorDeclarations) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Loading " + activatorDeclaration.getClassName());
            }
            ModuleActivator activator = null;
            try {
                Class<ModuleActivator> activatorClass = (Class<ModuleActivator>)activatorDeclaration.loadClass();
                try {
                    activator = newInstance(activatorClass, ExtensionPointRegistry.class, registry);
                } catch (NoSuchMethodException e) {
                    try {
                        activator =
                            newInstance(activatorClass,
                                        new Class<?>[] {ExtensionPointRegistry.class, Map.class},
                                        registry,
                                        activatorDeclaration.getAttributes());

                    } catch (NoSuchMethodException e1) {
                        activator = newInstance(activatorClass);

                    }
                }
            } catch (Throwable e) {
                String optional = activatorDeclaration.getAttributes().get("optional");
                if ("true".equalsIgnoreCase(optional)) {
                    // If the optional flag is true, just log the error
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    continue;
                } else {
                    throw new IllegalArgumentException(e);
                }
            }
            addModuleActivator(activator);
        }

        loadedActivators = true;
    }

    public void start() {
        if (started) {
            return;
        }
        getModuleActivators();
        for (ModuleActivator activator : activators) {
            try {
                activator.start();
            } catch (Throwable e) {
                // Ignore the failing module for now
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        started = true;
    }

    public void stop() {
        if (!started) {
            return;
        }
        for (int i = activators.size() - 1; i >= 0; i--) {
            try {
                activators.get(i).stop();
            } catch (Throwable e) {
                // Ignore the failing module for now
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        started = false;
    }

}
