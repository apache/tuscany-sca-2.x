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

package org.apache.tuscany.sca.endpoint.hazelcast.client;

import java.net.BindException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.endpoint.hazelcast.HazelcastEndpointRegistry;
import org.apache.tuscany.sca.endpoint.hazelcast.RegistryConfig;
import org.apache.tuscany.sca.runtime.RuntimeProperties;

import com.hazelcast.client.ClientProperties;
import com.hazelcast.client.ClientProperties.ClientPropertyName;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

/**
 * An EndpointRegistry using a Hazelcast Native Client
 */
public class HazelcastClientEndpointRegistry extends HazelcastEndpointRegistry {

    RegistryConfig rc;
    HazelcastClient hazelcastClient;

    public HazelcastClientEndpointRegistry(ExtensionPointRegistry registry,
                                     Map<String, String> attributes,
                                     String domainRegistryURI,
                                     String domainURI) {
        super(registry, attributes, domainRegistryURI, domainURI);
    }

    @Override
    public void start() {
        if (endpointMap != null) {
            throw new IllegalStateException("The registry has already been started");
        }
        initHazelcastClientInstance();
        endpointMap = hazelcastClient.getMap(rc.getUserid() + "/Endpoints");
        endpointOwners = hazelcastClient.getMultiMap(rc.getUserid() + "/EndpointOwners");
    }

    @Override
    public void stop() {
        if (hazelcastClient != null) {
            hazelcastClient.shutdown();
            hazelcastClient = null;
            endpointMap = null;
        }
    }

    private void initHazelcastClientInstance() {
        if (this.domainURI == null) {
            this.properties = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class).getProperties();
            this.domainURI = properties.getProperty("defaultDomainName", "default");
        }
        this.rc = RegistryConfig.parseConfigURI(domainURI);
        if (rc.getWKAs().size() < 1) {
            String ip = getDefaultWKA();
            if (ip != null) {
                rc.getWKAs().add(ip);
            }
        }
        if (rc.getWKAs().size() < 1) {
            throw new IllegalArgumentException("Must specify remote IP address(es) for domain");
        }
        
        // Hazelcast is outputs a lot on info level log messages which are unnecessary for us,
        // so disable info logging for hazelcast client classes unless fine logging is on for tuscany.
        if (!Logger.getLogger(this.getClass().getName()).isLoggable(Level.CONFIG)) {
            Logger hzl = Logger.getLogger("com.hazelcast");
            if (!hzl.isLoggable(Level.FINE)) {
                hzl.setLevel(Level.WARNING);
            }
        }

        ClientProperties clientProps = ClientProperties.crateBaseClientProperties(rc.getUserid(), rc.getPassword());
        clientProps.setPropertyValue(ClientPropertyName.INIT_CONNECTION_ATTEMPTS_LIMIT, "1");        
        this.hazelcastClient = HazelcastClient.newHazelcastClient(clientProps, rc.getWKAs().toArray(new String[0]));
    }

    @Override
    public HazelcastInstance getHazelcastInstance() {
        return hazelcastClient;
    }

    /**
     * See if there's a local IP listening on port 14820
     */
    protected static String getDefaultWKA() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress addr = ips.nextElement();
                    ServerSocket ss = null;
                    try {
                        ss = new ServerSocket(14820, 0, addr);
                    } catch (BindException e) {
                        return addr.getHostAddress() + ":14820";
                    } finally {
                        if (ss != null) {
                            ss.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }    
    
}
