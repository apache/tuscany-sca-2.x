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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.common.java.collection.CollectionMap;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.hooks.service.ListenerHook.ListenerInfo;
import org.osgi.service.remoteserviceadmin.EndpointDescription;

/**
 * Matching endpoint descriptions against the sevice listeners using OSGi filiters
 */
public class EndpointMatcher {
    private static final Logger logger = Logger.getLogger(EndpointMatcher.class.getName());
    private final EndpointMap endpointDescriptions = new EndpointMap();
    private final ListenerMap listeners = new ListenerMap();
    private final BundleContext context;
    private final BlockingQueue<ImportAction> importQueue = new ArrayBlockingQueue<ImportAction>(256, true);

    public EndpointMatcher(BundleContext context) {
        super();
        this.context = context;
    }

    public static boolean matches(String filter, EndpointDescription endpointDescription) {
        Filter f = null;
        try {
            f = FrameworkUtil.createFilter(filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        Hashtable<String, Object> props = new Hashtable<String, Object>(endpointDescription.getProperties());
        return f.match(props);
    }

    private void importEndpoint(ListenerInfo listener, EndpointDescription ep) {
        ImportAction request = new ImportAction(ImportAction.Type.Add, listener, ep);
        try {
            importQueue.put(request);
        } catch (InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void unimportEndpoint(ListenerInfo listener, EndpointDescription ep) {
        ImportAction request = new ImportAction(ImportAction.Type.Remove, listener, ep);
        try {
            importQueue.put(request);
        } catch (InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public synchronized void added(ListenerInfo listener) {
        String filter = listener.getFilter();
        listeners.putValue(filter, listener);
        for (EndpointDescription ep : getEndpoints(filter)) {
            importEndpoint(listener, ep);
        }
    }

    public synchronized Collection<String> added(Collection<ListenerInfo> listeners) {
        for (ListenerInfo listener : listeners) {
            if (accepts(listener)) {
                if (!listener.isRemoved() && listener.getBundleContext().getBundle().getBundleId() != 0L) {
                    added(listener);
                }
            }
        }
        return getFilters();
    }

    private boolean accepts(ListenerInfo listener) {
        BundleContext context = listener.getBundleContext();
        return context != null && listener.getFilter() != null && context != this.context;
    }

    public synchronized void removed(ListenerInfo listener) {
        String filter = listener.getFilter();
        if (accepts(listener))
            if (listeners.removeValue(filter, listener, true)) {
                // Find the corresponding ImportRegistration with the listener
                for (EndpointDescription ep : getEndpoints(filter)) {
                    unimportEndpoint(listener, ep);
                }
                if (getListeners(filter).isEmpty()) {
                    // No more listeners on the this filter, clean up the endpoint descriptionss
                    endpointDescriptions.remove(filter);
                }

            }
    }

    public synchronized Collection<String> removed(Collection<ListenerInfo> listeners) {
        for (ListenerInfo listener : listeners) {
            removed(listener);
        }
        return getFilters();
    }

    public synchronized void added(EndpointDescription endpointDescription) {
        for (Map.Entry<String, Collection<ListenerInfo>> entry : listeners.entrySet()) {
            if (matches(entry.getKey(), endpointDescription)) {
                endpointDescriptions.putValue(entry.getKey(), endpointDescription);
                for (ListenerInfo listener : entry.getValue()) {
                    importEndpoint(listener, endpointDescription);
                }
            }
        }
    }

    public synchronized void added(EndpointDescription endpointDescription, String matchedFilter) {
        if (endpointDescriptions.putValue(matchedFilter, endpointDescription)) {
            Collection<ListenerInfo> listenerInfos = listeners.get(matchedFilter);
            if (listenerInfos != null) {
                for (ListenerInfo listener : listenerInfos) {
                    importEndpoint(listener, endpointDescription);
                }
            }
        }
    }

    public synchronized void removed(EndpointDescription endpointDescription, String matchedFilter) {
        if (endpointDescriptions.removeValue(matchedFilter, endpointDescription, true)) {
            for (ListenerInfo listener : getListeners(matchedFilter)) {
                unimportEndpoint(listener, endpointDescription);
            }
        }
    }

    public synchronized Set<String> getFilters() {
        return new HashSet<String>(listeners.keySet());
    }

    public synchronized void clear() {
        endpointDescriptions.clear();
        listeners.clear();
        importQueue.clear();
    }

    public synchronized Collection<ListenerInfo> getListeners(String filter) {
        Collection<ListenerInfo> collection = listeners.get(filter);
        if (collection == null) {
            return Collections.emptySet();
        } else {
            return collection;
        }
    }

    public synchronized Collection<EndpointDescription> getEndpoints(String filter) {
        Collection<EndpointDescription> collection = endpointDescriptions.get(filter);
        if (collection == null) {
            return Collections.emptySet();
        } else {
            return collection;
        }
    }

    public CollectionMap<Class<?>, ListenerInfo> groupListeners(EndpointDescription endpointDescription,
                                                                String matchedFilter) {
        Collection<ListenerInfo> snapshot = new HashSet<ListenerInfo>(getListeners(matchedFilter));

        // Try to partition the listeners by the interface classes 
        List<String> interfaceNames = endpointDescription.getInterfaces();
        CollectionMap<Class<?>, ListenerInfo> interfaceToListeners = new CollectionMap<Class<?>, ListenerInfo>();
        for (String i : interfaceNames) {
            for (Iterator<ListenerInfo> it = snapshot.iterator(); it.hasNext();) {
                try {
                    ListenerInfo listener = it.next();
                    if (listener.isRemoved()) {
                        it.remove();
                        continue;
                    }
                    if (!matchedFilter.equals(listener.getFilter())) {
                        continue;
                    }
                    try {
                        // The classloading can be synchronzed against the serviceListeners
                        Class<?> interfaceClass = listener.getBundleContext().getBundle().loadClass(i);
                        interfaceToListeners.putValue(interfaceClass, listener);
                    } catch (IllegalStateException e) {
                        logger.log(Level.WARNING, e.getMessage(), e);
                        // Ignore the exception
                    }
                } catch (ClassNotFoundException e) {
                    // Ignore the listener as it cannot load the interface class
                }
            }
        }
        return interfaceToListeners;
    }

    public BlockingQueue<ImportAction> getImportQueue() {
        return importQueue;
    }

    private static class ListenerMap extends CollectionMap<String, ListenerInfo> {
        private static final long serialVersionUID = -8612202123531331219L;

        @Override
        protected Collection<ListenerInfo> createCollection() {
            return new HashSet<ListenerInfo>();
        }
    }

    private static class EndpointMap extends CollectionMap<String, EndpointDescription> {
        private static final long serialVersionUID = -6261405398109798549L;

        @Override
        protected Collection<EndpointDescription> createCollection() {
            return new HashSet<EndpointDescription>();
        }
    }

    /**
     * Representation of an import/unimport request 
     */
    public static class ImportAction {
        enum Type {
            Add, Remove
        };

        public final Type type;
        public final ListenerInfo listenerInfo;
        public final EndpointDescription endpointDescription;

        /**
         * @param type
         * @param listenerInfo
         * @param endpointDescription
         */
        public ImportAction(Type type, ListenerInfo listenerInfo, EndpointDescription endpointDescription) {
            super();
            this.type = type;
            this.listenerInfo = listenerInfo;
            this.endpointDescription = endpointDescription;
        }
    }

}
