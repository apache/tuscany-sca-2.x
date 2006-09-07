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

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Creates proxies that implement Java interfaces and invocation handlers for fronting wires
 *
 * @version $$Rev$$ $$Date$$
 */

public interface WireService {

    /**
     * Creates a Java proxy for the given wire
     *
     * @param wire the wire to proxy
     * @return the proxy
     * @throws ProxyCreationException
     */
    <T> T createProxy(RuntimeWire<T> wire) throws ProxyCreationException;

    /**
     * Creates a Java proxy for the service contract callback
     *
     * @param contract the service contract
     * @return the proxy
     * @throws ProxyCreationException
     */
    <T> T createCallbackProxy(ServiceContract<?> contract, InboundWire<?> wire) throws ProxyCreationException;


    /**
     * Creates an {@link WireInvocationHandler} for the given wire
     *
     * @param wire the wire to create the invocation handler for
     * @return the invocation handler
     */
    <T> WireInvocationHandler createHandler(RuntimeWire<T> wire);

    /**
     * Creates a wire invocation handler for flowing invocations through a callback
     *
     * @return the invocation handler for flowing invocations through a callback
     */
    WireInvocationHandler createCallbackHandler(InboundWire<?> wire);

    /**
     * Creates an outbound invocation chain for a given operation
     *
     * @param operation the operation to create the chain for
     * @return the outbound invocation chain for a given operation
     */
    OutboundInvocationChain createOutboundChain(Operation<?> operation);

    /**
     * Creates an inbound invocation chain for a given operation
     *
     * @param operation the operation to create the chain for
     * @return the inbound invocation chain for a given operation
     */
    InboundInvocationChain createInboundChain(Operation<?> operation);

    /**
     * Creates a wire for flowing inbound invocations to a service
     *
     * @param service the model representation of the service
     * @return the wire for flowing inbound invocations to a service
     */
    InboundWire createWire(ServiceDefinition service);

    /**
     * Creates a wire for flowing outbound invocations to a reference
     *
     * @param reference the model artifact representing the reference on the source side
     * @param def       the model artifact representing the target reference
     * @return the wire for flowing outbound invocations to a reference
     */
    OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def);

    /**
     * Creates wires for a component and injects them on the component
     *
     * @param component  the component
     * @param definition the model artifact representing the component
     */
    void createWires(Component component, ComponentDefinition<?> definition);

    /**
     * Creates wires for a reference and injects them on the reference
     *
     * @param reference the reference
     * @param contract  the model artifact representing the service contract for the reference
     */
    <T> void createWires(Reference<T> reference, ServiceContract<?> contract);

    /**
     * Creates wires for a service and injects them on the service
     *
     * @param service the service
     * @param def     the model artifact representing the service
     */
    void createWires(Service<?> service, BoundServiceDefinition<?> def);

    /**
     * Creates wires for a composite service and injects them on the service
     *
     * @param service the service
     * @param def     the model artifact representing the service
     */
    void createWires(Service<?> service, BindlessServiceDefinition def);

}
