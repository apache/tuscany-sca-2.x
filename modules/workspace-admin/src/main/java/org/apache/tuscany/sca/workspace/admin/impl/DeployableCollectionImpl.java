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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem;
import org.apache.tuscany.sca.assembly.builder.Problem.Severity;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeConfigurationBuilderImpl;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionListener;
import org.apache.tuscany.sca.contribution.service.ContributionListenerExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.xml.ContributionGeneratedMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataProcessor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.ItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
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
@Service(interfaces={ItemCollection.class, LocalItemCollection.class, Servlet.class})
public class DeployableCollectionImpl extends HttpServlet implements ItemCollection, LocalItemCollection {
    private static final long serialVersionUID = -8809641932774129151L;
    
    private final static Logger logger = Logger.getLogger(DeployableCollectionImpl.class.getName());    

    @Reference
    public LocalItemCollection contributionCollection;
    
    @Reference 
    public LocalItemCollection domainCompositeCollection;
    
    @Reference 
    public LocalItemCollection cloudCollection;    

    private ModelFactoryExtensionPoint modelFactories;
    private ModelResolverExtensionPoint modelResolvers;
    private AssemblyFactory assemblyFactory;
    private URLArtifactProcessor<Contribution> contributionContentProcessor;
    private StAXArtifactProcessor<Composite> compositeProcessor;
    private XMLOutputFactory outputFactory;
    private CompositeBuilder compositeBuilder;
    private CompositeConfigurationBuilderImpl compositeConfigurationBuilder;
    private List<ContributionListener> contributionListeners;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws IOException, ContributionReadException, XMLStreamException, ParserConfigurationException {
        
        try {

            // FIXME Remove this later
            // Bootstrap a registry
            ExtensionPointRegistry registry = registry();
            
            // Get model factories
            modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
            assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
            XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
            outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
            outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
            ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
            PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
            
            // Get and initialize artifact processors
            StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
            StAXArtifactProcessor<Object> staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory);
            staxProcessors.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory, contributionFactory, staxProcessor));
            compositeProcessor = (StAXArtifactProcessor<Composite>)staxProcessors.getProcessor(Composite.class);
    
            URLArtifactProcessorExtensionPoint urlProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            URLArtifactProcessor<Object> urlProcessor = new ExtensibleURLArtifactProcessor(urlProcessors);
            urlProcessors.addArtifactProcessor(new ContributionMetadataDocumentProcessor(staxProcessor, inputFactory));
            urlProcessors.addArtifactProcessor(new ContributionGeneratedMetadataDocumentProcessor(staxProcessor, inputFactory));
            
            // Create contribution processor
            modelResolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);
            contributionContentProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlProcessor);
            contributionListeners = registry.getExtensionPoint(ContributionListenerExtensionPoint.class).getContributionListeners();
    
            // Create composite builder
            SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
            IntentAttachPointTypeFactory intentAttachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
            InterfaceContractMapper contractMapper = new InterfaceContractMapperImpl();
            List<PolicySet> domainPolicySets = new ArrayList<PolicySet>();
            
            // TODO need to get these messages back to the browser
            CompositeBuilderMonitor monitor = new CompositeBuilderMonitor() {
                public void problem(Problem problem) {
                    if (problem.getSeverity() == Severity.INFO) {
                        logger.info(problem.toString());
                    } else if (problem.getSeverity() == Severity.WARNING) {
                        logger.warning(problem.toString());
                    } else if (problem.getSeverity() == Severity.ERROR) {
                        if (problem.getCause() != null) {
                            logger.log(Level.SEVERE, problem.toString(), problem.getCause());
                        } else {
                            logger.severe(problem.toString());
                        }
                    }
                }
            };
            
            compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, intentAttachPointTypeFactory,
                                                        contractMapper, domainPolicySets, monitor);
            
            compositeConfigurationBuilder = new CompositeConfigurationBuilderImpl(assemblyFactory, 
                                                                                 scaBindingFactory, 
                                                                                 intentAttachPointTypeFactory,
                                                                                 contractMapper,
                                                                                 monitor);
            
        } catch (Exception e) {
            throw new ContributionReadException(e); 
        }
    }
    
    public Entry<String, Item>[] getAll() {
        // Return all the deployable composites in the contributions
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        
        // Get the list of contributions in the workspace
        Entry<String, Item>[] contributionEntries = contributionCollection.getAll();

        // Read contribution metadata
        for (Entry<String, Item> contributionEntry: contributionEntries) {
            Contribution contribution = contribution(contributionEntry.getKey(), contributionEntry.getData().getLink());

            // Create entries for the deployable composites
            for (Composite deployable: contribution.getDeployables()) {
                Entry<String, Item> entry = new Entry<String, Item>();
                String contributionURI = contribution.getURI();
                QName qname = deployable.getName();
                String key = key(contributionURI, qname);
                entry.setKey(key);
                Item item = new Item();
                item.setTitle(title(contributionURI, qname));
                item.setContents(components(deployable));
                item.setLink(link(contribution.getLocation(), deployable.getURI()));
                item.setContents(components(deployable));
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
                item.setTitle(title(contributionURI, qname));
                item.setContents(components(deployable));
                item.setLink(link(contribution.getLocation(), deployable.getURI()));
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
                QName qname = deployable.getName();
                String key = key(contributionURI, qname);
                entry.setKey(key);
                Item item = new Item();
                item.setTitle(title(contributionURI, qname));
                item.setContents(components(deployable));
                item.setLink(link(contribution.getLocation(), deployable.getURI()));
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
        
        // Expecting a key in the form:
        // composite:contributionURI;namespace;localName
        QName keyQName = qname(key);
        
        // Somewhere to store the composite we expect to write out at the end
        Composite compositeImage = null;

        // Create a domain composite model
        Composite domainComposite = assemblyFactory.createComposite();
        URL url = new URL(request.getRequestURL().toString());
        url= new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
        domainComposite.setName(new QName(url.toString(), "domain"));
            
        // Get the domain composite items
        Entry<String, Item>[] domainEntries = domainCompositeCollection.getAll();
        
        // Populate the domain composite
        List<Contribution> loadedContributions = new ArrayList<Contribution>();
        Map<String, Contribution> contributionMap = new HashMap<String, Contribution>(); 
        for (Entry<String, Item> domainEntry: domainEntries) {
            
            // Load the required contributions
            String contributionURI = uri(domainEntry.getKey());
            Contribution contribution = contributionMap.get(contributionURI);
            if (contribution == null) {
                
                // The contribution has not been loaded yet, load it with all its dependencies
                for (Entry<String, Item> entry: contributionCollection.query("alldependencies=" + contributionURI)) {
                    Item contributionItem = entry.getData();
    
                    // Read the contribution
                    Contribution c = contribution(loadedContributions, entry.getKey(), contributionItem.getLink());
                    loadedContributions.add(c);
                    if (contributionURI.equals(entry.getKey())) {
                        contribution = c;
                        contributionMap.put(contributionURI, contribution);
                    }
                }
            }
            
            if (contribution == null) {
                //FIXME We should just report a warning here
                throw new ServletException(new NotFoundException("Contribution not found: " + contributionURI));
            }
            
            // Find the specified deployable composite in the contribution
            Composite deployable = null;
            QName qname = qname(domainEntry.getKey());
            for (Composite d: contribution.getDeployables()) {
                if (qname.equals(d.getName())) {
                    deployable = d;
                    break;
                }
            }
            if (deployable == null) {
                //FIXME We should just report a warning here
                throw new ServletException(new NotFoundException("Deployable not found: " + qname));
            }
            
            // add the deployable composite to the domain composite
            domainComposite.getIncludes().add(deployable);
            
            // store away the composite we are generating the deployable XML for. 
            if (keyQName.equals(deployable.getName())){
                compositeImage = deployable;
            }
        }

        // Get the clouds composite
        Composite cloudsComposite;
        try {
            cloudsComposite = clouds();
        } catch (NotFoundException e) {
            //FIXME We should just report a warning here
            throw new ServletException(e);
        }
        
        // configure the endpoints for each composite in the domain
        List<Composite> domainIncludes = domainComposite.getIncludes(); 
        for (int i = 0, n =domainIncludes.size(); i < n; i++) {
            Composite composite = domainIncludes.get(i);
            QName compositeName = composite.getName();
            String contributionURI = uri(domainEntries[i].getKey());
            
            // find the node that will run this composite and the default
            // bindings that it configures
            Component node = null;
            for (Composite cloudComposite : cloudsComposite.getIncludes()) {
                for (Component nc : cloudComposite.getComponents()) {
                    NodeImplementation nodeImplementation = (NodeImplementation)nc.getImplementation();
                    if (nodeImplementation.getComposite().getName().equals(compositeName) &&
                        nodeImplementation.getComposite().getURI().equals(contributionURI)) {
                        node = nc;
                        break;
                    }
                }
            }

            if (node != null) {
                try {
                    List<Binding> defaultBindings = node.getServices().get(0).getBindings();
                    compositeConfigurationBuilder.configureBindingURIs(composite, null, defaultBindings);
                } catch (CompositeBuilderException e) {
                    throw new ServletException(e);
                }
            }
        }
        
        // build the domain composite
        try {
            compositeBuilder.build(domainComposite);
        } catch (CompositeBuilderException e) {
            throw new ServletException(e);
        }        
        
        // rebuild the requested composite from the domain composite
        // we have to reverse the flatterning that went on when the domain
        // composite was built
        List<Component> tempComponentList = new ArrayList<Component>();
        tempComponentList.addAll(compositeImage.getComponents());
        compositeImage.getComponents().clear();
        
        for (Component inputComponent : tempComponentList){
            for (Component deployComponent : domainComposite.getComponents()){
                if (deployComponent.getName().equals(inputComponent.getName())){
                    compositeImage.getComponents().add(deployComponent);
                }
            }
        }
        
        // Write the deployable composite
        try {
            // First write to a byte stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
            compositeProcessor.write(compositeImage, writer);
            
            // Parse again to pretty format the document
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(2);
            
            // Write to the response stream
            XMLSerializer serializer = new XMLSerializer(response.getOutputStream(), format);
            serializer.serialize(document);
            
        } catch (Exception e) {
            throw new ServletException(e);
        }
               
    }

    /**
     * Returns the clouds composite.
     * 
     * @return the clouds composite
     */
    private Composite clouds() throws NotFoundException {

        // Create a new composite for the clouds
        Composite clouds = assemblyFactory.createComposite();
        
        // Get the collection of cloud composites
        Entry<String, Item>[] cloudEntries = cloudCollection.getAll();
        
        // Load the cloud composites
        List<Contribution> loadedContributions = new ArrayList<Contribution>();
        Map<String, Contribution> contributionMap = new HashMap<String, Contribution>(); 
        for (int i=0; i < cloudEntries.length; i++) {

            // load the contribution
            String contributionURI = uri(cloudEntries[i].getKey());
            Contribution contribution = contributionMap.get(contributionURI);
            if (contribution == null) {
                Item contributionItem = contributionCollection.get(contributionURI);
                
                // Read the contribution
                contribution = contribution(loadedContributions, contributionURI, contributionItem.getLink());
                loadedContributions.add(contribution);
                contributionMap.put(contributionURI, contribution);
            }
            
            // Include the composite in the clouds composite
            for (Artifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    clouds.getIncludes().add(composite);
                }
            } 
        }
        
        return clouds;
    }

    /**
     * Returns the contribution with the given URI.
     * 
     * @param contributionURI
     * @return
     * @throws NotFoundException
     */
    private Contribution contribution(List<Contribution> contributions, String contributionURI, String contributionURL) {
        try {
            URI uri = URI.create(contributionURI);
            URL url = url(contributionURL);
            Contribution contribution = (Contribution)contributionContentProcessor.read(null, uri, url);
            
            // FIXME simplify this later
            // Fix up contribution imports
            ContributionRepository dummyRepository = new DummyContributionRepository(contributions);
            for (ContributionListener listener: contributionListeners) {
                listener.contributionAdded(dummyRepository, contribution);
            }
            
            ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
            contributionContentProcessor.resolve(contribution, modelResolver);
            return contribution;

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private Contribution contribution(String contributionURI, String contributionURL) {
        return contribution(new ArrayList<Contribution>(), contributionURI, contributionURL);
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
    private static String link(String contributionLocation, String deployableURI) {
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
     * Returns the list of components in a composite.
     * 
     * @param composite
     * @return
     */
    private static String components(Composite composite) {
        StringBuffer sb = new StringBuffer();
        for (Component component: composite.getComponents()) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(component.getName());
        }
        return sb.toString();
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
     * Returns a composite title expressed as contributionURI - namespace;localpart.
     * @param qname
     * @return
     */
    private static String title(String uri, QName qname) {
        return uri + " - " + qname.getNamespaceURI() + ';' + qname.getLocalPart();
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

    /**
     * Temporary instantiation of a dummy runtime to get a registry populated
     * with all the right things.
     * 
     * @return the registry
     */
    private ExtensionPointRegistry registry() {
        try {
            ReallySmallRuntime runtime = new ReallySmallRuntime(Thread.currentThread().getContextClassLoader());
            runtime.start();
            return runtime.getExtensionPointRegistry();
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * FIXME Remove this later
     * DummyContributionRepository
     */
    private class DummyContributionRepository implements ContributionRepository {
        
        private List<Contribution> contributions;

        public DummyContributionRepository(List<Contribution> contributions) {
            this.contributions = contributions;
        }
        
        public void addContribution(Contribution contribution) {}
        public URL find(String contribution) { return null; }
        public Contribution getContribution(String uri) { return null; }
        public List<Contribution> getContributions() { return contributions; }
        public URI getDomain() { return null; }
        public List<String> list() { return null; }
        public void remove(String contribution) {}
        public void removeContribution(Contribution contribution) {}
        public URL store(String contribution, URL sourceURL, InputStream contributionStream) throws IOException { return null; }
        public URL store(String contribution, URL sourceURL) throws IOException { return null;}
        public void updateContribution(Contribution contribution) {}
    }
}
