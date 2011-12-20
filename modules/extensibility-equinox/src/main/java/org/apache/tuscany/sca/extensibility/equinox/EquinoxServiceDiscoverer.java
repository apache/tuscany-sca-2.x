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

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDeclarationParser;
import org.apache.tuscany.sca.extensibility.ServiceDiscoverer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.util.tracker.BundleTracker;

/**
 * A ServiceDiscoverer that find META-INF/services/... in installed bundles
 *
 * @version $Rev$ $Date$
 */
public class EquinoxServiceDiscoverer implements ServiceDiscoverer {
    private static final Logger logger = Logger.getLogger(EquinoxServiceDiscoverer.class.getName());

    private BundleContext context;
    private Version version;
    private BundleTracker bundleTracker;
    private BundleTracker tuscanyProviderBundleTracker;

    public EquinoxServiceDiscoverer(BundleContext context) {
        this.context = context;
        Bundle bundle = context.getBundle();
        this.version = getSCAVersion(bundle);
        if (this.version.equals(Version.emptyVersion)) {
            this.version = Version.parseVersion("1.1");
        }
        bundleTracker = new ActiveBundleTracker(context, false);
        bundleTracker.open();
        tuscanyProviderBundleTracker = new ActiveBundleTracker(context, true);
        tuscanyProviderBundleTracker.open();
    }

    public void stop() {
        bundleTracker.close();
        tuscanyProviderBundleTracker.close();
    }

    private Version getSCAVersion(Bundle bundle) {
        String header = (String)bundle.getHeaders().get("SCA-Version");
        return Version.parseVersion(header);
    }

    public class ActiveBundleTracker extends BundleTracker {

        private boolean isTuscanyService;

        /**
         * @param context
         * @param stateMask
         * @param customizer
         */
        public ActiveBundleTracker(BundleContext context, boolean isTuscanyService) {
            super(context, Bundle.INSTALLED | Bundle.RESOLVED | Bundle.ACTIVE | Bundle.STARTING | Bundle.STOPPING, null);
            this.isTuscanyService = isTuscanyService;
        }

        @Override
        public Object addingBundle(Bundle bundle, BundleEvent event) {
            if (!isProviderBundle(bundle, isTuscanyService))
                return null;
            return super.addingBundle(bundle, event);
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
            if (isTuscanyService) {
                Version scaVersion = getSCAVersion(bundle);
                return scaVersion.compareTo(version) == 0;
            }
            return true;
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

        public Class<?> loadClass(final String className) throws ClassNotFoundException {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                    public Class<?> run() throws ClassNotFoundException {
                        return bundle.loadClass(className);
                    }
                });
            } catch (PrivilegedActionException e) {
                throw (ClassNotFoundException)e.getException();
            }
        }

        public URL getLocation() {
            return url;
        }

        public URL getResource(final String name) {
            return AccessController.doPrivileged(new PrivilegedAction<URL>() {
                public URL run() {
                    // Search bundle first using getEntry()
                    URL url = bundle.getEntry(name);
                    // If not found in bundle, try getResource() which looks at imports
                    if (url == null)
                        url = bundle.getResource(name);
                    return url;
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

        @Override
        public Enumeration<URL> getResources(final String name) throws IOException {
            return (Enumeration<URL>) bundle.getResources(name);
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

    public ServiceDeclaration getServiceDeclaration(String name) {
        Collection<ServiceDeclaration> declarations = getServiceDeclarations(name);
        if (declarations.isEmpty()) {
            return null;
        } else {
            return declarations.iterator().next();
        }
    }

    protected Bundle[] getBundles(boolean isTuscanyService) {
        // Use the tracked bundles.
        if (isTuscanyService)
            return tuscanyProviderBundleTracker.getBundles();
        else
            return bundleTracker.getBundles();
    }

    public Collection<ServiceDeclaration> getServiceDeclarations(final String _serviceName) {
        return AccessController.doPrivileged(new PrivilegedAction<Collection<ServiceDeclaration>>() {
          public Collection<ServiceDeclaration> run() {
              boolean debug = logger.isLoggable(Level.FINE);
              Collection<ServiceDeclaration> descriptors = new HashSet<ServiceDeclaration>();
              
              // http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/XPathFactory.html
              boolean isPropertyFile = "javax.xml.xpath.XPathFactory".equals(_serviceName);
              boolean isTuscanyService = _serviceName.startsWith("org.apache.tuscany.sca.");
              
              String serviceName;
              if (_serviceName.startsWith("/")) {
                  // If the service name starts with /, treat it as the entry name
                  serviceName = _serviceName.substring(1);
              } else {
                  // Use JDK SPI pattern
                  serviceName = "META-INF/services/" + _serviceName;
              }
              
              Set<URL> visited = new HashSet<URL>();
              //System.out.println(">>>> getServiceDeclarations()");
              Bundle[] bundles = getBundles(isTuscanyService);
              if (bundles != null) {
                  for (Bundle bundle : bundles) {
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
                                  logger.warning("Unresolved resource " + serviceName + " found in bundle: " + EquinoxServiceDiscoverer.toString(bundle));
                                  try {
                                      bundle.start();
                                  } catch (BundleException e) {
                                      logger.log(Level.SEVERE, "Bundle: " + bundle.getSymbolicName() + " - " + e.getMessage(), e);
                                  }
                                  // urls = Collections.enumeration(Arrays.asList(entry));
                              }
                          }
                      } catch (IOException e) {
                          logger.log(Level.SEVERE, "Bundle: " + bundle.getSymbolicName() + " - " + e.getMessage(), e);
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
                              for (Map<String, String> attributes : ServiceDeclarationParser.load(url, isPropertyFile)) {
                                  String className = attributes.get("class");
                                  ServiceDeclarationImpl descriptor =
                                      new ServiceDeclarationImpl(bundle, url, className, attributes);
                                  descriptors.add(descriptor);
                              }
                          } catch (IOException e) {
                              logger.log(Level.SEVERE, "Bundle: " + bundle.getSymbolicName() + " - " + e.getMessage(), e);
                          }
                      }
                  }
              }
              return descriptors;
          }
        });
    }

    public ClassLoader getContextClassLoader() {
        // Get the bundle classloader for the extensibility bundle that has DynamicImport-Package *
        return getClass().getClassLoader();
    }

}
