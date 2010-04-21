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
import static org.apache.tuscany.sca.common.java.io.IOHelper.createURI;
import static org.apache.tuscany.sca.common.java.io.IOHelper.openStream;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.deployment.Deployer;
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
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointReferenceBinder;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * This class provides a node factory that can create multiple nodes that share the same 
 * extension point registry
 */
public class NodeFactoryImpl extends NodeFactory {
    protected static final Logger logger = Logger.getLogger(NodeImpl.class.getName());

    protected boolean inited;
    protected Map<Object, Node> nodes = new ConcurrentHashMap<Object, Node>();

    protected Deployer deployer;
    protected ExtensionPointRegistry registry;
    protected ProxyFactory proxyFactory;
    protected MonitorFactory monitorFactory;
    
    /**
     * Automatically destroy the factory when last node is stopped. Subclasses
     * can set this flag.
     */
    protected boolean autoDestroy = true;

    @Override
    public Node createNode(NodeConfiguration configuration) {
        if (configuration.getURI() == null) {
            // Make sure a unique node URI is created for the same node factory
            configuration.setURI(generateNodeURI());
        }
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

    @Override
    public NodeConfiguration loadConfiguration(InputStream xml, URL base) {
        try {
            init();
            InputStreamReader reader = new InputStreamReader(xml, "UTF-8");
            ProcessorContext context = deployer.createProcessorContext();
            NodeConfiguration config = deployer.loadXMLDocument(reader, context.getMonitor());
            if (base != null && config != null) {
                // Resolve the contribution location against the node.xml
                for (ContributionConfiguration c : config.getContributions()) {
                    String location = c.getLocation();
                    if (location != null) {
                        URL url = new URL(base, location);
                        url = IOHelper.normalize(url);
                        c.setLocation(url.toString());
                    }
                }
            }
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
            }
            nodes.clear();
            deployer.stop();
            registry.stop();
            super.destroy();
            inited = false;
        }
    }

    /**
     * Analyze problems reported by the artifact processors and builders.
     *
     * @throws Exception
     */
    private void analyzeProblems(Monitor monitor) throws Throwable {
        try {
            for (Problem problem : monitor.getProblems()) {
                if ((problem.getSeverity() == Severity.ERROR)) {
                    if (problem.getCause() != null) {
                        throw problem.getCause();
                    } else {
                        throw new ServiceRuntimeException(problem.toString());
                    }
                }
            }
        } finally {
            // FIXME: Clear problems so that the monitor is clean again
            monitor.reset();
        }
    }

    private boolean attachDeploymentComposite(Contribution contribution, Reader xml, String location, boolean attached, ProcessorContext context)
        throws XMLStreamException, ContributionReadException {

        // Read the composite model
        Composite composite = deployer.loadXMLDocument(xml, context.getMonitor());

        // Replace the deployable composites with the deployment composites
        // Clear the deployable composites if it's the first deployment composite
        deployer.attachDeploymentComposite(contribution, composite, attached);
        if (!attached) {
            attached = true;
        } 
        return attached;
    }

    public ExtensionPointRegistry getExtensionPointRegistry() {
        if (registry == null) {
            // Create extension point registry
            registry = createExtensionPointRegistry();
            registry.start();
        }        
        return registry;
    }

    public synchronized void init() {
        if (inited) {
            return;
        }
        long start = currentTimeMillis();

        getExtensionPointRegistry();
        
        // Use the runtime-enabled assembly factory
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = new RuntimeAssemblyFactory(registry);
        modelFactories.addFactory(assemblyFactory);

        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        monitorFactory = utilities.getUtility(MonitorFactory.class);

        utilities.getUtility(RuntimeProperties.class).setProperties(properties);
        
        // Load the Deployer
        deployer = utilities.getUtility(Deployer.class);

        // Enable schema validation only of the logger level is FINE or higher
        deployer.setSchemaValidationEnabled(isSchemaValidationEnabled());

        // Initialize the Tuscany module activators
        // The module activators will be started
        registry.getExtensionPoint(ModuleActivatorExtensionPoint.class);

        // Initialize runtime

        // Get proxy factory
        proxyFactory = ExtensibleProxyFactory.getInstance(registry);

        utilities.getUtility(WorkScheduler.class);

        inited = true;

        if (logger.isLoggable(Level.FINE)) {
            long end = currentTimeMillis();
            logger.fine("The tuscany runtime started in " + (end - start) + " ms.");
        }
    }

    protected ExtensionPointRegistry createExtensionPointRegistry() {
        return new DefaultExtensionPointRegistry();
    }
    
