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

import java.io.File;
import java.io.IOException;

import javax.security.cert.CertificateException;
import javax.xml.stream.XMLStreamReader;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.NetPeerGroupFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.protocol.ModuleImplAdvertisement;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.services.discovery.AbstractDiscoveryService;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.omg.CORBA.Any;
import org.osoa.sca.annotations.Property;

/**
 * Discovery service implemented using Apple bonjour.
 * 
 * @version $Revision$ $Date$
 *
 */
public class JxtaDiscoveryService extends AbstractDiscoveryService {
    
    /** Default discovery interval. */
    private static long DEFAULT_INTERVAL = 2000L;

    /** Peer listener. */
    private PeerListener peerListener;

    /** Network platform configurator. */
    private NetworkConfigurator configurator;
    
    /** Work scheduler. */
    private WorkScheduler workScheduler;
    
    /** Interval for sending discivery messages .*/
    private long interval = DEFAULT_INTERVAL;
    
    /** Domain peer group. */
    private PeerGroup domainGroup;

    /**
     * Adds a network configurator for this service.
     * @param configurator Network configurator.
     */
    @Autowire
    public void setConfigurator(NetworkConfigurator configurator) {
        this.configurator = configurator;
    }

    /**
     * Adds a work scheduler for runningbackground discovery operations.
     * @param workScheduler Work scheduler.
     */
    @Autowire
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }
    
    /**
     * Sets the interval at which discovery messages are sent.
     * @param interval Interval at which discovery messages are sent.
     */
    @Property
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * Starts the discovery service.
     * @throws Any unexpected JXTA exception to bubble up the call stack.
     */
    @Override
    public void onStart() throws JxtaException {

        try {

            // Configure the platform
            configure();
            
            String domain = getRuntimeInfo().getDomain().toString();
            createAndJoinDomainGroup(domain);

        } catch (PeerGroupException ex) {
            throw new JxtaException(ex);
        } catch (IOException ex) {
            throw new JxtaException(ex);
        } catch (Exception ex) {
            throw new JxtaException(ex);
        }

    }

    /**
     * Sends a message to the specified runtime.
     * 
     * @param runtimeId Runtime id of recipient.
     * @param content Message content.
     */
    public void sendMessage(final String runtimeId, final XMLStreamReader content) {
        
        if(runtimeId == null) {
            throw new IllegalArgumentException("Runtime id is null");
        }
        if(content == null) {
            throw new IllegalArgumentException("Content id is null");
        }
        
        if(peerListener.isRuntimeAlive(runtimeId)) {
            // Send the message
        }
        
    }

    /**
     * Stops the discovery service.
     */
    @Override
    protected void onStop() {
        peerListener.stop();
    }

    /**
     * Configures the platform.
     *
     */
    private void configure() {

        try {
            
            configurator.setName(getRuntimeInfo().getRuntimeId());
            
            if (configurator.exists()) {
                File pc = new File(configurator.getHome(), "PlatformConfig");
                configurator.load(pc.toURI());
                configurator.save();
            } else {
                configurator.save();
            }
            
        } catch (IOException ex) {
            throw new JxtaException(ex);
        } catch (CertificateException ex) {
            throw new JxtaException(ex);
        }
        
    }

    /**
     * Creates and joins the domain peer group.
     * @return Domain peer group.
     * @throws Exception In case of unexpected JXTA exceptions.
     */
    private void createAndJoinDomainGroup(String domain) throws Exception {
        
        PeerGroup netGroup = new NetPeerGroupFactory().getInterface();
            
        ModuleImplAdvertisement implAdv = netGroup.getAllPurposePeerGroupImplAdvertisement();
        domainGroup = netGroup.newGroup(null, implAdv, domain, "Tuscany domain group");
        
        final DiscoveryService discoveryService = netGroup.getDiscoveryService();
        discoveryService.remotePublish(netGroup.getPeerAdvertisement());
            
        AuthenticationCredential authCred = new AuthenticationCredential(domainGroup, null, null);
        MembershipService membership = domainGroup.getMembershipService();
        Authenticator auth = membership.apply(authCred);
                
        if (auth.isReadyForJoin()){
            membership.join(auth);
            System.err.println("Joined" + domainGroup.getPeerGroupName());
        } else {
            throw new JxtaException("Unable to join domain group");
        }
        
        peerListener = new PeerListener(discoveryService, interval);
        workScheduler.scheduleWork(peerListener);
        
    }

}
