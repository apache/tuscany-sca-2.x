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

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.extensibility.ServiceHelper;

/**
 * Default implementation of a registry to hold all the Tuscany core extension
 * points. As the point of contact for all extension artifacts this registry
 * allows loaded extensions to find all other parts of the system and register
 * themselves appropriately.
 *
 * @version $Rev$ $Date$
 */
public class DefaultExtensionPointRegistry implements ExtensionPointRegistry {
    protected Map<Class<?>, Object> extensionPoints = new HashMap<Class<?>, Object>();
    private ServiceDiscovery discovery;
    /**
     * Constructs a new registry.
     */
    public DefaultExtensionPointRegistry() {
        this.discovery = ServiceDiscovery.getInstance();
    }
    
    protected DefaultExtensionPointRegistry(ServiceDiscovery discovery) {
        this.discovery = discovery;
    }

    /**
     * Add an extension point to the registry. This default implementation
     * stores extensions against the interfaces that they implement.
     *
     * @param extensionPoint The instance of the extension point
     *
     * @throws IllegalArgumentException if extensionPoint is null
     */
    public synchronized void addExtensionPoint(Object extensionPoint) {
        addExtensionPoint(extensionPoint, null);
    }

    public synchronized void addExtensionPoint(Object extensionPoint, ServiceDeclaration declaration) {
        if (extensionPoint == null) {
            throw new IllegalArgumentException("Cannot register null as an ExtensionPoint");
        }
        ServiceHelper.start(extensionPoint);
        
        Set<Class<?>> interfaces = getAllInterfaces(extensionPoint.getClass());
        for (Class<?> i : interfaces) {
            registerExtensionPoint(i, extensionPoint, declaration);
        }
    }

    protected void registerExtensionPoint(Class<?> i, Object extensionPoint, ServiceDeclaration declaration) {
        extensionPoints.put(i, extensionPoint);
    }

    /**
     * Get the extension point by the interface that it implements
     *
     * @param extensionPointType The lookup key (extension point interface)
     * @return The instance of the extension point
     *
     * @throws IllegalArgumentException if extensionPointType is null
     */
    public synchronized <T> T getExtensionPoint(Class<T> extensionPointType) {
        if (extensionPointType == null) {
            throw new IllegalArgumentException("Cannot lookup ExtensionPoint of type null");
        }

        Object extensionPoint = findExtensionPoint(extensionPointType);
        if (extensionPoint == null) {

            // Dynamically load an extension point class declared under META-INF/services
            try {
                ServiceDeclaration extensionPointDeclaration =
                    getServiceDiscovery().getServiceDeclaration(extensionPointType);
                if (extensionPointDeclaration != null) {
                    extensionPoint = newInstance(this, extensionPointDeclaration);
                    // Cache the loaded extension point
                    addExtensionPoint(extensionPoint, extensionPointDeclaration);
                }
            } catch (Throwable e) {
                throw new IllegalArgumentException(e);
            }
        }
        return extensionPointType.cast(extensionPoint);
    }

    protected <T> Object findExtensionPoint(Class<T> extensionPointType) {
        return extensionPoints.get(extensionPointType);
    }

    /**
     * Remove an extension point based on the interface that it implements
     *
     * @param extensionPoint The extension point to remove
     *
     * @throws IllegalArgumentException if extensionPoint is null
     */
    public synchronized void removeExtensionPoint(Object extensionPoint) {
        if (extensionPoint == null) {
            throw new IllegalArgumentException("Cannot remove null as an ExtensionPoint");
        }

        ServiceHelper.stop(extensionPoint);

        Set<Class<?>> interfaces = getAllInterfaces(extensionPoint.getClass());
        for (Class<?> i : interfaces) {
            unregisterExtensionPoint(i);
        }
    }

    protected void unregisterExtensionPoint(Class<?> i) {
        extensionPoints.remove(i);
    }

    /**
     * Returns the set of interfaces implemented by the given class and its
     * ancestors or a blank set if none
     */
    private static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> implemented = new HashSet<Class<?>>();
        getAllInterfaces(clazz, implemented);
        implemented.remove(LifeCycleListener.class);
        return implemented;
    }

    private static void getAllInterfaces(Class<?> clazz, Set<Class<?>> implemented) {
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaze : interfaces) {
            if (Modifier.isPublic(interfaze.getModifiers())) {
                implemented.add(interfaze);
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        // Object has no superclass so check for null
        if (superClass != null && !superClass.equals(Object.class)) {
            getAllInterfaces(superClass, implemented);
        }
    }

    public synchronized void start() {
        // Do nothing
    }

    public synchronized void stop() {
        // Get a unique map as an extension point may exist in the map by different keys
        Map<LifeCycleListener, LifeCycleListener> map = new IdentityHashMap<LifeCycleListener, LifeCycleListener>();
        for (Object extp : extensionPoints.values()) {
            if (extp instanceof LifeCycleListener) {
                LifeCycleListener listener = (LifeCycleListener)extp;
                map.put(listener, listener);
            }
        }
        ServiceHelper.stop(map.values());
        extensionPoints.clear();
    }

    public ServiceDiscovery getServiceDiscovery() {
        return discovery;
    }

}
