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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;



/**
 * Default implementation of a model factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultFactoryExtensionPoint implements FactoryExtensionPoint {
    private ExtensionPointRegistry extensionPointRegistry;
    private HashMap<Class<?>, Object> factories = new HashMap<Class<?>, Object>();
    
    /**
     * Constructs a new DefaultModelFactoryExtensionPoint.
     */
    public DefaultFactoryExtensionPoint(ExtensionPointRegistry extensionPointRegistry) {
        this.extensionPointRegistry = extensionPointRegistry;
    }

    /**
     * Add a model factory extension.
     * 
     * @param factory The factory to add
     */
    public void addFactory(Object factory) {
        Class<?>[] interfaces = factory.getClass().getInterfaces();
        if (interfaces.length == 0) {
            Class<?> sc = factory.getClass().getSuperclass();
            if (sc != Object.class) {
                factories.put(sc, factory);
            }
        } else {
            for (int i = 0; i<interfaces.length; i++) {
                factories.put(interfaces[i], factory);
            }
        }
    }

    /**
     * Remove a model factory.
     *  
     * @param factory The factory to remove
     */
    public void removeFactory(Object factory) {
        Class<?>[] interfaces = factory.getClass().getInterfaces();
        if (interfaces.length == 0) {
            Class<?> sc = factory.getClass().getSuperclass();
            if (sc != Object.class) {
                factories.remove(sc);
            }
        } else {
            for (int i = 0; i<interfaces.length; i++) {
                factories.remove(interfaces[i]);
            }
        }
    }
    
    private ClassLoader setContextClassLoader(final ClassLoader classLoader) {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                if (classLoader != null) {
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
                return tccl;
            }
        });
    }

    /**
     * Get a factory implementing the given interface.
     * @param factoryInterface The lookup key (factory interface)
     * @return The factory
     */    
    public <T> T getFactory(Class<T> factoryInterface) {
        Object factory = factories.get(factoryInterface);
        if (factory == null) {

            // Dynamically load a factory class declared under META-INF/services 
            try {
                ServiceDeclaration factoryDeclaration =
                    ServiceDiscovery.getInstance().getServiceDeclaration(factoryInterface.getName());
                if (factoryDeclaration != null) {
                    Class<?> factoryClass = factoryDeclaration.loadClass();
                    try {
                        
                        // Default empty constructor
                        Constructor<?> constructor = factoryClass.getConstructor();
                        factory = constructor.newInstance();
                    } catch (NoSuchMethodException e) {
                        try {

                            // Constructor taking the model factory extension point
                            Constructor<?> constructor = factoryClass.getConstructor(FactoryExtensionPoint.class);
                            factory = constructor.newInstance(this);
                        } catch (NoSuchMethodException e1) {

                            // Constructor taking the extension point registry
                            Constructor<?> constructor = factoryClass.getConstructor(ExtensionPointRegistry.class);
                            factory = constructor.newInstance(extensionPointRegistry);
                        }
                    }

                    // Cache the loaded factory
                    factories.put(factoryInterface, factory);
                    
                    return  factoryInterface.cast(factory);
                    
                } else {
                    
                    // If the input interface is an abstract class
                    if (!factoryInterface.isInterface() && Modifier.isAbstract(factoryInterface.getModifiers())) {
                        Method newInstanceMethod = factoryInterface.getDeclaredMethod("newInstance");
                        ClassLoader tccl = setContextClassLoader(factoryInterface.getClassLoader());
                        try {
                            
                            // Create a new instance
                            factory = newInstanceMethod.invoke(null);
                            
                            // Cache the factory
                            factories.put(factoryInterface, factory);
                            
                            return  factoryInterface.cast(factory);
                        } catch (Exception e) {
                            // Sorry no factory found
                            return null;
                        } finally {
                            setContextClassLoader(tccl);
                        }
                    } else {
                        
                        // Sorry no factory found
                        return null;
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            return factoryInterface.cast(factory);
        }
    }

}
