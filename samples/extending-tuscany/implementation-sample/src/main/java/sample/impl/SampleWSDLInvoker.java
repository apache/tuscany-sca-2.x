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

package sample.impl;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.core.invocation.AsyncResponseInvoker;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLOperation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Element;

/**
 * Invoker for Sample components that implement a WSDL interface using a generic
 * call method.
 * 
 * @version $Rev$ $Date$
 */
class SampleWSDLInvoker extends InterceptorAsyncImpl {
    final String name;
    final Object instance;
    final Method method;

    SampleWSDLInvoker(final WSDLOperation op, final Class<?> clazz, final Object instance) throws SecurityException, NoSuchMethodException {
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
    	// Retrieve the async callback information
    	AsyncResponseInvoker respInvoker = (AsyncResponseInvoker)msg.getHeaders().get("ASYNC_RESPONSE_INVOKER");
    	if( respInvoker == null ) throw new ServiceRuntimeException("Async Implementation invoked with no response invoker");
    	
        Message responseMsg = processRequest(msg);
        
        // in this sample programming model we make the async
        // response from the implementation provider. The 
        // component implementation itself doesn't get a chance to 
        // do async responses. 
        
        // At this point we could serialize the AsyncResponseInvoker and pick it up again 
        // later to send the async response
        
        //((RuntimeEndpoint)msg.getTo()).invokeAsyncResponse(responseMsg);
        respInvoker.invokeAsyncResponse(responseMsg);
    } // end method invokeAsyncRequest
    
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
