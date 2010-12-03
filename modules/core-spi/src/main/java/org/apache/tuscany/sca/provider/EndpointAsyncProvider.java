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

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;


/**
 * TUSCANY-3783
 * 
 * Async related operations that are here rather than higher up
 * while we develop them. 
 * 
 */
public interface EndpointAsyncProvider extends EndpointProvider {
    
    /**
     * TUSCANY-3801 
     * Returns true if the service binding provider is natively able
     * to dispatch async responses. 
     * 
     * @return true if the service provide support async operation natively
     */
    boolean supportsNativeAsync();
    
    /**
     * TUSCANY-3801 
     * Create an async response invoker. This is used when 
     * supportsNativeAsync = true so that the endpoint
     * has somewhere to send the async response when it
     * eventually returns from the implementation. 
     * 
     * @para operation
     * @return the invoker that will dispatch the async response 
     */
    Invoker createAsyncResponseInvoker(Operation operation);  
}
