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

package org.apache.tuscany.sca.endpoint.hazelcast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.impl.EndpointRegistryImpl;

import com.hazelcast.config.Config;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.Address;

/**
 * tuscany:[domainName]?listen=[port|ip:port]]&password=abc&multicast=[off|port|ip:port]&remotes=ip:port,ip:port,...
 * listen defines the local bind address and port, it defaults to all network interfaces on port 14820 and if that port in use it will try incrementing by one till a free port is found.
 * password is the password other nodes must use to connect to this domain. The default is 'tuscany'. 
 * multicast defines if multicast discover is used and if so what multicast ip group and port is used. The default is multicast is off if remotes= is specified (only for now due to a code limitation that is planned to be fixed), other wise if remotes= is not specified then multicast defaults to 224.5.12.10:51482
 * 
 */

public class HazelcastRegistry extends EndpointRegistryImpl {

    private String endpointRegistryURI;
    private String domainURI;
    private IMap<Object, Object> hazelcastMap;

    public HazelcastRegistry(ExtensionPointRegistry extensionPoints, String endpointRegistryURI, String domainURI) {
        super(extensionPoints, endpointRegistryURI, domainURI);
        init();
    }

    @Override
    public synchronized void addEndpoint(Endpoint endpoint) {
        hazelcastMap.put(endpoint.getURI(), endpoint);
        super.addEndpoint(endpoint);
    }

    @Override
    public synchronized void removeEndpoint(Endpoint endpoint) {
        hazelcastMap.remove(endpoint.getURI());
        super.addEndpoint(endpoint);
    }
    
    @Override
    public synchronized void updateEndpoint(String uri, Endpoint endpoint) {
        // TODO: is updateEndpoint needed?
        throw new UnsupportedOperationException();
    }
    
    protected void init() {
        int listenPort = 0;
        int connectPorts = 0;
        boolean multicast = false;
        try {
            HazelcastInstance hazelcastInstance = createHazelcastInstance(multicast, listenPort, connectPorts);
            String domainName = "";
            hazelcastMap = hazelcastInstance.getMap(domainName);

        
        
        
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
    private HazelcastInstance createHazelcastInstance(boolean multicast, int listenPort, int... connectPorts) throws UnknownHostException {
        Config config = new XmlConfigBuilder().build();
        config.setPort(listenPort);
        config.setPortAutoIncrement(false);

        // declare the interface Hazelcast should bind to
        config.getNetworkConfig().getInterfaces().clear();
        config.getNetworkConfig().getInterfaces().addInterface(InetAddress.getLocalHost().getHostAddress());
        config.getNetworkConfig().getInterfaces().setEnabled(true);

        if (!multicast) {
            config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        }
        
        if (connectPorts.length > 0) {
            TcpIpConfig tcpconfig = config.getNetworkConfig().getJoin().getJoinMembers();
            tcpconfig.setEnabled(true);

            List<Address> lsMembers = tcpconfig.getAddresses();
            lsMembers.clear();
            for (int p : connectPorts) {
                lsMembers.add(new Address(InetAddress.getLocalHost(), p));
            }
        }

        return Hazelcast.newHazelcastInstance(config);
    }
}
