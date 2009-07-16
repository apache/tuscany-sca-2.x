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
import static org.osgi.service.discovery.DiscoveredServiceTracker.FILTER_MATCH_CRITERIA;
import static org.osgi.service.discovery.DiscoveredServiceTracker.INTERFACE_MATCH_CRITERIA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.util.tracker.ServiceTracker;

/**
 *
 */
public abstract class AbstractDiscoveryService implements Discovery {
    private final static Logger logger = Logger.getLogger(AbstractDiscoveryService.class.getName());

    protected BundleContext context;
    protected ExtensionPointRegistry registry;

    private Map<String, List<DiscoveredServiceTracker>> filtersToTrackers =
        new HashMap<String, List<DiscoveredServiceTracker>>();
    private Map<String, List<DiscoveredServiceTracker>> interfacesToTrackers =
        new HashMap<String, List<DiscoveredServiceTracker>>();
    // this is effectively a set which allows for multiple service descriptions with the
    // same interface name but different properties and takes care of itself with respect to concurrency
    protected Map<ServiceEndpointDescription, Bundle> servicesInfo =
        new ConcurrentHashMap<ServiceEndpointDescription, Bundle>();
    private Map<DiscoveredServiceTracker, Collection<String>> trackersToFilters =
        new HashMap<DiscoveredServiceTracker, Collection<String>>();
    private Map<DiscoveredServiceTracker, Collection<String>> trackersToInterfaces =
        new HashMap<DiscoveredServiceTracker, Collection<String>>();
    private ServiceTracker trackerTracker;

