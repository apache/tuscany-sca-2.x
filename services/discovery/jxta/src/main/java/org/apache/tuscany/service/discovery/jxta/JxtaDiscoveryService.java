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

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.spi.services.discovery.AbstractDiscoveryService;
import org.apache.tuscany.spi.services.domain.DomainModelService;

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
    @Override
    public void onStart() throws JxtaException {
        
        try {
            
            pipeReceiver = PipeReceiver.newInstance(this);
            
            RuntimeInfo runtimeInfo = getRuntimeInfo();
            URI domain = runtimeInfo.getDomain();
            // TODO Move profile from StandaloneRuntimeInfo to RuntimeInfo
            String profile = null;  
            
            pipeReceiver.start(domain, profile);
            
            // TODO Use pipe sender to notify coming alive
            
        } catch (PeerGroupException ex) {
            throw new JxtaException(ex);
        } catch (IOException ex) {
            throw new JxtaException(ex);
        }
        
    }
    
    /**
     * Stops the discovery service.
     */
    @Override
    protected void onStop() {
        
        // TODO Use pipe sender to notify shutdown
        pipeReceiver.stop();
    }

}
