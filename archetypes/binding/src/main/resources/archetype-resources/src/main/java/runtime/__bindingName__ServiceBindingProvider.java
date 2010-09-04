#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.binding.foo.runtime;

import ${package}.binding.foo.FooBinding;
import ${package}.interfacedef.InterfaceContract;
import ${package}.provider.ServiceBindingProvider;
import ${package}.runtime.RuntimeEndpoint;

public class FooServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeEndpoint endpoint;
    private InterfaceContract contract;

    public FooServiceBindingProvider(RuntimeEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void start() {
        // add some code here to start the service

        // For this sample we'll just share it in a static
        FooStash.addService(endpoint.getBinding().getURI(), new FooServiceInvoker(endpoint));
        
        System.out.println("bindingType=" + ((FooBinding)endpoint.getBinding()).getBindingType());
    }

    public void stop() {
        FooStash.removeService(endpoint.getBinding().getURI());
    }

    public InterfaceContract getBindingInterfaceContract() {
        return contract;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

}
