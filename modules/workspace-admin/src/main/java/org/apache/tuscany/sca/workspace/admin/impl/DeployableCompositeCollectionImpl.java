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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
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
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.workspace.admin.CompositeCollection;
import org.apache.tuscany.sca.workspace.admin.LocalCompositeCollection;
import org.apache.tuscany.sca.workspace.admin.LocalContributionCollection;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionContentProcessor;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Document;

/**
 * Implementation of a deployable composite collection service. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={CompositeCollection.class, LocalCompositeCollection.class, Servlet.class})
public class DeployableCompositeCollectionImpl extends HttpServlet implements CompositeCollection, LocalCompositeCollection {
    private static final long serialVersionUID = -8809641932774129151L;

    @Reference
    public LocalContributionCollection contributionCollection;

    private ModelFactoryExtensionPoint modelFactories;
    private ModelResolverExtensionPoint modelResolvers;
    private AssemblyFactory assemblyFactory;
    private URLArtifactProcessor<Contribution> contributionContentProcessor;
    private StAXArtifactProcessor<Composite> compositeProcessor;
    private XMLOutputFactory outputFactory;
    private CompositeBuilder compositeBuilder;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws IOException, ContributionReadException, XMLStreamException, ParserConfigurationException {
        
        // Create factories
        modelFactories = new DefaultModelFactoryExtensionPoint();
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);

        // Create artifact processors
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(modelFactories);
        StAXArtifactProcessor<Object> staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory);
        staxProcessors.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory, contributionFactory, staxProcessor));
        compositeProcessor = new CompositeProcessor(contributionFactory, assemblyFactory, policyFactory, staxProcessor);
        staxProcessors.addArtifactProcessor(compositeProcessor);

        URLArtifactProcessorExtensionPoint urlProcessors = new DefaultURLArtifactProcessorExtensionPoint(modelFactories);
        URLArtifactProcessor<Object> urlProcessor = new ExtensibleURLArtifactProcessor(urlProcessors);
        urlProcessors.addArtifactProcessor(new ContributionMetadataDocumentProcessor(staxProcessor, inputFactory));
        urlProcessors.addArtifactProcessor(new ContributionGeneratedMetadataDocumentProcessor(staxProcessor, inputFactory));
        urlProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        
        // Create contribution processor
        modelResolvers = new DefaultModelResolverExtensionPoint();
        contributionContentProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlProcessor);

        // Create composite builder
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory intentAttachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        InterfaceContractMapper contractMapper = new InterfaceContractMapperImpl();
        List<PolicySet> domainPolicySets = new ArrayList<PolicySet>();
        compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, intentAttachPointTypeFactory,
                                                                                            contractMapper, domainPolicySets, null);
    }
    
    public Entry<String, Item>[] getAll() {
        // Return all the deployable composites in the contributions
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        
        // Get the list of contributions in the workspace
        Entry<String, Item>[] contributionEntries = contributionCollection.getAll();

        // Read contribution metadata
        for (Entry<String, Item> contributionEntry: contributionEntries) {
            Contribution contribution = contribution(contributionEntry.getKey(), contributionEntry.getData().getLink());
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
                String key = key(contribution.getURI(), deployable.getName());
                entry.setKey(key);
                Item item = new Item();
                item.setTitle(key);
                item.setLink(deployableLink(contribution.getLocation(), deployable.getURI()));
                entry.setData(item);
                entries.add(entry);
            }
            
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {

        // Get the specified contribution info 
        String contributionURI = uri(key);
        Item contributionItem = contributionCollection.get(contributionURI);
        
        // Read the contribution
        Contribution contribution = contribution(contributionURI, contributionItem.getLink());

        // Find the specified deployable composite
        QName qname = qname(key);
        for (Composite deployable: contribution.getDeployables()) {
            if (qname.equals(deployable.getName())) {
                
                // Return an item describing the deployable composite
                Item item = new Item();
                item.setTitle(key);
                item.setLink(deployableLink(contribution.getLocation(), deployable.getURI()));
                return item;
            }
        }

        throw new NotFoundException();
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
            String contributionURI = queryString.substring(13);
            Item contributionItem;
            try {
                contributionItem = contributionCollection.get(contributionURI);
            } catch (NotFoundException e) {
                return entries.toArray(new Entry[entries.size()]);
            }
            
            // Read the contribution
            Contribution contribution = contribution(contributionURI, contributionItem.getLink());

            // Create entries for the deployable composites
            for (Composite deployable: contribution.getDeployables()) {
                Entry<String, Item> entry = new Entry<String, Item>();
                String key = key(contributionURI, deployable.getName());
                entry.setKey(key);
                Item item = new Item();
                item.setTitle(key);
                item.setLink(deployableLink(contribution.getLocation(), deployable.getURI()));
                entry.setData(item);
                entries.add(entry);
            }

            return entries.toArray(new Entry[entries.size()]);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        String key = path.startsWith("/")? path.substring(1) : path;

        // Get the specified contribution info 
        String contributionURI = uri(key);
        Item contributionItem;
        try {
            contributionItem = contributionCollection.get(contributionURI);
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Read the contribution
        Contribution contribution = contribution(contributionURI, contributionItem.getLink());

        // Find the specified deployable composite
        Composite deployable = null;
        QName qname = qname(key);
        for (Composite d: contribution.getDeployables()) {
            if (qname.equals(d.getName())) {
                deployable = d;
                break;
            }
        }
        if (deployable == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Build the composite
        try {
            compositeBuilder.build(deployable);
        } catch (CompositeBuilderException e) {
            throw new ServletException(e);
        }
        
        // Write the deployable composite back to XML
        try {
            // First write to a byte stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
            compositeProcessor.write(deployable, writer);
            
            // Parse again to pretty format the document
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(2);
            
            // Write to domain.composite
            XMLSerializer serializer = new XMLSerializer(response.getOutputStream(), format);
            serializer.serialize(document);
            
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Returns the contribution with the given URI.
     * 
     * @param contributionURI
     * @return
     * @throws NotFoundException
     */
    private Contribution contribution(String contributionURI, String contributionURL) {
        try {
            URI uri = URI.create(contributionURI);
            URL url = url(contributionURL);
            Contribution contribution = (Contribution)contributionContentProcessor.read(null, uri, url);
            ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
            contributionContentProcessor.resolve(contribution, modelResolver);
            return contribution;

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Returns a link to a deployable composite.
     * 
     * If the containing contribution is a local directory, return the URI of  the local composite file
     * inside the contribution.
     * 
     * If the containing contribution is a local or remote file, return a URI of the form:
     * /files/ contribution URI !/ composite URI.
     * The contribution file servlet at '/files/' will open the contribution and extract the composite
     * file from it.
     *  
     * @param contributionLocation
     * @param deployableURI
     * @return
     */
    private static String deployableLink(String contributionLocation, String deployableURI) {
        URI uri = URI.create(contributionLocation);
        if ("file".equals(uri.getScheme())) {
            if (new File(uri).isDirectory()) {
                return contributionLocation + "/" + deployableURI;
            } else {
                return "/files/" + contributionLocation + "!/" + deployableURI; 
            }
        } else {
            if (uri.getPath().startsWith("/files/")) {
                return contributionLocation + "!/" + deployableURI;
            } else {
                return "/files/" + contributionLocation + "!/" + deployableURI;
            }
        }
    }
    
    /**
     * Extracts a qname from a key expressed as contributionURI;namespace;localpart.
     * @param key
     * @return
     */
    private static QName qname(String key) {
        int i = key.indexOf(';');
        key = key.substring(i + 1);
        i = key.indexOf(';');
        return new QName(key.substring(0, i), key.substring(i + 1));
    }
    
    /**
     * Extracts a contribution uri from a key expressed as contributionURI;namespace;localpart.
     * @param key
     * @return
     */
    private static String uri(String key) {
        int i = key.indexOf(';');
        return key.substring("composite:".length(), i);
    }
    
    /**
     * Returns a composite key expressed as contributionURI;namespace;localpart.
     * @param qname
     * @return
     */
    private static String key(String uri, QName qname) {
        return "composite:" + uri + ';' + qname.getNamespaceURI() + ';' + qname.getLocalPart();
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
