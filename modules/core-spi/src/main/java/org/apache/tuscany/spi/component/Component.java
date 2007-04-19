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
import java.util.Map;

import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.ComponentContext;

/**
 * The runtime instantiation of an SCA component
 * 
 * @version $$Rev$$ $$Date: 2007-04-15 03:53:20 -0700 (Sun, 15 Apr
 *          2007) $$
 */
public interface Component extends Invocable {

    /**
     * Returns the SCA ComponentContext for this component.
     * 
     * @return the SCA ComponentContext for this component
     */
    ComponentContext getComponentContext();

    /**
     * Returns a collection of wires for the component associated with a
     * reference.
     * 
     * @param name the reference name
     * @return a collection of wires for the component associated with the
     *         reference
     */
    List<Wire> getWires(String name);

    /**
     * Returns the properties associated with the component.
     * 
     * @return properties associated with the component.
     */
    Map<String, Property> getProperties();

    /**
     * Sets the properties associated with the component.
     * 
     * @param properties Properties associated with the
     *            component.
     */
    void setProperties(Map<String, Property> properties);

    /**
     * Returns the ScopeContainer responsible for managing implementation
     * instance or null if a ScopeContainer is not associated with the Component
     * 
     * @return the scope container
     */
    ScopeContainer getScopeContainer();

    /**
     * Returns the component scope
     * 
     * @return the component scope
     */

    Scope getScope();

    /**
     * Sets the scope container associated with the component
     * 
     * @param scopeContainer the scope container associated with the component
     */

    void setScopeContainer(ScopeContainer scopeContainer);

    /**
     * Returns true if invocation dispatching can be optimized, i.e. invocation
     * chains are not required
     * 
     * @return true if invocation dispatching can be optimized, i.e. invocation
     *         chains are not required
     */

    boolean isOptimizable();

    /**
     * Registers a service of this composite.
     * 
     * @param service the service to add as a child
     * @throws RegistrationException
     */

    void register(Service service) throws RegistrationException;

    /**
     * Registers a reference of this composite.
     * 
     * @param reference the reference to add as a child
     * @throws RegistrationException
     */

    void register(Reference reference) throws RegistrationException;

    /**
     * Returns the service with the given name or null if not found
     * 
     * @param name the service name which is relative to the composite
     * @return the service with the given name or null if not found
     */

    Service getService(String name);

    /**
     * Returns the reference with the given name or null if not found
     * 
     * @param name the reference name which is relative to the composite
     * @return the reference with the given name or null if not found
     */

    Reference getReference(String name);

    /**
     * Attaches a callback wire to the comoponent
     * 
     * @param wire the wire to attach
     */

    void attachCallbackWire(Wire wire);

    /**
     * Attaches a wire to a component reference
     * 
     * @param wire the wire to attach
     */

    void attachWire(Wire wire);

    /**
     * Attaches a set of wires to a component reference. Used for multiplicity.
     * 
     * @param wires the wire to attach
     */

    void attachWires(List<Wire> wires);

}
