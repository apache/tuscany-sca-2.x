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
package org.apache.tuscany.invocation;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.LinkedList;

import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Base class for performing invocations on a wire. Subclasses are responsible for retrieving and supplying the
 * appropriate chain, target invoker, and invocation arguments.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractInvocationHandler {
    protected boolean conversational;
    private boolean conversationStarted;

    protected AbstractInvocationHandler(boolean conversational) {
        this.conversational = conversational;
    }

    public AbstractInvocationHandler() {
    }

    protected Object invoke(InvocationChain chain,
                            TargetInvoker invoker,
                            Object[] args,
                            Object correlationId,
                            LinkedList<URI> callbackUris, WorkContext workContext)
        throws Throwable {
        Interceptor headInterceptor = chain.getHeadInterceptor();
        if (headInterceptor == null) {
            try {
                // short-circuit the dispatch and invoke the target directly
                TargetInvoker targetInvoker = chain.getTargetInvoker();
                if (targetInvoker == null) {
                    String name = chain.getTargetOperation().getName();
                    throw new AssertionError("No target invoker [" + name + "]");
                }
                return targetInvoker.invokeTarget(args, TargetInvoker.NONE, workContext);
            } catch (InvocationTargetException e) {
                // the cause was thrown by the target so throw it
                throw e.getCause();
            }
        } else {
            Message msg = new MessageImpl();
            msg.setWorkContext(workContext);
            msg.setTargetInvoker(invoker);
            msg.setCorrelationId(workContext.getCorrelationId());
            msg.setCallbackUris(workContext.getCallbackUris());
            Operation operation = chain.getTargetOperation();
            Interface contract = operation.getInterface();
            if (contract != null && contract.isConversational()) {
                Operation.ConversationSequence sequence = chain.getTargetOperation().getConversationSequence();
                if (sequence == Operation.ConversationSequence.CONVERSATION_END) {
                    msg.setConversationSequence(TargetInvoker.END);
                    conversationStarted = false;
                } else if (sequence == Operation.ConversationSequence.CONVERSATION_CONTINUE) {
                    if (conversationStarted) {
                        msg.setConversationSequence(TargetInvoker.CONTINUE);
                    } else {
                        conversationStarted = true;
                        msg.setConversationSequence(TargetInvoker.START);
                    }
                }
            }
            msg.setBody(args);
            // dispatch the wire down the chain and get the response
            Message resp = headInterceptor.invoke(msg);
            Object body = resp.getBody();
            if (resp.isFault()) {
                throw (Throwable) body;
            }
            return body;
        }
    }

    protected Object invokeTarget(InvocationChain chain,
                                  Object[] args,
                                  Object correlationId,
                                  LinkedList<RuntimeWire> callbackWires)
        throws Throwable {
        Interceptor headInterceptor = chain.getHeadInterceptor();
        assert headInterceptor != null;
        Message msg = new MessageImpl();
        if (correlationId != null) {
            msg.setCorrelationId(correlationId);
        }
        if (callbackWires != null) {
            msg.setCallbackWires(callbackWires);
        }
        Operation operation = chain.getTargetOperation();
        if (conversational) {
            Operation.ConversationSequence sequence = operation.getConversationSequence();
            if (sequence == Operation.ConversationSequence.CONVERSATION_END) {
                msg.setConversationSequence(TargetInvoker.END);
                conversationStarted = false;
            } else if (sequence == Operation.ConversationSequence.CONVERSATION_CONTINUE) {
                if (conversationStarted) {
                    msg.setConversationSequence(TargetInvoker.CONTINUE);
                } else {
                    conversationStarted = true;
                    msg.setConversationSequence(TargetInvoker.START);
                }
            }
        }
        msg.setBody(args);
        // dispatch the wire down the chain and get the response
        Message resp = headInterceptor.invoke(msg);
        Object body = resp.getBody();
        if (resp.isFault()) {
            throw (Throwable) body;
        }
        return body;
    }

}
