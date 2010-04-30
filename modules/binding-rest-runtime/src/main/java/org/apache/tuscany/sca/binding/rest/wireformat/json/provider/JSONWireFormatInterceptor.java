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

package org.apache.tuscany.sca.binding.rest.wireformat.json.provider;

import java.io.CharArrayWriter;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.json.JSONObject;

/**
 * JSON wire format Interceptor.
 * 
 * @version $Rev$ $Date$
*/
public class JSONWireFormatInterceptor implements Interceptor {
    private Invoker next;

    public JSONWireFormatInterceptor(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {

    }
    
    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public Message invoke(Message msg) {
        HTTPContext bindingContext = (HTTPContext) msg.getBindingContext();
        
        try {
            if(bindingContext.getHttpRequest().getMethod().equalsIgnoreCase("get") == false && bindingContext.getHttpRequest().getMethod().equalsIgnoreCase("delete") == false  && msg.getBody() != null) {
                Object[] args = msg.getBody();
                CharArrayWriter data = (CharArrayWriter) args[0];
                
                JSONObject jsonPayload = new JSONObject(data.toString());
                msg.setBody(new Object[]{jsonPayload});
            }
        } catch(Exception e) {
            throw new RuntimeException("Unable to parse json paylod: " + msg.getBody().toString());
        }
        
        return getNext().invoke(msg);
    }

}
