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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.LinkedList;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.InvalidConversationSequenceException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;

/**
 * Responsible for dispatching an invocation to a Java component implementation instance.
 *
 * @version $Rev$ $Date$
 */
public class JavaInvokerInterceptor implements Interceptor {
    /* indicates that no conversational sequence is associated with the message */
    public final static short NONE = 0;
    /* indicates that the message initiates a conversation */
    public final static short START = 1;
    /* indicates that the message continues a conversation */
    public final static short CONTINUE = 2;
    /* indicates that the message ends a conversation */
    public final static short END = 3;

    private Method operation;
    private AtomicComponent component;
    private WorkContext workContext;
    private ScopeContainer scopeContainer;

    /**
     * Creates a new interceptor instance.
     *
     * @param operation      the method to invoke on the target instance
     * @param component      the target component
     * @param workContext    the work context
     * @param scopeContainer the ScopeContainer that manages implementation instances for the target component
     */
    public JavaInvokerInterceptor(Method operation,
                                  AtomicComponent component,
                                  ScopeContainer scopeContainer,
                                  WorkContext workContext) {
        this.operation = operation;
        this.component = component;
        this.workContext = workContext;
        this.scopeContainer = scopeContainer;
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an target interceptor chain");
    }

    public Interceptor getNext() {
        return null;
    }

    public boolean isOptimizable() {
        return true;
    }

    public Message invoke(Message msg) {
        try {
            Object messageId = msg.getMessageId();
            if (messageId != null) {
                workContext.setCorrelationId(messageId);
            }
            LinkedList<URI> callbackRoutingChain = msg.getCallbackUris();
            if (callbackRoutingChain != null) {
                workContext.setCallbackUris(callbackRoutingChain);
            }
            Object resp = invokeTarget(msg.getBody(), msg.getConversationSequence());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        }
        return msg;
    }

    @SuppressWarnings({"unchecked"})
    private Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
        try {
            InstanceWrapper<?> wrapper = getInstance(sequence);
            Object instance = wrapper.getInstance();
            Object ret;
            if (payload != null && !payload.getClass().isArray()) {
                ret = operation.invoke(instance, payload);
            } else {
                ret = operation.invoke(instance, (Object[]) payload);
            }
            scopeContainer.returnWrapper(component, wrapper);
            if (sequence == END) {
                // if end conversation, remove resource
                scopeContainer.remove(component);
            }
            return ret;
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e);
        } catch (ComponentException e) {
            throw new InvocationTargetException(e);
        }
    }

    /**
     * Resolves the target service instance or returns a cached one
     *
     * @param sequence the conversational sequence
     * @return the InstanceWrapper
     * @throws TargetException if an exception getting the wrapper is encountered
     */
    private InstanceWrapper<?> getInstance(short sequence) throws TargetException {
        switch (sequence) {
            case NONE:
                return scopeContainer.getWrapper(component);
            case START:
                return scopeContainer.getWrapper(component);
            case CONTINUE:
            case END:
                return scopeContainer.getAssociatedWrapper(component);
            default:
                throw new InvalidConversationSequenceException("Unknown sequence type", String.valueOf(sequence));
        }
    }


}
