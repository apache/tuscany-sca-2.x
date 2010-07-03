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

package org.apache.tuscany.sca.node2;

import java.util.Properties;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.node2.impl.NodeImpl;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.apache.tuscany.sca.work.WorkScheduler;

public class NodeFactory {

    private Deployer deployer;
    private ExtensionPointRegistry extensionPointRegistry;
    private CompositeActivator compositeActivator;
    private ExtensibleDomainRegistryFactory domainRegistryFactory;
    private RuntimeAssemblyFactory assemblyFactory;

    /**
     * A helper method to simplify creating a Node with an installed contributions
     * @param compositeURI  URI of a composite to run relative to the first contribution
     *         if compositeURI is null then all deployable composites in the first contribution will be run 
     * @param contributionURLs  URLs to contributions to install
     * @return a Node with installed contributions
     */
    public static Node createNode(String compositeURI, String... contributionURLs) {
        try {
            
            Node node = newInstance().createOneoffNode();
            String uri = "";
            for (int i=contributionURLs.length-1; i>-1; i--) {
                boolean runDeployables = (i==0) && (compositeURI == null);
                int lastDot = contributionURLs[i].lastIndexOf('.');
                int lastSep = contributionURLs[i].lastIndexOf("/");
                if (lastDot > -1 && lastSep > -1 && lastDot > lastSep) {
                    uri = contributionURLs[i].substring(lastSep+1, lastDot);
                } else {
                    uri = contributionURLs[i];
                }

                node.installContribution(uri, contributionURLs[i], null, null, runDeployables);
            }
            if (compositeURI != null) {
                if (uri.endsWith("/")) {
                    uri = uri + compositeURI;
                } else {
                    uri = uri + "/" + compositeURI;
                }
                node.addToDomainLevelComposite(uri);
            }
            return node;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static NodeFactory newInstance() {
        return new NodeFactory(null);
    }
    public static NodeFactory newInstance(Properties config) {
        return new NodeFactory(config);
    }

    protected NodeFactory(Properties config) {
        init(config);
    }

    public Node createNode(String domainURI) {
        EndpointRegistry endpointRegistry = domainRegistryFactory.getEndpointRegistry("default", domainURI);
        return new NodeImpl(domainURI, deployer, compositeActivator, endpointRegistry, extensionPointRegistry, null);
    }

    protected Node createOneoffNode() {
        EndpointRegistry endpointRegistry = domainRegistryFactory.getEndpointRegistry("default", "default");
        return new NodeImpl("default", deployer, compositeActivator, endpointRegistry, extensionPointRegistry, this);
    }

    public void stop() {
        deployer.stop();
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
}
