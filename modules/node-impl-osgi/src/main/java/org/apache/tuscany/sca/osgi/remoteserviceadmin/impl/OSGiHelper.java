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

package org.apache.tuscany.sca.osgi.remoteserviceadmin.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationFactory;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * 
 */
public class OSGiHelper {
    public final static String FRAMEWORK_UUID = "org.osgi.framework.uuid";

    private OSGiHelper() {
    }

    /**
     * In OSGi, the value of String+ can be a single String, String[] or Collection<String>
     * @param value
     * @return
     */
    public static String[] getStringArray(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return new String[] {(String)value};
        } else if (value instanceof Collection) {
            Collection<String> collection = (Collection)value;
            return collection.toArray(new String[collection.size()]);
        }
        return (String[])value;

    }

    public static Collection<String> getStringCollection(Object value) {
        String[] values = getStringArray(value);
        if (values == null) {
            return null;
        } else {
            return Arrays.asList(values);
        }
    }

    public static String[] getStringArray(ServiceReference serviceReference, String propertyName) {
        Object propertyValue = serviceReference.getProperty(propertyName);
        return getStringArray(propertyValue);
    }

    public static Collection<String> getStringCollection(ServiceReference serviceReference, String propertyName) {
        Object propertyValue = serviceReference.getProperty(propertyName);
        return getStringCollection(propertyValue);
    }

    public static Filter createFilter(BundleContext context, String filterValue) {
        if (filterValue == null) {
            return null;
        }
        try {
            return context.createFilter(filterValue);
        } catch (InvalidSyntaxException ex) {
            throw new IllegalArgumentException("Invalid Filter: " + filterValue, ex);
        }
    }

    /**
     * Get a collection of resources that are configured by the given header
     * @param bundle The bundle
     * @param header 
     * @param defaultValue
     * @return
     */
    public static Collection<URL> getConfiguration(Bundle bundle, String header, String defaultValue) {
        String value = (String)bundle.getHeaders().get(header);
        if (value == null) {
            return Collections.emptyList();
        }
        value = value.trim();
        String paths[] = value.split("( |\t|\n|\r|\f|,)+");
        if ("".equals(value) || paths.length == 0) {
            if (defaultValue != null) {
                paths = new String[] {defaultValue};
            } else {
                paths = new String[0];
            }
        }
        Collection<URL> files = new HashSet<URL>();
        for (String path : paths) {
            if ("".equals(path)) {
                // Skip empty ones
                continue;
            }
            if (path.endsWith("/")) {
                path = path + "*.xml";
            }
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            int lastIndex = path.lastIndexOf('/');
            String root = path.substring(0, lastIndex);
            if ("".equals(root)) {
                root = "/";
            }
            String pattern = path.substring(lastIndex + 1);
            Enumeration<URL> entries = bundle.findEntries(root, pattern, false);
            if (entries != null) {
                while (entries.hasMoreElements()) {
                    files.add(entries.nextElement());
                }
            }
        }
        return files;
    }
    
    public static Collection<OSGiProperty> getOSGiProperties(ExtensionPointRegistry registry, ServiceReference reference) {
        FactoryExtensionPoint factoryExtensionPoint = registry.getExtensionPoint(FactoryExtensionPoint.class);
        OSGiImplementationFactory implementationFactory= factoryExtensionPoint.getFactory(OSGiImplementationFactory.class);
        return implementationFactory.createOSGiProperties(reference);
    }
    
    public static OSGiProperty createOSGiProperty(ExtensionPointRegistry registry, String name, Object value) {
        FactoryExtensionPoint factoryExtensionPoint = registry.getExtensionPoint(FactoryExtensionPoint.class);
        OSGiImplementationFactory implementationFactory= factoryExtensionPoint.getFactory(OSGiImplementationFactory.class);
        return implementationFactory.createOSGiProperty(name, value);
    }


    public synchronized static String getFrameworkUUID(BundleContext bundleContext) {
        String uuid = null;
        if (bundleContext != null) {
            uuid = bundleContext.getProperty(FRAMEWORK_UUID);
        } else {
            uuid = System.getProperty(FRAMEWORK_UUID);
        }
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        System.setProperty(FRAMEWORK_UUID, uuid);
        return uuid;
    }  
    
    public static ClassLoader createBundleClassLoader(Bundle bundle) {
        return new BundleClassLoader(bundle);
    }
    
    private static class BundleClassLoader extends ClassLoader {
        private Bundle bundle;
        public BundleClassLoader(Bundle bundle) {
            super(null);
            this.bundle = bundle;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return bundle.loadClass(name);
        }

        @Override
        protected URL findResource(String name) {
            return bundle.getResource(name);
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            Enumeration<URL> urls = bundle.getResources(name);
            if (urls == null) {
                List<URL> list = Collections.emptyList();
                return Collections.enumeration(list);
            } else {
                return urls;
            }
        }
    }    

}
