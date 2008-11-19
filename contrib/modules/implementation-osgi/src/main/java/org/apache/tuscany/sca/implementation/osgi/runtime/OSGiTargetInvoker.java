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

package org.apache.tuscany.sca.implementation.osgi.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.invocation.TargetInvocationException;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.TargetResolutionException;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
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
 *
 * @version $Rev$ $Date$
 */
public class OSGiTargetInvoker<T> implements Invoker {

    private Operation operation;
    protected InstanceWrapper<T> target;

    private final OSGiImplementationProvider provider;
    private final RuntimeComponentService service;
    
    // Scope container is reset by the OSGi implementation provider if @Scope
    // annotation is used to modify the scope (default is composite)
    // Hence this field is initialized on the first invoke.
    private ScopeContainer scopeContainer;

    public OSGiTargetInvoker(
            Operation operation, 
            OSGiImplementationProvider provider, 
            RuntimeComponentService service) {
        

        this.operation = operation;
        this.service = service;
        this.provider = provider;

    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    @SuppressWarnings("unchecked")
    protected InstanceWrapper getInstance(Object contextId)
        throws TargetResolutionException, TargetInvocationException {
        
        if (scopeContainer == null)
            scopeContainer = provider.getScopeContainer();
        
        
        return scopeContainer.getWrapper(contextId);

    }

    @SuppressWarnings("unchecked")
    private Object invokeTarget(Message msg) throws InvocationTargetException {

        if (scopeContainer == null)
            scopeContainer = provider.getScopeContainer();
        
        
        Operation op = msg.getOperation();
        if (op == null) {
            op = this.operation;
        }
        ConversationSequence sequence = op.getConversationSequence();

        Object contextId = null;

        EndpointReference from = msg.getFrom();
        ReferenceParameters parameters = null;

        if (from != null) {
            parameters = from.getReferenceParameters();
        }
        // check what sort of context is required
        if (scopeContainer != null) {
            Scope scope = scopeContainer.getScope();
            if (scope == Scope.REQUEST) {
                contextId = Thread.currentThread();
            } else if (scope == Scope.CONVERSATION && parameters != null) {
                contextId = parameters.getConversationID();
            }
        }

        try {
        	
            OSGiInstanceWrapper wrapper = (OSGiInstanceWrapper)getInstance(contextId);
            Object instance;


            // detects whether the scope container has created a conversation Id. This will
            // happen in the case that the component has conversational scope but only the
            // callback interface is conversational. Or in the callback case if the service interface
            // is conversational and the callback interface isn't. If we are in this situation we need
            // to get the contextId of this component and remove it after we have invoked the method on 
            // it. It is possible that the component instance will not go away when it is removed below 
            // because a callback conversation will still be holding a reference to it
            boolean removeTemporaryConversationalComponentAfterCall = false;
            if (parameters != null && (contextId == null) && (parameters.getConversationID() != null)) {
                contextId = parameters.getConversationID();
                removeTemporaryConversationalComponentAfterCall = true;
            }
            
            instance = wrapper.getInstance(service);
            
            Method m = JavaInterfaceUtil.findMethod(instance.getClass(), operation);

            Object ret = invokeMethod(instance, m, msg);

            scopeContainer.returnWrapper(wrapper, contextId);

            if ((sequence == ConversationSequence.CONVERSATION_END) || (removeTemporaryConversationalComponentAfterCall)) {
                // if end conversation, or we have the special case where a conversational
                // object was created to service the stateless half of a stateful component
                scopeContainer.remove(contextId);
                parameters.setConversationID(null);
            }
            
            return ret;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    protected Object invokeMethod(Object instance, Method m, Message msg) throws InvocationTargetException {

        try {

            Object payload = msg.getBody();

            if (payload != null && !payload.getClass().isArray()) {
                return m.invoke(instance, payload);
            } else {
                return m.invoke(instance, (Object[])payload);
            }

        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    public Message invoke(Message msg) {
        try {
            //            Object messageId = msg.getMessageID();
            //            Message workContext = ThreadMessageContext.getMessageContext();
            //            if (messageId != null) {
            //                workContext.setCorrelationID(messageId);
            //            }
            Object resp = invokeTarget(msg);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }


}
