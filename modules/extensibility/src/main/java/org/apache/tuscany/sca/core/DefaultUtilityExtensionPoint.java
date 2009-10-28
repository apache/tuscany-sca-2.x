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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;

/**
 * Default implementation of an extension point to hold Tuscany utility utilities.
 *
 * @version $Rev$ $Date$
 */
public class DefaultUtilityExtensionPoint implements UtilityExtensionPoint {
    private Map<Object, Object> utilities = new ConcurrentHashMap<Object, Object>();

    private ExtensionPointRegistry registry;
    /**
     * Constructs a new extension point.
     */
    public DefaultUtilityExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.registry = extensionPoints;
    }

    /**
     * Add a utility to the extension point. This default implementation
     * stores utilities against the interfaces that they implement.
     *
     * @param utility The instance of the utility
     *
     * @throws IllegalArgumentException if utility is null
     */
    public synchronized void addUtility(Object utility) {
        addUtility(null, utility);
    }
    
    public synchronized void addUtility(Object key, Object utility) {
        if (utility == null) {
            throw new IllegalArgumentException("Cannot register null as a Service");
        }

        if (utility instanceof LifeCycleListener) {
            ((LifeCycleListener)utility).start();
        }

        if (key == null) {
            Class<?> cls = utility.getClass();
            Set<Class<?>> interfaces = getAllInterfaces(cls);
            for (Class<?> i : interfaces) {
                utilities.put(i, utility);
            }
            if (interfaces.isEmpty() || isConcreteClass(cls)) {
                utilities.put(cls, utility);
            }
        } else {
            utilities.put(key, utility);
        }
    }

    /**
     * Get the utility by the interface that it implements
     *
     * @param utilityType The lookup key (utility interface)
     * @return The instance of the utility
     *
     * @throws IllegalArgumentException if utilityType is null
     */
    public synchronized <T> T getUtility(Class<T> utilityType) {
        return getUtility(utilityType, null);
    }

    /**
     * Remove a utility based on the interface that it implements
     *
     * @param utility The utility to remove
     *
     * @throws IllegalArgumentException if utility is null
     */
    public synchronized void removeUtility(Object utility) {
        if (utility == null) {
            throw new IllegalArgumentException("Cannot remove null as a Service");
        }

        if(utility instanceof LifeCycleListener) {
            ((LifeCycleListener) utility).stop();
        }
        
        for (Iterator<Map.Entry<Object, Object>> i = utilities.entrySet().iterator(); i.hasNext();) {
            Map.Entry<Object, Object> entry = i.next();
            if (entry.getValue() == utility) {
                i.remove();
            }
        }
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

    public synchronized <T> T getUtility(Class<T> utilityType, Object key) {
        if (utilityType == null) {
            throw new IllegalArgumentException("Cannot lookup Service of type null");
        }
        
        if (key == null) {
            key = utilityType;
        }

        Object utility = utilities.get(key);
   
        if (utility == null) {

            // Dynamically load a utility class declared under META-INF/services/"utilityType"
            try {
                ServiceDeclaration utilityDeclaration =
                    registry.getServiceDiscovery().getServiceDeclaration(utilityType.getName());
                Class<?> utilityClass = null;
                if (utilityDeclaration != null) {
                    utilityClass = utilityDeclaration.loadClass();
                } else if (isConcreteClass(utilityType)) {
                    utilityClass = utilityType;
                    key = utilityType;
                }
                if (utilityClass != null) {
                    // Construct the utility
                    if (utilityDeclaration != null) {
                        utility = newInstance(registry, utilityDeclaration);
                    } else {
                        try {
                            utility = newInstance(utilityClass, ExtensionPointRegistry.class, registry);
                        } catch (NoSuchMethodException e) {
                            utility = newInstance(utilityClass);
                        }
                    }
                    // Cache the loaded utility
                    if (key == utilityType) {
                        addUtility(utility);
                    } else {
                        addUtility(key, utility);
                    }
                }
            } catch (Throwable e) {
                throw new IllegalArgumentException(e);
            } 
        }
        return utilityType.cast(utility);
    }

    private boolean isConcreteClass(Class<?> utilityType) {
        int modifiers = utilityType.getModifiers();
        return !utilityType.isInterface() && Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers);
    }

    public synchronized void start() {
        // NOOP
    }

    public synchronized void stop() {
        // Get a unique map as an extension point may exist in the map by different keys
        Map<LifeCycleListener, LifeCycleListener> map = new IdentityHashMap<LifeCycleListener, LifeCycleListener>();
        for (Object util : utilities.values()) {
            if (util instanceof LifeCycleListener) {
                LifeCycleListener listener = (LifeCycleListener)util;
                map.put(listener, listener);
            }
        }
        for (LifeCycleListener listener : map.values()) {
            listener.stop();
        }
        utilities.clear();
    }

}
