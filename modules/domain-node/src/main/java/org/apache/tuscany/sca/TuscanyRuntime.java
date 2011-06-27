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

package org.apache.tuscany.sca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * The TuscanyRuntime is the main class for using Tuscany. It can create Nodes,
 * run composites, and provides access to various utility APIs
 */
public class TuscanyRuntime {

    public static final String DEFAUL_DOMAIN_NAME = "default";
    private Deployer deployer;
    private ExtensionPointRegistry extensionPointRegistry;
    private CompositeActivator compositeActivator;
    private ExtensibleDomainRegistryFactory domainRegistryFactory;
    private RuntimeAssemblyFactory assemblyFactory;

    /**
     * Creates a new TuscanyRuntime 
     * @return a TuscanyRuntime
     */
    public static TuscanyRuntime newInstance() {
        return new TuscanyRuntime(null);
    }
    
    /**
     * Creates a new TuscanyRuntime 
     * @param config  Properties to configure the TuscanyRuntime
     * @return a TuscanyRuntime
     */
    public static TuscanyRuntime newInstance(Properties config) {
        return new TuscanyRuntime(config);
    }

    /**
     * A helper method to run a standalone SCA composite in the default standalone SCA domain.
     * @param compositeURI  URI within the contribution of a composite to run 
     *         if compositeURI is null then all deployable composites in the contribution will be run 
     * @param contributionURL  URL of the contribution
     * @param dependentContributionURLs  optional URLs of dependent contributions
     * @return a Node with installed contributions
     */
    public static Node runComposite(String compositeURI, String contributionURL, String... dependentContributionURLs) {
        return runComposite(null, compositeURI, contributionURL, dependentContributionURLs);
    }

