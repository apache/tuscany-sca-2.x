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
package org.apache.tuscany.sca.implementation.java.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.ConversationSequence;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.scope.CoreRuntimeException;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.scope.ScopeContainer;
import org.apache.tuscany.sca.scope.TargetResolutionException;

/**
 * Responsible for synchronously dispatching an invocation to a Java component
 * implementation instance
 * 
 * @version $Rev$ $Date$
 */
public class JavaTargetInvoker implements TargetInvoker {
    protected Method operation;
    protected boolean stateless;
    protected InstanceWrapper target;
    protected boolean cacheable;

    private final RuntimeComponent component;
    private final ScopeContainer scopeContainer;

    public JavaTargetInvoker(Method operation, RuntimeComponent component) {
        assert operation != null : "Operation method cannot be null";
        this.operation = operation;
        this.component = component;
        this.scopeContainer = component.getScopeContainer();
        stateless = Scope.STATELESS == this.scopeContainer.getScope();
    }

    @Override
    public JavaTargetInvoker clone() throws CloneNotSupportedException {
        try {
            JavaTargetInvoker invoker = (JavaTargetInvoker)super.clone();
            invoker.target = null;
            return invoker;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected InstanceWrapper getInstance(ConversationSequence sequence, Object contextId)
        throws TargetResolutionException, InvalidConversationSequenceException {
        if (sequence == null) {
            if (cacheable) {
                if (target == null) {
                    target = scopeContainer.getWrapper(contextId);
                }
                return target;
            } else {
                return scopeContainer.getWrapper(contextId);
            }
        } else {
            switch (sequence) {
                case CONVERSATION_START:
                    assert !cacheable;
                    return scopeContainer.getWrapper(contextId);
                case CONVERSATION_CONTINUE:
                case CONVERSATION_END:
                    assert !cacheable;
                    return scopeContainer.getAssociatedWrapper(contextId);
                default:
                    throw new InvalidConversationSequenceException("Unknown sequence type: " + String.valueOf(sequence));
            }
        }
    }

    public Object invokeTarget(final Object payload, final ConversationSequence sequence)
        throws InvocationTargetException {

        // FIXME: How to deal with other scopes
        Object contextId = ThreadMessageContext.getMessageContext().getConversationID();
        try {
            InstanceWrapper wrapper = getInstance(sequence, contextId);
            Object instance = wrapper.getInstance();
            Object ret;
            if (payload != null && !payload.getClass().isArray()) {
                ret = operation.invoke(instance, payload);
            } else {
                ret = operation.invoke(instance, (Object[])payload);
            }
            scopeContainer.returnWrapper(wrapper, contextId);
            if (sequence == ConversationSequence.CONVERSATION_END) {
                // if end conversation, remove resource
                scopeContainer.remove();
            }
            return ret;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    public Message invoke(Message msg) {
        try {
            Object messageId = msg.getMessageID();
            Message workContext = ThreadMessageContext.getMessageContext();
            if (messageId != null) {
                workContext.setCorrelationID(messageId);
            }
            Object resp = invokeTarget(msg.getBody(), msg.getConversationSequence());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    protected InvocationChain getInvocationChain(List<InvocationChain> chains, Operation targetOperation) {
        for (InvocationChain chain : chains) {
            if (chain.getTargetOperation().equals(targetOperation)) {
                return chain;
            }
        }
        return null;
    }

}
