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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Common functions and constants used by the admin components.
 *
 * @version $Rev$ $Date$
 */
public final class NodeLauncherUtil {

    private final static Logger logger = Logger.getLogger(NodeLauncherUtil.class.getName());
    
    private static final String TUSCANY_HOME = "TUSCANY_HOME";


    /**
     * Returns a classloader for the Tuscany runtime JARs.
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
            
        // If the launcher class is in a JAR, add all runtime JARs from the same directory
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
                File directory = file.getParentFile();
                collectJARFiles(directory, jarURLs);
            }
        }
        
        // Look for a TUSCANY_HOME system property or environment variable
        // Add all the JARs found under $TUSCANY_HOME, $TUSCANY_HOME/modules
        // and $TUSCANY_HOME/lib
        String home = System.getProperty(NodeLauncherUtil.TUSCANY_HOME);
        if (home == null || home.length() == 0) {
            home = System.getenv(NodeLauncherUtil.TUSCANY_HOME);
        }
        if (home != null && home.length() != 0) {
            NodeLauncherUtil.logger.info(NodeLauncherUtil.TUSCANY_HOME + ": " + home);
            File homeDirectory = new File(home);
            if (homeDirectory.exists()) {
                
                // Collect files under $TUSCANY_HOME
                collectJARFiles(homeDirectory, jarURLs);
                
                // Collect files under $TUSCANY_HOME/modules
                File moduleDirectory = new File(homeDirectory, "modules");
                if (moduleDirectory.exists()) {
                    collectJARFiles(moduleDirectory, jarURLs);
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
            
            // Return a classloader configured with the runtime JARs
            ClassLoader classLoader = new URLClassLoader(jarURLs.toArray(new URL[0]), parentClassLoader);
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
        File[] files = directory.listFiles(new FilenameFilter() {
    
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
                if (name.startsWith("tuscany-host-jetty") || name.startsWith("tuscany-host-webapp")) {
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
        });
    
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

}
