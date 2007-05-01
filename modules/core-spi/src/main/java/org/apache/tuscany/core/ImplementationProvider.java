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

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.wire.Interceptor;

/**
 * A component implementation can implement this interface to provide additional logic 
 * to the Tuscany runtime 
 * 
 * @version $Rev$ $Date$
 */
public interface ImplementationProvider {
    /**
     * Create an intercetor for the component implementation in the invocation
     * chain. The interceptor will be responsible for calling the implementation
     * logic for the given component.
     * 
     * @param component The component that owns the component service
     * @param service The component service
     * @param operation The operation that the interceptor will handle
     * @param isCallback A flag to tell if the operation is for the callback
     * @return An interceptor that handles the invocation logic, null should be
     *         returned if no interceptor is required
     */
    Interceptor createInterceptor(Component component, ComponentService service, Operation operation, boolean isCallback);

    /**
     * Get the effective interface contract imposed by the implementation.
     * 
     * @param service The component service
     * @return The effective interface contract, if null is returned, the interface contract
     * for the component service will be used
     */
    InterfaceContract getImplementationInterfaceContract(ComponentService service);

    /**
     * Get the scope for the component implementation
     * @return The scope for the component implementation, if null is returned, STATELESS
     * will be used
     */
    Scope getScope();
    
    /**
     * Create a local instance to represent the component service
     * @param component The component
     * @param service The component service
     * @return A new instance to represent the component service
     */
    Object createInstance(RuntimeComponent component, ComponentService service);
    
    /**
     * Configure the component by adding additional metadata for the component
     * @param component The runtime component
     */
    void configure(RuntimeComponent component);
}
