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

import ${package}.assembly.EndpointReference;
import ${package}.interfacedef.Operation;
import ${package}.invocation.Invoker;
import ${package}.invocation.Message;

public class FooReferenceInvoker implements Invoker {
    
    protected Operation operation;
    protected EndpointReference endpoint;

    public FooReferenceInvoker(Operation operation, EndpointReference endpoint) {
        this.operation = operation;
        this.endpoint = endpoint;
    }

    public Message invoke(Message msg) {
        try {

            return doInvoke(msg);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Message doInvoke(Message msg) {
        // Add some code here to make an invocation over the foo binding protocol
        // For this sample we'll just get it from the static stash
        FooServiceInvoker fi = FooStash.getService(endpoint.getBinding().getURI());
        return fi.invokeService(msg);
    }
}
