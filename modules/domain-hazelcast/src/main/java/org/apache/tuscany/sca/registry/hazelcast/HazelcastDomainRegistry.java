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

package org.apache.tuscany.sca.registry.hazelcast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.BaseDomainRegistry;
import org.apache.tuscany.sca.runtime.ContributionDescription;
import org.apache.tuscany.sca.runtime.ContributionListener;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.oasisopen.sca.ServiceRuntimeException;

import com.hazelcast.config.Config;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.DistributedTask;
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
 * An DomainRegistry using a Hazelcast
 */
public class HazelcastDomainRegistry extends BaseDomainRegistry implements DomainRegistry, LifeCycleListener, EntryListener<String, Endpoint>, MembershipListener {
    private final static Logger logger = Logger.getLogger(HazelcastDomainRegistry.class.getName());

    private HazelcastInstance hazelcastInstance;

    protected Map<Object, Object> endpointMap;
    protected MultiMap<String, String> endpointOwners;
    
    // key contributionURI, value map key compositeURI value compositeXML
    protected Map<String, Map<String, String>> runningComposites;
    // key member, value map key contributionURI value list of compositeURI 
    protected Map<String, Map<String, List<String>>> runningCompositeOwners;
    // key componentName, value contributionURI
    protected Map<String, String> runningComponentContributions;

    protected Map<String, Endpoint> localEndpoints = new ConcurrentHashMap<String, Endpoint>();

    protected Map<String, ContributionDescription> contributionDescriptions;

    protected AssemblyFactory assemblyFactory;
    protected Object shutdownMutex = new Object();
    protected Properties properties;

    public HazelcastDomainRegistry(ExtensionPointRegistry registry, Properties properties, String domainURI, String domainName) {
        super(registry, null, domainURI, domainName);
        this.assemblyFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(AssemblyFactory.class);
        this.properties = properties;
    }

