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

import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeAlternateLink;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeKey;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeQName;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeSourceLink;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeTitle;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.contributionURI;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.lastModified;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.locationURL;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.ItemCollection;
import org.apache.tuscany.sca.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.apache.tuscany.sca.domain.manager.impl.DeployableCompositeCollectionImpl.Cache.ContributionCache;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionContentProcessor;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a deployable composite collection service. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={ItemCollection.class, LocalItemCollection.class})
public class DeployableCompositeCollectionImpl implements ItemCollection, LocalItemCollection {

    private static final Logger logger = Logger.getLogger(DeployableCompositeCollectionImpl.class.getName());

    @Reference
    public LocalItemCollection contributionCollection;
    
    @Reference
    public DomainManagerConfiguration domainManagerConfiguration;
    
    private ModelFactoryExtensionPoint modelFactories;
    private ModelResolverExtensionPoint modelResolvers;
    private URLArtifactProcessor<Contribution> contributionProcessor;
    private XMLOutputFactory outputFactory;
    private ContributionDependencyBuilder contributionDependencyBuilder;
    private Monitor monitor;
    
    /**
     * Cache contribution models. 
     */
    static class Cache {
        static class ContributionCache {
            private Contribution contribution;
            private long contributionLastModified;
        }
        private Map<URL, ContributionCache> contributions = new HashMap<URL, ContributionCache>();
    }
    
    private Cache cache = new Cache();
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws ParserConfigurationException {
        
        ExtensionPointRegistry extensionPoints = domainManagerConfiguration.getExtensionPoints();
        
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();        
        
        // Get model factories
        modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        
        // Get and initialize artifact processors
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Object> staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, monitor);

        URLArtifactProcessorExtensionPoint urlProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        URLArtifactProcessor<Object> urlProcessor = new ExtensibleURLArtifactProcessor(urlProcessors, monitor);
        
