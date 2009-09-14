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

package org.apache.tuscany.sca.scdl;

import static org.apache.tuscany.sca.common.java.io.IOHelper.createURI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultImport;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtendedURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultImportModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.definitions.DefinitionsFactory;
import org.apache.tuscany.sca.definitions.util.DefinitionsUtil;
import org.apache.tuscany.sca.definitions.xml.DefinitionsExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;

public class SCDLUtils {

//  private static final String SCA11_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";

    public static Composite readComposite(InputStream is) throws XMLStreamException, ContributionReadException {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        StAXArtifactProcessor<Object> staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null, monitor);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = (Composite)staxProcessor.read(reader);
        
        List<Problem> ps = monitor.getProblems();
        if (ps.size() > 0) {
            throw new ContributionReadException(ps.get(0).toString());
        }
        
        return composite;
    }

    public static Contribution readContribution(String location) throws Exception {
        
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        extensionPoints.start();
        
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        ExtendedURLArtifactProcessor<Contribution> contributionProcessor = (ExtendedURLArtifactProcessor<Contribution>) docProcessorExtensions.getProcessor(Contribution.class);
        
        File f = new File(location);
        List<Contribution> contributions = new ArrayList<Contribution>();
        contributions.add(contributionProcessor.read(null, f.toURI(), f.toURI().toURL()));

        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
//        AssemblyFactory assemblyFactory = new RuntimeAssemblyFactory(extensionPoints);
//        modelFactories.addFactory(assemblyFactory);

        monitor = monitorFactory.createMonitor();

        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);

        contributionProcessor = (ExtendedURLArtifactProcessor<Contribution>) docProcessorExtensions.getProcessor(Contribution.class);

        ModelResolverExtensionPoint modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);

        DefinitionsFactory definitionsFactory = modelFactories.getFactory(DefinitionsFactory.class);
        Definitions systemDefinitions = definitionsFactory.createDefinitions();

        // create a system contribution to hold the definitions. The contribution
        // will be extended later with definitions from application contributions
        Contribution systemContribution = contributionFactory.createContribution();
        systemContribution.setURI("http://tuscany.apache.org/SystemContribution");
        systemContribution.setLocation("http://tuscany.apache.org/SystemContribution");
        ModelResolver modelResolverSys = new ExtensibleModelResolver(systemContribution, modelResolvers, modelFactories, monitor);
        systemContribution.setModelResolver(modelResolverSys);
        systemContribution.setUnresolved(true);

        // create an artifact to represent the system defintions and
        // add it to the contribution
        List<Artifact> systemArtifacts = systemContribution.getArtifacts();
        Artifact definitionsArtifact = contributionFactory.createArtifact();
        definitionsArtifact.setURI("http://tuscany.apache.org/SystemContribution/Definitions");
        definitionsArtifact.setLocation("Derived");
        definitionsArtifact.setModel(systemDefinitions);
        systemArtifacts.add(definitionsArtifact);

        // Build an aggregated SCA definitions model. Must be done before we try and
        // resolve any contributions or composites as they may depend on the full
        // definitions.xml picture

        monitor.pushContext("Extension points definitions");
        DefinitionsExtensionPoint definitionsExtensionPoint = extensionPoints.getExtensionPoint(DefinitionsExtensionPoint.class);
        for(Definitions defs: definitionsExtensionPoint.getDefinitions()) {
            DefinitionsUtil.aggregate(defs, systemDefinitions, monitor);
        }
        monitor.popContext();
        
        // get all definitions.xml artifacts from contributions and aggregate
        // into the system contribution. In turn add a default import into
        // each contribution so that for unresolved items the resolution
        // processing will look in the system contribution
        for (Contribution contribution: contributions) {
            monitor.pushContext("Contribution: " + contribution.getURI());
            // aggregate definitions
            for (Artifact artifact : contribution.getArtifacts()) {
                Object model = artifact.getModel();
                if (model instanceof Definitions) {
                    monitor.pushContext("Definitions: " + artifact.getLocation());
                    DefinitionsUtil.aggregate((Definitions)model, systemDefinitions, monitor);
                    monitor.popContext();
                }
            }

            // create a default import and wire it up to the system contribution
            // model resolver. This is the trick that makes the resolution processing
            // skip over to the system contribution if resolution is unsuccessful
            // in the current contribution
            DefaultImport defaultImport = contributionFactory.createDefaultImport();
            defaultImport.setModelResolver(systemContribution.getModelResolver());
            contribution.getImports().add(defaultImport);
            monitor.popContext();
        }

        ExtensibleModelResolver modelResolver = new ExtensibleModelResolver(new Contributions(contributions), modelResolvers, modelFactories, monitor);

        contributionProcessor.resolve(systemContribution, modelResolver);
        contributions.add(systemContribution);

        // TODO - Now we can calculate applicable policy sets for each composite

        // pre-resolve the contributions
        contributionsPreresolve(contributionProcessor, contributions, modelResolver);

        // Build the contribution dependencies
        Set<Contribution> resolved = new HashSet<Contribution>();
        for (Contribution contribution: contributions) {
            buildDependencies(contribution, contributions, monitor);

            // Resolve contributions
            for (Contribution dependency: contribution.getDependencies()) {
                if (!resolved.contains(dependency)) {
                    resolved.add(dependency);
                    contributionProcessor.resolve(dependency, modelResolver);
                }
            }
        }

