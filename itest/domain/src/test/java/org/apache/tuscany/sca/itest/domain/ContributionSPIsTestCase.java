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

package org.apache.tuscany.sca.itest.domain;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import calculator.CalculatorService;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class ContributionSPIsTestCase {
    
     final static Logger logger = Logger.getLogger(ContributionSPIsTestCase.class.getName());
    
    private static ModelFactoryExtensionPoint modelFactories;
    private static WorkspaceFactory workspaceFactory;  
    private static XMLOutputFactory outputFactory;    
    
    private static ModelResolverExtensionPoint modelResolvers;
    
    private static URLArtifactProcessorExtensionPoint urlProcessors;
    private static URLArtifactProcessor<Contribution> contributionProcessor;
    
    private static Workspace workspace;
    
    private static List<String> problems = new ArrayList<String>();
    private static ContributionDependencyBuilder dependencyBuilder;
    
    @BeforeClass
    public static void init() throws Exception {

        try {
            // Bootstrap a runtime to get a populated registry
            // FIXME needs to be tidied so we can get the registry without all of the other configuration
            //       that is being repeated below
            ReallySmallRuntime runtime = new ReallySmallRuntime(Thread.currentThread().getContextClassLoader());
            runtime.start();
            ExtensionPointRegistry registry = runtime.getExtensionPointRegistry();
            
            // Create model factories
            modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
            outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
            outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
            workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class);
            
            // Create model resolvers
            modelResolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);

            // Create artifact processors
            urlProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
                 
            // Create contribution processor
            contributionProcessor = urlProcessors.getProcessor(Contribution.class);
            
            // Create workspace model to hold contribution information
            workspace = workspaceFactory.createWorkspace();
            
            MonitorFactory monitorFactory = registry.getExtensionPoint(MonitorFactory.class);
            Monitor monitor = monitorFactory.createMonitor();
            dependencyBuilder = new ContributionDependencyBuilderImpl(monitor);  
            
        } catch(Exception ex){
            ex.printStackTrace();
        } 
        
   }

    @Test
    public void testReadDependentContributions() throws Exception { 
        try {            
            // ====================================================================
            // The contribution management phase. I.e. where a use is adding contributions
            // prior to selecting a composite to run
            
            // Load a contribution
            // Note that this contribution is added before the contribution that it depends on
            // as the contribution processing doesn't start until both have been added
            URI uri = URI.create("contributionPrimary");
            File file = new File("./src/main/resources/contributionPrimary");
            URL url = file.toURI().toURL();
            Contribution contribution = (Contribution)contributionProcessor.read(null,uri, url);
            workspace.getContributions().add(contribution);
            System.out.println("Added contributionPrimary");
            
            // Load another contribution  
            uri = URI.create("contributionDependent");
            file = new File("./src/main/resources/contributionDependent");
            url = file.toURI().toURL();      
            contribution = (Contribution)contributionProcessor.read(null,uri, url);        
            workspace.getContributions().add(contribution);
            System.out.println("Added contributionDependent");
              
            // Choose a deployables as though a user had chosen it
            List<Composite> deployables = workspace.getContributions().get(0).getDeployables();
            QName chosenDeployableName = deployables.get(0).getName();
            System.out.println("Composite chosen to deploy = " + chosenDeployableName);

                  
            // List the dependency problems
            for (int i = 0, n = problems.size(); i < n ; i++) {
                System.out.println("Problem: "+ problems.get(i));
            }
            
            // ====================================================================
            // process the first chosen composite ready for a node to run the composite
             
            // find the contribution that holds our chosen composite and all its dependencies 
            // we are using the first deployable composite from the first contribution 
            // so we really know this here really but lets find it anyway
            List<Contribution> contributionsToDeploy = null;
            String chosenDeployableLocation = null;
            for (Contribution tmpContribution : workspace.getContributions()){
                for (Composite deployable : tmpContribution.getDeployables()){
                    if (deployable.getName().equals(chosenDeployableName)){
                        contributionsToDeploy = dependencyBuilder.buildContributionDependencies(tmpContribution, workspace);
                    }
                }
            }
            
            // load all the contributions in the dependency chain to find the chosen 
            // composite
            List<Contribution> loadedContributions = new ArrayList<Contribution>();
            for (Contribution tmpContribution : contributionsToDeploy){
                Contribution loadedContribution = contribution(loadedContributions, tmpContribution.getURI(), tmpContribution.getLocation());
                loadedContributions.add(loadedContribution);
                
                // find the chosen composite artifact location
                for ( Artifact artifact :loadedContribution.getArtifacts()){
                    if ( artifact.getURI().endsWith(".composite")){
                        Composite model = (Composite)artifact.getModel();
                        if (model.getName().equals(chosenDeployableName)){
                            chosenDeployableLocation = artifact.getLocation();
                        }
                    }
                }                
            }            
            
            System.out.println("Composite chosen to deploy location = " + chosenDeployableLocation);
            for (Contribution dependency : contributionsToDeploy){
                System.out.println("Composite chosen to deploy dependency chain = " + dependency.getURI());
            }
            
/* At this point if there is more than one composite in the domain
 * we would build the domain to configure all the endpoint URIs
 * and then pass the individual composites off to the separate
 * nodes that are going to run the
 * TODO - I've skipped this part for clarity at the moment
 *             
            // create a domain level composite
            Composite domainComposite = assemblyFactory.createComposite();
            domainComposite.setName(new QName(Constants.SCA10_TUSCANY_NS, "domain"));
            
            // etc.
 */
           
          
            
            // ====================================================================
            // run the chosen composite   
            SCAContribution [] contributions = new SCAContribution[contributionsToDeploy.size()];
            for (int i = 0; i < contributionsToDeploy.size(); i++) {
                contributions[i] = new SCAContribution(contributionsToDeploy.get(i).getURI(), contributionsToDeploy.get(i).getLocation()); 
            }
                   
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            
            SCANode node = nodeFactory.createSCANode(chosenDeployableLocation, contributions);
            
            node.start();
            SCAClient client = (SCAClient)node;
            CalculatorService calculatorService = 
                client.getService(CalculatorService.class, "CalculatorServiceComponentA");
            
            System.out.println("Add 2.0 + 3.0 + 3.0 = " + calculatorService.add(2.0, 3.0));

/*
            AssemblyInspector assemblyInspector = new AssemblyInspector();
            System.out.println(assemblyInspector.assemblyAsString(node));
*/
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }             
    }
    
    
    private Contribution contribution(List<Contribution> contributions, String contributionURI, String contributionLocation) throws ContributionReadException {
        try {
            URI uri = URI.create(contributionURI);
            URL location = locationURL(contributionLocation);
            Contribution contribution = (Contribution)contributionProcessor.read(null, uri, location);
            
            ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
            contributionProcessor.resolve(contribution, modelResolver);
            
            return contribution;

        } catch (ContributionReadException e) {
            throw e;
        } catch (ContributionResolveException e) {
            throw new ContributionReadException(e);
        } catch (MalformedURLException e) {
            throw new ContributionReadException(e);
        }
    }
    
    private static URL locationURL(String location) throws MalformedURLException {
        URI uri = URI.create(location);
        String scheme = uri.getScheme();
        if (scheme == null) {
            File file = new File(location);
            return file.toURI().toURL();
        } else if (scheme.equals("file")) {
            File file = new File(location.substring(5));
            return file.toURI().toURL();
        } else {
            return uri.toURL();
        }
    }
    
}
