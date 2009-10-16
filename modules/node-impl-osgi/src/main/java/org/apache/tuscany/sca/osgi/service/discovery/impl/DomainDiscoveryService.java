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

import static org.apache.tuscany.sca.osgi.service.remoteadmin.impl.EndpointHelper.createEndpointDescription;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.osgi.framework.BundleContext;

/**
 * Discovery service based on the distributed SCA domain
 */
public class DomainDiscoveryService extends AbstractDiscoveryService implements EndpointListener {
    private DomainRegistryFactory domainRegistryFactory;

    public DomainDiscoveryService(BundleContext context) {
        super(context);
    }

    public void start() {
        super.start();
        getExtensionPointRegistry();
        this.domainRegistryFactory =
            registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(DomainRegistryFactory.class);
        domainRegistryFactory.addListener(this);
    }

    public void endpointAdded(Endpoint endpoint) {
        Implementation impl = endpoint.getComponent().getImplementation();
        if (!(impl instanceof OSGiImplementation)) {
            return;
        }

        OSGiImplementation osgiImpl = (OSGiImplementation)impl;
        BundleContext bundleContext = osgiImpl.getBundle().getBundleContext();

        /*
        if (!endpoint.isRemote()) {
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

            }
        } else
        */ 
        {
            // Notify the endpoint listeners
            EndpointDescription description = createEndpointDescription(bundleContext, endpoint);
            endpointChanged(description, ADDED);
        }
    }

    public void endpointRemoved(Endpoint endpoint) {
        /*
        if (!endpoint.isRemote()) {
            // export services
        } else
        */ 
        {
            EndpointDescription description = createEndpointDescription(context, endpoint);
            endpointChanged(description, REMOVED);
        }
    }

    public void endpointUpdated(Endpoint oldEndpoint, Endpoint newEndpoint) {
        // FIXME: This is a quick and dirty way for the update
        endpointRemoved(oldEndpoint);
        endpointAdded(newEndpoint);
    }

    public void stop() {
        domainRegistryFactory.removeListener(this);
        super.stop();
    }

}
