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

package org.apache.tuscany.sca.node.impl;

import static java.lang.System.currentTimeMillis;
import static org.apache.tuscany.sca.node.impl.NodeUtil.createURI;
import static org.apache.tuscany.sca.node.impl.NodeUtil.openStream;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderTmp;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultImport;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ExtendedURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultImportModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.definitions.DefinitionsFactory;
import org.apache.tuscany.sca.definitions.util.DefinitionsUtil;
import org.apache.tuscany.sca.definitions.xml.DefinitionsExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.BindingConfiguration;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.DeploymentComposite;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.configuration.xml.NodeConfigurationProcessor;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 *
 */
public class NodeFactoryImpl extends NodeFactory {
    protected static final Logger logger = Logger.getLogger(NodeImpl.class.getName());
    private static final String SCA11_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";

    protected boolean inited;
    protected Map<Object, Node> nodes = new ConcurrentHashMap<Object, Node>();

    private AssemblyFactory assemblyFactory;
    private CompositeBuilder compositeBuilder;
    private StAXArtifactProcessor<Composite> compositeProcessor;
    private ContributionFactory contributionFactory;
    private ExtendedURLArtifactProcessor<Contribution> contributionProcessor;
    private CompositeBuilder endpointReferenceBuilder;
    protected ExtensionPointRegistry extensionPoints;
    private XMLInputFactory inputFactory;
    protected FactoryExtensionPoint modelFactories;
    private ModelResolverExtensionPoint modelResolvers;
    private Monitor monitor;
    protected ProxyFactory proxyFactory;
    private Contribution systemContribution;
    private Definitions systemDefinitions;
    private StAXArtifactProcessorExtensionPoint xmlProcessors;

    /**
     * Automatically destroy the factory when last node is stopped. Subclasses
     * can set this flag.
     */
    protected boolean autoDestroy = true;

    @Override
    public Node createNode(NodeConfiguration configuration) {
        return new NodeImpl(this, configuration);
    }

    protected Node removeNode(NodeConfiguration configuration) {
        Node node = nodes.remove(getNodeKey(configuration));
        if (autoDestroy && nodes.isEmpty()) {
            destroy();
        }
        return node;
    }

    protected void addNode(NodeConfiguration configuration, Node node) {
        nodes.put(getNodeKey(configuration), node);
    }

    /**
     * @param <T>
     * @param factory
     * @return
     * @throws Exception
     */
    private <T> T getFactory(Class<T> factory) throws Exception {
        ServiceDeclaration sd = ServiceDiscovery.getInstance().getServiceDeclaration(factory.getName());
        if (sd != null) {
            return factory.cast(sd.loadClass().newInstance());
        } else {
            return factory.cast(factory.getMethod("newInstance").invoke(null));
        }
    }

