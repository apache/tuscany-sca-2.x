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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * Discovers Activators in the classpath using the J2SE
 * jar file extensions for Service Provider discovery
 */
public class DiscoveryUtils {

    public static <T> List<T> discoverActivators(Class<T> activatorClass, ClassLoader classLoader, ExtensionPointRegistry registry) {
        Set<Class> activatorClasses = getServiceClasses(classLoader, activatorClass);
        List<T> activators = new ArrayList<T>();
        for (Class<T> c : activatorClasses) {
            try {
                activators.add(c.cast(instantiateActivator(c, registry)));
            } catch (Throwable e) {
                e.printStackTrace(); // TODO: log
            }
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

        for (int i=0; i< paramTypes.length; i++) {
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

    static Set<Class> getServiceClasses(ClassLoader classLoader, Class name) {
        try {

            Set<Class> set = new HashSet<Class>();
            Enumeration<URL> urls = classLoader.getResources("META-INF/services/" + name.getName());
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Set<String> services = getServiceClassNames(url);
                if (services != null) {
                    for (String className : services) {
                        try {
                            set.add(Class.forName(className, true, classLoader));
                        } catch (Throwable e) {
                            // TODO: log 
                        }
                    }
                }
            }
            return set;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Set<String> getServiceClassNames(URL url) throws IOException {
        Set<String> names = new HashSet<String>();
        InputStream is = url.openStream();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (!line.startsWith("#") && !"".equals(line)) {
                    names.add(line.trim());
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            
            if (is != null){
                try {
                    is.close();
                } catch( IOException ioe) {
                    //ignore
                }
            }
        }
        return names;
    }

}
