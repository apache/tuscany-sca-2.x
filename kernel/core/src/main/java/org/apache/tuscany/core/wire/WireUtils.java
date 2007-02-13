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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.SCAObject;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundChainHolder;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Utilities for operating on wires
 *
 * @version $Rev$ $Date$
 */
public final class WireUtils {

    private WireUtils() {
    }

    /**
     * Maps invocation chains on a wire to corresponding methods
     *
     * @param wire    the wire containing the invocation chains to map
     * @param methods the methods to map to
     * @return a collection containing the method to invocation chain mapping
     * @throws NoMethodForOperationException
     */
    public static Map<Method, InboundInvocationChain> createInboundMapping(InboundWire wire, Method[] methods)
        throws NoMethodForOperationException {
        Map<Method, InboundInvocationChain> chains = new HashMap<Method, InboundInvocationChain>();
        for (Map.Entry<Operation<?>, InboundInvocationChain> entry : wire.getInboundInvocationChains().entrySet()) {
            Operation<?> operation = entry.getKey();
            InboundInvocationChain chain = entry.getValue();
            Method method = findMethod(operation, methods);
            if (method == null) {
                throw new NoMethodForOperationException(operation.getName());
            }
            chains.put(method, chain);
        }
        return chains;
    }


    /**
     * Maps methods on an interface to operations on a wire
     *
     * @param interfaze the interface to map from
     * @param wire      the wire to map to
     * @return a collection of method to operation mappings
     * @throws NoMethodForOperationException
     */
    public static Map<Method, OutboundChainHolder> createInterfaceToWireMapping(Class<?> interfaze, OutboundWire wire)
        throws NoMethodForOperationException {
        Map<Operation<?>, OutboundInvocationChain> invocationChains = wire.getOutboundInvocationChains();
        Map<Method, OutboundChainHolder> chains = new HashMap<Method, OutboundChainHolder>(invocationChains.size());
        Method[] methods = interfaze.getMethods();
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : invocationChains.entrySet()) {
            Operation operation = entry.getKey();
            Method method = findMethod(operation, methods);
            if (method == null) {
                throw new NoMethodForOperationException(operation.getName());
            }
            chains.put(method, new OutboundChainHolder(entry.getValue()));
        }
        return chains;
    }

    /**
     * Determines if the given wire is optimizable, i.e. its invocation chains may be bypassed during an invocation.
     * This is typically calculated during the connect phase to optimize away invocation chains.
     *
     * @param wire the wire
     * @return true if the wire is optimizable
     */
    public static boolean isOptimizable(OutboundWire wire) {
        for (OutboundInvocationChain chain : wire.getOutboundInvocationChains().values()) {
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                if (current == null) {
                    break;
                }
                while (current != null) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
        }
        // if there is a callback, the wire is never optimizable since the callback target needs to be disambiguated
        return wire.getTargetCallbackInvocationChains().isEmpty();
    }

    /**
     * Determines if the given wire is optimizable, i.e. its invocation chains may be bypassed during an invocation.
     * This is typically calculated during the connect phase to optimize away invocation chains.
     *
     * @param container the wire container
     * @param wire the wire
     * @return true if the wire is optimizable
     */
    public static boolean isOptimizable(SCAObject container, InboundWire wire) {
        if (!(container instanceof Component) || !((Component) container).isOptimizable()) {
            return false;
        }
        for (InboundInvocationChain chain : wire.getInboundInvocationChains().values()) {
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                while (current != null) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
        }
        return true;
    }

}
