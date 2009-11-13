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

package org.apache.tuscany.sca.core.invocation;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.context.ThreadMessageContext;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.Invocable;

/**
 * Invoker for a endpoint or endpoint reference
 * @version $Rev$ $Date$
 */
public class RuntimeInvoker implements Invoker {
    protected MessageFactory messageFactory;
    protected Invocable invocable;

    public RuntimeInvoker(MessageFactory messageFactory, Invocable invocable) {
        this.messageFactory = messageFactory;
        this.invocable = invocable;
    }

    public Message invokeBinding(Message msg) {
        Message context = ThreadMessageContext.setMessageContext(msg);
        try {
            return invocable.getBindingInvocationChain().getHeadInvoker().invoke(msg);
        } finally {
            ThreadMessageContext.setMessageContext(context);
        }
    }
    
    public Message invoke(Message msg) {
        return invoke(msg.getOperation(), msg);
    }

    public Object invoke(Operation operation, Object[] args) throws InvocationTargetException {
        Message msg = messageFactory.createMessage();
        msg.setBody(args);
        Message resp = invoke(operation, msg);
        Object body = resp.getBody();
        if (resp.isFault()) {
            throw new InvocationTargetException((Throwable)body);
        }
        return body;
    }


    public Message invoke(Operation operation, Message msg) {
        InvocationChain chain = invocable.getInvocationChain(operation);
        return invoke(chain, msg);
    }

    public Message invoke(InvocationChain chain, Message msg) {

        if (invocable instanceof Endpoint) {
            msg.setTo((Endpoint)invocable);
        } else if (invocable instanceof EndpointReference) {
            msg.setFrom((EndpointReference)invocable);
        }

        Invoker headInvoker = chain.getHeadInvoker();
        Operation operation = chain.getTargetOperation();
        msg.setOperation(operation);

        Message msgContext = ThreadMessageContext.setMessageContext(msg);
        try {
            return headInvoker.invoke(msg);
        } finally {
            ThreadMessageContext.setMessageContext(msgContext);
        }
    }

}
