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

package org.apache.tuscany.sca.extensibility.osgi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscoverer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The ServiceDiscoverer that find META-INF/services/... in installed bundles
 */
public class OSGiServiceDiscoverer implements ServiceDiscoverer {
    private static final Logger logger = Logger.getLogger(OSGiServiceDiscoverer.class.getName());
    private BundleContext context;
    private ClassLoader classLoader;

    public OSGiServiceDiscoverer(BundleContext context) {
        this.context = context;
        this.classLoader = new ClassLoaderImpl();
    }

    /**
     * This class loader provides resource access to META-INF/services/... which is used by
     * many frameworks. OSGi Import-Package and DynmaicImport-Package headers do not support
     * split packages. Another option is to use Require-Bundle header. We can collect the list
     * of bundles and add them as required bundles to a special gateway bundle.
     */
    public class ClassLoaderImpl extends SecureClassLoader {

        public ClassLoaderImpl() {
            super(OSGiServiceDiscoverer.class.getClassLoader());
        }

        /**
         * Open a back-door to expose the META-INF/services resources
         */
        @Override
        protected URL findResource(String name) {
            int index = name.lastIndexOf('/');
            if (index == -1) {
                return null;
            }
            String path = name.substring(0, index);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            if (!path.startsWith("META-INF/services")) {
                return null;
            }

            for (Bundle bundle : context.getBundles()) {
                URL url = bundle.getEntry(name);
                if (url != null) {
                    return url;
                }
            }

            return null;
        }

        /**
         * Open a back-door to expose the META-INF/services resources
         */
        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            int index = name.lastIndexOf('/');
            if (index == -1) {
                return null;
            }
            String path = name.substring(0, index);
            String file = name.substring(index + 1);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            if (!path.startsWith("META-INF/services")) {
                return null;
            }

            Set<URL> urlSet = new HashSet<URL>();

            for (Bundle bundle : context.getBundles()) {
                Enumeration<URL> urls = bundle.findEntries(path, file, false);
                if (urls != null) {
                    urlSet.addAll(Collections.list(urls));
                }
            }
            return Collections.enumeration(urlSet);
        }

    }

    public static class ServiceDeclarationImpl implements ServiceDeclaration {
        private Bundle bundle;
        private URL url;
        private String className;
        private Class<?> javaClass;
        private Map<String, String> attributes;

        public ServiceDeclarationImpl(Bundle bundle, URL url, String className, Map<String, String> attributes) {
            super();
            this.bundle = bundle;
            this.url = url;
            this.className = className;
            this.attributes = attributes;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public String getClassName() {
            return className;
        }

        public Class<?> loadClass() throws ClassNotFoundException {
            if (className == null) {
                return null;
            }
            if (javaClass == null) {
                javaClass = loadClass(className);
            }
            return javaClass;
        }

        public Class<?> loadClass(String className) throws ClassNotFoundException {
            try {
                return bundle.loadClass(className);
            } catch (ClassNotFoundException e) {
                logger.severe(e.getMessage() + ": " + OSGiServiceDiscoverer.toString(bundle));
                throw e;
            }
        }

        public URL getLocation() {
            return url;
        }

        public URL getResource(final String name) {
            return AccessController.doPrivileged(new PrivilegedAction<URL>() {
                public URL run() {
                    return bundle.getResource(name);
                }
            });
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("Bundle: ").append(OSGiServiceDiscoverer.toString(bundle));
            sb.append(" Resource: ").append(url);
            sb.append(" Attributes: ").append(attributes);
            return sb.toString();
        }

    }

    public static String toString(Bundle b) {
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
        return sb.toString();

    }

    /**
     * Parse a service declaration in the form class;attr=value,attr=value and
     * return a map of attributes
     * 
     * @param declaration
     * @return a map of attributes
     */
    protected static Map<String, String> parseServiceDeclaration(String declaration) {
        Map<String, String> attributes = new HashMap<String, String>();
        int index = declaration.indexOf(';');
        if (index != -1) {
            attributes.put("class", declaration.substring(0, index).trim());
            declaration = declaration.substring(index);
        } else {
            int j = declaration.indexOf('=');
            if (j == -1) {
                attributes.put("class", declaration.trim());
                return attributes;
            } else {
                declaration = ";" + declaration;
            }
        }
        StringTokenizer tokens = new StringTokenizer(declaration);
        for (; tokens.hasMoreTokens();) {
            String key = tokens.nextToken("=").substring(1).trim();
            if (key == null)
                break;
            String value = tokens.nextToken(",").substring(1).trim();
            if (value == null)
                break;
            attributes.put(key, value);
        }
        return attributes;
    }

    public BundleContext getBundleContext() {
        return context;
    }

    /**
     * This class loader can be set as the thread context class loader for non-OSGi code
     * @return
     */
    public ClassLoader getContextClassLoader() {
        return classLoader;
    }

    @SuppressWarnings("unchecked")
    public Set<ServiceDeclaration> discover(String serviceName, boolean firstOnly) {
        boolean debug = logger.isLoggable(Level.FINE);
        Set<ServiceDeclaration> descriptors = new HashSet<ServiceDeclaration>();

        serviceName = "META-INF/services/" + serviceName;

        //        int index = serviceName.lastIndexOf('/');
        //        String path = serviceName.substring(0, index);
        //        String file = serviceName.substring(index + 1);

        // long start = System.currentTimeMillis();
        try {
            for (Bundle bundle : context.getBundles()) {
                // Enumeration<URL> urls = bundle.findEntries(path, file, false); // This is expensive
                final URL url = bundle.getEntry(serviceName);
                if (url == null) {
                    continue;
                }

                if (debug) {
                    logger.fine("Reading service provider file: " + url.toExternalForm());
                }
                try {
                    // Allow privileged access to open URL stream. Add FilePermission to added to security
                    // policy file.
                    InputStream is;
                    try {
                        is = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                            public InputStream run() throws IOException {
                                return url.openStream();
                            }
                        });
                    } catch (PrivilegedActionException e) {
                        throw (IOException)e.getException();
                    }
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(is));
                        int count = 0;
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
                                if (className == null) {
                                    // Add a unique class name to prevent equals() from returning true
                                    className = "_class_" + count;
                                    count++;
                                }
                                ServiceDeclarationImpl descriptor =
                                    new ServiceDeclarationImpl(bundle, url, className, attributes);
                                descriptors.add(descriptor);
                                if (firstOnly) {
                                    return descriptors;
                                }
                            }
                        }
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } finally {
//            long end = System.currentTimeMillis();
//            if (true) {
//                logger.info("Duration: " + (end - start) + " ms");
//            }
        }
        return descriptors;

    }

}
