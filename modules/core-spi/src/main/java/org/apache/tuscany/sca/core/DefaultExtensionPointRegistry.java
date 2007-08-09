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
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.interfacedef.impl.TempServiceDeclarationUtil;

/**
 * Default implementation of a registry to hold all the Tuscany core extension
 * points. As the point of contact for all extension artifacts this registry
 * allows loaded extensions to find all other parts of the system and register
 * themselves appropriately.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultExtensionPointRegistry implements ExtensionPointRegistry {
    private Map<Class<?>, Object> extensionPoints = new HashMap<Class<?>, Object>();

    /**
     * Add an extension point to the registry. This default implementation
     * stores extensions against the interfaces that they implement.
     * 
     * @param extensionPoint The instance of the extension point
     */
    public void addExtensionPoint(Object extensionPoint) {
        Set<Class> interfaces = getAllInterfaces(extensionPoint.getClass());
        for (Class i : interfaces) {
            extensionPoints.put(i, extensionPoint);
        }
    }

    /**
     * Get the extension point by the interface that it implements
     * 
     * @param extensionPointType The lookup key (extension point interface)
     * @return The instance of the extension point
     */
    public <T> T getExtensionPoint(Class<T> extensionPointType) {
        Object extensionPoint = extensionPoints.get(extensionPointType);
        if (extensionPoint == null) {
            
            // Dynamically load an extension point class declared under META-INF/services 
            ClassLoader classLoader = extensionPointType.getClassLoader();
            try {
                Set<String> classNames = TempServiceDeclarationUtil.getServiceClassNames(classLoader, extensionPointType.getName());
                if (!classNames.isEmpty()) {
                    Class<?> extensionPointClass = Class.forName(classNames.iterator().next(), true, classLoader);
                    extensionPoint = extensionPointClass.newInstance();
                    
                    // Cache the loaded extension point
                    addExtensionPoint(extensionPoint);
                }
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
        return extensionPointType.cast(extensionPoint);
    }

    /**
     * Remove an extension point based on the interface that it implements
     * 
     * @param extensionPoint The extension point to remove
     */
    public void removeExtensionPoint(Object extensionPoint) {
        Set<Class> interfaces = getAllInterfaces(extensionPoint.getClass());
        for (Class i : interfaces) {
            extensionPoints.remove(i);
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
            String name = interfaze.getName();
            if (name.startsWith("java.") || name.startsWith("javax.")) {
                continue;
            }
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
