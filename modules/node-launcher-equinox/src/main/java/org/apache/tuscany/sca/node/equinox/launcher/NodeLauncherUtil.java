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

import static org.osgi.framework.Constants.ACTIVATION_LAZY;
import static org.osgi.framework.Constants.BUNDLE_ACTIVATIONPOLICY;
import static org.osgi.framework.Constants.BUNDLE_CLASSPATH;
import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_NAME;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VENDOR;
import static org.osgi.framework.Constants.BUNDLE_VERSION;
import static org.osgi.framework.Constants.DYNAMICIMPORT_PACKAGE;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;
import static org.osgi.framework.Constants.REQUIRE_BUNDLE;
import static org.osgi.framework.Constants.RESOLUTION_DIRECTIVE;
import static org.osgi.framework.Constants.RESOLUTION_OPTIONAL;
import static org.osgi.framework.Constants.VISIBILITY_DIRECTIVE;
import static org.osgi.framework.Constants.VISIBILITY_REEXPORT;

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
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
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
import org.osgi.framework.Constants;

/**
 * Common functions and constants used by the admin components.
 *
 * @version $Rev$ $Date$
 */
final class NodeLauncherUtil {
    private static final String NODE_API_BUNDLE = "org.apache.tuscany.sca.node.api";
    private static final String BASE_BUNDLE = "org.apache.tuscany.sca.base";

    private static final Logger logger = Logger.getLogger(NodeLauncherUtil.class.getName());

    static final String META_INF_SERVICES = "META-INF.services;partial=true;mandatory:=partial";
    static final String LAUNCHER_EQUINOX_LIBRARIES = "org.apache.tuscany.sca.node.launcher.equinox.libraries";
    static final String GATEWAY_BUNDLE = "org.apache.tuscany.sca.gateway";
    
    private static final String NODE_FACTORY = "org.apache.tuscany.sca.node.NodeFactory";

    private static final String DOMAIN_MANAGER_LAUNCHER_BOOTSTRAP =
        "org.apache.tuscany.sca.domain.manager.launcher.DomainManagerLauncherBootstrap";

