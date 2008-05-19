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

import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.DEPLOYMENT_CONTRIBUTION_URI;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeSimpleTitle;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeSourceLink;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.locationURL;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.ItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Document;

/**
 * Implementation of a contribution collection service component. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={ItemCollection.class, LocalItemCollection.class})
public class ContributionCollectionImpl implements ItemCollection, LocalItemCollection {

    private static final Logger logger = Logger.getLogger(ContributionCollectionImpl.class.getName());

    @Property
    public String workspaceFile;
    
    @Property
    public String deploymentContributionDirectory;
    
    @Reference
    public DomainManagerConfiguration domainManagerConfiguration;
    
    private Monitor monitor;
    private ContributionFactory contributionFactory;
    private WorkspaceFactory workspaceFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private URLArtifactProcessor<Contribution> contributionProcessor;
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private DocumentBuilder documentBuilder;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws ParserConfigurationException {
        
        ExtensionPointRegistry extensionPoints = domainManagerConfiguration.getExtensionPoints();
        
        // Create a validation monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
        
        // Create model factories
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class);
        
        // Create artifact processors
        inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, monitor);

        URLArtifactProcessorExtensionPoint urlProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        
        // Create contribution info processor
        contributionProcessor = urlProcessors.getProcessor(".contribution/info");

        // Create a document builder (used to pretty print XML)
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    
    public Entry<String, Item>[] getAll() {
        logger.fine("getAll");

        // Return all the contributions
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        Workspace workspace = readContributions(readWorkspace());
        
        for (Contribution contribution: workspace.getContributions()) {
            if (contribution.getURI().equals(DEPLOYMENT_CONTRIBUTION_URI)) {
                continue;
            }
            entries.add(entry(workspace, contribution));
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        logger.fine("get " + key);

        // Returns the contribution with the given URI key
        Workspace workspace = readContributions(readWorkspace());
        for (Contribution contribution: workspace.getContributions()) {
            if (key.equals(contribution.getURI())) {
                return item(workspace, contribution);
            }
        }
        throw new NotFoundException(key);
    }
    
    public String post(String key, Item item) {
        logger.fine("post " + key);
        
        // Adds a new contribution to the workspace
        Workspace workspace = readWorkspace();
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(key);
        try {
            contribution.setLocation(locationURL(item.getLink()).toString());
        } catch (MalformedURLException e) {
            throw new ServiceRuntimeException(e);
        }
        workspace.getContributions().add(contribution);
        
        // Write the workspace
        writeWorkspace(workspace);
        
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        
        // Update a contribution already in the workspace
        Workspace workspace = readWorkspace();
        Contribution newContribution = contributionFactory.createContribution();
        newContribution.setURI(key);
        try {
            newContribution.setLocation(locationURL(item.getLink()).toString());
        } catch (MalformedURLException e) {
            throw new ServiceRuntimeException(e);
        }
        List<Contribution> contributions = workspace.getContributions();
        for (int i = 0, n = contributions.size(); i < n; i++) {
            if (contributions.get(i).getURI().equals(key)) {
                contributions.set(i, newContribution);
                
                // Write the workspace
                writeWorkspace(workspace);
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public void delete(String key) throws NotFoundException {
        logger.fine("delete " + key);
        
        // Delete a contribution from the workspace
        Workspace workspace = readWorkspace();
        List<Contribution> contributions = workspace.getContributions();
        for (int i = 0, n = contributions.size(); i < n; i++) {
            if (contributions.get(i).getURI().equals(key)) {
                contributions.remove(i);

                // Write the workspace
                writeWorkspace(workspace);
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public Entry<String, Item>[] query(String queryString) {
        logger.fine("query " + queryString);
        
        if (queryString.startsWith("dependencies=") || queryString.startsWith("alldependencies=")) {

            // Return the collection of dependencies of the specified contribution
            List<Entry<String, Item>> entries = new ArrayList<Entry<String,Item>>();
            
            // Extract the contribution URI
            int eq = queryString.indexOf('=');
            String key = queryString.substring(eq+1);
            
            // Read the metadata for all the contributions
            Workspace workspace = readContributions(readWorkspace());
            
            // Look for the specified contribution
            for (Contribution contribution: workspace.getContributions()) {
                if (key.equals(contribution.getURI())) {                

                    // Compute the contribution dependencies
                    ContributionDependencyBuilder analyzer = new ContributionDependencyBuilderImpl(monitor);
                    List<Contribution> dependencies = analyzer.buildContributionDependencies(contribution, workspace);
                    
                    // Returns entries for the dependencies
                    // optionally skip the specified contribution
                    boolean allDependencies = queryString.startsWith("alldependencies=");
                    for (Contribution dependency: dependencies) {
                        if (!allDependencies && dependency == contribution) {
                            // Skip the specified contribution
                            continue;
                        }
                        entries.add(entry(workspace, dependency));
                    }
                    break;
                }
            }

            return entries.toArray(new Entry[entries.size()]);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Returns an entry representing a contribution
     * @param contribution
     * @return
     */
    private static Entry<String, Item> entry(Workspace workspace, Contribution contribution) {
        Entry<String, Item> entry = new Entry<String, Item>();
        entry.setKey(contribution.getURI());
        entry.setData(item(workspace, contribution));
        return entry;
    }
    
    /**
     * Returns an item representing a contribution.
     * 
     * @param contribution
     * @return
     */
    private static Item item(Workspace workspace, Contribution contribution) {
        String contributionURI = contribution.getURI();
        Item item = new Item();
        item.setTitle(title(contributionURI));
        item.setLink(link(contributionURI));
        item.setAlternate(contribution.getLocation());
        
        // List the contribution dependencies in the item contents
        final List<String> problems = new ArrayList<String>();
        Monitor monitor = new Monitor() {
            public void problem(Problem problem) {
                problems.add(problem.getMessageId() + " " + problem.getProblemObject().toString());
            }
        };
        
        StringBuffer sb = new StringBuffer();
        ContributionDependencyBuilderImpl analyzer = new ContributionDependencyBuilderImpl(monitor);
        List<Contribution> dependencies = analyzer.buildContributionDependencies(contribution, workspace);
        if (dependencies.size() > 1) {
            sb.append("Dependencies: <span id=\"dependencies\">");
            for (int i = 0, n = dependencies.size(); i < n ; i++) {
                if (i > 0) {
                    sb.append("  ");
                }
                Contribution dependency = dependencies.get(i);
                if (dependency != contribution) {
                    String dependencyURI = dependency.getURI();
                    sb.append("<a href=\""+ link(dependencyURI) +"\">" + title(dependencyURI) + "</a>");
                }
            }
            sb.append("</span><br>");
        }
        
        // List the deployables
        List<Composite> deployables = contribution.getDeployables();
        if (!deployables.isEmpty()) {
            sb.append("Deployables: <span id=\"deployables\">");
            for (int i = 0, n = deployables.size(); i < n ; i++) {
                if (i > 0) {
                    sb.append("  ");
                }
                Composite deployable = deployables.get(i);
                QName qname = deployable.getName();
                sb.append("<a href=\""+ compositeSourceLink(contributionURI, qname) +"\">" + compositeSimpleTitle(contributionURI, qname) + "</a>");
            }
            sb.append("</span><br>");
        }
        
        // List the dependency problems
        if (contribution.isUnresolved()) {
            problems.add("Contribution not found");
        }
        if (problems.size() > 0) {
            sb.append("<span id=\"problems\" style=\"color: red\">");
            for (int i = 0, n = problems.size(); i < n ; i++) {
                sb.append("Problem: "+ problems.get(i) + "<br>");
            }
            sb.append("</span>");
        }
        
        // Store in the item contents
        item.setContents(sb.toString());
        
        return item;
    }

    /**
     * Returns a link to a contribution.
     * @param contributionURI
     * @return
     */
    private static String link(String contributionURI) {
        return "/contribution/" + contributionURI;
    }
    
    /**
     * Returns a title for the given contribution
     * 
     * @param contributionURI
     * @return
     */
    private static String title(String contributionURI) {
        return contributionURI;
    }

    
    /**
     * Read the workspace.
     * 
     * @return
     */
    private Workspace readWorkspace() {
        String rootDirectory = domainManagerConfiguration.getRootDirectory();
        
        Workspace workspace;
        File file = new File(rootDirectory + "/" + workspaceFile);
        if (file.exists()) {
            try {
                FileInputStream is = new FileInputStream(file);
                XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
                reader.nextTag();
                workspace = (Workspace)staxProcessor.read(reader);
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }
        } else {
            workspace = workspaceFactory.createWorkspace();
        }
        
        // Make sure that the workspace contains the cloud contribution
        // The cloud contribution contains the composites describing the
        // SCA nodes declared in the cloud
        Contribution cloudContribution = null;
        for (Contribution contribution: workspace.getContributions()) {
            if (contribution.getURI().equals(DEPLOYMENT_CONTRIBUTION_URI)) {
                cloudContribution = contribution;
            }
        }
        if (cloudContribution == null) {
            Contribution contribution = contributionFactory.createContribution();
            contribution.setURI(DEPLOYMENT_CONTRIBUTION_URI);
            File cloudDirectory = new File(rootDirectory + "/" + deploymentContributionDirectory);
            contribution.setLocation(cloudDirectory.toURI().toString());
            workspace.getContributions().add(contribution);
        }
        return workspace;
    }
    
    /**
     * Write the workspace back to disk
     * 
     * @param workspace
     */
    private void writeWorkspace(Workspace workspace) {
        try {
            String rootDirectory = domainManagerConfiguration.getRootDirectory();
            
            // First write to a byte stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
            staxProcessor.write(workspace, writer);
            
            // Parse again to pretty format the document
            Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(2);
            
            // Write to workspace.xml
            FileOutputStream os = new FileOutputStream(new File(rootDirectory + "/" + workspaceFile));
            XMLSerializer serializer = new XMLSerializer(os, format);
            serializer.serialize(document);
            os.close();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Returns a workspace populated with the contribution info read from
     * the contributions.
     * 
     * @param workspace
     * @return
     */
    private Workspace readContributions(Workspace workspace) {
        Workspace dependencyWorkspace = workspaceFactory.createWorkspace();
        try {
            for (Contribution c: workspace.getContributions()) {
                URI uri = URI.create(c.getURI());
                URL url = locationURL(c.getLocation());
                try {
                    Contribution contribution = (Contribution)contributionProcessor.read(null, uri, url);
                    contribution.setUnresolved(false);
                    dependencyWorkspace.getContributions().add(contribution);
                } catch (ContributionReadException e) {
                    Contribution contribution = contributionFactory.createContribution();
                    contribution.setURI(c.getURI());
                    contribution.setLocation(c.getLocation());
                    contribution.setUnresolved(true);
                    dependencyWorkspace.getContributions().add(contribution);
                }
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return dependencyWorkspace;
    }
    
}
