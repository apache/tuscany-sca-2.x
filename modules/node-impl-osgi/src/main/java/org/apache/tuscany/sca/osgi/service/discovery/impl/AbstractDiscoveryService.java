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

import static org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointListener.ENDPOINT_LISTENER_SCOPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointListener;
import org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteConstants;
import org.apache.tuscany.sca.osgi.service.remoteadmin.impl.OSGiHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
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

    private Map<String, List<EndpointListener>> filtersToListeners = new HashMap<String, List<EndpointListener>>();
    // this is effectively a set which allows for multiple service descriptions with the
    // same interface name but different properties and takes care of itself with respect to concurrency
    protected Map<EndpointDescription, Bundle> servicesInfo = new ConcurrentHashMap<EndpointDescription, Bundle>();
    private Map<EndpointListener, Collection<String>> listenersToFilters =
        new HashMap<EndpointListener, Collection<String>>();
    private ServiceTracker trackerTracker;

    public AbstractDiscoveryService(BundleContext context) {
        super();
        this.context = context;
    }

    public void start() {
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
        NodeFactoryImpl factory = (NodeFactoryImpl)NodeFactory.newInstance();
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
        props.put(SUPPORTED_PROTOCOLS, new String[] {"local", "sca"});
        return props;
    }

    private synchronized void cacheTracker(ServiceReference reference, Object service) {
        if (service instanceof EndpointListener) {
            EndpointListener listener = (EndpointListener)service;
            Collection<String> filters =
                addTracker(reference, listener, ENDPOINT_LISTENER_SCOPE, filtersToListeners, listenersToFilters);

            triggerCallbacks(null, filters, listener, true);
        }
    }

    private synchronized void clearTracker(Object service) {
        if (service instanceof EndpointListener) {
            removeTracker((EndpointListener)service, filtersToListeners, listenersToFilters);
        }
    }

    private synchronized void updateTracker(ServiceReference reference, Object service) {
        if (service instanceof EndpointListener) {
            EndpointListener listener = (EndpointListener)service;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("updating listener: " + listener);
            }
            Collection<String> oldFilters = removeTracker(listener, filtersToListeners, listenersToFilters);

            Collection<String> newFilters =
                addTracker(reference, listener, ENDPOINT_LISTENER_SCOPE, filtersToListeners, listenersToFilters);

            triggerCallbacks(oldFilters, newFilters, listener, true);
        }
    }

    private void triggerCallbacks(Collection<String> oldInterest,
                                  Collection<String> newInterest,
                                  EndpointListener listener,
                                  boolean isFilter) {
        // compute delta between old & new interfaces/filters and
        // trigger callbacks for any entries in servicesInfo that
        // match any *additional* interface/filters
        Collection<String> deltaInterest = new ArrayList<String>();
        if (newInterest != null && !newInterest.isEmpty()) {
            if (oldInterest == null || oldInterest.isEmpty()) {
                deltaInterest.addAll(newInterest);
            } else {
                Iterator<String> i = newInterest.iterator();
                while (i.hasNext()) {
                    String next = (String)i.next();
                    if (!oldInterest.contains(next)) {
                        deltaInterest.add(next);
                    }
                }
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            if (servicesInfo.size() > 0) {
                logger.fine("search for matches to trigger callbacks with delta: " + deltaInterest);
            } else {
                logger.fine("nothing to search for matches to trigger callbacks with delta: " + deltaInterest);
            }
        }
        Iterator<String> i = deltaInterest.iterator();
        while (i.hasNext()) {
            String next = i.next();
            for (EndpointDescription sd : servicesInfo.keySet()) {
                triggerCallbacks(listener, next, sd, ADDED);
            }
        }
    }

    private void triggerCallbacks(EndpointListener listener, String matchedFilter, EndpointDescription sd, int type) {
        switch (type) {
            case ADDED:
                listener.addEndpoint(sd, matchedFilter);
                break;
            case REMOVED:
                listener.removeEndpoint(sd);
                break;
            case MODIFIED:
                listener.removeEndpoint(sd);
                listener.addEndpoint(sd, matchedFilter);
                break;
        }
    }

    private boolean filterMatches(String filterValue, EndpointDescription sd) {
        Filter filter = OSGiHelper.createFilter(context, filterValue);
        Hashtable<String, Object> props = new Hashtable<String, Object>(sd.getProperties());
        // Add two faked properties to make the filter match
        props.put(Constants.OBJECTCLASS, sd.getInterfaces());
        props.put(RemoteConstants.SERVICE_IMPORTED, "true");
        return filter != null ? filter.match(props) : false;
    }

    static Collection<String> removeTracker(EndpointListener listener,
                                            Map<String, List<EndpointListener>> forwardMap,
                                            Map<EndpointListener, Collection<String>> reverseMap) {
        Collection<String> collection = reverseMap.get(listener);
        if (collection != null && !collection.isEmpty()) {
            reverseMap.remove(listener);
            Iterator<String> i = collection.iterator();
            while (i.hasNext()) {
                String element = i.next();
                if (forwardMap.containsKey(element)) {
                    forwardMap.get(element).remove(listener);
                } else {
                    // if the element wasn't on the forwardmap, its a new element and
                    // shouldn't be returned as part of the collection of old ones
                    i.remove();
                }
            }
        }
        return collection;
    }

    @SuppressWarnings("unchecked")
    static Collection<String> addTracker(ServiceReference reference,
                                         EndpointListener listener,
                                         String property,
                                         Map<String, List<EndpointListener>> forwardMap,
                                         Map<EndpointListener, Collection<String>> reverseMap) {
        Collection<String> collection = OSGiHelper.getStringCollection(reference, property);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("adding listener: " + listener
                + " collection: "
                + collection
                + " registered against prop: "
                + property);
        }
        if (collection != null && !collection.isEmpty()) {
            reverseMap.put(listener, new ArrayList<String>(collection));
            Iterator<String> i = collection.iterator();
            while (i.hasNext()) {
                String element = i.next();
                if (forwardMap.containsKey(element)) {
                    forwardMap.get(element).add(listener);
                } else {
                    List<EndpointListener> trackerList = new ArrayList<EndpointListener>();
                    trackerList.add(listener);
                    forwardMap.put(element, trackerList);
                }
            }
        }
        return collection;
    }

    protected void endpointChanged(EndpointDescription sd, int type) {
        for (Map.Entry<EndpointListener, Collection<String>> entry : listenersToFilters.entrySet()) {
            for (String filter : entry.getValue()) {
                if (filterMatches(filter, sd)) {
                    triggerCallbacks(entry.getKey(), filter, sd, type);
                }
            }
        }
    }

}
