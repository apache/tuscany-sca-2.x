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

import javax.xml.stream.XMLStreamReader;

import net.jxta.exception.PeerGroupException;

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.spi.services.discovery.AbstractDiscoveryService;

/**
 * Discovery service implemented using Apple bonjour.
 * 
 * @version $Revision$ $Date$
 *
 */
public class JxtaDiscoveryService extends AbstractDiscoveryService {
    
    /** Pipe receiver. */
    private PipeReceiver pipeReceiver;
    
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
            String runtimeId = runtimeInfo.getRuntimeId();  
            
            pipeReceiver.start(domain, runtimeId);
            
        } catch (PeerGroupException ex) {
            throw new JxtaException(ex);
        } catch (IOException ex) {
            throw new JxtaException(ex);
        }
        
    }
    
    /**
     * Sends a message to the specified runtime.
     * 
     * @param runtimeId Runtime id of recipient.
     * @param content Message content.
     */
    public void sendMessage(String runtimeId, XMLStreamReader content) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Stops the discovery service.
     */
    @Override
    protected void onStop() {
        pipeReceiver.stop();
    }

}
