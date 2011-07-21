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

package org.apache.tuscany.sca.binding.local;

import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.provider.EndpointAsyncProvider;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/*
 * This service side binding provide doesn't actually serve much purpose as the 
 * local optimization skips over it. 
 */
public class LocalSCAServiceBindingProvider implements EndpointAsyncProvider {
    private RuntimeEndpoint endpoint;

    public LocalSCAServiceBindingProvider(RuntimeEndpoint endpoint, SCABindingMapper scaBindingMapper) {
        this.endpoint = endpoint;
    }

    @Override
    public InterfaceContract getBindingInterfaceContract() {
        return endpoint.getComponentTypeServiceInterfaceContract();
    }

    @Override
    public InvokerAsyncResponse createAsyncResponseInvoker() {
        return null;
    }

    @Override
    public boolean supportsOneWayInvocation() {
        // Default for Local invocation
        return false;
    }

    @Override
    public boolean supportsNativeAsync() {
        return true;
    }

    @Override
    public void stop() {
        // Nothing required for local invocation
    }

    @Override
    public void start() {
        // Nothing required for local invocation
    }

    @Override
    public void configure() {
        // Nothing required for local invocation
    }
}
