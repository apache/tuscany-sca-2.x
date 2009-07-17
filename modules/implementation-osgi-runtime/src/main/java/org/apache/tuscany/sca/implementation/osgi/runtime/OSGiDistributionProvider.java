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

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * OSGi distribution provider for remote services
 */
public class OSGiDistributionProvider {
    /**
     * Registered by the distribution provider on one of its services to indicate the 
     * supported configuration types.
     */
    public static final String REMOTE_CONFIGS_SUPPORTED = "remote.configs.supported"; // String+

    /**
     * Registered by the distribution provider on one of its services to indicate the vocabulary 
     * of implemented intents.
     */
    public static final String REMOTE_INTENTS_SUPPORTED = "remote.intents.supported"; // String+ 

    /**
     * Service Registration property for the name of the Distribution Provider
     * product.
     * <p>
     * The value of this property is of type String.
     */
    static final String PRODUCT_NAME = "osgi.remote.distribution.product";

    /**
     * Service Registration property for the version of the Distribution
     * Provider product.
     * <p>
     * The value of this property is of type String.
     */
    static final String PRODUCT_VERSION = "osgi.remote.distribution.product.version";

    /**
     * Service Registration property for the Distribution Provider product
     * vendor name.
     * <p>
     * The value of this property is of type String.
     */
    static final String VENDOR_NAME = "osgi.remote.distribution.vendor";

    private BundleContext bundleContext;

    public OSGiDistributionProvider(BundleContext bundleContext) {
        super();
        this.bundleContext = bundleContext;
    }

    public Dictionary<String, Object> getProperties() {
        Dictionary headers = bundleContext.getBundle().getHeaders();
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(PRODUCT_NAME, "Apache Tuscany SCA");
        props.put(PRODUCT_VERSION, headers.get(Constants.BUNDLE_VERSION));
        props.put(VENDOR_NAME, headers.get(Constants.BUNDLE_VENDOR));
        props.put(REMOTE_CONFIGS_SUPPORTED, new String[] {"sca"});
        // FIXME: We need to populate the list of intents from the SCA definitions
        props.put(REMOTE_INTENTS_SUPPORTED, new String[] {});
        return props;
    }
}
