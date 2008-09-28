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

package org.apache.tuscany.sca.node.equinox.launcher;

import static org.osgi.framework.Constants.BUNDLE_CLASSPATH;
import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.DYNAMICIMPORT_PACKAGE;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;
import static org.osgi.framework.Constants.IMPORT_PACKAGE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Common functions and constants used by the admin components.
 *
 * @version $Rev$ $Date$
 */
final class NodeLauncherUtil {
    private static final Logger logger = Logger.getLogger(NodeLauncherUtil.class.getName());

    private static final String LAUNCHER_EQUINOX_LIBRARIES = "org.apache.tuscany.sca.node.launcher.equinox.libraries";

    private static final String SCANODE_FACTORY = "org.apache.tuscany.sca.node.SCANodeFactory";

    private static final String DOMAIN_MANAGER_LAUNCHER_BOOTSTRAP =
        "org.apache.tuscany.sca.domain.manager.launcher.DomainManagerLauncherBootstrap";

    private static final String NODE_IMPLEMENTATION_DAEMON_BOOTSTRAP =
        "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationDaemonBootstrap";

    private static final String NODE_IMPLEMENTATION_LAUNCHER_BOOTSTRAP =
        "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationLauncherBootstrap";

    private static final String TUSCANY_HOME = "TUSCANY_HOME";
    private static final String TUSCANY_PATH = "TUSCANY_PATH";

