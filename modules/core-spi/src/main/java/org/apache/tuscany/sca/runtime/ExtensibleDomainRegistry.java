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

package org.apache.tuscany.sca.runtime;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * 
 */
public class ExtensibleDomainRegistry implements DomainRegistryFactory {
    private final DomainRegistryFactoryExtensionPoint factories;

    public ExtensibleDomainRegistry(ExtensionPointRegistry registry) {
        this.factories = registry.getExtensionPoint(DomainRegistryFactoryExtensionPoint.class);
    }
    
    public ExtensibleDomainRegistry(DomainRegistryFactoryExtensionPoint factories) {
        this.factories = factories;
    }

    public void addListener(EndpointListener listener) {
        for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
            factory.addListener(listener);
        }
    }

    public Collection<EndpointRegistry> getEndpointRegistries() {
        List<EndpointRegistry> registries = new ArrayList<EndpointRegistry>();
        for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
            registries.addAll(factory.getEndpointRegistries());
        }
        return registries;
    }

    public EndpointRegistry getEndpointRegistry(String endpointRegistryURI, String domainURI) {
        if (endpointRegistryURI == null) {
            endpointRegistryURI = domainURI;
        }

        URI uri = URI.create(endpointRegistryURI);
        String scheme = uri.getScheme();
        if (scheme == null) {
            
            // See if there is a previously created registry for that domain
            for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
                for (EndpointRegistry endpointRegistry : factory.getEndpointRegistries()) {
                    if (endpointRegistry.getDomainName().equals(domainURI)) {
                        return endpointRegistry;
                    }
                }
            }

            scheme = "vm";
            endpointRegistryURI = "vm:" + endpointRegistryURI;
        } else {
            scheme = scheme.toLowerCase();
        }
        for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
            String[] schemes = factory.getSupportedSchemes();
            if (schemes != null && Arrays.asList(schemes).contains(scheme)) {
                EndpointRegistry endpointRegistry = factory.getEndpointRegistry(endpointRegistryURI, domainURI);
                if (endpointRegistry == null) {
                    continue;
                } else {
                    return endpointRegistry;
                }
            }
        }
        throw new ServiceRuntimeException("No EndpointRegistry can support " + endpointRegistryURI);
    }

    public void removeListener(EndpointListener listener) {
        for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
            factory.removeListener(listener);
        }
    }

    public String[] getSupportedSchemes() {
        return null;
    }

}
