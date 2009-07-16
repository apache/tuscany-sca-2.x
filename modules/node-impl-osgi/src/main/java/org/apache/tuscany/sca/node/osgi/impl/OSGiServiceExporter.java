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

import java.util.Collections;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.osgi.introspection.ExportedServiceIntrospector;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Watching and exporting OSGi services 
 */
public class OSGiServiceExporter implements ServiceTrackerCustomizer {
    private ExtensionPointRegistry registry;
    private BundleContext context;
    private ServiceTracker serviceTracker;
    private NodeFactoryImpl nodeFactory;
    private ExportedServiceIntrospector introspector;

    /**
     * @param context
     * @param clazz
     * @param customizer
     */
    public OSGiServiceExporter(BundleContext context) {
        this.context = context;
    }

    private synchronized void init() {
        if (nodeFactory == null) {
            this.nodeFactory = (NodeFactoryImpl)NodeFactory.newInstance();
            this.nodeFactory.init();
            this.introspector = new ExportedServiceIntrospector(getExtensionPointRegistry());
        }
    }

    public void start() {
        String filterStr = "(& (osgi.remote.configuration.type=sca) (osgi.remote.interfaces=*) (!(osgi.remote=true)) )";
        try {
            Filter filter = context.createFilter(filterStr);
            serviceTracker = new ServiceTracker(context, filter, this);
            serviceTracker.open(true);
        } catch (InvalidSyntaxException e) {
            // Ignore
        }
    }

    public void stop() {
        if (serviceTracker != null) {
            serviceTracker.close();
            serviceTracker = null;
        }
    }

    public Object addingService(ServiceReference reference) {
        init();
        try {
            Contribution contribution = introspector.introspect(reference);
            if (contribution != null) {

                NodeConfiguration configuration = nodeFactory.createNodeConfiguration();
                configuration.setURI(String.valueOf(reference.getProperty("service.id")));
                configuration.getExtensions().add(reference.getBundle());
                // FIXME: Configure the domain and node URI
                NodeImpl node = new NodeImpl(nodeFactory, configuration, Collections.singletonList(contribution));
                return node.start();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void modifiedService(ServiceReference reference, Object service) {
        Node node = (Node)service;
        node.stop();
        node.start();
    }

    public void removedService(ServiceReference reference, Object service) {
        Node node = (Node)service;
        node.stop();
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

}