    /**
     * Creates a new node.
     *
     * @param configurationURI
     * @param compositeURI
     * @param compositeContent
     * @param contributions
     * @param contributionClassLoader
     * @param bundleContext
     * @throws LauncherException
     */
    static Object node(String configurationURI,
                       String compositeURI,
                       String compositeContent,
                       Contribution[] contributions,
                       BundleContext bundleContext) throws LauncherException {
        try {
            
            // Get the node runtime bundle.
            Bundle bundle = null;
            for (Bundle b : bundleContext.getBundles()) {
                if ("org.apache.tuscany.sca.implementation.node.runtime".equals(b.getSymbolicName())) {
                    bundle = b;
                    break;
                }
            }
            if (bundle == null) {
                throw new IllegalStateException("Bundle org.apache.tuscany.sca.implementation.node.runtime is not installed");
            }
            
            // Use Java reflection to create the node as only the runtime class
            // loader knows the runtime classes required by the node
            Class<?> bootstrapClass = bundle.loadClass(NODE_IMPLEMENTATION_LAUNCHER_BOOTSTRAP);

            Object bootstrap;
            if (configurationURI != null) {

                // Construct the node with a configuration URI
                bootstrap = bootstrapClass.getConstructor(String.class).newInstance(configurationURI);

            } else if (compositeContent != null) {

                // Construct the node with a composite URI, the composite content and
                // the URIs and locations of a list of contributions
                Constructor<?> constructor = bootstrapClass.getConstructor(String.class, String.class, String[].class, String[].class);
                String[] uris = new String[contributions.length];
                String[] locations = new String[contributions.length];
                for (int i = 0; i < contributions.length; i++) {
                    uris[i] = contributions[i].getURI();
                    locations[i] = contributions[i].getLocation();
                }
                bootstrap = constructor.newInstance(compositeURI, compositeContent, uris, locations);

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

            // Get the node instance
            Object node = bootstrapClass.getMethod("getNode").invoke(bootstrap);

            // If the SCANodeFactory interface is available in the current classloader, create
            // an SCANode proxy around the node we've just create
            try {
                Class<?> type = Class.forName(SCANODE_FACTORY);
                type = type.getDeclaredClasses()[0];
                return type.getMethod("createProxy", Class.class, Object.class).invoke(null, type, node);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
            return node;

        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Node could not be created", e);
            throw new LauncherException(e);
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

            // Use Java reflection to create the node daemon as only the runtime class
            // loader knows the runtime classes required by the node
            String className = NODE_IMPLEMENTATION_DAEMON_BOOTSTRAP;
            Class<?> bootstrapClass;
            bootstrapClass = Class.forName(className, false, tccl);
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

            // Use Java reflection to create the node daemon as only the runtime class
            // loader knows the runtime classes required by the node
            String className = DOMAIN_MANAGER_LAUNCHER_BOOTSTRAP;
            Class<?> bootstrapClass;
            bootstrapClass = Class.forName(className, false, tccl);
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

    private static Pattern pattern = Pattern.compile("-([0-9.]+)");

    /**
     * Returns the version number to use for the given JAR file.
     *   
     * @param jarFile
     * @return
     */
    private static String jarVersion(String jarFile) {
        Matcher matcher = pattern.matcher(jarFile);
        String version = "1.0.0";
        if (matcher.find()) {
            version = matcher.group();
            if (version.endsWith(".")) {
                version = version.substring(1, version.length() - 1);
            } else {
                version = version.substring(1);
            }
        }
        return version;
    }

    /**
     * Add the packages found in the given JAR to a set.
     * 
     * @param jarFile
     * @param packages
     * @throws IOException
     */
    private static void addPackages(String jarFile, Set<String> packages) throws IOException {
        String version = ";version=" + jarVersion(jarFile);
        ZipInputStream is = new ZipInputStream(new FileInputStream(file(new URL(jarFile))));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (!entry.isDirectory() && entryName != null
                && entryName.length() > 0
                && !entryName.startsWith(".")
                && entryName.endsWith(".class") // Exclude resources from Export-Package
                && entryName.lastIndexOf("/") > 0) {
                String pkg = entryName.substring(0, entryName.lastIndexOf("/")).replace('/', '.') + version;
                packages.add(pkg);
            }
        }
        is.close();
    }

    /**
     * Generate a manifest from a list of third-party JAR files.
     * 
     * @param jarFiles
     * @return
     * @throws IllegalStateException
     */
    static private Manifest thirdPartyLibraryBundleManifest(List<String> jarFiles) throws IllegalStateException {
        try {

            // List exported packages and bundle classpath entries
            StringBuffer classpath = new StringBuffer();
            StringBuffer exports = new StringBuffer();
            StringBuffer imports = new StringBuffer();
            Set<String> packages = new HashSet<String>();
            for (String jarFile : jarFiles) {
                addPackages(jarFile, packages);
                classpath.append("\"external:");
                classpath.append(file(new URL(jarFile)).getPath().replace(File.separatorChar, '/'));
                classpath.append("\",");
            }

            Set<String> importPackages = new HashSet<String>();
            for (String pkg : packages) {
                exports.append(pkg);
                exports.append(',');

                String importPackage = pkg;
                int index = pkg.indexOf(';');
                if (index != -1) {
                    importPackage = pkg.substring(0, index);
                }
                if (!importPackages.contains(importPackage)) {
                    imports.append(importPackage);
                    imports.append(',');
                    importPackages.add(importPackage);
                }
            }

            // Create a manifest
            Manifest manifest = new Manifest();
            Attributes attributes = manifest.getMainAttributes();
            attributes.putValue("Manifest-Version", "1.0");
            attributes.putValue(BUNDLE_MANIFESTVERSION, "2");
            attributes.putValue(BUNDLE_SYMBOLICNAME, LAUNCHER_EQUINOX_LIBRARIES);
            attributes.putValue(EXPORT_PACKAGE, exports.substring(0, exports.length() - 1));
            attributes.putValue(IMPORT_PACKAGE, imports.substring(0, imports.length() - 1));
            attributes.putValue(BUNDLE_CLASSPATH, classpath.substring(0, classpath.length() - 1));
            attributes.putValue(DYNAMICIMPORT_PACKAGE, "*");

            return manifest;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Generates a library bundle from a list of third-party JARs.
     * 
     * @param jarFiles
     * @return
     * @throws IOException
     */
    static InputStream thirdPartyLibraryBundle(List<String> jarFiles) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Manifest mf = thirdPartyLibraryBundleManifest(jarFiles);
        JarOutputStream jos = new JarOutputStream(bos, mf);
        jos.close();
        return new ByteArrayInputStream(bos.toByteArray());
    }

    /**
     * Returns the location of this bundle.
     * 
     * @return
     * @throws IOException
     */
    static String thisBundleLocation() throws IOException, URISyntaxException {
        String resource = NodeLauncherUtil.class.getName().replace('.', '/') + ".class";
        URL url = NodeLauncherUtil.class.getClassLoader().getResource(resource);
        if (url == null) {
            throw new FileNotFoundException(resource);
        }
        URI uri = url.toURI();

        String scheme = uri.getScheme();
        if (scheme.equals("jar")) {
            String path = uri.toString().substring(4);
            int i = path.indexOf("!/");
            path = path.substring(0, i);
            return path;
        } else {
            String path = uri.toString();
            path = path.substring(0, path.length() - resource.length());
            return path;
        }
    }
    
    /**
     * Returns the location of this bundle.
     * 
     * @param bundle
     * @return
     * @throws IOException
     */
    static String thisBundleLocation(Bundle bundle) throws IOException, URISyntaxException, ClassNotFoundException {
        String resource = NodeLauncherUtil.class.getName();
        Class<?> clazz = bundle.loadClass(NodeLauncherUtil.class.getName());
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        if (url == null) {
            throw new FileNotFoundException(resource);
        }
        URI uri = url.toURI();

        String scheme = uri.getScheme();
        if (scheme.equals("jar")) {
            String path = uri.toString().substring(4);
            int i = path.indexOf("!/");
            path = path.substring(0, i);
            return path;
        } else {
            String path = uri.toString();
            //path = path.substring(0, path.length() - resource.length());
            return path;
        }
    }

    /**
     * Install the given bundle.
     * 
     * @param bundleContext
     * @param location
     * @throws BundleException
     * @throws IOException
     */
    static void fixupBundle(String location) throws BundleException, IOException {
        File target = file(new URL(location));
        location = target.toURI().toString();
        
        // For development mode, copy the MANIFEST.MF file to the bundle location as it's
        // initially outside of target/classes, at the root of the project.
        if (location.endsWith("/target/classes/")) {
            File targetManifest = new File(target, "META-INF/MANIFEST.MF");
            File sourceManifest = new File(target.getParentFile().getParentFile(), "META-INF/MANIFEST.MF");
            targetManifest.getParentFile().mkdirs();
            OutputStream os = new FileOutputStream(targetManifest);
            InputStream is = new FileInputStream(sourceManifest);
            byte[] buf = new byte[2048];
            for (;;) {
                int l = is.read(buf);
                if (l == -1) {
                    break;
                }
                os.write(buf, 0, l);
            }
            is.close();
            os.close();
        }
    }

    /**
     * Returns a string representation of the given bundle.
     * 
     * @param b
     * @param verbose
     * @return
     */
    static String string(Bundle bundle, boolean verbose) {
        StringBuffer sb = new StringBuffer();
        sb.append(bundle.getBundleId()).append(" ").append(bundle.getSymbolicName());
        int s = bundle.getState();
        if ((s & Bundle.UNINSTALLED) != 0) {
            sb.append(" UNINSTALLED");
        }
        if ((s & Bundle.INSTALLED) != 0) {
            sb.append(" INSTALLED");
        }
        if ((s & Bundle.RESOLVED) != 0) {
            sb.append(" RESOLVED");
        }
        if ((s & Bundle.STARTING) != 0) {
            sb.append(" STARTING");
        }
        if ((s & Bundle.STOPPING) != 0) {
            sb.append(" STOPPING");
        }
        if ((s & Bundle.ACTIVE) != 0) {
            sb.append(" ACTIVE");
        }

        if (verbose) {
            sb.append(" ").append(bundle.getLocation());
            sb.append(" ").append(bundle.getHeaders());
        }
        return sb.toString();
    }

    /**
     * Returns the name of a bundle, or null if the given file is not a bundle.
     *  
     * @param file
     * @return
     * @throws IOException
     */
    static String bundleName(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        String bundleName = null;
        if (file.isDirectory()) {
            File mf = new File(file, "META-INF/MANIFEST.MF");
            if (mf.isFile()) {
                Manifest manifest = new Manifest(new FileInputStream(mf));
                bundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
            } else {
                if (file.toURI().getPath().endsWith("/target/classes/")) {
                    // Development mode, MANIFEST.MF is outside the bundle location
                    mf = new File(file.getParentFile().getParentFile(), "META-INF/MANIFEST.MF");
                    if (mf.isFile()) {
                        Manifest manifest = new Manifest(new FileInputStream(mf));
                        bundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
                    }
                }
            }
        } else {
            JarFile jar = new JarFile(file, false);
            Manifest manifest = jar.getManifest();
            bundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
            jar.close();
        }
        if (bundleName == null) {
            return bundleName;
        }
        int sc = bundleName.indexOf(';');
        if (sc != -1) {
            bundleName = bundleName.substring(0, sc);
        }
        return bundleName;
    }

    /**
     * Collect JAR files in the given directory.
     * 
     * @param directory
     * @param urls
     * @param filter
     * @throws MalformedURLException
     */
    private static void collectClasspathEntries(File directory, Set<URL> urls, FilenameFilter filter) throws MalformedURLException {
        File[] files = directory.listFiles(filter);
        if (files != null) {
            int count = 0;
            for (File file: files) {
                urls.add(file.toURI().toURL());
                count++;
            }
            if (count != 0) {
                logger.info("Runtime classpath: "+ count + " JAR" + (count > 1? "s":"")+ " from " + directory.toString());
            }
        }
    }

    /**
     * Collect development .../ target/classes directories in the given directory.
     * 
     * @param directory
     * @param urls
     * @param filter
     * @throws MalformedURLException
     */
    private static void collectTargetClassesClasspathEntries(File directory, Set<URL> urls, FilenameFilter filter) throws MalformedURLException {
        File[] files = directory.listFiles();
        if (files != null) {
            int count = 0;
            for (File file: files) {
                if (!file.isDirectory()) {
                    continue;
                }
                File target = new File(file, "target");
                if (!target.isDirectory()) {
                    continue; 
                }
                File classes = new File(target, "classes");
                if (classes.isDirectory() && filter.accept(target, "classes")) {
                    urls.add(classes.toURI().toURL());
                    count++;
                }
            }
            if (count != 0) {
                logger.info("Runtime classpath: "+ count + " classes folder" + (count > 1? "s":"")+ " from " + directory.toString());
            }
        }
    }

    /**
     * Collect JAR files under the given distribution directory.
     * 
     * @param directory
     * @param jarDirectoryURLs
     * @param jarURLs
     * @param filter
     * @throws MalformedURLException
     */
    private static void collectDistributionClasspathEntries(String directory, Set<URL> jarDirectoryURLs, Set<URL> jarURLs, FilenameFilter filter)
        throws MalformedURLException {
        File directoryFile = new File(directory);
        URL directoryURL = directoryFile.toURI().toURL(); 
        if (!jarDirectoryURLs.contains(directoryURL) && directoryFile.exists()) {
            
            // Collect files under the given directory
            jarDirectoryURLs.add(directoryURL);
            collectClasspathEntries(directoryFile, jarURLs, filter);
            
            // Collect files under <directory>/modules
            File modulesDirectory = new File(directoryFile, "modules");
            URL modulesDirectoryURL = modulesDirectory.toURI().toURL(); 
            if (!jarDirectoryURLs.contains(modulesDirectoryURL) && modulesDirectory.exists()) {
                jarDirectoryURLs.add(modulesDirectoryURL);
                collectClasspathEntries(modulesDirectory, jarURLs, filter);
            }

            // Collect files under <directory>/lib
            File libDirectory = new File(directoryFile, "lib");
            URL libDirectoryURL = libDirectory.toURI().toURL(); 
            if (!jarDirectoryURLs.contains(libDirectoryURL) && libDirectory.exists()) {
                jarDirectoryURLs.add(libDirectoryURL);
                collectClasspathEntries(libDirectory, jarURLs, filter);
            }
        }
    }

    /**
     * Determine the Tuscany runtime classpath entries.
     *
     * @param useDistribution
     * @param useAppClasspath
     * @param useModulesDirectory
     * @return
     */
    static Set<URL> runtimeClasspathEntries(boolean useDistribution, boolean useAppClasspath, boolean useModulesDirectory) throws FileNotFoundException,
        URISyntaxException, MalformedURLException {
        
        // Build list of runtime JARs
        Set<URL> jarDirectoryURLs = new HashSet<URL>();
        Set<URL> jarURLs = new HashSet<URL>();
    
        // Determine the path to the launcher class
        URI uri = URI.create(codeLocation(NodeLauncherUtil.class));
    
        // If the launcher class is in a JAR, add all runtime JARs from directory containing
        // that JAR (e.g. the Tuscany modules directory) as well as the ../modules and
        // ../lib directories
        if (uri.getPath().endsWith(".jar")) {
            if (useDistribution) {
        
                File file = new File(uri);
                if (file.exists()) {
                    File jarDirectory = file.getParentFile();
                    if (jarDirectory != null && jarDirectory.exists()) {
        
                        // Collect JAR files from the directory containing the input JAR
                        // (e.g. the Tuscany modules directory)
                        URL jarDirectoryURL = jarDirectory.toURI().toURL();
                        jarDirectoryURLs.add(jarDirectoryURL);
                        collectClasspathEntries(jarDirectory, jarURLs, new StandAloneJARFileNameFilter());
        
                        File homeDirectory = jarDirectory.getParentFile();
                        if (homeDirectory != null && homeDirectory.exists()) {
                            collectDistributionClasspathEntries(homeDirectory.getAbsolutePath(), jarDirectoryURLs, jarURLs, new StandAloneJARFileNameFilter());
                        }
                    }
                }
            }
        } else if (uri.getPath().endsWith("/target/classes/")) {
                
            // Development mode, we're running off classes in a workspace
            // and not from Maven surefire, collect all bundles in the workspace
            if (useModulesDirectory) {
                ClassLoader cl = NodeLauncherUtil.class.getClassLoader();
                if (!cl.getClass().getName().startsWith("org.apache.maven.surefire")) {
                    File file = new File(uri);
                    if (file.exists()) {
                        File moduleDirectory = file.getParentFile().getParentFile();
                        if (moduleDirectory != null) {
                            File modulesDirectory = moduleDirectory.getParentFile();
                            if (modulesDirectory != null && modulesDirectory.exists() && modulesDirectory.getName().equals("modules")) {
                                collectDevelopmentClasspathEntries(modulesDirectory.getAbsolutePath(), jarDirectoryURLs, jarURLs, new StandAloneDevelopmentClassesFileNameFilter());
                            }
                        }
                    }
                }
            }
        }
    
        // Look for a TUSCANY_HOME system property or environment variable
        // Add all the JARs found under $TUSCANY_HOME, $TUSCANY_HOME/modules
        // and $TUSCANY_HOME/lib
        if (useDistribution) {
            String home = getProperty(TUSCANY_HOME);
            if (home != null && home.length() != 0) {
                logger.info(TUSCANY_HOME + ": " + home);
                collectDistributionClasspathEntries(home, jarDirectoryURLs, jarURLs, new StandAloneJARFileNameFilter());
            }
        
            // Look for a TUSCANY_PATH system property or environment variable
            // Add all the JARs found under $TUSCANY_PATH, $TUSCANY_PATH/modules
            // and $TUSCANY_PATH/lib
            String ext = getProperty(TUSCANY_PATH);
            if (ext != null && ext.length() != 0) {
                logger.info(TUSCANY_PATH + ": " + ext);
                String separator = getProperty("path.separator");
                for (StringTokenizer tokens = new StringTokenizer(ext, separator); tokens.hasMoreTokens();) {
                    collectDistributionClasspathEntries(tokens.nextToken(), jarDirectoryURLs, jarURLs, new StandAloneJARFileNameFilter());
                }
            }
        }
        
        // Add the classpath entries from the current classloader
        if (useAppClasspath) {
            collectClassLoaderClasspathEntries(jarURLs, NodeLauncherUtil.class.getClassLoader());
        }
    
        return jarURLs;
    
    }

    /**
     * Returns the JAR files on the classpath used by the given classloader.
     * 
     * @param classLoader
     * @return
     */
    static List<URL> jarFilesOnClasspath(ClassLoader classLoader) {
        Set<URL> entries = new HashSet<URL>();
        collectClassLoaderClasspathEntries(entries, classLoader);
        return new ArrayList<URL>(entries);
    }

    private static String getProperty(final String prop) {
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

    /**
     * Collect JARs on the classpath of a URLClassLoader.
     * 
     * @param urls
     * @param cl
     */
    private static void collectClassLoaderClasspathEntries(Set<URL> urls, ClassLoader cl) {
        if (cl == null) {
            return;
        }
    
        // Collect JARs from the URLClassLoader's classpath
        if (cl instanceof URLClassLoader) {
            URL[] jarURLs = ((URLClassLoader)cl).getURLs();
            if (jarURLs != null) {
                for (URL jarURL : jarURLs) {
                    urls.add(jarURL);
                }
                int count = jarURLs.length;
                if (count != 0) {
                    logger.info("Runtime classpath: " + count + " JAR" + (count > 1? "s":"")+ " from application classpath.");
                }
            }
        }
    }

    /**
     * A file name filter used to filter JAR files.
     */
    private static class StandAloneJARFileNameFilter implements FilenameFilter {
        
        public boolean accept(File dir, String name) {
            name = name.toLowerCase(); 
            
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
     * A file name filter used to filter target/classes directories.
     */
    private static class StandAloneDevelopmentClassesFileNameFilter implements FilenameFilter {
        
        public boolean accept(File dir, String name) {
            name = name.toLowerCase();
            if (dir.getName().equals("target") && name.equals("classes")) {

                // Filter out the Tomcat and Webapp hosts
                String dirPath = dir.getAbsolutePath(); 
                if (dirPath.endsWith("host-tomcat/target") ||
                    dirPath.endsWith("host-webapp/target")) {
                    //FIXME This is temporary
                    return false;
                }
                return true;
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
     * Returns the File object representing  the given URL.
     * 
     * @param url
     * @return
     */
    static File file(URL url) {
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
    
    /**
     * Returns the location of the classpath entry, JAR, WAR etc. containing the given class.
     *  
     * @param clazz
     * @return
     */
    static private String codeLocation(Class<?> clazz) {
        String filename = clazz.getProtectionDomain().getCodeSource().getLocation().toString();      
        int pos = 0;
        while ((pos = filename.indexOf('%', pos)) >= 0) {
            if (pos + 2 < filename.length()) {
                String hexStr = filename.substring(pos + 1, pos + 3);
                char ch = (char)Integer.parseInt(hexStr, 16);
                filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
            }
        }
        return filename;
    }

    /**
     * Collect JAR files under the given distribution directory.
     * 
     * @param directory
     * @param jarDirectoryURLs
     * @param jarURLs
     * @param filter
     * @throws MalformedURLException
     */
    private static void collectDevelopmentClasspathEntries(String directory, Set<URL> jarDirectoryURLs, Set<URL> jarURLs, FilenameFilter filter)
        throws MalformedURLException {
        File directoryFile = new File(directory);
        URL directoryURL = directoryFile.toURI().toURL(); 
        if (!jarDirectoryURLs.contains(directoryURL) && directoryFile.exists()) {
            
            // Collect files under the given directory
            jarDirectoryURLs.add(directoryURL);
            collectTargetClassesClasspathEntries(directoryFile, jarURLs, filter);
            
            // Collect files under <directory>/thirdparty-library/lib
            File libDirectory = new File(directoryFile, "thirdparty-library/lib");
            URL libDirectoryURL = libDirectory.toURI().toURL(); 
            if (!jarDirectoryURLs.contains(libDirectoryURL) && libDirectory.exists()) {
                jarDirectoryURLs.add(libDirectoryURL);
                collectClasspathEntries(libDirectory, jarURLs, filter);
            }
        }
    }

}
