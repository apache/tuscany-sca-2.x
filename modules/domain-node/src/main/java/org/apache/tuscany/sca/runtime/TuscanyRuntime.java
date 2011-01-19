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

package org.apache.tuscany.sca.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

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
import org.apache.tuscany.sca.core.assembly.impl.EndpointRegistryImpl;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.apache.tuscany.sca.runtime.impl.NodeImpl;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.ServiceRuntimeException;

import sun.security.jca.GetInstance;

public class TuscanyRuntime {

    private Deployer deployer;
    private ExtensionPointRegistry extensionPointRegistry;
    private CompositeActivator compositeActivator;
    private ExtensibleDomainRegistryFactory domainRegistryFactory;
    private RuntimeAssemblyFactory assemblyFactory;

    public static TuscanyRuntime newInstance() {
        return new TuscanyRuntime(null);
    }
    public static TuscanyRuntime newInstance(Properties config) {
        return new TuscanyRuntime(config);
    }

    /**
     * A helper method to run a standalone SCA composite 
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
     * A helper method to run a standalone SCA composite 
     * @param runtime a TuscanyRuntime instance which will be used to run the composite
     *                 this allows sharing a runtime instance to run multiple composites
     * @param compositeURI  URI within the contribution of a composite to run 
     *         if compositeURI is null then all deployable composites in the contribution will be run 
     * @param contributionURL  URL of the contribution
     * @param dependentContributionURLs  optional URLs of dependent contributions
     * @return a Node with installed contributions
     */
    public static Node runComposite(TuscanyRuntime runtime, String compositeURI, String contributionURL, String... dependentContributionURLs) {
        try {
            boolean sharedRuntime = runtime != null;
            if (runtime == null) {
                runtime = newInstance();
            }
            EndpointRegistry endpointRegistry = new EndpointRegistryImpl(runtime.extensionPointRegistry, null, null);
            NodeImpl node = new NodeImpl("default", runtime.deployer, runtime.compositeActivator, endpointRegistry, runtime.extensionPointRegistry, sharedRuntime? null : runtime);

            if (dependentContributionURLs != null) {
                for (int i=dependentContributionURLs.length-1; i>-1; i--) {
                    node.installContribution(null, dependentContributionURLs[i], null, null, false);
                }
            }

            String curi = node.installContribution(null, contributionURL, null, null, compositeURI == null);
            if (compositeURI != null) {
                node.start(curi, compositeURI);
            }
            return node;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected TuscanyRuntime(Properties config) {
        init(config);
    }
    
    public Node createNode() {
        return createNode(null);
    }

    public Node createNode(String domainURI) {
        String domainName = "default";
        if (domainURI != null){
            domainName = getDomainName(domainURI);
        }
        EndpointRegistry endpointRegistry = domainRegistryFactory.getEndpointRegistry(domainURI, domainName);
        return new NodeImpl(domainName, deployer, compositeActivator, endpointRegistry, extensionPointRegistry, null);
    }

    public Node createNodeFromXML(String configURL) throws ContributionReadException, ActivationException, ValidationException {
        NodeConfiguration configuration = loadConfiguration(configURL);
        Node node = createNode(configuration.getDomainURI());
        for ( ContributionConfiguration c : configuration.getContributions()) {
            node.installContribution(c.getURI(), c.getLocation(), c.getMetaDataURL(), c.getDependentContributionURIs(), c.isStartDeployables());
        }
        return node;
    }

    public void stop() {
        extensionPointRegistry.stop();
    }

    protected void init(Properties config) {
        if (config == null) {
            config = new Properties();
            config.setProperty("defaultScheme", "vm");
            config.setProperty("defaultDomainName", "default");
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

    private String getDomainName(String domainURI) {
        int scheme = domainURI.indexOf(':');
        int qm = domainURI.indexOf('?');
        if (qm == -1) {
            return domainURI.substring(scheme+1);
        } else {
            return domainURI.substring(scheme+1, qm);
        }
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
