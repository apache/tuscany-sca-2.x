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
package org.apache.tuscany.sca.binding.jms.host;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.naming.NamingException;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * TODO RRB experiement
 * Listener for the JMSBinding.
 * 
 * @version $Rev$ $Date$
 */
public class JMSAsyncResponseInvoker implements MessageListener {

    private static final Logger logger = Logger.getLogger(JMSAsyncResponseInvoker.class.getName());

    private RuntimeEndpointReference endpointReference;
    private JMSBinding jmsBinding;
    private JMSResourceFactory jmsResourceFactory;
    private RuntimeComponentReference reference;
    private MessageFactory messageFactory;

    public JMSAsyncResponseInvoker(RuntimeEndpointReference endpointReference, 
    		MessageFactory messageFactory, 
    		JMSResourceFactory rf) throws NamingException {
        this.endpointReference = endpointReference;
        this.jmsBinding = (JMSBinding) endpointReference.getBinding();
        this.jmsResourceFactory = rf;
        this.reference = (RuntimeComponentReference) endpointReference.getReference();
        this.messageFactory = messageFactory;       
    }

    public void onMessage(Message requestJMSMsg) {
        logger.log(Level.FINE, "JMS reference '" + reference.getName() + "' received message " + requestJMSMsg);
        try {
            invokeReference(requestJMSMsg);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Exception send fault response '" + reference.getName(), e);
        }
    }  // end method onMessage

    protected void invokeReference(Message requestJMSMsg) throws JMSException, InvocationTargetException {

        // create the Tuscany message
        org.apache.tuscany.sca.invocation.Message tuscanyMsg = messageFactory.createMessage();
        
        // populate the message context with JMS binding information
        JMSBindingContext context = new JMSBindingContext();
        tuscanyMsg.setBindingContext(context);
        
        context.setJmsMsg(requestJMSMsg);
        context.setJmsResourceFactory(jmsResourceFactory);
        context.setReplyToDestination(requestJMSMsg.getJMSReplyTo());
        
        
        
        // set the message body
        tuscanyMsg.setBody(requestJMSMsg);
        
        // call the runtime wire - the response is handled by the 
        // transport interceptor
        endpointReference.invokeAsyncResponse(tuscanyMsg);
            
    } // end method invokeReference

} // end class AsyncResponseInvoker
