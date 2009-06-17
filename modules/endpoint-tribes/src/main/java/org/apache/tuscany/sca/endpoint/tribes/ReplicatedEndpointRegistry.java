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

package org.apache.tuscany.sca.endpoint.tribes;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.group.GroupChannel;
import org.apache.catalina.tribes.membership.McastService;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.catalina.tribes.tipis.ReplicatedMap;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

/**
 * A replicated EndpointRegistry based on Apache Tomcat Tribes
 */
public class ReplicatedEndpointRegistry implements EndpointRegistry, LifeCycleListener {
    private final static Logger logger = Logger.getLogger(ReplicatedEndpointRegistry.class.getName());
    private static final String MULTICAST_ADDRESS = "228.0.0.100";
    private static final int MULTICAST_PORT = 50000;

    private int port = MULTICAST_PORT;
    private String address = MULTICAST_ADDRESS;
    private String bind = null;
    private int timeout = 50;

    private final static String DEFAULT_DOMAIN_URI = "http://tuscany.apache.org/sca/1.1/domains/default";
    private String domainURI = DEFAULT_DOMAIN_URI;
    private List<EndpointReference> endpointreferences = new CopyOnWriteArrayList<EndpointReference>();
    private List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();

    private ExtensionPointRegistry registry;
    private ReplicatedMap map;

    private static final Channel createChannel(String address, int port, String bindAddress) {

        //create a channel
        GroupChannel channel = new GroupChannel();
        McastService mcastService = (McastService)channel.getMembershipService();
        mcastService.setPort(port);
        mcastService.setAddress(address);

        // REVIEW: In my case, there are multiple IP addresses
        // One for the WIFI and the other one for VPN. For some reason the VPN one doesn't support
        // Multicast
        /*
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni.isLoopback() || !ni.isUp() || !ni.supportsMulticast()) {
                    continue;
                }
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress addr = ips.nextElement();
                    System.out.println(addr.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        */
        if (bindAddress != null) {
            mcastService.setBind(bindAddress);
        }

        // mcastService.setBind("192.168.1.100");
        return channel;
    }

    public ReplicatedEndpointRegistry(ExtensionPointRegistry registry, Map<String, String> attributes) {
        this.registry = registry;
        String portStr = attributes.get("port");
        if (portStr != null) {
            port = Integer.parseInt(portStr);
        }
        String address = attributes.get("address");
        if (address == null) {
            address = MULTICAST_ADDRESS;
        }
        bind = attributes.get("bind");
        String timeoutStr = attributes.get("timeout");
        if (timeoutStr != null) {
            timeout = Integer.parseInt(timeoutStr);
        }
        // start();
    }

    public ReplicatedEndpointRegistry(String domainURI) {
        this.domainURI = domainURI;
        // start();
    }

    public void start() {
        if (map != null) {
            throw new IllegalStateException("The registry has already been started");
        }
        map =
            new ReplicatedMap(null, createChannel(address, port, bind), timeout, this.domainURI,
                              new ClassLoader[] {ReplicatedEndpointRegistry.class.getClassLoader()});
        try {
            map.getChannel().start(Channel.DEFAULT);
        } catch (ChannelException e) {
            throw new IllegalStateException(e);
        }
    }

    public void stop() {
        if (map != null) {
            Channel channel = map.getChannel();
            map.breakdown();
            try {
                channel.stop(Channel.DEFAULT);
            } catch (ChannelException e) {
                throw new IllegalStateException(e);
            }
            map = null;
        }
    }

    public void addEndpoint(Endpoint endpoint) {
        map.put(endpoint.getURI(), endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(endpoint);
        }
        logger.info("EndpointRegistry: Add endpoint - " + endpoint);
    }

    public void addEndpointReference(EndpointReference endpointReference) {
        endpointreferences.add(endpointReference);
        logger.info("EndpointRegistry: Add endpoint reference - " + endpointReference);
    }

    public void addListener(EndpointListener listener) {
        listeners.add(listener);
    }

