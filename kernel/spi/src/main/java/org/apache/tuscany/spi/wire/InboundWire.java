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

import java.util.Map;

import org.apache.tuscany.spi.model.Operation;

/**
 * Implementations are responsible for managing the inbound side of a wire, including the invocation chains associated
 * with each service operation.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface InboundWire extends RuntimeWire {

    /**
     * Returns the name of the target service of the wire
     */
    String getServiceName();

    /**
     * Sets the name of the target service of the wire
     */
    void setServiceName(String name);

    /**
     * Returns the invocation chain for each operation on a service specified by a reference or a target
     * service.
     */
    Map<Operation<?>, InboundInvocationChain> getInvocationChains();

    /**
     * Adds the collection of invocation chains keyed by operation
     */
    void addInvocationChains(Map<Operation<?>, InboundInvocationChain> chains);

    /**
     * Adds the invocation chain associated with the given operation
     */
    void addInvocationChain(Operation<?> operation, InboundInvocationChain chain);

    /**
     * Returns the callback invocation configuration for each operation on a service specified by a reference or a
     * target service.
     */
    Map<Operation<?>, OutboundInvocationChain> getSourceCallbackInvocationChains(Object targetAddr);

    /**
     * Adds the collection of callback invocation chains keyed by operation for a given target addr
     */
    void addSourceCallbackInvocationChains(Object targetAddr, Map<Operation<?>, OutboundInvocationChain> chains);

    /**
     * Adds the callback invocation chain associated with the given operation for a given target addr
     */
    void addSourceCallbackInvocationChain(Object targetAddr, Operation<?> operation, OutboundInvocationChain chain);

    /**
     * Returns the name of the callback associated with the service of the wire
     */
    String getCallbackReferenceName();

    /**
     * Sets the name of the callback associated with the service of the wire
     */
    void setCallbackReferenceName(String callbackReferenceName);

    /**
     * Set when a wire can be optimized; that is when no handlers or interceptors exist on either end
     */
    void setTargetWire(OutboundWire wire);

    /**
     * Creates an association between a message id and the address of the SCAObject that the corresponding message
     * originates from
     */
    void addMapping(Object messageId, Object fromAddress);

    /**
     * Retrieves the SCAObject address that is associated with a message id
     */
    Object retrieveMapping(Object messageId);

    /**
     * Removes an association between a message id and the address of the SCAObject that the corresponding message
     * originates from
     */
    void removeMapping(Object messageId);
}
