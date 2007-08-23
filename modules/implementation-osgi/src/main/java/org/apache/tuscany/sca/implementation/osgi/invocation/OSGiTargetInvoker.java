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

package org.apache.tuscany.sca.implementation.osgi.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.invocation.TargetInvocationException;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.core.scope.TargetResolutionException;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Java->OSGi references use OSGiTargetInvoker to call methods from OSGi bundles
 * OSGi->Java references use JDKProxyService and invocation handler and do not use this class
 * OSGi->OSGi references go through OSGi reference mechanisms when a proxy is not used
 *    When a proxy is used, this invoker is used to call methods from OSGi bundles
 *    A proxy is used for OSGi->OSGi if
 *       1) target reference properties are specified  OR
 *       2) there are one or more non-blocking methods in the target interface OR
 *       3) scope is not COMPOSITE
 */
public class OSGiTargetInvoker<T> implements Invoker {
    
    private Operation operation;
    protected InstanceWrapper<T> target;
    protected boolean stateless;
    protected boolean cacheable;

    private final RuntimeComponentService service;
    private final ScopeContainer scopeContainer;

    public OSGiTargetInvoker(
            Operation operation, 
            RuntimeComponent component, 
            RuntimeComponentService service) {
        
        this.operation = operation;
        this.service = service;
        this.scopeContainer = ((ScopedRuntimeComponent) component).getScopeContainer();
        this.cacheable = true;
        stateless = Scope.STATELESS == scopeContainer.getScope();
        
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected InstanceWrapper getInstance(ConversationSequence sequence, Object contextId)
        throws TargetResolutionException, TargetInvocationException {
        
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
                throw new TargetInvocationException("Unknown sequence type: " + String.valueOf(sequence));
            }
        }
    }

    
    private Object invokeTarget(Message msg) throws InvocationTargetException {

    
        ConversationSequence sequence = msg.getConversationSequence();
        Object contextId = ThreadMessageContext.getMessageContext().getConversationID();
        EndpointReference from = ThreadMessageContext.getMessageContext().getFrom();
        try {
            OSGiInstanceWrapper wrapper = (OSGiInstanceWrapper)getInstance(sequence, contextId);
            Object instance;
            
            if (service != null)
                instance = wrapper.getInstance(service);
            else
                instance = wrapper.getCallbackInstance(from, operation.getInterface());
        
            Method m = JavaInterfaceUtil.findMethod(instance.getClass(), operation);
        
            Object ret = invokeMethod(instance, m, msg);
            
            scopeContainer.returnWrapper(wrapper, contextId);
            if (sequence == ConversationSequence.CONVERSATION_END) {
                // if end conversation, remove resource
                scopeContainer.remove(contextId);
            }
            return ret;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }
    
    protected Object invokeMethod(Object instance,
            Method m,
            Message msg)    
        throws InvocationTargetException {

        
        try {
            
            Object payload = msg.getBody();
            
            if (payload != null && !payload.getClass().isArray()) {
                return m.invoke(instance, payload);
            } else {
                return m.invoke(instance, (Object[]) payload);
            }
            
        } catch (InvocationTargetException e) {
            throw e;
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
            Object resp = invokeTarget(msg);
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
