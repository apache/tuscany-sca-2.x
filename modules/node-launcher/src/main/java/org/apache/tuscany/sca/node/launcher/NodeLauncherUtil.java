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
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common functions and constants used by the admin components.
 *
 * @version $Rev$ $Date$
 */
final class NodeLauncherUtil {
    private static final String NODE_FACTORY = "org.apache.tuscany.sca.node.NodeFactory";

    private static final Logger logger = Logger.getLogger(NodeLauncherUtil.class.getName());

    private static final String TUSCANY_HOME = "TUSCANY_HOME";
    private static final String TUSCANY_PATH = "TUSCANY_PATH";


    /**
     * Returns a ClassLoader for the Tuscany runtime JARs for use in a standalone
     * J2SE environment.
     *
     * @param parentClassLoader
     *
     * @return
     */
    static ClassLoader standAloneRuntimeClassLoader(ClassLoader parentClassLoader) throws FileNotFoundException, URISyntaxException, MalformedURLException {
        return runtimeClassLoader(parentClassLoader, new StandAloneJARFileNameFilter());
    }

    /**
     * Returns a ClassLoader for the Tuscany runtime JARs for use in a Webapp
     * environment.
     *
     * @param parentClassLoader
     *
     * @return
     */
    static ClassLoader webAppRuntimeClassLoader(ClassLoader parentClassLoader) throws FileNotFoundException, URISyntaxException, MalformedURLException {
        return runtimeClassLoader(parentClassLoader, new WebAppJARFileNameFilter());
    }

    /**
     * Returns a ClassLoader for the Tuscany runtime JARs.
     *
     * @param parentClassLoader
     * @param filter
     *
     * @return
     */
    private static ClassLoader runtimeClassLoader(ClassLoader parentClassLoader, FilenameFilter filter) throws FileNotFoundException, URISyntaxException, MalformedURLException {
        // First try to see if the runtime classes are already on the classpath
        // If yes, skip the discovery to avoid duplicate jars
        try {
            Class.forName(NODE_FACTORY, false, parentClassLoader);
            return parentClassLoader;
        } catch (ClassNotFoundException e) {
            // Ignore;
        }
        // Build list of runtime JARs
        Set<URL> jarDirectoryURLs = new HashSet<URL>();
        Set<URL> jarURLs = new HashSet<URL>();

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
                    File homeDirectory = jarDirectory.getParentFile();
                    if (homeDirectory != null && homeDirectory.exists()) {
                        collectJARFiles(homeDirectory.getPath(), jarDirectoryURLs, jarURLs, filter);
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
            logger.fine(TUSCANY_HOME + ": " + home);
            collectJARFiles(home, jarDirectoryURLs, jarURLs, filter);
        }

        // Look for a TUSCANY_PATH system property or environment variable
        // Add all the JARs found under $TUSCANY_PATH, $TUSCANY_PATH/modules
        // and $TUSCANY_PATH/lib
        String ext = System.getProperty(TUSCANY_PATH);
        if (ext == null || ext.length() == 0) {
            ext = System.getenv(TUSCANY_PATH);
        }
        if (ext != null && ext.length() != 0) {
            logger.fine(TUSCANY_PATH + ": " + ext);
            String separator = System.getProperty("path.separator");
            for (StringTokenizer tokens = new StringTokenizer(ext, separator); tokens.hasMoreTokens(); ) {
                collectJARFiles(tokens.nextToken(), jarDirectoryURLs, jarURLs, filter);
            }
        }

        // Return the runtime class loader
        if (!jarURLs.isEmpty()) {
            // Remove the URLs which are already in the parent classloader
            if (parentClassLoader instanceof URLClassLoader) {
                URLClassLoader cl = (URLClassLoader)parentClassLoader;
                jarURLs.removeAll(Arrays.asList(cl.getURLs()));
            }

            // Return a ClassLoader configured with the runtime JARs
            ClassLoader classLoader = new RuntimeClassLoader(jarURLs.toArray(new URL[jarURLs.size()]), parentClassLoader);
            return classLoader;

        } else {
            return null;
        }
    }

