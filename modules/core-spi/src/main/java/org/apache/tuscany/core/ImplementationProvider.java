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

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
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
     * @return An interceptor that handles the invocation logic, null should be
     *         returned if no interceptor is required
     */
    Interceptor createInterceptor(RuntimeComponent component, ComponentService service, Operation operation);

    /**
     * Create an interceptor to call back to the given component
     * @param component The component that receives the callback
     * @param operation The operation
     * @return
     */
    Interceptor createCallbackInterceptor(RuntimeComponent component, Operation operation);
    
    /**
     * Get the effective interface contract imposed by the implementation.
     * 
     * @param service The component service
     * @return The effective interface contract, if null is returned, the interface contract
     * for the component service will be used
     */
    InterfaceContract getImplementationInterfaceContract(ComponentService service);
  
    /**
     * Configure the component by adding additional metadata for the component
     * @param component The runtime component
     */
    void configure(RuntimeComponent component);
}