    /**
     * A helper method to run a standalone SCA composite in a SCA domain
     * @param domainURI the URI of the SCA domain
     * @param compositeURI  URI within the contribution of a composite to run 
     *         if compositeURI is null then all deployable composites in the contribution will be run 
     * @param contributionURL  URL of the contribution
     * @param dependentContributionURLs  optional URLs of dependent contributions
     * @return a Node with installed contributions
     */
    public static Node runComposite(URI domainURI, String compositeURI, String contributionURL, String... dependentContributionURLs) {
        try {
            TuscanyRuntime runtime = newInstance();
            String domain = domainURI == null ? DEFAUL_DOMAIN_NAME : domainURI.toString();
            DomainRegistry domainRegistry = runtime.domainRegistryFactory.getEndpointRegistry(domain, null);
            NodeImpl node = new NodeImpl(runtime.deployer, runtime.compositeActivator, domainRegistry, runtime.extensionPointRegistry, runtime);

            if (dependentContributionURLs != null) {
                for (int i=dependentContributionURLs.length-1; i>-1; i--) {
                    node.installContribution(null, dependentContributionURLs[i], null, null);
                }
            }

            String curi = node.installContribution(null, contributionURL, null, null);
            if (compositeURI != null) {
                node.startComposite(curi, compositeURI);
            } else {
                for (String compURI : node.getDeployableCompositeURIs(curi)) {
                    node.startComposite(curi, compURI);
                }
            }
            return node;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected TuscanyRuntime(Properties config) {
        init(config);
    }
    
    /**
     * Creates a Node
     * @return a Node
     */
    public Node createNode() {
        return createNode((String)null);
    }

    /**
     * Creates a Node in an SCA domain 
     * @param domainURI  the URI of the SCA domain
     * @return a Node
     */
    public Node createNode(String domainURI) {
        if (domainURI == null){
            domainURI = DEFAUL_DOMAIN_NAME;
        }
        DomainRegistry domainRegistry = domainRegistryFactory.getEndpointRegistry(domainURI, null);
        return new NodeImpl(deployer, compositeActivator, domainRegistry, extensionPointRegistry, null);
    }
    
    /*
     * Create a node from a file system directory. 
     * If the directory is actually a file use createNodeFromXML
     * if the directory contains a file named node.xml then use createNodeFromXML
     * Otherwise, the directory can contain:
     *  domain.properties 
     *  contributions - jar, zip, or exploded directories
     *  sca-contribution.xml metaData files to override whats in a contribution
     *  .composite files to add to contributions as additional deployables
     * 
     * TODO: Review if this is useful?
     */
    public Node createNode(File directory) throws ContributionReadException, ValidationException, ActivationException, XMLStreamException, IOException {
        
        if (!directory.isDirectory()) {
            return createNodeFromXML(directory.toURI().toURL().toString());
        }
        
        File nodeXML = new File(directory, "node.xml");
        if (nodeXML.exists()) {
            return createNodeFromXML(nodeXML.toURI().toURL().toString());
        }
        
        Properties domainProps = new Properties();
        File propsFile = new File(directory, "domain.properties");
        if (propsFile.exists()) {
            domainProps.load(new FileInputStream(propsFile));
        }
        String domainName = domainProps.getProperty("domainName", directory.getName());
        String domainURI = domainProps.getProperty("domainURI", domainName);

        DomainRegistry domainRegistry = domainRegistryFactory.getEndpointRegistry(domainURI, domainName);
        Node node = new NodeImpl(deployer, compositeActivator, domainRegistry, extensionPointRegistry, null);

        List<String> installed = new ArrayList<String>();
        for (File f : directory.listFiles()) {
            if (f.getName().endsWith(".jar") || f.getName().endsWith(".zip") || (f.isDirectory() && !f.getName().startsWith("."))) {
                String fn = f.getName().lastIndexOf('.') == -1 ? f.getName() : f.getName().substring(0, f.getName().lastIndexOf('.'));
                // ignore the contribution if it has an associated exploded folder version
                if (!f.isDirectory() && new File(f.getParent(), fn).isDirectory()) {
                    continue;
                }
                String metaData = null;
                for (File f2 : directory.listFiles()) {
                    if (f2.getName().startsWith(fn) && f2.getName().endsWith(".xml")) {
                        metaData = f2.getPath();
                        break;
                    }
                }
                
                List<String> dependencyURIs = new ArrayList<String>();
                File dependencyFile = new File(directory, fn + ".dependencies");
                if (dependencyFile.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(dependencyFile));
                    String s;
                    while ((s = br.readLine()) != null)   {
                        if (!s.startsWith("#") && s.trim().length() > 0) {
                            dependencyURIs.addAll(Arrays.asList(s.trim().split("[ ,]+")));
                        }
                    }
                    br.close();
                }

                String curi = node.installContribution(null, f.getPath(), metaData, dependencyURIs);
                installed.add(curi);

                for (File f2 : directory.listFiles()) {
                    if (f2.getName().startsWith(fn) && f2.getName().endsWith(".composite")) {
                        node.addDeploymentComposite(curi, new FileReader(f2));
                    }
                }
            }
        }

        for (String curi : installed) {
            node.startDeployables(curi);
        }

        return node;
    }

    /* Node.xml hot update
     * - domain URi changed 
     *       - restart entire node
     * - List of contributions (matched on uri)
     *    - uninstall removed
     *    - install added
     *    - for each existing
     *        - if url or metadata or duris changed - update
     *        - if startdeployables changed
     *           - if now false then stop all started
     *           - if now true then start deployables
     *        
     *        
     *        
     */       
    
    /**
     * Creates a Node from an XML configuration file
     * @param configURL  the URL to the XML configuration file
     * @return Node  the configured Node
     */
    public Node createNodeFromXML(String configURL) throws ContributionReadException, ActivationException, ValidationException {
        NodeConfiguration configuration = loadConfiguration(configURL);
        NodeImpl node = (NodeImpl)createNode(configuration.getDomainURI());
        for ( ContributionConfiguration c : configuration.getContributions()) {
            String curi = node.installContribution(c.getURI(), c.getLocation(), c.getMetaDataURL(), c.getDependentContributionURIs());
            if (c.isStartDeployables()) {
                for (String compURI : node.getDeployableCompositeURIs(curi)) {
                    node.startComposite(curi, compURI);
                }
            }
        }
        return node;
    }

    /**
     * Stop the TuscanyRuntime
     */
    public void stop() {
        extensionPointRegistry.stop();
    }

    protected void init(Properties config) {
        if (config == null) {
            config = new Properties();
        }
        this.extensionPointRegistry = new DefaultExtensionPointRegistry();
        extensionPointRegistry.start();

        FactoryExtensionPoint modelFactories = extensionPointRegistry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = new RuntimeAssemblyFactory(extensionPointRegistry);
        modelFactories.addFactory(assemblyFactory);

        UtilityExtensionPoint utilities = extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class);
        this.compositeActivator = utilities.getUtility(CompositeActivator.class);
        this.deployer = utilities.getUtility(Deployer.class);
        utilities.getUtility(RuntimeProperties.class).setProperties(config);
        utilities.getUtility(WorkScheduler.class);

        // Initialize the Tuscany module activators
        // The module activators will be started
        extensionPointRegistry.getExtensionPoint(ModuleActivatorExtensionPoint.class);

        this.domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(extensionPointRegistry);

    }
    
    /**
     * Get the ExtensionPointRegistry used by this runtime
     * @return extensionPointRegistry
     */
    public ExtensionPointRegistry getExtensionPointRegistry() {
        return extensionPointRegistry;
    }
    
    /**
     * Get the Deployer. The Deployer can be used to create contribution artifacts 
     * when configuring a Node programatically.
     * @return the Deployer
     */
    public Deployer getDeployer() {
        return deployer;
    }
    
    /**
     * Get the AssemblyFactory. The AssemblyFactory can be used to create contribution
     * artifact contents when configuring a Node programatically.
     * @return the AssemblyFactory
     */
    public AssemblyFactory getAssemblyFactory() {
        return assemblyFactory;
    }

    protected NodeConfiguration loadConfiguration(String configURL) {
        InputStream xml =null;
        try {
            URL base = IOHelper.getLocationAsURL(configURL);
            xml = IOHelper.openStream(base);
            InputStreamReader reader = new InputStreamReader(xml, "UTF-8");
            ProcessorContext context = deployer.createProcessorContext();
            NodeConfiguration config = deployer.loadXMLDocument(reader, context.getMonitor());
            if (base != null && config != null) {
                // Resolve the contribution location against the node.xml
                // TODO: absolute locations?
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
        } finally {
            try {
                if (xml != null) xml.close();
            } catch (IOException e) {
                throw new ServiceRuntimeException(e);
            }
        }
    }

}
