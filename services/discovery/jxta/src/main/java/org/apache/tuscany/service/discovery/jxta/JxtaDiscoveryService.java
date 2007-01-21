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
import net.jxta.exception.PeerGroupException;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.NetPeerGroupFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.protocol.ModuleImplAdvertisement;

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.services.discovery.AbstractDiscoveryService;
import org.omg.CORBA.Any;

/**
 * Discovery service implemented using Apple bonjour.
 * 
 * @version $Revision$ $Date$
 *
 */
public class JxtaDiscoveryService extends AbstractDiscoveryService {

    /** Pipe receiver. */
    private PipeReceiver pipeReceiver;

    /** Network platform configurator. */
    private NetworkConfigurator configurator;

    /**
     * Adds a network configurator for this service.
     * @param configurator Network configurator.
     */
    @Autowire
    public void setConfigurator(NetworkConfigurator configurator) {
        this.configurator = configurator;
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

            PeerGroup domainGroup = createAndJoinDomainGroup();            
            pipeReceiver = PipeReceiver.newInstance(this, domainGroup);

            RuntimeInfo runtimeInfo = getRuntimeInfo();
            String runtimeId = runtimeInfo.getRuntimeId();

            pipeReceiver.start(domain, runtimeId);

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
    private PeerGroup createAndJoinDomainGroup() throws Exception {
        
        PeerGroup netGroup = new NetPeerGroupFactory().getInterface();
            
        ModuleImplAdvertisement implAdv = netGroup.getAllPurposePeerGroupImplAdvertisement();
        PeerGroup domainGroup = netGroup.newGroup(null, implAdv, "JoinTest", "testing group adv");
        domainGroup.getDiscoveryService().remotePublish(domainGroup.getPeerGroupAdvertisement());
            
        AuthenticationCredential authCred = new AuthenticationCredential(domainGroup, null, null);
        MembershipService membership = domainGroup.getMembershipService();
        Authenticator auth = membership.apply(authCred);
                
        if (auth.isReadyForJoin()){
            membership.join(auth);
        } else {
            throw new JxtaException("Unable to join domain group");
        }
        return domainGroup;
        
    }

}
