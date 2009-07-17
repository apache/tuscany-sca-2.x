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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Bundle activator to receive the BundleContext
 */
public class OSGiImplementationRuntimeActivator implements BundleActivator {
    private static BundleContext bundleContext;
    private ServiceRegistration distributionProvider;

    public void start(BundleContext context) throws Exception {
        bundleContext = context;
        OSGiDistributionProvider provider = new OSGiDistributionProvider(bundleContext);
        distributionProvider =
            bundleContext.registerService(OSGiDistributionProvider.class.getName(), provider, provider.getProperties());
    }

    public void stop(BundleContext context) throws Exception {
        if (distributionProvider != null) {
            distributionProvider.unregister();
        }
        bundleContext = null;
    }

    static BundleContext getBundleContext() {
        return bundleContext;
    }

}