    public HazelcastDomainRegistry(ExtensionPointRegistry registry,
                                     Map<String, String> attributes,
                                     String domainURI,
                                     String domainName) {
        super(registry, attributes, domainURI, domainName);
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

            runningComposites = hazelcastInstance.getMap(domainURI + "/RunningComposites");
            runningCompositeOwners = hazelcastInstance.getMap(domainURI + "/RunningCompositeOwners");
            runningComponentContributions = hazelcastInstance.getMap(domainURI + "/RunningComponentContributions");
            
            contributionDescriptions = hazelcastInstance.getMap(domainURI + "/ContributionDescriptions");
            ((IMap<String, ContributionDescription>)contributionDescriptions).addEntryListener(new EntryListener<String, ContributionDescription>() {
                public void entryAdded(EntryEvent<String, ContributionDescription> event) {
                    for (ContributionListener listener : contributionlisteners) {
                        listener.contributionInstalled(event.getKey());
                    }
                }
                public void entryRemoved(EntryEvent<String, ContributionDescription> event) {
                    for (ContributionListener listener : contributionlisteners) {
                        listener.contributionRemoved(event.getKey());
                    }
                }
                public void entryUpdated(EntryEvent<String, ContributionDescription> event) {
                    for (ContributionListener listener : contributionlisteners) {
                        listener.contributionUpdated(event.getKey());
                    }
                }
                public void entryEvicted(EntryEvent<String, ContributionDescription> event) {
                }
            }, false);
            
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
                runningComposites = null;
                runningCompositeOwners = null;
            }
        }
    }

    private void initHazelcastInstance() {
        
        // Hazelcast is outputs a lot on info level log messages which are unnecessary for us,
        // so disable info logging for hazelcast client classes unless fine logging is on for tuscany.
        if (!logger.isLoggable(Level.CONFIG)) {
            Logger hzl = Logger.getLogger("com.hazelcast");
            if (!hzl.isLoggable(Level.FINE)) {
                hzl.setLevel(Level.WARNING);
                // we want the ClusterManager info messages so we can see nodes come and go
                Logger.getLogger("com.hazelcast.cluster.ClusterManager").setLevel(Level.INFO);
                // we don't want any of the XmlConfigBuilder warnings as set the config programatically
                Logger.getLogger("com.hazelcast.config.XmlConfigBuilder").setLevel(Level.SEVERE);
                }
        }

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
        if (logger.isLoggable(Level.INFO)) {
            logger.info("started node in domain '" + domainURI + "' + at: " + hazelcastInstance.getCluster().getLocalMember().getInetSocketAddress());
        }
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
            RegistryConfig rc = RegistryConfig.parseConfigURI(domainRegistryURI);
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
            throw new IllegalStateException("Endpoint " + endpoint.getURI() + " already exists in domain " + domainURI + " at " + (m == null? "null" : m.getInetSocketAddress()));
        }
            
        String localMemberAddr = hazelcastInstance.getCluster().getLocalMember().getInetSocketAddress().toString();
        String endpointURI = endpoint.getURI();
        String componentName = endpoint.getComponent().getName();
        String curi = null;
        if (endpoint instanceof RuntimeEndpoint) {
            Composite dc = ((RuntimeEndpoint)endpoint).getCompositeContext().getDomainComposite();
            if (dc != null) {
                curi = dc.getContributionURI();
            }
        }
        Transaction txn = hazelcastInstance.getTransaction();
        txn.begin();
        try {
            localEndpoints.put(endpointURI, endpoint);
            endpointMap.put(endpointURI, endpoint);
            endpointOwners.put(localMemberAddr, endpointURI);
            if (curi != null) {
                runningComponentContributions.put(componentName, curi);
            }
            txn.commit();
        } catch (Throwable e) {
            txn.rollback();
            throw new ServiceRuntimeException(e);
        }
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
            String componentName = endpoint.getComponent().getName();
            Transaction txn = hazelcastInstance.getTransaction();
            txn.begin();
            try {
                endpointOwners.remove(localMemberAddr, endpointURI);
                endpointMap.remove(endpointURI);
                runningComponentContributions.remove(componentName);
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
                                Endpoint endpoint = (Endpoint)endpointMap.remove(k);
                                runningComponentContributions.remove(endpoint.getComponent().getName());
                            }
                        }
                        if (runningCompositeOwners.containsKey(memberAddr)) {
                            Map<String, List<String>> cs = runningCompositeOwners.remove(memberAddr);
                            for (String curi : cs.keySet()) {
                                Map<String, String> rcs = runningComposites.get(curi);
                                for (String uri : cs.get(curi)) {
                                    rcs.remove(uri);
                                }
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

    public void addRunningComposite(String curi, Composite composite) {
        String localMemberAddr = hazelcastInstance.getCluster().getLocalMember().getInetSocketAddress().toString();
        String compositeXML = writeComposite(composite);
        Transaction txn = hazelcastInstance.getTransaction();
        txn.begin();
        try {
            Map<String, String> cs = runningComposites.get(curi);
            if (cs == null) {
                cs = new HashMap<String, String>();
            }
            cs.put(composite.getURI(), compositeXML);
            runningComposites.put(curi, cs);
            Map<String, List<String>> ocs = runningCompositeOwners.get(localMemberAddr);
            if (ocs == null) {
                ocs = new HashMap<String, List<String>>();
            }
            List<String> lcs = ocs.get(curi);
            if (lcs == null) {
                lcs = new ArrayList<String>();
                ocs.put(curi, lcs);
            }
            lcs.add(composite.getURI());
            runningCompositeOwners.put(localMemberAddr, ocs);
            txn.commit();
        } catch (Throwable e) {
            txn.rollback();
            throw new ServiceRuntimeException(e);
        }
    }

    public void removeRunningComposite(String curi, String compositeURI) {
        String localMemberAddr = hazelcastInstance.getCluster().getLocalMember().getInetSocketAddress().toString();
        Transaction txn = hazelcastInstance.getTransaction();
        txn.begin();
        try {
            Map<String, String> cs = runningComposites.get(curi);
            if (cs != null) {
                cs.remove(compositeURI);
                if (cs.size() > 0) {
                    runningComposites.put(curi, cs);                
                } else {
                    runningComposites.remove(curi);                
                }
            }
            Map<String, List<String>> ocs = runningCompositeOwners.get(localMemberAddr);
            if (ocs != null) {
                List<String> xya = ocs.get(curi);
                if (xya != null) {
                    xya.remove(compositeURI);
                    if (xya.size() > 0) {
                        runningCompositeOwners.put(localMemberAddr, ocs);
                    } else {
                        runningCompositeOwners.remove(localMemberAddr);
                    }
                }
            }
            txn.commit();
        } catch (Throwable e) {
            txn.rollback();
            throw new ServiceRuntimeException(e);
        }
    }

    public Map<String, List<String>> getRunningCompositeURIs() {
        Map<String, List<String>> compositeURIs = new HashMap<String, List<String>>();
        for (String curi : runningComposites.keySet()) {
            List<String> uris = new ArrayList<String>();
            compositeURIs.put(curi, uris);
            for (String uri : runningComposites.get(curi).keySet()) {
                uris.add(uri);
            }
        }
         return compositeURIs;
    }

    @Override
    public Composite getRunningComposite(String contributionURI, String compositeURI) {
        Map<String, String> cs = runningComposites.get(contributionURI);
        if (cs != null) {
            String compositeXML = cs.get(compositeURI);
            // TODO: cache the Composite locally so that it doesn't get deserialized multiple times
            Composite composite = readComposite(compositeXML);
            composite.setContributionURI(contributionURI);
            return composite;
        }
        return null;
    }

    protected Composite readComposite(String compositeXML) {
        try {
            StAXHelper stAXHelper = StAXHelper.getInstance(registry);
            StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
            ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, stAXHelper.getInputFactory(), null);
            XMLStreamReader reader = stAXHelper.createXMLStreamReader(compositeXML);
            Composite composite = (Composite)staxProcessor.read(reader, new ProcessorContext(registry));
            return composite;
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (ContributionReadException e) {
            throw new RuntimeException(e);
        }
    }

    protected String writeComposite(Composite composite) {
        try {
            StAXHelper stAXHelper = StAXHelper.getInstance(registry);
            StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
            ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, null, stAXHelper.getOutputFactory());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            staxProcessor.write(composite, bos, new ProcessorContext(registry));
            bos.close();
            return bos.toString();
        } catch (ContributionWriteException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getInstalledContributionURIs() {
        return new ArrayList<String>(contributionDescriptions.keySet());
    }

    public ContributionDescription getInstalledContribution(String uri) {
        return contributionDescriptions.get(uri);
    }

    public void uninstallContribution(String uri) {
        contributionDescriptions.remove(uri);
    }

    @Override
    public void installContribution(ContributionDescription cd) {
        contributionDescriptions.put(cd.getURI(), cd);
    }

    @Override
    public void updateInstalledContribution(ContributionDescription cd) {
        contributionDescriptions.put(cd.getURI(), cd);
    }

    @Override
    public List<String> getNodeNames() {
        List<String> members = new ArrayList<String>();
        for (Member m : hazelcastInstance.getCluster().getMembers()) {
            if (!m.isSuperClient()) {
                members.add(m.getInetSocketAddress().toString());
            }
        }
        return members;
    }

    @Override
    public String getLocalNodeName() {
        return hazelcastInstance.getCluster().getLocalMember().getInetSocketAddress().toString();
    }

    @Override
    public String getRunningNodeName(String contributionURI, String compositeURI) {
        for (String m : runningCompositeOwners.keySet()) {
            Map<String, List<String>> rcs = runningCompositeOwners.get(m);
            if (rcs != null) {
                List<String> cs = rcs.get(contributionURI);
                if (cs != null && cs.contains(compositeURI)) {
                    return m;
                }
            }
        }
        return null;
    }

    @Override
    public String remoteCommand(String memberName, Callable<String> command) {
        for (Member member : hazelcastInstance.getCluster().getMembers()) {
            if (member.getInetSocketAddress().toString().equals(memberName)) {
                FutureTask<String> task = new DistributedTask<String>(command, member);
                hazelcastInstance.getExecutorService().execute(task);
                try {
                    return task.get();
                } catch (Exception e) {
                    throw new ServiceRuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException("member not found: " + memberName);
    }

    @Override
    public String getContainingCompositesContributionURI(String componentName) {
        int x = runningComponentContributions.size();
        return runningComponentContributions.get(componentName);
    }
}
