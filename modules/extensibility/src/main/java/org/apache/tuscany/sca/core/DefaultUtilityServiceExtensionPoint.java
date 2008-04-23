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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Default implementation of an extension point to hold Tuscany utility services.
 *
 * @version $Rev$ $Date$
 */
public class DefaultUtilityServiceExtensionPoint implements UtilityServiceExtensionPoint {
    private Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();
    
    private ExtensionPointRegistry extensionPoints;
    
    /**
     * Constructs a new extension point. 
     */
    public DefaultUtilityServiceExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
    }

    /**
     * Add a service to the extension point. This default implementation
     * stores services against the interfaces that they implement.
     *
     * @param service The instance of the service
     *
     * @throws IllegalArgumentException if service is null
     */
    public void addService(Object service) {
        if (service == null) {
            throw new IllegalArgumentException("Cannot register null as a Service");
        }

        Set<Class> interfaces = getAllInterfaces(service.getClass());
        for (Class i : interfaces) {
            services.put(i, service);
        }
    }
    
    private Constructor<?> getConstructor(Constructor<?>[] constructors, Class<?>[] paramTypes) {
        for (Constructor<?> c : constructors) {
            Class<?> types[] = c.getParameterTypes();
            if (c.getParameterTypes().length == paramTypes.length) {
                boolean found = true;
                for (int i = 0; i < types.length; i++) {
                    if (types[i] != paramTypes[i]) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Get the service by the interface that it implements
     *
     * @param serviceType The lookup key (service interface)
     * @return The instance of the service
     *
     * @throws IllegalArgumentException if serviceType is null
     */
    public <T> T getService(Class<T> serviceType) {
        if (serviceType == null) {
            throw new IllegalArgumentException("Cannot lookup Service of type null");
        }

        Object service = services.get(serviceType);
        if (service == null) {
            
            // Dynamically load a service class declared under META-INF/services           
            try {
                Class<?> serviceClass = 
                	ServiceDiscovery.getInstance().loadFirstServiceClass(serviceType);
                if (serviceClass != null) {
                    // Construct the service
                    Constructor<?>[] constructors = serviceClass.getConstructors();
                    Constructor<?> constructor = getConstructor(constructors, new Class<?>[] {ExtensionPointRegistry.class});
                    if (constructor != null) {
                        service = constructor.newInstance(extensionPoints);
                    } else {
                        constructor = getConstructor(constructors, new Class<?>[] {});
                        if (constructor != null) {
                            service = constructor.newInstance();
                        } else {
                            throw new IllegalArgumentException(
                                                               "No valid constructor is found for " + serviceClass);
                        }
                    }
                   
                    // Cache the loaded service
                    addService(service);
                }
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return serviceType.cast(service);
    }

    /**
     * Remove a service based on the interface that it implements
     *
     * @param service The service to remove
     *
     * @throws IllegalArgumentException if service is null
     */
    public void removeService(Object service) {
        if (service == null) {
            throw new IllegalArgumentException("Cannot remove null as a Service");
        }

        Set<Class> interfaces = getAllInterfaces(service.getClass());
        for (Class i : interfaces) {
            services.remove(i);
        }
    }

    /**
     * Returns the set of interfaces implemented by the given class and its
     * ancestors or a blank set if none
     */
    private static Set<Class> getAllInterfaces(Class clazz) {
        Set<Class> implemented = new HashSet<Class>();
        getAllInterfaces(clazz, implemented);
        return implemented;
    }

    private static void getAllInterfaces(Class clazz, Set<Class> implemented) {
        Class[] interfaces = clazz.getInterfaces();
        for (Class interfaze : interfaces) {
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

}