    protected boolean isSchemaValidationEnabled() {
        String enabled = getSystemProperty(ValidationSchemaExtensionPoint.class.getName() + ".enabled");
        if (enabled == null) {
            enabled = "true";
        }
        boolean debug = logger.isLoggable(Level.FINE);
        return "true".equals(enabled) || debug;
    }    

    protected Composite configureNode(NodeConfiguration configuration, List<Contribution> contributions, ProcessorContext context)
        throws Throwable {
        if (contributions == null) {
            // Load contributions
            contributions = loadContributions(configuration, context);
        }
        
        Monitor monitor = context.getMonitor();
        Map<QName, List<String>> bindingBaseURIs = new HashMap<QName, List<String>>();
        for (BindingConfiguration config : configuration.getBindings()) {
            bindingBaseURIs.put(config.getBindingType(), config.getBaseURIs());
        }
        Composite domainComposite = deployer.build(contributions, bindingBaseURIs, monitor);
        analyzeProblems(monitor);
        
        // postBuildEndpointReferenceMatching(domainComposite);
        
        return domainComposite;
    }
    
    // =============================================
    // TODO - TUSCANY-3425
    // post build endpoint reference matching. Give the matching algorithm
    // a chance to run and report any errors for local references prior to 
    // runtime start. Not in use at the moment as we are getting away with
    // runtime matching. Leaving here for when we come to sorting out 
    // autowire which still relies on matching in the builder
    private void postBuildEndpointReferenceMatching(Composite composite){
        EndpointReferenceBinder endpointReferenceBinder = registry.getExtensionPoint(EndpointReferenceBinder.class);
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(registry);
        
        // create temporary local registry for all available local endpoints
        // TODO - need a better way of getting a local registry
        EndpointRegistry registry = domainRegistryFactory.getEndpointRegistry("vm://tmp", "local");
        
        // populate the registry with all the endpoints that are currently present in the model
        populateLocalRegistry(composite, registry);
        
        // look at all the endpoint references and try to match them to 
        // any local endpoints
        for (EndpointReference endpointReference : registry.getEndpointReferences()){
            endpointReferenceBinder.bindBuildTime(registry, endpointReference);
        }
        
        // remove the local registry
        domainRegistryFactory.getEndpointRegistries().remove(registry);
    }
    
    private void populateLocalRegistry(Composite composite, EndpointRegistry registry){
        for (Component component : composite.getComponents()) {
            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                populateLocalRegistry((Composite)implementation, registry);
            }
            
            for (ComponentService service : component.getServices()) {
                for (Endpoint endpoint : service.getEndpoints()){
                    registry.addEndpoint(endpoint);
                }
            }
            
            for (ComponentReference reference : component.getReferences()) {
                for (EndpointReference endpointReference : reference.getEndpointReferences()){
                    registry.addEndpointReference(endpointReference);
                }
            }            
        }
    }    
    
    // =============================================

    protected List<Contribution> loadContributions(NodeConfiguration configuration, ProcessorContext context) throws Throwable {
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
            Contribution contribution = deployer.loadContribution(contributionURI, contributionURL, context.getMonitor());
            contributions.add(contribution);

            boolean attached = false;
            for (DeploymentComposite dc : contrib.getDeploymentComposites()) {
                if (dc.getContent() != null) {
                    Reader xml = new StringReader(dc.getContent());
                    attached = attachDeploymentComposite(contribution, xml, null, attached, context);
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
                        attached = attachDeploymentComposite(contribution, xml, url.toString(), attached, context);
                    }
                }
            }
            analyzeProblems(context.getMonitor());
        }
        return contributions;
    }

    protected static String getSystemProperty(final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty(name);
            }
        });
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

    @Override
    public void configure(Map<String, Map<String, String>> attributes) {
        ServiceDiscovery discovery = getExtensionPointRegistry().getServiceDiscovery();
        for (Map.Entry<String, Map<String, String>> e : attributes.entrySet()) {
            discovery.setAttribute(e.getKey(), e.getValue());
        }
        for (Object o : properties.keySet()) {
            String p = (String) o;
            if (p.indexOf('.') > -1) {
                String serviceType = p.substring(0, p.lastIndexOf('.'));
                String attribute = p.substring(p.lastIndexOf('.')+1);
                discovery.setAttribute(serviceType, attribute, properties.getProperty(p));
            }
        }
        super.configure(attributes);
    }
    
    /**
     * Added to allow the node access to the deployer in order to get 
     * to the systemContribution and hence set up the CompositeContext so that
     * the runtime epr matching algorithm can get at the binding types
     * @return
     */
    public Deployer getDeployer() {
        return deployer;
    }
    
}
