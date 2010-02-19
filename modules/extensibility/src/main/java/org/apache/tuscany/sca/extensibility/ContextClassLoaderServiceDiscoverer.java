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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
            sb.append("Location: ").append(url);
            sb.append(" ClassLoader: ").append(classLoaderReference.get());
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

        public boolean isAssignableTo(Class<?> serviceType) {
            try {
                loadClass();
            } catch (ClassNotFoundException e) {
                // Ignore 
            }
            return (javaClass != null && serviceType.isAssignableFrom(javaClass));
        }
    }

    private WeakReference<ClassLoader> classLoaderReference;

    public ContextClassLoaderServiceDiscoverer() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        this.classLoaderReference = new WeakReference<ClassLoader>(classLoader);
    }
    
    public ClassLoader getContextClassLoader() {
        //return classLoaderReference.get();
        return Thread.currentThread().getContextClassLoader();
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

                for (Map<String, String> attributes : ServiceDeclarationParser.load(url, isPropertyFile)) {
                    String className = attributes.get("class");
                    ServiceDeclarationImpl descriptor = new ServiceDeclarationImpl(url, className, attributes);
                    descriptors.add(descriptor);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return descriptors;

    }

}
