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
package org.apache.tuscany.osgi.util;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public final class BundleContextUtil {

    private static BundleContext context;
    private static final ServiceReference EMPTY_REFERENCE_ARRAY[] = {};

    private BundleContextUtil() {
    }

    public static void setContext(BundleContext ctx) {
        context = ctx;
    }

    public static ServiceReference[] getServiceReference(String clazz, String filter) throws InvalidSyntaxException {
        ServiceReference[] references;
        references = context.getServiceReferences(clazz, filter);
        if (references == null) {
            references = EMPTY_REFERENCE_ARRAY;
        }
        return references;
    }

    public static Object getService(ServiceReference reference) {
        return context.getService(reference);
    }

    public static Object getService(ServiceReference references[]) {
        if (references == null) {
            return null;
        }
        return context.getService(references[0]);
    }

    public static void addServiceListener(ServiceListener listener, String filter) {
        try {
            if (filter != null) {
                context.addServiceListener(listener, filter);
            } else {
                context.addServiceListener(listener);
            }
        } catch (InvalidSyntaxException e) {
            //FIXME
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void removeServiceListener(ServiceListener listener) {
        context.removeServiceListener(listener);
    }

    public static ServiceRegistration registerService(String serviceName,
                                                      Object serviceFactory,
                                                      Dictionary properties) {
        return context.registerService(serviceName, serviceFactory, properties);
    }

    public static boolean trackServiceReference(String filter, ServiceListener listener) {
        try {
            context.addServiceListener(listener, filter);
            return true;
        } catch (InvalidSyntaxException e) {
            return false;
        }
    }
}
