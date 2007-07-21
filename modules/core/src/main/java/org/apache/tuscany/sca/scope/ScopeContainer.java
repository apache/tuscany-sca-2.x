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
package org.apache.tuscany.sca.scope;

import org.apache.tuscany.sca.event.RuntimeEventListener;
import org.apache.tuscany.sca.runtime.RuntimeComponent;


/**
 * Manages the lifecycle and visibility of instances associated with a an {@link RuntimeComponent}.
 *
 * @version $Rev$ $Date$
 * @param <KEY> the type of IDs that this container uses to identify its contexts.
 * For example, for COMPOSITE scope this could be the URI of the composite component,
 * or for HTTP Session scope it might be the HTTP session ID.
 */
public interface ScopeContainer<KEY> extends Lifecycle, RuntimeEventListener {

    /**
     * Returns the Scope that this container supports.
     *
     * @return the Scope that this container supports
     */
    Scope getScope();

    /**
     * Start a new context with the supplied ID.
     *
     * @param contextId an ID that uniquely identifies the context.
     */
    void startContext(KEY contextId);

    /**
     * Stop the context with the supplied ID.
     *
     * @param contextId an ID that uniquely identifies the context.
     */
    void stopContext(KEY contextId);

    /**
     * Returns an instance wrapper associated with the current scope context, creating one if necessary
     * @param contextId the id for the scope context
     *
     * @return the wrapper for the target instance
     * @throws TargetResolutionException if there was a problem instantiating the target instance
     */
     InstanceWrapper getWrapper(KEY contextId) throws TargetResolutionException;

    /**
     * Returns an implementation instance associated with the current scope context.
     * If no instance is found, a {@link TargetNotFoundException} is thrown.
     * @param contextId the id for the scope context
     *
     * @return the wrapper for the target instance
     * @throws TargetResolutionException if there was a problem instantiating the target instance
     */
     InstanceWrapper getAssociatedWrapper(KEY contextId)
        throws TargetResolutionException;

    /**
     * Return a wrapper after use (for example, after invoking the instance).
     * @param wrapper the wrapper for the target instance being returned
     * @param contextId the id for the scope context
     *
     * @throws TargetDestructionException if there was a problem returning the target instance
     */
     void returnWrapper(InstanceWrapper wrapper, KEY contextId)
        throws TargetDestructionException;

    /**
     * Removes a component implementation instance associated with the current context from persistent storage
     *
     * @param component the owning component
     * @throws PersistenceException if there was a problem removing the instance
     */
     void remove() throws PersistenceException;
     
     /**
      * Removes an identified component implementation instance associated with the current 
      * context from persistent storage
      *
      * @param contextId the identifier of the context to remove. 
      * @throws PersistenceException if there was a problem removing the instance
      */
      void remove(KEY contextId) 
          throws TargetDestructionException;     
}