    private static final String NODE_IMPLEMENTATION_DAEMON_BOOTSTRAP =
        "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationDaemonBootstrap";

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
                if (NODE_API_BUNDLE.equals(b.getSymbolicName())) {
                    bundle = b;
                    break;
                }
                if (b.getSymbolicName().contains(BASE_BUNDLE)) {
                    bundle = b;
                    break;
                }
            }
            if (bundle == null) {
                throw new IllegalStateException("Bundle " + NODE_API_BUNDLE + " is not installed");
            }

            // Use Java reflection to create the node as only the runtime class
            // loader knows the runtime classes required by the node
            Class<?> bootstrapClass = bundle.loadClass(NODE_FACTORY);

            Object node = createNode(bootstrapClass, configurationURI, compositeURI, compositeContent, contributions);

            // If the SCANodeFactory interface is available in the current classloader, create
            // an SCANode proxy around the node we've just create
            try {
                Class<?> type = Class.forName(NODE_FACTORY);
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

    private static Object createNode(Class<?> bootstrapClass,
                                     String configurationURI,
                                     String compositeURI,
                                     String compositeContent,
                                     Contribution[] contributions) throws NoSuchMethodException,
        IllegalAccessException, InvocationTargetException, MalformedURLException {
        Method newInstance = bootstrapClass.getMethod("newInstance");
        Object nodeFactory = newInstance.invoke(null);

        Object node;
        if (configurationURI != null) {

            // NodeFactory.createNode(URL)
            Method create = bootstrapClass.getMethod("createNode", URL.class);
            node = create.invoke(nodeFactory, new URL(configurationURI));

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

    /**
     * starting with -, then some digits, then . or - or _, then some digits again
     *
     */
    // Mike Edwards 13/04/2009 - this original pattern allows for any number of repeated
    // groups of digits, so that 1.2.3.4 is legal, for example.  The problem with this is
    // that OSGi only deals with a maximum of 3 groups of digits...
    // private static Pattern pattern = Pattern.compile("-(\\d)+((\\.|-|_)(\\d)+)*");
    //
    // This updated version restricts the allowed patterns to a maximum of 3 groups of
    // digits so that "1", "1.2" and "1.2.3" are allowed but not "1.2.3.4" etc
    private static Pattern pattern = Pattern.compile("-(\\d)+((\\.|-|_)(\\d)+){0,2}");

    /**
     * Returns the version number to use for the given JAR file.
     *
     * @param jarFile
     * @return
     */
    static String jarVersion(URL jarFile) {
        String name = jarFile.getFile();
        int index = name.lastIndexOf('/');
        if (index != -1) {
            // Find the last segment
            name = name.substring(index + 1);
        }
        index = name.lastIndexOf('.');
        if (index != -1) {
            // Trim the extension
            name = name.substring(0, index);
        }

        Matcher matcher = pattern.matcher(name);
        String version = "0.0.0";
        if (matcher.find()) {
            version = matcher.group();
            // Remove the leading "-" character
            version = version.substring(1);
            // The Pattern above allows the version string to contain "-" and "_" as digit separators.
            // OSGi only allows for "." as a separator thus any "-" and "_" characters in the version string must be replaced by "."
            version = version.replace('-', '.');
            version = version.replace('_', '.');
        }
        return version;
    }

    static String artifactId(URL jarFile) {
        String name = jarFile.getFile();
        int index = name.lastIndexOf('/');
        if (index != -1) {
            // Find the last segment
            name = name.substring(index + 1);
        }
        index = name.lastIndexOf('.');
        if (index != -1) {
            // Trim the extension
            name = name.substring(0, index);
        }

        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            return name.substring(0, matcher.start());
        } else {
            return name;
        }
    }

    /**
     * Add the packages found in the given JAR to a set.
     *
     * @param jarFile
     * @param packages
     * @throws IOException
     */
    private static void addPackages(URL jarFile, Set<String> packages, String version) throws IOException {
        if (version == null) {
            version = ";version=" + jarVersion(jarFile);
        } else {
            version = ";version=" + version;
        }
        File file = file(jarFile);
        if (file.isDirectory()) {
            List<String> classFiles = listClassFiles(file);
            for (String cls : classFiles) {
                int index = cls.lastIndexOf('/');
                if (index == -1) {
                    // Default package cannot be exported
                    continue;
                }
                String pkg = cls.substring(0, index);
                pkg = pkg.replace('/', '.') + version;
                // Export META-INF.services
                if ("META-INF.services".equals(pkg)) {
                    packages.add(META_INF_SERVICES);
                } else {
                    packages.add(pkg);
                }
            }
        } else if (file.isFile()) {
            ZipInputStream is = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                String entryName = entry.getName();
                // Export split packages for META-INF/services
                if(entryName.startsWith("META-INF/services/")) {
                    packages.add("META-INF.services" + ";partial=true;mandatory:=partial");
                }
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
    }

    private static List<String> listClassFiles(File directory) {
        List<String> artifacts = new ArrayList<String>();
        traverse(artifacts, directory, directory);
        // Add META-INF/services to be exported
        if (new File(directory, "META-INF/services").isDirectory()) {
            artifacts.add("META-INF/services/");
        }
        return artifacts;
    }

    /**
     * Recursively traverse a root directory
     *
     * @param fileList
     * @param file
     * @param root
     * @throws IOException
     */
    private static void traverse(List<String> fileList, File file, File root) {
        if (file.isFile() && file.getName().endsWith(".class")) {
            fileList.add(root.toURI().relativize(file.toURI()).toString());
        } else if (file.isDirectory()) {
            String uri = root.toURI().relativize(file.toURI()).toString();
            if (uri.endsWith("/")) {
                uri = uri.substring(0, uri.length() - 1);
            }
            fileList.add(uri);

            File[] files = file.listFiles();
            for (File f : files) {
                if (!f.getName().startsWith(".")) {
                    traverse(fileList, f, root);
                }
            }
        }
    }

    /**
     * Finds the OSGi manifest file for a JAR file, where the manifest file is held in a META-INF directory
     * alongside the JAR
     * @param jarURL - The URL of the JAR file
     * @return - a Manifest object corresponding to the manifest file, or NULL if there is no OSGi manifest
     */
    static private Manifest findOSGiManifest(URL jarURL) {
        try {
            File jarFile = new File(jarURL.toURI());
            File theManifestFile = new File(jarFile.getParent(), "META-INF/MANIFEST.MF");
            if (theManifestFile.exists()) {
                // Create manifest object by reading the manifest file
                Manifest manifest = new Manifest(new FileInputStream(theManifestFile));
                // Check that this manifest file has the necessary OSGi metadata
                String bundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
                if (bundleName != null) {
                    return manifest;
                } // end if
            } // end if
        } catch (Exception e) {
            // Could not read the manifest - continue
        }
        return null;
    } // end findOSGiManifest

    /**
     * Generate a manifest from a list of third-party JAR files.
     *
     * @param jarFiles
     * @param bundleSymbolicName The Bundle-SymbolicName
     * @param bundleVersion The Bundle-Version
     * @return
     * @throws IllegalStateException
     */
    static private Manifest thirdPartyLibraryBundleManifest(Collection<URL> jarFiles,
                                                            String bundleSymbolicName,
                                                            String bundleVersion) throws IllegalStateException {
        try {

            // List exported packages and bundle classpath entries
            StringBuffer classpath = new StringBuffer();
            StringBuffer exports = new StringBuffer();
            StringBuffer imports = new StringBuffer();
            Set<String> packages = new HashSet<String>();

            for (URL jarFile : jarFiles) {
                addPackages(jarFile, packages, bundleVersion);
                classpath.append("\"external:");
                classpath.append(file(jarFile).getPath().replace(File.separatorChar, '/'));
                classpath.append("\",");
            }

            Set<String> importPackages = new HashSet<String>();
            for (String pkg : packages) {

                String importPackage = pkg;
                int index = pkg.indexOf(';');
                if (index != -1) {
                    importPackage = pkg.substring(0, index);
                }
                if (!importPackages.contains(importPackage)) {
                    // Exclude META-INF.services
                    if (!"META-INF.services".equals(importPackage)) {
                        imports.append(pkg);
                        imports.append(',');
                    }
                    importPackages.add(importPackage);
                    exports.append(pkg);
                    exports.append(',');
                } else {
                    logger.warning("Duplicate package skipped: " + pkg);
                }
            }

            // Create a manifest
            Manifest manifest = new Manifest();
            Attributes attributes = manifest.getMainAttributes();
            attributes.putValue("Manifest-Version", "1.0");
            attributes.putValue(BUNDLE_MANIFESTVERSION, "2");

            if (bundleVersion == null) {
                bundleVersion = "0.0.0";
            }
            attributes.putValue(Constants.BUNDLE_VERSION, bundleVersion);
            if (bundleSymbolicName == null) {
                bundleSymbolicName = LAUNCHER_EQUINOX_LIBRARIES;
            }
            attributes.putValue(BUNDLE_SYMBOLICNAME, bundleSymbolicName);
            if (exports.length() > 0) {
                attributes.putValue(EXPORT_PACKAGE, exports.substring(0, exports.length() - 1));
            }
            /*
            if (imports.length() > 0) {
                attributes.putValue(IMPORT_PACKAGE, imports.substring(0, imports.length() - 1));
            }
            */
            if (classpath.length() > 0) {
                attributes.putValue(BUNDLE_CLASSPATH, classpath.substring(0, classpath.length() - 1));
            }
            // The system bundle has incomplete javax.transaction* packages exported
            attributes.putValue(DYNAMICIMPORT_PACKAGE, "javax.transaction;version=\"1.1\",javax.transaction.xa;version=\"1.1\",*");

            return manifest;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Generates a library bundle from a list of third-party JARs.
     *
     * @param jarFiles
     * @param bundleSymbolicName The Bundle-SymbolicName
     * @param bundleVersion The Bundle-Version
     * @return
     * @throws IOException
     */
    static InputStream thirdPartyLibraryBundle(Collection<URL> jarFiles, String bundleSymbolicName, String bundleVersion)
        throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Manifest mf = thirdPartyLibraryBundleManifest(jarFiles, bundleSymbolicName, bundleVersion);
        JarOutputStream jos = new JarOutputStream(bos, mf);
        jos.close();
        return new ByteArrayInputStream(bos.toByteArray());
    }
    
    static InputStream thirdPartyLibraryBundle(Collection<URL> jarFiles, Manifest manifest) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StringBuffer classpath = new StringBuffer();
        for (URL jarFile : jarFiles) {
            classpath.append("\"external:");
            classpath.append(file(jarFile).getPath().replace(File.separatorChar, '/'));
            classpath.append("\",");
        }

        if (classpath.length() > 0) {
            manifest.getMainAttributes().putValue(BUNDLE_CLASSPATH, classpath.substring(0, classpath.length() - 1));
        }

        JarOutputStream jos = new JarOutputStream(bos, manifest);
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
        URI uri = toURI(url);

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

    static URI toURI(URL url) {
        File file = file(url);
        if (file != null) {
            return file.toURI();
        } else {
            return createURI(url.toString());
        }
    }

    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    static URI createURI(String uri) {
        if (uri == null) {
            return null;
        }
        if (uri.indexOf('%') != -1) {
            // Avoid double-escaping
            return URI.create(uri);
        }
        int index = uri.indexOf(':');
        String scheme = null;
        String ssp = uri;
        if (index != -1) {
            scheme = uri.substring(0, index);
            ssp = uri.substring(index + 1);
        }
        try {
            return new URI(scheme, ssp, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
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
        URI uri = toURI(url);

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
        if (!target.exists()) {
            return;
        }
        location = target.toURI().toString();

        // For development mode, copy the MANIFEST.MF file to the bundle location as it's
        // initially outside of target/classes, at the root of the project.
        if (location.endsWith("/target/classes/")) {
            File targetManifest = new File(target, "META-INF/MANIFEST.MF");
            File sourceManifest = new File(target.getParentFile().getParentFile(), "META-INF/MANIFEST.MF");
            if (!sourceManifest.isFile()) {
                return;
            }
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
            if (manifest != null) {
                bundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
            }
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
    private static void collectClasspathEntries(File directory, Set<URL> urls, FilenameFilter filter, boolean recursive)
        throws MalformedURLException {
        File[] files = directory.listFiles(filter);
        if (files != null) {
            int count = 0;
            for (File file : files) {
                if (recursive && file.isDirectory()) {
                    collectClasspathEntries(file, urls, filter, recursive);
                } else {
                    urls.add(file.toURI().toURL());
                    count++;
                }
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
     * Collect development .../ target/classes directories in the given directory.
     *
     * @param directory
     * @param urls
     * @param filter
     * @throws MalformedURLException
     */
    private static void collectTargetClassesClasspathEntries(File directory, Set<URL> urls, FilenameFilter filter)
        throws MalformedURLException {
        File[] files = directory.listFiles();
        if (files != null) {
            int count = 0;
            for (File file : files) {
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
            if (count != 0 && logger.isLoggable(Level.FINE)) {
                logger.fine("Runtime classpath: " + count
                    + " classes folder"
                    + (count > 1 ? "s" : "")
                    + " from "
                    + directory.toString());
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
    private static void collectDistributionClasspathEntries(String directory,
                                                            Set<URL> jarDirectoryURLs,
                                                            Set<URL> jarURLs,
                                                            FilenameFilter filter) throws MalformedURLException {
        File directoryFile = new File(directory);
        URL directoryURL = directoryFile.toURI().toURL();
        if (!jarDirectoryURLs.contains(directoryURL) && directoryFile.exists()) {

            // Collect files under the given directory
            jarDirectoryURLs.add(directoryURL);
            collectClasspathEntries(directoryFile, jarURLs, filter, false);

            // Collect files under <directory>/modules
            File modulesDirectory = new File(directoryFile, "modules");
            URL modulesDirectoryURL = modulesDirectory.toURI().toURL();
            if (!jarDirectoryURLs.contains(modulesDirectoryURL) && modulesDirectory.exists()) {
                jarDirectoryURLs.add(modulesDirectoryURL);
                collectClasspathEntries(modulesDirectory, jarURLs, filter, true);
            }

            // Collect files under <directory>/lib
/* SL - this is commented out to prevent the jars in the lib dir being installed as 
 *      OSGi bundles. There must have been a time (1.x?) when lib jars would have been 
 *      installed but now the shaded jars live in lib and we don't want to install them
 *             
            File libDirectory = new File(directoryFile, "lib");
            URL libDirectoryURL = libDirectory.toURI().toURL();
            if (!jarDirectoryURLs.contains(libDirectoryURL) && libDirectory.exists()) {
                jarDirectoryURLs.add(libDirectoryURL);
                collectClasspathEntries(libDirectory, jarURLs, filter, true);
            }
*/            
        }
    }

    private static boolean isMavenTestMode() {
        return getProperty("surefire.test.class.path") != null || getProperty("surefire.real.class.path") != null
            || getProperty("localRepository") != null;
    }

    /**
     * Determine the Tuscany runtime classpath entries.
     *
     * @param useDistribution
     * @param useAppClasspath
     * @param useModulesDirectory
     * @return
     */
    static Set<URL> runtimeClasspathEntries(boolean useDistribution,
                                            boolean useAppClasspath,
                                            boolean useModulesDirectory) throws FileNotFoundException,
        URISyntaxException, MalformedURLException {

        // Build list of runtime JARs
        Set<URL> jarDirectoryURLs = new HashSet<URL>();
        Set<URL> jarURLs = new HashSet<URL>();

        // Determine the path to the launcher class
        URI uri;
        try {
            uri = codeLocation(NodeLauncherUtil.class);
        } catch (Exception e) {
            uri = URI.create("");
        }

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
                        collectClasspathEntries(jarDirectory, jarURLs, new StandAloneJARFileNameFilter(), true);

                        File homeDirectory = jarDirectory.getParentFile();
                        if (homeDirectory != null && homeDirectory.exists()) {
                            collectDistributionClasspathEntries(homeDirectory.getAbsolutePath(),
                                                                jarDirectoryURLs,
                                                                jarURLs,
                                                                new StandAloneJARFileNameFilter());
                        }
                    }
                }
            }
        } else if (uri.getPath().endsWith("/target/classes/")) {

            // Development mode, we're running off classes in a workspace
            // and not from Maven surefire, collect all bundles in the workspace
            if (useModulesDirectory) {
                if (!isMavenTestMode()) {
                    File file = new File(uri);
                    if (file.exists()) {
                        File moduleDirectory = file.getParentFile().getParentFile();
                        if (moduleDirectory != null) {
                            File modulesDirectory = moduleDirectory.getParentFile();
                            if (modulesDirectory != null && modulesDirectory.exists()
                                && modulesDirectory.getName().equals("modules")) {
                                collectDevelopmentClasspathEntries(modulesDirectory.getAbsolutePath(),
                                                                   jarDirectoryURLs,
                                                                   jarURLs,
                                                                   new StandAloneDevelopmentClassesFileNameFilter());
                                // Added Mike Edwards, 09/04/2009
                                // Get hold of the Libraries that are used by the Tuscany modules
                                collectDevelopmentLibraryEntries(modulesDirectory, jarDirectoryURLs, jarURLs);
                            } // end if
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
                    collectDistributionClasspathEntries(tokens.nextToken(),
                                                        jarDirectoryURLs,
                                                        jarURLs,
                                                        new StandAloneJARFileNameFilter());
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

        int count = urls.size();
        // Collect JARs from the URLClassLoader's classpath
        if (cl instanceof URLClassLoader) {
            URL[] jarURLs = ((URLClassLoader)cl).getURLs();
            if (jarURLs != null) {
                for (URL jarURL : jarURLs) {
                    urls.add(jarURL);
                    try {
                        urls.addAll(manifestClassPath(jarURL));
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
                count = urls.size() - count;

                if (count != 0 && logger.isLoggable(Level.FINE)) {
                    logger.fine("Runtime classpath: " + count
                        + " JAR"
                        + (count > 1 ? "s" : "")
                        + " from application classpath.");
                }
            }
        }
    }

    static Set<URL> manifestClassPath(URL jarFile) throws Exception {
        Set<URL> urls = new HashSet<URL>();
        if (jarFile != null) {
            Manifest mf = null;
            if ("file".equals(jarFile.getProtocol())) {
                File f = file(jarFile);
                if (f.isDirectory()) {
                    File mfFile = new File(f, "META-INF/MANIFEST.MF");
                    if (mfFile.isFile()) {
                        FileInputStream is = new FileInputStream(mfFile);
                        mf = new Manifest(is);
                        is.close();
                    }
                } else if (f.isFile()) {
                    JarInputStream jar = new JarInputStream(jarFile.openStream());
                    mf = jar.getManifest();
                    jar.close();
                }
            }
            if (mf != null) {
                String cp = mf.getMainAttributes().getValue("Class-Path");
                if (cp != null) {
                    StringTokenizer st = new StringTokenizer(cp);
                    while (st.hasMoreTokens()) {
                        URL entry = new URL(jarFile.toURI().toURL(), st.nextToken()).toURI().toURL();
                        urls.add(entry);
                    }
                }
            }
        }
        return urls;
    }

    /**
     * A file name filter used to filter JAR files.
     */
    private static class StandAloneJARFileNameFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            name = name.toLowerCase();

            if (new File(dir, name).isDirectory()) {
                return true;
            }

            // Filter out the Tomcat and Webapp hosts
            if (name.startsWith("tuscany-host-tomcat") || name.startsWith("tuscany-host-webapp")) {
                //FIXME This is temporary
                return false;
            }

            // Don't include the sources jar files
            if (name.endsWith("-sources.jar")) {
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
                if (dirPath.endsWith("host-tomcat/target") || dirPath.endsWith("host-webapp/target")) {
                    //FIXME This is temporary
                    return false;
                }
                return true;
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
     * A file name filter used to filter the libraries in the \java\sca\distribution\all\target\modules
     * directory
     */
    private static class DistributionLibsFileNameFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            name = name.toLowerCase();

            // Include subdirectories
            if (new File(dir, name).isDirectory()) {
                return true;
            }

            // Filter out the Tuscany SCA jars - since the development versions of these are used
            // from the \target\classes directories...
            if (name.startsWith("tuscany")) {
            	// tuscany-sdo jars don't form part of the SCA modules...
            	if (name.startsWith("tuscany-sdo")) return true;
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
        } // end accept
    } // end DistributionLibsFileNameFilter

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
    static private URI codeLocation(Class<?> clazz) {
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        return toURI(url);
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
    private static void collectDevelopmentClasspathEntries(String directory,
                                                           Set<URL> jarDirectoryURLs,
                                                           Set<URL> jarURLs,
                                                           FilenameFilter filter) throws MalformedURLException {
        File directoryFile = new File(directory);
        URL directoryURL = directoryFile.toURI().toURL();
        if (!jarDirectoryURLs.contains(directoryURL) && directoryFile.exists()) {

            // Collect files under the given directory
            jarDirectoryURLs.add(directoryURL);
            collectTargetClassesClasspathEntries(directoryFile, jarURLs, filter);

        }
    } // end collectDevelopmentClasspathEntries

    /**
     * Collect the dependent Library JAR files for the development use of Tuscany
     * It is assumed that these live in the \java\sca\distribution\all\target\modules
     * directory, where the development modules live in \java\sca\modules, but that
     * same directory also contains prebuilt versions of the Tuscany JARs, which must be
     * filtered out so as not to clash with the development versions of the code
     *
     * @param directory - the \java\sca\modules directory
     * @param jarDirectoryURLs
     * @param jarURLs
     * @throws MalformedURLException
     */
    private static void collectDevelopmentLibraryEntries(File modulesDirectory,
                                                         Set<URL> jarDirectoryURLs,
                                                         Set<URL> jarURLs) throws MalformedURLException {
        // Get the \java\sca directory
        File rootDirectory = modulesDirectory.getParentFile();
        // Get the \java\sca\distribution\all\target\modules
        String sep = File.separator;
        File libsDirectory = new File(rootDirectory, "distribution" + sep + "all" + sep + "target" + sep + "modules");
        URL libsURL = libsDirectory.toURI().toURL();
        if (!jarDirectoryURLs.contains(libsURL) && libsDirectory.exists()) {
            // Collect files under the libs module directory
            jarDirectoryURLs.add(libsURL);
            collectClasspathEntries(libsDirectory, jarURLs, new DistributionLibsFileNameFilter(), true);
        } // end if
    } // end collectDevelopmentLibraryEntries

    /**
     * Generate a gateway bundle that aggregate other bundles to handle split packages
     * @param bundleSymbolicNames
     * @throws FileNotFoundException
     * @throws IOException
     */
    static InputStream generateGatewayBundle(Collection<String> bundleSymbolicNames, String bundleVersion, boolean reexport)
        throws IOException {
        Manifest manifest = new Manifest();
        Attributes attrs = manifest.getMainAttributes();
        StringBuffer requireBundle = new StringBuffer();
        for (String name : new HashSet<String>(bundleSymbolicNames)) {
            requireBundle.append(name).append(";").append(RESOLUTION_DIRECTIVE).append(":=")
                .append(RESOLUTION_OPTIONAL);
            if (reexport) {
                requireBundle.append(";").append(VISIBILITY_DIRECTIVE).append(":=").append(VISIBILITY_REEXPORT);
            }
            requireBundle.append(",");
        }
        int len = requireBundle.length();
        if (len > 0 && requireBundle.charAt(len - 1) == ',') {
            requireBundle.deleteCharAt(len - 1);
            attrs.putValue(REQUIRE_BUNDLE, requireBundle.toString());
            attrs.putValue("Manifest-Version", "1.0");
            attrs.putValue("Implementation-Vendor", "The Apache Software Foundation");
            attrs.putValue("Implementation-Vendor-Id", "org.apache");
            if (bundleVersion != null) {
                attrs.putValue(BUNDLE_VERSION, bundleVersion);
            }
            attrs.putValue(BUNDLE_MANIFESTVERSION, "2");
            attrs.putValue(BUNDLE_SYMBOLICNAME, GATEWAY_BUNDLE);
            attrs.putValue(BUNDLE_NAME, "Apache Tuscany SCA Gateway Bundle");
            attrs.putValue(BUNDLE_VENDOR, "The Apache Software Foundation");
            attrs.putValue(EXPORT_PACKAGE, "META-INF.services");
            attrs.putValue(DYNAMICIMPORT_PACKAGE, "*");
            attrs.putValue(BUNDLE_ACTIVATIONPOLICY, ACTIVATION_LAZY);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            JarOutputStream jos = new JarOutputStream(bos, manifest);
            jos.close();
            return new ByteArrayInputStream(bos.toByteArray());
        } else {
            return null;
        }
    }

}
