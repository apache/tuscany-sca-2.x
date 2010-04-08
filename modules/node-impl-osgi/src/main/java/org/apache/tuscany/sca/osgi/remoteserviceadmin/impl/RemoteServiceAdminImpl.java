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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.Intent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.util.tracker.ServiceTracker;

/**
 * SCA Implementation of {@link RemoteServiceAdmin}
 */
public class RemoteServiceAdminImpl implements RemoteServiceAdmin, ManagedService {
    private BundleContext context;
    private ServiceRegistration registration;
    private ServiceRegistration managedService;
    private ServiceTracker listeners;

    private OSGiServiceExporter exporter;
    private OSGiServiceImporter importer;

    private Collection<ImportRegistration> importRegistrations = new ArrayList<ImportRegistration>();
    private Collection<ExportRegistration> exportedRegistrations = new ArrayList<ExportRegistration>();

    public RemoteServiceAdminImpl(BundleContext context) {
        this.context = context;
    }

    public void start() {
        this.exporter = new OSGiServiceExporter(context);
        this.importer = new OSGiServiceImporter(context);
        exporter.start();
        importer.start();
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(RemoteConstants.REMOTE_CONFIGS_SUPPORTED, new String[] {"org.osgi.sca"});
        
        ExtensionPointRegistry registry = exporter.getExtensionPointRegistry();
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        Deployer deployer = utilities.getUtility(Deployer.class);
        Definitions definitions = deployer.getSystemDefinitions();

        String[] intents = new String[definitions.getIntents().size()];
        int i = 0;
        for (Intent intent : definitions.getIntents()) {
            intents[i++] = intent.toString();
        }

        String[] bindingTypes = new String[definitions.getBindingTypes().size()];
        i = 0;
        for (BindingType bindingType : definitions.getBindingTypes()) {
            bindingTypes[i++] = bindingType.getType().toString();
        }
        
        // FIXME: We should ask SCA domain for the supported intents
        props.put(RemoteConstants.REMOTE_INTENTS_SUPPORTED, intents);
        // FIXME: We should ask SCA domain for the supported binding types
        props.put("org.osgi.sca.binding.types", bindingTypes);
        registration = context.registerService(RemoteServiceAdmin.class.getName(), this, props);
        
        props = new Hashtable<String, Object>();
        props.put(Constants.SERVICE_PID, RemoteServiceAdminImpl.class.getName());
        managedService = context.registerService(ManagedService.class.getName(), this, props);
        listeners = new ServiceTracker(this.context, RemoteServiceAdminListener.class.getName(), null);
        listeners.open();
    }

    public void stop() {
        if (registration != null) {
            try {
                registration.unregister();
            } catch (IllegalStateException e) {
                // The service has been unregistered, ignore it
            }
            registration = null;
        }
        if (managedService != null) {
            try {
                managedService.unregister();
            } catch (IllegalStateException e) {
                // The service has been unregistered, ignore it
            }
            managedService = null;
        }
        if (listeners != null) {
            listeners.close();
            listeners = null;
        }
        for (ExportRegistration exportRegistration : exportedRegistrations) {
            exportRegistration.close();
        }
        exportedRegistrations.clear();
        for (ImportRegistration importRegistration : importRegistrations) {
            importRegistration.close();
        }
        importRegistrations.clear();
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
     * @see org.osgi.remoteserviceadmin.RemoteServiceAdmin#exportService(org.osgi.framework.ServiceReference,
     *      java.util.Map)
     */
    public List<ExportRegistration> exportService(ServiceReference ref, Map properties) {
        List<ExportRegistration> exportRegistrations = exporter.exportService(ref, properties);
        if (exportRegistrations != null) {
            exportRegistrations.addAll(exportedRegistrations);
            fireExportEvents(ref.getBundle(), exportRegistrations);
        }
        return exportRegistrations;
    }

    private void fireExportEvents(Bundle source, List<ExportRegistration> exportRegistrations) {
        for (ExportRegistration registration : exportRegistrations) {
            RemoteServiceAdminEvent rsaEvent =
                new RemoteServiceAdminEvent(RemoteServiceAdminEvent.EXPORT_REGISTRATION, source, registration
                    .getExportReference(), registration.getException());
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
        props.put("import.registration", rsaEvent.getImportReference());
        props.put("export.registration", rsaEvent.getExportReference());
        EndpointDescription ep = null;
        if (rsaEvent.getImportReference() != null) {
            ep = rsaEvent.getImportReference().getImportedEndpoint();
        } 
        if (rsaEvent.getExportReference() != null) {
            ep = rsaEvent.getExportReference().getExportedEndpoint();
        }
        
        if (ep != null) {
            props.put("endpoint.service.id", ep.getServiceId());
            props.put("endpoint.framework.uuid", ep.getFrameworkUUID());
            props.put("endpoint.id", ep.getId());
            props.put("objectClass", ep.getInterfaces());
            props.put("service.imported.configs", ep.getConfigurationTypes());
            Object bindings = ep.getProperties().get("org.osgi.sca.bindings");
            if (bindings != null) {
                props.put("org.osgi.sca.bindings", bindings);
            }
        }
        props.put("timestamp", Long.valueOf(System.currentTimeMillis()));
        props.put("event", rsaEvent);
        return new Event(topic, props);
    }

    private void fireImportEvents(Bundle source, ImportRegistration registration) {
        RemoteServiceAdminEvent rsaEvent =
            new RemoteServiceAdminEvent(RemoteServiceAdminEvent.IMPORT_REGISTRATION, source, registration
                .getImportReference(), registration.getException());
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
     * @see org.osgi.remoteserviceadmin.RemoteServiceAdmin#getExportedServices()
     */
    public Collection<ExportReference> getExportedServices() {
        Collection<ExportReference> exportedServices = new ArrayList<ExportReference>();
        for (ExportRegistration registration : exportedRegistrations) {
            exportedServices.add(registration.getExportReference());
        }
        return exportedServices;
    }

    /**
     * @see org.osgi.remoteserviceadmin.RemoteServiceAdmin#getImportedEndpoints()
     */
    public Collection<ImportReference> getImportedEndpoints() {
        Collection<ImportReference> importedEndpoints = new ArrayList<ImportReference>();
        for (ImportRegistration registration : importRegistrations) {
            importedEndpoints.add(registration.getImportReference());
        }
        return importedEndpoints;
    }

    /**
     * @see org.osgi.remoteserviceadmin.RemoteServiceAdmin#importService(org.apache.tuscany.sca.dosgi.discovery.EndpointDescription)
     */
    public ImportRegistration importService(EndpointDescription endpoint) {
        Bundle bundle = (Bundle)endpoint.getProperties().get(Bundle.class.getName());
        ImportRegistration importReg = importer.importService(bundle, endpoint);
        if (importReg != null) {
            fireImportEvents(bundle, importReg);
            importRegistrations.add(importReg);
        }
        return importReg;
    }

    public synchronized void updated(Dictionary props) throws ConfigurationException {
        if (props == null) {
            // It can be null in Apache Felix
            return;
        }
        String domainRegistry = (String)props.get("org.osgi.sca.domain.registry");
        String domainURI = (String)props.get("org.osgi.sca.domain.uri");
        if (domainRegistry != null) {
            exporter.setDomainRegistry(domainRegistry);
            importer.setDomainRegistry(domainRegistry);
        }
        if (domainURI != null) {
            exporter.setDomainURI(domainURI);
            importer.setDomainURI(domainURI);
        }
    }
}
