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

package org.apache.tuscany.sca.core.assembly.impl;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.runtime.BaseDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

/**
 * The utility responsible for finding the endpoint regstry by the scheme and creating instances for the
 * given domain
 */
public class LocalDomainRegistryFactory extends BaseDomainRegistryFactory {
    private final static String[] schemes = new String[] {"local", "vm"};

    /**
     * @param extensionRegistry
     */
    public LocalDomainRegistryFactory(ExtensionPointRegistry registry) {
        super(registry);
    }

    protected EndpointRegistry createEndpointRegistry(String endpointRegistryURI, String domainURI) {
        EndpointRegistry endpointRegistry =
            new EndpointRegistryImpl(registry, endpointRegistryURI, domainURI);
        return endpointRegistry;
    }

    public String[] getSupportedSchemes() {
        return schemes;
    }
}
