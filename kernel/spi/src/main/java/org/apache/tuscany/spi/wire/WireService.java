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

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Operation;
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
     * @param interfaze the interface the proxy implements
     * @param wire      the wire to proxy @return the proxy
     * @throws ProxyCreationException
     */
    <T> T createProxy(Class<T> interfaze, Wire wire) throws ProxyCreationException;

    /**
     * Creates a Java proxy for the given wire
     *
     * @param interfaze the interface the proxy implements
     * @param wire      the wire to proxy @return the proxy
     * @param mapping   the method to chain holder mapping to use in creating the proxy. Clients may cache and resuse
     *                  this mapping for performance.
     * @throws ProxyCreationException
     */
    <T> T createProxy(Class<T> interfaze, Wire wire, Map<Method, OutboundChainHolder> mapping)
        throws ProxyCreationException;

    /**
     * Creates a Java proxy for the service contract callback
     *
     * @param interfaze the interface the proxy should implement
     * @return the proxy
     * @throws ProxyCreationException
     */
    Object createCallbackProxy(Class<?> interfaze, InboundWire wire) throws ProxyCreationException;

    /**
     * Creates an {@link WireInvocationHandler} for the given wire
     *
     * @param interfaze the client side interface
     * @param wire      the wire to create the invocation handler for
     * @return the invocation handler
     */
    WireInvocationHandler createHandler(Class<?> interfaze, Wire wire);

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
     * Creates a wire for flowing inbound invocations to a service. The returned inbound chain will always contain at
     * least one interceptor in order for outbound wires to connect to it.
     *
     * @param service the model representation of the service
     * @return the wire for flowing inbound invocations to a service
     */
    InboundWire createWire(ServiceDefinition service);

    /**
     * Creates and injects wires for an atomic component
     *
     * @param component  the component
     * @param definition the model artifact representing the component
     */
    void createWires(AtomicComponent component, ComponentDefinition<?> definition);

    /**
     * Creates and injects wires for a reference binding
     *
     * @param referenceBinding the reference
     * @param contract         the model artifact representing the service contract for the reference
     * @param targetName       the qualified target name or null if the reference referes to a target outside the SCA
     *                         domain
     */
    void createWires(ReferenceBinding referenceBinding, ServiceContract<?> contract, URI targetName);

    /**
     * Creates and injects wires for a service binding
     *
     * @param serviceBinding the serviceBinding
     * @param contract       the serviceBinding contract
     * @param targetName     the target nane
     */
    void createWires(ServiceBinding serviceBinding, ServiceContract<?> contract, String targetName);

    /**
     * Check the compatiblity of the source and the target service contracts.<p> A wire may only connect a source to a
     * target if the target implements an interface that is compatible with the interface required by the source. The
     * source and the target are compatible if:
     * <p/>
     * <ol> <li>the source interface and the target interface MUST either both be remotable or they are both local
     * <li>the methods on the target interface MUST be the same as or be a superset of the methods in the interface
     * specified on the source <li>compatibility for the individual method is defined as compatibility of the signature,
     * that is method name, input types, and output types MUST BE the same. <li>the order of the input and output types
     * also MUST BE the same. <li>the set of Faults and Exceptions expected by the source MUST BE the same or be a
     * superset of those specified by the service. <li>other specified attributes of the two interfaces MUST match,
     * including Scope and Callback interface </ol>
     * <p/>
     * <p>Please note this test is not symetric: the success of checkCompatibility(A, B) does NOT imply that
     * checkCompatibility(B, A)
     *
     * @param source         The source service contract
     * @param target         The target service contract
     * @param ignoreCallback Indicate the callback should be checked
     * @throws IncompatibleServiceContractException
     *          If the source service contract is not compatible with the target one
     */
    void checkCompatibility(ServiceContract<?> source, ServiceContract<?> target, boolean ignoreCallback)
        throws IncompatibleServiceContractException;

}
