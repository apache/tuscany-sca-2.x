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

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;

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
    
    private static URLArtifactProcessor<Contribution> contributionProcessor;
    private static WorkspaceFactory workspaceFactory;
    private static ContributionDependencyBuilder contributionDependencyBuilder;

    private static void init() throws Exception {
        
        // Create extension point registry 
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        // Get contribution, workspace and assembly model factories
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class); 
        
        // Create contribution info processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = docProcessorExtensions.getProcessor(Contribution.class);

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
        Contribution storeContribution = (Contribution)contributionProcessor.read(null, storeURI, storeURL);
        workspace.getContributions().add(storeContribution);
        
        // Read the contribution info for the assets contribution
        URI assetsURI = URI.create("assets");
        URL assetsURL = new File("./target/sample-domain-management-assets.jar").toURI().toURL();
        Contribution assetsContribution = contributionProcessor.read(null, assetsURI, assetsURL);
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
