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

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.BaseEndpointRegistry;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
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
import com.hazelcast.nio.Address;

/**
 * An EndpointRegistry using a Hazelcast
 */
public class HazelcastEndpointRegistry extends BaseEndpointRegistry implements EndpointRegistry, LifeCycleListener, EntryListener<String, Endpoint>, MembershipListener {
    private final static Logger logger = Logger.getLogger(HazelcastEndpointRegistry.class.getName());

    private HazelcastInstance hazelcastInstance;
    protected Map<Object, Object> endpointMap;
    protected Map<String, Endpoint> localEndpoints = new ConcurrentHashMap<String, Endpoint>();
    protected MultiMap<String, String> endpointOwners;
    protected AssemblyFactory assemblyFactory;
    protected Object shutdownMutex = new Object();
    protected Properties properties;

    public HazelcastEndpointRegistry(ExtensionPointRegistry registry, Properties properties, String domainURI) {
        super(registry, null, null, domainURI);
        this.assemblyFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(AssemblyFactory.class);
        this.properties = properties;
    }

    public HazelcastEndpointRegistry(ExtensionPointRegistry registry,
                                     Map<String, String> attributes,
                                     String domainRegistryURI,
                                     String domainURI) {
        super(registry, attributes, domainRegistryURI, domainURI);
        this.assemblyFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(AssemblyFactory.class);
        this.properties = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class).getProperties();
    }
    
    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void start() {
        if (endpointMap != null) {
            throw new IllegalStateException("The registry has already been started");
        }
//        if (configURI.toString().startsWith("tuscany:vm:")) {
//            endpointMap = new HashMap<Object, Object>();
//        } else {
            initHazelcastInstance();
            IMap imap = hazelcastInstance.getMap(domainURI + "/Endpoints");
            imap.addEntryListener(this, true);
            endpointMap = imap;
            
            endpointOwners = hazelcastInstance.getMultiMap(domainURI + "/EndpointOwners");

            hazelcastInstance.getCluster().addMembershipListener(this);
//        }
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
        Config config = getHazelcastConfig();

        // do this when theres a way to have adders be the key owners
        // config.getMapConfig(configURI.getDomainName() + "/Endpoints").setBackupCount(0);

        // this caches reads locally
        config.getMapConfig("default").setNearCacheConfig(new NearCacheConfig(0, 0, "NONE", 0, true));

        // Disable the Hazelcast shutdown hook as Tuscany has its own and with both there are race conditions
        config.setProperty("hazelcast.shutdownhook.enabled",
                           // GroupProperties.PROP_SHUTDOWNHOOK_ENABLED, 
                           "false");
        
        // By default this is 5 seconds, not sure what the implications are but dropping it down to 1 makes 
        // things like the samples look much faster
        config.setProperty("hazelcast.wait.seconds.before.join",
                           // GroupProperties.PROP_WAIT_SECONDS_BEFORE_JOIN, 
                           "1");

        this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    protected Config getHazelcastConfig() {
        Config config;
        this.properties = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class).getProperties();
        String configFile = properties.getProperty("hazelcastConfig");
        if (configFile != null) {
            try {
                config = new XmlConfigBuilder(configFile).build();
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(configFile, e);
            }
        } else {
            // TUSCANY-3675 - domainRegistryURI properties don't seem to be copied into the
            //                properties collection anywhere
            config = new XmlConfigBuilder().build();
            RegistryConfig rc = new RegistryConfig(properties);
            config.setPort(rc.getBindPort());
            //config.setPortAutoIncrement(false);

            if (!rc.getBindAddress().equals("*")) {
                config.getNetworkConfig().getInterfaces().setEnabled(true);
                config.getNetworkConfig().getInterfaces().clear();
                config.getNetworkConfig().getInterfaces().addInterface(rc.getBindAddress());
            }

            config.getGroupConfig().setName(rc.getUserid());
            config.getGroupConfig().setPassword(rc.getPassword());

            if (rc.isMulticastDisabled()) {
                config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
            } else {
                config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
                config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastPort(rc.getMulticastPort());
                config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastGroup(rc.getMulticastAddress());
            }
            
            if (rc.getWKAs().size() > 0) {
                TcpIpConfig tcpconfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
                tcpconfig.setEnabled(true);
                List<Address> lsMembers = tcpconfig.getAddresses();
                lsMembers.clear();
                for (String addr : rc.getWKAs()) {
                    String[] ipNPort = addr.split(":");
                    try {
                        lsMembers.add(new Address(ipNPort[0], Integer.parseInt(ipNPort[1])));
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return config;
    }

    public void addEndpoint(Endpoint endpoint) {
        if (findEndpoint(endpoint.getURI()).size() > 0) {
            Member m = getOwningMember(endpoint.getURI());
            throw new IllegalStateException("Endpoint " + endpoint.getURI() + " already exists in domain " + domainURI + " at " + m.getInetSocketAddress());
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
                endpoint = localizeEndpoint(endpoint);
                foundEndpoints.add(endpoint);
                logger.fine("Found endpoint with matching service  - " + endpoint);
            }
        }
        return foundEndpoints;
    }

    private Endpoint localizeEndpoint(Endpoint endpoint) {
        if (endpoint == null) return null;
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
        return endpoint;
    }
    

    private boolean isLocal(Endpoint endpoint) {
        return localEndpoints.containsKey(endpoint.getURI());
    }

    public Endpoint getEndpoint(String uri) {
        return localizeEndpoint((Endpoint)endpointMap.get(uri));
    }

    public List<Endpoint> getEndpoints() {
        ArrayList<Endpoint> eps = new ArrayList();
        for (Object ep : endpointMap.values()) {
            eps.add(localizeEndpoint((Endpoint)ep));
        }
        return eps;
    }

    public void removeEndpoint(Endpoint endpoint) {
        if (hazelcastInstance == null) {
            return;
        }
        synchronized (shutdownMutex) {
            String localMemberAddr = hazelcastInstance.getCluster().getLocalMember().getInetSocketAddress().toString();
            String endpointURI = endpoint.getURI();
            
// TODO: seems to be a txn bug in Hazelcast, see http://code.google.com/p/hazelcast/issues/detail?id=258 
//            Transaction txn = hazelcastInstance.getTransaction();
//            txn.begin();
//            try {
                endpointOwners.remove(localMemberAddr, endpointURI);
                endpointMap.remove(endpointURI);
//                txn.commit();
//            } catch (Throwable e) {
//                txn.rollback();
//                throw new ServiceRuntimeException(e);
//            }
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
