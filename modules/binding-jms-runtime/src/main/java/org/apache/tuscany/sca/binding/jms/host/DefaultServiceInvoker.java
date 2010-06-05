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
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * TODO RRB experiement
 * Listener for the JMSBinding.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultServiceInvoker implements MessageListener {

    private static final Logger logger = Logger.getLogger(DefaultServiceInvoker.class.getName());

    private RuntimeEndpoint endpoint;
    private JMSBinding jmsBinding;
    private Binding targetBinding;
    private JMSResourceFactory jmsResourceFactory;
    private RuntimeComponentService service;
    private MessageFactory messageFactory;

    public DefaultServiceInvoker(RuntimeEndpoint endpoint, Binding targetBinding, MessageFactory messageFactory, JMSResourceFactory rf) throws NamingException {
        this.endpoint = endpoint;
        this.jmsBinding = (JMSBinding) endpoint.getBinding();
        this.jmsResourceFactory = rf;
        this.service = (RuntimeComponentService) endpoint.getService();
        this.targetBinding = targetBinding;
        this.messageFactory = messageFactory;
        
    }

    public void onMessage(Message requestJMSMsg) {
        logger.log(Level.FINE, "JMS service '" + service.getName() + "' received message " + requestJMSMsg);
        try {
            invokeService(requestJMSMsg);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Exception send fault response '" + service.getName(), e);
        }
    }

    protected void invokeService(Message requestJMSMsg) throws JMSException, InvocationTargetException {

        // create the tuscany message
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
        getEndpoint(targetBinding).invoke(tuscanyMsg);
            
    } 
    
    private RuntimeEndpoint getEndpoint(Binding targetBinding) {
        for(Endpoint ep: service.getEndpoints()) {
            if(ep.getBinding() == targetBinding) {
                return (RuntimeEndpoint) ep;
            }
        }
        return endpoint; 
    }

}
