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

package org.apache.tuscany.sca.dosgi.discovery;

import static org.osgi.service.discovery.DiscoveredServiceNotification.AVAILABLE;
import static org.osgi.service.discovery.DiscoveredServiceNotification.UNAVAILABLE;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.ServiceEndpointDescription;

/**
 * Discovery service based on the distributed SCA domain
 */
public class DomainDiscoveryService extends AbstractDiscoveryService implements EndpointListener {
    private EndpointRegistry endpointRegistry;

    public DomainDiscoveryService(BundleContext context) {
        super(context);

        ExtensionPointRegistry registry = getExtensionPointRegistry();
        this.endpointRegistry =
            registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(EndpointRegistry.class);
        this.endpointRegistry.addListener(this);
    }

    public void endpointAdded(Endpoint endpoint) {
        Implementation impl = endpoint.getComponent().getImplementation();
        if (!(impl instanceof OSGiImplementation)) {
            return;
        }

        OSGiImplementation osgiImpl = (OSGiImplementation)impl;
        BundleContext bundleContext = osgiImpl.getBundle().getBundleContext();

        boolean local = true;
        if (local) {

            Interface intf = endpoint.getService().getInterfaceContract().getInterface();
            JavaInterface javaInterface = (JavaInterface)intf;
            // String filter = getOSGiFilter(provider.getOSGiProperties(service));
            // FIXME: What is the filter?
            String filter = "(!(sca.reference=*))";
            // "(sca.service=" + component.getURI() + "#service-name\\(" + service.getName() + "\\))";
            ServiceReference ref = null;
            try {
                ref = bundleContext.getServiceReferences(javaInterface.getName(), filter)[0];
            } catch (InvalidSyntaxException e) {
                // Ignore
            }
            if (ref != null) {
                localServicePublished(ref, endpoint);
            }
        } else {
            // Remote endpoints
            ServiceEndpointDescription description = getServiceEndpointDescription(endpoint);
            discoveredServiceChanged(description, AVAILABLE);
        }
    }

    private boolean isLocal(Endpoint endpoint) {
        // FIXME: To be implemented
        return true;
    }

    public void endpointRemoved(Endpoint endpoint) {
        if (isLocal(endpoint)) {
            // unregister the ServicePublication here
        } else {
            // Remote endpoints
            ServiceEndpointDescription description = getServiceEndpointDescription(endpoint);
            discoveredServiceChanged(description, UNAVAILABLE);
        }
    }

    public void endpointUpdated(Endpoint oldEndpoint, Endpoint newEndpoint) {
    }

    public void stop() {
        endpointRegistry.removeListener(this);
        super.stop();
    }

    private ServiceEndpointDescription getServiceEndpointDescription(Endpoint endpoint) {
        return null;
    }
}
