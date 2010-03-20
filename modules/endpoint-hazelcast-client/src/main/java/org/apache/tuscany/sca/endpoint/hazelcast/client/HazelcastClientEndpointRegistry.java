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

package org.apache.tuscany.sca.endpoint.hazelcast.client;

import java.util.Map;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.endpoint.hazelcast.HazelcastEndpointRegistry;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

/**
 * An EndpointRegistry using a Hazelcast Native Client
 */
public class HazelcastClientEndpointRegistry extends HazelcastEndpointRegistry {

    HazelcastClient hazelcastClient;

    public HazelcastClientEndpointRegistry(ExtensionPointRegistry registry,
                                     Map<String, String> attributes,
                                     String domainRegistryURI,
                                     String domainURI) {
        super(registry, attributes, domainRegistryURI, domainURI);
    }

    @Override
    public void start() {
        if (endpointMap != null) {
            throw new IllegalStateException("The registry has already been started");
        }
        initHazelcastClientInstance();
        endpointMap = hazelcastClient.getMap(configURI.getDomainName() + "/Endpoints");
        endpointOwners = hazelcastClient.getMultiMap(configURI.getDomainName() + "/EndpointOwners");
    }

    @Override
    public void stop() {
        if (hazelcastClient != null) {
            hazelcastClient.shutdown();
            hazelcastClient = null;
            endpointMap = null;
        }
    }

    private void initHazelcastClientInstance() {
        if (configURI.getRemotes().size() < 1) {
            throw new IllegalArgumentException("Must specify remote IP address(es) for domain");
        }
        this.hazelcastClient = HazelcastClient.newHazelcastClient(configURI.getDomainName(), configURI.getPassword(), configURI.getRemotes().toArray(new String[0]));
    }

    @Override
    public HazelcastInstance getHazelcastInstance() {
        return hazelcastClient;
    }

}
