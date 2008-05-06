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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.binding.atom.AtomBindingFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.implementation.node.builder.impl.NodeCompositeBuilderImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.launcher.NodeLauncher;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Sample RunComponents task
 *
 * Under construction... This sample is similar to the DistributeComponents sample,
 * with extra steps to configure SCA runtime nodes with the models, start and stop
 * them.
 *
 * @version $Rev$ $Date$
 */
public class DistributeAndRunComponents {
    
    private static URLArtifactProcessor<Contribution> contributionProcessor;
    private static ModelResolverExtensionPoint modelResolvers;
    private static ModelFactoryExtensionPoint modelFactories;
    private static WorkspaceFactory workspaceFactory;
    private static AssemblyFactory assemblyFactory;
    private static XMLOutputFactory outputFactory;
    private static StAXArtifactProcessor<Object> xmlProcessor; 
    private static ContributionDependencyBuilder contributionDependencyBuilder;
    private static CompositeBuilder domainCompositeBuilder;
    private static CompositeBuilder nodeCompositeBuilder;
    private static NodeImplementationFactory nodeFactory;
    private static AtomBindingFactory atomBindingFactory;

    private static void init() {
        
        // Create extension point registry 
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        // Initialize the Tuscany module activators
        ModuleActivatorExtensionPoint moduleActivators = extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);
        for (ModuleActivator activator: moduleActivators.getModuleActivators()) {
            activator.start(extensionPoints);
        }

        // Get XML input/output factories
        modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        
        // Get contribution workspace and assembly model factories
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class); 
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        nodeFactory = modelFactories.getFactory(NodeImplementationFactory.class);
        atomBindingFactory = modelFactories.getFactory(AtomBindingFactory.class);
        
        // Create XML artifact processors
        StAXArtifactProcessorExtensionPoint xmlProcessorExtensions = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessorExtensions, inputFactory, outputFactory);
        
        // Create contribution content processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = docProcessorExtensions.getProcessor(Contribution.class);
        
        // Get the model resolvers
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
        
        // Create a contribution dependency builder
        contributionDependencyBuilder = new ContributionDependencyBuilderImpl(monitor);
        
        // Create a composite builder
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory attachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        InterfaceContractMapper contractMapper = utilities.getUtility(InterfaceContractMapper.class);
        domainCompositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, attachPointTypeFactory, contractMapper, monitor);
        
        // Create a node composite builder
        nodeCompositeBuilder = new NodeCompositeBuilderImpl(assemblyFactory, scaBindingFactory, contractMapper, null, monitor);
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
        Map<Contribution, List<Contribution>> contributionDependencies = new HashMap<Contribution, List<Contribution>>();
        Set<Contribution> resolved = new HashSet<Contribution>();
        for (Contribution contribution: workspace.getContributions()) {
            List<Contribution> dependencies = contributionDependencyBuilder.buildContributionDependencies(contribution, workspace);
            
            // Resolve contributions
            for (Contribution dependency: dependencies) {
                if (!resolved.contains(dependency)) {
                    resolved.add(dependency);
                    contributionProcessor.resolve(dependency, workspace.getModelResolver());
                }
            }
            
            contributionDependencies.put(contribution, dependencies);
        }
        
        // Create a set of nodes, and assign the sample deployables to them
        Map<Component, List<Contribution>> nodeDependencies = new HashMap<Component, List<Contribution>>();
        Composite cloudComposite = assemblyFactory.createComposite();
        cloudComposite.setName(new QName("http://sample", "cloud"));
        int nodeID = 8100;
        for (Contribution contribution: workspace.getContributions()) {
            for (Composite deployable: contribution.getDeployables()) {
                
                // Create a node
                Component node = assemblyFactory.createComponent();
                node.setName("Node" + nodeID);
                cloudComposite.getComponents().add(node);
                
                // Add default binding configuration to the node, our samples use
                // Atom bindings so here we're just creating default Atom binding
                // configurations, but all the other binding types can be configured
                // like that too
                ComponentService nodeService = assemblyFactory.createComponentService();
                Binding binding = atomBindingFactory.createAtomBinding();
                binding.setURI("http://localhost:" + (8100 + nodeID));
                nodeService.getBindings().add(binding);
                node.getServices().add(nodeService);

                // Assign a deployable to the node
                NodeImplementation nodeImplementation = nodeFactory.createNodeImplementation();
                nodeImplementation.setComposite(deployable);
                node.setImplementation(nodeImplementation);
                
                // Keep track of what contributions will be needed by the node
                nodeDependencies.put(node, contributionDependencies.get(contribution));
                
                nodeID++;
            }
        }
        
        // Print the model describing the nodes that we just built
        System.out.println("cloud.composite");
        System.out.println(print(cloudComposite));
        
        // Build the nodes, this will apply their default binding configuration to the
        // composites assigned to them
        nodeCompositeBuilder.build(cloudComposite);
        
        // Create a composite model for the domain
        Composite domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName("http://sample", "domain"));
        
        // Add all deployables to it, normally the domain administrator would select
        // the deployables to include
        domainComposite.getIncludes().addAll(workspace.getDeployables());
        
        // Build the domain composite and wire the components included in it
        domainCompositeBuilder.build(domainComposite);

        // Print out the resulting domain composite
        System.out.println("domain.composite");
        System.out.println(print(domainComposite));
        
        // Now start our SCA nodes
        List<SCANode2> runtimeNodes = new ArrayList<SCANode2>();
        NodeLauncher launcher = NodeLauncher.newInstance();
        for (Component node: cloudComposite.getComponents()) {
            
            // Create a composite containing the components that we want to run
            // on the node
            Composite runnable = assemblyFactory.createComposite();
            runnable.setName(new QName("http://sample", node.getName()));
            NodeImplementation nodeImplementation = (NodeImplementation)node.getImplementation();
            for (Component component: nodeImplementation.getComposite().getComponents()) {
                for (Component configured: domainComposite.getComponents()) {
                    if (configured.getName().equals(component.getName())) {
                        runnable.getComponents().add(configured);
                        break;
                    }
                }
            }

            // Create the SCA node, give it the composite and the list of contributions
            // to use
            List<Contribution> dependencies = nodeDependencies.get(node);
            org.apache.tuscany.sca.node.launcher.NodeLauncher.Contribution[] contributions = new org.apache.tuscany.sca.node.launcher.NodeLauncher.Contribution[dependencies.size()];
            for (int c =0, n = dependencies.size(); c < n; c++) {
                Contribution dependency = dependencies.get(c);
                contributions[c] = new org.apache.tuscany.sca.node.launcher.NodeLauncher.Contribution(dependency.getURI(), dependency.getLocation());
            }
            SCANode2 runtimeNode = launcher.createNode("http://sample/" + node.getName(), print(runnable), contributions);
            
            // Start the node
            runtimeNode.start();
            runtimeNodes.add(runtimeNode);
        }
        
        System.out.println("Nodes are running, press enter to stop...");
        System.in.read();
        
        for (SCANode2 runtimeNode: runtimeNodes) {
            runtimeNode.stop();
        }
    }

    private static String print(Composite composite) throws XMLStreamException, ContributionWriteException, ParserConfigurationException, SAXException, IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
        xmlProcessor.write(composite, writer);
        
        // Parse and write again to pretty format it
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(2);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(document);
        return out.toString();
    }
}
