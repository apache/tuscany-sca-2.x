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

package org.apache.tuscany.sca.binding.http.wireformat.provider;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Handles the default wire format for the http binding
 * by passing the HTTP request and response down to the
 * actual component implementation that is a servlet.
 * 
 */
public class HTTPDefaultWireFormatServiceInterceptor implements Interceptor {
    private Invoker next;
    
    public HTTPDefaultWireFormatServiceInterceptor(RuntimeEndpoint endpoint) {

    }

    @Override
    public void setNext(Invoker next) {
        this.next = next;
    }

    @Override
    public Invoker getNext() {
        return next;
    }

    @Override
    public Message invoke(Message msg) {
        HTTPContext context = msg.getBindingContext();

        msg.setBody(new Object[] {context.getHttpRequest(), context.getHttpResponse()});
        return getNext().invoke(msg);
    }
}
