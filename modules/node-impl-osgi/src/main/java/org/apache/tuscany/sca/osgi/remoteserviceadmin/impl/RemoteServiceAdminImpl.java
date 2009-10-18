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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.osgi.remoteserviceadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.ExportRegistration;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.ImportRegistration;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteServiceAdmin;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteServiceAdminEvent;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * SCA Implementation of {@link RemoteServiceAdmin}
 */
public class RemoteServiceAdminImpl implements RemoteServiceAdmin {
    private BundleContext context;
    private ServiceRegistration registration;
    private ServiceTracker listeners;

    private OSGiServiceExporter exporter;
    private OSGiServiceImporter importer;

    private Collection<ImportRegistration> importedEndpoints = new ArrayList<ImportRegistration>();
    private Collection<ExportRegistration> exportedServices = new ArrayList<ExportRegistration>();

    public RemoteServiceAdminImpl(BundleContext context) {
        this.context = context;
    }

    public void start() {
        this.exporter = new OSGiServiceExporter(context);
        this.importer = new OSGiServiceImporter(context);
        exporter.start();
        importer.start();
        registration = context.registerService(RemoteServiceAdmin.class.getName(), this, null);
        listeners = new ServiceTracker(this.context, RemoteServiceAdminListener.class.getName(), null);
        listeners.open();
    }

    public void stop() {
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
        if (listeners != null) {
            listeners.close();
            listeners = null;
        }
        for (ExportRegistration exportRegistration : exportedServices) {
            exportRegistration.close();
        }
        exportedServices.clear();
        for (ImportRegistration importRegistration : importedEndpoints) {
            importRegistration.close();
        }
        importedEndpoints.clear();
        if (importer != null) {
            importer.stop();
            importer = null;
        }
        if (exporter != null) {
            exporter.stop();
            exporter = null;
        }
    }

    /**
     * @see org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteServiceAdmin#exportService(org.osgi.framework.ServiceReference,
     *      java.util.Map)
     */
    public List<ExportRegistration> exportService(ServiceReference ref, Map properties) {
        List<ExportRegistration> exportRegistrations = exporter.exportService(ref, properties);
        if (exportRegistrations != null) {
            exportedServices.addAll(exportRegistrations);
            fireExportEvents(ref.getBundle(), exportRegistrations);
        }
        return exportRegistrations;
    }

    private void fireExportEvents(Bundle source, List<ExportRegistration> exportRegistrations) {
        for (ExportRegistration registration : exportRegistrations) {
            RemoteServiceAdminEvent rsaEvent =
                new RemoteServiceAdminEvent(RemoteServiceAdminEvent.EXPORT_REGISTRATION, source, registration,
                                            registration.getException());
            EventAdmin eventAdmin = getEventAdmin();
            if (eventAdmin != null) {
                eventAdmin.postEvent(wrap(rsaEvent));
            }
            for (Object listener : listeners.getServices()) {
                RemoteServiceAdminListener rsaListener = (RemoteServiceAdminListener)listener;
                rsaListener.remoteAdminEvent(rsaEvent);
            }
        }
    }

    private EventAdmin getEventAdmin() {
        ServiceReference reference = context.getServiceReference(EventAdmin.class.getName());
        if (reference == null) {
            return null;
        } else {
            return (EventAdmin)context.getService(reference);
        }
    }

