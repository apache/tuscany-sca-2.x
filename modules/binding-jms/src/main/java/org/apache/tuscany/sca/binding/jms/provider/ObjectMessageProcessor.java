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

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;

public class ObjectMessageProcessor extends AbstractMessageProcessor {

    public ObjectMessageProcessor(JMSBinding jmsBinding) {
        super(jmsBinding);
    }

    @Override
    protected Object[] extractPayload(Message msg) {
        try {

            return new Object[] {((ObjectMessage)msg).getObject()};

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    protected Message createJMSMessage(Session session, Object o) {
        try {

            if (!(o instanceof Serializable)) {
                throw new IllegalStateException("JMS ObjectMessage payload not Serializable: " + o);
            }

            ObjectMessage message = session.createObjectMessage();
            message.setObject((Serializable)o);
            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

}
