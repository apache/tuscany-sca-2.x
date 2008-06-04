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
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Responsible for synchronously dispatching an invocation to a Java component
 * implementation instance
 * 
 * @version $Rev$ $Date$
 */
public class JavaImplementationInvoker implements Invoker, DataExchangeSemantics {
    protected Operation operation;
    protected Method method;
    protected boolean allowsPBR;

    @SuppressWarnings("unchecked")
    protected final ScopeContainer scopeContainer;

    public JavaImplementationInvoker(Operation operation, Method method, RuntimeComponent component) {
        assert method != null : "Operation method cannot be null";
        this.method = method;
        this.operation = operation;
        this.scopeContainer = ((ScopedRuntimeComponent)component).getScopeContainer();
        this.allowsPBR = ((JavaImplementation)component.getImplementation()).isAllowsPassByReference(method);
    }

    @SuppressWarnings("unchecked")
    public Message invoke(Message msg) {
        Operation op = msg.getOperation();
        if (op == null) {
            op = this.operation;
        }
        ConversationSequence sequence = op.getConversationSequence();
        Object payload = msg.getBody();

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
            // The following call might create a new conversation, as a result, the msg.getConversationID() might 
            // return a new value
            InstanceWrapper wrapper = scopeContainer.getWrapper(contextId);

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

            Object instance = wrapper.getInstance();
            Object ret;
            if (payload != null && !payload.getClass().isArray()) {
                ret = method.invoke(instance, payload);
            } else {
                ret = method.invoke(instance, (Object[])payload);
            }

            scopeContainer.returnWrapper(wrapper, contextId);

            if ((sequence == ConversationSequence.CONVERSATION_END) || (removeTemporaryConversationalComponentAfterCall)) {
                // if end conversation, or we have the special case where a conversational
                // object was created to service the stateless half of a stateful component
                scopeContainer.remove(contextId);
                parameters.setConversationID(null);
            }
            msg.setBody(ret);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            boolean isChecked = false;
            for (DataType<?> d : operation.getFaultTypes()) {
                if (d.getPhysical().isInstance(cause)) {
                    isChecked = true;
                    msg.setFaultBody(cause);
                    break;
                }
            }

            
            if (sequence != ConversationSequence.CONVERSATION_NONE ){
                try {
//                    // If the exception is not a business exception then end the conversation
//                    boolean businessException = false;
//                    
//                    for (DataType dataType : operation.getFaultTypes()){
//                        if ((dataType.getPhysical() == e.getCause().getClass()) &&
//                            (contextId != null) ){
//                            businessException = true;
//                            break;
//                        }
//                    }
                    
                    if (!isChecked && contextId != null) {
                        scopeContainer.remove(contextId);
                        parameters.setConversationID(null);
                    }
                } catch (Exception ex){
                    // TODO - sure what the best course of action is here. We have
                    //        a system exception in the middle of a business exception 
                }
            }
            if (!isChecked) {
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                } else {
                    throw new ServiceRuntimeException(cause.getMessage(), cause);
                }
            }            
                
        } catch (Exception e) {
            msg.setFaultBody(e);           
        }
        return msg;
    }

    public boolean allowsPassByReference() {
        return allowsPBR;
    }

}
