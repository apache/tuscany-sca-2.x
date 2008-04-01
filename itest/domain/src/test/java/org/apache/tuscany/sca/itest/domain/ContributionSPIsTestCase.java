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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem;
import org.apache.tuscany.sca.assembly.builder.Problem.Severity;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeConfigurationBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.Constants;
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
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.xml.ContributionGeneratedMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataProcessor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.SCANode2Factory;
import org.apache.tuscany.sca.node.SCANode2Factory.SCAContribution;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilderMonitor;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionContentProcessor;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionInfoProcessor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import calculator.CalculatorService;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class ContributionSPIsTestCase {
    
     final static Logger logger = Logger.getLogger(ContributionSPIsTestCase.class.getName());
    
    static ModelFactoryExtensionPoint modelFactories;
    static ContributionFactory contributionFactory;
    static AssemblyFactory assemblyFactory;
    static WorkspaceFactory workspaceFactory;  
    static PolicyFactory policyFactory;
    static XMLInputFactory inputFactory;
    static XMLOutputFactory outputFactory;    
    
    static ModelResolverExtensionPoint modelResolvers;
    
    static StAXArtifactProcessorExtensionPoint staxProcessors;
    static StAXArtifactProcessor<Object> staxProcessor;
    static URLArtifactProcessorExtensionPoint urlProcessors;
    static URLArtifactProcessor<Object> urlProcessor;
    static URLArtifactProcessor<Contribution> contributionInfoProcessor;
    static URLArtifactProcessor<Contribution> contributionContentProcessor;
    static StAXArtifactProcessor<Composite> compositeProcessor;
    
    static Workspace workspace;
    
    static List<String> problems = new ArrayList<String>();
    static ContributionDependencyBuilderMonitor dependencyBuilderMonitor;
    static ContributionDependencyBuilderImpl analyzer;
    static List<ContributionListener> contributionListeners;
    
    static CompositeBuilder compositeBuilder;
    static CompositeConfigurationBuilderImpl compositeConfigurationBuilder;
    
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
            contributionFactory = modelFactories.getFactory(ContributionFactory.class);
            assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
            inputFactory = modelFactories.getFactory(XMLInputFactory.class);
            outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
            outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
            contributionFactory = modelFactories.getFactory(ContributionFactory.class);
            policyFactory = modelFactories.getFactory(PolicyFactory.class); 
            workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class);
            
            // Create model resolvers
            modelResolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);

            // Create artifact processors
            staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
            staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory);
            staxProcessors.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory, contributionFactory, staxProcessor));
            compositeProcessor = (StAXArtifactProcessor<Composite>)staxProcessors.getProcessor(Composite.class);
           
            urlProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            urlProcessor = new ExtensibleURLArtifactProcessor(urlProcessors);
            urlProcessors.addArtifactProcessor(new ContributionMetadataDocumentProcessor(staxProcessor, inputFactory));
            urlProcessors.addArtifactProcessor(new ContributionGeneratedMetadataDocumentProcessor(staxProcessor, inputFactory));
            urlProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory, null));
                 
            // Create contribution processor
            contributionInfoProcessor = new ContributionInfoProcessor(modelFactories, modelResolvers, urlProcessor);
            contributionContentProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlProcessor);
            contributionListeners = registry.getExtensionPoint(ContributionListenerExtensionPoint.class).getContributionListeners();            
            
            // Create workspace model to hold contribution information
            workspace = workspaceFactory.createWorkspace();
            
            // create a dependency builder 
            dependencyBuilderMonitor = new ContributionDependencyBuilderMonitor() {
                    public void problem(Problem problem) {
                        problems.add(problem.getMessage() + " " + problem.getModel());
                    }
                };
                
            analyzer = new ContributionDependencyBuilderImpl(dependencyBuilderMonitor);  
            
            // Create composite builder
            SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
            IntentAttachPointTypeFactory intentAttachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
            InterfaceContractMapper contractMapper = new InterfaceContractMapperImpl();
            
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
            
            compositeBuilder = new CompositeBuilderImpl(assemblyFactory, 
                                                        scaBindingFactory, 
                                                        intentAttachPointTypeFactory,
                                                        contractMapper, 
                                                        monitor);
            
            compositeConfigurationBuilder = new CompositeConfigurationBuilderImpl(assemblyFactory, 
                                                                                  scaBindingFactory, 
                                                                                  intentAttachPointTypeFactory,
                                                                                  contractMapper,
                                                                                  monitor);            
            

        } catch(Exception ex){
            ex.printStackTrace();
        } 
        
   }

    @AfterClass
    public static void destroy() throws Exception {
       
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
            Contribution contribution = (Contribution)contributionInfoProcessor.read(null,uri, url);
            workspace.getContributions().add(contribution);
            System.out.println("Added contributionPrimary");
            
            // Load another contribution  
            uri = URI.create("contributionDependent");
            file = new File("./src/main/resources/contributionDependent");
            url = file.toURI().toURL();      
            contribution = (Contribution)contributionInfoProcessor.read(null,uri, url);        
            workspace.getContributions().add(contribution);
            System.out.println("Added contributionDependent");
           
            // List contribution dependencies for the first contribution 
            // first contribution chosen to represent the user selecting a composite
            /*
            List<Contribution> dependencies = analyzer.buildContributionDependencies(workspace, workspace.getContributions().get(0));
            for (Contribution dependency : dependencies){
                System.out.println("contributionPrimary dependency chain = " + dependency.getURI());
            }
            */
    
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
                        contributionsToDeploy = analyzer.buildContributionDependencies(workspace, tmpContribution);
                    }
                }
            }
            
            // load all the contributions in the dependency chain to find the chosen 
            // composite
            List<Contribution> loadedContributions = new ArrayList<Contribution>();
            Composite deployable = null;
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
            
            SCANode2Factory nodeFactory = SCANode2Factory.newInstance();
            SCAContribution contribution0 = new SCAContribution(contributionsToDeploy.get(0).getURI(), contributionsToDeploy.get(0).getLocation());
            SCAContribution contribution1 = new SCAContribution(contributionsToDeploy.get(1).getURI(), contributionsToDeploy.get(1).getLocation());
            
            // FIXME - need a more flexible constructor on the node so we can pass in a 
            //         dynamic list of contributions
            SCANode2 node = nodeFactory.createSCANode(chosenDeployableLocation, contribution0, contribution1);
            
            node.start();
            SCAClient client = (SCAClient)node;
            CalculatorService calculatorService = 
                client.getService(CalculatorService.class, "CalculatorServiceComponentA");
            
            System.out.println("Add 2.0 + 3.0 = " + calculatorService.add(2.0, 3.0));
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }             
    }
    
    
    /**
     * FIXME Remove this later
     * Waiting for more tidying of contribution processing. At the moment we have to 
     * set up a dummy contribution repository to make it work
     */    
    private Contribution contribution(List<Contribution> contributions, String contributionURI, String contributionLocation) throws ContributionReadException {
        try {
            URI uri = URI.create(contributionURI);
            URL location = locationURL(contributionLocation);
            Contribution contribution = (Contribution)contributionContentProcessor.read(null, uri, location);
            
            // FIXME simplify this later
            // Fix up contribution imports
            ContributionRepository dummyRepository = new DummyContributionRepository(contributions);
            for (ContributionListener listener: contributionListeners) {
                listener.contributionAdded(dummyRepository, contribution);
            }
            
            ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
            contributionContentProcessor.resolve(contribution, modelResolver);
            
            return contribution;

        } catch (ContributionReadException e) {
            throw e;
        } catch (ContributionResolveException e) {
            throw new ContributionReadException(e);
        } catch (MalformedURLException e) {
            throw new ContributionReadException(e);
        }
    }
    
    static URL locationURL(String location) throws MalformedURLException {
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
    
    /**
     * FIXME Remove this later
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
