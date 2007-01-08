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

import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * The runtime instantiation of an SCA atomic, or leaf-type, component
 *
 * @version $Rev$ $Date$
 */
public interface AtomicComponent extends Component {

    /**
     * Returns true if component instances should be eagerly initialized.
     *
     * @return true if component instances should be eagerly initialized
     */
    boolean isEagerInit();

    /**
     * Returns true if component instances receive destroy events.
     *
     * @return true if component instances receive destroy events
     */
    boolean isDestroyable();

    /**
     * Returns the initialization level for this component.
     *
     * @return the initialization level for this component
     */
    int getInitLevel();

    /**
     * Returns the idle time allowed between operations in milliseconds if the implementation is conversational.
     *
     * @return the idle time allowed between operations in milliseconds if the implementation is conversational
     */
    long getMaxIdleTime();

    /**
     * Returns the maximum age a conversation may remain active in milliseconds if the implementation is
     * conversational.
     *
     * @return the maximum age a conversation may remain active in milliseconds if the implementation is conversational
     */
    long getMaxAge();

    /**
     * Adds a target-side wire. Target-side wire factories contain the invocation chains associated with the destination
     * service of a wire
     */
    void addInboundWire(InboundWire wire);

    /**
     * Adds a source-side wire for the given reference. Source-side wires contain the invocation chains for a reference
     * in the implementation associated with the instance wrapper created by this configuration.
     */
    void addOutboundWire(OutboundWire wire);

    /**
     * Adds a set of source-side multiplicity wires for the given reference. Source-side wires contain the invocation
     * chains for a reference in the implementation associated with the instance wrapper created by this configuration.
     * FIXME should take ServiceContract
     */
    void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires);

    /**
     * Notifies the given instance of an initialization event.
     *
     * @throws TargetInitializationException
     */
    void init(Object instance) throws TargetInitializationException;

    /**
     * Notifies the given instance of a destroy event.
     *
     * @throws TargetDestructionException
     */
    void destroy(Object instance) throws TargetDestructionException;

    /**
     * Creates a new implementation instance, generally used as a callback by a {@link
     * org.apache.tuscany.spi.component.ScopeContainer}.
     *
     * @return the instance
     * @throws ObjectCreationException
     */
    Object createInstance() throws ObjectCreationException;

    /**
     * Removes an implementation instance associated with the current invocation context.
     *
     * @throws ComponentException
     */
    void removeInstance() throws ComponentException;

    /**
     * Returns the target instance associated with the component. A target instance is the actual object a request is
     * dispatched to sans wire chain.
     *
     * @throws TargetResolutionException
     */
    Object getTargetInstance() throws TargetResolutionException;

}
