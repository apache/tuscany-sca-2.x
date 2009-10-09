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

import java.util.Collection;
import java.util.List;

/**
 * 
 */
public interface DomainRegistryFactory {
    /**
     * Get the EndpointRegistry for the given registry URI and domain URI
     * @param endpointRegistryURI A URI can be used to connect to the registry, such as vm://localhost
     * or multicast://200.0.100.200:50000/...
     * @param domainURI The domain URI
     * @return
     */
    EndpointRegistry getEndpointRegistry(String endpointRegistryURI, String domainURI);
    
    /**
     * Return all active endpoint registries
     * @return
     */
    Collection<EndpointRegistry> getEndpointRegistries();
    void addListener(EndpointListener listener);
    void removeListener(EndpointListener listener);
    List<EndpointListener> getListeners();
}
