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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;

/**
 * Listener that keeps track of peers in the same peer group.
 * 
 * @version $Revision$ $Date$
 *
 */
public class PeerListener implements Runnable, DiscoveryListener {
    
    /** Discovery service to use. */
    private DiscoveryService discoveryService;
    
    /** Interval for sending discivery messages. */
    private long interval;
    
    /** Liveness indicator. */
    private AtomicBoolean live = new AtomicBoolean();
    
    /** Available peers. */
    private List<String> availablePeers = new LinkedList<String>();
    
    /**
     * Initializes the JXTA discovery service.
     * @param discoveryService JXTA discovery service.
     */
    public PeerListener(DiscoveryService discoveryService, long interval) {
        this.discoveryService = discoveryService;
    }

    /**
     * Sends discovery messages for peer advertisements.
     */
    public void run() {

        discoveryService.addDiscoveryListener(this);
        live.set(true);
        
        while(live.get()) {
            discoveryService.getRemoteAdvertisements(null, DiscoveryService.PEER, null, null, 5);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                return;
            }
        }
        
    }
    
    /**
     * Returns the available peers participating in the domain.
     * @return True if the specified runtime is alive.
     */
    public synchronized boolean isRuntimeAlive(String runtimeId) {
        return availablePeers.contains(runtimeId);
    }

    /**
     * Receives asynchronous discovery responses.
     */
    public synchronized void discoveryEvent(DiscoveryEvent discoveryEvent) {
        // TODO Update the list of available peers
    }
    
    /**
     * Stops the pipe listener.
     */
    public void stop() {
        live.set(false);
    }

}
