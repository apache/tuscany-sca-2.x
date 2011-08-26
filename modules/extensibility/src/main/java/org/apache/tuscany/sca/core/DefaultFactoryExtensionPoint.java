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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;



/**
 * Default implementation of a model factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultFactoryExtensionPoint implements FactoryExtensionPoint {
    private ExtensionPointRegistry registry;
    private Map<Class<?>, Object> factories = new ConcurrentHashMap<Class<?>, Object>();
    
    /**
     * Constructs a new DefaultModelFactoryExtensionPoint.
     */
    public DefaultFactoryExtensionPoint(ExtensionPointRegistry extensionPointRegistry) {
        this.registry = extensionPointRegistry;
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
    public <T> T getFactory(final Class<T> factoryInterface) {
        Object factory = factories.get(factoryInterface);
        if (factory == null) {

            // Dynamically load a factory class declared under META-INF/services 
            try {
                ServiceDeclaration factoryDeclaration =
                    registry.getServiceDiscovery().getServiceDeclaration(factoryInterface);
                if (factoryDeclaration != null) {
                    try {
                        // Constructor taking the extension point registry
                        factory = newInstance(registry, factoryDeclaration);
                    } catch (NoSuchMethodException e) {
                        factory = newInstance(factoryDeclaration.loadClass(), FactoryExtensionPoint.class, this);
                    }

                    // Cache the loaded factory
                    factories.put(factoryInterface, factory);

                    return factoryInterface.cast(factory);

                } else {
                    
                    // If the input interface is an abstract class
                    if (!factoryInterface.isInterface() && Modifier.isAbstract(factoryInterface.getModifiers())) {
                    
                        Method newInstanceMethod;
                        try {
                            newInstanceMethod = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
                                public Method run() throws Exception {
                                    return factoryInterface.getDeclaredMethod("newInstance");
                                }
                            });
                        } catch (PrivilegedActionException e){
                            throw (Exception)e.getException();
                        }
                        
                        ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                            public ClassLoader run() {
                                ClassLoader cl = factoryInterface.getClassLoader();
                                return cl;
                            }
                        });
                        ClassLoader tccl = setContextClassLoader(cl);
                        try {
                            try {
                                final Method fnewInstanceMethod = newInstanceMethod;
                                factory = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                                public Object run() throws Exception {
                                    Object factory = fnewInstanceMethod.invoke(null);
                                    return factory;
                                }
                            });
                            } catch (PrivilegedActionException e){
                                throw (Exception)e.getException();
                            }
                            
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
