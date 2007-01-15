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

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.services.domain.DomainModelService;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * Abstract implementation of the discovery service.
 * 
 * @version $Revision$ $Date$
 *
 */
public abstract class AbstractDiscoveryService implements DiscoveryService {

    /** Domain model service. */
    private DomainModelService domainModelService;
    
    /** Runtime info. */
    private RuntimeInfo runtimeInfo;
    
    /**
     * Makes a reference to the domain model service available to the discovery service. 
     * This is required by the dicovery service to propogate any changes in the domain 
     * topology back to the admin server.
     * 
     * @param domainModelService Domain model service used for callbacks.
     */
    @Autowire
    public final void setDomainModelService(DomainModelService domainModelService) {
        this.domainModelService = domainModelService;
    }
    
    /**
     * Sets the runtime info for the runtime using the discovery service.
     * 
     * @param runtimeInfo Runtime info for the runtime using the discovery service.
     */
    @Autowire
    public final void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }
    
    /**
     * Starts the discovery service.
     * @throws Any unexpected JXTA exception to bubble up the call stack.
     */
    @Init
    public final void start() {
        
        onStart();
        
        Runnable shutdownHook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
        
    }
    
    /**
     * Stops the discovery service.
     * @throws Any unexpected JXTA exception to bubble up the call stack.
     */
    @Destroy
    public final void stop() {
        onStop();
    }
    
    /**
     * Gets the domain model service used by this discovery service.
     * 
     * @return Domain model service used for callbacks.
     */
    protected final DomainModelService getDomainModelService() {
        return domainModelService;
    }
    
    /**
     * Gets the runtime info for the runtime using the discovery service.
     * 
     * @return Runtime info for the runtime using the discovery service.
     */
    protected final RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }
    
    /**
     * Required to be overridden by sub-classes.
     *
     */
    protected abstract void onStart();
    
    /**
     * Required to be overridden by sub-classes.
     *
     */
    protected abstract void onStop();
    
    /**
     * Shutdown hook.
     */
    private class ShutdownHook implements Runnable {

        public void run() {
            stop();
        }
        
    }

}
