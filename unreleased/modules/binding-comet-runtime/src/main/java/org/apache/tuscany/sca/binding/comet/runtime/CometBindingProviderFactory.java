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

package org.apache.tuscany.sca.binding.comet.runtime;

import org.apache.tuscany.sca.binding.comet.CometBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostHelper;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * Factory for binding providers.
 */
public class CometBindingProviderFactory implements BindingProviderFactory<CometBinding> {

    /**
     * Underlying servlet host. Injected by constructor.
     */
    private final ServletHost servletHost;

    /**
     * Constructor.
     */
    public CometBindingProviderFactory(final ExtensionPointRegistry extensionPoints) {
        this.servletHost = ServletHostHelper.getServletHost(extensionPoints);
    }

    @Override
    public Class<CometBinding> getModelType() {
        return CometBinding.class;
    }

    /**
     * Creates a provider for a reference that has comet binding specified in
     * the scdl.
     */
    @Override
    public ReferenceBindingProvider createReferenceBindingProvider(final RuntimeEndpointReference endpoint) {
        return new CometReferenceBindingProvider(endpoint);
    }

    /**
     * Creates a provider for a service that has comet binding specified in the
     * scdl.
     */
    @Override
    public ServiceBindingProvider createServiceBindingProvider(final RuntimeEndpoint endpoint) {
        return new CometServiceBindingProvider(endpoint, this.servletHost);
    }

}
