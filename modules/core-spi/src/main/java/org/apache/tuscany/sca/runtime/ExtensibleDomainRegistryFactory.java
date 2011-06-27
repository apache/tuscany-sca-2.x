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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * A delegating DomainRegistryFactory
 */
public class ExtensibleDomainRegistryFactory implements DomainRegistryFactory {
    private final DomainRegistryFactoryExtensionPoint factories;
    private String[] allSchemes;
    private String defaultScheme = "vm";

    public ExtensibleDomainRegistryFactory(ExtensionPointRegistry registry) {
        this.factories = registry.getExtensionPoint(DomainRegistryFactoryExtensionPoint.class);
        RuntimeProperties ps = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class);
        if (ps.getProperties().containsKey("defaultScheme")) {
            defaultScheme = ps.getProperties().getProperty("defaultScheme");
        }
        
    }
    
    public ExtensibleDomainRegistryFactory(DomainRegistryFactoryExtensionPoint factories) {
        this.factories = factories;
    }
    
    public static ExtensibleDomainRegistryFactory getInstance(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        return utilities.getUtility(ExtensibleDomainRegistryFactory.class);
    }

    public void addListener(EndpointListener listener) {
        for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
            factory.addListener(listener);
        }
    }

    public Collection<DomainRegistry> getEndpointRegistries() {
        List<DomainRegistry> registries = new ArrayList<DomainRegistry>();
        for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
            registries.addAll(factory.getEndpointRegistries());
        }
        return registries;
    }

    public DomainRegistry getEndpointRegistry(String endpointRegistryURI, String domainURI) {
        if (domainURI == null) {
            domainURI = getDomainName(endpointRegistryURI);
        }
        if (endpointRegistryURI == null) {
            endpointRegistryURI = factories.getDomainRegistryMapping().get(domainURI);
            if (endpointRegistryURI == null) {
                endpointRegistryURI = domainURI;
            }
        }

        String scheme = endpointRegistryURI == null ? null : URI.create(endpointRegistryURI).getScheme();
        if (scheme == null) {
            
            // See if there is a previously created registry for that domain
            for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
                for (DomainRegistry domainRegistry : factory.getEndpointRegistries()) {
                    if (domainRegistry.getDomainURI().equals(domainURI)) {
                        return domainRegistry;
                    }
                }
            }

            scheme = defaultScheme;
            endpointRegistryURI = scheme + ":" + endpointRegistryURI;
        } else {
            scheme = scheme.toLowerCase();
        }
        for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
            String[] schemes = factory.getSupportedSchemes();
            if (schemes != null && Arrays.asList(schemes).contains(scheme)) {
                DomainRegistry domainRegistry = factory.getEndpointRegistry(endpointRegistryURI, domainURI);
                if (domainRegistry == null) {
                    continue;
                } else {
                    return domainRegistry;
                }
            }
        }
        throw new ServiceRuntimeException("No DomainRegistry can support " + endpointRegistryURI);
    }

    public void removeListener(EndpointListener listener) {
        for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
            factory.removeListener(listener);
        }
    }

    public synchronized String[] getSupportedSchemes() {
        if (allSchemes == null) {
            Set<String> supportedSchemes = new HashSet<String>();
            for (DomainRegistryFactory factory : factories.getDomainRegistryFactories()) {
                String[] schemes = factory.getSupportedSchemes();
                if (schemes != null) {
                    supportedSchemes.addAll(Arrays.asList(schemes));
                }
            }
            allSchemes = supportedSchemes.toArray(new String[supportedSchemes.size()]);
        }
        return allSchemes;
    }

    /**
     * Derive a domain name from a domain URI
     * Examples:
     * Domain URI  -           Domain Name
     * default                 default
     * foo                     foo
     * uri:foo                 foo
     * uri://foo?key=x&key2=y  foo
     * uri://foo/bar           foo/bar
     */
    private static String getDomainName(String domainURI) {
        int scheme = domainURI.indexOf(':');
        int qm = domainURI.indexOf('?');
        if (qm == -1) {
            return domainURI.substring(scheme+1);
        } else {
            return domainURI.substring(scheme+1, qm);
        }
    }

}
