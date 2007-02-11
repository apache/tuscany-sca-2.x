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
package org.apache.tuscany.spi.model.physical;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Model class representing the portable definition of a wire. This class is used to describe the inbound and outbound
 * wires on a physical component definition.
 *
 * @version $Rev$ $Date$
 */
public class WireDefinition {

    // The resolved URI of the wire
    private final URI wireUri;

    // Interceptors defined against the wire
    private final Set<InterceptorDefinition> interceptors = new HashSet<InterceptorDefinition>();

    /**
     * Initializes the wire URI.
     *
     * @param wireUri The resolved URI of the wire.
     */
    public WireDefinition(final URI wireUri) {

        if (wireUri == null) {
            throw new IllegalArgumentException("Wire uri is null");
        }
        this.wireUri = wireUri;

    }

    /**
     * Returns a read-only view of the available interceptors.
     *
     * @return List of interceptors available on the wire.
     */
    public Set<InterceptorDefinition> getInterceptors() {
        return Collections.unmodifiableSet(interceptors);
    }

    /**
     * Adds an interceptor definition.
     *
     * @param interceptorDefinition Interceptor definition to add to the wire.
     */
    public void addInterceptor(InterceptorDefinition interceptorDefinition) {

        if (interceptorDefinition == null) {
            throw new IllegalArgumentException("Interceptor definition is null");
        }
        interceptors.add(interceptorDefinition);

    }

    /**
     * Returns the wire URI.
     *
     * @return The resolved URI of the wire.
     */
    public URI getWireUri() {
        return wireUri;
    }

}
