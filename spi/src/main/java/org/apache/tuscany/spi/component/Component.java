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
package org.apache.tuscany.spi.component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * The runtime instantiation of an SCA component
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Component<T> extends SCAObject<T> {

    /**
     * Returns a service associated with the given name
     *
     * @throws TargetException if an error occurs retrieving the service instance
     */
    Object getServiceInstance(String name) throws TargetException;

    /**
     * Returns the service interfaces implemented by the component
     */
    List<Class<?>> getServiceInterfaces();

    /**
     * Adds a target-side wire. Target-side wire factories contain the invocation chains associated with the destination
     * service of a wire
     */
    void addInboundWire(InboundWire wire);

    /**
     * Returns the target-side wire associated with the given service name
     */
    InboundWire getInboundWire(String serviceName);

    /**
     * Returns a map of inbound wires for a service.
     */
    Map<String, InboundWire> getInboundWires();

    /**
     * Adds a source-side wire for the given reference. Source-side wires contain the invocation chains for a reference
     * in the implementation associated with the instance wrapper created by this configuration.
     */
    void addOutboundWire(OutboundWire wire);

    /**
     * Adds a set of source-side multiplicity wires for the given reference. Source-side wires contain the invocation
     * chains for a reference in the implementation associated with the instance wrapper created by this configuration.
     */
    void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires);

    /**
     * Returns a map of source-side wires for references. There may be 1..n wires per reference.
     */
    Map<String, List<OutboundWire>> getOutboundWires();

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to a service offered by
     * the component
     *
     * @param serviceName the name of the service
     * @param operation   the operation to invoke
     */
    TargetInvoker createTargetInvoker(String serviceName, Method operation);

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to a service offered by
     * the component
     *
     * @param serviceName the name of the service
     * @param operation   the operation to invoke
     */
    TargetInvoker createAsyncTargetInvoker(String serviceName, Method operation, OutboundWire wire);

}
