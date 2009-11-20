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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * MessageProcessor for sending/receiving javax.jms.TextMessage with the JMSBinding.
 * 
 * @version $Rev$ $Date$
 */
public class TextMessageProcessor extends AbstractMessageProcessor {
    private static final Logger logger = Logger.getLogger(TextMessageProcessor.class.getName());

    public TextMessageProcessor(JMSBinding jmsBinding, ExtensionPointRegistry registry) {
        super(jmsBinding);
    }

    @Override
    protected Object extractPayload(Message msg) {
        try {

            if (!(msg instanceof TextMessage)) {
                throw new IllegalStateException("expecting JMS TextMessage: " + msg);
            }

            return ((TextMessage)msg).getText();

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    @Override
    protected Message createJMSMessage(Session session, Object o) {
        if (session == null) {
            logger.fine("no response session to create message: " + String.valueOf(o));
            return null;
        }
        try {

            TextMessage message = session.createTextMessage();
            
            if (o != null){
                message.setText(String.valueOf(o));
            }
            
            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

}
