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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.extensibility.impl.LDAPFilter;

/**
 * Service discovery for Tuscany based on J2SE Jar service provider spec.
 * Services are described using configuration files in META-INF/services.
 * Service description specifies a class name followed by optional properties.
 *
 *
 * @version $Rev$ $Date$
 */
public final class ServiceDiscovery implements ServiceDiscoverer {
    private final static Logger logger = Logger.getLogger(ServiceDiscovery.class.getName());
    private final static ServiceDiscovery INSTANCE = new ServiceDiscovery();

    private ServiceDiscoverer discoverer;

    private ServiceDiscovery() {
        super();
    }

    /**
     * Get an instance of Service discovery, one instance is created per
     * ClassLoader that this class is loaded from
     *
     * @return
     */
    public static ServiceDiscovery getInstance() {
        return INSTANCE;
    }

    public ServiceDiscoverer getServiceDiscoverer() {
        if (discoverer != null) {
            return discoverer;
        }
        try {
            // FIXME: This is a hack to trigger the activation of the extensibility-equinox bundle in OSGi
            Class.forName("org.apache.tuscany.sca.extensibility.equinox.EquinoxServiceDiscoverer");
            if (discoverer != null) {
                return discoverer;
            }
        } catch (Throwable e) {
        }
        discoverer = new ContextClassLoaderServiceDiscoverer();
        return discoverer;
    }

    public void setServiceDiscoverer(ServiceDiscoverer sd) {
        if (discoverer != null) {
            throw new IllegalStateException("The ServiceDiscoverer cannot be reset");
        }
        discoverer = sd;
    }

    public Collection<ServiceDeclaration> getServiceDeclarations(String name) throws IOException {
        return getServiceDeclarations(name, false);
    }

