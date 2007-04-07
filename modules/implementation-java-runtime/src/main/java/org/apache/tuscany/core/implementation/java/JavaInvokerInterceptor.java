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
 * @param <T> the implementation class for the component being invoked
 * @param <CONTEXT> the type of context id used by the ScopeContainer
 */
public class JavaInvokerInterceptor<T, CONTEXT> implements Interceptor {
    /* indicates that no conversational sequence is associated with the message */
    public static final short NONE = 0;
    /* indicates that the message initiates a conversation */
    public static final short START = 1;
    /* indicates that the message continues a conversation */
    public static final short CONTINUE = 2;
    /* indicates that the message ends a conversation */
    public static final short END = 3;

    private Method operation;
    private AtomicComponent<T> component;
    private ScopeContainer<CONTEXT> scopeContainer;

    /**
     * Creates a new interceptor instance.
     *
     * @param operation      the method to invoke on the target instance
     * @param component      the target component
     * @param scopeContainer the ScopeContainer that manages implementation instances for the target component
     */
    public JavaInvokerInterceptor(Method operation,
                                  AtomicComponent<T> component,
                                  ScopeContainer<CONTEXT> scopeContainer
    ) {
        this.operation = operation;
        this.component = component;
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
            Object body = msg.getBody();
            short sequence = msg.getConversationSequence();
            WorkContext workContext = msg.getWorkContext();
            Object resp = invokeTarget(body, sequence, workContext);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        }
        return msg;
    }

    private Object invokeTarget(final Object payload, final short sequence, final WorkContext workContext)
        throws InvocationTargetException {
        @SuppressWarnings("unchecked")
        CONTEXT contextId = (CONTEXT) workContext.getIdentifier(scopeContainer.getScope());
        try {
            InstanceWrapper<T> wrapper = getInstance(sequence, contextId);
            Object instance = wrapper.getInstance();
            try {
                return operation.invoke(instance, (Object[]) payload);
            } finally {
                scopeContainer.returnWrapper(component, wrapper, contextId);
                if (sequence == END) {
                    // if end conversation, remove resource
                    scopeContainer.remove(component);
                }
            }
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
     * @param contextId the scope contextId
     * @return the InstanceWrapper
     * @throws TargetException if an exception getting the wrapper is encountered
     */
    private InstanceWrapper<T> getInstance(short sequence, CONTEXT contextId) throws TargetException {
        switch (sequence) {
            case NONE:
            case START:
                return scopeContainer.getWrapper(component, contextId);
            case CONTINUE:
            case END:
                return scopeContainer.getAssociatedWrapper(component, contextId);
            default:
                throw new InvalidConversationSequenceException("Unknown sequence type", String.valueOf(sequence));
        }
    }


}
