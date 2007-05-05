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
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Interceptor;

/**
 * @version $Rev$ $Date$
 */
public interface ReferenceBindingProvider {
    /**
     * Create an intercetor for the reference binding in the invocation chain.
     * The interceptor is responsible for making the outbound invocation over
     * the binding protocol.
     * 
     * @param model The service that defines the binding
     * @param operation The operation that the interceptor will handle
     * @param isCallback A flag to tell if the operation is for the callback
     * @return An interceptor that handles the invocation logic, null should be
     *         returned if no interceptor is required
     */
    Interceptor createInterceptor(Component component, ComponentReference reference, Operation operation, boolean isCallback);

    /**
     * Get the effective interface contract imposed by the binding. For example,
     * it will be interface contract introspected from the WSDL portType used by
     * the endpoint for a WebService binding.
     * 
     * @param model The model object
     * @return The effective interface contract, if null is returned, the interface contract
     * for the component reference will be used
     */
    InterfaceContract getBindingInterfaceContract(ComponentReference reference);
    

}
