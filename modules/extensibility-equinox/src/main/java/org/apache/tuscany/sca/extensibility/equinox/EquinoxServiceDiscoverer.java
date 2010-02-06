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

package org.apache.tuscany.sca.extensibility.equinox;

import static org.apache.tuscany.sca.extensibility.ServiceDeclarationParser.parseDeclaration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscoverer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * A ServiceDiscoverer that find META-INF/services/... in installed bundles
 *
 * @version $Rev$ $Date$
 */
public class EquinoxServiceDiscoverer implements ServiceDiscoverer {
    private static final Logger logger = Logger.getLogger(EquinoxServiceDiscoverer.class.getName());

    private BundleContext context;
    private Version version;

    public EquinoxServiceDiscoverer(BundleContext context) {
        this.context = context;
        Bundle bundle = context.getBundle();
        this.version = getSCAVersion(bundle);
        if (this.version.equals(Version.emptyVersion)) {
            this.version = Version.parseVersion("1.1");
        }
    }

    private Version getSCAVersion(Bundle bundle) {
        String header = (String)bundle.getHeaders().get("SCA-Version");
        return Version.parseVersion(header);
    }
    
    /*
    private Map<Bundle, Object> bundles = new ConcurrentHashMap<Bundle, Object>();

    public Object addingBundle(Bundle bundle, BundleEvent event) {
        if (isProviderBundle(bundle)) {
            bundles.put(bundle, bundle);
            System.out.println("Bundle added: " + toString(bundle));
            return bundle;
        }
        return null;
    }

    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
    }

    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        bundles.remove(object);
    }
    */

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
            return bundle.loadClass(className);
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
            sb.append("Bundle: ").append(EquinoxServiceDiscoverer.toString(bundle));
            sb.append(" Resource: ").append(url);
            sb.append(" Attributes: ").append(attributes);
            return sb.toString();
        }

        public Bundle getBundle() {
            return bundle;
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

    /**
     * Empty static method to trigger the activation of this bundle.
     */
    public static void init() {
    }

    private static String toString(Bundle b) {
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


    public ServiceDeclaration getServiceDeclaration(String name) throws IOException {
        Collection<ServiceDeclaration> declarations = getServiceDeclarations(name);
        if (declarations.isEmpty()) {
            return null;
        } else {
            return declarations.iterator().next();
        }
    }
    
    private boolean isProviderBundle(Bundle bundle, boolean isTuscanyService) {
        if (bundle.getBundleId() == 0 || bundle.getSymbolicName().startsWith("1.x-osgi-bundle")
            || bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null) {
            // Skip system bundle as it has access to the application classloader
            // Skip the 1.x runtime bundle as this has 1.x services in it
            //    For testing running 1.x and 2.x in same VM. 
            //    Don't know what final form will be yet.
            // Skip bundle fragments too
            return false;
        }
        // FIXME: [rfeng] What bundles should be searched? ACTIVE and STARTING?
        if ((bundle.getState() & Bundle.UNINSTALLED) != 0) {
            return false;
        }
        if (isTuscanyService) {
            Version scaVersion = getSCAVersion(bundle);
            return scaVersion.compareTo(version) == 0;
        }
        return true;
    }
    
    protected Collection<Bundle> getBundles(boolean isTuscanyService) {
        // return bundles.keySet();
        Set<Bundle> set = new HashSet<Bundle>();
        for (Bundle b : context.getBundles()) {
            if (isProviderBundle(b, isTuscanyService)) {
                set.add(b);
            }
            /*
            else {
                if (b.getBundleId() != 0 && isTuscanyService) {
                    logger.warning("Bundle is skipped for service discovery: " + toString(b));
                }
            }
            */
        }
        return set;
    }

    public Collection<ServiceDeclaration> getServiceDeclarations(String serviceName) throws IOException {
        boolean debug = logger.isLoggable(Level.FINE);
        Collection<ServiceDeclaration> descriptors = new HashSet<ServiceDeclaration>();

        // http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/XPathFactory.html
        boolean isPropertyFile = "javax.xml.xpath.XPathFactory".equals(serviceName);
        boolean isTuscanyService = serviceName.startsWith("org.apache.tuscany.sca.");
        serviceName = "META-INF/services/" + serviceName;

        Set<URL> visited = new HashSet<URL>();
        //System.out.println(">>>> getServiceDeclarations()");
        for (Bundle bundle : getBundles(isTuscanyService)) {
//            if (!isProviderBundle(bundle)) {
//                continue;
//            }
            Enumeration<URL> urls = null;
            try {
                // Use getResources to find resources on the classpath of the bundle
                // Please note there are cases that getResources will return null even
                // the bundle containing such entries:
                // 1. There is a match on Import-Package or DynamicImport-Package, and another
                // bundle exports the resource package, there is a possiblity that it doesn't
                // find the containing entry
                // 2. The bundle cannot be resolved, then getResources will return null
                urls = bundle.getResources(serviceName);
                if (urls == null) {
                    URL entry = bundle.getEntry(serviceName);
                    if (entry != null) {
                        logger.warning("Unresolved resource " + serviceName + " found in " + toString(bundle));
                        try {
                            bundle.start();
                        } catch (BundleException e) {
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                        // urls = Collections.enumeration(Arrays.asList(entry));
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            if (urls == null) {
                continue;
            }
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();

                if (!visited.add(url)) {
                    // The URL has already been processed
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
                            attributes.putAll(parseDeclaration(value));
                            ServiceDeclarationImpl descriptor = new ServiceDeclarationImpl(bundle, url, value, attributes);
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

                                Map<String, String> attributes = parseDeclaration(reg);
                                String className = attributes.get("class");
                                if (className == null) {
                                    // Add a unique class name to prevent equals() from returning true
                                    className = "_class_" + count;
                                    count++;
                                }
                                ServiceDeclarationImpl descriptor =
                                    new ServiceDeclarationImpl(bundle, url, className, attributes);
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
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        return descriptors;
    }
    
    public ClassLoader getContextClassLoader() {
        // Get the bundle classloader for the extensibility bundle that has DynamicImport-Package *
        return getClass().getClassLoader();
    }

}
