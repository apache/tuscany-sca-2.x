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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * The runtime instantiation of an SCA component
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Component extends SCAObject {

    /**
     * Sets the scope container associated with the component
     *
     * @param scopeContainer the scope container associated with the component
     */
    void setScopeContainer(ScopeContainer scopeContainer);

    /**
     * Returns the wire associated with the given service name or null if not found.
     *
     * @return the wire associated with the given service name or null if not found.
     */
    InboundWire getInboundWire(String serviceName);

    /**
     * Returns a map of inbound wires.
     *
     * @return a map of inbound wires.
     */
    Collection<InboundWire> getInboundWires();

    /**
     * Returns a map of source-side wires for references. There may be 1..n wires per reference.
     *
     * @return a map of source-side wires for references.
     */
    Map<String, List<OutboundWire>> getOutboundWires();

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to a service offered by
     * the component
     *
     * @param targetName   the service name
     * @param operation    the operation to invoke
     * @param callbackWire the callback wire or null if the associated wire is unidirectional
     * @return the target invoker
     * @throws TargetInvokerCreationException
     */
    TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire)
        throws TargetInvokerCreationException;

    /**
     * Returns the default property values associated with the component.
     *
     * @return default property values associated with the component.
     */
    Map<String, PropertyValue<?>> getDefaultPropertyValues();

    /**
     * Sets the default property values associated with the component.
     *
     * @param defaultPropertyValues Default property values associated with the component.
     */
    void setDefaultPropertyValues(Map<String, PropertyValue<?>> defaultPropertyValues);

}
