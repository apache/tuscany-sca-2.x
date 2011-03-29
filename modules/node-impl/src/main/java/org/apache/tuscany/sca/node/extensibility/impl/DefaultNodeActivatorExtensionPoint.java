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

package org.apache.tuscany.sca.node.extensibility.impl;

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
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.extensibility.NodeActivator;
import org.apache.tuscany.sca.node.extensibility.NodeActivatorExtensionPoint;

public class DefaultNodeActivatorExtensionPoint implements NodeActivatorExtensionPoint {
    private final static Logger logger = Logger.getLogger(DefaultNodeActivatorExtensionPoint.class.getName());
    private List<NodeActivator> activators = new ArrayList<NodeActivator>();
    private ExtensionPointRegistry registry;
    private boolean loadedActivators;

    public DefaultNodeActivatorExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public void addNodeActivator(NodeActivator activator) {
        this.activators.add(activator);
    }

    @Override
    public void removeNodeActivator(NodeActivator activator) {
        this.activators.remove(activator);
    }

    @Override
    public List<NodeActivator> getNodeActivators() {
        loadModuleActivators();
        return activators;
    }

    @Override
    public void nodeStarted(Node node) {
        for(NodeActivator activator : activators) {
            activator.nodeStarted(node);
        }
    }

    @Override
    public void nodeStopped(Node node) {
        for(NodeActivator activator : activators) {
            activator.nodeStopped(node);
        }
    }



    /**
     * Dynamically load node activators declared under META-INF/services
     */
    private synchronized void loadModuleActivators() {
        if (loadedActivators)
            return;

        // Get the activator service declarations
        Collection<ServiceDeclaration> activatorDeclarations;
        try {
            // Load the module activators by ranking
            activatorDeclarations = registry.getServiceDiscovery().getServiceDeclarations(NodeActivator.class.getName(), true);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Load and instantiate module activators
        for (ServiceDeclaration activatorDeclaration : activatorDeclarations) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Loading " + activatorDeclaration.getClassName());
            }
            NodeActivator activator = null;
            try {
                Class<NodeActivator> activatorClass = (Class<NodeActivator>)activatorDeclaration.loadClass();
                try {
                    activator = ServiceHelper.newInstance(activatorClass, ExtensionPointRegistry.class, registry);
                } catch (NoSuchMethodException e) {
                    try {
                        activator =
                            ServiceHelper.newInstance(activatorClass,
                                          new Class<?>[] {ExtensionPointRegistry.class, Map.class},
                                          registry,
                                          activatorDeclaration.getAttributes());

                    } catch (NoSuchMethodException e1) {
                        activator = ServiceHelper.newInstance(activatorClass);

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
            addNodeActivator(activator);
        }

        loadedActivators = true;
    }
}
