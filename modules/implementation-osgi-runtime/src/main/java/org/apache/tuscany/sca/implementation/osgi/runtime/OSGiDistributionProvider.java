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

package org.apache.tuscany.sca.implementation.osgi.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.distribution.DistributionProvider;

/**
 * OSGi distribution provider
 */
public class OSGiDistributionProvider implements DistributionProvider {
    private BundleContext bundleContext;
    private Collection<ServiceReference> exposedServices = new ArrayList<ServiceReference>();
    private Collection<ServiceReference> remoteServices = new ArrayList<ServiceReference>();

    public OSGiDistributionProvider(BundleContext bundleContext) {
        super();
        this.bundleContext = bundleContext;
    }

    public Map<String, String> getExposedProperties(ServiceReference sr) {
        return Collections.emptyMap();
    }

    public Collection<ServiceReference> getExposedServices() {
        return exposedServices;
    }

    public Collection<ServiceReference> getRemoteServices() {
        return remoteServices;
    }

    public Dictionary<String, Object> getProperties() {
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(PRODUCT_NAME, "Apache Tuscany SCA");
        props.put(PRODUCT_VERSION, "2.0.0");
        props.put(VENDOR_NAME, "Apache Software Foundation");
        return props;
    }
}
