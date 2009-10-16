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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.service.remoteadmin.ExportRegistration;
import org.apache.tuscany.sca.osgi.service.remoteadmin.ImportRegistration;
import org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteServiceAdmin;
import org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteServiceAdminListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
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
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteServiceAdmin#exportService(org.osgi.framework.ServiceReference)
     */
    public List<ExportRegistration> exportService(ServiceReference ref) {
        List<ExportRegistration> exportRegistrations = exporter.exportService(ref);
        if (exportRegistrations != null) {
            exportedServices.addAll(exportRegistrations);
        }
        return exportRegistrations;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteServiceAdmin#exportService(org.osgi.framework.ServiceReference,
     *      java.util.Map)
     */
    public List<ExportRegistration> exportService(ServiceReference ref, Map properties) {
        List<ExportRegistration> exportRegistrations = exporter.exportService(ref);
        if (exportRegistrations != null) {
            exportedServices.addAll(exportRegistrations);
        }
        return exportRegistrations;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteServiceAdmin#getExportedServices()
     */
    public Collection<ExportRegistration> getExportedServices() {
        return exportedServices;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteServiceAdmin#getImportedEndpoints()
     */
    public Collection<ImportRegistration> getImportedEndpoints() {
        return importedEndpoints;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteServiceAdmin#importService(org.apache.tuscany.sca.dosgi.discovery.EndpointDescription)
     */
    public ImportRegistration importService(EndpointDescription endpoint) {
        Bundle bundle = (Bundle) endpoint.getProperties().get(Bundle.class.getName());
        ImportRegistration importReg = importer.importService(bundle, endpoint);
        if (importReg != null) {
            importedEndpoints.add(importReg);
        }
        return importReg;
    }

}
