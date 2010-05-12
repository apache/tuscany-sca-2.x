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

package org.apache.tuscany.sca.binding.rest.operationselector.rpc.provider;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.rest.operationselector.rpc.RPCOperationSelector;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.OperationSelectorProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * RPC operation selector Service Provider.
 * 
 * @version $Rev$ $Date$
*/
public class RPCOperationSelectorServiceProvider implements OperationSelectorProvider {
    private ExtensionPointRegistry extensionPoints;
    private RuntimeEndpoint endpoint;
    
    private Binding binding;
    
    public RPCOperationSelectorServiceProvider(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {
        this.extensionPoints = extensionPoints;
        this.endpoint = endpoint;
        this.binding = endpoint.getBinding();
    }
    
    public Interceptor createInterceptor() {
        if(binding.getOperationSelector() != null && binding.getOperationSelector() instanceof RPCOperationSelector) {
            return new RPCOperationSelectorInterceptor(extensionPoints, endpoint);
        }
        return null;
    }

    public String getPhase() {
        return Phase.SERVICE_BINDING_OPERATION_SELECTOR;
    }
}
