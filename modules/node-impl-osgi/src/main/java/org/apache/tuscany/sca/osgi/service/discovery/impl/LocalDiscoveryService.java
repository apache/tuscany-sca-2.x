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
package org.apache.tuscany.sca.osgi.service.discovery.impl;

import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.BundleEvent.STARTED;
import static org.osgi.framework.BundleEvent.STOPPING;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescription;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteConstants;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.impl.EndpointHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;

public class LocalDiscoveryService extends AbstractDiscoveryService implements BundleListener {
    private StAXHelper staxHelper;
    private AssemblyFactory assemblyFactory;
    private StAXArtifactProcessor processor;

    public LocalDiscoveryService(BundleContext context) {
        super(context);
    }

    public void start() {
        super.start();
        context.addBundleListener(this);
        getExtensionPointRegistry();

        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        this.staxHelper = StAXHelper.getInstance(registry);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        UtilityExtensionPoint utilities = this.registry.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        Monitor monitor = null;
        if (monitorFactory != null) {
            monitor = monitorFactory.createMonitor();
        }
        processor =
            new ExtensibleStAXArtifactProcessor(processors, staxHelper.getInputFactory(),
                                                staxHelper.getOutputFactory());
        processExistingBundles();
    }

    public void bundleChanged(BundleEvent event) {
        try {
            switch (event.getType()) {
                case STARTED:
                    discover(event.getBundle());
                    break;
                case STOPPING:
                    removeServicesDeclaredInBundle(event.getBundle());
                    break;
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            if (e instanceof Error) {
                throw (Error)e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                // Should not happen
                throw new RuntimeException(e);
            }
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
        List<URL> urls = findServiceDescriptionsDocuments(b);
        if (urls == null || urls.isEmpty()) {
            return;
        }

        List<ServiceDescription> serviceDescriptions = new ArrayList<ServiceDescription>();

        for (URL url : urls) {
            ServiceDescriptions descriptions = null;
            try {
                descriptions = loadServiceDescriptions(url);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            if (descriptions != null) {
                serviceDescriptions.addAll(descriptions);
            }
        }

        for (ServiceDescription sd : serviceDescriptions) {
            EndpointDescription sed = createEndpointDescription(sd);
            servicesInfo.put(sed, b);
            serviceDescriptionAdded(sed);
        }
    }

    private EndpointDescription createEndpointDescription(ServiceDescription sd) {
        Map<String, Object> props = new HashMap<String, Object>(sd.getProperties());
        props.put(Constants.OBJECTCLASS, sd.getInterfaces().toArray(new String[sd.getInterfaces().size()]));
        if (!props.containsKey(RemoteConstants.SERVICE_REMOTE_ID)) {
            props.put(RemoteConstants.SERVICE_REMOTE_ID, String.valueOf(System.currentTimeMillis()));
        }
        if (!props.containsKey(RemoteConstants.SERVICE_REMOTE_FRAMEWORK_UUID)) {
            props.put(RemoteConstants.SERVICE_REMOTE_FRAMEWORK_UUID, EndpointHelper.getFrameworkUUID(context));
        }
        if (!props.containsKey(RemoteConstants.SERVICE_REMOTE_URI)) {
            props.put(RemoteConstants.SERVICE_REMOTE_URI, UUID.randomUUID().toString());
        }

        EndpointDescription sed = new EndpointDescription(props);
        return sed;
    }

    private List<URL> findServiceDescriptionsDocuments(Bundle b) {
        List<URL> urls = null;
        String path = (String)b.getHeaders().get(ServiceDescriptions.REMOTE_SERVICE_HEADER);
        if (path == null) {
            Enumeration<URL> files = b.findEntries(ServiceDescriptions.REMOTE_SERVICE_FOLDER, "*.xml", false);
            if (files == null || !files.hasMoreElements()) {
                return Collections.emptyList();
            } else {
                urls = new ArrayList<URL>();
                while (files.hasMoreElements()) {
                    urls.add(files.nextElement());
                }
            }
        } else {
            URL url = b.getEntry(path);
            if (url != null) {
                urls = Collections.singletonList(url);
            } else {
                urls = Collections.emptyList();
            }
        }
        return urls;
    }

    private ServiceDescriptions loadServiceDescriptions(URL url) throws Exception {
        InputStream is = IOHelper.openStream(url);
        try {
            XMLStreamReader reader = staxHelper.createXMLStreamReader(is);
            reader.nextTag();
            Object model = processor.read(reader, new ProcessorContext(registry));
            if (model instanceof ServiceDescriptions) {
                return (ServiceDescriptions)model;
            } else {
                return null;
            }
        } finally {
            is.close();
        }
    }

    private void removeServicesDeclaredInBundle(Bundle bundle) {
        for (Iterator<Map.Entry<EndpointDescription, Bundle>> i = servicesInfo.entrySet().iterator(); i.hasNext();) {
            Entry<EndpointDescription, Bundle> entry = i.next();
            if (entry.getValue().equals(bundle)) {
                serviceDescriptionRemoved(entry.getKey());
                i.remove();
            }
        }
    }

    private void serviceDescriptionAdded(EndpointDescription endpointDescription) {
        endpointChanged(endpointDescription, ADDED);
    }

    private void serviceDescriptionRemoved(EndpointDescription endpointDescription) {
        endpointChanged(endpointDescription, REMOVED);
    }

    public void stop() {
        context.removeBundleListener(this);
        super.stop();
    }

}
