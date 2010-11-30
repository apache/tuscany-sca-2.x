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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.w3c.dom.Element;

import sample.api.WSDLReference;

/**
 * Proxy used to call operations on WSDL references.
 */
class SampleWSDLProxy implements WSDLReference {
    final RuntimeEndpointReference repr;
    final Map<String, Operation> ops;
    final ExtensionPointRegistry ep;
    final MessageFactory mf;
    Map<String, Object> asyncMessageMap;

    SampleWSDLProxy(Map<String, Object> asyncMessageMap, EndpointReference epr, Interface wi, ExtensionPointRegistry ep) {
        this.asyncMessageMap = asyncMessageMap;        
        this.ep = ep;
        mf = ep.getExtensionPoint(MessageFactory.class);
        
        repr = (RuntimeEndpointReference)epr;
        ops = new HashMap<String, Operation>();
        for(Operation o: wi.getOperations())
            ops.put(o.getName(), o);
    }
    
    @Override
    public Element call(String op, Element e) {
        try {
            // Invoke the named operation on the endpoint reference
            return (Element)repr.invoke(ops.get(op), new Object[] {e});
        } catch(InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void callAsync(String op, Element e) {
        // Asynchronously invoke the named operation on the endpoint reference
        Message message = mf.createMessage();
        message.setBody(new Object[]{e});
        
        // We could add implementation specific headers here if required
        try {
            repr.invokeAsync(ops.get(op), message);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        // save the message id ready for when we process the response
        String messageID = (String) message.getHeaders().get(Constants.MESSAGE_ID);
        
        asyncMessageMap.put(messageID, op);
    }
}