    /**
     * Collect JAR files under the given directory.
     *
     * @param directory
     * @param jarDirectoryURLs
     * @param jarURLs
     * @param filter
     * @throws MalformedURLException
     */
    private static void collectJARFiles(String directory, Set<URL> jarDirectoryURLs, Collection<URL> jarURLs, FilenameFilter filter)
        throws MalformedURLException {
        File directoryFile = new File(directory);
        URL directoryURL = directoryFile.toURI().toURL();
        if (!jarDirectoryURLs.contains(directoryURL) && directoryFile.exists()) {

            // Collect files under $TUSCANY_HOME
            jarDirectoryURLs.add(directoryURL);
            collectJARFiles(directoryFile, jarURLs, filter, false);

            // Collect files under $TUSCANY_HOME/modules
            File modulesDirectory = new File(directoryFile, "modules");
            URL modulesDirectoryURL = modulesDirectory.toURI().toURL();
            if (!jarDirectoryURLs.contains(modulesDirectoryURL) && modulesDirectory.exists()) {
                jarDirectoryURLs.add(modulesDirectoryURL);
                collectJARFiles(modulesDirectory, jarURLs, filter, true);
            }

            // Collect files under $TUSCANY_HOME/lib
            File libDirectory = new File(directoryFile, "lib");
            URL libDirectoryURL = libDirectory.toURI().toURL();
            if (!jarDirectoryURLs.contains(libDirectoryURL) && libDirectory.exists()) {
                jarDirectoryURLs.add(libDirectoryURL);
                collectJARFiles(libDirectory, jarURLs, filter, true);
            }
        }
    }

    /**
     * Collect JAR files in the given directory
     * @param directory
     * @param urls
     * @param filter
     * @param recursive
     * @throws MalformedURLException
     */
    private static void collectJARFiles(File directory, Collection<URL> urls, FilenameFilter filter, boolean recursive) throws MalformedURLException {
        File[] files = directory.listFiles(filter);
        if (files != null) {
            int count = 0;
            for (File file: files) {
                if (recursive && file.isDirectory()) {
                    collectJARFiles(file, urls, filter, recursive);
                } else if (file.isFile()) {
                    urls.add(file.toURI().toURL());
                    count++;
                }
            }
            if (count != 0 && logger.isLoggable(Level.FINE)) {
                logger.fine("Runtime classpath: "+ count + " JAR" + (count > 1? "s":"")+ " from " + directory.toString());
            }
        }
    }

    /**
     * A file name filter used to filter JAR files.
     */
    private static class StandAloneJARFileNameFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            if(new File(dir, name).isDirectory()) {
                return true;
            }
            name = name.toLowerCase();

            // Exclude tuscany-sca-all and tuscany-sca-manifest as they duplicate
            // code in the individual runtime module JARs
            if (name.startsWith("tuscany-sca-all")) {
                return false;
            }
            if (name.startsWith("tuscany-sca-manifest")) {
                return false;
            }

            if ("features".equals(dir.getName()) && name.startsWith("equinox-manifest")) {
                return false;
            }

