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

package org.apache.tuscany.sca.deployment.impl;

import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultImport;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ExtendedURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultImportModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.definitions.DefinitionsFactory;
import org.apache.tuscany.sca.definitions.util.DefinitionsUtil;
import org.apache.tuscany.sca.definitions.xml.DefinitionsExtensionPoint;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;

/**
 * 
 */
public class DeployerImpl implements Deployer {
    protected static final Logger logger = Logger.getLogger(DeployerImpl.class.getName());

    protected boolean inited;
    protected boolean schemaValidationEnabled;
    protected StAXHelper staxHelper;
    protected AssemblyFactory assemblyFactory;
    protected CompositeBuilder compositeBuilder;
    protected ContributionFactory contributionFactory;
    protected ExtendedURLArtifactProcessor<Contribution> contributionProcessor;
    protected ExtensionPointRegistry registry;
    protected FactoryExtensionPoint modelFactories;
    protected ModelResolverExtensionPoint modelResolvers;
    protected Contribution systemContribution;
    protected Definitions systemDefinitions;
    protected ExtensibleURLArtifactProcessor artifactProcessor;
    protected ExtensibleStAXArtifactProcessor staxProcessor;

    protected MonitorFactory monitorFactory;

    protected static final String DEPLOYER_IMPL_VALIDATION_MESSAGES =
        "org.apache.tuscany.sca.deployment.impl.deployer-impl-validation-messages";

    /**
     * @param registry
     */
    public DeployerImpl(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }
    
    public Monitor createMonitor() {
        init();
        return monitorFactory.createMonitor();
    }

    public synchronized void stop() {
        if (inited) {
            staxHelper = null;
            assemblyFactory = null;
            compositeBuilder = null;
            contributionFactory = null;
            contributionProcessor = null;
            modelFactories = null;
            modelResolvers = null;
            systemContribution = null;
            systemDefinitions = null;
            artifactProcessor = null;
            staxProcessor = null;
            monitorFactory = null;
            inited = false;
        }
    }

    /**
     * Analyze a contribution and add its dependencies to the given dependency set.
     */
    protected void addContributionDependencies(Contribution contribution,
                                               List<Contribution> contributions,
                                               List<Contribution> dependencies,
                                               Set<Contribution> set,
                                               Monitor monitor) {   

        // Go through the contribution imports
        for (Import import_ : contribution.getImports()) {
            boolean resolved = false;

            // Go through all contribution candidates and their exports
            List<Export> matchingExports = new ArrayList<Export>();
            for (Contribution dependency : contributions) {
                if (dependency == contribution) {
                    // Do not self import
                    continue;
                }
                
                // When a contribution contains a reference to an artifact from a namespace that 
                // is declared in an import statement of the contribution, if the SCA artifact 
                // resolution mechanism is used to resolve the artifact, the SCA runtime MUST resolve 
                // artifacts from the locations identified by the import statement(s) for the namespace.
                if (import_ instanceof NamespaceImport) {
                	NamespaceImport namespaceImport = (NamespaceImport)import_;
                	if (namespaceImport.getLocation() != null)
	                	if (!namespaceImport.getLocation().equals(dependency.getURI())) 
	                		continue;
                }
                
                for (Export export : dependency.getExports()) {

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
                    
                    // push context here as the "stack" in this case is a list of nexted contributions
                    // through which imports have been chased which may not make much sense to the 
                    // user so just report the contribution in error
                    monitor.pushContext("Contribution: " + contribution.getLocation());
                    Monitor.error(monitor, this, DEPLOYER_IMPL_VALIDATION_MESSAGES, "UnresolvedImport", import_);
                    monitor.popContext();
                }
            } // end if
        }
    }

