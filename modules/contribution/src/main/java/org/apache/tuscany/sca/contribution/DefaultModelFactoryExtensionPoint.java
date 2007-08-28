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

package org.apache.tuscany.sca.contribution;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.apache.tuscany.sca.contribution.util.ServiceConfigurationUtil;


/**
 * Default implementation of a model factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultModelFactoryExtensionPoint implements ModelFactoryExtensionPoint {
    
    private HashMap<Class<?>, Object> factories = new HashMap<Class<?>, Object>();
    
    public DefaultModelFactoryExtensionPoint() {
    }

    /**
     * Add a model factory extension.
     * 
     * @param factory The factory to add
     */
    public void addFactory(Object factory) {
        Class[] interfaces = factory.getClass().getInterfaces();
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
     * Remove a model factory extension.
     *  
     * @param factory The factory to remove
     */
    public void removeFactory(Object factory) {
        Class[] interfaces = factory.getClass().getInterfaces();
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
    
    /**
     * Get a factory implementing the given interface.
     * @param factoryInterface The lookup key (factory interface)
     * @return The factory
     */    
    public <T> T getFactory(Class<T> factoryInterface) {
        Object factory = factories.get(factoryInterface);
        if (factory == null) {
            
            if (factoryInterface.isInterface()) {
                
                // Dynamically load a factory class declared under META-INF/services 
                ClassLoader classLoader = factoryInterface.getClassLoader();
                if (classLoader == null)
                    classLoader = ClassLoader.getSystemClassLoader();
                try {
                    List<String> classNames = ServiceConfigurationUtil.getServiceClassNames(classLoader, factoryInterface.getName());
                    if (!classNames.isEmpty()) {
                        Class<?> factoryClass = Class.forName(classNames.iterator().next(), true, classLoader);
                        
                        try {
                            // Default empty constructor
                            Constructor<?> constructor = factoryClass.getConstructor();
                            factory = constructor.newInstance();
                        } catch (NoSuchMethodException e) {
                            
                            // Constructor taking the model factory extension point
                            Constructor<?> constructor = factoryClass.getConstructor(ModelFactoryExtensionPoint.class);
                            factory = constructor.newInstance(this);
                        }
                        
                        // Cache the loaded factory
                        addFactory(factory);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            } else {

                // Call the newInstance static method on the factory abstract class
                try {
                    Method newInstanceMethod = factoryInterface.getMethod("newInstance");
                    factory = newInstanceMethod.invoke(null);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
                
                // Cache the factory
                addFactory(factory);
            }
        }
        return factoryInterface.cast(factory);
    }

}
