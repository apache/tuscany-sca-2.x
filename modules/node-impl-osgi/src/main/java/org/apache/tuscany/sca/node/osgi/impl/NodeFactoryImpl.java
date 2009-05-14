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

package org.apache.tuscany.sca.node.osgi.impl;

import static java.lang.System.currentTimeMillis;
import static org.apache.tuscany.sca.node.osgi.impl.NodeUtil.contribution;
import static org.apache.tuscany.sca.node.osgi.impl.NodeUtil.createURI;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderExtensionPoint;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultImport;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.definitions.DefinitionsFactory;
import org.apache.tuscany.sca.definitions.util.DefinitionsUtil;
import org.apache.tuscany.sca.definitions.xml.DefinitionsExtensionPoint;
import org.apache.tuscany.sca.implementation.node.ConfiguredNodeImplementation;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.provider.DefinitionsProvider;
import org.apache.tuscany.sca.provider.DefinitionsProviderException;
import org.apache.tuscany.sca.provider.DefinitionsProviderExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionBuilder;
import org.apache.tuscany.sca.workspace.builder.ContributionBuilderExtensionPoint;
import org.oasisopen.sca.CallableReference;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Represents an SCA runtime node.
 *
 * @version $Rev$ $Date$
 */
public class NodeFactoryImpl {

    private static final String SCA11_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";

    private static final Logger logger = Logger.getLogger(NodeFactoryImpl.class.getName());

    private boolean inited;
    private BundleContext bundleContext;
    private ServiceRegistration registration;

    private ExtensionPointRegistry extensionPoints;
    private UtilityExtensionPoint utilities;
    private Monitor monitor;
    private URLArtifactProcessor<Contribution> contributionProcessor;
    private ModelResolverExtensionPoint modelResolvers;
    private FactoryExtensionPoint modelFactories;
    private WorkspaceFactory workspaceFactory;
    private ContributionFactory contributionFactory;
    private AssemblyFactory assemblyFactory;
    private XMLInputFactory inputFactory;
    private ContributionBuilder contributionDependencyBuilder;
    private CompositeBuilder compositeBuilder;
    private StAXArtifactProcessorExtensionPoint xmlProcessors;
    private StAXArtifactProcessor<Composite> compositeProcessor;
    private ProxyFactory proxyFactory;
    private List<ModuleActivator> moduleActivators = new ArrayList<ModuleActivator>();
    private WorkScheduler workScheduler;
    private Contribution systemContribution;
    private Definitions systemDefinitions;

    private Map<Bundle, Node> nodes = new ConcurrentHashMap<Bundle, Node>();

    /**
     * Constructs a new Node controller
     */
    public NodeFactoryImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private ConfiguredNodeImplementation getNodeConfiguration(Bundle bundle) {
        // Create a node configuration
        NodeImplementationFactory nodeImplementationFactory =
            modelFactories.getFactory(NodeImplementationFactory.class);
        ConfiguredNodeImplementation configuration = nodeImplementationFactory.createConfiguredNodeImplementation();

        String compositeURI = (String)bundle.getHeaders().get("SCA-Composite");
        if (compositeURI == null) {
            compositeURI = "OSGI-INF/sca/bundle.composite";
        }
        if (compositeURI != null) {
            Composite composite = assemblyFactory.createComposite();
            composite.setURI(compositeURI);
            composite.setUnresolved(true);
            configuration.setComposite(composite);
        }

        URL root = bundle.getEntry("/");
        org.apache.tuscany.sca.node.Contribution bundleContribution =
            new org.apache.tuscany.sca.node.Contribution(bundle.getSymbolicName(), root.toString());

        Contribution contribution = contribution(contributionFactory, bundleContribution);
        configuration.getContributions().add(contribution);
        return configuration;
    }

