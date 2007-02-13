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
package org.apache.tuscany.spi.wire;

import java.net.URI;
import java.util.Map;

import org.apache.tuscany.spi.model.Operation;

/**
 * Implementations are responsible for managing the reference side of a wire, including the invocation chains associated
 * with each service operation.  An <code>OutboundWire</code> is connected to an {@link InboundWire} through their
 * invocation chains.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface OutboundWire extends Wire {

    /**
     * Returns the URI of the wire target
     *
     * @return the URI of the wire target
     */
    URI getTargetUri();

    /**
     * Sets the URI of the wire target
     *
     * @param uri the URI of the wire target
     */
    void setTargetUri(URI uri);

    /**
     * Returns the invocation configuration for each operation on a service specified by a reference or a target
     * service.
     */
    Map<Operation<?>, OutboundInvocationChain> getInvocationChains();

    /**
     * Adds the invocation chain associated with the given operation
     */
    void addInvocationChain(Operation<?> operation, OutboundInvocationChain chain);

    /**
     * Returns the callback invocation configuration for each operation on a service specified by a reference or a
     * target service.
     */
    Map<Operation<?>, InboundInvocationChain> getTargetCallbackInvocationChains();

    /**
     * Adds the callback invocation chain associated with the given operation
     */
    void addTargetCallbackInvocationChain(Operation<?> operation, InboundInvocationChain chain);

    /**
     * Set when a wire can be optimized; that is when no handlers or interceptors exist on either end
     */
    void setTargetWire(InboundWire wire);

}
