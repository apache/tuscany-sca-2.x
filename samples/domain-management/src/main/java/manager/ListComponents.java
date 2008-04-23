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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
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
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.xml.ContributionGeneratedMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataProcessor;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionContentProcessor;

/**
 * Sample ListComponents task
 *
 * @version $Rev$ $Date$
 */
public class ListComponents {
    
    private static URLArtifactProcessor<Contribution> contributionContentProcessor;
    private static ModelResolverExtensionPoint modelResolvers;
    private static ModelFactoryExtensionPoint modelFactories;
    private static WorkspaceFactory workspaceFactory;
    private static Monitor monitor;

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
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        
        // Get contribution, workspace, assembly and policy model factories
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class); 
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
        
        // Create XML and document artifact processors
        StAXArtifactProcessorExtensionPoint xmlProcessorExtensions = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Object> xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessorExtensions, inputFactory, outputFactory);
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        URLArtifactProcessor<Object> urlExtensionProcessor = new ExtensibleURLArtifactProcessor(docProcessorExtensions);
        
        // Create and register XML artifact processor extensions for sca-contribution XML and
        // SCDL <composite>, <componentType> and <constrainingType>
        xmlProcessorExtensions.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory, contributionFactory, xmlProcessor));
        xmlProcessorExtensions.addArtifactProcessor(new CompositeProcessor(contributionFactory, assemblyFactory, policyFactory, xmlProcessor));
        xmlProcessorExtensions.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, xmlProcessor));
        xmlProcessorExtensions.addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, xmlProcessor));
        
        // Create and register document processor extensions for sca-contribution.xml, 
        // sca-contribution-generated.xml, .composite, .componentType and
        // .constrainingType documents 
        docProcessorExtensions.addArtifactProcessor(new ContributionMetadataDocumentProcessor(xmlProcessor, inputFactory));
        docProcessorExtensions.addArtifactProcessor(new ContributionGeneratedMetadataDocumentProcessor(xmlProcessor, inputFactory));
        docProcessorExtensions.addArtifactProcessor(new CompositeDocumentProcessor(xmlProcessor, inputFactory, null));
        docProcessorExtensions.addArtifactProcessor(new ComponentTypeDocumentProcessor(xmlProcessor, inputFactory));
        docProcessorExtensions.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(xmlProcessor, inputFactory));
        
        // Create contribution content processor
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        contributionContentProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlExtensionProcessor);
        
        // Create a monitor
        UtilityExtensionPoint services = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = services.getService(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
    }
    

    public static void main(String[] args) throws Exception {
        init();

        // Create workspace model
        Workspace workspace = workspaceFactory.createWorkspace();
        workspace.setModelResolver(new ExtensibleModelResolver(workspace, modelResolvers, modelFactories));

        // Read the contribution info for the sample contribution
        URI storeURI = URI.create("store");
        URL storeURL = new File("./target/sample-domain-management-store.jar").toURI().toURL();
        Contribution storeContribution = (Contribution)contributionContentProcessor.read(null, storeURI, storeURL);
        workspace.getContributions().add(storeContribution);

        // Read the contribution info for the sample assets contribution
        URI assetsURI = URI.create("assets");
        URL assetsURL = new File("./target/sample-domain-management-assets.jar").toURI().toURL();
        Contribution assetsContribution = (Contribution)contributionContentProcessor.read(null, assetsURI, assetsURL);
        workspace.getContributions().add(assetsContribution);

        // Build the store contribution dependencies
        ContributionDependencyBuilder dependencyBuilder = new ContributionDependencyBuilderImpl(monitor);
        List<Contribution> dependencies = dependencyBuilder.buildContributionDependencies(storeContribution, workspace);
        
        // Resolve the contributions
        for (Contribution contribution: dependencies) {
            contributionContentProcessor.resolve(contribution, workspace.getModelResolver());
        }
        
        // List the components declared in the deployables found in the
        // contribution, their services, bindings, interfaces, and implementations
        for (Composite deployable: storeContribution.getDeployables()) {
            System.out.println("Deployable: " + deployable.getName());
            for (Component component: deployable.getComponents()) {
                System.out.println("  component: " + component.getName());
                for (ComponentService componentService: component.getServices()) {
                    System.out.println("    componentService: " + componentService.getName());
                    for (Binding binding: componentService.getBindings()) {
                        System.out.println("      binding: " + binding.getClass() + " - " + binding.getURI());
                    }
                }
                // Assume Java implementation and interface here as this is what we are
                // using in the sample
                Implementation implementation = component.getImplementation();
                System.out.println("    implementation: " + implementation);
                for (Service service: implementation.getServices()) {
                    System.out.println("      service: " + service.getName());
                    InterfaceContract contract = service.getInterfaceContract();
                    System.out.println("        interface: " + contract.getInterface());
                }
            }
        }
    }

}
