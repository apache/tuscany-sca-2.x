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

package manager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

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
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderExtensionPoint;
import org.apache.tuscany.sca.binding.atom.AtomBindingFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionBuilder;
import org.apache.tuscany.sca.workspace.builder.ContributionBuilderExtensionPoint;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Sample DistributeComponents task
 *
 * This sample shows how to use a subset of Tuscany to read contribution
 * metadata, analyze and resolve contribution dependencies, read and resolve
 * the artifacts that they contribute (in particular implementation artifacts,
 * interfaces, composites, componentTypes etc.) and assembe and wire the
 * deployable composites together in a composite model representing an SCA
 * domain composite.
 * 
 * The difference between this sample and the WireComponents sample is an
 * extra step to allocate deployable composites to SCA nodes. SCA nodes allow
 * you to provide default configuration for the deploayable composites allocated
 * to them, for example default binding configuration. 
 * 
 * The sample first reads the SCA metadata for three sample contributions,
 * reads and resolve the artifacts contained in the contributions, includes all their
 * deployable composites in a composite model representing an SCA domain, then
 * uses several composite builder utilities to configure them as specified in the
 * SCA nodes hosting them and assemble and wire them together.
 * Finally it prints the resulting domain composite model, showing service bindings
 * configured with the URIs from the nodes hosting them.
 *
 * @version $Rev$ $Date$
 */
public class DistributeComponents {
    
    private static URLArtifactProcessor<Contribution> contributionProcessor;
    private static Monitor monitor;
    private static ModelResolverExtensionPoint modelResolvers;
    private static FactoryExtensionPoint modelFactories;
    private static WorkspaceFactory workspaceFactory;
    private static AssemblyFactory assemblyFactory;
    private static XMLOutputFactory outputFactory;
    private static StAXArtifactProcessor<Object> xmlProcessor; 
    private static ContributionBuilder contributionDependencyBuilder;
    private static CompositeBuilder domainCompositeBuilder;
    private static CompositeBuilder nodeCompositeBuilder;
    private static NodeImplementationFactory nodeFactory;
    private static AtomBindingFactory atomBindingFactory;

    private static void init() {
        
        // Create extension point registry 
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
        
        // Initialize the Tuscany module activators
        ModuleActivatorExtensionPoint moduleActivators = extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);
        for (ModuleActivator activator: moduleActivators.getModuleActivators()) {
            activator.start(extensionPoints);
        }

        // Get XML input/output factories
        modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        
        // Get contribution workspace and assembly model factories
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class); 
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        nodeFactory = modelFactories.getFactory(NodeImplementationFactory.class);
        atomBindingFactory = modelFactories.getFactory(AtomBindingFactory.class);
        
        // Create XML artifact processors
        StAXArtifactProcessorExtensionPoint xmlProcessorExtensions = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessorExtensions, inputFactory, outputFactory, monitor);
        
        // Create contribution content processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = docProcessorExtensions.getProcessor(Contribution.class);
        
        // Get the model resolvers
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        
        // Get a contribution dependency builder
        ContributionBuilderExtensionPoint contributionBuilders = extensionPoints.getExtensionPoint(ContributionBuilderExtensionPoint.class);
        contributionDependencyBuilder = contributionBuilders.getContributionBuilder("org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder");
        
        // Get composite builders
        CompositeBuilderExtensionPoint compositeBuilders = extensionPoints.getExtensionPoint(CompositeBuilderExtensionPoint.class);
        domainCompositeBuilder = compositeBuilders.getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");
        nodeCompositeBuilder = compositeBuilders.getCompositeBuilder("org.apache.tuscany.sca.implementation.node.builder.NodeCompositeBuilder");
        
    }
    

    public static void main(String[] args) throws Exception {
        init();

        // Create workspace model
        Workspace workspace = workspaceFactory.createWorkspace();
        workspace.setModelResolver(new ExtensibleModelResolver(workspace, modelResolvers, modelFactories));

        // Read the sample store contribution
        URI storeURI = URI.create("store");
        URL storeURL = new File("./target/sample-domain-management-store.jar").toURI().toURL();
        Contribution storeContribution = contributionProcessor.read(null, storeURI, storeURL);
        workspace.getContributions().add(storeContribution);

        // Read the sample assets contribution
        URI assetsURI = URI.create("assets");
        URL assetsURL = new File("./target/sample-domain-management-assets.jar").toURI().toURL();
        Contribution assetsContribution = contributionProcessor.read(null, assetsURI, assetsURL);
        workspace.getContributions().add(assetsContribution);

        // Read the sample client contribution
        URI clientURI = URI.create("client");
        URL clientURL = new File("./target/sample-domain-management-client.jar").toURI().toURL();
        Contribution clientContribution = contributionProcessor.read(null, clientURI, clientURL);
        workspace.getContributions().add(clientContribution);

        // Build the contribution dependencies
        Set<Contribution> resolved = new HashSet<Contribution>();
        for (Contribution contribution: workspace.getContributions()) {
            contributionDependencyBuilder.build(contribution, workspace, monitor);
            
            // Resolve contributions
            for (Contribution dependency: contribution.getDependencies()) {
                if (!resolved.contains(dependency)) {
                    resolved.add(dependency);
                    contributionProcessor.resolve(dependency, workspace.getModelResolver());
                }
            }
        }
        
        // Create a set of nodes, and assign the sample deployables to them
        Composite cloudComposite = assemblyFactory.createComposite();
        cloudComposite.setName(new QName("http://sample", "cloud"));
        for (int i = 0, n = workspace.getDeployables().size(); i < n; i++) {
            
            // Create a node
            Component node = assemblyFactory.createComponent();
            node.setName("Node" + i);
            cloudComposite.getComponents().add(node);
            
            // Add default binding configuration to the node, our samples use
            // Atom bindings so here we're just creating default Atom binding
            // configurations, but all the other binding types can be configured
            // like that too
            ComponentService nodeService = assemblyFactory.createComponentService();
            Binding binding = atomBindingFactory.createAtomBinding();
            binding.setURI("http://localhost:" + (8100 + i));
            nodeService.getBindings().add(binding);
            node.getServices().add(nodeService);

            // Assign a deployable to the node
            NodeImplementation nodeImplementation = nodeFactory.createNodeImplementation();
            Composite deployable = workspace.getDeployables().get(i);
            nodeImplementation.setComposite(deployable);
            node.setImplementation(nodeImplementation);
        }
        
        // Print the model describing the nodes that we just built
        System.out.println("cloud.composite");
        print(cloudComposite);
        System.out.println();
        
        // Build the nodes, this will apply their default binding configuration to the
        // composites assigned to them
        nodeCompositeBuilder.build(cloudComposite, null, monitor);
        
        // Create a composite model for the domain
        Composite domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName("http://sample", "domain"));
        
        // Add all deployables to it, normally the domain administrator would select
        // the deployables to include
        domainComposite.getIncludes().addAll(workspace.getDeployables());
        
        // Build the domain composite and wire the components included in it
        domainCompositeBuilder.build(domainComposite, null, monitor);

        // Print out the resulting domain composite
        System.out.println("domain.composite");
        print(domainComposite);
    }

    private static void print(Composite composite) throws XMLStreamException, ContributionWriteException, ParserConfigurationException, SAXException, IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
        xmlProcessor.write(composite, writer);
        
        // Parse and write again to pretty format it
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(2);
        XMLSerializer serializer = new XMLSerializer(System.out, format);
        serializer.serialize(document);
    }
}
