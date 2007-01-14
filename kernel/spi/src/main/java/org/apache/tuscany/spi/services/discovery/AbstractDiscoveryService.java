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

import org.apache.tuscany.spi.services.domain.DomainModelService;

/**
 * Abstract implementation of the discovery service.
 * 
 * @version $Revision$ $Date$
 *
 */
public abstract class AbstractDiscoveryService implements DiscoveryService {

    /** Domain model service. */
    private DomainModelService domainModelService;
    
    /**
     * Makes a reference to the domain model service available to the discovery service. 
     * This is required by the dicovery service to propogate any changes in the domain 
     * topology back to the admin server.
     * 
     * @param domainModelService Domain model service used for callbacks.
     */
    public final void setDomainModelService(DomainModelService domainModelService) {
        this.domainModelService = domainModelService;
    }
    
    /**
     * Gets the domain model service used by this discovery service.
     * @return Domain model service used for callbacks.
     */
    protected final DomainModelService getDomainModelService() {
        return domainModelService;
    }

}
