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
import java.lang.reflect.Method;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.core.assembly.RuntimeWireImpl;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.core.conversation.ConversationState;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.NoRegisteredCallbackException;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Responsible for dispatching to a callback through a wire. <p/> TODO cache
 * target invoker
 * 
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends JDKInvocationHandler {
    private static final long serialVersionUID = -3350283555825935609L;

    public JDKCallbackInvocationHandler(MessageFactory messageFactory, CallbackReferenceImpl ref) {
        super(messageFactory, ref);
        this.fixedWire = false;
    }

    @Override
    @SuppressWarnings( {"unchecked"})
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            return invokeObjectMethod(method, args);
        }

        // wire not pre-selected, so select a wire now to be used for the callback
        Message msgContext = ThreadMessageContext.getMessageContext();
        RuntimeWire wire = ((CallbackReferenceImpl)callableReference).selectCallbackWire(msgContext);
        if (wire == null) {
            //FIXME: need better exception
            throw new ServiceRuntimeException("No callback wire found for " + msgContext.getFrom().getURI());
        }

        // set the conversational state based on the interface that
        // is specified for the reference that this wire belongs to
        init(wire);

        // set the conversation id into the conversation object. This is
        // a special case for callbacks as, unless otherwise set manually,
        // the callback should use the same conversation id as was received
        // on the incoming call to this component
        if (conversational) {

            if (conversation == null || conversation.getState() == ConversationState.ENDED) {
                conversation = null;
            }
            Object convID = conversation == null ? null : conversation.getConversationID();

            // create a conversation id if one doesn't exist 
            // already, i.e. the conversation is just starting
            if (convID == null) {
                convID = msgContext.getFrom().getReferenceParameters().getConversationID();
                if (convID != null) {
                    conversation = ((RuntimeWireImpl)wire).getConversationManager().getConversation(convID);
                    if (callableReference != null) {
                        ((CallableReferenceImpl)callableReference).attachConversation(conversation);
                    }
                }
            }
        }

        Object callbackID = msgContext.getFrom().getReferenceParameters().getCallbackID();
        ((CallbackReferenceImpl)callableReference).attachCallbackID(callbackID);

        EndpointReference epr = msgContext.getFrom().getReferenceParameters().getCallbackReference();
        setEndpoint(epr);

        // code that was previously here has been moved to CallbackReferenceImpl.configureWire()

        //FIXME: can we use the same code as JDKInvocationHandler to select the chain? 
        InvocationChain chain = getInvocationChain(method, wire);
        if (chain == null) {
            throw new IllegalArgumentException("No matching operation is found: " + method);
        }

        try {
            return invoke(chain, args, wire);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof NoRegisteredCallbackException) {
                throw t;
            }
            throw e;
        } finally {
            // allow the cloned wire to be reused by subsequent callbacks
            ((RuntimeWireImpl)wire).releaseWire();
        }
    }

}
