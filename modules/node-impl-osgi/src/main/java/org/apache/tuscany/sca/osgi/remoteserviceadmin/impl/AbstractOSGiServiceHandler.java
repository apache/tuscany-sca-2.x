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

package org.apache.tuscany.sca.osgi.remoteserviceadmin.impl;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.osgi.service.discovery.impl.LocalDiscoveryService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Watching and exporting OSGi services 
 */
public class AbstractOSGiServiceHandler implements LifeCycleListener {
    protected ExtensionPointRegistry registry;
    protected BundleContext context;
    protected NodeFactoryImpl nodeFactory;
    protected EndpointIntrospector introspector;
    protected ServiceTracker discoveryTracker;
    protected String domainRegistry;

    /**
     * @param context
     * @param clazz
     * @param customizer
     */
    protected AbstractOSGiServiceHandler(BundleContext context) {
        this.context = context;
    }

    protected ExtensionPointRegistry getExtensionPointRegistry() {
        if (registry == null) {
            ServiceTracker tracker = new ServiceTracker(context, ExtensionPointRegistry.class.getName(), null);
            tracker.open();
            // tracker.waitForService(1000);
            registry = (ExtensionPointRegistry)tracker.getService();
            tracker.close();
        }
        return registry;
    }

    protected synchronized void init() {
        if (nodeFactory == null) {
            this.nodeFactory = (NodeFactoryImpl)NodeFactory.newInstance();
            this.nodeFactory.init();
            this.discoveryTracker = LocalDiscoveryService.getTracker(context);
            discoveryTracker.open();
            this.introspector = new EndpointIntrospector(context, getExtensionPointRegistry(), discoveryTracker);
        }
    }

    public void start() {
        init();
    }

    public void stop() {
        discoveryTracker.close();
        discoveryTracker = null;
        introspector = null;
        nodeFactory = null;
        registry = null;
        context = null;
    }

    public void setDomainRegistry(String domainRegistry) {
        this.domainRegistry = domainRegistry;
    }

}
