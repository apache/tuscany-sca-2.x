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

import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Handles the default wire format for the http binding
 * 
 * 1- determine the request and response format (xml, json, etc) from the 
 *    binding config or content type header and accept headers
 *    - TODO: need a way to configure the databinding framework based on that format
 * 2- get the request contents from the HttpServletRequest
 *    - for a post its just the request body
 *    - for a get need to convert the query string into a body based on the format (xml, json, etc)
 * 3- send the request on down the wire
 * 4- set the response contents in the HttpServletResponse 
 *    (the databinding should already have put it in the correct format)
 * 
 */
public class HTTPDefaultWireFormatServiceInterceptor implements Interceptor {
    
    private RuntimeEndpoint endpoint;
    private HTTPBinding binding;
    private Invoker next;
    
    public HTTPDefaultWireFormatServiceInterceptor(RuntimeEndpoint endpoint) {
        this.endpoint = endpoint;
        this.binding = (HTTPBinding) endpoint.getBinding();
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
