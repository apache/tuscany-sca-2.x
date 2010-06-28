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

    // TODO: keep this method?
    public static Node createSection10() {
        return new NodeFactory().createSection10("default");
    }

    public NodeFactory() {
        init(null);
    }

    public NodeFactory(Properties config) {
        init(config);
    }

    public Node createSection10(String domainName) {
        EndpointRegistry endpointRegistry = domainRegistryFactory.getEndpointRegistry("default", domainName);
        return new NodeImpl(domainName, deployer, compositeActivator, endpointRegistry, extensionPointRegistry);
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
        AssemblyFactory assemblyFactory = new RuntimeAssemblyFactory(extensionPointRegistry);
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
//        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(extensionPointRegistry);
//        domainRegistryFactory.getEndpointRegistry(config.getProperty("reguri"), config.getProperty("defaultDomainName"));

    }
}
