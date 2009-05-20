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

package org.apache.tuscany.sca.extensibility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A ServiceDiscoverer that find META-INF/services/... using the Context ClassLoader.
 *
 * @version $Rev$ $Date$
 */
public class ContextClassLoaderServiceDiscoverer implements ServiceDiscoverer {
    private static final Logger logger = Logger.getLogger(ContextClassLoaderServiceDiscoverer.class.getName());

    public class ServiceDeclarationImpl implements ServiceDeclaration {
        private URL url;
        private String className;
        private Class<?> javaClass;
        private Map<String, String> attributes;

        public ServiceDeclarationImpl(URL url, String className, Map<String, String> attributes) {
            super();
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

        public URL getLocation() {
            return url;
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
            return Class.forName(className, false, classLoaderReference.get());
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("ClassLoader: ").append(classLoaderReference.get());
            sb.append(" Attributes: ").append(attributes);
            return sb.toString();
        }

        public URL getResource(final String name) {
            return AccessController.doPrivileged(new PrivilegedAction<URL>() {
                public URL run() {
                    return classLoaderReference.get().getResource(name);
                }
            });
        }

    }

    private WeakReference<ClassLoader> classLoaderReference;

    public ContextClassLoaderServiceDiscoverer() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        this.classLoaderReference = new WeakReference<ClassLoader>(classLoader);
    }

    private List<URL> getResources(final String name) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<List<URL>>() {
                public List<URL> run() throws IOException {
                    List<URL> urls = Collections.list(classLoaderReference.get().getResources(name));
                    return urls;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }

    /**
     * Parse a service declaration in the form class;attr=value,attr=value and
     * return a map of attributes
     *
     * @param declaration
     * @return a map of attributes
     */
    private static Map<String, String> parseServiceDeclaration(String declaration) {
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

    public ServiceDeclaration getServiceDeclaration(String name) throws IOException {
        Collection<ServiceDeclaration> declarations = getServiceDeclarations(name);
        if (declarations.isEmpty()) {
            return null;
        } else {
            return declarations.iterator().next();
        }
    }

    public Collection<ServiceDeclaration> getServiceDeclarations(String serviceName) {
        Collection<ServiceDeclaration> descriptors = new HashSet<ServiceDeclaration>();

        // http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/XPathFactory.html
        boolean isPropertyFile = "javax.xml.xpath.XPathFactory".equals(serviceName);
        String name = "META-INF/services/" + serviceName;
        boolean debug = logger.isLoggable(Level.FINE);
        try {
            for (final URL url : getResources(name)) {
                if (debug) {
                    logger.fine("Reading service provider file: " + url.toExternalForm());
                }

                // Allow privileged access to open URL stream. Add FilePermission to added to security
                // policy file.
                InputStream is;
                try {
                    is = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                        public InputStream run() throws IOException {
                            URLConnection connection = url.openConnection();
                            // TUSCANY-2539
                            // Don't cache connections by default to stop Tuscany locking contribution jar files
                            // done here as this is one of the first places we open a stream and the only way to
                            // set the default is to set it on an instance of URLConnection
                            connection.setDefaultUseCaches(false);
                            connection.setUseCaches(false);
                            return url.openStream();
                        }
                    });
                } catch (PrivilegedActionException e) {
                    throw (IOException)e.getException();
                }
                if (isPropertyFile) {
                    // Load as a property file
                    Properties props = new Properties();
                    props.load(is);
                    is.close();
                    for (Map.Entry<Object, Object> e : props.entrySet()) {
                        Map<String, String> attributes = new HashMap<String, String>();
                        String key = (String)e.getKey();
                        String value = (String)e.getValue();
                        // Unfortunately, the xalan file only has the classname
                        if ("".equals(value)) {
                            value = key;
                            key = "";
                        }
                        if (!"".equals(key)) {
                            attributes.put(key, value);
                            attributes.put("uri", key);
                        }
                        attributes.put("class", value);
                        ServiceDeclarationImpl descriptor = new ServiceDeclarationImpl(url, value, attributes);
                        descriptors.add(descriptor);
                    }
                    continue;
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
                            ServiceDeclarationImpl descriptor = new ServiceDeclarationImpl(url, className, attributes);
                            descriptors.add(descriptor);
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
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return descriptors;

    }

}
