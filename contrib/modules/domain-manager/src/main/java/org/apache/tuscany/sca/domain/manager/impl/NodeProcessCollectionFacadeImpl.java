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

package org.apache.tuscany.sca.domain.manager.impl;

import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeQName;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.nodeURI;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.binding.atom.AtomBindingFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.ItemCollection;
import org.apache.tuscany.sca.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a node process collection service. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={ItemCollection.class, LocalItemCollection.class})
public class NodeProcessCollectionFacadeImpl implements ItemCollection, LocalItemCollection {

    private static final Logger logger = Logger.getLogger(NodeProcessCollectionFacadeImpl.class.getName());

    @Reference
    public LocalItemCollection cloudCollection;
    
    @Reference
    public ItemCollection processCollection;
    
    @Reference
    public DomainManagerConfiguration domainManagerConfiguration;
    
    private AssemblyFactory assemblyFactory;
    private AtomBindingFactory atomBindingFactory;
    private CompositeActivator compositeActivator;

    /**
     * Initialize the component.
     */
    @Init
    public void initialize() {

        // Get its composite activator
        //FIXME
        //compositeActivator = runtime.getCompositeActivator();

        // Get the model factories
        ExtensionPointRegistry extensionPoints = domainManagerConfiguration.getExtensionPoints();
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        atomBindingFactory = modelFactories.getFactory(AtomBindingFactory.class);
    }
    
    public Entry<String, Item>[] getAll() {
        logger.fine("getAll");
        
        // Get the collection of nodes
        Entry<String, Item>[] nodeEntries = cloudCollection.getAll();

        // Dispatch to the hosts hosting these nodes
        List<Entry<String, Item>> entries = new ArrayList<Entry<String,Item>>();
        for (String host: hosts(nodeEntries)) {
            ItemCollection processCollection = processCollection(host);
            for (Entry<String, Item> remoteEntry: processCollection.getAll()) {
                entries.add(remoteEntry);
            }
            break;
        }
        
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        logger.fine("get " + key);
        
        // Get the host hosting the given node
        String host = host(key);
        
        // Dispatch the request to that host
        ItemCollection processCollection = processCollection(host);
        return processCollection.get(key);
    }

    public String post(String key, Item item) {
        logger.fine("post " + key);

        // Get the host hosting the given node
        String host;
        try {
            host = host(key);
        } catch (NotFoundException e) {
            throw new ServiceRuntimeException(e);
        }
        
        // Dispatch the request to that host
        ItemCollection processCollection = processCollection(host);
        return processCollection.post(key, item);
    }

    public void put(String key, Item item) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public void delete(String key) throws NotFoundException {
        logger.fine("delete " + key);
        
        // Get the host hosting the given node
        String host = host(key);
        
        // Dispatch the request to that host
        ItemCollection processCollection = processCollection(host);
        processCollection.delete(key);
    }
    
    public Entry<String, Item>[] query(String queryString) {
        logger.fine("query " + queryString);
        
        if (queryString.startsWith("node=")) {
            String key = queryString.substring(queryString.indexOf('=') + 1);
            
            // Get the host hosting the given node
            String host;
            try {
                host = host(key);
            } catch (NotFoundException e) {
                return new Entry[0];
            }
            
            // Dispatch the request to that host
            ItemCollection processCollection = processCollection(host);
            return processCollection.query(queryString);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    private String host(String nodeName) throws NotFoundException {

        // Get the entry representing the given node
        Entry<String, Item> nodeEntry = nodeEntry(cloudCollection.getAll(), nodeName);
        if (nodeEntry == null) {
            throw new NotFoundException(nodeName);
        }
        
        // Get the host hosting it
        return host(nodeEntry.getData());
    }

    /**
     * Returns the entry representing the given node.
     *  
     * @param entries
     * @param name
     * @return
     */
    private static Entry<String, Item> nodeEntry(Entry<String, Item>[] entries, String name) {
        for (Entry<String, Item> entry: entries) {
            QName qname = compositeQName(entry.getKey());
            if (qname.getLocalPart().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Returns the lists of hosts hosting the nodes in the given entries.
     *  
     * @param entries
     * @return
     */
    private static List<String> hosts(Entry<String, Item>[] entries) {
        List<String> hosts = new ArrayList<String>();
        for (Entry<String, Item> entry: entries) {
            String host = host(entry.getData());
            if (!hosts.contains(host)) {
                hosts.add(host);
            }
        }
        return hosts;
    }

    /**
     * Returns the host of the node represented by the given item.
     * 
     * @param item
     * @return
     */
    private static String host(Item item) {
        String uri = nodeURI(item.getContents());
        if (uri != null) {
            return URI.create(uri).getHost();
        } else {
            return null;
        }
    }

    /**
     * Returns a proxy to the process collection service on the specified
     * host.
     * 
     * @param host
     * @return
     */
    private ItemCollection processCollection(String host) {
        return processCollection;
        
//FIXME        
//        AtomBinding binding = atomBindingFactory.createAtomBinding();
//        binding.setURI("http://" + host + ":9990/node/processes");
//        ServiceReference<ItemCollection> reference = dynamicReference(ItemCollection.class, binding, assemblyFactory, compositeActivator);
//        return reference.getService();
    }
        
}
