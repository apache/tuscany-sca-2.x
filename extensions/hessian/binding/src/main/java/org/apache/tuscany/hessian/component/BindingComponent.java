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
package org.apache.tuscany.hessian.component;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osoa.sca.ComponentContext;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.hessian.Channel;
import org.apache.tuscany.hessian.DestinationCreationException;
import org.apache.tuscany.hessian.InvalidDestinationException;
import org.apache.tuscany.hessian.InvokerInterceptor;
import org.apache.tuscany.hessian.ServletHostNotFoundException;
import org.apache.tuscany.hessian.channel.HttpChannel;
import org.apache.tuscany.hessian.channel.LocalChannel;
import org.apache.tuscany.hessian.destination.HttpDestination;
import org.apache.tuscany.hessian.destination.LocalDestination;

/**
 * @version $Rev$ $Date$
 */
public class BindingComponent extends AbstractLifecycle implements Component {
    private String LOCAL_SCHEME = "hessianLocal";
    private String HTTP_SCHEME = "http";
    private URI uri;
    private ServletHost servletHost;
    private Map<URI, LocalDestination> destinations;

    public BindingComponent(URI uri, ServletHost host) {
        this.uri = uri;
        this.servletHost = host;
        destinations = new HashMap<URI, LocalDestination>();
    }

    public URI getUri() {
        return uri;
    }

    public List<Wire> getWires(String name) {
        return null;
    }

    public Map<String, PropertyValue<?>> getDefaultPropertyValues() {
        return null;
    }

    public void setDefaultPropertyValues(Map<String, PropertyValue<?>> defaultPropertyValues) {

    }

    public void createEndpoint(URI endpointUri, Wire wire, ClassLoader loader) throws DestinationCreationException {
        if (LOCAL_SCHEME.equals(uri.getScheme())) {
            LocalDestination destination = new LocalDestination(wire, loader);
            destinations.put(uri, destination);
        } else if (HTTP_SCHEME.equals(uri.getScheme())) {
            if (servletHost == null) {
                throw new ServletHostNotFoundException("ServletHost is was not found");
            }
            HttpDestination destination = new HttpDestination(wire, loader);
            // FIXME mapping
            servletHost.registerMapping(uri.toString(), destination);
        }
        throw new UnsupportedOperationException("Unsupported scheme");
    }

    public void bindToEndpoint(URI endpointUri, Wire wire) throws InvalidDestinationException {
        Channel channel = createChannel(endpointUri);
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getPhysicalInvocationChains()
            .entrySet()) {
            String name = entry.getKey().getName();
            InvokerInterceptor interceptor = new InvokerInterceptor(name, channel);
            entry.getValue().addInterceptor(interceptor);
        }
    }

    public ScopeContainer getScopeContainer() {
        return null;
    }

    public ComponentContext getComponentContext() {
        return null;
    }

    public void publish(Event object) {

    }

    public void addListener(RuntimeEventListener listener) {

    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {

    }

    public void removeListener(RuntimeEventListener listener) {

    }

    @Deprecated
    public void attachWire(Wire wire) {
    }

    @Deprecated
    public void attachCallbackWire(Wire wire) {
    }

    @Deprecated
    public void attachWires(List<Wire> wires) {
    }

    @Deprecated
    public Scope getScope() {
        return null;
    }

    @Deprecated
    public void setScopeContainer(ScopeContainer scopeContainer) {

    }

    @Deprecated
    public boolean isOptimizable() {
        return false;
    }

    @Deprecated
    public void register(Service service) throws RegistrationException {

    }

    @Deprecated
    public void register(Reference reference) throws RegistrationException {

    }

    @Deprecated
    public Service getService(String name) {
        return null;
    }

    @Deprecated
    public Reference getReference(String name) {
        return null;
    }

    @Deprecated
    public TargetInvoker createTargetInvoker(String targetName, Operation operation)
        throws TargetInvokerCreationException {
        return null;
    }

    @Deprecated
    public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
        throws TargetInvokerCreationException {
        return null;
    }


    /**
     * Creates a Channel to the service at the given URI
     *
     * @param uri the service uri
     * @return the channel
     * @throws InvalidDestinationException if an error is encountered creating the channel
     */
    private Channel createChannel(URI uri) throws InvalidDestinationException {
        if (LOCAL_SCHEME.equals(uri.getScheme())) {
            LocalDestination destination = destinations.get(uri);
            if (destination != null) {
                throw new InvalidDestinationException("Destination not found", uri.toString());
            }
            return new LocalChannel(destination);
        } else if (HTTP_SCHEME.equals(uri.getScheme())) {
            try {
                return new HttpChannel(uri.toURL());
            } catch (MalformedURLException e) {
                throw new InvalidDestinationException("URI must be a valid URL ", e);
            }
        }
        throw new UnsupportedOperationException("Unsupported scheme");
    }

}