    public AbstractDiscoveryService(BundleContext context) {
        super();
        this.context = context;

        // track the registration of DiscoveredServiceTrackers
        trackerTracker = new ServiceTracker(context, DiscoveredServiceTracker.class.getName(), null) {
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
        NodeFactoryImpl factory = (NodeFactoryImpl) NodeFactory.newInstance();
        factory.init();
        ServiceTracker tracker = new ServiceTracker(context, ExtensionPointRegistry.class.getName(), null);
        tracker.open();
        // tracker.waitForService(1000);
        registry = (ExtensionPointRegistry)tracker.getService();
        tracker.close();
        return registry;
    }

    private synchronized void cacheTracker(ServiceReference reference, Object service) {
        if (service instanceof DiscoveredServiceTracker) {
            DiscoveredServiceTracker tracker = (DiscoveredServiceTracker)service;
            Collection<String> interfaces =
                addTracker(reference, tracker, INTERFACE_MATCH_CRITERIA, interfacesToTrackers, trackersToInterfaces);
            Collection<String> filters =
                addTracker(reference, tracker, FILTER_MATCH_CRITERIA, filtersToTrackers, trackersToFilters);

            triggerCallbacks(null, interfaces, tracker, false);
            triggerCallbacks(null, filters, tracker, true);
        }
    }

    private synchronized void clearTracker(Object service) {
        if (service instanceof DiscoveredServiceTracker) {
            removeTracker((DiscoveredServiceTracker)service, interfacesToTrackers, trackersToInterfaces);
            removeTracker((DiscoveredServiceTracker)service, filtersToTrackers, trackersToFilters);
        }
    }

    private synchronized void updateTracker(ServiceReference reference, Object service) {
        if (service instanceof DiscoveredServiceTracker) {
            DiscoveredServiceTracker tracker = (DiscoveredServiceTracker)service;
            logger.info("updating tracker: " + tracker);
            Collection<String> oldInterfaces = removeTracker(tracker, interfacesToTrackers, trackersToInterfaces);
            Collection<String> oldFilters = removeTracker(tracker, filtersToTrackers, trackersToFilters);

            Collection<String> newInterfaces =
                addTracker(reference, tracker, INTERFACE_MATCH_CRITERIA, interfacesToTrackers, trackersToInterfaces);
            Collection<String> newFilters =
                addTracker(reference, tracker, FILTER_MATCH_CRITERIA, filtersToTrackers, trackersToFilters);

            triggerCallbacks(oldInterfaces, newInterfaces, tracker, false);
            triggerCallbacks(oldFilters, newFilters, tracker, true);
        }
    }

    private void triggerCallbacks(Collection<String> oldInterest,
                                  Collection<String> newInterest,
                                  DiscoveredServiceTracker tracker,
                                  boolean isFilter) {
        // compute delta between old & new interfaces/filters and
        // trigger callbacks for any entries in servicesInfo that
        // match any *additional* interface/filters
        Collection<String> deltaInterest = new ArrayList<String>();
        if (!isEmpty(newInterest)) {
            if (isEmpty(oldInterest)) {
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

        if (servicesInfo.size() > 0) {
            logger.info("search for matches to trigger callbacks with delta: " + deltaInterest);
        } else {
            logger.info("nothing to search for matches to trigger callbacks with delta: " + deltaInterest);
        }
        Iterator<String> i = deltaInterest.iterator();
        while (i.hasNext()) {
            String next = i.next();
            for (ServiceEndpointDescription sd : servicesInfo.keySet()) {
                triggerCallbacks(tracker, next, isFilter, sd, AVAILABLE);
            }
        }
    }

    private void triggerCallbacks(DiscoveredServiceTracker tracker,
                                  String toMatch,
                                  boolean isFilter,
                                  ServiceEndpointDescription sd,
                                  int type) {
        logger.fine("check if string: " + toMatch
            + (isFilter ? " matches " : " contained by ")
            + sd.getProvidedInterfaces());

        DiscoveredServiceNotification notification =
            isFilter ? (filterMatches(toMatch, sd) ? new DiscoveredServiceNotificationImpl(sd, true, toMatch, type)
                : null) : (sd.getProvidedInterfaces().contains(toMatch)
                ? new DiscoveredServiceNotificationImpl(sd, false, toMatch, type) : null);

        if (notification != null) {
            tracker.serviceChanged(notification);
        }
    }

    private boolean filterMatches(String filterValue, ServiceEndpointDescription sd) {
        Filter filter = createFilter(filterValue);
        return filter != null ? filter.match(getServiceProperties(null, sd)) : false;
    }

    private Filter createFilter(String filterValue) {

        if (filterValue == null) {
            return null;
        }

        try {
            return context.createFilter(filterValue);
        } catch (InvalidSyntaxException ex) {
            System.out.println("Invalid filter expression " + filterValue);
        } catch (Exception ex) {
            System.out.println("Problem creating a Filter from " + filterValue);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Dictionary<String, Object> getServiceProperties(String interfaceName, ServiceEndpointDescription sd) {
        Dictionary<String, Object> d = new Hashtable<String, Object>(sd.getProperties());

        String[] interfaceNames = getProvidedInterfaces(sd, interfaceName);
        if (interfaceNames != null) {
            d.put(INTERFACE_MATCH_CRITERIA, interfaceNames);
        }
        return d;
    }

    @SuppressWarnings("unchecked")
    private static String[] getProvidedInterfaces(ServiceEndpointDescription sd, String interfaceName) {

        Collection<String> interfaceNames = sd.getProvidedInterfaces();
        if (interfaceName == null) {
            return null;
        }

        Iterator<String> iNames = interfaceNames.iterator();
        while (iNames.hasNext()) {
            if (iNames.next().equals(interfaceName)) {
                return new String[] {interfaceName};
            }
        }
        return null;
    }

    static Collection<String> removeTracker(DiscoveredServiceTracker tracker,
                                            Map<String, List<DiscoveredServiceTracker>> forwardMap,
                                            Map<DiscoveredServiceTracker, Collection<String>> reverseMap) {
        Collection<String> collection = reverseMap.get(tracker);
        if (!isEmpty(collection)) {
            reverseMap.remove(tracker);
            Iterator<String> i = collection.iterator();
            while (i.hasNext()) {
                String element = i.next();
                if (forwardMap.containsKey(element)) {
                    forwardMap.get(element).remove(tracker);
                } else {
                    // if the element wasn't on the forwardmap, its a new element and
                    // shouldn't be returned as part of the collection of old ones
                    i.remove();
                }
            }
        }
        return collection;
    }

    private static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    @SuppressWarnings("unchecked")
    static Collection<String> addTracker(ServiceReference reference,
                                         DiscoveredServiceTracker tracker,
                                         String property,
                                         Map<String, List<DiscoveredServiceTracker>> forwardMap,
                                         Map<DiscoveredServiceTracker, Collection<String>> reverseMap) {
        Collection<String> collection = (Collection<String>)reference.getProperty(property);
        logger.info("adding tracker: " + tracker
            + " collection: "
            + collection
            + " registered against prop: "
            + property);
        if (!isEmpty(collection)) {
            reverseMap.put(tracker, new ArrayList<String>(collection));
            Iterator<String> i = collection.iterator();
            while (i.hasNext()) {
                String element = i.next();
                if (forwardMap.containsKey(element)) {
                    forwardMap.get(element).add(tracker);
                } else {
                    List<DiscoveredServiceTracker> trackerList = new ArrayList<DiscoveredServiceTracker>();
                    trackerList.add(tracker);
                    forwardMap.put(element, trackerList);
                }
            }
        }
        return collection;
    }

    protected void discoveredServiceChanged(ServiceEndpointDescription sd, int type) {
        for (Map.Entry<DiscoveredServiceTracker, Collection<String>> entry : trackersToInterfaces.entrySet()) {
            for (String match : entry.getValue()) {
                triggerCallbacks(entry.getKey(), match, false, sd, type);
            }
        }
        for (Map.Entry<DiscoveredServiceTracker, Collection<String>> entry : trackersToFilters.entrySet()) {
            for (String match : entry.getValue()) {
                triggerCallbacks(entry.getKey(), match, true, sd, type);
            }
        }
    }

    /**
     * Publish the OSGi services that are exposed to SCA. For SCA, the replicated endpoint registry
     * serves are the discovery protocol. The OSGi services are added to endpoint registry first before
     * the ServicePublication services are registered so that othe Discovery services can see them.
     * @param ref
     * @param endpoint
     * @return
     */
    protected ServiceRegistration localServicePublished(ServiceReference ref, Endpoint endpoint) {
        EndpointPublication publication = new EndpointPublication(ref, endpoint);
        ServiceRegistration registration =
            ref.getBundle().getBundleContext().registerService(ServicePublication.class.getName(),
                                                               publication,
                                                               publication.getProperties());
        return registration;
    }

}