//    // Create a top level composite to host our composite
//    // This is temporary to make the activator happy
//        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
////      AssemblyFactory assemblyFactory = new RuntimeAssemblyFactory(extensionPoints);
////      modelFactories.addFactory(assemblyFactory);
//    Composite tempComposite = assemblyFactory.createComposite();
//    tempComposite.setName(new QName(SCA11_TUSCANY_NS, "_tempComposite"));
//    tempComposite.setURI(SCA11_TUSCANY_NS);
//
//    for (Contribution contribution : contributions) {
//        for (Composite composite : contribution.getDeployables()) {
//            // Include the node composite in the top-level composite
//            tempComposite.getIncludes().add(composite);
//        }
//    }
//
//    
//    CompositeActivator compositeActivator = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class).getUtility(CompositeActivator.class);
//
//        // get the top level composite for this node
//        compositeActivator.setDomainComposite(tempComposite);
//
//        // Activate the composite
//        compositeActivator.activate(compositeActivator.getDomainComposite());
//
//        // Start the composite
//        compositeActivator.start(compositeActivator.getDomainComposite());
//    
//    
////    // TODO - EPR - create a binding map to pass down into the builders
////    //              for use during URI calculation.
////    Map<QName, List<String>> bindingMap = new HashMap<QName, List<String>>();
////    for (BindingConfiguration config : configuration.getBindings()) {
////        bindingMap.put(config.getBindingType(), config.getBaseURIs());
////    }
//
//        CompositeBuilderExtensionPoint compositeBuilders = extensionPoints.getExtensionPoint(CompositeBuilderExtensionPoint.class);
//        CompositeBuilder compositeBuilder = compositeBuilders.getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");
//    ((CompositeBuilderTmp)compositeBuilder).build(tempComposite, systemDefinitions, new HashMap<QName, List<String>>(), monitor);
////    analyzeProblems();
//
////    endpointReferenceBuilder.buildtimeBuild(tempComposite);
////    analyzeProblems();
//
////    return tempComposite;
////    Composite xxx = configureNode(extensionPoints, cs, monitor);
    return contributions.get(0);

}
    /**
     * Pre-resolve phase for contributions, to set up handling of imports and exports prior to full resolution
     * @param contributionProcessor 
     * @param contributions - the contributions to preresolve
     * @param resolver - the ModelResolver to use
     * @throws ContributionResolveException
     */
    private static void contributionsPreresolve( ExtendedURLArtifactProcessor<Contribution> contributionProcessor, List<Contribution> contributions, ModelResolver resolver )
        throws ContributionResolveException {

        for( Contribution contribution : contributions ) {
                contributionProcessor.preResolve(contribution, resolver);
        } // end for
    } // end method contributionsPreresolve

    private static void buildDependencies(Contribution contribution, List<Contribution> contributions, Monitor monitor) {
        contribution.getDependencies().clear();

        List<Contribution> dependencies = new ArrayList<Contribution>();
        Set<Contribution> set = new HashSet<Contribution>();

        dependencies.add(contribution);
        set.add(contribution);
        addContributionDependencies(contribution, contributions, dependencies, set, monitor);

        Collections.reverse(dependencies);

        contribution.getDependencies().addAll(dependencies);
    }
    /**
     * Analyze a contribution and add its dependencies to the given dependency set.
     */
    private static void addContributionDependencies(Contribution contribution, List<Contribution> contributions, List<Contribution> dependencies, Set<Contribution> set, Monitor monitor) {

        // Go through the contribution imports
        for (Import import_: contribution.getImports()) {
            boolean resolved = false;

            // Go through all contribution candidates and their exports
            List<Export> matchingExports = new ArrayList<Export>();
            for (Contribution dependency: contributions) {
                if (dependency == contribution) {
                    // Do not self import
                    continue;
                }
                for (Export export: dependency.getExports()) {

                    // If an export from a contribution matches the import in hand
                    // add that contribution to the dependency set
                    if (import_.match(export)) {
                        resolved = true;
                        matchingExports.add(export);

                        if (!set.contains(dependency)) {
                            set.add(dependency);
                            dependencies.add(dependency);

                            // Now add the dependencies of that contribution
                            addContributionDependencies(dependency, contributions, dependencies, set, monitor);
                        } // end if
                    } // end if 
                } // end for
            } // end for

            if (resolved) {
                // Initialize the import's model resolver with a delegating model
                // resolver which will delegate to the matching exports
                import_.setModelResolver(new DefaultImportModelResolver(matchingExports));

            } else {
                // Record import resolution issue
                if (!(import_ instanceof DefaultImport)) {
                        // Add the (empty) matchingExports List and report a warning
                        import_.setModelResolver(new DefaultImportModelResolver(matchingExports));
//                    warning(monitor, "UnresolvedImport", import_, import_);
                }
            } // end if
        }
    }

    
    private static List<Contribution> loadContributions(DefaultExtensionPointRegistry extensionPoints, String s) throws MalformedURLException, ContributionReadException, XMLStreamException, IOException, UnsupportedEncodingException, Exception {
        List<Contribution> contributions = new ArrayList<Contribution>();

        URI contributionURI = createURI(s);

        URI uri = createURI(s);
        if (uri.getScheme() == null) {
            uri = new File(s).toURI();
        }
        URL contributionURL = uri.toURL();

            URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            ExtendedURLArtifactProcessor<Contribution> contributionProcessor = (ExtendedURLArtifactProcessor<Contribution>) docProcessorExtensions.getProcessor(Contribution.class);

            // Load the contribution
            Contribution contribution = contributionProcessor.read(null, contributionURI, contributionURL);
            contributions.add(contribution);

            boolean attached = false;
//            for (DeploymentComposite dc : contrib.getDeploymentComposites()) {
//                if (dc.getContent() != null) {
//                    Reader xml = new StringReader(dc.getContent());
//                    attached = attachDeploymentComposite(extensionPoints, contribution, xml, null, attached);
//                } else if (dc.getLocation() != null) {
//                    URI dcURI = createURI(dc.getLocation());
//                    if (!dcURI.isAbsolute()) {
//                        Composite composite = null;
//                        // The location is pointing to an artifact within the contribution
//                        for (Artifact a : contribution.getArtifacts()) {
//                            if (dcURI.toString().equals(a.getURI())) {
//                                composite = (Composite)a.getModel();
//                                if (!attached) {
//                                    contribution.getDeployables().clear();
//                                    attached = true;
//                                }
//                                contribution.getDeployables().add(composite);
//                                break;
//                            }
//                        }
//                        if (composite == null) {
//                            // Not found
//                            throw new ServiceRuntimeException("Deployment composite " + dcURI
//                                + " cannot be found within contribution "
//                                + contribution.getLocation());
//                        }
//                    } else {
//                        URL url = dcURI.toURL();
//                        InputStream is = openStream(url);
//                        Reader xml = new InputStreamReader(is, "UTF-8");
//                        attached = attachDeploymentComposite(extensionPoints, contribution, xml, url.toString(), attached);
//                    }
//                }
////            analyzeProblems();
//        }
    return contributions;
}

    private boolean attachDeploymentComposite(DefaultExtensionPointRegistry extensionPoints, Contribution contribution, Reader xml, String location, boolean attached) throws XMLStreamException, ContributionReadException {

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(xml);
        reader.nextTag();

        StAXArtifactProcessorExtensionPoint xmlProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Composite> compositeProcessor = xmlProcessors.getProcessor(Composite.class);
        
        // Read the composite model
        Composite composite = (Composite)compositeProcessor.read(reader);
        reader.close();

        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        
        // Create an artifact for the deployment composite
        Artifact artifact = contributionFactory.createArtifact();
        String uri = composite.getName().getLocalPart() + ".composite";
        artifact.setURI(uri);
        // Set the location to avoid NPE
        if (location == null) {
            location = uri;
        }
        artifact.setLocation(location);
        artifact.setModel(composite);
        artifact.setUnresolved(false);
        // Add it to the contribution
        contribution.getArtifacts().add(artifact);

        // Replace the deployable composites with the deployment composites
        // Clear the deployable composites if it's the first deployment composite
        if (!attached) {
            contribution.getDeployables().clear();
            attached = true;
        }
        contribution.getDeployables().add(composite);

        return attached;
    }
}
