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

import java.util.LinkedList;

/**
 * Implementations track information associated with a request as it is processed by the runtime
 *
 * @version $Rev$ $Date$
 */
public interface WorkContext {

    Object getCurrentCorrelationId();

    void setCurrentCorrelationId(Object correlationId);

    /**
     * Returns the current atomic component as a request is processed or null if it is not being tracked. Note that the
     * current atomic component is typically only tracked during persistence operations involving implementation
     * instances
     *
     * @return the current atomic component as a request is processed or null
     */
    AtomicComponent getCurrentAtomicComponent();

    /**
     * Sets the current atomic component that is handling processing of a request. Note that in most cases it will not
     * be necessary to track this in the rumtime
     *
     * @param component the current atomic component
     */
    void setCurrentAtomicComponent(AtomicComponent component);

    /**
     * Returns the current chain of SCAObject addresses
     */
    LinkedList<Object> getCurrentCallbackRoutingChain();

    /**
     * Sets the current stack of SCAObject addresses
     */
    void setCurrentCallbackRoutingChain(LinkedList<Object> callbackRoutingChain);

    /**
     * Returns the composite where a remote request came in
     */
    CompositeComponent getRemoteComponent();

    /**
     * Sets the composite where a remote request came in
     */
    void setRemoteComponent(CompositeComponent component);

    /**
     * Returns the unique key for the given identifier associated with the current request
     */
    Object getIdentifier(Object type);

    /**
     * Sets the unique key for the given identifier associated with the current request
     */
    void setIdentifier(Object type, Object identifier);

    /**
     * Clears the unique key for the given identifier associated with the current request
     */
    void clearIdentifier(Object type);

    /**
     * Clears all identifiers associated with the current request
     */
    void clearIdentifiers();

}
