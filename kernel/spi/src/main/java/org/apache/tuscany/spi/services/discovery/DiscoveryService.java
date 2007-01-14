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
package org.apache.tuscany.spi.services.discovery;

import java.net.URI;

import org.apache.tuscany.spi.services.domain.DomainModelService;

/**
 * Defines the abstraction that allows runtimes participating 
 * in the domain to discover each other and broadcast liveness 
 * to the admin server that holds the domain's runtime physical 
 * model and the domain-wide assembly model..
 * 
 * @version $Revision$ $Date$
 *
 */
public interface DiscoveryService {
    
    /**
     * Makes a reference to the domain model service available to the discovery service. 
     * This is required by the dicovery service to propogate any changes in the domain 
     * topology back to the admin server.
     * 
     * @param domainModelService Domain model service used for callbacks.
     */
    void setDomainModelService(DomainModelService domainModelService);
    
    /**
     * Publish the event to indicate that the specified runtime is started.
     * 
     * @param domain Domain in which the runtime is participating.
     * @param profile Name of the runtime profile.
     * @param admin A flag to indicate this is the admin runtime.
     */
    void runtimeStarted(URI domain, String profile, boolean admin);

}
