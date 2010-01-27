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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.impl.OSGiHelper;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 *
 */
public abstract class AbstractDiscoveryService implements Discovery, LifeCycleListener {
    protected final static int ADDED = 0x1;
    protected final static int REMOVED = 0x2;
    protected final static int MODIFIED = 0x4;

    protected final static Logger logger = Logger.getLogger(AbstractDiscoveryService.class.getName());

    protected BundleContext context;
    protected ExtensionPointRegistry registry;
    private WorkScheduler workScheduler;

    private Map<EndpointListener, Collection<String>> listenersToFilters =
        new ConcurrentHashMap<EndpointListener, Collection<String>>();

    protected Map<EndpointDescription, Bundle> endpointDescriptions =
        new ConcurrentHashMap<EndpointDescription, Bundle>();
    private ServiceTracker trackerTracker;

    public AbstractDiscoveryService(BundleContext context) {
        super();
        this.context = context;
    }

    public void start() {
        getExtensionPointRegistry();
        UtilityExtensionPoint utilityExtensionPoint = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.workScheduler = utilityExtensionPoint.getUtility(WorkScheduler.class);

        // track the registration of EndpointListener
        trackerTracker = new ServiceTracker(this.context, EndpointListener.class.getName(), null) {
            public Object addingService(ServiceReference reference) {
                Object result = super.addingService(reference);
                cacheTracker(reference, result);
                return result;
            }

            public void modifiedService(ServiceReference reference, Object service) {
                super.modifiedService(reference, service);
                updateTracker(reference, service);
            }

            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);
                clearTracker(service);
            }
        };

        trackerTracker.open();
    }

    public void stop() {
        trackerTracker.close();
    }

    protected ExtensionPointRegistry getExtensionPointRegistry() {
        NodeFactoryImpl factory = (NodeFactoryImpl)NodeFactory.getInstance();
        factory.init();
        ServiceTracker tracker = new ServiceTracker(context, ExtensionPointRegistry.class.getName(), null);
        tracker.open();
        // tracker.waitForService(1000);
        registry = (ExtensionPointRegistry)tracker.getService();
        tracker.close();
        return registry;
    }

    protected Dictionary<String, Object> getProperties() {
        Dictionary headers = context.getBundle().getHeaders();
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(PRODUCT_NAME, "Apache Tuscany SCA");
        props.put(PRODUCT_VERSION, headers.get(Constants.BUNDLE_VERSION));
        props.put(VENDOR_NAME, headers.get(Constants.BUNDLE_VENDOR));
        // props.put(SUPPORTED_PROTOCOLS, new String[] {"local", "org.osgi.sca"});
        return props;
    }

    private void cacheTracker(ServiceReference reference, Object service) {
        if (service instanceof EndpointListener) {
            EndpointListener listener = (EndpointListener)service;
            Collection<String> filters = null;
            Collection<EndpointDescription> endpoints = null;
            synchronized (this) {
                filters = addTracker(reference, listener, EndpointListener.ENDPOINT_LISTENER_SCOPE);
                // Take a snapshot of the endpoints
                triggerCallbacks(null, filters, listener);
            }
        }
    }

    private void clearTracker(Object service) {
        if (service instanceof EndpointListener) {
            synchronized (this) {
                removeTracker((EndpointListener)service);
            }
        }
    }

    private void updateTracker(ServiceReference reference, Object service) {
        if (service instanceof EndpointListener) {
            EndpointListener listener = (EndpointListener)service;
            Collection<String> oldFilters = null;
            Collection<String> newFilters = null;
            Collection<EndpointDescription> endpoints = null;
            synchronized (this) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("updating listener: " + listener);
                }
                oldFilters = removeTracker(listener);
                newFilters = addTracker(reference, listener, EndpointListener.ENDPOINT_LISTENER_SCOPE);
                triggerCallbacks(oldFilters, newFilters, listener);
            }
        }
    }

    private void triggerCallbacks(Collection<String> oldInterest,
                                  Collection<String> newInterest,
                                  EndpointListener listener) {
        // compute delta between old & new interfaces/filters and
        // trigger callbacks for any entries in servicesInfo that
        // match any *additional* interface/filters
        Collection<String> deltaInterest = getDelta(oldInterest, newInterest);

        Iterator<String> i = deltaInterest.iterator();
        while (i.hasNext()) {
            String next = i.next();
            for (EndpointDescription sd : endpointDescriptions.keySet()) {
                triggerCallbacks(listener, next, sd, ADDED);
            }
        }
    }

    private Collection<String> getDelta(Collection<String> oldInterest, Collection<String> newInterest) {
        if (newInterest == null) {
            newInterest = Collections.emptySet();
        }

        Collection<String> deltaInterest = new ArrayList<String>(newInterest);
        if (oldInterest == null) {
            oldInterest = Collections.emptySet();
        }
        deltaInterest.removeAll(oldInterest);
        return deltaInterest;
    }

    /**
     * Notify the endpoint listener
     * @param listener
     * @param matchedFilter
     * @param endpoint
     * @param type
     */
    private static void notify(EndpointListener listener, String matchedFilter, EndpointDescription endpoint, int type) {
        switch (type) {
            case ADDED:
                listener.endpointAdded(endpoint, matchedFilter);
                break;
            case REMOVED:
                listener.endpointRemoved(endpoint, matchedFilter);
                break;
            case MODIFIED:
                listener.endpointRemoved(endpoint, matchedFilter);
                listener.endpointAdded(endpoint, matchedFilter);
                break;
        }
    }

    private static class Notifier implements Runnable {
        private EndpointListener listener;
        private String matchedFilter;
        private EndpointDescription endpoint;
        private int type;

        /**
         * @param listener
         * @param matchedFilter
         * @param endpoint
         * @param type
         */
        public Notifier(EndpointListener listener, String matchedFilter, EndpointDescription endpoint, int type) {
            super();
            this.listener = listener;
            this.matchedFilter = matchedFilter;
            this.endpoint = endpoint;
            this.type = type;
        }

        public void run() {
            AbstractDiscoveryService.notify(listener, matchedFilter, endpoint, type);
        }
    }

    private void triggerCallbacks(EndpointListener listener,
                                  String matchedFilter,
                                  EndpointDescription endpoint,
                                  int type) {
        workScheduler.scheduleWork(new Notifier(listener, matchedFilter, endpoint, type));

    }

    private boolean filterMatches(String filterValue, EndpointDescription sd) {
        Filter filter = OSGiHelper.createFilter(context, filterValue);
        Hashtable<String, Object> props = new Hashtable<String, Object>(sd.getProperties());
        // Add two faked properties to make the filter match
        props.put(Constants.OBJECTCLASS, sd.getInterfaces());
        props.put(RemoteConstants.SERVICE_IMPORTED, "true");
        return filter != null ? filter.match(props) : false;
    }

    private Collection<String> removeTracker(EndpointListener listener) {
        return listenersToFilters.remove(listener);
    }

    private Collection<String> addTracker(ServiceReference reference, EndpointListener listener, String property) {
        Collection<String> collection = OSGiHelper.getStringCollection(reference, property);
        if (collection != null && !collection.isEmpty()) {
            listenersToFilters.put(listener, new ArrayList<String>(collection));
        }
        return collection;
    }

    protected void endpointChanged(EndpointDescription sd, int type) {
        synchronized (this) {
            for (Map.Entry<EndpointListener, Collection<String>> entry : listenersToFilters.entrySet()) {
                for (String filter : entry.getValue()) {
                    if (filterMatches(filter, sd)) {
                        triggerCallbacks(entry.getKey(), filter, sd, type);
                    }
                }
            }
        }
    }

}
