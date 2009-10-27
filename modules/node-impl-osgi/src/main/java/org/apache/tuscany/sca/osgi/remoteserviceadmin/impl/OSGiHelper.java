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

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * 
 */
public class OSGiHelper {
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
        String paths[] = value.trim().split("( |\t|\n|\r|\f|,)+");
        if (paths.length == 0) {
            if (defaultValue != null) {
                paths = new String[] {defaultValue};
            } else {
                paths = new String[0];
            }
        }
        Collection<URL> files = new HashSet<URL>();
        for (String path : paths) {
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

}
