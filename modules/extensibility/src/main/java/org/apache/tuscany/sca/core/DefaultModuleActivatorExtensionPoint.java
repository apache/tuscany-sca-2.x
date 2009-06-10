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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Default implementation of an extension point to hold Tuscany module activators.
 *
 * @version $Rev$ $Date$
 */
public class DefaultModuleActivatorExtensionPoint implements ModuleActivatorExtensionPoint {
    private final static Logger logger = Logger.getLogger(DefaultModuleActivatorExtensionPoint.class.getName());
    private List<ModuleActivator> activators = new ArrayList<ModuleActivator>();
    private boolean loadedActivators;

    /**
     * Constructs a new extension point.
     */
    public DefaultModuleActivatorExtensionPoint() {
    }

    public void addModuleActivator(ModuleActivator activator) {
        activators.add(activator);
    }

    public List<ModuleActivator> getModuleActivators() {
        loadModuleActivators();
        return activators;
    }

    public void removeModuleActivator(Object activator) {
        activators.remove(activator);
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
            activatorDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(ModuleActivator.class.getName(), true);
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
                Constructor<ModuleActivator> constructor = null;
                try {
                    constructor = activatorClass.getConstructor();
                    activator = constructor.newInstance();
                } catch (NoSuchMethodException e) {
                    // Try the one that takes a Map<String, String>
                    constructor = activatorClass.getConstructor(Map.class);
                    activator = constructor.newInstance(activatorDeclaration.getAttributes());
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

}