    protected void buildDependencies(Contribution contribution, List<Contribution> contributions, Monitor monitor) {
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
     * Pre-resolve phase for contributions, to set up handling of imports and exports prior to full resolution
     * @param contributions - the contributions to preresolve
     * @param resolver - the ModelResolver to use
     * @throws ContributionResolveException
     */
    protected void contributionsPreresolve(List<Contribution> contributions,
                                           ModelResolver resolver,
                                           ProcessorContext context) throws ContributionResolveException {

        for (Contribution contribution : contributions) {
            contributionProcessor.preResolve(contribution, resolver, context);
        } // end for
    } // end method contributionsPreresolve

    public ExtensionPointRegistry getExtensionPointRegistry() {
        return registry;
    }

    public void start() {
        // Defer to the init() method
    }
    
    public synchronized void init() {
        if (inited) {
            return;
        }

        // Enable schema validation only of the logger level is FINE or higher
        if (isSchemaValidationEnabled()) {
            ValidationSchemaExtensionPoint schemas = registry.getExtensionPoint(ValidationSchemaExtensionPoint.class);
            if (schemas != null) {
                schemas.setEnabled(true);
            }
        }

        // Use the runtime-enabled assembly factory
        modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);

        // Create a monitor
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        staxHelper = utilities.getUtility(StAXHelper.class);

        monitorFactory = utilities.getUtility(MonitorFactory.class);

        // Initialize the Tuscany module activators
        // The module activators will be started
        registry.getExtensionPoint(ModuleActivatorExtensionPoint.class);

        // Get contribution workspace and assembly model factories
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);

        // Create XML artifact processors
        staxProcessor = new ExtensibleStAXArtifactProcessor(registry);

        // Create contribution content processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions =
            registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        artifactProcessor = new ExtensibleURLArtifactProcessor(docProcessorExtensions);

        contributionProcessor =
            (ExtendedURLArtifactProcessor<Contribution>)docProcessorExtensions.getProcessor(Contribution.class);

