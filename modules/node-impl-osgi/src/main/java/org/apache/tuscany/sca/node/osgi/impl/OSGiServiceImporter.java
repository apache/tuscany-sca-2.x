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

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.service.remoteadmin.ImportRegistration;
import org.apache.tuscany.sca.osgi.service.remoteadmin.impl.EndpointIntrospector;
import org.apache.tuscany.sca.osgi.service.remoteadmin.impl.ImportRegistrationImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Watching and exporting OSGi services 
 */
public class OSGiServiceImporter implements LifeCycleListener {
    private ExtensionPointRegistry registry;
    private BundleContext context;
    private NodeFactoryImpl nodeFactory;
    private EndpointIntrospector introspector;

    /**
     * @param context
     * @param clazz
     * @param customizer
     */
    public OSGiServiceImporter(BundleContext context) {
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
    }

    public void stop() {
    }

    public ImportRegistration importService(Bundle bundle, EndpointDescription endpointDescription) {
        init();
        try {
            Contribution contribution = introspector.introspect(bundle, endpointDescription);
            if (contribution != null) {

                NodeConfiguration configuration = nodeFactory.createNodeConfiguration();
                configuration.setURI("osgi.reference." + endpointDescription.getURI());
                configuration.getExtensions().add(bundle);
                // FIXME: Configure the domain and node URI
                NodeImpl node = new NodeImpl(nodeFactory, configuration, Collections.singletonList(contribution));
                node.start();

                Component component = contribution.getDeployables().get(0).getComponents().get(0);
                ComponentReference componentReference = component.getReferences().get(0);
                ServiceReference serviceReference =
                    context.getServiceReference("(sca.reference=" + component.getURI()
                        + "#reference("
                        + componentReference.getName()
                        + ")");
                return new ImportRegistrationImpl(node, serviceReference, endpointDescription);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void unimportService(ImportRegistration importRegistration) {
        Node node = (Node)importRegistration.getImportedService().getProperty("sca.node");
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
