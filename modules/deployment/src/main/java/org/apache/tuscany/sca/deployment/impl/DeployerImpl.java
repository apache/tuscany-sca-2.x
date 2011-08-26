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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.xsd.Constants;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.DefaultImport;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaImport;
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
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.BaseDomainRegistry;
import org.apache.tuscany.sca.runtime.ContributionDescription;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.EndpointReferenceBinder;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;

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
    protected ValidationSchemaExtensionPoint validationSchema;
    protected EndpointReferenceBinder endpointReferenceBinder;

    protected MonitorFactory monitorFactory;

    protected static final String DEPLOYER_IMPL_VALIDATION_MESSAGES =
        "org.apache.tuscany.sca.deployment.impl.deployer-impl-validation-messages";
    
    public static final QName XSD = new QName("http://www.w3.org/2001/XMLSchema", "schema");

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
                if (import_ instanceof JavaImport) {
                	JavaImport javaImport = (JavaImport)import_;
                	if (javaImport.getLocation() != null)
	                	if (!javaImport.getLocation().equals(dependency.getURI())) 
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
                    monitor.pushContext("Contribution: " + contribution.getURI());
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
    
    public List<String> getDependencies(Map<String, ContributionMetadata> possibles, String targetURI, Monitor monitor) {   
        Map<String, Contribution> contributions = new HashMap<String, Contribution>();
        for (String curi : possibles.keySet()) {
            Contribution c = contributionFactory.createContribution();
            c.setURI(curi);
            c.mergeMetaData(possibles.get(curi));
            contributions.put(curi, c);
        }

        Contribution tc = contributions.remove(targetURI);
        buildDependencies(tc, new ArrayList<Contribution>(contributions.values()), monitor);
        
        List<String> dcuris = new ArrayList<String>();
        for (Contribution dc : tc.getDependencies()) {
            dcuris.add(dc.getURI());
        }
        dcuris.remove(targetURI);
        return dcuris;
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

        // get the validation schema
        validationSchema = registry.getExtensionPoint(ValidationSchemaExtensionPoint.class);
        
        // Get the reference binder
        endpointReferenceBinder = registry.getExtensionPoint(EndpointReferenceBinder.class);
            
        loadSystemContribution(new ProcessorContext(monitorFactory.createMonitor()));

        inited = true;
    }

    protected void loadSystemContribution(ProcessorContext context) {
        DefinitionsFactory definitionsFactory = modelFactories.getFactory(DefinitionsFactory.class);
        systemDefinitions = definitionsFactory.createDefinitions();

        DefinitionsExtensionPoint definitionsExtensionPoint =
            registry.getExtensionPoint(DefinitionsExtensionPoint.class);
        
        Monitor monitor = context.getMonitor();
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
                
        // now resolve and add the system contribution
        try {
            contributionProcessor.resolve(systemContribution, modelResolver, context);
        } catch (ContributionResolveException e) {
            throw new IllegalStateException(e);
        }
    }

    public Contribution cloneSystemContribution(Monitor monitor) {
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
        
        // create resolver entries to represent the SCA schema. We don't create artifacts
        // in the contribution as the XSD schema are only actually loaded on demand
        // so as long as they are in the model resolver we are set. We do it on the clone
        // so that every copy of the system contribution has the schema
        ProcessorContext context = new ProcessorContext(monitor);
        XSDFactory xsdFactory = modelFactories.getFactory(XSDFactory.class);
        List<String> scaSchemas = validationSchema.getSchemas();
        for (String scaSchemaLocation : scaSchemas){
            try {
                URL scaSchemaURL = new URL(scaSchemaLocation);
                String namespace = staxHelper.readAttribute(scaSchemaURL, XSD, "targetNamespace");

                // if this is the SCA schema store it in the system contribution
                if (namespace.equals(Constants.SCA11_TUSCANY_NS)){
                    
                    // add the schema to the model resolver under the Tuscany namespace
                    XSDefinition scaSchema = xsdFactory.createXSDefinition();
                    scaSchema.setUnresolved(true);
                    scaSchema.setNamespace(namespace);
                    scaSchema.setLocation(IOHelper.toURI(scaSchemaURL));
                    scaSchema.setUnresolved(false); 
//                    modelResolver.addModel(scaSchema, context);
                } else if (namespace.equals(Constants.SCA11_NS)) { 
                    // we know that the SCA schema's are referenced form the Tuscany schemas so 
                    // register the schema under the SCA namespace too
                    XSDefinition scaSchema = xsdFactory.createXSDefinition();
                    scaSchema.setUnresolved(true);
                    scaSchema.setNamespace(Constants.SCA11_NS);
                    scaSchema.setLocation(IOHelper.toURI(scaSchemaURL));
                    scaSchema.setUnresolved(false); 
                    modelResolver.addModel(scaSchema, context);                  
                }
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }        
        
        return contribution;
    }

    public String attachDeploymentComposite(Contribution contribution, Composite composite, boolean appending) {
        init();
        
        String compositeArtifactURI = composite.getName().getLocalPart() + ".composite";

        if (appending) {
            // check its not already there
            for (Artifact a : contribution.getArtifacts()) {
                if (compositeArtifactURI.equals(a.getURI())) {
                    throw new IllegalStateException("artifact '" + compositeArtifactURI + "' already exists in contribution: " + contribution.getURI());
                }
            }
        }

        // Create an artifact for the deployment composite
        Artifact artifact = contributionFactory.createArtifact();
        artifact.setURI(compositeArtifactURI);

        artifact.setLocation(compositeArtifactURI);
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
        return compositeArtifactURI;
    }

    public Composite build(List<Contribution> contributions, List<Contribution> allContributions, Map<QName, List<String>> bindingMap, Monitor monitor)
        throws ContributionResolveException, CompositeBuilderException {
        return build(contributions, allContributions, null, bindingMap, monitor);
    }
    
    public Composite build(List<Contribution> contributions, List<Contribution> allContributions, Contribution systemContribution, Map<QName, List<String>> bindingMap, Monitor monitor)
        throws ContributionResolveException, CompositeBuilderException {
        init();
        List<Contribution> contributionList = new ArrayList<Contribution>(contributions);
        
        if (systemContribution == null) {
            systemContribution = cloneSystemContribution(monitor);
        }
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
            buildDependencies(contribution, allContributions, monitor);

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
                // TUSCANY-3907 - clone the top level composite before we include
                //                it so that the composite model retained within 
                //                the CompositeModelResolver is not changed by the build
                try {
                    composite = (Composite)composite.clone();
                } catch (CloneNotSupportedException ex){
                   // it is supported on Composite 
                }
                
                // Include the node composite in the top-level composite
                domainComposite.getIncludes().add(composite);
            }
        }

        // build the top level composite
        BuilderContext builderContext = new BuilderContext(systemDefinitions, bindingMap, monitor);
        compositeBuilder.build(domainComposite, builderContext);
        // analyzeProblems(monitor);
   
        // do build time reference binding
        buildTimeReferenceBind(domainComposite, builderContext);        

        return domainComposite;
    }
    
    public void resolve(Contribution c, List<Contribution> dependentContributions, Monitor monitor) throws ContributionResolveException, CompositeBuilderException {
        init();
        List<Contribution> contributionList = new ArrayList<Contribution>();
        contributionList.add(c);

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
            buildDependencies(contribution, dependentContributions, monitor);

            // Resolve contributions
            for (Contribution dependency : contribution.getDependencies()) {
                if (!resolved.contains(dependency)) {
                    resolved.add(dependency);
                    contributionProcessor.resolve(dependency, modelResolver, context);
                }
            }
        }
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

    public Object loadXMLDocument(URL document, Monitor monitor) throws XMLStreamException, ContributionReadException {
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
    public Object loadXMLElement(XMLStreamReader reader, Monitor monitor) throws ContributionReadException,
        XMLStreamException {
        init();
        return staxProcessor.read(reader, new ProcessorContext(monitor));
    }

    public Object loadXMLDocument(Reader document) throws XMLStreamException, ContributionReadException, ValidationException {
        Monitor monitor = createMonitor();
        Object model = loadXMLDocument(document, monitor);
        monitor.analyzeProblems();
        return model;
    }
    
    public Object loadXMLDocument(Reader document, Monitor monitor) throws XMLStreamException, ContributionReadException {
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

    public Definitions getSystemDefinitions() {
        init();
        return systemDefinitions;
    }
    
    // The following operations gives references a chance to bind to 
    // services at deployment time. 
    
    private void buildTimeReferenceBind(Composite domainComposite, BuilderContext context){
        // create temporary local registry for all available local endpoints
        DomainRegistry domainRegistry = new LocalEndpointRegistry(registry);
        
        // populate the registry with all the endpoints that are present in the model
        populateLocalRegistry(domainComposite, domainRegistry, context);        
        
        // match all local services against the endpoint references 
        // we've just created
        matchEndpointReferences(domainComposite, domainRegistry, context); 
    }
    
    private void populateLocalRegistry(Composite composite, DomainRegistry registry, BuilderContext context){
        for (Component component : composite.getComponents()) {
            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                populateLocalRegistry((Composite)implementation, registry, context);
            }
            
            // add all endpoints to the local registry
            for (ComponentService service : component.getServices()) {
                for (Endpoint endpoint : service.getEndpoints()){
                    registry.addEndpoint(endpoint);
                }
            }
            
            // add endpoint references that we want to match to the registry
            for (ComponentReference reference : component.getReferences()) {
                for (EndpointReference epr : reference.getEndpointReferences()){
                    if (epr.getStatus().equals(EndpointReference.Status.WIRED_TARGET_NOT_FOUND)||
                        epr.getStatus().equals(EndpointReference.Status.WIRED_TARGET_IN_BINDING_URI)){
                        registry.addEndpointReference(epr);
                    }
                }
            }           
        }
    }

    private void matchEndpointReferences(Composite composite, DomainRegistry registry, BuilderContext builderContext){
        
        // look at all the endpoint references and try to match them to 
        // endpoints
        for (EndpointReference endpointReference : registry.getEndpointReferences()){
            endpointReferenceBinder.bindBuildTime(registry, endpointReference, builderContext);
        }
    } 
    
    // A minimal endpoint registry implementation used to store the Endpoints/EndpointReferences 
    // for build time local reference resolution. We don't rely on the endpoint registry
    // factory here as we specifically just want to do simple local resolution
    class LocalEndpointRegistry extends BaseDomainRegistry {
        
        private List<Endpoint> endpoints = new ArrayList<Endpoint>();
        
        public LocalEndpointRegistry(ExtensionPointRegistry registry){
            super(registry, null, "", "");
        }

        public void addEndpoint(Endpoint endpoint) {
            endpoints.add(endpoint);
            endpointAdded(endpoint);
        }
        
        public void removeEndpoint(Endpoint endpoint) {
        }
        
        public Collection<Endpoint> getEndpoints() {
            return endpoints;
        }
        
        public Endpoint getEndpoint(String uri) {
            return null;
        }
        
        public List<Endpoint> findEndpoint(String uri) {
            List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();
            for (Endpoint endpoint : endpoints) {
                if (endpoint.matches(uri)) {
                    foundEndpoints.add(endpoint);
                    logger.fine("Found endpoint with matching service  - " + endpoint);
                }
                // else the service name doesn't match
            }
            return foundEndpoints;
        }
        
        public void start() {
        }

        public void stop() {
        }

        public List<String> getInstalledContributionURIs() {
            return null;
        }

        public void uninstallContribution(String uri) {
        }

        public void installContribution(ContributionDescription cd) {
        }

        public ContributionDescription getInstalledContribution(String uri) {
            return null;
        }

        public void addRunningComposite(String contributionURI, Composite composite) {
        }

        public void removeRunningComposite(String contributionURI, String compositeURI) {
        }

        public Composite getRunningComposite(String contributionURI, String compositeURI) {
            return null;
        }

        public Map<String, List<String>> getRunningCompositeURIs() {
            return null;
        }

        public void updateInstalledContribution(ContributionDescription cd) {
        }

        @Override
        public List<String> getNodeNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getLocalNodeName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getRunningNodeName(String contributionURI, String compositeURI) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String remoteCommand(String memberName, Callable<String> command) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getContainingCompositesContributionURI(String componentName) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
