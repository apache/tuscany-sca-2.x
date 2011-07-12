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
package org.apache.tuscany.sca.core.invocation.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.context.ThreadMessageContext;
import org.apache.tuscany.sca.core.context.impl.CallbackServiceReferenceImpl;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.Invocable;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Responsible for dispatching to a callback through a wire. <p/> TODO cache
 * target invoker
 * 
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends JDKInvocationHandler {
    private static final long serialVersionUID = -3350283555825935609L;

    public JDKCallbackInvocationHandler(MessageFactory messageFactory, ServiceReference<?> ref) {
        super(messageFactory, ref);
        this.fixedWire = false;
    }

    @Override
    @SuppressWarnings( {"unchecked", "rawtypes"})
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        if (Object.class == method.getDeclaringClass()) {
            return invokeObjectMethod(method, args);
        }
   
        // obtain a dedicated wire to be used for this callback invocation
        RuntimeEndpointReference wire = ((CallbackServiceReferenceImpl)callableReference).getCallbackEPR();
        if (wire == null) {
            //FIXME: need better exception
            throw new ServiceRuntimeException("No callback wire found");
        }

        setEndpoint(((CallbackServiceReferenceImpl)callableReference).getResolvedEndpoint());

        InvocationChain chain = getInvocationChain(method, wire);
        if (chain == null) {
            throw new IllegalArgumentException("No matching operation is found: " + method);
        }

        try {
        	String msgID = ((CallbackServiceReferenceImpl)callableReference).getMsgID();
            return invoke(method, chain, args, wire, msgID );
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            throw t;
        } finally {
            // allow the cloned wire to be reused by subsequent callbacks
        }
    }
    
    /**
     * Invoke the chain
     * @param chain - the chain
     * @param args - arguments to the invocation as an array of Objects
     * @param source - the Endpoint or EndpointReference to which the chain relates
     * @param msgID - ID of the message to which this invovation is a callback - ID ends up in "RELATES_TO" header
     * @return - the Response message from the invocation
     * @throws Throwable - if any exception occurs during the invocation
     */
    @Override
    protected Object invoke(Method method, InvocationChain chain, Object[] args, Invocable source, String msgID)
                         throws Throwable {
        Message msg = messageFactory.createMessage();
        if (source instanceof RuntimeEndpointReference) {
            msg.setFrom((RuntimeEndpointReference)source);
        }
        if (target != null) {
            msg.setTo(target);
        } else {
            if (source instanceof RuntimeEndpointReference) {
                msg.setTo(((RuntimeEndpointReference)source).getTargetEndpoint());
            }
        }
        Invoker headInvoker = chain.getHeadInvoker();
        
        Operation operation = null;
        if(source instanceof RuntimeEndpoint) {
            for (InvocationChain c : source.getInvocationChains()) {
                Operation op = c.getTargetOperation();
                if (method.getName().equals(op.getName())) {
                    operation = op;
                    break;
                }
            }
        } else {
            operation = chain.getTargetOperation();
        }
        msg.setOperation(operation);
        msg.setBody(args);

        Message msgContext = ThreadMessageContext.getMessageContext();
        
        // Deal with header information that needs to be copied from the message context to the new message...
        transferMessageHeaders( msg, msgContext);
        
        ThreadMessageContext.setMessageContext(msg);
        
        // If there is a supplied message ID, place its value into the Message Header under "RELATES_TO"
        if( msgID != null ){
        	msg.getHeaders().put(Constants.RELATES_TO, msgID);
        } // end if

        try {
            // dispatch the source down the chain and get the response
            Message resp = headInvoker.invoke(msg);
            Object body = resp.getBody();
            if (resp.isFault()) {
                throw (Throwable)body;
            }
            return body;
        } finally {
            ThreadMessageContext.setMessageContext(msgContext);
        }
    } // end method invoke

}
