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

package sampleasync.impl;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLOperation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.w3c.dom.Element;

/**
 * Invoker for Sample components that implement a WSDL interface using a generic
 * call method.
 * 
 * @version $Rev$ $Date$
 */
class SampleWSDLInvoker extends InterceptorAsyncImpl {
    final Endpoint endpoint;
    final String name;
    final Object instance;
    final Method method;

    SampleWSDLInvoker(Endpoint endpoint, final WSDLOperation op, final Class<?> clazz, final Object instance) throws SecurityException, NoSuchMethodException {
        this.endpoint = endpoint;
        this.name = op.getName();
        this.instance = instance;
        this.method = clazz.getMethod("call", String.class, Element.class);
    }
    
    public Invoker getNext() {
        // Can't get next for an implementation invoker
        return null;
    }

    public Message invoke(final Message msg) {
        return processRequest(msg);
    }
    
    public void invokeAsyncRequest(Message msg) {
        Message responseMsg = processRequest(msg);
        
        // in this sample programming model we make the async
        // response from the implementation provider. The 
        // component implementation itself doesn't get a chance to 
        // do async responses. 
        
        ((RuntimeEndpoint)endpoint).invokeAsyncResponse(responseMsg);
    }
    
    public Message processRequest(Message msg) {
        try {
            //AsyncHeader asyncHeader = (String) message.getHeaders().get("ASYNC-HEADER");
            // Invoke the generic call method
            Object response = method.invoke(instance, name, ((Object[])msg.getBody())[0]);
            msg.setBody(response);
        } catch(Exception e) {
            e.printStackTrace();
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }
    
    public Message processResponse(Message msg) {
        return msg;
    }
}
