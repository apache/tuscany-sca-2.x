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

package org.apache.tuscany.sca.node.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common functions and constants used by the admin components.
 *
 * @version $Rev$ $Date$
 */
final class NodeLauncherUtil {

    private final static Logger logger = Logger.getLogger(NodeLauncherUtil.class.getName());
    
    private static final String TUSCANY_HOME = "TUSCANY_HOME";


    /**
     * Returns a ClassLoader for the Tuscany runtime JARs.
     * 
     * @param parentClassLoader
     * 
     * @return
     */
    static ClassLoader runtimeClassLoader(ClassLoader parentClassLoader) throws FileNotFoundException, URISyntaxException, MalformedURLException {
        
        // Build list of runtime JARs
        List<URL> jarURLs = new ArrayList<URL>();
        
        // First determine the path to the launcher class
        String resource = NodeLauncherUtil.class.getName().replace('.', '/') + ".class"; 
        URL url = NodeLauncherUtil.class.getClassLoader().getResource(resource);
        if (url == null) {
            throw new FileNotFoundException(resource);
        }
        URI uri = url.toURI();
            
        // If the launcher class is in a JAR, add all runtime JARs from directory containing
        // that JAR (e.g. the Tuscany modules directory) as well as the ../modules and
        // ../lib directories
        String scheme = uri.getScheme();
        if (scheme.equals("jar")) {
            String path = uri.toString().substring(4);
            int i = path.indexOf("!/");
            if (i != -1) {
                path = path.substring(0, i);
                uri = URI.create(path);
            }
    
            File file = new File(uri);
            if (file.exists()) {
                File jarDirectory = file.getParentFile();
                if (jarDirectory != null && jarDirectory.exists()) {

                    // Collect JAR files from the directory containing the input JAR
                    // (e.g. the Tuscany modules directory)
                    collectJARFiles(jarDirectory, jarURLs);
                    
                    File homeDirectory = jarDirectory.getParentFile();
                    if (homeDirectory != null && homeDirectory.exists()) {
                        
                        // Collect JARs from the ../modules directory
                        File modulesDirectory = new File(homeDirectory, "modules");
                        if (modulesDirectory.exists() && !modulesDirectory.getAbsolutePath().equals(jarDirectory.getAbsolutePath())) {
                            collectJARFiles(modulesDirectory, jarURLs);
                        }

                        // Collect JARs from the ../lib directory
                        File libDirectory = new File(homeDirectory, "lib");
                        if (libDirectory.exists() && !libDirectory.getAbsolutePath().equals(jarDirectory.getAbsolutePath())) {
                            collectJARFiles(libDirectory, jarURLs);
                        }

                    }
                }
            }
        }
        
        // Look for a TUSCANY_HOME system property or environment variable
        // Add all the JARs found under $TUSCANY_HOME, $TUSCANY_HOME/modules
        // and $TUSCANY_HOME/lib
        String home = System.getProperty(TUSCANY_HOME);
        if (home == null || home.length() == 0) {
            home = System.getenv(TUSCANY_HOME);
        }
        if (home != null && home.length() != 0) {
            logger.info(TUSCANY_HOME + ": " + home);
            File homeDirectory = new File(home);
            if (homeDirectory.exists()) {
                
                // Collect files under $TUSCANY_HOME
                collectJARFiles(homeDirectory, jarURLs);
                
                // Collect files under $TUSCANY_HOME/modules
                File modulesDirectory = new File(homeDirectory, "modules");
                if (modulesDirectory.exists()) {
                    collectJARFiles(modulesDirectory, jarURLs);
                }
    
                // Collect files under $TUSCANY_HOME/lib
                File libDirectory = new File(homeDirectory, "lib");
                if (libDirectory.exists()) {
                    collectJARFiles(libDirectory, jarURLs);
                }
            }
        }
    
        // Return the runtime class loader
        if (!jarURLs.isEmpty()) {
            
            // Return a ClassLoader configured with the runtime JARs
            ClassLoader classLoader = new RuntimeClassLoader(jarURLs.toArray(new URL[0]), parentClassLoader);
            return classLoader;
            
        } else {
            return null;
        }
    }

    /**
     * Collect JAR files in the given directory
     * @param directory
     * @param urls
     * @throws MalformedURLException
     */
    private static void collectJARFiles(File directory, List<URL> urls) throws MalformedURLException {
        File[] files = directory.listFiles(new JARFileNameFilter());
        if (files != null) {
            int count = 0;
            for (File file: files) {
                URL url = file.toURI().toURL();
                
                // Collect URL of the JAR file, make sure that there are no
                // duplicates in the list
                if (!urls.contains(url)) {
                    urls.add(url);
                    count++;
                }
            }
            if (count != 0) {
                logger.info("Runtime classpath: "+ count + " JAR" + (count > 1? "s":"")+ " from " + directory.toString());
            }
        }
    }

