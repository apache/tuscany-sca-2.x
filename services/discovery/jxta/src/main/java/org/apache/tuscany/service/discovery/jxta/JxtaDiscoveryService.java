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
package org.apache.tuscany.service.discovery.jxta;

import java.io.IOException;
import java.net.URI;

import net.jxta.endpoint.Message;
import net.jxta.exception.PeerGroupException;

import org.apache.tuscany.spi.services.discovery.AbstractDiscoveryService;
import org.apache.tuscany.spi.services.domain.DomainModelService;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * Discovery service implemented using Apple bonjour.
 * 
 * @version $Revision$ $Date$
 *
 */
public class JxtaDiscoveryService extends AbstractDiscoveryService implements MessageListener {
    
    /** Pipe receiver. */
    private PipeReceiver pipeReceiver;
    
    /**
     * Publish the event to indicate that the specified runtime is started.
     * 
     * @param domain Domain in which the runtime is participating.
     * @param profile Name of the runtime profile.
     * @param admin A flag to indicate this is the admin runtime.
     * @throws Any unexpected JXTA exception to bubble up the call stack.
     */
    public void runtimeStarted(URI domain, String profile, boolean admin) throws JxtaException {
        
        try {
            pipeReceiver.start(domain, profile);
        } catch (IOException ex) {
            throw new JxtaException(ex);
        }
        
    }

    /**
     * Callback method for message reception.
     * @param message Message that is received.
     */
    public void onMessage(Message message) {  
        
        DomainModelService domainModelService = getDomainModelService();        
        // TODO Notify the domain model service
    }
    
    /**
     * Starts the discovery service.
     * @throws Any unexpected JXTA exception to bubble up the call stack.
     */
    @Init
    public void start() throws JxtaException {
        
        try {
            pipeReceiver = PipeReceiver.newInstance(this);
        } catch (PeerGroupException ex) {
            throw new JxtaException(ex);
        }
        
    }
    
    /**
     * Stops the discovery service.
     */
    @Destroy
    public void stop() {
        pipeReceiver.stop();
    }

}
