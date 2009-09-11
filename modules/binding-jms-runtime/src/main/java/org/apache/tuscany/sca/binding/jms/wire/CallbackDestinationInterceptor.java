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
package org.apache.tuscany.sca.binding.jms.wire;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.tuscany.sca.assembly.impl.EndpointReferenceImpl;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

public class CallbackDestinationInterceptor implements Interceptor {
    private Invoker next;
    private RuntimeComponentService service;
          
    public CallbackDestinationInterceptor(RuntimeWire runtimeWire) {
        super();
        this.service = (RuntimeComponentService) runtimeWire.getTarget().getContract();
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public Message invoke(Message msg) {
        return next.invoke(invokeRequest(msg));
    }

    public Message invokeRequest(Message msg) {
        try {
            // get the jms context
            JMSBindingContext context = msg.getBindingContext();
            javax.jms.Message jmsMsg = context.getJmsMsg();
        
            ReferenceParameters parameters = msg.getFrom().getReferenceParameters();

            if (service.getInterfaceContract().getCallbackInterface() != null) {

                String callbackdestName = jmsMsg.getStringProperty(JMSBindingConstants.CALLBACK_Q_PROPERTY);
                if (callbackdestName == null && msg.getOperation().isNonBlocking()) {
                    // if the request has a replyTo but this service operation is oneway but the service uses callbacks
                    // then use the replyTo as the callback destination
                    Destination replyTo = jmsMsg.getJMSReplyTo();
                    if (replyTo != null) {
                        callbackdestName = (replyTo instanceof Queue) ? ((Queue) replyTo).getQueueName() : ((Topic) replyTo).getTopicName();
                    }
                }

                if (callbackdestName != null) {
                    // append "jms:" to make it an absolute uri so the invoker can determine it came in on the request
                    // as otherwise the invoker should use the uri from the service callback binding
                    parameters.setCallbackReference(new EndpointReferenceImpl("jms:" + callbackdestName));
                }

                String callbackID = jmsMsg.getStringProperty(JMSBindingConstants.CALLBACK_ID_PROPERTY);
                if (callbackID != null) {
                    parameters.setCallbackID(callbackID);
                }
            }

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
        
        return msg;
    }
}