    /**
     * A file name filter used to filter JAR files.
     */
    private static class JARFileNameFilter implements FilenameFilter {
        
        public boolean accept(File dir, String name) {
            name = name.toLowerCase(); 
            
            // Exclude tuscany-sca-all and tuscany-sca-manifest as they duplicate
            // code in the individual runtime module JARs
            if (name.startsWith("tuscany-sca-all")) {
                return false;
            }
            if (name.startsWith("tuscany-sca-manifest")) {
                return false;
            }
            
            // Filter out the Jetty and Webapp hosts
            if (name.startsWith("tuscany-host-jetty") ||
                name.startsWith("tuscany-host-webapp")) {
                //FIXME This is temporary
                return false;
            }
            
            // Include JAR and MAR files
            if (name.endsWith(".jar")) {
                return true;
            }
            if (name.endsWith(".mar")) {
                return true;
            }
            return false;
        }
    }
    
    
    /**
     * Creates a new node.
     * 
     * @param compositeURI
     * @param contributions
     * @throws LauncherException
     */
    static Object node(String configurationURI, String compositeURI, NodeLauncher.Contribution[] contributions) throws LauncherException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            // Set up runtime ClassLoader
            ClassLoader runtimeClassLoader = runtimeClassLoader(Thread.currentThread().getContextClassLoader());
            if (runtimeClassLoader != null) {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
            }
    
            // Use Java reflection to create the node as only the runtime class
            // loader knows the runtime classes required by the node
            String className = "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationLauncherBootstrap";
            Class<?> bootstrapClass;
            if (runtimeClassLoader != null) {
                bootstrapClass = Class.forName(className, true, runtimeClassLoader);
            } else {
                bootstrapClass = Class.forName(className);
            }
            Object bootstrap;
            if (configurationURI != null) {
                
                // Construct the node with a configuration URI
                bootstrap = bootstrapClass.getConstructor(String.class).newInstance(configurationURI);
                
            } else {
                
                // Construct the node with a composite URI and the URIs and
                // locations of a list of contributions
                Constructor<?> constructor = bootstrapClass.getConstructor(String.class, String[].class, String[].class);
                String[] uris = new String[contributions.length];
                String[] locations = new String[contributions.length];
                for (int i = 0; i < contributions.length; i++) {
                    uris[i] = contributions[i].getURI();
                    locations[i] = contributions[i].getLocation();
                }
                bootstrap = constructor.newInstance(compositeURI, uris, locations);
            }
            
            Object node = bootstrapClass.getMethod("getNode").invoke(bootstrap);
            return node;
            
        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Node could not be created", e);
            throw new LauncherException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    /**
     * Creates a new node daemon.
     * 
     * @throws LauncherException
     */
    static Object nodeDaemon() throws LauncherException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            // Set up runtime ClassLoader
            ClassLoader runtimeClassLoader = runtimeClassLoader(Thread.currentThread().getContextClassLoader());
            if (runtimeClassLoader != null) {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
            }
    
            // Use Java reflection to create the node daemon as only the runtime class
            // loader knows the runtime classes required by the node
            String className = "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationDaemonBootstrap";
            Class<?> bootstrapClass;
            if (runtimeClassLoader != null) {
                bootstrapClass = Class.forName(className, true, runtimeClassLoader);
            } else {
                bootstrapClass = Class.forName(className);
            }
            Object bootstrap = bootstrapClass.getConstructor().newInstance();
            
            Object nodeDaemon = bootstrapClass.getMethod("getNode").invoke(bootstrap);
            return nodeDaemon;
            
        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Node Daemon could not be created", e);
            throw new LauncherException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    /**
     * Creates a new domain manager.
     * 
     * @throws LauncherException
     */
    static Object domainManager() throws LauncherException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            // Set up runtime ClassLoader
            ClassLoader runtimeClassLoader = runtimeClassLoader(Thread.currentThread().getContextClassLoader());
            if (runtimeClassLoader != null) {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
            }
    
            // Use Java reflection to create the node daemon as only the runtime class
            // loader knows the runtime classes required by the node
            String className = "org.apache.tuscany.sca.workspace.admin.launcher.DomainManagerLauncherBootstrap";
            Class<?> bootstrapClass;
            if (runtimeClassLoader != null) {
                bootstrapClass = Class.forName(className, true, runtimeClassLoader);
            } else {
                bootstrapClass = Class.forName(className);
            }
            Object bootstrap = bootstrapClass.getConstructor().newInstance();
            
            Object domainManager = bootstrapClass.getMethod("getNode").invoke(bootstrap);
            return domainManager;
            
        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Domain Manager could not be created", e);
            throw new LauncherException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    /**
     * Simple URL class loader for the runtime JARs
     */
    private static class RuntimeClassLoader extends URLClassLoader {
        
        /**
         * Constructs a new class loader.
         * @param urls
         * @param parent
         */
        private RuntimeClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
        
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return super.loadClass(name);
        }
    }
}
