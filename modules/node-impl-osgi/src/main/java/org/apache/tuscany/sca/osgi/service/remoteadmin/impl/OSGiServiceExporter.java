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

package org.apache.tuscany.sca.osgi.service.remoteadmin.impl;

import static org.apache.tuscany.sca.osgi.service.remoteadmin.impl.EndpointHelper.createEndpointDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.service.remoteadmin.ExportRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Watching and exporting OSGi services 
 */
public class OSGiServiceExporter implements ServiceTrackerCustomizer, LifeCycleListener {
    private ExtensionPointRegistry registry;
    private BundleContext context;
    private NodeFactoryImpl nodeFactory;
    private EndpointIntrospector introspector;

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
            this.introspector = new EndpointIntrospector(context, getExtensionPointRegistry());
        }
    }

    public void start() {
        init();
    }

    public void stop() {
        this.introspector = null;
    }

    public Object addingService(ServiceReference reference) {
        return exportService(reference);
    }

    public List<ExportRegistration> exportService(ServiceReference reference) {
        try {
            Contribution contribution = introspector.introspect(reference);
            if (contribution != null) {

                NodeConfiguration configuration = nodeFactory.createNodeConfiguration();
                configuration.setURI(contribution.getURI());
                configuration.getExtensions().add(reference.getBundle());
                // FIXME: Configure the domain and node URI
                NodeImpl node = new NodeImpl(nodeFactory, configuration, Collections.singletonList(contribution));
                node.start();
                List<ExportRegistration> exportedServices = new ArrayList<ExportRegistration>();
                Component component = contribution.getDeployables().get(0).getComponents().get(0);
                ComponentService service = component.getServices().get(0);
                for (Endpoint endpoint : service.getEndpoints()) {
                    EndpointDescription endpointDescription = createEndpointDescription(context, endpoint);
                    ExportRegistration exportRegistration =
                        new ExportRegistrationImpl(node, reference, endpointDescription);
                    exportedServices.add(exportRegistration);
                }
                return exportedServices;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void modifiedService(ServiceReference reference, Object service) {
        removedService(reference, service);
        exportService(reference);
    }

    public void removedService(ServiceReference reference, Object service) {
        List<ExportRegistration> exportedServices = (List<ExportRegistration>)service;
        for(ExportRegistration exportRegistration: exportedServices) {
            exportRegistration.close();
        }
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
