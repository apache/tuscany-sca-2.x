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

package org.apache.tuscany.sca.contribution.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service discovery for Tuscany based on J2SE Jar service provider spec.
 * Services are described using configuration files in META-INF/services.
 * Service description specifies a classname followed by optional properties.
 * Multi-classloader environments are supported through a classloader
 * registration API
 */
public class ServiceDiscovery {
    private final static Logger logger = Logger.getLogger(ServiceDiscovery.class.getName());

    private static ServiceDiscovery instance;

    private HashSet<ClassLoader> registeredClassLoaders;

    /**
     * Get an instance of Service discovery, one instance is created per
     * classloader that this class is loaded from
     * 
     * @return
     */
    public static ServiceDiscovery getInstance() {

        if (instance == null) {
            instance = new ServiceDiscovery();
            instance.registeredClassLoaders = new HashSet<ClassLoader>();
            instance.registeredClassLoaders.add(ServiceDiscovery.class.getClassLoader());
        }
        return instance;
    }

    /**
     * Register a classloader with this discovery mechanism. Tuscany extension
     * classloaders are registered here.
     * 
     * @param classLoader
     */
    public void registerClassLoader(ClassLoader classLoader) {
        registeredClassLoaders.add(classLoader);
    }

    /**
     * Get all service declarations for this name
     * 
     * @param name
     * @return set of service declarations
     * @throws IOException
     */
    public Set<ServiceDeclaration> getServiceDeclarations(String name) throws IOException {

        Set<ServiceDeclaration> classSet = new HashSet<ServiceDeclaration>();

        for (ClassLoader classLoader : registeredClassLoaders) {
            getServiceClasses(classLoader, name, classSet, true);
        }
        return classSet;
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
    public Class<?> loadFirstServiceClass(Class<?> serviceInterface) throws IOException, ClassNotFoundException {

        Set<ServiceDeclaration> classSet = new HashSet<ServiceDeclaration>();

        for (ClassLoader classLoader : registeredClassLoaders) {
            getServiceClasses(classLoader, serviceInterface.getName(), classSet, false);
            if (classSet.size() > 0)
                break;
        }
        if (classSet.size() > 0)
            return classSet.iterator().next().loadClass();
        else
            return null;

    }

    /**
     * Returns a unique list of resource URLs of name META-INF/services/<name>
     * Each URL is associated with the first classloader that it was visible
     * from
     * 
     * @param name Name of resource
     * @return Table of URLs with associated classloaders
     * @throws IOException
     */
    public Hashtable<ClassLoader, Set<URL>> getServiceResources(String name) throws IOException {

        Hashtable<ClassLoader, Set<URL>> resourceTable = new Hashtable<ClassLoader, Set<URL>>();

        HashSet<URL> allURLs = new HashSet<URL>();
        for (ClassLoader classLoader : registeredClassLoaders) {
            HashSet<URL> urls = new HashSet<URL>();
            resourceTable.put(classLoader, urls);
            boolean debug = logger.isLoggable(Level.FINE);
            if (debug) {
                logger.fine("Discovering service resources using class loader " + classLoader);
            }
            for (URL url : Collections.list(classLoader.getResources("META-INF/services/" + name))) {
                if (allURLs.contains(url))
                    continue;
                urls.add(url);
            }
            allURLs.addAll(urls);
        }
        return resourceTable;
    }

    /**
     * Parse a service declaration in the form class;attr=value,attr=value and
     * return a map of attributes
     * 
     * @param declaration
     * @return a map of attributes
     */
    private Map<String, String> parseServiceDeclaration(String declaration) {
        Map<String, String> attributes = new HashMap<String, String>();
        StringTokenizer tokens = new StringTokenizer(declaration);
        String className = tokens.nextToken(";");
        if (className != null)
            attributes.put("class", className);
        for (; tokens.hasMoreTokens();) {
            String key = tokens.nextToken("=").substring(1);
            if (key == null)
                break;
            String value = tokens.nextToken(",").substring(1);
            if (value == null)
                break;
            attributes.put(key, value);
        }
        return attributes;
    }

    /**
     * Load the service class whose name specified in a configuration file
     * 
     * @param classLoader
     * @param name The name of the service class
     * @param classSet Populate this set with classes extends/implements the
     *                service class
     * @throws IOException
     */
    private void getServiceClasses(ClassLoader classLoader,
                                   String name,
                                   Set<ServiceDeclaration> classSet,
                                   boolean findAllClasses) throws IOException {

        boolean debug = logger.isLoggable(Level.FINE);
        if (debug) {
            logger.fine("Discovering service providers using class loader " + classLoader);
        }
        for (URL url : Collections.list(classLoader.getResources("META-INF/services/" + name))) {
            if (debug) {
                logger.fine("Reading service provider file: " + url.toExternalForm());
            }
            InputStream is = url.openStream();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is));
                while (true) {
                    String line = reader.readLine();
                    if (line == null)
                        break;
                    line = line.trim();
                    if (!line.startsWith("#") && !"".equals(line)) {
                        String reg = line.trim();
                        if (debug) {
                            logger.fine("Registering service provider: " + reg);
                        }

                        Map<String, String> attributes = parseServiceDeclaration(reg);
                        String className = attributes.get("class");
                        ServiceDeclaration serviceClass = new ServiceDeclaration(className, classLoader, attributes);
                        classSet.add(serviceClass);

                        if (!findAllClasses)
                            break;
                    }
                }
            } finally {
                if (reader != null)
                    reader.close();
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ioe) {
                    }
                }
            }
            if (!findAllClasses && classSet.size() > 0)
                break;
        }
    }

}
