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

package org.apache.tuscany.sca.provider;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.InvokerAsyncRequest;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * TUSCANY-3786 - Possibly temporary interface to describe an
 *                async invocation. Need to make it work end to 
 *                end before committing to this. 
 *                
 * A component implementation can implement this interface in order to tie 
 * into the Tuscany runtime to process asynchronous responses. 
 * 
 */
public interface ImplementationAsyncProvider extends ImplementationProvider {
    
    /**
     * TUSCANY-3801
     * Create an async invoker for the component implementation in the invocation
     * chain. The invoker will be responsible for calling the implementation
     * logic for the given component. The only real difference between this and 
     * createInvoker is that the Endpoint is passed in so that the invoker can 
     * engineer the async response
     * 
     * @param endpoint the endoint at the head of this invocation chain
     * @param service The component service
     * @param operation The operation that the interceptor will handle
     * @return An invoker that handles the invocation logic, null should be
     *         returned if no invoker is required
     */
    InvokerAsyncRequest createAsyncInvoker(Endpoint endpoint, RuntimeComponentService service, Operation operation);

    /**
     * TUSCANY-3801
     * Create an invoker for the asynchronous responses in the invocation
     * chain. The invoker will be responsible for processing the async
     * response including correlating it with the forward call using
     * the MESAGE_ID that appears in the message header. 
     * 
     * @param service The component service
     * @param operation The operation that the interceptor will handle
     * @return An AsyncResponseHandler<T> instance
     */
    InvokerAsyncResponse createAsyncResponseInvoker(Operation operation);

}
