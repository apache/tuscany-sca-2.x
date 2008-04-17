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

import java.io.StringReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.osoa.sca.ServiceRuntimeException;

public abstract class AbstractMessageProcessor implements JMSMessageProcessor {

    protected String operationPropertyName;
    protected boolean xmlFormat = true;

    public AbstractMessageProcessor(JMSBinding jmsBinding) {
        this.operationPropertyName = jmsBinding.getOperationSelectorPropertyName();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tuscany.binding.jms.OperationAndDataBinding#getOperationName(javax.jms.Message)
     */
    public String getOperationName(Message message) {
        try {

            return message.getStringProperty(operationPropertyName);

        } catch (JMSException e) {
            throw new JMSBindingException("Exception retreiving operation name from message", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tuscany.binding.jms.OperationAndDataBinding#setOperationName(javax.jms.Message, java.lang.String)
     */
    public void setOperationName(String operationName, Message message) {
        try {

            message.setStringProperty(operationPropertyName, operationName);

        } catch (JMSException e) {
            throw new JMSBindingException("Exception setting the operation name on message", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tuscany.binding.jms.OperationAndDataBinding#extractPayload(javax.jms.Session, java.lang.Object)
     */
    public Message insertPayloadIntoJMSMessage(Session session, Object o) {
        return createJMSMessage(session, o);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tuscany.binding.jms.OperationAndDataBinding#extractPayload(javax.jms.Message)
     */
    public Object extractPayloadFromJMSMessage(Message msg) {
        try {
            if (msg.getBooleanProperty(JMSBindingConstants.FAULT_PROPERTY)) {
                throw new ServiceRuntimeException("remote service exception, see nested exception",(Throwable)((ObjectMessage)msg).getObject());
            }
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
        return extractPayload(msg);
    }

    public Message createFaultMessage(Session session, Throwable o) {
        try {

            ObjectMessage message = session.createObjectMessage();
            message.setObject(o);
            message.setBooleanProperty(JMSBindingConstants.FAULT_PROPERTY, true);
            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    protected abstract Object[] extractPayload(Message msg);

    protected abstract Message createJMSMessage(Session session, Object o);

}
