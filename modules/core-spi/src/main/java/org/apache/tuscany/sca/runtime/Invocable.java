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

package org.apache.tuscany.sca.runtime;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.provider.PolicyProvider;

/**
 * The abstraction of an invocable model that contains invocation chains
 * @tuscany.spi.extension.asclient 
 */
public interface Invocable {
    /**
     * Bind the invocable to the composite context
     * @param context
     */
    void bind(CompositeContext context);
    
    /**
     * Bind the invocable to the extension point registry and endpoint registry. This is typically
     * called after the endpoint or endpoint reference is deserialized
     * @param registry
     * @param endpointRegistry
     */
    void bind(ExtensionPointRegistry registry, EndpointRegistry endpointRegistry);
    
    /**
     * Get the associated composite context
     * @return
     */
    CompositeContext getCompositeContext();
    
    /**
     * Unbind the invocable from the composite context
     */
    void unbind();

    /**
     * Get the component
     * @return
     */
    Component getComponent();

    /**
     * Get the service or reference (contract)
     * @return
     */
    Contract getContract();

    /**
     * Get the binding
     * @return
     */
    Binding getBinding();
    
    /**
     * Returns the invocation chains for service operations associated with the
     * wire
     *
     * @return the invocation chains for service operations associated with the
     *         wire
     */
    List<InvocationChain> getInvocationChains();

    /**
     * Lookup the invocation chain by operation
     * @param operation The operation
     * @return The invocation chain for the given operation
     */
    InvocationChain getInvocationChain(Operation operation);

    /**
     * Get the invocation chain for the binding-specific handling
     * @return The binding invocation chain
     */
    InvocationChain getBindingInvocationChain();

    /**
     * This invoke method assumes that the binding invocation chain is in force
     * and that there will be an operation selector element there to
     * determine which operation to call
     * @param msg The request message
     * @return The response message
     */
    Message invoke(Message msg);

    /**
     * Invoke an operation with given arguments
     * @param operation The operation
     * @param args The arguments
     * @return The result
     * @throws InvocationTargetException
     */
    Object invoke(Operation operation, Object[] args) throws InvocationTargetException;

    /**
     * Invoke an operation with a context message
     * @param operation The operation
     * @param msg The request message
     * @return The response message
     * @throws InvocationTargetException
     */
    Message invoke(Operation operation, Message msg);
    
    /**
     * Asynchronously invoke an operation with a context message
     * @param operation The operation
     * @param msg The request message
     * @return The ticket that can be used to identify this invocation
     * @throws InvocationTargetException
     */
    void invokeAsync(Operation operation, Message msg);
    
    /**
     * Asynchronously invoke an operation with a context message
     * @param tailInvoker the  invoker at the end of the chain
     * @param msg The request message
     */
    void invokeAsyncResponse(Message msg);    

    /**
     * Get a list of policy providers
     * @return
     */
    List<PolicyProvider> getPolicyProviders();
}