    @Override
    public NodeConfiguration loadConfiguration(InputStream xml) {
        try {
            XMLInputFactory inputFactory = getFactory(XMLInputFactory.class);
            XMLOutputFactory outputFactory = getFactory(XMLOutputFactory.class);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(xml);
            NodeConfigurationProcessor processor = new NodeConfigurationProcessor(this, inputFactory, outputFactory);
            reader.nextTag();
            NodeConfiguration config = processor.read(reader);
            xml.close();
            return config;
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Map<Object, Node> getNodes() {
        return nodes;
    }

    protected Object getNodeKey(NodeConfiguration configuration) {
        return new NodeKey(configuration);
    }

    public synchronized void destroy() {
        if (inited) {
            for (Node node : nodes.values()) {
                node.stop();
                node.destroy();
            }
            nodes.clear();
            extensionPoints.stop();
            inited = false;
        }
    }

    protected static String getSystemProperty(final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty(name);
            }
        });
    }

    private static void warning(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(NodeImpl.class.getName(), "node-impl-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    /**
     * Analyze a contribution and add its dependencies to the given dependency set.
     */
    private void addContributionDependencies(Contribution contribution, List<Contribution> contributions, List<Contribution> dependencies, Set<Contribution> set, Monitor monitor) {

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
                        }
                    }
                }
            }

            if (resolved) {

                // Initialize the import's model resolver with a delegating model
                // resolver which will delegate to the matching exports
                import_.setModelResolver(new DefaultImportModelResolver(matchingExports));

            } else {
                // Record import resolution issue
                if (!(import_ instanceof DefaultImport)) {
                    warning(monitor, "UnresolvedImport", import_, import_);
                }
            }
        }
    }

    /**
     * Analyze problems reported by the artifact processors and builders.
     *
     * @throws Exception
     */
    private void analyzeProblems() throws Exception {
        for (Problem problem : monitor.getProblems()) {
            if ((problem.getSeverity() == Severity.ERROR) && (!problem.getMessageId().equals("SchemaError"))) {
                if (problem.getCause() != null) {
                    throw problem.getCause();
                } else {
                    throw new ServiceRuntimeException(problem.toString());
                }
            }
        }
    }

    private boolean attachDeploymentComposite(Contribution contribution, Reader xml, String location, boolean attached)
        throws XMLStreamException, ContributionReadException {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(xml);
        reader.nextTag();

        // Read the composite model
        Composite composite = (Composite)compositeProcessor.read(reader);
        reader.close();

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
        // REVIEW: Is it needed?
        contribution.getModelResolver().addModel(composite);
        return attached;
    }

    private void buildDependencies(Contribution contribution, List<Contribution> contributions, Monitor monitor) {
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
    private void contributionsPreresolve( List<Contribution> contributions, ModelResolver resolver )
        throws ContributionResolveException {

        for( Contribution contribution : contributions ) {
                contributionProcessor.preResolve(contribution, resolver);
        } // end for
    } // end method contributionsPreresolve

    public ExtensionPointRegistry getExtensionPoints() {
        return extensionPoints;
    }

    protected boolean isSchemaValidationEnabled() {
        String enabled = getSystemProperty(ValidationSchemaExtensionPoint.class.getName() + ".enabled");
        if (enabled == null) {
            enabled = "true";
        }
        boolean debug = logger.isLoggable(Level.FINE);
        return "true".equals(enabled) || debug;
    }

    public synchronized void init() {
        if (inited) {
            return;
        }
        long start = currentTimeMillis();

        // Create extension point registry
        extensionPoints = createExtensionPointRegistry();
        extensionPoints.start();

        // Enable schema validation only of the logger level is FINE or higher
        if (isSchemaValidationEnabled()) {
            ValidationSchemaExtensionPoint schemas =
                extensionPoints.getExtensionPoint(ValidationSchemaExtensionPoint.class);
            if (schemas != null) {
                schemas.setEnabled(true);
            }
        }

        // Use the runtime-enabled assembly factory
        modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = new RuntimeAssemblyFactory(extensionPoints);
        modelFactories.addFactory(assemblyFactory);

        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();

        // Initialize the Tuscany module activators
        // The module activators will be started
        extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);

        // Get XML input/output factories
        inputFactory = modelFactories.getFactory(XMLInputFactory.class);

        // Get contribution workspace and assembly model factories
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);

        // Create XML artifact processors
        xmlProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        compositeProcessor = xmlProcessors.getProcessor(Composite.class);

        // Create contribution content processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = (ExtendedURLArtifactProcessor<Contribution>) docProcessorExtensions.getProcessor(Contribution.class);

        // Get the model resolvers
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);

        // Get composite builders
        CompositeBuilderExtensionPoint compositeBuilders = extensionPoints.getExtensionPoint(CompositeBuilderExtensionPoint.class);
        compositeBuilder = compositeBuilders.getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");

        // Get endpoint builders
        // TODO - new extension point?
        endpointReferenceBuilder = compositeBuilders.getCompositeBuilder("org.apache.tuscany.sca.endpoint.impl.EndpointReferenceBuilderImpl");

        // Initialize runtime

        // Get proxy factory
        ProxyFactoryExtensionPoint proxyFactories = extensionPoints.getExtensionPoint(ProxyFactoryExtensionPoint.class);
        proxyFactory = new ExtensibleProxyFactory(proxyFactories);

        utilities.getUtility(WorkScheduler.class);

        DefinitionsFactory definitionsFactory = modelFactories.getFactory(DefinitionsFactory.class);
        systemDefinitions = definitionsFactory.createDefinitions();

        DefinitionsExtensionPoint definitionsExtensionPoint = extensionPoints.getExtensionPoint(DefinitionsExtensionPoint.class);
        for(Definitions defs: definitionsExtensionPoint.getDefinitions()) {
            DefinitionsUtil.aggregate(defs, systemDefinitions);
        }

        /*
        // Load the system definitions.xml from all of the loaded extension points
        DefinitionsProviderExtensionPoint definitionsProviders = extensionPoints.getExtensionPoint(DefinitionsProviderExtensionPoint.class);

        // aggregate all the definitions into a single definitions model
        try {
            for (DefinitionsProvider definitionsProvider : definitionsProviders.getDefinitionsProviders()) {
                DefinitionsUtil.aggregate(definitionsProvider.getDefinitions(), systemDefinitions);
            }
        } catch (DefinitionsProviderException e) {
            throw new IllegalStateException(e);
        }
        */

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

        inited = true;

        if (logger.isLoggable(Level.FINE)) {
            long end = currentTimeMillis();
            logger.fine("The tuscany runtime started in " + (end - start) + " ms.");
        }
    }

    protected ExtensionPointRegistry createExtensionPointRegistry() {
        return new DefaultExtensionPointRegistry();
    }

    Composite configureNode(NodeConfiguration configuration) throws Exception {

        List<Contribution> contributions = new ArrayList<Contribution>();

        // Load the specified contributions
        for (ContributionConfiguration contrib : configuration.getContributions()) {
            URI contributionURI = createURI(contrib.getURI());

            URI uri = createURI(contrib.getLocation());
            if (uri.getScheme() == null) {
                uri = new File(contrib.getLocation()).toURI();
            }
            URL contributionURL = uri.toURL();

            // Load the contribution
            logger.log(Level.INFO, "Loading contribution: " + contributionURL);
            Contribution contribution = contributionProcessor.read(null, contributionURI, contributionURL);
            contributions.add(contribution);

            boolean attached = false;
            for (DeploymentComposite dc : contrib.getDeploymentComposites()) {
                if (dc.getContent() != null) {
                    Reader xml = new StringReader(dc.getContent());
                    attached = attachDeploymentComposite(contribution, xml, null, attached);
                } else if (dc.getLocation() != null) {
                    URI dcURI = createURI(dc.getLocation());
                    if (!dcURI.isAbsolute()) {
                        Composite composite = null;
                        // The location is pointing to an artifact within the contribution
                        for (Artifact a : contribution.getArtifacts()) {
                            if (dcURI.toString().equals(a.getURI())) {
                                composite = (Composite)a.getModel();
                                if (!attached) {
                                    contribution.getDeployables().clear();
                                    attached = true;
                                }
                                contribution.getDeployables().add(composite);
                                break;
                            }
                        }
                        if (composite == null) {
                            // Not found
                            throw new ServiceRuntimeException("Deployment composite " + dcURI
                                + " cannot be found within contribution "
                                + contribution.getLocation());
                        }
                    } else {
                        URL url = dcURI.toURL();
                        InputStream is = openStream(url);
                        Reader xml = new InputStreamReader(is, "UTF-8");
                        attached = attachDeploymentComposite(contribution, xml, url.toString(), attached);
                    }
                }
            }
            analyzeProblems();
        }

        // Build an aggregated SCA definitions model. Must be done before we try and
        // resolve any contributions or composites as they may depend on the full
        // definitions.xml picture

        // get all definitions.xml artifacts from contributions and aggregate
        // into the system contribution. In turn add a default import into
        // each contribution so that for unresolved items the resolution
        // processing will look in the system contribution
        for (Contribution contribution: contributions) {
            // aggregate definitions
            for (Artifact artifact : contribution.getArtifacts()) {
                Object model = artifact.getModel();
                if (model instanceof Definitions) {
                    DefinitionsUtil.aggregate((Definitions)model, systemDefinitions);
                }
            }

            // create a default import and wire it up to the system contribution
            // model resolver. This is the trick that makes the resolution processing
            // skip over to the system contribution if resolution is unsuccessful
            // in the current contribution
            DefaultImport defaultImport = contributionFactory.createDefaultImport();
            defaultImport.setModelResolver(systemContribution.getModelResolver());
            contribution.getImports().add(defaultImport);
        }

        ExtensibleModelResolver modelResolver = new ExtensibleModelResolver(new Contributions(contributions), modelResolvers, modelFactories);

        // now resolve and add the system contribution
        contributionProcessor.resolve(systemContribution, modelResolver);
        contributions.add(systemContribution);

        // TODO - Now we can calculate applicable policy sets for each composite

        // pre-resolve the contributions
        contributionsPreresolve(contributions, modelResolver);

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

        // Create a top level composite to host our composite
        // This is temporary to make the activator happy
        Composite tempComposite = assemblyFactory.createComposite();
        tempComposite.setName(new QName(SCA11_TUSCANY_NS, "_tempComposite"));
        tempComposite.setURI(SCA11_TUSCANY_NS);

        for (Contribution contribution : contributions) {
            for (Composite composite : contribution.getDeployables()) {
                // Include the node composite in the top-level composite
                tempComposite.getIncludes().add(composite);
            }
        }

        // TODO - EPR - create a binding map to pass down into the builders
        //              for use during URI calculation.
        Map<QName, List<String>> bindingMap = new HashMap<QName, List<String>>();
        for (BindingConfiguration config : configuration.getBindings()) {
            bindingMap.put(config.getBindingType(), config.getBaseURIs());
        }

        // build the top level composite
        ((CompositeBuilderTmp)compositeBuilder).build(tempComposite, systemDefinitions, bindingMap, monitor);
        analyzeProblems();

        endpointReferenceBuilder.build(tempComposite, systemDefinitions, monitor);
        analyzeProblems();

        return tempComposite;

    }

    protected static class NodeKey {
        private String domainURI;
        private String nodeURI;

        public NodeKey(NodeConfiguration configuration) {
            this.domainURI = configuration.getDomainURI();
            this.nodeURI = configuration.getURI();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((domainURI == null) ? 0 : domainURI.hashCode());
            result = prime * result + ((nodeURI == null) ? 0 : nodeURI.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NodeKey other = (NodeKey)obj;
            if (domainURI == null) {
                if (other.domainURI != null)
                    return false;
            } else if (!domainURI.equals(other.domainURI))
                return false;
            if (nodeURI == null) {
                if (other.nodeURI != null)
                    return false;
            } else if (!nodeURI.equals(other.nodeURI))
                return false;
            return true;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            if (domainURI != null) {
                buf.append("{").append(domainURI).append("}");
            }
            if (nodeURI != null) {
                buf.append(nodeURI);
            }
            return buf.toString();
        }
    }
}