    public Collection<ServiceDeclaration> getServiceDeclarations(String name, boolean byRanking) throws IOException {
        Collection<ServiceDeclaration> declarations = getServiceDiscoverer().getServiceDeclarations(name);
        if (!byRanking) {
            return declarations;
        }
        if (!declarations.isEmpty()) {
            List<ServiceDeclaration> declarationList = new ArrayList<ServiceDeclaration>(declarations);
            /*
            for (ServiceDeclaration sd1 : declarations) {
                for (Iterator<ServiceDeclaration> i = declarationList.iterator(); i.hasNext();) {
                    ServiceDeclaration sd2 = i.next();
                    if (sd1 != sd2 && sd1.getAttributes().equals(sd2.getAttributes())) {
                        logger
                            .warning("Duplicate service declarations: " + sd1.getLocation() + "," + sd2.getLocation());
                        i.remove();
                    }
                }
            }
            */
            Collections.sort(declarationList, ServiceComparator.DESCENDING_ORDER);
            return declarationList;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Get the service declaration. If there are more than one services, the one with highest ranking will
     * be returned.
     */
    public ServiceDeclaration getServiceDeclaration(final String name) throws IOException {
        Collection<ServiceDeclaration> declarations = getServiceDeclarations(name, true);
        if (!declarations.isEmpty()) {
            // List<ServiceDeclaration> declarationList = new ArrayList<ServiceDeclaration>(declarations);
            // Collections.sort(declarationList, ServiceComparator.DESCENDING_ORDER);
            return declarations.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Get service declarations that are filtered by the service type. In an OSGi runtime, there
     * might be different versions of the services 
     * @param serviceType
     * @return
     * @throws IOException
     */
    public Collection<ServiceDeclaration> getServiceDeclarations(Class<?> serviceType, boolean byRanking)
        throws IOException {
        Collection<ServiceDeclaration> sds = getServiceDeclarations(serviceType.getName(), byRanking);
        for (Iterator<ServiceDeclaration> i = sds.iterator(); i.hasNext();) {
            ServiceDeclaration sd = i.next();
            if (!sd.isAssignableTo(serviceType)) {
                logger.log(Level.WARNING, "Service provider {0} is not a type of {1}", new Object[] {
                                                                                                     sd,
                                                                                                     serviceType
                                                                                                         .getName()});
                i.remove();
            }
        }
        return sds;
    }

    /**
     * Discover all service providers that are compatible with the service type
     * @param serviceType
     * @return
     * @throws IOException
     */
    public Collection<ServiceDeclaration> getServiceDeclarations(Class<?> serviceType) throws IOException {
        return getServiceDeclarations(serviceType, false);
    }
    
    /**
     * Discover all service providers that are compatible with the service type and match the filter
     * @param serviceType
     * @param filter
     * @return
     * @throws IOException
     */
    public Collection<ServiceDeclaration> getServiceDeclarations(Class<?> serviceType, String filter) throws IOException {
        Collection<ServiceDeclaration> sds = getServiceDeclarations(serviceType, false);
        Collection<ServiceDeclaration> filtered = new ArrayList<ServiceDeclaration>();
        LDAPFilter filterImpl = LDAPFilter.newInstance(filter);
        for(ServiceDeclaration sd: sds) {
            if(filterImpl.match(sd.getAttributes())) {
                filtered.add(sd);
            }
        }
        return filtered;
    }
    
    /**
     * @param serviceName
     * @param filter
     * @return
     * @throws IOException
     */
    public Collection<ServiceDeclaration> getServiceDeclarations(String serviceName, String filter) throws IOException {
        Collection<ServiceDeclaration> sds = getServiceDeclarations(serviceName, false);
        Collection<ServiceDeclaration> filtered = new ArrayList<ServiceDeclaration>();
        LDAPFilter filterImpl = LDAPFilter.newInstance(filter);
        for(ServiceDeclaration sd: sds) {
            if(filterImpl.match(sd.getAttributes())) {
                filtered.add(sd);
            }
        }
        return filtered;
    }

    public ServiceDeclaration getServiceDeclaration(Class<?> serviceType) throws IOException {
        Collection<ServiceDeclaration> sds = getServiceDeclarations(serviceType, true);
        if (sds.isEmpty()) {
            return null;
        } else {
            return sds.iterator().next();
        }
    }

    /**
     * Compare service declarations by ranking
     */
    private static class ServiceComparator implements Comparator<ServiceDeclaration> {
        private final static Comparator<ServiceDeclaration> DESCENDING_ORDER = new ServiceComparator();

        public int compare(ServiceDeclaration o1, ServiceDeclaration o2) {
            int rank1 = 0;
            String r1 = o1.getAttributes().get("ranking");
            if (r1 != null) {
                rank1 = Integer.parseInt(r1);
            }
            int rank2 = 0;
            String r2 = o2.getAttributes().get("ranking");
            if (r2 != null) {
                rank2 = Integer.parseInt(r2);
            }
            return rank2 - rank1; // descending
        }
    }

    public ClassLoader getContextClassLoader() {
        return discoverer.getContextClassLoader();
    }
    
    private static class ClassLoaderDelegate extends ClassLoader {
        private final List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();

        /**
         * @param parent The parent classloaders
         * @param loaders A list of classloaders to be used to load classes or resources
         */
        public ClassLoaderDelegate(ClassLoader parent, Collection<ClassLoader> loaders) {
            super(parent);
            if (loaders != null) {
                for (ClassLoader cl : loaders) {
                    if (cl != null && cl != parent && !classLoaders.contains(cl)) {
                        this.classLoaders.add(cl);
                    }
                }
            }
        }

        @Override
        protected Class<?> findClass(String className) throws ClassNotFoundException {
            for (ClassLoader parent : classLoaders) {
                try {
                    return parent.loadClass(className);
                } catch (ClassNotFoundException e) {
                    continue;
                }
            }
            throw new ClassNotFoundException(className);
        }

        @Override
        protected URL findResource(String resName) {
            for (ClassLoader parent : classLoaders) {
                URL url = parent.getResource(resName);
                if (url != null) {
                    return url;
                }
            }
            return null;
        }

        @Override
        protected Enumeration<URL> findResources(String resName) throws IOException {
            Set<URL> urlSet = new HashSet<URL>();
            for (ClassLoader parent : classLoaders) {
                Enumeration<URL> urls = parent.getResources(resName);
                if (urls != null) {
                    while (urls.hasMoreElements()) {
                        urlSet.add(urls.nextElement());
                    }
                }
            }
            return Collections.enumeration(urlSet);
        }
    }

    private ClassLoader getClassLoader(String serviceProvider) {
        try {
            ServiceDeclaration sd = getServiceDeclaration(serviceProvider);
            if (sd != null) {
                return sd.loadClass().getClassLoader();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
    
    /**
     * Set the context classloader so that it can access the list of service providers
     * @param parent The parent classloader
     * @param serviceProviders A list of service provider names
     * @return The old TCCL if a new one is set, otherwise null
     */
    public ClassLoader setContextClassLoader(ClassLoader parent, String... serviceProviders) {
        List<ClassLoader> loaders = getClassLoaders(serviceProviders);
        return setContextClassLoader(parent, loaders.toArray(new ClassLoader[loaders.size()]));
    }

    private List<ClassLoader> getClassLoaders(String... serviceProviders) {
        List<ClassLoader> loaders = new ArrayList<ClassLoader>();
        for (String sp : serviceProviders) {
            ClassLoader loader = getClassLoader(sp);
            if (loader != null) {
                if (!loaders.contains(loader)) {
                    loaders.add(loader);
                }
            }
        }
        return loaders;
    }

    public ClassLoader setContextClassLoader(ClassLoader parent, ClassLoader... delegates) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        List<ClassLoader> loaders = new ArrayList<ClassLoader>();
        for (ClassLoader loader : delegates) {
            if (loader != null && loader != tccl && loader != parent) {
                if (!loaders.contains(loader)) {
                    loaders.add(loader);
                }
            }
        }
        if (!loaders.isEmpty()) {
            ClassLoader cl = getContextClassLoader();
            if (cl != parent) {
                loaders.add(cl);
            }
            if (tccl != parent) {
                loaders.add(tccl);
            }
            ClassLoader newTccl = new ClassLoaderDelegate(parent, loaders);
            Thread.currentThread().setContextClassLoader(newTccl);
            return tccl;
        } else {
            return null;
        }
    }

}
