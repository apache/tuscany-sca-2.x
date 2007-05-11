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
package org.apache.tuscany.sca.spi.component;

import java.net.URI;
import java.util.LinkedList;

import org.apache.tuscany.sca.core.RuntimeComponent;

/**
 * Implementations track information associated with a request as it is processed by the runtime
 *
 * @version $Rev$ $Date$
 */
public interface WorkContext {

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

    /**
     * Returns an ordered list of callback URIs for the current context. Ordering is based on the sequence of service
     * invocations for collocated components
     *
     * @return the current list of callback URIs
     * @Deprecated
     */
    LinkedList<URI> getCallbackUris();

    /**
     * Sets an ordered list of callback URIs for the current context. Ordering is based on the sequence of service
     * invocations for collocated components
     *
     * @Deprecated
     */
    void setCallbackUris(LinkedList<URI> uris);

    /**
     * Returns the correlation id for the current invocation or null if not available. Transports may use correlation
     * ids for message routing.
     *
     * @return the correlation id for the current invocation or null
     */
    Object getCorrelationId();

    /**
     * Sets the correlation id for the current invocation. Transports may use correlation ids for message routing.
     *
     * @param id the correlation id
     */
    void setCorrelationId(Object id);

    /**
     * Returns the current atomic component as a request is processed or null if it is not being tracked. Note that the
     * current atomic component is typically only tracked during persistence operations involving implementation
     * instances
     *
     * @return the current atomic component as a request is processed or null
     */
    RuntimeComponent getCurrentComponent();

    /**
     * Sets the current atomic component that is handling processing of a request. Note that in most cases it will not
     * be necessary to track this in the rumtime
     *
     * @param component the current atomic component
     */
    void setCurrentComponent(RuntimeComponent component);

    /**
     * Returns the name of the last remotable service to handle the current request
     *
     * @return the name of the last remotable service to handle the current request or null
     */
    String getCurrentServiceName();

    /**
     * Clears the stack of current service names
     */
    void clearServiceNames();
}