            // Filter out the Tomcat and Webapp hosts
            if (name.startsWith("tuscany-host-tomcat") ||
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
     * A file name filter used to filter JAR files.
     */
    private static class WebAppJARFileNameFilter extends StandAloneJARFileNameFilter {

        @Override
        public boolean accept(File dir, String name) {
            if (!super.accept(dir, name)) {
                return false;
            }
            name = name.toLowerCase();

            // Exclude servlet-api JARs
            if (name.startsWith("servlet-api")) {
                return false;
            }

            // Exclude the Tomcat and Jetty hosts
            if (name.startsWith("tuscany-host-tomcat") || name.startsWith("tuscany-host-jetty")) {
                //FIXME This is temporary
                return false;
            }

            return true;
        }
    }


    /**
     * Creates a new node.
     *
     * @param compositeURI
     * @param contributions
     * @throws LauncherException
     */
    static Object node(String configurationURI, String compositeURI, String compositeContent, Contribution[] contributions, ClassLoader contributionClassLoader) throws LauncherException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {

            // Set up runtime ClassLoader
            ClassLoader runtimeClassLoader = runtimeClassLoader(Thread.currentThread().getContextClassLoader(),
                                                                new StandAloneJARFileNameFilter());
            if (runtimeClassLoader != null) {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
            }

            // Use Java reflection to create the node as only the runtime class
            // loader knows the runtime classes required by the node
            String className = NODE_FACTORY;
            Class<?> bootstrapClass;
            if (runtimeClassLoader != null) {
                bootstrapClass = Class.forName(className, true, runtimeClassLoader);
            } else {
                bootstrapClass = Class.forName(className);
            }

            Object node =
                createNode(bootstrapClass,
                           configurationURI,
                           compositeURI,
                           compositeContent,
                           contributions,
                           contributionClassLoader);

            return node;

        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Node could not be created", e);
            throw new LauncherException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    private static Object createNode(Class<?> bootstrapClass,
                                     String configurationURI,
                                     String compositeURI,
                                     String compositeContent,
                                     Contribution[] contributions,
                                     ClassLoader contributionClassLoader) throws NoSuchMethodException,
        IllegalAccessException, InvocationTargetException, MalformedURLException {
        Method newInstance = bootstrapClass.getMethod("newInstance");
        Object nodeFactory = newInstance.invoke(null);

        Object node;
        if (configurationURI != null) {

            URL url = null;
            URI uri = URI.create(configurationURI);
            if (uri.getScheme() == null) {
                uri = new File(configurationURI).toURI();
            }
            url = uri.toURL();

            // NodeFactory.createNode(URL)
            Method create = bootstrapClass.getMethod("createNode", URL.class);
            node = create.invoke(nodeFactory, url);

        } else if (contributionClassLoader != null) {

            // NodeFactory.createNode(String, ClassLoader)
            Method create = bootstrapClass.getMethod("createNode", String.class, ClassLoader.class);
            node = create.invoke(nodeFactory, compositeURI, contributionClassLoader);

        } else if (compositeContent != null) {

            // NodeFactory.createNode(Reader, Stringp[], String[])
            Method create = bootstrapClass.getMethod("createNode", Reader.class, String[].class, String[].class);
            String[] uris = new String[contributions.length];
            String[] locations = new String[contributions.length];
            for (int i = 0; i < contributions.length; i++) {
                uris[i] = contributions[i].getURI();
                locations[i] = contributions[i].getLocation();
            }
            node = create.invoke(nodeFactory, compositeContent, uris, locations);

        } else {

            // NodeFactory.createNode(String, Stringp[], String[])
            Method create = bootstrapClass.getMethod("createNode", String.class, String[].class, String[].class);
            String[] uris = new String[contributions.length];
            String[] locations = new String[contributions.length];
            for (int i = 0; i < contributions.length; i++) {
                uris[i] = contributions[i].getURI();
                locations[i] = contributions[i].getLocation();
            }
            node = create.invoke(nodeFactory, compositeURI, uris, locations);
        }
        return node;
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
            ClassLoader runtimeClassLoader = runtimeClassLoader(Thread.currentThread().getContextClassLoader(),
                                                                new StandAloneJARFileNameFilter());
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
            NodeDaemonLauncher.logger.log(Level.SEVERE, "SCA Node Daemon could not be created", e);
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
    static Object domainManager(String rootDirectory) throws LauncherException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            // Set up runtime ClassLoader
            ClassLoader runtimeClassLoader = runtimeClassLoader(Thread.currentThread().getContextClassLoader(),
                                                                new StandAloneJARFileNameFilter());
            if (runtimeClassLoader != null) {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
            }

            // Use Java reflection to create the node daemon as only the runtime class
            // loader knows the runtime classes required by the node
            String className = "org.apache.tuscany.sca.domain.manager.launcher.DomainManagerLauncherBootstrap";
            Class<?> bootstrapClass;
            if (runtimeClassLoader != null) {
                bootstrapClass = Class.forName(className, true, runtimeClassLoader);
            } else {
                bootstrapClass = Class.forName(className);
            }
            Constructor<?> constructor = bootstrapClass.getConstructor(String.class);
            Object bootstrap = constructor.newInstance(rootDirectory);

            Object domainManager = bootstrapClass.getMethod("getNode").invoke(bootstrap);
            return domainManager;

        } catch (Exception e) {
            DomainManagerLauncher.logger.log(Level.SEVERE, "SCA Domain Manager could not be created", e);
            throw new LauncherException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    /**
     * Simple URL class loader for the runtime JARs
     */
    private static class RuntimeClassLoader extends URLClassLoader {
        private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        private ClassLoader parent;

        /**
         * Constructs a new class loader.
         * @param urls
         * @param parent
         */
        private RuntimeClassLoader(URL[] urls, ClassLoader parent) {
            super(urls);
            this.parent = parent;
        }

        @Override
        public URL findResource(String name) {
            URL url = super.findResource(name);
            if (url == null) {
                url = parent.getResource(name);
            }
            return url;
        }

        @Override
        public Enumeration<URL> findResources(String name) throws IOException {
            Enumeration<URL> resources = super.findResources(name);
            Enumeration<URL> parentResources = parent.getResources(name);
            List<URL> allResources = new ArrayList<URL>();
            for (; resources.hasMoreElements(); ) {
                allResources.add(resources.nextElement());
            }
            for (; parentResources.hasMoreElements(); ) {
                allResources.add(parentResources.nextElement());
            }
            return Collections.enumeration(allResources);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            Class<?> cl;

            // First try to load the class using the parent classloader
            try {
                cl = parent.loadClass(name);
                ClassLoader loadedBy = cl.getClassLoader();

                // If the class was not loaded directly by the parent classloader
                // or the system classloader try to load a local version of the class
                // using our RuntimeClassloader instead
                if (loadedBy != parent &&
                    loadedBy != systemClassLoader &&
                    loadedBy != null) {

                    try {
                        cl = super.findClass(name);
                    } catch (ClassNotFoundException e) {
                        // No class alternative was found in our RuntimeClassloader,
                        // use the class found in the parent classloader hierarchy
                    }
                }
            } catch (ClassNotFoundException e) {

                // The class was not found by the parent class loader, try
                // to load it using our RuntimeClassloader
                cl = super.findClass(name);
            }

            return cl;
        }
    }

}
