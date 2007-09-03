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
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.core.context.ConversationImpl;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.NoRegisteredCallbackException;

/**
 * Responsible for dispatching to a callback through a wire. <p/> TODO cache
 * target invoker
 * 
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends JDKInvocationHandler {
    private static final long serialVersionUID = -3350283555825935609L;

    public JDKCallbackInvocationHandler(MessageFactory messageFactory,
                                        CallbackWireObjectFactory wireFactory) {
        super(messageFactory, wireFactory);
        this.fixedWire = false;
    }

    @Override
    @SuppressWarnings( {"unchecked"})
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class) && "equals".equals(method.getName())) {
            // TODO implement
            throw new UnsupportedOperationException();
        } else if (Object.class.equals(method.getDeclaringClass()) && "hashCode".equals(method.getName())) {
            return hashCode();
            // TODO beter hash algorithm
        }

        // wire not pre-selected, so select a wire now to be used for the callback
        Message msgContext = ThreadMessageContext.getMessageContext();
        RuntimeWire wire = ((CallbackWireObjectFactory)callableReference).selectCallbackWire(msgContext);
        if (wire == null) {
            //FIXME: need better exception
            throw new RuntimeException("No callback wire found for " + msgContext.getFrom().getURI());
        }
        
        // set the conversational state based on the interface that
        // is specified for the reference that this wire belongs to
        setConversational(wire);
        
        // set the conversation id into the conversation object. This is
        // a special case for callbacks as, unless otherwise set manually,
        // the callback should use the same conversation id as was received
        // on the incoming call to this component
        if (conversational) {
            if (conversation == null) {
                // this is a call via an automatic proxy rather than a
                // callable/service reference so no conversation object 
                // will have been constructed yet
                conversation = new ConversationImpl();
            }
            
            Object conversationId = conversation.getConversationID();

            // create a conversation id if one doesn't exist 
            // already, i.e. the conversation is just starting
            if (conversationId == null) {
                conversationId = msgContext.getConversationID();
                conversation.setConversationID(conversationId);
            } 
        }
        
        callbackID = msgContext.getCorrelationID();
        ((CallbackWireObjectFactory)callableReference).attachCallbackID(callbackID);
        setEndpoint(msgContext.getFrom());
        
        
        
        // need to set the endpoint on the binding also so that when the chains are created next
        // the sca binding can decide whether to provide local or remote invokers. 
        // TODO - there is a problem here though in that I'm setting a target on a 
        //        binding that may possibly be trying to point at two things in the multi threaded 
        //        case. Need to confirm the general model here and how the clone and bind part
        //        is intended to work
        wire.getSource().getBinding().setURI(msgContext.getFrom().getURI());
        
        // also need to set the target contract as it varies for the sca binding depending on 
        // whether it is local or remote
        RuntimeComponentReference ref = (RuntimeComponentReference)wire.getSource().getContract();
        Binding binding = wire.getSource().getBinding();
        wire.getTarget().setInterfaceContract(ref.getBindingProvider(binding).getBindingInterfaceContract());
        
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
        }
    }

}
