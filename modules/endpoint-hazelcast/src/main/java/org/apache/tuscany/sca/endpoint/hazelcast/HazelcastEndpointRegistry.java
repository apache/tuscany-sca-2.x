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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.runtime.BaseEndpointRegistry;
import org.apache.tuscany.sca.runtime.DomainRegistryURI;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;

import com.hazelcast.config.Config;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.Transaction;
import com.hazelcast.impl.GroupProperties;
import com.hazelcast.nio.Address;

/**
 * An EndpointRegistry using a Hazelcast
 */
public class HazelcastEndpointRegistry extends BaseEndpointRegistry implements EndpointRegistry, LifeCycleListener, EntryListener<String, Endpoint>, MembershipListener {
    private final static Logger logger = Logger.getLogger(HazelcastEndpointRegistry.class.getName());

    protected DomainRegistryURI configURI;

    private HazelcastInstance hazelcastInstance;
    protected Map<Object, Object> endpointMap;
    private Map<String, Endpoint> localEndpoints = new ConcurrentHashMap<String, Endpoint>();
    protected MultiMap<String, String> endpointOwners;
    private AssemblyFactory assemblyFactory;
    private Object shutdownMutex = new Object();

    public HazelcastEndpointRegistry(ExtensionPointRegistry registry,
                                     Map<String, String> attributes,
                                     String domainRegistryURI,
                                     String domainURI) {
        super(registry, attributes, domainRegistryURI, domainURI);
        this.configURI = new DomainRegistryURI(domainRegistryURI);
        this.assemblyFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(AssemblyFactory.class);
    }
    
    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void start() {
        if (endpointMap != null) {
            throw new IllegalStateException("The registry has already been started");
        }
        if (configURI.toString().startsWith("tuscany:vm:")) {
            endpointMap = new HashMap<Object, Object>();
        } else {
            initHazelcastInstance();
            IMap imap = hazelcastInstance.getMap(configURI.getDomainName() + "/Endpoints");
            imap.addEntryListener(this, true);
            endpointMap = imap;
            
            endpointOwners = hazelcastInstance.getMultiMap(configURI.getDomainName() + "/EndpointOwners");

            hazelcastInstance.getCluster().addMembershipListener(this);
        }
    }

