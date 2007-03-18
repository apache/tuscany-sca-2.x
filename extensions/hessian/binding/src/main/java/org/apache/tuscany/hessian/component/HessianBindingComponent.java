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
import java.util.Map;

import org.apache.tuscany.hessian.Channel;
import org.apache.tuscany.hessian.DestinationCreationException;
import org.apache.tuscany.hessian.InvalidDestinationException;
import org.apache.tuscany.hessian.InvokerInterceptor;
import org.apache.tuscany.hessian.ServletHostNotFoundException;
import org.apache.tuscany.hessian.channel.HttpChannel;
import org.apache.tuscany.hessian.channel.LocalChannel;
import org.apache.tuscany.hessian.destination.HttpDestination;
import org.apache.tuscany.hessian.destination.LocalDestination;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * Binding component for hessian transport.
 */
public class HessianBindingComponent {

    public static String LOCAL_SCHEME = "hessianLocal";
    public static String HTTP_SCHEME = "http";
    private URI uri;
    private ServletHost servletHost;
    private Map<URI, LocalDestination> destinations;

    public HessianBindingComponent(@Property(name = "uri")
    URI uri, @Reference(name = "servletHost")
    ServletHost host) {
        this.uri = uri;
        this.servletHost = host;
        destinations = new HashMap<URI, LocalDestination>();
    }

    public void createEndpoint(URI endpointUri, Wire wire, ClassLoader loader) throws DestinationCreationException {
        if (LOCAL_SCHEME.equals(endpointUri.getScheme())) {
            LocalDestination destination = new LocalDestination(wire, loader);
            destinations.put(uri, destination);
        } else if (HTTP_SCHEME.equals(endpointUri.getScheme())) {
            if (servletHost == null) {
                throw new ServletHostNotFoundException("ServletHost is was not found");
            }
            HttpDestination destination = new HttpDestination(wire, loader);
            // FIXME mapping
            servletHost.registerMapping(endpointUri.getPath(), destination);
        } else {
            throw new UnsupportedOperationException("Unsupported scheme");
        }
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

    /**
     * Creates a Channel to the service at the given URI
     * 
     * @param uri the service uri
     * @return the channel
     * @throws InvalidDestinationException if an error is encountered creating
     *             the channel
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
