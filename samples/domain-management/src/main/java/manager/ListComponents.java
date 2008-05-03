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

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionContentProcessor;

/**
 * Sample ListComponents task.
 *
 * This sample shows how to use subset of Tuscany to read contribution
 * metadata, analyze and resolve contribution dependencies, read and resolve
 * the artifacts that they contribute (in particular implementation artifacts,
 * interfaces, composites and componentTypes).
 * 
 * The sample first reads the SCA metadata for two sample contributions, then displays
 * their dependencies, reads and resolve the artifacts contained in the contributions,
 * and finally prints the deployables composites and the components they declare as
 * well as their main characteristics (showing that their interfaces and implementations
 * for example are actually resolved).
 *
 * @version $Rev$ $Date$
 */
public class ListComponents {
    
    private static URLArtifactProcessor<Contribution> contributionContentProcessor;
    private static ModelResolverExtensionPoint modelResolvers;
    private static ModelFactoryExtensionPoint modelFactories;
    private static WorkspaceFactory workspaceFactory;
    private static ContributionDependencyBuilder contributionDependencyBuilder;

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
        
        // Get contribution, workspace, assembly and policy model factories
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class); 
        
        // Create XML and document artifact processors
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        URLArtifactProcessor<Object> urlExtensionProcessor = new ExtensibleURLArtifactProcessor(docProcessorExtensions);
        
        // Create contribution content processor
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        contributionContentProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlExtensionProcessor);
        
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
        
        // Create a contribution dependency builder
        contributionDependencyBuilder = new ContributionDependencyBuilderImpl(monitor);
    }
    

    public static void main(String[] args) throws Exception {
        init();

        // Create workspace model
        Workspace workspace = workspaceFactory.createWorkspace();
        workspace.setModelResolver(new ExtensibleModelResolver(workspace, modelResolvers, modelFactories));

        // Read the sample store contribution
        URI storeURI = URI.create("store");
        URL storeURL = new File("./target/sample-domain-management-store.jar").toURI().toURL();
        Contribution storeContribution = (Contribution)contributionContentProcessor.read(null, storeURI, storeURL);
        workspace.getContributions().add(storeContribution);

        // Read the sample assets contribution
        URI assetsURI = URI.create("assets");
        URL assetsURL = new File("./target/sample-domain-management-assets.jar").toURI().toURL();
        Contribution assetsContribution = (Contribution)contributionContentProcessor.read(null, assetsURI, assetsURL);
        workspace.getContributions().add(assetsContribution);

        // Build the store contribution dependencies
        List<Contribution> dependencies = contributionDependencyBuilder.buildContributionDependencies(storeContribution, workspace);
        
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
