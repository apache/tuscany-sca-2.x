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

package org.apache.tuscany.sca.node.osgi.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * 
 */
public class JarFileFinder {
    /**
     * A file name filter used to filter JAR files.
     */
    static class StandAloneJARFileNameFilter implements FilenameFilter {

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

            // Filter out the Tomcat and Webapp hosts
            if (name.startsWith("tuscany-host-tomcat") || name.startsWith("tuscany-host-webapp")) {
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
    static class WebAppJARFileNameFilter extends StandAloneJARFileNameFilter {

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

    private static final Logger logger = Logger.getLogger(JarFileFinder.class.getName());

    static final String TUSCANY_HOME = "TUSCANY_HOME";
    private static final String TUSCANY_PATH = "TUSCANY_PATH";

    /**
     * Collect JAR files in the given directory
     * @param directory
     * @param urls
     * @param filter
     * @throws MalformedURLException
     */
    private static void collectJARFiles(File directory, List<URL> urls, FilenameFilter filter)
        throws MalformedURLException {
        String[] files = directory.list(filter);
        if (files != null) {
            URL directoryURL = new URL(directory.toURI().toString() + "/");
            int count = 0;
            for (String file : files) {
                URL url = new URL(directoryURL, file);
                urls.add(url);
                count++;
            }
            if (count != 0) {
                logger.fine("Runtime classpath: " + count
                    + " JAR"
                    + (count > 1 ? "s" : "")
                    + " from "
                    + directory.toString());
            }
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
    private static void collectJARFiles(String directory,
                                        Set<URL> jarDirectoryURLs,
                                        List<URL> jarURLs,
                                        FilenameFilter filter) throws MalformedURLException {
        File directoryFile = new File(directory);
        URL directoryURL = directoryFile.toURI().toURL();
        if (!jarDirectoryURLs.contains(directoryURL) && directoryFile.exists()) {

            // Collect files under $TUSCANY_HOME
            jarDirectoryURLs.add(directoryURL);
            collectJARFiles(directoryFile, jarURLs, filter);

            // Collect files under $TUSCANY_HOME/modules
            File modulesDirectory = new File(directoryFile, "modules");
            URL modulesDirectoryURL = modulesDirectory.toURI().toURL();
            if (!jarDirectoryURLs.contains(modulesDirectoryURL) && modulesDirectory.exists()) {
                jarDirectoryURLs.add(modulesDirectoryURL);
                collectJARFiles(modulesDirectory, jarURLs, filter);
            }

            // Collect files under $TUSCANY_HOME/lib
            File libDirectory = new File(directoryFile, "lib");
            URL libDirectoryURL = libDirectory.toURI().toURL();
            if (!jarDirectoryURLs.contains(libDirectoryURL) && libDirectory.exists()) {
                jarDirectoryURLs.add(libDirectoryURL);
                collectJARFiles(libDirectory, jarURLs, filter);
            }
        }
    }

    /**
     * Returns a ClassLoader for the Tuscany runtime JARs.
     * 
     * @param parentClassLoader
     * @param filter
     * 
     * @return
     */
    public static List<URL> findJarFiles(File root, FilenameFilter filter) throws FileNotFoundException,
        URISyntaxException, MalformedURLException {

        // Build list of runtime JARs
        Set<URL> jarDirectoryURLs = new HashSet<URL>();
        List<URL> jarURLs = new ArrayList<URL>();

        URL url = null;
        if (root != null) {
            url = root.toURI().toURL();
        } else {
            // First determine the path to the launcher class
            String resource = JarFileFinder.class.getName().replace('.', '/') + ".class";
            url = JarFileFinder.class.getClassLoader().getResource(resource);
            if (url == null) {
                throw new FileNotFoundException(resource);
            }

            url = getContainer(url, resource);
        }
        URI uri = url.toURI();

        // If the launcher class is in a JAR, add all runtime JARs from directory containing
        // that JAR (e.g. the Tuscany modules directory) as well as the ../modules and
        // ../lib directories
        if (url != null && "file".equals(url.getProtocol())) {

            File file = new File(uri);
            if (file.exists()) {
                File jarDirectory = file.getParentFile();
                if (jarDirectory != null && jarDirectory.exists()) {

                    // Collect JAR files from the directory containing the input JAR
                    // (e.g. the Tuscany modules directory)
                    URL jarDirectoryURL = jarDirectory.toURI().toURL();
                    jarDirectoryURLs.add(jarDirectoryURL);
                    collectJARFiles(jarDirectory, jarURLs, filter);

                    File homeDirectory = jarDirectory.getParentFile();
                    if (homeDirectory != null && homeDirectory.exists()) {

                        // Collect JARs from the ../modules directory
                        File modulesDirectory = new File(homeDirectory, "modules");
                        URL modulesDirectoryURL = modulesDirectory.toURI().toURL();
                        if (!jarDirectoryURLs.contains(modulesDirectoryURL) && modulesDirectory.exists()) {
                            jarDirectoryURLs.add(modulesDirectoryURL);
                            collectJARFiles(modulesDirectory, jarURLs, filter);
                        }

                        // Collect JARs from the ../lib directory
                        File libDirectory = new File(homeDirectory, "lib");
                        URL libDirectoryURL = libDirectory.toURI().toURL();
                        if (!jarDirectoryURLs.contains(libDirectoryURL) && libDirectory.exists()) {
                            jarDirectoryURLs.add(libDirectoryURL);
                            collectJARFiles(libDirectory, jarURLs, filter);
                        }
                    }
                }
            }
        }

        // Look for a TUSCANY_HOME system property or environment variable
        // Add all the JARs found under $TUSCANY_HOME, $TUSCANY_HOME/modules
        // and $TUSCANY_HOME/lib
        String home = getProperty(TUSCANY_HOME);
        if (home != null && home.length() != 0) {
            logger.fine(TUSCANY_HOME + ": " + home);
            collectJARFiles(home, jarDirectoryURLs, jarURLs, filter);
        }

        // Look for a TUSCANY_PATH system property or environment variable
        // Add all the JARs found under $TUSCANY_PATH, $TUSCANY_PATH/modules
        // and $TUSCANY_PATH/lib
        String ext = getProperty(TUSCANY_PATH);
        if (ext != null && ext.length() != 0) {
            logger.fine(TUSCANY_PATH + ": " + ext);
            String separator = getProperty("path.separator");
            for (StringTokenizer tokens = new StringTokenizer(ext, separator); tokens.hasMoreTokens();) {
                collectJARFiles(tokens.nextToken(), jarDirectoryURLs, jarURLs, filter);
            }
        }

        return jarURLs;

    }

    static String getProperty(final String prop) {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                String value = System.getProperty(prop);
                if (value == null || value.length() == 0) {
                    return System.getenv(prop);
                } else {
                    return value;
                }
            }
        });
    }

    private static File toFile(URL url) {
        if (url == null || !url.getProtocol().equals("file")) {
            return null;
        } else {
            String filename = url.getFile().replace('/', File.separatorChar);
            int pos = 0;
            while ((pos = filename.indexOf('%', pos)) >= 0) {
                if (pos + 2 < filename.length()) {
                    String hexStr = filename.substring(pos + 1, pos + 3);
                    char ch = (char)Integer.parseInt(hexStr, 16);
                    filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
                }
            }
            return new File(filename);
        }
    }

    private static URL getContainer(URL resourceURL, String resourceName) {
        URL root = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String url = resourceURL.toExternalForm();
            String protocol = resourceURL.getProtocol();
            if ("file".equals(protocol)) {
                // directory contribution
                if (url.endsWith("/" + resourceName)) {
                    final String location = url.substring(0, url.length() - resourceName.length() - 1);
                    // workaround from evil URL/URI form Maven
                    // contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                    // Allow privileged access to open URL stream. Add FilePermission to added to
                    // security policy file.
                    try {
                        root = AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {
                            public URL run() throws IOException {
                                return toFile(new URL(location)).toURI().toURL();
                            }
                        });
                    } catch (PrivilegedActionException e) {
                        throw (MalformedURLException)e.getException();
                    }
                }

            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = url.substring(4, url.lastIndexOf("!/"));
                // workaround for evil URL/URI from Maven
                root = toFile(new URL(location)).toURI().toURL();

            } else if ("wsjar".equals(protocol)) {
                // See https://issues.apache.org/jira/browse/TUSCANY-2219
                // wsjar contribution 
                String location = url.substring(6, url.lastIndexOf("!/"));
                // workaround for evil url/uri from maven 
                root = toFile(new URL(location)).toURI().toURL();

            } else if (protocol != null && (protocol.equals("bundle") || protocol.equals("bundleresource"))) {
                root = new URL(resourceURL.getProtocol(), resourceURL.getHost(), resourceURL.getPort(), "/");
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        }
        return root;
    }

}
