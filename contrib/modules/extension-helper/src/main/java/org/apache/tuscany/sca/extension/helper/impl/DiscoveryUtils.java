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

package org.apache.tuscany.sca.extension.helper.impl;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Discovers Activators in the classpath using the J2SE
 * jar file extensions for Service Provider discovery
 *
 * @version $Rev$ $Date$
 */
public class DiscoveryUtils {

    @SuppressWarnings("unchecked")
    public static <T> List<T> discoverActivators(Class<T> activatorClass, ExtensionPointRegistry registry) {
        List<T> activators;
        try {
            Set<ServiceDeclaration> activatorClasses =
                ServiceDiscovery.getInstance().getServiceDeclarations(activatorClass);
            activators = new ArrayList<T>();
            for (ServiceDeclaration declaration : activatorClasses) {
                try {
                    Class<T> c = (Class<T>)declaration.loadClass();
                    activators.add(c.cast(instantiateActivator(c, registry)));
                } catch (Throwable e) {
                    e.printStackTrace(); // TODO: log
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return activators;
    }

    static Object instantiateActivator(Class activator, ExtensionPointRegistry registry) {
        Constructor[] cs = activator.getConstructors();
        if (cs.length != 1) {
            throw new RuntimeException("Activator must have only one constructors");
        }

        Class<?>[] paramTypes = cs[0].getParameterTypes();
        Object[] extensions = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            if ("org.apache.tuscany.sca.host.http.ServletHost".equals(paramTypes[i].getName())) {
                extensions[i] = getServletHost(registry);
            } else {
                extensions[i] = registry.getExtensionPoint(paramTypes[i]);
            }
        }

        try {

            return cs[0].newInstance(extensions);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getServletHost(ExtensionPointRegistry registry) {
        try {

            Class<?> servletHostEPClass = Class.forName("org.apache.tuscany.sca.host.http.ServletHostExtensionPoint");
            Object servletHostEP = registry.getExtensionPoint(servletHostEPClass);
            Class<?> extensibleServletHost = Class.forName("org.apache.tuscany.sca.host.http.ExtensibleServletHost");
            return extensibleServletHost.getConstructor(new Class[] {servletHostEPClass}).newInstance(servletHostEP);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
