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

package org.apache.tuscany.sca.something;

import java.util.Properties;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.something.impl.Section10Impl;

public class Section10Factory {

    private NodeFactoryImpl nodeFactory;
    private Deployer deployer;
    private ExtensionPointRegistry extensionPointRegistry;
    private MonitorFactory monitorFactory;
    private CompositeActivator compositeActivator;
    private ExtensibleDomainRegistryFactory domainRegistryFactory;

    public static Section10 createSection10() {
        return new Section10Factory().createSection10("default");
    }

    public Section10Factory() {
        init(null);
    }
    
    public Section10Factory(Properties config) {
        init(config);
    }
    
    public Section10 createSection10(String domainName) {
        EndpointRegistry endpointRegistry = domainRegistryFactory.getEndpointRegistry("default", domainName);
        return new Section10Impl(domainName, deployer, monitorFactory, compositeActivator, endpointRegistry, extensionPointRegistry);
    }
    
    public void shutdown() {
        nodeFactory.destroy();
    }

    protected void init(Properties config) {
        if (config == null) {
            config = new Properties();
            config.setProperty("defaultScheme", "vm");
            config.setProperty("defaultDomainName", "default");
        }
        this.nodeFactory = (NodeFactoryImpl)NodeFactory.newInstance(config);
        nodeFactory.start();
        this.deployer = nodeFactory.getDeployer();
        this.extensionPointRegistry = nodeFactory.getExtensionPointRegistry();
        UtilityExtensionPoint utilities = extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class);
        this.monitorFactory = utilities.getUtility(MonitorFactory.class);
        this.compositeActivator = utilities.getUtility(CompositeActivator.class);
        this.domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(extensionPointRegistry);
    }
}