    private ConfiguredNodeImplementation getNodeConfiguration(Bundle bundle, String compositeContent) throws Exception {

        ConfiguredNodeImplementation configuration = getNodeConfiguration(bundle);
        if (compositeContent != null) {

            Composite deploymentComposite =
                addDeploymentComposite(configuration.getContributions().get(0), compositeContent);

            configuration.setComposite(deploymentComposite);
        }

        return configuration;
    }

    /**
     * Add the deployment composite to an installed SCA contribution
     * @param compositeContent The XML string for the deployment composite
     * @return The deployment composite
     * @throws Exception
     */
    private Composite addDeploymentComposite(Contribution contrib, String compositeContent) throws Exception {

        // Load the deployment composite
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(compositeContent));
        reader.nextTag();

        // Read the composite model
        Composite deploymentComposite = (Composite)compositeProcessor.read(reader);

        Artifact compositeArtifact = contributionFactory.createArtifact();
        compositeArtifact.setModel(deploymentComposite);
        compositeArtifact.setURI(deploymentComposite.getName()+".composite");
        compositeArtifact.setUnresolved(false);

        contrib.getArtifacts().add(compositeArtifact);
        contrib.getDeployables().add(deploymentComposite);

        analyzeProblems();
        return deploymentComposite;
    }

    private synchronized void init() {
        if (inited) {
            return;
        }
        long start = currentTimeMillis();

        // Create extension point registry
        extensionPoints = new DefaultExtensionPointRegistry();

        utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);

        // Add the OSGi BundleContext as a system utility
        utilities.addUtility(bundleContext);

        // Register the ExtensionPointRegistry as an OSGi service
        Dictionary<Object, Object> props = new Hashtable<Object, Object>();
        registration = bundleContext.registerService(ExtensionPointRegistry.class.getName(), extensionPoints, props);

        // Enable schema validation only of the logger level is FINE or higher
        ValidationSchemaExtensionPoint schemas =
            extensionPoints.getExtensionPoint(ValidationSchemaExtensionPoint.class);
        if (schemas != null) {
            schemas.setEnabled(logger.isLoggable(Level.FINE));
        }

        // Use the runtime-enabled assembly factory
        modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = new RuntimeAssemblyFactory();
        modelFactories.addFactory(assemblyFactory);

        // Create a monitor
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();

        // Initialize the Tuscany module activators
        ModuleActivatorExtensionPoint activators =
            extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);
        for (ModuleActivator moduleActivator : activators.getModuleActivators()) {
            try {
                moduleActivator.start(extensionPoints);
                moduleActivators.add(moduleActivator);
            } catch (Throwable e) {
                // Ignore the failing module for now
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        // Get XML input/output factories
        inputFactory = modelFactories.getFactory(XMLInputFactory.class);

        // Get contribution workspace and assembly model factories
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class);

        // Create XML artifact processors
        xmlProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        compositeProcessor = xmlProcessors.getProcessor(Composite.class);

        // Create contribution content processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions =
            extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = docProcessorExtensions.getProcessor(Contribution.class);

        // Get the model resolvers
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);

        // Get a contribution dependency builder
        ContributionBuilderExtensionPoint contributionBuilders =
            extensionPoints.getExtensionPoint(ContributionBuilderExtensionPoint.class);
        contributionDependencyBuilder =
            contributionBuilders
                .getContributionBuilder("org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder");

        // Get composite builders
        CompositeBuilderExtensionPoint compositeBuilders =
            extensionPoints.getExtensionPoint(CompositeBuilderExtensionPoint.class);
        compositeBuilder =
            compositeBuilders.getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");

        // Initialize runtime

        // Get proxy factory
        ProxyFactoryExtensionPoint proxyFactories = extensionPoints.getExtensionPoint(ProxyFactoryExtensionPoint.class);
        proxyFactory = new ExtensibleProxyFactory(proxyFactories);

        workScheduler = utilities.getUtility(WorkScheduler.class);

        // Load the system definitions.xml from all of the loaded extension points
        DefinitionsFactory definitionsFactory = modelFactories.getFactory(DefinitionsFactory.class);
        systemDefinitions = definitionsFactory.createDefinitions();

        DefinitionsExtensionPoint definitionsExtensionPoint = extensionPoints.getExtensionPoint(DefinitionsExtensionPoint.class);
        for(Definitions defs: definitionsExtensionPoint.getDefinitions()) {
            DefinitionsUtil.aggregate(systemDefinitions, defs);
        }

        DefinitionsProviderExtensionPoint definitionsProviders =
            extensionPoints.getExtensionPoint(DefinitionsProviderExtensionPoint.class);

        // aggregate all the definitions into a single definitions model
        try {
            for (DefinitionsProvider definitionsProvider : definitionsProviders.getDefinitionsProviders()) {
                DefinitionsUtil.aggregate(definitionsProvider.getDefinitions(), systemDefinitions);
            }
        } catch (DefinitionsProviderException e) {
            throw new IllegalStateException(e);
        }

        // create a system contribution to hold the definitions. The contribution
        // will be extended later with definitions from application contributions
        systemContribution = contributionFactory.createContribution();
        systemContribution.setURI(SCA11_TUSCANY_NS + "/contributions/_system_");
        systemContribution.setLocation(SCA11_TUSCANY_NS + "/contributions/_system_");
        ModelResolver modelResolver = new ExtensibleModelResolver(systemContribution, modelResolvers, modelFactories);
        systemContribution.setModelResolver(modelResolver);
        systemContribution.setUnresolved(true);

        // create an artifact to represent the system defintions and
        // add it to the contribution
        List<Artifact> artifacts = systemContribution.getArtifacts();
        Artifact artifact = contributionFactory.createArtifact();
        artifact.setURI(SCA11_TUSCANY_NS + "/contributions/_system_/definitions");
        artifact.setLocation(SCA11_TUSCANY_NS + "/contributions/_system_/definitions");
        artifact.setModel(systemDefinitions);
        artifacts.add(artifact);

        if (logger.isLoggable(Level.FINE)) {
            long end = currentTimeMillis();
            logger.fine("The tuscany runtime started in " + (end - start) + " ms.");
        }
        inited = true;
    }

    private Composite configureNode(ConfiguredNodeImplementation configuration) throws Exception {

        // Create workspace model
        Workspace workspace = workspaceFactory.createWorkspace();
        workspace.setModelResolver(new ExtensibleModelResolver(workspace, modelResolvers, modelFactories));

        // Load the specified contributions
        for (Contribution c : configuration.getContributions()) {
            URI contributionURI = NodeUtil.createURI(c.getURI());

            URI uri = createURI(c.getLocation());
            if (uri.getScheme() == null) {
                uri = new File(c.getLocation()).toURI();
            }
            URL contributionURL = uri.toURL();

            // Load the contribution
            logger.log(Level.INFO, "Loading contribution: " + contributionURL);
            Contribution contribution = contributionProcessor.read(null, contributionURI, contributionURL);
            workspace.getContributions().add(contribution);
            analyzeProblems();
        }

        // Build an aggregated SCA definitions model. Must be done before we try and
        // resolve any contributions or composites as they may depend on the full
        // definitions.xml picture

        // get all definitions.xml artifacts from contributions and aggregate
        // into the system contribution. In turn add a default import into
        // each contribution so that for unresolved items the resolution
        // processing will look in the system contribution
        for (Contribution contribution : workspace.getContributions()) {
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

        // now resolve the system contribution and add the contribution
        // to the workspace
        contributionProcessor.resolve(systemContribution, workspace.getModelResolver());
        workspace.getContributions().add(systemContribution);

        // TODO - Now we can calculate applicable policy sets for each composite

        // Build the contribution dependencies
        Set<Contribution> resolved = new HashSet<Contribution>();
        for (Contribution contribution : workspace.getContributions()) {
            contributionDependencyBuilder.build(contribution, workspace, monitor);

            // Resolve contributions
            for (Contribution dependency : contribution.getDependencies()) {
                if (!resolved.contains(dependency)) {
                    resolved.add(dependency);
                    contributionProcessor.resolve(dependency, workspace.getModelResolver());
                }
            }
        }

        Composite composite = configuration.getComposite();

        if (composite == null) {
            composite = getDefaultComposite(configuration, workspace);
        }

        // Find the composite in the given contributions
        boolean found = false;
        Artifact compositeFile = contributionFactory.createArtifact();
        compositeFile.setUnresolved(true);
        compositeFile.setURI(composite.getURI());
        for (Contribution contribution : workspace.getContributions()) {
            ModelResolver resolver = contribution.getModelResolver();
            //            for (Artifact artifact : contribution.getArtifacts()){
            //                logger.log(Level.INFO,"artifact - " + artifact.getURI());
            //            }
            Artifact resolvedArtifact = resolver.resolveModel(Artifact.class, compositeFile);
            //            if (!resolvedArtifact.isUnresolved() && resolvedArtifact.getModel() instanceof Composite) {

            if (!composite.isUnresolved()) {

                // The composite content was passed into the node and read into a composite model,
                // don't use the composite found in the contribution, use that composite, but just resolve
                // it within the context of the contribution
                compositeProcessor.resolve(composite, resolver);

            } else {

                // Use the resolved composite we've found in the contribution
                composite = (Composite)resolvedArtifact.getModel();
            }
            found = true;
            //            break;
            //          }
        }
        //        if (!found) {
        //            throw new IllegalArgumentException("Composite not found: " + composite.getURI());
        //        }

        // Build the composite and wire the components included in it
        compositeBuilder.build(composite, systemDefinitions, monitor);
        analyzeProblems();

        // Create a top level composite to host our composite
        // This is temporary to make the activator happy
        Composite tempComposite = assemblyFactory.createComposite();
        tempComposite.setName(new QName(SCA11_TUSCANY_NS, "_domain_fragment_"));
        tempComposite.setURI(SCA11_TUSCANY_NS + "_domain_fragment_.composite");

        // Include the node composite in the top-level composite
        tempComposite.getIncludes().add(composite);

        /*
        // The following line may return null, to be investigated
        XPathFactory xPathFactory = modelFactories.getFactory(XPathFactory.class);

        for (PolicySet policySet : systemDefinitions.getPolicySets()) {
            if (policySet.getAppliesTo() != null) {
                XPath xpath = xPathFactory.newXPath();
                // FIXME: We need to develop a xpath function resolver to
                // deal with the SCA functions
                // xpath.setXPathFunctionResolver(resolver);
                XPathExpression exp = xpath.compile(policySet.getAppliesTo());
                // exp.evaluate(item, XPathConstants.BOOLEAN);
            }
        }
        */
        return tempComposite;
    }

    public void destroy() {
        if (registration != null) {
            registration.unregister();
        }

        // Stop the runtime modules
        for (ModuleActivator moduleActivator : moduleActivators) {
            moduleActivator.stop(extensionPoints);
        }

        // Stop and destroy the work manager
        workScheduler.destroy();
    }

    public Node createNode(Bundle bundle) {
        Node node = new NodeImpl(bundle);
        nodes.put(bundle, node);
        return node;
    }

    public Node createNode(Bundle bundle, String compositeContent) {
        Node node = new NodeImpl(bundle, compositeContent);
        nodes.put(bundle, node);
        return node;
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

    /*
     * Sets a default composite by using any deployable one.
     */
    private Composite getDefaultComposite(ConfiguredNodeImplementation configuration, Workspace workspace) {
        // just use the first deployable composte
        for (Contribution contribution : workspace.getContributions()) {
            for (Composite c : contribution.getDeployables()) {
                Composite composite = assemblyFactory.createComposite();
                composite.setURI(c.getURI());
                composite.setUnresolved(true);
                configuration.setComposite(composite);
                return composite;
            }
        }
        throw new ServiceRuntimeException("no deployable composite found");
    }

    public ExtensionPointRegistry getExtensionPoints() {
        return extensionPoints;
    }

    public class NodeImpl implements Node, Client {
        private Bundle bundle;
        private Composite domainFragementComposite;
        private CompositeActivator compositeActivator;
        private ConfiguredNodeImplementation configuration;

        public NodeImpl(Bundle bundle) {
            this(bundle, null);
        }

        public NodeImpl(Bundle bundle, String compositeContent) {
            try {
                // Initialize the runtime
                init();

                this.bundle = bundle;
                this.configuration = getNodeConfiguration(bundle, compositeContent);

                // Configure the node
                this.domainFragementComposite = configureNode(configuration);
                this.compositeActivator = utilities.getUtility(CompositeActivator.class, true);
                this.compositeActivator.setDomainComposite(domainFragementComposite);

            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }
        }

        public Node start() {
            logger.log(Level.INFO, "Starting node: " + bundle.getSymbolicName());

            try {

                Composite composite = domainFragementComposite.getIncludes().get(0);
                // Activate the composite
                compositeActivator.activate(composite);

                // Start the composite
                compositeActivator.start(composite);

                return this;

            } catch (ActivationException e) {
                throw new IllegalStateException(e);
            }

        }

        public void stop() {
            logger.log(Level.INFO, "Stopping node: " + bundle.getSymbolicName());

            try {

                Composite composite = domainFragementComposite.getIncludes().get(0);
                // Stop the composite
                compositeActivator.stop(composite);

                // Deactivate the composite
                compositeActivator.deactivate(composite);

            } catch (ActivationException e) {
                throw new IllegalStateException(e);
            }

        }

        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
            return (R)proxyFactory.cast(target);
        }

        public <B> B getService(Class<B> businessInterface, String serviceName) {

            ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
            if (serviceReference == null) {
                throw new ServiceRuntimeException("Service not found: " + serviceName);
            }
            return serviceReference.getService();
        }

        public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {

            // Extract the component name
            String componentName;
            String serviceName;
            int i = name.indexOf('/');
            if (i != -1) {
                componentName = name.substring(0, i);
                serviceName = name.substring(i + 1);

            } else {
                componentName = name;
                serviceName = null;
            }

            // Lookup the component
            Component component = null;

            for (Component compositeComponent : domainFragementComposite.getIncludes().get(0).getComponents()) {
                if (compositeComponent.getName().equals(componentName)) {
                    component = compositeComponent;
                }
            }

            if (component == null) {
                throw new ServiceRuntimeException("The service " + name + " has not been contributed to the domain");
            }
            RuntimeComponentContext componentContext = null;

            // If the component is a composite, then we need to find the
            // non-composite component that provides the requested service
            if (component.getImplementation() instanceof Composite) {
                for (ComponentService componentService : component.getServices()) {
                    if (serviceName == null || serviceName.equals(componentService.getName())) {
                        CompositeService compositeService = (CompositeService)componentService.getService();
                        if (compositeService != null) {
                            if (serviceName != null) {
                                serviceName = "$promoted$" + component.getName() + "$slash$" + serviceName;
                            }
                            componentContext =
                                ((RuntimeComponent)compositeService.getPromotedComponent()).getComponentContext();
                            return componentContext.createSelfReference(businessInterface, compositeService
                                .getPromotedService());
                        }
                        break;
                    }
                }
                // No matching service found
                throw new ServiceRuntimeException("Composite service not found: " + name);
            } else {
                componentContext = ((RuntimeComponent)component).getComponentContext();
                if (serviceName != null) {
                    return componentContext.createSelfReference(businessInterface, serviceName);
                } else {
                    return componentContext.createSelfReference(businessInterface);
                }
            }
        }

        public void destroy() {
            this.bundle = null;
            this.domainFragementComposite = null;
            this.compositeActivator = null;
            this.configuration = null;
            nodes.remove(this);
        }

    }

    public Map<Bundle, Node> getNodes() {
        return nodes;
    }
}
