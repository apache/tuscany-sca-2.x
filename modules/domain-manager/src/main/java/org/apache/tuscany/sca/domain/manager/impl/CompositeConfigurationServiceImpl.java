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
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.contributionURI;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.locationURL;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeIncludeBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;
import org.apache.tuscany.sca.implementation.node.builder.impl.NodeCompositeBuilderImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionContentProcessor;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a service that returns a fully configured composite. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(Servlet.class)
public class CompositeConfigurationServiceImpl extends HttpServlet implements Servlet {
    private static final long serialVersionUID = -8809641932774129152L;
    
    private static final Logger logger = Logger.getLogger(CompositeConfigurationServiceImpl.class.getName());

    @Reference
    public LocalItemCollection contributionCollection;
    
    @Reference 
    public LocalItemCollection domainCompositeCollection;
    
    @Reference
    public DomainManagerConfiguration domainManagerConfiguration;
    
    @Reference 
    public LocalItemCollection cloudCollection;    

    private ModelFactoryExtensionPoint modelFactories;
    private ModelResolverExtensionPoint modelResolvers;
    private AssemblyFactory assemblyFactory;
    private WorkspaceFactory workspaceFactory;
    private URLArtifactProcessor<Contribution> contributionProcessor;
    private StAXArtifactProcessorExtensionPoint staxProcessors;
    private StAXArtifactProcessor<Composite> compositeProcessor;
    private XMLOutputFactory outputFactory;
    private ContributionDependencyBuilder contributionDependencyBuilder;
    private CompositeBuilder compositeBuilder;
    private CompositeBuilder compositeIncludeBuilder;
    private CompositeBuilder nodeConfigurationBuilder;
    private Monitor monitor;
    
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
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class);
        
        // Get and initialize artifact processors
        staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        compositeProcessor = (StAXArtifactProcessor<Composite>)staxProcessors.getProcessor(Composite.class);
        StAXArtifactProcessor<Object> staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, monitor);

        URLArtifactProcessorExtensionPoint urlProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        URLArtifactProcessor<Object> urlProcessor = new ExtensibleURLArtifactProcessor(urlProcessors, monitor);
        
        // Create contribution processor
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        contributionProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlProcessor, staxProcessor, monitor);
        
        // Create contribution and composite builders
        contributionDependencyBuilder = new ContributionDependencyBuilderImpl(monitor);
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory intentAttachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        InterfaceContractMapper contractMapper = utilities.getUtility(InterfaceContractMapper.class);
        compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, intentAttachPointTypeFactory, contractMapper, monitor);
        compositeIncludeBuilder = new CompositeIncludeBuilderImpl(monitor);
        nodeConfigurationBuilder = new NodeCompositeBuilderImpl(assemblyFactory, scaBindingFactory, contractMapper, null, monitor);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        String key;
        if (path.startsWith("/")) {
            if (path.length() > 1) {
                key = path.substring(1);
            } else {
                key ="";
            }
        } else {
            key =path;
        }
        logger.fine("get " + key);
        
        // Expect a key in the form composite:contributionURI;namespace;localName or
        // a path in the form componentName/componentName/...
        // and return the corresponding resolved composite
        String requestedContributionURI = null;
        QName requestedCompositeName = null;
        String[] requestedComponentPath = null;
        if (key.startsWith("composite:")) {
            
            // Extract the composite qname from the key
            requestedContributionURI = contributionURI(key);
            requestedCompositeName = compositeQName(key);
            
        } else if (key.length() != 0) {
            
            // Extract the path to the requested component from the key
            requestedComponentPath = key.split("/");
        }
        
        // Somewhere to store the composite we expect to write out at the end
        Composite requestedComposite = null;

        // Create a domain composite model
        Composite domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName(Constants.SCA10_TUSCANY_NS, "domain"));
            
        // Get the domain composite items
        Entry<String, Item>[] domainEntries = domainCompositeCollection.getAll();
        
        // Populate the domain composite
        Workspace workspace = workspaceFactory.createWorkspace();
        workspace.setModelResolver(new ExtensibleModelResolver(workspace, modelResolvers, modelFactories));
        
        Map<String, Contribution> contributionMap = new HashMap<String, Contribution>(); 
        for (Entry<String, Item> domainEntry: domainEntries) {
            
            // Load the required contributions
            String contributionURI = contributionURI(domainEntry.getKey());
            Contribution contribution = contributionMap.get(contributionURI);
            if (contribution == null) {
                
                // The contribution has not been loaded yet, load it with all its dependencies
                Entry<String, Item>[] entries = contributionCollection.query("alldependencies=" + contributionURI);
                for (Entry<String, Item> entry: entries) {
                    Item dependencyItem = entry.getData();
                    String dependencyURI = entry.getKey();
                    
                    if (!contributionMap.containsKey(dependencyURI)) {
                        
                        // Read the contribution
                        Contribution dependency;
                        try {
                            String dependencyLocation = dependencyItem.getAlternate();
                            dependency = contribution(workspace, dependencyURI, dependencyLocation);
                        } catch (ContributionReadException e) {
                            continue;
                        }
                        workspace.getContributions().add(dependency);
                        contributionMap.put(dependencyURI, dependency);
                        
                        if (contributionURI.equals(entry.getKey())) {
                            contribution = dependency;
                        }
                    }
                }
            }
            
            if (contribution == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, contributionURI);
                return;
            }
            
            // Find the specified deployable composite in the contribution
            Composite deployable = null;
            QName qn = compositeQName(domainEntry.getKey());
            for (Composite d: contribution.getDeployables()) {
                if (qn.equals(d.getName())) {
                    deployable = d;
                    break;
                }
            }
            if (deployable == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, qn.toString());
                return;
            }
            
            // add the deployable composite to the domain composite
            domainComposite.getIncludes().add(deployable);

            // Fuse includes into the deployable composite
            try {
                compositeIncludeBuilder.build(deployable);
            } catch (CompositeBuilderException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            }
            
            // Store away the requested composite  
            if (requestedCompositeName != null) {
                if (requestedContributionURI.equals(contributionURI) && requestedCompositeName.equals(deployable.getName())){
                    requestedComposite = deployable;
                }
            }
        }
        
        // The requested composite was not found
        if (requestedCompositeName != null && requestedComposite == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
            return;
        }

        // Get the clouds composite
        Composite cloudsComposite;
        try {
            cloudsComposite = cloud();
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        }
        
        // configure the endpoints for each composite in the domain
        List<Composite> domainIncludes = domainComposite.getIncludes(); 
        for (int i = 0, n =domainIncludes.size(); i < n; i++) {
            Composite composite = domainIncludes.get(i);
            QName compositeName = composite.getName();
            String contributionURI = contributionURI(domainEntries[i].getKey());
            
            // find the node that will run this composite and the default
            // bindings that it configures
            Component nodeComponent = null;
            QName nodeCompositeName = null;
            for (Composite cloudComposite : cloudsComposite.getIncludes()) {
                for (Component nc : cloudComposite.getComponents()) {
                    NodeImplementation nodeImplementation = (NodeImplementation)nc.getImplementation();
                    if (nodeImplementation.getComposite().getName().equals(compositeName) &&
                        nodeImplementation.getComposite().getURI().equals(contributionURI)) {
                        nodeImplementation.setComposite(composite);
                        nodeComponent = nc;
                        nodeCompositeName = cloudComposite.getName();
                        break;
                    }
                }
            }

            if (nodeComponent != null) {
                try {
                    Composite nodeComposite = assemblyFactory.createComposite();
                    nodeComposite.setName(nodeCompositeName);
                    nodeComposite.getComponents().add(nodeComponent);
                    nodeConfigurationBuilder.build(nodeComposite);
                } catch (CompositeBuilderException e) {
                    throw new ServletException(e);
                }
            }
        }
        
        // Build the domain composite
        try {
            compositeBuilder.build(domainComposite);
        } catch (CompositeBuilderException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            return;
        }        

        // Return the requested composite
        if (requestedComposite != null) {
            
            // Rebuild the requested composite from the domain composite
            // we have to reverse the flattening that went on when the domain
            // composite was built
            List<Component> tempComponentList = new ArrayList<Component>();
            tempComponentList.addAll(requestedComposite.getComponents());
            requestedComposite.getComponents().clear();
            for (Component inputComponent : tempComponentList){
                for (Component deployComponent : domainComposite.getComponents()){
                    if (deployComponent.getName().equals(inputComponent.getName())){
                        requestedComposite.getComponents().add(deployComponent);
                    }
                }
            }
            
        } else if (requestedComponentPath != null) {
            
            // If a component path was specified, walk the path to get to the requested
            // component and the composite that implements it
            Composite nestedComposite = domainComposite;
            for (String componentName: requestedComponentPath) {
                Component component = null;
                for (Component c: nestedComposite.getComponents()) {
                    if (componentName.equals(c.getName())) {
                        component = c;
                        break;
                    }
                }
                if (component == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
                    return;
                } else {
                    if (component.getImplementation() instanceof Composite) {
                        nestedComposite = (Composite)component.getImplementation();
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
                        return;
                    }
                }
            }
            
            // Return the nested composite
            requestedComposite = nestedComposite;
            
        } else {
            
            
            // Return the whole domain composite
            requestedComposite = domainComposite;
        }
        
        // Write the composite in the requested format
        StAXArtifactProcessor<Composite> processor;
        String queryString = request.getQueryString();
        if (queryString != null && queryString.startsWith("format=")) {
            String format = queryString.substring(7);
            int s = format.indexOf(';');
            QName formatName = new QName(format.substring(0, s), format.substring(s +1));
            processor = (StAXArtifactProcessor<Composite>)staxProcessors.getProcessor(formatName);
            if (processor == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new IllegalArgumentException(queryString).toString());
                return;
            }
        } else {
            processor = compositeProcessor;
        }
        try {
            response.setContentType("text/xml");
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(response.getOutputStream());
            processor.write(requestedComposite, writer);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            return;
        }
        
        
    }

    /**
     * Returns the cloud composite.
     * 
     * @return the cloud composite
     */
    private Composite cloud() throws NotFoundException {

        // Create a new composite for the clouds
        Composite cloudComposite = assemblyFactory.createComposite();
        cloudComposite.setName(new QName(Constants.SCA10_TUSCANY_NS, "cloud"));
        
        // Get the collection of cloud composites
        Entry<String, Item>[] cloudEntries = cloudCollection.getAll();
        
        // Load the cloud contributions
        Workspace workspace = workspaceFactory.createWorkspace();
        Map<String, Contribution> contributionMap = new HashMap<String, Contribution>(); 
        for (Entry<String, Item> cloudEntry: cloudEntries) {
            String key = cloudEntry.getKey();
            String contributionURI = contributionURI(key);

            // Load the contribution
            Contribution contribution = contributionMap.get(contributionURI);
            if (contribution == null) {
                Item contributionItem = contributionCollection.get(contributionURI);
                
                // Read the contribution
                try {
                    contribution = contribution(workspace, contributionURI, contributionItem.getAlternate());
                } catch (ContributionReadException e) {
                    continue;
                }
                workspace.getContributions().add(contribution);
                contributionMap.put(contributionURI, contribution);
                
            }

            // Include the cloud composite in the clouds composite
            QName qname = compositeQName(key);
            for (Artifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    if (composite.getName().equals(qname)) {
                        cloudComposite.getIncludes().add(composite);
                    }
                }
            } 
        }
        
        return cloudComposite;
    }

    /**
     * Returns the contribution with the given URI.
     * 
     * @param workspace
     * @param contributionURI
     * @param contributionLocation
     * @return
     * @throws NotFoundException
     */
    private Contribution contribution(Workspace workspace, String contributionURI, String contributionLocation) throws ContributionReadException {
        try {
            URI uri = URI.create(contributionURI);
            URL location = locationURL(contributionLocation);
            Contribution contribution = (Contribution)contributionProcessor.read(null, uri, location);
            
            // Resolve the contribution dependencies
            contributionDependencyBuilder.buildContributionDependencies(contribution, workspace);
            
            contributionProcessor.resolve(contribution, workspace.getModelResolver());
            return contribution;

        } catch (ContributionReadException e) {
            throw e;
        } catch (ContributionResolveException e) {
            throw new ContributionReadException(e);
        } catch (MalformedURLException e) {
            throw new ContributionReadException(e);
        }
    }

}
