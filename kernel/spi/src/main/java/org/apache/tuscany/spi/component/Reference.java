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

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Manages an SCA reference configured with a binding
 *
 * @version $Rev$ $Date$
 */
public interface Reference<T> extends SCAObject<T> {

    /**
     * Returns the service interface configured for the reference
     */
    Class<T> getInterface();

    /**
     * Returns the handler responsible for flowing a request through the reference
     *
     * @throws TargetException
     */
    WireInvocationHandler getHandler() throws TargetException;

    /**
     * Returns the inbound wire for flowing a request through the reference
     */
    InboundWire<T> getInboundWire();

    /**
     * Sets the inbound wire for flowing a request through the reference
     */
    void setInboundWire(InboundWire<T> wire);

    /**
     * Returns the outbound wire used by the reference to connect to a target
     */
    OutboundWire<T> getOutboundWire();

    /**
     * Sets the outbound wire used by the reference to connect to a target
     */
    void setOutboundWire(OutboundWire<T> wire);

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to the target service of
     * the reference
     *
     * @param contract  the service contract to invoke on
     * @param operation the operation to invoke
     */
    TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation);

    /**
     * Creates a target invoker for callbacks
     */
    TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation);

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which issues a non-blocking dispatch
     *
     * @param wire      the outbound wire of the invocation source, used for callbacks
     * @param operation the operation to invoke
     */
    TargetInvoker createAsyncTargetInvoker(OutboundWire wire, Operation operation);

}
