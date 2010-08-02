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
import org.apache.tuscany.sca.core.assembly.impl.EndpointRegistryImpl;
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

    public static NodeFactory newInstance() {
        return new NodeFactory(null);
    }
    public static NodeFactory newInstance(Properties config) {
        return new NodeFactory(config);
    }

    /**
     * A helper method to simplify creating a standalone Node 
     * @param compositeURI  URI within the contribution of a composite to run 
     *         if compositeURI is null then all deployable composites in the contribution will be run 
     * @param contributionURL  URL of the contribution
     * @param dependentContributionURLs  optional URLs of dependent contributions
     * @return a Node with installed contributions
     */
    public static Node createStandaloneNode(String compositeURI, String contributionURL, String... dependentContributionURLs) {
        try {
            NodeFactory nodeFactory = newInstance();
            EndpointRegistry endpointRegistry = new EndpointRegistryImpl(nodeFactory.extensionPointRegistry, null, null);
            NodeImpl node = new NodeImpl("default", nodeFactory.deployer, nodeFactory.compositeActivator, endpointRegistry, nodeFactory.extensionPointRegistry, nodeFactory);

            for (int i=dependentContributionURLs.length-1; i>-1; i--) {
                node.installContribution(null, dependentContributionURLs[i], null, null, false);
            }

            String curi = node.installContribution(null, contributionURL, null, null, compositeURI == null);
            if (compositeURI != null) {
                node.addToDomainLevelComposite(curi, compositeURI);
            }
            return node;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected NodeFactory(Properties config) {
        init(config);
    }

    public Node createNode(String domainURI) {
        String domainName = getDomainName(domainURI);
        EndpointRegistry endpointRegistry = domainRegistryFactory.getEndpointRegistry(domainURI, domainName);
        return new NodeImpl(domainName, deployer, compositeActivator, endpointRegistry, extensionPointRegistry, null);
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

    private String getDomainName(String domainURI) {
        int scheme = domainURI.indexOf(':');
        int qm = domainURI.indexOf('?');
        if (qm == -1) {
            return domainURI.substring(scheme+1);
        } else {
            return domainURI.substring(scheme+1, qm);
        }
    }
}
