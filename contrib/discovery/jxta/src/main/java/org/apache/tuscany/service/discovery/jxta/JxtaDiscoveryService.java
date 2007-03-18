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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.security.cert.CertificateException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;
import net.jxta.impl.id.UUID.UUID;
import net.jxta.impl.protocol.ResolverQuery;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.NetPeerGroupFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.resolver.QueryHandler;
import net.jxta.resolver.ResolverService;

import org.apache.tuscany.service.discovery.jxta.pdp.PeerListener;
import org.apache.tuscany.service.discovery.jxta.prp.TuscanyQueryHandler;
import org.apache.tuscany.spi.services.discovery.AbstractDiscoveryService;
import org.apache.tuscany.spi.services.discovery.DiscoveryException;
import org.apache.tuscany.spi.services.work.NotificationListener;
import org.apache.tuscany.spi.services.work.NotificationListenerAdaptor;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.util.stax.StaxUtil;
import org.omg.CORBA.Any;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * Discovery service implemented using Apple bonjour.
 * 
 * @version $Revision$ $Date$
 *
 */
public class JxtaDiscoveryService extends AbstractDiscoveryService {
    
    /** Well known peer group id. */
    private static final TuscanyPeerGroupID PEER_GROUP_ID = 
        new TuscanyPeerGroupID(new UUID("aea468a4-6450-47dc-a288-a7f1bbcc5927"));
    
    /** Default discovery interval. */
    private static long DEFAULT_INTERVAL = 10000L;

    /** Peer listener. */
    private PeerListener peerListener;
    
    /** Resolver service. */
    private ResolverService resolverService;
    
    /** Domain group. */
    private PeerGroup domainGroup;

    /** Network platform configurator. */
    private NetworkConfigurator configurator;
    
    /** Work scheduler. */
    private WorkScheduler workScheduler;
    
    /** Interval for sending discivery messages .*/
    private long interval = DEFAULT_INTERVAL;
    
    /** Started flag. */
    private final AtomicBoolean started = new AtomicBoolean();
    
    /** Message id generator. */
    private final AtomicInteger messageIdGenerator = new AtomicInteger();

    /**
     * Adds a network configurator for this service.
     * @param configurator Network configurator.
     */
    @Reference
    public void setConfigurator(NetworkConfigurator configurator) {
        this.configurator = configurator;
    }

    /**
     * Adds a work scheduler for runningbackground discovery operations.
     * @param workScheduler Work scheduler.
     */
    @Reference
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
    public void onStart() throws DiscoveryException {
        
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    startService();
                } catch(DiscoveryException ex) {
                    throw new JxtaException(ex);
                }
            }
        };
        
        NotificationListener<Runnable> listener = new NotificationListenerAdaptor<Runnable>();        
        workScheduler.scheduleWork(runnable, listener);
        
    }
    
    /**
     * Rusn the discovery service in a different thread.
     */
    private void startService() throws DiscoveryException {

        try {  
            
            configure();            
            createAndJoinDomainGroup();
            
            setupDiscovery();        
            setupResolver();
            
            started.set(true); 
            peerListener.start();
            
        } catch (PeerGroupException ex) {
            throw new DiscoveryException(ex);
        } catch (IOException ex) {
            throw new DiscoveryException(ex);
        } catch (Exception ex) {
            throw new DiscoveryException(ex);
        }
        
    }

    /**
     * Sends a message to the specified runtime.
     * 
     * @param runtimeId Runtime id of recipient. If null, the message is 
     * broadcasted to all runtimes in the domain.
     * @param content Message content.
     * @return The message id. 
     * @throws DiscoveryException In case of discovery errors.
     */
    public int sendMessage(final String runtimeId, final XMLStreamReader content) throws DiscoveryException {
        
        if(content == null) {
            throw new IllegalArgumentException("Content id is null");
        }
        
        PeerID peerID = null;
        if(runtimeId != null) {
            peerID = peerListener.getPeerId(runtimeId);
            if(peerID == null) {
                throw new DiscoveryException("Unrecognized runtime " + runtimeId);
            }
        }
        
        String message = null;
        try {
            StaxUtil.serialize(content);
        } catch(XMLStreamException ex) {
            throw new DiscoveryException(ex);
        }
        
        int messageId = messageIdGenerator.incrementAndGet();
        
        ResolverQuery query = new ResolverQuery();
        query.setHandlerName(TuscanyQueryHandler.class.getSimpleName());
        query.setQuery(message);
        query.setSrc(domainGroup.getPeerID().toString());
        
        if(peerID == null) {
            resolverService.sendQuery(null, query);
        } else {
            resolverService.sendQuery(peerID.toString(), query);
        }
        
        return messageId;
        
    }
    
    /**
     * Checks whether the service is started.
     * @return True if the service is started.
     */
    public boolean isStarted() {
        return started.get();
    }

    /**
     * Stops the discovery service.
     */
    @Override
    protected void onStop() {
        peerListener.stop();
        started.set(false);
    }

    /**
     * Configures the platform.
     *
     */
    private void configure() throws DiscoveryException {

        try {
            
            String runtimeId = getRuntimeInfo().getRuntimeId();
            
            configurator.setName(runtimeId);
            configurator.setHome(new File(runtimeId));
            
            if (configurator.exists()) {
                File pc = new File(configurator.getHome(), "PlatformConfig");
                configurator.load(pc.toURI());
                configurator.save();
            } else {
                configurator.save();
            }
            
        } catch (IOException ex) {
            throw new DiscoveryException(ex);
        } catch (CertificateException ex) {
            throw new DiscoveryException(ex);
        }
        
    }

    /**
     * Creates and joins the domain peer group.
     * @throws Exception In case of unexpected JXTA exceptions.
     */
    private void createAndJoinDomainGroup() throws Exception {
        
        String domain = getRuntimeInfo().getDomain().toString();
        
        PeerGroup netGroup = new NetPeerGroupFactory().getInterface(); 
        ModuleImplAdvertisement implAdv = netGroup.getAllPurposePeerGroupImplAdvertisement();
        domainGroup = netGroup.newGroup(PEER_GROUP_ID, implAdv, domain, "Tuscany domain group");
            
        AuthenticationCredential authCred = new AuthenticationCredential(domainGroup, null, null);
        MembershipService membership = domainGroup.getMembershipService();
        Authenticator auth = membership.apply(authCred);
                
        if (auth.isReadyForJoin()){
            membership.join(auth);
        } else {
            throw new DiscoveryException("Unable to join domain group");
        }
        
    }

    /**
     * Sets up the resolver service.
     */
    private void setupResolver() {
        
        resolverService = domainGroup.getResolverService();
        QueryHandler queryHandler = new TuscanyQueryHandler(resolverService, this);
        resolverService.registerHandler(TuscanyQueryHandler.class.getSimpleName(), queryHandler);
        
    }

    /**
     * Sets up peer discovery service.
     */
    private void setupDiscovery() {
        
        final DiscoveryService discoveryService = domainGroup.getDiscoveryService();
        discoveryService.remotePublish(domainGroup.getPeerAdvertisement());
        peerListener = new PeerListener(discoveryService, interval, getRuntimeInfo().getRuntimeId());
        
    }
    
    /*
     * Well known peer grroup.
     */
    @SuppressWarnings("serial")
    private static class TuscanyPeerGroupID extends net.jxta.impl.id.CBID.PeerGroupID {
        public TuscanyPeerGroupID(UUID uuid) {
            super(uuid);
        }
    }

}
