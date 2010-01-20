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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.GroupChannel;
import org.apache.catalina.tribes.group.interceptors.StaticMembershipInterceptor;
import org.apache.catalina.tribes.membership.McastService;
import org.apache.catalina.tribes.membership.StaticMember;
import org.apache.catalina.tribes.transport.ReceiverBase;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.endpoint.tribes.AbstractReplicatedMap.MapEntry;
import org.apache.tuscany.sca.endpoint.tribes.MapStore.MapListener;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * A replicated EndpointRegistry based on Apache Tomcat Tribes
 */
public class ReplicatedEndpointRegistry implements EndpointRegistry, LifeCycleListener, MapListener {
    private final static Logger logger = Logger.getLogger(ReplicatedEndpointRegistry.class.getName());
    private static final String MULTICAST_ADDRESS = "228.0.0.100";
    private static final int MULTICAST_PORT = 50000;
    
    private static final int FIND_REPEAT_COUNT = 10;

    private int port = MULTICAST_PORT;
    private String address = MULTICAST_ADDRESS;
    private String bind = null;
    private int timeout = 50;
    private int receiverPort = 4000;
    private int receiverAutoBind = 100;
    private List<URI> staticRoutes;

    private final static String DEFAULT_DOMAIN_URI = "http://tuscany.apache.org/sca/1.1/domains/default";
    private String domainURI = DEFAULT_DOMAIN_URI;
    private List<EndpointReference> endpointreferences = new CopyOnWriteArrayList<EndpointReference>();
    private List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();

    private ExtensionPointRegistry registry;
    private ReplicatedMap map;

    private String id;
    private boolean noMultiCast;

    private static final GroupChannel createChannel(String address, int port, String bindAddress) {

        //create a channel
        GroupChannel channel = new GroupChannel();
        McastService mcastService = (McastService)channel.getMembershipService();
        mcastService.setPort(port);
        mcastService.setAddress(address);

        // REVIEW: In my case, there are multiple IP addresses
        // One for the WIFI and the other one for VPN. For some reason the VPN one doesn't support
        // Multicast

        if (bindAddress != null) {
            mcastService.setBind(bindAddress);
        } else {
            mcastService.setBind(getBindAddress());
        }
        
        return channel;
    }

    public ReplicatedEndpointRegistry(ExtensionPointRegistry registry,
                                      Map<String, String> attributes,
                                      String domainRegistryURI,
                                      String domainURI) {
        this.registry = registry;
        this.domainURI = domainURI;
        this.id = "[" + System.identityHashCode(this) + "]";
        getParameters(attributes, domainRegistryURI);
    }

    private Map<String, String> getParameters(Map<String, String> attributes, String domainRegistryURI) {
        Map<String, String> map = new HashMap<String, String>();
        if (attributes != null) {
            map.putAll(attributes);
        }
        URI uri = URI.create(domainRegistryURI);
        if (uri.getHost() != null) {
            map.put("address", uri.getHost());
        }
        if (uri.getPort() != -1) {
            map.put("port", String.valueOf(uri.getPort()));
        }
        int index = domainRegistryURI.indexOf('?');
        if (index == -1) {
            setConfig(map);
            return map;
        }
        String query = domainRegistryURI.substring(index + 1);
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        String[] params = query.split("&");
        for (String param : params) {
            index = param.indexOf('=');
            if (index != -1) {
                map.put(param.substring(0, index), param.substring(index + 1));
            }
        }
        setConfig(map);
        return map;
    }

