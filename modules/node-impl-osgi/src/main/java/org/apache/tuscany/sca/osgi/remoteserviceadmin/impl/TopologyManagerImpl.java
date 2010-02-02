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

import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.SERVICE_EXPORTED_INTERFACES;
import static org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_CONFIGS;
import static org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.common.java.collection.CollectionMap;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.impl.EndpointMatcher.ImportAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Implementation of Remote Controller
 */
public class TopologyManagerImpl implements ListenerHook, RemoteServiceAdminListener, EndpointListener,
    ServiceTrackerCustomizer, LifeCycleListener /*, EventHook */{
    private final static Logger logger = Logger.getLogger(TopologyManagerImpl.class.getName());
    public final static String ENDPOINT_LOCAL = "service.local";

    private BundleContext context;
    private ServiceTracker remoteAdmins;

    private volatile ServiceRegistration registration;
    private ServiceRegistration endpointListener;

    private ServiceTracker remotableServices;

    private EndpointMatcher endpointMatcher;

    private CollectionMap<ServiceReference, ExportRegistration> exportedServices =
        new CollectionMap<ServiceReference, ExportRegistration>();
    private CollectionMap<ImportKey, ImportRegistration> importedServices =
        new CollectionMap<ImportKey, ImportRegistration>();

    private Filter remotableServiceFilter;

    public TopologyManagerImpl(BundleContext context) {
        this.context = context;
        this.endpointMatcher = new EndpointMatcher(context);
    }

    public void start() {
        String filter =
            "(& (!(" + SERVICE_IMPORTED
                + "=*)) ("
                + SERVICE_EXPORTED_INTERFACES
                + "=*) ("
                + SERVICE_EXPORTED_CONFIGS
                + "=org.osgi.sca) )";
        try {
            remotableServiceFilter = context.createFilter(filter);
        } catch (InvalidSyntaxException e) {
            // Ignore
        }

        endpointListener = context.registerService(EndpointListener.class.getName(), this, null);
        remoteAdmins = new ServiceTracker(this.context, RemoteServiceAdmin.class.getName(), null);
        remoteAdmins.open();

        // DO NOT register EventHook.class.getName() as it cannot report existing services
        String interfaceNames[] =
            new String[] {ListenerHook.class.getName(), RemoteServiceAdminListener.class.getName()};
        // The registration will trigger the added() method before registration is assigned
        registration = context.registerService(interfaceNames, this, null);

        remotableServices = new ServiceTracker(context, remotableServiceFilter, this);
        remotableServices.open(true);

        Thread thread = new Thread(new ImportTask());
        thread.start();
    }

    public Object addingService(ServiceReference reference) {
        exportService(reference);
        return reference.getBundle().getBundleContext().getService(reference);
    }

    public void modifiedService(ServiceReference reference, Object service) {
        unexportService(reference);
        exportService(reference);
    }

    public void removedService(ServiceReference reference, Object service) {
        unexportService(reference);
    }

    private void unexportService(ServiceReference reference) {
        // Call remote admin to unexport the service
        Collection<ExportRegistration> exportRegistrations = exportedServices.get(reference);
        if (exportRegistrations != null) {
            for (Iterator<ExportRegistration> i = exportRegistrations.iterator(); i.hasNext();) {
                ExportRegistration exported = i.next();
                exported.close();
                i.remove();
            }
        }
    }

    private void exportService(ServiceReference reference) {
        // Call remote admin to export the service
        Object[] admins = remoteAdmins.getServices();
        if (admins == null) {
            // Ignore
            logger.warning("No RemoteAdmin services are available.");
        } else {
            for (Object ra : admins) {
                RemoteServiceAdmin remoteAdmin = (RemoteServiceAdmin)ra;
                Collection<ExportRegistration> exportRegistrations = remoteAdmin.exportService(reference, null);
                if (exportRegistrations != null && !exportRegistrations.isEmpty()) {
                    exportedServices.putValues(reference, exportRegistrations);
                }
            }
        }
    }

    /**
     * @see org.osgi.framework.hooks.service.ListenerHook#added(java.util.Collection)
     */
    public void added(Collection listeners) {
        try {
            synchronized (endpointMatcher) {
                Collection<String> oldFilters = endpointMatcher.getFilters();
                Collection<String> newFilters = endpointMatcher.added(listeners);
                if (!OSGiHelper.getAddedItems(oldFilters, newFilters).isEmpty()) {
                    updateEndpointListenerScope(newFilters);
                }
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

    private void updateEndpointListenerScope(Collection<String> filters) {
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(ENDPOINT_LISTENER_SCOPE, filters);
        endpointListener.setProperties(props);
    }

    /**
     * @see org.osgi.framework.hooks.service.ListenerHook#removed(java.util.Collection)
     */
    public void removed(Collection listeners) {
        try {
            synchronized (endpointMatcher) {
                Collection<String> oldFilters = endpointMatcher.getFilters();
                Collection<String> newFilters = endpointMatcher.removed(listeners);
                if (!OSGiHelper.getRemovedItems(oldFilters, newFilters).isEmpty()) {
                    updateEndpointListenerScope(newFilters);
                }
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

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteAdminListener#remoteAdminEvent(org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteAdminEvent)
     */
    public void remoteAdminEvent(RemoteServiceAdminEvent event) {
        switch (event.getType()) {
            case RemoteServiceAdminEvent.EXPORT_ERROR:
            case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
            case RemoteServiceAdminEvent.EXPORT_UNREGISTRATION:
            case RemoteServiceAdminEvent.EXPORT_WARNING:
                break;
            case RemoteServiceAdminEvent.IMPORT_ERROR:
            case RemoteServiceAdminEvent.IMPORT_REGISTRATION:
            case RemoteServiceAdminEvent.IMPORT_UNREGISTRATION:
            case RemoteServiceAdminEvent.IMPORT_WARNING:
                break;
        }
    }

    /**
     * @see org.osgi.remoteserviceadmin.EndpointListener#addEndpoint(org.osgi.service.remoteserviceadmin.EndpointDescription,
     *      java.lang.String)
     */
    public void endpointAdded(EndpointDescription endpoint, String matchedFilter) {
        endpointMatcher.added(endpoint, matchedFilter);
        //        importService(endpoint, matchedFilter);
    }

    /**
     * @see org.osgi.remoteserviceadmin.EndpointListener#removeEndpoint(org.osgi.service.remoteserviceadmin.EndpointDescription)
     */
    public void endpointRemoved(EndpointDescription endpoint, String matchedFilter) {
        endpointMatcher.removed(endpoint, matchedFilter);
        //        unimportService(endpoint);
    }

    private void importService(EndpointDescription endpoint, String matchedFilter) {
        Object[] admins = remoteAdmins.getServices();
        if (admins == null) {
            logger.warning("No Remote Service Admin services are available.");
            return;
        }

        CollectionMap<Class<?>, ListenerInfo> interfaceToListeners =
            endpointMatcher.groupListeners(endpoint, matchedFilter);
        for (Map.Entry<Class<?>, Collection<ListenerInfo>> e : interfaceToListeners.entrySet()) {
            Class<?> interfaceClass = e.getKey();
            Collection<ListenerInfo> listeners = e.getValue();
            // Get a listener
            ListenerInfo listener = listeners.iterator().next();
            Bundle bundle = listener.getBundleContext().getBundle();
            if (bundle.getBundleId() == 0L) {
                // Skip system bundles
                continue;
            }
            try {
                Filter filter = listener.getBundleContext().createFilter(matchedFilter);
                if (!filter.match(new Hashtable<String, Object>(endpoint.getProperties()))) {
                    continue;
                }
            } catch (InvalidSyntaxException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                continue;
            }

            Map<String, Object> props = new HashMap<String, Object>(endpoint.getProperties());
            props.put(Bundle.class.getName(), bundle);
            props.put(Constants.OBJECTCLASS, new String[] {interfaceClass.getName()});
            EndpointDescription description = new EndpointDescription(props);

            if (admins != null) {
                for (Object ra : admins) {
                    RemoteServiceAdmin remoteAdmin = (RemoteServiceAdmin)ra;
                    ImportRegistration importRegistration = remoteAdmin.importService(description);
                    if (importRegistration != null) {
                        importedServices.putValue(new ImportKey(description, listener), importRegistration);
                    }
                }
            }
        }
    }

    private void unimportService(EndpointDescription endpoint, ListenerInfo listenerInfo) {
        // Call remote admin to unimport the service
        Collection<ImportRegistration> importRegistrations =
            importedServices.get(new ImportKey(endpoint, listenerInfo));
        if (importRegistrations != null) {
            for (Iterator<ImportRegistration> i = importRegistrations.iterator(); i.hasNext();) {
                ImportRegistration imported = i.next();
                imported.close();
                i.remove();
            }
        }
    }

    public void stop() {
        remotableServices.close();

        if (registration != null) {
            try {
                registration.unregister();
            } catch (IllegalStateException e) {
                // The service has been unregistered, ignore it
            }
            registration = null;
        }
        if (remoteAdmins != null) {
            remoteAdmins.close();
            remoteAdmins = null;
        }
        if (endpointMatcher != null) {
            endpointMatcher.clear();
        }
    }

    private class ImportTask implements Runnable {
        public void run() {
            while (registration != null) {
                BlockingQueue<EndpointMatcher.ImportAction> queue = endpointMatcher.getImportQueue();
                ImportAction action = null;
                try {
                    action = queue.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // Ignore
                }
                if (action != null) {
                    if (action.type == ImportAction.Type.Add) {
                        importService(action.endpointDescription, action.listenerInfo.getFilter());
                    } else if (action.type == ImportAction.Type.Remove) {
                        unimportService(action.endpointDescription, action.listenerInfo);
                    }
                }
            }
        }
    }

    private static class ImportKey {
        private EndpointDescription endpointDescription;

        /**
         * @param endpointDescription
         * @param listenerInfo
         */
        private ImportKey(EndpointDescription endpointDescription, ListenerInfo listenerInfo) {
            super();
            this.endpointDescription = endpointDescription;
            this.listenerInfo = listenerInfo;
        }

        private ListenerInfo listenerInfo;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((endpointDescription == null) ? 0 : endpointDescription.hashCode());
            result = prime * result + ((listenerInfo == null) ? 0 : listenerInfo.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ImportKey other = (ImportKey)obj;
            if (endpointDescription == null) {
                if (other.endpointDescription != null)
                    return false;
            } else if (!endpointDescription.equals(other.endpointDescription))
                return false;
            if (listenerInfo == null) {
                if (other.listenerInfo != null)
                    return false;
            } else if (!listenerInfo.equals(other.listenerInfo))
                return false;
            return true;
        }
    }

}
