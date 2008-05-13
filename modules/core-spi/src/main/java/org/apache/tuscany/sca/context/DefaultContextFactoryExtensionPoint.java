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

package org.apache.tuscany.sca.context;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Default implementation of a model factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultContextFactoryExtensionPoint implements ContextFactoryExtensionPoint {

    /**
     * The Map of Factories that have been registered.
     */
    private HashMap<Class<?>, Object> factories = new HashMap<Class<?>, Object>();

    private ExtensionPointRegistry registry;

    public DefaultContextFactoryExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    /**
     * Add a model factory extension.
     *
     * @param factory The factory to add.
     * @throws IllegalArgumentException if factory is null
     */
    public void addFactory(Object factory) throws IllegalArgumentException {
        if (factory == null) {
            throw new IllegalArgumentException("Cannot add null as a factory");
        }

        Class<?>[] interfaces = factory.getClass().getInterfaces();
        for (int i = 0; i<interfaces.length; i++) {
            factories.put(interfaces[i], factory);
        }
    }

    /**
     * Remove a model factory extension.
     *
     * @param factory The factory to remove
     * @throws IllegalArgumentException if factory is null
     */
    public void removeFactory(Object factory) throws IllegalArgumentException {
        if (factory == null) {
            throw new IllegalArgumentException("Cannot remove null as a factory");
        }

        Class<?>[] interfaces = factory.getClass().getInterfaces();
        for (int i = 0; i<interfaces.length; i++) {
            factories.remove(interfaces[i]);
        }
    }

    /**
     * Get a factory implementing the given interface.
     *
     * @param factoryInterface The lookup key (factory interface)
     * @return The factory
     */
    public <T> T getFactory(Class<T> factoryInterface) throws IllegalArgumentException {
        if (factoryInterface == null) {
            throw new IllegalArgumentException("Cannot get null as a factory");
        }

        Object factory = factories.get(factoryInterface);
        if (factory == null) {

            // Dynamically load a factory class declared under META-INF/services
            try {
                Class<?> factoryClass = ServiceDiscovery.getInstance().loadFirstServiceClass(factoryInterface);
                if (factoryClass != null) {
            
                    // Default empty constructor
                    Constructor<?> constructor = factoryClass.getConstructor(ExtensionPointRegistry.class);
                    factory = constructor.newInstance(registry);
            
                    // Cache the loaded factory
                    addFactory(factory);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

        }

        return factoryInterface.cast(factory);
    }
}
