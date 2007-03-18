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
package org.apache.tuscany.service.discovery.jxta.pdp;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.tuscany.service.discovery.jxta.JxtaException;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.peer.PeerID;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;

/**
 * Listener that keeps track of peers in the same peer group.
 * 
 * @version $Revision$ $Date$
 *
 */
public class PeerListener implements DiscoveryListener {
    
    /** Discovery service to use. */
    private DiscoveryService discoveryService;
    
    /** Interval for sending discivery messages. */
    private long interval;
    
    /** Liveness indicator. */
    private AtomicBoolean live = new AtomicBoolean();
    
    /** Owning runtime. */
    private String runtimeId;
    
    /** Available peers. */
    private Map<String, PeerID> availablePeers = new HashMap<String, PeerID>();
    
    /**
     * Initializes the JXTA discovery service.
     * @param discoveryService JXTA discovery service.
     * @param interval Interval between sending discovery messages.
     * @param runtimeId Runtime that owns this peer.
     */
    public PeerListener(DiscoveryService discoveryService, long interval, String runtimeId) {
        this.discoveryService = discoveryService;
        this.interval = interval;
        this.runtimeId = runtimeId;
    }

    /**
     * Sends discovery messages for peer advertisements.
     */
    public void start() {

        live.set(true);
        discoveryService.addDiscoveryListener(this);
        while(live.get()) {
            discoveryService.getRemoteAdvertisements(null, DiscoveryService.PEER, null, null, 5);
            try {
                Thread.sleep(interval);
            } catch(InterruptedException ex) {
                throw new JxtaException(ex);
            }

        }
        
    }
    
    /**
     * returns the peer id for the runtime id.
     * @param runtimeId Runtime id for which peer id is requested.
     * @return Peer id.
     */
    public synchronized PeerID getPeerId(String runtimeId) {
        return availablePeers.get(runtimeId);
    }

    /**
     * Listens for discovery event.
     */
    public synchronized void discoveryEvent(DiscoveryEvent event) {

        DiscoveryResponseMsg res = event.getResponse();
        Enumeration en = res.getAdvertisements();
        if (en != null ) {
            while (en.hasMoreElements()) {
                PeerAdvertisement adv = (PeerAdvertisement) en.nextElement();
                String peerName = adv.getName();
                if(!runtimeId.equals(peerName)) {
                    availablePeers.put(adv.getName(), adv.getPeerID());
                }
            }
        }
        System.err.println("Peer view for " + runtimeId + ": " + availablePeers.keySet());
        
    }
    
    /**
     * Stops the pipe listener.
     */
    public void stop() {
        live.set(false);
    }

}
