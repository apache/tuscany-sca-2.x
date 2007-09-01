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

import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.core.scope.TargetResolutionException;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Responsible for synchronously dispatching an invocation to a Java component
 * implementation instance
 * 
 * @version $Rev$ $Date$
 */
public class JavaImplementationInvoker implements Invoker {
    private Method method;

    @SuppressWarnings("unchecked")
    private final ScopeContainer scopeContainer;

    public JavaImplementationInvoker(Method method, RuntimeComponent component) {
        assert method != null : "Operation method cannot be null";
        this.method = method;
        this.scopeContainer = ((ScopedRuntimeComponent)component).getScopeContainer();
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    @SuppressWarnings("unchecked")
    private InstanceWrapper getInstance(ConversationSequence sequence, Object contextId)
        throws TargetResolutionException, InvalidConversationSequenceException {
        if (sequence == null) {
            return scopeContainer.getWrapper(contextId);
        } else {
            switch (sequence) {
                case CONVERSATION_START:
                    return scopeContainer.getWrapper(contextId);
                case CONVERSATION_CONTINUE:
                case CONVERSATION_END:
                    return scopeContainer.getAssociatedWrapper(contextId);
                default:
                    throw new InvalidConversationSequenceException("Unknown sequence type: " + String.valueOf(sequence));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Message invoke(Message msg) {
        ConversationSequence sequence = msg.getConversationSequence();
        Object payload = msg.getBody();

        Object contextId = msg.getConversationID();
        try {
            // The following call might create a new conversation, as a result, the msg.getConversationID() might 
            // return a new value
            InstanceWrapper wrapper = getInstance(sequence, contextId);

            // detects whether the scope container has created a conversation Id. This will
            // happen in the case that the component has conversational scope but only the
            // callback interface is conversational
            boolean cleanUpComponent = (contextId == null) && (msg.getConversationID() != null);
            contextId = msg.getConversationID();

            Object instance = wrapper.getInstance();
            Object ret;
            if (payload != null && !payload.getClass().isArray()) {
                ret = method.invoke(instance, payload);
            } else {
                ret = method.invoke(instance, (Object[])payload);
            }
            scopeContainer.returnWrapper(wrapper, contextId);
            if ((sequence == ConversationSequence.CONVERSATION_END) || (cleanUpComponent)) {
                // if end conversation, or we have the special case where a conversational
                // object was created to service stateful callbacks, remove resource
                scopeContainer.remove(contextId);
            }
            msg.setBody(ret);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        } catch (Exception e) {
            msg.setFaultBody(e);
        }
        return msg;
    }

}
