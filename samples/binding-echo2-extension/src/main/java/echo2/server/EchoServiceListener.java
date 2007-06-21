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
package echo2.server;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;

/**
 * The EchoService listener
 */
public class EchoServiceListener {
    private Invoker invoker;
    private MessageFactory messageFactory;

    public EchoServiceListener(Invoker invoker, MessageFactory messageFactory) {
        super();
        this.invoker = invoker;
        this.messageFactory = messageFactory;
    }

    public String sendReceive(String input) throws InvocationTargetException {

        // Create a request message
        Message request = messageFactory.createMessage();
        request.setBody(new Object[] {input});
        
        // Dispatch and get the response
        Message response = invoker.invoke(request);
        Object body = response.getBody();
        if (response.isFault()) {
            throw new InvocationTargetException((Throwable)body);
        }
        return (String)body;
    }

}