    private Event wrap(RemoteServiceAdminEvent rsaEvent) {
        int type = rsaEvent.getType();
        String eventType = null;
        switch (type) {
            case RemoteServiceAdminEvent.EXPORT_ERROR:
                eventType = "EXPORT_ERROR";
                break;
            case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
                eventType = "EXPORT_REGISTRATION";
                break;
            case RemoteServiceAdminEvent.EXPORT_UNREGISTRATION:
                eventType = "EXPORT_UNREGISTRATION";
                break;
            case RemoteServiceAdminEvent.EXPORT_WARNING:
                eventType = "EXPORT_WARNING";
                break;
            case RemoteServiceAdminEvent.IMPORT_ERROR:
                eventType = "IMPORT_ERROR";
                break;
            case RemoteServiceAdminEvent.IMPORT_REGISTRATION:
                eventType = "IMPORT_REGISTRATION";
                break;
            case RemoteServiceAdminEvent.IMPORT_UNREGISTRATION:
                eventType = "EXPORT_ERROR";
                break;
            case RemoteServiceAdminEvent.IMPORT_WARNING:
                eventType = "IMPORT_UNREGISTRATION";
                break;
        }
        String topic = "org/osgi/service/remoteserviceadmin/" + eventType;
        Map<String, Object> props = new HashMap<String, Object>();
        /*
         * <ul>
        <li>bundle – (Bundle) The Remote Service Admin bundle
        <li>bundle-id – (Long) The id of the Blueprint bundle.
        <li>bundle-symbolicname – (String) The Bundle Symbolic Name of the
        Remote Service Admin bundle.
        <li>bundle-version - (Version) The version of the Blueprint bundle.
        <li>cause – The exception, if present.
        <li>import.registration – An imported endpoint, if present
        <li>export.registration – An exported endpoint, if present
        <li>service.remote.id – Remote service UUID, if present
        <li>service.remote.uuid – Remote service UUID, if present
        <li>service.remote.uri – (String) The URI of the endpoint, if present
        <li>objectClass – (String[]) The interface names, if present
        <li>service.imported.configs – (String+) The configuration types of the
        imported services, if present
        <li>timestamp – (Long) The time when the event occurred
        <li>event – (RemoteServiceAdminEvent) The RemoteServiceAdminEvent
        object that caused this event.
        </ul>
        */
        Bundle rsaBundle = context.getBundle();
        props.put("bundle", rsaBundle);
        props.put("bundle-id", rsaBundle.getBundleId());
        props.put("bundle-symbolicname", rsaBundle.getSymbolicName());
        props.put("bundle-version", rsaBundle.getHeaders().get(Constants.BUNDLE_VERSION));
        props.put("cause", rsaEvent.getException());
        props.put("import.registration", rsaEvent.getImportRegistration());
        props.put("export.registration", rsaEvent.getExportRegistration());
        EndpointDescription ep = null;
        if (rsaEvent.getImportRegistration() != null) {
            ep = rsaEvent.getImportRegistration().getImportedEndpointDescription();
        } else {
            ep = rsaEvent.getExportRegistration().getEndpointDescription();
        }
        props.put("service.remote.id", ep.getRemoteServiceID());
        props.put("service.remote.uuid", ep.getRemoteFrameworkUUID());
        props.put("service.remote.uri", ep.getRemoteURI());
        props.put("objectClass", ep.getInterfaces());
        props.put("service.imported.configs", ep.getConfigurationTypes());
        props.put("timestamp", new Long(System.currentTimeMillis()));
        props.put("event", rsaEvent);
        return new Event(topic, props);
    }

    private void fireImportEvents(Bundle source, ImportRegistration registration) {
        RemoteServiceAdminEvent rsaEvent =
            new RemoteServiceAdminEvent(RemoteServiceAdminEvent.IMPORT_REGISTRATION, source, registration, registration
                .getException());
        EventAdmin eventAdmin = getEventAdmin();
        if (eventAdmin != null) {
            eventAdmin.postEvent(wrap(rsaEvent));
        }
        for (Object listener : listeners.getServices()) {
            RemoteServiceAdminListener rsaListener = (RemoteServiceAdminListener)listener;
            rsaListener.remoteAdminEvent(rsaEvent);
        }
    }

    /**
     * @see org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteServiceAdmin#getExportedServices()
     */
    public Collection<ExportRegistration> getExportedServices() {
        return exportedServices;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteServiceAdmin#getImportedEndpoints()
     */
    public Collection<ImportRegistration> getImportedEndpoints() {
        return importedEndpoints;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteServiceAdmin#importService(org.apache.tuscany.sca.dosgi.discovery.EndpointDescription)
     */
    public ImportRegistration importService(EndpointDescription endpoint) {
        Bundle bundle = (Bundle)endpoint.getProperties().get(Bundle.class.getName());
        ImportRegistration importReg = importer.importService(bundle, endpoint);
        if (importReg != null) {
            fireImportEvents(bundle, importReg);
            importedEndpoints.add(importReg);
        }
        return importReg;
    }
}