    private void setConfig(Map<String, String> attributes) {
        String portStr = attributes.get("port");
        if (portStr != null) {
            port = Integer.parseInt(portStr);
            if (port == -1) {
                port = MULTICAST_PORT;
            }
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

        String routesStr = attributes.get("routes");
        if (routesStr != null) {
            StringTokenizer st = new StringTokenizer(routesStr);
            staticRoutes = new ArrayList<URI>();
            while (st.hasMoreElements()) {
                staticRoutes.add(URI.create("tcp://" + st.nextToken()));
            }
        }
        String mcast = attributes.get("nomcast");
        if (mcast != null) {
            noMultiCast = Boolean.valueOf(mcast);
        }
        String recvPort = attributes.get("receiverPort");
        if (recvPort != null) {
            receiverPort = Integer.parseInt(recvPort);
        }
        String recvAutoBind = attributes.get("receiverAutoBind");
        if (recvAutoBind != null) {
            receiverAutoBind = Integer.parseInt(recvAutoBind);
        }
    }

    public void start() {
        if (map != null) {
            throw new IllegalStateException("The registry has already been started");
        }
        GroupChannel channel = createChannel(address, port, bind);
        map =
            new ReplicatedMap(null, channel, timeout, this.domainURI,
                              new ClassLoader[] {ReplicatedEndpointRegistry.class.getClassLoader()});
        map.addListener(this);

        if (noMultiCast) {
            map.getChannel().addInterceptor(new DisableMcastInterceptor());
        }

        // Configure the receiver ports
        ChannelReceiver receiver = channel.getChannelReceiver();
        if (receiver instanceof ReceiverBase) {
            ((ReceiverBase)receiver).setAutoBind(receiverAutoBind);
            ((ReceiverBase)receiver).setPort(receiverPort);
        }

        /*
        Object sender = channel.getChannelSender();
        if (sender instanceof ReplicationTransmitter) {
            sender = ((ReplicationTransmitter)sender).getTransport();
        }
        if (sender instanceof AbstractSender) {
            ((AbstractSender)sender).setKeepAliveCount(0);
            ((AbstractSender)sender).setMaxRetryAttempts(5);
        }
        */
        
        if (staticRoutes != null) {
            StaticMembershipInterceptor smi = new StaticMembershipInterceptor();
            for (URI staticRoute : staticRoutes) {
                Member member;
                try {
                    // The port has to match the receiver port
                    member = new StaticMember(staticRoute.getHost(), staticRoute.getPort(), 5000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                smi.addStaticMember(member);
                logger.info("Added static route: " + staticRoute.getHost() + ":" + staticRoute.getPort());
            }
            smi.setLocalMember(map.getChannel().getLocalMember(false));
            map.getChannel().addInterceptor(smi);
        }
        
        try {
            map.getChannel().start(Channel.DEFAULT);
        } catch (ChannelException e) {
            throw new IllegalStateException(e);
        }

    }

    public void stop() {
        if (map != null) {
            map.removeListener(this);
            Channel channel = map.getChannel();
            map.breakdown();
            try {
                channel.stop(Channel.DEFAULT);
            } catch (ChannelException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
            map = null;
        }
    }

    public void addEndpoint(Endpoint endpoint) {
        map.put(endpoint.getURI(), endpoint);
        logger.info("Add endpoint - " + endpoint);
    }

    public void addEndpointReference(EndpointReference endpointReference) {
        endpointreferences.add(endpointReference);
        logger.fine("Add endpoint reference - " + endpointReference);
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
        logger.fine("Find endpoint for reference - " + endpointReference);

        if (endpointReference.getReference() != null) {
            Endpoint targetEndpoint = endpointReference.getTargetEndpoint();
            return findEndpoint(targetEndpoint.getURI());
        }

        return new ArrayList<Endpoint>();
    }

    public List<Endpoint> findEndpoint(String uri) {
        List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();

        // in the failure case we repeat the look up after a short
        // delay to take account of tribes replication delays
        int repeat = FIND_REPEAT_COUNT;
        
        while (repeat > 0){
            for (Object v : map.values()) {
                Endpoint endpoint = (Endpoint)v;
                // TODO: implement more complete matching
                logger.fine("Matching against - " + endpoint);
                if (matches(uri, endpoint.getURI())) {
                    MapEntry entry = map.getInternal(endpoint.getURI());
                    if (!isLocal(entry)) {
                        endpoint.setRemote(true);
                    }
                    // if (!entry.isPrimary()) {
                    ((RuntimeEndpoint) endpoint).bind(registry, this);
                    // }
                    foundEndpoints.add(endpoint);
                    logger.fine("Found endpoint with matching service  - " + endpoint);
                    repeat = 0;
                } 
                // else the service name doesn't match
            }
            
            if (foundEndpoints.size() == 0) {
                // the service name doesn't match any endpoints so wait a little and try
                // again in case this is caused by tribes synch delays
                logger.info("Repeating endpoint reference match - " + uri);
                repeat--;
                try {
                    Thread.sleep(1000);
                } catch(Exception ex){
                    // do nothing
                    repeat=0;
                }
            }
        }

        return foundEndpoints;
    }


    private boolean isLocal(MapEntry entry) {
        return entry.getPrimary().equals(map.getChannel().getLocalMember(false));
    }

    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        return endpointreferences;
    }

    public Endpoint getEndpoint(String uri) {
        return (Endpoint)map.get(uri);
    }

    public List<EndpointReference> getEndpointReferences() {
        return endpointreferences;
    }

    public List<Endpoint> getEndpoints() {
        return new ArrayList(map.values());
    }

    public List<EndpointListener> getListeners() {
        return listeners;
    }

    public void removeEndpoint(Endpoint endpoint) {
        map.remove(endpoint.getURI());
        logger.info("Remove endpoint - " + endpoint);
    }

    public void removeEndpointReference(EndpointReference endpointReference) {
        endpointreferences.remove(endpointReference);
        logger.fine("Remove endpoint reference - " + endpointReference);
    }

    public void removeListener(EndpointListener listener) {
        listeners.remove(listener);
    }

    public void replicate(boolean complete) {
        map.replicate(complete);
    }

    public void updateEndpoint(String uri, Endpoint endpoint) {
        Endpoint oldEndpoint = getEndpoint(uri);
        if (oldEndpoint == null) {
            throw new IllegalArgumentException("Endpoint is not found: " + uri);
        }
        map.put(endpoint.getURI(), endpoint);
    }

    public void entryAdded(Object key, Object value) {
        MapEntry entry = (MapEntry)value;
        Endpoint newEp = (Endpoint)entry.getValue();
        if (!isLocal(entry)) {
            logger.info(id + " Remote endpoint added: " + entry.getValue());
            newEp.setRemote(true);
        }
        ((RuntimeEndpoint) newEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(newEp);
        }
    }

    public void entryRemoved(Object key, Object value) {
        MapEntry entry = (MapEntry)value;
        if (!isLocal(entry)) {
            logger.info(id + " Remote endpoint removed: " + entry.getValue());
        }
        Endpoint oldEp = (Endpoint)entry.getValue();
        ((RuntimeEndpoint) oldEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(oldEp);
        }
    }

    public void entryUpdated(Object key, Object oldValue, Object newValue) {
        MapEntry oldEntry = (MapEntry)oldValue;
        MapEntry newEntry = (MapEntry)newValue;
        if (!isLocal(newEntry)) {
            logger.info(id + " Remote endpoint updated: " + newEntry.getValue());
        }
        Endpoint oldEp = (Endpoint)oldEntry.getValue();
        Endpoint newEp = (Endpoint)newEntry.getValue();
        ((RuntimeEndpoint) newEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEp, newEp);
        }
    }

    public static void main(String[] args) throws Exception {
        //create a channel
        GroupChannel channel = new GroupChannel();
        McastService mcastService = (McastService)channel.getMembershipService();
        mcastService.setPort(MULTICAST_PORT);
        mcastService.setAddress(MULTICAST_ADDRESS);

        
//        ChannelReceiver rcv = channel.getChannelReceiver();
//        ReceiverBase rcvb = (ReceiverBase)rcv;
//        rcvb.setPort(10480);

        InetAddress localhost = InetAddress.getLocalHost();

        // REVIEW: In my case, there are multiple IP addresses
        // One for the WIFI and the other one for VPN. For some reason the VPN one doesn't support
        // Multicast

        // You can use "route add 228.0.0.0 mask 252.0.0.0 192.168.1.100"
        mcastService.setBind(getBindAddress());
        channel.start(Channel.DEFAULT);
        ReplicatedMap map = new ReplicatedMap(null, channel, 50, "01", null);
        map.put(UUID.randomUUID().toString(), localhost.getHostAddress());
        for (int i = 0; i < 4; i++) {
            Thread.sleep(3000);
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

    private static String getBindAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // The following APIs require JDK 1.6
                /*
                if (ni.isLoopback() || !ni.isUp() || !ni.supportsMulticast()) {
                    continue;
                }
                */
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                if (!ips.hasMoreElements()) {
                    continue;
                }
                while (ips.hasMoreElements()) {
                    InetAddress addr = ips.nextElement();
                    if (addr.isLoopbackAddress()) {
                        continue;
                    }
                    return addr.getHostAddress();
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

}
