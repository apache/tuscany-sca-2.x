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
import java.util.Map;

import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLOperation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.w3c.dom.Element;

/**
 * Invoker for Sample components that implement a WSDL interface using a generic
 * call method.
 * 
 * @version $Rev$ $Date$
 */
class SampleAsyncResponseInvoker implements Invoker {
    final String name;
    final Object instance;
    final Operation op;
    Map<String, Object> asyncMessageMap;

    SampleAsyncResponseInvoker(Map<String, Object> asyncMessageMap, final Operation op, final Class<?> clazz, final Object instance) {
        this.asyncMessageMap = asyncMessageMap;  
        this.name = op.getName();
        this.instance = instance;
        this.op = op;
    }

    public Message invoke(final Message msg) {
        try {
            String messageID = (String) msg.getHeaders().get(Constants.MESSAGE_ID);
            String forwardOpName = (String)asyncMessageMap.get(messageID);
            
            // process the async response
            //Object reponse = ((Object[])msg.getBody())[0];
            Object reponse = msg.getBody();
            
            Method method = instance.getClass().getMethod(forwardOpName + "Callback", Element.class);
            method.invoke(instance, reponse);
        } catch(Exception e) {
            e.printStackTrace();
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }
}
