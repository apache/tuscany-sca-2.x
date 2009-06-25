/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements. See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership. The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied. See the License for the
  * specific language governing permissions and limitations
  * under the License.
  */
package org.apache.tuscany.sca.dosgi.discovery;

import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.BundleEvent.STARTED;
import static org.osgi.framework.BundleEvent.STOPPING;
import static org.osgi.service.discovery.DiscoveredServiceNotification.AVAILABLE;
import static org.osgi.service.discovery.DiscoveredServiceNotification.UNAVAILABLE;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.discovery.ServiceEndpointDescription;

public class LocalDiscoveryService extends AbstractDiscoveryService implements BundleListener {
    private ExtensionPointRegistry registry;

    public LocalDiscoveryService(BundleContext context) {
        super(context);
        context.addBundleListener(this);
        this.registry = getExtensionPointRegistry();
        processExistingBundles();
    }

    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case STARTED:
                discover(event.getBundle());
                break;
            case STOPPING:
                removeServicesDeclaredInBundle(event.getBundle());
                break;
        }
    }

    private void processExistingBundles() {
        Bundle[] bundles = context.getBundles();
        if (bundles == null) {
            return;
        }

        for (Bundle b : bundles) {
            if (b.getState() == ACTIVE) {
                discover(b);
            }
        }
    }

    private void discover(Bundle b) {
        String path = (String)b.getHeaders().get(ServiceDescriptions.REMOTE_SERVICE_HEADER);
        if (path == null) {
            Enumeration files = b.findEntries(ServiceDescriptions.REMOTE_SERVICE_FOLDER, "*.xml", false);
            if (files == null || !files.hasMoreElements()) {
                return;
            }
        }

        ServiceDescriptions descriptions = null;

        // TODO: Use SCA contribution to load the service discription files
        List<ServiceEndpointDescription> refs = Collections.emptyList();
        for (ServiceEndpointDescription sed : refs) {
            servicesInfo.put(sed, b);
            serviceDescriptionAdded(sed);
        }
        throw new RuntimeException("To be implemented");
    }

    private void removeServicesDeclaredInBundle(Bundle bundle) {
        for (Iterator<Map.Entry<ServiceEndpointDescription, Bundle>> i = servicesInfo.entrySet().iterator(); i
            .hasNext();) {
            Entry<ServiceEndpointDescription, Bundle> entry = i.next();
            if (entry.getValue().equals(bundle)) {
                serviceDescriptionRemoved(entry.getKey());
                i.remove();
            }
        }
    }

    private void serviceDescriptionAdded(ServiceEndpointDescription sd) {
        discoveredServiceChanged(sd, AVAILABLE);
    }

    private void serviceDescriptionRemoved(ServiceEndpointDescription sd) {
        discoveredServiceChanged(sd, UNAVAILABLE);
    }

    public void stop() {
        context.removeBundleListener(this);
        super.stop();
    }

}