        // Create contribution processor
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        contributionProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlProcessor, staxProcessor, monitor);
        
        // Create contribution and composite builders
        contributionDependencyBuilder = new ContributionDependencyBuilderImpl(monitor);
    }
    
    public Entry<String, Item>[] getAll() {
        logger.fine("getAll");
        
        // Return all the deployable composites in the contributions
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        
        // Get the list of contributions in the workspace
        Entry<String, Item>[] contributionEntries = contributionCollection.getAll();

        // Read contribution metadata
        for (Entry<String, Item> contributionEntry: contributionEntries) {
            Item contributionItem = contributionEntry.getData();
            Contribution contribution;
            try {
                contribution = contribution(contributionEntry.getKey(), contributionItem.getAlternate());
            } catch (ContributionReadException e) {
                continue;
            }

            // Create entries for the deployable composites
            for (Composite deployable: contribution.getDeployables()) {
                entries.add(entry(contribution, deployable));
            }
            
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        logger.fine("get " + key);

        // Get the specified contribution info 
        String contributionURI = contributionURI(key);
        Item contributionItem = contributionCollection.get(contributionURI);
        
        // Read the contribution
        Contribution contribution;
        try {
            contribution = contribution(contributionURI, contributionItem.getAlternate());
        } catch (ContributionReadException e) {
            throw new NotFoundException(key);
        }

        // Find the specified deployable composite
        QName qname = compositeQName(key);
        for (Composite deployable: contribution.getDeployables()) {
            if (qname.equals(deployable.getName())) {
                if (deployable.isUnresolved()) {
                    throw new NotFoundException(key);
                }
                
                // Return an item describing the deployable composite
                return item(contribution, deployable);
            }
        }

        throw new NotFoundException(key);
    }
    
    public String post(String key, Item item) {
        throw new UnsupportedOperationException();
    }

    public void put(String key, Item item) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public void delete(String key) throws NotFoundException {
        throw new UnsupportedOperationException();
    }
    
    public Entry<String, Item>[] query(String queryString) {
        logger.fine("query " + queryString);
        
        if (queryString.startsWith("contribution=")) {

            // Return all the deployable composites in the specified
            // contribution
            List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();

            // Get the specified contribution info 
            String contributionURI = queryString.substring(queryString.indexOf('=') + 1);
            Item contributionItem;
            try {
                contributionItem = contributionCollection.get(contributionURI);
            } catch (NotFoundException e) {
                return entries.toArray(new Entry[entries.size()]);
            }
            
            // Read the contribution
            Contribution contribution;
            try {
                contribution = contribution(contributionURI, contributionItem.getAlternate());
            } catch (ContributionReadException e) {
                return entries.toArray(new Entry[entries.size()]);
            }

            // Create entries for the deployable composites
            for (Composite deployable: contribution.getDeployables()) {
                entries.add(entry(contribution, deployable));
            }

            return entries.toArray(new Entry[entries.size()]);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Returns the contribution with the given URI.
     * 
     * @param contributionURI
     * @param contributionLocation
     * @return
     * @throws NotFoundException
     */
    private Contribution contribution(String contributionURI, String contributionLocation) throws ContributionReadException {
        try {
            URI uri = URI.create(contributionURI);
            URL location = locationURL(contributionLocation);
            
            // Get contribution from cache
            ContributionCache contributionCache = cache.contributions.get(location);
            long lastModified = lastModified(location);
            if (contributionCache != null) {
                if (contributionCache.contributionLastModified == lastModified) {
                    return contributionCache.contribution;
                }
                
                // Reset contribution cache
                cache.contributions.remove(location);
            }
            
            Contribution contribution = (Contribution)contributionProcessor.read(null, uri, location);
            
            contributionProcessor.resolve(contribution, new DefaultModelResolver());
            
            // Cache contribution
            contributionCache = new ContributionCache();
            contributionCache.contribution = contribution;
            contributionCache.contributionLastModified = lastModified;
            cache.contributions.put(location, contributionCache);
            
            return contribution;

        } catch (ContributionReadException e) {
            throw e;
        } catch (MalformedURLException e) {
            throw new ContributionReadException(e);
        } catch (ContributionResolveException e) {
            throw new ContributionReadException(e);
        } catch (Throwable e) {
            throw new ContributionReadException(e);
        }
    }
    
    /**
     * Returns the entry contents describing a composite.
     * 
     * @param composite
     * @return
     */
    private static String content(Composite composite) {
        StringBuffer sb = new StringBuffer();
        List<Component> components = composite.getComponents();
        for (int i = 0, n = components.size(); i < n; i++) {
            Component component = components.get(i);
            if (component.getImplementation() instanceof NodeImplementation) {
                List<ComponentService> services = component.getServices();
                if (!services.isEmpty()) {
                    List<Binding> bindings = services.get(0).getBindings();
                    if (!bindings.isEmpty()) {
                        
                        // List node URIs
                        sb.append("Node URI: <span id=\"nodeURI\">");
                        sb.append(component.getServices().get(0).getBindings().get(0).getURI());
                        break;
                    }
                }
            } else {
                
                // List component names
                if (sb.length() == 0) {
                    sb.append("Components: <span id=\"components\">");
                } else {
                    sb.append(" ");
                }
                sb.append(component.getName());
            }
        }
        if (sb.length() != 0) {
            sb.append("</span>");
        }
        return sb.toString();
    }
    
    /**
     * Returns the link to the resource related to a composite.
     * 
     * @param composite
     * @return
     */
    private static String relatedLink(Composite composite) {
        for (Component component: composite.getComponents()) {
            if (component.getImplementation() instanceof NodeImplementation) {
                NodeImplementation nodeImplementation = (NodeImplementation)component.getImplementation();
                Composite deployable = nodeImplementation.getComposite();
                String contributionURI = deployable.getURI();
                QName qname = deployable.getName();
                String key = compositeKey(contributionURI, qname);
                return "/composite-source/" + key;
            }
        }
        return null;
    }
    
    /**
     * Returns an entry describing the given deployable.
     * 
     * @param contribution
     * @param deployable
     * @return
     */
    private static Entry<String, Item> entry(Contribution contribution, Composite deployable) {
        Entry<String, Item> entry = new Entry<String, Item>();
        entry.setKey(DomainManagerUtil.compositeKey(contribution.getURI(), deployable.getName()));
        entry.setData(item(contribution, deployable));
        return entry;
    }

    /**
     * Returns an item describing the given deployable.
     * 
     * @param contribution
     * @param deployable
     * @return
     */
    private static Item item(Contribution contribution, Composite deployable) {
        String contributionURI = contribution.getURI();
        String contributionLocation = contribution.getLocation();
        QName qname = deployable.getName();
        String deployableURI = deployable.getURI();
        Item item = new Item();
        item.setTitle(compositeTitle(contributionURI, qname));
        item.setContents(content(deployable));
        item.setLink(compositeSourceLink(contributionURI, qname));
        item.setAlternate(compositeAlternateLink(contributionLocation, deployableURI));
        item.setRelated(relatedLink(deployable));
        return item;
    }

}
