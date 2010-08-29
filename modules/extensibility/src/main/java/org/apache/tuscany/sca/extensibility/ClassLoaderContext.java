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

import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tuscany.sca.extensibility.impl.ClassLoaderDelegate;

/**
 * A utility that controls context class loaders
 * @tuscany.spi.extension.asclient
 */
public class ClassLoaderContext {
    private ClassLoader classLoader;

    /**
     * Create a context with the parent classloader and a list of service types that can be discovered
     * by the {@link ServiceDiscovery}
     * @param parent
     * @param discovery
     * @param serviceTypes
     */
    public ClassLoaderContext(ClassLoader parent, ServiceDiscovery discovery, Class<?>... serviceTypes) {
        this(parent, getClassLoaders(discovery, serviceTypes));
    }

    private ClassLoaderContext(ClassLoader parent, List<ClassLoader> delegates) {
        List<ClassLoader> loaders = new ArrayList<ClassLoader>(delegates);
        loaders.remove(parent);
        if (delegates.isEmpty()) {
            classLoader = parent;
        } else {
            classLoader = new ClassLoaderDelegate(parent, loaders);
        }
    }

    /**
     * Create a context that is visible to the parent classloader as well as the list of classloaders
     * @param parent
     * @param delegates
     */
    public ClassLoaderContext(ClassLoader parent, ClassLoader... delegates) {
        this(parent, Arrays.asList(delegates));
    }

    /**
     * Create a context with the parent classloader and a list of service types that can be discovered
     * by the {@link ServiceDiscovery}
     * @param parent
     * @param discovery
     * @param serviceTypes
     */
    public ClassLoaderContext(ClassLoader parent, ServiceDiscovery discovery, String... serviceTypes) {
        this(parent, getClassLoaders(discovery, serviceTypes));
    }

    public <T> T doPrivileged(PrivilegedAction<T> action) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != classLoader) {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        try {
            return action.run();
        } finally {
            if (tccl != classLoader) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    public <T> T doPrivileged(PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != classLoader) {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        try {
            return action.run();
        } catch (Exception e) {
            throw new PrivilegedActionException(e);
        } finally {
            if (tccl != classLoader) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    /**
     * Set the thread context classloader (TCCL) to a classloader that delegates to a collection
     * of classloaders
     * @param parent The parent classloader
     * @param delegates A list of classloaders to try
     * @return The existing TCCL
     */
    public static ClassLoader setContextClassLoader(ClassLoader parent, ClassLoader... delegates) {
        ClassLoaderContext context = new ClassLoaderContext(parent, delegates);
        return context.setContextClassLoader();
    }

    /**
     * Set the context classloader so that it can access the list of service providers
     * @param parent The parent classloader
     * @param serviceNames A list of service provider names
     * @return The old TCCL if a new one is set, otherwise null
     */
    public static ClassLoader setContextClassLoader(ClassLoader parent, ServiceDiscovery discovery, String... serviceNames) {
        ClassLoaderContext context = new ClassLoaderContext(parent, discovery, serviceNames);
        return context.setContextClassLoader();
    }

    /**
     * Set the context classloader so that it can access the list of service providers
     * @param parent The parent classloader
     * @param serviceNames A list of service provider names
     * @return The old TCCL if a new one is set, otherwise null
     */
    public static ClassLoader setContextClassLoader(ClassLoader parent, ServiceDiscovery discovery, Class<?>... serviceTypes) {
        ClassLoaderContext context = new ClassLoaderContext(parent, discovery, serviceTypes);
        return context.setContextClassLoader();
    }

    public ClassLoader setContextClassLoader() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != classLoader) {
            Thread.currentThread().setContextClassLoader(classLoader);
            return tccl;
        } else {
            return null;
        }
    }

    private static ClassLoader getClassLoader(ServiceDiscovery discovery, String serviceProvider) {
        try {
            ServiceDeclaration sd = discovery.getServiceDeclaration(serviceProvider);
            if (sd != null) {
                return sd.loadClass().getClassLoader();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    private static List<ClassLoader> getClassLoaders(ServiceDiscovery discovery, String... serviceNames) {
        List<ClassLoader> loaders = new ArrayList<ClassLoader>();
        for (String sp : serviceNames) {
            ClassLoader loader = getClassLoader(discovery, sp);
            if (loader != null) {
                if (!loaders.contains(loader)) {
                    loaders.add(loader);
                }
            }
        }
        ClassLoader tccl = discovery.getContextClassLoader();
        if (!loaders.contains(tccl)) {
            loaders.add(tccl);
        }
        return loaders;
    }

    private static ClassLoader getClassLoader(ServiceDiscovery discovery, Class<?> serviceType) {
        try {
            ServiceDeclaration sd = discovery.getServiceDeclaration(serviceType);
            if (sd != null) {
                return sd.loadClass().getClassLoader();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    private static List<ClassLoader> getClassLoaders(ServiceDiscovery discovery, Class<?>... serviceTypes) {
        List<ClassLoader> loaders = new ArrayList<ClassLoader>();
        for (Class<?> serviceType : serviceTypes) {
            ClassLoader classLoader = getClassLoader(discovery, serviceType);
            if (classLoader != null && !loaders.contains(classLoader)) {
                loaders.add(classLoader);
            }
        }
        ClassLoader tccl = discovery.getContextClassLoader();
        if (!loaders.contains(tccl)) {
            loaders.add(tccl);
        }
        return loaders;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

}
