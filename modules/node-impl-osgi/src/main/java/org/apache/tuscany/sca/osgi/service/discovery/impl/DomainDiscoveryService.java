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

package org.apache.tuscany.sca.osgi.service.discovery.impl;

import static org.apache.tuscany.sca.osgi.remoteserviceadmin.impl.EndpointHelper.createEndpointDescription;

import java.util.Dictionary;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.remoteserviceadmin.EndpointDescription;

/**
 * Discovery service based on the distributed SCA domain
 */
public class DomainDiscoveryService extends AbstractDiscoveryService implements EndpointListener {
    private DomainRegistryFactory domainRegistryFactory;
    private EndpointRegistry endpointRegistry;

    public DomainDiscoveryService(BundleContext context) {
        super(context);
    }

    public void start() {
        super.start();
        this.domainRegistryFactory = new ExtensibleDomainRegistry(registry);
        domainRegistryFactory.addListener(this);

        // [rfeng] Starting of the endpoint registry takes a long time and it leaves the bundle
        // state to be starting. When the registry is started, remote endpoints come in and that
        // triggers the classloading from this bundle.
        Thread thread = new Thread() {
            public void run() {
                startEndpointRegistry();
            }
        };
        thread.start();
    }

    private synchronized void startEndpointRegistry() {
        // The following code forced the start() of the domain registry in absense of services
        String domainRegistry = context.getProperty("org.osgi.sca.domain.registry");
        if (domainRegistry == null) {
            domainRegistry = NodeConfiguration.DEFAULT_DOMAIN_REGISTRY_URI;
        }
        String domainURI = context.getProperty("org.osgi.sca.domain.uri");
        if (domainURI == null) {
            domainURI = NodeConfiguration.DEFAULT_DOMAIN_URI;
        }
        if (domainRegistry != null) {
            endpointRegistry = domainRegistryFactory.getEndpointRegistry(domainRegistry, domainURI);
        }
    }

    public void endpointAdded(Endpoint endpoint) {
        Implementation impl = endpoint.getComponent().getImplementation();
        if (!(impl instanceof OSGiImplementation)) {
            return;
        }

        BundleContext bundleContext = null;
        // Remote endpoint doesn't have a bundle
        if (!endpoint.isRemote()) {
            OSGiImplementation osgiImpl = (OSGiImplementation)impl;
            Bundle bundle = osgiImpl.getBundle();
            bundleContext = bundle != null ? bundle.getBundleContext() : null;
        }

        // Notify the endpoint listeners
        EndpointDescription description = createEndpointDescription(bundleContext, endpoint);
        // Set the owning bundle to runtime bundle to avoid NPE
        synchronized (this) {
            endpointDescriptions.put(description, context.getBundle());
            endpointChanged(description, ADDED);
        }
    }

    public void endpointRemoved(Endpoint endpoint) {
        EndpointDescription description = createEndpointDescription(context, endpoint);
        synchronized (this) {
            endpointDescriptions.remove(description);
            endpointChanged(description, REMOVED);
        }
    }

    public void endpointUpdated(Endpoint oldEndpoint, Endpoint newEndpoint) {
        // FIXME: This is a quick and dirty way for the update
        endpointRemoved(oldEndpoint);
        endpointAdded(newEndpoint);
    }

    public void stop() {
        if (domainRegistryFactory != null) {
            domainRegistryFactory.removeListener(this);
            if (endpointRegistry instanceof LifeCycleListener) {
                ((LifeCycleListener)endpointRegistry).stop();
            }
            domainRegistryFactory = null;
            endpointRegistry = null;
            super.stop();
        }
    }

    @Override
    protected Dictionary<String, Object> getProperties() {
        Dictionary<String, Object> props = super.getProperties();
        props.put(SUPPORTED_PROTOCOLS, new String[] {"org.osgi.sca"});
        return props;
    }

}
