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

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

import com.hazelcast.config.Config;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.nio.Address;

/**
 * An EndpointRegistry using a Hazelcast
 */
public class HazelcastEndpointRegistry implements EndpointRegistry, LifeCycleListener, EntryListener<String, Endpoint>, MembershipListener {
    private final static Logger logger = Logger.getLogger(HazelcastEndpointRegistry.class.getName());

    private List<EndpointReference> endpointreferences = new CopyOnWriteArrayList<EndpointReference>();
    private List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();

    private ExtensionPointRegistry registry;
    private ConfigURI configURI;

    HazelcastInstance hazelcastInstance;
    Map<Object, Object> map;
    private List<String> localEndpoints = new ArrayList<String>();;

    public HazelcastEndpointRegistry(ExtensionPointRegistry registry,
                                     Map<String, String> attributes,
                                     String domainRegistryURI,
                                     String domainURI) {
        this.registry = registry;
        this.configURI = new ConfigURI(domainRegistryURI);
    }

    public void start() {
        if (map != null) {
            throw new IllegalStateException("The registry has already been started");
        }
        if (configURI.toString().startsWith("tuscany:vm:")) {
            map = new HashMap<Object, Object>();
        } else {
            initHazelcastInstance();
            IMap imap = hazelcastInstance.getMap(configURI.getDomainName() + "/Endpoints");
            imap.addEntryListener(this, true);
            map = imap;
            hazelcastInstance.getCluster().addMembershipListener(this);
        }
    }

    public void stop() {
        if (hazelcastInstance != null) {
            hazelcastInstance.shutdown();
            hazelcastInstance = null;
            map = null;
        }
    }

    private void initHazelcastInstance() {
        Config config = new XmlConfigBuilder().build();

        config.setPort(configURI.getListenPort());
        //config.setPortAutoIncrement(false);

        if (configURI.getBindAddress() != null) {
            config.getNetworkConfig().getInterfaces().setEnabled(true);
            config.getNetworkConfig().getInterfaces().clear();
            config.getNetworkConfig().getInterfaces().addInterface(configURI.getBindAddress());
        }

        config.getGroupConfig().setName(configURI.getDomainName());
        config.getGroupConfig().setPassword(configURI.getPassword());

        if (configURI.isMulticastDisabled()) {
            config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        } else {
            config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
            config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastPort(configURI.getMulticastPort());
            config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastGroup(configURI.getMulticastAddress());
        }
        
        // config.getMapConfig(configURI.getDomainName() + "/Endpoints").setBackupCount(0);

        if (configURI.getRemotes().size() > 0) {
            TcpIpConfig tcpconfig = config.getNetworkConfig().getJoin().getJoinMembers();
            tcpconfig.setEnabled(true);
            List<Address> lsMembers = tcpconfig.getAddresses();
            lsMembers.clear();
            for (String addr : configURI.getRemotes()) {
                String[] ipNPort = addr.split(":");
                try {
                    lsMembers.add(new Address(ipNPort[0], Integer.parseInt(ipNPort[1])));
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    public void addEndpoint(Endpoint endpoint) {
        map.put(endpoint.getURI(), endpoint);
        localEndpoints.add(endpoint.getURI());
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
        for (Object v : map.values()) {
            Endpoint endpoint = (Endpoint)v;
            logger.fine("Matching against - " + endpoint);
            if (matches(uri, endpoint.getURI())) {
                if (!isLocal(endpoint)) {
                    endpoint.setRemote(true);
                }
                ((RuntimeEndpoint)endpoint).bind(registry, this);
                foundEndpoints.add(endpoint);
                logger.fine("Found endpoint with matching service  - " + endpoint);
            }
        }
        return foundEndpoints;
    }
    

    private boolean isLocal(Endpoint endpoint) {
        return localEndpoints.contains(endpoint.getURI());
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
        localEndpoints.remove(endpoint.getURI());
        logger.info("Removed endpoint - " + endpoint);
    }

    public void removeEndpointReference(EndpointReference endpointReference) {
        endpointreferences.remove(endpointReference);
        logger.fine("Remove endpoint reference - " + endpointReference);
    }

    public void removeListener(EndpointListener listener) {
        listeners.remove(listener);
    }

    public void updateEndpoint(String uri, Endpoint endpoint) {
        //      // TODO: is updateEndpoint needed?
        //      throw new UnsupportedOperationException();
    }

    public void entryAdded(EntryEvent<String, Endpoint> event) {
        entryAdded(event.getKey(), event.getValue());
    }

    public void entryEvicted(EntryEvent<String, Endpoint> event) {
        // Should not happen
    }

    public void entryRemoved(EntryEvent<String, Endpoint> event) {
        entryRemoved(event.getKey(), event.getValue());
    }

    public void entryUpdated(EntryEvent<String, Endpoint> event) {
        entryUpdated(event.getKey(), null, event.getValue());
    }

    public void entryAdded(Object key, Object value) {
        Endpoint newEp = (Endpoint)value;
        if (!isLocal(newEp)) {
            logger.info(" Remote endpoint added: " + newEp);
            newEp.setRemote(true);
        }
        ((RuntimeEndpoint)newEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(newEp);
        }
    }

    public void entryRemoved(Object key, Object value) {
        Endpoint oldEp = (Endpoint)value;
        if (!isLocal(oldEp)) {
            logger.info(" Remote endpoint removed: " + value);
        }
        ((RuntimeEndpoint) oldEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(oldEp);
        }
    }

    public void entryUpdated(Object key, Object oldValue, Object newValue) {
        Endpoint oldEp = (Endpoint)oldValue;
        Endpoint newEp = (Endpoint)newValue;
        if (!isLocal(newEp)) {
            logger.info(" Remote endpoint updated: " + newEp);
        }
        ((RuntimeEndpoint)newEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEp, newEp);
        }
    }

    public void memberAdded(MembershipEvent event) {
    }

    public void memberRemoved(MembershipEvent event) {
    }

}
