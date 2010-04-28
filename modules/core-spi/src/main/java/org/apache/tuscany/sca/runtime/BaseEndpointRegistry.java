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

package org.apache.tuscany.sca.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;

/**
 * A replicated EndpointRegistry based on Apache Tomcat Tribes
 * @tuscany.spi.extension.inheritfrom
 */
public abstract class BaseEndpointRegistry implements EndpointRegistry, LifeCycleListener {
    protected final static Logger logger = Logger.getLogger(BaseEndpointRegistry.class.getName());

    protected String domainRegistryURI;
    protected String domainURI;

    protected List<EndpointReference> endpointreferences = new CopyOnWriteArrayList<EndpointReference>();
    protected List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();
    protected ExtensionPointRegistry registry;
    protected Map<String, String> attributes;

    public BaseEndpointRegistry(ExtensionPointRegistry registry,
                                Map<String, String> attributes,
                                String domainRegistryURI,
                                String domainURI) {
        this.registry = registry;
        this.domainURI = domainURI;
        this.domainRegistryURI = domainRegistryURI;
        this.attributes = attributes;
    }

    public abstract void addEndpoint(Endpoint endpoint);

    public void addEndpointReference(EndpointReference endpointReference) {
        endpointreferences.add(endpointReference);
        logger.fine("Add endpoint reference - " + endpointReference);
    }

    public void addListener(EndpointListener listener) {
        listeners.add(listener);
    }

    protected void endpointAdded(Endpoint endpoint) {
        ((RuntimeEndpoint)endpoint).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(endpoint);
        }
    }

    protected void endpointRemoved(Endpoint endpoint) {
        ((RuntimeEndpoint)endpoint).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(endpoint);
        }
    }

    protected void endpointUpdated(Endpoint oldEp, Endpoint newEp) {
        ((RuntimeEndpoint)newEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEp, newEp);
        }
    }

    public List<Endpoint> findEndpoint(EndpointReference endpointReference) {
        logger.fine("Find endpoint for reference - " + endpointReference);

        if (endpointReference.getReference() != null) {
            Endpoint targetEndpoint = endpointReference.getTargetEndpoint();
            return findEndpoint(targetEndpoint.getURI());
        }

        return new ArrayList<Endpoint>();
    }

    public abstract List<Endpoint> findEndpoint(String uri);

    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        return endpointreferences;
    }

    public abstract Endpoint getEndpoint(String uri);

    public List<EndpointReference> getEndpointReferences() {
        return endpointreferences;
    }

    public abstract Collection<Endpoint> getEndpoints();

    public List<EndpointListener> getListeners() {
        return listeners;
    }

    public abstract void removeEndpoint(Endpoint endpoint);

    public void removeEndpointReference(EndpointReference endpointReference) {
        endpointreferences.remove(endpointReference);
        logger.fine("Remove endpoint reference - " + endpointReference);
    }

    public void removeListener(EndpointListener listener) {
        listeners.remove(listener);
    }

    public String getDomainURI() {
        return domainURI;
    }

}
