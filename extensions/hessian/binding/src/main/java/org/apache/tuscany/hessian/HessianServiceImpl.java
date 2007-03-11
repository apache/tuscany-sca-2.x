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
package org.apache.tuscany.hessian;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.hessian.channel.HttpChannel;
import org.apache.tuscany.hessian.channel.LocalChannel;
import org.apache.tuscany.hessian.destination.HttpDestination;
import org.apache.tuscany.hessian.destination.LocalDestination;

/**
 * The default Hessian system service
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public class HessianServiceImpl implements HessianService {
    private ServletHost servletHost;
    private Map<URI, LocalDestination> destinations;

    public HessianServiceImpl(@Reference ServletHost servletHost) {
        this.servletHost = servletHost;
        destinations = new HashMap<URI, LocalDestination>();
    }

    public Channel createChannel(URI uri) throws InvalidDestinationException {
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

    public void createDestination(URI uri, Wire wire, ClassLoader loader) throws DestinationCreationException {
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
}
