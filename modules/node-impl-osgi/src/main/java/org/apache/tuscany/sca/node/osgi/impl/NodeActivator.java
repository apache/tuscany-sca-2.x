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

import static org.apache.tuscany.sca.node.osgi.impl.NodeManager.isSCABundle;

import org.apache.tuscany.sca.osgi.remoteserviceadmin.impl.TopologyManagerImpl;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.impl.RemoteServiceAdminImpl;
import org.apache.tuscany.sca.osgi.service.discovery.impl.DiscoveryActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

/**
 * Bundle activator to receive the BundleContext
 */
public class NodeActivator implements BundleActivator, SynchronousBundleListener {
    private static BundleContext bundleContext;
    private boolean inited;
    private NodeManager manager;
    
    private DiscoveryActivator discoveryActivator = new DiscoveryActivator();
    private RemoteServiceAdminImpl remoteAdmin;
    private TopologyManagerImpl controller;

    private void init() {
        synchronized (this) {
            if (inited) {
                return;
            }
            manager = new NodeManager(bundleContext);
            manager.start();
            bundleContext.addBundleListener(manager);
            inited = true;
        }
    }

    public void start(BundleContext context) throws Exception {
        bundleContext = context;

        // FIXME: We should try to avoid aggressive initialization
        init();
        
        remoteAdmin = new RemoteServiceAdminImpl(context);
        remoteAdmin.start();
        
        controller = new TopologyManagerImpl(context);
        controller.start();
        
//        exporter = new OSGiServiceExporter(context);
//        exporter.start();
        
        discoveryActivator.start(context);
        
        boolean found = false;
        for (Bundle b : context.getBundles()) {
            if (isSCABundle(b)) {
                found = true;
                break;
            }
        }

        if (found) {
            init();
        } else {
            context.addBundleListener(this);
        }
    }

    public void stop(BundleContext context) throws Exception {
        context.removeBundleListener(this);
        controller.stop();
        controller = null;

        discoveryActivator.stop(context);
        discoveryActivator = null;
        
        remoteAdmin.stop();
        remoteAdmin = null;
        
        manager.stop();
        bundleContext.removeBundleListener(manager);
        manager = null;
        bundleContext = null;
        inited = false;
    }

    public static BundleContext getBundleContext() {
        return bundleContext;
    }

    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.STARTING) {
            if (isSCABundle(event.getBundle())) {
                bundleContext.removeBundleListener(this);
                init();
            }
        }

    }

}
