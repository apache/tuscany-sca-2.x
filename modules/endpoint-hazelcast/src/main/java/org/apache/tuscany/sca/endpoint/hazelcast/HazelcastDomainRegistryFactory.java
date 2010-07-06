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

package org.apache.tuscany.sca.endpoint.hazelcast;

import java.util.Properties;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.BaseDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeProperties;

/**
 * The utility responsible for finding the endpoint regstry by the scheme and creating instances for the
 * given domain
 */
public class HazelcastDomainRegistryFactory extends BaseDomainRegistryFactory {
    private final static String[] schemes = new String[] {"multicast", "wka", "tuscany", "hazelcast", "uri"};

    public HazelcastDomainRegistryFactory(ExtensionPointRegistry registry) {
        super(registry);
    }

    protected EndpointRegistry createEndpointRegistry(String endpointRegistryURI, String domainURI) {
        Properties properties = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class).getProperties();
        return new HazelcastEndpointRegistry(registry, properties, domainURI);
    }

    public String[] getSupportedSchemes() {
        return schemes;
    }
}
