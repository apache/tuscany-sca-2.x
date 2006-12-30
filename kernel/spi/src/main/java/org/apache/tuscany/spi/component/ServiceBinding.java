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

/**
 * The runtime instantiation of an SCA service binding.
 *
 * @version $Rev$ $Date$
 */
public interface ServiceBinding extends SCAObject {

    void setService(Service service);

    /**
     * Get the ServiceContract for the binding
     *
     * @return the ServiceContract for the binding
     */
    ServiceContract<?> getBindingServiceContract();

    /**
     * Set the ServiceContract for the binding. This contract will be used for the inbound wire. If not set, it will be
     * the same as the ServideContract from the interface.
     *
     * @param serviceContract the binding contract
     */
    void setBindingServiceContract(ServiceContract<?> serviceContract);

    /**
     * Returns the inbound wire for flowing a request through the service
     *
     * @return the inbound wire for flowing a request through the service
     */
    InboundWire getInboundWire();

    /**
     * Sets the inbound wire for flowing a request through the service
     *
     * @param wire the inbound wire for flowing a request through the service
     */
    void setInboundWire(InboundWire wire);

    /**
     * Returns the outbound wire for flowing a request out of the service
     *
     * @return the outbound wire for flowing a request out of the service
     */
    OutboundWire getOutboundWire();

    /**
     * Sets the outbound wire for flowing a request out of the service
     *
     * @param wire the outbound wire for flowing a request out of the service
     */
    void setOutboundWire(OutboundWire wire);

    /**
     * Returns the target invoker for dispatching callback invocations
     *
     * @param contract  the callback contract
     * @param operation the callback operation the target invoker dispatches to
     * @throws TargetInvokerCreationException
     */
    TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation)
        throws TargetInvokerCreationException;

    /**
     * Returns the target invoker for dispatching callback invocations
     *
     * @param contract  the callback contract
     * @param operation the callback operation the target invoker dispatches to
     * @throws TargetInvokerCreationException
     */
    TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation)
        throws TargetInvokerCreationException;

}
