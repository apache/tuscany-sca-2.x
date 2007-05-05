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

package org.apache.tuscany.core;

import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.scope.Scope;

/**
 * A component implementation can implement this interface to provide scope
 * management for the components
 * 
 * @version $Rev$ $Date$
 */
public interface ScopedImplementationProvider extends ImplementationProvider {
    /**
     * Get the scope for the component implementation
     * 
     * @return The scope for the component implementation, if null is returned,
     *         STATELESS will be used
     */
    Scope getScope();

    /**
     * Indicate if the component needs to be eagerly initialized
     * 
     * @param component The component
     * @return true if the component is marked to be eagerly initialized, false
     *         otherwise
     */
    boolean isEagerInit(RuntimeComponent component);

    /**
     * Create a wrapper for the component instance for the scope management
     * 
     * @param component The component
     * @return A wrapper for the component instance
     */
    InstanceWrapper createInstanceWrapper(RuntimeComponent component);

}
