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
package org.apache.tuscany.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;

/**
 * A factory for creating runtime artifacts to facilitate testing without directly instantiating core implementation
 * classes
 *
 * @version $$Rev$$ $$Date$$
 */
public final class ArtifactFactory {

    private ArtifactFactory() {
    }

    public static Connector createConnector() {
        return new ConnectorImpl(createWireService(), null);
    }

    public static WireService createWireService() {
        return new JDKWireService();
    }

    /**
     * Creates an inbound wire. After a wire is returned, client code must call {@link
     * #terminateWire(org.apache.tuscany.spi.wire.InboundWire<T>)}. These two methods have been separated to allow wires
     * to be decorated with interceptors or handlers prior to their completion
     *
     * @param serviceName the service name associated with the wire
     * @param interfaze   the interface associated with the wire
     */
    public static <T> InboundWire<T> createInboundWire(String serviceName, Class<T> interfaze)
        throws InvalidServiceContractException {
        InboundWire<T> wire = new InboundWireImpl<T>();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(interfaze);
        wire.setServiceContract(contract);
        wire.setServiceName(serviceName);
        wire.addInvocationChains(createInboundChains(interfaze));
        return wire;
    }

    /**
     * Creates an outbound wire. After a wire is returned, client code must call {@link
     * #terminateWire(org.apache.tuscany.spi.wire.OutboundWire<T>)}. These two methods have been separated to allow
     * wires to be decorated with interceptors or handlers prior to their completion
     *
     * @param refName   the reference name the wire is associated with on the client
     * @param interfaze the interface associated with the wire
     */
    public static <T> OutboundWire<T> createOutboundWire(String refName, Class<T> interfaze)
        throws InvalidServiceContractException {
        OutboundWire<T> wire = new OutboundWireImpl<T>();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createOutboundChains(interfaze));
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(interfaze);
        wire.setServiceContract(contract);
        return wire;
    }


    /**
     * Finalizes the target wire
     */
    public static <T> void terminateWire(InboundWire<T> wire) {
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
        }
    }

    public static <T> void terminateWire(OutboundWire<T> wire) {
        for (OutboundInvocationChain chain : wire.getInvocationChains().values()) {
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
        }
    }

    private static Map<Operation<?>, OutboundInvocationChain> createOutboundChains(Class<?> interfaze)
        throws InvalidServiceContractException {
        Map<Operation<?>, OutboundInvocationChain> invocations = new HashMap<Operation<?>, OutboundInvocationChain>();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(interfaze);
        for (Operation operation : contract.getOperations().values()) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
            invocations.put(operation, chain);
        }
        return invocations;
    }

    private static Map<Operation<?>, InboundInvocationChain> createInboundChains(Class<?> interfaze)
        throws InvalidServiceContractException {
        Map<Operation<?>, InboundInvocationChain> invocations = new HashMap<Operation<?>, InboundInvocationChain>();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(interfaze);
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
            // add tail interceptor
            //chain.addInterceptor(new InvokerInterceptor());
            invocations.put(operation, chain);
        }
        return invocations;
    }


}
