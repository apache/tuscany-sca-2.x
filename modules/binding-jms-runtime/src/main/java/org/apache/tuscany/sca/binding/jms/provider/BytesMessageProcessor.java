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
package org.apache.tuscany.sca.binding.jms.provider;

import java.util.logging.Logger;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;

/**
 * MessageProcessor for sending/receiving javax.jms.BytesMessage with the JMSBinding.
 * 
 * @version $Rev$ $Date$
 */
public class BytesMessageProcessor extends AbstractMessageProcessor {
    private static final Logger logger = Logger.getLogger(AbstractMessageProcessor.class.getName());

    public BytesMessageProcessor(JMSBinding jmsBinding) {
        super(jmsBinding);
    }

/* TUSCANY-2967 - disable this change while we decide what to do and
 *                return faults as JMSObject messages to be consistent
 *                again with other wire formats
    @Override
    public Object extractPayloadFromJMSMessage(Message msg) {
        byte [] bytes = (byte [])extractPayload(msg);
        
        try {
            if (msg.getBooleanProperty(JMSBindingConstants.FAULT_PROPERTY)) {
                return new InvocationTargetException(new ServiceRuntimeException(new String(bytes)));
            } else {
                return bytes;
            }
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }
*/
    
    @Override
    protected Object extractPayload(Message msg) {
        try {

            if (!(msg instanceof BytesMessage)) {
                throw new IllegalStateException("expecting JMS BytesMessage: " + msg);
            }
            
            long noOfBytes = ((BytesMessage)msg).getBodyLength();
            byte [] bytes = new byte[(int)noOfBytes];
            ((BytesMessage)msg).readBytes(bytes);
            ((BytesMessage)msg).reset();
            return bytes;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }
    
/* TUSCANY-2967 - disable this change while we decide what to do and
 *                return faults as JMSObject messages to be consistent
 *                again with other wire formats    
    @Override
    public Message createFaultMessage(Session session, Throwable o) {
        try {
            Message message = createJMSMessage(session, o.toString().getBytes());
            message.setBooleanProperty(JMSBindingConstants.FAULT_PROPERTY, true);
            return message;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }        
    } 
*/   

    @Override
    protected Message createJMSMessage(Session session, Object o) {
        if (session == null) {
            logger.fine("no response session to create message: " + String.valueOf(o));
            return null;
        }
        try {

            // TODO - an experiment. How to enforce a single
            //        byte array parameter
            BytesMessage message = session.createBytesMessage();
            
            if (o != null){
                message.writeBytes((byte[])o); 
            }
            
            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

}