    /**
     * Parse the component/service/binding URI into an array of parts (componentURI, serviceName, bindingName)
     * @param uri
     * @return
     */
    private String[] parse(String uri) {
        String[] names = new String[3];
        int index = uri.lastIndexOf('#');
        if (index == -1) {
            names[0] = uri;
        } else {
            names[0] = uri.substring(0, index);
            String str = uri.substring(index + 1);
            if (str.startsWith("service-binding(") && str.endsWith(")")) {
                str = str.substring("service-binding(".length(), str.length() - 1);
                String[] parts = str.split("/");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid service-binding URI: " + uri);
                }
                names[1] = parts[0];
                names[2] = parts[1];
            } else if (str.startsWith("service(") && str.endsWith(")")) {
                str = str.substring("service(".length(), str.length() - 1);
                names[1] = str;
            } else {
                throw new IllegalArgumentException("Invalid component/service/binding URI: " + uri);
            }
        }
        return names;
    }

    private boolean matches(String target, String uri) {
        String[] parts1 = parse(target);
        String[] parts2 = parse(uri);
        for (int i = 0; i < parts1.length; i++) {
            if (parts1[i] == null || parts1[i].equals(parts2[i])) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public List<Endpoint> findEndpoint(EndpointReference endpointReference) {
        List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();

        logger.info("EndpointRegistry: Find endpoint for reference - " + endpointReference);

        if (endpointReference.getReference() != null) {
            Endpoint targetEndpoint = endpointReference.getTargetEndpoint();
            for (Object v : map.values()) {
                Endpoint endpoint = (Endpoint)v;
                // TODO: implement more complete matching
                if (matches(targetEndpoint.getURI(), endpoint.getURI())) {
                    // MapEntry entry = map.getInternal(endpoint.getURI());
                    // if (!entry.isPrimary()) {
                        endpoint.setExtensionPointRegistry(registry);
                    // }
                    foundEndpoints.add(endpoint);
                    logger.info("EndpointRegistry: Found endpoint with matching service  - " + endpoint);
                }
                // else the service name doesn't match
            }
        }
        return foundEndpoints;
    }

    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        return endpointreferences;
    }

    public Endpoint getEndpoint(String uri) {
        return (Endpoint)map.get(uri);
    }

    public List<EndpointReference> getEndpointRefereneces() {
        return endpointreferences;
    }

    public List<Endpoint> getEndpoints() {
        return new ArrayList<Endpoint>(map.values());
    }

    public List<EndpointListener> getListeners() {
        return listeners;
    }

    public void removeEndpoint(Endpoint endpoint) {
        map.remove(endpoint.getURI());
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(endpoint);
        }
        logger.info("EndpointRegistry: Remove endpoint - " + endpoint);
    }

    public void removeEndpointReference(EndpointReference endpointReference) {
        endpointreferences.remove(endpointReference);
        logger.info("EndpointRegistry: Remove endpoint reference - " + endpointReference);
    }

    public void removeListener(EndpointListener listener) {
        listeners.remove(listener);
    }

    public void updateEndpoint(String uri, Endpoint endpoint) {
        Endpoint oldEndpoint = getEndpoint(uri);
        if (oldEndpoint == null) {
            throw new IllegalArgumentException("Endpoint is not found: " + uri);
        }
        map.put(endpoint.getURI(), endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEndpoint, endpoint);
        }
    }

    public static void main(String[] args) throws Exception {
        //create a channel
        GroupChannel channel = new GroupChannel();
        McastService mcastService = (McastService)channel.getMembershipService();
        mcastService.setPort(MULTICAST_PORT);
        mcastService.setAddress(MULTICAST_ADDRESS);

        InetAddress localhost = InetAddress.getLocalHost();

        // REVIEW: In my case, there are multiple IP addresses
        // One for the WIFI and the other one for VPN. For some reason the VPN one doesn't support
        // Multicast
        mcastService.setBind("192.168.1.100");
        channel.start(Channel.DEFAULT);
        ReplicatedMap map = new ReplicatedMap(null, channel, 50, "01", null);
        map.put(UUID.randomUUID().toString(), localhost.getHostAddress());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(2000);
            System.out.println(localhost + ": " + map.keySet());
        }
        for (Object e : map.entrySetFull()) {
            Map.Entry en = (Map.Entry)e;
            AbstractReplicatedMap.MapEntry entry = (AbstractReplicatedMap.MapEntry)en.getValue();
            System.out.println(entry);
        }
        map.breakdown();
        channel.stop(Channel.DEFAULT);
    }

}
