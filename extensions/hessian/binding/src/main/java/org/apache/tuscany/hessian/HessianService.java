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

import java.net.URI;

import org.apache.tuscany.spi.wire.Wire;

/**
 * Creates Channels and Destinations using the Hessian protocol
 *
 * @version $Rev$ $Date$
 */
public interface HessianService {
    String LOCAL_SCHEME = "hessianLocal";
    String HTTP_SCHEME = "http";

    /**
     * Creates a Channel for the given target URI. The URI scheme will determine the transport protocol
     *
     * @param uri the target URI.
     * @return the
     * @throws InvalidDestinationException
     */
    Channel createChannel(URI uri) throws InvalidDestinationException;

    /**
     * Creates and registers a destination at the given URI for a service. The URI scheme will determine the transport
     * protocol
     *
     * @param uri    the destination URI
     * @param wire   the service wire
     * @param loader  the classloader to use for payload deserializing
     * @throws DestinationCreationException
     */
    void createDestination(URI uri, Wire wire, ClassLoader loader) throws DestinationCreationException;

}
