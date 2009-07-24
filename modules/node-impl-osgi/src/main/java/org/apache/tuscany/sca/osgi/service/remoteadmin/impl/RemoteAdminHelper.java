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

package org.apache.tuscany.sca.osgi.service.remoteadmin.impl;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * 
 */
public class RemoteAdminHelper {
    private RemoteAdminHelper() {
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

}
