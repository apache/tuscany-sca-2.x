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

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;

/**
 * MessageProcessor for sending/receiving javax.jms.BytesMessage with the JMSBinding.
 * 
 * @version $Rev$ $Date$
 */
public class BytesMessageProcessor extends AbstractMessageProcessor {

    public BytesMessageProcessor(JMSBinding jmsBinding) {
        super(jmsBinding);
    }

    @Override
    protected Object[] extractPayload(Message msg) {
        try {

            if (!(msg instanceof BytesMessage)) {
                throw new IllegalStateException("expecting JMS BytesMessage: " + msg);
            }
            
            long noOfBytes = ((BytesMessage)msg).getBodyLength();
            byte [] bytes = new byte[(int)noOfBytes];
            ((BytesMessage)msg).readBytes(bytes);
            return new Object[] {bytes};

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    @Override
    protected Message createJMSMessage(Session session, Object o) {
        try {

            // TODO - an experiment. How to enforce a single
            //        byte array parameter
            BytesMessage message = session.createBytesMessage();
            byte [] bytes = (byte[])((Object[])o)[0];
            message.writeBytes(bytes); 
            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

}
