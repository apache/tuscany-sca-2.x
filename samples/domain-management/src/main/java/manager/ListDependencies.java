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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.xml.ContributionGeneratedMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataProcessor;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionInfoProcessor;

/**
 * Sample ListDependencies task.
 * 
 * This sample shows how to use a subset of Tuscany to read contribution
 * metadata, analyze and resolve contribution dependencies given a set of
 * available contributions.
 * 
 * The sample reads the SCA metadata for two sample contributions then
 * prints their dependencies.
 *
 * @version $Rev$ $Date$
 */
public class ListDependencies {
    
    private static URLArtifactProcessor<Contribution> contributionInfoProcessor;
    private static WorkspaceFactory workspaceFactory;
    private static ContributionDependencyBuilder contributionDependencyBuilder;

    private static void init() throws Exception {
        
        // Create extension point registry 
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        // Get XML input/output factories
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        
        // Get contribution, workspace and assembly model factories
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class); 
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        
        // Create XML and document artifact processors
        StAXArtifactProcessorExtensionPoint xmlProcessorExtensions = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Object> xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessorExtensions, inputFactory, outputFactory);
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        URLArtifactProcessor<Object> docProcessor = new ExtensibleURLArtifactProcessor(docProcessorExtensions);
        
        // Create and register XML artifact processor extension for sca-contribution XML
        xmlProcessorExtensions.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory, contributionFactory, xmlProcessor));

        // Create and register document processors for sca-contribution.xml and
        // sca-contribution-generated.xml documents
        docProcessorExtensions.addArtifactProcessor(new ContributionMetadataDocumentProcessor(xmlProcessor, inputFactory));
        docProcessorExtensions.addArtifactProcessor(new ContributionGeneratedMetadataDocumentProcessor(xmlProcessor, inputFactory));
        
        // Create contribution info processor
        ModelResolverExtensionPoint modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        contributionInfoProcessor = new ContributionInfoProcessor(modelFactories, modelResolvers, docProcessor);

        // Create a monitor
        UtilityExtensionPoint services = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = services.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
        
        // Create a contribution dependency builder
        contributionDependencyBuilder = new ContributionDependencyBuilderImpl(monitor);
    }
    

    public static void main(String[] args) throws Exception {
        init();
        
        // Create workspace model
        Workspace workspace = workspaceFactory.createWorkspace();

        // Read the contribution info for the sample contribution
        URI storeURI = URI.create("store");
        URL storeURL = new File("./target/sample-domain-management-store.jar").toURI().toURL();
        Contribution storeContribution = (Contribution)contributionInfoProcessor.read(null, storeURI, storeURL);
        workspace.getContributions().add(storeContribution);
        
        // Read the contribution info for the assets contribution
        URI assetsURI = URI.create("assets");
        URL assetsURL = new File("./target/sample-domain-management-assets.jar").toURI().toURL();
        Contribution assetsContribution = (Contribution)contributionInfoProcessor.read(null, assetsURI, assetsURL);
        workspace.getContributions().add(assetsContribution);
        
        // List the contribution dependencies of each contribution
        for (Contribution contribution: workspace.getContributions()) {
            System.out.println("Contribution: " + contribution.getURI());
            for (Contribution dependency: contributionDependencyBuilder.buildContributionDependencies(contribution, workspace)) {
                System.out.println("  dependency: " + dependency.getURI());
            }
        }
    }
    
}
