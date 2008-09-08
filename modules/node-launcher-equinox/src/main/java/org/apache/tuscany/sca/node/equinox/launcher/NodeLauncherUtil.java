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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Common functions and constants used by the admin components.
 *
 * @version $Rev$ $Date$
 */
final class NodeLauncherUtil {

    private static final String DOMAIN_MANAGER_LAUNCHER_BOOTSTRAP =
        "org.apache.tuscany.sca.domain.manager.launcher.DomainManagerLauncherBootstrap";

    private static final String NODE_IMPLEMENTATION_DAEMON_BOOTSTRAP =
        "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationDaemonBootstrap";

    private static final String NODE_IMPLEMENTATION_LAUNCHER_BOOTSTRAP =
        "org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationLauncherBootstrap";

    /**
     * Collect JAR files under the given directory.
     * 
     * @p @param contributions
     * @param bundleContext TODO
     * @throws LauncherException
     */
    static Object node(String configurationURI,
                       String compositeURI,
                       String compositeContent,
                       Contribution[] contributions,
                       ClassLoader contributionClassLoader,
                       BundleContext bundleContext) throws LauncherException {
        try {

            Bundle bundle = null;
            for (Bundle b : bundleContext.getBundles()) {
                if ("org.apache.tuscany.sca.implementation.node.runtime".equals(b.getSymbolicName())) {
                    bundle = b;
                    break;
                }
            }
            if (bundle == null) {
                throw new IllegalStateException(
                                                "Bundle org.apache.tuscany.sca.implementation.node.runtime is not installed");
            }
            // Use Java reflection to create the node as only the runtime class
            // loader knows the runtime classes required by the node
            String className = NODE_IMPLEMENTATION_LAUNCHER_BOOTSTRAP;
            Class<?> bootstrapClass = bundle.loadClass(NODE_IMPLEMENTATION_LAUNCHER_BOOTSTRAP);

            Object bootstrap;
            if (configurationURI != null) {

                // Construct the node with a configuration URI
                bootstrap = bootstrapClass.getConstructor(String.class).newInstance(configurationURI);

            } else if (contributionClassLoader != null) {

                // Construct the node with a compositeURI and a classloader
                Constructor<?> constructor = bootstrapClass.getConstructor(String.class, ClassLoader.class);
                bootstrap = constructor.newInstance(compositeURI, contributionClassLoader);

            } else if (compositeContent != null) {

                // Construct the node with a composite URI, the composite content and
                // the URIs and locations of a list of contributions
                Constructor<?> constructor =
                    bootstrapClass.getConstructor(String.class, String.class, String[].class, String[].class);
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
                Constructor<?> constructor =
                    bootstrapClass.getConstructor(String.class, String[].class, String[].class);
                String[] uris = new String[contributions.length];
                String[] locations = new String[contributions.length];
                for (int i = 0; i < contributions.length; i++) {
                    uris[i] = contributions[i].getURI();
                    locations[i] = contributions[i].getLocation();
                }
                bootstrap = constructor.newInstance(compositeURI, uris, locations);
            }

            Object node = bootstrapClass.getMethod("getNode").invoke(bootstrap);
            try {
                Class<?> type = Class.forName("org.apache.tuscany.sca.node.SCANodeFactory");
                type = type.getDeclaredClasses()[0];
                return type.getMethod("createProxy", Class.class, Object.class).invoke(null, type, node);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
            return node;

        } catch (Exception e) {
            NodeLauncher.logger.log(Level.SEVERE, "SCA Node could not be created", e);
            throw new LauncherException(e);
        } finally {
            // 
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
    
    static Pattern pattern = Pattern.compile("-([0-9.]+)");

    private static String version(String jarFile) {
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

    private static void addPackages(String jarFile, Set<String> packages) throws IOException {
        String version = ";version=" + version(jarFile);
        ZipInputStream is = new ZipInputStream(new FileInputStream(file(new URL(jarFile))));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (!entry.isDirectory() && entryName != null && entryName.length() > 0 &&
                !entryName.startsWith(".") && !entryName.startsWith("META-INF") &&
                entryName.lastIndexOf("/") > 0) {
                String pkg = entryName.substring(0, entryName.lastIndexOf("/")).replace('/', '.') + version;
                packages.add(pkg);
            }
        }
        is.close();
    }

    static Manifest libraryManifest(String[] jarFiles) throws IllegalStateException {
        try {

            // List exported packages and bundle classpath entries
            StringBuffer classpath = new StringBuffer();
            StringBuffer exports = new StringBuffer();
            Set<String> packages = new HashSet<String>(); 
            for (String jarFile: jarFiles) {
                addPackages(jarFile, packages);
                classpath.append("\"external:");
                classpath.append(file(new URL(jarFile)).getAbsolutePath().replace(File.separatorChar, '/'));
                classpath.append("\",");
            }
            for (String pkg: packages) {
                exports.append(pkg);
                exports.append(',');
            }
    
            // Create a manifest
            Manifest manifest = new Manifest();
            Attributes attributes = manifest.getMainAttributes();
            attributes.putValue("Manifest-Version", "1.0");
            attributes.putValue(BUNDLE_MANIFESTVERSION, "2");
            attributes.putValue(BUNDLE_SYMBOLICNAME, "org.apache.tuscany.sca.node.launcher.equinox.libraries");
            attributes.putValue(EXPORT_PACKAGE, exports.substring(0, exports.length() -1));
            attributes.putValue(BUNDLE_CLASSPATH, classpath.substring(0, classpath.length() -1));
            attributes.putValue(DYNAMICIMPORT_PACKAGE, "*");
            
            try {
                ManifestElement[] elements = ManifestElement.parseHeader(BUNDLE_CLASSPATH, classpath.substring(0, classpath.length() -1));
                for(ManifestElement e: elements) {
                    System.out.println(Arrays.asList(e.getValueComponents()));
                }
            } catch (BundleException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return manifest;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    static byte[] generateBundle(Manifest mf) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JarOutputStream jos = new JarOutputStream(bos, mf);
        jos.close();
        return bos.toByteArray();
    }
    
    /**
     * Returns the location of this bundle.
     * 
     * @return
     * @throws IOException
     */
    static String bundleLocation() throws IOException, URISyntaxException {
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
    
    static String string(Bundle b, boolean verbose) {
        StringBuffer sb = new StringBuffer();
        sb.append(b.getBundleId()).append(" ").append(b.getSymbolicName());
        int s = b.getState();
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
            sb.append(" ").append(b.getLocation());
            sb.append(" ").append(b.getHeaders());
        }
        return sb.toString();
    }
}
