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

package org.apache.tuscany.sca.workspace.admin.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.xml.ContributionGeneratedMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataProcessor;
import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.workspace.admin.CompositeCollection;
import org.apache.tuscany.sca.workspace.admin.LocalCompositeCollection;
import org.apache.tuscany.sca.workspace.admin.LocalContributionCollection;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionContentProcessor;
import org.osoa.sca.ServiceRuntimeException;
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
@Service(interfaces={CompositeCollection.class, LocalCompositeCollection.class})
public class DeployableCompositeCollectionImpl implements CompositeCollection, LocalCompositeCollection {
    
    @Reference
    public LocalContributionCollection contributionCollection;

    private ModelFactoryExtensionPoint modelFactories;
    private ModelResolverExtensionPoint modelResolvers;
    private AssemblyFactory assemblyFactory;
    private URLArtifactProcessor<Contribution> contributionContentProcessor;
    
    /**
     * Initialize the workspace administration component.
     */
    @Init
    public void init() throws IOException, ContributionReadException, XMLStreamException, ParserConfigurationException {
        
        // Create factories
        modelFactories = new DefaultModelFactoryExtensionPoint();
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);

        // Create model resolvers
        modelResolvers = new DefaultModelResolverExtensionPoint();

        // Create artifact processors
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(modelFactories);
        StAXArtifactProcessor<Object> staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory);
        staxProcessors.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory, contributionFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new CompositeProcessor(contributionFactory, assemblyFactory, policyFactory, staxProcessor));

        URLArtifactProcessorExtensionPoint urlProcessors = new DefaultURLArtifactProcessorExtensionPoint(modelFactories);
        URLArtifactProcessor<Object> urlProcessor = new ExtensibleURLArtifactProcessor(urlProcessors);
        urlProcessors.addArtifactProcessor(new ContributionMetadataDocumentProcessor(staxProcessor, inputFactory));
        urlProcessors.addArtifactProcessor(new ContributionGeneratedMetadataDocumentProcessor(staxProcessor, inputFactory));
        urlProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        
        // Create contribution processors
        contributionContentProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlProcessor);

    }
    
    public Entry<String, Item>[] getAll() {
        // Return all the deployable composites in the contributions
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        
        // Get the list of contributions in the workspace
        Entry<String, Item>[] contributionEntries = contributionCollection.getAll();

        // Read contribution metadata
        for (Entry<String, Item> contributionEntry: contributionEntries) {
            Contribution contribution;
            try {
                URI uri = URI.create(contributionEntry.getKey());
                URL url = url(contributionEntry.getData().getLink());
                contribution = (Contribution)contributionContentProcessor.read(null, uri, url);
                ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
                contributionContentProcessor.resolve(contribution, modelResolver);
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }

            // Create entries for the deployable composites
            for (Composite deployable: contribution.getDeployables()) {
                Entry<String, Item> entry = new Entry<String, Item>();
                entry.setKey(name(deployable.getName()));
                Item item = new Item();
                item.setTitle(name(deployable.getName()));
                item.setLink(deployableLink(contribution.getLocation(), deployable.getURI()));
                entry.setData(item);
                entries.add(entry);
            }
            
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        throw new UnsupportedOperationException();
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
        if (queryString.startsWith("contribution=")) {

            // Return all the deployable composites in the specified
            // contribution
            List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();

            // Get the specified contribution info 
            String key = queryString.substring(13);
            Item contributionItem;
            try {
                contributionItem = contributionCollection.get(key);
            } catch (NotFoundException e) {
                return entries.toArray(new Entry[entries.size()]);
            }

            Contribution contribution;
            try {
                URI uri = URI.create(key);
                URL url = url(contributionItem.getLink());
                contribution = (Contribution)contributionContentProcessor.read(null, uri, url);
                ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
                contributionContentProcessor.resolve(contribution, modelResolver);
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }

            // Create entries for the deployable composites
            for (Composite deployable: contribution.getDeployables()) {
                Entry<String, Item> entry = new Entry<String, Item>();
                entry.setKey(name(deployable.getName()));
                Item item = new Item();
                item.setTitle(name(deployable.getName()));
                item.setLink(deployableLink(contribution.getLocation(), deployable.getURI()));
                entry.setData(item);
                entries.add(entry);
            }

            return entries.toArray(new Entry[entries.size()]);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    private static String deployableLink(String contributionLocation, String deployableURI) {
        URI uri = URI.create(contributionLocation);
        if (uri.getPath().startsWith("/files/")) {
            return contributionLocation + "!/" + deployableURI;
        } else {
            return "/files/" + contributionLocation + "!/" + deployableURI;
        }
    }
    
    /**
     * Returns a qname object from its expression as namespace#localpart.
     * @param name
     * @return
     */
    private static QName qname(String name) {
        int i = name.indexOf('}');
        if (i != -1) {
            return new QName(name.substring(1, i), name.substring(i + 1));
        } else {
            return new QName(name);
        }
    }
    
    /**
     * Returns a qname expressed as namespace#localpart.
     * @param qname
     * @return
     */
    private static String name(QName qname) {
        String ns = qname.getNamespaceURI();
        if (ns != null) {
            return '{' + ns + '}' + qname.getLocalPart();
        } else {
            return qname.getLocalPart();
        }
    }

    /**
     * Returns a URL from a location string.
     * @param location
     * @return
     * @throws MalformedURLException
     */
    private URL url(String location) throws MalformedURLException {
        URI uri = URI.create(location);
        if (uri.getScheme() == null) {
            File file = new File(location);
            return file.toURI().toURL();
        } else {
            return uri.toURL();
        }
    }

}
