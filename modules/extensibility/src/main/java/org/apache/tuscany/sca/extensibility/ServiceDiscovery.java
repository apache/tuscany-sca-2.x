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

package org.apache.tuscany.sca.extensibility;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service discovery for Tuscany based on J2SE Jar service provider spec.
 * Services are described using configuration files in META-INF/services.
 * Service description specifies a class name followed by optional properties.
 * 
 *
 * @version $Rev$ $Date$
 */
public class ServiceDiscovery {
    private static final Logger logger = Logger.getLogger(ServiceDiscovery.class.getName());

    private final static ServiceDiscovery instance = new ServiceDiscovery();

    private static ServiceDiscoverer discoverer;
    private Set<ClassLoader> registeredClassLoaders = new HashSet<ClassLoader>();

    /**
     * Get an instance of Service discovery, one instance is created per
     * ClassLoader that this class is loaded from
     * 
     * @return
     */
    public static ServiceDiscovery getInstance() {
        return instance;
    }

    public static ServiceDiscoverer getServiceDiscoverer() {
        if (discoverer == null) {
            discoverer = new ClasspathServiceDiscoverer();
        }
        return discoverer;
    }

    public static void setServiceDiscoverer(ServiceDiscoverer sd) {
        if (discoverer != null) {
            throw new IllegalStateException("The ServiceDiscoverer cannot be reset");
        }
        discoverer = sd;
    }

    /**
     * Register a ClassLoader with this discovery mechanism. Tuscany extension
     * ClassLoaders are registered here.
     * 
     * @param classLoader
     */
    public synchronized void registerClassLoader(ClassLoader classLoader) {
        registeredClassLoaders.add(classLoader);
    }

    /**
     * Unregister a ClassLoader with this discovery mechanism. 
     * 
     * @param classLoader
     */
    public synchronized void unregisterClassLoader(ClassLoader classLoader) {
        registeredClassLoaders.remove(classLoader);
    }

    /**
     * Get all service declarations for this name
     * 
     * @param name
     * @return set of service declarations
     * @throws IOException
     */
    public Set<ServiceDeclaration> getServiceDeclarations(String name) throws IOException {
        Set<ServiceDeclaration> services = getServiceDiscoverer().discover(name, false);
        return services;
    }

    /**
     * Get all service declarations for this interface
     * 
     * @param serviceInterface
     * @return set of service declarations
     * @throws IOException
     */
    public Set<ServiceDeclaration> getServiceDeclarations(Class<?> serviceInterface) throws IOException {

        return getServiceDeclarations(serviceInterface.getName());
    }

    /**
     * Load one service implementation class for this interface
     * 
     * @param serviceInterface
     * @return service implementation class
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Class<?> loadFirstServiceClass(final Class<?> serviceInterface) throws IOException, ClassNotFoundException {
        // Try System property first
        String className = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty(serviceInterface.getName());
            }
        });
        if (className != null) {
            try {
                return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        Set<ServiceDeclaration> services = getServiceDiscoverer().discover(serviceInterface.getName(), true);
        if (services.isEmpty()) {
            return null;
        }
        return services.iterator().next().loadClass();
    }

}