        // Get the model resolvers
        modelResolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);

        // Get composite builders
        BuilderExtensionPoint compositeBuilders = registry.getExtensionPoint(BuilderExtensionPoint.class);
        compositeBuilder =
            compositeBuilders.getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");

        loadSystemContribution(monitorFactory.createMonitor());

        inited = true;

    }

    protected void loadSystemContribution(Monitor monitor) {
        DefinitionsFactory definitionsFactory = modelFactories.getFactory(DefinitionsFactory.class);
        systemDefinitions = definitionsFactory.createDefinitions();

        DefinitionsExtensionPoint definitionsExtensionPoint =
            registry.getExtensionPoint(DefinitionsExtensionPoint.class);
        monitor.pushContext("Extension points definitions");
        try {
            for (Definitions defs : definitionsExtensionPoint.getDefinitions()) {
                DefinitionsUtil.aggregate(defs, systemDefinitions, monitor);
            }
        } finally {
            monitor.popContext();
        }

        // create a system contribution to hold the definitions. The contribution
        // will be extended later with definitions from application contributions
        systemContribution = contributionFactory.createContribution();
        systemContribution.setURI("http://tuscany.apache.org/SystemContribution");
        systemContribution.setLocation("http://tuscany.apache.org/SystemContribution");
        ModelResolver modelResolver = new ExtensibleModelResolver(systemContribution, modelResolvers, modelFactories);
        systemContribution.setModelResolver(modelResolver);
        systemContribution.setUnresolved(true);

        // create an artifact to represent the system defintions and
        // add it to the contribution
        List<Artifact> artifacts = systemContribution.getArtifacts();
        Artifact artifact = contributionFactory.createArtifact();
        artifact.setURI("http://tuscany.apache.org/SystemContribution/Definitions");
        artifact.setLocation("Derived");
        artifact.setModel(systemDefinitions);
        artifacts.add(artifact);
    }

    protected Contribution cloneSystemContribution(Monitor monitor) {
        init();
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(systemContribution.getURI());
        contribution.setLocation(systemContribution.getLocation());
        ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
        contribution.setModelResolver(modelResolver);
        contribution.setUnresolved(true);

        DefinitionsFactory definitionsFactory = modelFactories.getFactory(DefinitionsFactory.class);
        Definitions definitions = definitionsFactory.createDefinitions();
        DefinitionsUtil.aggregate(systemDefinitions, definitions, monitor);
        // create an artifact to represent the system defintions and
        // add it to the contribution
        List<Artifact> artifacts = contribution.getArtifacts();
        Artifact artifact = contributionFactory.createArtifact();
        artifact.setURI("http://tuscany.apache.org/SystemContribution/Definitions");
        artifact.setLocation("Derived");
        artifact.setModel(definitions);
        artifacts.add(artifact);
        return contribution;
    }

    public void attachDeploymentComposite(Contribution contribution, Composite composite, boolean appending) {
        init();
        // Create an artifact for the deployment composite
        Artifact artifact = contributionFactory.createArtifact();
        String uri = composite.getName().getLocalPart() + ".composite";
        artifact.setURI(uri);

        artifact.setLocation(uri);
        artifact.setModel(composite);
        artifact.setUnresolved(false);
        // Add it to the contribution
        contribution.getArtifacts().add(artifact);

        // Replace the deployable composites with the deployment composites
        // Clear the deployable composites if it's the first deployment composite
        if (!appending) {
            contribution.getDeployables().clear();
        }
        contribution.getDeployables().add(composite);
    }

    public Composite build(List<Contribution> contributions, Map<QName, List<String>> bindingMap, Monitor monitor)
        throws ContributionResolveException, CompositeBuilderException {
        init();
        List<Contribution> contributionList = new ArrayList<Contribution>(contributions);
        
        Contribution systemContribution = cloneSystemContribution(monitor);
        Definitions systemDefinitions = systemContribution.getArtifacts().get(0).getModel();
        // Build an aggregated SCA definitions model. Must be done before we try and
        // resolve any contributions or composites as they may depend on the full
        // definitions.xml picture

        // get all definitions.xml artifacts from contributions and aggregate
        // into the system contribution. In turn add a default import into
        // each contribution so that, for unresolved items, the resolution
        // processing will look in the system contribution
        ProcessorContext context = new ProcessorContext(monitor);
        for (Contribution contribution : contributionList) {
            monitor.pushContext("Contribution: " + contribution.getURI());
            try {
                // aggregate definitions
                for (Artifact artifact : contribution.getArtifacts()) {
                    if (!"META-INF/definitions.xml".equals(artifact.getURI())) {
                        continue;
                    }
                    Object model = artifact.getModel();
                    // FIXME: Should we check the artifact URI is META-INF/definitions.xml?
                    if (model instanceof Definitions) {
                        try {
                            monitor.pushContext("Definitions: " + artifact.getLocation());
                            DefinitionsUtil.aggregate((Definitions)model, systemDefinitions, monitor);
                        } finally {
                            monitor.popContext();
                        }                            
                    }
                }

                // create a default import and wire it up to the system contribution
                // model resolver. This is the trick that makes the resolution processing
                // skip over to the system contribution if resolution is unsuccessful
                // in the current contribution
                DefaultImport defaultImport = contributionFactory.createDefaultImport();
                defaultImport.setModelResolver(systemContribution.getModelResolver());
                contribution.getImports().add(defaultImport);
            } finally {
                monitor.popContext();
            }
        }

        ExtensibleModelResolver modelResolver =
            new ExtensibleModelResolver(new Contributions(contributionList), modelResolvers, modelFactories);

        // now resolve and add the system contribution
        contributionProcessor.resolve(systemContribution, modelResolver, context);
        contributionList.add(systemContribution);

        // pre-resolve the contributions
        contributionsPreresolve(contributionList, modelResolver, context);

        // Build the contribution dependencies
        Set<Contribution> resolved = new HashSet<Contribution>();
        for (Contribution contribution : contributionList) {
            buildDependencies(contribution, contributionList, monitor);

            // Resolve contributions
            for (Contribution dependency : contribution.getDependencies()) {
                if (!resolved.contains(dependency)) {
                    resolved.add(dependency);
                    contributionProcessor.resolve(dependency, modelResolver, context);
                }
            }
        }

        // Create a top level composite to host our composite
        // This is temporary to make the activator happy
        Composite domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(Composite.DOMAIN_COMPOSITE);
        domainComposite.setURI(Base.SCA11_NS);

        for (Contribution contribution : contributionList) {
            for (Composite composite : contribution.getDeployables()) {
                // Include the node composite in the top-level composite
                domainComposite.getIncludes().add(composite);
            }
        }

        // build the top level composite
        BuilderContext builderContext = new BuilderContext(systemDefinitions, bindingMap, monitor);
        compositeBuilder.build(domainComposite, builderContext);
        // analyzeProblems(monitor);

        return domainComposite;
    }

    public Artifact loadArtifact(URI uri, URL location, Monitor monitor) throws ContributionReadException {
        init();
        Artifact artifact = contributionFactory.createArtifact();
        artifact.setLocation(location.toString());
        artifact.setURI(uri.toString());
        URLArtifactProcessorExtensionPoint artifactProcessors =
            registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        ExtensibleURLArtifactProcessor processor = new ExtensibleURLArtifactProcessor(artifactProcessors);
        Object model = processor.read(null, uri, location, new ProcessorContext(monitor));
        artifact.setModel(model);
        return artifact;
    }

    @SuppressWarnings("unchecked")
    public <T> T loadDocument(URI uri, URL location, Monitor monitor) throws ContributionReadException {
        init();
        Object model = artifactProcessor.read(null, uri, location, new ProcessorContext(monitor));
        return (T)model;
    }

    public <T> T loadXMLDocument(URL document, Monitor monitor) throws XMLStreamException, ContributionReadException {
        init();
        XMLStreamReader reader = staxHelper.createXMLStreamReader(document);
        reader.nextTag();
        ValidatingXMLInputFactory.setMonitor(reader, monitor);
        try {
            return loadXMLElement(reader, monitor);
        } finally {
            reader.close();
        }
    }

    public void saveXMLDocument(Object model, Writer writer, Monitor monitor) throws XMLStreamException,
        ContributionWriteException {
        init();
        XMLStreamWriter streamWriter = staxHelper.createXMLStreamWriter(writer);
        staxProcessor.write(model, streamWriter, new ProcessorContext(monitor));
    }

    public void saveXMLElement(Object model, XMLStreamWriter writer, Monitor monitor) throws XMLStreamException,
        ContributionWriteException {
        init();
        staxProcessor.write(model, writer, new ProcessorContext(monitor));
    }

    @SuppressWarnings("unchecked")
    public <T> T loadXMLElement(XMLStreamReader reader, Monitor monitor) throws ContributionReadException,
        XMLStreamException {
        init();
        return (T)staxProcessor.read(reader, new ProcessorContext(monitor));
    }

    public <T> T loadXMLDocument(Reader document, Monitor monitor) throws XMLStreamException, ContributionReadException {
        init();
        XMLStreamReader reader = staxHelper.createXMLStreamReader(document);
        ValidatingXMLInputFactory.setMonitor(reader, monitor);
        reader.nextTag();
        try {
            return loadXMLElement(reader, monitor);
        } finally {
            reader.close();
        }
    }

    public Contribution loadContribution(URI uri, URL location, Monitor monitor) throws ContributionReadException {
        init();
        ProcessorContext context = new ProcessorContext(monitor);
        // Load the contribution
        Contribution contribution = contributionProcessor.read(null, uri, location, context);
        return contribution;
    }

    public ProcessorContext createProcessorContext() {
        init();
        return new ProcessorContext(monitorFactory.createMonitor());
    }

    public BuilderContext createBuilderContext() {
        init();
        return new BuilderContext(monitorFactory.createMonitor());
    }

    public boolean isSchemaValidationEnabled() {
        return schemaValidationEnabled;
    }

    public void setSchemaValidationEnabled(boolean schemaValidationEnabled) {
        this.schemaValidationEnabled = schemaValidationEnabled;
    }

}