    public void stop() {
        if (hazelcastInstance != null) {
            synchronized (shutdownMutex) {
                hazelcastInstance.shutdown();
                hazelcastInstance = null;
                endpointMap = null;
                endpointOwners = null;
            }
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
            TcpIpConfig tcpconfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
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
        
        config.getMapConfig("default").setNearCacheConfig(new NearCacheConfig(0, 0, "NONE", 0, true));

        // Disable the Hazelcast shutdown hook as Tuscany has its own and with both there are race conditions
        config.setProperty(GroupProperties.PROP_SHUTDOWNHOOK_ENABLED, "false");
        
        // By default this is 5 seconds, not sure what the implications are but dropping it down to 1 makes 
        // things like the samples look much faster
        config.setProperty(GroupProperties.PROP_WAIT_SECONDS_BEFORE_JOIN, "1");

        this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    public void addEndpoint(Endpoint endpoint) {
        if (findEndpoint(endpoint.getURI()).size() > 0) {
            Member m = getOwningMember(endpoint.getURI());
            throw new IllegalStateException("Endpoint " + endpoint.getURI() + " already exists in domain " + configURI.getDomainName() + " at " + m.getInetSocketAddress());
        }
            
        String localMemberAddr = hazelcastInstance.getCluster().getLocalMember().getInetSocketAddress().toString();
        String endpointURI = endpoint.getURI();
        Transaction txn = hazelcastInstance.getTransaction();
        txn.begin();
        try {
            endpointMap.put(endpointURI, endpoint);
            endpointOwners.put(localMemberAddr, endpointURI);
            txn.commit();
        } catch (Throwable e) {
            txn.rollback();
            throw new ServiceRuntimeException(e);
        }
        localEndpoints.put(endpointURI, endpoint);
        logger.info("Add endpoint - " + endpoint);
    }

    public List<Endpoint> findEndpoint(String uri) {
        List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();
        for (Object v : endpointMap.values()) {
            Endpoint endpoint = (Endpoint)v;
            logger.fine("Matching against - " + endpoint);
            if (endpoint.matches(uri)) {
                if (!isLocal(endpoint)) {
                    endpoint.setRemote(true);
                    ((RuntimeEndpoint)endpoint).bind(registry, this);
                } else {
                    // get the local version of the endpoint
                    // this local version won't have been serialized
                    // won't be marked as remote and will have the 
                    // full interface contract information
                    endpoint = localEndpoints.get(endpoint.getURI());
                }
                
                foundEndpoints.add(endpoint);
                logger.fine("Found endpoint with matching service  - " + endpoint);
            }
        }
        return foundEndpoints;
    }
    

    private boolean isLocal(Endpoint endpoint) {
        return localEndpoints.containsKey(endpoint.getURI());
    }

    public Endpoint getEndpoint(String uri) {
        return (Endpoint)endpointMap.get(uri);
    }

    public List<Endpoint> getEndpoints() {
        return new ArrayList(endpointMap.values());
    }

    public void removeEndpoint(Endpoint endpoint) {
        if (hazelcastInstance == null) {
            return;
        }
        synchronized (shutdownMutex) {
            String localMemberAddr = hazelcastInstance.getCluster().getLocalMember().getInetSocketAddress().toString();
            String endpointURI = endpoint.getURI();
            Transaction txn = hazelcastInstance.getTransaction();
            txn.begin();
            try {
                endpointMap.remove(endpointURI);
                endpointOwners.remove(localMemberAddr, endpointURI);
                txn.commit();
            } catch (Throwable e) {
                txn.rollback();
                throw new ServiceRuntimeException(e);
            }
            localEndpoints.remove(endpointURI);
            logger.info("Removed endpoint - " + endpoint);
        }
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
        } 
        endpointAdded(newEp);
    }

    public void entryRemoved(Object key, Object value) {
        Endpoint oldEp = (Endpoint)value;
        if (!isLocal(oldEp)) {
            logger.info(" Remote endpoint removed: " + value);
        }
        endpointRemoved(oldEp);
    }

    public void entryUpdated(Object key, Object oldValue, Object newValue) {
        Endpoint oldEp = (Endpoint)oldValue;
        Endpoint newEp = (Endpoint)newValue;
        if (!isLocal(newEp)) {
            logger.info(" Remote endpoint updated: " + newEp);
        }
        endpointUpdated(oldEp, newEp);
    }

    public void memberAdded(MembershipEvent event) {
    }

    public void memberRemoved(MembershipEvent event) {
        try {
            String memberAddr = event.getMember().getInetSocketAddress().toString();
            if (endpointOwners.containsKey(memberAddr)) {
                synchronized (shutdownMutex) {
                    ILock lock = hazelcastInstance.getLock("EndpointOwners/" + memberAddr);
                    lock.lock();
                    try {
                        if (endpointOwners.containsKey(memberAddr)) {
                            Collection<String> keys = endpointOwners.remove(memberAddr);
                            for (Object k : keys) {
                                endpointMap.remove(k);
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().getCause() != null) {
                // ignore hazelcast already shutdown exception
                if (!"Hazelcast Instance is not active!".equals(e.getCause().getCause().getMessage())) {
                    throw new ServiceRuntimeException(e);
                }
            }
        }
    }

    public Member getOwningMember(String serviceURI) {
        for (String memberAddr : endpointOwners.keySet()) {
            for (String service : endpointOwners.get(memberAddr)) {
                Endpoint ep = assemblyFactory.createEndpoint();
                ep.setURI(service);
                if (ep.matches(serviceURI)) {
                    for (Member m : getHazelcastInstance().getCluster().getMembers()) {
                        if (memberAddr.equals(m.getInetSocketAddress().toString())) {
                            return m;
                        }
                    }
                }
            }
        }
        return null;
    }
}
