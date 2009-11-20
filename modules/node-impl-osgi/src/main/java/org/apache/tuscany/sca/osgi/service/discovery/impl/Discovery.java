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
package org.apache.tuscany.sca.osgi.service.discovery.impl;

/**
 * Every Discovery Provider registers a service implementing this interface.
 * This service is registered with extra properties identified at the beginning
 * of this interface to denote the name of the product providing Discovery
 * functionality, its version, vendor, used protocols etc..
 * <p>
 * Discovery allows to publish services exposed for remote access as well as to
 * search for remote services. 
 * <p>
 * Discovery service implementations usually rely on some discovery protocols or
 * other information distribution means.
 *
 * @ThreadSafe
 */
public interface Discovery {

    /**
     * ServiceRegistration property for the name of the Discovery product.
     * <p>
     * Value of this property is of type <code>String</code>.
     */
    String PRODUCT_NAME = "osgi.remote.discovery.product";

    /**
     * ServiceRegistration property for the version of the Discovery product.
     * <p>
     * Value of this property is of type <code>String</code>.
     */
    String PRODUCT_VERSION = "osgi.remote.discovery.product.version";

    /**
     * ServiceRegistration property for the Discovery product vendor name.
     * <p>
     * Value of this property is of type <code>String</code>.
     */
    String VENDOR_NAME = "osgi.remote.discovery.vendor";

    /**
     * ServiceRegistration property that lists the discovery protocols used by
     * this Discovery service.
     * <p>
     * Value of this property is of type
     * <code>Collection (&lt;? extends String&gt;)</code>.
     */
    String SUPPORTED_PROTOCOLS = "osgi.remote.discovery.supported_protocols";
}
